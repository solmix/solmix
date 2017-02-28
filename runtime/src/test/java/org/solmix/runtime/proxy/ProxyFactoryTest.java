package org.solmix.runtime.proxy;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.proxy.interceptor.DebugInterceptor;
import org.solmix.runtime.proxy.support.DefaultCutpointAspector;
import org.solmix.runtime.proxy.support.DefaultIntroductionAspector;
import org.solmix.runtime.proxy.support.DelegatingIntroductionInterceptor;
import org.solmix.runtime.proxy.support.ProxyUtils;
import org.solmix.test.bean.ComplexBean;
import org.solmix.test.bean.IComplexBean;
import org.solmix.tests.proxy.CountingBeforeAspect;
import org.solmix.tests.proxy.NopInterceptor;

public class ProxyFactoryTest {

	@Test
	public void testReplaceAspector() {
		ComplexBean target = new ComplexBean();
		ProxyFactory pf = new ProxyFactory(target);
		NopInterceptor nop = new NopInterceptor();
		CountingBeforeAspect cba1 = new CountingBeforeAspect();
		CountingBeforeAspect cba2 = new CountingBeforeAspect();
		Aspector advisor1 = new DefaultCutpointAspector(cba1);
		Aspector advisor2 = new DefaultCutpointAspector(cba2);
		pf.addAspector(advisor1);
		pf.addAspect(nop);
		IComplexBean proxied = (IComplexBean) pf.getProxy();
		// Use the type cast feature
		// Replace etc methods on advised should be same as on ProxyFactory
		Aspected advised = (Aspected) proxied;
		proxied.setAge(5);
		assertEquals(1, cba1.getCalls());
		assertEquals(0, cba2.getCalls());
		assertEquals(1, nop.getCount());
		assertFalse(advised.replaceAspector(new DefaultCutpointAspector(new NopInterceptor()), advisor2));
		assertTrue(advised.replaceAspector(advisor1, advisor2));
		assertEquals(advisor2, pf.getAspectors()[0]);
		assertEquals(5, proxied.getAge());
		assertEquals(1, cba1.getCalls());
		assertEquals(2, nop.getCount());
		assertEquals(1, cba2.getCalls());
		assertFalse(pf.replaceAspector(new DefaultCutpointAspector(null), advisor1));
	}
	@Test
	public void testRemoveAspectorByIndex() {
		ComplexBean target = new ComplexBean();
		ProxyFactory pf = new ProxyFactory(target);
		NopInterceptor nop = new NopInterceptor();
		CountingBeforeAspect cba = new CountingBeforeAspect();
		Aspector advisor = new DefaultCutpointAspector(cba);
		pf.addAspect(nop);
		pf.addAspector(advisor);
		NopInterceptor nop2 = new NopInterceptor();
		pf.addAspect(nop2);
		IComplexBean proxied = (IComplexBean) pf.getProxy();
		proxied.setAge(5);
		assertEquals(1, cba.getCalls());
		assertEquals(1, nop.getCount());
		assertEquals(1, nop2.getCount());
		// Removes counting before advisor
		pf.removeAspector(1);
		assertEquals(5, proxied.getAge());
		assertEquals(1, cba.getCalls());
		assertEquals(2, nop.getCount());
		assertEquals(2, nop2.getCount());
		// Removes Nop1
		pf.removeAspector(0);
		assertEquals(5, proxied.getAge());
		assertEquals(1, cba.getCalls());
		assertEquals(2, nop.getCount());
		assertEquals(3, nop2.getCount());

		// Check out of bounds
		try {
			pf.removeAspector(-1);
		}
		catch (ProxyConfigException ex) {
			// Ok
		}

		try {
			pf.removeAspector(2);
		}
		catch (ProxyConfigException ex) {
			// Ok
		}

		assertEquals(5, proxied.getAge());
		assertEquals(4, nop2.getCount());
	}
	@Test
	public void testRemoveAspectorByReference() {
		ComplexBean target = new ComplexBean();
		ProxyFactory pf = new ProxyFactory(target);
		NopInterceptor nop = new NopInterceptor();
		CountingBeforeAspect cba = new CountingBeforeAspect();
		Aspector aspector = new DefaultCutpointAspector(cba);
		pf.addAspect(nop);
		pf.addAspector(aspector);
		IComplexBean proxied = (IComplexBean) pf.getProxy();
		proxied.setAge(5);
		assertEquals(1, cba.getCalls());
		assertEquals(1, nop.getCount());
		assertTrue(pf.removeAspector(aspector));
		assertEquals(5, proxied.getAge());
		assertEquals(1, cba.getCalls());
		assertEquals(2, nop.getCount());
		assertFalse(pf.removeAspector(new DefaultCutpointAspector(null)));
	}
	
	@Test
	public void testIndexOfMethods() {
		ComplexBean target = new ComplexBean();
		ProxyFactory pf = new ProxyFactory(target);
		NopInterceptor nop = new NopInterceptor();
		Aspector aspector = new DefaultCutpointAspector(new CountingBeforeAspect());
		Aspected aspected = (Aspected) pf.getProxy();
		Assert.assertNotNull(aspected);
		aspected.addAspect(nop);
		pf.addAspector(aspector);
		assertEquals(-1, pf.indexOf(new NopInterceptor()));
		assertEquals(0, pf.indexOf(nop));
		assertEquals(1, pf.indexOf(aspector));
		assertEquals(-1, aspected.indexOf(new DefaultCutpointAspector(null)));
	}
	
	@Test
	public void testAddRepeatedInterface() {
		TimeStamped tst = new TimeStamped() {
			@Override
			public long getTimeStamp() {
				throw new UnsupportedOperationException("getTimeStamp");
			}
		};
		ProxyFactory pf = new ProxyFactory(tst);
		// We've already implicitly added this interface.
		// This call should be ignored without error
		pf.addInterface(TimeStamped.class);
		// All cool
		assertThat(pf.getProxy(), instanceOf(TimeStamped.class));
	}

	@Test
	public void testGetsAllInterfaces() throws Exception {
		// Extend to get new interface
		class ComplexBeanSubclass extends ComplexBean implements Comparable<Object> {
			@Override
			public int compareTo(Object arg0) {
				throw new UnsupportedOperationException("compareTo");
			}
		}
		ComplexBeanSubclass raw = new ComplexBeanSubclass();
		ProxyFactory factory = new ProxyFactory(raw);
		assertEquals("Found correct number of interfaces", 2, factory.getProxiedInterfaces().length);
		IComplexBean tb = (IComplexBean) factory.getProxy();
		assertThat("Picked up secondary interface", tb, instanceOf(Comparable.class));

		raw.setAge(25);
		assertTrue(tb.getAge() == raw.getAge());

		long t = 555555L;
		TimestampIntroductionInterceptor ti = new TimestampIntroductionInterceptor(t);

		Class<?>[] oldProxiedInterfaces = factory.getProxiedInterfaces();

		factory.addAspector(0, new DefaultIntroductionAspector(ti, TimeStamped.class));

		Class<?>[] newProxiedInterfaces = factory.getProxiedInterfaces();
		assertEquals("Aspector proxies one more interface after introduction", oldProxiedInterfaces.length + 1, newProxiedInterfaces.length);

		TimeStamped ts = (TimeStamped) factory.getProxy();
		assertTrue(ts.getTimeStamp() == t);
		// Shouldn't fail;
		 ((IComplexBean) ts).getAge();
	}

	/**
	 * Should see effect immediately on behavior.
	 */
	@Test
	public void testCanAddAndRemoveAspectInterfacesOnSingleton() {
		ProxyFactory config = new ProxyFactory(new ComplexBean());

		assertFalse("Shouldn't implement TimeStamped before manipulation",
				config.getProxy() instanceof TimeStamped);

		long time = 666L;
		TimestampIntroductionInterceptor ti = new TimestampIntroductionInterceptor();
		ti.setTime(time);

		// Add to front of interceptor chain
		int oldCount = config.getAspectors().length;
		config.addAspector(0, new DefaultIntroductionAspector(ti, TimeStamped.class));

		assertTrue(config.getAspectors().length == oldCount + 1);

		TimeStamped ts = (TimeStamped) config.getProxy();
		assertTrue(ts.getTimeStamp() == time);

		// Can remove
		config.removeAspect(ti);

		assertTrue(config.getAspectors().length == oldCount);

		try {
			// Existing reference will fail
			ts.getTimeStamp();
			fail("Existing object won't implement this interface any more");
		}
		catch (RuntimeException ex) {
		}

		assertFalse("Should no longer implement TimeStamped",
				config.getProxy() instanceof TimeStamped);

		// Now check non-effect of removing interceptor that isn't there
		config.removeAspect(new DebugInterceptor());

		assertTrue(config.getAspectors().length == oldCount);

		IComplexBean it = (IComplexBean) ts;
		DebugInterceptor debugInterceptor = new DebugInterceptor();
		config.addAspect(0, debugInterceptor);
		it.getSpouse();
		assertEquals(1, debugInterceptor.getCount());
		config.removeAspect(debugInterceptor);
		it.getSpouse();
		// not invoked again
		assertTrue(debugInterceptor.getCount() == 1);
	}

	@Test
	public void testProxyTargetClassWithInterfaceAsTarget() {
		ProxyFactory pf = new ProxyFactory();
		pf.setTargetClass(IComplexBean.class);
		Object proxy = pf.getProxy();
		assertTrue("Proxy is a JDK proxy", ProxyUtils.isJdkDynamicProxy(proxy));
		assertTrue(proxy instanceof IComplexBean);
		assertEquals(IComplexBean.class, ProxyUtils.getTargetClass(proxy));

		ProxyFactory pf2 = new ProxyFactory(proxy);
		Object proxy2 = pf2.getProxy();
		assertTrue("Proxy is a JDK proxy", ProxyUtils.isJdkDynamicProxy(proxy2));
		assertTrue(proxy2 instanceof IComplexBean);
		assertEquals(IComplexBean.class, ProxyUtils.getTargetClass(proxy2));
	}

	@Test
	public void testProxyTargetClassWithConcreteClassAsTarget() {
		ProxyFactory pf = new ProxyFactory();
		pf.setTargetClass(ComplexBean.class);
		Object proxy = pf.getProxy();
		assertTrue("Proxy is a Javassist proxy", ProxyUtils.isJavassistProxy(proxy));
		assertTrue(proxy instanceof ComplexBean);
		assertEquals(ComplexBean.class, ProxyUtils.getTargetClass(proxy));

		ProxyFactory pf2 = new ProxyFactory(proxy);
		pf2.setProxyTargetClass(true);
		Object proxy2 = pf2.getProxy();
		assertTrue("Proxy is a Javassist proxy", ProxyUtils.isJavassistProxy(proxy2));
		assertTrue(proxy2 instanceof ComplexBean);
		assertEquals(ComplexBean.class, ProxyUtils.getTargetClass(proxy2));
	}


	@SuppressWarnings("serial")
	private static class TimestampIntroductionInterceptor extends DelegatingIntroductionInterceptor
			implements TimeStamped {

		private long ts;

		public TimestampIntroductionInterceptor() {
		}

		public TimestampIntroductionInterceptor(long ts) {
			this.ts = ts;
		}

		public void setTime(long ts) {
			this.ts = ts;
		}

		@Override
		public long getTimeStamp() {
			return ts;
		}
	}
	
	private interface TimeStamped{
		 long getTimeStamp();
	}
	private interface IOther{
		void absquatulate();
	}
}
