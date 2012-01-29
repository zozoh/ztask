var ioc = {
	/*
	 * 系统整体配置
	 */
	safeMongoDaoInt : {
		type : "org.nutz.mongo.util.SafeMooMethodInterceptor"
	},
	myMongoDao : {
		type : "org.nutz.mongo.util.MyMongoDao",
		args : [ { refer : 'connector' }, { java : '$conf.getDB("db-name")' } ]
	},
        $aop : {
                type : 'org.nutz.ioc.aop.config.impl.JsonAopConfigration',
                fields : {
                        itemList : [
                                ['org.nutz.mongo.util.MyMongoDao','.+','ioc:safeMongoDaoInt']
                        ]
                }
        }

// ~ End Ioc
}