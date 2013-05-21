
package com.solmix.web.box.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author ffz131@gmail.com
 * @version 110035 2012-2-12
 */
public class MainBox implements EntryPoint
{

    private static Messages messages;

    private static Constants constants;

    private BoxShell box;

    @Override
    public void onModuleLoad() {
        // inject CSS style resource.
        GWT.<BoxResource> create(BoxResource.class).css().ensureInjected();
        messages = GWT.create(Messages.class);
        constants = GWT.create(Constants.class);
        // set global Window style.
        Window.setMargin("0px");
        Window.enableScrolling(false);
        box = new BoxShell();
        Window.addWindowClosingHandler(new ClosingHandler() {

            @Override
            public void onWindowClosing(ClosingEvent event) {

                event.setMessage("Are you sure leave this page!");
            }

        });
        Window.addResizeHandler(new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                box.onResize();

            }

        });

        RootLayoutPanel.get().add(box);

    }

    public static Constants getConstants() {
        return constants;
    }

    public static Messages getMessages() {
        return messages;
    }
}
