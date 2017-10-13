package org.solmix.generator.internal.xml;

import static org.solmix.generator.util.Messages.getString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.JavaTypeResolver;
import org.solmix.generator.config.DomainInfo;
import org.solmix.generator.config.TableInfo;
import org.solmix.generator.internal.db.ActualTableName;


public class XmlIntrospector
{

    private JavaTypeResolver javaTypeResolver;

    private List<String> warnings;

    private DomainInfo domain;

    private Logger logger;
    
    public XmlIntrospector(DomainInfo domain, JavaTypeResolver javaTypeResolver, List<String> warnings)
    {
        super();
        this.domain = domain;
        this.javaTypeResolver = javaTypeResolver;
        this.warnings = warnings;
        logger = LoggerFactory.getLogger(getClass());
    }

    public List<IntrospectedTable> introspectTables(TableInfo tc) {
        Map<ActualTableName, List<IntrospectedColumn>> columns = getColumns(tc);
        // TODO Auto-generated method stub
        return null;
    }
    public static boolean stringContainsSpace(String s) {
        return s != null && s.indexOf(' ') != -1;
    }
    private Map<ActualTableName, List<IntrospectedColumn>> getColumns(TableInfo tc) {
        String localCatalog;
        String localSchema;
        String localTableName;

        boolean delimitIdentifiers = tc.isDelimitIdentifiers() || stringContainsSpace(tc.getCatalog()) || stringContainsSpace(tc.getSchema())
            || stringContainsSpace(tc.getTableName());

            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
        Map<ActualTableName, List<IntrospectedColumn>> answer = new HashMap<ActualTableName, List<IntrospectedColumn>>();

        if (logger.isDebugEnabled()) {
            String fullTableName = TableInfo.composeFullyQualifiedTableName(localCatalog, localSchema, localTableName, '.');
            logger.debug(getString("Tracing.1", fullTableName));
        }
        return null;
    }

}
