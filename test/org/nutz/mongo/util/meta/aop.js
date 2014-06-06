var ioc = {
	/*
	 * 系统整体配置
	 */
	safeMongoDaoInter : { type : "org.nutz.mongo.util.SafeMooMethodInterceptor" },
	/*
	 * Dao 对象
	 */
	myMongoDao : {
		type : "org.nutz.mongo.util.MyMongoDao",
		args : [ { refer : 'connector' }, { java : '$conf.getDB("db-name")' } ] },
	/*
	 * 设置一个拦截器
	 */
	$aop : {
		type : 'org.nutz.ioc.aop.config.impl.JsonAopConfigration',
		fields : { itemList : [ [
			'org.nutz.mongo.util.MyMongoDao',
			'.+',
			'ioc:safeMongoDaoInter' ] ] } }

// ~ End Ioc
}