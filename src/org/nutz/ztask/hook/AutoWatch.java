package org.nutz.ztask.hook;

import java.util.Map;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.ztask.api.HookHandler;
import org.nutz.ztask.api.HookType;
import org.nutz.ztask.api.Hooking;
import org.nutz.ztask.api.User;

/**
 * 当内容被修改时，如果有 ‘@xxx’ 的, 把他加为关注者
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@IocBean(name = "h_auto_watch")
public class AutoWatch implements HookHandler {

	private static final Log log = Logs.get();

	@Override
	public void doHandle(HookType htp, String name, Hooking ing) {

		String text = null;

		// Comment 修改
		if (HookType.COMMENT == htp) {
			try {
				text = Lang.get(ing.t().getComments(), ing.getReferInt());
			}
			catch (Exception e) {}
		}
		// 内容修改
		else if (HookType.UPDATE == htp || HookType.CREATE == htp) {
			text = ing.t().getText();
		}

		// 有效的 HookType 会给出一段文本的 ...
		if (Strings.isBlank(text))
			return;

		/*
		 * 评估用户，没有提及任何用户，则跳过
		 */
		Map<String, User> map = ing.extractUsers(text);
		if (map.isEmpty())
			return;

		/*
		 * 如果用户不是关注者，让其关注当前的任务
		 */
		String[] newWatchers = map.keySet().toArray(new String[map.size()]);
		if (log.isDebugEnabled())
			log.debugf("  - auto watch: %s ", Lang.concat(", ", newWatchers));

		ing.factory().tasks().addWatchers(ing.t(), newWatchers);

	}

}
