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
package org.solmix.generator.codegen.mybatis.javamapper.elements.sqlprovider;

import java.util.List;

import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.api.java.TopLevelClass;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class ProviderUpdateByExampleWithBLOBsMethodGenerator extends
        ProviderUpdateByExampleWithoutBLOBsMethodGenerator {

    public ProviderUpdateByExampleWithBLOBsMethodGenerator(boolean useLegacyBuilder) {
        super(useLegacyBuilder);
    }

    @Override
    public String getMethodName() {
        return introspectedTable.getUpdateByExampleWithBLOBsStatementId();
    }

    @Override
    public List<IntrospectedColumn> getColumns() {
        return introspectedTable.getAllColumns();
    }

    @Override
    public boolean callPlugins(Method method, TopLevelClass topLevelClass) {
        return domain.getPlugins().providerUpdateByExampleWithBLOBsMethodGenerated(method, topLevelClass,
                introspectedTable);
    }
}
