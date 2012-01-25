package org.nutz.mongo.entity;

import java.util.Map;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;

import com.mongodb.DBObject;

public class StringMongoEntity extends DynamicMongoEntity<CharSequence> {

	@Override
	public DBObject from(CharSequence cs) {
		Map<String, Object> map = Json.fromJsonAsMap(Object.class, cs);
		return super.fromMap(map);
	}

	@Override
	public CharSequence to(DBObject dbo) {
		Map<String, Object> map = super.toMap(dbo);
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

}
