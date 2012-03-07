package org.nutz.ztask.api;

import java.util.List;

public interface LabelService extends AbstractService {

	/**
	 * 同步系统中的标签
	 * <ul>
	 * <li>它会检查当前数据库中所有的任务标签
	 * <li>统计每个标签的任务数量，保存到 Label 集合中
	 * <li>如果标签不存在任务了，将从 Label 集合删除
	 * </ul>
	 * 
	 * @return 系统中所有的标签（最新）
	 */
	List<Label> syncLables();

	/**
	 * 根据标签名称得到一个标签
	 * 
	 * @param lbnm
	 *            标签名称
	 * @return 标签对象
	 */
	Label get(String lbnm);

	/**
	 * @return 所有顶级标签列表
	 */
	List<Label> tops();

	/**
	 * 列出一组标签，
	 * 
	 * @param lbnm
	 *            父标签，如果为 null，则表示列出所有顶级标签
	 * @return 某标签的子标签
	 */
	List<Label> list(String lbnm);

	/**
	 * @return 所有标签
	 */
	List<Label> all();

	/**
	 * 将一个标签改名， 同时，所有使用这个标签的 task 也会被修改
	 * 
	 * @param lbnm
	 *            旧标签名
	 * @param newName
	 *            新标签名
	 * @return 修改后的标签
	 */
	Label rename(String lbnm, String newName);

	/**
	 * 保存一组标签，如果已存在的，则获取
	 * 
	 * @param lbstrs
	 *            标签字符串列表，格式为 "名称[?计数]"
	 * @return 标签对象列表
	 * @see #save(String)
	 */
	List<Label> saveList(String... lbstrs);

	/**
	 * 保存一个标签，如果存在，则获取
	 * 
	 * 下面的这些标签名称都是合法的
	 * 
	 * <pre>
	 * A     # 表示名称为 A 且计数为 0
	 * A?2   # 表示名称为 A 且计数为 2
	 * </pre>
	 * 
	 * @param lbstr
	 *            标签字符串，格式为 "名称[?计数]"
	 * @return 标签对象
	 */
	Label save(String lbstr);

	/**
	 * 移除一个标签
	 * 
	 * @param lbnm
	 *            标签名
	 * @return 标签，null 表示不存在
	 */
	Label removeByName(String lbnm);

	/**
	 * 移除一个标签对象
	 * 
	 * @param lb
	 *            标签对象
	 */
	void remove(Label lb);

	/**
	 * 是否存在某标签
	 * 
	 * @param lbnm
	 *            标签名
	 * @return 是否存在
	 */
	boolean hasLabel(String lbnm);

	/**
	 * 将一组标签移动成某一个标签的子
	 * <p>
	 * 如果父标签名称为 null ，则将所有的标签设置成根标签<br>
	 * 否则如果不存在，则创建父标签
	 * 
	 * @param parentName
	 *            父标签名称
	 * @param lbnms
	 *            自标签名称
	 * @return 移动的标签对象列表
	 */
	List<Label> joinTo(String parentName, String... lbnms);

	/**
	 * @return 标签的数量
	 */
	long count();

}
