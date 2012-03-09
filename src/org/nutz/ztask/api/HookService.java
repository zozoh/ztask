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
	 *            当前操作的任务
	 * @param Object
	 *            refer 参考对象，钩子可以从这个对象中获得更多的信息，当然，有些时候这个对象是 null
	 * 
	 * @return 处理进行时，null 表示没有钩子被触发
	 */
	Hooking doHook(HookType htp, Task t, Object refer);

	/**
	 * 根据名称获取一个处理器实例
	 * 
	 * @param handler
	 *            处理器名
	 * @return 处理器, null 表示不存在
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
	 * @return 旧钩子，null 表示木有这个数据
	 */
	Hook remove(HookType htp, String handler);

	/**
	 * 移除一个钩子
	 * 
	 * @param hookId
	 *            钩子的 ID
	 * @return 旧钩子，null 表示木有这个数据
	 */
	Hook removeById(String hookId);

	/**
	 * 增加一个钩子，如果系统中已经有这个钩子了，则
	 * 
	 * @param hook
	 *            钩子
	 * @return true 增加成功，fales 表示已经有这个钩子了，没必要增加
	 */
	boolean add(Hook hook);

	/**
	 * 根据一个钩子类型以及一个处理器名称，获取一个钩子对象
	 * 
	 * @param htp
	 *            钩子类型
	 * @param handler
	 *            处理器名
	 * @return 钩子
	 */
	Hook get(HookType htp, String handler);

	/**
	 * 清除所有的钩子
	 */
	void clear();

	/**
	 * 根据事件类型，获取某一类型的钩子
	 * 
	 * @param htp
	 *            钩子类型，如果为 null 表示获取全部钩子
	 * @return 钩子列表
	 */
	List<? extends Hook> list(HookType htp);

}
