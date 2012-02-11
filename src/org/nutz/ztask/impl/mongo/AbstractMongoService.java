package org.nutz.ztask.impl.mongo;

import org.nutz.ioc.Ioc;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.MongoDao;
import org.nutz.ztask.api.ZTaskFactory;

public abstract class AbstractMongoService {

	protected MongoDao dao;

	protected ZTaskFactory factory;
	
	protected Ioc ioc;

	public AbstractMongoService(MongoConnector conn, String dbname) {
		this.dao = conn.getDao(dbname);
	}

	public MongoDao dao() {
		return dao;
	}

}
