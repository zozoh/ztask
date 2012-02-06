package org.nutz.ztask;

import static org.junit.Assert.assertEquals;

import java.util.Date;

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
	
	public static void AD(String b, String e, Date[] ds) {
		assertEquals(b, ZTasks.D(ds[0]));
		assertEquals(e, ZTasks.D(ds[1]));
	}

	protected long countStack() {
		return dao.count(TaskStack.class, null);
	}

	protected long countTask() {
		return dao.count(Task.class, null);
	}

	protected Task t(String title) {
		return t(null, title);
	}

	protected Task t(Task p, String title) {
		Task t = new Task();
		if (null != p)
			t.setParentId(p.get_id());
		t.setText(title);
		return t;
	}

	protected Task t_l(String title, String... lbs) {
		Task t = new Task();
		t.setText(title);
		t.setLabels(lbs);
		return t;
	}

	protected Task t_u(String title, String userName) {
		Task t = new Task();
		t.setText(title);
		t.setCreater(userName);
		t.setOwner(userName);
		return t;
	}

	protected Task t_u(Task p, String title, String userName) {
		Task t = new Task();
		if (null != p)
			t.setParentId(p.get_id());
		t.setText(title);
		t.setCreater(userName);
		t.setOwner(userName);
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
