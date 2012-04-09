package org.nutz.mongo.session;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import org.bson.types.ObjectId;
import org.nutz.lang.Lang;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * 基于Mongo的分布式会话
 * @author wendal(wendal1985@gmail.com)
 *
 */
@SuppressWarnings("unchecked")
public class MongoSession {

	protected DBCollection sessions;
	private ObjectId id;
	private BasicDBObject queryKey;
	private boolean newCreate;
	protected ManagerContext context;

	public MongoSession(ManagerContext context, ObjectId id) {
		this.context = context;
		this.id = id;
		queryKey = new BasicDBObject("_id", id);
		sessions = context.getSessions();
	}

	public Object getAttribute(String key) {
		Object attr = ((Map<String, Object>) fetch("attr")).get(key);
		if (attr == null)
			return null;
		try {
			return context.getProvider().fromValue((DBObject) attr, context.getMongoDao());
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void setAttribute(String key, Object obj) {
		try {
			sessions.update(queryKey, new BasicDBObject("$set",
					new BasicDBObject("attr." + key, context.getProvider().toValue(obj))));
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}

	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(((Map<String, Object>) fetch("attr"))
				.keySet());
	}

	public long getCreationTime() {
		return (Long) fetch("creationTime");
	}

	public String getId() {
		return id.toString();
	}

	public long getLastAccessedTime() {
		return (Long) fetch("lastAccessedTime");
	}

	public void touch() {
		sessions.update(queryKey, new BasicDBObject("$set", new BasicDBObject(
				"lastAccessedTime", System.currentTimeMillis())));
	}

	public int getMaxInactiveInterval() {
		return (Integer) fetch("maxInactiveInterval");
	}

	public void invalidate() {
		sessions.remove(queryKey);
	}

	public void removeAttribute(String key) {
		sessions.update(queryKey, new BasicDBObject("$unset",
				new BasicDBObject("attr." + key, 1)));
	}

	public void setMaxInactiveInterval(int interval) {
		sessions.update(queryKey, new BasicDBObject("$set", new BasicDBObject(
				"maxInactiveInterval", interval)));
	}

	protected Object fetch(String key) {
		DBObject dbo = sessions.findOne(queryKey, new BasicDBObject(key, 1));
		if (dbo == null)
			throw Lang.makeThrow("Session is invalidated!");
		return dbo.get(key);
	}

	@Deprecated
	public Object getValue(String key) {
		throw Lang.noImplement();
	}

	@Deprecated
	public void putValue(String key, Object obj) {
		throw Lang.noImplement();
	}

	@Deprecated
	public String[] getValueNames() {
		throw Lang.noImplement();
	}

	@Deprecated
	public void removeValue(String key) {
		throw Lang.noImplement();
	}

	public boolean isNew() {
		return newCreate;
	}

	protected void setNewCreate(boolean newCreate) {
		this.newCreate = newCreate;
	}

	public Map<String, Object> getExtData() {
		return (Map<String, Object>) fetch("extData");
	}
	
	public void setExtData(Map<String, Object> extData) {
		sessions.update(queryKey, new BasicDBObject("$set", new BasicDBObject("extData", extData)));
	}
}
