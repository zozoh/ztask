package org.nutz.ztask.api;

/**
 * 用户服务接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface UserService {

	/**
	 * 根据给定的用户名和密码，来判断是否存在这样的用户
	 * 
	 * @param name
	 *            用户名
	 * @param password
	 *            密码
	 * @return 用户对象，null 表示该用户不存在或者密码不正确
	 */
	User verify(String name, String password);

	/**
	 * 根据给定的用户名得到一个用户对象
	 * 
	 * @param name
	 *            用户名
	 * @return 用户对象, null 表示该用户不存在
	 */
	User get(String name);

}
