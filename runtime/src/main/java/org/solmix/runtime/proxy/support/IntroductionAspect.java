package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Aspect;

public interface IntroductionAspect  extends Aspect{

	Class<?>[] getInterfaces();

}
