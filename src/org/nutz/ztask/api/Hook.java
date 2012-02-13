package org.nutz.ztask.api;

/**
 * 保存一个钩子的信息,确定一个钩子的方法，就是根据类型和处理器名称
 * <p>
 * 就是说，类型和处理器将保证了钩子的唯一性
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Hook {

	/**
	 * @return 钩子的内部标识号
	 */
	String getId();

	/**
	 * @return 钩子的唯一标识符，一般是钩子类型+处理器名
	 */
	String getName();

	/**
	 * @return 钩子类型
	 */
	HookType getType();

	/**
	 * @return 处理者详细信息
	 */
	String getHandler();

	/**
	 * 判断两个钩子是否相同。
	 * <p>
	 * 如果钩子类型和处理器名称都一样，那么这两个钩子就是相同的
	 * 
	 * @param hook
	 *            另外一个钩子
	 * @return 是否相同
	 */
	boolean isSame(Hook hook);

}
