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

package com.solmix.web.box.client.dnd;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-15
 */

public class Util
{

    public static void copyStyle(FlexTable source, FlexTable target, int[] sp, int[] tp) {
        String styleName = source.getCellFormatter().getStyleName(sp[0], sp[1]);
        target.getCellFormatter().setStyleName(tp[0], tp[1], styleName);
    }

    public static void copyCell(FlexTable source, FlexTable target, int[] sp, int[] tp) {
        // target.insertCell(tp[0], tp[1]);
        HTML html = new HTML(source.getHTML(sp[0], sp[1]));
        target.setWidget(tp[0], tp[1], html);
        copyStyle(source, target, sp, tp);
    }

    public static void moveCell(FlexTable source, FlexTable target, int[] sp, int[] tp) {
        if (source == target && sp[0] >= tp[0]) {
            sp[0] = sp[0] + 1;
        }

        target.getRowCount();
        target.getCellCount(0);
        target.insertRow(tp[0]);
        target.insertCell(tp[0], tp[1]);
        Widget w = source.getWidget(sp[0], sp[1]);
        if (w != null) {
            target.setWidget(tp[0], tp[1], w);
        } else {
            HTML html = new HTML(source.getHTML(sp[0], sp[1]));
            target.setWidget(tp[0], tp[1], html);
        }
        copyStyle(source, target, sp, tp);
        source.removeCell(sp[0], sp[1]);
    }
}
