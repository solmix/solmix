package org.solmix.runtime.proxy.support;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassUtils;
import org.solmix.runtime.helper.ClassGenerator;
import org.solmix.runtime.proxy.AspectProxy;

public class JavasistAspectProxy implements AspectProxy {

	private static final Logger LOG = LoggerFactory.getLogger(JavasistAspectProxy.class);
	
	private ProxyAspectSupport aspector; 
	/** Keeps track of the Classes that we have validated for final methods */
	private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap<Class<?>, Boolean>();


	public JavasistAspectProxy(ProxyAspectSupport config) {
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
		Class<?> rootClass = this.aspector.getTargetClass();
		if(rootClass==null){
			throw new IllegalStateException("Target class must be available for creating a Javassist proxy");
		}
		Class<?> proxySuperClass = rootClass;
		if (ClassUtils.isByteCodeProxyClass(rootClass)) {
			proxySuperClass = rootClass.getSuperclass();
			Class<?>[] additionalInterfaces = rootClass.getInterfaces();
			for (Class<?> additionalInterface : additionalInterfaces) {
				this.aspector.addInterface(additionalInterface);
			}
		}

		validateClassIfNecessary(proxySuperClass);
		// ProxyClassLoader pcl = new ProxyClassLoader(classLoader);
		ClassGenerator cg  = ClassGenerator.newInstance(classLoader);
		cg.setSuperClass(proxySuperClass);
		try {
			String superName=proxySuperClass.getName();
			ClassPool pool = ClassGenerator.getClassPool(classLoader);
			CtClass superctcs = pool.get(superName);
			String classname = ClassGenerator.getClassName(superName, javassist.Modifier.isPublic(proxySuperClass.getModifiers()));
			CtClass proxyClass = pool.makeClass(classname);
			Class<?>[] infs=ProxyUtils.completeProxiedInterfaces(this.aspector);
			for(Class<?> inf:infs){
				proxyClass.addInterface(pool.get(inf.getName()));
			}
			proxyClass.setSuperclass(superctcs);
			CtField[] fileds=superctcs.getFields();
			for(CtField cm:fileds){
				System.out.println(cm.getName());
			}
			CtMethod[] methods =superctcs.getMethods();
			for(CtMethod cm:methods){
				System.out.println(cm.getLongName());
			}
			Class c=proxyClass.toClass();
			return c.newInstance();
//			
//			CtClass[] interfaces=superctcs.getInterfaces();
//			for(CtClass inf:interfaces){
//				System.out.println(inf.getName());
//				CtMethod[] methods =inf.getMethods();
//				for(CtMethod cm:methods){
//					System.out.println(cm.getLongName());
//				}
//			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String classnmae= javassist.Modifier.isPublic(superctcs.getModifiers())
//				? ClassGenerator.class.getName() : mSuperClass + "$sc" ) + id;
//		CtClass proxyClass = pool.makeClass(classname);
//		proxyClass.addInterface(pool.get(DC.class.getName())); 
		return null;
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

}
