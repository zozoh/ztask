package org.nutz.ztask;

public class ZTask {

	/**
	 * 获取 ZTask 的版本号，版本号的命名规范
	 * 
	 * <pre>
	 * [大版本号].[质量号].[发布流水号]
	 * </pre>
	 * 
	 * 这里有点说明
	 * <ul>
	 * <li>大版本号 - 表示 API 的版本，如果没有重大变化，基本上同样的大版本号，使用方式是一致的
	 * <li>质量号 - 可能为 a: Alpha内部测试品质, b:Beta 公测品质, r:Release 最终发布版
	 * <li>发布流水 - 每次发布增加 1
	 * </ul>
	 * 
	 * @return zTask 项目的版本号
	 */
	public static String version() {
		return "1.a.2";
	}

}
