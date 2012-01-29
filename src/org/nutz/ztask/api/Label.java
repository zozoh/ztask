package org.nutz.ztask.api;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIdType;
import org.nutz.mongo.annotation.CoIndexes;

@Co("label")
@CoIndexes("!:+name")
public class Label {

	@CoId(CoIdType.DEFAULT)
	private String id;

	@CoField("nm")
	private String name;

	@CoField("pnm")
	private String parent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

}
