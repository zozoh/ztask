package org.nutz.ztask.thread;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Callback3;
import org.nutz.quartz.Quartz;
import org.nutz.ztask.api.GInfo;

/**
 * 本原子，将自动更新定时器时间表。事实上，它一运行就干这个事儿
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ScheduleUpdateAtom extends AbstractAtom {

	@Override
	protected long exec() {
		// 得到信息
		GInfo info = factory.htasks().getGlobalInfo();

		if (log.isDebugEnabled())
			log.debug("Reset schedule ...");
		factory.schedule().reset();

		// 开始更新
		info.eachTimer(ioc, new Callback3<Integer, Quartz, String[]>() {
			public void invoke(Integer index, Quartz qz, String[] handlerNames) {
				// 迭代所有的表达式
				for (String handlerName : handlerNames)
					factory.schedule().overlap(qz, handlerName);
				// 打印日志
				if (log.isDebugEnabled())
					log.debugf(	"  @SET[%d]: %s :: (%d)'%s' ",
								index,
								qz,
								handlerNames.length,
								Lang.concat(", ", handlerNames));
			}
		});

		// 成功
		if (log.isDebugEnabled())
			log.debug("... done");
		factory.schedule().ready();

		// 最后通知一下运行进程
		synchronized (factory.schedule()) {
			factory.schedule().notifyAll();
		}

		// 然后无限等待
		return 0;
	}

	public static final String NAME = "SCHD.update";

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("\n").append(factory.schedule().toString());
		sb.append("\n------------------ The End ----------------------\n");
		return sb.toString();
	}

	@Override
	public String name() {
		return NAME;
	}

}
