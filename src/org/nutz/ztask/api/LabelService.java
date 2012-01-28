package org.nutz.ztask.api;

import java.util.List;

public interface LabelService extends AbstractService {

	Label get(String labelName);

	List<Label> getTopLabels();

	List<Label> getChildren(String labelName);

	/**
	 * 增加一组标签，如果已存在的，则忽略
	 * 
	 * @param labelNames
	 *            标签的名称
	 * @return 新增的标签对象列表
	 */
	List<Label> addIfNoExists(String... labelNames);

	Label remove(String labelName);

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

	long count();

}
