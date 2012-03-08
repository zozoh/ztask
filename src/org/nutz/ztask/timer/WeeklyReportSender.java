package org.nutz.ztask.timer;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.mail.MailObj;
import org.nutz.ztask.api.GInfo;
import org.nutz.ztask.api.TaskReport;
import org.nutz.ztask.api.TimerHandler;
import org.nutz.ztask.api.Timering;
import org.nutz.ztask.api.User;
import org.nutz.ztask.api.ZTaskFactory;
import org.nutz.ztask.util.ZTasks;

/**
 * 定期发送周报
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name = "t_weekend_report")
public class WeeklyReportSender implements TimerHandler {

	/**
	 * 注入: 服务类工厂
	 */
	@Inject("refer:serviceFactory")
	private ZTaskFactory factory;

	@Override
	public String doHandle(String name, Timering ing) {
		GInfo info = factory.htasks().getGlobalInfo();
		User u = factory.users().get(info.getReportTo());
		if (null == u) {
			return "Fail to find user " + info.getReportTo();
		}

		StringBuilder sb = new StringBuilder();
		TaskReport rpt = factory.reportor().makeIfNoExists(ing.now());
		factory.reportor().writeAndClose(rpt, Lang.ops(sb));

		MailObj mo = ZTasks.textMail("Weekly:" + rpt.getFullName(), sb.toString());
		mo.setTos(Lang.array(u.getName()));
		factory.mails().joinMail(mo);

		return "push in queue";
	}

}
