/**
 * ListItemBox.java
 * Copyright (c) 2005-2007 Radek Burget
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
 * Created on 26.9.2006, 21:25:38 by radek
 */
package org.fit.cssbox.layout;

import java.awt.Graphics2D;

import org.w3c.dom.Element;
import cz.vutbr.web.css.*;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.net.MalformedURLException;
import java.net.URL;
import org.fit.net.DataURLHandler;
import org.w3c.dom.Node;

/**
 * This class represents a list-item box. This box behaves the same way
 * as a block box with some modifications.
 *
 * @author radek
 */
public class ListItemBox extends BlockBox {

    private static final String[] RCODE = {"M", "CM", "D", "CD", "C", "XC", "L",
        "XL", "X", "IX", "V", "IV", "I"};
    private static final int[] BVAL = {1000, 900, 500, 400, 100, 90, 50,
        40, 10, 9, 5, 4, 1};
    private int itemNumber;
    private ReplacedImage image;

    /**
     * Create a new list item
     */
    public ListItemBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        isblock = true;
        itemNumber = findItemNumber();
    }

    /**
     * Create a new list item from an inline box
     */
    public ListItemBox(InlineBox src) {
        super(src);
        isblock = true;
        itemNumber = findItemNumber();
        CSSProperty.ListStyleImage lsi = style.getProperty("list-style-image");
        if (lsi == CSSProperty.ListStyleImage.uri) {
            TermURI urlstring = style.getValue(TermURI.class, "list-style-image");
            try {
                URL url = DataURLHandler.createURL(urlstring.getBase(), urlstring.getValue());
                image = new ReplacedImage(src, src.getVisualContext(), url);
            } catch (MalformedURLException e) {
                System.err.println("BackgroundImage: Warning: " + e.getMessage());
            }
        }
    }

    /**
     * Find number of the item in (ordered) list node hierarchy
     *
     * @return item number or -1 when parent node is not ordered list
     */
    private int findItemNumber() {
        Node thisNode = getNode();
        Node parentNode = node.getParentNode();
        if (!"ol".equalsIgnoreCase(parentNode.getNodeName())) {
            return -1;
        }
        for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
            if (thisNode.equals(parentNode.getChildNodes().item(i))) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Return item number in ordered list.
     * For unordered list or corrupted node structure returns -1.
     *
     * @return item number
     */
    public int getItemNumber() {
        return itemNumber;
    }

    @Override
    public void draw(Graphics2D g, int turn, int mode) {
        super.draw(g, turn, mode);
        if (displayed && isVisible()) {
            if (turn == DRAW_ALL || turn == DRAW_NONFLOAT) {
                if (mode == DRAW_BOTH || mode == DRAW_FG) {
                    drawList(g);
                }
            }
        }
    }

    /**
     * Checks whether the list item has a visible bullet.
     *
     * @return <code>true</code> when the bullet type is set to other value than <code>none</code>.
     */
    public boolean hasVisibleBullet() {
        CSSProperty.ListStyleType type = style.getProperty("list-style-type");
        return type != CSSProperty.ListStyleType.NONE;
    }

    /**
     * Get ordered list item text depending on list-style-type property.
     *
     * @return item text or empty string for unordered list
     */
    public String getItemText() {
        CSSProperty.ListStyleType type = style.getProperty("list-style-type");
        String text = ("" + itemNumber).trim() + ". ";  // default decimal
        if (itemNumber > 0) {  // ordered list
            if (type == CSSProperty.ListStyleType.UPPER_ALPHA) {
                text = "" + ((char) (64 + (itemNumber % 24))) + " ";
            } else if (type == CSSProperty.ListStyleType.LOWER_ALPHA) {
                text = "" + ((char) (96 + (itemNumber % 24))) + ") ";
            } else if (type == CSSProperty.ListStyleType.UPPER_ROMAN) {
                text = "" + binaryToRoman(itemNumber) + ". ";
            } else if (type == CSSProperty.ListStyleType.LOWER_ROMAN) {
                text = "" + binaryToRoman(itemNumber).toLowerCase() + ". ";
            }
            return text;
        } else {  // unordered list
            return "";
        }
    }

    /**
     * Draw list item symbol or number depending on list-style-type
     *
     * @param g
     */
    protected void drawList(Graphics2D g) {
        CSSProperty.ListStyleType type = style.getProperty("list-style-type");
        if (itemNumber > 0) {  // ordered list
            drawText(g, getItemText());
        } else if (image != null) {
            drawImage(g);
        } else {  // unordered list
            drawBullet(g);
        }
    }

    /**
     * Draw a bullet
     */
    private void drawBullet(Graphics2D g) {
        int x = (int) Math.round(getAbsoluteContentX() - 1.2 * ctx.getEm());
        int y = (int) Math.round(getAbsoluteContentY() + 0.4 * ctx.getEm());
        int r = (int) Math.round(0.5 * ctx.getEm());
        CSSProperty.ListStyleType type = style.getProperty("list-style-type");
        if (type == CSSProperty.ListStyleType.CIRCLE) {
            g.drawOval(x, y, r, r);
        } else if (type == CSSProperty.ListStyleType.SQUARE) {
            g.fillRect(x, y, r, r);
        } //else if (type == CSSProperty.ListStyleType.BOX) //not documented, recognized by Konqueror 
        //	g.drawRect(x, y, r, r);
        else if (type != CSSProperty.ListStyleType.NONE) //use 'disc'
        {
            g.fillOval(x, y, r, r);
        }
    }

    protected void drawImage(Graphics2D g) {
        int x = (int) Math.round(getAbsoluteContentX() - 1.2 * ctx.getEm());
        int y = (int) Math.round(getAbsoluteContentY() + 0.4 * ctx.getEm());
        Image img = image.getImage();
        if (img != null) {
            int w = img.getWidth(image);
            int h = img.getHeight(image);
            x = x - w / 2;
            y = y + h / 2;
            g.drawImage(img, x, y, image);
        }
    }

    protected void drawText(Graphics2D g, String text) {
        //top left corner
        int x = (int) Math.round(getAbsoluteContentX());
        int y = (int) Math.round(getAbsoluteContentY());

        //Draw the string
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        g.drawString(text, x + ((int) Math.round(rect.getX())) - ((int) Math.round(rect.getWidth())),
                y - ((int) Math.round(rect.getY())));
    }

    /**
     * Conversion int to Roman numbers
     * from http://www.roseindia.net/java/java-tips/45examples/misc/roman/roman.shtml
     *
     * @param binary
     * @return
     */
    private static String binaryToRoman(int binary) {
        if (binary <= 0 || binary >= 4000) {
            throw new NumberFormatException("Value outside roman numeral range.");
        }
        String roman = "";         // Roman notation will be accumualated here.

        // Loop from biggest value to smallest, successively subtracting,
        // from the binary value while adding to the roman representation.
        for (int i = 0; i < RCODE.length; i++) {
            while (binary >= BVAL[i]) {
                binary -= BVAL[i];
                roman += RCODE[i];
            }
        }
        return roman;
    }
}
