package org.nutz.mongo;

import org.nutz.lang.Lang;
import org.nutz.mongo.entity.MongoEntity;
import org.nutz.mongo.entity.MongoEntityMaker;

/**
 * 封装一些 MongoDB 的常用操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Mongos {

	private static final MongoEntityMaker entities = new MongoEntityMaker();

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

}
