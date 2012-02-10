package org.nutz.ztask.api;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.quartz.Quartz;
import org.nutz.quartz.QzOverlapor;

/**
 * 封装了一个定时器。这个类是线程安全的
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TimerSchedule extends GlobalLock {

	private static final Log log = Logs.get();

	/**
	 * 根据当前时间运行某一个时间槽<br>
	 * 并返回从当前时间点开始算，经过多少毫秒，可以遇到下一个非空时间槽
	 * 
	 * @param now
	 *            当前时间
	 * @param log
	 *            日志接口，本函数将向其内写入，如果为 null，则用自己的 Log 接口
	 * @return 毫秒 ，它最小会返回 1 毫秒
	 */
	synchronized public long runSlot(final Calendar now, Log log) {
		final Log _log = (null == log ? TimerSchedule.log : log);
		/*
		 * 根据时间，得到现在的秒数
		 */
		int sec = now.get(Calendar.HOUR_OF_DAY)
					* 3600
					+ now.get(Calendar.MINUTE)
					* 60
					+ now.get(Calendar.SECOND);
		/*
		 * 确定一个时间槽
		 */
		int index = sec / this.sec_each_slot;

		if (_log.isDebugEnabled())
			_log.debugf("Run slots[%d] @%dsec", index, sec);

		// 保护一下
		if (null == slots)
			throw Lang.makeThrow("Null Slots");

		if (index < 0 || index >= slots.length)
			throw Lang.makeThrow("!!! Slot(%s) out-of-range, i=%d !!!", slots.length, index);

		final TimerSlot slot = slots[index];

		/*
		 * 准备 Timing，并在一个新线程里运行它
		 */
		if (null != slot) {
			threads.execute(new Runnable() {
				public void run() {
					slot.run(new Timering(now, context, _log));
				}
			});
		}
		// 否则打印一下调试信息
		else if (_log.isDebugEnabled()) {
			_log.debugf("slots[%d]==null, skip, sec=%d", index, sec);
		}

		/*
		 * 寻找下一个非空的时间槽
		 */
		int next = index + 1;
		for (; next < slots.length; next++) {
			if (slots[next] != null)
				break;
		}
		next = next < slots.length ? next : -1;
		if (_log.isDebugEnabled())
			_log.debugf("next slots: %d", next);

		// 将时间变成一个时间对象以便获得毫秒数
		// 如果没有，则相当于定位到 23:59:59
		String ds = Times.sD(now.getTime()) + " ";
		if (next < 0) {
			ds += "23:59:59";
		} else {
			ds += Times.sT(next * sec_each_slot);
		}
		Date d = Times.D(ds);

		// 返回差值，以便调用的线程，决定 wait 的时间
		return Math.max(1, d.getTime() - System.currentTimeMillis());
	}

	/**
	 * 叠加完成后的操作，这里会按照叠加的结果，初始化时间槽
	 * <p>
	 * 这个函数执行完毕后，整个时间表就可工作了
	 * 
	 * @return 多少个时间槽是可执行的
	 */
	synchronized public int ready() {
		int count = 0;

		for (int i = 0; i < overlapors.length; i++) {
			QzOverlapor qo = overlapors[i];

			// 空时间槽
			if (null == qo || qo.isEmpty())
				continue;

			// 转换成执行器
			String[] handlerNames = qo.toArray(String.class);
			slots[i] = new TimerSlot(ioc, handlerNames);
			count++;
		}
		// 打印粗略的调试信息
		if (log.isInfoEnabled())
			log.infof(	"ready %d slots @ %s in %dms",
						count,
						Times.sDTms(time),
						System.currentTimeMillis() - time.getTime());

		// 更详细的调试信息
		if (log.isDebugEnabled()) {
			log.debug(this.toString());
		}

		// 标志完成
		_done_ = true;

		// 返回计数
		return count;
	}

	/**
	 * 向时间槽叠加一个 Quartz 表达式对应的定时处理器.
	 * <p>
	 * 如果不能匹配今天的日期(this.time)，那么将跳过叠加
	 * 
	 * @param qz
	 *            Quartz 表达式，用来声明时间点
	 * @param handlerName
	 *            定时处理器
	 */
	synchronized public void overlap(Quartz qz, String handlerName) {
		qz.overlap(overlapors, handlerName, Times.C(time));
	}

	/**
	 * 新建一个时间槽数组，同时标定时间一个时间戳
	 */
	synchronized public void reset() {
		slots = new TimerSlot[86400 / sec_each_slot];
		overlapors = new QzOverlapor[slots.length];
		context.clear();
		time = Times.now();
		_done_ = false;
		if (log.isDebugEnabled())
			log.debugf("Reset slots @ %s", Times.sDTms(time));
	}

	/**
	 * 关闭线程池，以及通知所有的后台线程停止
	 */
	synchronized public void stop() {
		threads.shutdown();
		_done_ = true;
		this.setStop(true);
		synchronized (this) {
			this.notifyAll();
		}
	}

	/**
	 * 复写，以便提供一个 dump 自身内容的方法
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getName());
		sb.append("\n$ioc: ").append(ioc.toString());
		sb.append("\n#_done: ").append(_done_);
		sb.append("\n#thread-pool-size: ").append(this.thread_pool_size);
		sb.append("\n#time: ").append(Times.sDTms(time));
		sb.append("\n#threads: ").append(threads.getClass().getName());
		sb.append("\n");
		sb.append("\n==================================== Context =");
		if (context.keys().isEmpty())
			sb.append("\n\n   - EMPTY -\n");
		else {
			int i = 0;
			for (String key : context.keys())
				sb.append("\n")
					.append(Strings.alignRight(i++, 3, ' '))
					.append(". ")
					.append(key)
					.append(" = ")
					.append(context.get(key));
		}
		sb.append("\n");
		sb.append("\n===================================== Slots =");
		sb.append("\n#sec_each_slot: ").append(sec_each_slot);
		int count = 0;
		for (int i = 0; i < slots.length; i++) {
			if (null == slots[i])
				continue;
			sb.append('\n').append(Strings.alignRight(i, 6, ' ')).append(") ");
			sb.append(Times.sT(i * sec_each_slot)).append(": ");
			sb.append(slots[i].toString());
			count++;
		}
		sb.append("\n ").append(count).append(" slots in active!");
		sb.append("\n");
		sb.append("\n================== Over ======================");

		return sb.toString();
	}

	public TimerSchedule() {
		context = new SimpleContext();
		sec_each_slot = 60 * 60; // 默认为一个小时
		threads = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()
												* (thread_pool_size <= 0 ? 10 : thread_pool_size));
		_done_ = false;
	}

	private boolean _done_;

	/**
	 * @return 本 scheudle 是否准备完毕
	 */
	public boolean isReady() {
		return _done_;
	}

	/**
	 * 临时: 一个与时间槽长度相同的数组，用来叠加 handlers，它与时间槽一同分配
	 */
	private transient QzOverlapor[] overlapors;

	/**
	 * 私有: 执行器时间槽，全天 24 小时均分
	 */
	private TimerSlot[] slots;

	/**
	 * 注入: 一个时间槽对应多少秒
	 */
	private int sec_each_slot;

	/**
	 * 注入: Ioc 容器
	 */
	private Ioc ioc;

	/**
	 * 注入: 线程池大小，默认为10
	 */
	private int thread_pool_size;

	/**
	 * 构造函数:生成线程池
	 */
	private ExecutorService threads;

	/**
	 * 私有:本计划生成的时间
	 */
	private Date time;

	/**
	 * 共享:本 schedule 声明周期内的一个上下文对象，以便所有的定时器跨越时间共享一些数据
	 */
	private Context context;

}
