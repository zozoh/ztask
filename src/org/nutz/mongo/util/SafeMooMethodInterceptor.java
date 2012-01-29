package org.nutz.mongo.util;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Callback;
import org.nutz.mongo.MongoDao;
import org.nutz.mongo.Mongos;

import com.mongodb.CommandResult;
import com.mongodb.DB;

/**
 * 提供安全操作的基本保证,结合Nutz.IOC和Aop进行使用
 * @author Wendal(wendal1985@gmail.com)
 */
public class SafeMooMethodInterceptor implements MethodInterceptor {

	public void filter(final InterceptorChain chain) throws Throwable {
		Mongos.run(null, new Callback<DB>() {
			
			public void invoke(DB db) {
				try {
					chain.invoke();
					CommandResult cr = ((MongoDao)chain.getCallingObj()).getLastError();
					if (cr.get("err") != null)
						throw Lang.makeThrow("Fail! %s", cr.getErrorMessage());
				} catch (Throwable e) {
					throw Lang.wrapThrow(e);
				}
			}
		});
	}

}
