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
package org.solmix.generator.codegen.mybatis.xmlmapper;

import static org.solmix.generator.util.Messages.getString;

import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.Document;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.api.FullyQualifiedTable;
import org.solmix.generator.codegen.AbstractXmlGenerator;
import org.solmix.generator.codegen.XmlConstants;
import org.solmix.generator.codegen.mybatis.xmlmapper.elements.AbstractXmlElementGenerator;
import org.solmix.generator.codegen.mybatis.xmlmapper.elements.DeleteByPrimaryKeyElementGenerator;
import org.solmix.generator.codegen.mybatis.xmlmapper.elements.InsertElementGenerator;
import org.solmix.generator.codegen.mybatis.xmlmapper.elements.ResultMapWithoutBLOBsElementGenerator;
import org.solmix.generator.codegen.mybatis.xmlmapper.elements.SimpleSelectAllElementGenerator;
import org.solmix.generator.codegen.mybatis.xmlmapper.elements.SimpleSelectByPrimaryKeyElementGenerator;
import org.solmix.generator.codegen.mybatis.xmlmapper.elements.UpdateByPrimaryKeyWithoutBLOBsElementGenerator;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class SimpleXMLMapperGenerator extends AbstractXmlGenerator {

    public SimpleXMLMapperGenerator() {
        super();
    }

    protected XmlElement getSqlMapElement() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString("Progress.12", table.toString())); 
        XmlElement answer = new XmlElement("mapper"); 
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", 
                namespace));

        domain.getCommentGenerator().addRootComment(answer);

        addResultMapElement(answer);
        addDeleteByPrimaryKeyElement(answer);
        addInsertElement(answer);
        addUpdateByPrimaryKeyElement(answer);
        addSelectByPrimaryKeyElement(answer);
        addSelectAllElement(answer);

        return answer;
    }

    protected void addResultMapElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBaseResultMap()) {
            AbstractXmlElementGenerator elementGenerator = new ResultMapWithoutBLOBsElementGenerator(
                    true);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectByPrimaryKeyElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractXmlElementGenerator elementGenerator = new SimpleSelectByPrimaryKeyElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addSelectAllElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SimpleSelectAllElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addDeleteByPrimaryKeyElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractXmlElementGenerator elementGenerator = new DeleteByPrimaryKeyElementGenerator(true);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addInsertElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractXmlElementGenerator elementGenerator = new InsertElementGenerator(true);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void addUpdateByPrimaryKeyElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementGenerator(
                    true);
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }

    protected void initializeAndExecuteGenerator(
            AbstractXmlElementGenerator elementGenerator,
            XmlElement parentElement) {
        elementGenerator.setDomain(domain);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.setProgressCallback(progressCallback);
        elementGenerator.setWarnings(warnings);
        elementGenerator.addElements(parentElement);
    }

    @Override
    public Document getDocument() {
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        document.setRootElement(getSqlMapElement());

        if (!domain.getPlugins().sqlMapDocumentGenerated(document,
                introspectedTable)) {
            document = null;
        }

        return document;
    }
}
