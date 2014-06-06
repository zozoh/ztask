package org.nutz.mongo;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;
import com.mongodb.ReadPreference;

/**
 * 封装 MongoDB 的连接器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public class MongoConnector {
	
	private Mongo mongo;
	
	public MongoConnector(Mongo mongo) {
		this.mongo = mongo;
	}

	private String user;
	private String password;

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
	 * 使用MongoURI创建复杂的单个连接,以支持RepSet
	 */
	public MongoConnector(String mongoURI) throws UnknownHostException, MongoException {
		this.mongo = new Mongo(new MongoURI(mongoURI));
	}

	/**
	 * 使用MongoURI创建复杂的单个连接,以支持RepSet
	 */
	public MongoConnector(String mongoURI, boolean slaveOK) throws UnknownHostException, MongoException {
		this.mongo = new Mongo(new MongoURI(mongoURI));
		//@see http://api.mongodb.org/java/current/com/mongodb/Mongo.html#slaveOk()
		//slaveOk已经被废弃,但名字很好听
		if (slaveOK)
			this.mongo.setReadPreference(ReadPreference.SECONDARY);
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
		if (user != null)
			db.authenticate(user, password.toCharArray());
		return new MongoDao(db);
	}

	/**
	 * 注销一个 MongoDB 的连接
	 */
	public void close() {
		mongo.close();
	}

	public Mongo getMongo() {
		return mongo;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
