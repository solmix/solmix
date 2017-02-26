package org.solmix.runtime.proxy.support;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.Aspector;

public class ThrowsAspectAdapter implements AspectorAdapter {

	@Override
	public MethodInterceptor getInterceptor(Aspector aspector) {
		return new ThrowsAspectInterceptor(aspector.getAspect());
	}

	@Override
	public boolean supportsAspect(Aspect aspect) {
		return (aspect instanceof ThrowsAspect) ;
	}

}
