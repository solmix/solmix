/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.fmk.datasource;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.IOUtils;
import org.solmix.fmk.util.XMLUtil;
import org.solmix.runtime.SystemContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年12月4日
 */

public class FileSystemDataSource extends BasicDataSource
{

    private static final Logger log = LoggerFactory.getLogger(BasicDataSource.class.getName());

    @Override
    public DataSource instance(DataSourceData data) throws SlxException {
        FileSystemDataSource file = new FileSystemDataSource(sc);
        file.init(data);
        return file;
    }

    @Override
    public String getServerType() {
        return EserverType.FILESYSTEM.value();
    }

    public FileSystemDataSource(final SystemContext sc)
    {
        super(sc);
    }

    @Override
    public DSResponse executeFetch(DSRequest req) throws SlxException {
        ToperationBinding opBind = this.getContext().getOperationBinding(req);
        String url =  (String) DataUtils.getProperty("dataURL", opBind, getContext().getTdataSource());
        String xpath=(String) DataUtils.getProperty("recordXPath", opBind, getContext().getTdataSource());
        DSResponse res = new DSResponseImpl(this,req);
        Object value = getDataFromFile(sc.getBean(ClassLoader.class),url);
        if(xpath!=null){
            JXPathContext context = JXPathContext.newContext(value);
            res.setRawData(context.getValue(xpath));
        }else{
            res.setRawData(value);
        }
        
        return res;
    }

    public static  Object getDataFromFile(ClassLoader loader,String resourceName) {
        InputStream is = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            URL url = ClassLoaderUtils.getResource(loader, resourceName, FileSystemDataSource.class);
            is = url.openStream();
            Document doc = builder.parse(is);
            Element e = doc.getDocumentElement();
            return XMLUtil.getValue(e);
        } catch (Exception e) {
            log.error("Parse xml failed:", e);
        } finally {
            if (is != null)
                IOUtils.closeQuitely(is);
        }
        return null;
    }
}
