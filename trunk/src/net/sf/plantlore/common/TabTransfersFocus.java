﻿package net.sf.plantlore.common;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Some components treat tabulator (TAB key) in their own way.
 * Sometimes the tabulator is supposed to simply transfer the focus
 * to the next focusable component.
 * <br/>
 * Here's how to use this class to override the "component's default"
 * behaviour:
 * <pre>
 * JTextArea  area  = new JTextArea(..);
 * <b>new TabTransfersFocus( area );</b>
 * </pre>
 * This should do the trick. To transfer the focus from the TextArea to
 * another component, you can always use <code>CTRL+TAB</code>.
 * 
 * @author kaimu
 * @since 2006-05-14
 * @version 1.0
 * @see TransferFocus#patch(Component)
 */
@Deprecated
public class TabTransfersFocus extends KeyAdapter {
	
	private Component a;

	/**
	 *  Create a new TabTransfersFocus.
	 *  
	 *  @param a The component whose behaviour should be "fixed".
	 */
	public TabTransfersFocus(Component a) {
		this.a = a;
		a.addKeyListener(this);
	}
	
	/**
	 * Neutralize the effect this class has on the component.
	 */
	public void neutralize() {
		a.removeKeyListener(this);
	}
	
	/**
	 * Override the behaviour so that TAB key transfers the focus
	 * to the next focusable component.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_TAB) {
			System.out.println(e.getModifiers());
			if(e.getModifiers() > 0) a.transferFocusBackward();
			else a.transferFocus();	
			e.consume();
		}
	}
}