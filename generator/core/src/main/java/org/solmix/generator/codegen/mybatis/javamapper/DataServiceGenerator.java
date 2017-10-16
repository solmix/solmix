
package org.solmix.generator.codegen.mybatis.javamapper;

import static org.solmix.commons.util.StringUtils.stringHasValue;
import static org.solmix.generator.util.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.solmix.generator.api.CommentGenerator;
import org.solmix.generator.api.java.CompilationUnit;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.Interface;
import org.solmix.generator.api.java.JavaVisibility;
import org.solmix.generator.codegen.AbstractJavaGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.DeleteByPrimaryKeyMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.InsertMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.InsertSelectiveMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.SelectAllMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.SelectByPrimaryKeyMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.UpdateByPrimaryKeySelectiveMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.UpdateByPrimaryKeyWithBLOBsMethodGenerator;
import org.solmix.generator.codegen.mybatis.javamapper.elements.UpdateByPrimaryKeyWithoutBLOBsMethodGenerator;
import org.solmix.generator.config.PropertyRegistry;

public class DataServiceGenerator extends AbstractJavaGenerator
{

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = domain.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getDAOInterfaceType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addImportedType(new FullyQualifiedJavaType("org.solmix.datax.annotation.DataService"));
        interfaze.addAnnotation("@DataService(\""+introspectedTable.getDataServiceType()+"\")");
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable.getTableInfoProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = domain.getDataxGeneratorInfo().getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

        addInsertMethod(interfaze);
        addInsertSelectiveMethod(interfaze);
        addDeleteByPrimaryKeyMethod(interfaze);
        addSelectByPrimaryKeyMethod(interfaze);
        addSelectAllMethod(interfaze);
        addUpdateByPrimaryKeySelectiveMethod(interfaze);
        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);
        // addCountByExampleMethod(interfaze);
        // addDeleteByExampleMethod(interfaze);
        // addDeleteByPrimaryKeyMethod(interfaze);
        //
        // addInsertSelectiveMethod(interfaze);
        // addSelectByExampleWithBLOBsMethod(interfaze);
        // addSelectByExampleWithoutBLOBsMethod(interfaze);
        // addSelectByPrimaryKeyMethod(interfaze);
        // addUpdateByExampleSelectiveMethod(interfaze);
        // addUpdateByExampleWithBLOBsMethod(interfaze);
        // addUpdateByExampleWithoutBLOBsMethod(interfaze);
        // addUpdateByPrimaryKeySelectiveMethod(interfaze);
        // addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
        // addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (domain.getPlugins().clientGenerated(interfaze, null, introspectedTable)) {
            answer.add(interfaze);
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;

    }
    protected void addSelectAllMethod(Interface interfaze) {
        AbstractJavaMapperMethodGenerator methodGenerator = new SelectAllMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator, interfaze);
    }

    protected void addUpdateByPrimaryKeyWithBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void addUpdateByPrimaryKeyWithoutBLOBsMethod(Interface interfaze) {
        if (introspectedTable.getRules()
                .generateUpdateByPrimaryKeyWithoutBLOBs()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeyWithoutBLOBsMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }
    protected void addUpdateByPrimaryKeySelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateUpdateByPrimaryKeySelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new UpdateByPrimaryKeySelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }
    protected void addSelectByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateSelectByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new SelectByPrimaryKeyMethodGenerator(true);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }
    protected void addDeleteByPrimaryKeyMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateDeleteByPrimaryKey()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new DeleteByPrimaryKeyMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }
    
    protected void addInsertSelectiveMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsertSelective()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertSelectiveMethodGenerator();
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }


    protected void addInsertMethod(Interface interfaze) {
        if (introspectedTable.getRules().generateInsert()) {
            AbstractJavaMapperMethodGenerator methodGenerator = new InsertMethodGenerator(false);
            initializeAndExecuteGenerator(methodGenerator, interfaze);
        }
    }

    protected void initializeAndExecuteGenerator(AbstractJavaMapperMethodGenerator methodGenerator, Interface interfaze) {
        methodGenerator.setDomain(domain);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        methodGenerator.addInterfaceElements(interfaze);
    }

    public List<CompilationUnit> getExtraCompilationUnits() {
        return null;
    }
}
