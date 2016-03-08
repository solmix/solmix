package org.solmix.runtime.management;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.support.spring.SpringContainerFactory;
public class DefaultComponentManagerTest
{

    ComponentManager manager;
    Container c;
   

    @After
    public void tearDown() throws Exception {
        if (c != null) {
            c.close(true);
        }
    }
    @Test
    public void testWithNoServer() {
       SpringContainerFactory fcf = new SpringContainerFactory();
      c= fcf.createContainer( );
      manager = c.getExtension(ComponentManager.class);
      Assert.assertTrue(manager!=null);
      Assert.assertNull(manager.getMBeanServer());
      
      
    }
    
    @Test
    public void test() {
       SpringContainerFactory fcf = new SpringContainerFactory();
       c=fcf.createContainer( "/org/solmix/runtime/management/mg-spring.xml");
       manager = c.getExtension(ComponentManager.class);
       Assert.assertTrue(manager!=null);
       Assert.assertNotNull(manager.getMBeanServer());
    }
}
