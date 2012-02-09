package org.nutz.mongo.entity;

import java.util.UUID;

import org.bson.types.ObjectId;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.annotation.CoIdType;

import com.mongodb.DBObject;

public class MongoEntityField {

	private String name;

	private String dbName;

	private CoIdType idType;

	private FieldAdaptor adaptor;

	private Mirror<?> mirror;
	
	private boolean ref;
	
	private boolean lazy;

	protected boolean isNull(Object obj) {
		Object val = adaptor.get(obj);
		return null == val;
	}

	protected void fillId(Object obj) {
		if (null != idType)
			switch (idType) {
			// 保证值为 null，这样 setToDB 的时候会设置一个 ObjectId
			case DEFAULT:
				adaptor.set(obj, null);
				break;
			case UU64:
				adaptor.set(obj, R.UU64());
				break;
			case UU16:
				adaptor.set(obj, R.UU16());
				break;
			case UUID:
				adaptor.set(obj, UUID.randomUUID().toString());
				break;
			default:
				throw Lang.noImplement();
			}
	}

	protected void setToDB(Object obj, DBObject dbo) {
		Object v = adaptor.get(obj);
		if (null == v) {
			// 如果是 Default Mongo ID，那么为其设置一个值
			if (isId() && idType == CoIdType.DEFAULT) {
				ObjectId ID = new ObjectId();
				dbo.put("_id", ID);
				// 设置回对象中，以便后续操作
				adaptor.set(obj, ID);
			}
			return;
		}
		// 对于默认 ID，则，保证 dbo 中没有 "_id" 以便 MongoDB 自动设值
		if (isId()) {
			if (idType == CoIdType.DEFAULT) {
				String ID = v.toString();
				// 检查一下 ID 的格式
				if (!Mongos.isDefaultMongoId(ID))
					throw Lang.makeThrow(	"_id(%s) field for '%s' is invalid format",
											ID,
											obj.getClass().getName());
				dbo.put("_id", new ObjectId(ID));
			} else
				dbo.put("_id", v);
		}
		// 其他字段
		else {
			dbo.put(dbName, v);
		}
	}

	protected void getFromDB(Object obj, DBObject dbo) {
		Object dbval = dbo.get(dbName);
		if (null == dbval)
			return;
		Object v;
		if (idType == CoIdType.DEFAULT) {
			v = dbval.toString();
		} else {
			v = dbval;
		}
		adaptor.set(obj, v);
	}

	public boolean isId() {
		return null != idType;
	}

	public String getName() {
		return name;
	}

	public String getDbName() {
		return dbName;
	}

	public CoIdType getIdType() {
		return idType;
	}

	public FieldAdaptor getAdaptor() {
		return adaptor;
	}

	public Mirror<?> getMirror() {
		return mirror;
	}
	
	public Class<?> getType(){
		return mirror.getType();
	}

	public MongoEntityField(FieldInfo fi) {
		mirror = fi.getMirror();
		name = fi.getName();
		dbName = Strings.sBlank(null == fi.getAnnotation() ? name : fi.getAnnotation().value(),
								name);
		if (null != fi.get_id())
			idType = fi.get_id().value();
		// 如果是 _id 那么， dbName 要固定
		if (isId())
			dbName = "_id";
		// 得到 In/Ejectint
		adaptor = fi.getAdaptor().setField(this);
		if (fi.getAnnotation() != null) {
			ref = fi.getAnnotation().ref();
			lazy = fi.getAnnotation().lazy();
		}
	}

	public String toString() {
		return String.format("%s(%s) > %s ", name, dbName, adaptor);
	}
	
	public boolean isRef() {
		return ref;
	}
	
	public boolean isLazy() {
		return lazy;
	}
}
