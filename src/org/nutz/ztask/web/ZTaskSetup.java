package org.nutz.ztask.web;

import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.session.MongoSessionManager;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.ztask.ZTask;
import org.nutz.ztask.api.GlobalLock;
import org.nutz.ztask.api.InitService;
import org.nutz.ztask.api.TimerSchedule;
import org.nutz.ztask.impl.mongo.MongoTaskService;
import org.nutz.ztask.thread.AbstractAtom;

/**
 * 启动和关闭服务时的设定
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZTaskSetup implements Setup {

	private static final Log log = Logs.get();

	@Override
	public void init(NutConfig config) {
		if (log.isInfoEnabled())
			log.infof("init zTask (%s) ...", ZTask.version());
		// 得到 Ioc 容器
		final Ioc ioc = config.getIoc();

		// 初始化数据
		ioc.get(InitService.class).init();

		// 启动后台进程
		AbstractAtom[] atoms = new AbstractAtom[3];
		atoms[0] = ioc.get(AbstractAtom.class, "schd_update");
		atoms[1] = ioc.get(AbstractAtom.class, "timer_run");
		atoms[2] = ioc.get(AbstractAtom.class, "send_mail");

		for (int i = 0; i < atoms.length; i++) {
			if (null != atoms[i]) {
				Thread t = new Thread(atoms[i], atoms[i].getName());
				t.start();
			}
		}

		// 最后将 atoms 数组存入应用上下文环境，以便界面的JSP端读取其状态
		config.setAttribute("$atoms", atoms);

		// 设置成MongoSession机制
		Mvcs.sessionProvider = new MongoSessionManager(ioc.get(MongoTaskService.class, "taskService").dao());
		
		// 初始化结束
		if (log.isInfoEnabled())
			log.info("... done for init zTask");
		
	}

	@Override
	public void destroy(NutConfig config) {
		Ioc ioc = config.getIoc();

		// 关闭同步在 schedule 上的线程
		ioc.get(TimerSchedule.class, "schedule").stop();
		if (log.isDebugEnabled())
			log.debug("stop schedule ...");

		// 关闭同步在 mailLock 上的线程
		ioc.get(GlobalLock.class, "mailLock").stop();
		if (log.isDebugEnabled())
			log.debug("stop mailing ...");

		if (log.isInfoEnabled())
			log.info("zTask is down");
	}

}
