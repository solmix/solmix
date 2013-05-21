package com.solmix.web.box.client.ui;

import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;

public class DesktopBar extends TabBar {

	public void removeTabProtected(int idx) {
		super.removeTab(idx);

	}

	public void insertTabProtected(String tabText, boolean asHTML,
			int beforeIndex) {
		super.insertTab(tabText, asHTML, beforeIndex);

	}

	public void insertTabProtected(Widget tabWidget, int beforeIndex) {
		super.insertTab(tabWidget, beforeIndex);

	}

}
