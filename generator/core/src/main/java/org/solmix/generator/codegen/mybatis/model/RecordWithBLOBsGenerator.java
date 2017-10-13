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
package org.solmix.generator.codegen.mybatis.model;

import static org.solmix.generator.util.JavaBeansUtil.getJavaBeansField;
import static org.solmix.generator.util.JavaBeansUtil.getJavaBeansGetter;
import static org.solmix.generator.util.JavaBeansUtil.getJavaBeansSetter;
import static org.solmix.generator.util.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.solmix.generator.api.CommentGenerator;
import org.solmix.generator.api.FullyQualifiedTable;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.Plugin;
import org.solmix.generator.api.java.CompilationUnit;
import org.solmix.generator.api.java.Field;
import org.solmix.generator.api.java.JavaVisibility;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.api.java.Parameter;
import org.solmix.generator.api.java.TopLevelClass;
import org.solmix.generator.codegen.AbstractJavaGenerator;
import org.solmix.generator.codegen.RootClassInfo;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class RecordWithBLOBsGenerator extends AbstractJavaGenerator {

    public RecordWithBLOBsGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.9", table.toString())); 
        Plugin plugins = domain.getPlugins();
        CommentGenerator commentGenerator = domain.getCommentGenerator();

        TopLevelClass topLevelClass = new TopLevelClass(introspectedTable
                .getRecordWithBLOBsType());
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        String rootClass = getRootClass();
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            topLevelClass.setSuperClass(introspectedTable.getBaseRecordType());
        } else {
            topLevelClass.setSuperClass(introspectedTable.getPrimaryKeyType());
        }
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);

            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }
        
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getBLOBColumns()) {
            if (RootClassInfo.getInstance(rootClass, warnings)
                    .containsProperty(introspectedColumn)) {
                continue;
            }

            Field field = getJavaBeansField(introspectedColumn, domain, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.RECORD_WITH_BLOBS)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, domain, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.RECORD_WITH_BLOBS)) {
                topLevelClass.addMethod(method);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, domain, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                        introspectedColumn, introspectedTable,
                        Plugin.ModelClassType.RECORD_WITH_BLOBS)) {
                    topLevelClass.addMethod(method);
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (domain.getPlugins().modelRecordWithBLOBsClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        domain.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getAllColumns()) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }

        boolean comma = false;
        StringBuilder sb = new StringBuilder();
        sb.append("super("); 
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getNonBLOBColumns()) {
            if (comma) {
                sb.append(", "); 
            } else {
                comma = true;
            }
            sb.append(introspectedColumn.getJavaProperty());
        }
        sb.append(");"); 
        method.addBodyLine(sb.toString());
        
        for (IntrospectedColumn introspectedColumn : introspectedTable
                .getBLOBColumns()) {
            sb.setLength(0);
            sb.append("this."); 
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = "); 
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }
}
