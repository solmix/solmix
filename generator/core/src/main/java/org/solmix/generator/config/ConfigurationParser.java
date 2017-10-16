
package org.solmix.generator.config;

import static org.solmix.commons.util.DataUtils.asBoolean;
import static org.solmix.commons.util.StringUtils.stringHasValue;

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
        String modelType = node.getStringAttribute("modelType");
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
        for (XMLNode cnode : nodeList) {

            String name = cnode.getName();
            if ("property".equals(name)) {
                parseProperty(di, ctx, cnode);
            } else if ("plugin".equals(name)) {
                parsePlugin(di, ctx, cnode);
            } else if ("commentGenerator".equals(name)) {
                parseCommentGenerator(di, ctx, cnode);
            } else if ("jdbcConnection".equals(name)) {
                parseJdbcConnection(di, ctx, cnode);
            } else if ("connectionFactory".equals(name)) {
                parseConnectionFactory(di, ctx, cnode);
            } else if ("javaModelGenerator".equals(name)) {
                parseJavaModelGenerator(di, ctx, cnode);
            } else if ("javaTypeResolver".equals(name)) {
                parseJavaTypeResolver(di, ctx, cnode);
            } else if ("sqlMapGenerator".equals(name)) {
                parseSqlMapGenerator(di, ctx, cnode);
            } else if ("javaClientGenerator".equals(name)) {
                parseJavaClientGenerator(di, ctx, cnode);
            } else if ("dataxGenerator".equals(name)) {
                parsedataxGenerator(di, ctx, cnode);
            } else if ("table".equals(name)) {
                parseTable(di, ctx, cnode);
            }
        }
    }

    private void parsedataxGenerator(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        DataxGeneratorInfo smgi = new DataxGeneratorInfo();
        di.setDataxGeneratorInfo(smgi);
        String targetPackage = node.getStringAttribute("targetPackage"); 
        String targetProject = node.getStringAttribute("targetProject"); 

        smgi.setTargetPackage(targetPackage);
        smgi.setTargetProject(targetProject);
        parseProperties(smgi, ctx, node.evalNodes("property"));
        
    }

    private void parseTable(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        TableInfo ti = new TableInfo(di);
        di.addTableInfo(ti);
        String catalog = node.getStringAttribute("catalog"); 
        if (stringHasValue(catalog)) {
            ti.setCatalog(catalog);
        }

        String schema = node.getStringAttribute("schema"); 
        if (stringHasValue(schema)) {
            ti.setSchema(schema);
        }
        
        String remark = node.getStringAttribute("remark"); 
        if (stringHasValue(remark)) {
            ti.setRemark(remark);
        }

        String tableName = node.getStringAttribute("tableName"); 
        if (stringHasValue(tableName)) {
            ti.setTableName(tableName);
        }

        String domainObjectName = node.getStringAttribute("domainObjectName"); 
        if (stringHasValue(domainObjectName)) {
            ti.setDomainObjectName(domainObjectName);
        }

        String alias = node.getStringAttribute("alias"); 
        if (stringHasValue(alias)) {
            ti.setAlias(alias);
        }
        

        String enableInsert = node.getStringAttribute("enableInsert"); 
        if (stringHasValue(enableInsert)) {
            ti.setInsertStatementEnabled(asBoolean(enableInsert));
        }

        String enableSelectByPrimaryKey = node.getStringAttribute("enableSelectByPrimaryKey"); 
        if (stringHasValue(enableSelectByPrimaryKey)) {
            ti.setSelectByPrimaryKeyStatementEnabled(asBoolean(enableSelectByPrimaryKey));
        }

        String enableSelectByExample = node.getStringAttribute("enableSelectByExample"); 
        if (stringHasValue(enableSelectByExample)) {
            ti.setSelectByExampleStatementEnabled(asBoolean(enableSelectByExample));
        }

        String enableUpdateByPrimaryKey = node.getStringAttribute("enableUpdateByPrimaryKey"); 
        if (stringHasValue(enableUpdateByPrimaryKey)) {
            ti.setUpdateByPrimaryKeyStatementEnabled(asBoolean(enableUpdateByPrimaryKey));
        }

        String enableDeleteByPrimaryKey = node.getStringAttribute("enableDeleteByPrimaryKey"); 
        if (stringHasValue(enableDeleteByPrimaryKey)) {
            ti.setDeleteByPrimaryKeyStatementEnabled(asBoolean(enableDeleteByPrimaryKey));
        }

        String enableDeleteByExample = node.getStringAttribute("enableDeleteByExample"); 
        if (stringHasValue(enableDeleteByExample)) {
            ti.setDeleteByExampleStatementEnabled(asBoolean(enableDeleteByExample));
        }

        String enableCountByExample = node.getStringAttribute("enableCountByExample"); 
        if (stringHasValue(enableCountByExample)) {
            ti.setCountByExampleStatementEnabled(asBoolean(enableCountByExample));
        }

        String enableUpdateByExample = node.getStringAttribute("enableUpdateByExample"); 
        if (stringHasValue(enableUpdateByExample)) {
            ti.setUpdateByExampleStatementEnabled(asBoolean(enableUpdateByExample));
        }

        String selectByPrimaryKeyQueryId = node.getStringAttribute("selectByPrimaryKeyQueryId"); 
        if (stringHasValue(selectByPrimaryKeyQueryId)) {
            ti.setSelectByPrimaryKeyQueryId(selectByPrimaryKeyQueryId);
        }

        String selectByExampleQueryId = node.getStringAttribute("selectByExampleQueryId"); 
        if (stringHasValue(selectByExampleQueryId)) {
            ti.setSelectByExampleQueryId(selectByExampleQueryId);
        }

        String modelType = node.getStringAttribute("modelType"); 
        if (stringHasValue(modelType)) {
            ti.setConfiguredModelType(modelType);
        }

        String escapeWildcards = node.getStringAttribute("escapeWildcards"); 
        if (stringHasValue(escapeWildcards)) {
            ti.setWildcardEscapingEnabled(asBoolean(escapeWildcards));
        }

        String delimitIdentifiers = node.getStringAttribute("delimitIdentifiers"); 
        if (stringHasValue(delimitIdentifiers)) {
            ti.setDelimitIdentifiers(asBoolean(delimitIdentifiers));
        }

        String delimitAllColumns = node.getStringAttribute("delimitAllColumns"); 
        if (stringHasValue(delimitAllColumns)) {
            ti.setAllColumnDelimitingEnabled(asBoolean(delimitAllColumns));
        }

        String mapperName = node.getStringAttribute("mapperName"); 
        if (stringHasValue(mapperName)) {
            ti.setMapperName(mapperName);
        }

        String sqlProviderName = node.getStringAttribute("sqlProviderName"); 
        if (stringHasValue(sqlProviderName)) {
            ti.setSqlProviderName(sqlProviderName);
        }
        List<XMLNode> nodeList = node.getChildren();
        for (XMLNode cnode : nodeList) {

            String name = cnode.getName();
            if ("property".equals(name)) {
                parseProperty(ti, ctx, cnode);
            } else if ("generatedKey".equals(name)) {
                parseGeneratedKey(ti, ctx, cnode);
            } else if ("columnRenamingRule".equals(name)) {
                parseColumnRenamingRule(ti, ctx, cnode);
            } else if ("columnIgnoreRule".equals(name)) {
                parseColumnIgnoreRule(ti, ctx, cnode);
            } else if ("domainObjectRenamingRule".equals(name)) {
                parseDomainObjectRenamingRule(ti, ctx, cnode);
            } else if ("column".equals(name)) {
                parseColumn(ti, ctx, cnode);
            }
        }
    }

    private void parseColumn(TableInfo ti, ConfigParserContext ctx, XMLNode node) {
        ColumnInfo co = new ColumnInfo();
        String column = node.getStringAttribute("column"); 

        co.setColumn(column);
        String property = node.getStringAttribute("property"); 
        if (stringHasValue(property)) {
            co.setProperty(property);
        }

        String javaType = node.getStringAttribute("javaType"); 
        if (stringHasValue(javaType)) {
            co.setJavaType(javaType);
        }

        String jdbcType = node.getStringAttribute("jdbcType"); 
        if (stringHasValue(jdbcType)) {
            co.setJdbcType(jdbcType);
        }

        String typeHandler = node.getStringAttribute("typeHandler"); 
        if (stringHasValue(typeHandler)) {
            co.setTypeHandler(typeHandler);
        }
        
        co.setColumnSize(node.getIntAttribute("columnSize",0));

        String delimitedColumnName = node.getStringAttribute("delimitedColumnName"); 
        if (stringHasValue(delimitedColumnName)) {
            co.setColumnNameDelimited(asBoolean(delimitedColumnName));
        }

        String isGeneratedAlways = node.getStringAttribute("isGeneratedAlways"); 
        if (stringHasValue(isGeneratedAlways)) {
            co.setGeneratedAlways(Boolean.parseBoolean(isGeneratedAlways));
        }
        co.setOverride(node.getBooleanAttribute("override",false));
        co.setIgnore(node.getBooleanAttribute("ignore",false));
        co.setPrimaryKey(node.getBooleanAttribute("primaryKey",false));
        co.setRemark(node.getStringAttribute("remark"));
        co.setDefaultValue(node.getStringAttribute("defaultValue"));
        co.setScale(node.getIntAttribute("scale", 0));
        co.setNativeType(node.getStringAttribute("nativeType"));
        XMLNode desc = node.evalNode("desc");
        if (desc != null) {
            co.setDesc(desc.getStringBody().trim());
        }
        ti.addColumn(co);

    }

    private void parseColumnIgnoreRule(TableInfo ti, ConfigParserContext ctx, XMLNode node) {
        String pattern = node.getStringAttribute("pattern"); 

        IgnoredColumnPattern icPattern = new IgnoredColumnPattern(pattern);
        ti.addIgnoredColumnPattern(icPattern);
    }

    private void parseDomainObjectRenamingRule(TableInfo ti, ConfigParserContext ctx, XMLNode node) {
        String searchString = node.getStringAttribute("searchString"); 
        String replaceString = node.getStringAttribute("replaceString"); 

        DomainObjectRenamingRule dorr = new DomainObjectRenamingRule();

        dorr.setSearchString(searchString);

        if (stringHasValue(replaceString)) {
            dorr.setReplaceString(replaceString);
        }

        ti.setDomainObjectRenamingRule(dorr);

    }

    private void parseColumnRenamingRule(TableInfo ti, ConfigParserContext ctx, XMLNode node) {
        String searchString = node.getStringAttribute("searchString"); 
        String replaceString = node.getStringAttribute("replaceString"); 

        ColumnRenamingRule crr = new ColumnRenamingRule();

        crr.setSearchString(searchString);

        if (stringHasValue(replaceString)) {
            crr.setReplaceString(replaceString);
        }

        ti.setColumnRenamingRule(crr);

    }

    private void parseGeneratedKey(TableInfo ti, ConfigParserContext ctx, XMLNode node) {
        String column = node.getStringAttribute("column"); 
        boolean identity = node.getBooleanAttribute("identity"); 
        String sqlStatement = node.getStringAttribute("sqlStatement"); 
        String type = node.getStringAttribute("type"); 

        GeneratedKey gk = new GeneratedKey(column, sqlStatement, identity, type);

        ti.setGeneratedKey(gk);

    }

    private void parseJavaClientGenerator(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        JavaClientGeneratorInfo jcgi = new JavaClientGeneratorInfo();
        di.setJavaClientGeneratorInfo(jcgi);
        String type = node.getStringAttribute("type");
        if (StringUtils.stringHasValue(type)) {
            jcgi.setType(type);
        }
        String targetPackage = node.getStringAttribute("targetPackage"); 
        String targetProject = node.getStringAttribute("targetProject"); 
        String implementationPackage = node.getStringAttribute("implementationPackage"); 

        jcgi.setTargetPackage(targetPackage);
        jcgi.setTargetProject(targetProject);
        jcgi.setImplementationPackage(implementationPackage);

    }

    private void parseSqlMapGenerator(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        SqlMapGeneratorInfo smgi = new SqlMapGeneratorInfo();
        di.setSqlMapGeneratorInfo(smgi);
        String targetPackage = node.getStringAttribute("targetPackage"); 
        String targetProject = node.getStringAttribute("targetProject"); 

        smgi.setTargetPackage(targetPackage);
        smgi.setTargetProject(targetProject);
        parseProperties(smgi, ctx, node.evalNodes("property"));
    }

    private void parseJavaTypeResolver(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        JavaTypeResolverInfo jtri = new JavaTypeResolverInfo();
        di.setJavaTypeResolverInfo(jtri);
        String type = node.getStringAttribute("type");
        if (StringUtils.stringHasValue(type)) {
            jtri.setType(type);
        }
        parseProperties(jtri, ctx, node.evalNodes("property"));
    }

    private void parseJavaModelGenerator(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        JavaModelGeneratorInfo jgi = new JavaModelGeneratorInfo();
        di.setJavaModelGeneratorInfo(jgi);
        String targetPackage = node.getStringAttribute("targetPackage"); 
        String targetProject = node.getStringAttribute("targetProject"); 

        jgi.setTargetPackage(targetPackage);
        jgi.setTargetProject(targetProject);

    }

    private void parseConnectionFactory(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        ConnectionFactoryInfo cfi = new ConnectionFactoryInfo();
        di.setConnectionFactoryInfo(cfi);
        String type = node.getStringAttribute("type");
        if (StringUtils.stringHasValue(type)) {
            cfi.setType(type);
        }
        parseProperties(cfi, ctx, node.evalNodes("property"));

    }

    private void parseJdbcConnection(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        JdbcConnectionInfo jci = new JdbcConnectionInfo();
        di.setJdbcConnectionInfo(jci);
        String driverClass = node.getStringAttribute("driverClass"); 
        String connectionURL = node.getStringAttribute("connectionURL"); 

        jci.setDriverClass(driverClass);
        jci.setConnectionURL(connectionURL);

        String userId = node.getStringAttribute("userId"); 
        if (StringUtils.stringHasValue(userId)) {
            jci.setUserId(userId);
        }

        String password = node.getStringAttribute("password"); 
        if (StringUtils.stringHasValue(password)) {
            jci.setPassword(password);
        }

    }

    private void parseCommentGenerator(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        CommentGeneratorInfo cgi = new CommentGeneratorInfo();
        di.setCommentGeneratorInfo(cgi);
        String type = node.getStringAttribute("type");
        if (StringUtils.stringHasValue(type)) {
            cgi.setType(type);
        }
        parseProperties(cgi, ctx, node.evalNodes("property"));
    }

    private void parsePlugin(DomainInfo di, ConfigParserContext ctx, XMLNode node) {
        PluginInfo pi = new PluginInfo();
        di.addPluginInfo(pi);
        pi.setType(node.getStringAttribute("type"));
        parseProperties(pi, ctx, node.evalNodes("property"));
    }

    private void parseProperties(PropertyHolder propertyHolder, ConfigParserContext ctx, List<XMLNode> nodes) {
        if (nodes == null || nodes.size() <= 0) {
            return;
        }
        for (XMLNode node : nodes) {
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
