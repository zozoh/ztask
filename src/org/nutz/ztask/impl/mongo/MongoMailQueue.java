package org.nutz.ztask.impl.mongo;

import org.nutz.lang.Each;
import org.nutz.mail.AfterEach;
import org.nutz.mail.EachMail;
import org.nutz.mail.MailObj;
import org.nutz.mail.MailQueue;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.util.MCur;
import org.nutz.mongo.util.Moo;

public class MongoMailQueue extends AbstractMongoService implements MailQueue {

	public MongoMailQueue(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	@Override
	public void each(final EachMail callback) {
		dao.each(new Each<MongoMailObj>() {
			public void invoke(int index, MongoMailObj mo, int length) {
				AfterEach re = callback.doMail(mo);
				switch (re) {
				// 删除队列中的 Email
				case REMOVE:
					dao.removeById(MongoMailObj.class, mo.get_id());
					break;

				// 添加重试次数
				case RETRY:
					dao.updateById(MongoMailObj.class, mo.get_id(), Moo.INC("retryCount", 1));
					break;
				}
			}
		}, MongoMailObj.class, null, null, MCur.ASC("retryCount"));
	}

	@Override
	public MailObj saveMail(MailObj mo) {
		if (dao.count(MongoMailObj.class, Moo.NEW("subject", mo.getSubject())) > 0)
			return mo;
		return dao.save(mo);
	}

	@Override
	public void clear() {
		dao.remove(MongoMailObj.class);
	}

}
