package org.nutz.ztask.api;

import java.util.Date;

import org.nutz.ioc.Ioc;
import org.nutz.lang.util.SimpleContext;
import org.nutz.mail.MailQueue;

public class ZTasking extends SimpleContext {

	/**
	 * 整个钩子列表开始处理的时间
	 */
	private Date startTime;

	/**
	 * 整个钩子列表结束处理的时间
	 */
	private Date endTime;

	/**
	 * 钩子服务访问接口
	 */
	private HookService hooks;

	/**
	 * 标签访问接口
	 */
	private LabelService labels;

	/**
	 * 用户访问接口
	 */
	private UserService users;

	/**
	 * 任务访问接口
	 */
	private TaskService tasks;

	/**
	 * Email队列操作接口
	 */
	private MailQueue mails;

	/**
	 * 报告接口
	 */
	private TaskReportor reportor;

	/**
	 * 一个帮助方法，快速获取 Ioc 容器接口
	 * <p>
	 * 当然，前提是你要设置了 Ioc 容器
	 * 
	 * @return Ioc 容器接口
	 */
	public Ioc getIoc() {
		return getAs(Ioc.class, "$ioc");
	}

	public void setIoc(Ioc ioc) {
		this.set("$ioc", ioc);
	}

	public Object getRefer() {
		return this.get("$refer");
	}

	public <T> T getReferAs(Class<T> referType) {
		return this.getAs(referType, "$refer");
	}

	public Date startTime() {
		return startTime;
	}

	public Date endTime() {
		return endTime;
	}

	public HookService hooks() {
		return hooks;
	}

	public LabelService labels() {
		return labels;
	}

	public UserService users() {
		return users;
	}

	public TaskService tasks() {
		return tasks;
	}

	public MailQueue mails() {
		return mails;
	}

	public TaskReportor reportor() {
		return reportor;
	}

	public void setReportor(TaskReportor reportor) {
		this.reportor = reportor;
	}

	public void setMails(MailQueue mails) {
		this.mails = mails;
	}

	public void setHooks(HookService hooks) {
		this.hooks = hooks;
	}

	public void setLabels(LabelService labels) {
		this.labels = labels;
	}

	public void setUsers(UserService users) {
		this.users = users;
	}

	public void setTasks(TaskService tasks) {
		this.tasks = tasks;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}