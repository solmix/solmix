package org.solmix.commons;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;


public class VersionTest
{

    @Test
    public void test_readFromMaven() {
       String slf4j= Version.readFromMaven("org.slf4j", "slf4j-api");
       Assert.assertNotNull(slf4j);
       Assert.assertEquals("1.7.18", slf4j);
    }

    

    @Test
    public void test_getVersion() {
       String slf4j= Version.getVersion(Logger.class, "0.0");
       Assert.assertNotNull(slf4j);
       Assert.assertEquals("1.7.18", slf4j);
    }
}
