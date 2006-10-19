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
 * This class can store a dialog and bring it back (make it visible) again.
 * It used to have much greater scope but that proved to be problematic.
 * It should be probably incorporated back into the AuthorManagerCtrl 
 * whence it came.
 *
 * @author kaimu
 * @version beta
 */
@Deprecated
public class Resurrector {
    
    private JDialog dialog;
    
    /**
     * Set the dialog to be reopened.
     */
    public synchronized void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }
    
    /**
     * Open the dialog again.
     */
    public synchronized void bringItBackIfPossible() {
        if( dialog == null )
            return;
        // The dialog must be cloned and erased here.
        final JDialog dialogClone = dialog;
        dialog = null;
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                dialogClone.setVisible( true );
                // This line will be executed after the dialog becomes invisible!
            }
        });
    }
    
}
