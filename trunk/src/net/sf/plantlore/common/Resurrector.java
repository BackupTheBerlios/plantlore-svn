/*
 * Resurrector.java
 *
 * Created on 17. říjen 2006, 16:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import javax.swing.JDialog;

/**
 *
 * @author kaimu
 */
public class Resurrector {
    
    private JDialog dialog;
    
    
    public synchronized void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }
    
    
    public synchronized void bringItBackIfPossible() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if( dialog != null )
                    dialog.setVisible( true );
                dialog = null;
            }
        });
    }
    
}
