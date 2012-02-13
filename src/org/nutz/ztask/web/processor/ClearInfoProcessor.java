package org.nutz.ztask.web.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.ztask.util.ZTasks;

/**
 * 清除当前线程设置操作用户
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ClearInfoProcessor extends AbstractProcessor {

	@Override
	public void process(ActionContext ac) throws Throwable {
		ZTasks.clearME();
		ZTasks.clearMsgs();
		doNext(ac);
	}

}
