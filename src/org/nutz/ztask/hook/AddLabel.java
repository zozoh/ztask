package org.nutz.ztask.hook;

import java.util.HashSet;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Hooking;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskStack;
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

			HashSet<String> lbset = new HashSet<String>();
			// 检查
			TaskStack s = ing.factory().tasks().getStack(refer);
			while (null != s) {
				lbset.add(s.getName());
				s = ing.factory().tasks().getStack(s.getParentName());
			}

			// 加入自身
			if (null != t.getLabels()) {
				for (String lb : t.getLabels()) {
					lbset.add(lb);
				}
			}

			if (null == t.getLabels() || lbset.size() != t.getLabels().length) {
				t.setLabels(lbset.toArray(new String[lbset.size()]));
				ing.factory().htasks().setLabels(ing.t(), t.getLabels());
			}
		}
	}

}
