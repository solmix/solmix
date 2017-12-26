package org.solmix.commons.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

public class DataTypeMapTest
{

    @Test
    public void test_null(){
        try {
            DataTypeMap map  = new DataTypeMap(null);
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("Map must not be null", expected.getMessage());
        }
        
    }
    
    @Test
    public void test_get_date(){
            DataTypeMap map  = new DataTypeMap();
            map.put("date", "2017-12-25");
            Assert.assertNotNull(map.getDate("date","yyyy-MM-dd"));
        
    }
}
