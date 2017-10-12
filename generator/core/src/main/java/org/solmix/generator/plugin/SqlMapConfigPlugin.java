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
package org.solmix.generator.plugin;

import static org.solmix.commons.util.StringUtils.stringHasValue;
import static org.solmix.generator.util.Messages.getString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.Document;
import org.solmix.commons.xml.dom.TextElement;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.api.GeneratedXmlFile;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.PluginAdapter;
import org.solmix.generator.codegen.XmlConstants;

/**
 * This plugin generates an SqlMapConfig file containing sqlMap entries for 
 * all generated SQL maps. This demonstrates hooking into the code
 * generation lifecycle and generating additional XML files.
 * 
 * <p>This plugin accepts three properties:
 * 
 * <ul>
 * <li><tt>fileName</tt> (optional) the name of the generated file. this
 * defaults to "SqlMapConfig.xml" if not specified.</li>
 * <li><tt>targetPackage</tt> (required) the name of the package where the file
 * should be placed. Specified like "com.mycompany.sql".</li>
 * <li><tt>targetProject</tt> (required) the name of the project where the file
 * should be placed.</li>
 * </ul>
 * 
 * <p>Note: targetPackage and targetProject follow the same rules as the
 * targetPackage and targetProject values on the sqlMapGenerator configuration
 * element.
 * 
 * @author Jeff Butler
 * 
 */
public class SqlMapConfigPlugin extends PluginAdapter {

    private List<String> sqlMapFiles;

    public SqlMapConfigPlugin() {
        sqlMapFiles = new ArrayList<String>();
    }

    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;

        if (!stringHasValue(properties
                .getProperty("targetProject"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "SqlMapConfigPlugin", //$NON-NLS-1$
                    "targetProject")); //$NON-NLS-1$
            valid = false;
        }

        if (!stringHasValue(properties
                .getProperty("targetPackage"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "SqlMapConfigPlugin", //$NON-NLS-1$
                    "targetPackage")); //$NON-NLS-1$
            valid = false;
        }

        return valid;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        Document document = new Document(
                XmlConstants.IBATIS2_SQL_MAP_CONFIG_PUBLIC_ID,
                XmlConstants.IBATIS2_SQL_MAP_CONFIG_SYSTEM_ID);

        XmlElement root = new XmlElement("sqlMapConfig"); //$NON-NLS-1$
        document.setRootElement(root);

        root.addElement(new TextElement("<!--")); //$NON-NLS-1$
        root.addElement(new TextElement(
                "  This file is generated by MyBatis Generator.")); //$NON-NLS-1$
        root.addElement(new TextElement(
                "  This file is the shell of an SqlMapConfig file - in many cases you will need to add")); //$NON-NLS-1$
        root.addElement(new TextElement(
                "    to this file before it is usable by iBATIS.")); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        sb.append("  This file was generated on "); //$NON-NLS-1$
        sb.append(new Date());
        sb.append('.');
        root.addElement(new TextElement(sb.toString()));

        root.addElement(new TextElement("-->")); //$NON-NLS-1$

        XmlElement settings = new XmlElement("settings"); //$NON-NLS-1$
        settings.addAttribute(new Attribute("useStatementNamespaces", "true")); //$NON-NLS-1$ //$NON-NLS-2$
        root.addElement(settings);

        XmlElement sqlMap;
        for (String sqlMapFile : sqlMapFiles) {
            sqlMap = new XmlElement("sqlMap"); //$NON-NLS-1$
            sqlMap.addAttribute(new Attribute("resource", sqlMapFile)); //$NON-NLS-1$
            root.addElement(sqlMap);
        }

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties
                .getProperty("fileName", "SqlMapConfig.xml"), //$NON-NLS-1$ //$NON-NLS-2$
                properties.getProperty("targetPackage"), //$NON-NLS-1$
                properties.getProperty("targetProject"), //$NON-NLS-1$
                false, domain.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }

    /*
     * This method collects the name of every SqlMap file generated in
     * this context.
     */
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap,
            IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append(sqlMap.getTargetPackage());
        sb.append('.');
        String temp = sb.toString();
        sb.setLength(0);
        sb.append(temp.replace('.', '/'));
        sb.append(sqlMap.getFileName());
        sqlMapFiles.add(sb.toString());

        return true;
    }
}
