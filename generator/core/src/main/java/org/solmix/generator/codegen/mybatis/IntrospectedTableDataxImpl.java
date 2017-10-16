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

package org.solmix.generator.codegen.mybatis;

import static org.solmix.commons.util.StringUtils.stringHasValue;

import java.util.ArrayList;
import java.util.List;

import org.solmix.commons.util.DataUtils;
import org.solmix.commons.xml.dom.Document;
import org.solmix.generator.api.GeneratedJavaFile;
import org.solmix.generator.api.GeneratedXmlFile;
import org.solmix.generator.api.ProgressCallback;
import org.solmix.generator.api.java.CompilationUnit;
import org.solmix.generator.codegen.AbstractGenerator;
import org.solmix.generator.codegen.AbstractJavaClientGenerator;
import org.solmix.generator.codegen.AbstractJavaGenerator;
import org.solmix.generator.codegen.AbstractXmlGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.DataServiceGenerator;
import org.solmix.generator.config.DataxGeneratorInfo;
import org.solmix.generator.config.PropertyHolder;
import org.solmix.generator.config.PropertyRegistry;

/**
 * @author Jeff Butler
 */
public class IntrospectedTableDataxImpl extends IntrospectedTableMyBatis3Impl
{


    protected List<AbstractJavaGenerator> dataxDaoGenerators;

    protected AbstractXmlGenerator serviceXmlMapperGenerator;

    public IntrospectedTableDataxImpl()
    {
        super(TargetRuntime.DATAX);
        dataxDaoGenerators = new ArrayList<AbstractJavaGenerator>();
    }
  
    protected String calculateDataServicePackage() {
        DataxGeneratorInfo config = domain.getDataxGeneratorInfo();
        if (config == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(config.getTargetPackage());

        sb.append(fullyQualifiedTable.getSubPackageForClientOrSqlMap(isSubPackagesEnabled(config)));

        return sb.toString();
    }
    
    private boolean isSubPackagesEnabled(PropertyHolder propertyHolder) {
        return DataUtils.asBoolean(propertyHolder.getProperty(PropertyRegistry.ANY_ENABLE_SUB_PACKAGES));
    }
    
    @Override
    protected void calculateJavaClientAttributes() {
        super.calculateJavaClientAttributes();
        if (domain.getDataxGeneratorInfo() == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
      
        sb.append(calculateDataServicePackage());
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("DAO"); 
        setDAOInterfaceType(sb.toString());
        
        
        sb.setLength(0);
        sb.append(calculateDataServicePackage());
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        setDataServiceType(sb.toString());
    }
    
    @Override
    protected void calculateXmlAttributes() {
        setMyBatis3XmlMapperFileName(calculateMyBatis3XmlMapperFileName());
        setMyBatis3XmlMapperPackage(calculateSqlMapPackage());

        setMyBatis3FallbackSqlMapNamespace(calculateMyBatis3FallbackSqlMapNamespace());

        setSqlMapFullyQualifiedRuntimeTableName(calculateSqlMapFullyQualifiedRuntimeTableName());
        setSqlMapAliasedFullyQualifiedRuntimeTableName(calculateSqlMapAliasedFullyQualifiedRuntimeTableName());

        setCountByExampleStatementId("countByExample"); 
        setDeleteByExampleStatementId("deleteByExample"); 
        setDeleteByPrimaryKeyStatementId("deleteByPrimaryKey"); 
        setInsertStatementId("add"); 
        setInsertSelectiveStatementId("addSelective"); 
        setSelectAllStatementId("selectAll"); 
        setSelectByExampleStatementId("selectByExample"); 
        setSelectByExampleWithBLOBsStatementId("selectByExampleWithBLOBs"); 
        setSelectByPrimaryKeyStatementId("selectByPrimaryKey"); 
        setUpdateByExampleStatementId("updateByExample"); 
        setUpdateByExampleSelectiveStatementId("updateByExampleSelective"); 
        setUpdateByExampleWithBLOBsStatementId("updateByExampleWithBLOBs"); 
        setUpdateByPrimaryKeyStatementId("updateByPrimaryKey"); 
        setUpdateByPrimaryKeySelectiveStatementId("updateByPrimaryKey"); 
        setUpdateByPrimaryKeyWithBLOBsStatementId("updateByPrimaryKeyWithBLOBs"); 
        setBaseResultMapId("BaseResultMap"); 
        setResultMapWithBLOBsId("ResultMapWithBLOBs"); 
        setExampleWhereClauseId("Example_Where_Clause"); 
        setBaseColumnListId("Base_Column_List"); 
        setBlobColumnListId("Blob_Column_List"); 
        setMyBatis3UpdateByExampleWhereClauseId("Update_By_Example_Where_Clause"); 
    }
    @Override
    protected String calculateMyBatis3XmlMapperFileName() {
        StringBuilder sb = new StringBuilder();
        if (stringHasValue(tableInfo.getMapperName())) {
            String mapperName = tableInfo.getMapperName();
            int ind = mapperName.lastIndexOf('.');
            if (ind == -1) {
                sb.append(mapperName);
            } else {
                sb.append(mapperName.substring(ind + 1));
            }
            sb.append(".xml"); 
        } else {
            sb.append(fullyQualifiedTable.getDomainObjectName());
            sb.append(".mapper.xml"); 
        }
        return sb.toString();
    }
    @Override
    public void calculateGenerators(List<String> warnings, ProgressCallback progressCallback) {
        super.calculateGenerators(warnings, progressCallback);
        calculateJavaDataServiceGenerators(warnings, progressCallback);


        calculateDataServiceXmlMapperGenerator( warnings, progressCallback);
    }
    
    

    private void calculateJavaDataServiceGenerators(List<String> warnings, ProgressCallback progressCallback) {
        AbstractJavaGenerator javaGenerator = new DataServiceGenerator();
        initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
        dataxDaoGenerators.add(javaGenerator);
        
    }

    protected void calculateDataServiceXmlMapperGenerator( List<String> warnings,
        ProgressCallback progressCallback) {
       /* if(domain.getDataxGeneratorInfo()!=null){
            serviceXmlMapperGenerator = new DataServiceXmlMapperGenerator();
            initializeAbstractGenerator(serviceXmlMapperGenerator, warnings, progressCallback);
        }*/
    }

 

    @Override
    protected void initializeAbstractGenerator(AbstractGenerator abstractGenerator, List<String> warnings, ProgressCallback progressCallback) {
        if (abstractGenerator == null) {
            return;
        }

        abstractGenerator.setDomain(domain);
        abstractGenerator.setIntrospectedTable(this);
        abstractGenerator.setProgressCallback(progressCallback);
        abstractGenerator.setWarnings(warnings);
    }

    @Override
    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        List<GeneratedJavaFile> answer = super.getGeneratedJavaFiles();

        for (AbstractJavaGenerator javaGenerator : dataxDaoGenerators) {
            List<CompilationUnit> compilationUnits = javaGenerator.getCompilationUnits();
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit, domain.getDataxGeneratorInfo().getTargetProject(),
                    domain.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), domain.getJavaFormatter());
                answer.add(gjf);
            }
        }


        return answer;
    }

    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = super.getGeneratedXmlFiles();

        if (serviceXmlMapperGenerator != null) {
            Document document = serviceXmlMapperGenerator.getDocument();
            GeneratedXmlFile gxf = new GeneratedXmlFile(document, getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(),
                domain.getDataxGeneratorInfo().getTargetProject(), true, domain.getXmlFormatter());
            if (domain.getPlugins().sqlMapGenerated(gxf, this)) {
                answer.add(gxf);
            }
        }

        return answer;
    }

    @Override
    public int getGenerationSteps() {
        return javaModelGenerators.size() + clientGenerators.size() + (xmlMapperGenerator == null ? 0 : 1);
    }

    @Override
    public boolean isJava5Targeted() {
        return true;
    }

    @Override
    public boolean requiresXMLGenerator() {
        AbstractJavaClientGenerator javaClientGenerator = createJavaClientGenerator();

        if (javaClientGenerator == null) {
            return false;
        } else {
            return javaClientGenerator.requiresXMLGenerator();
        }
    }
}
