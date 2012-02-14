package org.nutz.ztask.web.module.core;

import javax.servlet.http.HttpSession;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.Webs;
import org.nutz.ztask.api.User;
import org.nutz.ztask.api.ZTaskFactory;
import org.nutz.ztask.util.Err;
import org.nutz.ztask.web.ZTaskConfig;

@InjectName
@IocBean
@At("/do")
public class LoginModule {

	@Inject("refer:conf")
	private ZTaskConfig conf;

	@Inject("refer:serviceFactory")
	private ZTaskFactory factory;

	@At("/login")
	@Ok(">>:/page/message")
	public void doLogin(@Param("nm") String name, @Param("pwd") String password, HttpSession sess) {
		// 验证
		User u = factory.users().verify(name, password);
		if (null == u)
			throw Err.U.INVALID_LOGIN();
		// 通过 ...
		sess.setAttribute(Webs.ME, u);

		// 记录一些配置数据
		sess.setAttribute("msg_inter", conf.getInt("sys-msg-update-interval", 200) * 1000);
		sess.setAttribute("rs", conf.get("app-rs", ""));
	}

	@At("/logout")
	@Ok(">>:/page/login")
	public void doLogout(HttpSession sess) {
		sess.removeAttribute(Webs.ME);
	}

}
