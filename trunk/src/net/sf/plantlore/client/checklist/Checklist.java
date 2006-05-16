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
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.text.Position;

/**
 * The checklist holds records. Save/load. Me good english!
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @version 1.0
 */
public class Checklist extends JList {
	
	public Checklist(Object[] values) {
		if( !(values instanceof String[]) ) 
			for(int i = 0; i < values.length; i++) 
				values[i] = values[i].toString();
		
		setListData( (String[])values );
		
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
		
		addKeyListener(new KeyHelper());	
	}
	
	
	private int highlighted = -1;
	
	synchronized protected void setHighlighted(int index) {
		if(index >= 0) {
			highlighted = index;
			ensureIndexIsVisible(highlighted);
			repaint();
		}
	}
	
	synchronized protected int getHighlightedIndex() {
		return highlighted;
	}
	
	
	
	class KeyHelper extends KeyAdapter {
		private long lastEvent = 0;
		private StringBuilder cache = new StringBuilder(32);
		
		
		@Override
		public void keyPressed(KeyEvent key) {
			key.consume();
			
			if(key.getWhen() - lastEvent > 1000 || cache.length() > 16)
				cache.delete(0, 31);
			
			if(key.getKeyCode() > 64) {
				cache.append(key.getKeyChar());
				lastEvent = key.getWhen();
				int index = getNextMatch(cache.toString(), 0, Position.Bias.Forward);
				setHighlighted(index);
			} else {
				int index = getHighlightedIndex();
				switch(key.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					setSelectionInterval(index, index);
					break;
				case KeyEvent.VK_DOWN:
					if(index < getModel().getSize() - 1)
						setHighlighted(index + 1);
					break;
				case KeyEvent.VK_UP:
					if(index > 0 )
						setHighlighted(index - 1);
					break;
				case KeyEvent.VK_LEFT:
					index -= getVisibleRowCount();
					if(index < 0) index = 0;
					setHighlighted(index);
					break;
				case KeyEvent.VK_RIGHT:
					index += getVisibleRowCount();
					if(index > getModel().getSize()) index = getModel().getSize() - 1;
					setHighlighted(index);						
					break;
				}
			}
		}
	}
	

	
	class CheckCellRenderer extends DefaultListCellRenderer {
	    private Icon 
	    	checked = new ImageIcon("checked.gif"), 
	    	unchecked = new ImageIcon("unchecked.gif");
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

	    	super.getListCellRendererComponent(list, value, index, false, index == getHighlightedIndex());
	    	if( selected ) {
	    		setForeground( getSelectionBackground() );
	    		setFont( font );
	    	}
	    	setIcon( selected ? checked : unchecked );
	    	return this;
	    }
	}

	
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
	}
	
	
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
