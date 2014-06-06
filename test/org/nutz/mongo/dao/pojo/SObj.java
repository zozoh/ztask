package org.nutz.mongo.dao.pojo;

import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.mongo.annotation.*;

@Co("sobj")
public class SObj {

	public static SObj NEW(String name) {
		SObj obj = new SObj();
		obj.setName(name);
		return obj;
	}

	public static SObj NUMS(String name, int... numbers) {
		SObj obj = NEW(name);
		obj.setNumbers(numbers);
		return obj;
	}

	@CoId(CoIdType.DEFAULT)
	private String id;

	@CoField
	private String name;

	@CoField("n")
	private int num;

	@CoField("map")
	private Map<String, Object> map;

	@CoField("arr")
	private SInner[] inners;

	@CoField
	private SInner obj;

	@CoField("ns")
	private int[] numbers;

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

	public int getNum() {
		return num;
	}

	public void setNum(int number) {
		this.num = number;
	}

	public int[] getNumbers() {
		return numbers;
	}

	public void setNumbers(int[] numbers) {
		this.numbers = numbers;
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
