package org.nutz.ztask.impl.mongo;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ztask.ZTaskCase;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskService;
import org.nutz.ztask.api.TaskStack;

public class MongoTaskServiceTest extends ZTaskCase {

	private TaskService tasks;

	protected void onBefore() {
		tasks = this.getService(TaskService.class);
		tasks.dao().create(Task.class, true);
		tasks.dao().create(TaskStack.class, true);
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

}
