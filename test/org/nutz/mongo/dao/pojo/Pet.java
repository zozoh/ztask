package org.nutz.mongo.dao.pojo;

import org.nutz.lang.random.R;
import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIdType;

@Co("pet")
public class Pet {

	public static Pet me(PetType type, String name) {
		return me(type, name, R.random(3, 20), R.random(0, 16));
	}

	public static Pet me(String name) {
		return me(null, name);
	}

	public static Pet mel(String name, String... lbs) {
		Pet pet = me(null, name);
		pet.labels = lbs;
		return pet;
	}

	public static Pet me(String name, int age, int count) {
		return me(null, name, age, count);
	}

	public static Pet me(PetType type, String name, int age, int count) {
		Pet p = new Pet();
		p.setType(type);
		p.setName(name);
		p.setAge(age);
		p.setCount(count);
		return p;
	}

	@CoId(CoIdType.UU64)
	private String id;

	@CoField(value="nm",unique=true)
	private String name;

	@CoField("tp")
	private PetType type;

	@CoField
	private int age;

	@CoField("lbs")
	private String[] labels;

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

	public PetType getType() {
		return type;
	}

	public void setType(PetType type) {
		this.type = type;
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

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	@Override
	public String toString() {
		return String.format("%s:%s(%s) [%s]", name, age, count, id);
	}

}
