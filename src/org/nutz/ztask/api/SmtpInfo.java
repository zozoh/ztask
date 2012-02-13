package org.nutz.ztask.api;

import org.nutz.lang.Strings;

/**
 * 描述了 SMTP 服务器的信息，用来发送通知邮件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SmtpInfo {

	public SmtpInfo() {
		port = 25;
	}

	private String host;

	private int port;

	private String account;

	private String alias;

	private String password;

	private int timeout;

	public boolean isAvaliable() {
		return !Strings.isBlank(host)
				&& !Strings.isBlank(account)
				&& !Strings.isBlank(password)
				&& port > 0;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout < 1000 ? 0 : timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
