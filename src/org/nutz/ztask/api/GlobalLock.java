package org.nutz.ztask.api;

/**
 * 全局锁，存放在 Ioc 容器中，它有一个标志位，表示系统是否需要停止
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class GlobalLock {

	private boolean stop;

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
