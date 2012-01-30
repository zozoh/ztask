package org.nutz.mongo.entity.adaptor;

import java.lang.reflect.Type;

import org.nutz.lang.Lang;
import org.nutz.mongo.entity.FieldAdaptor;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumAdaptor extends FieldAdaptor {

	private Class<Enum> enumType;

	@Override
	public FieldAdaptor setFieldType(Type fieldType) {
		enumType = (Class<Enum>) Lang.getTypeClass(fieldType);
		return super.setFieldType(fieldType);
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
