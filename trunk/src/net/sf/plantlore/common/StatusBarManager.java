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

/** Status bar convenience class.
 *
 * To be more precise this class implements a mouse listener which after receiving
 * mouseEntered or mouseExited events changes the text of the JLabel that was given
 * to this class in constructor.
 *
 * This class is based on MouseOverHintManager from "Improving your Java GUI with status-bar hints"
 * on builder.com
 *
 * @author Jakub
 */
public class StatusBarManager implements MouseListener
{
    private JLabel statusLabel;
    private Map<Component, String> map;
    private String defaultText;
    
    /** Creates a new instance of StatusBarManager.
     * Sets the JLabel to be changed on mouseEntered and mouseExited events.
     */
    public StatusBarManager(JLabel status)
    {
        this.statusLabel = status;
        map = new WeakHashMap<Component, String>();
        defaultText = "";
    }

    /** Adds a component and text that should be displayed on the JLabel when mouse enters the component.
     *
     */
    public void add(Component component, String text) 
    {
        component.addMouseListener(this);
        map.put(component, text);
    }

    /** Sets the text that should be displayed when mouse pointer doesn't hover
     * over one of the added components.
     *
     */
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

    /** Sets the JLabel to the text associated to the component that was the
     * source of this event.
     *
     */
    public void mouseEntered(MouseEvent mouseEvent)
    {
        String text = (String) map.get(mouseEvent.getSource());
        statusLabel.setText(text);
    }

    /** Sets the JLabel to the default text set by setDefaultText().
     *
     */
    public void mouseExited(MouseEvent mouseEvent)
    {
        statusLabel.setText(defaultText);
    }
    
}
