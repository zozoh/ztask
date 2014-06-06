package org.nutz.mongo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述了一个 MongoDB 集合的索引。
 * <p>
 * 例如
 * 
 * <pre>
 * ＃单一键索引，升序
 * @CoIndex("+name")
 * 
 * ＃单一键索引，降序
 * @CoIndex("-name")
 * 
 * ＃复合索引
 * @CoIndex("+name,-age")
 * 
 * #自定义索引名称
 * @CoIndex("seq_abc:+name,-age")
 * 
 * #多个索引
 * @CoIndex({"seq_abc:+name,-age", "+type"})
 * 
 * #唯一索引，默认名称
 * @CoIndex("!:+name")
 * 
 * #唯一索引，自定义名称名称
 * @CoIndex("!seq_name:+name")
 * </pre>
 * 
 * 这里需要说明一下
 * <ul>
 * <li>索引字段必须用 "+" 或者 "-" 开头， "+" 表示升序，"-" 表示降序
 * <li>索引字段用半角逗号 "," 分隔
 * <li>空格将被忽略，所以你随便写多少个空格也无所谓
 * <li>索引名称放在半角冒号 ":" 前面， 如果没有，则用 MongoDB 的默认规则建立索引名称
 * <li>索引名称如果以叹号开头，表示唯一索引
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface CoIndexes {

	String[] value();

}
