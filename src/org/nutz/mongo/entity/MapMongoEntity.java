package org.nutz.mongo.entity;

import java.util.Map;

import org.nutz.mongo.Mongos;

import com.mongodb.DBObject;

public class MapMongoEntity extends DynamicMongoEntity<Map<String, Object>> {

	@Override
	public DBObject toDBObject(Map<String, Object> map) {
		return super.fromMap(map);
	}

	@Override
	public Map<String, Object> toObject(DBObject dbo) {
		return super.toMap(dbo);
	}

	@Override
	public void fillId(Map<String, Object> map) {
		super.fillIdToMap(map);
	}

	@Override
	public void fillIdIfNoexits(Map<String, Object> map) {
		super.fillIdToMapIfNoexits(map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCollectionName(Object ref) {
		return super.evalCollectionName((Map<String, Object>) ref);
	}

	@Override
	public DBObject formatObject(Object o) {
		if (null == o)
			return null;
		return super.map2dbo(Mongos.obj2map(o));
	}

}
