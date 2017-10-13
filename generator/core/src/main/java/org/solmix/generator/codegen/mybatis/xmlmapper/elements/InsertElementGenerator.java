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
package org.solmix.generator.codegen.mybatis.xmlmapper.elements;

import java.util.ArrayList;
import java.util.List;

import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.OutputUtilities;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.TextElement;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.codegen.mybatis.ListUtilities;
import org.solmix.generator.codegen.mybatis.MyBatis3FormattingUtilities;
import org.solmix.generator.config.GeneratedKey;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class InsertElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;

    public InsertElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); 

        answer.addAttribute(new Attribute(
                "id", introspectedTable.getInsertStatementId())); 

        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(
                    introspectedTable.getBaseRecordType());
        } else {
            parameterType = introspectedTable.getRules()
                    .calculateAllFieldsClass();
        }

        answer.addAttribute(new Attribute("parameterType", 
                parameterType.getFullyQualifiedName()));

        domain.getCommentGenerator().addComment(answer);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = introspectedTable
                    .getColumn(gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                if (gk.isJdbcStandard()) {
                    answer.addAttribute(new Attribute(
                            "useGeneratedKeys", "true"));  //$NON-NLS-2$
                    answer.addAttribute(new Attribute(
                            "keyProperty", introspectedColumn.getJavaProperty())); 
                    answer.addAttribute(new Attribute(
                            "keyColumn", introspectedColumn.getActualColumnName())); 
                } else {
                    answer.addElement(getSelectKey(introspectedColumn, gk));
                }
            }
        }

        StringBuilder insertClause = new StringBuilder();

        insertClause.append("insert into "); 
        insertClause.append(introspectedTable
                .getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" ("); 

        StringBuilder valuesClause = new StringBuilder();
        valuesClause.append("values ("); 

        List<String> valuesClauses = new ArrayList<String>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            insertClause.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            valuesClause.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            if (i + 1 < columns.size()) {
                insertClause.append(", "); 
                valuesClause.append(", "); 
            }

            if (valuesClause.length() > 80) {
                answer.addElement(new TextElement(insertClause.toString()));
                insertClause.setLength(0);
                OutputUtilities.xmlIndent(insertClause, 1);

                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                OutputUtilities.xmlIndent(valuesClause, 1);
            }
        }

        insertClause.append(')');
        answer.addElement(new TextElement(insertClause.toString()));

        valuesClause.append(')');
        valuesClauses.add(valuesClause.toString());

        for (String clause : valuesClauses) {
            answer.addElement(new TextElement(clause));
        }

        if (domain.getPlugins().sqlMapInsertElementGenerated(answer,
                introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
