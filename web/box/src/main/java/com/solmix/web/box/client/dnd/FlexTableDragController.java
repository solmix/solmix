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
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.BoundaryDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-14
 */

public class FlexTableDragController extends PickupDragController
{

    private FlexTable draggableTable;

    private int[] draggedPosition;

    /**
     * @return the draggedPosition
     */
    public int[] getDraggedPosition() {
        return draggedPosition;
    }

    /**
     * @param boundaryPanel
     * @param allowDroppingOnBoundaryPanel
     */
    public FlexTableDragController(AbsolutePanel boundaryPanel)
    {
        super(boundaryPanel, false);
        setBehaviorDragProxy(true);
        setBehaviorMultipleSelection(false);
    }

    public void dragEnd() {
        super.dragEnd();
        draggableTable = null;
    }

    public void setBehaviorDragProxy(boolean dragProxyEnabled) {
        if (!dragProxyEnabled) {
            // TODO implement drag proxy behavior
            throw new IllegalArgumentException();
        }
        super.setBehaviorDragProxy(dragProxyEnabled);
    }

    @Override
    protected BoundaryDropController newBoundaryDropController(AbsolutePanel boundaryPanel, boolean allowDroppingOnBoundaryPanel) {
        if (allowDroppingOnBoundaryPanel) {
            throw new IllegalArgumentException();
        }
        return super.newBoundaryDropController(boundaryPanel, allowDroppingOnBoundaryPanel);
    }

    protected Widget newDragProxy(DragContext context) {
        int[] _fist = { 0, 0 };
        FlexTable proxy = new FlexTable();
        proxy.addStyleName("slx-flex-table-drag-proxy");
        draggableTable = (FlexTable) context.draggable.getParent();
        draggedPosition = getWidgetPosition(context.draggable, draggableTable);
        Util.copyCell(draggableTable, proxy, draggedPosition, _fist);
        return proxy;

    }

    private int[] getWidgetPosition(Widget widget, FlexTable flexTable) {
        int[] _return = new int[2];
        for (int row = 0; row < flexTable.getRowCount(); row++) {
            for (int col = 0; col < flexTable.getCellCount(row); col++) {
                Widget target = flexTable.getWidget(row, col);
                if (target == widget) {
                    _return[0] = row;
                    _return[1] = col;
                    return _return;
                }
            }
        }// End for cycle.
        throw new RuntimeException("Unable to determine widget row");
    }

    /**
     * @return the draggableTable
     */
    public FlexTable getDraggableTable() {
        return draggableTable;
    }

}
