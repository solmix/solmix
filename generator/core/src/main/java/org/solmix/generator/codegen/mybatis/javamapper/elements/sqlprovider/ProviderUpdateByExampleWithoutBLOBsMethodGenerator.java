/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.solmix.generator.codegen.mybatis.javamapper.elements.sqlprovider;

import static org.solmix.generator.codegen.mybatis.MyBatis3FormattingUtilities.getAliasedEscapedColumnName;
import static org.solmix.generator.codegen.mybatis.MyBatis3FormattingUtilities.getParameterClause;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.solmix.commons.util.StringEscapeUtils;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.JavaVisibility;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.api.java.Parameter;
import org.solmix.generator.api.java.TopLevelClass;
import org.solmix.generator.codegen.mybatis.ListUtilities;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class ProviderUpdateByExampleWithoutBLOBsMethodGenerator extends
        AbstractJavaProviderMethodGenerator {

    public ProviderUpdateByExampleWithoutBLOBsMethodGenerator(boolean useLegacyBuilder) {
        super(useLegacyBuilder);
    }

    @Override
    public void addClassElements(TopLevelClass topLevelClass) {
        Set<String> staticImports = new TreeSet<String>();
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

        if (useLegacyBuilder) {
            staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.BEGIN"); 
            staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.UPDATE"); 
            staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.SET"); 
            staticImports.add("org.apache.ibatis.jdbc.SqlBuilder.SQL"); 
        } else {
            importedTypes.add(NEW_BUILDER_IMPORT);
        }

        importedTypes.add(new FullyQualifiedJavaType("java.util.Map")); 

        Method method = new Method(getMethodName());
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("java.util.Map<java.lang.String, java.lang.Object>"), 
                "parameter")); 
        
        domain.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        if (useLegacyBuilder) {
            method.addBodyLine("BEGIN();"); 
        } else {
            method.addBodyLine("SQL sql = new SQL();"); 
        }

        method.addBodyLine(String.format("%sUPDATE(\"%s\");", 
                builderPrefix,
                StringEscapeUtils.escapeJava(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime())));
        method.addBodyLine(""); 

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(getColumns())) {
            StringBuilder sb = new StringBuilder();
            sb.append(getParameterClause(introspectedColumn));
            sb.insert(2, "record."); 

            method.addBodyLine(String.format("%sSET(\"%s = %s\");", 
                    builderPrefix,
                    StringEscapeUtils.escapeJava(getAliasedEscapedColumnName(introspectedColumn)),
                    sb.toString()));
        }

        method.addBodyLine(""); 
        
        FullyQualifiedJavaType example =
                new FullyQualifiedJavaType(introspectedTable.getExampleType());
        importedTypes.add(example);
        method.addBodyLine(String.format("%s example = (%s) parameter.get(\"example\");", 
                example.getShortName(), example.getShortName()));

        if (useLegacyBuilder) {
            method.addBodyLine("applyWhere(example, true);"); 
            method.addBodyLine("return SQL();"); 
        } else {
            method.addBodyLine("applyWhere(sql, example, true);"); 
            method.addBodyLine("return sql.toString();"); 
        }

        if (callPlugins(method, topLevelClass)) {
            topLevelClass.addStaticImports(staticImports);
            topLevelClass.addImportedTypes(importedTypes);
            topLevelClass.addMethod(method);
        }
    }

    public String getMethodName() {
        return introspectedTable.getUpdateByExampleStatementId();
    }

    public List<IntrospectedColumn> getColumns() {
        return introspectedTable.getNonBLOBColumns();
    }

    public boolean callPlugins(Method method, TopLevelClass topLevelClass) {
        return domain.getPlugins().providerUpdateByExampleWithoutBLOBsMethodGenerated(method, topLevelClass,
                introspectedTable);
    }
}
