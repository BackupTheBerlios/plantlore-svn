package net.sf.plantlore.client.login;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;


public class AuthView extends JDialog {
	
	private Login model;
	
	public AuthView(Login model) {
		this.model = model;
		setTitle("Authorization");
		setSize(300, 200);
		setModal(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		initComponents();
		setLocationRelativeTo(null); // center of the screen
	}
	
	
	protected void initComponents() {
		setTitle("Authentification");
		setModal(true);

		next = new JButton("next");
		user = new JComboBox(); user.setEditable(true); 
		user.setPreferredSize(new Dimension(50, 23));
		password = new JPasswordField(32); password.setPreferredSize(new Dimension(50, 23));

		
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p1.add(next);
		add(p1, BorderLayout.SOUTH);
		
		JLabel l1 = new JLabel("Username"), l2 = new JLabel("Password");
		l1.setLabelFor(user); l2.setLabelFor(password);
		JPanel p2 = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// CHANGE HERE
		c.gridx = 0; c.gridy = 0; c.fill =  GridBagConstraints.NONE; 
		c.insets = new Insets(2, 4, 0, 0);
		p2.add(l1, c);
		
		c.gridy = 1; c.insets = new Insets(1, 4, 0, 0);
		p2.add(l2, c); 
		
		c.gridx = 1 ; c.gridy = 0; c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 3, 0, 4);
		p2.add(user, c); 
	
		c.gridy = 1; c.insets = new Insets(2, 3, 0, 4);
		p2.add(password, c);
		
		
		add(p2, BorderLayout.CENTER);
		
		pack();
		
		setResizable(false);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible) {
			user.removeAllItems();
			String[] u = model.getSelected().users;
			int i = 0;
			for(; i < u.length && u[i] != null; i++) user.addItem(u[i]);
			if(i > 0) user.setSelectedIndex(0);
		}
		super.setVisible(visible);
	}
	
	protected String getUserName() {
		return (String) user.getSelectedItem();
	}
	
	protected String getPassword() {
		return new String( password.getPassword() );
	}
	
	public void nextAddActionListener(ActionListener a) {
		next.addActionListener(a);
	}
	
	
	protected JButton next;
	protected JComboBox user;
	protected JPasswordField password;

	
}
