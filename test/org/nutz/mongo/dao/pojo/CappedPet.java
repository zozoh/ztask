package org.nutz.mongo.dao.pojo;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;


@Co(value="capped_pet", cappedSize=10240, cappedMax=100)
public class CappedPet {

	@CoId
	private String id;
	
	@CoField
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
