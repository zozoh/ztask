package org.nutz.ztask.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Email;
import org.nutz.ztask.api.User;
import org.nutz.ztask.api.UserService;

/**
 * 根据一个 text 文件，读取用户信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ReadonlyUserService implements UserService {

	private Map<String, User> map;

	public ReadonlyUserService(String dataPath) {
		map = new HashMap<String, User>();
		String str = Files.read(dataPath);
		String[] lines = Strings.splitIgnoreBlank(str, "\n");
		for (String line : lines) {
			// 注释行
			if (line.startsWith("#"))
				continue;
			// 执行 ...
			String[] cols = Strings.splitIgnoreBlank(line, ":");
			ReadonlyUser u = new ReadonlyUser();
			u.setSuperUser(cols[0].equalsIgnoreCase("S"));
			u.setName(cols[1]);
			u.setPassword(cols[2]);
			u.setEmail(new Email(cols[3]));
			u.setDescription(cols[4]);
			map.put(u.getName(), u);
		}
	}

	@Override
	public List<User> all() {
		ArrayList<User> list = new ArrayList<User>(map.size());
		list.addAll(map.values());
		return list;
	}

	@Override
	public void each(Each<User> callback) {
		int i = 0;
		int len = map.size();
		try {
			for (User u : map.values())
				try {
					callback.invoke(i++, u, len);
				}
				catch (ContinueLoop e) {}
		}
		catch (ExitLoop e) {}
		catch (LoopException e) {
			throw Lang.wrapThrow(e);
		}
	}

	@Override
	public User verify(String name, String password) {
		User u = map.get(name);
		if (null == u || null == password || !password.equals(u.getPassword()))
			return null;
		return u;
	}

	@Override
	public User get(String name) {
		return map.get(name);
	}

}
