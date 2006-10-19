/*
 * VisibilityChanger.java
 *
 * Created on 19. říjen 2006, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import java.awt.Component;

/**
 *
 * @author yaa
 */
public class VisibilityChanger implements Runnable {
    
        private Component c;
        private boolean visible;
        
        public VisibilityChanger(Component c, boolean visible) {
            this.c = c; this.visible = visible;
            // Preposterous!
            java.awt.EventQueue.invokeLater( this );
        }

        public void run() {
            if(c != null)
                c.setVisible( visible );
        }
    
}
