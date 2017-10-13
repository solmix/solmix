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
import static org.solmix.generator.codegen.mybatis.MyBatis3FormattingUtilities.getEscapedColumnName;
import static org.solmix.generator.codegen.mybatis.MyBatis3FormattingUtilities.getParameterClause;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.solmix.commons.util.StringEscapeUtils;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.Interface;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.codegen.mybatis.ListUtilities;
import org.solmix.generator.codegen.mybatis.javamapper.elements.InsertMethodGenerator;
import org.solmix.generator.config.GeneratedKey;

/**
 * 
 * @author Jeff Butler
 */
public class AnnotatedInsertMethodGenerator extends InsertMethodGenerator {

    public AnnotatedInsertMethodGenerator(boolean isSimple) {
        super(isSimple);
    }

    @Override
    public void addMapperAnnotations(Method method) {

        method.addAnnotation("@Insert({"); 
        StringBuilder insertClause = new StringBuilder();
        StringBuilder valuesClause = new StringBuilder();

        javaIndent(insertClause, 1);
        javaIndent(valuesClause, 1);

        insertClause.append("\"insert into "); 
        insertClause.append(StringEscapeUtils.escapeJava(introspectedTable
                .getFullyQualifiedTableNameAtRuntime()));
        insertClause.append(" ("); 

        valuesClause.append("\"values ("); 

        List<String> valuesClauses = new ArrayList<String>();
        Iterator<IntrospectedColumn> iter =
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns())
                .iterator();
        boolean hasFields = false;
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();

            insertClause.append(StringEscapeUtils.escapeJava(getEscapedColumnName(introspectedColumn)));
            valuesClause.append(getParameterClause(introspectedColumn));
            hasFields = true;
            if (iter.hasNext()) {
                insertClause.append(", "); 
                valuesClause.append(", "); 
            }

            if (valuesClause.length() > 60) {
                if (!iter.hasNext()) {
                    insertClause.append(')');
                    valuesClause.append(')');
                }
                insertClause.append("\","); 
                valuesClause.append('\"');
                if (iter.hasNext()) {
                    valuesClause.append(',');
                }

                method.addAnnotation(insertClause.toString());
                insertClause.setLength(0);
                javaIndent(insertClause, 1);
                insertClause.append('\"');

                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                javaIndent(valuesClause, 1);
                valuesClause.append('\"');
                hasFields = false;
            }
        }

        if (hasFields) {
            insertClause.append(")\","); 
            method.addAnnotation(insertClause.toString());

            valuesClause.append(")\""); 
            valuesClauses.add(valuesClause.toString());
        }

        for (String clause : valuesClauses) {
            method.addAnnotation(clause);
        }

        method.addAnnotation("})"); 

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            addGeneratedKeyAnnotation(method, gk);
        }
    }

    @Override
    public void addExtraImports(Interface interfaze) {
        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            addGeneratedKeyImports(interfaze, gk);
        }
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Insert")); 
    }
}
