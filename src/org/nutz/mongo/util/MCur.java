package org.nutz.mongo.util;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.entity.MongoEntity;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 修改 DBCursor 的值链
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MCur extends MoChain {

	public MCur asc(String field) {
		return append(Mongos.SK_ASC, field);
	}

	public MCur desc(String field) {
		return append(Mongos.SK_DESC, field);
	}

	public MCur limit(int num) {
		return append(Mongos.SK_LIMIT, num);
	}

	public MCur skip(int num) {
		return append(Mongos.SK_SKIP, num);
	}

	public void setupCursor(final DBCursor cur, final MongoEntity moe) {
		final DBObject sort = Mongos.dbo();
		each(new Each<MoChain>() {
			public void invoke(int index, MoChain ele, int length) {
				String key = ele.key();
				// ASC
				if (Mongos.SK_ASC.equals(key)) {
					String dbnm = moe.getFieldDbName(ele.value().toString());
					sort.put(dbnm, 1);
				}
				// DESC
				else if (Mongos.SK_DESC.equals(key)) {
					String dbnm = moe.getFieldDbName(ele.value().toString());
					sort.put(dbnm, -1);
				}
				// LIMIT
				else if (Mongos.SK_LIMIT.equals(key)) {
					cur.limit((Integer) ele.value());
				}
				// SKIP
				else if (Mongos.SK_SKIP.equals(key)) {
					cur.skip((Integer) ele.value());
				}
				// 未知
				else {
					throw Lang.makeThrow("Unknown sort command '{%s:%s}'", key, ele.value());
				}
			}
		});
		// 设置排序
		if (!sort.keySet().isEmpty()) {
			cur.sort(sort);
		}
	}

	@Override
	public MCur append(String key, Object val) {
		return new MCur(this, key, val);
	}

	private MCur() {
		super();
	}

	private MCur(MCur prev, String key, Object value) {
		super(prev, key, value);
	}

	/**
	 * 创建链表，并加入一个偏移节点
	 * 
	 * @return 链表对象
	 */
	public static MCur SKIP(int num) {
		return NEW().skip(num);
	}

	/**
	 * 创建链表，并加入一个限制数量的节点
	 * 
	 * @return 链表对象
	 */
	public static MCur LIMIT(int num) {
		return NEW().limit(num);
	}

	/**
	 * 创建链表，并加入一个降序节点
	 * 
	 * @return 链表对象
	 */
	public static MCur DESC(String field) {
		return NEW().desc(field);
	}

	/**
	 * 创建链表，并加入一个升序节点
	 * 
	 * @return 链表对象
	 */
	public static MCur ASC(String field) {
		return NEW().asc(field);
	}

	/**
	 * 创建链表的静态方法
	 * 
	 * @return 链表对象
	 */
	public static MCur NEW() {
		return new MCur();
	}

}
