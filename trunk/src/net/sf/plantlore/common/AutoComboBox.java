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
 * It prevents the User from entering an invalid or incomplete entry,
 * and ensures that something is always selected.
 * <br/>
 * Heavily refined from the source code created by Stephane Crasnier. 
 * <br/>
 * I wish I wrote it myself from scratch :/
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since The beginning of time.
 */
public class AutoComboBox extends JComboBox {
	

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
	
	
	
	private class AutoDocument extends PlainDocument implements KeyListener, FocusListener {
		
		/** Prevent entering the setMatch method recursively. */
		private boolean prevent = false;
		
		/** 
		 * Select the first suitable choice beginnig with <code>prefix</code>.
		 * 
		 * @param prefix		The prefix of the string. 
		 * @param partial	Display only partial string? 
		 */
		synchronized private void setMatch(String prefix, boolean partial) {
			if (!prevent) {
				prevent = true;
				setPopupVisible(partial); // make sure popup is/isn't visible
				try {
					if (prefix == null) prefix = getText(0, getLength());
					// Find the first suitable choice and select it.
					for(int i = 0; i < getItemCount(); i++) {
						String item = (String) getItemAt(i); // test the i-th choice
						if( prefix.length() <= item.length() && prefix.equalsIgnoreCase(item.substring(0, prefix.length())) ) {
							setSelectedIndex(i); // CRAP! This method calls remove() & insertString()!!!
							if(partial) item = item.substring(0, prefix.length()); // trim the string
							super.remove(0, getLength());
							super.insertString(0, item, null); // rewrite the text
							break;
						}
					}
				} catch (BadLocationException e) {} 
				finally { prevent = false; }
			}
		}
		
		@Override
		public void insertString(int offset, String insert, AttributeSet attr) throws BadLocationException {
			setMatch(getText(0, offset) + insert, true);
		}
		
		@Override
		public void remove(int offset, int length) throws BadLocationException {
			setMatch(getText(0, offset), true);
		}

		/** Ensure something got selected. */
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) setMatch(null, false);
		}
		
		/** Make sure something is selected even if the AutoComboBox loses focus. */
		public void focusLost(FocusEvent e) {
			setMatch(null, false); 
		}

		/* Bunch of uninteresting methods... */
		public void keyTyped(KeyEvent arg0) {}
		public void keyReleased(KeyEvent arg0) {}
		public void focusGained(FocusEvent arg0) {}
	}
}
