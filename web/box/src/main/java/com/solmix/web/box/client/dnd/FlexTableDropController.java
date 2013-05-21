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

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-15
 */

public class FlexTableDropController extends AbstractPositioningDropController
{

    private FlexTable flexTable;

    private int[] targetPosition = new int[2];

    private Widget positioner = null;

    /**
     * @param dropTarget
     */
    public FlexTableDropController(FlexTable panel)
    {
        super(panel);
        flexTable = panel;
    }

    private InsertPanel flexTableRowsAsIndexPanel = new InsertPanel() {

        @Override
        public void add(Widget w) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Widget getWidget(int index) {
            return flexTable.getWidget(index, 0);
        }

        @Override
        public int getWidgetCount() {
            return flexTable.getRowCount();
        }

        @Override
        public int getWidgetIndex(Widget child) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void insert(Widget w, int beforeIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(int index) {
            throw new UnsupportedOperationException();
        }
    };

    @Override
    public void onDrop(DragContext context) {
        FlexTableDragController trDragController = (FlexTableDragController) context.dragController;
        // TODO
        Util.moveCell(trDragController.getDraggableTable(), flexTable, trDragController.getDraggedPosition(), targetPosition);
        super.onDrop(context);
    }

    @Override
    public void onEnter(DragContext context) {
        super.onEnter(context);
        positioner = newPositioner(context);
    }

    @Override
    public void onLeave(DragContext context) {
        positioner.removeFromParent();
        positioner = null;
        super.onLeave(context);
    }

    @Override
    public void onMove(DragContext context) {
        super.onMove(context);
        targetPosition[0] = DOMUtil.findIntersect(flexTableRowsAsIndexPanel, new CoordinateLocation(context.mouseX, context.mouseY),
            LocationWidgetComparator.BOTTOM_HALF_COMPARATOR) - 1;
        if (targetPosition[0] < 0)
            targetPosition[0] = 0;

        if (flexTable.getRowCount() > 0) {
            Widget w = flexTable.getWidget(targetPosition[0] == -1 ? 0 : targetPosition[0], 0);
            Location widgetLocation = new WidgetLocation(w, context.boundaryPanel);
            Location tableLocation = new WidgetLocation(flexTable, context.boundaryPanel);
            context.boundaryPanel.add(positioner, tableLocation.getLeft(),
                widgetLocation.getTop() + (targetPosition[0] == -1 ? 0 : w.getOffsetHeight()));
        }
    }

    Widget newPositioner(DragContext context) {
        Widget p = new SimplePanel();
        p.addStyleName("slx-flex-table-drop-positioner");
        p.setPixelSize(flexTable.getOffsetWidth(), 1);
        return p;
    }
}
