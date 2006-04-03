package net.sf.plantlore.client.login;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTextField;

public class ItemView extends JDialog {
		
	private Login model;
	

	public ItemView(Login model) {
		this.model = model;
		setTitle("ItemEditor");
		setModal(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		initComponents();
		setResizable(false);
		setLocationRelativeTo(null);
	}
	
	
	public void fillWithSelected() {
		DBInfo info = model.getSelected();
		if(info == null) return;
		alias.setText(info.alias); host.setText(info.host + ((info.port != 1099) ? " : " + info.port : ""));
		db.setText(info.db);
	}
	
	
	protected void initComponents() {
		next = new JButton("change/add");
		alias = new JTextField(20); 
		host = new JTextField(20);
		db = new JTextField(20);
		 
		
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p1.add(next);
		add(p1, BorderLayout.SOUTH);
		
		JLabel l1 = new JLabel("Alias"), l2 = new JLabel("Hostname"), l3 = new JLabel("DB ID");
		l1.setLabelFor(alias); l2.setLabelFor(host); l3.setLabelFor(db);
		JPanel p2 = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// CHANGE HERE
		c.gridx = 0; c.gridy = 0; c.fill =  GridBagConstraints.NONE; 
		c.insets = new Insets(2, 4, 0, 0);
		p2.add(l1, c);
		
		c.gridy = 1; c.insets = new Insets(1, 4, 0, 0);
		p2.add(l2, c); 
		
		c.gridy = 2; c.insets = new Insets(1, 4, 0, 0);
		p2.add(l3, c);
		
		c.gridx = 1 ; c.gridy = 0; c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 3, 0, 4);
		p2.add(alias, c); 
	
		c.gridy = 1; c.insets = new Insets(2, 3, 0, 4);
		p2.add(host, c);
		
		c.gridy = 2; c.insets = new Insets(2, 3, 0, 4);
		p2.add(db, c);
		
		
		add(p2, BorderLayout.CENTER);
		
		pack();
	}
	
	
	public void nextAddActionListener(ActionListener a) {
		next.addActionListener(a);
	}
	
	
	public String getAlias() { return alias.getText(); }
	public String getHost() { return host.getText(); }
	public String getDB() { return db.getText(); }
	
	
	protected JButton next;
	protected JTextField alias, host, db;

}
