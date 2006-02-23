/**
 * 
 */
package net.sf.plantlore.client.history;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import net.sf.plantlore.client.Settings;
import net.sf.plantlore.client.SettingsView;


import org.apache.log4j.Logger;

/**
 * @author Lada
 *
 */
public class HistoryCtrl {

	private Logger logger;
    private History model;
    private HistoryView view;
    
    /** Creates a new instance of HistoryCtrl */
    public HistoryCtrl(History model, HistoryView view)
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        this.model = model;
        this.view = view;
                     
        view.addOkButtonListener(new okButtonListener());
        view.addCancelButtonListener(new cancelButtonListener());
        view.addHelpButtonListener(new helpButtonListener());
        view.addPreviousButtonListener(new previousButtonListener());
        view.addNextButtonListener(new nextButtonListener());
        view.addSelectAllButtonListener(new selectAllButtonListener());
        view.addUnselectAllButtonListener(new unselectAllButtonListener());
        view.addUnselectAllButtonListener(new unselectAllButtonListener());
        view.addUndoToButtonListener(new undoToButtonListener());
    }
    
    /** 
    * On Ok makes the model store() the preferences and hides the view.
    * 
    */
   class okButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {       
           view.dispose();           
       }
   }
  
   /**
    * On Cancel just hides the view.
    *
    */
   class cancelButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   view.dispose();
       }
   }
   
   /**
    * On Help should call help.
    *
    */
   class helpButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   System.out.println("Tady se bude volat Help!");
       }
   }
   
   /**
    * 
    *
    */
   class previousButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
          
       }
   }
   
   /**
    * 
    *
    */
   class nextButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
          
       }
   }
   
   /**
    * 
    *
    */
   class selectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   int countRow = view.getTable().getRowCount();
           for (int row=0; row < countRow; row++)
           {
         	  //System.out.println(view.getTable().getValueAt(row, 0));         	
         	  view.getTable().setValueAt(true, row, 0);           	
           } 
           view.repaint();
       }
   }
   
   /**
    * 
    *
    */
   class unselectAllButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
    	   int countRow = view.getTable().getRowCount();
           for (int row=0; row < countRow; row++)
           {
         	  //System.out.println(view.getTable().getValueAt(row, 0));         	  
         	  view.getTable().setValueAt(false, row, 0);           	
           }
           view.repaint();
       }
   }
   
   /**
    * 
    *
    */
   class undoSelectedButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
          
       }
   }
   
   /**
    * 
    *
    */
   class undoToButtonListener implements ActionListener {
       public void actionPerformed(ActionEvent actionEvent)
       {
          
       }
   }
    

}
