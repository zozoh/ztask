package org.nutz.mongo.util;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Callback;
import org.nutz.mongo.MongoDao;

import com.mongodb.DB;

/**
 * 提供安全操作的基本保证,结合Nutz.IOC和Aop进行使用,专供MongoDao使用
 * 
 * @author Wendal(wendal1985@gmail.com)
 */
public class SafeMooMethodInterceptor implements MethodInterceptor {

	public void filter(final InterceptorChain chain) throws Throwable {
		if ("runNoError".equals(chain.getCallingMethod().getName()))
			chain.doChain();
		else
			((MongoDao) chain.getCallingObj()).runNoError(new Callback<DB>() {

				public void invoke(DB obj) {
					try {
						chain.doChain();
					} catch (Throwable e) {
						throw Lang.wrapThrow(e);
					}
				}
			});
	}

}
