package org.nutz.mongo.entity;

import java.lang.reflect.Type;

import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;

/**
 * 将字段与 DBObject 中的字段进行适配
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FieldAdaptor {

	/**
	 * @param adapt4eject
	 *            (obj) 对象
	 * @return 对象中某一个字段的值
	 */
	public Object get(Object obj) {
		return adaptForGet(ejecting.eject(obj));
	}

	/**
	 * 子类复写：修改 get 后的值，以便设置给 DBObject
	 * 
	 * @param val
	 *            get 后的值
	 * @return 修改 get 后的值，这个值是 ejecting 从对象中取出的
	 */
	public Object adaptForGet(Object val) {
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

	protected Type fieldType;

	public FieldAdaptor setFieldType(Type fieldType) {
		this.fieldType = fieldType;
		return this;
	}

	public void setEjecting(Ejecting ejecting) {
		this.ejecting = ejecting;
	}

	public void setInjecting(Injecting injecting) {
		this.injecting = injecting;
	}
}
