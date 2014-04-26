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

package org.solmix.api.call;

import org.solmix.api.context.Context;
import org.solmix.api.exception.SlxException;
import org.solmix.commons.collections.DataTypeMap;

/**
 * Intercept the dispatch of {@link Context} before they {@link org.solmix.api.datasource.DSRequest#execute() execute}.
 * An implementation of this class can intercept the dispatch and modify the Context and its associated Resource.
 * <p/>
 * This class can be used to implement custom protocols like Http-servlet tcp, etc.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-23
 */

public interface DSCallInterceptor
{

    public static final String DSC_STYLE = "dsc_style";

    public static final String DSC_FORMAT = "dsc_format";

    public enum Action
    {
        CONTINUE , CANCELLED
    }

    /**
     * Allow this object to configure its state when initialized.
     * 
     * @param config
     */
    void configure(DataTypeMap config);
    
    void prepareRequest(DSCall dsCall, Context context)throws SlxException;

    void inspect(DSCall dsCall, Context context) throws SlxException;

    Action postInspect(DSCall dsCall, Context context) throws SlxException;

}
