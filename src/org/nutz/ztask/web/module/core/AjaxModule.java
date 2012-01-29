package org.nutz.ztask.web.module.core;

import java.util.List;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.Scope;
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

	@At("/set/title")
	public Task doSetTaskTitle(@Param("tid") String taskId, @Param("tt") String newTitle) {
		return tasks.setTaskTitle(taskId, newTitle);
	}

	@At("/set/labels")
	public Task doSetTaskLabels(@Param("tid") String taskId, @Param("lbs") String[] lbs) {
		return tasks.setTaskLabels(taskId, lbs);
	}

	@At("/set/owner")
	public Task doSetTaskOwner(@Param("tid") String taskId, @Param("ow") String ownerName) {
		return tasks.setTaskOwner(taskId, ownerName);
	}

	@At("/set/parent")
	public Task doSetTaskParent(@Param("tid") String taskId, @Param("pid") String parentId) {
		return tasks.setTaskParent(taskId, parentId);
	}

	@At("/task/tops")
	public List<Task> getTopTasks(@Param("s") String stackName, @Param("a") TaskStatus au) {
		return tasks.getTopTasks(stackName, au);
	}

	@At("/task/children")
	public List<Task> getChildrenTasks(@Param("tid") String taskId) {
		return tasks.getChildTasks(taskId);
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
		t.setTitle(title);
		t.setLabels(labels);
		t.setOwner(me.getName());
		t.setParentId(null == parent ? null : parent.get_id());

		// 创建
		return tasks.createTask(t);
	}
}
