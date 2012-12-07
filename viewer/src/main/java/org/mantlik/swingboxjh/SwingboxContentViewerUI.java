package org.mantlik.swingboxjh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Locale;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
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
import javax.help.plaf.basic.BasicNativeContentViewerUI;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import org.fit.cssbox.swingbox.BrowserPane;
import org.openide.util.Exceptions;

/**
 * Javahelp component viewer based on SwingBox viewer
 *
 * @author fm
 */
public class SwingboxContentViewerUI extends HelpContentViewerUI
        implements HelpModelListener, TextHelpModelListener, PropertyChangeListener,
            Serializable {
    protected JHelpContentViewer theViewer;
    
    private static Dimension PREF_SIZE = new Dimension(200, 300);
    private static Dimension MIN_SIZE = new Dimension(80, 80);
    private static final HighlightPainter HIGHLIGHT_PAINTER = new DefaultHighlightPainter (Color.ORANGE);
    
    private BrowserPane html;
    private JViewport vp;
    private HelpBrowserHyperlinkHandler hyperlinkListener;
    
    public static ComponentUI createUI(JComponent x) {
        debug("createUI");
        return new BasicNativeContentViewerUI((JHelpContentViewer) x);
    }
    
    public SwingboxContentViewerUI(JHelpContentViewer b) {
        debug("createUI - sort of");
    }
    
    @Override
    public void installUI(JComponent c) {
        debug("installUI");
        theViewer = (JHelpContentViewer)c;
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
        
	html = new BrowserPane();
        html.getAccessibleContext().setAccessibleName(HelpUtilities.getString(HelpUtilities.getLocale(html), "access.contentViewer"));
        Rectangle bounds = html.getBounds();
        bounds.setSize(c.getWidth(), c.getHeight());
        html.setBounds(bounds);
        if (debug) {
	    //html.setDebug(true);
	}
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
                    html.setPage(url);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
	    }
	}

        JScrollPane scroller = new JScrollPane();
        scroller.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.white,
        Color.gray));
        vp = scroller.getViewport();
        vp.add(html);
        vp.setBackingStoreEnabled(true);
        theViewer.add("Center", scroller);
    }
    
    @Override
    public void uninstallUI(JComponent c) {
        debug("uninstallUI");
        JHelpContentViewer viewer = (JHelpContentViewer) c;
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
        viewer.setLayout(null);
        viewer.removeAll();
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
        debug("idChanged("+e+")");
        debug("  = " + id + " " + url);
        debug("  my helpModel: "+model);
        
        model.setDocumentTitle(null);
            /**
             * html future additions
             * if we were doing any highlighting of the search text
             * code would be needed here remove all the highlights before
             * the new page is displayed
             */
        html.getHighlighter().removeAllHighlights();

        try {
            html.setPage(url);
        } catch (IOException ex) {
                    ex.printStackTrace();
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
        html.getHighlighter().removeAllHighlights();

        HelpSet hs = model.getHelpSet();
        // for glossary - not set homeID page - glossary is not synchronized
        if(theViewer.getSynch()){
            try {
                Map.ID homeID = hs.getHomeID();
                Locale locale = hs.getLocale();
                String name = HelpUtilities.getString(locale, "history.homePage");
                model.setCurrentID(homeID, name, (JHelpNavigator)null);
                html.setPage(model.getCurrentURL());
            } catch (Exception e) {
                // ignore
            }
        }
        debug("rebuild-end");
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        debug("propertyChange: " + event.getPropertyName() + "\n\toldValue:" + event.getOldValue() + "\n\tnewValue:" + event.getNewValue());
        
        if (event.getSource() == theViewer) {
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
                Font newFont = (Font)event.getNewValue();
		/**
		 * ~~
		 * Put font change handling code here
		 */
            }else if (changeName.equals("clear")) {
		/**
		 * html future additions
		 * do not know how to do this at the current time
		 */
                // a~~ html.setText("");
            }else if (changeName.equals("reload")) {
                try {
                    //html..refresh();
                    html.setPage(html.getPage());
                } catch (IOException ex) {
                    ex.printStackTrace();
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
        debug("highlightsChanged "+e);
 
	// if we do anything with highlighting it would need to
	// be handled here.
        TextHelpModel model = theViewer.getModel();
        Highlighter highlighter = html.getHighlighter();
        highlighter.removeAllHighlights();
        for (Highlight highlight : model.getHighlights()) {
            try {
                highlighter.addHighlight(highlight.getStartOffset(), highlight.getEndOffset(), HIGHLIGHT_PAINTER);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        html.repaint();
    }
    
    /**
     * For printf debugging.
     */
    private final static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("NativeContentViewerUI: " + str);
        }
    }

}
