package org.nutz.ztask.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

/**
 * 封装了报告接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface TaskReportor {

	/**
	 * 获取某个报告的内容输出流
	 * 
	 * @param rpt
	 *            报告
	 * @return 输出流
	 */
	InputStream getInputStream(TaskReport rpt);

	/**
	 * 向一个给定的输入流写入某个报告的内容，最后并关闭输入流
	 * 
	 * @param rpt
	 *            报告对象
	 * @param ops
	 *            输入流
	 */
	void writeAndClose(TaskReport rpt, OutputStream ops);

	/**
	 * 根据一个日期，得到一个报告对象
	 * 
	 * @param c
	 * @return 报告对象，null 不存在
	 */
	TaskReport get(Calendar c);

	/**
	 * 获取一个时间范围内的报告对象
	 * 
	 * @param from
	 *            开始日期
	 * @oaran to 结束日期
	 * @return 报告列表
	 */
	List<TaskReport> getBy(Calendar from, Calendar to);

	/**
	 * 根据日期对象，生成一个报告。如果报告已经存在，覆盖
	 * 
	 * @param c
	 *            日期对象
	 * @return 报告对象
	 */
	TaskReport make(Calendar c);

	/**
	 * 根据日期对象，生成一个报告。如果报告已经存在，跳过
	 * 
	 * @param c
	 *            日期对象
	 * @return 报告对象
	 */
	TaskReport makeIfNoExists(Calendar c);

	/**
	 * 移除一个报告对象
	 * 
	 * @param reportId
	 *            一个报告的唯一标识名
	 * @return 被移除的 TaskReport 对象, null 表示没有这个报告
	 */
	TaskReport drop(Calendar c);

	/**
	 * 一次删除一个时间范围内的报告对象
	 * 
	 * @param from
	 *            开始日期
	 * @oaran to 结束日期
	 * @return 被删除的报告列表
	 */
	List<TaskReport> dropBy(Calendar from, Calendar to);

}
