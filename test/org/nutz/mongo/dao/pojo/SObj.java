package org.nutz.mongo.dao.pojo;

import java.util.Map;

import org.nutz.lang.Lang;
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

	@CoField
	private String name;

	@CoField("n")
	private int number;

	@CoField("map")
	private Map<String, Object> map;

	@CoField("arr")
	private SInner[] inners;

	@CoField
	private SInner obj;

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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public void setMap(String mapStr) {
		this.map = Lang.map(mapStr);
	}

	public SInner[] getInners() {
		return inners;
	}

	public void setInners(SInner[] inners) {
		this.inners = inners;
	}

	public SInner getObj() {
		return obj;
	}

	public void setObj(SInner obj) {
		this.obj = obj;
	}

}
