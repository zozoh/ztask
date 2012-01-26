package org.nutz.ztask.web.module;

import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.ztask.web.ZTaskSetup;

/**
 * 系统主模块，定义了 Nutz.Mvc 的配置信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Modules(scanPackage = true)
@Localization("msg")
@Fail("jsp:jsp.error")
@SetupBy(ZTaskSetup.class)
@IocBy(args = {	"*org.nutz.ioc.loader.json.JsonLoader",
				"ioc",
				"*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
				"org.nutz.ztask.web.module"}, type = ComboIocProvider.class)
public class ZTaskMainModule {}
