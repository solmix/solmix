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
package org.solmix.generator.codegen.mybatis.javamapper.elements.annotated;

import static org.solmix.generator.api.OutputUtilities.javaIndent;
import static org.solmix.generator.codegen.mybatis.MyBatis3FormattingUtilities.getSelectListPhrase;

import java.util.Iterator;

import org.solmix.commons.util.StringEscapeUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.Interface;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.codegen.mybatis.javamapper.elements.SelectAllMethodGenerator;
import org.solmix.generator.config.PropertyRegistry;

/**
 * 
 * @author Jeff Butler
 */
public class AnnotatedSelectAllMethodGenerator extends SelectAllMethodGenerator {
    
    public AnnotatedSelectAllMethodGenerator() {
        super();
    }

    @Override
    public void addMapperAnnotations(Interface interfaze, Method method) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select")); 

        StringBuilder sb = new StringBuilder();
        method.addAnnotation("@Select({"); 
        javaIndent(sb, 1);
        sb.append("\"select\","); 
        method.addAnnotation(sb.toString());
        
        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append('"');
        boolean hasColumns = false;
        Iterator<IntrospectedColumn> iter = introspectedTable.getAllColumns().iterator();
        while (iter.hasNext()) {
            sb.append(StringEscapeUtils.escapeJava(getSelectListPhrase(iter.next())));
            hasColumns = true;

            if (iter.hasNext()) {
                sb.append(", "); 
            }

            if (sb.length() > 80) {
                sb.append("\","); 
                method.addAnnotation(sb.toString());

                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append('"');
                hasColumns = false;
            }
        }

        if (hasColumns) {
            sb.append("\","); 
            method.addAnnotation(sb.toString());
        }
        
        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append("\"from "); 
        sb.append(StringEscapeUtils.escapeJava(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime()));
        sb.append('\"');

        String orderByClause = introspectedTable.getTableInfoProperty(
                PropertyRegistry.TABLE_SELECT_ALL_ORDER_BY_CLAUSE);
        boolean hasOrderBy = StringUtils.stringHasValue(orderByClause);
        if (hasOrderBy) {
            sb.append(',');
        }
        method.addAnnotation(sb.toString());

        if (hasOrderBy) {
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append("\"order by "); 
            sb.append(orderByClause);
            sb.append('\"');
            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})"); 

        addAnnotatedResults(interfaze, method);
    }

    private void addAnnotatedResults(Interface interfaze, Method method) {

        if (introspectedTable.isConstructorBased()) {
            method.addAnnotation("@ConstructorArgs({"); 
        } else {
            method.addAnnotation("@Results({"); 
        }

        StringBuilder sb = new StringBuilder();

        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, introspectedColumn, true,
                    introspectedTable.isConstructorBased()));
            
            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            method.addAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, introspectedColumn, false,
                    introspectedTable.isConstructorBased()));
            
            if (iterNonPk.hasNext()) {
                sb.append(',');
            }

            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})"); 
    }

    @Override
    public void addExtraImports(Interface interfaze) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType")); 
        if (introspectedTable.isConstructorBased()) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg")); 
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs")); 
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result")); 
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results")); 
        }
    }
}
