package org.nutz.ztask.thread;

import java.util.Calendar;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 本原子，每次唤醒，都去运行 schedule, <br>
 * schedule 负责为每个 handle 启动新线程运行，以便本原子运行时间精确
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TimerRunnerAtom extends AbstractAtom {

	private final static Log log = Logs.get();

	@Override
	protected long exec() {
		// 首先以间隔 1000 ms 的时间，自旋等待 schedule 的准备完成
		while (!factory.schedule().isReady() && !factory.schedule().isStop()) {

			if (log.isDebugEnabled())
				log.debug("check schedule ...");

			synchronized (factory.schedule()) {
				try {
					factory.schedule().wait(1000);
				}
				catch (InterruptedException e) {
					throw Lang.wrapThrow(e);
				}
			}
		}

		return factory.schedule().runSlot(Calendar.getInstance(), log);
	}

	public static final String NAME = "SCHD.run";

	@Override
	public String getName() {
		return NAME;
	}
}
