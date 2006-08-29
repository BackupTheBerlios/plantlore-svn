package net.sf.plantlore.client.checklist;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.text.Position;

import net.sf.plantlore.client.resources.Resource;

/**
 * The Checklist ("Škrták" in Czech). 
 * The Checklist displays plants and allows the User to select some of them
 * easily. The User can use either a mouse or a keyboard to (de)select records.
 * The Checklist supports searching (immediately as User types).
 * <br/>
 * The Checklist can be stored (saved, created) and loaded again.
 *  
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @version 1.0
 */
public class Checklist extends JList {
	
	private String[] original;
	
	/**
	 * Create a new checklist. Displayes the supplied values in several rows and columns.
	 * 
	 * @param values	The values displayed to the User. 
	 * Each object of the field is converted to a String (using the toString() method). 
	 */
	public Checklist(Object[] values) {
		setListData(values);
		// Specify the default looks.
		setCellRenderer( new CheckCellRenderer() );
		setSelectionModel( new ToggleSelectionModel() );
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL_WRAP);
		setPrototypeCellValue("VERY LONG NAME MOMENTS AGO");
		setDragEnabled(false);
		setVisibleRowCount(20);
		
		// Remove the nasty hyperactive listeners
		for(Object listener : getListeners(KeyListener.class)) 
			removeKeyListener((KeyListener) listener);
		for(Object listener : getListeners(MouseMotionListener.class))
			removeMouseMotionListener((MouseMotionListener) listener);
		
		// Install the keyboard search.
		addKeyListener(new KeyHelper());	
	}
	
	@Override
	public void setListData(Object[] values) {
		if( !(values instanceof String[]) ) 
			for(int i = 0; i < values.length; i++) 
				values[i] = values[i].toString();
		// Insert the values to the list.
		super.setListData( values );
		// Remember the list.
		original = (String[])values;
	}
	
	/**
	 * Reset the list of items.
	 *
	 */
	public void restore() {
		Object[] selected = getSelectedValues();
		clearSelection();
		
		super.setListData( original );
		
		for(Object r : selected)
			setSelectedValue(r, false);
	}
	
	
	/**
	 * Index of the record that is highlighted.
	 */
	private int highlighted = -1;
	
	/**
	 * Set another record as highlighted. 
	 * The highlighted records come from the search method.
	 * The method makes sure the index is visible and the UI is repainted.
	 *  
	 * @param index	Index of the highlighted record.
	 */
	protected void setHighlighted(int index) {
		if(index >= 0) {
			highlighted = index;
			ensureIndexIsVisible(highlighted);
			repaint();
		}
	}
	
	/**
	 * @return The index of the currently highlighted record.
	 */
	protected int getHighlightedIndex() {
		return highlighted;
	}
	
	
	/**
	 * KeyHelper allows the User to easily traverse the Checklist using the keyboard:
	 * <ul>
	 * <li>UP, DOWN, LEFT, RIGHT - use to navigate in the list,</li>
	 * <li>SPACEBAR - toggle selection of the highlighted record,</li>
	 * <li>A, ..., Z - search and highlight another record .</li>
	 * </ul>
	 * If you type the letters A, ..., Z quickly enough, they will be queried 
	 * and the search will use them all. The delay is 1 second.
	 * <br/>
	 * For instance: if you have several records such as `Ea`, `Eb`, `Ec`, .., `Ez`
	 * and type "E" and "Z" quickly enough, the record `Ez` will be highlighted. 
	 */
	class KeyHelper extends KeyAdapter {
		private long lastEvent = 0;
		private StringBuilder cache = new StringBuilder(32);
		
		
		@Override
		public void keyPressed(KeyEvent key) {
			key.consume();
			
			// The delay is too long - clear the cache and start the search anew.
			if(key.getWhen() - lastEvent > 1000 || cache.length() > 16)
				cache.delete(0, 31);
			
			// Search another matching record.
			if(key.getKeyCode() > 64) {
				cache.append(key.getKeyChar());
				lastEvent = key.getWhen();
				int index = getNextMatch(cache.toString(), 0, Position.Bias.Forward);
				setHighlighted(index);
			} else {
				int index = getHighlightedIndex();
				switch(key.getKeyCode()) {
				// Select the highlighted record. Toggle the selection in fact.
				case KeyEvent.VK_SPACE:
					setSelectionInterval(index, index);
					break;
				// Nagivate down - move to the next record.
				case KeyEvent.VK_DOWN:
					if(index < getModel().getSize() - 1)
						setHighlighted(index + 1);
					break;
				// Navigate up - move to the previous record.
				case KeyEvent.VK_UP:
					if(index > 0 )
						setHighlighted(index - 1);
					break;
				// Navigate left - move one column to the left.
				case KeyEvent.VK_LEFT:
					index -= getVisibleRowCount();
					if(index < 0) index = 0;
					setHighlighted(index);
					break;
				// Navigate right - move one column to the right.
				case KeyEvent.VK_RIGHT:
					index += getVisibleRowCount();
					if(index > getModel().getSize()) index = getModel().getSize() - 1;
					setHighlighted(index);						
					break;
				}
			}
		}
	}
	

	/**
	 * The Cell Renderer. Selected records are displayed in bold face and different color.
	 * Each record has a small checkbox - if the record is selected, the checkbox is marked too.
	 * Highlighted records are displayed with a rectangle around them.
	 */
	class CheckCellRenderer extends DefaultListCellRenderer {
	    private Icon 
	    	checked = Resource.createIcon("Checked.gif"), 
	    	unchecked = Resource.createIcon("Unchecked.gif");
	    private Font font;
	    
	    public CheckCellRenderer() {
	    	font = getFont();
	    	font = new Font(font.getName(), Font.BOLD, font.getSize());
	    	
	    }

	    @Override
	    public Component getListCellRendererComponent(
	    		JList list,
	    		Object value,
	    		int index, // cell index
	    		boolean selected,
	    		boolean focus)   {

	    	// Draw the text.
	    	super.getListCellRendererComponent(list, value, index, false, index == getHighlightedIndex());
	    	// Perform some modifications - bold face, another color - if the record is selected.
	    	if( selected ) {
	    		setForeground( getSelectionBackground() );
	    		setFont( font );
	    	}
	    	setIcon( selected ? checked : unchecked );
	    	return this;
	    }
	}

	/**
	 * A modified SelectionModel - toggles the selection (selected records are deselected and vice versa). 
	 */
	class ToggleSelectionModel extends DefaultListSelectionModel {
	    @Override
	    public void setSelectionInterval(int from, int to) {
	    	if (isSelectedIndex(from))
	    		super.removeSelectionInterval(from, to);
	    	else
	    		super.addSelectionInterval(from, to);
	    	setHighlighted(from);
	    }
	}
	
	
	/**
	 * Save (store) the list of selected records into a file.
	 *   Only the selected records will be visible now.
	 * 
	 * @param filename	The name of the file where the list shall be stored.
	 * @throws IOException	If the file cannot be created/written.
	 */
	public void save(String filename) 
	throws IOException {
		if(isSelectionEmpty())
			throw new RuntimeException("The selection is empty!");
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		Vector<String> selected = new Vector<String>( 100 );
		for(Object obj : getSelectedValues())
			selected.add( obj.toString() );
		oos.writeObject( selected );
		oos.close();
		clearSelection();
		setListData( selected );
	}
	
	/**
	 * Load the previously stored checklist from the file. 
	 * Keep the selected records selected, if possible.
	 * 
	 * @param filename	The name of the file where the list is stored.
	 * @throws IOException	If the file doesn't exist or cannot be read (insufficient access rights, ...).
	 * @throws ClassNotFoundException	If the version of the checklist file is not consistent with the current version.
	 */
	@SuppressWarnings("unchecked")
	public void load(String filename) 
	throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Vector<String> listData = (Vector<String>) ois.readObject();
		ois.close();
		Object[] selected = getSelectedValues();
		clearSelection();
		setListData(listData);
		for(Object r : selected)
			setSelectedValue(r, false);
	}
	
	
}
