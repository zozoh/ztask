package org.nutz.mongo.dao.pojo;

import org.nutz.mongo.annotation.*;

@Co("sobj")
public class SObj {

	public static SObj create(String name) {
		SObj obj = new SObj();
		obj.setName(name);
		return obj;
	}

	@CoId(CoIdType.DEFAULT)
	private String id;

	@CoField("nm")
	private String name;

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

}
