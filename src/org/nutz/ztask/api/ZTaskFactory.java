package org.nutz.ztask.api;

import org.nutz.mail.MailQueue;

/**
 * 所有 Task 的业务服务类的工厂。
 * <p>
 * 它用来负责解开所有业务逻辑之间的的耦合，以便将来更容易支持多数据库
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ZTaskFactory {

	/**
	 * @return 钩子服务接口
	 */
	HookService hooks();

	/**
	 * @return 标签服务接口
	 */
	LabelService labels();

	/**
	 * @return 消息服务接口
	 */
	MessageService messages();

	/**
	 * @return 用户服务接口
	 */
	UserService users();

	/**
	 * @return 带触发的任务访问接口
	 */
	TaskService htasks();

	/**
	 * @return 不带触发的任务访问接口
	 */
	TaskService tasks();

	/**
	 * @return 邮件队列接口
	 */
	MailQueue mails();

	/**
	 * @return 报告接口
	 */
	ZTaskReportor reportor();

	/**
	 * @return 计划任务接口
	 */
	TimerSchedule schedule();

}
