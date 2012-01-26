package org.nutz.mongo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.mongo.dao.MongoDaoPojoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({MongoDaoPojoTest.class})
public class AllMongo {}
