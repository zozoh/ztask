package org.nutz.mongo.entity;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.born.Borning;
import org.nutz.mongo.Mongos;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 封装一个 POJO 与 DBObject 的映射关系
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public class StaticMongoEntity<T> implements MongoEntity<T> {

	private Class<T> type;

	private Mirror<T> mirror;

	private Borning<T> borning;

	private String collectionName;

	private Map<String, MongoEntityField> fields;

	private MongoEntityField _id;
	
	public StaticMongoEntity(Class<T> type) {
		this.type = type;
		this.mirror = Mirror.me(type);
		this.borning = this.mirror.getBorning();
		this.fields = new HashMap<String, MongoEntityField>();
	}

	@Override
	public DBObject toDBObject(T obj) {
		DBObject dbo = new BasicDBObject();
		for (MongoEntityField mef : fields.values())
			mef.setToDB(obj, dbo);
		return dbo;
	}

	@Override
	public T toObject(DBObject dbo) {
		if (null == dbo)
			return null;
		T obj = borning.born(new Object[0]);
		for (MongoEntityField mef : fields.values())
			mef.getFromDB(obj, dbo);
		return obj;
	}

	@Override
	public void fillId(T obj) {
		if (null != _id) {
			_id.fillId(obj);
		}
	}

	@Override
	public void fillIdIfNoexits(T obj) {
		if (null != _id && _id.isNull(obj)) {
			_id.fillId(obj);
		}
	}

	@Override
	public String getCollectionName(Object ref) {
		return this.collectionName;
	}

	@Override
	public String getFieldDbName(String key) {
		MongoEntityField mef = fields.get(key);
		if (null == mef)
			throw Lang.makeThrow("Unknow '%s' in '%s'", key, collectionName);
		return mef.getDbName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public DBObject formatObject(Object o) {
		if (null == o)
			return new BasicDBObject();
		// 如果是本类型
		if (type.isAssignableFrom(o.getClass())) {
			return toDBObject((T) o);
		}
		// 如果是 DBObject 直接返回 
		if (o instanceof DBObject)
			return (DBObject) o;

		// 否则变成 Map
		Map<String, Object> map = Mongos.obj2map(o);
		Map<String, Object> dboMap = new HashMap<String, Object>();
		// 循环，改变键值
		for (String key : map.keySet()) {
			// 那么让我们看看值吧 ...
			Object val = map.get(key);
			// 如果值还是一个 Map , 且当前 key 是个修改器
			if (null != val && key.startsWith("$") && val instanceof Map<?, ?>) {
				dboMap.put(key, formatObject(val));
				continue;
			}
			// 默认情况
			MongoEntityField mef = "_id".equals(key) ? _id : fields.get(key);
			// 未知的键，抛错
			if (null == mef)
				throw Lang.makeThrow("Unknow key '%s' in collection '%s'", key, collectionName);

			// 加入值
			dboMap.put(mef.getDbName(), mef.getAdaptor().adaptForGet(val));
		}
		// 建立返回值
		DBObject dbo = new BasicDBObject();
		dbo.putAll(dboMap);
		return dbo;
	}

	public Map<String, MongoEntityField> getFields() {
		return fields;
	}

	public Class<T> getType() {
		return type;
	}

	public Mirror<T> getMirror() {
		return mirror;
	}

	void addField(FieldInfo fi) {
		MongoEntityField mef = new MongoEntityField(fi);
		if (null != fi.get_id())
			_id = mef;
		fields.put(fi.getName(), mef);
	}

	String getCollectionName() {
		return collectionName;
	}

	void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}


}
