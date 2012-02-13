package org.nutz.ztask.impl;

import org.nutz.mail.MailQueue;
import org.nutz.ztask.api.HookService;
import org.nutz.ztask.api.LabelService;
import org.nutz.ztask.api.MessageService;
import org.nutz.ztask.api.ZTaskReportor;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TimerSchedule;
import org.nutz.ztask.api.ZTaskFactory;
import org.nutz.ztask.api.UserService;

/**
 * 一个简单的静态服务类工厂，它由 Ioc 容器构建成后，就直接返回各种服务对象就是了
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class StaticZTaskFactory implements ZTaskFactory {

	/**
	 * 钩子服务访问接口
	 */
	private HookService hooks;

	/**
	 * 标签访问接口
	 */
	private LabelService labels;

	/**
	 * 消息访问接口
	 */
	private MessageService messages;

	/**
	 * 用户访问接口
	 */
	private UserService users;

	/**
	 * 任务访问接口
	 */
	private TaskService tasks;

	/**
	 * 带触发的任务访问接口
	 */
	private TaskService htasks;

	/**
	 * Email队列操作接口
	 */
	private MailQueue mails;

	/**
	 * 报告接口
	 */
	private ZTaskReportor reportor;

	/**
	 * 计划任务接口
	 */
	private TimerSchedule schedule;

	public HookService hooks() {
		return hooks;
	}

	public LabelService labels() {
		return labels;
	}

	public MessageService messages() {
		return messages;
	}

	public UserService users() {
		return users;
	}

	public TaskService htasks() {
		return htasks;
	}

	public TaskService tasks() {
		return tasks;
	}

	public MailQueue mails() {
		return mails;
	}

	public ZTaskReportor reportor() {
		return reportor;
	}

	@Override
	public TimerSchedule schedule() {
		return schedule;
	}

}
