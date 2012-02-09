package org.nutz.quartz;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 封装 Quartz 表达式的解析，和解释
 * <ul>
 * <li>解析 : 将字符串格式化
 * <li>解释 : 根据解释结果，填充一个执行数组
 * </ul>
 * 
 * <h4>关于 Quartz 的简要说明</h4>
 * 
 * <pre>
 * 表达式是一个字符串，它有六个子表达式构成。这些子表达式用空格来分隔。 
 *  # 子表达式描述如下： 
 *     0) 秒（0~59） 
 *     1) 分钟（0~59） 
 *     2) 小时（0~23） 
 *     3) 天（月）（1~31，但是你需要考虑你月的天数） 
 *     4) 月（1~12） 
 *     5) 天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
 *  # 特殊符号
 *     '-' : 范围， 比如在子表达式（月），"1-4" 表示 2月到5月
 *     "," : 列表分隔, 比如在子表达式（天－星期），"1,3" 表示 周日和周二
 *     "*" : 代表所有可能的值
 *     "/" : 用来指定数值的增量
 *            > 例如: 在子表达式（分钟）里的 "0/15" 表示从第0分钟开始，每15分钟
 *            > 又如: 在子表达式（分钟）里的 "3/20" 表示从第3分钟开始，每20分钟
 *                   （它和 "3，23，43" ）的含义一样 
 *     "?" : 仅被用于天（月）和天（星期）两个子表达式，表示不指定值 
 *     "L" : 仅被用于天（月）和天（星期）两个子表达式，它是单词“last”的缩写
 *            > 在天（月）子表达式中，“L”表示一个月的最后一天
 *                >> 6L”表示这个月的倒数第６天
 *            > 在天（星期）自表达式中，“L”表示一个星期的最后一天，也就是SAT
 *                >> “FRIL”表示这个月的最一个星期五
 *     "W" : 仅被用于天（月）子表达式，表示工作日
 *            > "W" 为所有工作日
 *            > "4W" 为距离本月第5日最近的工作日
 *            > "4LW" 为距离当月倒数第4日最近的工作日
 *     "#" : 仅用在天（星期）表示第几个
 *            > 3#1 表示 第1个周二
 *            > FRI#2 第2个周五
 * 
 * 
 * </pre>
 * 
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
// TODO 还未支持年
public class Quartz {

	public static Quartz NEW(String qzs) {
		Quartz qs = NEW();
		qs.valueOf(qzs);
		return qs;
	}

	public static Quartz NEW() {
		return new Quartz();
	}

	/**
	 * 让数组更紧凑
	 * <p>
	 * 这个函数可以配合 fill 来使用， fill 过的数组有些元素为 null<br>
	 * 为了能紧凑显示，本函数去掉所有 null 元素，但是同时数组的下标信息就丢失了<br>
	 * 如果不想丢失下标信息并且还想紧凑表达 请用 compactAll
	 * 
	 * @param <T>
	 * @param array
	 *            Quartz 填充的数组
	 * @return 紧凑的数组（下标信息丢失）
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] compact(T[] array) {
		ArrayList<T> list = new ArrayList<T>(array.length);
		for (T ele : array)
			if (null != ele)
				list.add(ele);
		T[] re = (T[]) Array.newInstance(array.getClass().getComponentType(), list.size());
		return list.toArray(re);
	}

	/**
	 * 让数组更紧凑，并保留下标信息
	 * 
	 * @param <T>
	 * @param array
	 *            Quartz 填充的数组
	 * @return 紧凑的列表（下标信息保留）
	 */
	public static <T> ArrayList<QzObj<T>> compactAll(T[] array) {
		ArrayList<QzObj<T>> list = new ArrayList<QzObj<T>>(array.length);
		for (int i = 0; i < array.length; i++)
			if (null != array[i])
				list.add(new QzObj<T>(array[i], i));
		list.trimToSize();
		return list;
	}

	/*-------------------------------子表达式下标的含义----*/
	private QzItem iss;
	private QzItem imm;
	private QzItem iHH;
	private QzDateItem idd;
	private QzDateItem iMM;
	private QzDateItem iww;

	/*---------------------------------------构造函数----*/
	public Quartz() {
		iss = new QzItem();
		imm = new QzItem();
		iHH = new QzItem();
		idd = new QzItem_dd();
		iMM = new QzItem_MM();
		iww = new QzItem_ww();
	}

	/**
	 * 根据字符串，重新解析一个 Quartz 表达式
	 * 
	 * @param qzs
	 *            Quartz 表达式字符串
	 * @return 自身以便链式使用
	 */
	public Quartz valueOf(String qzs) {
		// 拆
		String[] ss = Strings.splitIgnoreBlank(qzs, "[ ]");
		// 验证
		if (6 != ss.length)
			throw Lang.makeThrow("Wrong format '%s': expect %d items but %d", qzs, 6, ss.length);
		// 解析子表达式
		iss.valueOf(ss[0]);
		imm.valueOf(ss[1]);
		iHH.valueOf(ss[2]);
		idd.valueOf(ss[3]);
		iMM.valueOf(ss[4]);
		iww.valueOf(ss[5]);
		// 返回
		return this;
	}

	/**
	 * 是否匹配一个日期
	 * 
	 * @param c
	 *            日期对象，时间部分无视
	 * @return 是否匹配
	 */
	public boolean matchDate(Calendar c) {
		if (!idd.match(c))
			return false;

		if (!iMM.match(c))
			return false;

		if (!iww.match(c))
			return false;

		return true;
	}

	/**
	 * 是否匹配一个日期
	 * 
	 * @param ds
	 *            日期字符串，格式为 yyyy-MM-dd 的字符串
	 * @return 是否匹配
	 */
	public boolean matchDate(String ds) {
		return matchDate(_C_(ds));
	}

	/**
	 * 是否匹配一个日期
	 * 
	 * @param d
	 *            日期
	 * @return 是否匹配
	 */
	public boolean matchDate(Date d) {
		return matchDate(_C_(d.getTime()));
	}

	/**
	 * 根据给定的秒数，判断是否匹配本表达式
	 * 
	 * @param sec
	 *            一天中的秒数，为 0-86399，如果超出，按 86399，如果小于，按0
	 * @return 是否匹配
	 */
	public boolean matchTime(int sec) {
		int HH = sec / 3600;
		if (!iHH.match(HH, 0, 24))
			return false;

		int mm = (sec - (HH * 3600)) / 60;
		if (!imm.match(mm, 0, 60))
			return false;

		int ss = sec - (HH * 3600) - (mm * 60);
		if (!iss.match(ss, 0, 60))
			return false;

		return true;
	}

	/**
	 * 根据给定的时间字符串，判断是否匹配本表达式
	 * 
	 * @param ts
	 *            时间字符串，格式为 HH:mm:ss
	 * @return 是否匹配
	 */
	public boolean matchTime(String ts) {
		String[] ss = ts.split(":");
		int sec = Integer.parseInt(ss[2]);
		sec += Integer.parseInt(ss[1]) * 60;
		sec += Integer.parseInt(ss[0]) * 3600;
		return matchTime(sec);
	}

	/**
	 * 本函数用来填充数组
	 * <p>
	 * 如果给定的日期，不能匹配，则跳过执行，即，本函数执行的是填充，如果匹配上了就填充，否则无视 <br>
	 * 这样，你可以很容易叠加多个 Quartz 表达式的执行结果
	 * <p>
	 * 这里涉及到一个时间的缩放问题，Quartz 实际上是声明了一天中的一系列启动点<br>
	 * 这些点，我们可以用秒来表示，从 0－86399 分别表示一天中的任何一秒。 <br>
	 * <b style="color:red">这里，给定的数组的长度最好能把 86400 整除 否则一天中最后一段时间会被忽略掉</b>
	 * <p>
	 * 根据数组的长度，我就能知道，你所关心的 Quartz 表达式精细程度，<br>
	 * 比如 如果长度为 24 则，你其实仅仅关心到一个小时，如果 1440 你仅仅关系到1分钟
	 * <p>
	 * 同时，你可以自由的定义，比如你给定一个 400 长度的数组，那么 86400/400=216。<br>
	 * 因此，对你来说，时间的单位是 216 秒。
	 * <p>
	 * 本函数，执行的策略
	 * <ol>
	 * <li>得到时间单位
	 * <li>然后，循环数组，根据下标，我们能得到一个秒数范围
	 * <li>这个范围内，如果任意一秒能被匹配，就算匹配成功
	 * </ol>
	 * <p>
	 * 
	 * @param <T>
	 * @param array
	 *            要被填充的数组
	 * @param obj
	 *            填充的对象
	 * @param c
	 *            日期对象，时间部分无视
	 * @return 数组本身以便链式赋值
	 */
	public <T> T[] fill(T[] array, T obj, Calendar c) {
		// 如果日期不匹配，无视
		if (!matchDate(c))
			return array;

		// 根据数组，获得一个数组元素表示多少秒
		int unit = 86400 / array.length;

		// 循环数组
		for (int i = 0; i < array.length; i++) {
			int sec = i * unit;
			int max = sec + unit;
			// 循环每个数组元素
			for (; sec < max; sec++) {
				if (this.matchTime(sec)) {
					array[i] = obj;
					break;
				}
			}
		}

		// 返回
		return array;
	}

	/**
	 * @see org.nutz.quartz.Quartz#fillBy(Object[], Object, long)
	 */
	public <T> T[] fillByToday(T[] array, T obj) {
		return fill(array, obj, Calendar.getInstance());
	}

	/**
	 * 根据时间的毫秒数填充数组
	 * 
	 * @param <T>
	 * @param array
	 *            要被填充的数组
	 * @param obj
	 *            填充的对象
	 * @param ms
	 *            毫秒数，但是仅仅其中的天这部分有意义
	 * @return 数组本身以便链式赋值
	 * @see org.nutz.quartz.Quartz#fill(Object[], Object, int, int, int)
	 */
	public <T> T[] fillBy(T[] array, T obj, long ms) {
		return fill(array, obj, _C_(ms));
	}

	/**
	 * 根据时间的毫秒数填充数组
	 * 
	 * @param <T>
	 * @param array
	 *            要被填充的数组
	 * @param obj
	 *            填充的对象
	 * @param ds
	 *            日期字符串，格式为 yyyy-MM-dd 的字符串
	 * @return 数组本身以便链式赋值
	 * @see org.nutz.quartz.Quartz#fillBy(Object[], Object, long)
	 */
	public <T> T[] fillBy(T[] array, T obj, String ds) {
		return fill(array, obj, _C_(ds));
	}

	/**
	 * 根据时间的毫秒数填充数组
	 * 
	 * @param <T>
	 * @param array
	 *            要被填充的数组
	 * @param obj
	 *            填充的对象
	 * @param d
	 *            时间，但是仅仅其中的天这部分有意义
	 * @return 数组本身以便链式赋值
	 * @see org.nutz.quartz.Quartz#fillBy(Object[], Object, long)
	 */
	public <T> T[] fillBy(T[] array, T obj, Date d) {
		return fill(array, obj, _C_(d.getTime()));
	}

	private static Calendar _C_(String ds) {
		try {
			Date d = DF.parse(ds);
			return _C_(d.getTime());
		}
		catch (ParseException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private static Calendar _C_(long ms) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ms);
		return c;
	}

	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
}
