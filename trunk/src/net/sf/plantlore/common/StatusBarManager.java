/*
 * StatusBarManager.java
 *
 * Created on 25. leden 2006, 22:04
 *
 */

package net.sf.plantlore.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JLabel;

/**
 * This class is based on MouseOverHintManager from Improving your Java GUI with status-bar hints
 * on builder.com
 *
 * @author Jakub
 */
public class StatusBarManager implements MouseListener
{
    private JLabel statusLabel;
    private Map map;
    private String defaultText;
    
    /** Creates a new instance of StatusBarManager */
    public StatusBarManager(JLabel status)
    {
        this.statusLabel = status;
        map = new WeakHashMap();
        defaultText = "";
    }

    public void add(Component component, String text) 
    {
        component.addMouseListener(this);
        map.put(component, text);
    }

    public void setDefaultText(String text) 
    {
        defaultText = text;
    }
    
    public void mouseClicked(MouseEvent mouseEvent)
    {
    }

    public void mousePressed(MouseEvent mouseEvent)
    {
    }

    public void mouseReleased(MouseEvent mouseEvent)
    {
    }

    public void mouseEntered(MouseEvent mouseEvent)
    {
        String text = (String) map.get(mouseEvent.getSource());
        statusLabel.setText(text);
    }

    public void mouseExited(MouseEvent mouseEvent)
    {
        statusLabel.setText(defaultText);
    }
    
}
