package net.sf.plantlore.common;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;



/**
 * AutoComboBox is an extension of the standard JComboBox.
 * The AutoComboBox can run in two modes
 * <ul>
 * <li><b>strict</b> ~
 * It prevents the User from entering an invalid or incomplete entry,
 * and ensures that something is always selected.
 * The list displayes the current selection as the User types, 
 * the automatic search is case insensitive. 
 * </li>
 * <li><b>"benevolent"</b> ~ 
 * The User can enter anything (not just something
 * that matches one of the choices in the list). The component acts as a 
 * guide this time - the list displayes the current selection when the User types
 * as long as it belongs to the list, the automatic search is case sensitive. 
 * However, the User can enter an already unknown text.
 * </li>
 * </ul>
 * <br/>
 * Inspired by the source code created by Stephane Crasnier. 
 * <br/>
 * If you plan to use the AutoComboBox in a <b>nonstrict</b> mode
 * you must use the <code>getValue()</code> method in order to
 * obtain the value the User entered instead of the <code>getSelectedItem()</code>.
 * The reason is simple - if the User types a new text (a text that is not in the 
 * list of available values) the value cannot be marked as selected since it is not in the list!
 * <br/>
 * Example:
 * <pre>
 * AutoComboBox acb= new AutoComboBox(<i>values</i>);
 * 
 * acb.addActionListener(new AbstractAction(){
 *			private int n = 0;
 *
 *			public void actionPerformed(ActionEvent e) {
 *				System.out.println(++n + ". " + acb.getValue());
 *			}
 *		});
 *</pre>
 * 
 * @author Erik Kratochvíl
 * @author Jakub Kotowski (several bugfixes: MetalComboBoxEditor, ...)
 * @version 3.0
 */
public class AutoComboBoxNG3 extends JComboBox {
	
	/**
	 * The mode of this component. 
	 * Strict means "nothing except allowed values is accepted".
	 */
	protected boolean strict = true;
	
	protected int capacity = 32;
	
	
	/**
	 * Create the AutoComboBox with given array of values.
	 * The AutoComboBox is in the strict mode by default.
	 * 
	 * @param values	<b>Sorted</b> list of available values (items).
	 */
	public AutoComboBoxNG3(Object[] values) {
		super(values); // base class constructor
		setEditable(true);
		setEditor( new EnhancedEditor() );
		JTextField e = (JTextField)(editor.getEditorComponent());
		AutoDocument a = new AutoDocument(); // change the model ~~> AutoDocument 
		e.setDocument(a); e.addKeyListener(a); e.addFocusListener(a);
                //setBorder(BorderFactory.createLineBorder(Color.BLUE));
                //updateUI();
	}
	
	/**
	 * Create the AutoComboBox with given array of values
	 * 
	 * @param values	<b>Sorted</b> list of available values (items).
	 * @param strict	True if the AutoComboBox should not accept values (items)
	 *        that are not in the list of available values.
	 */
	public AutoComboBoxNG3(Object[] values, boolean strict) {
		this(values);
		this.strict = strict;
	}
	
	/**
	 * Set the visibility of the popup list.
	 */
	@Override
	public void setPopupVisible(boolean visibility) {
		if(isShowing()) // popup cannot be displayed while the component is invisible
			super.setPopupVisible(visibility);
	}
	
	
	public AutoComboBoxNG3() {
		this(new String[] { "" });		
	} 
	
	public AutoComboBoxNG3(boolean strict) {
		this(new String[] { "" });
                this.strict = strict;
	} 
	
	public void addItems(Object[] items) {
		for(Object item : items)
			if(item != null) 
				this.addItem(item);
	}
	

	/**
	 * Enhanced ComboBox Editor returns the selected item based 
	 * on the partial information from the current text.
	 * 
	 * @author Erik Kratochvíl
	 */
	private class EnhancedEditor extends MetalComboBoxEditor {
		@Override
		public Object getItem() {
			String prefix = editor.getText();
			for(int i = 0; i < getItemCount(); i++) {
				Object value = getItemAt(i); 
				String item = value.toString();
				if(item.length() >= prefix.length() && item.startsWith(prefix))
					return value;
			}
			return (strict ? null : prefix);
		}               
        }
        
	/**
	 * @return
	 * The return value is based on the mode. 
	 * <ul>
	 * <li>
	 * In the strict mode the returned value is the
	 * same as if the <code>getSelectedItem()</code> is called.
	 * </li>
	 * <li>
	 * In the non-strict mode there are two possible results:
	 * (1) if the value matches one of the available values, that value is returned;
	 * (2) the text inserted by the User is returned otherwise. 
	 * </li>
	 * </ul>
	 */
	public Object getValue() {
		return editor.getItem();
	}

	
	/**
	 *	The Enhanced document supports the automatic record search. 
	 */
	private class AutoDocument extends PlainDocument 
	implements KeyListener, FocusListener {
		
		private boolean deflect = false;
		private Object previousValue;
		private String previousPrefix;

		private boolean keyPressed = false;
		
		/** 
		 * Select the first suitable item beginnig with <code>prefix</code>.
		 * 
		 * @param prefix		The prefix of the string. 
		 */
		private void setMatch(String prefix) {
				try {
					boolean noTrim = (prefix == null);
					if (prefix == null) prefix = getText(0, getLength());
					
					// Find the first suitable choice and select it.
					for(int i = 0; i < getItemCount(); i++) {
						Object selected = getItemAt(i);
						String item = selected.toString(); // test the i-th choice
						if( prefix.length() <= item.length() && 
								(strict ? prefix.equalsIgnoreCase(item.substring(0, prefix.length())) :
									prefix.equals(item.substring(0, prefix.length())) ) ) {
							super.remove(0, getLength());
							if( !noTrim ) item = item.substring(0, prefix.length());
							super.insertString(0, item, null); // rewrite the text
							
							if(previousValue != selected) { // the previous value and the current value differ
								previousValue = selected;
								if(keyPressed || !prefix.equals(selected)) 
									setSelectedItem(selected);
							}
							keyPressed = false;
							return;
						}
					}
				
					if(!strict && !prefix.equals(previousPrefix) && prefix.length() < capacity) { // non-strict mode allows entering an unknown value 
						super.remove(0, getLength());
						super.insertString(0, prefix, null);
						previousPrefix = prefix;
						if(keyPressed) {
							keyPressed = false;
							fireActionEvent();
						}
					}
					
				} catch (BadLocationException e) {} 
		}
		
		
		@Override
		public void insertString(int offset, String insert, AttributeSet attr) throws BadLocationException {}
		
		@Override
		public void remove(int offset, int length) throws BadLocationException {
			if(!deflect) {
				deflect = true;
				if(keyPressed) setPopupVisible(true);
				setMatch(getText(0, offset));
				deflect = false;
			}
		}
		
		@Override
		public void replace(int offset, int length, String text, AttributeSet attr) throws BadLocationException {
			if(!deflect) {
				deflect = true;
				if(keyPressed) setPopupVisible(true);
				setMatch(getText(0,offset) + text);
				deflect = false;
			}
		}

		private final Set<Integer> ignore = new HashSet<Integer>( Arrays.asList(
						KeyEvent.VK_UP, KeyEvent.VK_DOWN, 
						KeyEvent.VK_HOME, KeyEvent.VK_END,
						KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP,
						KeyEvent.VK_ENTER, KeyEvent.VK_TAB ) );
		
		/** Ensure something got selected. */
		public void keyPressed(KeyEvent e) {
			Integer c = e.getKeyCode();
			keyPressed = false;
			if(c == KeyEvent.VK_ENTER) {
				setMatch(null);
				setPopupVisible(false);
				e.consume();
			} else if( !ignore.contains(c) ) keyPressed = true; 
		}
		
		/** Make sure something is selected even if the AutoComboBox loses focus. */
		public void focusLost(FocusEvent e) {
			if(strict) {
				keyPressed = false;
				setMatch(null);
				setPopupVisible(false);
			}
		}

		
		
		/* Bunch of uninteresting methods... */
		public void keyTyped(KeyEvent arg0) {}
		public void keyReleased(KeyEvent arg0) {}
		public void focusGained(FocusEvent arg0) {}
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

}
