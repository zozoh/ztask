package org.nutz.mongo.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.types.ObjectId;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.MongoDao;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.SessionProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * MongoSession会话管理器,负责查询/生成MongoSession
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class MongoSessionManager implements SessionProvider {

	private static final Log log = Logs.get();

	private ManagerContext context;

	private Thread cleaner;

	private boolean stop;

	private Object lock;
	
	public MongoSessionManager(MongoConnector conn, String dbname) {
		this(conn.getDao(dbname));
	}

	public MongoSessionManager(final MongoDao dao) {
		this(dao, "http.session");
	}

	public MongoSessionManager(final MongoDao dao, final String colName) {
		context = new ManagerContext();
		context.setMongoDao(dao);
		context.setSessions(dao.getDB().getCollection(colName));
		context.setProvider(new SessionValueAdpter());
		lock = new Object();
		cleaner = new Thread("MongoSessionCleaner") {
			public void run() {
				DBObject query = new BasicDBObject();
				BasicDBObject keys = new BasicDBObject();
				keys.put("lastAccessedTime", 1);
				keys.put("maxInactiveInterval", 1);
				while (!stop) {
					DBCursor cur = null;
					try {
						cur = context.getSessions().find(query, keys).snapshot();
						while (cur.hasNext()) {
							DBObject dbo = cur.next();
							long lastAccessedTime = ((Number)dbo.get("lastAccessedTime")).longValue();
							long maxInactiveInterval = ((Number)dbo.get("maxInactiveInterval")).longValue();
							if ((lastAccessedTime - System.currentTimeMillis()) / 1000 > maxInactiveInterval) {
								if (log.isDebugEnabled())
									log.debug("Remove session id="+dbo.get("_id"));
								context.getSessions().remove(new BasicDBObject("_id", dbo.get("_id")));
							}
						}
					} catch (Throwable e) {
						if (log.isWarnEnabled())
							log.warn("Clean case some error", e);
					} finally {
						if (cur != null) {
							try {
								cur.close();
								cur = null;
							} catch (Throwable e) {}
						}
					}

					synchronized (lock) {
						try {
							lock.wait(60000);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			}
		};
		cleaner.start();
	}

	public void notifyStop() {
		stop = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public void setProvider(SessionValueAdpter provider) {
		context.setProvider(provider);
	}

	public MongoSession getSession(String key) {
		DBObject dbo = context.getSessions().findOne(
				new BasicDBObject("_id", new ObjectId(key)),
				new BasicDBObject("_id", 1));
		if (dbo == null || dbo.get("_id") == null)
			return null;
		return new MongoSession(context, (ObjectId) dbo.get("_id"));
	}

	// -----------------------------------------------------------------------------------------------
	public MongoHttpSession getSession(HttpServletRequest req) {
		return getHttpSession(req, Mvcs.getResp(), Mvcs.getServletContext(),
				true);
	}

	public MongoHttpSession getHttpSession(HttpServletRequest req,
			HttpServletResponse resp, ServletContext servletContext,
			boolean createNew) {
		String key = null;
		if (req.getCookies() != null)
			for (Cookie cookie : req.getCookies()) {
				if ("msessionid".equalsIgnoreCase(cookie.getName()))
					key = cookie.getValue();
			}
		if (!Strings.isBlank(key)) {
			MongoSession session = getSession(key);
			if (session != null ) {
				Map<String,Object> extData = session.getExtData();
				if (req.getRemoteAddr().equals(extData.get("remoteAddr"))
						&& req.getHeader("User-Agent").equals(extData.get("userAgent"))) {
					session.touch();
					return new MongoHttpSession(context, new ObjectId(session.getId()));
				}
			}
		}
		if (!createNew)
			return null;
		Map<String, Object> extData = new HashMap<String, Object>();
		extData.put("remoteAddr", req.getRemoteAddr());
		extData.put("userAgent", req.getHeader("User-Agent"));
		MongoHttpSession httpSession = new MongoHttpSession(context,create(extData));
		httpSession.setServletContext(servletContext);
		httpSession.setNewCreate(true);
		Cookie cookie = new Cookie("msessionid", httpSession.getId());
		cookie.setMaxAge(30 * 24 * 60 * 60);
		resp.addCookie(cookie);
		return httpSession;
	}

	public HttpServletRequest filter(final HttpServletRequest req,
			final HttpServletResponse resp, final ServletContext servletContext) {
		return new HttpServletRequestWrapper(req) {
			
			public HttpSession getSession(boolean create) {
				return getHttpSession(req, resp, servletContext, create);
			}
			
			public HttpSession getSession() {
				return getHttpSession(req, resp, servletContext, true);
			}
		};
	}


	public final ObjectId create(Map<String, Object> extData) {
		BasicDBObject dbo = new BasicDBObject();
		dbo.put("_id", new ObjectId());
		dbo.put("extData", extData != null ? extData : Collections.EMPTY_MAP);
		dbo.put("creationTime", System.currentTimeMillis());
		dbo.put("lastAccessedTime", System.currentTimeMillis());
		dbo.put("maxInactiveInterval", 30 * 60); // 30min
		dbo.put("attr", Collections.EMPTY_MAP);
		context.getSessions().insert(dbo);
		ObjectId id = dbo.getObjectId("_id");
		if (log.isDebugEnabled())
			log.debugf("New MongoSession(%s) ==> %s",id, Json.toJson(extData, JsonFormat.compact()));
		return id;
	}
}
