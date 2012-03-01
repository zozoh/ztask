package org.nutz.ztask.api;

import java.util.Date;
import java.util.List;

import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.mongo.annotation.Co;
import org.nutz.mongo.annotation.CoField;
import org.nutz.mongo.annotation.CoId;
import org.nutz.ztask.util.ZTasks;

/**
 * 描述了一个 TASK 的全部信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Co("task")
public class Task {

	public Task() {
		number = new int[]{0, 0, 0, 0};
	}

	/**
	 * 任务的 ID，必须符合 ObjectId 的定义
	 * 
	 * @see org.bson.types.ObjectId
	 */
	@CoId
	private String _id;

	/**
	 * 本任务的父任务，一个任务可以被无限级拆分
	 */
	@CoField("pid")
	private String parentId;

	/**
	 * 本任务的内容简述，内容不要超过 140 个字
	 */
	@CoField("txt")
	private String text;

	/**
	 * 一个附加注释的列表
	 */
	@CoField("cmts")
	private String[] comments;

	/**
	 * 格式为 yyyy-MM-dd HH:mm:ss 格式的字符串
	 */
	@CoField("ct")
	private Date createTime;

	/**
	 * 格式为 yyyy-MM-dd HH:mm:ss 格式的字符串
	 */
	@CoField("lm")
	private Date lastModified;

	/**
	 * 加入堆栈的时间
	 */
	@CoField("at_push")
	private Date pushAt;

	/**
	 * 开始的时间
	 */
	@CoField("at_s")
	private Date startAt;

	/**
	 * 挂起的时间
	 */
	@CoField("at_h")
	private Date hungupAt;

	/**
	 * 弹出堆栈的时间
	 */
	@CoField("at_pop")
	private Date popAt;

	/**
	 * 计划完成时间
	 */
	@CoField("at_plan")
	private Date planAt;

	/**
	 * 所在堆栈的名称
	 */
	@CoField("stack")
	private String stack;

	/**
	 * 所有者的帐户名
	 */
	@CoField("ow")
	private String owner;

	/**
	 * 创建者的帐号
	 */
	@CoField("cre")
	private String creater;

	/**
	 * 任务状态
	 */
	@CoField("sta")
	private TaskStatus status;

	/**
	 * 本任务的标签
	 */
	@CoField("lbls")
	private String[] labels;

	/**
	 * 记录当前任务都有哪些关注者
	 */
	@CoField("wch")
	private String[] watchers;

	/**
	 * 记录任务堆栈操作历史
	 */
	@CoField("hiss")
	private TaskHistory[] history;

	/**
	 * 保存任务数量的统计
	 * <ul>
	 * <li>0 - ALL: 总的子任务数量
	 * <li>1 - DONE: 完成的子任务数量
	 * <li>2 - ING: 正在进行的子任务数量
	 * <li>3 - NEW: 未指派的子任务数量
	 * <li>4 - HUNGUP: 挂起的子任务数量
	 * </ul>
	 */
	@CoField("num")
	private int[] number;

	public static final int I_ALL = 0;
	public static final int I_DONE = 1;
	public static final int I_ING = 2;
	public static final int I_NEW = 3;
	public static final int I_HUNGUP = 4;

	/**
	 * 缓存自身的 Children 列表
	 */
	private List<Task> children;

	/**
	 * 缓存自身的所有的 parent 节点，第一个元素一定是根节点
	 */
	private List<Task> parents;

	public TaskHistory[] getHistory() {
		return history;
	}

	public void setHistory(TaskHistory[] history) {
		this.history = history;
	}

	public List<Task> getChildren() {
		return children;
	}

	public void setChildren(List<Task> children) {
		this.children = children;
	}

	public List<Task> getParents() {
		return parents;
	}

	public void setParents(List<Task> parents) {
		this.parents = parents;
	}

	private int N(int index) {
		return null == number || index >= number.length ? 0 : number[index];
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getText() {
		return text;
	}

	public void setText(String title) {
		this.text = title;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String[] getComments() {
		return comments;
	}

	public void setComments(String[] comments) {
		this.comments = comments;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getPushAt() {
		return pushAt;
	}

	public void setPushAt(Date pushAt) {
		this.pushAt = pushAt;
	}

	public Date getStartAt() {
		return startAt;
	}

	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}

	public Date getHungupAt() {
		return hungupAt;
	}

	public void setHungupAt(Date hungupAt) {
		this.hungupAt = hungupAt;
	}

	public Date getPopAt() {
		return popAt;
	}

	public void setPopAt(Date popAt) {
		this.popAt = popAt;
	}

	public Date getPlanAt() {
		return planAt;
	}

	public void setPlanAt(Date planAt) {
		this.planAt = planAt;
	}

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public String getStack() {
		return Strings.sBlank(stack, ZTasks.NULL_STACK);
	}

	public boolean isInStack() {
		return !ZTasks.isBlankStack(stack);
	}

	public void setStack(String stack) {
		this.stack = Strings.sBlank(stack, ZTasks.NULL_STACK);
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String createor) {
		this.creater = createor;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String[] getWatchers() {
		return watchers;
	}

	public void setWatchers(String[] watchers) {
		this.watchers = watchers;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public int[] getNumber() {
		return number;
	}

	public void setNumber(int[] number) {
		this.number = number;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public int getNumberAll() {
		return N(I_ALL);
	}

	public int getNumberNew() {
		return N(I_NEW);
	}

	public int getNumberDone() {
		return N(I_DONE);
	}

	public int getNumberIng() {
		return N(I_ING);
	}

	public int getNumberHungup() {
		return N(I_HUNGUP);
	}

	public int getNumberProcessing() {
		return N(I_ING) + N(I_HUNGUP);
	}

	public boolean isChildrenNew() {
		return getNumberAll() > 0 && getNumberAll() == getNumberNew();
	}

	public boolean isChildrenProcessing() {
		return getNumberAll() > 0
				&& getNumberAll() > getNumberDone()
				&& getNumberAll() != getNumberNew();
	}

	public boolean isChildrenDone() {
		return getNumberAll() > 0 && getNumberAll() == getNumberDone();
	}

	public boolean isTop() {
		return Strings.isBlank(this.parentId);
	}

	public boolean isLeaf() {
		return 0 == getNumberAll();
	}

	public String toBrief() {
		String prefix = "..";
		switch (status) {
		case DONE:
			prefix = "OK";
			break;
		case ING:
			prefix = "..";
			break;
		case HUNGUP:
			prefix = "~~";
			break;
		case NEW:
			prefix = "++";
			break;
		default:
			prefix = "??";
		}
		return String.format(	"[%s] %s  # %s%s",
								prefix,
								Strings.alignLeft(text, 48, ' '),
								ZTasks.isBlankStack(stack) ? "" : " {" + stack + "} ",
								Times.sD(lastModified));
	}

	public String toString() {
		return String.format(	"#%s@%s<%s> [%s] ( ..%d+%d %d/%d):'%s'",
								_id,
								owner,
								status,
								stack,
								getNumberProcessing(),
								getNumberNew(),
								getNumberDone(),
								getNumberAll(),
								null == text ? "<..EMPTY..>"
											: text.length() > 10 ? text.substring(0, 8) + "..."
																: text);
	}
}
