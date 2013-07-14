/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.commons.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @version 110035
 */
public class DataUtilTest {
	@Test
	public void getPropertyDescriptorsTest() throws Exception {
		Map map = DataUtil.getPropertyDescriptors(Bean1.class);

		Assert.assertNotNull(map.get("b"));
	}

	@Test
	public void setPropertiesTest() throws Exception {
		Bean2 bean = new Bean2();
		Map testValues = new HashMap();
		testValues.put("intObj", 123);
		testValues.put("justInt", "");
		testValues.put("floatObj", "");
		testValues.put("justFloat", "");
		testValues.put("boolObj", "");
		testValues.put("justBool", "");
		DataUtil.setProperties(testValues, bean);
		// Assert.assertEquals(123, bean.getIntObj().intValue());
		Map map = DataUtil.getPropertyDescriptors(bean.getClass());
		for(Object o : map.keySet()) {
			System.out.println(o.toString() + "===" + map.get(o));
		}
		System.out.println((new StringBuilder()).append("Bean is: ").append("").toString());
		testValues.put("intObj", null);
		testValues.put("justInt", null);
		testValues.put("floatObj", null);
		testValues.put("justFloat", null);
		testValues.put("boolObj", null);
		testValues.put("justBool", null);
		DataUtil.setProperties(testValues, bean);
		Assert.assertNull(bean.getIntObj());
	}

	@Test
	public void arrayAddTest() {
		String[] str = { "13", "123" };
		String[] str2 = { "13", "123" };
		String[] str3 = DataUtil.arrayAdd(str, str2);
		Assert.assertEquals("123", str3[3]);
		for(String s : str3)
			System.out.println(s);

	}
	@Test
	public void converType() {
		Boolean b=Boolean.TRUE;
		Boolean str3;
		try {
			str3 = DataUtil.convertType(Boolean.class, b);
			Assert.assertEquals(Boolean.TRUE, str3);
			Integer i=4;
			Object d = DataUtil.castValue(i, Double.class);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	@Test
	public void beanMergeTest() {
		Bean1 b1 = new Bean1();
		b1.setA(123);
		b1.setA11(true);
		Bean11 b2 = new Bean11();
		try {
			DataUtil.beanMerge(b1, b2, false);
		} catch(IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("bean2:" + b2.getA() + b2.isA11());

	}
}
