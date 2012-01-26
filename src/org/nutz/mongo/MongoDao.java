package org.nutz.mongo;

import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.mongo.entity.MongoEntity;
import org.nutz.mongo.util.MCur;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * 提供一组 MongoDB 操作的便捷接口，它会尽量复合 MongoDB 控制台用户的使用习惯
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MongoDao {

	/**
	 * 执行删除
	 * 
	 * @param <T>
	 * @param type
	 *            对象类型，除了 POJO 也可以是 Map 或者 String
	 * @param q
	 *            查询条件
	 * @return 修改结果
	 */
	public WriteResult remove(Class<?> type, Object q) {
		WriteResult wr = null;
		MongoEntity<?> moe = Mongos.entity(type);
		// 获得集合
		String collName = moe.getCollectionName(q);
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			// 将 ref 对象转换成 DBObject
			DBObject dbRef = moe.formatObject(q);

			// 执行删除
			wr = coll.remove(dbRef);
		}
		return wr;
	}

	/**
	 * @param q
	 *            参考对象，可以是 Map 或者 String
	 * @return 修改结果
	 */
	public WriteResult remove(Object q) {
		return remove(q.getClass(), q);
	}

	/**
	 * 根据 ID 删除一个对象
	 * 
	 * @param id
	 *            对象 ID
	 * @return 修改结果
	 */
	public WriteResult removeById(String id) {
		return remove(String.class, String.format("{'_id':'%s'}", id));
	}

	/**
	 * 保存一个对象，如果对象不存在，则添加。
	 * <p>
	 * 如果对象没有存在 "_id"，则自动设置 "_id"
	 * 
	 * @param <T>
	 * @param obj
	 *            对象
	 * @return 保存后的对象，如果为 null，表示集合不存在，保存失败
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T save(T obj) {
		MongoEntity<T> moe = (MongoEntity<T>) Mongos.entity(obj);
		String collName = moe.getCollectionName(obj);
		if (db.collectionExists(collName)) {
			moe.fillIdIfNoexits(obj);
			DBObject dbo = moe.toDBObject(obj);
			DBCollection coll = db.getCollection(collName);
			coll.save(dbo);
			return obj;
		}
		return null;
	}

	/**
	 * 更新一组对象
	 * 
	 * @param enref
	 *            参考对象，通过这个这对象获得集合名称。可以是 Class, POJO,Map,JSON字符串
	 * @param q
	 *            查询条件
	 * @param o
	 *            要更新的字段
	 * @return 修改结果
	 */
	public WriteResult update(Object enref, Object q, Object o) {
		MongoEntity<?> moe = (MongoEntity<?>) Mongos.entity(enref);
		String collName = moe.getCollectionName(q);
		WriteResult wr = null;
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			DBObject dbq = moe.formatObject(q);
			DBObject dbo = moe.formatObject(o);
			wr = coll.updateMulti(dbq, dbo);
		}
		return wr;
	}

	/**
	 * 更新一组对象
	 * 
	 * @param collName
	 *            集合名称
	 * @param q
	 *            查询条件
	 * @param o
	 *            要更新的字段
	 * @return 修改结果
	 */
	public WriteResult updateBy(String collName, Object q, Object o) {
		WriteResult wr = null;
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			DBObject dbq = Mongos.obj2dbo(q);
			DBObject dbo = Mongos.obj2dbo(o);
			wr = coll.updateMulti(dbq, dbo);
		}
		return wr;
	}

	/**
	 * 根据 ID 更新一个对象
	 * 
	 * @param enref
	 *            参考对象，通过这个这对象获得集合名称。可以是 Class, POJO,Map,JSON字符串
	 * @param id
	 *            对象 ID
	 * @param o
	 *            要更新的字段
	 * @return 修改结果
	 */
	public WriteResult updateById(Object enref, String id, Object o) {
		return update(enref, Lang.mapf("{_id:'%s'}", id), o);
	}

	/**
	 * 根据 ID 更新一个对象
	 * 
	 * @param collName
	 *            集合名称
	 * @param id
	 *            对象 ID
	 * @param o
	 *            要更新的字段
	 * @return 修改结果
	 */
	public WriteResult updateObj(String collName, String id, Object o) {
		return updateBy(collName, Lang.mapf("{_id:'%s'}", id), o);
	}

	/**
	 * 根据 Map 或者 JSON 字符串或者 POJO 执行查询
	 * 
	 * @param <T>
	 * @param type
	 *            对象类型，除了 POJO 也可以是 Map 或者 String
	 * @param q
	 *            查询条件，可以是 POJO, String,Map,或者 Moo
	 * 
	 * @param mcur
	 *            对游标的排序等方式的修改
	 * 
	 * @return 对象列表
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> type, Object q, MCur mcur) {
		LinkedList<T> re = new LinkedList<T>();
		MongoEntity<?> moe = Mongos.entity(type);
		// 获得集合
		String collName = moe.getCollectionName(q);
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			// 将 ref 对象转换成 DBObject
			DBObject dbRef = moe.formatObject(q);

			// 执行查询
			DBCursor cur = null == dbRef ? coll.find() : coll.find(dbRef);

			// 设置排序条件
			if (null != mcur) {
				mcur.setupCursor(cur, moe);
			}

			// 执行转换
			while (cur.hasNext()) {
				DBObject dbo = cur.next();
				T obj = (T) moe.toObject(dbo);
				re.add(obj);
			}
		}
		return re;
	}

	/**
	 * 根据 Map 或者 JSON 字符串执行查询
	 * 
	 * @param <T>
	 * @param q
	 *            查询条件，可以是 POJO, String,Map,或者 Moo
	 * @param mcur
	 *            对游标的排序等方式的修改
	 * @return 对象列表
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> find(T q, MCur mcur) {
		if (null == q)
			return null;
		Class<T> type = (Class<T>) q.getClass();
		return find(type, q, mcur);
	}

	/**
	 * 查找一条记录
	 * 
	 * @param <T>
	 * @param type
	 *            对象类型，除了 POJO 也可以是 Map 或者 String
	 * @param q
	 *            查询条件
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T findOne(Class<T> type, Object q) {
		T re = null;
		MongoEntity<?> moe = Mongos.entity(type);
		// 获得集合
		String collName = moe.getCollectionName(q);
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			// 将 ref 对象转换成 DBObject
			DBObject dbRef = moe.formatObject(q);
			DBObject dbo = null == dbRef ? coll.findOne() : coll.findOne(dbRef);
			// 执行转换
			re = (T) moe.toObject(dbo);
		}
		return re;
	}

	/**
	 * 根据 ID 查找一条记录
	 * 
	 * @param <T>
	 * @param type
	 *            对象类型，除了 POJO 也可以是 Map 或者 String
	 * @param id
	 *            对象 ID
	 * @return 对象
	 */
	public <T> T findById(Class<T> type, String id) {
		return findOne(type, Lang.mapf("{_id:'%s'}", id));
	}

	// ======================================================== 私有

	private DB db;

	MongoDao(DB db) {
		this.db = db;
	}

	/**
	 * 清除一个集合
	 * 
	 * @param collName
	 *            集合名称
	 * 
	 * @param dropIfExists
	 *            true 则如果存在，就 drop
	 */
	public void create(String collName, boolean dropIfExists) {
		// 判断集合是否存在
		if (!dropIfExists && db.collectionExists(collName))
			return;
		// 首先移除
		drop(collName);
		// TODO 这里可以设置集合一些特殊的设置，比如固定集合等
		db.createCollection(collName, new BasicDBObject());
	}

	/**
	 * 建立一个集合
	 * 
	 * @param pojoType
	 *            集合的 POJO 类
	 * 
	 * @param dropIfExists
	 *            true 则如果存在，就 drop
	 */
	public void create(Class<?> pojoType, boolean dropIfExists) {
		MongoEntity<?> moe = Mongos.entity(pojoType);
		create(moe.getCollectionName(null), dropIfExists);
	}

	/**
	 * 移除一个集合
	 * 
	 * @param collName
	 *            集合名称
	 */

	public void drop(String collName) {
		DBCollection coll = db.getCollection(collName);
		coll.drop();
	}

	/**
	 * 移除一个集合
	 * 
	 * @param pojoType
	 *            集合的 POJO 类
	 * 
	 */
	public void drop(Class<?> pojoType) {
		MongoEntity<?> moe = Mongos.entity(pojoType);
		drop(moe.getCollectionName(null));
	}

	/**
	 * 移除这个数据库
	 */
	public void dropMe() {
		db.dropDatabase();
	}

	/**
	 * 清除数据库的游标
	 */
	public void cleanCursors() {
		db.cleanCursors(true);
	}

}
