package org.nutz.mongo.util;

import org.nutz.lang.Strings;

/**
 * 字段过滤器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MKeys extends MoChain {

	/**
	 * 根据滤掉的字段的 key 生成一个新链表
	 * 
	 * @param keys
	 *            字段名列表
	 * @return 新节点
	 */
	public static MKeys OFF(String... keys) {
		return NEW().off(keys);
	}

	/**
	 * 根据要显示的字段的 key 生成一个新链表
	 * 
	 * @param keys
	 *            字段名列表
	 * @return 新节点
	 */
	public static MKeys ON(String... keys) {
		return NEW().on(keys);
	}

	/**
	 * 生成一个新的空链表
	 * 
	 * @return 新节点
	 */
	public static MKeys NEW() {
		return new MKeys();
	}

	private MKeys() {
		super();
	}

	private MKeys(MKeys prev, String key, Object value) {
		super(prev, key, value);
	}

	@Override
	public MKeys append(String key, Object val) {
		return new MKeys(this, key, Boolean.valueOf(Strings.sBlank(val, "false")));
	}

	/**
	 * 滤掉的字段的 key
	 * 
	 * @param keys
	 *            字段名列表
	 * @return 新节点
	 */
	public MKeys off(String... keys) {
		MKeys re = this;
		for (String key : keys)
			re = new MKeys(this, key, false);
		return re;
	}

	/**
	 * 显示的字段的 key
	 * 
	 * @param keys
	 *            字段名列表
	 * @return 新节点
	 */
	public MKeys on(String... keys) {
		MKeys re = this;
		for (String key : keys)
			re = new MKeys(this, key, true);
		return re;
	}

}
