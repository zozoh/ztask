package org.nutz.ztask.web.module.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Chain;
import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.web.Webs;
import org.nutz.web.ajax.AjaxCheckSession;
import org.nutz.ztask.Err;
import org.nutz.ztask.api.*;

@Filters(@By(type = AjaxCheckSession.class, args = Webs.ME))
@InjectName
@IocBean
@Ok("ajax")
@Fail("ajax")
@At("/ajax")
public class AjaxModule {

	@Inject("java:$conf.getInt('sys-task-len-min')")
	private int taskTitleMinLength;

	@Inject("java:$conf.getInt('sys-task-len-max')")
	private int taskTitleMaxLength;

	@Inject("refer:taskService")
	private TaskService tasks;

	@Inject("refer:labelService")
	private LabelService labels;

	@At("/label/tops")
	public List<Label> getTopLabels() {
		return labels.getTopLabels();
	}

	@At("/label/children")
	public List<Label> getChildrenLabels(@Param("lbnm") String labelName) {
		return labels.getChildren(labelName);
	}

	@At("/do/sync/labels")
	public List<Label> doSyncLabels() {
		return labels.syncLables();
	}

	@At("/do/push")
	public Task doPush(@Param("tid") String taskId, @Param("s") String stackName) {
		return tasks.pushToStack(taskId, stackName);
	}

	@At("/do/pop")
	public Task doPop(@Param("tid") String taskId, @Param("done") boolean done) {
		return tasks.popFromStack(taskId, done);
	}

	@At("/do/hungup")
	public Task doHungup(@Param("tid") String taskId) {
		return tasks.hungupTask(taskId);
	}

	@At("/do/restart")
	public Task doRestart(@Param("tid") String taskId) {
		return tasks.restartTask(taskId);
	}

	@At("/do/comment")
	public Task doCommentTask(@Param("tid") String taskId, @Param("cmt") String comment) {
		return tasks.addComment(taskId, comment);
	}

	@AdaptBy(type = JsonAdaptor.class)
	@At("/task/query")
	public List<Task> queryTask(TaskQuery tq) {
		return tasks.queryTasks(tq);
	}

	@At("/task/set/text")
	public Task doSetTaskTitle(@Param("tid") String taskId, @Param("txt") String newText) {
		return tasks.setTaskText(taskId, newText);
	}

	@At("/task/set/labels")
	public Task doSetTaskLabels(@Param("tid") String taskId, @Param("lbs") String[] lbs) {
		return tasks.setTaskLabels(taskId, lbs);
	}

	@At("/task/set/owner")
	public Task doSetTaskOwner(@Param("tid") String taskId, @Param("ow") String ownerName) {
		return tasks.setTaskOwner(taskId, ownerName);
	}

	@At("/task/set/parent")
	public Task doSetTaskParent(@Param("tids") String[] taskIds, @Param("pid") String parentId) {
		tasks.setTasksParent(parentId, taskIds);
		return tasks.getTask(parentId);
	}

	/**
	 * 将一个任务上移一层，如果任务本身就是顶层，那么则直接返回
	 * 
	 * @param taskId
	 *            任务 ID
	 * @return 任务对象
	 */
	@At("/task/gout")
	public Task doGoutTask(@Param("tid") String taskId) {
		Task t = tasks.checkTask(taskId);
		if (Strings.isBlank(t.getParentId()))
			return t;
		Task p = tasks.checkTask(t.getParentId());
		tasks.setTasksParent(p.getParentId(), t.get_id());
		return t;
	}

	@At("/task/topnews")
	public List<Task> getTopNewTasks() {
		return tasks.getTopNewTasks();
	}

	@At("/task/children")
	public List<Task> getChildrenTasks(@Param("tid") String taskId, HttpServletRequest req) {
		return tasks.getChildTasks(taskId);
	}

	@At("/task/get")
	public List<Task> getTasks(@Param("tids") String[] taskIds) {
		if (null == taskIds) {
			return new LinkedList<Task>();
		}
		List<Task> list = new ArrayList<Task>(taskIds.length);
		for (String taskId : taskIds)
			list.add(tasks.getTask(taskId));
		return list;
	}

	@At("/task/self")
	public Task getTaskSelfAndChildren(@Param("tid") String taskId) {
		return tasks.loadTaskChildren(tasks.checkTask(taskId));
	}

	@At("/task/del")
	public Task removeTask(@Param("tid") String taskId, @Param("r") boolean recur) {
		return tasks.removeTask(taskId, recur);
	}

	@At("/task/save")
	public Task saveTask(	@Param("pid") String parentId,
							@Param("tt") String title,
							@Param("lbs") String[] labels,
							@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		// 检查: 空
		if (Strings.isBlank(title))
			throw Err.T.BLANK_TASK();
		// 检查: 过短
		if (title.length() < taskTitleMinLength)
			throw Err.T.SHORT_TASK(title.length());
		// 检查: 过长
		if (title.length() > taskTitleMaxLength)
			throw Err.T.LONG_TASK(title.length());
		// 检查: 父任务是否存在
		Task parent = null;
		if (!Strings.isBlank(parentId)) {
			parent = tasks.checkTask(parentId);
		}

		// 合适，那么我们来创建它
		Task t = new Task();
		t.setText(title);
		t.setLabels(labels);
		t.setCreater(me.getName());
		t.setOwner(me.getName());
		t.setParentId(null == parent ? null : parent.get_id());

		// 创建
		return tasks.createTask(t);
	}

	/**
	 * 返回一个堆栈详细的信息，结构为
	 * 
	 * <pre>
	 * {
	 *    stack : TaskStack,
	 *    tasks : Task[]
	 * }
	 * </pre>
	 * 
	 * @param stackName
	 *            堆栈名称
	 * @return 详细信息
	 */
	@At("/stack/detail")
	public Map<String, Object> getStackDetail(@Param("s") String stackName) {
		TaskStack s = tasks.checkStack(stackName);
		List<Task> ts = tasks.getTasksInStack(s, null);
		return Chain.make("stack", s).add("tasks", ts).toMap();
	}

	@At("/stack/children")
	public List<TaskStack> getChildrenStacks(@Param("s") String stackName) {
		return tasks.getChildStacks(stackName);
	}

	@At("/stack/tops")
	public List<TaskStack> getTopStacks() {
		return tasks.getTopStacks();
	}

	@At("/stack/mytops")
	public List<TaskStack> getTopStacks(@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return tasks.getStacksByOwner(me.getName());
	}

	@AdaptBy(type = JsonAdaptor.class)
	@At("/g/set")
	public GInfo setGlobalInfo(GInfo info) {
		if (null == info)
			info = new GInfo();
		if (!Strings.isBlank(info.get_id())) {
			GInfo info2 = tasks.dao().findOne(GInfo.class, null);
			info.set_id(info2.get_id());
		}
		return tasks.setGlobalInfo(info);
	}

	@At("/g/get")
	public GInfo getGlobalInfo(@Param("smtp") boolean showSmtp) {
		GInfo info = tasks.getGlobalInfo();
		if (null == info)
			info = new GInfo();
		if (!showSmtp)
			info.setSmtp(null);
		return info;
	}

}
