/*
 * #%L
 * swingbox-javahelp-viewer
 * %%
 * Copyright (C) 2012 - 2013 RNDr. Frantisek Mantlik <frantisek at mantlik.cz>
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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.fit.cssbox.swingbox.BrowserPane;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author fm
 */
public class InheritedListsTest {
    
    public InheritedListsTest() {
    }
    
    private static BrowserPane pane;

    @Test//(timeOut=10000)
    public void testInheritedList() throws IOException, InterruptedException {
        URL testpage = getClass().getResource("process.html");
        pane.setPage(testpage);
        Graphics2D content = pane.renderContent();
        Assert.assertNotNull(content , "Content not rendered.");
        System.out.println(testpage + " page loaded.");
        BufferedImage bi = new BufferedImage(content.getClipBounds().width, 
                content.getClipBounds().height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D content2 = bi.createGraphics();
        content2.setClip(content.getClip());
        pane.renderContent(content2);
        new File("target/surefire-reports").mkdirs();
        ImageIO.write(bi, "PNG", new File("target/surefire-reports/inheritedListTest.png"));
    }
    
    @org.testng.annotations.BeforeClass
    public static void setUpClass() throws Exception {
        pane = new BrowserPane();
    }

    @org.testng.annotations.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.testng.annotations.BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @org.testng.annotations.AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
