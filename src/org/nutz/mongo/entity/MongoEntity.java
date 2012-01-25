package org.nutz.mongo.entity;

import com.mongodb.DBObject;

/**
 * MongoDB 的实体接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public interface MongoEntity<T> {

	/**
	 * 从一个对象生成 MongoDB 的文档对象
	 * 
	 * @param obj
	 *            对象
	 * @return DBObjet
	 */
	DBObject from(T obj);

	/**
	 * 根据一个 MongoDB 的文档对象 生成目标对象
	 * 
	 * @param dbo
	 *            MongoDB 的文档对象
	 * @return 目标对象
	 */
	T to(DBObject dbo);

	/**
	 * 为要操作的对象，填充一个 _id
	 * 
	 * @param obj
	 *            目标对象
	 */
	void fillId(T obj);

	/**
	 * 如果 _id 键不存在，为要操作的对象，填充一个 _id
	 * 
	 * @param obj
	 *            目标对象
	 */
	void fillIdIfNoexits(T obj);

}
