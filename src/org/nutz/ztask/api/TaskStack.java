package org.nutz.ztask.api;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIndexes;

/**
 * 一个任务栈，里面存放 Task
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Co("taskstack")
@CoIndexes("!:+name")
public class TaskStack {

	/**
	 * 任务栈的 ID
	 */
	@CoId
	private String _id;

	/**
	 * 本堆栈的父堆栈
	 */
	@CoField("pnm")
	private String parentName;

	/**
	 * 本任务栈的名称
	 */
	@CoField(value = "nm")
	private String name;

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

	/**
	 * 记录当前堆栈都有哪些额外关注者
	 */
	@CoField("wch")
	private String[] watchers;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String[] getWatchers() {
		return watchers;
	}

	public void setWatchers(String[] watchers) {
		this.watchers = watchers;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String toString() {
		return String.format("<%s>@%s[%d]", name, owner, count);
	}
}
