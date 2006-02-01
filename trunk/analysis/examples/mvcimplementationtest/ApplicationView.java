/*
 * GUIContainer.java
 *
 * Created on 4. listopad 2005, 23:42
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package mvcimplementationtest;

import java.util.Observer;
import java.util.Observable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author reimei
 */
public class ApplicationView implements Observer {
    private ApplicationModel model;
    private JFrame frame;
    private Container container;
    private JPanel numPad;
    private JPanel operationsPad;
    private JButton[] digitButtons = new JButton[10];
    private JButton[] operationButtons = new JButton[4];
    private JButton equalsButton;
    private JTextField text;
    
    /** Creates a new instance of GUIContainer */
    public ApplicationView(ApplicationModel model_) {
        model = model_;
        model.addObserver(this);
        
        frame = new JFrame("MVC Based Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320,240);
        frame.setVisible(false);
        container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        init();
    }//ApplicationView()
    
    public void init() {
        numPad = getNumPad();
        text = new JTextField(10);
        operationsPad = getOperationsPad();
        equalsButton = new JButton("=");
        container.add(text,BorderLayout.NORTH);
        container.add(numPad,BorderLayout.CENTER);
        container.add(operationsPad,BorderLayout.EAST);
        container.add(equalsButton,BorderLayout.SOUTH);
    }
    
    private JPanel getNumPad() {
        JPanel result = new JPanel(new BorderLayout());
        JPanel nonzeroes = new JPanel(new GridLayout(3,3));
        for (int i=1; i < 10; i++) {
            digitButtons[i] = new JButton(""+i);
            nonzeroes.add(digitButtons[i]);
        }        
        digitButtons[0] = new JButton("0");
        result.add(nonzeroes,BorderLayout.CENTER);
        result.add(digitButtons[0],BorderLayout.SOUTH);
        return result;
    }
    
    private JPanel getOperationsPad() {
        JPanel result = new JPanel(new GridLayout(2,2));
        operationButtons[0] = new JButton("+");
        operationButtons[1] = new JButton("*");
        operationButtons[2] = new JButton("-");
        operationButtons[3] = new JButton("/");
        result.add(operationButtons[0]);
        result.add(operationButtons[1]);
        result.add(operationButtons[2]);
        result.add(operationButtons[3]);
        return result;
    }
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
    
    public void buttonsAddActionListener(ActionListener al) {
        for (int i=0; i < digitButtons.length; i++) {
            digitButtons[i].addActionListener(al);
        }
    }//buttonsAddActionListener
    
    public void operationsAddActionListener(ActionListener al) {
        for (int i=0; i < operationButtons.length; i++) {
            operationButtons[i].addActionListener(al);
        }
        equalsButton.addActionListener(al);
    }
        
    public void update(Observable obs, Object obj) {
        text.setText(model.getDisplay());
    }    
} //class


