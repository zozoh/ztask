package org.nutz.ztask.api;

import java.util.List;

/**
 * 封装 zTASK 所有的核心操作，主要是针对 Task 和 TStack 的操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface TaskService extends AbstractService {

	/**
	 * 根据一个 TASK ID 得到一个 TASK 对象
	 * 
	 * @param taskId
	 *            任务ID
	 * @return 任务对象
	 */
	Task getTask(String taskId);

	/**
	 * 根据一个 TASK ID 得到一个 TASK 对象
	 * <p>
	 * 如果对象不存在抛错
	 * 
	 * @param taskId
	 *            任务ID
	 * @return 任务对象
	 * @throws org.nutz.ztask.Err.T
	 *             #NO_EXISTS
	 */
	Task checkTask(String taskId);

	/**
	 * 根据条件查询一组任务，条件详细请参见 TaskQuery 接口
	 * 
	 * @param tq
	 *            查询条件
	 * @return 任务对象列表
	 * @see org.nutz.ztask.api.TaskQuery
	 */
	List<Task> queryTasks(TaskQuery tq);

	/**
	 * 查询一个 Stack 下面到底有哪些任务，这里仅仅返回根任务
	 * 
	 * @param stackName
	 *            任务堆栈的名称，null 表示不在任何堆栈中的任务
	 * @param sta
	 *            为任务的状态，null 表示不关心这个限制条件
	 * @return 按照顺序返回任务对象列表，顺序按照 title 字段排序
	 */
	List<Task> getTopTasks(String stackName, TaskStatus sta);

	/**
	 * 查询一个 Stack 下面到底有哪些任务，这里仅仅返回根任务
	 * <p>
	 * 注，这里的 stack 必须存在，如果不存在，将抛错
	 * 
	 * @param stackName
	 *            任务堆栈的名称，对应的 stack 必须存在
	 * 
	 * @return 按照顺序返回任务对象列表，顺序按照 title 字段排序
	 */
	List<Task> getTopTasksInStack(String stackName);

	/**
	 * 得到一个任务所有的子任务
	 * 
	 * @param taskId
	 *            任务 ID
	 * @return 按照顺序返回任务对象列表，顺序按照 title 字段排序
	 */
	List<Task> getChildTasks(String taskId);

	/**
	 * 创建一个新的任务
	 * <p>
	 * 这个函数会自动标识 Task 的时间，以及将状态设置成 NEW
	 * 
	 * @param task
	 *            任务对象
	 * @return 创建后的任务对象
	 * 
	 * @see org.nutz.ztask.api.TaskStatus
	 */
	Task createTask(Task task);

	/**
	 * 根据一个 TASK ID 删除一个 TASK 对象
	 * 
	 * @param taskId
	 *            任务ID
	 * @param recur
	 *            是否递归删除
	 * @return 被移除的任务对象
	 */
	Task removeTask(String taskId, boolean recur);

	/**
	 * 修改一个任务对象的所有者
	 * 
	 * @param taskId
	 *            任务的 ID
	 * @param ownerName
	 *            任务新的所有者
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task setTaskOwner(String taskId, String ownerName);

	/**
	 * 修改一个任务对象的标题，同时也会改变最后的修改时间
	 * 
	 * @param taskId
	 *            任务的 ID
	 * @param newTitle
	 *            新标题
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task setTaskTitle(String taskId, String newTitle);

	/**
	 * 修改一个任务对象的标签，它会更新任务所有的标签
	 * 
	 * @param taskId
	 *            任务的 ID
	 * @param parentId
	 *            新的任务父ID
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task setTaskParent(String taskId, String parentId);

	/**
	 * 修改一个任务对象的标签，它会更新任务所有的标签
	 * 
	 * @param taskId
	 *            任务的 ID
	 * @param labels
	 *            标签数组
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task setTaskLabels(String taskId, String[] labels);

	/**
	 * 将一个任务压入某一个堆栈，即，记录这个任务所属的堆栈。
	 * <p>
	 * 并且，它会设置本任务的状态为 ING, 并重新计算该栈的任务数量
	 * 
	 * @param taskId
	 *            任务ID
	 * @param stackName
	 *            堆栈名
	 * @return 修改后的任务对象，null 表示该任务不存在
	 * @throws org.nutz.ztask.Err.S
	 *             #NO_EXISTS
	 */
	Task pushToStack(String taskId, String stackName);

	/**
	 * 将一个任务从一个堆栈弹出，
	 * <p>
	 * 并且，它会根据 done 参数设置本任务的状态为 DONE or NEW
	 * <p>
	 * 如果这个任务的确属于某个堆栈，该栈的任务数量将被重新计算
	 * 
	 * @param taskId
	 *            任务ID
	 * @param stackName
	 *            堆栈名
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task popFromStack(String taskId, boolean done);

	/**
	 * 根据堆栈的Name，得到一个任务堆栈
	 * 
	 * @param stackName
	 *            任务堆栈的名字
	 * @return 堆栈对象, null 表示不存在
	 */
	TaskStack getStack(String stackName);

	/**
	 * 根据堆栈的Name，得到一个任务堆栈
	 * <p>
	 * 如果对象不存在抛错
	 * 
	 * @param stackName
	 *            任务堆栈的名字
	 * @return 堆栈对象
	 * @throws org.nutz.ztask.Err.S
	 *             #NO_EXISTS
	 */
	TaskStack checkStack(String stackName);

	/**
	 * 创建一个新的任务堆栈
	 * 
	 * @param stack
	 *            任务堆栈对象
	 * @return 创建后的任务堆栈对象
	 * @throws org.nutz.ztask.Err.S
	 *             #EXISTS
	 */
	TaskStack createStack(TaskStack stack);

	/**
	 * 移除一个任务堆栈
	 * 
	 * @param stackName
	 *            堆栈名
	 * @return 移除后的任务对象, null 表示堆栈不存在
	 */
	TaskStack removeStack(String stackName);
}
