package org.nutz.ztask;

import static org.nutz.web.Webs.Err.*;

import org.nutz.web.WebException;

/**
 * 封装了本应用全部得错误
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Err {

	/**
	 * 用户相关的错误
	 */
	public static class U {

		public static WebException INVALID_LOGIN() {
			return create("e.u.invalid_login");
		}

		public static WebException NO_EXISTS(String name) {
			return create("e.u.no_exists").reason(name);
		}

		public static WebException EXISTS(String name) {
			return create("e.u.exists").reason(name);
		}

	}

}
