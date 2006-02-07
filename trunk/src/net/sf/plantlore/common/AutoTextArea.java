package net.sf.plantlore.common;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;


/**
 * AutoTextArea is an extension of the default <code>JTextArea</code>. It operates in two modes:
 * <dl>
 * <dt>FREE_ROAM</dt>
 * <dd>This mode allows the User to move freely through entries with minor limitations.
 * 	<ul>
 * 		<li><code>Up</code>, <code>Down</code>,<code>Left</code>, 
 * 			<code>Right</code>, <code>Home</code>, <code>End</code>,
 * 			<code>Page Up</code>, <code>Page Down</code> and
 * 			mouse clicks behave like when in <code>JTextArea</code>,
 * 		</li>
 * 		<li><code>Escape</code> and <code>Enter</code> are disabled,</li>
 * 		<li><code>Tab</code> transfers the focus to the next component,</li>
 * 		<li><code>Delete</code>, <code>Backspace</code> and any
 * 			other key switch the <code>AutoTextArea</code> to the ASSISTANCE mode.</li>
 * 	</ul>
 * </dd>
 * 
 * <dt>ASSISTANCE</dt>
 * <dd> In the ASSISTANCE mode the <i>Visual Assistant</i> is visible 
 * 	and the behaviour of most keys is limited or changed.
 * 	<ul>
 * 		<li><code>Left</code>, <code>Right</code>, <code>Home</code>, <code>End</code>
 * 			will not allow the User to leave the current line,</li> 
 * 		<li><code>Up</code>, <code>Down</code>, <code>Page Up</code>, <code>Page Down</code>
 * 			select the next/previous choice in the list of the Visual Assistant.</li>
 * 		<li><code>Escape</code> discards the current selection, deletes the current line, and
 * 			switches the AutoTextArea to the FREE ROAM mode.</li>
 * 		<li><code>Enter</code> picks the current selection, places it on the current line, moves
 * 			the caret to the next line, and switches the AutoTextArea to FREE ROAM mode.</li>
 * 		<li>mouse clicks & drags are disabled, the User cannot move the caret to another line.</li>
 * 		<li><code>Tab</code> "types" all characters until the space or end of record occurs.
 * 			For example: the User types "Panth", the Visual Assistant shows "Panthera Tigris", 
 * 			the User hits <code>Tab</code>, and "Panthera " appears on the line, the User hits <code>Tab</code>
 * 			again and "Panthera Tigris" appears on the line.
 * 			</li>
 * 		<li><code>Delete</code> removes the rest of the line. If the line is already empty,
 * 			<code>Delete</code> removes the whole line, and switches AutoTextArea to the FREE ROAM mode.</li>
 * 		<li><code>Backspace</code> removes the rest of the line and one character in front of the caret.
 * 			If the line is already emtpy, <code>Backspace</code> deletes the current line, moves the caret
 * 			to the end of the previous line (if possible), and switches AutoTextArea to the FREE ROAM mode.</li>
 * 	</ul>
 * 	The User is prevented from entering anything that is not part of the list of choices. The Visual assistant
 * 	displays the first choice that starts with the text the User has typed.
 * </dd>
 * </dl> 
 * <br/>
 * The AutoTextArea assumes the list of choices is sorted according to the selected language
 * (the behaviour of the Assistant may appear confusing otherwise).
 * <br/>
 * AutoTextArea <b>does not provide</b> 
 * <ul>
 * <li>size restriction - "unlimited" number of records can be entered,</li>
 * <li>duplicity checks - one record may be entered twice,</li> 
 * <li>scrolling - use JScrollPane with AutoTextArea explicitly,</li>
 * <li>smart assistant placement - the Visual Assistant may not be completely visible in some cases.</li>
 * </ul>   
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 */
public class AutoTextArea extends JTextArea implements KeyListener, FocusListener, MouseListener {
	
	/*======================================================================
	 	Members
	  ======================================================================*/
	
	/** The visual assistant. */
	protected Assistant		assistant;
	
	/** The possible modes of text insertion. */
	protected enum Mode { FREE_ROAM, ASSISTANCE }
	
	/** The current mode of text insertion. */
	protected Mode mode = Mode.FREE_ROAM;
	
	/** The number of items skipped in the assistant when PAGE_DOWN/PAGE_UP is pressed. */
	private int step = 10;
	

	/*======================================================================
	 	Keyboard behaviour
	  ======================================================================*/
	
	private KeyEvent enterEmulator = new KeyEvent(this, KeyEvent.KEY_PRESSED, (long)0, 0, KeyEvent.VK_ENTER, (char)KeyEvent.VK_ENTER);
	private KeyEvent escapeEmulator = new KeyEvent(this, KeyEvent.KEY_PRESSED, (long)0, 0, KeyEvent.VK_ESCAPE, (char)KeyEvent.VK_ESCAPE);
		
	/** Keyboard unleashed - see the JavaDoc of the AutoTextArea class for detailed description. */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		// --- Free roam mode ---
		if (mode == Mode.FREE_ROAM) {
			switch (key) {
			case KeyEvent.VK_TAB: // transfer the focus to another component
				if((e.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) > 0) transferFocusBackward();
				else transferFocus();				
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				e.consume(); // no further processing of this event
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_PAGE_UP:
			case KeyEvent.VK_HOME:
			case KeyEvent.VK_END:
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_ALT:
			case KeyEvent.VK_CONTROL:
				return; // "call" the default behaviour
			}
		}
		
		// Update the information about the current line (the one with the caret).
		int offset = getCaretPosition(), line, start, end;
		try {
			line = getLineOfOffset(offset); start = getLineStartOffset(line); end = getLineEndOffset(line);
		} catch(BadLocationException b) { return; }
		
		// Consume the event -> no further processing 
		e.consume();
		
		boolean eoln = false;
		String prefix = ""; // contents of the current line
		try { prefix = getText(start, end - start); } catch(BadLocationException b) {}
		if(prefix.endsWith("\n")) { end--; eoln = true; }
		
		// The Assistant's list 
		JList list = assistant.getList();
		
		// --- Assistance mode ---
		if(mode == Mode.ASSISTANCE) {
			switch (key) {
			case KeyEvent.VK_TAB: // autocomplete the entry
				if(!list.isSelectionEmpty()) {
					String s = (String)list.getSelectedValue();
					int i = s.indexOf(KeyEvent.VK_SPACE, offset - start) + 1;
					if(i <= 0) i = s.length();
					replaceRange(s.substring(0, i), start, end);
				}
				return;
			case KeyEvent.VK_UP: // select the previous choice
				assistant.setSelectedIndex(Math.max(list.getSelectedIndex() - 1, 0));
				return;
			case KeyEvent.VK_PAGE_UP: // select the previous choice
				assistant.setSelectedIndex(Math.max(list.getSelectedIndex() - step, 0));
				return;
			case KeyEvent.VK_DOWN: // select the next choice
				assistant.setSelectedIndex(Math.min(list.getSelectedIndex() + 1, assistant.getMaxIndex()));
				return;
			case KeyEvent.VK_PAGE_DOWN: // select the next choice
				assistant.setSelectedIndex(Math.min(list.getSelectedIndex() + step, assistant.getMaxIndex()));
				return;
			case KeyEvent.VK_LEFT: // forbid the caret to leave the current line
				if(start < offset) setCaretPosition(offset - 1);
				return;
			case KeyEvent.VK_RIGHT: // forbid the caret to leave the current line
				if(offset < end) setCaretPosition(offset + 1);
				return;
			case KeyEvent.VK_HOME: // set the caret to the start of the current line
				setCaretPosition(start);
				return;
			case KeyEvent.VK_END: // set the caret to the end of the current line
				setCaretPosition(end);
				return;
			case KeyEvent.VK_ESCAPE: // discard the current selection, delete the line, switch to FreeRoam
				mode = Mode.FREE_ROAM;
				assistant.setVisible(false);
				replaceRange("", start, end + (eoln ? 1 : 0));
				return;
			case KeyEvent.VK_ENTER: // insert the current selection, switch to the FreeRoam
				mode = Mode.FREE_ROAM;
				assistant.setVisible(false);
				if(eoln) end++;
				if(list.isSelectionEmpty()) replaceRange("", start, end);
				else replaceRange(list.getSelectedValue() + "\n", start, end);
				return;
			}
		}
		
		// Mode switchers - DEL, BACKSPACE, ANY_OTHER_KEY
		mode = Mode.ASSISTANCE;
		if(!assistant.isVisible())
			try {
				// Place the assistant below the current line
				Rectangle r = modelToView(start);
				assistant.display(r.x + 5, r.y + this.getFont().getSize() + 5); // with respect of the size of the font
			} catch (Exception b) { return; }
			
		String σ = prefix.substring(0, offset - start);
		
		switch(key) {
		case KeyEvent.VK_DELETE: // delete
			if(start == end) { 
				replaceRange("", start, end + (eoln ? 1 : 0));
				mode = Mode.FREE_ROAM;
				assistant.setVisible(false);
			} else {
				replaceRange("", offset, end);
				assistant.setSelectedValue(σ);
			}
			return;
		case KeyEvent.VK_BACK_SPACE: // backspace
			if(offset > start) {
				replaceRange("", offset - 1, end);
				assistant.setSelectedValue(σ.substring(0, σ.length() - 1));
			} else {
				replaceRange("", start, end + (eoln ? 1 : 0));
				if(start > 0) setCaretPosition(start - 1);
				mode = Mode.FREE_ROAM;
				assistant.setVisible(false);
			}
			return;
		default: // all other keys -> select the first suitable choice
			int i = list.getNextMatch(σ + e.getKeyChar(), 0, Position.Bias.Forward);
			if(i < 0) {
				assistant.setSelectedValue(σ);
				replaceRange(((String)list.getSelectedValue()).substring(0, offset - start), start, end);
			} else {
				assistant.setSelectedIndex(i);
				replaceRange(((String)list.getSelectedValue()).substring(0, offset - start + 1), start, end);
			}
		}
	}
	
	
	
	
	/*======================================================================
	 	FocusLost & Mouse behaviour 
	  ======================================================================*/
	
	/** Disable mouse click events processing while in the ASSISTANCE mode. */
	@Override
	protected void processMouseEvent(MouseEvent e) { if(mode == Mode.FREE_ROAM) super.processMouseEvent(e); }
	
	/** Disable mouse motion events processing while in the ASSISTANCE mode. */
	@Override
	protected void processMouseMotionEvent(MouseEvent e) { if(mode == Mode.FREE_ROAM) super.processMouseMotionEvent(e); }

	/** Behave as if ENTER were pressed. */
	public void mouseReleased(MouseEvent e) { keyPressed(enterEmulator); }
	
	/** Behave exactly as if ESCAPE was pressed. */
	public void focusLost(FocusEvent e) { keyPressed(escapeEmulator); }
	
	
	
	/*======================================================================
	 	Constructors
	  ======================================================================*/
	
	/**
	 * Create the AutoTextArea with a Visual Assistant that helps with choosing items from the
	 * list of available (allowed) strings.
	 * 
	 * @param choices	List of available (allowed) strings. 
	 * @param frame	The parent frame which the AutoTextArea is added to.  
	 */
	public AutoTextArea(String[] choices, JFrame frame) {
		this(choices, frame.getLayeredPane());
	}
	
	/**
	 * Create the AutoTextArea with a Visual Assistant that helps with choosing items from the
	 * list of available (allowed) strings.
	 * 
	 * @param choices	List of available (allowed) strings. 
	 * @param dialog	The parent dialog which the AutoTextArea is added to.  
	 */
	public AutoTextArea(String[] choices, JDialog dialog) {
		this(choices, dialog.getLayeredPane());
	}
	
	/**
	 * Create the AutoTextArea with a Visual Assistant that helps with choosing items from the
	 * list of available (allowed) strings.
	 * 
	 * @param choices	List of available (allowed) strings. 
	 * @param container	The container which the AutoTextArea is added to.  
	 */
	public AutoTextArea(String[] choices, Container container) {
		assistant = new Assistant(choices, container);
		assistant.getList().addMouseListener(this);
		addKeyListener(this); addFocusListener(this);
	}
	
	
	
	/*======================================================================
	 	Unimplemented & uninteresting methods
	  ======================================================================*/
	
	/** Unimplemented. */
	public void focusGained(FocusEvent e) {}
	/** Consume all events. */
	public void keyTyped(KeyEvent e) { e.consume(); }
	/** Consume all events. */
	public void keyReleased(KeyEvent e) { e.consume(); }
	/** Unimplemented. */
	public void mouseClicked(MouseEvent e) {}
	/** Unimplemented. */
	public void mousePressed(MouseEvent e) {}
	/** Unimplemented. */
	public void mouseEntered(MouseEvent e) {}
	/** Unimplemented. */
	public void mouseExited(MouseEvent e) {}

}
