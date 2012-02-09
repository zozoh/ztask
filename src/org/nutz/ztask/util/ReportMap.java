package org.nutz.ztask.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZD;
import org.nutz.doc.meta.ZType;
import org.nutz.lang.Mirror;
import org.nutz.lang.eject.Ejecting;
import org.nutz.ztask.api.Task;

/**
 * 一个报告的统计帮助类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ReportMap {

	/**
	 * 将本身的内容加入到文档块的子
	 * 
	 * @param block
	 *            父块
	 */
	public void joinTo(ZBlock block) {
		List<String> keys = sortKeys();
		for (String key : keys) {
			List<Task> ts = get(key);
			ZBlock p = ZD.p(key);
			// 开始任务
			ZBlock ol = ZD.ol();
			for (Task t : ts) {
				ZBlock li = ZD.block(ZType.OLI);
				li.append(ZD.ele(t.toBrief()));
				ol.add(li);
			}
			// 加入父块
			block.add(p.add(ol));
		}

	}

	/**
	 * 根据键获取一个任务列表
	 * 
	 * @param key
	 *            键
	 * @return 任务列表
	 */
	public List<Task> get(String key) {
		return map.get(key);
	}

	/**
	 * @return 一个排过序（正序）的键列表
	 */
	public List<String> sortKeys() {
		Set<String> keys = map.keySet();
		List<String> list = new ArrayList<String>(keys.size());
		list.addAll(keys);
		Collections.sort(list);
		return list;
	}

	/**
	 * 根据 Task 的某一个 Key 分组任务
	 * 
	 * @param list
	 *            任务列表
	 * @param keyField
	 *            任务的键
	 */
	public void add(List<Task> list, String keyField) {
		Mirror<Task> mirror = Mirror.me(Task.class);
		final Ejecting ej = mirror.getEjecting(keyField);
		add(list, new KeyGetter<Task>() {
			public String getKey(Task task) {
				return ej.eject(task).toString();
			}
		});
	}

	/**
	 * 根据 Task 的某一个 Key 分组任务
	 * 
	 * @param list
	 *            任务列表
	 * @param keyGetter
	 *            得到分组 key 的回调方法
	 */
	public void add(List<Task> list, KeyGetter<Task> keyGetter) {
		for (Task t : list) {
			String key = keyGetter.getKey(t);
			List<Task> ts = map.get(key);
			if (null == ts) {
				ts = new LinkedList<Task>();
				map.put(key, ts);
			}
			ts.add(t);
		}
	}
	
	public boolean isEmpty(){
		return map.isEmpty();
	}
	
	public int size(){
		return map.size();
	}

	public ReportMap() {
		this.map = new HashMap<String, List<Task>>();
	}

	/**
	 * 根据某一个键（比如人名），统计的任务列表
	 */
	private Map<String, List<Task>> map;

}
