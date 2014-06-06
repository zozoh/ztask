package org.nutz.mongo.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

	public void doFilter(final ServletRequest req, final ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = ((MongoSessionManager) servletContext
				.getAttribute(managerAttrName)).filter(
				(HttpServletRequest) req, (HttpServletResponse) resp,
				servletContext);
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
