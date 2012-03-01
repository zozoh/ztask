package org.nutz.ztask.api;

import java.util.Date;

/**
 * 一个记录了任务每次进栈出栈历史的结构
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TaskHistory {

	private TaskHistoryType type;

	private TaskStatus status;

	private Date at;

	private String user;

	private String stack;

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public TaskHistoryType getType() {
		return type;
	}

	public void setType(TaskHistoryType type) {
		this.type = type;
	}

	public Date getAt() {
		return at;
	}

	public void setAt(Date at) {
		this.at = at;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

}
