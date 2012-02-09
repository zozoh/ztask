package org.nutz.ztask.api;

/**
 * 钩子的处理器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface HookHandler {

	/**
	 * 处理器需要实现的方法
	 * 
	 * @param htp
	 *            钩子类型
	 * @param name
	 *            当前处理器名称
	 * @param ing
	 *            处理进行时
	 */
	void doHandle(HookType htp, String name, Hooking ing);

}
