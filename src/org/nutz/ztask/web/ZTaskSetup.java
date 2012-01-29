package org.nutz.ztask.web;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Each;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.ztask.api.InitService;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.User;
import org.nutz.ztask.api.UserService;

/**
 * 启动和关闭服务时的设定
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZTaskSetup implements Setup {

	private static final Log log = Logs.get();

	@Override
	public void init(NutConfig config) {
		if (log.isInfoEnabled())
			log.info("init zTask ...");
		// 得到 Ioc 容器
		final Ioc ioc = config.getIoc();

		// 初始化数据
		ioc.get(InitService.class).init();

		// 得到 TaskService
		final TaskService tasks = ioc.get(TaskService.class);

		// 为每个用户创建初始堆栈
		UserService users = ioc.get(UserService.class);
		users.each(new Each<User>() {
			public void invoke(int index, User u, int length) {
				String snm = u.getMainStackName();
				TaskStack s = tasks.getStack(snm);
				// 木有，创建
				if (null == s) {
					s = tasks.createStackIfNoExistis(snm, u.getName());
					if (log.isDebugEnabled())
						log.debugf("  +++add stack [%s]", s);
				}
				// 有就打印
				else {
					s.setOwner(u.getName());
					tasks.saveStack(s);
					if (log.isDebugEnabled()) {
						log.debugf("  +update stack [%s]", s);
					}
				}
			}
		});

	}

	@Override
	public void destroy(NutConfig config) {
		if (log.isInfoEnabled())
			log.info("destroy zTask");
	}

}
