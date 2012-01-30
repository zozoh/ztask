package org.nutz.ztask.api;

/**
 * 是 GInfo 的内部对象，描述了对于一个文本的格式化方式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class GFormatInfo {

	/**
	 * 正则表达式，用来匹配文本的
	 */
	private String regex;

	/**
	 * 表达式的第几个子表达式的内容要用来填充占位符，0 表示整体用来填充占位符
	 */
	private int gnum;

	/**
	 * 一个字符串模板，其中 "[$$]" 为占位符，比如类似下面的写法
	 * 
	 * <pre>
	 * http://abc.com/x/y?id=[$$]
	 * </pre>
	 */
	private String tmpl;

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public int getGnum() {
		return gnum;
	}

	public void setGnum(int gnum) {
		this.gnum = gnum;
	}

	public String getTmpl() {
		return tmpl;
	}

	public void setTmpl(String tmpl) {
		this.tmpl = tmpl;
	}

}
