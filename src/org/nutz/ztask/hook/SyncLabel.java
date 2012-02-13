package org.nutz.ztask.hook;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Hooking;

/**
 * 同步整个系统的 Label 数据
 * <p>
 * 通常建议这个钩子部署在 Task 的 label 发生改变的时候
 * <p>
 * 如果你的系统数据量比较大，不建议使用这个钩子
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name="h_sync_label")
public class SyncLabel implements HookHandler {

	@Override
	public void doHandle(HookType htp, String name, Hooking ing) {
		ing.factory().labels().syncLables();
	}

}
