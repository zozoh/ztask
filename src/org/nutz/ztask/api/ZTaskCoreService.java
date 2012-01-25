package org.nutz.ztask.api;

import java.util.List;

/**
 * 封装 zTASK 所有的核心操作，主要是针对 Task 和 TStack 的操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ZTaskCoreService {

	/**
	 * 根据一个 TASK ID 得到一个 TASK 对象
	 * 
	 * @param taskId
	 *            任务ID
	 * @return 任务对象
	 */
	public Task getTask(String taskId);

	/**
	 * 根据堆栈名称，得到一个堆栈
	 * 
	 * @param stackName
	 *            堆栈名称
	 * @return 堆栈对象
	 */
	public TStack getStack(String stackName);

	/**
	 * 查询一个 Stack 下面到底有哪些任务
	 * 
	 * @param stackName
	 *            栈名
	 * @return 按照顺序返回任务对象列表
	 */
	public List<Task> listTasks(String stackName);

}
