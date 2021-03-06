﻿/*
 * ServerView2.java
 *
 * Created on 20. duben 2006, 10:16
 */

package net.sf.plantlore.server.manager;

import java.util.Observable;
import java.util.Observer;

import net.sf.plantlore.common.Dispatcher;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.ConnectionInfo;

/**
 * The GUI for the Plantlore Server management.
 * The User can see the list of currently connected clients,
 * refresh it, kick some clients and terminate the Server.
 *
 * @author  kaimu
 */
public class ServerMngView extends javax.swing.JFrame implements Observer {
	
	private ServerMng model;
	
	
    /** Creates new form ServerView */
    public ServerMngView(ServerMng model) {
    	this.model = model;
    	model.addObserver(this);
    	
        initComponents();
        getRootPane().setDefaultButton(hide);
        
        PlantloreHelp.addKeyHelp(PlantloreHelp.SERVER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.SERVER, this.help);
        
        // Show the progress bar only when necessary.
        progress.setVisible( false );
        
        setLocationRelativeTo(null); // center of the screen
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        users = new javax.swing.JList();
        hide = new javax.swing.JButton();
        help = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        refresh = new javax.swing.JButton();
        kick = new javax.swing.JButton();
        progress = new javax.swing.JProgressBar();
        terminate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(L10n.getString("Server.Manage"));
        users.setToolTipText(L10n.getString("Server.ListOfUsersTT"));
        jScrollPane1.setViewportView(users);

        hide.setText(L10n.getString("Server.Hide"));

        help.setText(L10n.getString("Common.Help"));

        jToolBar1.setFloatable(false);
        refresh.setText(L10n.getString("Server.Refresh"));
        jToolBar1.add(refresh);

        kick.setText(L10n.getString("Server.KickUser"));
        jToolBar1.add(kick);

        progress.setBorderPainted(false);
        jToolBar1.add(progress);

        terminate.setText(L10n.getString("Server.Terminate"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(help)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 167, Short.MAX_VALUE)
                        .add(terminate)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(hide)))
                .add(10, 10, 10))
        );

        layout.linkSize(new java.awt.Component[] {hide, terminate}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hide)
                    .add(terminate)
                    .add(help))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton help;
    protected javax.swing.JButton hide;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    protected javax.swing.JButton kick;
    protected javax.swing.JProgressBar progress;
    protected javax.swing.JButton refresh;
    protected javax.swing.JButton terminate;
    protected javax.swing.JList users;
    // End of variables declaration//GEN-END:variables


    /**
     * Reload the list of the connected clients and
     * show the dialog when the connection or the creation was successful.
     */
	public void update(Observable source, final Object parameter) {
		if(parameter == ServerMng.UPDATE_LIST){
			ConnectionInfo[] clients = model.getConnectedUsers();
			if(clients != null) 
				users.setListData(clients);
			else 
				users.setListData(new String[] {""});
		} 
		else if( parameter == ServerMng.CONNECTED ) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					setVisible(true);
				}
			});
			Dispatcher.initialize( progress );
		}
	}
    
}
