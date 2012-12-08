/*
 * #%L
 * swingbox-javahelp-indexer
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mantlik.swingboxjh.indexer;

import com.sun.java.help.search.DefaultIndexerKit;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.search.ConfigFile;
import javax.help.search.IndexBuilder;
import javax.swing.text.Document;
import org.fit.cssbox.swingbox.BrowserPane;
import org.fit.cssbox.swingbox.SwingBoxEditorKit;

/**
 *
 * @author fm
 */
public class SwingboxIndexerKit extends DefaultIndexerKit {

    @Override
    public Object clone() {
        return new SwingboxIndexerKit();
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void parse(Reader reader, String file, boolean bln, IndexBuilder ib, ConfigFile cf) throws IOException {
        BrowserPane pane = new BrowserPane();
        SwingBoxEditorKit kit = (SwingBoxEditorKit) pane.getEditorKit();
        Document document = kit.createDefaultDocument();
        URL url = new File(file).toURI().toURL();
        if (document.getProperty(Document.StreamDescriptionProperty) == null)
        {
            document.putProperty(Document.StreamDescriptionProperty, url);
        }
        try {
            kit.read(reader, document, 0);
            String text = document.getText(0, document.getLength());
            this.builder = ib;
            this.config = cf;
            this.file = file;
            documentStarted = false;
            super.parseIntoTokens(text, 0);
            storeTitle(document.getProperty(Document.TitleProperty).toString());
            endStoreDocument();
        } catch (Exception ex) {
            Logger.getLogger(SwingboxIndexerKit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int parseIntoTokens(String string, int i) {
        return -1;
    }
    
}
