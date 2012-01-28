package org.nutz.ztask.web;

import org.nutz.web.WebConfig;

/**
 * 封装对于配置文件的读取
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZTaskConfig extends WebConfig {

	public static String JUNIT_DB_SUFFIX = "";

	public String getDB(String key) {
		return get(key) + JUNIT_DB_SUFFIX;
	}

	public ZTaskConfig(String path) {
		super(path);
	}

}
