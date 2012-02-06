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
		fields : {
			users : { refer : "userService" },
			tasks : { refer : 'taskService' },
			stacksPath : 'stacks.txt',
			autosync : true }
	// ~ End bean
	},
	/*
	 * Task 服务类
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
	}

// ~ End Ioc
}