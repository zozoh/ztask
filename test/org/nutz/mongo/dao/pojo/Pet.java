package org.nutz.mongo.dao.pojo;

import java.util.Date;

import org.nutz.castor.Castors;
import org.nutz.lang.random.R;
import org.nutz.mongo.annotation.*;

@Co(value = "pet")
@CoIndexes("!:+name")
public class Pet {

	public static Pet BIRTHDAY(String name, String birthday) {
		Pet p = NEW(name);
		p.setBirthday(Castors.me().castTo(birthday, java.util.Date.class));
		return p;
	}

	public static Pet NEW(PetType type, String name) {
		return NEW(type, name, R.random(3, 20), R.random(0, 16));
	}

	public static Pet NEW(String name) {
		return NEW(null, name);
	}

	public static Pet NEW(String name, int age) {
		return NEW(null, name, age, 0);
	}

	public static Pet LBS(String name, String... lbs) {
		Pet pet = NEW(null, name);
		pet.labels = lbs;
		return pet;
	}

	public static Pet AGE(String name, int age, int count) {
		return NEW(null, name, age, count);
	}

	public static Pet NEW(PetType type, String name, int age, int count) {
		Pet p = new Pet();
		p.setType(type);
		p.setName(name);
		p.setAge(age);
		p.setCount(count);
		return p;
	}

	@CoId(CoIdType.UU64)
	private String id;

	@CoField(value = "nm")
	private String name;

	@CoField("tp")
	private PetType type;

	@CoField("ag")
	private int age;

	@CoField("lbs")
	private String[] labels;

	@CoField("cun")
	private int count;

	@CoField("bth")
	private Date birthday;

	@CoField("frds")
	private Pet[] friends;

	@CoField("my_foods")
	private PetFood[] foods;

	public PetFood[] getFoods() {
		return foods;
	}

	public void setFoods(PetFood[] foods) {
		this.foods = foods;
	}

	public Pet[] getFriends() {
		return friends;
	}

	public void setFriends(Pet[] friends) {
		this.friends = friends;
	}

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

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
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
