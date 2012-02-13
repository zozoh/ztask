package org.nutz.ztask.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Callback3;
import org.nutz.lang.util.ObjFilter;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.MongoDao;
import org.nutz.quartz.Quartz;
import org.nutz.ztask.api.GInfo;
import org.nutz.ztask.api.GlobalLock;
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
import org.nutz.ztask.thread.ScheduleUpdateAtom;
import org.nutz.ztask.util.Err;

public class HookedTaskService implements TaskService {

	private final static Log log = Logs.get();

	private GlobalLock glock;

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

	public Task addComment(Task t, String comment) {
		t = tasks.addComment(t, comment);
		hooks.doHook(HookType.COMMENT, t, -1);
		return t;
	}

	public Task deleteComments(Task t, int... indexes) {
		return tasks.deleteComments(t, indexes);
	}

	public Task setComment(Task t, int index, String newText) {
		t = tasks.setComment(t, index, newText);
		hooks.doHook(HookType.COMMENT, t, index);
		return t;
	}

	@Override
	public Task addWatchers(Task t, String... watchers) {
		return tasks.addWatchers(t, watchers);
	}

	@Override
	public Task removeWatchers(Task t, String... watchers) {
		return tasks.removeWatchers(t, watchers);
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
		hooks.doHook(HookType.CREATE, t, null);
		return t;
	}

	public Task removeTask(String taskId, boolean recur) {
		Task t = tasks.checkTask(taskId);
		hooks.doHook(HookType.DROP, t, null);
		return tasks.removeTask(taskId, recur);
	}

	public Task setOwner(Task t, String ownerName) {
		Object refer = t.getOwner();
		t = tasks.setOwner(t, ownerName);
		hooks.doHook(HookType.OWNER, t, refer);
		return t;
	}

	public Task setText(Task t, String newTitle) {
		Object refer = t.getText();
		t = tasks.setText(t, newTitle);
		hooks.doHook(HookType.UPDATE, t, refer);
		return t;
	}

	public List<Task> setParent(String parentId, Task... ts) {
		return this.setParentTask(checkTask(parentId), ts);
	}

	public List<Task> setParentTask(Task p, Task... ts) {
		return tasks.setParentTask(p, ts);
	}

	public Task setLabels(Task t, String[] labels) {
		Object refer = t.getLabels();
		t = tasks.setLabels(t, labels);
		hooks.doHook(HookType.LABEL, t, refer);
		return t;
	}

	public Task syncDescendants(Task task) {
		return tasks.syncDescendants(task);
	}

	public Task pushToStack(Task t, String stackName) {
		return this.pushToStack(t, this.checkStack(stackName));
	}

	public Task pushToStack(Task t, TaskStack stack) {
		Object refer = t.getStack();
		t = tasks.pushToStack(t, stack);
		hooks.doHook(HookType.PUSH, t, refer);
		return t;
	}

	public Task popFromStack(String taskId, boolean done) {
		return this.popFromStack(checkTask(taskId), done);
	}

	public Task popFromStack(Task t, boolean done) {
		Object refer = t.getStack();
		t = tasks.popFromStack(t, done);
		hooks.doHook(done ? HookType.DONE : HookType.REJECT, t, refer);
		return t;
	}

	public Task hungupTask(Task t) {
		t = tasks.hungupTask(t);
		hooks.doHook(HookType.HUNGUP, t, null);
		return t;
	}

	public Task restartTask(Task t) {
		t = tasks.restartTask(t);
		hooks.doHook(HookType.RESTART, t, null);
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

	public TaskStack setStackParent(TaskStack s, String parentName) {
		return tasks.setStackParent(s, parentName);
	}

	public TaskStack watchStack(TaskStack s, String watcherName) {
		return tasks.watchStack(s, watcherName);
	}

	public TaskStack unwatchStack(TaskStack s, String watcherName) {
		return tasks.unwatchStack(s, watcherName);
	}

	public TaskStack removeStack(String stackName) {
		return tasks.removeStack(stackName);
	}

	public GInfo getGlobalInfo() {
		return tasks.getGlobalInfo();
	}

	public GInfo setGlobalInfo(GInfo info) {
		// 先得到老的数据
		GInfo oldInfo = tasks.getGlobalInfo();

		info = tasks.setGlobalInfo(info);

		// 同步钩子数据
		syncHooks(info);

		// 同步 timer 数据
		syncTimer(info, oldInfo);

		return info;
	}

	private void syncTimer(GInfo info, GInfo oldInfo) {
		// 都没 Timer 无视
		if (null == info.getTimers() && null == oldInfo.getTimers())
			return;

		// 如果 Timer 数据相等，也无视
		if (Lang.equals(info.getTimers(), oldInfo.getTimers()))
			return;

		String[] timers = info.getTimers();
		if (log.isDebugEnabled())
			log.debugf("Found %d timers in sys", timers.length);

		// 逐个检查，并打印 ...
		info.eachTimer(ioc, new Callback3<Integer, Quartz, String[]>() {
			public void invoke(Integer index, Quartz qz, String[] handlerNames) {
				if (log.isDebugEnabled())
					log.debugf(	"  @CHECK[%d]: %s :: (%d)'%s' ",
								index,
								qz,
								handlerNames.length,
								Lang.concat(", ", handlerNames));
			}
		});

		// 最后通知更新线程
		if (log.isInfoEnabled())
			log.infof("notify thread %s ... " + ScheduleUpdateAtom.NAME);

		synchronized (glock) {
			glock.notifyAll();
		}

		if (log.isInfoEnabled())
			log.infof("... done for notify" + ScheduleUpdateAtom.NAME);

	}

	private void syncHooks(GInfo info) {
		// 清除所有的钩子
		if (null == info.getHooks() || info.getHooks().length == 0) {
			hooks.clear();
		}
		// 开始同步钩子
		else {
			// 首先取出老钩子，以便比对只用
			List<? extends Hook> list = hooks.list(null);
			Map<String, Hook> all = new HashMap<String, Hook>();
			for (Hook h : list)
				all.put(h.getName(), h);

			if (log.isDebugEnabled())
				log.debugf("Found %d hooks in sys", all.size());

			/*
			 * 解析配置文件，支持 *:xxx,xxx 的写法
			 */
			int i = 0;
			List<String> strs = new LinkedList<String>();
			for (String s : info.getHooks()) {
				String[] ss = Strings.splitIgnoreBlank(s, ":");
				// 无效输入
				if (ss.length != 2)
					throw Err.H.INVALID_STR(i, s);

				String[] hhNames = Strings.splitIgnoreBlank(ss[1], ",");

				// 全部的钩子类型
				if (ss[0].equals("*")) {
					for (HookType ht : HookType.values()) {
						for (String hhName : hhNames)
							strs.add(ht + ":" + hhName);
					}
				}
				// 1-n 个钩子类型
				else {
					for (String htName : Strings.splitIgnoreBlank(ss[0], ",")) {
						HookType ht = HookType.valueOf(htName.toUpperCase());
						for (String hhName : hhNames)
							strs.add(ht + ":" + hhName);
					}
				}

				// 自增
				i++;
			}

			/*
			 * 循环新传进来的钩子配置信息
			 */
			i = 0;
			for (String s : strs) {
				String[] ss = Strings.splitIgnoreBlank(s, ":");

				MongoHook h = new MongoHook();
				h.setType(HookType.valueOf(Strings.trim(ss[0].toUpperCase())));
				h.setHandler(Strings.trim(ss[1]));

				// 检查一下 handler 是否有效
				if (null == ioc.get(HookHandler.class, h.getHandler()))
					throw Err.H.NO_HANDLER(h);

				// 有这个钩子 ...
				if (all.containsKey(h.getName())) {
					all.remove(h.getName());
					if (log.isDebugEnabled())
						log.debugf("  ..%2d) %s", i, h);
				}
				// 否则，新钩子，插入
				else {
					hooks.add(h);
					if (log.isDebugEnabled())
						log.debugf("  ++%2d) %s ", i, h);
				}

				// 自增
				i++;
			}

			// 最后删掉余下的老钩子
			if (log.isDebugEnabled())
				log.debugf("It will remove %d from sys", all.size());
			i = 0;
			for (Hook h : all.values()) {
				hooks.removeById(h.getId());
				if (log.isDebugEnabled())
					log.debugf("  --%2d) %s ", i, h);
			}

			if (log.isDebugEnabled())
				log.debugf("Done, for %d hooks", list.size());

		}
	}

}
