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

import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.Interface;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.codegen.mybatis.javamapper.elements.UpdateByExampleWithBLOBsMethodGenerator;

/**
 * 
 * @author Jeff Butler
 */
public class AnnotatedUpdateByExampleWithBLOBsMethodGenerator extends UpdateByExampleWithBLOBsMethodGenerator {

    public AnnotatedUpdateByExampleWithBLOBsMethodGenerator() {
        super();
    }

    @Override
    public void addMapperAnnotations(Method method) {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(introspectedTable.getMyBatis3SqlProviderType());
        StringBuilder sb = new StringBuilder();
        sb.append("@UpdateProvider(type="); 
        sb.append(fqjt.getShortName());
        sb.append(".class, method=\""); 
        sb.append(introspectedTable.getUpdateByExampleWithBLOBsStatementId());
        sb.append("\")"); 

        method.addAnnotation(sb.toString());
    }

    @Override
    public void addExtraImports(Interface interfaze) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.UpdateProvider")); 
    }
}
