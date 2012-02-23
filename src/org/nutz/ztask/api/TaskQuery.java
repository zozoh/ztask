package org.nutz.ztask.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.ztask.util.ZTasks;

/**
 * 封装了 Task 查询条件
 * <p>
 * 查询条件主要构成为 keyword，它的格式为:
 * 
 * <pre>
 * = 按 ID 查询 ==============
 *    如果字符串内容符合 MongoDB 的 ID 格式
 * 
 * = 按标签查询 ===============
 *    '#(A,B,C)'     // 同时具备 A,B,C 三个标签的任务
 *    '#()'          // 没有任何标签
 * 
 * = 按周查询 ================
 *    '&W(-1)'    // 上一周
 *    '&W(0)'     // 本周
 *    '&W(1)'     // 下一周
 *    '&W(-1:-3)' // 上三周
 * 
 * = 按 Task 的用户字段查询 ====
 *    '@(A, B)'      // owner 为 A 或 B
 *    '@C(A, B)'     // creater 为 A 或 B
 * 
 * = 指定某几种任务状态 ==
 *    '%(NEW,ING)'   // 状态为给定中的几种，忽略大小写
 *    
 * = 指定查看被某些用户关注的的任务
 *    'F(用户A,用户B)'  // 如果 F() 则表示查看自己收藏的
 * 
 * = 指定一个字段，用正则表达式 ==
 *    即搜索字符串，遇到第一个百分号，之后所有的内容都是正则表达式
 *    比如 '%REG%:^([a-z]|[ ])$'
 * 
 * = 混合模式 =================
 *       
 *    所有能被上述模式匹配的内容之外的内容，就是普通关键字，它会用来搜索 text 字段
 * 
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TaskQuery {

	/**
	 * @return text 字段的正则表达式，null 表示没有这个限制条件
	 */
	public Pattern qRegex() {
		return _kwd_.regex;
	}

	/**
	 * @return text 的关键字，null 表示没有这个限制条件
	 */
	public String qText() {
		return _kwd_.text;
	}

	/**
	 * @return 任务状态列表，null 表示没有这个限制条件
	 */
	public TaskStatus[] qStatus() {
		return _kwd_.status;
	}

	/**
	 * @return 查询的是否是 TaskId，null 表示不是 TaskID
	 */
	public String qID() {
		return _kwd_.ID;

	}

	/**
	 * @return null 表示没有 labels 的限定，否则用一个数组表示 labels 的列表
	 */
	public String[] qLabels() {
		return _kwd_.labels;
	}

	/**
	 * @return null 表示没有 owner 的限定，否则用一个数组表示 owner 的名字列表
	 */
	public String[] qOwners() {
		return _kwd_.owners;
	}

	/**
	 * null 表示没有 watchers 的限定，否则用一个数组表示 watchers 的名字列表
	 * <p>
	 * 如果返回一个长度为0的数组，则表示 "F()"
	 * 
	 * @return 名称列表
	 */
	public String[] qWatchers() {
		return _kwd_.watchers;
	}

	/**
	 * @return null 表示没有 creater 的限定，否则用一个数组表示 creater 的名字列表
	 */
	public String[] qCreaters() {
		return _kwd_.creaters;
	}

	/**
	 * 获得一个时间范围的查询条件
	 * 
	 * @return null 表示没有时间范围，否则用两个元素表示一个时间范围，精确到秒
	 */
	public Date[] qTimeScope() {
		return _kwd_.timescope;
	}

	/*-------------------------------------------------- 是一个字符串解析类 -----*/
	private KEYWORD _kwd_;

	private static class KEYWORD {

		private String ID;

		private Date[] timescope;

		private Pattern regex;

		private String[] labels;

		private String[] owners;

		private String[] creaters;

		private TaskStatus[] status;

		private String[] watchers;

		private String text;

		KEYWORD(String str) {
			// 忽略空
			if (Strings.isBlank(str))
				return;

			// 判断 ID
			if (str.length() == 24 && str.toLowerCase().matches("^[0-9a-f]{24}$")) {
				ID = str.toLowerCase();
				return;
			}

			/*
			 * 开始统计
			 */
			// 保存原始的关键字
			text = str;
			// 声明返回结果
			String[] re;

			// 统计: owners
			if (text.length() > 0) {
				re = find("(@[(])([^)]+)([)])");
				if (null != re) {
					owners = Strings.splitIgnoreBlank(re[2], ZTasks.REG_NOWORD);
				}
			}

			// 统计: 状态
			if (text.length() > 0) {
				re = find("(%[(])([ \ta-zA-Z,]+)([)])");
				if (null != re) {
					String[] ss = Strings.splitIgnoreBlank(re[2], ",");
					List<TaskStatus> ts = new ArrayList<TaskStatus>(ss.length);
					for (String s : ss) {
						try {
							ts.add(TaskStatus.valueOf(s.toUpperCase()));
						}
						catch (Exception e) {}
					}
					status = ts.isEmpty() ? null : ts.toArray(new TaskStatus[ts.size()]);
				}
			}

			// 统计: creaters
			if (text.length() > 0) {
				re = find("(@C[(])([^)]+)([)])");
				if (null != re) {
					creaters = Strings.splitIgnoreBlank(re[2], "[ \t\n\r,]");
				}
			}

			// 统计: labels
			if (text.length() > 0) {
				re = find("(#[(])([^)]*)([)])");
				if (null != re) {
					labels = Strings.splitIgnoreBlank(re[2], "[ \t\n\r,]");
				}
			}

			// 统计: watchers
			if (text.length() > 0) {
				re = find("(F[(])([^)]*)([)])");
				if (null != re) {
					watchers = Strings.splitIgnoreBlank(re[2], "[ \t\n\r,:]");
				}
			}

			// 统计: regex
			if (text.length() > 0) {
				re = find("(%REG%:)(.+)($)");
				if (null != re) {
					regex = Pattern.compile(Strings.trim(re[2]));
				}
			}

			// 统计: weeks -> timescope
			if (text.length() > 0) {
				re = find("(&[W|w][(])([ \t0-9,-]+)([)])");
				if (null != re) {
					String[] ss = Strings.splitIgnoreBlank(re[2], ",");
					if (1 == ss.length)
						timescope = Times.week(Integer.parseInt(ss[0]));
					else
						timescope = Times.weeks(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
				}
			}

			// 最后，如果 text 为空，设置成 null
			text = Strings.trim(Strings.sBlank(text.replaceAll("[ ]+", " "), null));

		}

		private String[] find(String reg) {
			return find(Pattern.compile(reg));
		}

		/**
		 * @param reg
		 *            正则表达式
		 * @return 匹配结果
		 */
		private String[] find(Pattern p) {
			Matcher m = p.matcher(text);

			if (m.find()) {
				text = text.substring(0, m.start()) + text.substring(m.end());

				String[] re = new String[m.groupCount()];
				for (int i = 0; i < re.length; i++) {
					re[i] = m.group(i);
				}
				return re;
			}
			return null;
		}

	}

	/*----------------------------------------------- 下面是这个类的基本内容 -----*/
	public static enum SORT {
		/**
		 * 从旧到新
		 */
		ASC,
		/**
		 * 从新到旧
		 */
		DESC
	}

	/**
	 * @return 简单的任务查询条件，仅包括一个按时间从新到旧的排序
	 */
	public static TaskQuery NEW() {
		return NEW(null);
	}

	/**
	 * @param keyword
	 *            关键字
	 * @return 任务查询条件，仅包括一个按时间从新到旧的排序，以及一个关键字
	 */
	public static TaskQuery NEW(String keyword) {
		TaskQuery tq = new TaskQuery();
		tq.setKeyword(keyword);
		return tq;
	}

	/**
	 * 
	 * @param format
	 *            字符串格式模板
	 * @param args
	 *            模板参数
	 * @return 任务查询条件，仅包括一个按时间从新到旧的排序，以及一个关键字
	 */
	public static TaskQuery NEWf(String format, Object... args) {
		return NEW(String.format(format, args));
	}

	public TaskQuery() {
		this.order = SORT.DESC;
		this.sortBy = "createTime";
	}

	private String keyword;

	private SORT order;

	private String sortBy;

	private boolean onlyTop;

	/**
	 * 小于等于0, 无视
	 */
	private int limit;

	/**
	 * 小于等于0, 无视
	 */
	private int skip;

	public TaskQuery setOnlyTop(boolean onlyTop) {
		this.onlyTop = onlyTop;
		return this;
	}

	public boolean isOnlyTop() {
		return onlyTop;
	}

	public TaskQuery sortBy(String fieldName) {
		sortBy = fieldName;
		return this;
	}

	public TaskQuery asc() {
		order = SORT.ASC;
		return this;
	}

	public TaskQuery desc() {
		order = SORT.DESC;
		return this;
	}

	public TaskQuery limit(int limit) {
		this.limit = limit;
		return this;
	}

	public TaskQuery skip(int skip) {
		this.skip = skip;
		return this;
	}

	public int limit() {
		return this.limit;
	}

	public int skip() {
		return this.skip;
	}

	public boolean isASC() {
		return SORT.ASC == order;
	}

	public boolean isDESC() {
		return SORT.DESC == order;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
		this._kwd_ = new KEYWORD(keyword);
	}

	public SORT getOrder() {
		return order;
	}

	public void setOrder(SORT order) {
		this.order = order;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

}
