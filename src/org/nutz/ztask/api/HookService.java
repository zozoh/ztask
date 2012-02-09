package org.nutz.ztask.api;

import java.util.List;

/**
 * 对于 Hook 的操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface HookService {

	/**
	 * 对一个任务， 找到系统中所有相关的钩子，并进行处理
	 * 
	 * @param htp
	 *            钩子类型
	 * @param t
	 *            任务
	 * @return 处理进行时，null 表示没有钩子被触发
	 */
	Hooking doHook(HookType htp, Task t);

	/**
	 * 根据名称获取一个处理器实例
	 * 
	 * @param handler
	 *            处理器名
	 * @return 处理器
	 */
	HookHandler getHandler(String handler);

	/**
	 * @param handler
	 *            处理器名
	 * @return 是否存在某个处理器
	 */
	boolean hasHandler(String handler);

	/**
	 * 移除一个钩子
	 * 
	 * @param htp
	 *            钩子类型
	 * @param handler
	 *            处理器名
	 * @return true 移除成功，fales 表示没有这个钩子
	 */
	boolean removeHook(HookType htp, String handler);

	/**
	 * 增加一个钩子，如果系统中已经有这个钩子了，则
	 * 
	 * @param hook
	 *            钩子
	 * @return true 增加成功，fales 表示已经有这个钩子了，没必要增加
	 */
	boolean addHook(Hook hook);

	/**
	 * 根据一个钩子类型以及一个处理器名称，获取一个钩子对象
	 * 
	 * @param htp
	 *            钩子类型
	 * @param handler
	 *            处理器名
	 * @return 钩子
	 */
	Hook getHook(HookType htp, String handler);

	/**
	 * 清除所有的钩子
	 */
	void clearHooks();

	/**
	 * 根据事件类型，获取某一类型的钩子
	 * 
	 * @param htp
	 *            钩子类型，如果为 null 表示获取全部钩子
	 * @return 钩子列表
	 */
	List<? extends Hook> getHooks(HookType htp);

}
