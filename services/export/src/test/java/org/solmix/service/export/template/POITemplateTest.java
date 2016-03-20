/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.service.export.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.io.CachedOutputStream;
import org.solmix.runtime.support.spring.SpringContainerFactory;
import org.solmix.service.template.TemplateContext;
import org.solmix.service.template.support.MappedTemplateContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月9日
 */

public class POITemplateTest
{
    Container container;
    XSSFTemplateEngine engine;
    
    @Test
    public void testxssf() throws IOException{
        CachedOutputStream ostream= new CachedOutputStream();
        TemplateContext tc = new MappedTemplateContext();
        Map<String,String> map = new HashMap<String,String>();
        map.put("A", "某某姓名");
        map.put("B", "2015-12-1");
        map.put("C", "56");
        tc.put("data", map);
        engine.evaluate("/template.xlsx", tc, ostream);
        File f = new File("target/test.xlsx");
        OutputStream outs = new FileOutputStream(f);
        IOUtils.copy(ostream.getInputStream(), outs);
        outs.flush();
        outs.close();
    }
    
    @Test
    public void testxwpf() throws IOException{
        XWPFTemplateEngine engine= container.getExtension(XWPFTemplateEngine.class);
        CachedOutputStream ostream= new CachedOutputStream();
        TemplateContext tc = new MappedTemplateContext();
        Map<String,String> map = new HashMap<String,String>();
        map.put("A", "某某姓名");
        map.put("B", "2015-12-1");
        map.put("C", "56");
        tc.put("data", map);
        engine.evaluate("/template.docx", tc, ostream);
        File f = new File("target/test.docx");
        OutputStream outs = new FileOutputStream(f);
        IOUtils.copy(ostream.getInputStream(), outs);
        outs.flush();
        outs.close();
    }
    
    @Before
    public void setup(){
        container= new SpringContainerFactory().createContainer("template-export.xml");
        engine= container.getExtension(XSSFTemplateEngine.class);
        Assert.assertNotNull(engine);
    }
    
    @After
    public void tearDown(){
        if(container!=null){
            container.close();
        }
    }

}
