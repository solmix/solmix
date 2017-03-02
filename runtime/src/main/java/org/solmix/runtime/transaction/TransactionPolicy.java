package org.solmix.runtime.transaction;



public enum TransactionPolicy {
	 /**
     * 如果没有事物就创建一个 
     */
	REQUIRED( "REQUIRED" ) ,
    /**
     * 需要指定一个事物，没有就抛错
     */
	MANDATORY( "MANDATORY" ) ,
    /**
     * 如果有事物就在事物中执行，没有就不用事物执行
     */
	SUPPORTS( "SUPPORTS" ) ,
	/**
     * 不支持事物，即使有也不管事物
     */
	NOT_SUPPORTED( "NOT_SUPPORTED" ) ,
	/**
     * 不支持事物，如果有事物就抛错
     */
	NEVER( "NEVER" ) ,
	/**
     * 如果当前有事物了，就嵌入执行
     */
	EMBED( "EMBED" ) ,
	 /**
     * 创建一个新事物并暂停以前的
     */
	NEW( "NEW" );

   private String value;

   TransactionPolicy( String value )
   {
      this.value = value;
   }

   public String value()
   {
      return value;
   }
   public static TransactionPolicy fromValue(String v) {
       for (TransactionPolicy c : TransactionPolicy.values()) {
           if (c.value.equals(v)) {
               return c;
           }
       }
       throw new IllegalArgumentException(v);
   }
}
