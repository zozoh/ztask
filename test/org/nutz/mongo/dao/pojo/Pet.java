package org.nutz.mongo.dao.pojo;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIdType;

@Co("pet")
public class Pet {

	public static Pet me(String name) {
		return me(name, 2, 1);
	}

	public static Pet me(String name, int age, int count) {
		Pet p = new Pet();
		p.setName(name);
		p.setAge(age);
		p.setCount(count);
		return p;
	}

	@CoId(CoIdType.UU64)
	private String id;

	@CoField("nm")
	private String name;

	@CoField
	private int age;

	@CoField("cun")
	private int count;

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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return String.format("%s:%s(%s) [%s]", name, age, count, id);
	}

}
