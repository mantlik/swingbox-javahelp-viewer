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

import com.sun.java.help.search.Indexer;

/**
 * Index search generator for Swingbox Javahelp Viewer
 *
 * @author fm
 */
public class SwingboxIndexer {

    public static void main(String args[]) {
        Indexer.registerIndexerKitForContentType("text/html", "org.mantlik.swingboxjh.indexer.SwingboxIndexerKit", null);
        Indexer.main(args);
    }
}
