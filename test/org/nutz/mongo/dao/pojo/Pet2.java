package org.nutz.mongo.dao.pojo;

import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.annotation.CoIdType;

@Co("pet2")
public class Pet2 {

	@CoId(CoIdType.AUTO_INC)
	private int id;
	
	@CoField
	private String name;
	
	@CoField(ref=true)
	private Pet refPet;

	@CoField()
	private Pet embedPet;
	
	@CoField(ref=true, lazy=true)
	private Pet lazyPet;
	
	@CoField()
	private Pet[] pets;
	
	@CoField(ref=true)
	private Pet[] refPets;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pet getRefPet() {
		return refPet;
	}

	public void setRefPet(Pet refPet) {
		this.refPet = refPet;
	}

	public Pet getEmbedPet() {
		return embedPet;
	}

	public void setEmbedPet(Pet embedPet) {
		this.embedPet = embedPet;
	}

	public Pet getLazyPet() {
		return lazyPet;
	}

	public void setLazyPet(Pet lazyPet) {
		this.lazyPet = lazyPet;
	}

	public Pet[] getPets() {
		return pets;
	}

	public void setPets(Pet[] pets) {
		this.pets = pets;
	}

	public Pet[] getRefPets() {
		return refPets;
	}

	public void setRefPets(Pet[] refPets) {
		this.refPets = refPets;
	}
	
	
}
