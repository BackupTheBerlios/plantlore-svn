package net.sf.plantlore.server;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;


public class ServerView implements Observer {
	
	private JList list;
	private RMIServer model;
	
	
	public ServerView(RMIServer model) {
		this.model = model;
	}
	
	
	private class LeftAction extends AbstractAction {
		
	    public LeftAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
	        super(text, icon);
	        putValue(SHORT_DESCRIPTION, desc);
	        putValue(MNEMONIC_KEY, mnemonic);
	    }
	    
	    public void actionPerformed(ActionEvent e) {
	        Object o = list.getSelectedValue();
	        if(o != null) {
	        	ConnectionInfo client = (ConnectionInfo) o;
	        	System.out.println("disconnecting " + client);
	        	model.disconnect(client);
	        }
	    }
	}
	
	private void createGUI() {
		// Create and set up the window.
        JFrame frame = new JFrame("Planlore server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        list = new JList();
        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setPreferredSize(new Dimension(350, 80));
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton disconnect = new JButton(new LeftAction("Disconnect", null, "Disconnect selected clients from the server.", KeyEvent.VK_D));
        panel.add(disconnect);
        
        frame.getContentPane().add(listScroll, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setVisible(true);
	}

	

	
	
	public void update(Observable observed, Object arg) {
		list.setListData(model.getClients().toArray());
	}



	
	
	
	public static void main(String[] args) {
		// Set beautiful Windows look & feel.
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try { UIManager.setLookAndFeel(lookAndFeel); }
        catch (Exception e) { JFrame.setDefaultLookAndFeelDecorated(true); }
        
        
        try{
        	RMIServer model = new RMIServer();
        	ServerView view = new ServerView(model);
        	view.createGUI();
        	model.start();
        	
        } catch(Exception e) { System.err.println(e); }
        
        
        
	}
	
}
