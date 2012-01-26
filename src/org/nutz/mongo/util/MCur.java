package org.nutz.mongo.util;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.entity.MongoEntity;

import com.mongodb.DBCursor;

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

	public MCur skip(String num) {
		return append(Mongos.SK_SKIP, num);
	}

	public void setupCursor(final DBCursor cur, final MongoEntity<?> moe) {
		each(new Each<MoChain>() {
			public void invoke(int index, MoChain ele, int length) {
				String key = ele.key();
				// ASC
				if (Mongos.SK_ASC.equals(key)) {
					cur.sort(Mongos.dbo(moe.getFieldDbName(ele.value().toString()), 1));
				}
				// DESC
				else if (Mongos.SK_DESC.equals(key)) {
					cur.sort(Mongos.dbo(moe.getFieldDbName(ele.value().toString()), -1));
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
	}

	@Override
	public MCur append(String key, Object val) {
		return new MCur(this, key, val);
	}

	public MCur() {
		super();
	}

	public MCur(MCur prev, String key, Object value) {
		super(prev, key, value);
	}

	/**
	 * 创建链表的静态方法
	 * 
	 * @return 链表对象
	 */
	public static MCur born() {
		return new MCur();
	}

}
