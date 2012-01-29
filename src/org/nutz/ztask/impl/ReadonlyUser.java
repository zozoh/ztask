package org.nutz.ztask.impl;

import org.nutz.lang.meta.Email;
import org.nutz.ztask.api.User;

public class ReadonlyUser implements User {

	private String name;

	private String password;

	private Email email;

	private String description;

	public String getMainStackName() {
		return "u_" + name;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
