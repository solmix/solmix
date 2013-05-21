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

package com.solmix.web.box.client.ui;

import java.util.Iterator;

import com.solmix.web.box.client.util.SizeUtil;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-13
 */

public class DeskSwitchShell extends ResizeComposite implements ProvidesResize,
		HasAnimation, HasBeforeSelectionHandlers<Integer>,
		HasSelectionHandlers<Integer>, HasWidgets {

	private static DeskSwitchShell instance;

	private class ResizePanel extends VerticalPanel implements RequiresResize {

		ResizePanel() {
			setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			// this.getElement().getStyle().setBackgroundColor("green");
			autoSize(this);
		}

		private void autoSize(Widget widget) {
			int w = Window.getClientWidth();
			int h = Window.getClientHeight();
			widget.setSize((w - SizeUtil.getAppBarWidth()) + "px",
					(h - SizeUtil.getTaskBarHeight()) + "px");
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
		 */
		@Override
		public void onResize() {
			autoSize(this);
			for (Widget child : getChildren()) {
				if (child instanceof RequiresResize) {
					((RequiresResize) child).onResize();
				}
			}

		}

	}

	private class DeskBar extends HorizontalPanel {

		DeskBar() {
			setWidth("299px");
			setHeight("30px");
			getElement().getStyle().setBackgroundColor("green");
			setStyleName("slx-DeskSwitch-bar");
			setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
			add(new Label("1123"));
			add(new Label("1123"));
			add(new Label("1132"));
			add(new Label("1123"));
			add(new Label("1123"));

		}

	}

	public static final int TAB_HEIGHT = 40;

	private final FlowPanel tabBar = new FlowPanel();

	ResizePanel panel;
	DesktopBar bar = new DesktopBar();
	DesktopPanel desk = new DesktopPanel(bar);

	private DeskSwitchShell() {
//		panel = new ResizePanel();
//		panel.add(bar);
//		panel.add(desk);
//
//		panel.setCellHeight(desk, "100%");
//		tabBar.setWidth("100%");

		// panel.setWidth("100%");
		// panel.setHeight("100%");
		// panel.getElement().getStyle().setBackgroundColor("red");
		TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(1.5, Unit.EM);
		tabLayoutPanel.setAnimationDuration(1000);
		
		
		SimplePanel simplePanel = new SimplePanel();
		tabLayoutPanel.add(simplePanel, "New Widget", false);
		
		SimplePanel simplePanel_1 = new SimplePanel();
		tabLayoutPanel.add(simplePanel_1, "New Widget", false);
		initWidget(tabLayoutPanel);
//		setStyleName("gwt-TabPanel");
//		desk.setStyleName("gwt-TabPanelBottom");
//		// Add a11y role "tabpanel"
//		Accessibility.setRole(desk.getElement(), Accessibility.ROLE_TABPANEL);
//		VerticalPanel v = new VerticalPanel();
//		v.setHeight("100%");
//		v.setWidth("100%");
//		v.getElement().getStyle().setBackgroundColor("green");
//		VerticalPanel v1 = new VerticalPanel();
//		v1.setHeight("100%");
//		v1.setWidth("100%");
//		v1.getElement().getStyle().setBackgroundColor("red");
//		this.add(v, "test");
//		this.add(v1, "test123");
	}

	public void moveToLeft() {
		panel.getElement().getStyle().setLeft(0, Unit.PX);
	}

	public void moveToRight() {
		panel.getElement().getStyle()
				.setLeft(SizeUtil.getAppBarWidth(), Unit.PX);
	}

	public static DeskSwitchShell getInstance() {
		if (instance == null) {
			instance = new DeskSwitchShell();
		}
		return instance;
	}

	public void add(Widget w, String tabText) {
		insert(w, tabText, getWidgetCount());
	}

	public void add(Widget w, String tabText, boolean asHTML) {
		insert(w, tabText, asHTML, getWidgetCount());
	}

	private void insert(Widget w, String tabText, boolean asHTML,
			int beforeIndex) {
		desk.insert(w, tabText, asHTML, beforeIndex);

	}

	private void insert(Widget w, String tabText, int beforeIndex) {
		insert(w, tabText, false, beforeIndex);

	}

	public int getWidgetCount() {
		return desk.getWidgetCount();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
	 */
	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers#addBeforeSelectionHandler(com.google.gwt.event.logical.shared.BeforeSelectionHandler)
	 */
	@Override
	public HandlerRegistration addBeforeSelectionHandler(
			BeforeSelectionHandler<Integer> handler) {
		return addHandler(handler, BeforeSelectionEvent.getType());
	}

	@Override
	public boolean isAnimationEnabled() {
		return desk.isAnimationEnabled();
	}

	@Override
	public void setAnimationEnabled(boolean enable) {
		desk.setAnimationEnabled(enable);

	}

	@Override
	public void add(Widget w) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

}
