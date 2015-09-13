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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.solmix.runtime.Extension;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmx.service.template.TemplateContext;
import org.solmx.service.template.TemplateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月9日
 */
@Extension(name = "world2007")
public class XWPFTemplateEngine extends PoiAbstractTemplateEngine
{
    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        InputStreamResource stream= getInputStreamResource( templateName);
        XWPFDocument hdt = new XWPFDocument(stream.getInputStream());
        Iterator<XWPFTable> it= hdt.getTablesIterator();
        while(it.hasNext()){
            XWPFTable table = it.next();
            int rcount = table.getNumberOfRows();
            for(int i=0;i<rcount;i++){
                XWPFTableRow row=  table.getRow(i);
                List<XWPFTableCell> cells = row.getTableCells();
                for(XWPFTableCell cell:cells){
                    System.out.println(cell.getText());
                }
            }
        }
        hdt.write(ostream);
        hdt.close();
    }

    @Override
    public String[] getDefaultExtensions() {
        return new String[]{"docx"};
    }


}
