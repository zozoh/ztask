package org.nutz.mongo;

import com.mongodb.DB;

public interface DBCallback {

	//TODO 到底要不要返回值呢?!!
	void run(DB db);
}
