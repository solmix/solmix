package org.solmix.commons.collections;

import org.junit.Assert;
import org.junit.Test;

public class CaseInsensitiveHashMapTest
{


    @Test
    public void test() {
        CaseInsensitiveHashMap map = new CaseInsensitiveHashMap();
        map.put("AaBb", "value");
        Object v = map.get("aabb");
        Assert.assertNotNull(v);
        Assert.assertTrue(map.containsKey("aaBB"));
        map.put("aabB", "revalue");
        Assert.assertEquals("revalue", map.get("AaBb"));
    }
}
