/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
