package org.solmix.runtime.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.solmix.runtime.transaction.TransactionInfo;
import org.solmix.runtime.transaction.TransactionIsolation;
import org.solmix.runtime.transaction.TransactionPolicy;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transaction {
	String value() default "";
	
	TransactionPolicy policy() default TransactionPolicy.REQUIRED;
	TransactionIsolation isolation() default TransactionIsolation.DEFAULT;
	int timeout() default TransactionInfo.TIMEOUT_DEFAULT;
	boolean readOnly() default false;
	Class<? extends Throwable>[] rollbackFor() default {};
	String[] rollbackForClassName() default {};
	Class<? extends Throwable>[] noRollbackFor() default {};
	String[] noRollbackForClassName() default {};
}
