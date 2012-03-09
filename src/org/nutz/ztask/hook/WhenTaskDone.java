package org.nutz.ztask.hook;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ztask.api.GInfo;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Hooking;
import org.nutz.ztask.api.Label;
import org.nutz.ztask.api.LabelService;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.TaskStatus;
import org.nutz.ztask.util.ZTasks;

/**
 * 当堆栈弹出时，如果是完成，那么将任务所在堆栈名，加在任务标签上
 * <p>
 * 如果，是属于 reportIgnoreLabels，那么仅仅将顶级堆栈名，放到它的标签里
 * <p>
 * 如果，GInfo 规定了应该移除的标签或者标签组，则删除对应的标签
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name = "h_task_done")
public class WhenTaskDone implements HookHandler {

	@Override
	public void doHandle(HookType htp, String name, Hooking ing) {
		if (HookType.DONE == htp && TaskStatus.DONE == ing.t().getStatus()) {

			// 取得必要的变量
			Task t = ing.factory().tasks().getTask(ing.t().get_id());
			GInfo ginfo = ing.factory().tasks().getGlobalInfo();
			LabelService labels = ing.factory().labels();

			// 当次操作的参考信息
			String snm = ing.getReferString();

			// 准备要处理的结果集
			HashSet<String> lbset = new HashSet<String>();
			Map<String, Label> drmap = labels.toFlatMap(ginfo.getDoneRemovedLabels());

			// 加入自身
			if (null != t.getLabels()) {
				for (String lb : t.getLabels()) {
					if (!drmap.containsKey(lb))
						lbset.add(lb);
				}
			}

			// 搜索所有堆栈
			TaskStack s = ing.factory().tasks().getStack(snm);
			LinkedList<String> slist = new LinkedList<String>();
			while (null != s) {
				slist.add(s.getName());
				s = ing.factory().tasks().getStack(s.getParentName());
			}

			// 准备 ...
			Map<String, Label> igmap = labels.toFlatMap(ginfo.getReportIgnoreLabels());

			// 是否需要忽略 ...
			boolean isIgnore = false;
			if (null != t.getLabels() && null != t.getLabels())
				for (String lb : t.getLabels()) {
					if (igmap.containsKey(lb)) {
						isIgnore = true;
						break;
					}
				}
			// 增加标签
			String myName = ZTasks.getMyName();
			lbset.add(myName);
			for (String lb : slist) {
				lbset.add(lb);
			}
			// 如果要忽略人员，那么操作者，所属者的标签都要移除掉
			if (isIgnore) {
				lbset.remove(myName);
				lbset.remove(t.getOwner());
			}

			// 更新标签
			t.setLabels(lbset.isEmpty() ? null : lbset.toArray(new String[lbset.size()]));
			ing.factory().htasks().setLabels(ing.t(), t.getLabels());
		}
	}

}
