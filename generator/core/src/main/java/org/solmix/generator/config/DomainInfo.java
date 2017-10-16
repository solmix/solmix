
package org.solmix.generator.config;

import static org.solmix.commons.util.StringUtils.isEmpty;
import static org.solmix.commons.util.StringUtils.stringHasValue;
import static org.solmix.generator.util.Messages.getString;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.solmix.commons.util.DataUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.api.CommentGenerator;
import org.solmix.generator.api.ConnectionFactory;
import org.solmix.generator.api.GeneratedJavaFile;
import org.solmix.generator.api.GeneratedXmlFile;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.JavaFormatter;
import org.solmix.generator.api.JavaTypeResolver;
import org.solmix.generator.api.Plugin;
import org.solmix.generator.api.ProgressCallback;
import org.solmix.generator.api.XmlFormatter;
import org.solmix.generator.internal.JdbcConnectionFactory;
import org.solmix.generator.internal.ObjectFactory;
import org.solmix.generator.internal.PluginAggregator;
import org.solmix.generator.internal.db.DatabaseIntrospector;
import org.solmix.generator.internal.xml.XmlIntrospector;

public class DomainInfo extends PropertyHolder
{

    private String id;

    private JdbcConnectionInfo jdbcConnectionInfo;

    private ConnectionFactoryInfo connectionFactoryInfo;

    private SqlMapGeneratorInfo sqlMapGeneratorInfo;

    private JavaTypeResolverInfo javaTypeResolverInfo;

    private JavaModelGeneratorInfo javaModelGeneratorInfo;

    private JavaClientGeneratorInfo javaClientGeneratorInfo;

    private ArrayList<TableInfo> tableInfos;

    private ModelType defaultModelType;

    private String beginningDelimiter = "\"";

    private String endingDelimiter = "\"";

    private CommentGeneratorInfo commentGeneratorInfo;

    private CommentGenerator commentGenerator;

    private PluginAggregator pluginAggregator;

    private List<PluginInfo> pluginInfos;

    private String targetRuntime;

    private String introspectedColumnImpl;

    private Boolean autoDelimitKeywords;

    private JavaFormatter javaFormatter;

    private XmlFormatter xmlFormatter;

    public DomainInfo(ModelType defaultModelType)
    {
        super();

        if (defaultModelType == null) {
            this.defaultModelType = ModelType.CONDITIONAL;
        } else {
            this.defaultModelType = defaultModelType;
        }

        tableInfos = new ArrayList<TableInfo>();
        pluginInfos = new ArrayList<PluginInfo>();
    }

    public void addTableInfo(TableInfo tc) {
        tableInfos.add(tc);
    }

    public JdbcConnectionInfo getJdbcConnectionInfo() {
        return jdbcConnectionInfo;
    }

    public JavaClientGeneratorInfo getJavaClientGeneratorInfo() {
        return javaClientGeneratorInfo;
    }

    public JavaModelGeneratorInfo getJavaModelGeneratorInfo() {
        return javaModelGeneratorInfo;
    }

    public JavaTypeResolverInfo getJavaTypeResolverInfo() {
        return javaTypeResolverInfo;
    }

    public SqlMapGeneratorInfo getSqlMapGeneratorInfo() {
        return sqlMapGeneratorInfo;
    }

    public void addPluginInfo(PluginInfo pluginInfo) {
        pluginInfos.add(pluginInfo);
    }

    /**
     * This method does a simple validate, it makes sure that all required fields have been filled in. It does not do
     * any more complex operations such as validating that database tables exist or validating that named columns exist
     *
     * @param errors the errors
     */
    public void validate(List<String> errors) {
        if (isEmpty(id)) {
            errors.add("domain id is empty");
        }

        /*
         * if (jdbcConnectionInfo == null && connectionFactoryInfo == null) { errors.add(getString("ValidationError.10",
         * id)); } else
         */
        if (jdbcConnectionInfo != null && connectionFactoryInfo != null) {
            // must not specify both
            errors.add("must not specify both jdbcConnection and connectionFactory");
        } else if (jdbcConnectionInfo != null) {
            jdbcConnectionInfo.validate(errors);
        } else if (connectionFactoryInfo != null) {
            connectionFactoryInfo.validate(errors);
        }

        if (javaModelGeneratorInfo == null) {
            errors.add("javaModelGenerator  is null");
        } else {
            javaModelGeneratorInfo.validate(errors, id);
        }

        if (javaClientGeneratorInfo != null) {
            javaClientGeneratorInfo.validate(errors, id);
        }

        IntrospectedTable it = null;
        try {
            it = ObjectFactory.createIntrospectedTableForValidation(this);
        } catch (Exception e) {
            errors.add(getString("ValidationError.25", id));
        }

        if (it != null && it.requiresXMLGenerator()) {
            if (sqlMapGeneratorInfo == null) {
                errors.add(getString("ValidationError.9", id));
            } else {
                sqlMapGeneratorInfo.validate(errors, id);
            }
        }

        if (tableInfos.size() == 0) {
            errors.add("<table> is empty");
        } else {
            for (int i = 0; i < tableInfos.size(); i++) {
                TableInfo tc = tableInfos.get(i);

                tc.validate(errors, i);
            }
        }

        for (PluginInfo pluginInfo : pluginInfos) {
            pluginInfo.validate(errors, id);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJavaClientGeneratorInfo(JavaClientGeneratorInfo javaClientGeneratorInfo) {
        this.javaClientGeneratorInfo = javaClientGeneratorInfo;
    }

    public void setJavaModelGeneratorInfo(JavaModelGeneratorInfo javaModelGeneratorInfo) {
        this.javaModelGeneratorInfo = javaModelGeneratorInfo;
    }

    public void setJavaTypeResolverInfo(JavaTypeResolverInfo javaTypeResolverInfo) {
        this.javaTypeResolverInfo = javaTypeResolverInfo;
    }

    public void setJdbcConnectionInfo(JdbcConnectionInfo jdbcConnectionInfo) {
        this.jdbcConnectionInfo = jdbcConnectionInfo;
    }

    public void setSqlMapGeneratorInfo(SqlMapGeneratorInfo sqlMapGeneratorInfo) {
        this.sqlMapGeneratorInfo = sqlMapGeneratorInfo;
    }

    public ModelType getDefaultModelType() {
        return defaultModelType;
    }

    /**
     * Builds an XmlElement representation of this context. Note that the XML may not necessarily validate if the
     * context is invalid. Call the <code>validate</code> method to check validity of this context.
     * 
     * @return the XML representation of this context
     */
    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("domain");

        xmlElement.addAttribute(new Attribute("id", id));

        if (defaultModelType != ModelType.CONDITIONAL) {
            xmlElement.addAttribute(new Attribute("defaultModelType", defaultModelType.getModelType()));
        }

        if (stringHasValue(introspectedColumnImpl)) {
            xmlElement.addAttribute(new Attribute("introspectedColumnImpl", introspectedColumnImpl));
        }

        if (stringHasValue(targetRuntime)) {
            xmlElement.addAttribute(new Attribute("targetRuntime", targetRuntime));
        }

        addPropertyXmlElements(xmlElement);

        for (PluginInfo pluginInfo : pluginInfos) {
            xmlElement.addElement(pluginInfo.toXmlElement());
        }

        if (commentGeneratorInfo != null) {
            xmlElement.addElement(commentGeneratorInfo.toXmlElement());
        }

        if (jdbcConnectionInfo != null) {
            xmlElement.addElement(jdbcConnectionInfo.toXmlElement());
        }

        if (connectionFactoryInfo != null) {
            xmlElement.addElement(connectionFactoryInfo.toXmlElement());
        }

        if (javaTypeResolverInfo != null) {
            xmlElement.addElement(javaTypeResolverInfo.toXmlElement());
        }

        if (javaModelGeneratorInfo != null) {
            xmlElement.addElement(javaModelGeneratorInfo.toXmlElement());
        }

        if (sqlMapGeneratorInfo != null) {
            xmlElement.addElement(sqlMapGeneratorInfo.toXmlElement());
        }

        if (javaClientGeneratorInfo != null) {
            xmlElement.addElement(javaClientGeneratorInfo.toXmlElement());
        }

        for (TableInfo tableInfo : tableInfos) {
            xmlElement.addElement(tableInfo.toXmlElement());
        }

        return xmlElement;
    }

    public List<TableInfo> getTableInfos() {
        return tableInfos;
    }

    public String getBeginningDelimiter() {
        return beginningDelimiter;
    }

    public String getEndingDelimiter() {
        return endingDelimiter;
    }

    @Override
    public void addProperty(String name, String value) {
        super.addProperty(name, value);

        if (PropertyRegistry.CONTEXT_BEGINNING_DELIMITER.equals(name)) {
            beginningDelimiter = value;
        } else if (PropertyRegistry.CONTEXT_ENDING_DELIMITER.equals(name)) {
            endingDelimiter = value;
        } else if (PropertyRegistry.CONTEXT_AUTO_DELIMIT_KEYWORDS.equals(name) && stringHasValue(value)) {
            autoDelimitKeywords = DataUtils.asBooleanObject(value);
        }
    }

    public CommentGenerator getCommentGenerator() {
        if (commentGenerator == null) {
            commentGenerator = ObjectFactory.createCommentGenerator(this);
        }

        return commentGenerator;
    }

    public JavaFormatter getJavaFormatter() {
        if (javaFormatter == null) {
            javaFormatter = ObjectFactory.createJavaFormatter(this);
        }

        return javaFormatter;
    }

    public XmlFormatter getXmlFormatter() {
        if (xmlFormatter == null) {
            xmlFormatter = ObjectFactory.createXmlFormatter(this);
        }

        return xmlFormatter;
    }

    public CommentGeneratorInfo getCommentGeneratorInfo() {
        return commentGeneratorInfo;
    }

    public void setCommentGeneratorInfo(CommentGeneratorInfo commentGeneratorInfo) {
        this.commentGeneratorInfo = commentGeneratorInfo;
    }

    public Plugin getPlugins() {
        return pluginAggregator;
    }

    public String getTargetRuntime() {
        return targetRuntime;
    }

    public void setTargetRuntime(String targetRuntime) {
        this.targetRuntime = targetRuntime;
    }

    public String getIntrospectedColumnImpl() {
        return introspectedColumnImpl;
    }

    public void setIntrospectedColumnImpl(String introspectedColumnImpl) {
        this.introspectedColumnImpl = introspectedColumnImpl;
    }

    // methods related to code generation.
    //
    // Methods should be called in this order:
    //
    // 1. getIntrospectionSteps()
    // 2. introspectTables()
    // 3. getGenerationSteps()
    // 4. generateFiles()
    //

    private List<IntrospectedTable> introspectedTables;

    private DataxGeneratorInfo dataxGeneratorInfo;

    public int getIntrospectionSteps() {
        int steps = 0;

        steps++; // connect to database

        // for each table:
        //
        // 1. Create introspected table implementation

        steps += tableInfos.size() * 1;

        return steps;
    }

    /**
     * Introspect tables based on the configuration specified in the constructor. This method is long running.
     * 
     * @param callback a progress callback if progress information is desired, or <code>null</code>
     * @param warnings any warning generated from this method will be added to the List. Warnings are always Strings.
     * @param fullyQualifiedTableNames a set of table names to generate. The elements of the set must be Strings that
     *        exactly match what's specified in the configuration. For example, if table name = "foo" and schema =
     *        "bar", then the fully qualified table name is "foo.bar". If the Set is null or empty, then all tables in
     *        the configuration will be used for code generation.
     * 
     * @throws SQLException if some error arises while introspecting the specified database tables.
     * @throws InterruptedException if the progress callback reports a cancel
     */
    public void introspectTables(ProgressCallback callback, List<String> warnings, Set<String> fullyQualifiedTableNames) throws SQLException,
        InterruptedException {

        introspectedTables = new ArrayList<IntrospectedTable>();
        JavaTypeResolver javaTypeResolver = ObjectFactory.createJavaTypeResolver(this, warnings);

        Connection connection = getConnection();
        //配置了数据库链接，需要对比数据库中配置
        if(connection!=null){
            try {
                callback.startTask(getString("Progress.0"));
                DatabaseIntrospector databaseIntrospector = new DatabaseIntrospector(this, connection.getMetaData(), javaTypeResolver, warnings);

                for (TableInfo tc : tableInfos) {
                    String tableName = TableInfo.composeFullyQualifiedTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName(), '.');

                    if (fullyQualifiedTableNames != null && fullyQualifiedTableNames.size() > 0 && !fullyQualifiedTableNames.contains(tableName)) {
                        continue;
                    }

                    if (!tc.areAnyStatementsEnabled()) {
                        warnings.add(getString("Warning.0", tableName));
                        continue;
                    }

                    callback.startTask(getString("Progress.1", tableName));
                    List<IntrospectedTable> tables = databaseIntrospector.introspectTables(tc);

                    if (tables != null) {
                        introspectedTables.addAll(tables);
                    }

                    callback.checkCancel();
                }
            } finally {
                closeConnection(connection);
            }
            //否则就直接根据配置文件生成
        }else{
            XmlIntrospector databaseIntrospector = new XmlIntrospector(this, javaTypeResolver, warnings);
            for (TableInfo tc : tableInfos) {
                String tableName = TableInfo.composeFullyQualifiedTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName(), '.');

                if (fullyQualifiedTableNames != null && fullyQualifiedTableNames.size() > 0 && !fullyQualifiedTableNames.contains(tableName)) {
                    continue;
                }

                if (!tc.areAnyStatementsEnabled()) {
                    warnings.add(getString("Warning.0", tableName));
                    continue;
                }

                callback.startTask(getString("Progress.1", tableName));
                List<IntrospectedTable> tables = databaseIntrospector.introspectTables(tc);

                if (tables != null) {
                    introspectedTables.addAll(tables);
                }

                callback.checkCancel();
            }


            callback.checkCancel();
        }
        
    }

    public int getGenerationSteps() {
        int steps = 0;

        if (introspectedTables != null) {
            for (IntrospectedTable introspectedTable : introspectedTables) {
                steps += introspectedTable.getGenerationSteps();
            }
        }

        return steps;
    }

    public void generateFiles(ProgressCallback callback, List<GeneratedJavaFile> generatedJavaFiles, List<GeneratedXmlFile> generatedXmlFiles,
        List<String> warnings) throws InterruptedException {

        pluginAggregator = new PluginAggregator();
        for (PluginInfo pluginInfo : pluginInfos) {
            Plugin plugin = ObjectFactory.createPlugin(this, pluginInfo);
            if (plugin.validate(warnings)) {
                pluginAggregator.addPlugin(plugin);
            } else {
                warnings.add(getString("Warning.24", pluginInfo.getType(), id));
            }
        }

        if (introspectedTables != null) {
            for (IntrospectedTable introspectedTable : introspectedTables) {
                callback.checkCancel();

                introspectedTable.initialize();
                introspectedTable.calculateGenerators(warnings, callback);
                generatedJavaFiles.addAll(introspectedTable.getGeneratedJavaFiles());
                generatedXmlFiles.addAll(introspectedTable.getGeneratedXmlFiles());

                generatedJavaFiles.addAll(pluginAggregator.contextGenerateAdditionalJavaFiles(introspectedTable));
                generatedXmlFiles.addAll(pluginAggregator.contextGenerateAdditionalXmlFiles(introspectedTable));
            }
        }

        generatedJavaFiles.addAll(pluginAggregator.contextGenerateAdditionalJavaFiles());
        generatedXmlFiles.addAll(pluginAggregator.contextGenerateAdditionalXmlFiles());
    }

    private Connection getConnection() throws SQLException {
        ConnectionFactory connectionFactory = null;
        if (jdbcConnectionInfo != null) {
            connectionFactory = new JdbcConnectionFactory(jdbcConnectionInfo);
        } else if (connectionFactoryInfo != null) {
            connectionFactory = ObjectFactory.createConnectionFactory(this);
        }
        if (connectionFactory != null)

            return connectionFactory.getConnection();
        else
            return null;
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    public boolean autoDelimitKeywords() {
        return autoDelimitKeywords != null && autoDelimitKeywords.booleanValue();
    }

    public ConnectionFactoryInfo getConnectionFactoryInfo() {
        return connectionFactoryInfo;
    }

    public void setConnectionFactoryInfo(ConnectionFactoryInfo connectionFactoryInfo) {
        this.connectionFactoryInfo = connectionFactoryInfo;
    }

    public void setDataxGeneratorInfo(DataxGeneratorInfo smgi) {
        this.dataxGeneratorInfo=smgi;
        
    }

    
    public DataxGeneratorInfo getDataxGeneratorInfo() {
        return dataxGeneratorInfo;
    }
    

}
