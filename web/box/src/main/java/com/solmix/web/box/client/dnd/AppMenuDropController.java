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
import com.allen_sauer.gwt.dnd.client.drop.AbstractInsertPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-16
 */

public class AppMenuDropController extends AbstractInsertPanelDropController
{

    /**
     * Label for IE quirks mode workaround.
     */
    private static final Label DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT = new Label("x");

    /**
     * @param dropTarget
     */
    public AppMenuDropController(VerticalPanel dropTarget)
    {
        super(dropTarget);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.allen_sauer.gwt.dnd.client.drop.AbstractInsertPanelDropController#getLocationWidgetComparator()
     */
    @Override
    protected LocationWidgetComparator getLocationWidgetComparator() {
        return LocationWidgetComparator.BOTTOM_HALF_COMPARATOR;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.allen_sauer.gwt.dnd.client.drop.AbstractInsertPanelDropController#newPositioner(com.allen_sauer.gwt.dnd.client.DragContext)
     */
    @Override
    protected Widget newPositioner(DragContext context) {
        // Use two widgets so that setPixelSize() consistently affects dimensions
        // excluding positioner border in quirks and strict modes
        SimplePanel outer = new SimplePanel();
        outer.addStyleName(DragClientBundle.INSTANCE.css().positioner());

        // place off screen for border calculation
        RootPanel.get().add(outer, -500, -500);

        // Ensure IE quirks mode returns valid outer.offsetHeight, and thus valid
        // DOMUtil.getVerticalBorders(outer)
        outer.setWidget(DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT);

        int width = 0;
        int height = 0;
        for (Widget widget : context.selectedWidgets) {
            width = Math.max(width, widget.getOffsetWidth());
            height += widget.getOffsetHeight();
        }

        SimplePanel inner = new SimplePanel();
        inner.setPixelSize(width - DOMUtil.getHorizontalBorders(outer), height - DOMUtil.getVerticalBorders(outer));

        outer.setWidget(inner);

        return outer;
    }

}
