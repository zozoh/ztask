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

	/**
	 * 关闭所有同步在自己身上的线程，子类可以重载，进行更多的操作
	 */
	synchronized public void stop() {

		// 设置标志
		stop = true;

		// 通知所有同步在自己上的线程，停止
		synchronized (this) {
			this.notifyAll();
		}

	}

}
