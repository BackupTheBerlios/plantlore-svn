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
    public DetailsHistoryCtrl(DetailsHistoryView view) {
                          
        this.view = view;
        // Add action listeners to buttons
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());
    }
       
    /** 
     * ActionListener class controlling the <b>OK</b> button on the form.
     * On Ok makes the model store() the preferences and hides the view.     
     */
   class closeButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   view.close();
       }
   }
   
   /**
    * ActionListener class controlling the <b>HELP</b> button on the form.
    * Display help viewer
    */
   class helpButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    	      	           
    	   System.out.println("Tady se bude volat Help!");
       }
   }
}
