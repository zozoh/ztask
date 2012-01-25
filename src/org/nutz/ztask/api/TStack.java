package org.nutz.ztask.api;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIdType;

/**
 * 一个任务栈，里面存放 Task
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Co("tstack")
public class TStack {

	/**
	 * 任务栈的 ID
	 */
	@CoId(CoIdType.DEFAULT)
	private String _id;

	/**
	 * 本任务栈的名称 （唯一性约束）
	 */
	@CoField("nm")
	private String name;

	/**
	 * 本任务栈的描述
	 */
	@CoField("des")
	private String description;

	/**
	 * 任务的数量
	 */
	@CoField("cnt")
	private int count;

	/**
	 * 保存每个任务的 ID
	 */
	@CoField("tids")
	private String[] taskIds;

	/**
	 * 本任务栈完成的任务，一定要被打上的额外标签
	 */
	@CoField("lbls")
	private String[] labels;

	/**
	 * 当前堆栈的所有者
	 */
	@CoField("ow")
	private String owner;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String[] getTaskIds() {
		return taskIds;
	}

	public void setTaskIds(String[] taskIds) {
		this.taskIds = taskIds;
	}

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
