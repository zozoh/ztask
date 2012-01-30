package org.nutz.ztask.api;

/**
 * 描述了 SMTP 服务器的信息，用来发送通知邮件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class GSmtp {

	private String host;

	private String port;

	private String account;

	private String password;

	/**
	 * 轮询时间间隔，单位秒，不能小于 300 秒
	 */
	private int interval;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

}
