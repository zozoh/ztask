package org.nutz.ztask.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * zTask 的一些帮助函数和常量
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class ZTasks {

	public static final String REG_NOWORD = "[ \t\r\b\n~!@#$%^&*()+=`:{}|\\[\\]\\\\:\"';<>?,./-]";

	public static final String REG_D = "[0-9]{4}-[01][0-9]-[0-3][0-9][ ][0-2][0-9]:[0-5][0-9]:[0-5][0-9]";

	private static final DateFormat DF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final DateFormat DF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	private static final long MS_DAY = 3600 * 24 * 1000;
	private static final long MS_WEEK = MS_DAY * 7;

	/**
	 * TASK 的 stack 字段，什么值表示 null
	 */
	public static final String NULL_STACK = "--";

	/**
	 * 将一段 comment 文本进行包裹，在其开头加上 '@xxx:', 在其结尾加上时间戳
	 * <p>
	 * 比如:
	 * 
	 * <pre>
	 * 文本 :  "Hello"  包裹后将变成 "@zzh: Hello //2012-02-07 00:00:00"
	 * 文本 :  "@zzh: Hello"  包裹后将变成 "@zzh: Hello //2012-02-07 00:00:00"
	 * 文本 :  "@zzh: Hello //2011-12-07 06:12:00"  包裹后将变成 "@zzh: Hello //2012-02-07 00:00:00"
	 * </pre>
	 * 
	 * @param text
	 *            要被包裹的文本
	 * @param unm
	 *            用户，如果为 null，将不处理前缀
	 * @param d
	 *            时间戳，如果为 null，将不处理后缀
	 * @return 包裹后的字符串
	 */
	public static String wrapComment(String text, String unm, Date d) {
		// 处理空串
		text = Strings.sNull(Strings.trim(text), "");
		// 处理前缀
		if (!Strings.isBlank(unm)) {
			text = "@" + unm + ": " + text.replaceAll("^@[^ :,]+: ", "");
		}
		// 处理后缀
		if (null != d) {
			String ds = ZTasks.SDT(d);
			text = text.replaceAll("[ \t]?//[ \t]*" + REG_D + "$", "") + " //" + ds;
		}
		return text;
	}

	/**
	 * 用当前时间作为时间戳，包裹 comment 文本
	 * 
	 * @param text
	 *            要被包裹的文本
	 * @param unm
	 *            用户，如果为 null，将不处理前缀
	 * @return @return 包裹后的字符串
	 * @see org.nutz.ztask.util.ZTasks#wrapComment(String, String, Date)
	 */
	public static String wrapComment(String text, String unm) {
		return wrapComment(text, unm, now());
	}

	/**
	 * 以本周为基础获得某一周的时间范围
	 * 
	 * @param off
	 *            从本周偏移几周， 0 表示本周，-1 表示上一周，1 表示下一周
	 * 
	 * @return 时间范围(毫秒级别)
	 * 
	 * @see org.nutz.ztask.util.ZTasks#weeks(long, int, int)
	 */
	public static Date[] week(int off) {
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
	 * @see org.nutz.ztask.util.ZTasks#weeks(long, int, int)
	 */
	public static Date[] week(long base, int off) {
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
	 * @see org.nutz.ztask.util.ZTasks#weeks(long, int, int)
	 */
	public static Date[] weeks(int offL, int offR) {
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
	public static Date[] weeks(long base, int offL, int offR) {
		int from = Math.min(offL, offR);
		int len = Math.abs(offL - offR);
		// 现在
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(base);

		Date[] re = new Date[2];

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
	public static Date now() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 根据字符串得到时间
	 * 
	 * <pre>
	 * 如果你输入了格式为 "yyyy-MM-dd HH:mm:ss"
	 *    那么会匹配到秒
	 *    
	 * 如果你输入格式为 "yyyy-MM-dd"
	 *    相当于你输入了 "yyyy-MM-dd 00:00:00"
	 * </pre>
	 * 
	 * @param ds
	 *            时间字符串
	 * @return 时间
	 */
	public static Date D(String ds) {
		try {
			if (ds.length() < 12)
				return DF_DATE.parse(ds);
			return DF_DATE_TIME.parse(ds);
		}
		catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 根据毫秒数得到时间
	 * 
	 * @param ms
	 *            时间的毫秒数
	 * @return 时间
	 */
	public static Date D(long ms) {
		return new Date(ms);
	}

	/**
	 * 根据字符串得到时间
	 * 
	 * <pre>
	 * 如果你输入了格式为 "yyyy-MM-dd HH:mm:ss"
	 *    那么会匹配到秒
	 *    
	 * 如果你输入格式为 "yyyy-MM-dd"
	 *    相当于你输入了 "yyyy-MM-dd 00:00:00"
	 * </pre>
	 * 
	 * @param ds
	 *            时间字符串
	 * @return 时间
	 */
	public static Calendar C(String ds) {
		return C(D(ds));
	}

	/**
	 * 根据日期对象得到时间
	 * 
	 * @param d
	 *            时间对象
	 * @return 时间
	 */
	public static Calendar C(Date d) {
		return C(d.getTime());
	}

	/**
	 * 根据毫秒数得到时间
	 * 
	 * @param ms
	 *            时间的毫秒数
	 * @return 时间
	 */
	public static Calendar C(long ms) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ms);
		return c;
	}

	/**
	 * 根据时间得到字符串
	 * 
	 * @param d
	 *            日期时间对象
	 * @return 时间字符串 , 格式为 yyyy-MM-dd HH:mm:ss
	 */
	public static String SDT(Date d) {
		return DF_DATE_TIME.format(d);
	}

	/**
	 * 根据时间得到日期字符串
	 * 
	 * @param d
	 *            日期时间对象
	 * @return 时间字符串 , 格式为 yyyy-MM-dd
	 */
	public static String SD(Date d) {
		return DF_DATE.format(d);
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
