/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mantlik.swingboxjh;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.help.JHelp;
import javax.swing.JFrame;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall implements WindowFocusListener {

    Frame m_help_frame = null;

    @Override
    public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(
                new Runnable() {
                    @Override
                    public void run() {
                        Frame mainWindow = WindowManager.getDefault().getMainWindow();

                        mainWindow.addWindowFocusListener(Installer.this);

                    }
                });

    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        if (m_help_frame != null) {
            return;
        }
        Frame[] frames = Frame.getFrames();
        for (int k = 0; k < frames.length; k++) {
            if (!(frames[k] instanceof JFrame)) {
                continue;
            }
            JFrame jf = (JFrame) frames[k];
            if (jf.getContentPane().getComponentCount() == 0) {
                continue;
            }
            Component c = jf.getContentPane().getComponent(0);
            if (c == null || !(c instanceof JHelp)) {
                continue;
            }
            m_help_frame = jf;
            JHelp jh = (JHelp) c;
            SwingboxContentViewerUI hcvui = new SwingboxContentViewerUI(jh.getContentViewer());
            jh.getContentViewer().setUI(hcvui);
        }
    }
}
