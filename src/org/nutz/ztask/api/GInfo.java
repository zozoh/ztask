package org.nutz.ztask.api;

import java.util.Date;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Callback3;
import org.nutz.mongo.annotation.*;
import org.nutz.quartz.Quartz;

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
	 * 周报的发送目的帐号
	 */
	@CoField
	private String weeklyTo;

	/**
	 * 如何格式化站内文本
	 */
	@CoField
	private GFormatInfo[] formats;

	/**
	 * SMTP 服务的信息
	 */
	@CoField
	private SmtpInfo smtp;

	/**
	 * 时间戳
	 */
	@CoField("lm")
	private Date lastModified;

	/**
	 * 声明一组钩子
	 * <p>
	 * 每个字符串的格式为 "类型:处理器名"
	 * <p>
	 * 比如 "LABEL:syncLabel" <br>
	 * 类型大小写不敏感，但是处理器名是 Ioc 容器的对象，大小写敏感
	 */
	@CoField("hooks")
	private String[] hooks;

	/**
	 * 声明一组定时器
	 * <p>
	 * 每个字符串的格式为 "Quartz 表达式::处理器名, 处理器名"
	 * <p>
	 * 比如 "0 4,5 * * * ? :: sendMail, makeMail"
	 * <ul>
	 * <li>Quartz 表达式与处理器名部分的分隔符为连续两个半角冒号，左右两边，会被去掉空白再做处理
	 * <li>如果处理器名为空白，则这条规则无效
	 * <li>处理器名是 Ioc 容器的对象，大小写敏感
	 * </ul>
	 */
	@CoField("timers")
	private String[] timers;

	/**
	 * 迭代自己内部的所有的定时器配置信息，并检查其有效性
	 * 
	 * @param ioc
	 *            Ioc 容器用来检查 TimerHander 名称的有效性
	 * @param callback
	 *            回调用来后续处理
	 */
	public void eachTimer(Ioc ioc, Callback3<Integer, Quartz, String[]> callback) {
		if (null == timers || timers.length == 0)
			return;

		for (int i = 0; i < timers.length; i++) {
			String timer = timers[i];
			String[] ss = Strings.splitIgnoreBlank(timer, "::");

			if (ss.length != 2)
				continue;

			// 检查 Quartz 表达式
			Quartz qz = Quartz.NEW(ss[0]);

			// 检查 handler
			String[] handlerNames = Strings.splitIgnoreBlank(ss[1], ",");
			for (String handlerName : handlerNames)
				ioc.get(TimerHandler.class, handlerName);

			// 调用回调
			if (callback != null)
				callback.invoke(i, qz, handlerNames);
		}
	}

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

	public SmtpInfo getSmtp() {
		return smtp;
	}

	public void setSmtp(SmtpInfo smtp) {
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

	public String[] getTimers() {
		return timers;
	}

	public void setTimers(String[] timers) {
		this.timers = timers;
	}

	public String getWeeklyTo() {
		return weeklyTo;
	}

	public void setWeeklyTo(String weeklyTo) {
		this.weeklyTo = weeklyTo;
	}
	
	

}
