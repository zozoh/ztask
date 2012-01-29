package org.nutz.mongo.util;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;

/**
 * 数据查询条件或者修改的值链
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class Moo extends MoChain {

	/**
	 * 移除一组字段
	 * 
	 * @param fields
	 *            字段名
	 * @return 新节点
	 */
	public Moo unset(String... fields) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String field : fields)
			map.put(field, 1);
		return modi("$unset", map);
	}

	/**
	 * 修改器: 设值
	 * 
	 * @param fmt
	 *            格式化字符串，JSON 格式
	 * @param args
	 *            字符串参数
	 * @return 新节点
	 */
	public Moo setf(String fmt, Object... args) {
		return set(Lang.mapf(fmt, args));
	}

	/**
	 * 修改器: 设值
	 * 
	 * @param field
	 *            需要修改的字段
	 * @param val
	 *            新值
	 * @return 新节点
	 */
	public Moo set(String field, Object val) {
		return modi("$set", field, val);
	}

	/**
	 * 修改器: 设值
	 * 
	 * @param map
	 *            需要设置的字段名值对
	 * @return 新节点
	 */
	public Moo set(Map<String, Object> map) {
		return modi("$set", map);
	}

	/**
	 * 修改器: 自增
	 * 
	 * @param field
	 *            需要自增的字段
	 * @param val
	 *            自增的步长
	 * @return 新节点
	 */
	public Moo inc(String field, int val) {
		return modi("$inc", field, val);
	}

	/**
	 * 针对一个字段的修改器
	 * 
	 * @param name
	 *            修改器名称
	 * @param field
	 *            字段
	 * @param val
	 *            参考值
	 * @return 新节点
	 */
	public Moo modi(String name, String field, Object val) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(field, val);
		return modi(name, map);
	}

	/**
	 * 针对多个字段的修改器
	 * 
	 * @param name
	 *            修改器名称
	 * @param field
	 *            字段
	 * @param val
	 *            参考值
	 * @return 新节点
	 */
	public Moo modi(String name, Map<String, Object> map) {
		return new Moo(this, name, map);
	}

	/**
	 * 在当前节点后面新增节点
	 * 
	 * @param key
	 *            键
	 * @param val
	 *            值
	 * @return 新增的链表对象
	 */
	@Override
	public Moo append(String key, Object val) {
		return new Moo(this, key, val);
	}

	private Moo() {
		super();
	}

	private Moo(Moo prev, String key, Object value) {
		super(prev, key, value);
	}

	/**
	 * 创建链表的静态方法
	 * 
	 * @return 链表对象
	 */
	public static Moo born() {
		return new Moo();
	}
	
	public static Moo born(String key, Object val) {
		Moo moo = born();
		moo.append(key, val);
		return moo;
	}

}
