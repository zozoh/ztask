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
import org.nutz.ztask.api.UserService;
import org.nutz.ztask.util.Err;

@InjectName
@IocBean
@At("/do")
public class LoginModule {

	@Inject("refer:userService")
	private UserService usrs;

	@At("/login")
	@Ok(">>:/page/mystack")
	public void doLogin(@Param("nm") String name, @Param("pwd") String password, HttpSession sess) {
		// 验证
		User u = usrs.verify(name, password);
		if (null == u)
			throw Err.U.INVALID_LOGIN();
		// 通过 ...
		sess.setAttribute(Webs.ME, u);
	}

	@At("/logout")
	@Ok(">>:/page/login")
	public void doLogout(HttpSession sess) {
		sess.removeAttribute(Webs.ME);
	}

}
