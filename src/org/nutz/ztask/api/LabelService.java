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
	 * @param labelName
	 *            标签名称
	 * @return 标签对象
	 */
	Label get(String labelName);

	/**
	 * @return 所有顶级标签列表
	 */
	List<Label> getTopLabels();

	/**
	 * 得到某标签所有的子标签
	 * 
	 * @param labelName
	 *            标签名
	 * @return 某标签的子标签
	 */
	List<Label> getChildren(String labelName);

	/**
	 * 增加一组标签，如果已存在的，则更新
	 * <p>
	 * 支持特殊格式的字符串
	 * 
	 * <pre>
	 *    标签名[:计数]
	 * 比如:
	 *    save(&quot;A&quot;, &quot;B:3&quot;, &quot;D:45&quot;);
	 * </pre>
	 * 
	 * @param lbs
	 *            标签列表
	 * @return 新增的标签对象列表
	 */
	List<Label> save(String... lbs);

	/**
	 * 移除一个标签
	 * 
	 * @param labelName
	 *            标签名
	 * @return 标签，null 表示不存在
	 */
	Label remove(String labelName);

	/**
	 * 是否存在某标签
	 * 
	 * @param labelName
	 *            标签名
	 * @return 是否存在
	 */
	boolean hasLabel(String labelName);

	/**
	 * 将一组标签移动成某一个标签的子
	 * <p>
	 * 如果父标签不存在，则将所有的标签设置成根标签，如果标签不需要设置 parentName，则忽略
	 * 
	 * @param parentName
	 *            父标签名称
	 * @param labelNames
	 *            自标签名称
	 * @return 移动的标签对象列表
	 */
	List<Label> moveTo(String parentName, String... labelNames);

	/**
	 * @return 标签的数量
	 */
	long count();

}
