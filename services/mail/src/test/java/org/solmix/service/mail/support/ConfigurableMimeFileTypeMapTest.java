package org.solmix.service.mail.support;


import org.junit.Assert;
import org.junit.Test;


public class ConfigurableMimeFileTypeMapTest
{

 
    @Test
    public void test() {
        ConfigurableMimeFileTypeMap map   = new ConfigurableMimeFileTypeMap();
       String ct= map.getContentType("6.jpg");
       Assert.assertEquals("image/jpeg", ct);
    }

}
