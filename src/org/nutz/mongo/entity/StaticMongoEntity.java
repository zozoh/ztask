package org.nutz.mongo.entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
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
public class StaticMongoEntity implements MongoEntity {

	private Class<?> type;

	private Mirror<?> mirror;

	private Borning<?> borning;

	private String collectionName;

	private Map<String, MongoEntityField> fields;

	private List<MongoEntityIndex> indexes;

	private MongoEntityField _id;

	private long cappedSize;

	private long cappedMax;

	public StaticMongoEntity(Class<?> type) {
		this.type = type;
		this.mirror = Mirror.me(type);
		this.borning = this.mirror.getBorning();
		this.fields = new HashMap<String, MongoEntityField>();
		this.indexes = new ArrayList<MongoEntityIndex>(6); // 一个集合默认 6 个索引，够了吧
	}

	@Override
	public DBObject toDBObject(Object obj) {
		DBObject dbo = new BasicDBObject();
		for (MongoEntityField mef : fields.values())
			mef.setToDB(obj, dbo);
		return dbo;
	}

	@Override
	public Object toObject(DBObject dbo) {
		if (null == dbo)
			return null;
		Object obj = borning.born(new Object[0]);
		for (MongoEntityField mef : fields.values())
			mef.getFromDB(obj, dbo);
		return obj;
	}

	@Override
	public void fillId(Object obj) {
		if (null != _id) {
			_id.fillId(obj);
		}
	}

	@Override
	public void fillIdIfNoexits(Object obj) {
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
		// ID 名字的特殊值
		if ("_id".equals(key))
			return key;
		// 根据配置得到数据库中的名字
		MongoEntityField mef = fields.get(key);
		if (null == mef)
			throw Lang.makeThrow("Unknow '%s' in '%s'", key, collectionName);
		return mef.getDbName();
	}

	@Override
	public DBObject formatObject(Object o) {
		if (null == o)
			return new BasicDBObject();
		// 深入解析对象 ...
		return _format_object(o);
	}

	private DBObject _format_object(Object o) {
		// 如果是本类型
		if (type.isAssignableFrom(o.getClass())) {
			return toDBObject(o);
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

			// 判断一下字段的名称，支持 xxxx.$n
			String[] ss = Strings.splitIgnoreBlank(key, "[.]");
			String fieldName = ss[0];
			int arrayOffset = ss.length > 1 ? Integer.parseInt(ss[1]) : -1;

			// 得到实体字段
			MongoEntityField mef = "_id".equals(key) ? _id : fields.get(fieldName);
			/*
			 * 如果是修改器 ...
			 */
			if (null != val && key.startsWith("$")) {
				Mirror<?> mi = Mirror.me(val.getClass());
				// Map
				if (mi.isMap()) {
					dboMap.put(key, _format_object(val));
					continue;
				}
				// 集合
				else if (mi.isColl()) {
					int len = Lang.length(val);
					final Object array = Array.newInstance(Object.class, len);
					Lang.each(val, new Each<Object>() {
						public void invoke(int index, Object ele, int length) {
							// 原生类型，直接加入
							if (!Mirror.me(ele).isObj()) {
								Array.set(array, index, ele);
							}
							// 否则，试图转换
							else {
								Array.set(array, index, _format_object(ele));
							}
						}
					});
					dboMap.put(key, array);
					continue;
				}
			}
			// 未知的键，抛错
			if (null == mef)
				throw Lang.makeThrow("Unknow key '%s' in collection '%s'", key, collectionName);

			// 加入值
			String dbKey = mef.getDbName() + (arrayOffset >= 0 ? "." + arrayOffset : "");
			dboMap.put(dbKey, mef.getAdaptor().adaptForFormatQuery(val));
		}
		// 建立返回值
		DBObject dbo = new BasicDBObject();
		dbo.putAll(dboMap);
		return dbo;
	}

	@Override
	public boolean hasIndexes() {
		return indexes.size() > 0;
	}

	@Override
	public List<MongoEntityIndex> getIndexes() {
		return indexes;
	}

	public Class<?> getType() {
		return type;
	}

	public Mirror<?> getMirror() {
		return mirror;
	}

	void addField(FieldInfo fi) {
		MongoEntityField mef = new MongoEntityField(fi);
		if (null != fi.get_id())
			_id = mef;
		fields.put(fi.getName(), mef);
	}

	void addIndex(String str) {
		indexes.add(new MongoEntityIndex(str));
	}

	String getCollectionName() {
		return collectionName;
	}

	void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public void setCappedSize(long cappedSize) {
		this.cappedSize = cappedSize;
	}

	public long getCappedSize() {
		return this.cappedSize;
	}

	public long getCappedMax() {
		return cappedMax;
	}

	public void setCappedMax(long cappedMax) {
		this.cappedMax = cappedMax;
	}

}
