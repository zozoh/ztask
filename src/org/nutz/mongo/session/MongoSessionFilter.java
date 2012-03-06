package org.nutz.mongo.session;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 替换原本的HttpServletRequest,改写其getSession方法为获取MongoSession
 * 
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class MongoSessionFilter implements Filter {

	private ServletContext servletContext;
	private String managerAttrName;
	private MongoSessionManager manager;

	public void doFilter(final ServletRequest req, final ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		InvocationHandler ih = new InvocationHandler() {
			public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
				if ("getSession".equals(method.getName())) {
					if (manager == null)
						manager = (MongoSessionManager) servletContext.getAttribute(managerAttrName);
					if (manager == null) {
						throw Lang.impossible();
					} else {
						if (args == null || args.length == 0)
							return manager.getHttpSession(	(HttpServletRequest) req,
															(HttpServletResponse) resp,
															servletContext,
															true);
						else
							return manager.getHttpSession(	(HttpServletRequest) req,
															(HttpServletResponse) resp,
															servletContext,
															(Boolean) args[0]);
					}
				}
				return method.invoke(req, args);
			}
		};
		HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(	getClass().getClassLoader(),
																					new Class<?>[]{HttpServletRequest.class},
																					ih);
		chain.doFilter(request, resp);
	}

	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();

		if (Strings.isBlank(config.getInitParameter("managerAttrName")))
			managerAttrName = "MongoSessionManager";
		else
			managerAttrName = config.getInitParameter("managerAttrName");
	}

	public void destroy() {
		if (manager != null)
			manager.notifyStop();
	}
}
