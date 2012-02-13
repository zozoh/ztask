package org.nutz.ztask.timer;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.ztask.api.TimerHandler;
import org.nutz.ztask.api.Timering;
import org.nutz.ztask.api.ZTaskFactory;

/**
 * 定期删除已读消息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name="t_msg_cleanup")
public class MessageCleanup implements TimerHandler {

	/**
	 * 注入: 服务类工厂
	 */
	@Inject("refer:serviceFactory")
	private ZTaskFactory factory;

	/**
	 * 注入:消息保留时间(单位天)
	 */
	@Inject("java:$conf.getInt('sys-msg-clean', 14)")
	private int keepDays;

	@Override
	public String doHandle(String name, Timering ing) {
		long ms = System.currentTimeMillis();
		ms -= Math.max(keepDays, 1) * 86400 * 1000;
		factory.messages().clearBefore(Times.D(ms), false);
		return null;
	}

}
