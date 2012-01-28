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
	}

// ~ End Ioc
}