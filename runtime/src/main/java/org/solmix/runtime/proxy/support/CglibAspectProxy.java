package org.solmix.runtime.proxy.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.UndeclaredThrowableException;
import net.sf.cglib.transform.impl.UndeclaredThrowableStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassUtils;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.AspectProxy;
import org.solmix.runtime.proxy.Aspector;
import org.solmix.runtime.proxy.ProxyConfigException;
import org.solmix.runtime.proxy.ProxyInvocationException;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class CglibAspectProxy implements AspectProxy {

	private static final Logger LOG = LoggerFactory.getLogger(CglibAspectProxy.class);
	private static final int AOP_PROXY = 0;
	private static final int INVOKE_TARGET = 1;
	private static final int NO_OVERRIDE = 2;
	private static final int DISPATCH_TARGET = 3;
	private static final int DISPATCH_ADVISED = 4;
	private static final int INVOKE_EQUALS = 5;
	private static final int INVOKE_HASHCODE = 6;
	private ProxyAspectSupport aspector; 
	/** Keeps track of the Classes that we have validated for final methods */
	private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap<Class<?>, Boolean>();

	private transient Map<String, Integer> fixedInterceptorMap;

	private transient int fixedInterceptorOffset;


	public CglibAspectProxy(ProxyAspectSupport config) {
		Assert.isNotNull(config,"ProxyAspectSupport must not be null");
		this.aspector=config;
	}

	@Override
	public Object getProxy() {
		
		return getProxy(null);
	}

	@Override
	public Object getProxy(ClassLoader classLoader) {
		if(LOG.isDebugEnabled()){
			LOG.debug("Creating Javassist proxy: target source is " + this.aspector.getTargetSource());
		}
		try {
			Class<?> rootClass = this.aspector.getTargetClass();
			if(rootClass==null){
				throw new IllegalStateException("Target class must be available for creating a Javassist proxy");
			}
			Class<?> proxySuperClass = rootClass;
			if (ClassUtils.isCglibProxyClass(rootClass)) {
				proxySuperClass = rootClass.getSuperclass();
				Class<?>[] additionalInterfaces = rootClass.getInterfaces();
				for (Class<?> additionalInterface : additionalInterfaces) {
					this.aspector.addInterface(additionalInterface);
				}
			}

			validateClassIfNecessary(proxySuperClass);
			Enhancer enhancer = createEnhancer();
			if (classLoader != null) {
				enhancer.setClassLoader(classLoader);
			}
			enhancer.setSuperclass(proxySuperClass);
			enhancer.setInterfaces(ProxyUtils.completeProxiedInterfaces(this.aspector));
			enhancer.setStrategy(new UndeclaredThrowableStrategy(UndeclaredThrowableException.class));
			Callback[] callbacks = adaptorCallbacks(rootClass);
			Class<?>[] types = new Class<?>[callbacks.length];
			for (int x = 0; x < types.length; x++) {
				types[x] = callbacks[x].getClass();
			}
			enhancer.setCallbackFilter(new ProxyCallbackFilter(
					this.aspector.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
			enhancer.setCallbackTypes(types);

			// Generate the proxy class and create a proxy instance.
			return createProxyClassAndInstance(enhancer, callbacks);
		} catch (CodeGenerationException ex) {
			throw new ProxyConfigException("Could not generate CGLIB subclass of class [" +
					this.aspector.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (IllegalArgumentException ex) {
			throw new ProxyConfigException("Could not generate CGLIB subclass of class [" +
					this.aspector.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}catch (Exception e) {
			throw new ProxyConfigException("Unexpected Proxy exception", e);
		}
	}
	
	private Object createProxyClassAndInstance(Enhancer enhancer,
			Callback[] callbacks) {
		// TODO Auto-generated method stub
		return null;
	}

	private Callback[] adaptorCallbacks(Class<?> rootClass) {
		Callback aopInterceptor = new DynamicAspectedInterceptor(this.aspector);
		boolean exposeProxy = this.aspector.isExposeProxy();
		boolean isFrozen = this.aspector.isFrozen();
		boolean isStatic = this.aspector.getTargetSource().isStatic();
		Callback targetInterceptor;
		if (exposeProxy) {
			targetInterceptor = isStatic ?
					new StaticUnaspectedExposedInterceptor(this.aspector.getTargetSource().getTarget()) :
					new DynamicUnaspectedExposedInterceptor(this.aspector.getTargetSource());
		}
		else {
			targetInterceptor = isStatic ?
					new StaticUnaspectedInterceptor(this.aspector.getTargetSource().getTarget()) :
					new DynamicUnaspectedInterceptor(this.aspector.getTargetSource());
		}

		return null;
	}

	protected Enhancer createEnhancer() {
		return new Enhancer();
	}


	private void validateClassIfNecessary(Class<?> proxySuperClass) {
		if (LOG.isWarnEnabled()) {
			synchronized (validatedClasses) {
				if (!validatedClasses.containsKey(proxySuperClass)) {
					doValidateClass(proxySuperClass);
					validatedClasses.put(proxySuperClass, Boolean.TRUE);
				}
			}
		}
	}
	
	private void doValidateClass(Class<?> proxySuperClass) {
		if (LOG.isWarnEnabled()) {
			Method[] methods = proxySuperClass.getMethods();
			for (Method method : methods) {
				if (!Object.class.equals(method.getDeclaringClass()) && !javassist.Modifier.isStatic(method.getModifiers()) &&
						javassist.Modifier.isFinal(method.getModifiers())) {
					LOG.warn("Unable to proxy method [" + method + "] because it is final: " +
							"All calls to this method via a proxy will NOT be routed to the target instance.");
				}
			}
		}
	}
	
	private static Object processReturnType(Object proxy, Object target, Method method, Object retVal) {
		// Massage return value if necessary
		if (retVal != null && retVal == target) {
			// Special case: it returned "this". Note that we can't help
			// if the target sets a reference to itself in another returned object.
			retVal = proxy;
		}
		Class<?> returnType = method.getReturnType();
		if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
			throw new ProxyInvocationException(
					"Null return value from advice does not match primitive return type for: " + method);
		}
		return retVal;
	}
	
	private static class ProxyCallbackFilter implements CallbackFilter {

		private final ProxyAspectSupport support;

		private final Map<String, Integer> fixedInterceptorMap;

		private final int fixedInterceptorOffset;

		public ProxyCallbackFilter(ProxyAspectSupport support, Map<String, Integer> fixedInterceptorMap, int fixedInterceptorOffset) {
			this.support = support;
			this.fixedInterceptorMap = fixedInterceptorMap;
			this.fixedInterceptorOffset = fixedInterceptorOffset;
		}

		@Override
		public int accept(Method method) {
			if (ClassUtils.isFinalizeMethod(method)) {
				LOG.debug("Found finalize() method - using NO_OVERRIDE");
				return NO_OVERRIDE;
			}
			if (!this.support.isOpaque() && method.getDeclaringClass().isInterface() &&
					method.getDeclaringClass().isAssignableFrom(Advised.class)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Method is declared on Advised interface: " + method);
				}
				return DISPATCH_ADVISED;
			}
			// We must always proxy equals, to direct calls to this.
			if (AopUtils.isEqualsMethod(method)) {
				LOG.debug("Found 'equals' method: " + method);
				return INVOKE_EQUALS;
			}
			// We must always calculate hashCode based on the proxy.
			if (AopUtils.isHashCodeMethod(method)) {
				LOG.debug("Found 'hashCode' method: " + method);
				return INVOKE_HASHCODE;
			}
			Class<?> targetClass = this.support.getTargetClass();
			// Proxy is not yet available, but that shouldn't matter.
			List<?> chain = this.support.getInterceptorsAndDynamicInterception(method, targetClass);
			boolean haveAdvice = !chain.isEmpty();
			boolean exposeProxy = this.support.isExposeProxy();
			boolean isStatic = this.support.getTargetSource().isStatic();
			boolean isFrozen = this.support.isFrozen();
			if (haveAdvice || !isFrozen) {
				// If exposing the proxy, then AOP_PROXY must be used.
				if (exposeProxy) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Must expose proxy on advised method: " + method);
					}
					return AOP_PROXY;
				}
				String key = method.toString();
				// Check to see if we have fixed interceptor to serve this method.
				// Else use the AOP_PROXY.
				if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(key)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Method has advice and optimisations are enabled: " + method);
					}
					// We know that we are optimising so we can use the
					// FixedStaticChainInterceptors.
					int index = this.fixedInterceptorMap.get(key);
					return (index + this.fixedInterceptorOffset);
				}
				else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Unable to apply any optimisations to advised method: " + method);
					}
					return AOP_PROXY;
				}
			}
			else {
				// See if the return type of the method is outside the class hierarchy
				// of the target type. If so we know it never needs to have return type
				// massage and can use a dispatcher.
				// If the proxy is being exposed, then must use the interceptor the
				// correct one is already configured. If the target is not static, then
				// cannot use a dispatcher because the target cannot be released.
				if (exposeProxy || !isStatic) {
					return INVOKE_TARGET;
				}
				Class<?> returnType = method.getReturnType();
				if (targetClass == returnType) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Method " + method +
								"has return type same as target type (may return this) - using INVOKE_TARGET");
					}
					return INVOKE_TARGET;
				}
				else if (returnType.isPrimitive() || !returnType.isAssignableFrom(targetClass)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Method " + method +
								" has return type that ensures this cannot be returned- using DISPATCH_TARGET");
					}
					return DISPATCH_TARGET;
				}
				else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Method " + method +
								"has return type that is assignable from the target type (may return this) - " +
								"using INVOKE_TARGET");
					}
					return INVOKE_TARGET;
				}
			}
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof ProxyCallbackFilter)) {
				return false;
			}
			ProxyCallbackFilter otherCallbackFilter = (ProxyCallbackFilter) other;
			ProxyAspectSupport otherAdvised = otherCallbackFilter.support;
			if (this.support == null || otherAdvised == null) {
				return false;
			}
			if (this.support.isFrozen() != otherAdvised.isFrozen()) {
				return false;
			}
			if (this.support.isExposeProxy() != otherAdvised.isExposeProxy()) {
				return false;
			}
			if (this.support.getTargetSource().isStatic() != otherAdvised.getTargetSource().isStatic()) {
				return false;
			}
			if (!ProxyUtils.equalsProxiedInterfaces(this.support, otherAdvised)) {
				return false;
			}
			// Advice instance identity is unimportant to the proxy class:
			// All that matters is type and ordering.
			Aspector[] thisAspectors = this.support.getAspectors();
			Aspector[] thatAspectors = otherAdvised.getAspectors();
			if (thisAspectors.length != thatAspectors.length) {
				return false;
			}
			for (int i = 0; i < thisAspectors.length; i++) {
				Aspector thisAspector = thisAspectors[i];
				Aspector thatAspector = thatAspectors[i];
				if (!equalsAdviceClasses(thisAspector, thatAspector)) {
					return false;
				}
				if (!equalsPointcuts(thisAspector, thatAspector)) {
					return false;
				}
			}
			return true;
		}

		private boolean equalsAdviceClasses(Aspector a, Aspector b) {
			Aspect aa = a.getAspect();
			Aspect ba = b.getAspect();
			if (aa == null || ba == null) {
				return (aa == ba);
			}
			return aa.getClass().equals(ba.getClass());
		}

		private boolean equalsPointcuts(Aspector a, Aspector b) {
			// If only one of the advisor (but not both) is PointcutAspector, then it is a mismatch.
			// Takes care of the situations where an IntroductionAspector is used (see SPR-3959).
			return (!(a instanceof CutpointAspector) ||
					(b instanceof CutpointAspector &&
							ObjectUtils.nullSafeEquals(((CutpointAspector) a).getCutpoint(), ((CutpointAspector) b).getCutpoint())));
		}

		@Override
		public int hashCode() {
			int hashCode = 0;
			Aspector[] advisors = this.support.getAspectors();
			for (Aspector advisor : advisors) {
				Aspect advice = advisor.getAspect();
				if (advice != null) {
					hashCode = 13 * hashCode + advice.getClass().hashCode();
				}
			}
			hashCode = 13 * hashCode + (this.support.isFrozen() ? 1 : 0);
			hashCode = 13 * hashCode + (this.support.isExposeProxy() ? 1 : 0);
			hashCode = 13 * hashCode + (this.support.isOptimize() ? 1 : 0);
			hashCode = 13 * hashCode + (this.support.isOpaque() ? 1 : 0);
			return hashCode;
		}
	}
	
	private static class DynamicAspectedInterceptor implements MethodInterceptor,Serializable{

		private final ProxyAspectSupport support;
		public DynamicAspectedInterceptor(ProxyAspectSupport support) {
			this.support = support;
		}
		@Override
		public Object intercept(Object obj, Method method, Object[] args,MethodProxy proxy) throws Throwable {
			Object oldProxy = null;
			boolean setProxyContext = false;
			Class<?> targetClass = null;
			Object target = null;
			try {
				if (this.support.isExposeProxy()) {
					// Make invocation available if necessary.
					oldProxy = ProxyContext.setCurrentProxy(proxy);
					setProxyContext = true;
				}
				// May be null. Get as late as possible to minimize the time we
				// "own" the target, in case it comes from a pool...
				target = getTarget();
				if (target != null) {
					targetClass = target.getClass();
				}
				List<Object> chain = this.support.getInterceptorsAndDynamicInterception(method, targetClass);
				Object retVal;
				// Check whether we only have one InvokerInterceptor: that is,
				// no real advice, but just reflective invocation of the target.
				if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
					// We can skip creating a MethodInvocation: just invoke the target directly.
					// Note that the final invoker must be an InvokerInterceptor, so we know
					// it does nothing but a reflective operation on the target, and no hot
					// swapping or fancy proxying.
					retVal = proxy.invoke(target, args);
				}
				else {
					// We need to create a method invocation...
					retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, proxy).proceed();
				}
				retVal = processReturnType(proxy, target, method, retVal);
				return retVal;
			}
			finally {
				if (target != null) {
					releaseTarget(target);
				}
				if (setProxyContext) {
					// Restore old proxy.
					ProxyContext.setCurrentProxy(oldProxy);
				}
			}
		}
		@Override
		public boolean equals(Object other) {
			return (this == other ||
					(other instanceof DynamicAspectedInterceptor &&
							this.support.equals(((DynamicAspectedInterceptor) other).support)));
		}

		/**
		 * CGLIB uses this to drive proxy creation.
		 */
		@Override
		public int hashCode() {
			return this.support.hashCode();
		}

		protected Object getTarget() throws Exception {
			return this.support.getTargetSource().getTarget();
		}

		protected void releaseTarget(Object target) throws Exception {
			this.support.getTargetSource().releaseTarget(target);
		}
		
	}
	
	private static class CglibMethodInvocation extends ReflectionMethodInvocation{
		private final MethodProxy methodProxy;

		private final boolean publicMethod;

		public CglibMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
				Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {
			super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
			this.methodProxy = methodProxy;
			this.publicMethod = Modifier.isPublic(method.getModifiers());
		}

		/**
		 * Gives a marginal performance improvement versus using reflection to
		 * invoke the target when invoking public methods.
		 */
		@Override
		protected Object invokeJoinpoint() throws Throwable {
			if (this.publicMethod) {
				return this.methodProxy.invoke(this.target, this.arguments);
			}
			else {
				return super.invokeJoinpoint();
			}
		}
	}

}
