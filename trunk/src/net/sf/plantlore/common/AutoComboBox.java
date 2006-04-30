package net.sf.plantlore.common;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;



/**
 * AutoComboBox is an extension of the standard JComboBox.
 * The AutoComboBox can run in two modes now
 * <ul>
 * <li><b>strict</b> ~
 * It prevents the User from entering an invalid or incomplete entry,
 * and ensures that something is always selected.</li>
 * <li><b>benevolent</b> ~ The User can enter anything (not just something
 * that matches one of the choices in the list). The component acts as a 
 * guide this time.</li>
 * </ul>
 * <br/>
 * Heavily refined from the source code created by Stephane Crasnier. 
 * <br/>
 * The use of the benevolent behaviour is discouraged as it contradicts the sole purpose 
 * of this component. If you want to allow the User 
 * to leave this field blank, use a special
 * record "------" or "not specified" instead.
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @version 1.1 jlist doesn't get updated when the user types
 * @since The beginning of time.
 */
public class AutoComboBox extends JComboBox {
	
	protected boolean allowNew = false;
	protected int capacity = 32;
	

	

	/**
	 * Create the AutoComboBox with given array of choices.
	 * 
	 * @param choices	<b>Sorted</b> list of available choices (items).
	 */
	public AutoComboBox(Object[] choices) {
		super(choices); // base class constructor
		setEditable(true);
		
		JTextField editor = (JTextField)getEditor().getEditorComponent();
		AutoDocument a = new AutoDocument(); // change the model ~~> AutoDocument 
		editor.setDocument(a); editor.addKeyListener(a); editor.addFocusListener(a);
	}
	
	
	public AutoComboBox() {
		this(new String[] { null });		
	} 
	
	public void addItems(Object[] items) {
		for(Object item : items)
			if(item != null) 
				this.addItem(item);
	}

	
	/**
	 * Specify whether the component should also accept strings that are not part of the list of choices.
	 * 
	 * @param strict  True (ie. be strict!) if new strings are not allowed.
	 */
	public void setStrict(boolean strict) { allowNew = !strict; }
	
	/**
	 * Set the maximum number of characters the user can insert into the text field.
	 * 
	 * @param capacity The maximum number of characters the user can type.
	 */
	public void setCapacity(int capacity) { this.capacity = capacity; }
	
	
	
	private class AutoDocument extends PlainDocument implements KeyListener, FocusListener {
		
		
		
		/** 
		 * Select the first suitable choice beginnig with <code>prefix</code>.
		 * 
		 * @param prefix		The prefix of the string. 
		 */
		synchronized private void setMatch(String prefix) {
				try {
					boolean noMatch = true;
					if (prefix == null) prefix = getText(0, getLength());
					
					// Find the first suitable choice and select it.
					for(int i = 0; i < getItemCount(); i++) {
						String item = getItemAt(i).toString(); // test the i-th choice
						if( prefix.length() <= item.length() && prefix.equalsIgnoreCase(item.substring(0, prefix.length())) ) {
							super.remove(0, getLength());
							super.insertString(0, item.substring(0, prefix.length()), null); // rewrite the text
							noMatch = false;
							break;
						}
					}
					if(allowNew && noMatch && prefix.length() < capacity) {
						super.remove(0, getLength());
						super.insertString(0, prefix, null);
					}
				} catch (BadLocationException e) {} 
		}
		
		@Override
		public void insertString(int offset, String insert, AttributeSet attr) throws BadLocationException {
			setMatch(getText(0, offset) + insert);
		}
		
		@Override
		public void remove(int offset, int length) throws BadLocationException {
			setMatch(getText(0, offset));
		}

		/** Ensure something got selected. */
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) setMatch(null);
		}
		
		/** Make sure something is selected even if the AutoComboBox loses focus. */
		public void focusLost(FocusEvent e) {
			setMatch(null); 
		}

		/* Bunch of uninteresting methods... */
		public void keyTyped(KeyEvent arg0) {}
		public void keyReleased(KeyEvent arg0) {}
		public void focusGained(FocusEvent arg0) {}
	}

	
}
