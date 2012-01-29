package org.nutz.ztask.impl.mongo;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.ztask.ZTaskCase;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskQuery;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TaskStack;
import org.nutz.ztask.api.TaskStatus;

public class MongoTaskServiceTest extends ZTaskCase {

	@Test
	public void test_modify_task_field() {
		Task a = tasks.createTask(t("I am A, NOT! B"));
		Task b = tasks.createTask(t("I am B"));
		Task c = tasks.createTask(t("Hello world!"));

		tasks.setTaskOwner(a.get_id(), "zzh");
		tasks.setTaskOwner(b.get_id(), "zzh");
		tasks.setTaskOwner(c.get_id(), "wendal");

		// 判断一下 owner
		List<Task> list = tasks.queryTasks(TaskQuery.create().owners("zzh"));
		assertEquals(2, list.size());
		assertEquals(a.get_id(), list.get(0).get_id());
		assertEquals(b.get_id(), list.get(1).get_id());

	}

	@Test
	public void test_simple_task_push_pop() {
		Task t = tasks.createTask(t("abcd"));
		tasks.createStack(ts("X"));
		tasks.createStack(ts("Y"));

		// 两个栈是空的
		assertEquals(0, tasks.getTopTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTopTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());

		// 压入 X ...
		tasks.pushToStack(t.get_id(), "X");

		// 验证 ...
		assertEquals(1, tasks.getTopTasksInStack("X").size());
		assertEquals(1, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTopTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(t.get_id()).getStatus());

		// 压入 Y ...
		tasks.pushToStack(t.get_id(), "Y");

		// 验证 ...
		assertEquals(0, tasks.getTopTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(1, tasks.getTopTasksInStack("Y").size());
		assertEquals(1, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.ING, tasks.getTask(t.get_id()).getStatus());

		// 弹出
		tasks.popFromStack(t.get_id(), true);

		// 验证 ...
		assertEquals(0, tasks.getTopTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTopTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.DONE, tasks.getTask(t.get_id()).getStatus());

		// 压入，再弹出
		tasks.pushToStack(t.get_id(), "X");
		tasks.popFromStack(t.get_id(), false);

		// 验证 ...
		assertEquals(0, tasks.getTopTasksInStack("X").size());
		assertEquals(0, tasks.getStack("X").getCount());
		assertEquals(0, tasks.getTopTasksInStack("Y").size());
		assertEquals(0, tasks.getStack("Y").getCount());
		assertEquals(TaskStatus.NEW, tasks.getTask(t.get_id()).getStatus());
	}

	@Test
	public void test_simple_task_tree() {
		Task t = tasks.createTask(t("A"));
		Task t2 = tasks.createTask(t("A2"));
		Task t11 = tasks.createTask(t("A11"));
		Task t1 = tasks.createTask(t("A1"));

		// 顺便测测顺序
		List<Task> tops = tasks.getTopTasks(null, null);
		assertEquals(4, tops.size());
		assertEquals("A", tops.get(0).getTitle());
		assertEquals("A1", tops.get(1).getTitle());
		assertEquals("A11", tops.get(2).getTitle());
		assertEquals("A2", tops.get(3).getTitle());

		// 调整树结构
		tasks.setTaskParent(t1.get_id(), t.get_id());
		tasks.setTaskParent(t2.get_id(), t.get_id());
		tasks.setTaskParent(t11.get_id(), t1.get_id());

		// 判断树节点
		tops = tasks.getTopTasks(null, null);
		assertEquals(1, tops.size());
		assertEquals("A", tops.get(0).getTitle());

		List<Task> l1 = tasks.getChildTasks(tops.get(0).get_id());
		assertEquals(2, l1.size());
		assertEquals("A1", l1.get(0).getTitle());
		assertEquals("A2", l1.get(1).getTitle());

		List<Task> l11 = tasks.getChildTasks(l1.get(0).get_id());
		assertEquals(1, l11.size());
		assertEquals("A11", l11.get(0).getTitle());
	}

	@Test
	public void task_simple_create_move_and_remove() {
		Task t = tasks.createTask(t("tA"));
		Task t2 = tasks.getTask(t.get_id());
		assertEquals(t.getTitle(), t2.getTitle());
		assertEquals(1, countTask());

		tasks.removeTask(t.get_id(), true);
		assertEquals(0, countTask());
	}

	@Test
	public void stack_simple_create_move_and_remove() {
		tasks.createStack(ts("sA"));
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
