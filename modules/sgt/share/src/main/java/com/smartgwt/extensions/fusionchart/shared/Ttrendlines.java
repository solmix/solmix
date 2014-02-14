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

package com.smartgwt.extensions.fusionchart.shared;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-6
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Ttrendlines
{

    private List<Tline> line;

    /**
     * @return the line
     */
    public List<Tline> getLine() {
        if (line == null)
            line = new ArrayList<Tline>();
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(List<Tline> line) {
        this.line = line;
    }
}
