package org.solmix.generator.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solmix.generator.config.ConfigurationInfo;
import org.solmix.generator.config.ConfigurationParser;
import org.solmix.generator.config.InvalidConfigurationException;
import org.solmix.generator.config.XMLParserException;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.monitor.support.MonitorServiceImpl;
import org.solmix.runtime.resource.ResourceManager;


public class GeneratedJavaFileTest
{
  static  Container c;

    @BeforeClass
    public static void setup() {
        System.out.println(new MonitorServiceImpl().getMonitorInfo().getUsedMemory());
        c = ContainerFactory.getThreadDefaultContainer(true);

    }

    @Test
    public void testParser() throws IOException, XMLParserException, InvalidConfigurationException {
        List<String> warnings = new ArrayList<String>();
        ConfigurationParser parser = new ConfigurationParser(c, warnings);
        ResourceManager rm = c.getExtension(ResourceManager.class);
        ConfigurationInfo ci = parser.parseConfiguration(rm.getResourceAsStream("classpath:config/generator-test.xml").getInputStream());
        CodeGenerator cg = new CodeGenerator(ci, null, warnings);
        try {
            cg.generate(null);
        } catch (SQLException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<GeneratedJavaFile> javaFiles=cg.getGeneratedJavaFiles();
        Assert.assertNotNull(javaFiles);
        
    }

    @AfterClass
    public static void tearDown() {
        if (c != null) {
            c.close();
        }
    }
}
