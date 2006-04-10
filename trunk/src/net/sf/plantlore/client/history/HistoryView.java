package net.sf.plantlore.client.history;


import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;

import net.sf.plantlore.common.ComponentAdjust;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.l10n.L10n;

/**
 * 
 * @author Lada
 *
 */
public class HistoryView implements Observer {

	//Dialog
	private JDialog historyDialog;
	//History model
	private History model;	
	//Panels
    private JPanel buttonsPane;
    private JPanel infoRecordPanel;
    private JPanel infoInsertPanel;
    private JPanel infoEditPanel;
    //Labels
    private JLabel nameLabel;    
    private JLabel authorLabel;
    private JLabel locationLabel;    
    private JLabel dateLabel;
    private JLabel insertWhoLabel;
    private JLabel nameValueLabel;
    private JLabel authorValueLabel;
    private JLabel locationValueLabel;
    private JLabel dateValueLabel;
    private JLabel insertWhoValueLabel;
    private JLabel displayRowsText;    
    private JLabel countResultText;
    private JLabel countResutl;
    private JLabel currentRowsInfoText;
    private JLabel currentRowsInfo;
    
    //JFormattedTextField
    private JFormattedTextField displayRows;
    //JTable
    private JTable tableEditList;
    private DefaultTableModel tableModel;
    private JScrollPane jsp;
    //data
    private Object[][] dateTable;
    //Buttons
    private JButton nextButton;
    private JButton previousButton;
    private JButton selectAllButton;
    private JButton unselectAllButton;
    private JButton undoSelectedButton;
    private JButton undoToButton;
    private JButton okButton;
    private JButton cancelButton;
    private JButton helpButton;
    //data
    private Object[][] data;
    
    /** Creates a new instance of HistoryView */
    public HistoryView(History model, JFrame owner)
    {    	
        this.model = model; 
        historyDialog = new JDialog(owner, "History", true);
        historyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        historyDialog.setVisible(false);
        init();
    }
    
    public void update(Observable observable, Object object)
    {
    } 
    
    /** 
     *
     * The top initializing method.
     *
     */
    private void init()
    {    
        
        //initialization of the subPanel
        initButtonsPane();
        
        //Layout (JDialog)
        historyDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbConstraints;       
        gbConstraints = new GridBagConstraints();
            
        //Add panel with information about record
        infoRecordPanel = new JPanel();
 	    infoRecordPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Details of the record"));
 	    infoRecordPanel.setLayout(new java.awt.GridBagLayout()); 
 	    gbConstraints = new GridBagConstraints();
        gbConstraints.gridy = 0;
        gbConstraints.gridx = 0;       
        gbConstraints.weighty = 0.1;
        gbConstraints.weightx = 1.0;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbConstraints.fill = GridBagConstraints.BOTH;  
        historyDialog.add(infoRecordPanel, gbConstraints);
         
        //Add panel with information about record created
        infoInsertPanel = new JPanel();
 	    infoInsertPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Record created"));
 	    infoInsertPanel.setLayout(new java.awt.GridBagLayout()); 
 	    gbConstraints = new GridBagConstraints();
 	    gbConstraints.gridy = 1;
        gbConstraints.gridx = 0;
        gbConstraints.weighty = 0.1;
        gbConstraints.weightx = 1;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbConstraints.fill = GridBagConstraints.BOTH;  
        historyDialog.add(infoInsertPanel, gbConstraints);
        
        //Add panel with list of changes
        infoEditPanel = new JPanel();
 	    infoEditPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("List of changes"));
 	    infoEditPanel.setLayout(new java.awt.GridBagLayout()); 
 	    gbConstraints = new GridBagConstraints();
 	    gbConstraints.gridy = 2;
        gbConstraints.gridx = 0;
        gbConstraints.weighty = 0.75;
        gbConstraints.weightx = 1;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gbConstraints.anchor = GridBagConstraints.NORTH;
        gbConstraints.fill = GridBagConstraints.BOTH;  
        historyDialog.add(infoEditPanel, gbConstraints);
        
        //Add panel with ok, cancle and help buttons
        gbConstraints.gridy = 3;
        gbConstraints.gridx = 0;
        gbConstraints.weighty = 0.05;
        gbConstraints.weightx = 1.0;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gbConstraints.anchor = GridBagConstraints.SOUTHEAST;
        historyDialog.add(buttonsPane, gbConstraints);
       
        // Add labels to the infoRecordPanel panel
        nameLabel = new JLabel();
        nameLabel.setText("Name:"); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;       
        gbConstraints.weightx = 0.1;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        infoRecordPanel.add(nameLabel, gbConstraints);    
        
        nameValueLabel = new JLabel();
        nameValueLabel.setText(model.getNamePlant());        
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;       
        gbConstraints.weightx = 0.9;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        infoRecordPanel.add(nameValueLabel, gbConstraints);   
        
        authorLabel = new JLabel();
        authorLabel.setText("Author:"); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 1;
        gbConstraints.weightx = 0.1;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        infoRecordPanel.add(authorLabel, gbConstraints);
        
        authorValueLabel = new JLabel();
        authorValueLabel.setText(model.getNameAuthor());   
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 1;
        gbConstraints.weightx = 0.9;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        infoRecordPanel.add(authorValueLabel, gbConstraints);
        
        locationLabel = new JLabel();
        locationLabel.setText("Location:");  
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 2;
        gbConstraints.weightx = 0.1;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        infoRecordPanel.add(locationLabel, gbConstraints);
        
        locationValueLabel = new JLabel();
        locationValueLabel.setText(model.getLocation());  
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 2;
        gbConstraints.weightx = 0.9;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        infoRecordPanel.add(locationValueLabel, gbConstraints);

        // Add labels to the infoInsertPanel panel
        dateLabel = new JLabel();
        dateLabel.setText("Date record creation:"); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;       
        gbConstraints.weightx = 0.1;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        infoInsertPanel.add(dateLabel, gbConstraints);    
        
        dateValueLabel = new JLabel();        
        dateValueLabel.setText(model.getWhen().toString());
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;       
        gbConstraints.weightx = 0.9;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        infoInsertPanel.add(dateValueLabel, gbConstraints);   
        
        insertWhoLabel = new JLabel();
        insertWhoLabel.setText("User who record created:"); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 1;
        gbConstraints.weightx = 0.1;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        infoInsertPanel.add(insertWhoLabel, gbConstraints);
        
        insertWhoValueLabel = new JLabel();
        insertWhoValueLabel.setText(model.getNameUser());   
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 1;
        gbConstraints.weightx = 0.9;
        gbConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gbConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        infoInsertPanel.add(insertWhoValueLabel, gbConstraints);              
        
        // Add table to the infoEditPanel panel    
        tableEditList = new JTable(new HistoryTableModel(model));        
        TableColumnModel tcm = tableEditList.getColumnModel();        
        TableColumn tc;
        for (int i = 0; i < tableEditList.getColumnCount(); i++) {
            tc = tcm.getColumn(i);
            if (i == 0) {
                tc.setPreferredWidth(20); 
            } else {
                tc.setPreferredWidth(100);
            }
        }
        tableEditList.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 100)); 
        tableEditList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jsp = new JScrollPane(tableEditList);     
                
        gbConstraints = new java.awt.GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gbConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gbConstraints.weightx = 1;        
        gbConstraints.weighty = 0.65;  
        gbConstraints.gridheight = 8;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jsp.setMinimumSize(new java.awt.Dimension(500, 100));        
        jsp.setPreferredSize(new java.awt.Dimension(500, 100));        
        infoEditPanel.add(jsp, gbConstraints);
        jsp.add(tableEditList);
        jsp.setViewportView(tableEditList); 
        
        //Add buttons to the infoEditPanel panel       
        
                
        selectAllButton = new JButton("Select all");
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 0;       
        gbConstraints.gridwidth = 2;
        gbConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        selectAllButton.setMinimumSize(new java.awt.Dimension(110, 25));        
        selectAllButton.setPreferredSize(new java.awt.Dimension(110, 25));
        infoEditPanel.add(selectAllButton, gbConstraints);
        
        unselectAllButton = new JButton("Unselect all");
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 1;       
        gbConstraints.gridwidth = 2;
        gbConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        unselectAllButton.setMinimumSize(new java.awt.Dimension(110, 25));        
        unselectAllButton.setPreferredSize(new java.awt.Dimension(110, 25));
        infoEditPanel.add(unselectAllButton, gbConstraints);
        
        undoSelectedButton = new JButton("Undo selected");
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 2;     
        gbConstraints.gridwidth = 2;
        gbConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        undoSelectedButton.setMinimumSize(new java.awt.Dimension(110, 25));        
        undoSelectedButton.setPreferredSize(new java.awt.Dimension(110, 25));
        infoEditPanel.add(undoSelectedButton, gbConstraints);
        
        countResultText = new JLabel();
        countResultText.setText("Total results:"); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 3;   
        gbConstraints.weighty = 0.1;         
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        infoEditPanel.add(countResultText, gbConstraints);
                
        Integer countRes = model.getResultRows();
        countResutl = new JLabel();
        countResutl.setText(countRes.toString()); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 2;
        gbConstraints.gridy = 3;       
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        infoEditPanel.add(countResutl, gbConstraints);
        
        currentRowsInfoText = new JLabel();
        currentRowsInfoText.setText("Displayed:"); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 4;                  
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        infoEditPanel.add(currentRowsInfoText, gbConstraints);
                        
        currentRowsInfo = new JLabel();
        currentRowsInfo.setText(model.getCurrentDisplayRows()); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 2;
        gbConstraints.gridy = 4;       
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        infoEditPanel.add(currentRowsInfo, gbConstraints);
        
        displayRowsText = new JLabel();
        displayRowsText.setText("Rows to display:"); 
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 5;       
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        infoEditPanel.add(displayRowsText, gbConstraints);
        
        displayRows = new JFormattedTextField();
        displayRows.setValue(model.getDisplayRows());
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 2;
        gbConstraints.gridy = 5;           
        gbConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        infoEditPanel.add(displayRows, gbConstraints);
        
        previousButton = new JButton("Previous");
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 6;            
        gbConstraints.gridwidth = 2;
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        previousButton.setMinimumSize(new java.awt.Dimension(110, 25));        
        previousButton.setPreferredSize(new java.awt.Dimension(110, 25));
        infoEditPanel.add(previousButton, gbConstraints);
        
        nextButton = new JButton("Next");
        gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 1;
        gbConstraints.gridy = 7;  
        gbConstraints.gridwidth = 2;
        gbConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gbConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        nextButton.setMinimumSize(new java.awt.Dimension(110, 25));        
        nextButton.setPreferredSize(new java.awt.Dimension(110, 25));
        infoEditPanel.add(nextButton, gbConstraints);
        
    }     
 
    public int messageUndo(String message) {
    	int okCancle = JOptionPane.showConfirmDialog(historyDialog, message, "Information about change", JOptionPane.OK_CANCEL_OPTION);
    	return okCancle;
    }

    public void close() {
        historyDialog.dispose();
    }
    
    public void show() {
        historyDialog.setSize(800,600);
        historyDialog.setLocationRelativeTo(null);
        historyDialog.setVisible(true);
    }
    
    public JTable getTable()
    {
    	return this.tableEditList;
    }
    
    public void setCountResutl(Integer resultRows)
    {
    	this.countResutl.setText(resultRows.toString());
    }
    
    /** */
    public void setCurrentRowsInfo(String displayedRows)
    {
    	this.currentRowsInfo.setText(displayedRows);
    }
    
    public Integer getDisplayRows() {
        return (Integer)displayRows.getValue();
    }
    
    public void setDisplayRows(int value) {
        this.displayRows.setValue(value);
    }  
    
    /** 
    *  Constructs the buttons pane.
    */
   private void initButtonsPane()
   {
       ComponentAdjust ca = new ComponentAdjust();
              
       okButton = new JButton(L10n.getString("Ok"));
       cancelButton = new JButton(L10n.getString("Cancel"));
       helpButton = new JButton(L10n.getString("Help"));
       okButton.setActionCommand("OK");
       cancelButton.setActionCommand("CANCEL");
       helpButton.setActionCommand("HELP");
       
       ca.add(okButton);
       ca.add(cancelButton);
       ca.add(helpButton);
       ca.setMaxWidth();
       
       buttonsPane = new JPanel();                    
       buttonsPane.setLayout(new FlowLayout());
       buttonsPane.add(okButton);
       buttonsPane.add(cancelButton);
       buttonsPane.add(helpButton);
   }
   

  public void addOkButtonListener(ActionListener al) {
     okButton.addActionListener(al);     
  }  
  
  public void addCancelButtonListener(ActionListener al) {	     
	     cancelButton.addActionListener(al);	     
	  }  
  
  public void addHelpButtonListener(ActionListener al) {
	     helpButton.addActionListener(al);
	  }  
  
  public void addNextButtonListener(ActionListener al) {
      nextButton.addActionListener(al);
  }
  
  public void addPreviousButtonListener(ActionListener al) {
      previousButton.addActionListener(al);
  }
  
  public void addSelectAllButtonListener(ActionListener al) {
      selectAllButton.addActionListener(al);
  }
  
  public void addUnselectAllButtonListener(ActionListener al) {
      unselectAllButton.addActionListener(al);
  }
  
  public void addUndoSelectedButtonListener(ActionListener al) {
      undoSelectedButton.addActionListener(al);
  }
  
  void rowSetPropertyChangeListener(PropertyChangeListener pcl) {
	  displayRows.addPropertyChangeListener(pcl);
  }  
}
