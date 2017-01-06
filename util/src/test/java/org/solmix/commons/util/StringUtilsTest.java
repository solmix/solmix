package org.solmix.commons.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class StringUtilsTest {
	@Test
	public void test()  {
		String a =StringUtils.camelToSplitName("objName","_").toUpperCase();
		  assertEquals("OBJ_NAME",a);
	}
}
