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
package org.solmix.service.export.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月9日
 */

public class ExcelExportServiceTest
{
    @Test
    public void resultTest() throws IOException{
        File f = new File("target/excelTest.xls");
        OutputStream outs = new FileOutputStream(f);
        ExcelExportService export = new ExcelExportService();
        MappedExportContext context= new MappedExportContext();
        export.setContext(context);
        Map<Object,Object > map = new HashMap<Object,Object>();
        map.put("USER_ID", "120121");
        map.put("USER_NAME", "admin");
        map.put("MASK", "测试用户导出excle是否有乱码出现");
        List< Map<Object,Object >> l = new ArrayList< Map<Object,Object >>();
        l.add(map);
        Map<String,String > clMap = new LinkedHashMap<String,String>();
        clMap.put("USER_ID", "用户ID");
        clMap.put("USER_NAME", "用户名");
        clMap.put("MASK", "备注");
        export.exportResultSet(l, clMap,outs);
        outs.flush();
        Assert.assertNotSame(0, f.length());
    }
  
}
