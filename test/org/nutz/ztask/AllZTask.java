package org.nutz.ztask;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.ztask.api.TaskQueryTest;
import org.nutz.ztask.impl.FileTaskReportTest;
import org.nutz.ztask.impl.mongo.MongoLabelServiceTest;
import org.nutz.ztask.impl.mongo.MongoMailQueueTest;
import org.nutz.ztask.impl.mongo.MongoMessageServiceTest;
import org.nutz.ztask.impl.mongo.MongoTaskServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({	ZTasksTest.class,
						TaskQueryTest.class,
						FileTaskReportTest.class,
						MongoMessageServiceTest.class,
						MongoLabelServiceTest.class,
						MongoMailQueueTest.class,
						MongoTaskServiceTest.class})
public class AllZTask {}
