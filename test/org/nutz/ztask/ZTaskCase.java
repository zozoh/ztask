package org.nutz.ztask;

import org.junit.After;
import org.junit.Before;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.MongoDao;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.web.ZTaskConfig;

public class ZTaskCase {

	/*
	 * 将数据库指向测试专用数据库，即，加一个后缀
	 */
	static {
		ZTaskConfig.JUNIT_DB_SUFFIX = "_unit";
	}

	protected Ioc ioc;

	private String dbname;

	protected MongoDao dao;

	@Before
	public void before() {
		ioc = new NutIoc(new JsonLoader("ioc"));
		dbname = ioc.get(ZTaskConfig.class, "conf").get("db-name");
		dao = ioc.get(MongoConnector.class, "connector").getDao(dbname
																+ ZTaskConfig.JUNIT_DB_SUFFIX);
		onBefore();
	}

	@After
	public void after() {
		ioc.depose();
	}

	protected long countStack() {
		return dao.count(TaskStack.class, null);
	}

	protected long countTask() {
		return dao.count(Task.class, null);
	}

	protected Task t(String title) {
		Task t = new Task();
		t.setTitle(title);
		return t;
	}

	protected TaskStack ts(String name) {
		TaskStack ts = new TaskStack();
		ts.setName(name);
		return ts;
	}

	protected <T> T getService(Class<T> serviceType) {
		return ioc.get(serviceType);
	}

	protected void onBefore() {}
}
