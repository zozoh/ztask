package org.nutz.ztask.api;

import java.util.Calendar;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

/**
 * 封装执行一个时间槽的操作（一组 TimerHandler）
 * <p>
 * 一般的说，有两个种操作
 * <ul>
 * <li>未执行 - 里面带了一组 TimerHandler，用来顺序执行
 * <li>已执行 - 仅仅用作一个占位，它保存了自己 TimerHandler 的执行结果
 * </ul>
 * 它的内存结构为一个标识符和二维数组
 * 
 * <pre>
 * doneAt = null;
 * +----------+--------------------+
 * | aaa      |  AAATimerHandler   |
 * +----------+--------------------+
 * | bbb      |  BBBTimerHandler   |
 * +----------+--------------------+
 * </pre>
 * 
 * 执行完毕后，将变成
 * 
 * <pre>
 * doneAt = "yyyy-MM-dd HH:mm:ss.ms"
 * +----------+--------------------+
 * | aaa      | "OK"               |
 * +----------+--------------------+
 * | bbb      | "OK:SenT 6 mails"  |
 * +----------+--------------------+
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TimerSlot {

	/**
	 * 执行自己所有的 TimerHandler，之后将自己设置
	 * 
	 * @param ing
	 *            执行器上下文
	 */
	public void run(Timering ing) {
		if (ing.log().isDebugEnabled())
			ing.log().debugf("@@ run %d handlers", Lang.length(rows));

		// 运行自己所有的 Timer
		for (int i = 0; i < rows.length; i++) {
			Object[] row = rows[i];
			Object o = row[1];
			if (null != o && o instanceof TimerHandler) {
				row[1] = ((TimerHandler) o).doHandle(row[0].toString(), ing);
				if (ing.log().isDebugEnabled())
					ing.log().debugf("   @%s> exec => %s", row[0], row[1]);
			} else {
				if (ing.log().isDebugEnabled())
					ing.log().debugf("   ~skip %s for null or no TimerHandler", row[0]);
			}
		}
		// 标记完成时间
		doneAt = Calendar.getInstance();
	}

	public String toString() {
		if (null == rows)
			return "NULL";

		if (0 == rows.length)
			return "EMPTY";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows.length; i++) {
			if (i > 0)
				sb.append(", ");
			Object[] row = rows[i];
			sb.append('"').append(row[0]).append(':');
			Object o = row[1];
			if (o == null || o instanceof CharSequence) {
				sb.append(Strings.alignLeft(Times.sDTms(doneAt.getTime()), 21, ' '));
				if (null != o)
					sb.append(" [").append(o).append("]");
			} else {
				sb.append("..").append(o.getClass().getSimpleName());
			}
		}
		return sb.toString();
	}

	/**
	 * 根据处理器名称，从 Ioc 容器中初始化自己的执行序列
	 * 
	 * @param ioc
	 *            Ioc 容器
	 * @param handlerNames
	 *            处理器名称列表
	 */
	public TimerSlot(Ioc ioc, String[] handlerNames) {
		rows = new Object[handlerNames.length][];
		for (int i = 0; i < handlerNames.length; i++) {
			rows[i] = new Object[2];
			rows[i][0] = handlerNames[i];
			rows[i][1] = ioc.get(TimerHandler.class, handlerNames[i]);
		}
	}

	private Calendar doneAt;

	/**
	 * nx2 的二维数组,
	 */
	private Object[][] rows;
}
