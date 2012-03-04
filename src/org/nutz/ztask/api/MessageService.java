package org.nutz.ztask.api;

import java.util.Date;
import java.util.List;

import org.nutz.lang.Each;

/**
 * 提供消息服务接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface MessageService {

	/**
	 * 获取一个消息
	 * 
	 * @param msgId
	 *            消息ID
	 * @return 消息对象
	 */
	Message get(String msgId);

	/**
	 * 根据消息的正文获取某用户一个消息对象
	 * 
	 * @param owner
	 *            用户名
	 * @param msgText
	 *            消息正文
	 * @return 消息对象
	 */
	Message getByText(String owner, String msgText);

	/**
	 * 保存一个消息
	 * 
	 * @param msg
	 *            消息对象
	 * @return 新建立的消息对象
	 */
	Message save(Message msg);

	/**
	 * 增加一条消息
	 * 
	 * @param text
	 *            消息正文
	 * @param owner
	 *            所有者
	 * @return 新增加的消息对象
	 */
	Message add(String text, String owner);

	/**
	 * 删除一条消息
	 * 
	 * @param msgId
	 *            消息 ID
	 * 
	 * @return 旧消息对象，null 表示不存在
	 */
	Message remove(String msgId);

	/**
	 * 删除一条消息
	 * 
	 * @param msg
	 *            消息对象
	 * 
	 * @return 旧消息对象，null 表示不存在
	 */
	Message remove(Message msg);

	/**
	 * 清除某个所有者全部的已读且未收藏的消息
	 * 
	 * @param ownerName
	 *            所有者名称
	 */
	void clearMine(String ownerName);

	/**
	 * 清除一个时间点之前的消息。（不包括收藏的消息）
	 * 
	 * @param d
	 *            时间点（不包括，精确到秒）
	 * @param force
	 *            是否强制。true 表示清除所有非收藏消息 false 表示清除所有已读消息
	 */
	void clearBefore(Date d, boolean force);

	/**
	 * 设置某用户下所有的消息已读状态
	 * 
	 * @param owner
	 *            用户
	 * @param read
	 *            是否已读
	 */
	void setAllRead(String owner, boolean read);

	/**
	 * 设置某条消息的一度状态
	 * 
	 * @param msg
	 *            消息对象
	 * @param read
	 *            是否已读
	 * @return 旧消息对象，null 表示不存在
	 */
	Message setRead(Message msg, boolean read);

	/**
	 * 设置某条消息的收藏状态
	 * 
	 * @param msg
	 *            消息对象
	 * @param favo
	 *            是否收藏
	 * @return 旧消息对象，null 表示不存在
	 */
	Message setFavorite(Message msg, boolean favo);

	/**
	 * 设置通知的时间
	 * 
	 * @param msg
	 *            消息对象
	 * @param d
	 *            通知时间，null 表示取消通知时间的设定
	 * @return 旧消息对象，null 表示不存在
	 */
	Message setNotified(Message msg, Date d);

	/**
	 * 返回某用户的未读消息数量
	 * 
	 * @param owner
	 *            用户
	 * @return 未读消息数量
	 */
	long countNew(String owner);

	/**
	 * 按创建时间从最新到最早，迭代某用户下，相关的消息
	 * 
	 * <pre>
	 * 关键字 "keyword" 可以支持如下特殊选项
	 * 
	 * "!R:" 开头表仅仅列出未读消息
	 *    
	 *    比如:  "!R: xxxxx"  或  "!R:"    
	 * 
	 * "R:" 开头表仅仅列出已读消息
	 * 
	 * 	  比如:  "R: xxxxx"  或  "R:"
	 * 
	 * "F:" 开头表仅仅列出收藏消息
	 * 
	 *    比如:  "F: xxxxx"  或  "F:"
	 * 
	 * "!F:" 开头表仅仅列出未收藏消息
	 * 
	 *    比如:  "!F: xxxxx"  或  "!F:"
	 * 
	 * "N:" 开头表仅仅列出未被通知过的消息
	 * 
	 *    比如:  "N: xxxxx"  或  "N:"
	 *    
	 * "!N:" 开头表仅仅列出未被通知过的消息
	 * 
	 *    比如:  "!N: xxxxx"  或  "!N:"
	 *    
	 * 可以混合
	 * 
	 * 	  比如:  
	 * 		> "R!N: xxxxx"   // 已读，但未被通知过的消息  
	 *      > "F!R!N:"       // 已收藏，但未读且未被通知过的消息
	 * 
	 * </pre>
	 * 
	 * @param owner
	 *            用户名
	 * @param keyword
	 *            关键字，可为 null
	 * @param String
	 *            lastMsgId 从那条消息开始向后读取，null 为从头读取
	 * @param limit
	 *            读取多少条，小于等于 0 表示读取全部
	 */
	void each(String owner, String keyword, String lastMsgId, int limit, Each<Message> callback);

	/**
	 * 列出某用户下，相关的消息
	 * 
	 * @param owner
	 *            用户名
	 * @param keyword
	 *            关键字，可为 null
	 * @param String
	 *            lastMsgId 从那条消息开始向后读取，null 为从头读取
	 * @param limit
	 *            读取多少条，小于等于 0 表示读取全部
	 * @return 一个按创建时间从最新到最早的消息列表
	 * @see #each(String, String, String, int, Each)
	 */
	List<Message> list(String owner, String keyword, String lastMsgId, int limit);

	/**
	 * 列出某用户下，相关的消息
	 * 
	 * @param owner
	 *            用户名
	 * @param String
	 *            lastMsgId 从那条消息开始向后读取，null 为从头读取
	 * @param limit
	 *            读取多少条，小于等于 0 表示读取全部
	 * @return 一个按创建时间从最新到最早的消息列表
	 */
	List<Message> list(String owner, String lastMsgId, int limit);

	/**
	 * 列出某用户下，相关的消息
	 * 
	 * @param owner
	 *            用户名
	 * @param keyword
	 *            关键字，可为 null
	 * 
	 * @return 一个按创建时间从最新到最早的消息列表
	 * @see #each(String, String, String, int, Each)
	 */
	List<Message> all(String owner, String keyword);

	/**
	 * 列出某用户下，相关的消息
	 * 
	 * @param owner
	 *            用户名
	 * @return 一个按创建时间从最新到最早的消息列表
	 */
	List<Message> all(String owner);
}
