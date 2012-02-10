var ioc = {
	/*
	 * 系统整体配置
	 */
	conf : {
		type : 'org.nutz.ztask.web.ZTaskConfig',
		args : [ "web.properties" ]
	// ~ End bean
	},
	/*
	 * 数据库连接器
	 */
	connector : {
		type : 'org.nutz.mongo.MongoConnector',
		events : { depose : 'close' },
		args : [
			{ java : "$conf.get('db-host')" },
			{ java : "$conf.getInt('db-port')" } ]
	// ~ End bean
	},
	/*
	 * 抽象的服务
	 */
	mongoService : { // 仅仅提供构造函数
	args : [ { refer : 'connector' }, { java : '$conf.getDB("db-name")' } ]
	// ~ End bean
	},
	/*
	 * 存放系统定时启动的时间槽同时也作为 系统同步锁，用来 notifyAll
	 */
	schedule : {
		type : 'org.nutz.ztask.api.TimerSchedule',
		fields : {
			ioc : { refer : '$Ioc' },
			sec_each_slot : { java : '$conf.get("sys-timer-slot", 3600)' },
			thread_pool_size : { java : '$conf.get("sys-timer-thread-pool-size", 10)' } } },
	/*
	 * 抽象原子
	 */
	atom : { fields : {
		ioc : { refer : '$Ioc' },
		mails : { refer : 'mailQueue' },
		users : { refer : 'userService' },
		schedule : { refer : 'schedule' },
		tasks : { refer : 'taskService' } } }

// ~ End Ioc
}