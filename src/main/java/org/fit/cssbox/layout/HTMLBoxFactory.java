/*
 * HTMLBoxFactory.java
 * Copyright (c) 2005-2012 Radek Burget
 *
 * CSSBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * CSSBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with CSSBox. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created on 23.11.2012, 15:52:00 by burgetr
 */
package org.fit.cssbox.layout;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.Declaration;
import java.awt.Graphics2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import cz.vutbr.web.css.NodeData;

import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DocumentSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author burgetr
 */
public class HTMLBoxFactory
{
    private BoxFactory factory;
    private Set<String> supported;
    
    public HTMLBoxFactory(BoxFactory parent)
    {
        this.factory = parent;
        supported = new HashSet<String>(2);
        supported.add("object");
        supported.add("img");
    }
    
    public boolean isTagSupported(Element e)
    {
        if (e.getNodeName() != null && supported.contains(e.getNodeName().toLowerCase())) //completely supported tags
            return true;
        else //special cases
        {
            //empty anchor elements must be preserved
            if (e.getNodeName().toLowerCase().equals("a") && e.hasAttribute("name") && e.getTextContent().trim().length() == 0)
                return true;
            else
                return false;
        }
    }
    
    /**
     * Creates the box according to the HTML element.
     * @param parent
     * @param e
     * @param viewport
     * @param style
     * @return The newly created box or <code>null</code> when the element is not supported
     * or cannot be created. 
     */
    public ElementBox createBox(ElementBox parent, Element e, Viewport viewport, NodeData style)
    {
        String name = e.getNodeName().toLowerCase();
        if (name.equals("object"))
            return createSubtreeObject(parent, e, viewport, style);
        else if (name.equals("img"))
            return createSubtreeImg(parent, e, viewport, style);
        else if (name.equals("a") && e.hasAttribute("name") && e.getTextContent().trim().length() == 0)
        {
            //make the named anchors sticky
            ElementBox eb = factory.createElementInstance(parent, e, style);
            eb.setSticky(true);
            return eb;
        }
        else
            return null;
    }
    
    protected ElementBox createSubtreeImg(ElementBox parent, Element e, Viewport viewport, NodeData style)
    {
        InlineReplacedBox rbox = new InlineReplacedBox(e, (Graphics2D) parent.getGraphics().create(), parent.getVisualContext().create());
        rbox.setViewport(viewport);
        rbox.setStyle(style);

        String src = e.getAttribute("src");
        rbox.setContentObj(new ReplacedImage(rbox, rbox.getVisualContext(), factory.getBaseURL(), src));
        
        if (rbox.isBlock())
            return new BlockReplacedBox(rbox);
        else
            return rbox;
    }
    
    protected ElementBox createSubtreeObject(ElementBox parent, Element e, Viewport viewport, NodeData style)
    {
        //create the replaced box
        InlineReplacedBox rbox = new InlineReplacedBox(e, (Graphics2D) parent.getGraphics().create(), parent.getVisualContext().create());
        rbox.setViewport(viewport);
        rbox.setStyle(style);
        
        //try to create the content object based on the mime type
        try
        {
            String mime = e.getAttribute("type").toLowerCase();
            String cb = e.getAttribute("codebase");
            String dataurl = e.getAttribute("data");
            String theContent = "";
            String text = "";
            
            if (mime.equals("") && cb.equals("") && dataurl.equals("")) {
                // try to process Netbeans Javahelp object
                NodeList params = e.getElementsByTagName("param");
                for (int i=0; i<params.getLength(); i++) {
                    NamedNodeMap attributes = params.item(i).getAttributes();
                    if (attributes.getNamedItem("name") == null || attributes.getNamedItem("value") == null) {
                        continue;
                    }
                    if (attributes.getNamedItem("name").getNodeValue().equals("content")) {
                        theContent = attributes.getNamedItem("value").getNodeValue();
                    } else if (attributes.getNamedItem("name").getNodeValue().equals("text")) {
                        text = attributes.getNamedItem("value").getNodeValue();
                    }
                }
                if ("".equals(text)) {
                    text = theContent;
                }
                Element anchor = viewport.getFactory().createAnonymousElement(e.getOwnerDocument(), 
                        "a", ElementBox.DISPLAY_INLINE.name());
                anchor.setAttribute("href", theContent);
                anchor.setAttribute("target", "_blank");
                anchor.setTextContent(text.replaceAll("\\<.*?>",""));
                ElementBox anbox = new InlineBox(anchor, (Graphics2D) parent.getGraphics().create(), parent.getVisualContext().create());
                anbox.setViewport(viewport);
                
                Declaration cls = CSSFactory.getRuleFactory().createDeclaration();
                cls.unlock();
                cls.setProperty("color");
                cls.add(CSSFactory.getTermFactory().createColor(0, 0, 255));
                cls.setImportant(true);
                style.push(cls);
                
                anbox.setStyle(style);
                anbox.isblock = false;
                return anbox;
            }

            URL base = new URL(factory.getBaseURL(), cb);
            
            DocumentSource src = factory.createDocumentSource(base, dataurl);
            if (mime.isEmpty())
            {
                mime = src.getContentType();
                if (mime == null || mime.isEmpty())
                    mime = "text/html";
            }
            System.out.println("ctype=" + mime);
            
            ReplacedContent content = null;
            if (mime.startsWith("image/"))
            {
                content = new ReplacedImage(rbox, rbox.getVisualContext(), base, dataurl);
            }
            else if (mime.equals("text/html"))
            {
                System.out.println("Parsing: " + src.getURL()); 
                DOMSource parser = new DefaultDOMSource(src);
                Document doc = parser.parse();
                String encoding = parser.getCharset();
                content = new ReplacedText(rbox, doc, src.getURL(), encoding);
            }
            rbox.setContentObj(content);
        } catch (MalformedURLException e1) {
            //something failed, no content object is created => the we should use the object element contents
        } catch (SAXException e1) {
            //parsing failed
        } catch (IOException e1) {
            //document reading failed
        }

        if (rbox.getContentObj() != null) //the content object has been sucessfuly created
        {
            //convert the type
            if (rbox.getDisplay() == ElementBox.DISPLAY_BLOCK)
                return new BlockReplacedBox(rbox);
            else //inline boxes are not allowed -- we must create a block formatting context
                return new InlineBlockReplacedBox(rbox);
        }
        else //no content object - fallback to a normal box (the default object behavior)
        {
            return null;
        }
    }
    
}
