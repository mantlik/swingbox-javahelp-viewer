/*
 * #%L
 * swingbox-javahelp-viewer
 * %%
 * Copyright (C) 2012 Frantisek Mantlik <frantisek at mantlik.cz>
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.mantlik.swingboxjh;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import javax.help.JHelpContentViewer;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.openide.awt.HtmlBrowser;
import org.openide.util.RequestProcessor;

/**
 *
 * @author fm
 */
class HelpBrowserHyperlinkHandler implements HyperlinkListener {

    private JHelpContentViewer viewer;
    private static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    private static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final MouseListener MOUSE_ADAPTER;
    private URL url = null;
    JEditorPane pane = null;

    ;

    public HelpBrowserHyperlinkHandler(JHelpContentViewer viewer) {
        this.viewer = viewer;
        MOUSE_ADAPTER = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (url != null) {
                        pane.setCursor(WAIT_CURSOR);
                        RequestProcessor.getDefault().post(new ViewerSetter(url, pane));
                    }
                }
            }
        };
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        JEditorPane apane = (JEditorPane) e.getSource();
        URL aurl = e.getURL();
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            pane = apane;
            url = aurl;
            pane.setCursor(HAND_CURSOR);
        } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            url = null;
            if (pane != null) {
                pane.setCursor(DEFAULT_CURSOR);
            }
        }
    }

    public MouseListener getMouseAdapter() {
        return MOUSE_ADAPTER;
    }

    protected class ViewerSetter implements Runnable {

        private final URL url;
        private final JEditorPane pane;

        private ViewerSetter(URL url, JEditorPane pane) {
            this.url = url;
            this.pane = pane;
        }

        @Override
        public void run() {
            if (url.getProtocol().equalsIgnoreCase("http")) {
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            } else {
                viewer.setCurrentURL(url);
            }
            pane.setCursor(DEFAULT_CURSOR);
        }
    }
}
