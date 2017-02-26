package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.AspectProxy;
import org.solmix.runtime.proxy.AspectProxyFactory;
import org.solmix.runtime.proxy.Aspector;
import org.solmix.runtime.proxy.ProxyInfo;
import org.solmix.runtime.proxy.target.NullTargetSource;
import org.solmix.runtime.proxy.target.SimpleTargetSource;
import org.solmix.runtime.proxy.target.TargetSource;

@SuppressWarnings("serial")
public class ProxyAspectSupport extends ProxyInfo {

	private List<ProxyAspectSupportListener> listeners = new LinkedList<ProxyAspectSupportListener>();
	
	private AspectProxyFactory aspectProxyFactory;
	
	private transient Map<MethodCacheKey, List<Object>> methodCache;

	private boolean created = false;
	
	private List<Class<?>> interfaces = new ArrayList<Class<?>>();

	public static final TargetSource EMPTY_TARGET_SOURCE = NullTargetSource.INSTANCE;

	private boolean preFiltered = false;

	private Aspector[] advisorArray = new Aspector[0];
	
	/** Package-protected to allow direct access for efficiency */
	TargetSource targetSource = EMPTY_TARGET_SOURCE;

	private List<Aspect> advisors = new LinkedList<Aspect>();
	
	private AspectChainFactory aspectChainFactory= new DefaultAspectChainFactory();

	public ProxyAspectSupport() {
		this(new DefaultAspectProxyFactory());
	}

	public ProxyAspectSupport(AspectProxyFactory aspectProxyFactory) {
		Assert.isNotNull(aspectProxyFactory,"AspectProxyFactory must not be null");
		this.aspectProxyFactory = aspectProxyFactory;
		initMethodCache();
	}

	private void initMethodCache() {
		this.methodCache = new ConcurrentHashMap<MethodCacheKey, List<Object>>(32);
	}

	public AspectProxyFactory getAspectProxyFactory() {
		return aspectProxyFactory;
	}

	public void setAspectProxyFactory(AspectProxyFactory aspectProxyFactory) {
		this.aspectProxyFactory = aspectProxyFactory;
	}
	
	protected final synchronized AspectProxy createAspectProxy() {
		if (!this.created) {
			create();
		}
		return getAspectProxyFactory().createAspectProxy(this);
	}
	
	public void addListener(ProxyAspectSupportListener listener) {
		Assert.isNotNull(listener, "ProxyAspectSupportListener must not be null");
		this.listeners.add(listener);
	}

	public void removeListener(ProxyAspectSupportListener listener) {
		Assert.isNotNull(listener, "ProxyAspectSupportListener must not be null");
		this.listeners.remove(listener);
	}


	private void create() {
		this.created = true;
		for (ProxyAspectSupportListener listener : this.listeners) {
			listener.created(this);
		}
	}
	
	protected final synchronized boolean isCreated() {
		return this.created;
	}
	
	
	private static class MethodCacheKey {

		private final Method method;

		private final int hashCode;

		public MethodCacheKey(Method method) {
			this.method = method;
			this.hashCode = method.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			}
			MethodCacheKey otherKey = (MethodCacheKey) other;
			return (this.method == otherKey.method);
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}
	}
	
	public void setInterfaces(Class<?>... interfaces) {
		Assert.isNotNull(interfaces, "Interfaces must not be null");
		this.interfaces.clear();
		for (Class<?> ifc : interfaces) {
			addInterface(ifc);
		}
	}
	
	public void addInterface(Class<?> intf) {
		Assert.isNotNull(intf, "Interface must not be null");
		if (!intf.isInterface()) {
			throw new IllegalArgumentException("[" + intf.getName() + "] is not an interface");
		}
		if (!this.interfaces.contains(intf)) {
			this.interfaces.add(intf);
			adviceChanged();
		}
	}
	
	protected void adviceChanged() {
		this.methodCache.clear();
		synchronized (this) {
			if (this.created) {
				for (ProxyAspectSupportListener listener : this.listeners) {
					listener.changed(this);
				}
			}
		}
	}

	public TargetSource getTargetSource() {
		return this.targetSource;
	}

	public void setTarget(Object target) {
		setTargetSource(new SimpleTargetSource(target));
	}

	public void setTargetSource(TargetSource targetSource) {
		this.targetSource = (targetSource != null ? targetSource : EMPTY_TARGET_SOURCE);
	}

	public Class<?>[] getProxiedInterfaces() {
		return this.interfaces.toArray(new Class<?>[this.interfaces.size()]);
	}

	public Class<?> getTargetClass() {
		return this.targetSource.getTargetClass();
	}

	public Aspector[] getAspects() {
		return this.advisorArray;
	}
	
	protected final void updateAspectArray() {
		this.advisorArray = this.advisors.toArray(new Aspector[this.advisors.size()]);
	}

	public boolean isInterfaceProxied(Class<?> intf) {
		for (Class<?> proxyIntf : this.interfaces) {
			if (intf.isAssignableFrom(proxyIntf)) {
				return true;
			}
		}
		return false;
	}

	public List<Object> getInterceptorsAndDynamicInterception(
			Method method, Class<?> targetClass) {
		MethodCacheKey cacheKey = new MethodCacheKey(method);
		List<Object> cached = this.methodCache.get(cacheKey);
		if (cached == null) {
			cached = this.aspectChainFactory.getInterceptorsAndDynamicInterception(
					this, method, targetClass);
			this.methodCache.put(cacheKey, cached);
		}
		return cached;
	}

	public boolean isPreFiltered() {
		return this.preFiltered;
	}
	
	public void setPreFiltered(boolean preFiltered) {
		this.preFiltered = preFiltered;
	}

}
