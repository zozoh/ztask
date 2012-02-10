package org.nutz.ztask.web.module.core;

import java.util.Calendar;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.filter.CheckSession;
import org.nutz.web.Webs;
import org.nutz.ztask.api.TaskReport;
import org.nutz.ztask.api.TaskReportor;
import org.nutz.ztask.api.TimerSchedule;
import org.nutz.ztask.api.User;

@Filters(@By(type = CheckSession.class, args = {Webs.ME, "/page/login"}))
@InjectName
@IocBean
@Fail(">>:/e500.html")
public class PageModule {

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
	@Inject("java:$conf.get('sys-report-day',2)")
	private int reportDay;

	@Inject("refer:reportor")
	private TaskReportor reportor;

	@Inject("refer:schedule")
	private TimerSchedule schedule;

	/**
	 * 自动决定，重定向到哪个视图
	 * 
	 * @param sess
	 *            会话对象
	 * 
	 * @return 重定向的 URL
	 */
	@Filters
	@At("/")
	@Ok(">>:${obj}")
	public String autoDispatchRoot(HttpSession sess) {
		User u = (User) sess.getAttribute(Webs.ME);
		if (null == u) {
			return "/page/login";
		}
		return "/page/mystack";
	}

	/**
	 * 登录界面
	 */
	@Filters
	@At("/page/login")
	@Ok("jsp:jsp.login")
	public void showLoginPage() {}

	/**
	 * 一个查看后台 schedule 状态的隐蔽 URL
	 * 
	 * @return 后台 schedule 的内容
	 */
	@At("/monitor/schedule")
	@Ok("jsp:jsp.monitor.schedule")
	public String showMonitorSchedule() {
		return schedule.toString();
	}

	/**
	 * 生成报告界面
	 * 
	 * @param ds
	 *            日期，格式如 yyyy-MM-dd
	 * @param force
	 *            是否强制生成报告
	 * @param req
	 *            请求对象
	 * @return 纯文本报告
	 */
	@At("/page/do/report/*")
	@Ok("jsp:jsp.report_display")
	public String doMakeReports(String ds, @Param("force") boolean force, ServletRequest req) {
		Calendar c = Times.C(ds);
		TaskReport rpt = force ? reportor.make(c) : reportor.makeIfNoExists(c);
		StringBuilder sb = new StringBuilder();
		reportor.writeAndClose(rpt, Lang.ops(sb));
		req.setAttribute("rpt", rpt);
		return sb.toString();
	}

	/**
	 * 报告界面
	 */
	@At("/page/report")
	@Ok("jsp:jsp.report")
	public void showReportPage(@Param("yy") int year, ServletRequest req) {
		Calendar now = Times.C(Times.now());
		now.set(Calendar.DAY_OF_WEEK, this.reportDay);
		String thisWeek = Times.sD(now.getTime());

		String yy = Strings.alignRight(	year > 0 ? year : Calendar.getInstance().get(Calendar.YEAR),
										4,
										'0');
		req.setAttribute("year", yy);

		// 生成 12 个格子，为每个格子填充应该有多少周
		NutMap[][] cells = new NutMap[13][];
		for (int i = 1; i < 13; i++) {
			String MM = Strings.alignRight(i, 2, '0');

			Calendar c = Times.C(yy + "-" + MM + "-01");
			double weekDay = c.get(Calendar.DAY_OF_WEEK);
			double maxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
			// 最多几周
			int weekNumber = (int) Math.ceil((weekDay + maxDays) / 7);

			cells[i] = new NutMap[weekNumber];

			for (int w = 0; w < weekNumber; w++) {
				String ds = yy + "-" + MM + "-" + Strings.alignRight(1 + w * 7, 2, '0');
				c = Times.C(ds);
				c.set(Calendar.DAY_OF_WEEK, this.reportDay);
				boolean hasReport = reportor.get(c) != null;
				boolean isCurrentWeek = thisWeek.equals(Times.sD(c.getTime()));
				int weekOfYear = c.get(Calendar.WEEK_OF_YEAR);

				NutMap map = new NutMap();
				map.putAll(Lang.mapf(	"{w:%d, wyy:%d, d:'%s', report:%s, current:%s}",
										w,
										weekOfYear,
										Times.sD(c.getTime()).substring(5),
										hasReport,
										isCurrentWeek));
				cells[i][w] = map;
			}
		}

		req.setAttribute("cells", cells);

	}

	/**
	 * 我的堆栈界面
	 */
	@At("/page/mystack")
	@Ok("jsp:jsp.mystack")
	public void showMyStackPage() {}

	/**
	 * 系统界面
	 */
	@At("/page/sys")
	@Ok("jsp:jsp.sys")
	public void showSystemConfigurationPage() {}

	/**
	 * 标签界面
	 */
	@At("/page/label")
	@Ok("jsp:jsp.label")
	public void showLabelPage() {}

	/**
	 * 任务界面
	 */
	@At("/page/task")
	@Ok("jsp:jsp.task")
	public void showTaskPage() {}

	/**
	 * 堆栈界面
	 */
	@At("/page/stack")
	@Ok("jsp:jsp.stack")
	public void showStackPage() {}
}
