package org.nutz.err;

public class Err {

	/******************************************************************************
	 * 通用创建方法
	 */
	public static NutException create(String key) {
		return create(null, key, null);
	}

	public static NutException create(String key, Object reason) {
		return create(null, key, reason);
	}

	public static NutException create(Throwable e, String key, Object reason) {
		return new NutException(e).key(key).reason(reason);
	}

	public static NutException wrap(Throwable e) {
		if (e instanceof NutException)
			return (NutException) e;
		return new NutException(e).key(e.getClass().getName()).reason(e.toString());
	}

}
