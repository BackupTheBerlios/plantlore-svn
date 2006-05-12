/*
 * MetadataManagerTableModel.java
 *
 * Created on 22. duben 2006, 22:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.metadata;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 *
 * @author Lada
 */
public class MetadataManagerTableModel  extends AbstractTableModel  {
    
  
        //Logger
    private Logger logger;
    // MetadataManager model
    private MetadataManager model; 
    private ArrayList<Metadata> metadataDataList;
    
    /** Names of the columns */
    private String[] columnNames;
    /** Data values displayed in the table*/
    private Object[][] data;

    public final static int SOURCEINSTITUTIONID = 0;
    public final static int SOURCEID = 1;
    public final static int DATASETTITLE = 2; 
    public final static int TECHNICALCONTACTNAME = 3;   
    public final static int CONTENTCONTACTNAME = 4;          
    public final static int DATECREATE = 5;
    public final static int DATEMODIFIED = 6;    
    
    /** Creates a new instance of MetadataManagerTableModel */
    public MetadataManagerTableModel(MetadataManager model) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();    	
    }  
   
    private void initColumns() {
        columnNames = new String[7];                  
        columnNames[0] = L10n.getString("metadata.sourceInstitutionId");   
        columnNames[1] = L10n.getString("metadata.sourceId");  
        columnNames[2] = L10n.getString("metadata.dataSetTitle");
        columnNames[3] = L10n.getString("metadata.technicalContactName"); 
        columnNames[4] = L10n.getString("metadata.contentContactName");                      
        columnNames[5] = L10n.getString("metadata.dateCreate"); 
        columnNames[6] = L10n.getString("metadata.dateModified");          
    }       
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	
    	logger.debug("Metadata - Init data.");
    	
    	metadataDataList = model.getMetadataList();
    	if (metadataDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}    	  	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(metadataDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;   
        int ii = 0;
    	//loud data for view
        Object[][] metadataData = new Object[countRow][7];   
    	for (int i=firstRow-1; i < countResult; i++) {     
            metadataData[ii][0] = ((Metadata)metadataDataList.get(i)).getSourceInstitutionId();  
            metadataData[ii][1] = ((Metadata)metadataDataList.get(i)).getSourceId();  
            metadataData[ii][2] = ((Metadata)metadataDataList.get(i)).getDataSetTitle();
            metadataData[ii][3] = ((Metadata)metadataDataList.get(i)).getTechnicalContactName();    	    
            metadataData[ii][4] = ((Metadata)metadataDataList.get(i)).getContentContactName();                                 
            metadataData[ii][5] = ((Metadata)metadataDataList.get(i)).getDateCreate();
            metadataData[ii][6] = ((Metadata)metadataDataList.get(i)).getDateModified();            
    	    ii++;
    	}      	    	
    	this.data = metadataData;    	
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
     * Gets number of rows in the actual MetadataTableModel
     * @return the number of rows in the MetadataTableModel.
     */
    public int getRowCount()
    {
        return data.length;
    }        

   /**   
     * Gets number of columns in the actual MetadataTableModel
     * @return the number of columns in the MetadataTableModel.
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
