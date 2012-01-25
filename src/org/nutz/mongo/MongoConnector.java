package org.nutz.mongo;

import java.net.UnknownHostException;

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
	 * 注销一个 MongoDB 的连接
	 */
	public void depose() {
		mongo.close();
	}

}
