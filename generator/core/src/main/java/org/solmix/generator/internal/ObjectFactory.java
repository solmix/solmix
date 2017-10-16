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
package org.solmix.generator.internal;

import static org.solmix.commons.util.StringUtils.isEmpty;
import static org.solmix.commons.util.StringUtils.stringHasValue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.solmix.generator.api.CommentGenerator;
import org.solmix.generator.api.ConnectionFactory;
import org.solmix.generator.api.FullyQualifiedTable;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.JavaFormatter;
import org.solmix.generator.api.JavaTypeResolver;
import org.solmix.generator.api.Plugin;
import org.solmix.generator.api.XmlFormatter;
import org.solmix.generator.api.java.DefaultJavaFormatter;
import org.solmix.generator.api.xml.DefaultXmlFormatter;
import org.solmix.generator.codegen.mybatis.IntrospectedTableDataxImpl;
import org.solmix.generator.codegen.mybatis.IntrospectedTableMyBatis3Impl;
import org.solmix.generator.config.CommentGeneratorInfo;
import org.solmix.generator.config.ConnectionFactoryInfo;
import org.solmix.generator.config.DomainInfo;
import org.solmix.generator.config.JavaTypeResolverInfo;
import org.solmix.generator.config.PluginInfo;
import org.solmix.generator.config.PropertyRegistry;
import org.solmix.generator.config.TableInfo;
import org.solmix.generator.internal.types.JavaTypeResolverDefaultImpl;

/**
 * This class creates the different objects needed by the generator.
 *
 * @author Jeff Butler
 */
public class ObjectFactory {

    private static List<ClassLoader> externalClassLoaders;

    static {
        externalClassLoaders = new ArrayList<ClassLoader>();
    }

    /**
     * Utility class. No instances allowed.
     */
    private ObjectFactory() {
        super();
    }

    /**
     * Clears the class loaders.  This method should be called at the beginning of
     * a generation run so that and change to the classloading configuration
     * will be reflected.  For example, if the eclipse launcher changes configuration
     * it might not be updated if eclipse hasn't been restarted.
     * 
     */
    public static void reset() {
        externalClassLoaders.clear();
    }

    /**
     * Adds a custom classloader to the collection of classloaders searched for "external" classes. These are classes
     * that do not depend on any of the generator's classes or interfaces. Examples are JDBC drivers, root classes, root
     * interfaces, etc.
     *
     * @param classLoader
     *            the class loader
     */
    public static synchronized void addExternalClassLoader(
            ClassLoader classLoader) {
        ObjectFactory.externalClassLoaders.add(classLoader);
    }

    /**
     * Returns a class loaded from the context classloader, or the classloader supplied by a client. This is
     * appropriate for JDBC drivers, model root classes, etc. It is not appropriate for any class that extends one of
     * the supplied classes or interfaces.
     *
     * @param type
     *            the type
     * @return the Class loaded from the external classloader
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    public static Class<?> externalClassForName(String type)
            throws ClassNotFoundException {

        Class<?> clazz;

        for (ClassLoader classLoader : externalClassLoaders) {
            try {
                clazz = Class.forName(type, true, classLoader);
                return clazz;
            } catch (Throwable e) {
                // ignore - fail safe below
            }
        }

        return internalClassForName(type);
    }

    public static Object createExternalObject(String type) {
        Object answer;

        try {
            Class<?> clazz = externalClassForName(type);
            answer = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    "RuntimeError.6", e); 
        }

        return answer;
    }

    public static Class<?> internalClassForName(String type)
            throws ClassNotFoundException {
        Class<?> clazz = null;

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            clazz = Class.forName(type, true, cl);
        } catch (Exception e) {
            // ignore - failsafe below
        }

        if (clazz == null) {
            clazz = Class.forName(type, true, ObjectFactory.class.getClassLoader());
        }

        return clazz;
    }

    public static URL getResource(String resource) {
        URL url;

        for (ClassLoader classLoader : externalClassLoaders) {
            url = classLoader.getResource(resource);
            if (url != null) {
                return url;
            }
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        url = cl.getResource(resource);

        if (url == null) {
            url = ObjectFactory.class.getClassLoader().getResource(resource);
        }

        return url;
    }

    public static Object createInternalObject(String type) {
        Object answer;

        try {
            Class<?> clazz = internalClassForName(type);

            answer = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    "RuntimeError.6", e); 

        }

        return answer;
    }

    public static JavaTypeResolver createJavaTypeResolver(DomainInfo context,
            List<String> warnings) {
        JavaTypeResolverInfo config = context.getJavaTypeResolverInfo();
        String type;

        if (config != null && config.getType() != null) {
            type = config.getType();
            if ("DEFAULT".equalsIgnoreCase(type)) { 
                type = JavaTypeResolverDefaultImpl.class.getName();
            }
        } else {
            type = JavaTypeResolverDefaultImpl.class.getName();
        }

        JavaTypeResolver answer = (JavaTypeResolver) createInternalObject(type);
        answer.setWarnings(warnings);

        if (config != null) {
            answer.addConfigurationProperties(config.getProperties());
        }

        answer.setDomain(context);

        return answer;
    }

    public static Plugin createPlugin(DomainInfo context,
            PluginInfo pluginConfiguration) {
        Plugin plugin = (Plugin) createInternalObject(pluginConfiguration
                .getType());
        plugin.setDomain(context);
        plugin.setProperties(pluginConfiguration.getProperties());
        return plugin;
    }

    public static CommentGenerator createCommentGenerator(DomainInfo context) {

        CommentGeneratorInfo config = context.getCommentGeneratorInfo();
        CommentGenerator answer;

        String type;
        if (config == null || config.getType() == null) {
            type = DefaultCommentGenerator.class.getName();
        } else {
            type = config.getType();
        }

        answer = (CommentGenerator) createInternalObject(type);

        if (config != null) {
            answer.addConfigurationProperties(config.getProperties());
        }

        return answer;
    }

    public static ConnectionFactory createConnectionFactory(DomainInfo context) {

        ConnectionFactoryInfo config = context.getConnectionFactoryInfo();
        ConnectionFactory answer;

        String type;
        if (config == null || config.getType() == null) {
            type = JdbcConnectionFactory.class.getName();
        } else {
            type = config.getType();
        }

        answer = (ConnectionFactory) createInternalObject(type);

        if (config != null) {
            answer.addConfigurationProperties(config.getProperties());
        }

        return answer;
    }

    public static JavaFormatter createJavaFormatter(DomainInfo context) {
        String type = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FORMATTER);
        if (!stringHasValue(type)) {
            type = DefaultJavaFormatter.class.getName();
        }

        JavaFormatter answer = (JavaFormatter) createInternalObject(type);

        answer.setDomain(context);

        return answer;
    }

    public static XmlFormatter createXmlFormatter(DomainInfo context) {
        String type = context.getProperty(PropertyRegistry.CONTEXT_XML_FORMATTER);
        if (!stringHasValue(type)) {
            type = DefaultXmlFormatter.class.getName();
        }

        XmlFormatter answer = (XmlFormatter) createInternalObject(type);

        answer.setDomain(context);

        return answer;
    }

    public static IntrospectedTable createIntrospectedTable(
            TableInfo tableConfiguration, FullyQualifiedTable table,
            DomainInfo context) {

        IntrospectedTable answer = createIntrospectedTableForValidation(context);
        answer.setFullyQualifiedTable(table);
        answer.setTableInfo(tableConfiguration);

        return answer;
    }

    /**
     * Creates an introspected table implementation that is only usable for validation (i.e. for a context
     * to determine if the target is ibatis2 or mybatis3).
     * 
     *
     * @param context
     *            the context
     * @return the introspected table
     */
    public static IntrospectedTable createIntrospectedTableForValidation(DomainInfo context) {
        String type = context.getTargetRuntime();
        if (isEmpty(type)) {
            type = IntrospectedTableDataxImpl.class.getName();
        } 
        if("mybatis".equalsIgnoreCase(type)){
            type = IntrospectedTableMyBatis3Impl.class.getName();
        }else  if("datax".equalsIgnoreCase(type)){
            type = IntrospectedTableDataxImpl.class.getName();
        }

        IntrospectedTable answer = (IntrospectedTable) createInternalObject(type);
        answer.setDomain(context);

        return answer;
    }

    public static IntrospectedColumn createIntrospectedColumn(DomainInfo context) {
        String type = context.getIntrospectedColumnImpl();
        if (!stringHasValue(type)) {
            type = IntrospectedColumn.class.getName();
        }

        IntrospectedColumn answer = (IntrospectedColumn) createInternalObject(type);
        answer.setDomain(context);

        return answer;
    }
}
