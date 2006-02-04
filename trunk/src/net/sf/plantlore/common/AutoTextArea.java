import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.UIManager;
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
 * AutoTextArea <b>does not provide</b> 
 * <ul>
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
	protected Mode μ = Mode.FREE_ROAM;
	
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
		
		// Mode specifics - ESC, ENTER, UP, DOWN, LEFT, RIGHT, PGUP, PGDOWN
		if (μ == Mode.FREE_ROAM) {
			switch (key) {
			case KeyEvent.VK_TAB:
				if((e.getModifiers() & KeyEvent.SHIFT_DOWN_MASK) > 0) transferFocusBackward();
				else transferFocus();				
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				e.consume(); // disable
				return;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_PAGE_UP:
			case KeyEvent.VK_HOME:
			case KeyEvent.VK_END:
			case KeyEvent.VK_SHIFT:
				// default behaviour
				return;
			}
		}
		
		int offset = getCaretPosition(), line, start, end;
		try {
			line = getLineOfOffset(offset); start = getLineStartOffset(line); end = getLineEndOffset(line);
		} catch(BadLocationException b) { return; }
		
		e.consume(); // noone shall mess with me controlling this component!
		
		boolean eoln = false;
		String prefix = ""; // just to please the compiler
		try { prefix = getText(start, end - start); } catch(BadLocationException b) { System.out.println("<!> Well, something went wrong, I wish I knew what it was... :/"); }
		if(prefix.endsWith("\n")) { end--; eoln = true; }// watch this!
		
		if(μ == Mode.ASSISTANCE) {
			switch (key) {
			case KeyEvent.VK_TAB:
				if(!assistant.isSelectionEmpty()) {
					String s = assistant.getSelectedValue();
					int i = s.indexOf(KeyEvent.VK_SPACE, offset - start) + 1;
					if(i <= 0) i = s.length();
					replaceRange(s.substring(0, i), start, end);
				}
				return;
			case KeyEvent.VK_UP:
				if(assistant.isSelectionEmpty()) assistant.setSelectedIndex(0);
				else assistant.setSelectedIndex(Math.max(assistant.getSelectedIndex() - 1, 0));
				return;
			case KeyEvent.VK_PAGE_UP:
				if(assistant.isSelectionEmpty()) assistant.setSelectedIndex(0);
				else assistant.setSelectedIndex(Math.max(assistant.getSelectedIndex() - step, 0));
				return;
			case KeyEvent.VK_DOWN:
				if(assistant.isSelectionEmpty()) assistant.setSelectedIndex(0);
				else assistant.setSelectedIndex(Math.min(assistant.getSelectedIndex() + 1, assistant.getMaxIndex()));
				return;
			case KeyEvent.VK_PAGE_DOWN:
				if(assistant.isSelectionEmpty()) assistant.setSelectedIndex(step);
				else assistant.setSelectedIndex(Math.min(assistant.getSelectedIndex() + step, assistant.getMaxIndex()));
				return;
			case KeyEvent.VK_LEFT:
				if(start < offset) setCaretPosition(offset - 1);
				return;
			case KeyEvent.VK_RIGHT:
				if(offset < end) setCaretPosition(offset + 1);
				return;
			case KeyEvent.VK_HOME:
				setCaretPosition(start);
				return;
			case KeyEvent.VK_END:
				setCaretPosition(end);
				return;
			case KeyEvent.VK_ESCAPE:
				//if(!prefix.equalsIgnoreCase(assistant.getSelectedValue())) 
				μ = Mode.FREE_ROAM;
				assistant.setVisible(false);
				replaceRange("", start, end + (eoln ? 1 : 0));
				return;
			case KeyEvent.VK_ENTER:
				μ = Mode.FREE_ROAM;
				assistant.setVisible(false);
				if(eoln) end++;
				if(assistant.isSelectionEmpty()) replaceRange("", start, end);
				else replaceRange(assistant.getSelectedValue() + "\n", start, end);
				return;
			}
		}
		
		// Mode switchers - DEL, BACKSPACE, ANY_OTHER_KEY
		μ = Mode.ASSISTANCE;
		try { assistant.show(modelToView(start)); } catch(BadLocationException b) {}
		String σ = prefix.substring(0, offset - start);
		
		
		switch(key) {
		case KeyEvent.VK_DELETE:
			if(start == end) { 
				replaceRange("", start, end + (eoln ? 1 : 0));
				μ = Mode.FREE_ROAM;
				assistant.setVisible(false);
			} else {
				replaceRange("", offset, end);
				assistant.setSomething(σ);
			}
			return;
		case KeyEvent.VK_BACK_SPACE:
			if(offset > start) {
				replaceRange("", offset - 1, end);
				assistant.setSomething(σ.substring(0, σ.length() - 1));
			} else {
				replaceRange("", start, end + (eoln ? 1 : 0));
				if(start > 0) setCaretPosition(start - 1);
				μ = Mode.FREE_ROAM;
				assistant.setVisible(false);
			}
			return;
		default: // ALL OTHER KEYS
			int i = assistant.getNextMatch(σ + e.getKeyChar(), Position.Bias.Forward);
			if(i < 0) {
				assistant.setSomething(σ);
				replaceRange(assistant.getSelectedValue().substring(0, offset - start), start, end);
			} else {
				assistant.setSelectedIndex(i);
				replaceRange(assistant.getSelectedValue().substring(0, offset - start + 1), start, end);
			}
		}
	}
	
	
	
	/*======================================================================
	 	FocusLost behaviour
	  ======================================================================*/
	
	/** 
	 * Deal with the loss of focus.
	 * 
	 * While in the ASSISTANCE mode, the current selection is discarded, 
	 * the line is deleted, and the mode is switched to FREE_ROAM. 
	 * 
	 * Nothing happens when the FREE_ROAM mode is active.
	 */
	public void focusLost(FocusEvent e) { keyPressed(escapeEmulator); }
	
	
	/*======================================================================
	 	Mouse behaviour 
	  ======================================================================*/
	
	/** Disable mouse click events processing while in the ASSISTANCE mode. */
	@Override
	protected void processMouseEvent(MouseEvent e) { if(μ == Mode.FREE_ROAM) super.processMouseEvent(e); }
	
	/** Disable mouse motion events processing while int the ASSISTANCE mode. */
	@Override
	protected void processMouseMotionEvent(MouseEvent e) { if(μ == Mode.FREE_ROAM) super.processMouseMotionEvent(e); }

	/** Behave as if ENTER were pressed. */
	public void mouseReleased(MouseEvent e) { keyPressed(enterEmulator); }
	
	
	
	/**
	 * Create the AutoTextArea with a Visual Assistant that helps with choosing items from the
	 * list of available (allowed) strings.
	 * 
	 * @param choices	List of available (allowed) strings. 
	 * @param frame	The parent frame which the AutoTextArea is added to.  
	 */
	public AutoTextArea(String[] choices, JFrame frame) {
		assistant = new Assistant(choices, frame.getLayeredPane());
		assistant.addMouseListener(this);
		addKeyListener(this); addFocusListener(this);
	}
	
	
	
	/** The visual assistant. */
	private final class Assistant extends JScrollPane {
		
		private JList list;
				
		public Assistant(String[] choices, JLayeredPane pane) {
			super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			list = new JList(choices);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setFocusable(false);

			setViewportView(list); setVisible(false); setSize(200, 100); setLocation(0, 0);
			pane.add(this, JLayeredPane.POPUP_LAYER); // pop up above everything
		}
		
		@Override
		public void addMouseListener(MouseListener m) { list.addMouseListener(m); }
		public void show(Rectangle r) {
			if(!isVisible() && r != null) { setLocation(r.x + 5, r.y + 15); setVisible(true); }
		}
		public boolean isSelectionEmpty() { return list.isSelectionEmpty(); }
		public int getSelectedIndex() { return list.getSelectedIndex(); }
		public void setSelectedIndex(int index) { list.setSelectedIndex(index); list.ensureIndexIsVisible(index); }
		public String getSelectedValue() { return (String)list.getSelectedValue();}
		public int getNextMatch(String prefix, Position.Bias bias) { return list.getNextMatch(prefix, 0, bias); }
		public void setSomething(String prefix) { setSelectedIndex(getNextMatch(prefix, Position.Bias.Forward)); }
		public int getMaxIndex() { return list.getModel().getSize() - 1; }
	}
	

	

	public static void main(String[] args) {
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try { UIManager.setLookAndFeel(lookAndFeel); }
        catch (Exception e) { JFrame.setDefaultLookAndFeelDecorated(true); }
        
        String[] ch = new String[5645];
        try {
        	BufferedReader r = new BufferedReader(new FileReader("species"));
        	for(int i = 0; i < 5645; i++)
        		ch[i] = r.readLine();
        	r.close();
        } catch(IOException e) {}
        
        /*
        String[]
        	ch = {"Anubis", "Apophis", "Ayiana", "Baal", "Baldur", "Camulus", "Cronus", "Daniel Jackson",
					"Elizabeth Weir", "Freir", "George Hammond", "Hallowed are the Ori", "Heimdall", "Hermiod",
					"Chaya Sar", "Imhotep", "Jack O'Neill", "John Sheppard", "Jonas Quinn", "Khalek", "Klorel",
					"Loki", "Martoufe", "Master Bra'tac", "Nerus", "Oma Desala", "Penegal", "Qetesh",
					"Ra", "Radek Zelenka", "Replicarter", "Rodney McKay", "Ronon Dex",
					"Samantha Carter", "Sokar", "Ševron", 
					"Teyla Emmagan", "The Eight", "The Fifth", "The First", "The Fourth",
					"The Second", "The Seventh", "The Sixth", "The Third", "Thor", "Ty'alc", "Vala Mal Doran", "Yu"};
        */
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		AutoTextArea a = new AutoTextArea(ch, f); 
		a.setPreferredSize(new Dimension(400, 300));
		a.setFont(new Font("Verdana", 0, 12));
		JButton b = new JButton("Done");
		f.getContentPane().add(a, BorderLayout.CENTER);
		f.getContentPane().add(b, BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);
	}

	
	
	/*======================================================================
	 	Unimplemented & uninteresting methods
	  ======================================================================*/
	
	/** Unimplemented. */
	public void focusGained(FocusEvent e) {}
	/** 
	 * Consume all events. 
	 * @see keyPressed
	 */
	public void keyTyped(KeyEvent e) { e.consume(); }
	/** 
	 * Consume all events. 
	 * @see keyPressed
	 */
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
