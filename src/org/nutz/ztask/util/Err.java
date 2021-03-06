package org.nutz.ztask.util;

import static org.nutz.web.Webs.Err.*;

import org.nutz.web.WebException;
import org.nutz.ztask.api.Hook;
import org.nutz.ztask.api.Message;

/**
 * 封装了本应用全部得错误
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Err {

	/**
	 * 消息相关的错误
	 */
	public static class M {

		public static WebException NULL_MSG() {
			return create("e.m.null_msg");
		}

		public static WebException NULL_OWNER(Message msg) {
			return create("e.m.null_owner").reason(msg);
		}

		public static WebException NULL_TEXT(Message msg) {
			return create("e.m.null_text").reason(msg);
		}

	}

	/**
	 * Timer 相关的错误
	 */
	public static class TIMER {

		public static WebException NO_HANDLER(String handler) {
			return create("e.t.no_handler").reason(handler);
		}

		public static WebException WRONG_QUARTZ(String qzs) {
			return create("e.t.wrongqz").reason(qzs);
		}

	}

	/**
	 * 钩子服务相关的错误
	 */
	public static class H {
		
		public static WebException GROUP_IS_USER(String groupName) {
			return create("e.h.grp_is_user").reason(groupName);
		}

		public static WebException NO_HANDLER(String handler) {
			return create("e.h.no_handler").reason(handler);
		}

		public static WebException NO_HANDLER(Hook hook) {
			return create("e.h.no_handler").reason(hook.toString());
		}

		public static WebException NULL_TYPE(Hook hook) {
			return create("e.h.null_type").reason(hook.toString());
		}

		public static WebException INVALID_STR(int index, String s) {
			return create("e.h.invalid_str").reasonf("[%d] %s", index, s);
		}

	}

	/**
	 * 任务相关的错误
	 */
	public static class T {

		public static WebException NO_EXISTS(String taskId) {
			return create("e.t.no_exists").reason(taskId);
		}

		public static WebException BLANK_TASK() {
			return create("e.t.blank_task");
		}

		public static WebException SHORT_TASK(int len) {
			return create("e.t.short_task").reason(len);
		}

		public static WebException LONG_TASK(int len) {
			return create("e.t.long_task").reason(len);
		}

		public static WebException SELF_PARENT(String taskId) {
			return create("e.t.self_parent").reason(taskId);
		}
	}

	/**
	 * 任务堆栈相关的错误
	 */
	public static class S {

		public static WebException NO_EXISTS(String name) {
			return create("e.s.no_exists").reason(name);
		}

		public static WebException EXISTS(String name) {
			return create("e.s.exists").reason(name);
		}

		public static WebException BLANK_NAME() {
			return create("e.s.blank_name");
		}

	}

	/**
	 * 用户相关的错误
	 */
	public static class U {

		public static WebException INVALID_LOGIN() {
			return create("e.u.invalid_login");
		}

		public static WebException NO_EXISTS(String name) {
			return create("e.u.no_exists").reason(name);
		}

		public static WebException EXISTS(String name) {
			return create("e.u.exists").reason(name);
		}

	}

}
