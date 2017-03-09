package org.solmix.commons.regex;

import junit.framework.TestCase;

public class SimpleUrlPatternTest extends TestCase {

	public void testPattern(){
		SimpleUrlPattern sup= new SimpleUrlPattern("com.foo.a*");
		assertTrue(sup.match("com.foo.a.b"));
	}
}
