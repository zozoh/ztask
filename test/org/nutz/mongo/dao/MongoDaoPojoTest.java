package org.nutz.mongo.dao;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.util.Callback;
import org.nutz.mongo.MongoCase;
import org.nutz.mongo.dao.pojo.CappedPet;
import org.nutz.mongo.dao.pojo.Pet;
import org.nutz.mongo.dao.pojo.Pet2;
import org.nutz.mongo.dao.pojo.PetFood;
import org.nutz.mongo.dao.pojo.PetType;
import org.nutz.mongo.dao.pojo.SInner;
import org.nutz.mongo.dao.pojo.SObj;
import org.nutz.mongo.util.MKeys;
import org.nutz.mongo.util.Moo;
import org.nutz.mongo.util.MCur;

import com.mongodb.DB;
import com.mongodb.WriteResult;

public class MongoDaoPojoTest extends MongoCase {

	@Test
	public void test_sum() {
		dao.create(Pet.class, true);
		dao.save(Pet.NEW("A", 6));
		dao.save(Pet.NEW("B", 3));
		dao.save(Pet.NEW("C", 1));

		long sum = dao.sum(Pet.class, null, "age");
		assertEquals(10, sum);

		sum = dao.sum(Pet.class, Moo.LT("age", 5), "age");
		assertEquals(4, sum);
	}

	@Test
	public void test_lt_obj_default_id() {
		dao.create(SObj.class, true);
		dao.save(SObj.NEW("A"));
		dao.save(SObj.NEW("B"));
		dao.save(SObj.NEW("C"));

		String bId = dao.findOne(SObj.class, Moo.NEW("name", "B")).getId();

		Moo q = Moo.LTE(new ObjectId(bId));
		List<SObj> objs = dao.find(SObj.class, q, null);

		assertEquals(2, objs.size());
		assertEquals("A", objs.get(0).getName());
		assertEquals("B", objs.get(1).getName());
	}

	@Test
	public void test_push_obj_array() {
		dao.create(Pet.class, true);
		Pet pet = dao.save(Pet.NEW("A"));

		PetFood food = new PetFood();
		food.setName("fish");
		food.setPrice(48);

		dao.updateById(Pet.class, pet.getId(), Moo.NEW().push("foods", food));

		pet = dao.findById(Pet.class, pet.getId());
		assertEquals(1, pet.getFoods().length);
		assertEquals("fish", pet.getFoods()[0].getName());
		assertEquals(48, pet.getFoods()[0].getPrice());
	}

	@Test
	public void test_push_pojo_array() {
		dao.create(Pet.class, true);
		Pet pet = dao.save(Pet.NEW("A"));
		dao.updateById(Pet.class, pet.getId(), Moo.NEW().push("friends", Pet.NEW("B")));

		pet = dao.findById(Pet.class, pet.getId());
		assertEquals(1, pet.getFriends().length);
		assertEquals("B", pet.getFriends()[0].getName());
	}

	@Test
	public void test_find_by_date_null() {
		dao.create(Pet.class, true);
		dao.save(Pet.NEW("A"));
		dao.save(Pet.BIRTHDAY("B", "2009-09-21 00:00:11"));
		dao.save(Pet.NEW("C"));

		List<Pet> pets;

		pets = dao.find(Pet.class, Moo.NEW("birthday", null), null);
		assertEquals(2, pets.size());
		assertEquals("A", pets.get(0).getName());
		assertEquals("C", pets.get(1).getName());

		pets = dao.find(Pet.class, Moo.NEW().ne("birthday", null), null);
		assertEquals(1, pets.size());
		assertEquals("B", pets.get(0).getName());
	}

	@Test
	public void test_find_and_modify_remove() {
		dao.create(Pet.class, true);
		dao.save(Pet.NEW("A"));
		Pet b = dao.save(Pet.NEW("B"));
		dao.save(Pet.NEW("C"));

		Pet pet = dao.findAndModify(Pet.class, Moo.NEW("name", "B"), Moo.SET("age", 800));
		assertEquals(b.getAge(), pet.getAge());

		pet = dao.findAndRemove(Pet.class, Moo.NEW("name", "B"));
		assertEquals(800, pet.getAge());

		List<Pet> pets = dao.find(Pet.class, null, null);
		assertEquals(2, pets.size());
		assertEquals("A", pets.get(0).getName());
		assertEquals("C", pets.get(1).getName());

		pet = dao.findAndModify(Pet.class, Moo.NEW("name", "B"), Moo.SET("age", 900));
		assertNull(pet);

		pet = dao.findAndRemove(Pet.class, Moo.NEW("name", "B"));
		assertNull(pet);

	}

	@Test
	public void test_two_asc() {
		dao.create(Pet.class, true);
		dao.save(Pet.AGE("A", 4, 1));
		dao.save(Pet.AGE("B", 3, 2));
		dao.save(Pet.AGE("C", 2, 2));
		dao.save(Pet.AGE("D", 1, 3));

		List<Pet> pets = dao.find(Pet.class, null, MCur.ASC("count").asc("age"));
		assertEquals("A", pets.get(0).getName());
		assertEquals("C", pets.get(1).getName());
		assertEquals("B", pets.get(2).getName());
		assertEquals("D", pets.get(3).getName());

	}

	@Test
	public void test_simple_push_pop_array_field() {
		dao.create(Pet.class, true);
		Pet a = dao.save(Pet.NEW("A"));
		Pet p;

		dao.updateById(Pet.class, a.getId(), Moo.NEW().push("labels", "x"));
		p = dao.findOne(Pet.class, null);
		assertEquals(1, p.getLabels().length);
		assertEquals("x", p.getLabels()[0]);
	}

	@Test
	public void test_push_pop_array_field() {
		dao.create(Pet.class, true);
		Pet a = dao.save(Pet.NEW("A"));
		Pet p;

		dao.updateById(Pet.class, a.getId(), Moo.NEW().push("labels", "x", "y", "z"));
		p = dao.findOne(Pet.class, null);
		assertEquals(3, p.getLabels().length);
		assertEquals("x", p.getLabels()[0]);
		assertEquals("y", p.getLabels()[1]);
		assertEquals("z", p.getLabels()[2]);

		dao.update(Pet.class, null, Moo.NEW().pull("labels", "y"));
		p = dao.findOne(Pet.class, null);
		assertEquals(2, p.getLabels().length);
		assertEquals("x", p.getLabels()[0]);
		assertEquals("z", p.getLabels()[1]);

		dao.update(Pet.class, null, Moo.NEW().pull("labels", "x"));
		p = dao.findOne(Pet.class, null);
		assertEquals(1, p.getLabels().length);
		assertEquals("z", p.getLabels()[0]);

		dao.update(Pet.class, null, Moo.NEW().pull("labels", "z"));
		p = dao.findOne(Pet.class, null);
		assertEquals(0, p.getLabels().length);

		dao.updateById(Pet.class, a.getId(), Moo.NEW().push("labels", "x", "y", "z"));
		p = dao.findOne(Pet.class, null);
		assertEquals(3, p.getLabels().length);
		assertEquals("x", p.getLabels()[0]);
		assertEquals("y", p.getLabels()[1]);
		assertEquals("z", p.getLabels()[2]);

		dao.update(Pet.class, null, Moo.NEW().pop("labels"));
		p = dao.findOne(Pet.class, null);
		assertEquals(2, p.getLabels().length);
		assertEquals("x", p.getLabels()[0]);
		assertEquals("y", p.getLabels()[1]);

		dao.update(Pet.class, null, Moo.NEW().popHead("labels"));
		p = dao.findOne(Pet.class, null);
		assertEquals(1, p.getLabels().length);
		assertEquals("y", p.getLabels()[0]);

		dao.update(Pet.class, null, Moo.NEW().pop("labels"));
		p = dao.findOne(Pet.class, null);
		assertEquals(0, p.getLabels().length);

		dao.update(Pet.class, null, Moo.NEW().unset("labels"));
		p = dao.findOne(Pet.class, null);
		assertNull(p.getLabels());

	}

	@Test
	public void test_simple_skip() {
		dao.create(Pet.class, true);
		dao.save(Pet.NEW("A"));
		dao.save(Pet.NEW("B"));
		dao.save(Pet.NEW("C"));
		dao.save(Pet.NEW("D"));

		MCur mcur = MCur.ASC("name");
		List<Pet> pets;

		pets = dao.find(Pet.class, null, mcur.skip(2));
		assertEquals(2, pets.size());
		assertEquals("C", pets.get(0).getName());
		assertEquals("D", pets.get(1).getName());
	}

	@Test
	public void test_simple_data_time() {
		dao.create(Pet.class, true);
		dao.save(Pet.BIRTHDAY("A", "1977-09-21 08:45:32"));
		dao.save(Pet.BIRTHDAY("B", "1980-02-04 09:16:16"));
		dao.save(Pet.BIRTHDAY("C", "1991-12-30 12:23:48"));
		dao.save(Pet.BIRTHDAY("D", "2006-10-11 18:00:15"));

		MCur mcur = MCur.ASC("name");
		List<Pet> pets;

		pets = dao.find(Pet.class, Moo.D_EQUALS("birthday", Times.D("1991-12-30 12:23:48")), mcur);
		assertEquals(1, pets.size());
		assertEquals("C", pets.get(0).getName());

		pets = dao.find(Pet.class,
						Moo.IN(	"birthday",
								Castors.me().castTo("1991-12-30 12:23:48", Date.class),
								Castors.me().castTo("1977-09-21 08:45:32", Date.class)),
						mcur);
		assertEquals(2, pets.size());
		assertEquals("A", pets.get(0).getName());
		assertEquals("C", pets.get(1).getName());

		pets = dao.find(Pet.class,
						Moo.D_GT("birthday", Times.D("1980-01-01 00:00:00"))
							.d_lt("birthday", Times.D("2010-01-01 00:00:00")),
						mcur);
		assertEquals(3, pets.size());
		assertEquals("B", pets.get(0).getName());
		assertEquals("C", pets.get(1).getName());
		assertEquals("D", pets.get(2).getName());

		Pet a = dao.findOne(Pet.class, Moo.NEW("name", "A"));
		assertEquals("1977-09-21 08:45:32", Castors.me().castToString(a.getBirthday()));

	}

	@Test
	public void test_query_by_or_not() {
		dao.create(Pet.class, true);
		dao.save(Pet.AGE("A", 1, -1));
		dao.save(Pet.AGE("B", 2, -1));
		dao.save(Pet.AGE("C", 3, -1));
		dao.save(Pet.AGE("D", 4, -1));
		dao.save(Pet.AGE("E", 5, -1));

		MCur mcur = MCur.ASC("name");
		List<Pet> pets;

		pets = dao.find(Pet.class, Moo.OR(Moo.NEW("name", "A"), Moo.NEW("name", "E")), mcur);
		assertEquals(2, pets.size());
		assertEquals("A", pets.get(0).getName());
		assertEquals("E", pets.get(1).getName());

		pets = dao.find(Pet.class, Moo.NOT(Moo.LT("age", 4)), mcur);
		assertEquals(2, pets.size());
		assertEquals("D", pets.get(0).getName());
		assertEquals("E", pets.get(1).getName());
	}

	@Test
	public void test_query_in_array_by_index() {
		dao.create(SObj.class, true);
		dao.save(SObj.NUMS("A", 1, 3, 5));
		dao.save(SObj.NUMS("B", 2, 4, 6));
		dao.save(SObj.NUMS("C", 3, 5, 7));
		dao.save(SObj.NUMS("D", 4, 6, 8));

		MCur mcur = MCur.ASC("name");
		List<SObj> objs;

		objs = dao.find(SObj.class, Moo.GT("numbers.1", 5), mcur);
		assertEquals(1, objs.size());
		assertEquals("D", objs.get(0).getName());
	}

	@Test
	public void test_simple_gt_lt() {
		dao.create(Pet.class, true);
		dao.save(Pet.AGE("A", 1, -1));
		dao.save(Pet.AGE("B", 2, -1));
		dao.save(Pet.AGE("C", 3, -1));
		dao.save(Pet.AGE("D", 4, -1));
		dao.save(Pet.AGE("E", 5, -1));

		MCur mcur = MCur.ASC("name");
		List<Pet> pets;

		pets = dao.find(Pet.class, Moo.NEW().gte("age", 2).lt("age", 4), mcur);
		assertEquals(2, pets.size());
		assertEquals("B", pets.get(0).getName());
		assertEquals("C", pets.get(1).getName());

		pets = dao.find(Pet.class, Moo.NEW().gt("age", 1), mcur);
		assertEquals(4, pets.size());
		assertEquals("B", pets.get(0).getName());
		assertEquals("C", pets.get(1).getName());
		assertEquals("D", pets.get(2).getName());
		assertEquals("E", pets.get(3).getName());

		pets = dao.find(Pet.class, Moo.NEW().gte("age", 4), mcur);
		assertEquals(2, pets.size());
		assertEquals("D", pets.get(0).getName());
		assertEquals("E", pets.get(1).getName());

		pets = dao.find(Pet.class, Moo.NEW().lte("age", 2), mcur);
		assertEquals(2, pets.size());
		assertEquals("A", pets.get(0).getName());
		assertEquals("B", pets.get(1).getName());

		pets = dao.find(Pet.class, Moo.NEW().lt("age", 2), mcur);
		assertEquals(1, pets.size());
		assertEquals("A", pets.get(0).getName());

	}

	@Test
	public void test_query_in_default_id() {
		dao.create(SObj.class, true);
		SObj obj = dao.save(SObj.NEW("ABC"));

		SObj o2 = dao.findOne(SObj.class, Moo.NEW().in("id", obj.getId()));
		assertEquals(obj.getName(), o2.getName());
	}

	@Test
	public void test_find_filter_by_fields() {
		dao.create(Pet.class, true);
		dao.save(Pet.AGE("XB", 10, 80));

		Pet pet = dao.find(Pet.class, null, MKeys.ON("name"), null).get(0);
		assertEquals("XB", pet.getName());
		assertEquals(0, pet.getAge());
		assertEquals(0, pet.getCount());
	}

	@Test
	public void test_save_inner_pojo() {
		dao.create(SObj.class, true);

		SObj o = SObj.NEW("abc");
		o.setObj(SInner.me(6, 9));
		dao.save(o);

		SObj o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(6, o2.getObj().getX());
		assertEquals(9, o2.getObj().getY());

		o.setObj(SInner.me(22, 33));
		dao.save(o);

		o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(22, o2.getObj().getX());
		assertEquals(33, o2.getObj().getY());

		dao.updateById(SObj.class, o.getId(), Moo.NEW().set("obj", SInner.me(44, 55)));

		o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(44, o2.getObj().getX());
		assertEquals(55, o2.getObj().getY());
	}

	@Test
	public void test_save_inner_objarray() {
		dao.create(SObj.class, true);

		SObj o = SObj.NEW("abc");
		o.setInners(Lang.array(SInner.me(45, 90), SInner.me(43, 62)));
		dao.save(o);

		SObj o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(2, o2.getInners().length);
		assertEquals(45, o2.getInners()[0].getX());
		assertEquals(90, o2.getInners()[0].getY());
		assertEquals(43, o2.getInners()[1].getX());
		assertEquals(62, o2.getInners()[1].getY());

		o.setInners(Lang.array(SInner.me(3, 5)));
		dao.save(o);

		o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(1, o2.getInners().length);
		assertEquals(3, o2.getInners()[0].getX());
		assertEquals(5, o2.getInners()[0].getY());

		dao.updateById(	SObj.class,
						o.getId(),
						Moo.NEW().set(	"inners",
										Lang.array(	SInner.me(77, 88),
													SInner.me(44, 33),
													SInner.me(55, 66))));

		o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(3, o2.getInners().length);
		assertEquals(77, o2.getInners()[0].getX());
		assertEquals(88, o2.getInners()[0].getY());
		assertEquals(44, o2.getInners()[1].getX());
		assertEquals(33, o2.getInners()[1].getY());
		assertEquals(55, o2.getInners()[2].getX());
		assertEquals(66, o2.getInners()[2].getY());

	}

	@Test
	public void test_save_inner_map() {
		dao.create(SObj.class, true);

		SObj o = SObj.NEW("abc");
		o.setMap("{x:1, y:36}");
		dao.save(o);

		SObj o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(1, ((Integer) o2.getMap().get("x")).intValue());
		assertEquals(36, ((Integer) o2.getMap().get("y")).intValue());

		o.setMap("{x:88, y:93}");
		dao.save(o);

		o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(88, ((Integer) o2.getMap().get("x")).intValue());
		assertEquals(93, ((Integer) o2.getMap().get("y")).intValue());

		dao.updateById(SObj.class, o.getId(), Moo.NEW().set("map", Lang.map("{x:100,y:300}")));

		o2 = dao.findById(SObj.class, o.getId());
		assertEquals(o.getName(), o2.getName());
		assertEquals(100, ((Integer) o2.getMap().get("x")).intValue());
		assertEquals(300, ((Integer) o2.getMap().get("y")).intValue());

	}

	@Test
	public void test_save_again_by_defaulID() {
		dao.create(SObj.class, true);

		// Create
		SObj obj = dao.save(SObj.NEW("xyz"));

		// Update
		obj.setNum(3000);
		dao.save(obj);

		// Get back
		SObj obj2 = dao.findById(SObj.class, obj.getId());

		// Assert
		assertEquals(3000, obj2.getNum());
	}

	@Test
	public void test_save_again_by_uu64() {
		dao.create(Pet.class, true);

		Pet xb = dao.save(Pet.NEW("xb"));
		xb.setCount(6000);

		dao.save(xb);
		Pet xb2 = dao.findById(Pet.class, xb.getId());

		assertEquals(6000, xb2.getCount());
	}

	@Test
	public void test_query_in_array() {
		dao.create(Pet.class, true);
		dao.save(Pet.NEW("xiaohei"));
		dao.save(Pet.NEW("xiaobai"));
		dao.save(Pet.NEW("super.man"));
		dao.save(Pet.NEW("bush"));
		dao.save(Pet.NEW("zozoh"));

		List<Pet> pets = dao.find(	Pet.class,
									Moo.NEW().in("name", "xiaohei", "zozoh"),
									MCur.NEW().asc("name"));
		assertEquals(2, pets.size());
		assertEquals("xiaohei", pets.get(0).getName());
		assertEquals("zozoh", pets.get(1).getName());
	}

	@Test
	public void test_query_by_labels() {
		dao.create(Pet.class, true);
		dao.save(Pet.LBS("xiaohei", "A", "B", "C"));
		dao.save(Pet.LBS("xiaobai", "A", "C"));
		dao.save(Pet.LBS("super.man", "A", "B"));
		dao.save(Pet.LBS("bush", "B"));
		dao.save(Pet.LBS("zozoh", "C"));

		// 默认按照名字从小到大
		MCur c = MCur.NEW().asc("name");

		// A+C
		List<Pet> pets = dao.find(Pet.class, Moo.NEW().array("labels", "A", "C"), c);
		assertEquals(2, pets.size());
		assertEquals("xiaobai", pets.get(0).getName());
		assertEquals("xiaohei", pets.get(1).getName());

		// B+C
		pets = dao.find(Pet.class, Moo.NEW().array("labels", "B", "C"), c);
		assertEquals(1, pets.size());
		assertEquals("xiaohei", pets.get(0).getName());

		// C
		pets = dao.find(Pet.class, Moo.NEW().array("labels", "C"), c);
		assertEquals(3, pets.size());
		assertEquals("xiaobai", pets.get(0).getName());
		assertEquals("xiaohei", pets.get(1).getName());
		assertEquals("zozoh", pets.get(2).getName());

	}

	@Test
	public void test_query_by_regex() {
		dao.create(Pet.class, true);
		dao.save(Pet.NEW("xiaohei"));
		dao.save(Pet.NEW("xiaobai"));
		dao.save(Pet.NEW("super.man"));
		dao.save(Pet.NEW("bush"));
		dao.save(Pet.NEW("zozoh"));

		// startsWith
		List<Pet> pets = dao.find(	Pet.class,
									Moo.NEW().startsWith("name", "x"),
									MCur.NEW().desc("name"));
		assertEquals(2, pets.size());
		assertEquals("xiaohei", pets.get(0).getName());
		assertEquals("xiaobai", pets.get(1).getName());

		// contains
		pets = dao.find(Pet.class, Moo.NEW().contains("name", "h"), MCur.NEW().desc("name"));
		assertEquals(3, pets.size());
		assertEquals("zozoh", pets.get(0).getName());
		assertEquals("xiaohei", pets.get(1).getName());
		assertEquals("bush", pets.get(2).getName());

		// regex
		pets = dao.find(Pet.class,
						Moo.NEW().match("name", "^(zozoh)|(.*u[p|s].*)$"),
						MCur.NEW().desc("name"));
		assertEquals(3, pets.size());
		assertEquals("zozoh", pets.get(0).getName());
		assertEquals("super.man", pets.get(1).getName());
		assertEquals("bush", pets.get(2).getName());
	}

	@Test
	public void test_default_id_save_and_remove() {
		dao.create(SObj.class, true);
		SObj obj = SObj.NEW("ABC");
		dao.save(obj);
		assertEquals(1, dao.count(obj, null));

		obj = dao.findOne(SObj.class, Moo.NEW().append("name", "ABC"));

		dao.removeById(SObj.class, obj.getId());
		assertEquals(0, dao.count(obj, null));
	}

	@Test
	public void test_enum_type_field() {
		dao.create(Pet.class, true);
		Pet snoopy = dao.save(Pet.NEW(PetType.DOG, "Snoopy"));
		dao.save(snoopy);
		assertNotNull(snoopy.getId());

		Pet snoopy2 = dao.findById(Pet.class, snoopy.getId());
		assertEquals(snoopy.getType(), snoopy2.getType());

		dao.save(Pet.NEW(PetType.CAT, "Tom"));
		dao.update(	Pet.class,
					Moo.NEW().append("type", PetType.CAT),
					Moo.NEW().set("type", PetType.MONKEY).set("count", 3000));

		Pet tom = dao.findOne(Pet.class, Moo.NEW().append("name", "Tom"));
		assertEquals(3000, tom.getCount());
		assertEquals(PetType.MONKEY, tom.getType());
	}

	@Test
	public void test_simple_update() {
		dao.create(Pet.class, true);
		Pet xb = dao.save(Pet.NEW("XiaoBai"));

		WriteResult wr = dao.update(xb,
									Moo.NEW().append("id", xb.getId()),
									Moo.NEW().set("name", "XB").inc("age", 2));
		assertEquals(1, wr.getN());
		assertNull(wr.getError());

		Pet xb2 = dao.findById(Pet.class, xb.getId());
		assertEquals(xb.getAge() + 2, xb2.getAge());
		assertEquals("XB", xb2.getName());

		dao.updateById(Pet.class, xb.getId(), Moo.NEW().inc("count", 1));
		Pet xb3 = dao.findById(Pet.class, xb.getId());
		assertEquals(xb.getCount() + 1, xb3.getCount());
		assertEquals("XB", xb3.getName());

		wr = dao.update(xb,
						Moo.NEW().append("id", "888888"),
						Moo.NEW().set("name", "GGGGGG").inc("age", 2555));
		assertEquals(0, wr.getN());
		assertNull(wr.getError());
	}

	@Test
	public void test_simple_save_find() {
		dao.create(Pet.class, true);
		Pet xb = dao.save(Pet.NEW("XiaoBai"));
		Pet xb2 = dao.findById(Pet.class, xb.getId());
		assertEquals(xb.getName(), xb2.getName());
	}

	@Test
	public void test_simple_query() {
		dao.create(Pet.class, true);
		dao.save(Pet.AGE("XiaoBai", 10, 3));
		dao.save(Pet.AGE("XiaoHei", 20, 3));
		dao.save(Pet.AGE("SuperMan", 20, 3));
		dao.save(Pet.AGE("Bush", 10, 3));
		dao.save(Pet.AGE("XiaoQiang", 10, 3));

		List<Pet> pets = dao.find(Pet.class, Moo.NEW().append("age", 20), null);
		assertEquals(2, pets.size());

		pets = dao.find(Pet.class, null, MCur.NEW().asc("name"));
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
		dao.save(Pet.AGE("XiaoBai", 10, 3));
		dao.runNoError(new Callback<DB>() {
			public void invoke(DB db) {
				dao.save(Pet.AGE("XiaoBai", 2, 222));
			}
		});
	}

	// 测试固定集合
	@Test
	public void test_capped() {
		dao.create(CappedPet.class, true); // 固定大小是10k,肯定放不到1000个对象
		for (int i = 0; i < 1000; i++) {
			CappedPet pet = new CappedPet();
			pet.setName("V" + System.currentTimeMillis());
			dao.save(pet);
		}
		long size = dao.count(CappedPet.class, null);
		System.out.println("CappedPet size=" + size);
		assertTrue(100 >= size);
	}

	@Test
	public void test_ref() {
		Pet embed = Pet.AGE("embed", 10, 1000);
		Pet lazy = Pet.AGE("LAZY", 20, 2000);
		Pet ref = Pet.AGE("REF", 30, 3000);
		dao.create(Pet.class, true);
		// dao.save(embed);
		dao.save(lazy);
		dao.save(ref);
		assertEquals(2, dao.count(Pet.class, null));

		dao.create(Pet2.class, true);
		Pet2 pet2 = new Pet2();
		pet2.setName("XXX");
		pet2.setId((int) System.currentTimeMillis());
		pet2.setEmbedPet(embed);
		pet2.setLazyPet(lazy);
		pet2.setRefPet(ref);
		pet2.setPets(new Pet[]{embed, embed});
		pet2.setRefPets(new Pet[]{ref, lazy});
		dao.save(pet2);
		assertTrue(dao.count(Pet2.class, null) == 1);
		Pet2 p = dao.findOne(Pet2.class, null);
		assertNotNull(p);
		// assertEquals(embed.getId(), p.getEmbedPet().getId());
		assertEquals(lazy.getId(), p.getLazyPet().getId());
		assertEquals(ref.getId(), p.getRefPet().getId());
		assertEquals("embed", p.getEmbedPet().getName());
		assertEquals(null, p.getLazyPet().getName());
		assertEquals("REF", p.getRefPet().getName());

		assertEquals(2, p.getPets().length);
		assertEquals("embed", p.getPets()[0].getName());
		assertEquals("embed", p.getPets()[1].getName());

		assertEquals(2, p.getRefPets().length);
		assertEquals("REF", p.getRefPets()[0].getName());
		assertEquals("LAZY", p.getRefPets()[1].getName());
	}
}
