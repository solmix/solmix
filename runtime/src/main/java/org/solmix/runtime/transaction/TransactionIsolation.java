package org.solmix.runtime.transaction;

import java.sql.Connection;

/**
 * 数据库事务的隔离级别有4种，由低到高分别为Read uncommitted 、Read committed 、Repeatable read 、Serializable 。
 * 而且，在事务的并发操作中可能会出现脏读，不可重复读，幻读
 * @author solmix.f@gmail.com
 *
 */
public enum TransactionIsolation {

	
	/**
	 * 大多数数据库默认的事务隔离级别是Read committed，比如Sql Server , Oracle。MySQL的默认隔离级别是Repeatable read
	 */
	DEFAULT(-1 ) ,
   
	/**
	 * 读未提交，顾名思义，就是一个事务可以读取另一个未提交事务的数据
	 */
	READ_UNCOMMITTED( Connection.TRANSACTION_READ_UNCOMMITTED ) ,
   
	/**
	 * 读提交，顾名思义，就是一个事务要等另一个事务提交后才能读取数据
	 */
	READ_COMMITTED( Connection.TRANSACTION_READ_COMMITTED) ,

	/**
	 * 重复读，就是在开始读取数据（事务开启）时，不再允许修改操作
	 */
	REPEATABLE_READ( Connection.TRANSACTION_REPEATABLE_READ ) ,
	
	/**
	 * Serializable 是最高的事务隔离级别，在该级别下，事务串行化顺序执行，可以避免脏读、不可重复读与幻读。
	 * 但是这种事务隔离级别效率低下，比较耗数据库性能，一般不使用
	 */
	SERIALIZABLE( Connection.TRANSACTION_SERIALIZABLE ) ;
	

   private int value;

   TransactionIsolation( int value )
   {
      this.value = value;
   }

   public int value()
   {
      return value;
   }
   public static TransactionIsolation fromValue(int v) {
       for (TransactionIsolation c : TransactionIsolation.values()) {
           if (c.value==v) {
               return c;
           }
       }
       throw new IllegalArgumentException("Not supprot value"+v);
   }
}
