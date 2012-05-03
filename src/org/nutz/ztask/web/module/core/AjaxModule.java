package org.nutz.ztask.web.module.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.web.Webs;
import org.nutz.web.ajax.AjaxCheckSession;
import org.nutz.ztask.api.*;
import org.nutz.ztask.util.Err;
import org.nutz.ztask.util.ZTasks;

@Filters(@By(type = AjaxCheckSession.class, args = Webs.ME))
@InjectName
@IocBean
@Ok("ajax")
@Fail("ajax")
@At("/ajax")
@Chain("ajax")
public class AjaxModule {

	@Inject("java:$conf.getInt('sys-task-len-min')")
	private int taskTitleMinLength;

	@Inject("java:$conf.getInt('sys-task-len-max')")
	private int taskTitleMaxLength;

	@Inject("refer:serviceFactory")
	private ZTaskFactory factory;

	@At("/report/year")
	public List<TaskReport> getReports(@Param("yy") String year) {
		Calendar from = Times.C(year + "-01-01 00:00:00");
		Calendar to = Times.C(year + "-12-31 23:59:59");
		return factory.reportor().getBy(from, to);
	}

	@At("/message/count")
	public long getMessageCount(@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return factory.messages().countNew(me.getName());
	}

	@At("/message/list")
	public List<Message> getMessageList(@Param("kwd") String keyword,
										@Param("lstId") String lastMsgId,
										@Param("asc") boolean asc,
										@Param("lmt") int limit,
										@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return factory.messages().list(me.getName(), keyword, lastMsgId, asc, limit);
	}

	@At("/message/set/read")
	public Message doSetMessageRead(@Param("mid") String msgId,
									@Param("read") boolean read,
									@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		MessageService msgs = factory.messages();
		Message msg = msgs.get(msgId);
		if (null == msg) {
			msgs.setAllRead(me.getName(), read);
		} else {
			msgs.setRead(msg, read);
		}
		return null == msg ? null : msgs.get(msg.get_id());
	}

	@At("/message/set/favo")
	public Message doSetMessageRavo(@Param("mid") String msgId, @Param("favo") boolean favo) {
		MessageService msgs = factory.messages();
		Message msg = msgs.get(msgId);
		if (null != msg) {
			msgs.setFavorite(msg, favo);
		}
		return null == msg ? null : msgs.get(msg.get_id());
	}

	@At("/message/del")
	public Message doSetMessageRead(@Param("mid") String msgId,
									@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return factory.messages().remove(msgId);
	}

	@At("/message/clear")
	public void doClearAllMyMessage(@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		factory.messages().clearMine(me.getName());
	}

	@At("/label/all")
	public List<Label> getAllLabels() {
		return factory.labels().all();
	}

	@At("/label/children")
	public List<Label> getChildrenLabels(@Param("lbnm") String labelName) {
		return factory.labels().list(labelName);
	}

	@At("/do/sync/labels")
	public void doSyncLabels() {
		factory.labels().syncLables();
	}

	@At("/label/group")
	public void doGroupLabels(	@Param("grpas") String grpas,
								@Param("mynm") String mynm,
								@Param("tanm") String tanm) {
		LabelService lbs = factory.labels();
		// 拖动到我里面
		if (Strings.isBlank(grpas)) {
			lbs.joinTo(Strings.sBlank(mynm, null), tanm);
		}
		// 我和它都加入新组
		else {
			lbs.joinTo(grpas, mynm, tanm);
		}
	}

	@At("/label/ungroup")
	public void doUngroupLabel(@Param("lbnm") String lbnm) {
		factory.labels().joinTo(null, lbnm);
	}

	@At("/label/rename")
	public Label doRenameLabel(@Param("lbnm") String lbnm, @Param("new_lbnm") String newName) {
		return factory.labels().rename(lbnm, newName);
	}

	@At("/do/push")
	public Task doPush(@Param("tid") String taskId, @Param("s") String stackName) {
		return factory.htasks().pushToStack(factory.htasks().checkTask(taskId), stackName);
	}

	@At("/do/pop")
	public Task doPop(@Param("tid") String taskId, @Param("done") boolean done) {
		return factory.htasks().popFromStack(taskId, done);
	}

	@At("/do/hungup")
	public Task doHungup(@Param("tid") String taskId) {
		return factory.htasks().hungupTask(factory.htasks().checkTask(taskId));
	}

	@At("/do/restart")
	public Task doRestart(@Param("tid") String taskId) {
		return factory.htasks().restartTask(factory.htasks().checkTask(taskId));
	}

	@At("/do/comment/add")
	public String doAddComment(	@Param("tid") String taskId,
								@Param("txt") String text,
								@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		text = ZTasks.wrapComment(text, me.getName());
		factory.htasks().addComment(factory.tasks().checkTask(taskId), text);
		return text;
	}

	@At("/do/comment/del")
	public Task doDeleteComment(@Param("tid") String taskId, @Param("i") int index) {
		return factory.htasks().deleteComments(factory.tasks().checkTask(taskId), index);
	}

	@At("/do/comment/set")
	public String doSetComment(	@Param("tid") String taskId,
								@Param("i") int index,
								@Param("txt") String text,
								@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		text = ZTasks.wrapComment(text, me.getName());
		factory.htasks().setComment(factory.tasks().checkTask(taskId),
									index,
									ZTasks.wrapComment(text, me.getName()));
		return text;
	}

	@At("/task/do/watch")
	public Task doWatchTask(@Param("tid") String taskId,
							@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return factory.htasks().addWatchers(factory.tasks().checkTask(taskId), me.getName());
	}

	@At("/task/do/unwatch")
	public Task doUnWatchTask(	@Param("tid") String taskId,
								@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return factory.htasks().removeWatchers(factory.tasks().checkTask(taskId), me.getName());
	}

	@AdaptBy(type = JsonAdaptor.class)
	@At("/task/query")
	public List<Task> queryTask(TaskQuery tq) {
		return factory.htasks().queryTasks(tq);
	}

	@At("/task/set/text")
	public Task doSetTaskText(@Param("tid") String taskId, @Param("txt") String newText) {
		return factory.htasks().setText(factory.htasks().checkTask(taskId), newText);
	}

	@At("/task/set/labels")
	public Task doSetTaskLabels(@Param("tid") String taskId, @Param("lbs") String[] lbs) {
		TaskService tasks = factory.htasks();
		return tasks.setLabels(tasks.checkTask(taskId), lbs);
	}

	@At("/task/set/owner")
	public Task doSetTaskOwner(@Param("tid") String taskId, @Param("ow") String ownerName) {
		return factory.htasks().setOwner(factory.htasks().checkTask(taskId), ownerName);
	}

	@At("/task/set/parent")
	public Task doSetTaskParent(@Param("tids") String[] taskIds, @Param("pid") String parentId) {
		TaskService tasks = factory.htasks();
		tasks.setParentTask(tasks.checkTask(parentId), tasks.checkTasks(taskIds));
		return tasks.getTask(parentId);
	}

	@At("/task/set/planat")
	public Task doSetTaskPlanAt(@Param("tid") String taskId, @Param("planat") Date planat) {
		TaskService tasks = factory.htasks();
		tasks.setPlanAt(tasks.checkTask(taskId), planat);
		return tasks.getTask(taskId);
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
		TaskService tasks = factory.htasks();
		Task t = tasks.checkTask(taskId);
		if (t.isTop())
			return t;
		Task p = tasks.checkTask(t.getParentId());
		Task pp = factory.tasks().getTask(p.getParentId());
		tasks.setParentTask(pp, t);
		return t;
	}

	@At("/task/children")
	public List<Task> getChildrenTasks(@Param("tid") String taskId, HttpServletRequest req) {
		return factory.htasks().getChildTasks(taskId);
	}

	@At("/task/get")
	public List<Task> getTasks(@Param("tids") String[] taskIds) {
		if (null == taskIds) {
			return new LinkedList<Task>();
		}
		List<Task> list = new ArrayList<Task>(taskIds.length);
		for (String taskId : taskIds)
			list.add(factory.htasks().getTask(taskId));
		return list;
	}

	@At("/task/self")
	public Task getTaskSelfAndChildren(@Param("tid") String taskId, @Param("recur") boolean recur) {
		return factory.htasks().loadTaskChildren(factory.htasks().checkTask(taskId), recur);
	}

	@At("/task/del")
	public Task removeTask(@Param("tid") String taskId, @Param("r") boolean recur) {
		return factory.htasks().removeTask(taskId, recur);
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
			parent = factory.htasks().checkTask(parentId);
		}

		// 合适，那么我们来创建它
		Task t = new Task();
		t.setText(title);
		t.setLabels(labels);
		t.setCreater(me.getName());
		t.setOwner(me.getName());
		t.setParentId(null == parent ? null : parent.get_id());

		// 创建
		return factory.htasks().createTask(t);
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
		TaskStack s = factory.htasks().checkStack(stackName);
		List<Task> ts = factory.htasks().getTasksInStack(s, null);
		return org.nutz.dao.Chain.make("stack", s).add("tasks", ts).toMap();
	}

	@At("/stack/children")
	public List<TaskStack> getChildrenStacks(@Param("s") String stackName) {
		return factory.htasks().getChildStacks(stackName);
	}

	@At("/stack/query")
	public List<TaskStack> queryStacks(	@Param("mine") boolean mine,
										@Param("favo") boolean favo,
										@Param("snms") String[] snms,
										@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		// 精确读取
		if (null != snms && snms.length > 0) {
			List<TaskStack> list = new ArrayList<TaskStack>(snms.length);
			for (String snm : snms) {
				TaskStack s = factory.htasks().getStack(snm);
				if (null != s)
					list.add(s);
			}
			return list;
		}

		// 读取我收藏的
		if (favo)
			return factory.htasks().getMyFavoStacks(me.getName());

		// 读取根或者属于我的
		return factory.htasks().getStacks(!mine, (mine ? me.getName() : null));
	}

	/**
	 * 关注一个堆栈
	 * 
	 * @param stackName
	 *            堆栈名
	 * @param me
	 *            当前会话帐号
	 * @return 堆栈对象
	 */
	@At("/stack/do/watch")
	public TaskStack doWatchStack(	@Param("s") String stackName,
									@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return factory.htasks().watchStack(factory.htasks().checkStack(stackName), me.getName());
	}

	/**
	 * 取消关注一个堆栈
	 * 
	 * @param stackName
	 *            堆栈名
	 * @param me
	 *            当前会话帐号
	 * @return 堆栈对象
	 */
	@At("/stack/do/unwatch")
	public TaskStack doUnwatchStack(@Param("s") String stackName,
									@Attr(scope = Scope.SESSION, value = Webs.ME) User me) {
		return factory.htasks().unwatchStack(factory.htasks().checkStack(stackName), me.getName());
	}

	@AdaptBy(type = JsonAdaptor.class)
	@At("/g/set")
	public GInfo setGlobalInfo(GInfo info) {
		if (null == info)
			info = new GInfo();
		if (!Strings.isBlank(info.get_id())) {
			GInfo info2 = factory.htasks().dao().findOne(GInfo.class, null);
			info.set_id(info2.get_id());
		}
		return factory.htasks().setGlobalInfo(info);
	}

	@At("/g/get")
	public GInfo getGlobalInfo(@Param("smtp") boolean showSmtp) {
		GInfo info = factory.htasks().getGlobalInfo();
		if (null == info)
			info = new GInfo();
		if (!showSmtp)
			info.setSmtp(null);
		return info;
	}

}
