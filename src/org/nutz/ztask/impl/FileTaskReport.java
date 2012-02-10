package org.nutz.ztask.impl;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.ztask.api.TaskReport;

/**
 * 根据一个文件对象（只能是文件）包裹成一个报告对象
 * 
 * <pre>
 * 文件名格式应该类似:
 * 
 *      /home/abc/somedir/a/b/2012/w05_09-23.txt
 *      
 * 即，根据 "/yyyy/MM-dd.txt" 格式结尾
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FileTaskReport implements TaskReport {

	private static final Pattern NM = Pattern.compile("(/)"
														+ "([0-9]{4})"
														+ "(/)"
														+ "(w[0-5][0-9])"
														+ "(_)"
														+ "([01][0-9]-[0-3][0-9])"

														+ "([.]txt$)");

	/**
	 * 根据时间生成一个虚文档
	 * 
	 * @param c
	 *            时间
	 */
	public FileTaskReport(Calendar c) {
		date = c;
		lastModified = Times.now();
		shortName = "No tasks";
		fullName = "No taks at " + Times.sD(Times.D(c.getTimeInMillis()));
	}

	public FileTaskReport(File f) {
		if (null == f || !f.isFile() || !f.exists())
			throw Lang.makeThrow("FileReport can only accept file '%s'", f);

		Matcher m = NM.matcher(f.getAbsolutePath().replace('\\', '/'));
		if (!m.find()) {
			throw Lang.makeThrow("FileReport wrong file name format '%s'", f);
		}

		// 文件最后修改时间就是 report 的 lm
		lastModified = Times.D(f.lastModified());

		// 根据文件名，得到日期
		String ds = m.group(2) + "-" + m.group(6);
		date = Times.C(ds);

		// 根据文件名得到 ...
		fullName = m.group(2) + "." + m.group(4) + "." + m.group(6);
		shortName = m.group(4) + "." + m.group(6);
	}

	private Calendar date;

	private String shortName;

	private String fullName;

	private Date lastModified;

	private String brief;

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getShortName() {
		return shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public Date getLastModified() {
		return lastModified;
	}

	@Override
	public Calendar getDate() {
		return date;
	}

}
