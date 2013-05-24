/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.web.client.cache;

import org.solmix.web.client.Solmix;
import org.solmix.web.client.widgets.TopControl;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.ResultSet;

/**
 * 
 * @author administrator
 * @version $Id$ 2011-11-11
 */

public class ClientCacheImpl implements ClientCache
{

    private ResultSet userRS;

    public ClientCacheImpl()
    {
        fetchUserData();
    }

    private void fetchUserData() {
        DataSource userDS =DataSource.get("SYSTEM:customConfig");
        userRS = new ResultSet( userDS);
//        userRS.setDisableCacheSync(false);
        userDS.fetchData(null, new DSCallback() {

            public void execute(DSResponse response, Object rawData, DSRequest request) {
              
                userRS.setAllRows(response.getData());
                String userName=getCurrentUserData().getAttributeAsString("user_name");
                TopControl.welcome.setContents(Solmix.getMessages().main_username()+":"+userName);
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.cache.ClientCache#getUserData()
     */
    @Override
    public ResultSet getUserData() {

        return userRS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.cache.ClientCache#forceRefreshUserData()
     */
    @Override
    public void forceRefreshUserData() {
        fetchUserData();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.cache.ClientCache#getCurrentUserData()
     */
    @Override
    public Record getCurrentUserData() {
            return (userRS!=null&&!userRS.isEmpty())?userRS.get(0):null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.cache.ClientCache#loadFrieldData()
     */
    @Override
    public ResultSet loadFrieldData() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.cache.ClientCache#loadFrieldGroupData()
     */
    @Override
    public ResultSet loadFrieldGroupData() {
        // TODO Auto-generated method stub
        return null;
    }

}
