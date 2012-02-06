package org.nutz.mongo.entity.adaptor;

import org.nutz.mongo.entity.FieldAdaptor;
import org.nutz.mongo.entity.MongoEntityField;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumAdaptor extends FieldAdaptor {

	private Class<Enum> enumType;

	@Override
	public FieldAdaptor setField(MongoEntityField field) {
		enumType = (Class<Enum>) field.getType();
		return super.setField(field);
	}

	@Override
	public Object adaptForGet(Object val, boolean check) {
		if (null == val)
			return null;
		return val.toString();
	}

	@Override
	public Object adaptForSet(Object val) {
		if (null == val)
			return null;
		return Enum.valueOf(enumType, val.toString());
	}

}
