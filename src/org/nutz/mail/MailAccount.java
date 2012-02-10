package org.nutz.mail;

import org.nutz.lang.Strings;

public class MailAccount {

	public MailAccount() {}

	public MailAccount(String account) {
		this.account = account;
	}

	public MailAccount(String account, String alias) {
		this.account = account;
		this.alias = alias;
	}

	private String account;

	private String alias;

	public boolean hasAccount() {
		return !Strings.isBlank(account);
	}

	public boolean hasAlias() {
		return !Strings.isBlank(alias);
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
