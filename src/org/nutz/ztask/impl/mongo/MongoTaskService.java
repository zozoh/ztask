package org.nutz.ztask.impl.mongo;

import java.util.List;

import org.nutz.lang.Strings;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.util.MCur;
import org.nutz.mongo.util.Moo;
import org.nutz.ztask.Err;
import org.nutz.ztask.ZTasks;
import org.nutz.ztask.api.TaskQuery;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TaskStatus;

public class MongoTaskService extends AbstractMongoService implements TaskService {

	public MongoTaskService(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	@Override
	public Task getTask(String taskId) {
		return dao.findById(Task.class, taskId);
	}

	@Override
	public List<Task> queryTasks(TaskQuery tq) {
		Moo q = Moo.born();

		// 仅仅搜索根节点
		q.append("parentId", null);

		// 处理 owners
		String[] ows = tq.getOwners();
		if (null != ows && ows.length > 0) {
			q.inArray("owner", ows);
		}

		// 处理标签
		String[] lbs = tq.getLabels();
		if (null != lbs && lbs.length > 0) {
			q.all("labels", lbs);
		}

		// 处理关键字，这个放在后面，以便提高查询效率
		if (!Strings.isBlank(tq.getKeyword())) {
			if (tq.getKeyword().startsWith("^"))
				q.startsWith("title", tq.getKeyword().substring(1));
			else
				q.contains("title", tq.getKeyword());
		}

		// 排序
		MCur mcur = MCur.born();
		String sortBy = tq.isSortByCreateTime() ? "createTime" : "lastModified";
		if (tq.isNew2old()) {
			mcur.desc(sortBy);
		} else {
			mcur.asc(sortBy);
		}
		// 然后固定按照 title 排序
		mcur.asc("title");

		// 返回结果
		return dao.find(Task.class, q, mcur);
	}

	@Override
	public List<Task> getTopTasks(String stackName) {
		return dao.find(Task.class,
						Moo.born().append("stack", stackName).append("parentId", null),
						MCur.born().asc("title"));
	}

	@Override
	public List<Task> getChildTasks(String taskId) {
		return dao.find(Task.class, Moo.born().append("parentId", taskId), MCur.born().asc("title"));
	}

	@Override
	public Task createTask(Task task) {
		// 检查标题
		if (Strings.isBlank(task.getTitle())) {
			throw Err.T.BLANK_TASK();
		}
		// 设置创建时间
		task.setStatus(TaskStatus.NEW);
		task.setCreateTime(ZTasks.now());
		task.setLastModified(task.getCreateTime());
		// 执行创建
		dao.save(task);
		// 返回
		return task;
	}

	@Override
	public Task removeTask(String taskId, boolean recur) {
		Task t = _check_task(taskId);
		_remove_task(t, recur);
		return t;
	}

	private void _remove_task(Task t, boolean recur) {
		// 处理递归
		if (recur) {
			List<Task> subs = this.getChildTasks(t.get_id());
			if (null != subs)
				for (Task sub : subs) {
					_remove_task(sub, true);
				}
		}
		// 不递归，向上一级
		else {
			dao.update(	Task.class,
						Moo.born().append("parentId", t.get_id()),
						Moo.born().set("parentId", t.getParentId()));
		}
		// 删除本尊
		dao.removeById(Task.class, t.get_id());
	}

	@Override
	public Task setTaskOwner(String taskId, String ownerName) {
		Task t = _check_task(taskId);
		dao.updateById(Task.class, taskId, Moo.born().set("owner", ownerName));
		t.setOwner(ownerName);
		return t;
	}

	@Override
	public Task setTaskTitle(String taskId, String newTitle) {
		Task t = _check_task(taskId);
		dao.updateById(Task.class, taskId, Moo.born().set("title", newTitle));
		t.setTitle(newTitle);
		return t;
	}

	@Override
	public Task setTaskLabels(String taskId, String[] labels) {
		Task t = _check_task(taskId);
		dao.updateById(Task.class, taskId, Moo.born().set("labels", labels));
		t.setLabels(labels);
		return t;
	}

	@Override
	public Task setTaskParent(String taskId, String parentId) {
		Task t = _check_task(taskId);
		dao.updateById(Task.class, taskId, Moo.born().set("parentId", parentId));
		t.setParentId(parentId);
		return t;
	}

	@Override
	public Task pushToStack(String taskId, String stackName) {
		_check_stack(stackName);
		Task t = _check_task(taskId);
		// 已经在栈里，就没必要再执行了
		if (stackName.equals(t.getStack()))
			return t;
		// 如果这个任务之前在别的栈里，先弹栈
		if (!Strings.isBlank(t.getStack()))
			_pop_from_stack(t, false);
		// 最后压入 ...
		Moo o = Moo.born();
		o.set("status", TaskStatus.ING);
		o.set("stack", stackName);
		dao.updateById(Task.class, taskId, o);
		// 更新 Java 对象 ...
		t.setStack(stackName);
		t.setStatus(TaskStatus.ING);
		// 最后重新计算一下堆栈的数量
		_recountStackTaskNumber(stackName);

		// 最后返回
		return t;
	}

	@Override
	public Task popFromStack(String taskId, boolean done) {
		Task t = getTask(taskId);
		if (null != t) {
			_pop_from_stack(t, done);
		}
		return t;
	}

	private void _pop_from_stack(Task t, boolean done) {
		Moo o = Moo.born();
		o.set("status", done ? TaskStatus.DONE : TaskStatus.NEW);
		o.set("stack", ZTasks.NULL_STACK);
		dao.updateById(Task.class, t.get_id(), o);

		_recountStackTaskNumber(t.getStack());
	}

	private void _recountStackTaskNumber(String stackName) {
		if (!Strings.isBlank(stackName)) {
			int num = (int) dao.count(Task.class, Moo.born().append("stack", stackName));
			dao.update(	TaskStack.class,
						Moo.born().append("name", stackName),
						Moo.born().set("count", num));
		}
	}

	@Override
	public TaskStack getStack(String stackName) {
		return dao.findOne(TaskStack.class, Moo.born().append("name", stackName));
	}

	@Override
	public TaskStack createStack(TaskStack stack) {
		// 检查名称
		stack.setName(Strings.trim(stack.getName()));
		if (Strings.isBlank(stack.getName())) {
			throw Err.S.BLANK_NAME();
		}
		// 确保不存在
		TaskStack ts = getStack(stack.getName());
		if (null != ts)
			throw Err.S.EXISTS(stack.getName());
		// 执行创建
		dao.save(stack);
		// 返回
		return stack;
	}

	@Override
	public TaskStack removeStack(String stackName) {
		Moo q = Moo.born().append("name", stackName);
		// 获取任务堆栈
		TaskStack ts = dao.findOne(TaskStack.class, q);

		// 修改所有相关任务状态为 NEW, stack 为 ZTasks.NULL_STACK
		dao.update(	Task.class,
					Moo.born().append("stack", stackName).append("status", TaskStatus.ING),
					Moo.born().set("stack", ZTasks.NULL_STACK).set("status", TaskStatus.NEW));

		// 移除任务堆栈
		dao.remove(TaskStack.class, q);

		// 返回任务堆栈
		return ts;
	}

	private Task _check_task(String taskId) {
		Task t = getTask(taskId);
		if (null == t)
			throw Err.T.NO_EXISTS(taskId);
		return t;
	}

	private TaskStack _check_stack(String stackName) {
		TaskStack ts = getStack(stackName);
		if (null == ts)
			throw Err.S.NO_EXISTS(stackName);
		return ts;
	}

}
