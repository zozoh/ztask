package org.nutz.ztask.hook;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.impl.NutMessageMap;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Hooking;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.User;
import org.nutz.ztask.util.ZTasks;

/**
 * 增加一个任务的通知
 * 
 * <pre>
 *  * 通过 ThreadLoacal :: ZTasks.getME() 得到触发当前操作的帐号名称
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name = "h_notify")
public class AddNotify implements HookHandler {

	private static final Log log = Logs.get();

	@Override
	public void doHandle(HookType htp, String name, Hooking ing) {
		User me = ZTasks.getME();
		/*
		 * 没有操作帐户，则拒绝执行
		 */
		if (null == me) {
			if (log.isWarnEnabled())
				log.warnf("!!!NULL-ME: [%s]$%s :: %s ", htp, name, ing.t());
			return;
		}

		/*
		 * 没有任务，拒绝执行
		 */
		Task t = ing.t();
		if (null == t) {
			if (log.isWarnEnabled())
				log.warnf(	"!!!NULL-Task!!!: @%s >> [%s]$%s :: %s ",
							me.getName(),
							htp,
							name,
							ing.t());
			return;
		}

		/*
		 * 根据类型进行判断
		 */
		switch (htp) {
		// @${u.name} 为任务 ${t._id} "${t.brief}" 重设了标签: #${str}#
		case LABEL:
			_N(	me,
				t.getStack(),
				htp,
				ing,
				Lang.length(t.getLabels()) == 0 ? "--" : Lang.concat(",", t.getLabels()).toString());
			break;

		// @${u.name} 对任务 ${t._id} "${t.brief}" 做了补充说明: ${str}
		case COMMENT:
			String cmt = Lang.get(t.getComments(), ing.getReferInt());
			_N(me, t.getStack(), htp, ing, ZTasks.unwrapComment(cmt));
			break;

		// @${u.name} 将任务 ${t._id} "${t.brief}" 从 @${str} 分配给 @${t.owner}
		case OWNER:
			_N(me, t.getStack(), htp, ing, ing.getReferString());
			break;

		// @${u.name} 将任务 ${t._id} 的描述修改为: @${t.text}
		case UPDATE:
			_N(me, t.getStack(), htp, ing, null);
			break;

		// @${u.name} 开始执行任务 ${t._id} "${t.brief}"
		case RESTART:
			_N(me, t.getStack(), htp, ing, null);
			break;

		// @${u.name} 挂起了任务 "${t.brief}"
		case HUNGUP:
			_N(me, t.getStack(), htp, ing, null);
			break;

		// @${u.name} 向堆栈 [${t.stack}] 压入了任务 ${t._id} "${t.brief}"
		case PUSH:
			_N(me, t.getStack(), htp, ing, null);
			break;

		// @${u.name} 完成了任务 ${t._id} "${t.brief}"
		case DONE:
			_N(me, ing.getReferString(), htp, ing, null);
			break;

		// @${u.name} 拒绝了任务 ${t._id} "${t.brief}"
		case REJECT:
			_N(me, ing.getReferString(), htp, ing, null);
			break;

		// @${u.name} 删除了任务 ${t._id} "${t.brief}"
		case DROP:
			_N(me, t.getStack(), htp, ing, null);
			break;

		// 默认，写入出错 Log
		default:
			if (log.isWarnEnabled())
				log.warnf(	"!!!Uknow HOOK-TYPE: @%s >> [%s]$%s :: %s ",
							me.getName(),
							htp,
							name,
							ing.t());
		}
	}

	/**
	 * 发送一个消息，它寻找接受者的逻辑是
	 * 
	 * <pre>
	 *  0. 不是 me
	 *  1. 是 task 的 watcher
	 *  2. 或者是 stack 的 watcher
	 * </pre>
	 * 
	 * @param me
	 *            操作者
	 * @param stackName
	 *            相关堆栈名称
	 * @param htp
	 *            钩子类型
	 * @param ing
	 *            运行时
	 * @param str
	 *            消息
	 */
	private void _N(User me, String stackName, HookType htp, Hooking ing, String str) {
		/*
		 * 寻找消息接收者
		 */
		HashMap<String, User> accepters = new HashMap<String, User>();
		TaskStack stack = ing.factory().tasks().getStack(stackName);
		if (null != stack && null != stack.getWatchers())
			for (String nm : stack.getWatchers())
				if (!me.getName().equals(nm)) {
					User u = ing.factory().users().get(nm);
					if (null != u)
						accepters.put(nm, u);
				}

		if (null != ing.t().getWatchers())
			for (String nm : ing.t().getWatchers())
				if (!me.getName().equals(nm)) {
					User u = ing.factory().users().get(nm);
					if (null != u)
						accepters.put(nm, u);
				}

		// 没有接受者，跳过
		if (accepters.isEmpty())
			return;

		/*
		 * 准备消息文本
		 */
		String key = "notify." + htp.toString();

		// 得到文本
		NutMessageMap msgs = ZTasks.getMsgs();
		Object tmpl = null == msgs ? null : msgs.getObject(key);

		// 采用默认消息文本
		if (null == tmpl) {
			tmpl = dft_msgs.get(key);
		}

		// 采用默认模板
		if (null == tmpl) {
			tmpl = dft_msgs.get("--");
		}

		// 解析模板
		Segment seg = tmpl instanceof Segment ? (Segment) tmpl : Segments.create(tmpl.toString());

		/*
		 * 准备替换的 Context
		 */
		Context context = Lang.context();

		Map<String, Object> taskMap = Lang.obj2map(ing.t());
		Map<String, Object> userMap = Lang.obj2map(me);

		context.set("str", str);
		context.set("htp", htp.toString());
		context.set("t.brief", Strings.brief(ing.t().getText(), 16));
		context.putAll("u.", userMap).putAll("t.", taskMap);

		/*
		 * 格式化文本
		 */
		String text = seg.render(context).toString();

		// 保存消息
		for (String nm : accepters.keySet())
			ing.factory().messages().add(text, nm);
	}

	/*
	 * 初始化默认的消息
	 */
	private final static Map<String, String> dft_msgs = new HashMap<String, String>();

	static {
		// --
		dft_msgs.put("--", "@${u.name} ${htp} ${t._id} \"${t.brief}\" :: ${str}");

		// LABEL
		dft_msgs.put(	"notify.LABEL",
						"@${u.name} reseted labels: #${str}# for task ${t._id} \"${t.brief}\"");

		// COMMENT
		dft_msgs.put(	"notify.COMMENT",
						"@${u.name} commonted on ${t._id} \"${t.brief}\" : \"${str}\"");

		// OWNER
		dft_msgs.put(	"notify.OWNER",
						"@${u.name} assigned ${t._id} \"${t.brief}\" from @${str} to @${t.owner}");

		// UPDATE
		dft_msgs.put(	"notify.UPDATE",
						"@${u.name} update task description ${t._id} to: \"${t.text}\"");

		// RESTART
		dft_msgs.put("notify.RESTART", "@${u.name} started ${t._id} \"${t.brief}\"");

		// HUNGUP
		dft_msgs.put("notify.HUNGUP", "@${u.name} hunguped ${t._id} \"${t.brief}\"");

		// PUSH
		dft_msgs.put("notify.PUSH", "@${u.name} had pushed ${t._id} \"${t.brief}\" to [${t.stack}]");

		// DONE
		dft_msgs.put("notify.DONE", "@${u.name} had done ${t._id} \"${t.brief}\"");

		// REJECT
		dft_msgs.put("notify.REJECT", "@${u.name} rejected ${t._id} \"${t.brief}\"");

		// DROP
		dft_msgs.put("notify.DROP", "@${u.name} deleted ${t._id} \"${t.brief}了\"");
	}

}
