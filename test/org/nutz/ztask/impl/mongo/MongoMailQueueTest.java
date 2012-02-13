package org.nutz.ztask.impl.mongo;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mail.MailQueue;
import org.nutz.ztask.ZTaskCase;
import org.nutz.ztask.util.ZTasks;

public class MongoMailQueueTest extends ZTaskCase {

	@Test
	public void test_simple_save_count() {
		mails.joinMail(ZTasks.textMail("A", "aaa", "zozoh"));
		assertEquals(1, mails.count());

		mails.joinMail(ZTasks.textMail("B", "bbb", "wendal"));
		assertEquals(2, mails.count());

		MongoMailObj mo = (MongoMailObj) mails.joinMail(ZTasks.textMail("C", "ccc", "zozoh"));
		assertEquals(2, mails.count());

		mo = dao.findById(MongoMailObj.class, mo.get_id());
		assertEquals("A", mo.getSubject());
		assertEquals("aaa\nccc", mo.getMailBody());
	}

	private MailQueue mails;

	protected void onBefore() {
		mails = this.getService(MailQueue.class);
		dao.create(MongoMailObj.class, true);
	}

}
