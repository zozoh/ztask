package org.nutz.ztask.timer;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.nutz.ztask.api.TimerHandler;
import org.nutz.ztask.api.Timering;

@IocBean(name = "t_test")
public class TestTimer implements TimerHandler {

	@Override
	public String doHandle(String name, Timering ing) {
		ing.getSchedulerContext().set("xyz", Times.now());
		System.out.println(Times.sDTms(Times.now()));
		return "@tester:" + Times.sDTms(Times.now());
	}
}
