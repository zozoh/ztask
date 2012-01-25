package org.nutz.mongo.entity;

import java.util.Map;

import com.mongodb.DBObject;

public class MapMongoEntity extends DynamicMongoEntity<Map<String, Object>> {

	@Override
	public DBObject from(Map<String, Object> map) {
		return super.fromMap(map);
	}

	@Override
	public Map<String, Object> to(DBObject dbo) {
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

}
