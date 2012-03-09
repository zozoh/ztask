package org.nutz.ztask.impl.mongo;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.ztask.ZTaskCase;
import org.nutz.ztask.api.Label;
import org.nutz.ztask.api.LabelService;
import org.nutz.ztask.api.Task;
import org.nutz.ztask.api.TaskService;

public class MongoLabelServiceTest extends ZTaskCase {

	@Test
	public void test_jointo_and_sync() {
		// 准备 Task
		TaskService tasks = this.getService(TaskService.class);
		dao.create(Task.class, true);
		Label a, b, c, d, e, x, y;

		tasks.createTask(t_l("Task0", "A", "B", "C"));
		tasks.createTask(t_l("Task1", "D", "E"));
		tasks.createTask(t_l("Task2", "A", "E"));

		lbls.syncLables();

		lbls.joinTo("X", "A", "B", "C");
		lbls.joinTo("Y", "D", "E");

		a = lbls.get("A");
		b = lbls.get("B");
		c = lbls.get("C");
		d = lbls.get("D");
		e = lbls.get("E");
		x = lbls.get("X");
		y = lbls.get("Y");
		assertTrue(x.isNode());
		assertTrue(y.isNode());
		assertEquals(a.getCount() + b.getCount() + c.getCount(), x.getCount());
		assertEquals(d.getCount() + e.getCount(), y.getCount());

		lbls.joinTo("Y", "A");
		a = lbls.get("A");
		b = lbls.get("B");
		c = lbls.get("C");
		d = lbls.get("D");
		e = lbls.get("E");
		x = lbls.get("X");
		y = lbls.get("Y");
		assertTrue(x.isNode());
		assertTrue(y.isNode());
		assertEquals(b.getCount() + c.getCount(), x.getCount());
		assertEquals(a.getCount() + d.getCount() + e.getCount(), y.getCount());

		lbls.joinTo(null, "B", "C");
		a = lbls.get("A");
		b = lbls.get("B");
		c = lbls.get("C");
		d = lbls.get("D");
		e = lbls.get("E");
		x = lbls.get("X");
		y = lbls.get("Y");
		assertTrue(x.isNode());
		assertTrue(y.isNode());
		assertEquals(0, x.getCount());
		assertEquals(a.getCount() + d.getCount() + e.getCount(), y.getCount());

		lbls.syncLables();
		a = lbls.get("A");
		b = lbls.get("B");
		c = lbls.get("C");
		d = lbls.get("D");
		e = lbls.get("E");
		x = lbls.get("X");
		y = lbls.get("Y");
		assertNull(x);
		assertTrue(y.isNode());
		assertEquals(a.getCount() + d.getCount() + e.getCount(), y.getCount());

	}

	@Test
	public void test_sync_task_label() {
		// 准备 Task
		TaskService tasks = this.getService(TaskService.class);
		dao.create(Task.class, true);

		// 初始化数据
		tasks.createTask(t_l("Task0", "A", "B", "C"));
		tasks.createTask(t_l("Task1", "D", "E"));
		tasks.createTask(t_l("Task2", "A", "E"));
		Task t3 = tasks.createTask(t_l("Task3", "B", "F"));
		Task t4 = tasks.createTask(t_l("Task4", "D", "G"));

		// 同步一次
		lbls.syncLables();
		List<Label> lbs = lbls.all();

		// 检查一下
		assertEquals(7, lbs.size());
		lbs = lbls.tops();
		assertEquals(7, lbs.size());
		assertEquals("A:2", lbs.get(0).toString());
		assertEquals("B:2", lbs.get(1).toString());
		assertEquals("C:1", lbs.get(2).toString());
		assertEquals("D:2", lbs.get(3).toString());
		assertEquals("E:2", lbs.get(4).toString());
		assertEquals("F:1", lbs.get(5).toString());
		assertEquals("G:1", lbs.get(6).toString());

		// 修改任务数据
		tasks.setLabels(t3, Lang.array("A", "B", "F"));
		tasks.setLabels(t4, Lang.array("B", "D", "H", "I"));

		// 同步一次
		lbls.syncLables();
		lbs = lbls.all();

		// 检查一下
		assertEquals(8, lbs.size());
		lbs = lbls.tops();
		assertEquals(8, lbs.size());
		assertEquals("A:3", lbs.get(0).toString());
		assertEquals("B:3", lbs.get(1).toString());
		assertEquals("C:1", lbs.get(2).toString());
		assertEquals("D:2", lbs.get(3).toString());
		assertEquals("E:2", lbs.get(4).toString());
		assertEquals("F:1", lbs.get(5).toString());
		assertEquals("H:1", lbs.get(6).toString());
		assertEquals("I:1", lbs.get(7).toString());

	}

	@Test
	public void test_simple_add_and_remove() {
		lbls.saveList("A", "B", "C");
		assertEquals(3, lbls.count());

		lbls.saveList("A", "B", "C", "E", "D");
		assertEquals(5, lbls.count());

		lbls.saveList("F", "G", "H");
		assertEquals(8, lbls.count());

		lbls.removeByName("H");
		assertEquals(7, lbls.count());

		List<Label> ls = lbls.tops();
		assertEquals(7, ls.size());
		assertEquals("A", ls.get(0).getName());
		assertEquals("B", ls.get(1).getName());
		assertEquals("C", ls.get(2).getName());
		assertEquals("D", ls.get(3).getName());
		assertEquals("E", ls.get(4).getName());
		assertEquals("F", ls.get(5).getName());
		assertEquals("G", ls.get(6).getName());

		lbls.joinTo("A", "B", "C");
		lbls.joinTo("C", "D");
		lbls.joinTo("C", "E", "F");

		ls = lbls.tops();
		assertEquals(2, ls.size());
		assertEquals("A", ls.get(0).getName());
		assertEquals("G", ls.get(1).getName());

		ls = lbls.list("A");
		assertEquals(2, ls.size());
		assertEquals("B", ls.get(0).getName());
		assertEquals("C", ls.get(1).getName());

		ls = lbls.list("C");
		assertEquals(3, ls.size());
		assertEquals("D", ls.get(0).getName());
		assertEquals("E", ls.get(1).getName());
		assertEquals("F", ls.get(2).getName());

		ls = lbls.list("G");
		assertEquals(0, ls.size());
	}

	private LabelService lbls;

	protected void onBefore() {
		lbls = this.getService(LabelService.class);
		dao.create(Label.class, true);
	}
}
