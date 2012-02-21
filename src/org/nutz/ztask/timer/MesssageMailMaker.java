package org.nutz.ztask.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mail.MailObj;
import org.nutz.ztask.api.TimerHandler;
import org.nutz.ztask.api.Timering;
import org.nutz.ztask.api.User;
import org.nutz.ztask.api.ZTaskFactory;
import org.nutz.ztask.api.Message;
import org.nutz.ztask.util.ZTasks;

/**
 * 定期将未读的消息发送成 Email
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name = "t_msg_mail")
public class MesssageMailMaker implements TimerHandler {

	private final static Log log = Logs.get();

	/**
	 * 注入: 服务类工厂
	 */
	@Inject("refer:serviceFactory")
	private ZTaskFactory factory;

	@Override
	public String doHandle(String name, Timering ing) {
		final Map<String, List<Message>> map = new HashMap<String, List<Message>>();
		// 分组
		factory.messages().each(null, "!R!N:", null, 0, new Each<Message>() {
			public void invoke(int index, Message msg, int length) {
				List<Message> list = map.get(msg.getOwner());
				if (null == list) {
					list = new LinkedList<Message>();
					map.put(msg.getOwner(), list);
				}
				list.add(msg);
			}
		});

		if (log.isDebugEnabled())
			log.debugf("MailMaker: found %d users %s", map.size(), Strings.dup('*', 40));

		// 循环为每个用户发送通知邮件
		for (String unm : map.keySet()) {

			User u = factory.users().get(unm);
			if (null == u || null == u.getEmail())
				continue;

			List<Message> list = map.get(unm);

			if (null == list || list.isEmpty()) {
				continue;
			}

			if (log.isDebugEnabled())
				log.debugf(" - MailMaker: will send to '%s'", unm);

			Date now = Times.now();

			// 准备消息
			StringBuilder sb = new StringBuilder();
			sb.append("Message in zTask @ " + Times.sDT(now) + " : \n\n");
			for (Message msg : list) {
				sb.append("    ").append(msg.toString()).append('\n');
				sb.append("   [").append(Times.sDT(msg.getCreateTime())).append("] - ");
				sb.append(msg.getText());
			}
			sb.append("\n\n");

			Message firstMsg = list.get(0);
			String subject = String.format(	"[zTask:%d] %s ",
											list.size(),
											firstMsg.getText()
													.replaceAll("[\\[]?[0-9a-f]{24}[\\]]?", ""));

			if (log.isDebugEnabled())
				log.debugf(" - MailMaker: mail is: '%s':\n%s", subject, sb);

			// 设置收件人
			MailObj mo = ZTasks.textMail(subject, sb.toString());
			mo.setTos(Lang.array(u.getName()));

			if (log.isDebugEnabled())
				log.debugf(" - MailMaker: mail to @%s(%s)", u.getName(), u.getEmail());

			// 加入队列
			factory.mails().joinMail(mo);

			if (log.isDebugEnabled())
				log.debugf(" - MailMaker: after join, queue(%d)", factory.mails().count());

			// 设置通知状态
			for (Message msg : list)
				factory.messages().setNotified(msg, now);

			if (log.isDebugEnabled()) {
				log.debugf(" - MailMaker: set noti for %d messages", list.size());
				log.debugf(" - MailMaker: %s -- NEXT LOOP --", Strings.dup('-', 28));
			}

		}

		// 返回成功
		return "OK:" + map.size() + " users";
	}
}
