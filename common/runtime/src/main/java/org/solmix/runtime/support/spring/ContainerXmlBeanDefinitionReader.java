/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.runtime.support.spring;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.EncodedResource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月29日
 */

public class ContainerXmlBeanDefinitionReader extends XmlBeanDefinitionReader
{
    /**
     * Exception class used to avoid reading old FastInfoset files.
     */
    private static class StaleFastinfosetException extends Exception {

        private static final long serialVersionUID = -3594973504794187383L;

    }

    // the following flag allows performance comparisons with and 
    // without fast infoset processing.
    private final boolean noFastinfoset;
    // Spring has no 'getter' for this, so we need our own copy.
    private int visibleValidationMode = VALIDATION_AUTO;
    // We need a reference to the subclass.
    private final TunedDocumentLoader tunedDocumentLoader;
    /**
     * @param beanFactory
     */
    public ContainerXmlBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        super(beanFactory);
        tunedDocumentLoader = new TunedDocumentLoader();
        this.setDocumentLoader(tunedDocumentLoader);
        noFastinfoset = System.getProperty("org.solmix.fastinfo") != null 
            || !TunedDocumentLoader.hasFastInfoSet();
    }

    @Override
    protected int doLoadBeanDefinitions(InputSource inputSource, 
                                        Resource resource) throws BeanDefinitionStoreException {
        // sadly, the Spring class we are extending has the critical function
        // getValidationModeForResource
        // marked private instead of protected, so trickery is called for here.
        boolean suppressValidation = false;
        try {
            URL url = resource.getURL();
            if (url.getFile().contains("META-INF/solmix/")) {
                suppressValidation = true;
            }
        } catch (IOException e) {
            // this space intentionally left blank.
        }
        
        int savedValidation = visibleValidationMode;
        if (suppressValidation) {
            setValidationMode(VALIDATION_NONE);
        }
        int r = super.doLoadBeanDefinitions(inputSource, resource);
        setValidationMode(savedValidation);
        return r;
    }

    @Override
    public void setValidationMode(int validationMode) {
        visibleValidationMode = validationMode;
        super.setValidationMode(validationMode);
    }

    @Override
    public int loadBeanDefinitions(final EncodedResource encodedResource)
        throws BeanDefinitionStoreException {
        if (!noFastinfoset) {
            try {
                return fastInfosetLoadBeanDefinitions(encodedResource);
            } catch (BeanDefinitionStoreException bdse) {
                throw bdse;
            } catch (Throwable e) {
                //ignore - just call the super to load them
            }
        }
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Integer>() {
                @Override
                public Integer run() throws Exception {
                    return internalLoadBeanDefinitions(encodedResource);
                }
                
            });
        } catch (PrivilegedActionException e) {
            if (e.getException() instanceof RuntimeException) {
                throw (RuntimeException)e.getException();
            }
            throw (BeanDefinitionStoreException)e.getException();
        }
    }
    
    private int internalLoadBeanDefinitions(EncodedResource encodedResource) {
        return super.loadBeanDefinitions(encodedResource);
    }
    
    private int fastInfosetLoadBeanDefinitions(EncodedResource encodedResource)
        throws IOException, StaleFastinfosetException, 
        ParserConfigurationException, XMLStreamException {
        
        URL resUrl = encodedResource.getResource().getURL();
        // There are XML files scampering around that don't end in .xml.
        // We don't apply the optimization to them.
        if (!resUrl.getPath().endsWith(".xml")) {
            throw new StaleFastinfosetException();
        }
        String fixmlPath = resUrl.getPath().replaceFirst("\\.xml$", ".fixml");
        String protocol = resUrl.getProtocol();
        // beware of the relative URL rules for jar:, which are surprising.
        if ("jar".equals(protocol)) {
            fixmlPath = fixmlPath.replaceFirst("^.*!", "");
        }
        
        URL fixmlUrl = new URL(resUrl, fixmlPath);

        // if we are in unpacked files, we take some extra time
        // to ensure that we aren't using a stale Fastinfoset file.
        if ("file".equals(protocol)) {
            URLConnection resCon = null;
            URLConnection fixCon = null;
            resCon = resUrl.openConnection();
            fixCon = fixmlUrl.openConnection();
            if (resCon.getLastModified() > fixCon.getLastModified()) {
                throw new StaleFastinfosetException();
            }
        }
        
        Resource newResource = new UrlResource(fixmlUrl); 
        Document doc = TunedDocumentLoader.loadFastinfosetDocument(fixmlUrl);
        if (doc == null) {
            //something caused FastinfoSet to not be able to read the doc
            throw new StaleFastinfosetException();
        }
        return registerBeanDefinitions(doc, newResource);
    }

   

}
