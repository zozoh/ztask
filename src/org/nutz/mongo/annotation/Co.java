package org.nutz.mongo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明一个 MongoDB 实体
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Co {

	/**
	 * 声明一个 MongoDB 实体的集合名称
	 * <p>
	 * 空串表示采用类名的小写形式
	 * 
	 */
	String value() default "";

	/**
	 * 如果大于0,则设置为一个固定集合
	 */
	long cappedSize() default -1;
	
	long cappedMax() default -1;
}
