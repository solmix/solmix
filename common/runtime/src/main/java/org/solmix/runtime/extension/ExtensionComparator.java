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

package org.solmix.runtime.extension;

import java.util.Comparator;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月27日
 */

public class ExtensionComparator implements Comparator<ExtensionInfo>
{

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ExtensionInfo o1, ExtensionInfo o2) {
        if ((o1.getName().equals(o2.getName()))
            && ((o1.extensionType == null && o2.extensionType == null) 
                || o1.extensionType.equals(o2.extensionType))) {
            return 0;
        }
        return -1;
    }

}
