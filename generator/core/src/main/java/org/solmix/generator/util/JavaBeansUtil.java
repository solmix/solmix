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

package org.solmix.generator.util;

import java.util.Locale;
import java.util.Properties;

import org.solmix.commons.util.DataUtils;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.java.Field;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.JavaVisibility;
import org.solmix.generator.api.java.Method;
import org.solmix.generator.api.java.Parameter;
import org.solmix.generator.config.DomainInfo;
import org.solmix.generator.config.PropertyRegistry;
import org.solmix.generator.config.TableInfo;

/**
 * @author Jeff Butler
 */
public class JavaBeansUtil
{

    private JavaBeansUtil()
    {
        super();
    }

    /**
     * Computes a getter method name. Warning - does not check to see that the property is a valid property. Call
     * getValidPropertyName first.
     * 
     * @param property the property
     * @param fullyQualifiedJavaType the fully qualified java type
     * @return the getter method name
     */
    public static String getGetterMethodName(String property, FullyQualifiedJavaType fullyQualifiedJavaType) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }

        if (fullyQualifiedJavaType.equals(FullyQualifiedJavaType.getBooleanPrimitiveInstance())) {
            sb.insert(0, "is");
        } else {
            sb.insert(0, "get");
        }

        return sb.toString();
    }

    /**
     * Computes a setter method name. Warning - does not check to see that the property is a valid property. Call
     * getValidPropertyName first.
     *
     * @param property the property
     * @return the setter method name
     */
    public static String getSetterMethodName(String property) {
        StringBuilder sb = new StringBuilder();

        sb.append(property);
        if (Character.isLowerCase(sb.charAt(0))) {
            if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            }
        }

        sb.insert(0, "set");

        return sb.toString();
    }

    public static String getCamelCaseString(String inputString, boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);

            switch (c) {
                case '_':
                case '-':
                case '@':
                case '$':
                case '#':
                case ' ':
                case '/':
                case '&':
                    if (sb.length() > 0) {
                        nextUpperCase = true;
                    }
                    break;

                default:
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                    break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    /**
     * This method ensures that the specified input string is a valid Java property name.
     * 
     * <p>
     * The rules are as follows:
     * 
     * <ol>
     * <li>If the first character is lower case, then OK</li>
     * <li>If the first two characters are upper case, then OK</li>
     * <li>If the first character is upper case, and the second character is lower case, then the first character should
     * be made lower case</li>
     * </ol>
     * 
     * <p>
     * For example:
     * 
     * <ul>
     * <li>eMail &gt; eMail</li>
     * <li>firstName &gt; firstName</li>
     * <li>URL &gt; URL</li>
     * <li>XAxis &gt; XAxis</li>
     * <li>a &gt; a</li>
     * <li>B &gt; b</li>
     * <li>Yaxis &gt; yaxis</li>
     * </ul>
     *
     * @param inputString the input string
     * @return the valid property name
     */
    public static String getValidPropertyName(String inputString) {
        String answer;

        if (inputString == null) {
            answer = null;
        } else if (inputString.length() < 2) {
            answer = inputString.toLowerCase(Locale.US);
        } else {
            if (Character.isUpperCase(inputString.charAt(0)) && !Character.isUpperCase(inputString.charAt(1))) {
                answer = inputString.substring(0, 1).toLowerCase(Locale.US) + inputString.substring(1);
            } else {
                answer = inputString;
            }
        }

        return answer;
    }

    public static Method getJavaBeansGetter(IntrospectedColumn introspectedColumn, DomainInfo context, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fqjt);
        method.setName(getGetterMethodName(property, fqjt));
        context.getCommentGenerator().addGetterComment(method, introspectedTable, introspectedColumn);

        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(property);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    public static Field getJavaBeansField(IntrospectedColumn introspectedColumn, DomainInfo context, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fqjt);
        field.setName(property);
        context.getCommentGenerator().addFieldComment(field, introspectedTable, introspectedColumn);

        return field;
    }

    public static Method getJavaBeansSetter(IntrospectedColumn introspectedColumn, DomainInfo context, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
        String property = introspectedColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(getSetterMethodName(property));
        method.addParameter(new Parameter(fqjt, property));
        context.getCommentGenerator().addSetterComment(method, introspectedTable, introspectedColumn);

        StringBuilder sb = new StringBuilder();
        if (introspectedColumn.isStringColumn() && isTrimStringsEnabled(introspectedColumn)) {
            sb.append("this.");
            sb.append(property);
            sb.append(" = ");
            sb.append(property);
            sb.append(" == null ? null : ");
            sb.append(property);
            sb.append(".trim();");
            method.addBodyLine(sb.toString());
        } else {
            sb.append("this.");
            sb.append(property);
            sb.append(" = ");
            sb.append(property);
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        return method;
    }

    private static boolean isTrimStringsEnabled(DomainInfo context) {
        Properties properties = context.getJavaModelGeneratorInfo().getProperties();
        boolean rc = DataUtils.asBoolean(properties.getProperty(PropertyRegistry.MODEL_GENERATOR_TRIM_STRINGS));
        return rc;
    }

    private static boolean isTrimStringsEnabled(IntrospectedTable table) {
        TableInfo tableConfiguration = table.getTableInfo();
        String trimSpaces = tableConfiguration.getProperties().getProperty(PropertyRegistry.MODEL_GENERATOR_TRIM_STRINGS);
        if (trimSpaces != null) {
            return DataUtils.asBoolean(trimSpaces);
        }
        return isTrimStringsEnabled(table.getDomain());
    }

    private static boolean isTrimStringsEnabled(IntrospectedColumn column) {
        String trimSpaces = column.getProperties().getProperty(PropertyRegistry.MODEL_GENERATOR_TRIM_STRINGS);
        if (trimSpaces != null) {
            return DataUtils.asBoolean(trimSpaces);
        }
        return isTrimStringsEnabled(column.getIntrospectedTable());
    }
}
