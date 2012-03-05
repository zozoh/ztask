package org.nutz.mongo.entity;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.types.ObjectId;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.annotation.CoIdType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * 将字段与 DBObject 中的字段进行适配
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FieldAdaptor {

	/**
	 * @param obj
	 *            adapt4eject (obj) 对象
	 * @return 对象中某一个字段的值
	 */
	public Object get(Object obj) {
		return adaptForGet(ejecting.eject(obj));
	}

	/**
	 * 修改 get 后的值，以便设置给 DBObject，会严格检查值类型是否匹配
	 * 
	 * @param val
	 *            get 后的值
	 * @return 修改 get 后的值，这个值是 ejecting 从对象中取出的
	 */
	public Object adaptForGet(Object val) {
		return adaptForGet(val, true);
	}

	/**
	 * 修改 get 后的值，以便修改查询的 DBObject
	 * 
	 * @param val
	 *            get 后的值
	 * @return 修改 get 后的值，这个值是 ejecting 从对象中取出的
	 */
	public Object adaptForFormatQuery(Object val) {
		if (null == val)
			return null;
		// 如果是 DBObject，则表示调用者手动准备好了 QueryObject
		if (val instanceof DBObject)
			return val;
		
		Mirror<?> vMirror = Mirror.me(val.getClass());
		// 如果值为 Map，且自己的类型不是 Map，那么就是深入修改 Map 的各个修改器的键 ...
		if (vMirror.isMap() && !field.getMirror().isMap()) {
			final Map<String, Object> map = new HashMap<String, Object>();
			Lang.each(val, true, new Each<Map.Entry<String, Object>>() {
				public void invoke(int index, Entry<String, Object> ele, int length) {
					String key = ele.getKey();
					if (null != key && key.startsWith("$")) {
						map.put(key, adaptForFormatQuery(ele.getValue()));
					}
				}
			});
			return map;
		}
		// 如果值为集合或者数组, 且自己不为集合或者数组，则生成个新数组返回
		else if (vMirror.isContainer() && !field.getMirror().isContainer()) {
			int len = Lang.length(val);
			final Object array = Array.newInstance(Object.class, len);
			Lang.each(val, false, new Each<Object>() {
				public void invoke(int index, Object o, int length) {
					Array.set(array, index, adaptForFormatQuery(o));
				}
			});
			return array;
		}
		return adaptForGet(val, false);
	}

	/**
	 * 子类复写：修改 get 后的值，以便设置给 DBObject
	 * <p>
	 * 如果不是严格检查的话， adaptor 如果发现自己不能接受这个类型，则可以直接返回输入的对象
	 * 
	 * @param val
	 *            get 后的值
	 * @param check
	 *            是否严格检查
	 * @return 修改 get 后的值，这个值是 ejecting 从对象中取出的
	 */
	public Object adaptForGet(Object val, boolean check) {
		if (null != val && CoIdType.DEFAULT == field.getIdType() && !(val instanceof ObjectId)) {
			String s = val.toString();
			if (!Mongos.isDefaultMongoId(s))
				throw Lang.makeThrow("Expect Mongo ID format, but is was '%s'", s);
			return new ObjectId(s);
		}
		return val;
	}

	/**
	 * 为对象某个字段设置一个值
	 * 
	 * @param adapt4eject
	 *            (obj) 对象
	 * @param adapt4eject
	 *            (val) 值
	 */
	public void set(Object obj, Object val) {
		injecting.inject(obj, adaptForSet(val));
	}

	/**
	 * 子类复写：修改 set 前的值， 以便设置给 POJO
	 * 
	 * @param val
	 *            set 前的值
	 * @return set 前的值
	 */
	public Object adaptForSet(Object val) {
		return val;
	}

	protected Ejecting ejecting;

	protected Injecting injecting;

	protected MongoEntityField field;

	public FieldAdaptor setField(MongoEntityField field) {
		this.field = field;
		return this;
	}

	public void setEjecting(Ejecting ejecting) {
		this.ejecting = ejecting;
	}

	public void setInjecting(Injecting injecting) {
		this.injecting = injecting;
	}

	public String toString() {
		return String.format("%s:%s", this.getClass().getName(), field);
	}

	// public DBRef packRef(Object val) {
	//
	// }

	public Object unpackRef(DBRef ref, Class<?> eleType) {
		MongoEntity en = Mongos.entity(eleType);
		if (field.isLazy()) {
			return en.toObject(new BasicDBObject("_id", ref.getId()));
		} else {
			return en.toObject(ref.fetch());
		}
	}
}
