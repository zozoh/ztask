package org.nutz.mongo.entity;

import java.util.List;

import com.mongodb.DBObject;

/**
 * MongoDB 的实体接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public interface MongoEntity {

	/**
	 * 根据一个 ref 对象，得到集合名称
	 * <p>
	 * 如果是 POJO 则实现类会根据注解得到集合名称，否则会从 ref 对象中获取集合名称
	 * 
	 * @param ref
	 *            参考对象
	 * @return 集合名称
	 */
	String getCollectionName(Object ref);

	/**
	 * @return 实体是否声明了索引
	 */
	boolean hasIndexes();

	/**
	 * @return 实体的索引列表
	 */
	List<MongoEntityIndex> getIndexes();

	/**
	 * 格式花参考对象
	 * <p>
	 * 参考对象用来查询和设置，实现类应该根据自己的具体设置修改参考对象的键值。
	 * 
	 * @param o
	 *            参考对象
	 * @return DBObject 以供驱动程序使用
	 */
	DBObject formatObject(Object o);

	/**
	 * 从一个对象生成 MongoDB 的文档对象
	 * 
	 * @param obj
	 *            对象
	 * @return DBObjet
	 */
	DBObject toDBObject(Object obj);

	/**
	 * 根据一个键，得到这个键在 MongoDB 集合中的名字
	 * 
	 * @param key
	 *            键
	 * @return 数据库中的名字
	 */
	String getFieldDbName(String key);

	/**
	 * 根据一个 MongoDB 的文档对象 生成目标对象
	 * 
	 * @param dbo
	 *            MongoDB 的文档对象
	 * @return 目标对象
	 */
	Object toObject(DBObject dbo);

	/**
	 * 为要操作的对象，填充一个 _id
	 * 
	 * @param obj
	 *            目标对象
	 */
	void fillId(Object obj);

	/**
	 * 如果 _id 键不存在，为要操作的对象，填充一个 _id
	 * 
	 * @param obj
	 *            目标对象
	 */
	void fillIdIfNoexits(Object obj);

	/**
	 * @return 固定集合的大小，小于等于 0 表示为非固定集合
	 */
	long getCappedSize();
	
	long getCappedMax() ;
}
