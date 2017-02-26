package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;
import java.util.List;

public interface AspectChainFactory {

	List<Object> getInterceptorsAndDynamicInterception(ProxyAspectSupport config, Method method, Class<?> targetClass);

}
