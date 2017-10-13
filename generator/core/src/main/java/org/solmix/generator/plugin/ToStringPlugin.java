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

import java.util.List;
import java.util.Properties;

import org.solmix.commons.util.DataUtils;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.PluginAdapter;
import org.solmix.generator.api.java.Field;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.JavaVisibility;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.api.java.TopLevelClass;

public class ToStringPlugin extends PluginAdapter {

    private boolean useToStringFromRoot;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        useToStringFromRoot = DataUtils.asBoolean(properties.getProperty("useToStringFromRoot"));
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    private void generateToString(IntrospectedTable introspectedTable,
            TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("toString"); 
        if (introspectedTable.isJava5Targeted()) {
            method.addAnnotation("@Override"); 
        }

        domain.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        method.addBodyLine("StringBuilder sb = new StringBuilder();"); 
        method.addBodyLine("sb.append(getClass().getSimpleName());"); 
        method.addBodyLine("sb.append(\" [\");"); 
        method.addBodyLine("sb.append(\"Hash = \").append(hashCode());"); 
        StringBuilder sb = new StringBuilder();
        for (Field field : topLevelClass.getFields()) {
            String property = field.getName();
            sb.setLength(0);
            sb.append("sb.append(\"").append(", ").append(property)  //$NON-NLS-2$
                    .append("=\")").append(".append(").append(property)  //$NON-NLS-2$
                    .append(");"); 
            method.addBodyLine(sb.toString());
        }

        method.addBodyLine("sb.append(\"]\");"); 
        if (useToStringFromRoot && topLevelClass.getSuperClass() != null) {
            method.addBodyLine("sb.append(\", from super class \");"); 
            method.addBodyLine("sb.append(super.toString());"); 
        }
        method.addBodyLine("return sb.toString();"); 

        topLevelClass.addMethod(method);
    }
}
