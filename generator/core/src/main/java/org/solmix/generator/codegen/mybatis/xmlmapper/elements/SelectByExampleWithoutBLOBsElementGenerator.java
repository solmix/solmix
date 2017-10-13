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

import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.TextElement;
import org.solmix.commons.xml.dom.XmlElement;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class SelectByExampleWithoutBLOBsElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectByExampleWithoutBLOBsElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        String fqjt = introspectedTable.getExampleType();

        XmlElement answer = new XmlElement("select"); 

        answer.addAttribute(new Attribute("id", 
                introspectedTable.getSelectByExampleStatementId()));
        answer.addAttribute(new Attribute(
                "resultMap", introspectedTable.getBaseResultMapId())); 
        answer.addAttribute(new Attribute("parameterType", fqjt)); 

        domain.getCommentGenerator().addComment(answer);

        answer.addElement(new TextElement("select")); 
        XmlElement ifElement = new XmlElement("if"); 
        ifElement.addAttribute(new Attribute("test", "distinct"));  //$NON-NLS-2$
        ifElement.addElement(new TextElement("distinct")); 
        answer.addElement(ifElement);

        StringBuilder sb = new StringBuilder();
        if (stringHasValue(introspectedTable
                .getSelectByExampleQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByExampleQueryId());
            sb.append("' as QUERYID,"); 
            answer.addElement(new TextElement(sb.toString()));
        }
        answer.addElement(getBaseColumnListElement());

        sb.setLength(0);
        sb.append("from "); 
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getExampleIncludeElement());

        ifElement = new XmlElement("if"); 
        ifElement.addAttribute(new Attribute("test", "orderByClause != null"));  //$NON-NLS-2$
        ifElement.addElement(new TextElement("order by ${orderByClause}")); 
        answer.addElement(ifElement);

        if (domain.getPlugins()
                .sqlMapSelectByExampleWithoutBLOBsElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
