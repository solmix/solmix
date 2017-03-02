package org.solmix.runtime.proxy.interceptor;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.proxy.support.DynamicIntroductionAspect;
import org.solmix.runtime.proxy.support.IntroductionAspectSupport;
import org.solmix.runtime.proxy.support.MethodInvocation;
import org.solmix.runtime.proxy.support.ProxyUtils;

public class DelegatingIntroductionInterceptor extends IntroductionAspectSupport implements IntroductionInterceptor{

	private Object delegate;


	public DelegatingIntroductionInterceptor(Object delegate) {
		init(delegate);
	}

	
	protected DelegatingIntroductionInterceptor() {
		init(this);
	}


	private void init(Object delegate) {
		Assert.isNotNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
		implementInterfacesOnObject(delegate);

		// We don't want to expose the control interface
		suppressInterface(IntroductionInterceptor.class);
		suppressInterface(DynamicIntroductionAspect.class);
	}


	/**
	 * Subclasses may need to override this if they want to perform custom
	 * behaviour in around advice. However, subclasses should invoke this
	 * method, which handles introduced interfaces and forwarding to the target.
	 */
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		if (isMethodOnIntroducedInterface(mi)) {
			// Using the following method rather than direct reflection, we
			// get correct handling of InvocationTargetException
			// if the introduced method throws an exception.
			Object retVal = ProxyUtils.invokeJoinpointUsingReflection(this.delegate, mi.getMethod(), mi.getArguments());

			// Massage return value if possible: if the delegate returned itself,
			// we really want to return the proxy.
			if (retVal == this.delegate && mi instanceof ProxyMethodInvacation) {
				Object proxy = ((ProxyMethodInvacation) mi).getProxy();
				if (mi.getMethod().getReturnType().isInstance(proxy)) {
					retVal = proxy;
				}
			}
			return retVal;
		}

		return doProceed(mi);
	}

	/**
	 * Proceed with the supplied {@link org.aopalliance.intercept.MethodInterceptor}.
	 * Subclasses can override this method to intercept method invocations on the
	 * target object which is useful when an introduction needs to monitor the object
	 * that it is introduced into. This method is <strong>never</strong> called for
	 * {@link MethodInvocation MethodInvocations} on the introduced interfaces.
	 */
	protected Object doProceed(MethodInvocation mi) throws Throwable {
		// If we get here, just pass the invocation on.
		return mi.proceed();
	}


}
