package org.nutz.mongo;

import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;

import com.mongodb.MongoException;

public class MongoCase {

	protected MongoDao dao;

	private MongoConnector mgconn;

	@Before
	public void before() throws UnknownHostException, MongoException {
		mgconn = new MongoConnector("localhost", 27017);
		dao = mgconn.getDao("nutz_mongo_unit");
		onBefore();
	}

	protected void onBefore() {}

	@After
	public void after() {
		dao.cleanCursors();
		mgconn.close();
	}

}
