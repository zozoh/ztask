package org.nutz.ztask.impl.mongo;

import org.nutz.mail.MailObj;
import org.nutz.mongo.annotation.*;

@Co("mail")
public class MongoMailObj implements MailObj {

	@CoId
	private String _id;

	@CoField
	private String subject;

	@CoField
	private String mailBody;

	@CoField
	private String[] tos;

	@CoField
	private String[] ccs;

	@CoField
	private String[] bccs;

	@CoField("res")
	private String[] replyTos;

	@CoField("retry")
	private int retryCount;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public String[] getTos() {
		return tos;
	}

	public void setTos(String[] tos) {
		this.tos = tos;
	}

	public String[] getCcs() {
		return ccs;
	}

	public void setCcs(String[] ccs) {
		this.ccs = ccs;
	}

	public String[] getBccs() {
		return bccs;
	}

	public void setBccs(String[] bccs) {
		this.bccs = bccs;
	}

	public String[] getReplyTos() {
		return replyTos;
	}

	public void setReplyTos(String[] replyTos) {
		this.replyTos = replyTos;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

}
