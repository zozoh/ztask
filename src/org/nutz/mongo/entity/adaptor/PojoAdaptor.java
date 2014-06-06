package org.nutz.mongo.entity.adaptor;

import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.entity.FieldAdaptor;
import org.nutz.mongo.entity.MongoEntity;
import org.nutz.mongo.entity.StaticMongoEntity;

import com.mongodb.DBObject;
import com.mongodb.DBRef;

public class PojoAdaptor extends FieldAdaptor {

	private static final Log log = Logs.get();
	
	@Override
	public Object adaptForGet(Object val, boolean check) {
		if (val == null)
			return null;
		MongoEntity en = Mongos.entity(val);
		DBObject dbo = en.toDBObject(val);
		if (!field.isRef())
			return dbo;

		if (!dbo.containsField("_id")) {
			if (log.isWarnEnabled())
				log.warn("!!obj without _id but using as ref field value!! fallback to embed doc!!");
			return dbo;
		}
		return new DBRef(null, en.getCollectionName(null), dbo.get("_id"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object adaptForSet(Object val) {
		if (val instanceof DBRef && field.isRef()) {
			return unpackRef((DBRef)val, field.getType());
		}
		MongoEntity en = Mongos.entity(field.getMirror().getType());
		if (en instanceof StaticMongoEntity)
			return en.toObject((DBObject) val);
		Map<String, Object> map = (Map<String, Object>) ((DBObject) val).toMap();
		return Lang.map2Object(map, field.getType());
	}

}
