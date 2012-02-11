package org.nutz.ztask.api;

import org.nutz.lang.Strings;
import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIndexes;

@Co("label")
@CoIndexes("!:+name")
public class Label {

	@CoId
	private String _id;

	@CoField("nm")
	private String name;

	@CoField("txt")
	private String text;

	@CoField
	private String color;

	@CoField("pnm")
	private String parent;

	@CoField("cnt")
	private int count;

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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String toString() {
		return text + (Strings.sBlank(color, "")) + ":" + count;
	}

}
