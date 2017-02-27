package org.solmix.runtime.proxy.support;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.runtime.proxy.AspectProxy;
import org.solmix.runtime.proxy.Aspected;
import org.solmix.runtime.proxy.ProxyConfigException;
import org.solmix.runtime.proxy.ProxyInvocationException;
import org.solmix.runtime.proxy.target.TargetSource;

public class JdkAspectProxy implements AspectProxy, InvocationHandler,Serializable {

	private static final long serialVersionUID = -5955470265462916452L;
	private static final Logger LOG = LoggerFactory.getLogger(JdkAspectProxy.class);
	private boolean equalsDefined;
	private boolean hashCodeDefined;
	private ProxyAspectSupport config;

	public JdkAspectProxy(ProxyAspectSupport config) {
		Assert.isNotNull(config, "ProxyAspectSupport must not be null");
		if (config.getAspectors().length == 0
				&& config.getTargetSource() == ProxyAspectSupport.EMPTY_TARGET_SOURCE) {
			throw new ProxyConfigException("No advisors and no TargetSource specified");
		}
		this.config = config;
	}

	@Override
	public Object getProxy() {
		return getProxy(ClassLoaderUtils.getDefaultClassLoader());
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating JDK dynamic proxy: target source is "
					+ this.config.getTargetSource());
		}
		Class<?>[] proxiedInterfaces = ProxyUtils
				.completeProxiedInterfaces(this.config);
		findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
		return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
	}

	private void findDefinedEqualsAndHashCodeMethods(
			Class<?>[] proxiedInterfaces) {
		for (Class<?> proxiedInterface : proxiedInterfaces) {
			Method[] methods = proxiedInterface.getDeclaredMethods();
			for (Method method : methods) {
				if (ProxyUtils.isEqualsMethod(method)) {
					this.equalsDefined = true;
				}
				if (ProxyUtils.isHashCodeMethod(method)) {
					this.hashCodeDefined = true;
				}
				if (this.equalsDefined && this.hashCodeDefined) {
					return;
				}
			}
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		MethodInvocation invocation;
		Object oldProxy = null;

		TargetSource targetSource = this.config.targetSource;
		Class<?> targetClass = null;
		Object target = null;

		try {
			if (!this.equalsDefined && ProxyUtils.isEqualsMethod(method)) {
				// The target does not implement the equals(Object) method itself.
				return equals(args[0]);
			}
			if (!this.hashCodeDefined && ProxyUtils.isHashCodeMethod(method)) {
				// The target does not implement the hashCode() method itself.
				return hashCode();
			}
			if (!this.config.isOpaque() && method.getDeclaringClass().isInterface() &&
					method.getDeclaringClass().isAssignableFrom(Aspected.class)) {
				// Service invocations on ProxyConfig with the proxy config...
				return ProxyUtils.invokeJoinpointUsingReflection(this.config, method, args);
			}

			Object retVal;

//			if (this.config.exposeProxy) {
//				oldProxy = AopContext.setCurrentProxy(proxy);
//			}

			// May be null. Get as late as possible to minimize the time we "own" the target,
			// in case it comes from a pool.
			target = targetSource.getTarget();
			if (target != null) {
				targetClass = target.getClass();
			}

			// Get the interception chain for this method.
			List<Object> chain = this.config.getInterceptorsAndDynamicInterception(method, targetClass);

			// Check whether we have any advice. If we don't, we can fallback on direct
			// reflective invocation of the target, and avoid creating a MethodInvocation.
			if (chain.isEmpty()) {
				// We can skip creating a MethodInvocation: just invoke the target directly
				// Note that the final invoker must be an InvokerInterceptor so we know it does
				// nothing but a reflective operation on the target, and no hot swapping or fancy proxying.
				retVal = ProxyUtils.invokeJoinpointUsingReflection(target, method, args);
			}
			else {
				// We need to create a method invocation...
				invocation = new ReflectionMethodInvocation(proxy, target, method, args, targetClass, chain);
				// Proceed to the joinpoint through the interceptor chain.
				retVal = invocation.proceed();
			}

			// Massage return value if necessary.
			Class<?> returnType = method.getReturnType();
			if (retVal != null && retVal == target && returnType.isInstance(proxy) ) {
				
				retVal = proxy;
			} else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
				throw new ProxyInvocationException("Null return value from advice does not match primitive return type for: " + method);
			}
			return retVal;
		}
		finally {
			if (target != null && !targetSource.isStatic()) {
				// Must have come from TargetSource.
				targetSource.releaseTarget(target);
			}
//			if (setProxyContext) {
//				// Restore old proxy.
//				AopContext.setCurrentProxy(oldProxy);
//			}
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null) {
			return false;
		}

		JdkAspectProxy otherProxy;
		if (other instanceof JdkAspectProxy) {
			otherProxy = (JdkAspectProxy) other;
		} else if (Proxy.isProxyClass(other.getClass())) {
			InvocationHandler ih = Proxy.getInvocationHandler(other);
			if (!(ih instanceof JdkAspectProxy)) {
				return false;
			}
			otherProxy = (JdkAspectProxy) ih;
		} else {
			return false;
		}

		return ProxyUtils.equalsInProxy(this.config, otherProxy.config);
	}

	
	@Override
	public int hashCode() {
		return JdkAspectProxy.class.hashCode() * 13
				+ this.config.getTargetSource().hashCode();
	}

}
