package org.nutz.mongo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述了一个实体字段，以及它在 MongoDB 中的名字，如果为空串，就取 Java 字段的名字
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface CoField {

	String value() default "";

	/*ref doc or embem doc*/
	boolean ref() default false;
	
	boolean lazy() default false;
}
