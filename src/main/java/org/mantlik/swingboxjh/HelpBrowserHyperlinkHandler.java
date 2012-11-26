/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mantlik.swingboxjh;

import javax.help.JHelpContentViewer;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import org.fit.cssbox.swingbox.util.DefaultHyperlinkHandler;

/**
 *
 * @author fm
 */
class HelpBrowserHyperlinkHandler extends DefaultHyperlinkHandler {
    
    private JHelpContentViewer viewer;

    public HelpBrowserHyperlinkHandler(JHelpContentViewer viewer) {
        this.viewer = viewer;
    }

    @Override
    protected void loadPage(JEditorPane pane, HyperlinkEvent evt) {
        viewer.setCurrentURL(evt.getURL());
    }
    
}
