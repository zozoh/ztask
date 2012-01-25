package org.nutz.mongo.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;

/**
 * 根据一个 POJO 的注解，得到这个 POJO 与 MongoDB 的文档对象的映射关系
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MongoEntityMaker {

	private Map<Class<?>, MongoEntity<?>> ens;

	public MongoEntityMaker() {
		this.ens = new HashMap<Class<?>, MongoEntity<?>>();
	}

	/**
	 * 根据传入的对象，构建 MongoEntity 接口的实现，可以接受
	 * <ul>
	 * <li>字符串 - 作为 JSON 字符串，所以当作 Dynamic
	 * <li>Map - 作为 Map，所以当作 Dynamic
	 * <li>POJO - 作为 POJO，所以当作 Static
	 * </ul>
	 * 如果传入数组，或者集合，将会根据第一个元素来生成 MongoEntity
	 * 
	 * @param obj
	 *            参考对象
	 * @return MongoEntity 的操作类
	 */
	@SuppressWarnings("unchecked")
	public MongoEntity<?> get(Object obj) {
		// 绝对不能是 null
		if (null == obj) {
			throw Lang.makeThrow("Null refer for MongoEntity!");
		}
		// 下面看看是不是集合
		if (obj.getClass().isArray() || obj instanceof Collection<?>) {
			Object sub = Lang.first(obj);
			if (null == sub)
				throw Lang.makeThrow("Empty refer for MongoEntity!");
			return get(sub);
		}
		// Map...
		if (obj instanceof Map<?, ?>) {
			return new MapMongoEntity();
		}
		// String ...
		else if (obj instanceof CharSequence) {
			return new StringMongoEntity();
		}
		// 检查缓存
		MongoEntity<?> en = ens.get(obj.getClass());
		if (null != en)
			return en;
		// 开始构建 Static MongoEntity
		return _makeStaticMontoEntity((Class<Object>) obj.getClass());
	}

	private MongoEntity<?> _makeStaticMontoEntity(Class<Object> type) {
		// 准备返回对象
		StaticMongoEntity<Object> en = new StaticMongoEntity<Object>(type);
		// 获得集合名称
		Co co = type.getAnnotation(Co.class);
		en.setCollectionName(Strings.sBlank(co.value(), type.getName().toLowerCase()));
		// 循环所有的字段
		for (Field fld : en.getMirror().getFields()) {
			CoField cf = fld.getAnnotation(CoField.class);
			CoId ci = fld.getAnnotation(CoId.class);
			if (null != cf || null != ci) {
				en.addField(new FieldInfo(fld));
			}
		}
		// 循环所有的方法
		for (Method method : en.getType().getMethods()) {
			CoField cf = method.getAnnotation(CoField.class);
			CoId ci = method.getAnnotation(CoId.class);
			if (null != cf || null != ci) {
				en.addField(new FieldInfo(method));
			}
		}
		// 存入缓存
		ens.put(type, en);
		return en;
	}

}
