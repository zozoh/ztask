package org.nutz.ztask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * zTask 的一些帮助函数和常量
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class ZTasks {

	public static final String REG_NOWORD = "[ \t\r\b\n~!@#$%^&*()+=`:{}|\\[\\]\\\\:\"';<>?,./-]";

	private static final DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final long MS_DAY = 3600 * 24 * 1000;
	private static final long MS_WEEK = MS_DAY * 7;

	/**
	 * TASK 的 stack 字段，什么值表示 null
	 */
	public static final String NULL_STACK = "--";

	/**
	 * 以本周为基础获得某一周的时间范围
	 * 
	 * @param off
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 * 
	 * @see org.nutz.ztask.ZTasks#weeks(long, int, int)
	 */
	public static java.util.Date[] week(int off) {
		return week(System.currentTimeMillis(), off);
	}

	/**
	 * 以某周为基础获得某一周的时间范围
	 * 
	 * @param base
	 *            基础时间，毫秒
	 * @param off
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 * 
	 * @see org.nutz.ztask.ZTasks#weeks(long, int, int)
	 */
	public static java.util.Date[] week(long base, int off) {
		return weeks(base, off, off);
	}

	/**
	 * 以本周为基础获得时间范围
	 * 
	 * @param offL
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * @param offR
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 * 
	 * @see org.nutz.ztask.ZTasks#weeks(long, int, int)
	 */
	public static java.util.Date[] weeks(int offL, int offR) {
		return weeks(System.currentTimeMillis(), offL, offR);
	}

	/**
	 * 按周获得某几周周一 00:00:00 到周六 的时间范围
	 * <p>
	 * 它会根据给定的 offL 和 offR 得到一个时间范围
	 * <p>
	 * 对本函数来说 week(-3,-5) 和 week(-5,-3) 是一个意思
	 * 
	 * @param base
	 *            基础时间，毫秒
	 * @param offL
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * @param offR
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 */
	public static java.util.Date[] weeks(long base, int offL, int offR) {
		int from = Math.min(offL, offR);
		int len = Math.abs(offL - offR);
		// 现在
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(base);

		java.util.Date[] re = new java.util.Date[2];

		// 计算开始
		c.setTimeInMillis(c.getTimeInMillis() + MS_WEEK * from);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		re[0] = c.getTime();

		// 计算结束
		c.setTimeInMillis(c.getTimeInMillis() + MS_WEEK * (len + 1) - 1000);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		re[1] = c.getTime();

		// 返回
		return re;
	}

	/**
	 * @return 服务器当前时间
	 */
	public static java.util.Date now() {
		return new java.util.Date(System.currentTimeMillis());
	}

	/**
	 * 根据字符串得到时间
	 * 
	 * @param d
	 *            时间字符串, 格式为 yyyy-MM-dd HH:mm:ss
	 * @return 时间
	 */
	public static java.util.Date D(String d) {
		try {
			return date_format.parse(d);
		}
		catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据时间得到字符串
	 * 
	 * @param d
	 *            日期时间对象
	 * @return 时间字符串 , 格式为 yyyy-MM-dd HH:mm:ss
	 */
	public static String D(java.util.Date d) {
		return date_format.format(d);
	}

	/**
	 * 获取 zTask 项目的版本号，版本号的命名规范
	 * 
	 * <pre>
	 * [大版本号].[质量号].[发布流水号]
	 * </pre>
	 * 
	 * 这里有点说明
	 * <ul>
	 * <li>大版本号 - 表示使用方式的版本，如果没有重大变化，基本上同样的大版本号，使用方式是一致的
	 * <li>质量号 - 可能为 a: Alpha内部测试品质, b:Beta 公测品质, r:Release 最终发布版
	 * <li>发布流水 - 每次发布增加 1
	 * </ul>
	 * 
	 * @return zTask 项目的版本号
	 */
	public static String version() {
		return "1.a.1";
	}

	/**
	 * 判断一个堆栈名是否为空
	 * 
	 * @param stackName
	 *            堆栈名
	 * @return 是否是有效堆栈名
	 */
	public static boolean isBlankStack(String stackName) {
		return Strings.isBlank(stackName) || ZTasks.NULL_STACK.equals(stackName);
	}

}
