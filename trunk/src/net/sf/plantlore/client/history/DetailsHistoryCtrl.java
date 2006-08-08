/*
 * DetailsHistoryCtrl.java
 *
 * Created on 21. duben 2006, 17:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.sf.plantlore.common.DefaultCancelAction;
import net.sf.plantlore.common.DefaultEscapeKeyPressed;

/**
 * Controller for the DetailsHistory dialog (part of the DetailsHistory MVC).
 *
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class DetailsHistoryCtrl {
     
	/** View of the DerailsHistory MVC*/
    private DetailsHistoryView view;
    
    /** 
     * Creates a new instance of DetailsHistoryCtrl
     * @param view View of the DerailsHistory MVC 
     */
    public DetailsHistoryCtrl(DetailsHistoryView viewDialog) {
                          
        this.view = viewDialog;
        DefaultEscapeKeyPressed escapeKeyPressed = new DefaultEscapeKeyPressed(view);
        // Add action listeners to buttons
        view.closeButton.setAction(new DefaultCancelAction(view));
        view.helpButton.addActionListener(new helpButtonListener());
        // Add key listeners
        view.closeButton.addKeyListener(escapeKeyPressed);
        view.helpButton.addKeyListener(escapeKeyPressed);
        view.detailsTextArea.addKeyListener(escapeKeyPressed);
    }
       
   /**
    * ActionListener class controlling the <b>HELP</b> button on the form.
    * Display help viewer
    */
   class helpButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    	      	               	   
       }
   }
}
