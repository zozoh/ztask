package org.nutz.mongo.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.mongo.MongoCase;
import org.nutz.mongo.dao.pojo.Pet;
import org.nutz.mongo.util.Moo;
import org.nutz.mongo.util.MCur;

public class MongoDaoPojoTest extends MongoCase {

	@Test
	public void test_simple_update() {
		mgdao.create(Pet.class, true);
		Pet xb = mgdao.save(Pet.me("XiaoBai"));
		mgdao.update(	xb,
						Moo.born().append("id", xb.getId()),
						Moo.born().set("name", "XB").inc("age", 2));
		Pet xb2 = mgdao.findById(Pet.class, xb.getId());
		assertEquals(xb.getAge() + 2, xb2.getAge());
		assertEquals("XB", xb2.getName());

		mgdao.updateById(Pet.class, xb.getId(), Moo.born().inc("count", 1));
		Pet xb3 = mgdao.findById(Pet.class, xb.getId());
		assertEquals(xb.getCount() + 1, xb3.getCount());
		assertEquals("XB", xb3.getName());
	}

	@Test
	public void test_simple_save_find() {
		mgdao.create(Pet.class, true);
		Pet xb = mgdao.save(Pet.me("XiaoBai"));
		Pet xb2 = mgdao.findById(Pet.class, xb.getId());
		assertEquals(xb.getName(), xb2.getName());
	}

	@Test
	public void test_simple_query() {
		mgdao.create(Pet.class, true);
		mgdao.save(Pet.me("XiaoBai", 10, 3));
		mgdao.save(Pet.me("XiaoHei", 20, 3));
		mgdao.save(Pet.me("SuperMan", 20, 3));
		mgdao.save(Pet.me("Bush", 10, 3));
		mgdao.save(Pet.me("XiaoQiang", 10, 3));

		List<Pet> pets = mgdao.find(Pet.class, Moo.born().append("age", 20), null);
		assertEquals(2, pets.size());

		pets = mgdao.find(Pet.class, null, MCur.born().asc("name"));
		assertEquals(5, pets.size());
		assertEquals("Bush", pets.get(0).getName());
		assertEquals("SuperMan", pets.get(1).getName());
		assertEquals("XiaoBai", pets.get(2).getName());
		assertEquals("XiaoHei", pets.get(3).getName());
		assertEquals("XiaoQiang", pets.get(4).getName());

	}

}
