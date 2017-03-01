package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassUtils;
import org.solmix.runtime.proxy.Aspect;
import org.solmix.runtime.proxy.AspectProxy;
import org.solmix.runtime.proxy.AspectProxyFactory;
import org.solmix.runtime.proxy.Aspected;
import org.solmix.runtime.proxy.Aspector;
import org.solmix.runtime.proxy.ProxyConfigException;
import org.solmix.runtime.proxy.ProxyInfo;
import org.solmix.runtime.proxy.target.NullTargetSource;
import org.solmix.runtime.proxy.target.SimpleTargetSource;
import org.solmix.runtime.proxy.target.TargetSource;

@SuppressWarnings("serial")
public class ProxyAspectSupport extends ProxyInfo  implements Aspected{

	private List<ProxyAspectSupportListener> listeners = new LinkedList<ProxyAspectSupportListener>();
	
	private AspectProxyFactory aspectProxyFactory;
	
	private transient Map<MethodCacheKey, List<Object>> methodCache;

	private boolean created = false;
	
	private List<Class<?>> interfaces = new ArrayList<Class<?>>();

	public static final TargetSource EMPTY_TARGET_SOURCE = NullTargetSource.INSTANCE;

	private boolean preFiltered = false;

	private Aspector[] aspectorArray = new Aspector[0];
	
	/** Package-protected to allow direct access for efficiency */
	TargetSource targetSource = EMPTY_TARGET_SOURCE;

	private List<Aspector> aspectors = new LinkedList<Aspector>();
	
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

	@Override
	public TargetSource getTargetSource() {
		return this.targetSource;
	}

	public void setTarget(Object target) {
		setTargetSource(new SimpleTargetSource(target));
	}
	
	public void setTargetClass(Class<?> targetClass) {
		this.targetSource =NullTargetSource.forClass(targetClass);
	}
	@Override
	public void setTargetSource(TargetSource targetSource) {
		this.targetSource = (targetSource != null ? targetSource : EMPTY_TARGET_SOURCE);
	}

	@Override
	public Class<?>[] getProxiedInterfaces() {
		return this.interfaces.toArray(new Class<?>[this.interfaces.size()]);
	}

	@Override
	public Class<?> getTargetClass() {
		return this.targetSource.getTargetClass();
	}

	
	protected final void updateAspectArray() {
		this.aspectorArray = this.aspectors.toArray(new Aspector[this.aspectors.size()]);
	}

	@Override
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

	@Override
	public boolean isPreFiltered() {
		return this.preFiltered;
	}
	
	@Override
	public void setPreFiltered(boolean preFiltered) {
		this.preFiltered = preFiltered;
	}

	@Override
	public Aspector[] getAspectors() {
		return this.aspectorArray;
	}

	@Override
	public void addAspector(Aspector aspector) throws ProxyConfigException {
		int pos = this.aspectors.size();
		addAspector(pos, aspector);
	}

	@Override
	public void addAspector(int pos, Aspector aspector)
			throws ProxyConfigException {
		if (aspector instanceof IntroductionAspector) {
			validateIntroductionAspector((IntroductionAspector) aspector);
		}
		addAspectorInternal(pos, aspector);
		
	}
	
	private void validateIntroductionAspector(IntroductionAspector advisor) {
		advisor.validateInterfaces();
		Class<?>[] ifcs = advisor.getInterfaces();
		for (Class<?> ifc : ifcs) {
			addInterface(ifc);
		}
	}

	
	private void addAspectorInternal(int pos, Aspector aspector) throws ProxyConfigException {
		Assert.isNotNull(aspector, "Aspector must not be null");
		if (isFrozen()) {
			throw new ProxyConfigException("Cannot add aspector: Configuration is frozen.");
		}
		if (pos > this.aspectors.size()) {
			throw new IllegalArgumentException(
					"Illegal position " + pos + " in advisor list with size " + this.aspectors.size());
		}
		this.aspectors.add(pos, aspector);
		updateAspectArray();
		adviceChanged();
	}

	@Override
	public boolean removeAspector(Aspector aspector) {
		int index = indexOf(aspector);
		if (index == -1) {
			return false;
		}
		else {
			removeAspector(index);
			return true;
		}
	}

	@Override
	public void removeAspector(int index) throws ProxyConfigException {
		if (isFrozen()) {
			throw new ProxyConfigException("Cannot remove Aspector: Configuration is frozen.");
		}
		if (index < 0 || index > this.aspectors.size() - 1) {
			throw new ProxyConfigException("Aspector index " + index + " is out of bounds: " +
					"This configuration only has " + this.aspectors.size() + " advisors.");
		}

		Aspector advisor = this.aspectors.get(index);
		if (advisor instanceof IntroductionAspector) {
			IntroductionAspector ia = (IntroductionAspector) advisor;
			// We need to remove introduction interfaces.
			for (int j = 0; j < ia.getInterfaces().length; j++) {
				removeInterface(ia.getInterfaces()[j]);
			}
		}

		this.aspectors.remove(index);
		updateAspectorArray();
		adviceChanged();
	}
	
	protected final void updateAspectorArray() {
		this.aspectorArray = this.aspectors.toArray(new Aspector[this.aspectors.size()]);
	}
	
	public boolean removeInterface(Class<?> intf) {
		return this.interfaces.remove(intf);
	}

	@Override
	public int indexOf(Aspector aspector) {
		Assert.isNotNull(aspector, "Aspector must not be null");
		return this.aspectors.indexOf(aspector);
	}

	@Override
	public boolean replaceAspector(Aspector a, Aspector b)
			throws ProxyConfigException {
		Assert.isNotNull(a, "Aspector a must not be null");
		Assert.isNotNull(b, "Aspector b must not be null");
		int index = indexOf(a);
		if (index == -1) {
			return false;
		}
		removeAspector(index);
		addAspector(index, b);
		return true;
	}

	@Override
	public void addAspect(Aspect aspect) throws ProxyConfigException {
		int pos = this.aspectors.size();
		addAspect(pos, aspect);
	}

	@Override
	public void addAspect(int pos, Aspect aspect) throws ProxyConfigException {
		Assert.isNotNull(aspect, "Advice must not be null");
		if (aspect instanceof IntroductionAspect) {
			// We don't need an IntroductionAdvisor for this kind of introduction:
			// It's fully self-describing.
			addAspector(pos, new DefaultIntroductionAspector(aspect, (IntroductionAspect) aspect));
		}
		else {
			addAspector(pos, new DefaultCutpointAspector(aspect));
		}
		
	}

	@Override
	public boolean removeAspect(Aspect aspect) {
		int index = indexOf(aspect);
		if (index == -1) {
			return false;
		}
		else {
			removeAspector(index);
			return true;
		}
	}

	@Override
	public int indexOf(Aspect aspect) {
		Assert.isNotNull(aspect, "aspect must not be null");
		for (int i = 0; i < this.aspectors.size(); i++) {
			Aspector aspector = this.aspectors.get(i);
			if (aspector.getAspect() == aspect) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public String toProxyConfigString() {
		return toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		sb.append(": ").append(this.interfaces.size()).append(" interfaces ");
		sb.append(ClassUtils.classNamesToString(this.interfaces)).append("; ");
		sb.append(this.aspectors.size()).append(" aspectors ");
		sb.append(this.aspectors).append("; ");
		sb.append("targetSource [").append(this.targetSource).append("]; ");
		sb.append(super.toString());
		return sb.toString();
	}

	public ProxyAspectSupport getConfigurationOnlyCopy() {
		ProxyAspectSupport copy = new ProxyAspectSupport();
		copy.copyFrom(this);
		copy.targetSource = NullTargetSource.forClass(getTargetClass(), getTargetSource().isStatic());
		copy.aspectChainFactory = this.aspectChainFactory;
		copy.interfaces = this.interfaces;
		copy.aspectors = this.aspectors;
		copy.updateAspectorArray();
		return copy;
	}


}
