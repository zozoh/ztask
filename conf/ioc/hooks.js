/* 
 * 这里准备了一些钩子，可以供用户选择使用
 */
var ioc = {
	/*
	 * 暴力同步系统中的标签 - !!慢
	 */
	h_sync_label : { type : 'org.nutz.ztask.hook.SyncLabel' },
	/*
	 * 将用户的修改，保存到相关的通知列表中
	 */
	h_notify : { type : 'org.nutz.ztask.hook.AddNotify' }

}