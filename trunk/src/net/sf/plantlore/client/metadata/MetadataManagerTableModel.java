/*
 * MetadataManagerTableModel.java
 *
 * Created on 22. duben 2006, 22:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client.metadata;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import net.sf.plantlore.common.record.Metadata;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/**
 * Implements a table model for the MetadataManage
 * 
 * @author Lada Oberreiterova
 * @version 1.0
 */
public class MetadataManagerTableModel  extends AbstractTableModel  {
     
	private static final long serialVersionUID = 5504730682131771632L;
	/** Instance of a logger */
    private Logger logger;
    /** Model of MetadataManager */
    private MetadataManager model;
    /** Results of a search query for displaying */
    private ArrayList<Metadata> metadataDataList;    
    /** Names of the columns */
    private String[] columnNames;
    /** Data values displayed in the table*/
    private Object[][] data;

    /** Constants used for identification of columns of table */
    public final static int SOURCEINSTITUTIONID = 0;
    public final static int SOURCEID = 1;
    public final static int DATASETTITLE = 2; 
    public final static int TECHNICALCONTACTNAME = 3;   
    public final static int CONTENTCONTACTNAME = 4;          
    public final static int DATECREATE = 5;
    public final static int DATEMODIFIED = 6;    
    
    /** 
     * Creates a new instance of MetadataManagerTableModel 
     * @param model model of MetadataManager MVC     
     */
    public MetadataManagerTableModel(MetadataManager model) {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
    	this.model = model;        
    	initColumns();    	
    	initData();    	
    }  
   
    /**
     * Init names of columns.     
     */
    private void initColumns() {
        columnNames = new String[7];                  
        columnNames[0] = L10n.getString("MetadataManager.SourceInstitutionId");   
        columnNames[1] = L10n.getString("MetadataManager.SourceId");  
        columnNames[2] = L10n.getString("MetadataManager.DataSetTitle");
        columnNames[3] = L10n.getString("MetadataManager.TechnicalContactName"); 
        columnNames[4] = L10n.getString("MetadataManager.ContentContactName");                      
        columnNames[5] = L10n.getString("MetadataManager.DateCreate"); 
        columnNames[6] = L10n.getString("MetadataManager.DateModified");          
    }       
    
    /**
     * Load data for dislaying 
     */
    public void initData() {
    	
    	logger.debug("Metadata - Init data.");
    	//For no record in result return empty object
    	metadataDataList = model.getMetadataList();
    	if (metadataDataList.size()==0 ){
    		this.data = new Object[0][];
    		return;
    	}    	  	
    	int firstRow = model.getCurrentFirstRow();
    	int countResult = Math.min(metadataDataList.size(), firstRow+ model.getDisplayRows()-1);
    	int countRow = countResult - firstRow + 1;   
        int ii = 0;
    	//load data for view
        Object[][] metadataData = new Object[countRow][7];
        for (int i=firstRow-1; i < countResult; i++) {    	       		                    
            metadataData[ii][0] = ((Metadata)metadataDataList.get(i)).getSourceInstitutionId();  
            metadataData[ii][1] = ((Metadata)metadataDataList.get(i)).getSourceId();  
            metadataData[ii][2] = ((Metadata)metadataDataList.get(i)).getDataSetTitle();
            metadataData[ii][3] = ((Metadata)metadataDataList.get(i)).getTechnicalContactName();    	    
            metadataData[ii][4] = ((Metadata)metadataDataList.get(i)).getContentContactName();                                 
            Date dateCreate = ((Metadata)metadataDataList.get(i)).getDateCreate();
            metadataData[ii][5] = DateFormat.getDateInstance(DateFormat.MEDIUM, L10n.getCurrentLocale()).format(dateCreate);     	                               
            Date dateModified = ((Metadata)metadataDataList.get(i)).getDateModified();            
            metadataData[ii][6] = DateFormat.getDateInstance(DateFormat.MEDIUM, L10n.getCurrentLocale()).format(dateModified);
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
    
      /**
     * Gets right Class - Date Object in the DATECREATE, DATEMODIFIED column and String Object in other columns. 
     * @param column index of column
     * @return the Class for Object instances in the specified column.
     */
    public Class getColumnClass(int column) {
    	switch (column) {                        
            case 0: return String.class;
            case 1: return String.class;
            case 2: return String.class;
            case 3: return String.class;
            case 4: return String.class;
            case 5: return DateFormat.class;
            case 6: return DateFormat.class;
            default: return String.class;
        }
    }
}
