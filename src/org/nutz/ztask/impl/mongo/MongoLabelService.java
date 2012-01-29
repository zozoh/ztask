package org.nutz.ztask.impl.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mongo.MongoConnector;
import org.nutz.mongo.util.MCur;
import org.nutz.mongo.util.Moo;
import org.nutz.ztask.api.Label;
import org.nutz.ztask.api.LabelService;
import org.nutz.ztask.api.Task;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
public class MongoLabelService extends AbstractMongoService implements LabelService {

	public MongoLabelService(MongoConnector conn, String dbname) {
		super(conn, dbname);
	}

	@Override
	public List<Label> syncLables() {
		// 保存返回结果
		final List<Label> list = new LinkedList<Label>();
		final List<String> dels = new LinkedList<String>();
		final List<String> saves = new LinkedList<String>();

		// 保存从 Task 中同步出来的标签
		final Map<String, Integer> map = new HashMap<String, Integer>();
		dao.each(new Each<Task>() {
			public void invoke(int index, Task t, int length) {
				if (null != t.getLabels())
					for (String lb : t.getLabels()) {
						Integer c = map.get(lb);
						map.put(lb, null == c ? 1 : c + 1);
					}
			}
		}, Task.class, null, null);

		// 查所有的 Label, map 中木有的删除，有的话更新
		dao.each(new Each<Label>() {
			public void invoke(int index, Label lb, int length) {
				Integer c = map.get(lb.getName());
				// 存在在最新同步出来的标签表中的话，常识更新
				if (null != c) {
					// 不等，就更新
					if (lb.getCount() != c) {
						saves.add(lb.getName() + ":" + c);
					}
					// 否则直接保存到结果中
					else {
						list.add(lb);
					}
					// 从 map 中删除，那么剩下的，就是需要添加的
					map.remove(lb.getName());
				}
				// 不存在，要删除
				else {
					dels.add(lb.getName());
				}

			}
		}, Label.class, null, null);

		// 删除不在需要的标签
		for (String lbnm : dels)
			this.remove(lbnm);

		// 增加不存在的标签
		if (!map.isEmpty())
			for (Map.Entry<String, Integer> en : map.entrySet()) {
				saves.add(en.getKey() + ":" + en.getValue());
			}

		// 执行更新
		list.addAll(save(saves.toArray(new String[saves.size()])));

		// 返回
		return list;
	}

	@Override
	public Label get(String labelName) {
		return dao.findOne(Label.class, Moo.born("name", labelName));
	}

	@Override
	public Label remove(String labelName) {
		// TODO 无需先查询,直接执行remove即可
		Label l = get(labelName);
		if (null != l)
			dao.removeById(Label.class, l.get_id());
		return l;
	}

	@Override
	public boolean hasLabel(String labelName) {
		return get(labelName) != null;
	}

	@Override
	public List<Label> getTopLabels() {
		return dao.find(Label.class, Moo.born("parent", null), MCur.born().asc("name"));
	}

	@Override
	public List<Label> getChildren(String labelName) {
		return dao.find(Label.class, Moo.born("parent", labelName), MCur.born().asc("name"));
	}

	@Override
	public List<Label> save(String... lbs) {
		// TODO 使用findAndModify
		ArrayList<Label> list = new ArrayList<Label>(lbs.length);
		for (String lb : lbs) {
			String[] ss = Strings.splitIgnoreBlank(lb, ":");

			String lbnm = ss[0];
			int count = ss.length > 1 ? Integer.valueOf(ss[1]) : 0;

			Label l = get(lbnm);
			// 不存在创建
			if (null == l) {
				l = new Label();
				l.setName(lbnm);
				l.setCount(count);
				dao.save(l);
			}
			// 否则更新
			else {
				dao.updateById(Label.class, l.get_id(), Moo.born().set("count", count));
			}

			list.add(l);
		}
		return list;
	}

	@Override
	public List<Label> moveTo(String parentName, String... labelNames) {
		ArrayList<Label> list = new ArrayList<Label>(labelNames.length);
		for (String labelName : labelNames) {
			Label l = get(labelName);
			if (null != parentName && Lang.equals(parentName, l.getParent()))
				continue;
			dao.updateById(Label.class, l.get_id(), Moo.born().set("parent", parentName));
			list.add(l);
		}
		return list;
	}

	@Override
	public long count() {
		return dao.count(Label.class, null);
	}

}
