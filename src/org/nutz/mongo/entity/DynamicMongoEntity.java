package org.nutz.mongo.entity;

import java.util.Map;
import java.util.TreeMap;

import org.bson.types.ObjectId;
import org.nutz.lang.random.R;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 动态 MongoEntity 负责将一个 Map 与 DBObject，它向自己的子类提供一些帮助函数，子类将对象变成 Map 后调用
 * <p>
 * 与 MongoDB 默认设置不同，本类默认的，会为 _id 字段填充 UU64
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public abstract class DynamicMongoEntity<T> implements MongoEntity<T> {

	protected DBObject fromMap(Map<String, Object> map) {
		DBObject dbo = new BasicDBObject();
		dbo.putAll(map);
		return dbo;
	}

	protected Map<String, Object> toMap(DBObject dbo) {
		Map<String, Object> map = new TreeMap<String, Object>();
		for (String key : dbo.keySet()) {
			Object dbval = dbo.get(key);
			if (dbval instanceof ObjectId)
				map.put(key, dbval.toString());
			else
				map.put(key, dbval);
		}
		return map;
	}

	protected void fillIdToMap(Map<String, Object> map) {
		map.put("_id", R.UU64());
	}

	protected void fillIdToMapIfNoexits(Map<String, Object> map) {
		if (!map.containsKey("_id"))
			map.put("_id", R.UU64());
	}

}
