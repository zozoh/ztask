package org.nutz.ztask.api;

/**
 * 一个任务的几种状态
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public enum TaskStatus {

	/**
	 * 新建立: 这样的任务，用户可以分配到其他堆栈
	 */
	NEW,

	/**
	 * 正在处理: 已经加入某个栈的任务
	 */
	ING,

	/**
	 * 已完成: 与 NEW 一样不在任何栈中，但是它表示任务被标识完成了
	 */
	DONE

}
