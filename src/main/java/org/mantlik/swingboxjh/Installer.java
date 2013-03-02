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

import cz.vutbr.web.css.CSSFactory;
import javax.swing.UIManager;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // Suppose length without unit to be in px
        CSSFactory.setImplyPixelLength(true);
        UIManager.getLookAndFeelDefaults().put("HelpContentViewerUI", "org.mantlik.swingboxjh.SwingboxContentViewerUI");
        UIManager.put("HelpContentViewerUI", "org.mantlik.swingboxjh.SwingboxContentViewerUI");
    }

}
