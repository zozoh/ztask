package org.nutz.ztask.impl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Files;
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
			User u = new User();
			u.setName(cols[0]);
			u.setPassword(cols[1]);
			u.setEmail(new Email(cols[2]));
			u.setDescription(cols[3]);
			map.put(u.getName(), u);
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
