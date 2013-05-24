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
package org.solmix.web.client.widgets;

import com.smartgwt.client.widgets.Canvas;


/**
 * This Class is used for Js exception.
 * @author solmix
 * @version $Id$  2011-11-7
 */

public class BridgeCanvas extends Canvas
{
    public BridgeCanvas(){
        this.setVisible(false);
    }
    
    public void onAttach(){
        super.onAttach();
        show();
    }
    public void onDetach(){
        super.onDetach();
        hide();
    }
}
