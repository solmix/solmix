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
import org.solmix.generator.codegen.mybatis.javamapper.elements.DeleteByPrimaryKeyMethodGenerator;

/**
 * 
 * @author Jeff Butler
 */
public class AnnotatedDeleteByPrimaryKeyMethodGenerator extends
        DeleteByPrimaryKeyMethodGenerator {

    public AnnotatedDeleteByPrimaryKeyMethodGenerator(boolean isSimple) {
        super(isSimple);
    }

    @Override
    public void addMapperAnnotations(Method method) {

        method.addAnnotation("@Delete({"); 

        StringBuilder sb = new StringBuilder();
        javaIndent(sb, 1);
        sb.append("\"delete from "); 
        sb.append(StringEscapeUtils.escapeJava(
                introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        sb.append("\","); 
        method.addAnnotation(sb.toString());

        boolean and = false;
        Iterator<IntrospectedColumn> iter = introspectedTable.getPrimaryKeyColumns().iterator();
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
            sb.append(StringEscapeUtils.escapeJava(
                    getEscapedColumnName(introspectedColumn)));
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
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Delete")); 
    }
}
