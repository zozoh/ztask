package org.nutz.ztask.api;

import org.nutz.mongo.MongoDao;

public interface AbstractService {

	// TODO 与  Mongo 分开 ...
	MongoDao dao();
	
}
