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

import java.util.Iterator;

import org.solmix.commons.util.StringEscapeUtils;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.Interface;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.codegen.mybatis.ListUtilities;
import org.solmix.generator.codegen.mybatis.javamapper.elements.UpdateByPrimaryKeyWithBLOBsMethodGenerator;

/**
 * 
 * @author Jeff Butler
 */
public class AnnotatedUpdateByPrimaryKeyWithBLOBsMethodGenerator extends UpdateByPrimaryKeyWithBLOBsMethodGenerator {

    public AnnotatedUpdateByPrimaryKeyWithBLOBsMethodGenerator() {
        super();
    }

    @Override
    public void addMapperAnnotations(Method method) {

        method.addAnnotation("@Update({"); 

        StringBuilder sb = new StringBuilder();
        javaIndent(sb, 1);
        sb.append("\"update "); 
        sb.append(StringEscapeUtils.escapeJava(introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        sb.append("\","); 
        method.addAnnotation(sb.toString());

        // set up for first column
        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append("\"set "); 

        Iterator<IntrospectedColumn> iter =
                ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())
                .iterator();
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();

            sb.append(StringEscapeUtils.escapeJava(getEscapedColumnName(introspectedColumn)));
            sb.append(" = "); 
            sb.append(getParameterClause(introspectedColumn));

            if (iter.hasNext()) {
                sb.append(',');
            }

            sb.append("\","); 
            method.addAnnotation(sb.toString());

            // set up for the next column
            if (iter.hasNext()) {
                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append("  \""); 
            }
        }

        boolean and = false;
        iter = introspectedTable.getPrimaryKeyColumns().iterator();
        while (iter.hasNext()) {
            sb.setLength(0);
            javaIndent(sb, 1);
            if (and) {
                sb.append("  \"and "); 
            } else {
                sb.append("\"where "); 
                and = true;
            }

            IntrospectedColumn introspectedColumn = iter.next();
            sb.append(StringEscapeUtils.escapeJava(getEscapedColumnName(introspectedColumn)));
            sb.append(" = "); 
            sb.append(getParameterClause(introspectedColumn));
            sb.append('\"');
            if (iter.hasNext()) {
                sb.append(',');
            }
            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})"); 
    }

    @Override
    public void addExtraImports(Interface interfaze) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Update")); 
    }
}
