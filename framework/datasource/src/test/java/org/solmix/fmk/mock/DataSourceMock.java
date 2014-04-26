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

package org.solmix.fmk.mock;

import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.ObjectFactory;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.fmk.datasource.BasicDataSource;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-31
 */

public class DataSourceMock
{

    public BasicDataSource getBasicDataSource() {

        ObjectFactory of = new ObjectFactory();
        TdataSource tds = of.createTdataSource();
        Tfields fs = of.createTfields();
        tds.setFields(fs);
        Tfield f = of.createTfield();
        fs.getField().add(f);
        Tfield f1 = of.createTfield();
        fs.getField().add(f1);
        f.setName("test1");
        f.setType(Efield.DATE);
        f1.setName("test2");
        f1.setType(Efield.ENUM);
        tds.setID("mock");
        tds.setServerType(EserverType.JPA);
        DataSourceData context = new DataSourceData(tds);
        BasicDataSource _return = null;
        try {
            _return = new BasicDataSource();
            _return.init(context);
        } catch (SlxException e) {
            e.printStackTrace();
        }
        return _return;
    }

}
