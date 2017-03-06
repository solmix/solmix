package org.solmix.runtime.proxy.support;

import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassUtils;
import org.solmix.runtime.proxy.ProxyFactory;
import org.solmix.runtime.proxy.ProxyInfo;
import org.solmix.runtime.proxy.target.SimpleTargetSource;
import org.solmix.runtime.proxy.target.TargetSource;

@SuppressWarnings("serial")
public abstract class AbstractSimpleProxyFactory extends ProxyInfo {

	private Object target;

	private Class<?>[] proxyInterfaces;

	private Object[] preInterceptors;

	private Object[] postInterceptors;

	private AspectorAdapterRegistry advisorAdapterRegistry = AspectorAdapterRegistry.getInstance();

	private transient ClassLoader proxyClassLoader;

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Class<?>[] getProxyInterfaces() {
		return proxyInterfaces;
	}

	public void setProxyInterfaces(Class<?>[] proxyInterfaces) {
		this.proxyInterfaces = proxyInterfaces;
	}

	public Object[] getPreInterceptors() {
		return preInterceptors;
	}

	public void setPreInterceptors(Object[] preInterceptors) {
		this.preInterceptors = preInterceptors;
	}

	public Object[] getPostInterceptors() {
		return postInterceptors;
	}

	public void setPostInterceptors(Object[] postInterceptors) {
		this.postInterceptors = postInterceptors;
	}

	public AspectorAdapterRegistry getAdvisorAdapterRegistry() {
		return advisorAdapterRegistry;
	}

	public void setAdvisorAdapterRegistry(
			AspectorAdapterRegistry advisorAdapterRegistry) {
		this.advisorAdapterRegistry = advisorAdapterRegistry;
	}

	public ClassLoader getProxyClassLoader() {
		return proxyClassLoader;
	}

	public void setProxyClassLoader(ClassLoader proxyClassLoader) {
		this.proxyClassLoader = proxyClassLoader;
	}
	
	
	public Object getProxy(){
		if (this.target == null) {
			throw new IllegalArgumentException("Property 'target' is required");
		}
		if (this.target instanceof String) {
			throw new IllegalArgumentException("'target' needs to be a bean reference, not a bean name as value");
		}
		if (this.proxyClassLoader == null) {
			this.proxyClassLoader = ClassLoaderUtils.getDefaultClassLoader();
		}
		
		ProxyFactory proxyFactory = new ProxyFactory();

		if (this.preInterceptors != null) {
			for (Object interceptor : this.preInterceptors) {
				proxyFactory.addAspector(this.advisorAdapterRegistry.wrap(interceptor));
			}
		}

		// Add the main interceptor (typically an Advisor).
		proxyFactory.addAspector(this.advisorAdapterRegistry.wrap(createMainInterceptor()));

		if (this.postInterceptors != null) {
			for (Object interceptor : this.postInterceptors) {
				proxyFactory.addAspector(this.advisorAdapterRegistry.wrap(interceptor));
			}
		}

		proxyFactory.copyFrom(this);

		TargetSource targetSource = createTargetSource(this.target);
		proxyFactory.setTargetSource(targetSource);

		if (this.proxyInterfaces != null) {
			proxyFactory.setInterfaces(this.proxyInterfaces);
		}
		else if (!isProxyTargetClass()) {
			// Rely on AOP infrastructure to tell us what interfaces to proxy.
			proxyFactory.setInterfaces(
					ClassUtils.getAllInterfacesForClass(targetSource.getTargetClass(), this.proxyClassLoader));
		}

		return proxyFactory.getProxy(this.proxyClassLoader);
		
	}
	
	protected TargetSource createTargetSource(Object target) {
		if (target instanceof TargetSource) {
			return (TargetSource) target;
		}
		else {
			return new SimpleTargetSource(target);
		}
	}
	
	protected abstract Object createMainInterceptor();
}
