/**
 *    Copyright 2006-2016 the original author or authors.
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
package org.solmix.generator.codegen.mybatis.xmlmapper.elements;

import static org.solmix.commons.util.StringUtils.stringHasValue;

import java.util.Iterator;

import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.TextElement;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.codegen.mybatis.MyBatis3FormattingUtilities;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class SimpleSelectByPrimaryKeyElementGenerator extends
        AbstractXmlElementGenerator {

    public SimpleSelectByPrimaryKeyElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("select"); 

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getSelectByPrimaryKeyStatementId())); 
        answer.addAttribute(new Attribute("resultMap", 
                introspectedTable.getBaseResultMapId()));

        String parameterType;
        // PK fields are in the base class. If more than on PK
        // field, then they are coming in a map.
        if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
            parameterType = "map"; 
        } else {
            parameterType = introspectedTable.getPrimaryKeyColumns().get(0)
                    .getFullyQualifiedJavaType().toString();
        }

        answer.addAttribute(new Attribute("parameterType", 
                parameterType));

        domain.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select "); 

        if (stringHasValue(introspectedTable.getSelectByPrimaryKeyQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
            sb.append("' as QUERYID,"); 
        }

        Iterator<IntrospectedColumn> iter = introspectedTable.getAllColumns()
                .iterator();
        while (iter.hasNext()) {
            sb.append(MyBatis3FormattingUtilities.getSelectListPhrase(iter
                    .next()));

            if (iter.hasNext()) {
                sb.append(", "); 
            }

            if (sb.length() > 80) {
                answer.addElement(new TextElement(sb.toString()));
                sb.setLength(0);
            }
        }

        if (sb.length() > 0) {
            answer.addElement(new TextElement(sb.toString()));
        }

        sb.setLength(0);
        sb.append("from "); 
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and "); 
            } else {
                sb.append("where "); 
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities
                    .getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = "); 
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            answer.addElement(new TextElement(sb.toString()));
        }

        if (domain.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(
                answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
