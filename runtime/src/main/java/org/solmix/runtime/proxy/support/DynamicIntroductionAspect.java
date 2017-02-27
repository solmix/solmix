package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Aspect;

public interface DynamicIntroductionAspect extends Aspect {
	boolean implementsInterface(Class<?> intf);
}
