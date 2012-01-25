package org.nutz.ztask.api;

import org.nutz.lang.meta.Email;

/**
 * 封装了一个帐号的信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class User {

	private String name;

	private String password;

	private Email email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

}
