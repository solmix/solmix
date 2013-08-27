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
package org.solmix.fmk.velocity;

import org.solmix.commons.util.DataUtil;
import org.solmix.commons.util.DateUtil;
import org.solmix.fmk.util.DataTools;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-9-18
 */

public class Util 
{

    public static DateUtil date(){
        return new DateUtil();
    }
    public static DataUtil data(){
        return new DataUtil();
    }
    public static DataTools tools(){
        return new DataTools();
    }

}
