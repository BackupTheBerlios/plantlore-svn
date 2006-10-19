/*
 * DialogSwitcher.java
 *
 * Created on 19. říjen 2006, 14:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Stack;

/**
 * The problem with Swing is that two modal windows that share the same parent
 * cannot be visible at the same time. If we wish to switch from one window
 * to another, the first one must go, i.e. it must be hidden first.
 *
 * There is no other way because it is not possible to change the parent window
 * of any dialog.
 *
 * @author kaimu
 * @version not tested
 */
public class DialogSwitcher /*extends WindowAdapter implements ActionListener*/ {
    
    private static Stack<Component> components = new Stack<Component>();
    private static DialogSwitcher switcher;
    
    private DialogSwitcher() {}
    
    /*
    public static void involve(Window w, javax.swing.AbstractButton...buttons) {
        if(switcher == null)
            switcher = new DialogSwitcher();
        w.addWindowListener( switcher );
        if(buttons != null)
            for(javax.swing.AbstractButton b : buttons)
                b.addActionListener( switcher );
    }
     */
    
    public static void switchFromTo(Component from, Component to) {
        components.push(from);
        new VisibilityChanger(from, false);
        new VisibilityChanger(to, true);
     }

    public static void switchBack() {
        if( !components.empty() )
            new VisibilityChanger(components.pop(), true);
    }

    /*
    @Override
    public synchronized void windowClosing(WindowEvent e) {
        switchBack();
    }

    public void actionPerformed(ActionEvent e) {
        switchBack();
    }
     */
    
}
