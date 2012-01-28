package org.nutz.mongo.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.nutz.lang.Mirror;
import org.nutz.lang.eject.EjectByGetter;
import org.nutz.lang.inject.InjectBySetter;
import org.nutz.lang.util.Callback3;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.mongo.entity.adaptor.EnumAdaptor;

/**
 * 提供给解析用的一个实体字段中间描述类，它的构造函数提供一点点分析功能，以便解析类更方便的解析实体
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
class FieldInfo {

	private final static String ERR_MSG = "Method '%s'(%s) can not add '@CoField', it MUST be a setter or getter!";

	private String name;

	private CoId _id;

	private CoField annotation;

	private FieldAdaptor adaptor;

	/**
	 * 根据字段得到 Field 信息
	 * 
	 * @param fld
	 *            字段
	 */
	FieldInfo(Field fld) {
		Mirror<?> mirror = Mirror.me(fld.getDeclaringClass());
		this.name = fld.getName();
		this._id = fld.getAnnotation(CoId.class);
		this.annotation = fld.getAnnotation(CoField.class);
		this.adaptor = _eval_adaptor(fld.getGenericType());
		this.adaptor.setEjecting(mirror.getEjecting(fld.getName()));
		this.adaptor.setInjecting(mirror.getInjecting(fld.getName()));
	}

	/**
	 * 根据 Getter 或者 setter 得到 Field 信息
	 * 
	 * @param method
	 *            方法可以是 getter 或者 setter
	 */
	FieldInfo(Method method) {
		this._id = method.getAnnotation(CoId.class);
		this.annotation = method.getAnnotation(CoField.class);
		Mirror.evalGetterSetter(method, ERR_MSG, new Callback3<String, Method, Method>() {
			public void invoke(String nm, Method getter, Method setter) {
				name = nm;
				adaptor = _eval_adaptor(getter.getGenericReturnType());
				adaptor.setEjecting(new EjectByGetter(getter));
				adaptor.setInjecting(new InjectBySetter(setter));
			}
		});
	}

	private static FieldAdaptor _eval_adaptor(Type type) {
		Mirror<?> mirror = Mirror.me(type);
		// 枚举
		if (mirror.isEnum()) {
			return new EnumAdaptor().setFieldType(type);
		}
		// 默认
		return new FieldAdaptor().setFieldType(type);
	}

	public FieldAdaptor getAdaptor() {
		return adaptor;
	}

	public String getName() {
		return name;
	}

	public CoId get_id() {
		return _id;
	}

	public CoField getAnnotation() {
		return annotation;
	}

}
