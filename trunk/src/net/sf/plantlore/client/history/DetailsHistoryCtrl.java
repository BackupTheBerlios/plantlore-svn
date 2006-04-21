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
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class DetailsHistoryCtrl {
    
    private Logger logger;   
    private DetailsHistoryView view;
    
    /** Creates a new instance of DetailsHistoryCtrl */
    public DetailsHistoryCtrl(DetailsHistoryView view) {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());              
        this.view = view;
        
        view.okButton.addActionListener(new okButtonListener());
        view.closeButton.addActionListener(new closeButtonListener());
        view.helpButton.addActionListener(new helpButtonListener());
    }
    
               /** 
    * On Ok makes the model store() the preferences and hides the view.
    * 
    */
   class okButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {       
           view.close();           
       }
   }
  
   /**
    * On Cancel just hides the view.
    *
    */
   class closeButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   view.close();
       }
   }
   
   /**
    * On Help should call help.
    *
    */
   class helpButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {    	  
    	   // Display help viewer            
    	   System.out.println("Tady se bude volat Help!");
       }
   }
}
