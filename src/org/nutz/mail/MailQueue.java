package org.nutz.mail;

public interface MailQueue {

	MailObj joinMail(MailObj mo);
	
	MailObj dropMail(MailObj mo);

	void clear();
	
	long count();

	void each(EachMail callback);

}
