
package org.solmix.generator.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.xml.dom.Document;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.monitor.support.MonitorServiceImpl;
import org.solmix.runtime.resource.ResourceManager;

public class ConfigurationParserTest
{

    Container c;

    @Before
    public void setup() {
        System.out.println(new MonitorServiceImpl().getMonitorInfo().getUsedMemory());
        c = ContainerFactory.getThreadDefaultContainer(true);

    }

    @Test
    public void testParser() throws IOException, XMLParserException {
        List<String> warnings = new ArrayList<String>();
        ConfigurationParser parser = new ConfigurationParser(c, warnings);
        ResourceManager rm = c.getExtension(ResourceManager.class);
        ConfigurationInfo ci = parser.parseConfiguration(rm.getResourceAsStream("classpath:config/test.xml").getInputStream());
        List<DomainInfo> domains = ci.getDomains();
        Assert.assertNotNull(ci.getDomain("aaa"));
        Document doc= ci.toDocument();
        String content = doc.getFormattedContent();
        System.out.println(doc.getFormattedContent());
        parser.parseConfiguration(new StringReader(content));
        
    }

    @After
    public void tearDown() {
        if (c != null) {
            c.close();
        }
    }
}
