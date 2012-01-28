package org.nutz.ztask;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ztask.impl.mongo.MongoLabelServiceTest;
import org.nutz.ztask.impl.mongo.MongoTaskServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({MongoLabelServiceTest.class, MongoTaskServiceTest.class})
public class AllZTask {}
