package org.nutz.mongo.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.MongoDao;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.SessionProvider;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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
		DBCollection systemJs = dao.getDB().getCollection("system.js");
		systemJs.remove(new BasicDBObject("_id", "mongoSessionClean"));
		systemJs.insert(new BasicDBObject("_id", "mongoSessionClean").append(
				"value",
				new Code(
						Files.read(Files
								.findFile("org/nutz/mongo/session/mongoSessionClean.js")))));
		cleaner = new Thread("MongoSessionCleaner") {
			public void run() {
				while (!stop) {
					try {
						context.getMongoDao().getDB()
								.eval("mongoSessionClean()");
					} catch (Throwable e) {
						if (log.isWarnEnabled())
							log.warn("Clean case some error", e);
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
				if ("MongoSessionKey".equalsIgnoreCase(cookie.getName()))
					key = cookie.getValue();
			}
		if (!Strings.isBlank(key)) {
			MongoSession session = getSession(key);
			if (session != null
					&& req.getRemoteAddr().equals(
							session.getValue("remoteAddr"))
					&& req.getHeader("User-Agent").equals(
							session.getValue("userAgent"))) {
				session.touch();
				return new MongoHttpSession(context, new ObjectId(
						session.getId()));
			}
		}
		if (!createNew)
			return null;
		Map<String, String> info = new HashMap<String, String>();
		info.put("remoteAddr", req.getRemoteAddr());
		info.put("userAgent", req.getHeader("User-Agent"));
		MongoSession session = MongoSession.create(context, info);
		MongoHttpSession httpSession = new MongoHttpSession(context,
				new ObjectId(session.getId()));
		httpSession.setServletContext(servletContext);
		httpSession.setNewCreate(true);
		Cookie cookie = new Cookie("MongoSessionKey", session.getId());
		cookie.setMaxAge(30 * 60);
		resp.addCookie(cookie);
		return httpSession;
	}

	public HttpServletRequest filter(final HttpServletRequest req,
			final HttpServletResponse resp, final ServletContext servletContext) {
		InvocationHandler ih = new InvocationHandler() {
			public Object invoke(Object obj, Method method, Object[] args)
					throws Throwable {
				if ("getSession".equals(method.getName())) {
					if (args == null || args.length == 0)
						return getHttpSession((HttpServletRequest) req,
								(HttpServletResponse) resp, servletContext,
								true);
					else
						return getHttpSession((HttpServletRequest) req,
								(HttpServletResponse) resp, servletContext,
								(Boolean) args[0]);
				}
				return method.invoke(req, args);
			}
		};
		return (HttpServletRequest) Proxy.newProxyInstance(getClass()
				.getClassLoader(), new Class<?>[] { HttpServletRequest.class },
				ih);
	}

}
