/* 
 * 这里准备了一些钩子，可以供 Timer 使用
 */
var ioc = {
	/*
	 * 发送本周周报
	 */
	t_weekend_report : {
		type : 'org.nutz.ztask.timer.WeeklyReportSender',
		fields : {
			users : { refer : 'userService' },
			tasks : { refer : 'taskService' },
			reportor : { refer : 'reportor' },
			mails : { refer : 'mailQueue' } } },

	// 仅仅测试
	t_test : { type : 'org.nutz.ztask.timer.TestTimer' }
// ~ End
}