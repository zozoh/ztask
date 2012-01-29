package org.nutz.ztask.api;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIdType;

/**
 * 描述了一个 TASK 的全部信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Co("task")
public class Task {

	/**
	 * 任务的 ID
	 */
	@CoId(CoIdType.UU64)
	private String _id;

	/**
	 * 本任务的内容简述，内容不要超过 140 个字
	 */
	@CoField("tt")
	private String title;

	/**
	 * 本任务的父任务，一个任务可以被无限级拆分
	 */
	@CoField("pid")
	private String parentId;

	/**
	 * 一个附加注释的列表
	 */
	@CoField("cmts")
	private String[] comments;

	/**
	 * 格式为 yyyy-MM-dd HH:mm:ss 格式的字符串
	 */
	@CoField("ct")
	private String createTime; //TODO 使用Date/或者TimeStemp

	/**
	 * 格式为 yyyy-MM-dd HH:mm:ss 格式的字符串
	 */
	@CoField("lm")
	private String lastModified;//TODO 使用Date/或者TimeStemp

	/**
	 * 所在堆栈的名称
	 */
	@CoField("stack")
	private String stack;

	/**
	 * 所有者的帐户名
	 */
	@CoField("ow")
	private String owner;

	/**
	 * 任务状态
	 */
	@CoField("sta")
	private TaskStatus status;

	/**
	 * 本任务的标签
	 */
	@CoField("lbls")
	private String[] labels;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String[] getComments() {
		return comments;
	}

	public void setComments(String[] comments) {
		this.comments = comments;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

}
