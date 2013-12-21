/*
 * SOLMIX PROJECT
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
package org.solmix.jpa.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.api.context.SystemContext;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.ToperationBindings;
import org.solmix.api.jaxb.TqueryClauses;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.criterion.Criteria;
import org.solmix.fmk.datasource.AddOp;
import org.solmix.fmk.datasource.FetchOp;
import org.solmix.fmk.datasource.RemoveOp;
import org.solmix.fmk.datasource.UpdateOp;
import org.solmix.jpa.JPADataSourceBuilder;
import org.solmix.jpa.test.entity.AuthUser;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年12月11日
 */

public class JPADataSourceTest
{
    private  SystemContext sc;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        sc = SlxContext.getThreadSystemContext();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        sc.close(true);
    }

//    @Test
    public void fetchEntity()  {
        Criteria c = new Criteria("userName","superadminzz");
        try {
            JPADataSourceBuilder builder = new JPADataSourceBuilder();
            DataSource ds= builder.build(AuthUser.class,true);
            List<AuthUser> aUser= SlxContext.doInSystemContext(new FetchOp<List<AuthUser>>(ds){

                @Override
                public List<AuthUser> fetch(DSRequest request) throws SlxException {
                    DSResponse res= request.execute();
                    return res.getContext().getDataList(AuthUser.class);
                }
                
            }.withCriteria(c));
            Assert.assertNotNull(aUser);
        } catch (SlxException e) {
            e.printStackTrace();
        }
    
    }
//    @Test
    public void addByEntity()  {
        AuthUser au = new AuthUser();
        au.setUserName("superadminzz");
        au.setComments("super administrator ,this user can do anything.");
        au.setCreateDate(new Date());
        try {
            JPADataSourceBuilder builder = new JPADataSourceBuilder();
            DataSource ds= builder.build();
           
            AuthUser aUser= SlxContext.doInSystemContext(new AddOp<AuthUser>(ds){

                @Override
                public AuthUser add(DSRequest request) throws SlxException {
                    DSResponse res= request.execute();
                    return res.getContext().getData(AuthUser.class);
                }
                
            }.withValues(au));
            Assert.assertNotNull(au.getUserId());
            aUser.setUserName("superAdminzz");
            AuthUser uUser= SlxContext.doInSystemContext(new UpdateOp<AuthUser>(ds){

                @Override
                public AuthUser update(DSRequest request) throws SlxException {
                    DSResponse res= request.execute();
                    return res.getContext().getData(AuthUser.class);
                }
                
            }.withValues(au));
            Assert.assertEquals(aUser.getUserName(),"superAdminzz");
        } catch (SlxException e) {
            e.printStackTrace();
        }
       
    }
//    @Test
    public void removeByEntity() throws SlxException  {
        JPADataSourceBuilder builder = new JPADataSourceBuilder();
        DataSource ds= builder.build();
        AuthUser au = new AuthUser();
        au.setUserId(108);
        AuthUser aUser= SlxContext.doInSystemContext(new RemoveOp<AuthUser>(ds){

            @Override
            public AuthUser remove(DSRequest request) throws SlxException {
                DSResponse res= request.execute();
                return res.getContext().getData(AuthUser.class);
            }
            
        }.withCriteria(au));
        
    }
    @Test
    public void removeByJPQL()  {
        try {
            
            DataSource ds=getJPQLDataSource(Eoperation.REMOVE,"DELETE FROM AuthUser");
           
            DSResponse aUser= SlxContext.doInSystemContext(new org.solmix.fmk.datasource.RemoveOp.DfRemoveOp(ds));
            System.out.println(aUser);
//            Assert.assertNotNull(aUser.getUserId());
        } catch (SlxException e) {
            e.printStackTrace();
        }
    }
    
    private DataSource getJPQLDataSource(Eoperation operation,String jpql) throws SlxException{
        JPADataSourceBuilder builder = new JPADataSourceBuilder();
        TdataSource tds= builder.getJPATdata();
        ToperationBindings ops= new ToperationBindings();
        ToperationBinding op= new ToperationBinding();
        op.setOperationType(operation);
        TqueryClauses qc = new TqueryClauses();
        qc.setCustomQL(jpql);
        op.setQueryClauses(qc);
        ops.getOperationBinding().add(op);
        tds.setOperationBindings(ops);
        DataSourceData dsd = new DataSourceData(tds);
        return builder.build(dsd);
    }
//    @Test
    public void addByBean()  {
        Map au = new Hashtable();
        au.put("userName", "superadmin123");
        au.put("comments", "super administrator ,this user can do anything.");
        au.put("createDate", new Date());
        try {
            JPADataSourceBuilder builder = new JPADataSourceBuilder();
            DataSource ds= builder.build(AuthUser.class,false);
           
            AuthUser aUser= SlxContext.doInSystemContext(new AddOp<AuthUser>(ds){

                @Override
                public AuthUser add(DSRequest request) throws SlxException {
                    DSResponse res= request.execute();
                    return res.getContext().getData(AuthUser.class);
                }
                
            }.withValues(au));
            Assert.assertNotNull(aUser.getUserId());
        } catch (SlxException e) {
            e.printStackTrace();
        }
       
    }
//    @Test
    public void batchAddEntity()  {
        try {
            JPADataSourceBuilder builder = new JPADataSourceBuilder();
            DataSource ds= builder.build();
           
            SlxContext.doInSystemContext(new AddOp<Object>(ds){

                @Override
                public Object add(DSRequest request) throws SlxException {
                    DSResponse res= request.execute();
                    return res.getContext().getData();
                }
            }.withValues(getUsers()));
        } catch (SlxException e) {
            e.printStackTrace();
        }
    }
    
    private List<AuthUser> getUsers(){
        List<AuthUser> users= new ArrayList<AuthUser>();
        for(int i=0;i<10;i++){
            AuthUser au = new AuthUser();
            au.setUserName("superadmin"+i);
            au.setComments("super administrator ,this user can do anything.");
            au.setCreateDate(new Date());
            au.setCreateName("solmix"+i);
            au.setLastChangePassword(new Date());
            au.setPassword(UUID.randomUUID().toString());
            users.add(au);
        }
        
        return users;
    }

}
