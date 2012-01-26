var ioc = {
/*
 * 用户服务类
 */
userService : {
	type : 'org.nutz.ztask.impl.ReadonlyUserService',
	args : [ { java : '$conf.get("sys-usrs")' } ]
// ~ End bean
}
// ~ End Ioc
}