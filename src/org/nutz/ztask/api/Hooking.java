package org.nutz.ztask.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;
import org.nutz.ztask.util.ZTasks;

/**
 * 钩子处理器处理时的上下文对象
 * <p>
 * 在一次处理中，所有的钩子处理器将共享这个上下文对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Hooking extends ZTasking {

	public Hooking(ZTaskFactory factory) {
		super(factory);
		this.hookIndex = -1;
	}

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
		return factory().users().get(user);
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

	/**
	 * 从一段文本中查找一组被提及的用户
	 * <ul>
	 * <li>用 '@xxx' 声明得有可能是用户
	 * <li>这个用户必须存在
	 * <li>或者他可能是一个用户组
	 * </ul>
	 * 
	 * @param str
	 *            文本
	 * @return 用户列表
	 */
	public Map<String, User> extractUsers(String str) {
		// 准备返回
		Map<String, User> map = new HashMap<String, User>();
		if (Strings.isBlank(str))
			return map;

		// 得到用户的名称串
		String[] unms = ZTasks.findUserName(str);
		// 归纳...
		for (String unm : unms) {
			User u = factory().users().get(unm);
			if (null != u)
				map.put(unm, u);
			// 看看是不是分组
			List<String> userNames = ginfo().getUserByGroup(unm);
			if (null != userNames)
				for (String userName : userNames) {
					u = factory().users().get(userName);
					if (null != u)
						map.put(userName, u);
				}
		}
		// 返回
		return map;
	}

	public String toString() {
		if (null == list || list.length == 0)
			return "<Empty Hooking>";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.length; i++) {
			if (i > 0)
				sb.append(",");
			if (i == hookIndex)
				sb.append("*");
			sb.append(list[i]);
		}
		return String.format(	"Hooking(%d) [%s] @%s/%s",
								hookIndex,
								sb.toString(),
								null == startTime() ? "--" : Castors.me().castToString(startTime()),
								null == endTime() ? "--" : Castors.me().castToString(endTime()));
	}
}
