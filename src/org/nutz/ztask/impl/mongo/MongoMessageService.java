package org.nutz.ztask.impl.mongo;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Each;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.Mongos;
import org.nutz.mongo.util.MCur;
import org.nutz.mongo.util.Moo;
import org.nutz.ztask.api.Message;
import org.nutz.ztask.api.MessageService;
import org.nutz.ztask.util.Err;

/**
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MongoMessageService extends AbstractMongoService implements MessageService {

	@Override
	public Message get(String msgId) {
		return dao.findById(Message.class, msgId);
	}

	@Override
	public Message getByText(String owner, String msgText) {
		return dao.findOne(Message.class, Moo.NEW("owner", owner).append("text", msgText));
	}

	@Override
	public Message save(Message msg) {
		if (null == msg)
			throw Err.M.NULL_MSG();

		if (Strings.isBlank(msg.getOwner()))
			throw Err.M.NULL_OWNER(msg);

		if (Strings.isBlank(msg.getText()))
			throw Err.M.NULL_TEXT(msg);

		if (null == msg.getCreateTime())
			msg.setCreateTime(Times.now());

		return dao.save(msg);
	}

	@Override
	public Message add(String text, String owner) {
		Message msg = new Message();
		msg.setOwner(owner);
		msg.setText(text);
		msg.setRead(false);
		return save(msg);
	}

	@Override
	public Message remove(String msgId) {
		return dao.findAndRemove(Message.class, Mongos.dboId(msgId));
	}

	@Override
	public Message remove(Message msg) {
		if (null == msg)
			return null;
		return remove(msg.get_id());
	}

	@Override
	public void clearBefore(Date d, boolean force) {
		Moo q = Moo.D_LT("createTime", d).ne("favorite", false);
		// force 表示清除所有已读消息
		if (!force) {
			q.append("read", true);
		}
		// 执行 ...
		dao.remove(Message.class, q);
	}

	@Override
	public void setAllRead(String owner, boolean read) {
		dao.update(Message.class, Moo.NEW("owner", owner), Moo.SET("read", read));
	}

	@Override
	public Message setRead(Message msg, boolean read) {
		if (null == msg)
			return null;
		return dao.findAndModify(Message.class, Mongos.dboId(msg.get_id()), Moo.SET("read", read));
	}

	@Override
	public Message setFavorite(Message msg, boolean favo) {
		if (null == msg)
			return null;
		return dao.findAndModify(	Message.class,
									Mongos.dboId(msg.get_id()),
									Moo.SET("favorite", favo));
	}

	@Override
	public Message setNotified(Message msg, Date d) {
		if (null == msg)
			return null;
		return dao.findAndModify(Message.class, Mongos.dboId(msg.get_id()), Moo.SET("notified", d));
	}

	@Override
	public long countNew(String owner) {
		return dao.count(Message.class, Moo.NEW("owner", owner).eq("read", false));
	}

	private final static Pattern R = Pattern.compile("^((!?R)|(!?F)|(!?N))*(:)(.*)$");

	@Override
	public void each(	String owner,
						String keyword,
						String lastMsgId,
						int limit,
						Each<Message> callback) {
		if (null == callback)
			return;

		// 准备条件 & 设置 lastMsgId
		Moo q = Moo.NEW();

		if (!Strings.isBlank(lastMsgId))
			q.append("_id", Mongos.dbo("$lt", Mongos.dboId(lastMsgId)));

		if (!Strings.isBlank(owner))
			q.eq("owner", owner);

		// 分析 keyword
		keyword = Strings.trim(keyword);
		if (!Strings.isEmpty(keyword)) {
			Matcher m = R.matcher(keyword);
			String text;

			// 有特殊描述
			if (m.find()) {
				text = m.group(6);
				String R = m.group(2);
				String F = m.group(3);
				String N = m.group(4);
				// 读
				if (!Strings.isBlank(R)) {
					q.eq("read", !R.startsWith("!"));
				}
				// 收藏
				if (!Strings.isBlank(F)) {
					q.eq("favorite", !F.startsWith("!"));
				}
				// 通知
				if (!Strings.isBlank(N)) {
					// 未通知，所以，notified==null
					if (N.startsWith("!")) {
						q.eq("notified", null);
					}
					// 通知了，所以，notified!=null
					else {
						q.ne("notified", null);
					}

				}
			}
			// 只有关键字
			else {
				text = keyword;
			}

			// 增加全文匹配
			q.match("text", Pattern.compile(text));
		}

		// 设置 Limit， 固定的是倒序
		MCur mcur = MCur.DESC("createTime");
		if (limit > 0)
			MCur.LIMIT(limit);

		// 开始迭代
		dao.each(callback, Message.class, q, mcur);
	}

	@Override
	public List<Message> list(String owner, String keyword, String lastMsgId, int limit) {
		final List<Message> msgs = new LinkedList<Message>();
		each(owner, keyword, lastMsgId, limit, new Each<Message>() {
			public void invoke(int index, Message msg, int length) {
				msgs.add(msg);
			}
		});
		return msgs;
	}

	@Override
	public List<Message> list(String owner, String lastMsgId, int limit) {
		return list(owner, null, lastMsgId, limit);
	}

	@Override
	public List<Message> all(String owner) {
		return list(owner, null, 0);
	}

	@Override
	public List<Message> all(String owner, String keyword) {
		return list(owner, keyword, null, 0);
	}

	/*
	 * ========================================= 构造函数 =====
	 */
	public MongoMessageService(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

}
