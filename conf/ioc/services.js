var ioc = {
	/*
	 * 用户服务类
	 */
	userService : {
		type : 'org.nutz.ztask.impl.ReadonlyUserService',
		args : [ { java : '$conf.get("sys-usrs")' } ]
	// ~ End bean
	},
	/*
	 * 初始化服务类
	 */
	initService : {
		parent : 'mongoService',
		type : 'org.nutz.ztask.impl.mongo.MongoInitService',
		fields : { stacksPath : 'stacks.txt', autosync : true }
	// ~ End bean
	},
	/*
	 * 邮件队列服务
	 */
	mailQueue : {
		parent : 'mongoService',
		type : 'org.nutz.ztask.impl.mongo.MongoMailQueue'
	// ~ End bean
	},
	/*
	 * 基础服务类
	 */
	taskService : {
		parent : 'mongoService',
		type : 'org.nutz.ztask.impl.mongo.MongoTaskService'
	// ~ End bean
	},
	/*
	 * 标签服务类
	 */
	labelService : {
		parent : 'mongoService',
		type : 'org.nutz.ztask.impl.mongo.MongoLabelService'
	// ~ End bean
	},
	/*
	 * 消息服务类
	 */
	messageService : {
		parent : 'mongoService',
		type : 'org.nutz.ztask.impl.mongo.MongoMessageService'
	// ~ End bean
	},
	/*
	 * 钩子服务类
	 */
	hookService : {
		parent : 'mongoService',
		type : 'org.nutz.ztask.impl.mongo.MongoHookService',
		fields : {
			ioc : { refer : "$Ioc" },
			factory : { refer : 'serviceFactory' } }
	// ~ End bean
	},
	/*
	 * 组合钩子的任务服务类
	 */
	hookedTaskService : {
		type : 'org.nutz.ztask.impl.HookedTaskService',
		fields : {
			glock : { refer : "schedule" },
			ioc : { refer : "$Ioc" },
			users : { refer : "userService" },
			tasks : { refer : 'taskService' },
			hooks : { refer : 'hookService' } }
	// ~ End bean
	}

// ~ End Ioc
}