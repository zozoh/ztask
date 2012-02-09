package org.nutz.ztask.api;

import java.util.Date;

import org.nutz.mongo.annotation.*;

/**
 * 一个数据库的全局配置信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Co("ginfo")
public class GInfo {

	@CoId
	private String _id;

	/**
	 * 组织名称
	 */
	@CoField
	private String name;

	/**
	 * 如何格式化站内文本
	 */
	@CoField
	private GFormatInfo[] formats;

	@CoField
	private GSmtp smtp;

	@CoField("lm")
	private Date lastModified;

	/**
	 * 声明一组钩子
	 * <p>
	 * 每个字符串的格式为 "类型:处理器名"
	 * <p>
	 * 比如 "LABEL:syncLabel" , 类型大小写不敏感
	 */
	@CoField("hooks")
	private String[] hooks;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GFormatInfo[] getFormats() {
		return formats;
	}

	public void setFormats(GFormatInfo[] formats) {
		this.formats = formats;
	}

	public GSmtp getSmtp() {
		return smtp;
	}

	public void setSmtp(GSmtp smtp) {
		this.smtp = smtp;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String[] getHooks() {
		return hooks;
	}

	public void setHooks(String[] hooks) {
		this.hooks = hooks;
	}

}
