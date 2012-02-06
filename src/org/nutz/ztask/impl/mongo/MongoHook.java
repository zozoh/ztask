package org.nutz.ztask.impl.mongo;

import org.nutz.lang.Lang;
import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.ztask.api.Hook;
import org.nutz.ztask.api.HookType;

@Co("hook")
public class MongoHook implements Hook {

	@CoField("tp")
	private HookType type;

	@CoField("h")
	private String handler;

	@Override
	public String getID() {
		return type.toString() + ":" + handler;
	}

	public void setType(HookType type) {
		this.type = type;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	@Override
	public HookType getType() {
		return type;
	}

	@Override
	public String getHandler() {
		return handler;
	}

	public String toString() {
		return getID();
	}

	@Override
	public boolean isSame(Hook hook) {
		if (null == hook)
			return false;
		if (type != hook.getType())
			return false;
		if (!Lang.equals(handler, hook.getHandler()))
			return false;
		return true;
	}

}
