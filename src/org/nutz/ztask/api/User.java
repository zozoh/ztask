package org.nutz.ztask.api;

import org.nutz.lang.meta.Email;

/**
 * 封装了一个帐号的信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface User {

	public String getName();

	public String getPassword();
	
	public boolean isSuperUser();

	public Email getEmail();

	public String getDescription();

	public String getMainStackName();

}
