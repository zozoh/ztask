package org.nutz.mail;

/**
 * 描述了一个邮件，邮件的帐号，用字符串标识
 * 
 * <pre>
 * 帐号[::别名]
 * 比如
 * 
 *     zozohtnt@gmail.com::zozoh
 * 
 * 或者
 * 
 *     zozoh@263.net
 *     
 * 或者 
 *     
 *     ::zozoh
 * 
 * 都是合法帐号
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface MailObj {

	public int getRetryCount();

	public void setRetryCount(int retryCount);

	public String getSubject();

	public void setSubject(String subject);

	public String getMailBody();

	public void setMailBody(String mailBody);

	public String[] getTos();

	public void setTos(String[] tos);

	public String[] getCcs();

	public void setCcs(String[] ccs);

	public String[] getBccs();

	public void setBccs(String[] bccs);

	public String[] getReplyTos();

	public void setReplyTos(String[] replyTos);

}
