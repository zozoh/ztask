package org.nutz.mongo.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mongo.Mongos;

import com.mongodb.DBObject;

/**
 * 动态 MongoEntity 负责将一个 Map 与 DBObject，它向自己的子类提供一些帮助函数，子类将对象变成 Map 后调用
 * <p>
 * 与 MongoDB 默认设置不同，本类默认的，会为 _id 字段填充 UU64
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public class DynamicMongoEntity implements MongoEntity {

	public String getFieldDbName(String key) {
		return key;
	}

	@Override
	public long getCappedSize() {
		return -1;
	}
	
	@Override
	public long getCappedMax() {
		return -1;
	}

	@Override
	public String getCollectionName(Object ref) {
		NutMap map = Mongos.obj2map(ref);
		return map.get(Mongos.COLLECTION_KEY).toString();
	}

	@Override
	public boolean hasIndexes() {
		return false;
	}

	@Override
	public List<MongoEntityIndex> getIndexes() {
		return new LinkedList<MongoEntityIndex>();
	}

	@Override
	public DBObject formatObject(Object o) {
		return Mongos.obj2dbo(o);
	}

	@Override
	public DBObject toDBObject(Object obj) {
		return Mongos.obj2dbo(obj);
	}

	@Override
	public Object toObject(DBObject dbo) {
		if (null == dbo)
			return null;
		NutMap map = new NutMap();
		for (String key : dbo.keySet()) {
			Object dbval = dbo.get(key);
			if (dbval instanceof ObjectId)
				map.put(key, dbval.toString());
			else
				map.put(key, dbval);
		}
		return map;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void fillId(Object obj) {
		if (null != obj && obj instanceof Map) {
			((Map) obj).put("_id", new ObjectId());
			return;
		}
		throw _failToFill(obj);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public void fillIdIfNoexits(Object obj) {
		if (null != obj && obj instanceof Map) {
			if (!((Map) obj).containsKey("_id"))
				((Map) obj).put("_id", new ObjectId());
			return;
		}
		throw _failToFill(obj);
	}

	private RuntimeException _failToFill(Object obj) {
		if (obj == null)
			return Lang.makeThrow("Can only fillId to Map<String,Object> but it is NULL!");
		return Lang.makeThrow(	"Can only fillId to Map<String,Object> but it is '%s'",
								obj.getClass().getName());
	}
}
