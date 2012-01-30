package org.nutz.ztask.api;

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
	private String lastModified;

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

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

}
