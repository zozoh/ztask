package org.nutz.ztask.api;

import java.util.Date;

import org.nutz.mongo.annotation.*;

/**
 * 消息对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Co("msg")
public class Message {

	/**
	 * 消息 ID
	 */
	@CoId
	private String _id;

	/**
	 * 消息文本
	 */
	@CoField("txt")
	private String text;

	/**
	 * 消息的所有者
	 */
	@CoField("ow")
	private String owner;

	/**
	 * 消息创建时间
	 */
	@CoField("ct")
	private Date createTime;

	/**
	 * 消息是否已读
	 */
	@CoField("read")
	private boolean read;

	/**
	 * 消息是否已被收藏
	 */
	@CoField("favo")
	private boolean favorite;

	/**
	 * 消息被邮件通知的时间（给消息通知进程留的）
	 */
	@CoField("noti")
	private Date notified;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getNotified() {
		return notified;
	}

	public void setNotified(Date notified) {
		this.notified = notified;
	}

	public boolean isNotified() {
		return null != this.notified;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public String toString() {
		return String.format(	"@%s(%s%s) %s [%s]",
								owner,
								(favorite ? "*" : " "),
								(read ? "+" : " "),
								text,
								_id);
	}

}
