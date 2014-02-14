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
package org.solmix.atmosphere.client;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-27
 */

public class Atmosphere extends JavaScriptObject {
 
    public static native Atmosphere create() /*-{
        return $wnd.atmosphere;
    }-*/;
    
    public final  AtmosphereRequest subscribe( AtmosphereRequestConfig requestConfig) {
        AtmosphereRequest r = subscribeImpl(requestConfig);
        r.setOutboundSerializer(requestConfig.getOutboundSerializer());
        return r;
    }
    
    public native final void unsubscribe() /*-{
      this.unsubscribe();
    }-*/;
    
    private native AtmosphereRequest subscribeImpl(AtmosphereRequestConfig requestConfig) /*-{
      return this.subscribe(requestConfig);
    }-*/;
    
    protected Atmosphere() {
    }
 
}
