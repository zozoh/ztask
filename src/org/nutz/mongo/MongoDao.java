package org.nutz.mongo;

import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.NutMap;
import org.nutz.mongo.entity.MongoEntity;
import org.nutz.mongo.entity.MongoEntityIndex;
import org.nutz.mongo.util.MCur;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
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
		MongoEntity moe = Mongos.entity(type);
		// 获得集合
		String collName = moe.getCollectionName(q);
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			// 将 ref 对象转换成 DBObject
			DBObject dbRef = moe.formatObject(q);

			// 执行删除
			return coll.remove(dbRef);
		}
		return null;
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
	public WriteResult removeById(Class<?> type, String id) {
		return remove(type, Mongos.dboId(id));
	}

	/**
	 * 根据 ID 删除一个对象
	 * 
	 * @param collName
	 *            集合名称
	 * 
	 * @param id
	 *            对象 ID
	 * @return 修改结果
	 */
	public WriteResult removeById(String collName, String id) {
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			return coll.remove(Mongos.dboId(id));
		}
		return null;
	}

	/**
	 * 保存一个对象，如果对象不存在，则添加。
	 * <p>
	 * 如果对象没有存在 "_id"，则自动设置 "_id"
	 * 
	 * TODO 需要考虑一下 List 和 Array ...
	 * 
	 * @param <T>
	 * @param obj
	 *            对象
	 * @return 保存后的对象，如果为 null，表示集合不存在，保存失败
	 */
	public <T extends Object> T save(T obj) {
		MongoEntity moe = Mongos.entity(obj);
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
		MongoEntity moe = (MongoEntity) Mongos.entity(enref);
		String collName = moe.getCollectionName(q);
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			DBObject dbq = moe.formatObject(q);
			DBObject dbo = moe.formatObject(o);
			return coll.updateMulti(dbq, dbo);
		}
		return null;
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
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			DBObject dbq = Mongos.obj2dbo(q);
			DBObject dbo = Mongos.obj2dbo(o);
			return coll.updateMulti(dbq, dbo);
		}
		return null;
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
		return update(enref, Mongos.dboId(id), o);
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
		return updateBy(collName, Mongos.dboId(id), o);
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
	public <T> List<T> find(Class<T> type, Object q, MCur mcur) {
		final LinkedList<T> list = new LinkedList<T>();
		each(new Each<T>() {
			public void invoke(int index, T obj, int length) {
				list.add(obj);
			}
		}, type, q, mcur);
		return list;
	}

	/**
	 * 根据 Map 或者 JSON 字符串执行查询
	 * 
	 * @param <T>
	 * @param collName
	 *            集合名称
	 * @param q
	 *            查询条件，可以是 POJO, String,Map,或者 Moo
	 * @param mcur
	 *            对游标的排序等方式的修改
	 * @return 对象列表
	 */
	public List<NutMap> find(String collName, Object q, MCur mcur) {
		final LinkedList<NutMap> list = new LinkedList<NutMap>();
		each(new Each<NutMap>() {
			public void invoke(int index, NutMap obj, int length) {
				list.add(obj);
			}
		}, collName, q, mcur);
		return list;
	}

	/**
	 * 迭代某个集合里的数据，这个适合比较大的数据时，不能一次返回一个 List 的场景
	 * 
	 * @param <T>
	 * @param callback
	 *            回调，由于不知到数据的总体大小(为了效率)，所以 length 一项永远为 -1
	 * @param type
	 *            对象类型，除了 POJO 也可以是 Map 或者 String
	 * @param q
	 *            查询条件，可以是 POJO, String,Map,或者 Moo
	 * 
	 * @param mcur
	 *            对游标的排序等方式的修改
	 */
	public <T> void each(Each<T> callback, Class<T> type, Object q, MCur mcur) {
		MongoEntity moe = Mongos.entity(type);
		_each(callback, moe.getCollectionName(q), moe, q, mcur);
	}

	/**
	 * 根据 Map 或者 JSON 字符串迭代某个集合里的数据， <br>
	 * 这个适合比较大的数据时，不能一次返回一个 List 的场景
	 * 
	 * @param <T>
	 * @param callback
	 *            回调，由于不知到数据的总体大小(为了效率)，所以 length 一项永远为 -1
	 * @param q
	 *            查询条件，可以是 POJO, String,Map,或者 Moo
	 * @param mcur
	 *            对游标的排序等方式的修改
	 * @return 对象列表
	 */
	public void each(Each<NutMap> callback, String collName, Object q, MCur mcur) {
		MongoEntity moe = Mongos.entity(q);
		_each(callback, collName, moe, q, mcur);
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
		MongoEntity moe = Mongos.entity(type);
		// 获得集合
		String collName = moe.getCollectionName(q);
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			// 将 ref 对象转换成 DBObject
			DBObject dbRef = moe.formatObject(q);
			DBObject dbo = null == dbRef ? coll.findOne() : coll.findOne(dbRef);
			// 执行转换
			return (T) moe.toObject(dbo);
		}
		return null;
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

	/**
	 * 根据条件，计算一组对象的数量
	 * 
	 * @param enref
	 *            参考对象，根据这个对象获得集合名称
	 * @param q
	 *            条件，为 null 表示整个集合
	 * @return 数量
	 */
	public long count(Object enref, Object q) {
		MongoEntity moe = Mongos.entity(enref);
		String collName = moe.getCollectionName(q);
		if (db.collectionExists(collName)) {
			DBCollection coll = db.getCollection(collName);
			DBObject dbq = moe.formatObject(q);
			return null == q ? coll.count() : coll.count(dbq);
		}
		return -1;
	}

	// ======================================================== 私有

	private DB db;

	protected MongoDao(DB db) {
		this.db = db;
	}

	/**
	 * 创建一个集合
	 * 
	 * @param collName
	 *            集合名称
	 * 
	 * @param dropIfExists
	 *            true 则如果存在，就 drop
	 */
	public void create(String collName, boolean dropIfExists) {
		create(collName, dropIfExists, -1, -1);
	}

	/**
	 * 创建一个集合
	 * 
	 * @param collName
	 *            集合名称
	 * 
	 * @param dropIfExists
	 *            true 则如果存在，就 drop
	 * 
	 * @param cappedSize
	 *            如果大于0,则创建一个固定集合,单位是byte
	 */
	public void create(String collName, boolean dropIfExists, long cappedSize, long cappedMax) {
		// 判断集合是否存在，如果存在，且是不需要 drop 的 ...
		// TODO 如果一个集合是非固定的，想将其变成固定的，这个是不是需要再弄个方法叫 toCapped ?
		// by wendal : 这是不可逆的操作哦,一般不这样做吧?
		if (!dropIfExists && db.collectionExists(collName))
			return;

		// 首先移除
		drop(collName);

		// 创建固定集合?
		BasicDBObject cappedConfig = new BasicDBObject();
		if (cappedSize > 0 || cappedMax > 0)
			cappedConfig.put("capped", true);
		if (cappedSize > 0)
			cappedConfig.put("size", cappedSize);
		if (cappedMax > 0)
			cappedConfig.put("max", cappedMax);
		
		db.createCollection(collName, cappedConfig);
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
		// 得到对应实体
		MongoEntity moe = Mongos.entity(pojoType);

		// 创建集合
		String collName = moe.getCollectionName(null);
		create(collName, dropIfExists, moe.getCappedSize(), moe.getCappedMax());

		// 创建索引
		if (moe.hasIndexes()) {
			for (MongoEntityIndex mei : moe.getIndexes()) {
				DBCollection coll = db.getCollection(collName);
				DBObject keys = moe.formatObject(mei.getFields());
				// 采用默认的名称
				if (Strings.isBlank(mei.getName())) {
					coll.ensureIndex(keys, Mongos.dbo("unique", mei.isUnique()));
				}
				// 采用自定义名称
				else {
					coll.ensureIndex(keys, mei.getName(), mei.isUnique());
				}
			}
		}
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
		MongoEntity moe = Mongos.entity(pojoType);
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

	/**
	 * 最终极的方法，提供给你一个 callback，你可以随意访问 MongoDB 的 DB 这个类
	 * <p>
	 * 在你提供的回调内，你的 DB 对象，会保证访问同一个连接
	 * 
	 * @param callback
	 *            回调
	 */
	public void run(Callback<DB> callback) {
		Mongos.run(db, callback);
	}

	/**
	 * 最终极的方法，提供给你一个 callback，你可以随意访问 MongoDB 的 DB 这个类
	 * <p>
	 * 在你提供的回调内，你的 DB 对象，会保证访问同一个连接
	 * <p>
	 * 这个方法不容忍错误，即，它调用完你的回调后，会查查，是不是有 lastError，如果有，则抛错
	 * 
	 * @param callback
	 *            回调
	 */
	public void runNoError(final Callback<DB> callback) {
		Mongos.run(db, new Callback<DB>() {

			public void invoke(DB db) {
				callback.invoke(db);
				CommandResult cr = db.getLastError();
				if (cr.get("err") != null)
					throw Lang.makeThrow(MongoException.class, "Fail! %s", cr.getErrorMessage());
			}
		});
	}

	/**
	 * 获取lastError
	 * <p>
	 * <b style=color:red>注!!!</b> 仅适用于被db.requestStart()和db.requestDone()包裹的情况.
	 * <br>
	 * 其他情况下,无法保证一定是之前连接的lastError
	 * 
	 * @return 命令执行结果
	 */
	public CommandResult getLastError() {
		return db.getLastError();
	}

	@SuppressWarnings("unchecked")
	private <T> void _each(Each<T> callback, String collName, MongoEntity moe, Object q, MCur mcur) {
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

			// 遍历游标
			try {
				int index = 0;
				while (cur.hasNext()) {
					DBObject dbo = cur.next();
					T obj = (T) moe.toObject(dbo);
					try {
						callback.invoke(index++, obj, -1);
					}
					catch (ContinueLoop e) { //TODO 为何特别要捕捉ContinueLoop然后又抛出呢?
						throw Lang.wrapThrow(e);
					}
				}
			}
			catch (ExitLoop e) {}
			catch (LoopException e) {
				throw Lang.wrapThrow(e.getCause());
			}
		}
	}
}
