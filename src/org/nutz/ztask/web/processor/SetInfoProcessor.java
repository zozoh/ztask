package org.nutz.ztask.web.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.NutMessageMap;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.web.Webs;
import org.nutz.ztask.api.User;
import org.nutz.ztask.util.ZTasks;

/**
 * 为当前线程设置操作用户
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SetInfoProcessor extends AbstractProcessor {

	@Override
	public void process(ActionContext ac) throws Throwable {
		// 记录线程对象
		User me = (User) ac.getRequest().getSession().getAttribute(Webs.ME);
		ZTasks.setME(me);

		NutMessageMap msgs = Mvcs.getMessageMap(ac.getRequest());
		ZTasks.setMsgs(msgs);

		doNext(ac);
	}

}
