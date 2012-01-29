package org.nutz.mongo.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.lang.util.Callback;
import org.nutz.mongo.MongoCase;
import org.nutz.mongo.dao.pojo.Pet;
import org.nutz.mongo.dao.pojo.PetType;
import org.nutz.mongo.dao.pojo.SObj;
import org.nutz.mongo.util.Moo;
import org.nutz.mongo.util.MCur;

import com.mongodb.DB;

public class MongoDaoPojoTest extends MongoCase {

	@Test
	public void test_query_in_array() {
		dao.create(Pet.class, true);
		dao.save(Pet.me("xiaohei"));
		dao.save(Pet.me("xiaobai"));
		dao.save(Pet.me("super.man"));
		dao.save(Pet.me("bush"));
		dao.save(Pet.me("zozoh"));

		List<Pet> pets = dao.find(	Pet.class,
									Moo.born().in("name", "xiaohei", "zozoh"),
									MCur.born().asc("name"));
		assertEquals(2, pets.size());
		assertEquals("xiaohei", pets.get(0).getName());
		assertEquals("zozoh", pets.get(1).getName());
	}

	@Test
	public void test_query_by_labels() {
		dao.create(Pet.class, true);
		dao.save(Pet.mel("xiaohei", "A", "B", "C"));
		dao.save(Pet.mel("xiaobai", "A", "C"));
		dao.save(Pet.mel("super.man", "A", "B"));
		dao.save(Pet.mel("bush", "B"));
		dao.save(Pet.mel("zozoh", "C"));

		// 默认按照名字从小到大
		MCur c = MCur.born().asc("name");

		// A+C
		List<Pet> pets = dao.find(Pet.class, Moo.born().array("labels", "A", "C"), c);
		assertEquals(2, pets.size());
		assertEquals("xiaobai", pets.get(0).getName());
		assertEquals("xiaohei", pets.get(1).getName());

		// B+C
		pets = dao.find(Pet.class, Moo.born().array("labels", "B", "C"), c);
		assertEquals(1, pets.size());
		assertEquals("xiaohei", pets.get(0).getName());

		// C
		pets = dao.find(Pet.class, Moo.born().array("labels", "C"), c);
		assertEquals(3, pets.size());
		assertEquals("xiaobai", pets.get(0).getName());
		assertEquals("xiaohei", pets.get(1).getName());
		assertEquals("zozoh", pets.get(2).getName());

	}

	@Test
	public void test_query_by_regex() {
		dao.create(Pet.class, true);
		dao.save(Pet.me("xiaohei"));
		dao.save(Pet.me("xiaobai"));
		dao.save(Pet.me("super.man"));
		dao.save(Pet.me("bush"));
		dao.save(Pet.me("zozoh"));

		// startsWith
		List<Pet> pets = dao.find(	Pet.class,
									Moo.born().startsWith("name", "x"),
									MCur.born().desc("name"));
		assertEquals(2, pets.size());
		assertEquals("xiaohei", pets.get(0).getName());
		assertEquals("xiaobai", pets.get(1).getName());

		// contains
		pets = dao.find(Pet.class, Moo.born().contains("name", "h"), MCur.born().desc("name"));
		assertEquals(3, pets.size());
		assertEquals("zozoh", pets.get(0).getName());
		assertEquals("xiaohei", pets.get(1).getName());
		assertEquals("bush", pets.get(2).getName());

		// regex
		pets = dao.find(Pet.class,
						Moo.born().match("name", "^(zozoh)|(.*u[p|s].*)$"),
						MCur.born().desc("name"));
		assertEquals(3, pets.size());
		assertEquals("zozoh", pets.get(0).getName());
		assertEquals("super.man", pets.get(1).getName());
		assertEquals("bush", pets.get(2).getName());
	}

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

	// 测试唯一性索引
	@Test(expected = Throwable.class)
	public void test_index_unique() {
		dao.create(Pet.class, true);
		dao.save(Pet.me("XiaoBai", 10, 3));
		dao.runNoError(new Callback<DB>() {
			public void invoke(DB db) {
				dao.save(Pet.me("XiaoBai", 2, 222));
			}
		});
	}
}
