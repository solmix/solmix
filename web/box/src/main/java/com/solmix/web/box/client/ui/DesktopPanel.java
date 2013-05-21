package com.solmix.web.box.client.ui;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class DesktopPanel extends ComplexPanel implements HasAnimation {

	private static class ChangeAnimation extends Animation{
		/**
		 * the {@link Element} holding the {@link Widget} with a lower index.
		 */
		private Element container1=null;
		/**
		 * the {@link Element} holding the {@link Widget} with a higher index.
		 */
		private Element container2=null;
		
		/**
		 * A boolean indicating whether container1 is move to left or right.
		 */
		private boolean right = false;
		
		private Widget oldWidget = null;
		private int fixedWidth = -1;

		public void showWidget(Widget oldWidget, Widget newWidget,
				boolean animate) {
			cancel();
			Element newContainer = getContainer(newWidget);
			int newIndex = DOM.getChildIndex(DOM.getParent(newContainer),
					newContainer);
			if (oldWidget == null) {
				UIObject.setVisible(newContainer, true);
				newWidget.setVisible(true);
				return;
			}
			this.oldWidget = oldWidget;
			// Get the container and index of the old widget
			Element oldContainer = getContainer(oldWidget);
			int oldIndex = DOM.getChildIndex(DOM.getParent(oldContainer),
					oldContainer);

			// Figure out whether move container to left or right.
			if (newIndex > oldIndex) {
				this.container1 = oldContainer;
				this.container2 = newContainer;
				this.right = false;
			} else {
				this.container1 = newContainer;
				this.container2 = oldContainer;
				this.right = true;
			}
			if (animate) {
				com.google.gwt.dom.client.Element deskElem = container1
						.getParentElement();
				int deskWidth = deskElem.getOffsetWidth();
				if (right) {
					fixedWidth = container2.getOffsetWidth();
					container2.getStyle().setPropertyPx("width",
							Math.max(1, fixedWidth - 1));
				} else {
					fixedWidth = container1.getOffsetWidth();
					container1.getStyle().setPropertyPx("width",
							Math.max(1, fixedWidth - 1));
				}
				if (deskElem.getOffsetWidth() != deskWidth) {
					fixedWidth = -1;
				}
				// TODO
				run(ANIMATION_DURATION, deskElem);
			} else {
				onInstantaneousRun();
			}
			newWidget.setVisible(true);

		}

		private void onInstantaneousRun() {
			UIObject.setVisible(container1, right);
			UIObject.setVisible(container2, !right);
			container1 = null;
			container2 = null;
			hideOldWidget();

		}

		private void hideOldWidget() {
			oldWidget.setVisible(false);
			oldWidget = null;

		}

		@Override
		protected void onComplete() {
			if (right) {
				DOM.setStyleAttribute(container1, "width", "100%");
				UIObject.setVisible(container1, false);
				UIObject.setVisible(container2, true);
				DOM.setStyleAttribute(container2, "width", "100%");
			} else {
				UIObject.setVisible(container1, true);
				DOM.setStyleAttribute(container1, "width", "100%");
				DOM.setStyleAttribute(container2, "width", "100%");
				UIObject.setVisible(container2, false);
			}
			DOM.setStyleAttribute(container1, "overflow", "visible");
			DOM.setStyleAttribute(container2, "overflow", "visible");
			container1 = null;
			container2 = null;
			hideOldWidget();
		}

		@Override
		protected void onStart() {
			// Start the animation
			DOM.setStyleAttribute(container1, "overflow", "hidden");
			DOM.setStyleAttribute(container2, "overflow", "hidden");
			onUpdate(0.0);
			UIObject.setVisible(container1, true);
			UIObject.setVisible(container2, true);
		}

		@Override
		protected void onUpdate(double progress) {
			if (!right) {
				progress = 1.0 - progress;
			}
			// container1 expands (move to left) to
			int width1;
			int width2;
			if (fixedWidth == -1) {
				width1 = (int) (progress * DOM.getElementPropertyInt(
						container1, "scrollWidth"));
				width2 = (int) ((1.0 - progress) * DOM.getElementPropertyInt(
						container2, "scrollWidth"));
			} else {
				width1 = (int) (progress * fixedWidth);
				width2 = fixedWidth - width1;
			}
			if (width1 == 0) {
				width1 = 1;
				width2 = Math.max(1, width2 - 1);
			} else if (width2 == 0) {
				width2 = 1;
				width1 = Math.max(1, width1 - 1);
			}
			DOM.setStyleAttribute(container1, "width", width1 + "px");
			DOM.setStyleAttribute(container2, "width", width2 + "px");
		}
		
	}

	/**
	 * The duration of the animation.
	 */
	private static final int ANIMATION_DURATION = 350;

	private static ChangeAnimation animation;
	private Widget visibleWidget;

	private static Element getContainer(Widget w) {
		return DOM.getParent(w.getElement());
	}

	private boolean isAnimationEnabled = false;

	private DesktopBar bar;
	public DesktopPanel(DesktopBar bar) {
		this.bar = bar;
		this.setElement(DOM.createDiv());
	}

	@Override
	public boolean isAnimationEnabled() {
		return isAnimationEnabled;
	}

	@Override
	public void setAnimationEnabled(boolean enable) {
		this.isAnimationEnabled = enable;
		
	}

	@Override
	public void add(Widget w) {
		Element container = createWidgetContainer();
		DOM.appendChild(getElement(), container);

		// The order of these methods is very important. In order to preserve
		// backward compatibility, the offsetWidth and offsetHeight of the child
		// widget should be defined (greater than zero) when w.onLoad() is
		// called.
		// As a result, we first initialize the container with a height of 0px,
		// then
		// we attach the child widget to the container. See Issue 2321 for more
		// details.
		super.add(w, container);

	    // After w.onLoad is called, it is safe to make the container invisible
		// and
		// set the height of the container and widget to 100%.
		finishWidgetInitialization(container, w);
	}

	public int getVisibleWidget() {
		return getWidgetIndex(visibleWidget);
	}
	@Override
	public boolean remove(Widget w) {
		int idx = getWidgetIndex(w);
		if (idx != -1) {
			bar.removeTabProtected(idx);
			return removeWidget(w);
		}
		return false;
	}

	public void insert(Widget w, String tabText, boolean asHTML, int beforeIndex) {
		int idx = getWidgetIndex(w);
		if (idx != -1) {
			remove(w);
			if (idx < beforeIndex) {
				beforeIndex--;
			}
		}

		bar.insertTabProtected(tabText, asHTML, beforeIndex);
		insertWidget(w, beforeIndex);
	}

	public void insert(Widget w, Widget tabWidget, boolean asHTML,
			int beforeIndex) {
		int idx = getWidgetIndex(w);
		if (idx != -1) {
			remove(w);
			if (idx < beforeIndex) {
				beforeIndex--;
			}
		}

		bar.insertTabProtected(tabWidget, beforeIndex);
		insertWidget(w, beforeIndex);
	}

	/**
	 * Shows the widget at the specified index.This cause the currently-visible
	 * widget to be hidden.
	 * 
	 * @param index
	 *            the index of the widget to be shown.
	 */
	public void showWidget(int index) {
		checkIndexBoundsForAccess(index);
		Widget oldWidget = visibleWidget;
		visibleWidget = getWidget(index);

         if(visibleWidget != oldWidget){
        	 if(animation ==null){
        		 animation = new ChangeAnimation();
        	 }
			animation.showWidget(oldWidget, visibleWidget, isAnimationEnabled
					&& isAttached());
         }
	}
	protected void insertWidget(Widget w, int beforeIndex) {
		Element container = createWidgetContainer();
		DOM.insertChild(getElement(), container, beforeIndex);
		// See add(Widget) for important comments
		insert(w, container, beforeIndex, true);
		finishWidgetInitialization(container, w);
	}

	private void finishWidgetInitialization(Element container, Widget w) {
//		UIObject.setVisible(container, false);
		DOM.setStyleAttribute(container, "height", "100%");

		// Set 100% by default.
		Element element = w.getElement();
		if (DOM.getStyleAttribute(element, "width").equals("")) {
			w.setWidth("100%");
		}
		if (DOM.getStyleAttribute(element, "height").equals("")) {
			w.setHeight("100%");
		}

		// Issue 2510: Hiding the widget isn't necessary because we hide its
		// wrapper, but it's in here for legacy support.
//		w.setVisible(false);
	}

	/**
	 * Setup the container around the widget.
	 */
	private Element createWidgetContainer() {
		Element container = DOM.createDiv();
		DOM.setStyleAttribute(container, "width", "100%");
		DOM.setStyleAttribute(container, "height", "0px");
		DOM.setStyleAttribute(container, "padding", "0px");
		DOM.setStyleAttribute(container, "margin", "0px");
		return container;
	}
	private boolean removeWidget(Widget w) {
		Element container = getContainer(w);
		boolean removed = super.remove(w);
		if (removed) {
			resetChildWidget(w);
			DOM.removeChild(getElement(), container);
			if (visibleWidget == w) {
				visibleWidget = null;
			}
		}
		return removed;
	}

	/**
	 * Reset the dimensions of the widget when it is removed
	 */
	private void resetChildWidget(Widget w) {
		w.setSize("", "");
		w.setVisible(true);

	}

}
