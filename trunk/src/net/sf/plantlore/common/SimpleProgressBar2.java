/*
 * SimpleProgressBar2.java
 *
 * Created on 4. září 2006, 19:37
 */

package net.sf.plantlore.common;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;

import net.sf.plantlore.l10n.L10n;

/**
 *
 * @author  yaa
 */
public class SimpleProgressBar2 extends javax.swing.JDialog implements Observer {
	
	private Task monitoredTask;
    
    /** Creates new form SimpleProgressBar2 */
    public SimpleProgressBar2(Task task, java.awt.Frame parent) {
    	super(parent, true);
    	monitoredTask = task;
    	initialize();
    }
    
    public SimpleProgressBar2(Task task, java.awt.Dialog parent) {
    	super(parent, true);
    	monitoredTask = task;
    	initialize();
    }
    
    
    private void initialize() {
    	monitoredTask.addObserver( this );
    	
        initComponents();
        setLocationRelativeTo(null);
        
        cancel.setAction( new StandardAction("Common.Cancel") {
        	public void actionPerformed(java.awt.event.ActionEvent arg0) {
        		monitoredTask.kill();
        		monitoredTask.deleteObservers();
        		Dispatcher.getDispatcher().finished();
        		
        		SwingUtilities.invokeLater(new Runnable() {
        			public void run() {
        				setVisible( false );
        			}
        		});
        	}
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        progress = new javax.swing.JProgressBar();
        cancel = new javax.swing.JButton();
        status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(L10n.getString("Common.Progress"));
        setModal(true);
        setResizable(false);
        setUndecorated(true);

        cancel.setText(L10n.getString("Common.Cancel"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, status, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, progress, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .add(cancel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(progress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(status, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cancel)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton cancel;
    protected javax.swing.JProgressBar progress;
    protected javax.swing.JLabel status;
    // End of variables declaration//GEN-END:variables
    
    
	public void update(Observable arg0, Object parameter) {

		if(parameter instanceof Pair) {
    		Pair p = (Pair)parameter;
            Object first = p.getFirst();
            if (first instanceof Task.Message) {
            	Task.Message msg = (Task.Message)first;
                Object value = p.getSecond();
                
                switch (msg) {
                case STARTING:
                	SwingUtilities.invokeLater(new Runnable() {
                		public void run() {
                			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                			cancel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                	        if( monitoredTask.isDeterminate() ) {
                	            progress.setIndeterminate(false);
                	            progress.setMinimum(0);
                	            progress.setMaximum(monitoredTask.getLength());
                	        } else {
                	            progress.setIndeterminate(true);
                	        }
                	        setVisible(true);
                		}
                	});
                
                case POSITION_CHANGED:
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                        	 if( monitoredTask.isDeterminate() && progress.isIndeterminate() ) {
                 	            progress.setIndeterminate(false);
                 	            progress.setMinimum(0);
                 	            progress.setMaximum(monitoredTask.getLength());
                 	        } else if( !monitoredTask.isDeterminate() && !progress.isIndeterminate()){
                 	            progress.setIndeterminate(true);
                 	        }
                        	progress.setValue(monitoredTask.getPosition());
                        }
                    });
                    break;
                    
                case MESSAGE_CHANGED:
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            status.setText( monitoredTask.getStatusMessage() );
                        }
                    });
                    break;
                    
                case STOPPING:
                case STOPPED:
                	SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                        	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        	cancel.setText(L10n.getString("Common.Hide"));
                        }
                    });
                
                }
            }
    	}
    	else if (parameter instanceof Exception) {
    		monitoredTask.stop();
   			DefaultExceptionHandler.handle(this, (Exception)parameter);
   			cancel.doClick();
        }
		
	}
    
}
