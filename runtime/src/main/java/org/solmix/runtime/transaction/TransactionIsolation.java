package org.solmix.runtime.transaction;

import java.sql.Connection;

public enum TransactionIsolation {

	
	DEFAULT(-1 ) ,
   
	READ_UNCOMMITTED( Connection.TRANSACTION_READ_UNCOMMITTED ) ,
   
	READ_COMMITTED( Connection.TRANSACTION_READ_COMMITTED) ,

	REPEATABLE_READ( Connection.TRANSACTION_REPEATABLE_READ ) ,
	
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
