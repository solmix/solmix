package org.solmix.runtime.proxy.support;

import java.util.HashSet;
import java.util.Set;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassUtils;
import org.solmix.runtime.proxy.Aspect;

public class DefaultIntroductionAspector implements IntroductionAspector,ClassFilter {

	private final Aspect aspect;

	private final Set<Class<?>> interfaces = new HashSet<Class<?>>();

	
	public DefaultIntroductionAspector(Aspect advice) {
		this(advice, (advice instanceof IntroductionAspect ? (IntroductionAspect) advice : null));
	}

	/**
	 * Create a DefaultIntroductionAdvisor for the given advice.
	 * @param advice the Advice to apply
	 * @param introductionInfo the IntroductionInfo that describes
	 * the interface to introduce (may be {@code null})
	 */
	public DefaultIntroductionAspector(Aspect advice, IntroductionAspect introductionInfo) {
		Assert.isNotNull(advice, "Advice must not be null");
		this.aspect = advice;
		if (introductionInfo != null) {
			Class<?>[] introducedInterfaces = introductionInfo.getInterfaces();
			if (introducedInterfaces.length == 0) {
				throw new IllegalArgumentException("IntroductionAdviceSupport implements no interfaces");
			}
			for (Class<?> ifc : introducedInterfaces) {
				addInterface(ifc);
			}
		}
	}
	public void addInterface(Class<?> intf) {
		Assert.isNotNull(intf, "Interface must not be null");
		if (!intf.isInterface()) {
			throw new IllegalArgumentException("Specified class [" + intf.getName() + "] must be an interface");
		}
		this.interfaces.add(intf);
	}

	@Override
	public Class<?>[] getInterfaces() {
		return this.interfaces.toArray(new Class<?>[this.interfaces.size()]);
	}

	@Override
	public void validateInterfaces() throws IllegalArgumentException {
		for (Class<?> ifc : this.interfaces) {
			if (this.aspect instanceof DynamicIntroductionAspect &&
					!((DynamicIntroductionAspect) this.aspect).implementsInterface(ifc)) {
			 throw new IllegalArgumentException("DynamicIntroductionAspect [" + this.aspect + "] " +
					 "does not implement interface [" + ifc.getName() + "] specified for introduction");
			}
		}
	}



	@Override
	public boolean isPerInstance() {
		return true;
	}

	@Override
	public ClassFilter getClassFilter() {
		return this;
	}

	@Override
	public boolean matches(Class<?> clazz) {
		return true;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DefaultIntroductionAspector)) {
			return false;
		}
		DefaultIntroductionAspector otherAdvisor = (DefaultIntroductionAspector) other;
		return (this.aspect.equals(otherAdvisor.aspect) && this.interfaces.equals(otherAdvisor.interfaces));
	}

	@Override
	public int hashCode() {
		return this.aspect.hashCode() * 13 + this.interfaces.hashCode();
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + ": advice [" + this.aspect + "]; interfaces " +
				ClassUtils.classNamesToString(this.interfaces);
	}

	@Override
	public Aspect getAspect() {
		return aspect;
	}
}
