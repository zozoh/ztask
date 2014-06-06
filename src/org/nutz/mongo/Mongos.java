package org.nutz.mongo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.nutz.castor.Castors;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.NutMap;
import org.nutz.mongo.entity.MongoEntity;
import org.nutz.mongo.entity.MongoEntityMaker;
import org.nutz.mongo.util.MoChain;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

/**
 * 封装一些 MongoDB 的常用操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Mongos {

	/**
	 * 缓存所有实体的单例
	 */
	private static final MongoEntityMaker entities = new MongoEntityMaker();

	/**
	 * 在 Map 中存放集合名称的键
	 */
	public static final String COLLECTION_KEY = "@table";

	public static final String SK_ASC = "$asc";
	public static final String SK_DESC = "$desc";
	public static final String SK_LIMIT = "$limit";
	public static final String SK_SKIP = "$skip";

	/**
	 * 快速创建 DBObject 的帮助方法
	 * 
	 * @return 一个空的的 DBObject
	 */
	public static DBObject dbo() {
		return new BasicDBObject();
	}

	/**
	 * 快速创建 DBObject 的帮助方法
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return 一个包含一个键值对的 DBObject
	 */
	public static DBObject dbo(String key, Object value) {
		return new BasicDBObject(key, value);
	}

	/**
	 * MongoDB 默认 ID 的模板
	 */
	private static final Pattern OBJ_ID = Pattern.compile("^[0-9a-f]{24}$");

	/**
	 * 判断给定的字符串是否是 MongoDB 默认的 ID 格式
	 * 
	 * @param ID
	 *            给定 ID
	 * @return true or false
	 */
	public static boolean isDefaultMongoId(String ID) {
		if (null == ID || ID.length() != 24)
			return false;
		return OBJ_ID.matcher(ID).find();
	}

	/**
	 * 快速创建 DBObject 的帮助方法
	 * 
	 * @param ID
	 *            DBObject 的 ID
	 * @return 一个包含一个键值对的 DBObject
	 */
	public static DBObject dboId(String ID) {
		if (isDefaultMongoId(ID))
			return dbo("_id", new ObjectId(ID));
		return dbo("_id", ID);
	}

	/**
	 * 将一个 Java 对象格式化成 Map，以便 MongoDB 驱动执行查询等操作
	 * 
	 * @param o
	 *            参考对象，可以是 String,Map或者MoChain
	 * @return Map 对象
	 */
	@SuppressWarnings("unchecked")
	public static NutMap obj2map(Object o) {
		// 空
		if (null == o)
			return new NutMap();

		// MoChain
		if (o instanceof MoChain)
			return ((MoChain) o).toMap();

		// NutMap
		if (o instanceof NutMap)
			return (NutMap) o;

		// 字符串
		if (o instanceof CharSequence)
			return Json.fromJson(NutMap.class, o.toString());

		// 更多判断 ...
		Mirror<?> mirror = Mirror.me(o.getClass());

		// 普通 Map
		if (mirror.isMap()) {
			return new NutMap((Map<String, Object>) o);
		}

		// POJO
		if (mirror.isPojo())
			return new NutMap(Lang.obj2map(o));

		// 其他的，调用 Castors 先变 Map 再说
		return Castors.me().castTo(o, NutMap.class);
	}

	/**
	 * 将一个 Java 对象转换成 DBObject，以便 MongoDB 驱动执行查询等操作
	 * 
	 * @param o
	 *            参考对象，可以是 String,Map或者MoChain
	 * 
	 * @return DBObject 对象
	 */
	public static DBObject obj2dbo(Object o) {
		return map2dbo(obj2map(o));
	}

	/**
	 * 将一个 Map 转换成 DBObject，以便 MongoDB 驱动执行查询等操作
	 * 
	 * @param map
	 *            Map 对象
	 * @return DBObject 对象
	 */
	@SuppressWarnings("unchecked")
	public static DBObject map2dbo(Map<String, Object> map) {
		DBObject dbo = new BasicDBObject();
		if (null != map) {
			for (Map.Entry<String, ? extends Object> en : map.entrySet()) {
				String key = en.getKey();
				Object val = en.getValue();
				if (null == val)
					continue;
				// 如果是 _id
				if ("_id".equals(key)) {
					String ID = val.toString();
					if (Mongos.isDefaultMongoId(ID)) {
						val = new ObjectId(ID);
					}
				}
				// 如果是枚举
				else if (val.getClass().isEnum())
					val = val.toString();

				// 如果是 Map，递归
				else if (val instanceof Map<?, ?>)
					val = map2dbo((Map<String, Object>) val);

				// 加入 DBObject
				dbo.put(key, val);
			}
		}
		return dbo;
	}

	/**
	 * 建立只有一个键的 Map 对象
	 * 
	 * @param key
	 *            键
	 * @param val
	 *            值
	 * @return Map 对象
	 */
	public static NutMap map(String key, Object val) {
		NutMap map = new NutMap();
		map.put(key, val);
		return map;
	}

	/**
	 * 根据一个 POJO 对象，获取一个实体
	 * 
	 * @param obj
	 *            参考对象
	 * @return MongoEntity 对象
	 */
	public static MongoEntity entity(Object obj) {
		return entities.get(obj);
	}

	/**
	 * 快速帮你建立一个 MongoDB 的连接
	 * 
	 * @param host
	 *            主机地址
	 * @param port
	 *            端口
	 * @return MongoDB 的连接管理器
	 */
	public static MongoConnector connect(String host, int port) {
		try {
			return new MongoConnector(host, port);
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 本函数为你获得一个自动增长的整数
	 * <p>
	 * 它用一个集合模拟 SQL 数据库的序列，这个序列集合在整个 DB 中是唯一的
	 * 
	 * @param db
	 *            DB 对象
	 * @param seqName
	 *            序列集合名称
	 * @return 自增过的整数
	 */
	public static int autoInc(DB db, String seqName) {
		DBObject q = Mongos.dbo("name", seqName);
		DBObject o = Mongos.dbo("$inc", 1);
		return (Integer) db.getCollection("inc_ids")
							.findAndModify(q, null, null, false, o, true, true)
							.get("id");
	}

	private static ThreadLocal<AtomicInteger> reqs = new ThreadLocal<AtomicInteger>();

	public static void run(DB db, Callback<DB> callback) {
		try {
			if (reqs.get() == null) { // 最顶层
				reqs.set(new AtomicInteger(0));
				db.requestStart();
			} else
				reqs.get().incrementAndGet();
			callback.invoke(db);
		}
		finally {
			if (reqs.get().getAndDecrement() == 0)// 最顶层
				db.requestDone();
		}
	}
}
