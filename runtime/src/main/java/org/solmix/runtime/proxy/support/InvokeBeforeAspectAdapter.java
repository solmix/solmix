package org.solmix.runtime.proxy.support;

import java.io.Serializable;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.Aspector;

public class InvokeBeforeAspectAdapter implements AspectorAdapter, Serializable {

	private static final long serialVersionUID = 3009552539952406038L;

	@Override
	public MethodInterceptor getInterceptor(Aspector advisor) {
		InvokeBeforeAspect advice = (InvokeBeforeAspect) advisor.getAspect();
		return new InvokeBeforeAspectInterceptor(advice);
	}

	@Override
	public boolean supportsAspect(Aspect advice) {
		return (advice instanceof InvokeBeforeAspect);
	}

}
