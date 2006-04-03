package net.sf.plantlore.client.login;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public class LoginView extends JDialog implements Observer {
	
	private Login model;
	
	
	public LoginView(Login model) {
		this.model = model;
		model.addObserver(this);
		setTitle("Login");
		setSize(300,200);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		initComponents();
		setLocationRelativeTo(null); // center of the screen
	}
	
	
	
	protected void initComponents() {
		
		choice = new JList();
		choice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		next = new JButton("next");
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(next);
        
		add(choice, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);
		
		popup = new JPopupMenu();
		add = new JMenuItem("Add a new item.");
		edit = new JMenuItem("Edit this item.");
		remove = new JMenuItem("Remove this item.");
		popup.add(add);
		popup.add(edit);
		popup.add(remove);
		
		choice.addMouseListener(
				new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						if(e.isPopupTrigger()) popup.show(e.getComponent(), e.getX(), e.getY());						
					}
				});
	}
	
	
	
	




	public void update(Observable arg0, Object arg1) {
		System.out.println("Updating");
		choice.setListData(model.getRecords());		
	}
	
	
	public void nextAddActionListener(ActionListener a) {
		next.addActionListener(a);
	}
	
	public void addAddActionListener(ActionListener a) {
		add.addActionListener(a);
	}
	
	public void editAddActionListener(ActionListener a) {
		edit.addActionListener(a);
	}
	
	public void removeAddActionListener(ActionListener a) {
		remove.addActionListener(a);
	}
	
	public void listAddListSelectionListener(ListSelectionListener a) {
		choice.addListSelectionListener(a);
	}
	
	
	protected JList choice;
	protected JButton next;
	protected JMenuItem add, edit, remove;
	protected JPopupMenu popup;

}
