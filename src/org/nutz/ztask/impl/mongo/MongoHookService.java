package org.nutz.ztask.impl.mongo;

import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mail.MailQueue;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.util.MCur;
import org.nutz.mongo.util.Moo;
import org.nutz.ztask.api.Hook;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookService;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Hooking;
import org.nutz.ztask.api.LabelService;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskReportor;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.UserService;
import org.nutz.ztask.util.Err;

/**
 * 本实现将 Hook 的信息存放在 MongoDB 的一个名为 "hook" 的集合里
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MongoHookService extends AbstractMongoService implements HookService {

	public MongoHookService(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	private final static Log log = Logs.get();

	private final static Class<MongoHook> HT = MongoHook.class;

	private TaskService tasks;

	private UserService users;

	private LabelService labels;

	private MailQueue mails;
	
	private TaskReportor reportor;

	private Ioc ioc;

	@Override
	public Hooking doHook(HookType htp, Task t, Object refer) {
		List<? extends Hook> list = this.getHooks(htp);
		if (list.isEmpty())
			return null;

		// 构建进行时
		Hooking ing = new Hooking();
		ing.setT(t);
		ing.setTasks(tasks);
		ing.setLabels(labels);
		ing.setUsers(users);
		ing.setMails(mails);
		ing.setReportor(reportor);
		ing.setIoc(ioc);
		ing.set("$dao", dao);
		ing.set("$refer", refer);
		ing.setList(list.toArray(new Hook[list.size()]));

		// 循环调用
		ing.setStartTime(Times.now());
		if (log.isDebugEnabled())
			log.debugf("Begin Hooking : %s", ing);

		while (ing.nextHook() != null) {
			Hook h = ing.hook();

			// 获得处理器，并保证其存在
			HookHandler hh = getHandler(h.getHandler());
			if (null == hh)
				throw Lang.makeThrow("Hook handle noexists : %s", h.toString());

			if (log.isDebugEnabled())
				log.debugf("    %2d -> %s :: %s", ing.hookIndex(), h, hh.getClass().getName());

			// 进行处理
			hh.doHandle(h.getType(), h.getHandler(), ing);

		}

		// 结束
		ing.setEndTime(Times.now());
		if (log.isDebugEnabled())
			log.debugf("End Hooking : %s", ing);

		return null;
	}

	@Override
	public HookHandler getHandler(String handler) {
		return ioc.get(HookHandler.class, handler);
	}

	@Override
	public boolean hasHandler(String handler) {
		return ioc.has(handler);
	}

	@Override
	public boolean removeHook(HookType htp, String handler) {
		return dao.remove(HT, Moo.NEW("type", htp.toString()).append("handler", handler))
					.getLastError()
					.ok();
	}

	@Override
	public void clearHooks() {
		dao.remove(HT, null);
	}

	@Override
	public boolean addHook(Hook hook) {
		if (null != getHook(hook.getType(), hook.getHandler()))
			return false;

		// 检查类型
		if (null == hook.getType())
			throw Err.H.NULL_TYPE(hook);

		// 检查处理器
		if (!this.hasHandler(hook.getHandler()))
			throw Err.H.NO_HANDLER(hook);

		// 保存
		dao.save(hook);
		return true;
	}

	@Override
	public Hook getHook(HookType htp, String handler) {
		return dao.findOne(HT, Moo.NEW("type", htp.toString()).append("handler", handler));
	}

	@Override
	public List<? extends Hook> getHooks(HookType htp) {
		return dao.find(HT, null == htp ? null : Moo.NEW("type", htp.toString()), MCur.ASC("_id"));
	}

}
