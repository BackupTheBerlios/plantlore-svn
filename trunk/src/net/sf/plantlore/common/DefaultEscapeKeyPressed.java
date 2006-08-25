package net.sf.plantlore.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**     
 * KeyListener class controlling the pressing key ESCAPE. On key ESCAPE hides the view dialog.   
 * <br/>
 * The use is very simple (in the controller):
 *
 * Just put the following line after the dialog's or frame's initComponents() method:
 * <br/>
 * <code>new DefaultEscapeKeyPressed(this)</code>
 * <br/>
 * 
 *  This class installs itself recursively into each component of the dialog or frame.
 *
 *  You can also use the constructors with AbstractAction. In that case this listener won't simply hide the dialog or frame
 * but will call yourAction.actionPerformed(null). This is handful if your cancel action is more than just hiding the dialog.
 *
 *   @author Lada Oberreiterova
 *   @version 1.0
 */  
public class DefaultEscapeKeyPressed implements KeyListener {

	protected JDialog dialog;
	protected JFrame frame;
	private AbstractAction action;
        
        private void installMe(Component component) {
            component.addKeyListener(this);
            if (component instanceof Container)
                for (Component c : ((Container)component).getComponents() )
                    installMe(c);            
        }
        
	/**
	 * Create new instance of DefaultEscapeKeyPressed
	 * @param dialogView 
	 */
	public 	DefaultEscapeKeyPressed(JDialog dialogView) {
		dialog = dialogView;		
                installMe(dialog);
	}
	
	/**
	 * Create new instance of DefaultEscapeKeyPressed
	 * @param frameView
	 */
	public 	DefaultEscapeKeyPressed(JFrame frameView) {
		frame = frameView;	
                installMe(frame);
	}
	
	/**
	 * Create new instance of DefaultEscapeKeyPressed
	 * @param dialogView 
	 */
	public 	DefaultEscapeKeyPressed(JDialog dialogView, AbstractAction action) {
            this.action = action;
		dialog = dialogView;		
                installMe(dialog);
	}
	
	/**
	 * Create new instance of DefaultEscapeKeyPressed
	 * @param frameView
	 */
	public 	DefaultEscapeKeyPressed(JFrame frameView, AbstractAction action) {
            this.action = action;
		frame = frameView;	
                installMe(frame);
	}
	
	/**
	 * Invoked when a key has been pressed. On key ESCAPE hides the view dialog.
	 * @param evt
	 */
 	public void keyPressed(KeyEvent evt){
 		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) { 
                    if (action != null) {
                        action.actionPerformed(null);
                        return;
                    }
                    
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
