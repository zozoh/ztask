package org.nutz.ztask.impl.mongo;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.web.WebException;
import org.nutz.ztask.Err;
import org.nutz.ztask.ZTaskCase;
import org.nutz.ztask.ZTasks;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskQuery;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.TaskStatus;

public class MongoTaskServiceTest extends ZTaskCase {

	@Test
	public void test_push_reject() {
		TaskStack s = tasks.createStackIfNoExistis("S", "zozoh");
		Task a = tasks.createTask(t("A"));

		a = tasks.pushToStack(a, s);
		assertEquals("S", a.getStack());
		assertEquals(TaskStatus.ING, a.getStatus());

		a = tasks.popFromStack(a, false);
		assertFalse(a.isInStack());
		assertEquals(TaskStatus.NEW, a.getStatus());

		a = tasks.pushToStack(a, s);
		assertEquals("S", a.getStack());
		assertEquals(TaskStatus.ING, a.getStatus());

		a = tasks.popFromStack(a, true);
		assertFalse(a.isInStack());
		assertEquals(TaskStatus.DONE, a.getStatus());

	}

	/**
	 * For Issue#4
	 */
	@Test
	public void test_push_to_task_in_stack() {
		TaskStack s = tasks.createStackIfNoExistis("S", "zozoh");
		Task a = tasks.createTask(t("A"));

		tasks.pushToStack(a, s);
		assertEquals(TaskStatus.ING, tasks.getTask(a.get_id()).getStatus());
		assertEquals("zozoh", a.getOwner());
		assertEquals("S", a.getStack());
		assertEquals("zozoh", tasks.getTask(a.get_id()).getOwner());
		assertEquals("S", tasks.getTask(a.get_id()).getStack());

		Task b = tasks.createTask(t(a, "B"));

		assertEquals("S", tasks.getTask(b.get_id()).getStack());
		assertEquals(TaskStatus.ING, tasks.getTask(b.get_id()).getStatus());
		assertEquals("S", b.getStack());
		assertEquals(TaskStatus.ING, b.getStatus());

		assertEquals(ZTasks.NULL_STACK, tasks.getTask(a.get_id()).getStack());
		assertEquals(TaskStatus.ING, tasks.getTask(a.get_id()).getStatus());
	}

	@Test
	public void test_add_comment() {
		Task a = tasks.createTask(t("A"));
		Task t;

		tasks.addComment(a.get_id(), "c0");
		t = tasks.getTask(a.get_id());
		assertEquals(1, t.getComments().length);
		assertEquals("c0", t.getComments()[0]);
	}

	@Test
	public void test_query_by_status() {
		TaskStack s = tasks.createStackIfNoExistis("S", "zozoh");
		Task a = tasks.createTask(t("A"));
		Task b = tasks.createTask(t("B"));
		Task c = tasks.createTask(t("C"));

		List<Task> ts;

		ts = tasks.queryTasks(TaskQuery.NEW("%(NEW)").asc());
		assertEquals(3, ts.size());
		assertEquals("A", ts.get(0).getText());
		assertEquals("B", ts.get(1).getText());
		assertEquals("C", ts.get(2).getText());

		tasks.pushToStack(a, s);
		ts = tasks.queryTasks(TaskQuery.NEW("%(NEW)").asc());
		assertEquals(2, ts.size());
		assertEquals("B", ts.get(0).getText());
		assertEquals("C", ts.get(1).getText());

		tasks.pushToStack(b, s);
		ts = tasks.queryTasks(TaskQuery.NEW("%(ING)").asc());
		assertEquals(2, ts.size());
		assertEquals("A", ts.get(0).getText());
		assertEquals("B", ts.get(1).getText());

		tasks.popFromStack(a, true);
		ts = tasks.queryTasks(TaskQuery.NEW("%(NEW,DONE)").asc());
		assertEquals(2, ts.size());
		assertEquals("A", ts.get(0).getText());
		assertEquals("C", ts.get(1).getText());

		tasks.pushToStack(c, s);
		ts = tasks.queryTasks(TaskQuery.NEW("%(NEW,DONE)").asc());
		assertEquals(1, ts.size());
		assertEquals("A", ts.get(0).getText());
	}

	@Test
	public void test_query_by_labels_and_names() {
		tasks.createTask(t_l("AAA", "a", "b", "c"));
		tasks.createTask(t_l("BAB", "x", "y", "z"));
		tasks.createTask(t_l("CBC", "a", "b", "m"));
		tasks.createTask(t_l("ADD", "a", "c", "n"));

		List<Task> ts;

		ts = tasks.queryTasks(TaskQuery.NEW("#(a,b)").asc().skip(1));
		assertEquals(1, ts.size());
		assertEquals("CBC", ts.get(0).getText());

		ts = tasks.queryTasks(TaskQuery.NEW("#(a,b)").asc().limit(1));
		assertEquals(1, ts.size());
		assertEquals("AAA", ts.get(0).getText());

		ts = tasks.queryTasks(TaskQuery.NEW("#(a,b)").asc());
		assertEquals(2, ts.size());
		assertEquals("AAA", ts.get(0).getText());
		assertEquals("CBC", ts.get(1).getText());

		ts = tasks.queryTasks(TaskQuery.NEW("A #(n)").asc());
		assertEquals(1, ts.size());
		assertEquals("ADD", ts.get(0).getText());

		ts = tasks.queryTasks(TaskQuery.NEW("^A").asc());
		assertEquals(2, ts.size());
		assertEquals("AAA", ts.get(0).getText());
		assertEquals("ADD", ts.get(1).getText());

		ts = tasks.queryTasks(TaskQuery.NEW("ADD").asc());
		assertEquals(1, ts.size());
		assertEquals("ADD", ts.get(0).getText());

		ts = tasks.queryTasks(TaskQuery.NEW("B").asc());
		assertEquals(2, ts.size());
		assertEquals("BAB", ts.get(0).getText());
		assertEquals("CBC", ts.get(1).getText());

		ts = tasks.queryTasks(TaskQuery.NEW("A").asc());
		assertEquals(3, ts.size());
		assertEquals("AAA", ts.get(0).getText());
		assertEquals("BAB", ts.get(1).getText());
		assertEquals("ADD", ts.get(2).getText());

	}

	@Test
	public void test_gout_to_root() {
		Task a = tasks.createTask(t_u("A", "zzh"));
		Task b = tasks.createTask(t_u(a, "B", "zzh"));

		tasks.setTasksParent(null, b.get_id());
		assertNull(tasks.getTask(b.get_id()).getParentId());
	}

	private void COW(String expectOwnerName, Task... ts) {
		for (Task t : ts) {
			t = tasks.getTask(t.get_id());
			if (!Lang.equals(expectOwnerName, t.getOwner())) {
				throw Lang.makeThrow(	"#@%s :: T'%s'@%s:: %s",
										expectOwnerName,
										t.getText(),
										t.getOwner(),
										t.get_id());
			}
		}
	}

	private void CST(String expectStackName, Task... ts) {
		for (Task t : ts) {
			t = tasks.getTask(t.get_id());
			if (!Lang.equals(expectStackName, t.getStack())) {
				throw Lang.makeThrow(	"#[%s] :: T'%s'[%s]@%s:: %s",
										expectStackName,
										t.getText(),
										t.getStack(),
										t.getOwner(),
										t.get_id());
			}
		}
	}

	@Test
	public void test_quick_ower_creator() {
		TaskStack s = tasks.createStackIfNoExistis("XYZ", "abc");
		Task a = tasks.createTask(t_u("A", "zzh"));
		Task b = tasks.createTask(t_u(a, "B", "zzh"));
		Task c = tasks.createTask(t_u(a, "C", "zzh"));
		Task d = tasks.createTask(t_u(a, "D", "zzh"));

		// Init chceck :: A,b,C,D@zozoh，$none@abc
		COW("zzh", a, b, c, d);
		COW("abc");

		// Push B>>#S :: A,C,D@zozoh，B@abc
		tasks.pushToStack(b, s);
		COW("zzh", a, c, d);
		COW("abc", b);

		// Push C>>#S :: D@zozoh，A,B,C@abc
		tasks.pushToStack(c, s);
		COW("zzh", d);
		COW("abc", a, b, c);

		// Push D>>#S :: $none@zozoh，A,B,C,D@abc
		tasks.pushToStack(d, s);
		COW("zzh");
		COW("abc", a, b, c, d);

		// Pop D<<#S :: D@zozoh，A,B,C@abc
		tasks.popFromStack(d, false);
		COW("zzh", d);
		COW("abc", a, b, c);

		// Pop C<<#S :: A,C,D@zozoh，B@abc
		tasks.popFromStack(c, false);
		COW("zzh", a, c, d);
		COW("abc", b);

		// Pop B<<#S :: A,b,C,D@zozoh，$none@abc
		tasks.popFromStack(b, false);
		COW("zzh", a, b, c, d);
		COW("abc");

	}

	@Test
	public void test_sync_ower_creator_weight() {
		TaskStack s = tasks.createStackIfNoExistis("XYZ", "abc");
		Task a = tasks.createTask(t_u("A", "zzh"));
		Task a1 = tasks.createTask(t_u(a, "A1", "zzh"));
		Task b1 = tasks.createTask(t_u(a1, "B1", "zzh"));
		Task b2 = tasks.createTask(t_u(a1, "B2", "zzh"));
		Task b3 = tasks.createTask(t_u(a1, "B3", "zzh"));
		Task a2 = tasks.createTask(t_u(a, "A2", "zzh"));
		Task c1 = tasks.createTask(t_u(a2, "C1", "zzh"));
		Task c2 = tasks.createTask(t_u(a2, "C2", "zzh"));

		// Push B1>>#S ::::: B1@abc，A,A1,B2,B3,A2,C1,C2@zozoh
		tasks.pushToStack(b1, s);
		COW("abc", b1);
		COW("zzh", a, a1, b2, b3, a2, c1, c2);

		// Push B2>>#S ::::: A,A1,B1,B2@abc，B3,A2,C1,C2@zozoh
		tasks.pushToStack(b2, s);
		COW("abc", a, a1, b1, b2);
		COW("zzh", b3, a2, c1, c2);

		// Push C1,C2>>#S :: A,A1,B1,B2,A2,C1,C2@abc，B3@zozoh
		tasks.pushToStack(c1, s);
		tasks.pushToStack(c2, s);
		COW("abc", a, a1, b1, b2, a1, c1, c2);
		COW("zzh", b3);

		// Pop B2<<#S :::::: B1,A2,C1,C2@abc，A,A1,B2,B3@zozoh
		tasks.popFromStack(b2, false);
		COW("abc", b1, a2, c1, c2);
		COW("zzh", a, a1, b2, b3);
	}

	@Test
	public void test_sync_ower_creator() {
		TaskStack s = tasks.createStackIfNoExistis("XYZ", "abc");
		Task a = tasks.createTask(t_u("A", "zzh"));
		Task a1 = tasks.createTask(t_u(a, "A1", "zzh"));
		Task b1 = tasks.createTask(t_u(a1, "B1", "zzh"));
		Task b2 = tasks.createTask(t_u(a1, "B2", "zzh"));
		Task b3 = tasks.createTask(t_u(a1, "B3", "zzh"));
		Task a2 = tasks.createTask(t_u(a, "A2", "zzh"));

		tasks.syncDescendants(a);

		// Push A>>#S :::::::::: $all@abc
		tasks.pushToStack(a, s);
		CST("--", a, a1);
		CST("XYZ", b1, b2, b3, a2);
		COW("abc", a, a1, b1, b2, b3, a2);

		// POP B1,B2,B3,A2<<#S:: $all@zozoh
		tasks.popFromStack(b1, false);
		tasks.popFromStack(b2, false);
		tasks.popFromStack(b3, false);
		tasks.popFromStack(a2, false);
		COW("zzh", a, a1, b1, b2, b3, a2);
		CST("--", a, a1, b1, b2, b3, a2);

		// Push B1>>#S ::::::::: A,A1,B2,B3,A2@zozoh，B1@abc
		tasks.pushToStack(b1, s);
		COW("zzh", a, a1, b2, b3, a2);
		COW("abc", b1);

		// Push B2>>#S ::::::::: A2,B3@zozoh，A,A1,B1,B2@abc
		tasks.pushToStack(b2, s);
		COW("zzh", b3, a2);
		COW("abc", a, a1, b1, b2);

		// push A2>>#S ::::::::: B3@zozoh，A,A1,A2,B1,B2@abc
		tasks.pushToStack(a2, s);
		COW("zzh", b3);
		COW("abc", a, a1, b1, b2, a2);

		// Pop B2<<#S ::::::::: A,A1,B2,B3@zozoh，A2,B1@abc
		tasks.popFromStack(b2, false);
		COW("zzh", a, a1, b2, b3);
		COW("abc", a2, b1);
	}

	@Test
	public void test_simple_ower_creator() {
		TaskStack s = tasks.createStackIfNoExistis("XX", "abc");
		Task a = tasks.createTask(t_u("A", "zzh"));
		assertEquals("zzh", tasks.getTask(a.get_id()).getCreater());
		assertEquals("zzh", tasks.getTask(a.get_id()).getOwner());

		tasks.pushToStack(a.get_id(), s.getName());
		assertEquals("zzh", tasks.getTask(a.get_id()).getCreater());
		assertEquals("abc", tasks.getTask(a.get_id()).getOwner());

		tasks.popFromStack(a.get_id(), false);
		assertEquals("zzh", tasks.getTask(a.get_id()).getCreater());
		assertEquals("zzh", tasks.getTask(a.get_id()).getOwner());

		tasks.pushToStack(a.get_id(), s.getName());
		assertEquals("zzh", tasks.getTask(a.get_id()).getCreater());
		assertEquals("abc", tasks.getTask(a.get_id()).getOwner());

		tasks.popFromStack(a.get_id(), true);
		assertEquals("zzh", tasks.getTask(a.get_id()).getCreater());
		assertEquals("abc", tasks.getTask(a.get_id()).getOwner());
	}

	@Test
	public void test_trace_task_times() {
		TaskStack s = tasks.createStackIfNoExistis("XX", "zzh");
		Task a = tasks.createTask(t("A"));
		assertNull(tasks.getTask(a.get_id()).getPushAt());
		assertNull(tasks.getTask(a.get_id()).getPopAt());
		assertNull(tasks.getTask(a.get_id()).getStartAt());
		assertNull(tasks.getTask(a.get_id()).getHungupAt());

		a = tasks.pushToStack(a.get_id(), s.getName());
		assertNotNull(tasks.getTask(a.get_id()).getPushAt());
		assertNull(tasks.getTask(a.get_id()).getPopAt());
		assertNotNull(tasks.getTask(a.get_id()).getStartAt());
		assertNull(tasks.getTask(a.get_id()).getHungupAt());

		a = tasks.popFromStack(a.get_id(), false);
		assertNull(tasks.getTask(a.get_id()).getPushAt());
		assertNotNull(tasks.getTask(a.get_id()).getPopAt());
		assertNull(tasks.getTask(a.get_id()).getStartAt());
		assertNull(tasks.getTask(a.get_id()).getHungupAt());

		a = tasks.pushToStack(a.get_id(), s.getName());
		assertNotNull(tasks.getTask(a.get_id()).getPushAt());
		assertNull(tasks.getTask(a.get_id()).getPopAt());
		assertNotNull(tasks.getTask(a.get_id()).getStartAt());
		assertNull(tasks.getTask(a.get_id()).getHungupAt());

		a = tasks.hungupTask(a.get_id());
		assertNotNull(tasks.getTask(a.get_id()).getPushAt());
		assertNull(tasks.getTask(a.get_id()).getPopAt());
		assertNull(tasks.getTask(a.get_id()).getStartAt());
		assertNotNull(tasks.getTask(a.get_id()).getHungupAt());

		a = tasks.restartTask(a.get_id());
		assertNotNull(tasks.getTask(a.get_id()).getPushAt());
		assertNull(tasks.getTask(a.get_id()).getPopAt());
		assertNotNull(tasks.getTask(a.get_id()).getStartAt());
		assertNull(tasks.getTask(a.get_id()).getHungupAt());

		a = tasks.popFromStack(a.get_id(), true);
		assertNull(tasks.getTask(a.get_id()).getPushAt());
		assertNotNull(tasks.getTask(a.get_id()).getPopAt());
		assertNotNull(tasks.getTask(a.get_id()).getStartAt());
		assertNull(tasks.getTask(a.get_id()).getHungupAt());
	}

	@Test
	public void test_pop_push_java_obj_status() {
		TaskStack s = tasks.createStackIfNoExistis("XX", "zzh");
		Task a = tasks.createTask(t("A"));

		a = tasks.pushToStack(a.get_id(), s.getName());
		assertEquals(TaskStatus.ING, a.getStatus());

		a = tasks.popFromStack(a.get_id(), true);
		assertEquals(TaskStatus.DONE, a.getStatus());
	}

	@Test
	public void test_get_sync_when_remove() {
		TaskStack stack = tasks.createStackIfNoExistis("XX", "zzh");
		Task a = tasks.createTask(t("A"));
		Task b = tasks.createTask(t(a, "B"));
		Task c = tasks.createTask(t(a, "C"));

		// push B
		tasks.pushToStack(b, stack);
		assertEquals(1, tasks.getStack(stack.getName()).getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(a.get_id()).getStatus());
		assertEquals(2, tasks.getTask(a.get_id()).getNumberAll());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberProcessing());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberNew());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberDone());

		// push C
		tasks.pushToStack(c, stack);
		assertEquals(2, tasks.getStack(stack.getName()).getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(a.get_id()).getStatus());
		assertEquals(2, tasks.getTask(a.get_id()).getNumberAll());
		assertEquals(2, tasks.getTask(a.get_id()).getNumberProcessing());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberNew());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberDone());

		// pop C
		tasks.popFromStack(c.get_id(), false);
		assertEquals(1, tasks.getStack(stack.getName()).getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(a.get_id()).getStatus());
		assertEquals(2, tasks.getTask(a.get_id()).getNumberAll());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberProcessing());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberNew());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberDone());

		// remove C
		tasks.removeTask(c.get_id(), true);
		assertEquals(1, tasks.getStack(stack.getName()).getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(a.get_id()).getStatus());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberAll());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberProcessing());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberNew());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberDone());

		// remove B
		tasks.removeTask(b.get_id(), true);
		assertEquals(0, tasks.getStack(stack.getName()).getCount());
		assertEquals(TaskStatus.NEW, tasks.getTask(a.get_id()).getStatus());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberAll());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberProcessing());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberNew());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberDone());
	}

	@Test
	public void test_get_topnews() {
		TaskStack stack = tasks.createStackIfNoExistis("XX", "zzh");
		Task a = tasks.createTask(t("A"));
		Task b = tasks.createTask(t(a, "B"));
		Task c = tasks.createTask(t(a, "C"));
		Task d = tasks.createTask(t("D"));

		tasks.pushToStack(c.get_id(), stack.getName());
		assertEquals(TaskStatus.NEW, tasks.getTask(b.get_id()).getStatus());
		assertEquals(2, tasks.getTask(a.get_id()).getNumberAll());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberProcessing());
		assertEquals(1, tasks.getTask(a.get_id()).getNumberNew());
		assertEquals(0, tasks.getTask(a.get_id()).getNumberDone());

		List<Task> ts = tasks.getTopNewTasks();
		assertEquals(2, ts.size());
		assertEquals(d.getText(), ts.get(0).getText());
		assertEquals(a.getText(), ts.get(1).getText());
	}

	@Test
	public void test_simple_auto_sync_parent() {
		Task a = tasks.createTask(t("A"));
		Task b = tasks.createTask(t(a, "B"));

		a = tasks.getTask(a.get_id());
		assertEquals(1, a.getNumberAll());
		assertEquals(1, a.getNumberNew());
		assertEquals(0, a.getNumberDone());
		assertEquals(0, a.getNumberProcessing());
		assertEquals(TaskStatus.NEW, a.getStatus());

		tasks.createTask(t(b, "C"));
		tasks.createTask(t(b, "D"));

		a = tasks.getTask(a.get_id());
		assertEquals(2, a.getNumberAll());
		assertEquals(2, a.getNumberNew());
		assertEquals(0, a.getNumberDone());
		assertEquals(0, a.getNumberProcessing());
		assertEquals(TaskStatus.NEW, a.getStatus());
	}

	@Test
	public void test_set_self_parent() {
		Task a = tasks.createTask(t("A"));
		try {
			tasks.setTasksParent(a.get_id(), a.get_id());
			fail();
		}
		catch (WebException e) {
			assertEquals(Err.T.SELF_PARENT(a.get_id()).getKey(), e.getKey());
		}
	}

	@Test
	public void test_modify_task_field() {
		Task a = tasks.createTask(t("I am A, NOT! B"));
		Task b = tasks.createTask(t("I am B"));
		Task c = tasks.createTask(t("Hello world!"));

		tasks.setTaskOwner(a.get_id(), "zzh");
		tasks.setTaskOwner(b.get_id(), "zzh");
		tasks.setTaskOwner(c.get_id(), "wendal");

		// 判断一下 owner
		List<Task> list = tasks.queryTasks(TaskQuery.NEW("@(zzh)").asc());
		assertEquals(2, list.size());
		assertEquals(a.get_id(), list.get(0).get_id());
		assertEquals(b.get_id(), list.get(1).get_id());

	}

	@Test
	public void test_simple_task_push_pop() {
		Task t = tasks.createTask(t("abcd"));
		tasks.saveStack(ts("X"));
		tasks.saveStack(ts("Y"));

		// 两个栈是空的
		assertEquals(0, tasks.getTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());

		// 压入 X ...
		tasks.pushToStack(t.get_id(), "X");

		// 验证 ...
		assertEquals(1, tasks.getTasksInStack("X").size());
		assertEquals(1, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(t.get_id()).getStatus());

		// 压入 Y ...
		tasks.pushToStack(t.get_id(), "Y");

		// 验证 ...
		assertEquals(0, tasks.getTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(1, tasks.getTasksInStack("Y").size());
		assertEquals(1, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(t.get_id()).getStatus());

		// 弹出
		tasks.popFromStack(t.get_id(), true);

		// 验证 ...
		assertEquals(0, tasks.getTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.DONE, tasks.getTask(t.get_id()).getStatus());

		// 压入，再弹出
		tasks.pushToStack(t.get_id(), "X");
		tasks.popFromStack(t.get_id(), false);

		// 验证 ...
		assertEquals(0, tasks.getTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.NEW, tasks.getTask(t.get_id()).getStatus());
	}

	@Test
	public void test_simple_task_tree() {
		Task t = tasks.createTask(t("A"));
		Task t2 = tasks.createTask(t("A2"));
		Task t11 = tasks.createTask(t("A11"));
		Task t1 = tasks.createTask(t("A1"));

		// 现在应该有 4 个 Task 了
		List<Task> tops = tasks.getTasks(null, null);
		assertEquals(4, tops.size());

		// 调整树结构
		tasks.setTasksParent(t.get_id(), t1.get_id());
		tasks.setTasksParent(t.get_id(), t2.get_id());
		tasks.setTasksParent(t1.get_id(), t11.get_id());

		// 判断树节点
		tops = tasks.getTasks(null, null);
		assertEquals(1, tops.size());
		assertEquals("A", tops.get(0).getText());
		assertEquals(2, tops.get(0).getNumberAll());
		assertEquals(2, tops.get(0).getNumberNew());
		assertEquals(0, tops.get(0).getNumberDone());
		assertEquals(0, tops.get(0).getNumberProcessing());

		List<Task> l1 = tasks.getChildTasks(tops.get(0).get_id());
		assertEquals(2, l1.size());
		assertEquals("A1", l1.get(0).getText());
		assertEquals(1, l1.get(0).getNumberAll());
		assertEquals(1, l1.get(0).getNumberNew());
		assertEquals(0, l1.get(0).getNumberDone());
		assertEquals(0, l1.get(0).getNumberProcessing());
		assertEquals("A2", l1.get(1).getText());
		assertEquals(0, l1.get(1).getNumberAll());
		assertEquals(0, l1.get(1).getNumberNew());
		assertEquals(0, l1.get(1).getNumberDone());
		assertEquals(0, l1.get(1).getNumberProcessing());

		List<Task> l11 = tasks.getChildTasks(l1.get(0).get_id());
		assertEquals(1, l11.size());
		assertEquals("A11", l11.get(0).getText());
		assertEquals(0, l11.get(0).getNumberAll());
		assertEquals(0, l11.get(0).getNumberNew());
		assertEquals(0, l11.get(0).getNumberDone());
		assertEquals(0, l11.get(0).getNumberProcessing());

		// 现在的 Tree 的结构为
		// A
		// .. A1
		// .... A11
		// .. A2
		tasks.popFromStack(t11.get_id(), true);
		assertEquals(TaskStatus.DONE, tasks.getTask(t1.get_id()).getStatus());
		assertEquals(TaskStatus.ING, tasks.getTask(t.get_id()).getStatus());
		assertEquals(TaskStatus.NEW, tasks.getTask(t2.get_id()).getStatus());

		tasks.popFromStack(t2.get_id(), true);
		assertEquals(TaskStatus.DONE, tasks.getTask(t.get_id()).getStatus());
		assertEquals(TaskStatus.DONE, tasks.getTask(t1.get_id()).getStatus());
		assertEquals(TaskStatus.DONE, tasks.getTask(t2.get_id()).getStatus());

	}

	@Test
	public void task_simple_create_move_and_remove() {
		Task t = tasks.createTask(t("tA"));
		Task t2 = tasks.getTask(t.get_id());
		assertEquals(t.getText(), t2.getText());
		assertEquals(1, countTask());

		tasks.removeTask(t.get_id(), true);
		assertEquals(0, countTask());
	}

	@Test
	public void stack_simple_create_move_and_remove() {
		tasks.saveStack(ts("sA"));
		TaskStack ts = tasks.getStack("sA");
		assertEquals("sA", ts.getName());
		assertEquals(1, countStack());

		tasks.removeStack("sA");
		assertEquals(0, countStack());
	}

	private TaskService tasks;

	protected void onBefore() {
		tasks = this.getService(TaskService.class);
		tasks.dao().create(Task.class, true);
		tasks.dao().create(TaskStack.class, true);
	}
}
