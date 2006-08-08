package net.sf.plantlore.common;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**     
 * KeyListener class controlling the pressing key ESCAPE. On key ESCAPE hides the view dialog.   
 * <br/>
 * The use is very simple (in the controller):
 * <br/>
 * <code>myView.myButton.addKeyListener( new DefaultEscapeKeyPressed( myView ) ); </code>
 * <br/>
 * 
 *   @author Lada Oberreiterova
 *   @version 1.0
 */  
public class DefaultEscapeKeyPressed implements KeyListener {

	protected JDialog dialog;
	protected JFrame frame;
	
	/**
	 * Create new instance of DefaultEscapeKeyPressed
	 * @param dialogView 
	 */
	public 	DefaultEscapeKeyPressed(JDialog dialogView) {
		dialog = dialogView;		
	}
	
	/**
	 * Create new instance of DefaultEscapeKeyPressed
	 * @param frameView
	 */
	public 	DefaultEscapeKeyPressed(JFrame frameView) {
		frame = frameView;		
	}
	
	/**
	 * Invoked when a key has been pressed. On key ESCAPE hides the view dialog.
	 * @param evt
	 */
 	public void keyPressed(KeyEvent evt){
 		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) { 			
 			if (dialog != null) {
 				dialog.setVisible(false);
 			} else if (frame != null) {
 				frame.setVisible(false);
 			}
 		}    	 		     	 		 
 	 }
 	
     public void keyReleased(KeyEvent evt) {}    	     
     public void keyTyped(KeyEvent evt) {}    	               
}
