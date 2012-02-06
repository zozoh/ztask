package org.nutz.ztask.api;

import java.util.Date;

import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.lang.util.SimpleContext;

/**
 * 钩子处理器处理时的上下文对象
 * <p>
 * 在一次处理中，所有的钩子处理器将共享这个上下文对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Hooking extends SimpleContext {

	public Hooking() {
		super();
		this.hookIndex = -1;
	}

	/**
	 * 整个钩子列表开始处理的时间
	 */
	private Date startTime;

	/**
	 * 整个钩子列表结束处理的时间
	 */
	private Date endTime;

	/**
	 * 正在处理的钩子
	 */
	private int hookIndex;

	/**
	 * 本次处理所有的钩子
	 */
	private Hook[] list;

	/**
	 * 正在处理的任务对象（可能为 null）
	 */
	private Task t;

	/**
	 * 正在处理的堆栈对象（可能为 null）
	 */
	private TaskStack s;

	/**
	 * 正在处理的用户名（可能为 null）
	 */
	private String user;

	/**
	 * 钩子服务访问接口
	 */
	private HookService hooks;

	/**
	 * 标签访问接口
	 */
	private LabelService labels;

	/**
	 * 用户访问接口
	 */
	private UserService users;

	/**
	 * 任务访问接口
	 */
	private TaskService tasks;

	/**
	 * 一个帮助方法，快速获取 Ioc 容器接口
	 * <p>
	 * 当然，前提是你要设置了 Ioc 容器
	 * 
	 * @return Ioc 容器接口
	 */
	public Ioc getIoc() {
		return getAs(Ioc.class, "$ioc");
	}

	public void setIoc(Ioc ioc) {
		this.set("$ioc", ioc);
	}

	public Date startTime() {
		return startTime;
	}

	public Date endTime() {
		return endTime;
	}

	public int hookIndex() {
		return hookIndex;
	}

	public Hook[] list() {
		return list;
	}

	public Task t() {
		return t;
	}

	public TaskStack s() {
		return s;
	}

	public String user() {
		return user;
	}

	public User u() {
		if (null == user)
			return null;
		return users.get(user);
	}

	/**
	 * @return 当前的 Hook
	 */
	public Hook hook() {
		if (hookIndexIsOutOfRange())
			return null;
		return list[hookIndex];
	}

	/**
	 * @return 当前的 hookIndex 是否超过了界限
	 */
	public boolean hookIndexIsOutOfRange() {
		return null == list || hookIndex < 0 || hookIndex >= list.length;
	}

	/**
	 * 将 index 指向下一个 Hook并返回
	 * 
	 * @return 当前的 Hook
	 */
	public Hook nextHook() {
		if (hookIndex >= 0 && hookIndexIsOutOfRange())
			return null;
		hookIndex++;
		return hook();
	}

	public HookService hooks() {
		return hooks;
	}

	public LabelService labels() {
		return labels;
	}

	public UserService users() {
		return users;
	}

	public TaskService tasks() {
		return tasks;
	}

	public void setHookIndex(int hookIndex) {
		this.hookIndex = hookIndex;
	}

	public void setList(Hook[] hookList) {
		this.list = hookList;
	}

	public void setT(Task t) {
		this.t = t;
	}

	public void setS(TaskStack s) {
		this.s = s;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setHooks(HookService hooks) {
		this.hooks = hooks;
	}

	public void setLabels(LabelService labels) {
		this.labels = labels;
	}

	public void setUsers(UserService users) {
		this.users = users;
	}

	public void setTasks(TaskService tasks) {
		this.tasks = tasks;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String toString() {
		if (null == list || list.length == 0)
			return "<Empty Hooking>";
		String s = "";
		for (int i = 0; i < list.length; i++) {
			if (i > 0)
				s += ",";
			if (i == hookIndex)
				s += "*";
			s += list[i];
		}
		return String.format(	"Hooking(%d) [%s] @%s/%s",
								hookIndex,
								s,
								null == startTime ? "--" : Castors.me().castToString(startTime),
								null == endTime ? "--" : Castors.me().castToString(endTime));
	}
}
