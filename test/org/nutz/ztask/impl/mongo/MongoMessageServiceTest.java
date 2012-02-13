package org.nutz.ztask.impl.mongo;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.ztask.ZTaskCase;
import org.nutz.ztask.api.Message;
import org.nutz.ztask.api.MessageService;

public class MongoMessageServiceTest extends ZTaskCase {

	@Test
	public void test_get_byText() {
		Message msg = messages.add("ABC", "x");

		// 初始
		msg = messages.getByText("x", "ABC");
		assertEquals("ABC", msg.getText());
		assertEquals("x", msg.getOwner());
		assertNotNull(msg.get_id());
		assertNotNull(msg.getCreateTime());
		assertFalse(msg.isRead());
		assertFalse(msg.isFavorite());
		assertNull(msg.getNotified());

		// 设已读
		assertNotNull(messages.setRead(msg, true));
		msg = messages.getByText("x", "ABC");
		assertEquals("ABC", msg.getText());
		assertEquals("x", msg.getOwner());
		assertNotNull(msg.get_id());
		assertNotNull(msg.getCreateTime());
		assertTrue(msg.isRead());
		assertFalse(msg.isFavorite());
		assertNull(msg.getNotified());

		// 设收藏
		assertNotNull(messages.setFavorite(msg, true));
		msg = messages.getByText("x", "ABC");
		assertEquals("ABC", msg.getText());
		assertEquals("x", msg.getOwner());
		assertNotNull(msg.get_id());
		assertNotNull(msg.getCreateTime());
		assertTrue(msg.isRead());
		assertTrue(msg.isFavorite());
		assertNull(msg.getNotified());

		// 设发送
		assertNotNull(messages.setNotified(msg, Times.now()));
		msg = messages.getByText("x", "ABC");
		assertEquals("ABC", msg.getText());
		assertEquals("x", msg.getOwner());
		assertNotNull(msg.get_id());
		assertNotNull(msg.getCreateTime());
		assertTrue(msg.isRead());
		assertTrue(msg.isFavorite());
		assertNotNull(msg.getNotified());

		// 取消发送
		assertNotNull(messages.setNotified(msg, null));
		msg = messages.getByText("x", "ABC");
		assertEquals("ABC", msg.getText());
		assertEquals("x", msg.getOwner());
		assertNotNull(msg.get_id());
		assertNotNull(msg.getCreateTime());
		assertTrue(msg.isRead());
		assertTrue(msg.isFavorite());
		assertNull(msg.getNotified());

		// 取消收藏
		messages.setFavorite(msg, false);
		msg = messages.getByText("x", "ABC");
		assertEquals("ABC", msg.getText());
		assertEquals("x", msg.getOwner());
		assertNotNull(msg.get_id());
		assertNotNull(msg.getCreateTime());
		assertTrue(msg.isRead());
		assertFalse(msg.isFavorite());
		assertNull(msg.getNotified());

		// 取消已读
		messages.setRead(msg, false);
		msg = messages.getByText("x", "ABC");
		assertEquals("ABC", msg.getText());
		assertEquals("x", msg.getOwner());
		assertNotNull(msg.get_id());
		assertNotNull(msg.getCreateTime());
		assertFalse(msg.isRead());
		assertFalse(msg.isFavorite());
		assertNull(msg.getNotified());
	}

	@Test
	public void test_clear_unforce() {
		messages.add("A0", "x");
		messages.add("A1", "x");
		messages.add("A2", "x");
		messages.add("A3", "x");
		messages.add("B0", "y");
		messages.add("C0", "z");
		messages.add("C1", "z");

		// 清除
		Date d = Times.D(System.currentTimeMillis() + (4 * 30 * 86400 * 1000));
		messages.clearBefore(d, false);

		messages.clearBefore(Times.now(), true);

		// 验证 ...
		List<Message> msgs;

		msgs = messages.all("x");
		A(msgs, "A3", "A2", "A1", "A0");

		msgs = messages.all("y");
		A(msgs, "B0");

		msgs = messages.all("z");
		A(msgs, "C1", "C0");
	}

	@Test
	public void test_remove() {
		messages.add("A0", "x");
		messages.add("A1", "x");
		messages.add("A2", "x");
		messages.add("A3", "x");
		messages.add("B0", "y");
		Message c0 = messages.add("C0", "z");
		messages.add("C1", "z");

		// 开始移除
		messages.remove(messages.getByText("x", "A2"));
		messages.remove(messages.getByText("y", "B0"));
		messages.remove(c0);

		// 验证 ...
		List<Message> msgs;

		msgs = messages.all("x");
		A(msgs, "A3", "A1", "A0");

		msgs = messages.all("y");
		A(msgs);

		msgs = messages.all("z");
		A(msgs, "C1");

	}

	@Test
	public void test_list_read_unread() {
		messages.add("A0", "x");
		messages.add("A1", "x");
		messages.add("A2", "x");
		messages.add("A3", "x");

		messages.setAllRead("x", true);
		messages.setRead(messages.getByText("x", "A0"), false);

		// 验证 ...
		List<Message> msgs;

		msgs = messages.all("x", "!R:");
		A(msgs, "A0");

		msgs = messages.all("x", "R:");
		A(msgs, "A3", "A2", "A1");
	}

	@Test
	public void test_list() {
		messages.add("A0", "x");
		messages.add("A1", "x");
		messages.add("A2", "x");
		messages.add("A3", "x");
		messages.add("B0", "y");
		messages.add("C0", "z");
		messages.add("C1", "z");

		// 验证 ...
		List<Message> msgs;

		msgs = messages.all("x");
		A(msgs, "A3", "A2", "A1", "A0");

		msgs = messages.all("y");
		A(msgs, "B0");

		msgs = messages.all("z");
		A(msgs, "C1", "C0");

	}

	private static void A(List<Message> msgs, String... expects) {
		if (msgs.size() != expects.length) {
			throw Lang.makeThrow("Expect %d messages but %d", expects.length, msgs.size());
		}

		int i = 0;
		for (Message msg : msgs)
			assertEquals(expects[i++], msg.getText());
	}

	private MessageService messages;

	protected void onBefore() {
		messages = this.getService(MessageService.class);
		dao.create(Message.class, true);
	}

}
