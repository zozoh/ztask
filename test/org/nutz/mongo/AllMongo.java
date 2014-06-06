package org.nutz.mongo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.mongo.dao.MongoDaoPojoTest;
import org.nutz.mongo.util.SafeMooMethodInterceptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({MongoDaoPojoTest.class, SafeMooMethodInterceptorTest.class})
public class AllMongo {}
