
package com.solmix.sgt.client.widgets;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class Info extends com.smartgwt.client.widgets.Window
{

    public static void display(String title, String message) {

        final Info info = new Info(title, message);

        info.show();

        Timer t = new Timer() {

            @Override
            public void run() {
                info.hide();
            }
        };
        t.schedule(4000);
    }

    @Override
    public void show() {
        super.draw();
        slots.add(level, this);
    }

    @Override
    public void hide() {
        super.hide();
        slots.set(level, null);
    }

    protected Info(String title, String message)
    {

        addItem(new InfoWidget(buildHTML(title, message)));
        setWidth("300px");
        setHeight("100px");

        int root_width = Window.getClientWidth();
        int root_height = Window.getClientHeight();

        level = findAvailableLevel();

        int left = root_width - 320;
        int top = root_height - 80 - (level * 110);

        setLeft(left);
        setTop(top);

    }

    private static ArrayList<Info> slots = new ArrayList<Info>();

    private final int level;

    private String buildHTML(String title, String message) {
        return " <table><tr><td>" + title + "</td></tr> <tr> <td>" + message + "</td></tr></table>";

    }

    private static int findAvailableLevel() {
        int size = slots.size();
        for (int i = 0; i < size; i++) {
            if (slots.get(i) == null) {
                return i;
            }
        }
        return size;
    }
}
