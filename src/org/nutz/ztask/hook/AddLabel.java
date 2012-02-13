package org.nutz.ztask.hook;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Hooking;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskStatus;

/**
 * 当堆栈弹出时，如果是完成，那么将任务所在堆栈名，加在任务标签上
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name = "h_add_label")
public class AddLabel implements HookHandler {

	@Override
	public void doHandle(HookType htp, String name, Hooking ing) {
		if (HookType.DONE == htp && TaskStatus.DONE == ing.t().getStatus()) {

			Task t = ing.factory().tasks().getTask(ing.t().get_id());

			String refer = ing.getReferString();

			if (!Lang.contains(t.getLabels(), refer)) {
				t.setLabels(Lang.arrayLast(t.getLabels(), refer));
				ing.factory().htasks().setLabels(ing.t(), t.getLabels());
			}
		}
	}

}
