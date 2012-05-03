package org.nutz.ztask.api;

import java.util.Date;
import java.util.List;

import org.nutz.lang.Each;
import org.nutz.lang.util.ObjFilter;

/**
 * 封装 zTASK 所有的核心操作，主要是针对 Task 和 TStack 的操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface TaskService extends AbstractService {

	/**
	 * 根据一个 TASK ID 得到一个 TASK 对象
	 * 
	 * @param tid
	 *            任务ID
	 * @return 任务对象
	 */
	Task getTask(String tid);

	/**
	 * 根据一个 TASK ID 得到当前任务的根任务，如果当前的任务就是根，那么就返回
	 * 
	 * @param tid
	 *            任务 ID
	 * @return 任务对象, null 表示 top 不存在，即某个 Task 的 parentId 不正确
	 */
	Task getTopTask(String tid);

	/**
	 * 为任务增加一个注释
	 * <p>
	 * 为了速度，这个函数可以仅仅增加 Task.comments，但是并不取回所有的 comment <br>
	 * 因此在返回的 Task 对象里，可能不包括所有得 comments
	 * 
	 * @param t
	 *            任务
	 * @param comment
	 *            注释
	 * @return Task 对象
	 */
	Task addComment(Task t, String comment);

	/**
	 * 移除任务的一组注释说明
	 * 
	 * @param t
	 *            任务
	 * @param indexes
	 *            说明的下标，0 base
	 * @return 重新取回 Task 对象
	 */
	Task deleteComments(Task t, int... indexes);

	/**
	 * 修改某一个 comment
	 * 
	 * @param t
	 *            任务
	 * @param index
	 *            comment 的位置
	 * @param newText
	 *            新文本
	 * @return 重新取回 Task 对象
	 */
	Task setComment(Task t, int index, String newText);

	/**
	 * 为任务增加关注者，本函数会自动判断重复
	 * 
	 * @param t
	 *            任务
	 * @param watchers
	 *            关注者名列表，列表中的名称与老关注者融合
	 * @return 重新取回的 Task 对象
	 */
	Task addWatchers(Task t, String... watchers);

	/**
	 * 为任务重设关注者，本函数会自动判断重复
	 * 
	 * @param t
	 *            任务
	 * @param watchers
	 *            关注者名列表，将会取代老的关注者
	 * @return 重新取回的 Task 对象
	 */
	Task setWatchers(Task t, String[] watchers);

	/**
	 * 为任务取消关注者
	 * 
	 * @param t
	 *            任务
	 * @param watchers
	 *            关注者名列表，列表中的名称从老关注者中分离
	 * @return 重新取回的 Task 对象
	 */
	Task removeWatchers(Task t, String... watchers);

	/**
	 * 根据一个 TASK ID 得到当前任务的根任务，如果当前的任务就是根，那么就返回
	 * <p>
	 * 本函数会保证给定的 Task 的所有 Parent 都存在，如果不存在，则抛错
	 * 
	 * @param tid
	 *            任务 ID
	 * @return 任务对象
	 * @throws org.nutz.ztask.util.Err.T
	 *             #NO_EXISTS
	 */
	Task checkTopTask(String tid);

	/**
	 * 验证一个 Task ID 是否在数据库中存在
	 * <p>
	 * 如果对象不存在抛错
	 * 
	 * @param tid
	 *            任务ID
	 * @return 验证后的任务对象
	 * @throws org.nutz.ztask.util.Err.T
	 *             #NO_EXISTS
	 */
	Task checkTask(String tid);

	/**
	 * 验证一组 Task ID 是否在数据库中存在
	 * <p>
	 * 如果任何一个对象不存在抛错
	 * 
	 * @param taskIds
	 *            任务ID
	 * @return 验证后的任务对象列表
	 * @throws org.nutz.ztask.util.Err.T
	 *             #NO_EXISTS
	 */
	Task[] checkTasks(String... taskIds);

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
	 * 迭代所有符合条的任务， 这个方法适用于比较多的任务迭代，不占内存
	 * 
	 * @param callback
	 *            回调
	 * @param tq
	 *            查询条件，如果为 null，则用默认顺序迭代所有的 Task
	 */
	void each(Each<Task> callback, TaskQuery tq);

	/**
	 * 某种状态的任务列表
	 * 
	 * @param p
	 *            任务的父任务，为 null 表示获取顶层任务
	 * @param sta
	 *            为任务的状态，null 表示不关心这个限制条件
	 * 
	 * @return 按照顺序返回任务对象列表，顺序按照 title 字段排序
	 */
	List<Task> getTasks(Task p, TaskStatus sta);

	/**
	 * 返回所有未被分派完成的顶级任务
	 * 
	 * @return 任务列表
	 */
	List<Task> getTopNewTasks();

	/**
	 * 查询一个堆栈下面有哪些任务
	 * 
	 * @param s
	 *            堆栈对象, 不能为 null
	 * @param st
	 *            任务状态, null 表示不关心任务的状态
	 * @return 一个任务列表
	 */
	List<Task> getTasksInStack(TaskStack s, TaskStatus st);

	/**
	 * 查询一个 Stack 下面到底有哪些任务，这里仅仅返回根任务
	 * <p>
	 * 注，这里的 stack 必须存在，如果不存在，将抛错
	 * 
	 * @param snm
	 *            任务堆栈的名称，对应的 stack 必须存在
	 * 
	 * @return 按照顺序返回任务对象列表，顺序按照 title 字段排序
	 */
	List<Task> getTasksInStack(String snm);

	/**
	 * 得到一个任务所有的子任务
	 * 
	 * @param tid
	 *            任务 ID
	 * @return 按照顺序返回任务对象列表，顺序按照 title 字段排序
	 */
	List<Task> getChildTasks(String tid);

	/**
	 * 得到一个任务所有的子任务，并装入给定的 Task 对象中
	 * 
	 * @param t
	 *            任务对象
	 * 
	 * @param recur
	 *            是否递归
	 * 
	 * @return 给定的任务对象
	 */
	Task loadTaskChildren(Task t, boolean recur);

	/**
	 * 得到一个任务所有的叶子任务
	 * 
	 * @param t
	 *            任务对象
	 * @return 叶子任务列表
	 */
	List<Task> getLeafTasks(Task t);

	/**
	 * 得到一个任务所有某种类型的叶子任务
	 * 
	 * @param t
	 *            任务对象
	 * @param st
	 *            任务状态，如果为 null，则为全部任务
	 * @return 叶子任务列表
	 */
	List<Task> getLeafTasks(Task t, TaskStatus st);

	/**
	 * 迭代某任务所有
	 * 
	 * @param t
	 *            任务对象
	 * @param filter
	 *            过滤器
	 * @return 叶子任务列表
	 */
	List<Task> getLeafTasksBy(Task t, ObjFilter<Task> filter);

	/**
	 * 创建一个新的任务
	 * <p>
	 * 这个函数会自动标识 Task 的时间，以及将状态设置成 NEW
	 * 
	 * @param t
	 *            任务对象
	 * @return 创建后的任务对象
	 * 
	 * @see org.nutz.ztask.api.TaskStatus
	 */
	Task createTask(Task t);

	/**
	 * 根据一个 TASK ID 删除一个 TASK 对象
	 * 
	 * @param tid
	 *            任务ID
	 * @param recur
	 *            是否递归删除
	 * @return 被移除的任务对象
	 */
	Task removeTask(String tid, boolean recur);

	/**
	 * 修改一个任务对象的所有者
	 * 
	 * @param t
	 *            任务
	 * @param ownerName
	 *            任务新的所有者
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task setOwner(Task t, String ownerName);

	/**
	 * 修改一个任务的计划执行日期
	 * 
	 * @param t
	 *            任务
	 * @param planat
	 *            计划执行日期
	 * @return 修改后的任务对象
	 */
	Task setPlanAt(Task t, Date planat);

	/**
	 * 修改一个任务对象的标题，同时也会改变最后的修改时间
	 * 
	 * @param t
	 *            任务
	 * @param newText
	 *            新文本
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task setText(Task t, String newText);

	/**
	 * 修改一组任务的父任务
	 * 
	 * @param parentId
	 *            新的任务父ID
	 * @param ts
	 *            任务列表
	 * 
	 * @return 进行了修改后的任务对象
	 */
	List<Task> setParent(String parentId, Task... ts);

	/**
	 * 修改一组任务的父任务
	 * 
	 * @param p
	 *            新的任务父
	 * @param ts
	 *            任务列表
	 * 
	 * @return 进行了修改后的任务对象
	 */
	List<Task> setParentTask(Task p, Task... ts);

	/**
	 * 修改一个任务对象的标签，它会更新任务所有的标签
	 * 
	 * @param t
	 *            任务
	 * @param labels
	 *            标签数组
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task setLabels(Task t, String[] labels);

	/**
	 * 根据自己所有的子孙节点，计算自己节点的数量和状态
	 * <p>
	 * 递归查找自己所有的子孙，为了提高速度，查找的时候不排序，且不包括
	 * <ul>
	 * <li>text
	 * <li>createTime
	 * <li>lastModified
	 * <li>pushAt
	 * <li>startAt
	 * <li>hungupAt
	 * <li>popAt
	 * <li>creater
	 * <li>labels
	 * <li>comments
	 * </ul>
	 * 它也会按照如下规则自动统计父任务的 owner
	 * <ul>
	 * <li>最优先: 是多数子任务owner 的人也是这个父任务的 owner
	 * <li>其次: creater 更优先
	 * <li>最后: 随便选择一个子任务的 owner
	 * </ul>
	 * 如果子任务还有子任务，在评价owner 的时候，会根据子任务数量进行加权
	 * 
	 * @param task
	 *            任务对象
	 * @return 统计后的对象
	 */
	Task syncDescendants(Task task);

	/**
	 * 将一个任务压入某一个堆栈，即，记录这个任务所属的堆栈。
	 * <p>
	 * 并且，它会设置本任务的状态为 ING, 并重新计算该栈的任务数量
	 * 
	 * @param t
	 *            任务
	 * @param snm
	 *            堆栈名
	 * @return 修改后的任务对象，null 表示该任务不存在
	 * @throws org.nutz.ztask.util.Err.S
	 *             #NO_EXISTS
	 */
	Task pushToStack(Task t, String snm);

	/**
	 * 将一个任务压入某一个堆栈，即，记录这个任务所属的堆栈。
	 * <p>
	 * 并且，它会设置本任务的状态为 ING, 并重新计算该栈的任务数量
	 * 
	 * @param t
	 *            任务
	 * @param s
	 *            堆栈
	 * @return 修改后的任务对象，null 表示该任务不存在
	 * @throws org.nutz.ztask.util.Err.S
	 *             #NO_EXISTS
	 */
	Task pushToStack(Task t, TaskStack s);

	/**
	 * 将一个任务从一个堆栈弹出，
	 * <p>
	 * 并且，它会根据 done 参数设置本任务的状态为 DONE or NEW
	 * <p>
	 * 如果这个任务的确属于某个堆栈，该栈的任务数量将被重新计算
	 * 
	 * @param tid
	 *            任务ID
	 * @param stackName
	 *            堆栈名
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task popFromStack(String tid, boolean done);

	/**
	 * 将一个任务从一个堆栈弹出，
	 * <p>
	 * 并且，它会根据 done 参数设置本任务的状态为 DONE or NEW
	 * <p>
	 * 如果这个任务的确属于某个堆栈，该栈的任务数量将被重新计算
	 * 
	 * @param t
	 *            任务
	 * @param stackName
	 *            堆栈名
	 * @return 修改后的任务对象，null 表示该任务不存在
	 */
	Task popFromStack(Task t, boolean done);

	/**
	 * 将一个任务置成挂起状态
	 * 
	 * @param t
	 *            任务
	 * @return 任务对象
	 */
	Task hungupTask(Task t);

	/**
	 * 将一个任务置成非挂起状态
	 * 
	 * @param t
	 *            任务
	 * @return 任务对象
	 */
	Task restartTask(Task t);

	/**
	 * @return 系统中所有的堆栈对象
	 */
	List<TaskStack> getAllStacks();

	/**
	 * 迭代访问系统中所有的堆栈，适用于系统堆栈比较多的场景
	 * 
	 * @param onlyTop
	 *            是否仅仅获取根堆栈
	 * @param ownerName
	 *            所有者名称，如果为 null，则获取全部堆栈
	 * 
	 * @param callback
	 *            回调
	 */
	void eachStack(boolean onlyTop, String ownerName, Each<TaskStack> callback);

	/**
	 * 获取按照堆栈升序排序的 stack 列表
	 * 
	 * @param onlyTop
	 *            是否仅仅获取根堆栈
	 * @param ownerName
	 *            所有者名称，如果为 null，则获取全部堆栈
	 * 
	 * @return 堆栈列表
	 */
	List<TaskStack> getStacks(boolean onlyTop, String ownerName);

	/**
	 * 获得属于某个用户的Stack， 即所有该用户收藏的，或者 owner 是该用户的堆栈
	 * 
	 * @param ownerName
	 *            用户名
	 * @return 堆栈列表
	 */
	List<TaskStack> getMyFavoStacks(String ownerName);

	/**
	 * 获取一个堆栈下所有的子 stack
	 * 
	 * @param snm
	 *            堆栈名称
	 * @return 堆栈列表
	 */
	List<TaskStack> getChildStacks(String snm);

	/**
	 * 根据堆栈的名称，得到一个任务堆栈
	 * 
	 * @param snm
	 *            任务堆栈的名字
	 * @return 堆栈对象, null 表示不存在
	 */
	TaskStack getStack(String snm);

	/**
	 * 根据堆栈的名称，得到一个任务堆栈
	 * <p>
	 * 如果对象不存在抛错
	 * 
	 * @param snm
	 *            任务堆栈的名字
	 * @return 堆栈对象
	 * @throws org.nutz.ztask.util.Err.S
	 *             #NO_EXISTS
	 */
	TaskStack checkStack(String snm);

	/**
	 * 创建一个新的任务堆栈，如果堆栈已经存在，则更新
	 * 
	 * @param s
	 *            任务堆栈对象
	 * @return 创建后的任务堆栈对象
	 */
	TaskStack saveStack(TaskStack s);

	/**
	 * 创建一个新的任务堆栈，如果堆栈已经存在，则将其获取
	 * 
	 * @param snm
	 *            任务堆栈名称
	 * @param ownerName
	 *            堆栈的所有者
	 * @return 创建后的任务堆栈对象
	 */
	TaskStack createStackIfNoExistis(String snm, String ownerName);

	/**
	 * 更新堆栈得父堆栈，如果不存在则抛错
	 * 
	 * @param s
	 *            任务堆栈
	 * @param parentName
	 *            父堆栈的名称
	 * @return 堆栈对象
	 * @throws org.nutz.ztask.util.Err.S
	 *             #NO_EXISTS
	 */
	TaskStack setStackParent(TaskStack s, String parentName);

	/**
	 * 将一个用户设置成本堆栈的关注者
	 * 
	 * @param s
	 *            堆栈
	 * @param watcherName
	 *            关注者名
	 * @return 堆栈对象
	 * @throws org.nutz.ztask.util.Err.S
	 *             #NO_EXISTS
	 */
	TaskStack watchStack(TaskStack s, String watcherName);

	/**
	 * 取消一个用户对某个堆栈的关注
	 * 
	 * @param s
	 *            堆栈
	 * @param watcherName
	 *            关注者名
	 * @return 堆栈对象
	 * @throws org.nutz.ztask.util.Err.S
	 *             #NO_EXISTS
	 */
	TaskStack unwatchStack(TaskStack s, String watcherName);

	/**
	 * 移除一个任务堆栈
	 * 
	 * @param snm
	 *            堆栈名
	 * @return 移除后的任务对象, null 表示堆栈不存在
	 */
	TaskStack removeStack(String snm);

	/**
	 * 取得整个数据库的全局配置参数，这个函数绝地不能返回 null，如果没有信息，也要 new 一个默认的回去
	 * 
	 * @return 整个数据库的全局配置参数
	 */
	GInfo getGlobalInfo();

	/**
	 * 修改全局配置参数
	 * 
	 * @param info
	 *            全局配置参数
	 * @return 修改后的 GInfo 对象
	 */
	GInfo setGlobalInfo(GInfo info);
}
