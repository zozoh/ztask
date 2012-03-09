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
import org.nutz.ztask.api.TaskQuery;
import org.nutz.ztask.api.TaskService;

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
	public void syncLables() {
		// 保存返回结果
		final List<Label> nodes = new LinkedList<Label>();
		final List<Label> rms = new LinkedList<Label>();
		final List<String> saves = new LinkedList<String>();

		// 保存从 Task 中同步出来的标签
		final Map<String, Integer> map = new HashMap<String, Integer>();
		dao.each(new Each<Task>() {
			public void invoke(int index, Task t, int length) {
				if (null != t.getLabels())
					for (String lbnm : t.getLabels()) {
						Integer c = map.get(lbnm);
						map.put(lbnm, null == c ? 1 : c + 1);
					}
			}
		}, Task.class, null, null);

		// 查所有的 Label, map 中木有的删除，有的话更新
		dao.each(new Each<Label>() {
			public void invoke(int index, Label lb, int length) {
				// 节点标签，稍候处理
				if (lb.isNode()) {
					nodes.add(lb);
					return;
				}
				// 开始处理子标签 ...
				Integer c = map.get(lb.getName());
				// 存在在最新同步出来的标签表中的话，尝试更新
				if (null != c) {
					// 不等，就更新
					if (lb.getCount() != c) {
						saves.add(lb.getName() + "?" + c);
					}
					// 从 map 中删除，那么剩下的，就是需要添加的
					map.remove(lb.getName());
				}
				// 自标签，直接删除
				else {
					rms.add(lb);
				}

			}
		}, Label.class, null, null);

		// 这些是常驻的标签表
		TaskService tasks = factory.tasks();
		Map<String, Label> pmap = this.toFlatMap(tasks.getGlobalInfo().getPersistentLabels());

		// 删除不在需要的子标签
		for (Label lb : rms) {
			// 常驻的，则保留
			if (pmap.containsKey(lb.getName())) {
				dao.updateById(Label.class, lb.get_id(), Moo.SET("count", 0));
			}
			// 否则，删除
			else {
				this.remove(lb);
			}
		}

		// 整理所有的节点标签
		for (Label lb : nodes) {
			// 不需要删除 ..
			if (pmap.containsKey(lb.getName()))
				continue;

			// 如果是节点标签，统计一下它的子节点
			// 如果字节为 0 则移除
			lb.setCount((int) dao.sum(Label.class, Moo.NEW("parent", lb.getName()), "count"));
			if (lb.getCount() > 0) {
				dao.updateById(Label.class, lb.get_id(), Moo.SET("count", lb.getCount()));
				continue;
			}

			// 这个节点不在有用了，删除它
			this.remove(lb);
		}

		// 增加不存在的标签
		if (!map.isEmpty())
			for (Map.Entry<String, Integer> en : map.entrySet()) {
				saves.add(en.getKey() + "?" + en.getValue());
			}

		// 执行更新
		saveList(saves.toArray(new String[saves.size()]));

	}

	@Override
	public Map<String, Label> toFlatMap(String[] lbnms) {
		HashMap<String, Label> map = new HashMap<String, Label>();

		if (null != lbnms)
			for (String lbnm : lbnms) {
				Label lb = get(lbnm);
				if (null == lb)
					continue;
				if (lb.isNode()) {
					for (String chd : lb.getChildren()) {
						Label chdlb = get(chd);
						map.put(chd, chdlb);
					}
				}
				map.put(lb.getName(), lb);
			}

		return map;
	}

	@Override
	public Label get(String lbnm) {
		return dao.findOne(Label.class, Moo.NEW("name", lbnm));
	}

	@Override
	public Label removeByName(String lbnm) {
		// TODO 无需先查询,直接执行remove即可
		Label lb = get(lbnm);
		remove(lb);
		return lb;
	}

	@Override
	public void remove(Label lb) {
		if (null != lb)
			dao.removeById(Label.class, lb.get_id());
	}

	@Override
	public boolean hasLabel(String lbnm) {
		return get(lbnm) != null;
	}

	@Override
	public List<Label> tops() {
		return list(null);
	}

	@Override
	public List<Label> list(String lbnm) {
		return dao.find(Label.class, Moo.NEW("parent", lbnm), MCur.NEW().asc("name"));
	}

	@Override
	public List<Label> all() {
		return dao.find(Label.class, null, null);
	}

	@Override
	public List<Label> saveList(String... lbstrs) {
		// TODO 使用findAndModify
		ArrayList<Label> list = new ArrayList<Label>(lbstrs.length);
		for (String lbstr : lbstrs) {
			Label lb = save(lbstr);
			if (null != lb)
				list.add(lb);
		}
		return list;
	}

	@Override
	public Label save(String lbstr) {
		if (Strings.isBlank(lbstr))
			return null;

		int pos = lbstr.lastIndexOf('?');
		String lbnm;
		int lbcount = 0;
		if (pos > 0) {
			lbnm = lbstr.substring(0, pos);
			lbcount = Integer.parseInt(lbstr.substring(pos + 1));
		} else {
			lbnm = lbstr;
		}

		Label lb = get(lbnm);
		if (null == lb) {
			lb = new Label();
			lb.setCount(lbcount);
			lb.setName(lbnm);
			dao.save(lb);
		} else if (lb.getCount() != lbcount) {
			lb.setCount(lbcount);
			dao.updateById(Label.class, lb.get_id(), Moo.SET("count", lbcount));
		}

		return lb;
	}

	@Override
	public Label rename(final String lbnm, final String newName) {
		Label lb = get(lbnm);
		// 可以改名
		if (null != lb && !Strings.isBlank(newName) && !lbnm.equals(newName)) {

			// 准备修改 Task 的回调函数
			final TaskService tasks = factory.tasks();
			final int[] c = new int[1];
			Each<Task> callback = new Each<Task>() {
				public void invoke(int index, Task t, int length) {
					String[] lbs = t.getLabels();
					HashMap<String, Object> map = new HashMap<String, Object>();
					for (int i = 0; i < lbs.length; i++) {
						String l = lbs[i];
						if (l.equalsIgnoreCase(lbnm)) {
							map.put(newName, null);
						} else {
							map.put(l, null);
						}
					}
					// 更新
					tasks.setLabels(t, map.keySet().toArray(new String[map.size()]));
					// 顺便记个数
					if (lbs.length == map.size())
						c[0]++;
				}
			};

			// 执行修改
			tasks.each(callback, TaskQuery.NEWf("#(%s)", lbnm));

			// 修改自身
			Label lbNew = get(newName);
			// 已经存在了，删除自己
			if (null != lbNew) {
				dao.removeById(Label.class, lb.get_id());
				lb = lbNew;
				lb.setCount(lb.getCount() + c[0]);
				dao.updateById(Label.class, lb.get_id(), Moo.SET("count", lb.getCount()));
			}
			// 简单的更新自身
			else {
				dao.updateById(Label.class, lb.get_id(), Moo.SET("name", newName)
															.set("count", c[0]));
				lb.setName(newName);
				lb.setCount(c[0]);
			}

			// 如果是叶子节点修改自己的父
			if (null != lb.getParent()) {
				Label plb = get(lb.getParent());
				this._sync_children(plb);
			}
			// 否则修改自己的 children
			else {
				dao.update(Label.class, Moo.NEW("parent", lbnm), Moo.SET("parent", newName));
			}
		}

		// 返回
		return lb;
	}

	@Override
	public List<Label> joinTo(String parentName, String... lbnms) {
		ArrayList<Label> list = new ArrayList<Label>(lbnms.length);
		for (String lbnm : lbnms) {
			Label lb = get(lbnm);
			// 没必要移动
			if (null == lb
				|| (null != parentName && Lang.equals(parentName, lb.getParent()))
				|| (null == parentName && null == lb.getParent()))
				continue;
			// 如果已经在其他的里面，则减去
			if (null != lb.getParent())
				dao.update(	Label.class,
							Moo.NEW("name", lb.getParent()),
							Moo.INC("count", -1 * lb.getCount()));

			// 更新数据
			dao.updateById(Label.class, lb.get_id(), Moo.SET("parent", parentName));

			// 更新旧的 parent
			if (null != lb.getParent()) {
				_sync_children(get(lb.getParent()));
			}

			// 记录 ...
			lb.setParent(parentName);
			list.add(lb);
		}
		if (!Strings.isBlank(parentName)) {
			// 如果父标签不存在，创建它
			Label plb = get(parentName);
			if (null == plb)
				plb = save(parentName);

			// 开始计算它的子
			_sync_children(plb);
		}
		return list;
	}

	private void _sync_children(Label plb) {
		if (null == plb)
			return;
		int count = 0;
		int i = 0;
		List<Label> children = this.list(plb.getName());
		String[] childrenNames = new String[children.size()];
		for (Label lb : children) {
			count += lb.getCount();
			childrenNames[i++] = lb.getName();
		}
		dao.updateById(	Label.class,
						plb.get_id(),
						Moo.SET("count", count).set("children", childrenNames));
	}

	@Override
	public long count() {
		return dao.count(Label.class, null);
	}

}
