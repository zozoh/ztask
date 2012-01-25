package org.nutz.mongo.entity;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Mirror;
import org.nutz.lang.born.Borning;

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
	public DBObject from(T obj) {
		DBObject dbo = new BasicDBObject();
		for (MongoEntityField mef : fields.values())
			mef.setToDB(obj, dbo);
		return dbo;
	}

	@Override
	public T to(DBObject dbo) {
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
	public void fillIdIfNoexits(T obj) {}

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
