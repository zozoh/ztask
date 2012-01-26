var ioc = {
	/*
	 * 系统整体配置
	 */
	conf : { type : 'org.nutz.ztask.web.ZTaskConfig', args : [ "web.properties" ] },

	/*
	 * 数据库连接器
	 */
	connector : {
		type : 'org.nutz.mongo.MongoConnector',
		args : [ { java : "$conf.get('db-host')" }, { java : "$conf.getInt('db-port')" } ] }

// ~ End Ioc
}