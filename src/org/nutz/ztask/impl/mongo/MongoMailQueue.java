package org.nutz.ztask.impl.mongo;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mail.AfterEach;
import org.nutz.mail.EachMail;
import org.nutz.mail.MailObj;
import org.nutz.mail.MailQueue;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.util.MCur;
import org.nutz.mongo.util.Moo;

public class MongoMailQueue extends AbstractMongoService implements MailQueue {

	private final static Log log = Logs.get();

	public MongoMailQueue(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	@Override
	public void each(final EachMail callback) {
		dao.each(new Each<MongoMailObj>() {
			public void invoke(int index, MongoMailObj mo, int length) {
				AfterEach re = callback.doMail(mo);

				if (log.isDebugEnabled())
					log.debugf(	"MQ::%s :: [%s] => (%s) ",
								mo.get_id(),
								mo.getSubject(),
								Lang.concat(",", mo.getTos()));

				if (null != re)
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
		},
					MongoMailObj.class,
					null,
					null,
					MCur.ASC("retryCount"));
	}

	@Override
	public MailObj joinMail(MailObj mo) {
		// 如果已经有了这个用户
		MongoMailObj dbMail = dao.findOne(MongoMailObj.class, Moo.NEW("tos", mo.getTos()));
		if (null != dbMail) {
			dbMail.setMailBody(dbMail.getMailBody() + "\n" + mo.getMailBody());
			dao.updateById(	MongoMailObj.class,
							dbMail.get_id(),
							Moo.SET("mailBody", dbMail.getMailBody()));
			return dbMail;
		}
		// 否则保存
		return dao.save(mo);
	}

	@Override
	public MailObj dropMail(MailObj mo) {
		if (null != mo && mo instanceof MongoMailObj) {
			return dao.findAndRemove(MailObj.class, Mongos.dboId(((MongoMailObj) mo).get_id()));
		}
		return mo;
	}

	@Override
	public long count() {
		return dao.count(MongoMailObj.class, null);
	}

	@Override
	public void clear() {
		dao.remove(MongoMailObj.class);
	}

}
