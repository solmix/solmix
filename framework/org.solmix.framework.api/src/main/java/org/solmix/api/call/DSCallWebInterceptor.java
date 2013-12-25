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

package org.solmix.api.call;

import org.solmix.api.context.Context;
import org.solmix.api.context.WebContext;
import org.solmix.api.exception.SlxException;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-24
 */

public class DSCallWebInterceptor implements DSCallInterceptor, InterceptorOrder
{

    @Override
    public void configure(DataTypeMap config) {

    }

    @Override
    public void inspect(DSCall dsCall, Context context)throws SlxException {
        if (context != null && context.getClass().isAssignableFrom(WebContext.class)) {
            inspect(dsCall, WebContext.class.cast(context));
        }

    }

    public void inspect(DSCall dsCall, WebContext context) throws SlxException{

    }

    public ReturnType postInspect(DSCall dsCall, WebContext context) throws SlxException{
        return ReturnType.CONTINUE;

    }

    @Override
    public ReturnType postInspect(DSCall dsCall, Context context) throws SlxException{
        if (context != null && context.getClass().isAssignableFrom(WebContext.class)) {
            return postInspect(dsCall, WebContext.class.cast(context));
        }
        return ReturnType.CONTINUE;

    }

    @Override
    public PRIORITY priority() {
        return InterceptorOrder.AFTER_DEFAULT;
    }
}
