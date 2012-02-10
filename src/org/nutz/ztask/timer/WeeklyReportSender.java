package org.nutz.ztask.timer;

import org.nutz.lang.Lang;
import org.nutz.mail.MailObj;
import org.nutz.mail.MailQueue;
import org.nutz.ztask.api.GInfo;
import org.nutz.ztask.api.TaskReport;
import org.nutz.ztask.api.TaskReportor;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TimerHandler;
import org.nutz.ztask.api.Timering;
import org.nutz.ztask.api.User;
import org.nutz.ztask.api.UserService;
import org.nutz.ztask.util.ZTasks;

/**
 * 定期发送周报
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WeeklyReportSender implements TimerHandler {

	/**
	 * 周报接口
	 */
	private TaskReportor reportor;

	/**
	 * 邮件队列接口
	 */
	private MailQueue mails;

	/**
	 * 任务服务接口
	 */
	private TaskService tasks;

	/**
	 * 用户访问接口
	 */
	private UserService users;

	@Override
	public String doHandle(String name, Timering ing) {
		GInfo info = tasks.getGlobalInfo();
		User u = users.get(info.getWeeklyTo());
		if (null == u) {
			return "Fail to find user " + info.getWeeklyTo();
		}

		StringBuilder sb = new StringBuilder();
		TaskReport rpt = reportor.makeIfNoExists(ing.now());
		reportor.writeAndClose(rpt, Lang.ops(sb));

		MailObj mo = ZTasks.textMail("Weekly:" + rpt.getFullName(), sb.toString());
		mo.setTos(Lang.array(u.getName()));
		mails.saveMail(mo);

		return "push in queue";
	}

}
