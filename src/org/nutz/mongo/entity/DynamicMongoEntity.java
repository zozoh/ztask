package org.nutz.mongo.entity;

import java.util.Map;
import java.util.TreeMap;

import org.bson.types.ObjectId;
import org.nutz.lang.random.R;
import org.nutz.mongo.Mongos;

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

	protected String evalCollectionName(Map<String, Object> map) {
		return map.get(Mongos.COLLECTION_KEY).toString();
	}

	@SuppressWarnings("unchecked")
	protected DBObject fromMap(Map<String, Object> map) {
		DBObject dbo = new BasicDBObject();
		if (null != map) {
			for (Map.Entry<String, Object> en : map.entrySet()) {
				Object val = en.getValue();
				if (null == val)
					continue;
				// 如果是枚举
				if (val.getClass().isEnum())
					val = val.toString();
				// 如果是 Map，递归
				else if (val instanceof Map<?, ?>)
					val = fromMap((Map<String, Object>) val);

				// 加入 DBObject
				dbo.put(en.getKey(), val);
			}
		}
		return dbo;
	}

	protected Map<String, Object> toMap(DBObject dbo) {
		if (null == dbo)
			return null;
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

	protected DBObject map2dbo(Map<String, Object> map) {
		return Mongos.map2dbo(map);
	}

	public String getFieldDbName(String key) {
		return key;
	}

	public Map<String, MongoEntityField> getFields() {
		return null;
	}
}
