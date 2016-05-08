package org.solmix.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.hamcrest.Matcher;
import org.junit.Assert;

public class TestUtils {

	private static TestEnvSetup env = new TestEnvSetup().init();
	public static final File basedir = env.getBasedir();
	public static final File srcdir = env.getSrcdir();
	public static final File destdir = env.getDestdir();
	public static final File javaHome = getJavaHome();
	public static File getJavaHome() {
	        File javaHome = new File(System.getProperty("java.home"));

	        if ("jre".equals(javaHome.getName())) {
	            javaHome = javaHome.getParentFile();
	        }

	        return javaHome;
	    }
	   public static <T> T getFieldValue(Object target, String fieldName, Class<T> fieldType) {
	        return getFieldValue(target, null, fieldName, fieldType);
	    }

	public  static <T> T getFieldValue(Object target, Class<?> targetType, String fieldName, Class<T> fieldType) {
		 if (targetType == null && target != null) {
	            targetType = target.getClass();
	        }

	        Field field = getAccessibleField(targetType, fieldName);
	        Object value = null;

	        try {
	            value = field.get(target);
	        } catch (Exception e) {
	            Assert.fail(e.toString());
	            return null;
	        }

	        if (fieldType != null) {
	            return fieldType.cast(value);
	        } else {
	            return (T) value;
	        }
	}
	  /** 取得field，并设置为可访问。 */
    public static Field getAccessibleField(Class<?> targetType, String fieldName) {
    	 Assert. assertNotNull("missing targetType", targetType);

        Field field = null;

        for (Class<?> c = targetType; c != null && field == null; c = c.getSuperclass()) {
            try {
                field = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            } catch (Exception e) {
            	 Assert.fail(e.toString());
                return null;
            }
        }

        Assert.assertNotNull("field " + fieldName + " not found in " + targetType, field);

        field.setAccessible(true);

        return field;
    }
    /** 取得method，并设置为可访问。 */
    public static Method getAccessibleMethod(Class<?> targetType, String methodName, Class<?>[] argTypes) {
    	Assert.assertNotNull("missing targetType", targetType);

        Method method = null;

        for (Class<?> c = targetType; c != null && method == null; c = c.getSuperclass()) {
            try {
                method = c.getDeclaredMethod(methodName, argTypes);
            } catch (NoSuchMethodException e) {
            } catch (Exception e) {
            	Assert.fail(e.toString());
                return null;
            }
        }

        Assert.assertNotNull("method " + methodName + " not found in " + targetType, method);

        method.setAccessible(true);

        return method;
    }
    public static <T extends Throwable> Matcher<T> exception(String... snippets) {
        return new ExceptionMatcher<T>(snippets);
    }
}
