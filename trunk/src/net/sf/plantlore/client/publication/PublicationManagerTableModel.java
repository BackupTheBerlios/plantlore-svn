/*
 * PublicationManagerTableModel.java
 *
 * Created on 22. duben 2006, 22:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.publication;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.Publication;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class PublicationManagerTableModel  extends AbstractTableModel  {
    
  
        //Logger
    private Logger logger;
    // PublicationManager model
    private PublicationManager model; 
    private ArrayList<Publication> publicationDataList;
    
    /** Names of the columns */
    private String[] columnNames;
    /** Data values displayed in the table*/
    private Object[][] data;

    public final static int COLLECTIONNAME = 0;
    public final static int COLLECTIONYEARPUBLICATION = 1;
    public final static int JOURNALNAME = 2; 
    public final static int JOURNALAUTHORNAME = 3;   
    public final static int REFERENCEDETAIL = 4;          
    public final static int URL = 5;
    public final static int NOTE = 6;    
    
    /**
     * Creates a new instance of PublicationManagerTableModel
     */
    public PublicationManagerTableModel(PublicationManager model) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();    	
    }  
   
    private void initColumns() {
        columnNames = new String[7];                  
        columnNames[0] = L10n.getString("collectionName");   
        columnNames[1] = L10n.getString("collectionYearPublication");  
        columnNames[2] = L10n.getString("journalName");
        columnNames[3] = L10n.getString("journalAuthorName"); 
        columnNames[4] = L10n.getString("referenceDetail");                      
        columnNames[5] = L10n.getString("urlPublication"); 
        columnNames[6] = L10n.getString("notePublication");          
    }       
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	
    	logger.debug("Publication - Init data.");
    	
    	publicationDataList = model.getPublicationList();
    	if (publicationDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}    	  	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(publicationDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;   
        int ii = 0;
    	//loud data for view
        Object[][] publicationData = new Object[countRow][7];   
    	for (int i=firstRow-1; i < countResult; i++) {     
            publicationData[ii][0] = ((Publication)publicationDataList.get(i)).getCollectionName();
            publicationData[ii][1] = ((Publication)publicationDataList.get(i)).getCollectionYearPublication().toString(); 
            publicationData[ii][2] = ((Publication)publicationDataList.get(i)).getJournalName();
            publicationData[ii][3] = ((Publication)publicationDataList.get(i)).getJournalAuthorName();  	    
            publicationData[ii][4] = ((Publication)publicationDataList.get(i)).getReferenceDetail();                                 
            publicationData[ii][5] = ((Publication)publicationDataList.get(i)).getUrl();
            publicationData[ii][6] = ((Publication)publicationDataList.get(i)).getNote();          
    	    ii++;
    	}      	    	
    	this.data = publicationData;    	
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
     * Gets number of rows in the actual PublicationTableModel
     * @return the number of rows in the PublicationTableModel.
     */
    public int getRowCount()
    {
        return data.length;
    }        

   /**   
     * Gets number of columns in the actual publicationTableModel
     * @return the number of columns in the publicationTableModel.
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
