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

import static org.solmix.commons.util.DataUtils.asBoolean;
import static org.solmix.generator.util.JavaBeansUtil.getGetterMethodName;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.OutputUtilities;
import org.solmix.generator.api.PluginAdapter;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.JavaVisibility;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.api.java.Parameter;
import org.solmix.generator.api.java.TopLevelClass;

/**
 * This plugin adds equals() and hashCode() methods to the generated model
 * classes. It demonstrates the process of adding methods to generated classes
 * 
 * <p>The <tt>equals</tt> method generated by this class is correct in most cases,
 * but will probably NOT be correct if you have specified a rootClass - because
 * our equals method only checks the fields it knows about.
 * 
 * <p>Similarly, the <tt>hashCode</tt> method generated by this class only relies
 * on fields it knows about. Anything you add, or fields in a super class will
 * not be factored into the hash code.
 * 
 * @author Jeff Butler
 * 
 */
public class EqualsHashCodePlugin extends PluginAdapter {

    private boolean useEqualsHashCodeFromRoot;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        useEqualsHashCodeFromRoot = asBoolean(properties.getProperty("useEqualsHashCodeFromRoot"));
    }

    /**
     * This plugin is always valid - no properties are required.
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> columns;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            columns = introspectedTable.getNonBLOBColumns();
        } else {
            columns = introspectedTable.getAllColumns();
        }

        generateEquals(topLevelClass, columns, introspectedTable);
        generateHashCode(topLevelClass, columns, introspectedTable);

        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        generateEquals(topLevelClass, introspectedTable.getPrimaryKeyColumns(),
                introspectedTable);
        generateHashCode(topLevelClass, introspectedTable
                .getPrimaryKeyColumns(), introspectedTable);

        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateEquals(topLevelClass, introspectedTable.getAllColumns(),
                introspectedTable);
        generateHashCode(topLevelClass, introspectedTable.getAllColumns(),
                introspectedTable);

        return true;
    }

    /**
     * Generates an <tt>equals</tt> method that does a comparison of all fields.
     * 
     * <p>The generated <tt>equals</tt> method will be correct unless:
     * 
     * <ul>
     *   <li>Other fields have been added to the generated classes</li>
     *   <li>A <tt>rootClass</tt> is specified that holds state</li>
     * </ul>
     * 
     * @param topLevelClass
     *            the class to which the method will be added
     * @param introspectedColumns
     *            column definitions of this class and any superclass of this
     *            class
     * @param introspectedTable
     *            the table corresponding to this class
     */
    protected void generateEquals(TopLevelClass topLevelClass,
            List<IntrospectedColumn> introspectedColumns,
            IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType
                .getBooleanPrimitiveInstance());
        method.setName("equals"); 
        method.addParameter(new Parameter(FullyQualifiedJavaType
                .getObjectInstance(), "that")); 
        if (introspectedTable.isJava5Targeted()) {
            method.addAnnotation("@Override"); 
        }

        domain.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        method.addBodyLine("if (this == that) {"); 
        method.addBodyLine("return true;"); 
        method.addBodyLine("}"); 

        method.addBodyLine("if (that == null) {"); 
        method.addBodyLine("return false;"); 
        method.addBodyLine("}"); 

        method.addBodyLine("if (getClass() != that.getClass()) {"); 
        method.addBodyLine("return false;"); 
        method.addBodyLine("}"); 

        StringBuilder sb = new StringBuilder();
        sb.append(topLevelClass.getType().getShortName());
        sb.append(" other = ("); 
        sb.append(topLevelClass.getType().getShortName());
        sb.append(") that;"); 
        method.addBodyLine(sb.toString());

        if (useEqualsHashCodeFromRoot && topLevelClass.getSuperClass() != null) {
            method.addBodyLine("if (!super.equals(other)) {"); 
            method.addBodyLine("return false;"); 
            method.addBodyLine("}"); 
        }

        boolean first = true;
        Iterator<IntrospectedColumn> iter = introspectedColumns.iterator();
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();

            sb.setLength(0);

            if (first) {
                sb.append("return ("); 
                first = false;
            } else {
                OutputUtilities.javaIndent(sb, 1);
                sb.append("&& ("); 
            }

            String getterMethod = getGetterMethodName(
                    introspectedColumn.getJavaProperty(), introspectedColumn
                            .getFullyQualifiedJavaType());

            if (introspectedColumn.getFullyQualifiedJavaType().isPrimitive()) {
                sb.append("this."); 
                sb.append(getterMethod);
                sb.append("() == "); 
                sb.append("other."); 
                sb.append(getterMethod);
                sb.append("())"); 
            } else if (introspectedColumn.getFullyQualifiedJavaType().isArray()) {
                topLevelClass.addImportedType("java.util.Arrays"); 
                sb.append("Arrays.equals(this."); 
                sb.append(getterMethod);
                sb.append("(), "); 
                sb.append("other."); 
                sb.append(getterMethod);
                sb.append("()))"); 
            } else {
                sb.append("this."); 
                sb.append(getterMethod);
                sb.append("() == null ? other."); 
                sb.append(getterMethod);
                sb.append("() == null : this."); 
                sb.append(getterMethod);
                sb.append("().equals(other."); 
                sb.append(getterMethod);
                sb.append("()))"); 
            }

            if (!iter.hasNext()) {
                sb.append(';');
            }

            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }

    /**
     * Generates a <tt>hashCode</tt> method that includes all fields.
     * 
     * <p>Note that this implementation is based on the eclipse foundation hashCode
     * generator.
     * 
     * @param topLevelClass
     *            the class to which the method will be added
     * @param introspectedColumns
     *            column definitions of this class and any superclass of this
     *            class
     * @param introspectedTable
     *            the table corresponding to this class
     */
    protected void generateHashCode(TopLevelClass topLevelClass,
            List<IntrospectedColumn> introspectedColumns,
            IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("hashCode"); 
        if (introspectedTable.isJava5Targeted()) {
            method.addAnnotation("@Override"); 
        }

        domain.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        method.addBodyLine("final int prime = 31;"); 
        method.addBodyLine("int result = 1;"); 

        if (useEqualsHashCodeFromRoot && topLevelClass.getSuperClass() != null) {
            method.addBodyLine("result = prime * result + super.hashCode();"); 
        }

        StringBuilder sb = new StringBuilder();
        boolean hasTemp = false;
        Iterator<IntrospectedColumn> iter = introspectedColumns.iterator();
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();

            FullyQualifiedJavaType fqjt = introspectedColumn
                    .getFullyQualifiedJavaType();

            String getterMethod = getGetterMethodName(
                    introspectedColumn.getJavaProperty(), fqjt);

            sb.setLength(0);
            if (fqjt.isPrimitive()) {
                if ("boolean".equals(fqjt.getFullyQualifiedName())) { 
                    sb.append("result = prime * result + ("); 
                    sb.append(getterMethod);
                    sb.append("() ? 1231 : 1237);"); 
                    method.addBodyLine(sb.toString());
                } else if ("byte".equals(fqjt.getFullyQualifiedName())) { 
                    sb.append("result = prime * result + "); 
                    sb.append(getterMethod);
                    sb.append("();"); 
                    method.addBodyLine(sb.toString());
                } else if ("char".equals(fqjt.getFullyQualifiedName())) { 
                    sb.append("result = prime * result + "); 
                    sb.append(getterMethod);
                    sb.append("();"); 
                    method.addBodyLine(sb.toString());
                } else if ("double".equals(fqjt.getFullyQualifiedName())) { 
                    if (!hasTemp) {
                        method.addBodyLine("long temp;"); 
                        hasTemp = true;
                    }
                    sb.append("temp = Double.doubleToLongBits("); 
                    sb.append(getterMethod);
                    sb.append("());"); 
                    method.addBodyLine(sb.toString());
                    method
                            .addBodyLine("result = prime * result + (int) (temp ^ (temp >>> 32));"); 
                } else if ("float".equals(fqjt.getFullyQualifiedName())) { 
                    sb
                            .append("result = prime * result + Float.floatToIntBits("); 
                    sb.append(getterMethod);
                    sb.append("());"); 
                    method.addBodyLine(sb.toString());
                } else if ("int".equals(fqjt.getFullyQualifiedName())) { 
                    sb.append("result = prime * result + "); 
                    sb.append(getterMethod);
                    sb.append("();"); 
                    method.addBodyLine(sb.toString());
                } else if ("long".equals(fqjt.getFullyQualifiedName())) { 
                    sb.append("result = prime * result + (int) ("); 
                    sb.append(getterMethod);
                    sb.append("() ^ ("); 
                    sb.append(getterMethod);
                    sb.append("() >>> 32));"); 
                    method.addBodyLine(sb.toString());
                } else if ("short".equals(fqjt.getFullyQualifiedName())) { 
                    sb.append("result = prime * result + "); 
                    sb.append(getterMethod);
                    sb.append("();"); 
                    method.addBodyLine(sb.toString());
                } else {
                    // should never happen
                    continue;
                }
            } else if (fqjt.isArray()) {
                // Arrays is already imported by the generateEquals method, we don't need
                // to do it again
                sb.append("result = prime * result + (Arrays.hashCode("); 
                sb.append(getterMethod);
                sb.append("()));"); 
                method.addBodyLine(sb.toString());
            } else {
                sb.append("result = prime * result + (("); 
                sb.append(getterMethod);
                sb.append("() == null) ? 0 : "); 
                sb.append(getterMethod);
                sb.append("().hashCode());"); 
                method.addBodyLine(sb.toString());
            }
        }

        method.addBodyLine("return result;"); 

        topLevelClass.addMethod(method);
    }
}
