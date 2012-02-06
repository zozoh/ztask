package org.nutz.ztask.impl.mongo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Each;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segments;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.MongoConnector;
import org.nutz.ztask.api.GInfo;
import org.nutz.ztask.api.InitService;
import org.nutz.ztask.api.Label;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.User;
import org.nutz.ztask.api.UserService;

public class MongoInitService extends AbstractMongoService implements InitService {

	private static final Log log = Logs.get();

	/**
	 * 用户数据访问接口
	 */
	private UserService users;

	/**
	 * Task 数据访问接口
	 */
	private TaskService tasks;

	/**
	 * 是否强制保证数据库中的 stack 与配置中的相同
	 */
	private boolean autosync;

	/**
	 * 得到初始化堆栈的数据
	 */
	private String stacksPath;

	public MongoInitService(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	@Override
	public void init() {
		/*
		 * 确保对应的集合存在
		 */
		_init_collections();

		// 看看是否需要初始化
		if (null == users || Strings.isBlank(stacksPath)) {
			if (log.isWarnEnabled())
				log.warn("!!! No stack for init...");
			return;
		}

		// 得到系统中所有的 TaskStack，并做成散列，以便得到需要删除的堆栈信息
		final Map<String, TaskStack> alls = new HashMap<String, TaskStack>();
		tasks.eachStack(new Each<TaskStack>() {
			public void invoke(int index, TaskStack s, int length) {
				alls.put(s.getName(), s);
			}
		});
		if (log.isDebugEnabled())
			log.debugf("Loaded %d stacks", alls.size());

		// 得到文件内容
		String[] lines = Files.read(stacksPath).split("\n");
		// 记录父堆栈
		TaskStack prev = null;
		if (log.isDebugEnabled())
			log.debugf("Loaded '%s'", stacksPath);

		// 开始循环
		int i = 0;
		for (String line : lines) {
			i++; // 行计数
			line = Strings.trim(line);
			// 跳过注释 和 空行
			if (Strings.isBlank(line) || line.startsWith("#"))
				continue;
			// 堆栈名和用户名
			String[] ss = Strings.splitIgnoreBlank(line, "@");
			User u = users.get(ss[1]);
			// 保证是合法的用户名
			if (null == u) {
				throw Lang.makeThrow("User not exists line %d : %s", i, line);
			}

			// 解析堆栈名的含义是 - xxxx 表示子堆栈
			boolean isTop = true;
			String str = ss[0];
			if (str.startsWith("-")) {
				str = Strings.trim(str.substring(str.indexOf(" ")));
				isTop = false;
			}

			// 解析一下堆栈名，以便支持 ${name} 等表达式
			String stackName = Segments.replace(str, Lang.context().putAll(u));

			// 如果堆栈不存在，则创建它，否则从 alls 移除，这样就能知道应该删掉哪些堆栈了
			TaskStack s = alls.get(stackName);
			String newID = "";
			// 不存在，创建
			if (null == s) {
				s = tasks.createStackIfNoExistis(stackName, u.getName());
				newID = s.get_id();
			}
			// 存在，移除索引
			else {
				alls.remove(s.getName());
			}

			// 如果堆栈以 "-" 开头
			if (!isTop && null != prev) {
				tasks.setStackParent(s.getName(), prev.getName());
			}
			// 否则记录一下以便后续使用
			else {
				prev = s;
			}

			if (log.isDebugEnabled())
				log.debugf(	"%3d : %s  >> %s, [%s] @(%s) %s",
							alls.size(),
							Strings.alignLeft(line, 18, ' '),
							isTop,
							stackName,
							u.getName(),
							newID);
		}

		// 删除那些没有声明的堆栈，首先弹出所有任务
		if (autosync)
			if (log.isDebugEnabled())
				log.debugf("auto sync will remove : %d stacks", alls.size());
		for (TaskStack s : alls.values()) {
			if (log.isDebugEnabled())
				log.debugf("  -- : %s", s);
			List<Task> ts = tasks.getTasksInStack(s.getName());
			for (Task t : ts) {
				tasks.popFromStack(t, false);
				if (log.isDebugEnabled())
					log.debugf("     << Pop: %s", t);
			}
			tasks.removeStack(s.getName());
			if (log.isDebugEnabled())
				log.debugf("  -- Done", s.get_id());
		}

	}

	private void _init_collections() {
		dao.create(Task.class, false);
		dao.create(TaskStack.class, false);
		dao.create(Label.class, false);
		dao.create(GInfo.class, false);
		if (log.isDebugEnabled())
			log.debug("collections checked");
	}

}
