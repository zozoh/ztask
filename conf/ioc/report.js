/* 
 * 这里配置报告生成器
 */
var ioc = {
	/*
	 * 渲染器
	 */
	reportRender : { type : 'org.nutz.doc.txt.TextDocRender' },
	/*
	 * 发送本周周报
	 */
	reportor : {
		type : 'org.nutz.ztask.impl.WeeklyReportor',
		fields : {
			tasks : { refer : 'taskService' },
			render : { refer : 'reportRender' },
			dbName : { java : '$conf.getDB("db-name")' },
			home : { java : '$conf.get("sys-report-home")' },
			reportDay : { java : '$conf.get("sys-report-day", 2)' }
		// 结束 fields
		}
	// 结束 'reportor'
	}
// 结束 Ioc 配置
}