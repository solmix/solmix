package org.solmix.runtime.proxy.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.util.ClassUtils;

public class IntroductionAspectSupport implements IntroductionAspect {
	protected final Set<Class<?>> publishedInterfaces = new HashSet<Class<?>>();

	private transient Map<Method, Boolean> rememberedMethods = new ConcurrentHashMap<Method, Boolean>(32);


	/**
	 * Suppress the specified interface, which may have been autodetected
	 * due to the delegate implementing it. Call this method to exclude
	 * internal interfaces from being visible at the proxy level.
	 * <p>Does nothing if the interface is not implemented by the delegate.
	 * @param intf the interface to suppress
	 */
	public void suppressInterface(Class<?> intf) {
		this.publishedInterfaces.remove(intf);
	}

	@Override
	public Class<?>[] getInterfaces() {
		return this.publishedInterfaces.toArray(new Class<?>[this.publishedInterfaces.size()]);
	}

	/**
	 * Check whether the specified interfaces is a published introduction interface.
	 * @param ifc the interface to check
	 * @return whether the interface is part of this introduction
	 */
	public boolean implementsInterface(Class<?> ifc) {
		for (Class<?> pubIfc : this.publishedInterfaces) {
			if (ifc.isInterface() && ifc.isAssignableFrom(pubIfc)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Publish all interfaces that the given delegate implements at the proxy level.
	 * @param delegate the delegate object
	 */
	protected void implementInterfacesOnObject(Object delegate) {
		this.publishedInterfaces.addAll(ClassUtils.getAllInterfacesAsSet(delegate));
	}

	/**
	 * Is this method on an introduced interface?
	 * @param mi the method invocation
	 * @return whether the invoked method is on an introduced interface
	 */
	protected final boolean isMethodOnIntroducedInterface(MethodInvocation mi) {
		Boolean rememberedResult = this.rememberedMethods.get(mi.getMethod());
		if (rememberedResult != null) {
			return rememberedResult;
		}
		else {
			// Work it out and cache it.
			boolean result = implementsInterface(mi.getMethod().getDeclaringClass());
			this.rememberedMethods.put(mi.getMethod(), result);
			return result;
		}
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	/**
	 * This method is implemented only to restore the logger.
	 * We don't make the logger static as that would mean that subclasses
	 * would use this class's log category.
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		// Rely on default serialization; just initialize state after deserialization.
		ois.defaultReadObject();
		// Initialize transient fields.
		this.rememberedMethods = new ConcurrentHashMap<Method, Boolean>(32);
	}
}
