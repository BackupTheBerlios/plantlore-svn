﻿/*
 * ServerCreateView.java
 *
 * Created on 1. červen 2006, 19:21
 */

package net.sf.plantlore.server.manager;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;

import net.sf.plantlore.client.AppCoreView;
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.server.ServerSettings;

/**
 * Open a dialog where the User can either create a new server (and specify the
 * server's settings) or connect to an existing server (to administrate it).
 *
 * @author  kaimu
 */
public class ServerCreateView extends javax.swing.JFrame implements Observer {
	
	private ServerMng model;
	
    /** Creates new form ServerCreateView */
    public ServerCreateView(ServerMng model) {
    	this.model = model; 
    	model.addObserver(this);
    	
        initComponents();
        getRootPane().setDefaultButton(next);
        setLocationRelativeTo(null); // center of the screen
        
        PlantloreHelp.addKeyHelp(PlantloreHelp.SERVER, this.getRootPane());
        PlantloreHelp.addButtonHelp(PlantloreHelp.SERVER, this.help);
        
        // Initialize the components' contents.
        ServerSettings settings = model.getSettings(true);
        if(settings != null) {
        	// Server
        	serverPort.setText( new Integer(settings.getPort()).toString() );
        	// Database
        	databasePort.setText( new Integer(settings.getDatabaseSettings().getPort()).toString() );
        	( (javax.swing.JTextField)databaseType.getEditor().getEditorComponent() )
        	.setText( settings.getDatabaseSettings().getDatabase() );
        	databaseParameter.setText( settings.getDatabaseSettings().getConnectionStringSuffix() );
        }
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        // Show the progress bar only when necessary.
        //progress.setVisible( false );
        
        DocumentSizeFilter.patch(serverPassword, 20);
        DocumentSizeFilter.patch(serverPort, 5);
        DocumentSizeFilter.patch(databaseType, 30);
        DocumentSizeFilter.patch(databasePort, 5);
        DocumentSizeFilter.patch(databaseParameter, 60);
        DocumentSizeFilter.patch(remoteHost, 50);
        DocumentSizeFilter.patch(remoteServerPort, 5);
        DocumentSizeFilter.patch(remoteServerPassword, 20);
        
        logo.setIcon(
        		new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage(AppCoreView.class.getResource("resources/splash_mini.jpg")))
        		);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        next = new javax.swing.JButton();
        help = new javax.swing.JButton();
        choicePane = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        serverPassword = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        serverPort = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        databaseType = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        databasePort = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        databaseParameter = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        remoteHost = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        remoteServerPassword = new javax.swing.JPasswordField();
        jLabel8 = new javax.swing.JLabel();
        remoteServerPort = new javax.swing.JTextField();
        cancel = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        progress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(L10n.getString("Server.Create"));
        setResizable(false);
        next.setText(L10n.getString("Server.Continue"));

        help.setText(L10n.getString("Common.Help"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, L10n.getString("Server.ServerSettings")));
        jLabel2.setText(L10n.getString("Server.Password"));

        serverPassword.setToolTipText(L10n.getString("Server.PasswordTT"));

        jLabel1.setText(L10n.getString("Server.Port"));

        serverPort.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        serverPort.setText("1099");
        serverPort.setToolTipText(L10n.getString("Server.PortTT"));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serverPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serverPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel2)
                .add(serverPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel1))
            .add(serverPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, L10n.getString("Server.DatabaseSettings")));
        jLabel5.setText(L10n.getString("Server.DatabaseType"));

        databaseType.setEditable(true);
        databaseType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "postgresql", "firebirdsql", "mysql", "oraclesql" }));
        databaseType.setToolTipText(L10n.getString("Server.DatabaseTypeTT"));

        jLabel6.setText(L10n.getString("Server.DatabasePort"));

        databasePort.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        databasePort.setText("5432");
        databasePort.setToolTipText(L10n.getString("Server.DatabasePortTT"));

        jLabel7.setText(L10n.getString("Server.DatabaseParameter"));

        databaseParameter.setToolTipText(L10n.getString("Server.DatabaseParameterTT"));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(databaseType, 0, 257, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(databasePort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(databaseParameter, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(databasePort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(databaseType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(databaseParameter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2Layout.linkSize(new java.awt.Component[] {databaseParameter, databasePort, databaseType}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        choicePane.addTab(L10n.getString("Server.CreateNewServer"), jPanel3);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(L10n.getString("Server.ConnectToSettings")));
        jLabel3.setText(L10n.getString("Server.Host"));

        remoteHost.setText("localhost");
        remoteHost.setToolTipText(L10n.getString("Server.HostTT"));

        jLabel4.setText(L10n.getString("Server.Password"));

        remoteServerPassword.setToolTipText(L10n.getString("Server.PasswordTT"));

        jLabel8.setText(L10n.getString("Server.Port"));

        remoteServerPort.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        remoteServerPort.setText("1099");
        remoteServerPort.setToolTipText(L10n.getString("Server.PortTT"));

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jLabel3))
                    .add(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel4)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(remoteHost, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .add(remoteServerPassword, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(remoteServerPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel3)
                        .add(remoteServerPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel8))
                    .add(remoteHost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(remoteServerPassword, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5Layout.linkSize(new java.awt.Component[] {remoteHost, remoteServerPassword, remoteServerPort}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        choicePane.addTab(L10n.getString("Server.ConnectToRunningServer"), jPanel4);

        cancel.setText(L10n.getString("Common.Cancel"));

        logo.setBackground(new java.awt.Color(0, 0, 0));

        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        progress.setBorderPainted(false);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(progress, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(progress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(logo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                            .add(choicePane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(help)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 214, Short.MAX_VALUE)
                        .add(next)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancel)
                        .addContainerGap())))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(new java.awt.Component[] {cancel, next}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(logo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(choicePane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(help)
                    .add(next)
                    .add(cancel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton cancel;
    protected javax.swing.JTabbedPane choicePane;
    protected javax.swing.JTextField databaseParameter;
    protected javax.swing.JTextField databasePort;
    protected javax.swing.JComboBox databaseType;
    protected javax.swing.JButton help;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    protected javax.swing.JLabel logo;
    protected javax.swing.JButton next;
    protected javax.swing.JProgressBar progress;
    protected javax.swing.JTextField remoteHost;
    protected javax.swing.JPasswordField remoteServerPassword;
    protected javax.swing.JTextField remoteServerPort;
    protected javax.swing.JPasswordField serverPassword;
    protected javax.swing.JTextField serverPort;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Hide the dialog if the connection or creation was successful.
     */
	public void update(Observable source, Object parameter) {
		
		if(parameter == ServerMng.CONNECTED) {
			model.deleteObserver(this);
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					setVisible(false);
					dispose();
				}
			});
		}
	}
          
}
