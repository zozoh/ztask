package org.nutz.mongo.util;

import java.util.Map;

import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.util.NutMap;

/**
 * 便于链式赋值的参考对象组装器
 * <p>
 * 链表的头节点不存储数据
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class MoChain {

	public abstract MoChain append(String key, Object val);

	/**
	 * 遍历整个链表
	 * 
	 * @param callback
	 *            回调
	 */
	public void each(Each<MoChain> callback) {
		MoChain c = head == null ? null : head.next();
		while (null != c) {
			try {
				callback.invoke(c.index(), c, c.head.n);
			}
			catch (ExitLoop e) {}
			catch (ContinueLoop e) {}
			catch (LoopException e) {
				throw Lang.wrapThrow(e);
			}
			c = c.next();
		}
	}

	/**
	 * 将整个链表变成一个 Map 以便操作，这个操作，会自动合并一些值，比如你在链表中有两个 "$set"，则它会试图这两个节点的 Map
	 * <p>
	 * 即
	 * 
	 * <pre>
	 * MoChain.born().set(&quot;abc&quot;, 23).set(&quot;xyz&quot;, &quot;ttt&quot;);
	 * </pre>
	 * 
	 * 与
	 * 
	 * <pre>
	 * MoChain.born().setf(&quot;{abc:%d, xyz:'%s'}&quot;, 23, &quot;ttt&quot;);
	 * </pre>
	 * 
	 * 是一样的效果
	 * 
	 * @return Map 对象
	 */
	public NutMap toMap() {
		final NutMap map = new NutMap();
		each(new Each<MoChain>() {
			@SuppressWarnings("unchecked")
			public void invoke(int index, MoChain c, int length) {
				Object val = c.value;
				Object mapval = map.get(c.key);
				// 如果 map 里有，并且值为 Map，则合并
				if (val instanceof Map<?, ?> && mapval instanceof Map<?, ?>) {
					((Map<String, Object>) mapval).putAll((Map<? extends String, ? extends Object>) val);
				}
				// 否则替换值
				else {
					map.put(c.key, c.value);
				}
			}
		});
		return map;
	}

	/**
	 * 链表头节点
	 */
	private MoChain head;

	/**
	 * 链表下一个节点
	 */
	private MoChain next;

	/**
	 * 当前节点键
	 */
	private String key;

	/**
	 * 当前节点值
	 */
	private Object value;

	/**
	 * 计数，记录整个链表的长度，只有 head 节点的这个值是有效的，其他的节点，这个值表示自己的 index
	 */
	private int n;

	public MoChain head() {
		return head;
	}

	public MoChain next() {
		return next;
	}

	public boolean hasNext() {
		return null != next;
	}

	public String key() {
		return key;
	}

	public Object value() {
		return value;
	}

	public int index() {
		return this == head ? -1 : n;
	}

	public int size() {
		return head.n;
	}

	protected MoChain(MoChain prev, String key, Object value) {
		// 寻找链表的尾部
		while(null!=prev.next)
			prev = prev.next;
		// 开始设置
		this.head = prev.head;
		prev.next = this;
		this.n = prev.head.n; // 记录自己的 index
		prev.head.n++; // 链表头自增表示整个链表的长度
		this.key = key;
		this.value = value;
	}

	/**
	 * 头节点
	 */
	protected MoChain() {
		this.n = 0;
		this.head = this;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final MoChain me = this;
		each(new Each<MoChain>() {
			public void invoke(int index, MoChain c, int length) {
				sb.append("[");
				if (c == me) {
					sb.append('*');
				}
				sb.append(c.key).append(": ").append(c.value);
				sb.append("] ");
			}
		});
		return sb.toString();
	}
}
