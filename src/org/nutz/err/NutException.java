package org.nutz.err;

public class NutException extends RuntimeException {

	private static final long serialVersionUID = 3343036182101828118L;

	private String key;

	private String reason;

	public NutException() {
		super();
	}

	public NutException(Throwable cause) {
		super(cause);
	}

	public String getKey() {
		return key;
	}

	public NutException key(String key) {
		this.key = key;
		return this;
	}

	public String getReason() {
		return this.reason;
	}

	public NutException reasonf(String fmt, Object... args) {
		this.reason = String.format(fmt, args);
		return this;
	}

	public NutException reason(Object msg) {
		this.reason = null == msg ? null : msg.toString();
		return this;
	}

}
