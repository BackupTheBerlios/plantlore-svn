/*
 * UserManagerTableModel.java
 *
 * Created on 22. duben 2006, 22:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.user;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * Implements a table model for the UserManage
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class UserManagerTableModel  extends AbstractTableModel  {
    
  
	/** Instance of a logger */
    private Logger logger;
    /** Model of userMnagre MVC*/
    private UserManager model; 
    /** Results of a search query for displaying */
    private ArrayList<User> userDataList;    
    /** Names of the columns */
    private String[] columnNames;
    /** Data values displayed in the table*/
    private Object[][] data;

    /** Constants used for identification of columns of table */
    public final static int LOGIN = 0;
    public final static int WHOLENAME = 1;
    public final static int EMAIL = 2;       
    public final static int  CREATEWHEN = 3;          
    public final static int DROPWHEN = 4;        
    
    /**
     * Creates a new instance of UserManagerTableModel
     * @param model model of UserManager MVC 
     */
    public UserManagerTableModel(UserManager model) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();    	
    }  
   
    /**
     * Init names of columns.     
     */
    private void initColumns() {
        columnNames = new String[5];                  
        columnNames[0] = L10n.getString("UserManager.Login");   
        columnNames[1] = L10n.getString("UserManager.Name");  
        columnNames[2] = L10n.getString("UserManager.Email");
        columnNames[3] = L10n.getString("UserManager.CreateWhen"); 
        columnNames[4] = L10n.getString("UserManager.DropWhen");                              
    }       
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	
    	logger.debug("UserManager - Init data.");
    	
    	userDataList = model.getUserList();
    	if (userDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}    	  	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(userDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;   
        int ii = 0;        
    	//Loud data for view
        Object[][] userData = new Object[countRow][5];   
    	for (int i=firstRow-1; i < countResult; i++) {                             
            userData[ii][0] = ((User)userDataList.get(i)).getLogin();
            userData[ii][1] = ((User)userDataList.get(i)).getWholeName(); 
            userData[ii][2] = ((User)userDataList.get(i)).getEmail();
            Date createWhen = ((User)userDataList.get(i)).getCreateWhen();
            userData[ii][3] = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()).format(createWhen);     	                               
            Date dropWhen = ((User)userDataList.get(i)).getDropWhen();
            if (dropWhen != null){
                userData[ii][4] = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,L10n.getCurrentLocale()).format(dropWhen);     	                               
            } else {
                userData[ii][4] = null;
            }            
    	    ii++;
    	}      	    	
    	this.data = userData;    	
    }    
    
    /**
     * Gets the value of the given cell.
     * @param row index of row
     * @param column index of column
     */
    public Object getValueAt(int row, int column)
    {
        return data[row][column];
    }    
    
    /** 
     * Gets number of rows in the actual UserTableModel
     * @return the number of rows in the UserTableModel.
     */
    public int getRowCount()
    {
        return data.length;
    }        

   /**   
     * Gets number of columns in the actual UserTableModel
     * @return the number of columns in the UserTableModel.
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }
    
      /**
     * Gets the name of the specified column
     * @param column index of column
     * @return the name of the specified column.
     */
    public String getColumnName(int column){
        return columnNames[column];
    }
    
    /**
     * Gets right Class - Date Object in the CREATEWHEN, DROPWHEN column and String Object in other columns. 
     * @param column index of column
     * @return the Class for Object instances in the specified column.
     */
    public Class getColumnClass(int column) {
    	switch (column) {                        
            case 0: return String.class;
            case 1: return String.class;
            case 2: return String.class;
            case 3: return DateFormat.class;
            case 4: return DateFormat.class;
            default: return String.class;
        }
    }
}
