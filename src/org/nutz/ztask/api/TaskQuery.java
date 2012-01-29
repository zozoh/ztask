package org.nutz.ztask.api;

import java.util.Map;
import java.util.TreeMap;

/**
 * 封装了一组 Task 任务查询条件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class TaskQuery {

	/**
	 * @return 简单的任务查询条件，仅包括一个按时间从新到旧的排序
	 */
	public static TaskQuery create() {
		return create(null);
	}

	/**
	 * @param keyword
	 *            关键字
	 * @return 任务查询条件，仅包括一个按时间从新到旧的排序，以及一个关键字
	 */
	public static TaskQuery create(String keyword) {
		TaskQuery tq = new TaskQuery();
		return tq.keyword(keyword);
	}

	/**
	 * 标题包含的关键字
	 * 
	 * @param keyword
	 *            关键字
	 * @return 自身
	 */
	public TaskQuery keyword(String keyword) {
		this.keyword = keyword;
		return this;
	}

	/**
	 * 任务所属的标签
	 * 
	 * @param lbs
	 *            标签列表
	 * @return 自身
	 */
	public TaskQuery labels(String... lbs) {
		for (String lb : lbs)
			labels.put(lb, null);
		return this;
	}

	/**
	 * 任务所属的用户，所给的用户都是或的关系
	 * 
	 * @param nms
	 *            用户名称
	 * @return 自身
	 */
	public TaskQuery owners(String... nms) {
		for (String nm : nms)
			owners.put(nm, null);
		return this;
	}

	/**
	 * 表示按时间从新到旧（这个是默认的）
	 * 
	 * @return 自身
	 */
	public TaskQuery new2old() {
		timeSort = -1;
		return this;
	}

	/**
	 * 表示按时间从旧到新
	 * 
	 * @return 自身
	 */
	public TaskQuery old2new() {
		timeSort = 1;
		return this;
	}

	private TaskQuery() {
		this.labels = new TreeMap<String, Object>();
		this.owners = new TreeMap<String, Object>();
		this.timeSort = -1;
		this.sortByCreateTime = true;
	}

	private String keyword;

	private Map<String, Object> labels;

	/**
	 * 哪些 Owner
	 */
	private Map<String, Object> owners;

	/**
	 * 时间排序，-1 表示 从新到旧（默认），1 表示从旧到新
	 */
	private int timeSort;

	/**
	 * 按创建时间排序，默认为 true，如果为 false，按照 LastModifiedTime 排序
	 */
	private boolean sortByCreateTime;

	public boolean isSortByCreateTime() {
		return sortByCreateTime;
	}

	public String getKeyword() {
		return keyword;
	}

	public String[] getLabels() {
		return labels.keySet().toArray(new String[labels.size()]);
	}

	public String[] getOwners() {
		return owners.keySet().toArray(new String[owners.size()]);
	}

	/**
	 * @return 表示按时间是否从新到旧
	 */
	public boolean isNew2old() {
		return -1 == timeSort;
	}

	/**
	 * @return 表示按时间是否从旧到新
	 */
	public boolean isOld2new() {
		return 1 == timeSort;
	}

}
