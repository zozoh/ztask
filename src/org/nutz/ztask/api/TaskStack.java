package org.nutz.ztask.api;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIdType;
import org.nutz.mongo.annotation.CoIndexes;

/**
 * 一个任务栈，里面存放 Task
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Co("taskstack")
@CoIndexes("!:name")
public class TaskStack {

	/**
	 * 任务栈的 ID
	 */
	@CoId(CoIdType.DEFAULT)
	private String _id;

	/**
	 * 本任务栈的名称
	 */
	@CoField(value = "nm")
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
