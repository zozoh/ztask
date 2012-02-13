package org.nutz.ztask.thread;

import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mail.AfterEach;
import org.nutz.mail.EachMail;
import org.nutz.mail.MailObj;
import org.nutz.ztask.api.SmtpInfo;
import org.nutz.ztask.api.User;
import org.nutz.ztask.util.ZTasks;

public class SendMailAtom extends AbstractAtom {

	private final static Log log = Logs.get();

	private int interval;

	private boolean firstUp;

	public SendMailAtom() {
		firstUp = true;
	}

	@Override
	protected long exec() {
		if (firstUp) {
			if (log.isInfoEnabled())
				log.info("skip first up");
			firstUp = false;
			return inter();
		}

		final SmtpInfo smtp = factory.htasks().getGlobalInfo().getSmtp();
		if (smtp == null || !smtp.isAvaliable()) {
			if (log.isWarnEnabled())
				log.warn("SMTP not avaliable!");
			return inter();
		}

		factory.mails().each(new EachMail() {
			public AfterEach doMail(MailObj mo) {
				// 超过最大重试次数，删除
				if (mo.getRetryCount() > 3)
					return AfterEach.REMOVE;

				List<User> usrs = new LinkedList<User>();

				// 没有合法的收件人，删除
				if (null == mo.getTos() || mo.getTos().length == 0) {
					return AfterEach.REMOVE;
				}

				// 用户必须存在
				for (String to : mo.getTos()) {
					User u = factory.users().get(to);
					if (null == u) {
						if (log.isWarnEnabled())
							log.warnf("Fail to find user '%s'", to);
					}
					usrs.add(u);
				}
				// 发信人不能为空
				if (usrs.isEmpty()) {
					if (log.isInfoEnabled())
						log.infof("drop mail without tos '%s'", mo.getSubject());
					return AfterEach.REMOVE;
				}
				// 发送
				if (log.isDebugEnabled()) {
					String[] mailAddresses = new String[usrs.size()];
					int i = 0;
					for (User u : usrs)
						mailAddresses[i++] = u.getEmail().toString();
					log.debugf(	" -- mail[%d]:>> [%s] >> %d users (%s)",
								mo.getRetryCount(),
								mo.getSubject(),
								usrs.size(),
								Lang.concat(",", mailAddresses));
				}
				String re = ZTasks.sendTextMail(smtp, mo.getSubject(), mo.getMailBody(), usrs);

				if (log.isDebugEnabled())
					log.debug(re == null ? "fail" : "OK:" + re);

				return re == null ? AfterEach.RETRY : AfterEach.REMOVE;
			}
		});

		// 返回间隔时间
		return inter();
	}

	private int inter() {
		return interval <= 0 ? 30000 : interval * 1000;
	}

	public static final String NAME = "SEND.MAIL";

	@Override
	public String name() {
		return NAME;
	}

}
