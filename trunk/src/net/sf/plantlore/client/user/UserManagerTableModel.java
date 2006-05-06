/*
 * UserManagerTableModel.java
 *
 * Created on 22. duben 2006, 22:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.user;

import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.User;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class UserManagerTableModel  extends AbstractTableModel  {
    
  
        //Logger
    private Logger logger;
    // UserManager model
    private UserManager model; 
    private ArrayList<User> userDataList;
    
    /** Names of the columns */
    private String[] columnNames;
    /** Data values displayed in the table*/
    private Object[][] data;

    public final static int LOGIN = 0;
    public final static int WHOLENAME = 1;
    public final static int EMAIL = 2;       
    public final static int  CREATEWHEN = 3;          
    public final static int DROPWHEN = 4;        
    
    /**
     * Creates a new instance of UserManagerTableModel
     */
    public UserManagerTableModel(UserManager model) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();    	
    }  
   
    private void initColumns() {
        columnNames = new String[5];                  
        columnNames[0] = L10n.getString("loginUser");   
        columnNames[1] = L10n.getString("nameUser");  
        columnNames[2] = L10n.getString("emailUser");
        columnNames[3] = L10n.getString("createWhenUser"); 
        columnNames[4] = L10n.getString("dropWhenUser");                              
    }       
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	
    	logger.debug("User - Init data.");
    	
    	userDataList = model.getUserList();
    	if (userDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}    	  	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(userDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;   
        int ii = 0;
    	//loud data for view
        Object[][] userData = new Object[countRow][5];   
    	for (int i=firstRow-1; i < countResult; i++) {     
            userData[ii][0] = ((User)userDataList.get(i)).getLogin();
            userData[ii][1] = ((User)userDataList.get(i)).getWholeName(); 
            userData[ii][2] = ((User)userDataList.get(i)).getEmail();
            //userData[ii][3] = ((User)userDataList.get(i)).getCreateWhen().toString();  	    
            //userData[ii][4] = ((User)userDataList.get(i)).getDropWhen().toString();                 
            userData[ii][3] = new Date();
            userData[ii][4] = new Date();
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
}
