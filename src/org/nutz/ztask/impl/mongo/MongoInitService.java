package org.nutz.ztask.impl.mongo;

import org.nutz.mongo.MongoConnector;
import org.nutz.ztask.api.InitService;
import org.nutz.ztask.api.Label;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskStack;

public class MongoInitService extends AbstractMongoService implements InitService {

	public MongoInitService(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	@Override
	public void init() {
		dao.create(Task.class, false);
		dao.create(TaskStack.class, false);
		dao.create(Label.class, false);
	}

}
