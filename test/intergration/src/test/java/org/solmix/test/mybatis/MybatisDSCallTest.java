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
package org.solmix.test.mybatis;

import java.util.List;

import org.junit.Test;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.criterion.Criteria;
import org.solmix.fmk.datasource.AddOp;
import org.solmix.fmk.datasource.FetchOp;
import org.solmix.fmk.datasource.RemoveOp;
import org.solmix.fmk.datasource.UpdateOp;
import org.solmix.test.SolmixTestCase;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年6月14日
 */

public class MybatisDSCallTest extends SolmixTestCase
{
    @Test
    public void fetchTest() throws Throwable{
        List o= SlxContext.doInSystemContext(new FetchOp<List>("mybatis/mybatis"){

             @Override
             public List fetch(DSRequest request) throws SlxException {
                 request.getContext().setStartRow(0);
                 request.getContext().setBatchSize(12);;
                 DSResponse res= request.execute();
                 return res.getRecordList();
             }
             
         }.withCriteria(new Criteria("id", 23)));
        System.out.println(o.size());
         
     }
    @Test
    public void addTest() throws Throwable{
        Object o= SlxContext.doInSystemContext(new AddOp<Object>("mybatis/mybatis"){

             @Override
             public Object add(DSRequest request) throws SlxException {
                 request.getContext().setStartRow(0);
                 request.getContext().setBatchSize(50);;
                 DSResponse res= request.execute();
                 return res.getRawData();
             }
             
         }.withValues(new Criteria("id", 100)
         .add("title", "啥子菜单")
         .add("url", "/fetch/mybatis/mybatis")
         .add("comments", "赌博阿道夫是范德萨")));
         
     }
    @Test
    public void addUpdate() throws Throwable{
        Object o= SlxContext.doInSystemContext(new UpdateOp<Object>("mybatis/mybatis"){

             @Override
             public Object update(DSRequest request) throws SlxException {
                 request.getContext().setStartRow(0);
                 request.getContext().setBatchSize(50);;
                 DSResponse res= request.execute();
                 return res.getRawData();
             }
             
         }.withValues(new Criteria("url", "/fetch/mybatis/mybatis/menu")).withCriteria(new Criteria("id", 100)));
     }
    @Test
    public void addRemove() throws Throwable{
        Object o= SlxContext.doInSystemContext(new RemoveOp<Object>("mybatis/mybatis"){

             @Override
             public Object remove(DSRequest request) throws SlxException {
                 request.getContext().setStartRow(0);
                 request.getContext().setBatchSize(50);;
                 DSResponse res= request.execute();
                 return res.getRawData();
             }
             
         }.withCriteria(new Criteria("id", 100)));
         
     }
}
