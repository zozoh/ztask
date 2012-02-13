var ioc = {
	/*
	 * 计划更新原子
	 */
	schd_update : {
		parent : 'atom',
		type : 'org.nutz.ztask.thread.ScheduleUpdateAtom' },
	/*
	 * 计划执行原子
	 */
	timer_run : {
		parent : 'atom',
		type : 'org.nutz.ztask.thread.TimerRunnerAtom' },
	/*
	 * 邮件发送原子
	 */
	send_mail : {
		parent : 'atom',
		type : 'org.nutz.ztask.thread.SendMailAtom',
		fields : {
			interval : { java : '$conf.get("sys-mail-interval",30)' },
			lock : { refer : 'mailLock' } } }
// ~ End Ioc
}