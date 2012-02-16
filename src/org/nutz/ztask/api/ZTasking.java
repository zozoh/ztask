package org.nutz.ztask.api;

import java.util.Date;

import org.nutz.ioc.Ioc;
import org.nutz.lang.util.SimpleContext;

public class ZTasking extends SimpleContext {

	private ZTaskFactory factory;

	public ZTasking(ZTaskFactory factory) {
		this.factory = factory;
	}

	/**
	 * 整个钩子列表开始处理的时间
	 */
	private Date startTime;

	/**
	 * 整个钩子列表结束处理的时间
	 */
	private Date endTime;

	/**
	 * 缓存 ginfo
	 */
	private GInfo ginfo;

	public GInfo ginfo() {
		if (null == ginfo)
			ginfo = factory.tasks().getGlobalInfo();
		return ginfo;
	}

	public ZTaskFactory factory() {
		return factory;
	}

	public Ioc getIoc() {
		return this.getAs(Ioc.class, "$ioc");
	}

	public void setIoc(Ioc ioc) {
		this.set("$ioc", ioc);
	}

	public Object getRefer() {
		return this.get("$refer");
	}

	public <T> T getReferAs(Class<T> referType) {
		return this.getAs(referType, "$refer");
	}

	public String getReferString() {
		return getReferAs(String.class);
	}

	public int getReferInt() {
		return getReferAs(Integer.class);
	}

	public Date startTime() {
		return startTime;
	}

	public Date endTime() {
		return endTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}