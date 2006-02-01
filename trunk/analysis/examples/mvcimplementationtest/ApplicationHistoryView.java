/*
 * ApplicationHistoryView.java
 *
 * Created on 13. listopad 2005, 14:05
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
public class ApplicationHistoryView implements Observer {
    private ApplicationModel model;
    private JFrame frame;
    private Container container;
    private JTextField display;
    private JTextArea area;
    
    /** Creates a new instance of ApplicationHistoryView */
    public ApplicationHistoryView(ApplicationModel model_) {
        model = model_;
        model.addObserver(this);

        frame = new JFrame("History View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320,240);
        frame.setVisible(false);
        container = frame.getContentPane();
        container.setLayout(new BorderLayout());
        init();
    }

    private void init() {
        display = new JTextField(10);
        area = new JTextArea(20,10);
        container.add(display,BorderLayout.NORTH);
        container.add(area,BorderLayout.CENTER);
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
    
    public void update(Observable obs, Object obj) {
        display.setText(model.getDisplay());
        area.setText(model.getHistory());
    }    
}
