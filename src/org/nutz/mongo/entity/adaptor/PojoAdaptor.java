package org.nutz.mongo.entity.adaptor;

import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.entity.FieldAdaptor;

import com.mongodb.DBObject;

public class PojoAdaptor extends FieldAdaptor {

	@Override
	public Object adaptForGet(Object val, boolean check) {
		return Mongos.obj2dbo(val);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object adaptForSet(Object val) {
		Map<String, Object> map = (Map<String, Object>) ((DBObject) val).toMap();
		return Lang.map2Object(map, field.getType());
	}

}
