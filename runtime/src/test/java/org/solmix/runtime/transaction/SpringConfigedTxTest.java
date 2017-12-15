package org.solmix.runtime.transaction;

import junit.framework.TestCase;

import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.proxy.ProxyManager;
import org.solmix.runtime.support.spring.SpringContainerFactory;

public class SpringConfigedTxTest extends TestCase {

	@Test
    public void test() {
        SpringContainerFactory factory = new SpringContainerFactory();
        String file = "/org/solmix/runtime/support/spring/tx-resource.xml";
        Container c = factory.createContainer(file, true);
        try{
        	ProxyManager pm=	c.getExtension(ProxyManager.class);
        	assertNotNull(pm);
        	ContainerFactory.getContainers();
        }finally{
        	if(c!=null)
        		c.close();
        }
      
    }
}
