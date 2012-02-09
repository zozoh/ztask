package org.nutz.quartz;

import java.util.Calendar;

import org.nutz.lang.Lang;

/**
 * 判断周，它不支持 "L" 修饰符
 * <p>
 * 支持 "#" 第几个的方法:
 * <ul>
 * <li>如果有 "#"，则为值加上 100*$n，那么相当于最多加上 400
 * <li>如果值大于 100，则对 100 取摸，余数表示周几
 * <li>如果剩下的值除 100 得到 "#" 表示的值
 * </ul>
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QzItem_ww extends QzDateItem {

	protected boolean match(Calendar c) {
		// 忽略 ANY
		if (ANY == values[0])
			return true;

		// 得到周
		int ww = c.get(Calendar.DAY_OF_WEEK);

		// 准备判断数组
		int[] refs = null;

		// 特殊模式
		if (breakWeek) {
			int nowWeekN = c.get(Calendar.DAY_OF_MONTH) / 7 + 1;
			int v = values[1];
			int w = v % 100;
			int n = (v - w) / 100;
			// 周数不等
			if (n != nowWeekN)
				return false;
			// 周几数不等
			if (w != ww)
				return false;
			refs = new int[]{ONE, w};
		}

		// 普通模式
		return super._match_(ww, null == refs ? super.prepare(8) : refs);
	}

	/**
	 * 采用 "#" 的方式来判断
	 */
	private boolean breakWeek;

	@Override
	protected int eval4override(String str) {
		if (str.endsWith("L"))
			throw Lang.makeThrow("Week item don's support 'L' : '%s'", str);

		int n = 0;
		int pos = str.lastIndexOf("#");
		if (pos > 0) {
			if (str.indexOf('0') >= 0 || str.indexOf(',') >= 0) {
				throw Lang.makeThrow("Wrong week item '%s'!!!", str);
			}
			n = Integer.parseInt(str.substring(pos + 1));
			str = str.substring(0, pos);
			breakWeek = true;
		}
		int v = super.eval(str, QzItem.DAYS_OF_WEEK, 1);
		return v + 100 * n;
	}

}
