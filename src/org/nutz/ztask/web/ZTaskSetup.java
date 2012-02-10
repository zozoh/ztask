package org.nutz.ztask.web;

import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.ztask.ZTask;
import org.nutz.ztask.api.InitService;
import org.nutz.ztask.api.TimerSchedule;
import org.nutz.ztask.thread.AbstractAtom;

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

		// 启动后台进程
		AbstractAtom[] atoms = new AbstractAtom[3];
		atoms[0] = ioc.get(AbstractAtom.class, "schd_update");
		atoms[1] = ioc.get(AbstractAtom.class, "timer_run");
		atoms[2] = ioc.get(AbstractAtom.class, "send_mail");

		for (int i = 0; i < atoms.length; i++) {
			if (null != atoms[i]) {
				Thread t = new Thread(atoms[i], atoms[i].name());
				t.start();
			}
		}

		// 初始化结束
		if (log.isInfoEnabled())
			log.info("... done for init zTask");
	}

	@Override
	public void destroy(NutConfig config) {
		// 关闭并通知
		Ioc ioc = config.getIoc();
		TimerSchedule glock = ioc.get(TimerSchedule.class, "schedule");
		glock.stop();

		if (log.isInfoEnabled())
			log.info("zTask is down");
	}

}
