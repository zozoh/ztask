package org.nutz.ztask.web;

import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.ztask.ZTask;
import org.nutz.ztask.api.InitService;

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
			log.infof("init zTask (%s) ...", ZTask.version());
		// 得到 Ioc 容器
		final Ioc ioc = config.getIoc();

		// 初始化数据
		ioc.get(InitService.class).init();

		if (log.isInfoEnabled())
			log.info("... done for init zTask");
	}

	@Override
	public void destroy(NutConfig config) {
		if (log.isInfoEnabled())
			log.info("destroy zTask");
	}

}
