import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * AutoComboBox is an extension of the standard JComboBox.
 * It prevents the User from entering an invalid or incomplete entry,
 * and ensures that something is always selected.
 * <br/>
 * Refined from the source code created by Stephane Crasnier. 
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since The beginning of time.
 */
public class AutoComboBox extends JComboBox {
	
	/** List of available choices. */
	protected String[]		list;
	
	/**
	 * The default constructor.
	 * @param choices	<b>Sorted</b> list of available choices (items).
	 */
	public AutoComboBox(String[] choices) {
		super(choices); // base class constructor
		
		list = choices; // FIXME: proper copy!
		setEditable(true);
		
		JTextField editor = (JTextField)getEditor().getEditorComponent();
		AutoDocument a = new AutoDocument(); // change the model ~~> AutoDocument ext PlainDocument 
		editor.setDocument(a); editor.addKeyListener(a); editor.addFocusListener(a);
	}
	
	
	
	private final class AutoDocument extends PlainDocument implements KeyListener, FocusListener {
		
		/** Replace the string. */
		private void rewrite(String text) throws BadLocationException {
			if(text != null) { super.remove(0, getLength()); super.insertString(0, text, null); }
		}
		
		/** Select first suitable choice beginnig with <code>start</code>. */
		private String selectFirst(String start) {
			String insensitive = start.toLowerCase(); // case insensitive
			for(int i = 0; i < list.length; i++) 
				if(list[i].toLowerCase().startsWith(insensitive)) {
					setSelectedIndex(i);
					return list[i].substring(0, start.length());
				}
			return null;
		}
		
		/** Make sure something gets always selected. */
		private void selectItem() {
			try {
				if(getSelectedIndex() != -1) rewrite((String)getSelectedItem());
				else rewrite((String)getItemAt(0));
			} catch(BadLocationException e) {}
		}
		
		@Override
		public void insertString(int offset, String inserted, AttributeSet attr) throws BadLocationException {
			showPopup(); rewrite(selectFirst(getText(0, offset) + inserted));
		}
		
		@Override
		public void remove(int offset, int length) throws BadLocationException {
			if (offset == 0 && length == getLength()) super.remove(offset, length);
			else { showPopup(); rewrite(selectFirst(getText(0, offset))); }
		}

		
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER && isPopupVisible()) selectItem();
		}
		
		public void focusLost(FocusEvent e) {
			selectItem();
		}

		/* Bunch of uninteresting methods... */
		public void keyTyped(KeyEvent arg0) {}
		public void keyReleased(KeyEvent arg0) {}
		public void focusGained(FocusEvent arg0) {}
	}

	
	/**
	 * Test.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try { UIManager.setLookAndFeel(lookAndFeel); }
        catch (Exception e) { JFrame.setDefaultLookAndFeelDecorated(true); }
		
		JFrame f = new JFrame();
		String[] ch = { "Daniel Jackson", "Etrachlorethylen", "Jack O'Neill", "Samantha Carter", "Tetrachlorethylen", "Tetraethylen", "Thor", "Ty'alc" };
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new AutoComboBox(ch), BorderLayout.NORTH);
		f.getContentPane().add(new JButton("Done"), BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);
	}

}
