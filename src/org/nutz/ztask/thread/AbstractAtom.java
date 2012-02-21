package org.nutz.ztask.thread;

import java.util.Date;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;
import org.nutz.ztask.api.GlobalLock;
import org.nutz.ztask.api.ZTaskFactory;

public abstract class AbstractAtom implements Atom {

	/**
	 * 累积启动次数
	 */
	private int runCount;

	/**
	 * 上次启动于
	 */
	private Date upAt;

	/**
	 * 上次睡眠于
	 */
	private Date sleepAt;

	/**
	 * 本次睡眠时间
	 */
	private long sleepTime;

	/**
	 * @return 当前线程运行状态信息
	 */
	public String getRuntimeInfo() {
		String willUpAt = null == sleepAt	? " ?? "
											: (sleepTime <= 0	? " -wait notify- "
																: Times.sDTms(Times.D(sleepAt.getTime()
																						+ sleepTime)));
		long du = null == sleepAt ? -1 : sleepAt.getTime() - upAt.getTime();
		return String.format(	"[%s:%d] wait: %d ms, will up @ (%s), last-du:%dms (%s - %s)",
								getName(),
								runCount,
								sleepTime,
								willUpAt,
								du,
								Times.sDTms(upAt),
								Times.sDTms(sleepAt));
	}

	@Override
	public void run() {

		// 主锁
		GlobalLock lock = this.getMyLock();

		while (!lock.isStop()) {
			// 唤醒 ...
			// @记录:启动时间
			this.upAt = Times.now();

			if (log.isInfoEnabled())
				log.infof("up @ %s", Times.sDTms(upAt));

			// 运行
			try {
				sleepTime = exec();
			}
			// 确保不会崩溃
			catch (Throwable e) {
				if (log.isWarnEnabled())
					log.warn("Some error happend!", e);
			}

			// 保证间隔时间不少于 1 秒
			if (sleepTime > 0)
				sleepTime = Math.max(1000, sleepTime);

			// @记录:睡眠前 ...
			this.sleepAt = Times.now();
			this.runCount++;

			// 睡眠 ...
			if (log.isInfoEnabled())
				log.infof("sleep(%d) %d ms @ %s...", runCount, sleepTime, Times.sDTms(sleepAt));

			// 睡眠
			synchronized (lock) {
				try {
					// 等待一个固定的秒数
					if (sleepTime > 0)
						lock.wait(sleepTime);
					// 无限等待
					else
						lock.wait();
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

	/**
	 * 返回线程同步/通知锁。子类可以重载它，给出自己的新锁
	 * 
	 * @return 线程同步/通知锁
	 */
	public GlobalLock getMyLock() {
		return factory.schedule();
	}

	/**
	 * @return 是否首次启动
	 */
	public boolean isFirstUp() {
		return null == this.sleepAt;
	}

	public abstract String getName();

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

	/**
	 * @return 自己简要的描述，子类可以给出更多的秒数
	 */
	public String toString() {
		return getRuntimeInfo();
	}

	public AbstractAtom() {
		this.log = Logs.getLog(this.getClass());
	}

}
