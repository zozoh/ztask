package org.nutz.ztask.impl;

import org.nutz.lang.meta.Email;
import org.nutz.ztask.api.User;

public class ReadonlyUser implements User {

	private boolean superUser;

	private String name;

	private String password;

	private Email email;

	private String description;

	public boolean isSuperUser() {
		return superUser;
	}

	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}

	public String getMainStackName() {
		return name;
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

	public String toString() {
		return String.format("@%s%s(%s)", this.isSuperUser() ? "*" : "", name, email);
	}
}
