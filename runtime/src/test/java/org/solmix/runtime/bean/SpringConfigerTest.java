/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.bean;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.solmix.runtime.bean.ConfiguredBean.TTestBean;
import org.solmix.runtime.support.spring.ContainerApplicationContext;
import org.solmix.runtime.support.spring.SpringConfigurer;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月7日
 */

public class SpringConfigerTest
{

    @Test
    public void testNoFoundConfig() {
        //找不到也没有匹配上,所以啥都没有
        ConfiguredBean tb = new ConfiguredBean("xxxoo");
        String file = "/org/solmix/runtime/bean/configer.xml";
        ContainerApplicationContext overload = new ContainerApplicationContext(
            file, false);
        SpringConfigurer sg = new SpringConfigurer(overload);
        sg.configureBean(tb);

    }

    @Test
    public void testFoundStarConfig() {
        // 和spring配置中的bean id=*7匹配,并通过他注入配置
        ConfiguredBean tb = new ConfiguredBean("test7");
        String file = "/org/solmix/runtime/bean/configer.xml";
        ContainerApplicationContext overload = new ContainerApplicationContext(
            file, false);
        SpringConfigurer sg = new SpringConfigurer(overload);
        sg.configureBean(tb);
        Assert.assertEquals("StarHallo", tb.getStringAttr());
        Assert.assertTrue(tb.getBooleanAttr());

    }

    @Test
    public void testFoundStarAndNameConfig() {
        // 和spring配置中的bean id=*7和id=config7匹配,并通过他注入配置
        ConfiguredBean tb = new ConfiguredBean("config7");
        String file = "/org/solmix/runtime/bean/configer.xml";
        ContainerApplicationContext overload = new ContainerApplicationContext(
            file, false);
        SpringConfigurer sg = new SpringConfigurer(overload);
        sg.configureBean(tb);
        Assert.assertEquals("StarHallo", tb.getStringAttr());
        Assert.assertTrue(!tb.getBooleanAttr());
        Assert.assertEquals(BigInteger.TEN, tb.getIntegerAttr());

        TTestBean ttb = tb.new TTestBean("config7");
        sg.configureBean(ttb);
        Assert.assertEquals("StarHallo", ttb.getStringAttr());
        Assert.assertTrue(!ttb.getBooleanAttr());
        Assert.assertEquals(BigInteger.TEN, ttb.getIntegerAttr());
    }

    @Test
    public void testFoundConfigID() {
        // 和spring配置中的bean id=config匹配,并通过他注入配置
        ConfiguredBean tb = new ConfiguredBean("config");
        String file = "/org/solmix/runtime/bean/configer.xml";
        ContainerApplicationContext overload = new ContainerApplicationContext(
            file, false);
        SpringConfigurer sg = new SpringConfigurer(overload);
        sg.configureBean(tb);
        Assert.assertEquals("hallo", tb.getStringAttr());

    }

    @Test
    public void testFoundConfigName() {
        // 和spring配置中的bean name=configName匹配,并通过他注入配置
        ConfiguredBean tb = new ConfiguredBean("configName");
        String file = "/org/solmix/runtime/bean/configer.xml";
        ContainerApplicationContext overload = new ContainerApplicationContext(
            file, false);
        SpringConfigurer sg = new SpringConfigurer(overload);
        sg.configureBean(tb);
        Assert.assertEquals("Unexpected value for attribute stringAttr",
            "hallo", tb.getStringAttr());
        Assert.assertTrue("Unexpected value for attribute booleanAttr",
            !tb.getBooleanAttr());
        Assert.assertEquals("Unexpected value for attribute integerAttr",
            BigInteger.TEN, tb.getIntegerAttr());
        Assert.assertEquals("Unexpected value for attribute intAttr",
            new Integer(12), tb.getIntAttr());
        Assert.assertEquals("Unexpected value for attribute longAttr",
            new Long(13L), tb.getLongAttr());
        Assert.assertEquals("Unexpected value for attribute shortAttr",
            new Short((short) 14), tb.getShortAttr());
        Assert.assertEquals("Unexpected value for attribute decimalAttr",
            new BigDecimal("15"), tb.getDecimalAttr());
        Assert.assertEquals("Unexpected value for attribute floatAttr",
            new Float(16F), tb.getFloatAttr());
        Assert.assertEquals("Unexpected value for attribute doubleAttr",
            new Double(17D), tb.getDoubleAttr());
        Assert.assertEquals("Unexpected value for attribute byteAttr",
            new Byte((byte) 18), tb.getByteAttr());

        Assert.assertEquals(
            "Unexpected value for attribute unsignedIntAttrNoDefault",
            new Long(19L), tb.getUnsignedIntAttr());
        Assert.assertEquals(
            "Unexpected value for attribute unsignedShortAttrNoDefault",
            new Integer(20), tb.getUnsignedShortAttr());
        Assert.assertEquals(
            "Unexpected value for attribute unsignedByteAttrNoDefault",
            new Short((short) 21), tb.getUnsignedByteAttr());

    }
}
