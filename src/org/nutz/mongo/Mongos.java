package org.nutz.mongo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.nutz.castor.Castors;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
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
			return new BasicDBObject("_id", new ObjectId(ID));
		return new BasicDBObject("_id", ID);
	}

	/**
	 * 将一个 Java 对象格式化成 Map，以便 MongoDB 驱动执行查询等操作
	 * 
	 * @param o
	 *            参考对象，可以是 String,Map或者MoChain
	 * @return Map 对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> obj2map(Object o) {
		if (null == o)
			return new HashMap<String, Object>();

		if (o instanceof MoChain)
			return ((MoChain) o).toMap();

		if (o instanceof Map)
			return (Map<String, Object>) o;

		if (o instanceof CharSequence)
			return Json.fromJsonAsMap(Object.class, o.toString());

		return Castors.me().castTo(o, Map.class);
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
	public static DBObject map2dbo(Map<String, Object> map) {
		DBObject dbo = new BasicDBObject();
		dbo.putAll(map);
		return dbo;
	}

	/**
	 * 根据一个 POJO 对象，获取一个实体
	 * 
	 * @param obj
	 *            参考对象
	 * @return MongoEntity 对象
	 */
	public static MongoEntity<?> entity(Object obj) {
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
	 * 获取自动增长id
	 */
	public static Integer getAutoIncreaseID(DB db, String idName) {
		BasicDBObject query = new BasicDBObject("name", idName);
		BasicDBObject update = new BasicDBObject("$inc", new BasicDBObject("id", 1));
		return (Integer) db.getCollection("inc_ids")
				.findAndModify(query, null, null, false, update, true, true)
				.get("id");
	}
	
	
}
