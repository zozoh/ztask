package org.nutz.mongo.entity;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 描述一个实体的某一个索引
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MongoEntityIndex {

	private String name;

	private boolean unique;

	private Map<String, Integer> fields;

	/**
	 * 根据一个字符串构建索引对象
	 * 
	 * @param str
	 *            描述一个索引的字符串
	 * 
	 * @see org.nutz.mongo.annotation.CoIndexes
	 */
	public MongoEntityIndex(String str) {
		fields = new HashMap<String, Integer>();
		// 首先拆分，看看有木有名字
		String[] ss = Strings.splitIgnoreBlank(str, ":");
		if (ss.length > 1) {
			unique = ss[0].startsWith("!");
			name = unique ? ss[0].substring(1) : ss[0];
			ss = Strings.splitIgnoreBlank(ss[1], ",");
		}
		// 然后拆分，看看有多少字段
		for (String s : ss) {
			String key = s.substring(1);
			if (s.startsWith("+"))
				fields.put(key, 1);
			else if (s.startsWith("-"))
				fields.put(key, -1);
			else
				throw Lang.makeThrow("Index field '%s' of '%s' should starts by '+' or '-'", s, str);
		}
	}

	public String getName() {
		return name;
	}

	public boolean isUnique() {
		return unique;
	}

	public Map<String, Integer> getFields() {
		return fields;
	}

}
