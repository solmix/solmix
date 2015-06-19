/*
 * Copyright 2014 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.runtime.configpoint;

import java.io.InputStream;
import java.util.Collection;

import org.w3c.dom.Document;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月15日
 */

public interface Schema
{
    String XML_SCHEMA_EXTENSION = "xsd";

    String getName();

    String getVersion();

    String getTargetNamespace();

    String getPreferredNsPrefix();

    String[] getIncludes();

    Element getElement(String elementName);

    Collection<Element> getElements();

    /** 修改schema的elements。这个值只能由schemaSet来设置。 */
    void setElements(Collection<Element> elements);

    String getNamespacePrefix();

    String getSourceDescription();

    InputStream getInputStream();

    /**
     * 取得dom文档。
     * <p>
     * 假如文档读取失败，则返回<code>null</code>
     * ，但会打印警告日志。这样是为了避免因一个schema的错误，导致所有schema均不能装入。
     * </p>
     */
    Document getDocument();

    String getText();

    String getText(String charset);

    String getText(String charset, Transformer transformer);

    void transform(Transformer transformer);

    void transform(Transformer transformer, boolean doNow);

    interface Transformer {
        void transform(Document document, String systemId);
    }

    /** 代表schema中所定义的一个element的信息。 */
    interface Element {
        /** 取得名称。 */
        String getName();

        /** 取得文档注解。 */
        String getAnnotation();
    }
}
