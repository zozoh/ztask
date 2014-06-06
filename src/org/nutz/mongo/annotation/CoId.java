package org.nutz.mongo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述了 ID 字段，以及 ID 的获取方式
 * <p>
 * 如果 CoIdType.DEFAULT，那么就相当于 MongoDB 的默认 ID 生成方式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface CoId {

	CoIdType value() default CoIdType.DEFAULT;

}
