package org.nutz.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * 封装 MongoDB 的连接器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MongoConnector {

	private Mongo mongo;

	/**
	 * 创建一个单连接
	 * 
	 * @param host
	 *            数据服务的主机名
	 * @param port
	 *            数据服务的端口
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	public MongoConnector(String host, int port) throws UnknownHostException, MongoException {
		this.mongo = new Mongo(host, port);
	}

	/**
	 * 得到一个 MongoDB 的访问类，这个类是 DB 类的一个包裹，提供更便利的方法
	 * 
	 * @param dbname
	 *            数据库名称
	 * @return MongoDB 数据访问借口
	 */
	public MongoDao getDao(String dbname) {
		DB db = mongo.getDB(dbname);
		return new MongoDao(db);
	}

	/**
	 * 注销一个 MongoDB 的连接
	 */
	public void close() {
		mongo.close();
	}

}
