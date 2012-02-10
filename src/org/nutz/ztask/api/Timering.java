package org.nutz.ztask.api;

import java.util.Calendar;

import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 一个定时器一次运行的上下文
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Timering extends SimpleContext {

	private Context schedulerContext;

	private Calendar now;

	private Log log;

	public Timering(Calendar now, Context schedulerContext, Log log) {
		this.schedulerContext = schedulerContext;
		this.now = now;
		this.log = (log == null ? Logs.getLog(TimerSchedule.class) : log);
	}

	public Context getSchedulerContext() {
		return schedulerContext;
	}

	public Calendar now() {
		return now;
	}

	public Log log() {
		return log;
	}

}
