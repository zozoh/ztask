package org.nutz.mail;

public interface MailQueue {

	MailObj saveMail(MailObj mo);

	void clear();

	void each(EachMail callback);

}
