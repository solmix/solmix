package org.solmix.runtime.proxy;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.proxy.support.DefaultCutpointAspector;
import org.solmix.test.bean.ComplexBean;
import org.solmix.tests.proxy.CountingBeforeAspect;
import org.solmix.tests.proxy.NopInterceptor;

public class ProxyFactoryTest {

	
	@Test
	public void testIndexOfMethods() {
		ComplexBean target = new ComplexBean();
		ProxyFactory pf = new ProxyFactory(target);
		NopInterceptor nop = new NopInterceptor();
		Aspector aspector = new DefaultCutpointAspector(new CountingBeforeAspect());
		Aspected aspected = (Aspected) pf.getProxy();
		Assert.assertNotNull(aspected);
		// Can use advised and ProxyFactory interchangeably
//		pf.addAdvisor(advisor);
//		assertEquals(-1, pf.indexOf(new NopInterceptor()));
//		assertEquals(0, pf.indexOf(nop));
//		assertEquals(1, pf.indexOf(advisor));
//		assertEquals(-1, advised.indexOf(new DefaultPointcutAdvisor(null)));
	}
}
