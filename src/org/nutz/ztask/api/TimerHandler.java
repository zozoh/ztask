package org.nutz.ztask.api;

/**
 * 定时器的处理器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface TimerHandler {

	/**
	 * 处理器需要实现的方法
	 * 
	 * @param name
	 *            当前处理器名称
	 * @param ing
	 *            定时器处理进行时
	 * @return 运行的结果信息，null 仅仅表示 OK
	 */
	String doHandle(String name, Timering ing);

}
