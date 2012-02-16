package org.nutz.ztask.web.filter;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.ztask.api.GInfo;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.ZTaskFactory;

public class AddCustomizedMenu implements ActionFilter {

	@Override
	public View match(ActionContext ac) {
		// 保存当前的 URL
		ac.getRequest().setAttribute("page-url", Mvcs.getRequestPath(ac.getRequest()));

		// 设置菜单
		ZTaskFactory factory = ac.getIoc().get(ZTaskFactory.class, "serviceFactory");
		TaskService tasks = factory.tasks();
		if (null != tasks) {
			GInfo ginfo = tasks.getGlobalInfo();
			if (null != ginfo && null != ginfo.getMenus()) {
				// 拆分
				String[][] sss = new String[ginfo.getMenus().length][];
				for (int i = 0; i < sss.length; i++) {
					String[] ss = Strings.splitIgnoreBlank(ginfo.getMenus()[i], ":");
					if (ss.length == 1) {
						sss[i] = Lang.array(ss[0], ss[0]);
					} else if (ss.length == 2) {
						sss[i] = ss;
					} else {
						sss[i] = Lang.array(ss[0], Lang.concat(1, ss.length - 1, ":", ss)
														.toString());
					}
				}
				// 存放
				ac.getRequest().setAttribute("page-menu", sss);
			}
		}
		return null;
	}

}
