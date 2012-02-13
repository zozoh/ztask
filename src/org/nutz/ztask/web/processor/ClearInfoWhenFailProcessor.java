package org.nutz.ztask.web.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.FailProcessor;
import org.nutz.ztask.util.ZTasks;

/**
 * 清除当前线程设置操作用户，并处理错误
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ClearInfoWhenFailProcessor extends FailProcessor {

	@Override
	public void process(ActionContext ac) throws Throwable {
		ZTasks.clearME();
		ZTasks.clearMsgs();
		super.process(ac);
	}

}
