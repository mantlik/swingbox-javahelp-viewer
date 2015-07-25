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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Locale;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.InvalidHelpSetContextException;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.help.Map;
import javax.help.Map.ID;
import javax.help.TextHelpModel;
import javax.help.TextHelpModel.Highlight;
import javax.help.event.HelpModelEvent;
import javax.help.event.HelpModelListener;
import javax.help.event.TextHelpModelEvent;
import javax.help.event.TextHelpModelListener;
import javax.help.plaf.HelpContentViewerUI;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.EditorKit;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.fit.cssbox.swingbox.BrowserPane;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Javahelp component viewer based on SwingBox viewer
 *
 * @author fm
 */
public class SwingboxContentViewerUI extends HelpContentViewerUI
        implements HelpModelListener, TextHelpModelListener, PropertyChangeListener,
        Serializable {

    private static Logger log = LoggerFactory.getLogger(SwingboxContentViewerUI.class);
    protected JHelpContentViewer theViewer = null;
    private static Dimension PREF_SIZE = new Dimension(100, 100);
    private static Dimension MIN_SIZE = new Dimension(80, 80);
    private static final HighlightPainter HIGHLIGHT_PAINTER = new DefaultHighlightPainter(Color.ORANGE);
    private static final String A_XERCES_CLASS_NAME = "org.apache.xerces.parsers.DOMParser";
    private JEditorPane html;
    private JViewport vp;
    private transient HelpBrowserHyperlinkHandler hyperlinkListener;
    private String textDocument;
    private Component scroller;
    private LayoutManager layout;

    public static ComponentUI createUI(JComponent x) {
        debug("create UI");
        if (!(x instanceof JHelpContentViewer)) {
            throw new ClassCastException("Invalid argument of type " + x.getClass().getName() + ". Expected JHelpContentViewer.");
        } else {
            return new SwingboxContentViewerUI((JHelpContentViewer) x);
        }
    }

    public SwingboxContentViewerUI(JHelpContentViewer b) {
        debug("create UI instance");
    }

    @Override
    public void installUI(JComponent c) {
        debug("installUI");
        if (!(c instanceof JHelpContentViewer)) {
            throw new ClassCastException("Invalid argument of type " + c.getClass().getName() + ". Expected JHelpContentViewer.");
        } else {
            theViewer = (JHelpContentViewer) c;
        }
        layout = theViewer.getLayout();
        theViewer.setLayout(new BorderLayout());

        // listen to property changes...
        theViewer.addPropertyChangeListener(this);

        TextHelpModel model = theViewer.getModel();
        if (model != null) {
            // listen to id changes...
            model.addHelpModelListener(this);
            // listen to highlight changes...
            model.addTextHelpModelListener(this);
        }
        AccessController.doPrivileged(
                new PrivilegedAction<JoinClassLoader>() {
                    @Override
                    public JoinClassLoader run() {
                        try {
                            JoinClassLoader loader = new JoinClassLoader(getClass().getClassLoader().getParent(),
                                    getClass().getClassLoader(), ((Class) Thread.currentThread().getContextClassLoader()
                                    .loadClass(A_XERCES_CLASS_NAME)).getClassLoader());
                            Class cl = loader.forceLoader(BrowserPane.class.getName());
                            html = (JEditorPane) cl.newInstance();
                            return loader;
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        return null;
                    }
                });

        html.getAccessibleContext().setAccessibleName(HelpUtilities.getString(HelpUtilities.getLocale(html), "access.contentViewer"));
        Rectangle bounds = html.getBounds();
        bounds.setSize(c.getWidth(), c.getHeight());
        html.setBounds(bounds);
        /**
         * html future additions
         * add any listeners here
         */
        hyperlinkListener = new HelpBrowserHyperlinkHandler(theViewer);
        html.addMouseListener(hyperlinkListener.getMouseAdapter());
        html.addHyperlinkListener(hyperlinkListener);

        // if the model has a current URL then set it
        if (model != null) {
            URL url = model.getCurrentURL();
            if (url != null) {
                try {
                    setPage(url);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        JScrollPane ascroller = new JScrollPane();
        ascroller.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.white,
                Color.gray));
        ascroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        ascroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        vp = ascroller.getViewport();
        vp.add(html);
        vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        theViewer.add(ascroller, "Center");
        scroller = ascroller;
    }

    @Override
    public void uninstallUI(JComponent c) {
        debug("uninstallUI");
        JHelpContentViewer viewer = null;
        if (!(c instanceof JHelpContentViewer)) {
            throw new ClassCastException("Invalid argument of type " + c.getClass().getName() + ". Expected JHelpContentViewer.");
        } else {
            viewer = (JHelpContentViewer) c;
        }
        if (!theViewer.equals(viewer)) {
            log.warn("Attempt to uninstall UI from incorrect viewer.");
            return;
        }
        viewer.removePropertyChangeListener(this);
        /**
         * html future additions
         * remove all html listeners here - if we add any
         */
        html.removeHyperlinkListener(hyperlinkListener);
        html.removeMouseListener(hyperlinkListener.getMouseAdapter());

        TextHelpModel model = viewer.getModel();
        if (model != null) {
            model.removeHelpModelListener(this);
            model.removeTextHelpModelListener(this);
        }
        viewer.setLayout(layout);
        viewer.remove(scroller);
        theViewer = null;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return PREF_SIZE;
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return MIN_SIZE;
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        // This doesn't seem right. But I'm not sure what to do for now
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void idChanged(HelpModelEvent e) {
        ID id = e.getID();
        URL url = e.getURL();
        TextHelpModel model = theViewer.getModel();
        debug("idChanged(" + e + ")");
        debug("  = " + id + " " + url);
        debug("  my helpModel: " + model);

        model.setDocumentTitle(null);
        /**
         * html future additions
         * if we were doing any highlighting of the search text
         * code would be needed here remove all the highlights before
         * the new page is displayed
         */
        try {
            setPage(url);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        debug("done with idChanged");
    }

    private void rebuild() {
        debug("rebuild");
        TextHelpModel model = theViewer.getModel();
        if (model == null) {
            debug("rebuild-end: model is null");
            return;
        }

        /**
         * html future additions
         * if we were doing any highlighting the highlights would need
         * to be removed here
         */
        HelpSet hs = model.getHelpSet();
        // for glossary - not set homeID page - glossary is not synchronized
        if (theViewer.getSynch()) {
            try {
                Map.ID homeID = hs.getHomeID();
                Locale locale = hs.getLocale();
                String name = HelpUtilities.getString(locale, "history.homePage");
                model.setCurrentID(homeID, name, (JHelpNavigator) null);
                setPage(model.getCurrentURL());
            } catch (InvalidHelpSetContextException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            }
        }
        debug("rebuild-end");
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        debug("propertyChange: " + event.getPropertyName() + "\n\toldValue:" + event.getOldValue() + "\n\tnewValue:" + event.getNewValue());

        if (event.getSource().equals(theViewer)) {
            String changeName = event.getPropertyName();
            if (changeName.equals("helpModel")) {
                TextHelpModel oldModel = (TextHelpModel) event.getOldValue();
                TextHelpModel newModel = (TextHelpModel) event.getNewValue();
                if (oldModel != null) {
                    oldModel.removeHelpModelListener(this);
                    oldModel.removeTextHelpModelListener(this);
                }
                if (newModel != null) {
                    newModel.addHelpModelListener(this);
                    newModel.addTextHelpModelListener(this);
                }
                rebuild();
            } else if (changeName.equals("font")) {
                debug("font changed");
                //Font newFont = (Font) event.getNewValue();
                /**
                 * ~~
                 * Put font change handling code here
                 */
            } else if (changeName.equals("clear")) {
                /**
                 * html future additions
                 * do not know how to do this at the current time
                 */
                // a~~ html.setText("");
            } else if (changeName.equals("reload")) {
                try {
                    //html..refresh();
                    html.setPage(html.getPage());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    /**
     * Determines if highlights have changed.
     * Collects all the highlights and marks the presentation.
     *
     * @param e The TextHelpModelEvent.
     */
    @Override
    public void highlightsChanged(TextHelpModelEvent e) {
        debug("highlightsChanged " + e);

        // if we do anything with highlighting it would need to
        // be handled here.
        TextHelpModel model = theViewer.getModel();
        Highlighter highlighter = html.getHighlighter();
        highlighter.removeAllHighlights();
        for (Highlight highlight : model.getHighlights()) {
            try {
                int len = highlight.getEndOffset() - highlight.getStartOffset();
                if (len <= 0) {
                    continue;
                }
                Integer[] indexes = findIndexes(textDocument.substring(highlight.getStartOffset(),
                        highlight.getEndOffset()));
                for (int i = 0; i < indexes.length; i++) {
                    highlighter.addHighlight(indexes[i], indexes[i] + len, HIGHLIGHT_PAINTER);
                }
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        html.repaint();
    }

    /*
     * Set new content
     */
    private void setPage(URL url) throws IOException {
        textDocument = createText(url);
        html.setPage(url);
        highlightsChanged(null);
    }

    /**
     * For printf debugging.
     */
    private static void debug(String str) {
        log.debug(str);
    }

    /*
     * Generate DOM text page and create DOM elements index
     */
    private String createText(URL url) throws IOException {
        try {
            EditorKit kit = new HTMLEditorKit();
            HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
            doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
            Reader HTMLReader = new InputStreamReader(url.openConnection().getInputStream());
            kit.read(HTMLReader, doc, 0);
            String text = doc.getText(0, doc.getLength());
            return text;
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return "";
        }
    }

    private Integer[] findIndexes(String text) throws BadLocationException {
        String doc = html.getDocument().getText(0, html.getDocument().getLength());
        ArrayList<Integer> a = new ArrayList<Integer>();
        int pos = 0;
        while (pos >= 0 && pos < doc.length()) {
            pos = doc.indexOf(text, pos);
            if (pos >= 0 && pos < doc.length()) {
                a.remove(Integer.valueOf(pos));
                a.add(pos);
                pos = pos + 1;
            }
        }
        return a.toArray(new Integer[a.size()]);
    }
}
