package org.nutz.mongo.annotation;

/**
 * 一个 MongoDB 文档的 "_id" 字段可能的形式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public enum CoIdType {

	/**
	 * 类型为 MongoDB 默认形式，即 ObjectId
	 */
	DEFAULT, 
	
	/**
	 * Nutz的 UUID 64 进制紧凑表现形式
	 */
	UU64,
	
	/**
	 * Nutz的 UUID 16 进制紧凑表现形式
	 */
	UU16,
	
	/**
	 * Java的 UUID 默认表现形式，即 toString 后的形式
	 */
	UUID,
	
	/**
	 * 自动增长
	 */
	AUTO_INC
}
