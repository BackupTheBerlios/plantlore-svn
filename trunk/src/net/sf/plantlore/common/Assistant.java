package net.sf.plantlore.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.Position;


/** 
 * The <i>Visual Assistant</i>.
 * 
 * Visual Assistant is kind of a wrapper of the JList
 * that can pop-up above other components.
 * <br/>
 * The JList can be obtained so that an easy access
 * to all its methods is ensured.
 *
 *    
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 */
public class Assistant extends JScrollPane {

	protected JList list;
	private Point o;
	private Component father;
	
	/**
	 * Create the Assistant.
	 * 
	 * @param l	List of available choices.
	 * @param container	The container to which the assistant will be added. 
	 */
	public Assistant(JList l, Container container) {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setList(l); setVisible(false); setSize(200,100); setLocation(0, 0);
		container.add(this, JLayeredPane.POPUP_LAYER);
	}

	/**
	 * Create the Assistant with the default list (JList) settings.
	 * 
	 * @param choices	Array of choices.
	 * @param container	The container to which the assistant will be added.
	 */
	public Assistant(Object[] choices, Container container) {
		this(new JList(choices), container);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFocusable(false);
	}
        
        public Assistant(Container container) {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVisible(false); setSize(200,100); setLocation(0, 0);
		container.add(this, JLayeredPane.POPUP_LAYER);            
        }
        
        public void setChoices(Object[] choices) {
            setList(new JList(choices));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setFocusable(false);
        }
	
	/** Get the list of choices. */
	public JList getList() { 
		return list;
	}
	
	/** Set the list of choices. */
	public void setList(JList l) {
		list = l; setViewportView(list);
	}

	/** Display the Assistant on the designated coordinates. */
	public void display(int x, int y) {
		adjustOffset(father); // determine the offset precisely
		setLocation(o.x + x, o.y + y); 
		setVisible(true);
	}

	/** Display the Assistant on the designated coordinates if it is not already visible. */
	public void display(Rectangle r) {
		display(r.x, r.y);
	}
	
	/** Adjust the offset of the Visual Assistant according to the position of the parent components. */
	public void adjustOffset(Component c) {
		father = c;
		o =  new Point(0, 0); 
		while(c != null && !(c instanceof JRootPane)) {
			Point p = c.getLocation();
			o.translate(p.x, p.y);
			/* The Visual Assistant must take an extra effort to determine the
			 * offset where it should appear. A JScrollPane is a very special obstacle 
			 * as it shows only a part of the decorated component. 
			 * That's why we must subtract the view position of the viewport 
			 * from the offset to obtain the correct result.
			 */
			if(c instanceof JScrollPane) {
				Point q = ((JScrollPane)c).getViewport().getViewPosition();
				p.translate(-q.x, -q.y);
			}
			c = c.getParent();			
		}
	}

	/** Set selected index and ensure it is visible. */
	public void setSelectedIndex(int index) {
		list.setSelectedIndex(index); list.ensureIndexIsVisible(index);
	}

	/** Set the first suitable value starting with the <code>prefix</code>. */
	public void setSelectedValue(String prefix) {
		setSelectedIndex(list.getNextMatch(prefix, 0, Position.Bias.Forward));
	}
	
	/** Get the maximum index. */
	public int getMaxIndex() {
		return list.getModel().getSize() - 1;
	}

}