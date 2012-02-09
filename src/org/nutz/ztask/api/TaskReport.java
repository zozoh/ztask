package org.nutz.ztask.api;

import java.util.Calendar;
import java.util.Date;

/**
 * 封装了一个报告的详细信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface TaskReport {

	/**
	 * 每个报告都有一个代表日期，比如周报，可定在周五，月报，定在月末等
	 * 
	 * @return 报告的日期
	 */
	public Calendar getDate();

	/**
	 * @return 报告得简名
	 */
	public String getShortName();

	/**
	 * 报告的长名，应该保证唯一性
	 * 
	 * @return 报告完整名称
	 */
	public String getFullName();

	/**
	 * @return 报告最后修改时间
	 */
	public Date getLastModified();

	/**
	 * @return 报告内容的简要描述，null 表示没有简要描述
	 */
	public String getBrief();

}
