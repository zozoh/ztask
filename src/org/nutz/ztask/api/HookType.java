package org.nutz.ztask.api;

/**
 * 钩子的类型
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public enum HookType {

	/**
	 * 当一个任务的标签被更改后调用
	 */
	LABEL,

	/**
	 * 当一个任务的 owner 被主动改变时，调用。（被动改变不会被调用）
	 */
	OWNER,

	/**
	 * 当一个任务内容被更新后调用
	 */
	UPDATE,

	/**
	 * 当一个任务重新开始后，调用
	 */
	RESTART,

	/**
	 * 当一个任务挂起后调用
	 */
	HUNGUP,

	/**
	 * 当一个任务压栈后调用
	 */
	PUSH,

	/**
	 * 当一个任务完成后调用 (用 done==true 的方式弹栈)
	 */
	DONE,

	/**
	 * 当一个任务被拒绝后调用 (用 done==false 的方式弹栈)
	 */
	REJECT,

	/**
	 * 当一个任务被创建后调用
	 */
	CREATE,

	/**
	 * 当一个任务被删除前调用
	 */
	DROP

}
