
package org.solmix.generator.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.XMLNode;
import org.solmix.commons.xml.XMLParser;
import org.solmix.generator.internal.ObjectFactory;
import org.solmix.runtime.Container;
import org.xml.sax.InputSource;

public class ConfigurationParser
{

    public static final String NS = "gc:";

    private List<String> warnings;

    private List<String> parseErrors;

    private DataTypeMap extraProperties;

    private DataTypeMap configProperties;

    private final Container container;

    private BaseConfigNodeParserProvider provider;

    public ConfigurationParser(Container container, List<String> warnings)
    {
        this(container, null, warnings);
    }

    /**
     * This constructor accepts a properties object which may be used to specify an additional property set. Typically
     * this property set will be Ant or Maven properties specified in the build.xml file or the POM.
     * 
     * <p>
     * If there are name collisions between the different property sets, they will be resolved in this order:
     * 
     * <ol>
     * <li>System properties take highest precedence</li>
     * <li>Properties specified in the &lt;properties&gt; configuration element are next</li>
     * <li>Properties specified in this "extra" property set are lowest precedence.</li>
     * </ol>
     * 
     * @param extraProperties an (optional) set of properties used to resolve property references in the configuration
     *        file
     * @param warnings any warnings are added to this array
     */
    public ConfigurationParser(Container container, DataTypeMap extraProperties, List<String> warnings)
    {
        super();
        this.container = container;
        this.extraProperties = extraProperties;

        if (warnings == null) {
            this.warnings = new ArrayList<String>();
        } else {
            this.warnings = warnings;
        }
        provider = new BaseConfigNodeParserProvider(this.container);
        parseErrors = new ArrayList<String>();
    }

    public ConfigurationInfo parseConfiguration(File inputFile) throws IOException, XMLParserException {

        FileReader fr = new FileReader(inputFile);

        return parseConfiguration(fr);
    }

    public ConfigurationInfo parseConfiguration(Reader reader) throws IOException, XMLParserException {

        InputSource is = new InputSource(reader);

        return parseConfiguration(is);
    }

    public ConfigurationInfo parseConfiguration(InputStream inputStream) throws IOException, XMLParserException {

        InputSource is = new InputSource(inputStream);

        return parseConfiguration(is);
    }

    private ConfigurationInfo parseConfiguration(InputSource inputSource) throws IOException, XMLParserException {
        parseErrors.clear();
        XMLParser parser = new XMLParser(inputSource, true, extraProperties, new GeneratorEntityResolver(), NS);
        return parseConfigurationElement(parser.evalNode(ConfigNodeParserProvider.ROOT));
    }

    protected ConfigurationInfo parseConfigurationElement(XMLNode xmlNode) throws XMLParserException {
        ConfigurationInfo configuration = new ConfigurationInfo();
        ConfigParserContext ctx = new ConfigParserContext(provider, this);
        List<XMLNode> nodes = xmlNode.getChildren();
        for (XMLNode node : nodes) {
            String name = node.getName();
            if ("properties".equals(name)) {
                parseProperties(node, ctx, configuration);
            } else if ("classPathEntry".equals(name)) {
                parseClassPathEntry(node, ctx, configuration);
            } else if ("domain".equals(name)) {
                parseDomain(node, ctx, configuration);
            }
        }
        return configuration;

    }

    private void parseDomain(XMLNode node, ConfigParserContext ctx, ConfigurationInfo configuration) {
        if (node == null) {
            return;
        }
        String id = node.getStringAttribute("id");
        String targetRuntime = node.getStringAttribute("targetRuntime");
        String modelType  = node.getStringAttribute("modelType");
        String introspectedColumnImpl = node.getStringAttribute("introspectedColumnImpl");
        ModelType mt = modelType == null ? null : ModelType.getModelType(modelType);
        DomainInfo di = new DomainInfo(mt);
        di.setId(id);
        if (!StringUtils.isEmpty(introspectedColumnImpl)) {
            di.setIntrospectedColumnImpl(introspectedColumnImpl);
        }
        if (!StringUtils.isEmpty(targetRuntime)) {
            di.setTargetRuntime(targetRuntime);
        }

        configuration.addDomain(di);
        List<XMLNode> nodeList = node.getChildren();
        for (XMLNode cnode:nodeList) {

            String name = cnode.getName();
            if ("property".equals(name)) { 
                parseProperty(di, ctx,cnode);
            } else if ("plugin".equals(name)) { 
                parsePlugin(di, ctx,cnode);
            } else if ("commentGenerator".equals(name)) { 
                parseCommentGenerator(di, ctx,cnode);
            } else if ("jdbcConnection".equals(name)) { 
                parseJdbcConnection(di, ctx,cnode);
            } else if ("connectionFactory".equals(name)) { 
                parseConnectionFactory(di, ctx,cnode);
            } else if ("javaModelGenerator".equals(name)) { 
                parseJavaModelGenerator(di, ctx,cnode);
            } else if ("javaTypeResolver".equals(name)) { 
                parseJavaTypeResolver(di, ctx,cnode);
            } else if ("sqlMapGenerator".equals(name)) { 
                parseSqlMapGenerator(di, ctx,cnode);
            } else if ("javaClientGenerator".equals(name)) { 
                parseJavaClientGenerator(di, ctx,cnode);
            } else if ("table".equals(name)) { 
                parseTable(di, ctx,cnode);
            }
        }
    }

    private void parsePlugin(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        PluginInfo pi = new PluginInfo();
        di.addPluginInfo(pi);
        pi.setType(node.getStringAttribute("type"));
        parseProperties(pi, ctx,node.evalNodes("property"));
    }

    private void parseProperties(PropertyHolder propertyHolder, ConfigParserContext ctx, List<XMLNode> nodes) {
        if(nodes==null||nodes.size()<=0){
            return;
        }
        for(XMLNode node:nodes){
            parseProperty(propertyHolder, ctx, node);
        }
        
    }

    private void parseProperty(PropertyHolder propertyHolder, ConfigParserContext ctx, XMLNode node) {
        String name = node.getStringAttribute("name"); 
        String value = node.getStringAttribute("value"); 
        propertyHolder.addProperty(name, value);
    }

    private void parseClassPathEntry(XMLNode node, ConfigParserContext ctx, ConfigurationInfo configuration) {
        if (node == null) {
            return;
        }
        configuration.addClasspathEntry(node.getStringAttribute("location"));

    }

    private void parseProperties(XMLNode node, ConfigParserContext ctx, ConfigurationInfo configuration) throws XMLParserException {
        if (node == null) {
            return;
        }

        String resource = node.getStringAttribute("resource");
        String url = node.getStringAttribute("url");
        if (StringUtils.isEmpty(resource) && StringUtils.isEmpty(url)) {
            throw new XMLParserException("<properties > tag has no resource or url attr");
        }
        if (!StringUtils.isEmpty(resource) && !StringUtils.isEmpty(url)) {
            throw new XMLParserException("<properties > tag has bouth resource and url attr");
        }
        URL resourceUrl;
        try {
            if (!StringUtils.isEmpty(resource)) {
                resourceUrl = ObjectFactory.getResource(resource);
                if (resourceUrl == null) {
                    throw new XMLParserException("resource is null"); 
                }
            } else {
                resourceUrl = new URL(url);
            }

            InputStream inputStream = resourceUrl.openConnection().getInputStream();
            Properties configprop = new Properties();
            configprop.load(inputStream);
            inputStream.close();
            configProperties.putAll(DataUtils.toDataTypeMap(configprop));
        } catch (IOException e) {
            if (!StringUtils.isEmpty(resource)) {
                throw new XMLParserException("parse resource " + resource + "error"); 
            } else {
                throw new XMLParserException("parse url " + url + "error"); 
            }
        }
    }

    String resolveProperty(String key) {
        String property = null;

        property = System.getProperty(key);

        if (property == null) {
            property = configProperties.getString(key);
        }

        if (property == null) {
            property = extraProperties.getString(key);
        }

        return property;
    }
}
