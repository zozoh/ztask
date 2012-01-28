package org.nutz.mongo.entity;

import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.mongo.Mongos;

import com.mongodb.DBObject;

public class StringMongoEntity extends DynamicMongoEntity<CharSequence> {

	@Override
	public DBObject toDBObject(CharSequence cs) {
		Map<String, Object> map = Json.fromJsonAsMap(Object.class, cs);
		return super.fromMap(map);
	}

	@Override
	public CharSequence toObject(DBObject dbo) {
		Map<String, Object> map = super.toMap(dbo);
		if (null == map)
			return null;
		return Json.toJson(map, JsonFormat.compact().setQuoteName(true));
	}

	@Override
	public void fillId(CharSequence obj) {
		throw Lang.impossible();
	}

	@Override
	public void fillIdIfNoexits(CharSequence obj) {
		throw Lang.impossible();
	}

	@Override
	public String getCollectionName(Object ref) {
		Map<String, Object> map = Mongos.obj2map(ref);
		return super.evalCollectionName(map);
	}

	@Override
	public DBObject formatObject(Object o) {
		if (null == o)
			return null;
		// 如果是 DBObject 直接返回
		if (o instanceof DBObject)
			return (DBObject) o;
		return super.map2dbo(Json.fromJsonAsMap(Object.class, o.toString()));
	}

}
