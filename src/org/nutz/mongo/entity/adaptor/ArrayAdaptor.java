package org.nutz.mongo.entity.adaptor;

import java.lang.reflect.Array;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.entity.FieldAdaptor;
import org.nutz.mongo.entity.MongoEntity;
import org.nutz.mongo.entity.StaticMongoEntity;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

public class ArrayAdaptor extends FieldAdaptor {

	private static final Log log = Logs.get();

	@Override
	public Object adaptForGet(Object val, boolean check) {
		// 容忍 null
		if (null == val)
			return null;
		// val 必然是个 array
		if (!val.getClass().isArray()) {
			// 那么看看给定的 val 是不是自己数组的元素，如果是，则试图转换它
			Class<?> eleType = this.field.getType().getComponentType();
			if (!Mirror.me(eleType).isPojo()) {
				return val;
			}
			if (eleType.isAssignableFrom(val.getClass())) {
				MongoEntity en = Mongos.entity(eleType);
				if (en instanceof StaticMongoEntity)
					return en.toDBObject(val);
				return Mongos.obj2dbo(val);
			}
			// 否则 ...
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
			if (Mirror.me(ele.getClass()).isObj()) {
				if (field.isRef()) {
					MongoEntity en = Mongos.entity(val.getClass().getComponentType());
					DBObject dbo = en.toDBObject(ele);
					if (dbo.containsField("_id")) {
						array[i] = new DBRef(null, en.getCollectionName(null), dbo.get("_id"));
						continue;
					} else {
						if (log.isWarnEnabled())
							log.warn("!!obj without _id but using as ref field value!! fallback to embed doc!!");
					}
				} else {
					MongoEntity en = Mongos.entity(val.getClass().getComponentType());
					if (en instanceof StaticMongoEntity)
						array[i] = en.toDBObject(ele);
					else
						array[i] = Mongos.obj2dbo(ele);
				}
			}
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
		Object array = Array.newInstance(field.getType().getComponentType(), len);

		// 将每个项目都转换成当前数组的类型
		Class<?> eleType = field.getType().getComponentType();
		for (int i = 0; i < len; i++) {
			Object v = list.get(i);
			Object o = null;
			if (null != v) {
				// 如果是 DBObject
				if (v instanceof DBObject) {
					MongoEntity en = Mongos.entity(eleType);
					if (en instanceof StaticMongoEntity) {
						o = en.toObject((DBObject) v);
					} else
						o = Lang.map2Object(((DBObject) v).toMap(), eleType);
				}
				// 否则就强制转换一下
				else {
					// TODO
					if (v instanceof DBRef && field.isRef()) {
						o = unpackRef((DBRef) v, eleType);
					} else
						o = Castors.me().castTo(v, eleType);
				}
			}
			Array.set(array, i, o);
		}

		// 返回新数组，以便设置给 POJO
		return array;
	}
}
