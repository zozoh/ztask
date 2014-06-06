package org.nutz.mongo.util;

import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.MongoDao;

public class MyMongoDao extends MongoDao {

	public MyMongoDao(MongoConnector conn, String dbname) {
		super(conn.getMongo().getDB(dbname));
	}

}
