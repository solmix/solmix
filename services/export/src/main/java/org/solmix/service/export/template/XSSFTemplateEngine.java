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

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.service.template.TemplateContext;
import org.solmix.service.template.TemplateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月9日
 */

public class XSSFTemplateEngine extends PoiAbstractTemplateEngine
{

  
    @Override
    public String[] getDefaultExtensions() {
        return new String[] {"xlsx" };
    }

    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        InputStreamResource stream = getInputStreamResource(templateName);
        XSSFWorkbook workbook = new XSSFWorkbook(stream.getInputStream());
       for(int s=0;s< workbook.getNumberOfSheets();s++){
           XSSFSheet xsf = workbook.getSheetAt(s);
           for (int i = xsf.getFirstRowNum(); i < xsf.getLastRowNum(); i++) {
               XSSFRow row = xsf.getRow(i);
               for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                   if (row != null) {
                       XSSFCell cell = row.getCell(j);
                       String cellValue = cell.getStringCellValue();
                       if (cellValue!=null&&cellValue.startsWith("$>>")) {
                           String script = cellValue.substring(3);
                           Object value = evaluateValue(script, context);
                           if (value != null) {
                               cell.setCellValue(value.toString());
                           }
                       }

                   }
               }
           }
       }
        
        workbook.write(ostream);
        workbook.close();
    }


}
