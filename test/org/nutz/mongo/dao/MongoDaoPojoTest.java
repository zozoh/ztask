package org.nutz.mongo.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.mongo.MongoCase;
import org.nutz.mongo.dao.pojo.Pet;
import org.nutz.mongo.dao.pojo.PetType;
import org.nutz.mongo.dao.pojo.SObj;
import org.nutz.mongo.util.Moo;
import org.nutz.mongo.util.MCur;

public class MongoDaoPojoTest extends MongoCase {

	@Test
	public void test_default_id_save_and_remove() {
		dao.create(SObj.class, true);
		SObj obj = SObj.create("ABC");
		dao.save(obj);
		assertEquals(1, dao.count(obj, null));

		obj = dao.findOne(SObj.class, Moo.born().append("name", "ABC"));
		
		dao.removeById(SObj.class, obj.getId());
		assertEquals(0, dao.count(obj, null));
	}

	@Test
	public void test_enum_type_field() {
		dao.create(Pet.class, true);
		Pet snoopy = dao.save(Pet.me(PetType.DOG, "Snoopy"));
		dao.save(snoopy);
		assertNotNull(snoopy.getId());

		Pet snoopy2 = dao.findById(Pet.class, snoopy.getId());
		assertEquals(snoopy.getType(), snoopy2.getType());

		dao.save(Pet.me(PetType.CAT, "Tom"));
		dao.update(	Pet.class,
					Moo.born().append("type", PetType.CAT),
					Moo.born().set("type", PetType.MONKEY).set("count", 3000));

		Pet tom = dao.findOne(Pet.class, Moo.born().append("name", "Tom"));
		assertEquals(3000, tom.getCount());
		assertEquals(PetType.MONKEY, tom.getType());
	}

	@Test
	public void test_simple_update() {
		dao.create(Pet.class, true);
		Pet xb = dao.save(Pet.me("XiaoBai"));
		dao.update(	xb,
					Moo.born().append("id", xb.getId()),
					Moo.born().set("name", "XB").inc("age", 2));
		Pet xb2 = dao.findById(Pet.class, xb.getId());
		assertEquals(xb.getAge() + 2, xb2.getAge());
		assertEquals("XB", xb2.getName());

		dao.updateById(Pet.class, xb.getId(), Moo.born().inc("count", 1));
		Pet xb3 = dao.findById(Pet.class, xb.getId());
		assertEquals(xb.getCount() + 1, xb3.getCount());
		assertEquals("XB", xb3.getName());
	}

	@Test
	public void test_simple_save_find() {
		dao.create(Pet.class, true);
		Pet xb = dao.save(Pet.me("XiaoBai"));
		Pet xb2 = dao.findById(Pet.class, xb.getId());
		assertEquals(xb.getName(), xb2.getName());
	}

	@Test
	public void test_simple_query() {
		dao.create(Pet.class, true);
		dao.save(Pet.me("XiaoBai", 10, 3));
		dao.save(Pet.me("XiaoHei", 20, 3));
		dao.save(Pet.me("SuperMan", 20, 3));
		dao.save(Pet.me("Bush", 10, 3));
		dao.save(Pet.me("XiaoQiang", 10, 3));

		List<Pet> pets = dao.find(Pet.class, Moo.born().append("age", 20), null);
		assertEquals(2, pets.size());

		pets = dao.find(Pet.class, null, MCur.born().asc("name"));
		assertEquals(5, pets.size());
		assertEquals("Bush", pets.get(0).getName());
		assertEquals("SuperMan", pets.get(1).getName());
		assertEquals("XiaoBai", pets.get(2).getName());
		assertEquals("XiaoHei", pets.get(3).getName());
		assertEquals("XiaoQiang", pets.get(4).getName());

	}

	//测试唯一性索引
	// XXX 由于mongodb的特性,插入失败并不会报错,除非执行getError
	@Test
	public void test_index_unique() {
		dao.create(Pet.class, true);
		dao.save(Pet.me("XiaoBai", 10, 3));
		dao.save(Pet.me("XiaoBai", 2, 222));
		assertEquals(1, dao.count(Pet.class, null));
	}
}
