package org.nutz.mongo.entity;

import java.util.UUID;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mongo.annotation.CoIdType;

import com.mongodb.DBObject;

public class MongoEntityField {

	private String name;

	private String dbName;

	private CoIdType idType;

	private FieldAdaptor adaptor;

	protected boolean isNull(Object obj) {
		Object val = adaptor.get(obj);
		return null == val;
	}

	protected void fillId(Object obj) {
		if (null != idType)
			switch (idType) {
			// 保证值为 null，这样 MongoDB 会自动设置
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
		if (null == v)
			return;
		// 对于默认 ID，则，保证 dbo 中没有 "_id" 以便 MongoDB 自动设值
		if (isId()) {
			if (idType == CoIdType.DEFAULT)
				dbo.removeField("_id");
			else
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

	public MongoEntityField(FieldInfo fi) {
		name = fi.getName();
		dbName = Strings.sBlank(null == fi.getAnnotation() ? name : fi.getAnnotation().value(),
								name);
		if (null != fi.get_id())
			idType = fi.get_id().value();
		// 如果是 _id 那么， dbName 要固定
		if (isId())
			dbName = "_id";
		// 得到 In/Ejectint
		adaptor = fi.getAdaptor();
	}

}
