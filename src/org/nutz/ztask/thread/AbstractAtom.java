package org.nutz.ztask.thread;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;
import org.nutz.ztask.api.TimerSchedule;
import org.nutz.ztask.api.ZTaskFactory;

public abstract class AbstractAtom implements Atom {

	@Override
	public void run() {
		TimerSchedule schd = factory.schedule();
		
		while (!schd.isStop()) {
			// 唤醒 ...
			if (log.isInfoEnabled())
				log.infof("up @ %s", Times.sDTms(Times.now()));

			// 运行
			long interval = 0;
			try {
				interval = exec();
			}
			// 确保不会崩溃
			catch (Throwable e) {
				if (log.isWarnEnabled())
					log.warn("Some error happend!", e);
			}

			// 保证间隔时间不少于 1 秒
			if (interval > 0)
				interval = Math.max(1000, interval);

			// 等待 ...
			if (log.isInfoEnabled())
				log.infof("sleep %d ms...", interval);

			synchronized (schd) {
				try {
					// 等待一个固定的秒数
					if (interval > 0)
						schd.wait(interval);
					// 无限等待
					else
						schd.wait();
				}
				catch (InterruptedException e) {
					throw Lang.wrapThrow(e);
				}
			}
		}

		// 退出 ...
		if (log.isInfoEnabled())
			log.infof("down @ %s", Times.sDTms(Times.now()));
	}

	public abstract String name();

	/**
	 * @return 要等待的时间(秒)，小于等于0 表示无限等待
	 */
	protected abstract long exec();
	
	/**
	 * Ioc 容器接口
	 */
	protected Ioc ioc;

	/**
	 * 注入: 服务工厂接口
	 */
	protected ZTaskFactory factory;

	/**
	 * 构造函数: 日志接口
	 */
	protected Log log;

	public AbstractAtom() {
		this.log = Logs.getLog(this.getClass());
	}

}
