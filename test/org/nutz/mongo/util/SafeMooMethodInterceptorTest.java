package org.nutz.mongo.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.mongo.MongoDao;
import org.nutz.mongo.dao.pojo.Pet;

import com.mongodb.MongoException;

public class SafeMooMethodInterceptorTest {

	@Test(expected=MongoException.class)
	public void test_filter() {
		Ioc ioc = new NutIoc(new JsonLoader("org/nutz/mongo/util/meta/aop.js","ioc"));
		MongoDao dao = ioc.get(MyMongoDao.class);
		dao.create(Pet.class, true);
		dao.save(Pet.AGE("XiaoBai", 10, 3));
		dao.save(Pet.AGE("XiaoBai", 2, 222));//这里会抛异常
		fail();
	}

}
