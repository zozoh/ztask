package org.nutz.mongo.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.nutz.lang.Lang;
import org.nutz.mongo.Mongos;

/**
 * 数据查询条件或者修改的值链
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class Moo extends MoChain {

	/**
	 * 提供 $not 修改符
	 * 
	 * @param q
	 *            值链
	 * @return 新节点
	 */
	public Moo not(Moo q) {
		return append("$not", q);
	}

	/**
	 * 提供 $or 修改符
	 * 
	 * @param qs
	 *            值链
	 * @return 新节点
	 */
	public Moo or(Moo... qs) {
		return append("$or", qs);
	}

	/**
	 * 判断一个日期字段是否不等于给定值
	 * 
	 * @param field
	 *            字段
	 * @param d
	 *            日期
	 * @return 新节点
	 */
	public Moo d_ne(String field, Date d) {
		return append(field, Mongos.dbo("$ne", d));
	}

	/**
	 * 判断一个日期字段是否大于等于给定值
	 * 
	 * @param field
	 *            字段
	 * @param d
	 *            日期
	 * @return 新节点
	 */
	public Moo d_gte(String field, Date d) {
		return append(field, Mongos.dbo("$gte", d));
	}

	/**
	 * 判断一个日期字段是否大于给定值
	 * 
	 * @param field
	 *            字段
	 * @param d
	 *            日期
	 * @return 新节点
	 */
	public Moo d_gt(String field, Date d) {
		return append(field, Mongos.dbo("$gt", d));
	}

	/**
	 * 判断一个日期字段是否小于等于给定值
	 * 
	 * @param field
	 *            字段
	 * @param d
	 *            日期
	 * @return 新节点
	 */
	public Moo d_lte(String field, Date d) {
		return append(field, Mongos.dbo("$lte", d));
	}

	/**
	 * 判断一个日期字段是否小于给定值
	 * 
	 * @param field
	 *            字段
	 * @param d
	 *            日期
	 * @return 新节点
	 */
	public Moo d_lt(String field, Date d) {
		return append(field, Mongos.dbo("$lt", d));
	}

	/**
	 * 判断一个日期字段是否等于给定值
	 * 
	 * @param field
	 *            字段
	 * @param d
	 *            日期
	 * @return 新节点
	 */
	public Moo d_equals(String field, Date d) {
		return append(field, d);
	}

	/**
	 * 判断一个字段是否不等于给定值
	 * 
	 * @param field
	 *            字段
	 * @param obj
	 *            值
	 * @return 新节点
	 */
	public Moo ne(String field, Object obj) {
		return append(field, Mongos.dbo("$ne", obj));
	}

	/**
	 * 判断一个字段是否等于给定值，是 "append" 的一个别名
	 * 
	 * @param field
	 *            字段
	 * @param obj
	 *            值
	 * @return 新节点
	 */
	public Moo eq(String field, Object obj) {
		return append(field, obj);
	}

	/**
	 * 判断一个字段是否大于等于给定值
	 * 
	 * @param field
	 *            字段
	 * @param n
	 *            数值
	 * @return 新节点
	 */
	public Moo gte(String field, Number n) {
		return append(field, Mongos.dbo("$gte", n));
	}

	/**
	 * 判断一个字段是否大于给定值
	 * 
	 * @param field
	 *            字段
	 * @param n
	 *            数值
	 * @return 新节点
	 */
	public Moo gt(String field, Number n) {
		return append(field, Mongos.dbo("$gt", n));
	}

	/**
	 * 判断ID字段是否大于等于给定值
	 * 
	 * @param id
	 *            对象ID
	 * @return 新节点
	 */
	public Moo gte(ObjectId id) {
		return append("_id", Mongos.dbo("$gte", id));
	}

	/**
	 * 判断ID字段是否大于给定值
	 * 
	 * @param id
	 *            对象ID
	 * @return 新节点
	 */
	public Moo gt(ObjectId id) {
		return append("_id", Mongos.dbo("$gt", id));
	}

	/**
	 * 判断一个字段是否小于等于给定值
	 * 
	 * @param field
	 *            字段
	 * @param n
	 *            数值
	 * @return 新节点
	 */
	public Moo lte(String field, Number n) {
		return append(field, Mongos.dbo("$lte", n));
	}

	/**
	 * 判断一个字段是否小于给定值
	 * 
	 * @param field
	 *            字段
	 * @param n
	 *            数值
	 * @return 新节点
	 */
	public Moo lt(String field, Number n) {
		return append(field, Mongos.dbo("$lt", n));
	}

	/**
	 * 判断一个字段是否小于等于给定值
	 * 
	 * @param id
	 *            对象ID
	 * @return 新节点
	 */
	public Moo lte(ObjectId id) {
		return append("_id", Mongos.dbo("$lte", id));
	}

	/**
	 * 判断一个ID字段是否小于给定值
	 * 
	 * @param id
	 *            对象ID
	 * @return 新节点
	 */
	public Moo lt(ObjectId id) {
		return append("_id", Mongos.dbo("$lt", id));
	}

	/**
	 * 判断一个字段的值是否包含在给定数组中
	 * 
	 * @param field
	 *            字段
	 * @param args
	 *            变参数组
	 * @return 新节点
	 */
	public Moo in(String field, Object... args) {
		return inArray(field, args);
	}

	/**
	 * 判断一个字段的值是否包含在给定数组中
	 * 
	 * @param field
	 *            字段
	 * @param array
	 *            数组
	 * @return 新节点
	 */
	public Moo inArray(String field, Object array) {
		return append(field, Mongos.map("$in", array));
	}

	/**
	 * 判断字段型数组是否与给定的数组匹配
	 * 
	 * @param field
	 *            数组字段
	 * @param args
	 *            变参数组
	 * @return 新节点
	 */
	public Moo array(String field, Object... args) {
		return all(field, args);
	}

	/**
	 * 判断字段型数组是否与给定的数组匹配
	 * 
	 * @param field
	 *            数组字段
	 * @param array
	 *            参考数组
	 * @return 新节点
	 */
	public Moo all(String field, Object array) {
		return append(field, Mongos.map("$all", array));
	}

	/**
	 * 用正则表达式，查找可以匹配的字段
	 * 
	 * @param field
	 *            字段
	 * @param regex
	 *            正则式
	 * @return 新节点
	 */
	public Moo match(String field, String regex) {
		return match(field, Pattern.compile(regex));
	}

	/**
	 * 用正则表达式，查找可以匹配的字段
	 * 
	 * @param field
	 *            字段
	 * @param regex
	 *            正则式
	 * @return 新节点
	 */
	public Moo match(String field, Pattern regex) {
		return append(field, regex);
	}

	/**
	 * 查找包含的文字
	 * 
	 * @param field
	 *            字段
	 * @param str
	 *            字符串
	 * @return 新节点
	 */
	public Moo contains(String field, String str) {
		return append(field, Pattern.compile("^.*" + str + ".*$"));
	}

	/**
	 * 查找开头的文字，它会比 contains 要快点
	 * 
	 * @param field
	 *            字段
	 * @param str
	 *            字符串
	 * @return 新节点
	 */
	public Moo startsWith(String field, String str) {
		return append(field, Pattern.compile("^" + str));
	}

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
	 * 修改器: 向数组增加值
	 * 
	 * @param field
	 *            字段
	 * @param objs
	 *            值
	 * @return 新节点
	 */
	public Moo push(String field, Object... objs) {
		if (null == objs)
			return this;
		if (1 == objs.length)
			return modi("$push", field, objs[0]);
		return modi("$pushAll", field, objs);
	}

	/**
	 * 修改器: 删除数组最后一个值
	 * 
	 * @param field
	 *            字段
	 * @return 新节点
	 */
	public Moo pop(String field) {
		return modi("$pop", field, 1);
	}

	/**
	 * 修改器: 删除数组第一个值
	 * 
	 * @param field
	 *            字段
	 * @return 新节点
	 */
	public Moo popHead(String field) {
		return modi("$pop", field, -11);
	}

	/**
	 * 修改器: 删除数组中某一个值
	 * 
	 * @param field
	 *            字段
	 * @param obj
	 *            值。 数组中与这个值相同的值将都被删除
	 * @return 新节点
	 */
	public Moo pull(String field, Object obj) {
		return modi("$pull", field, obj);
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
	 * 创建链表，同时增加一个 $or 节点
	 * 
	 * @param qs
	 *            值链列表
	 * @return 链表对象
	 */
	public static Moo OR(Moo... qs) {
		return NEW().or(qs);
	}

	/**
	 * 创建链表，同时增加一个 $not 节点
	 * 
	 * @param q
	 *            值链
	 * @return 链表对象
	 */
	public static Moo NOT(Moo q) {
		return NEW().not(q);
	}

	/**
	 * 创建链表，同时增加一个 in 节点
	 * 
	 * @param field
	 *            字段名
	 * @param args
	 *            变参数组
	 * @return 链表对象
	 */
	public static Moo IN(String field, Object... args) {
		return NEW().in(field, args);
	}

	/**
	 * 创建链表，同时增加一个 d_gt 节点
	 * 
	 * @param field
	 *            字段名
	 * @param d
	 *            日期
	 * @return 链表对象
	 */
	public static Moo D_GT(String field, Date d) {
		return NEW().d_gt(field, d);
	}

	/**
	 * 创建链表，同时增加一个 d_gte 节点
	 * 
	 * @param field
	 *            字段名
	 * @param d
	 *            日期
	 * @return 链表对象
	 */
	public static Moo D_GTE(String field, Date d) {
		return NEW().d_gte(field, d);
	}

	/**
	 * 创建链表，同时增加一个 d_lt 节点
	 * 
	 * @param field
	 *            字段名
	 * @param d
	 *            日期
	 * @return 链表对象
	 */
	public static Moo D_LT(String field, Date d) {
		return NEW().d_lt(field, d);
	}

	/**
	 * 创建链表，同时增加一个 d_lte 节点
	 * 
	 * @param field
	 *            字段名
	 * @param d
	 *            日期
	 * @return 链表对象
	 */
	public static Moo D_LTE(String field, Date d) {
		return NEW().d_lte(field, d);
	}

	/**
	 * 创建链表，同时增加一个 d_equals 节点
	 * 
	 * @param field
	 *            字段名
	 * @param d
	 *            日期
	 * @return 链表对象
	 */
	public static Moo D_EQUALS(String field, Date d) {
		return NEW().d_equals(field, d);
	}

	/**
	 * 创建链表，同时增加一个 gte 节点
	 * 
	 * @param field
	 *            字段名
	 * @param n
	 *            值
	 * @return 链表对象
	 */
	public static Moo GTE(String field, Number n) {
		return NEW().gte(field, n);
	}

	/**
	 * 创建链表，同时增加一个 gt 节点
	 * 
	 * @param field
	 *            字段名
	 * @param n
	 *            值
	 * @return 链表对象
	 */
	public static Moo GT(String field, Number n) {
		return NEW().gt(field, n);
	}

	/**
	 * 创建链表，同时增加一个 lte 节点
	 * 
	 * @param field
	 *            字段名
	 * @param n
	 *            值
	 * @return 链表对象
	 */
	public static Moo LTE(String field, Number n) {
		return NEW().lte(field, n);
	}

	/**
	 * 创建链表，同时增加一个 lt 节点
	 * 
	 * @param field
	 *            字段名
	 * @param n
	 *            值
	 * @return 链表对象
	 */
	public static Moo LT(String field, Number n) {
		return NEW().lt(field, n);
	}

	/**
	 * 创建链表，同时增加一个 gte 节点
	 * 
	 * @param id
	 *            对象ID
	 * @return 链表对象
	 */
	public static Moo GTE(ObjectId id) {
		return NEW().gte(id);
	}

	/**
	 * 创建链表，同时增加一个 gt 节点
	 * 
	 * @param id
	 *            对象ID
	 * @return 链表对象
	 */
	public static Moo GT(ObjectId id) {
		return NEW().gt(id);
	}

	/**
	 * 创建链表，同时增加一个 lte 节点
	 * 
	 * @param id
	 *            对象ID
	 * @return 链表对象
	 */
	public static Moo LTE(ObjectId id) {
		return NEW().lte(id);
	}

	/**
	 * 创建链表，同时增加一个 lt 节点
	 * 
	 * @param id
	 *            对象ID
	 * @return 链表对象
	 */
	public static Moo LT(ObjectId id) {
		return NEW().lt(id);
	}

	/**
	 * 创建链表，同时增加一个 set 节点
	 * 
	 * @param field
	 *            字段名
	 * @param val
	 *            值
	 * @return 链表对象
	 */
	public static Moo SET(String field, Object val) {
		return NEW().set(field, val);
	}

	/**
	 * 创建链表，同时增加一个 inc 节点
	 * 
	 * @param field
	 *            字段名
	 * @param n
	 *            自增值
	 * @return 链表对象
	 */
	public static Moo INC(String field, int n) {
		return NEW().inc(field, n);
	}

	/**
	 * 创建链表的静态方法
	 * 
	 * @return 链表对象
	 */
	public static Moo NEW() {
		return new Moo();
	}

	/**
	 * 创建链表，同时增加一个节点
	 * 
	 * @param key
	 *            键
	 * @param val
	 *            值
	 * @return 链表对象
	 */
	public static Moo NEW(String key, Object val) {
		return NEW().append(key, val);
	}

}
