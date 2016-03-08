
package org.solmix.runtime.management.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedResource {

    String componentName() default "";

    String description() default "";

    int currencyTimeLimit() default -1;

    boolean log() default false;

    String logFile() default "";

    String persistPolicy() default "Never";

    int persistPeriod() default -1;

    String persistName() default "";

    String persistLocation() default "";        

}
