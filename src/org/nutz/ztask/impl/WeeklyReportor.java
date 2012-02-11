package org.nutz.ztask.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.nutz.doc.DocRender;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZD;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.Disks;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskQuery;
import org.nutz.ztask.api.TaskReport;
import org.nutz.ztask.api.ZTaskReportor;
import org.nutz.ztask.api.ZTaskFactory;
import org.nutz.ztask.util.KeyGetter;
import org.nutz.ztask.util.ReportMap;

public class WeeklyReportor implements ZTaskReportor {

	/**
	 * 注入: 根据它来得到数据库名称
	 */
	private String dbName;

	/**
	 * 注入: 服务工厂接口
	 */
	private ZTaskFactory factory;

	/**
	 * 注入: 用周几那一天，表示一周
	 * 
	 * 默认为 "MONDAY"
	 * 
	 * @see java.util.Calendar#SUNDAY
	 * @see java.util.Calendar#MONDAY
	 * @see java.util.Calendar#TUESDAY
	 * @see java.util.Calendar#WEDNESDAY
	 * @see java.util.Calendar#THURSDAY
	 * @see java.util.Calendar#FRIDAY
	 * @see java.util.Calendar#SATURDAY
	 */
	private int reportDay;

	/**
	 * 注入: 用什么方式渲染报告
	 */
	private DocRender<? extends Object> render;

	public WeeklyReportor() {
		reportDay = Calendar.MONDAY;
	}

	/**
	 * 注入: 周报文件的根目录，里面的路径格式为：
	 * 
	 * <pre>
	 * $HOME / 数据库名 / 年 / w周(年) _ 月 - 日.txt
	 * </pre>
	 */
	private String home;

	public void setHome(String home) {
		this.home = Disks.normalize(home);
	}

	@Override
	public InputStream getInputStream(TaskReport rpt) {
		if (null == rpt)
			return Lang.ins("Nothing happend this week! -_-!");
		File f = getFile(rpt.getDate().getTimeInMillis());
		if (!f.exists())
			return Lang.ins("Nothing happend this week! -_-!");
		return Streams.fileIn(f);
	}

	@Override
	public void writeAndClose(TaskReport rpt, OutputStream ops) {
		if (null == rpt)
			return;
		InputStream ins = getInputStream(rpt);
		Streams.writeAndClose(ops, ins);
	}

	@Override
	public TaskReport get(Calendar c) {
		File f = this.getFile(c.getTimeInMillis());
		return null != f && f.exists() ? new FileTaskReport(f) : null;
	}

	@Override
	public List<TaskReport> getBy(Calendar from, Calendar to) {
		return this.getReportIn(from.getTimeInMillis(), to.getTimeInMillis());
	}

	@Override
	public TaskReport make(Calendar c) {
		// 得到报告文件
		File f = getFile(c.getTimeInMillis());
		try {
			f = Files.createFileIfNoExists(f.getAbsolutePath());
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		// 进行准备查询
		float toYear = Calendar.getInstance().get(Calendar.YEAR);
		float toWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

		float rptYear = c.get(Calendar.YEAR);
		float rptWeek = c.get(Calendar.WEEK_OF_YEAR);

		float wwOfYear = 365 / 7;

		int wwOffset = (int) ((rptYear * wwOfYear + rptWeek) - (toYear * wwOfYear + toWeek));

		// 完成的任务
		List<Task> dones = factory.htasks().queryTasks(TaskQuery.NEWf("&W(%s) %%(DONE)", wwOffset)
																.asc()
																.sortBy("popAt"));

		// 开始分配的任务
		List<Task> pushs = factory.htasks().queryTasks(TaskQuery.NEWf(	"&W(%s) %%(ING,HUNGUP)",
																		wwOffset)
																.asc()
																.sortBy("pushAt"));

		// 新创建的任务
		List<Task> news = factory.htasks().queryTasks(TaskQuery.NEWf("&W(%s) %%(NEW)", wwOffset)
																.asc()
																.sortBy("createTime"));

		KeyGetter<Task> keyg = new KeyGetter<Task>() {
			public String getKey(Task task) {
				return "@" + task.getOwner();
			}
		};

		// 汇总统计
		ReportMap ok = new ReportMap();
		ok.add(dones, keyg);

		ReportMap ingOrHungup = new ReportMap();
		ingOrHungup.add(pushs, keyg);

		ReportMap newOrReject = new ReportMap();
		newOrReject.add(news, keyg);

		// 没有任务
		if ((ok.isEmpty() && ingOrHungup.isEmpty() && newOrReject.isEmpty())) {
			Files.deleteFile(f);
			return new FileTaskReport(c);
		}
		// 有任务
		// 生成文档
		ZDoc doc = new ZDoc();
		doc.addAuthor("weekly-reportor");
		doc.setTime(Times.now());

		doc.root().add(ZD.hr());
		ZBlock p = ZD.p("This week, we finished:");
		ok.joinTo(p);
		doc.root().add(p);

		doc.root().add(ZD.hr());
		p = ZD.p("Next week, we will do:");
		ingOrHungup.joinTo(p);
		doc.root().add(p);

		doc.root().add(ZD.hr());
		p = ZD.p("Some new tasks been created at this week also:");
		newOrReject.joinTo(p);
		doc.root().add(p);

		// 写入
		Object obj = render.render(doc);
		Files.write(f, obj);

		// 返回
		return new FileTaskReport(f);
	}

	@Override
	public TaskReport makeIfNoExists(Calendar c) {
		TaskReport trp = get(c);
		if (null == trp)
			trp = make(c);
		return trp;
	}

	@Override
	public TaskReport drop(Calendar c) {
		TaskReport rpt = this.get(c);
		File f = this.getFile(rpt.getDate().getTimeInMillis());
		if (null != f && f.exists()) {
			Files.deleteFile(f);
		}
		return rpt;
	}

	@Override
	public List<TaskReport> dropBy(Calendar from, Calendar to) {
		List<TaskReport> rpts = getReportIn(from.getTimeInMillis(), to.getTimeInMillis());
		for (TaskReport rpt : rpts)
			drop(rpt.getDate());
		return rpts;
	}

	/**
	 * 得到一个时间范围内，所有的报告
	 * 
	 * @param from
	 *            开始
	 * @param to
	 *            结束
	 * @return 文件列表 （列表中的文件，都是存在的）
	 */
	private List<TaskReport> getReportIn(long from, long to) {
		List<File> fs = getFilesIn(from, to);
		List<TaskReport> rpts = new ArrayList<TaskReport>(fs.size());
		for (File f : fs)
			rpts.add(new FileTaskReport(f));
		return rpts;
	}

	/**
	 * 得到一个时间范围内，所有的报告文件
	 * 
	 * @param from
	 *            开始
	 * @param to
	 *            结束
	 * @return 文件列表 （列表中的文件，都是存在的）
	 */
	private List<File> getFilesIn(long from, long to) {
		long min = Math.min(from, to);
		long max = Math.max(from, to);

		List<File> fs = new LinkedList<File>();

		long weekMS = 3600 * 24 * 7 * 1000;
		for (long ms = min; ms <= max; ms += weekMS) {
			File f = getFile(ms);
			if (f.exists())
				fs.add(f);
		}

		return fs;
	}

	/**
	 * 根据日期，得到文件对象
	 * 
	 * @param ms
	 *            毫秒数
	 * @return 文件对象
	 */
	private File getFile(long ms) {
		Calendar c = Times.C(ms);
		// 转换成周五 (可配置)
		c.set(Calendar.DAY_OF_WEEK, reportDay);
		// 得到 "/yyyy/ww_MM-dd.txt"
		String path = String.format("/%s/%s/w%s_%s-%s.txt",
									dbName,
									Strings.alignRight("" + c.get(Calendar.YEAR), 4, '0'),
									Strings.alignRight("" + c.get(Calendar.WEEK_OF_YEAR), 2, '0'),
									Strings.alignRight("" + (c.get(Calendar.MONTH) + 1), 2, '0'),
									Strings.alignRight("" + c.get(Calendar.DAY_OF_MONTH), 2, '0'));

		// 准备返回
		return new File(home + path);
	}

}
