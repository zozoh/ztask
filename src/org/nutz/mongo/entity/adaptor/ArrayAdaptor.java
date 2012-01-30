package org.nutz.mongo.entity.adaptor;

import java.lang.reflect.Array;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.entity.FieldAdaptor;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class ArrayAdaptor extends FieldAdaptor {

	@Override
	public Object adaptForGet(Object val, boolean check) {
		// 容忍 null
		if (null == val)
			return null;
		// val 必然是个 array
		if (!val.getClass().isArray()) {
			if (!check)
				return val;
			throw Lang.makeThrow(	"ArrayAdaptor can only adaptForGet array, but is is '%s'",
									val.getClass().getName());
		}
		// 首先，建立一个新数组
		Object[] array = new Object[Array.getLength(val)];

		// 将每个项目都转换成 DBObject
		for (int i = 0; i < array.length; i++) {
			Object ele = Array.get(val, i);
			// 如果是POJO或者容器
			if (Mirror.me(ele.getClass()).isObj())
				array[i] = Mongos.obj2dbo(ele);
			// 否则直接设置
			else {
				array[i] = ele;
			}
		}

		// 返回新的数组，以便 MongoDB 的驱动使用
		return array;
	}

	@Override
	public Object adaptForSet(Object val) {
		// 容忍 null
		if (null == val)
			return null;
		// val 一定是个DBList
		if (!(val instanceof BasicDBList)) {
			throw Lang.makeThrow(	"ArrayAdaptor can only adaptForSet BasicDBList, but is is '%s'",
									val.getClass().getName());
		}

		BasicDBList list = (BasicDBList) val;
		// 首先建立一个数组
		int len = list.size();
		Object array = Array.newInstance(fieldClass.getComponentType(), len);

		// 将每个项目都转换成当前数组的类型
		Class<?> eleType = fieldClass.getComponentType();
		for (int i = 0; i < len; i++) {
			Object v = list.get(i);
			Object o = null;
			if (null != v) {
				// 如果是 DBObject
				if (v instanceof DBObject) {
					o = Lang.map2Object(((DBObject) v).toMap(), eleType);
				}
				// 否则就强制转换一下
				else {
					o = Castors.me().castTo(v, eleType);
				}
			}
			Array.set(array, i, o);
		}

		// 返回新数组，以便设置给 POJO
		return array;
	}

}
