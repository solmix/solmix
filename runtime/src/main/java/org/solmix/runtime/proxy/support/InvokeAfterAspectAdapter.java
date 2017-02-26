package org.solmix.runtime.proxy.support;

import java.io.Serializable;

import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.Aspector;

public class InvokeAfterAspectAdapter implements AspectorAdapter, Serializable {

	private static final long serialVersionUID = -5138303488934058454L;

	@Override
	public MethodInterceptor getInterceptor(Aspector aspector) {
		InvokeAfterAspect aspect = (InvokeAfterAspect) aspector.getAspect();
		return new InvokeAfterAspectInterceptor(aspect);
	}

	@Override
	public boolean supportsAspect(Aspect aspect) {
		return (aspect instanceof InvokeAfterAspect);
	}

}
