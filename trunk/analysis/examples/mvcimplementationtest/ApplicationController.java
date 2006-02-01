/*
 * GUIContainerController.java
 *
 * Created on 4. listopad 2005, 23:43
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package mvcimplementationtest;

import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author reimei
 */
public class ApplicationController {
    ApplicationModel model;
    ApplicationView view;
    
    /** Creates a new instance of GUIContainerController */
    public ApplicationController(ApplicationModel model_, ApplicationView view_) {
        model = model_;
        view = view_;
        view.buttonsAddActionListener(new DigitButtonListener());
        view.operationsAddActionListener(new OperationsButtonListener());
    }
    
    class DigitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String digit=((JButton)e.getSource()).getText();
            model.addDigit(digit);
            System.out.println("Button "+digit+" pressed.");
        }
    }// class ButtonListener
    
    class OperationsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String operation=((JButton)e.getSource()).getText();
            model.performOperation(operation);
            System.out.println("Operation '"+ operation +"' performed");
        }
    }// class OperationsButtonListener
}
