package org.nutz.ztask.web.module;

import org.nutz.mvc.SessionProvider;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.web.ajax.AjaxViewMaker;
import org.nutz.ztask.web.ZTaskSetup;

/**
 * 系统主模块，定义了 Nutz.Mvc 的配置信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Modules(scanPackage = true)
@ChainBy(args = "ajax.chain.js")
@Localization("msg")
@Fail("jsp:jsp.error")
@SetupBy(ZTaskSetup.class)
@IocBy(args = {	"*org.nutz.ioc.loader.json.JsonLoader",
				"ioc",
				"*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
				"org.nutz.ztask"}, type = ComboIocProvider.class)
@Views({AjaxViewMaker.class})
@SessionBy(value = SessionProvider.class, args = {"ioc:sessionManager"})
public class ZTaskMainModule {}
