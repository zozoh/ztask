package org.nutz.mongo;

import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;

import com.mongodb.MongoException;

public class MongoCase {

	protected MongoDao mgdao;

	private MongoConnector mgconn;

	@Before
	public void before() throws UnknownHostException, MongoException {
		mgconn = new MongoConnector("localhost", 27017);
		mgdao = mgconn.getDao("nutz_mongo_unit");
	}

	@After
	public void after() {
		mgdao.cleanCursors();
		mgconn.close();
	}

}
