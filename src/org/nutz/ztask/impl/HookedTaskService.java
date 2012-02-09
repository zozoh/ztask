package org.nutz.ztask.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Each;
import org.nutz.lang.Strings;
import org.nutz.lang.util.ObjFilter;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.MongoDao;
import org.nutz.ztask.api.GInfo;
import org.nutz.ztask.api.Hook;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookService;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskQuery;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.TaskStatus;
import org.nutz.ztask.impl.mongo.MongoHook;
import org.nutz.ztask.util.Err;

public class HookedTaskService implements TaskService {

	private final static Log log = Logs.get();

	private TaskService tasks;

	private HookService hooks;

	private Ioc ioc;

	public MongoDao dao() {
		return tasks.dao();
	}

	public Task getTask(String taskId) {
		return tasks.getTask(taskId);
	}

	public Task getTopTask(String taskId) {
		return tasks.getTopTask(taskId);
	}

	public Task addComment(String taskId, String comment) {
		return tasks.addComment(taskId, comment);
	}

	public Task deleteComments(String taskId, int... indexes) {
		return tasks.deleteComments(taskId, indexes);
	}

	public Task setComment(String taskId, int index, String newText) {
		return tasks.setComment(taskId, index, newText);
	}

	public Task checkTopTask(String taskId) {
		return tasks.checkTopTask(taskId);
	}

	public Task checkTask(String taskId) {
		return tasks.checkTask(taskId);
	}

	public Task[] checkTasks(String... taskIds) {
		return tasks.checkTasks(taskIds);
	}

	public List<Task> queryTasks(TaskQuery tq) {
		return tasks.queryTasks(tq);
	}

	public void each(Each<Task> callback, TaskQuery tq) {
		tasks.each(callback, tq);
	}

	public List<Task> getTasks(Task p, TaskStatus sta) {
		return tasks.getTasks(p, sta);
	}

	public List<Task> getTopNewTasks() {
		return tasks.getTopNewTasks();
	}

	public List<Task> getTasksInStack(TaskStack stack, TaskStatus st) {
		return tasks.getTasksInStack(stack, st);
	}

	public List<Task> getTasksInStack(String stackName) {
		return tasks.getTasksInStack(stackName);
	}

	public List<Task> getChildTasks(String taskId) {
		return tasks.getChildTasks(taskId);
	}

	public Task loadTaskChildren(Task task) {
		return tasks.loadTaskChildren(task);
	}

	public List<Task> getLeafTasks(Task task) {
		return tasks.getLeafTasks(task);
	}

	public List<Task> getLeafTasks(Task task, TaskStatus st) {
		return tasks.getLeafTasks(task, st);
	}

	public List<Task> getLeafTasksBy(Task task, ObjFilter<Task> filter) {
		return tasks.getLeafTasksBy(task, filter);
	}

	public Task createTask(Task task) {
		Task t = tasks.createTask(task);
		hooks.doHook(HookType.CREATE, t);
		return t;
	}

	public Task removeTask(String taskId, boolean recur) {
		Task t = tasks.checkTask(taskId);
		hooks.doHook(HookType.DROP, t);
		return tasks.removeTask(taskId, recur);
	}

	public Task setTaskOwner(String taskId, String ownerName) {
		Task t = tasks.setTaskOwner(taskId, ownerName);
		hooks.doHook(HookType.OWNER, t);
		return t;
	}

	public Task setTaskText(String taskId, String newTitle) {
		Task t = tasks.setTaskText(taskId, newTitle);
		hooks.doHook(HookType.UPDATE, t);
		return t;
	}

	public List<Task> setTasksParent(String parentId, String... taskIds) {
		return tasks.setTasksParent(parentId, taskIds);
	}

	public Task setTaskLabels(String taskId, String[] labels) {
		Task t = tasks.setTaskLabels(taskId, labels);
		hooks.doHook(HookType.LABEL, t);
		return t;
	}

	public Task syncDescendants(Task task) {
		return tasks.syncDescendants(task);
	}

	public Task pushToStack(String taskId, String stackName) {
		Task t = tasks.pushToStack(taskId, stackName);
		hooks.doHook(HookType.PUSH, t);
		return t;
	}

	public Task pushToStack(Task task, TaskStack stack) {
		Task t = tasks.pushToStack(task, stack);
		hooks.doHook(HookType.PUSH, t);
		return t;
	}

	public Task popFromStack(String taskId, boolean done) {
		Task t = tasks.popFromStack(taskId, done);
		hooks.doHook(done ? HookType.DONE : HookType.REJECT, t);
		return t;
	}

	public Task popFromStack(Task task, boolean done) {
		Task t = tasks.popFromStack(task, done);
		hooks.doHook(done ? HookType.DONE : HookType.REJECT, t);
		return t;
	}

	public Task hungupTask(String taskId) {
		Task t = tasks.hungupTask(taskId);
		hooks.doHook(HookType.HUNGUP, t);
		return t;
	}

	public Task restartTask(String taskId) {
		Task t = tasks.restartTask(taskId);
		hooks.doHook(HookType.RESTART, t);
		return t;
	}

	public List<TaskStack> getAllStacks() {
		return tasks.getAllStacks();
	}

	public void eachStack(Each<TaskStack> callback) {
		tasks.eachStack(callback);
	}

	public List<TaskStack> getTopStacks() {
		return tasks.getTopStacks();
	}

	public List<TaskStack> getMyFavoStacks(String ownerName) {
		return tasks.getMyFavoStacks(ownerName);
	}

	public List<TaskStack> getChildStacks(String stackName) {
		return tasks.getChildStacks(stackName);
	}

	public TaskStack getStack(String stackName) {
		return tasks.getStack(stackName);
	}

	public TaskStack checkStack(String stackName) {
		return tasks.checkStack(stackName);
	}

	public TaskStack saveStack(TaskStack stack) {
		return tasks.saveStack(stack);
	}

	public TaskStack createStackIfNoExistis(String stackName, String ownerName) {
		return tasks.createStackIfNoExistis(stackName, ownerName);
	}

	public TaskStack setStackParent(String stackName, String parentName) {
		return tasks.setStackParent(stackName, parentName);
	}

	public TaskStack watchStack(String stackName, String watcherName) {
		return tasks.watchStack(stackName, watcherName);
	}

	public TaskStack unwatchStack(String stackName, String watcherName) {
		return tasks.unwatchStack(stackName, watcherName);
	}

	public TaskStack removeStack(String stackName) {
		return tasks.removeStack(stackName);
	}

	public GInfo getGlobalInfo() {
		return tasks.getGlobalInfo();
	}

	public GInfo setGlobalInfo(GInfo info) {
		info = tasks.setGlobalInfo(info);

		// 清除所有的钩子
		if (null == info.getHooks() || info.getHooks().length == 0) {
			hooks.clearHooks();
		}
		// 开始同步钩子
		else {
			// 记录老钩子
			List<? extends Hook> list = hooks.getHooks(null);
			Map<String, Hook> all = new HashMap<String, Hook>();
			for (Hook h : list)
				all.put(h.getID(), h);

			if (log.isDebugEnabled())
				log.debugf("Found %d hooks in sys", all.size());

			// 循环新钩子
			int i = 0;
			for (String s : info.getHooks()) {
				String[] ss = Strings.splitIgnoreBlank(s, ":");

				MongoHook h = new MongoHook();
				h.setType(HookType.valueOf(Strings.trim(ss[0].toUpperCase())));
				h.setHandler(Strings.trim(ss[1]));

				// 检查一下 handler 是否有效
				if (null == ioc.get(HookHandler.class, h.getHandler()))
					throw Err.H.NO_HANDLER(h);

				// 有这个钩子 ...
				if (all.containsKey(h.getID())) {
					all.remove(h.getID());
					if (log.isDebugEnabled())
						log.debugf("  ..%2d) %s", i, h);
				}
				// 否则，新钩子，插入
				else {
					hooks.addHook(h);
					if (log.isDebugEnabled())
						log.debugf("  ++%2d) %s ", i, h);
				}
			}

			// 最后删掉余下的老钩子
			if (log.isDebugEnabled())
				log.debugf("It will remove %d from sys", all.size());
			i = 0;
			for (Hook h : all.values()) {
				hooks.removeHook(h.getType(), h.getHandler());
				if (log.isDebugEnabled())
					log.debugf("  --%2d) %s ", i, h);
			}

			if (log.isDebugEnabled())
				log.debugf("Done, for %d hooks", list.size());

		}

		return info;
	}

}
