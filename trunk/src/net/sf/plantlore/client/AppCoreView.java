/*
 * AppCoreViewMatisse.java
 *
 * Created on 26. květen 2006, 17:45
 */

package net.sf.plantlore.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFormattedTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;
import net.sf.plantlore.client.overview.*;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.DocumentSizeFilter;
import net.sf.plantlore.common.PlantloreHelp;
import net.sf.plantlore.common.StatusBarManager;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

/**
 *
 * @author  fraktalek
 */
public class AppCoreView extends javax.swing.JFrame implements Observer {
    AppCore model;
    Logger logger;
    Preferences prefs;
    private StatusBarManager sbm;
    
    /** Creates new form AppCoreViewMatisse */
    public AppCoreView(AppCore model) {
        this.model = model;
        prefs = Preferences.userNodeForPackage(this.getClass());        
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
        initComponents();
        sbm = new OverviewStatusBarManager(statusLabel);    
        sbm.setDefaultText(L10n.getString("Overview.StatusReady"));

        initOverview();
        
        fileExit.setText(L10n.getString("Overview.MenuFileExit"));
        fileExit.setToolTipText(L10n.getString("Overview.MenuFileExitTT"));
        fileExit.setMnemonic(L10n.getMnemonic("Overview.MenuFileExit"));
        
        PlantloreHelp.addButtonHelp(PlantloreHelp.PLANTLORE_OVERVIEW,helpContents);

        setLocationRelativeTo(null);
        model.addObserver(this); 
        model.addObserver(sbm);
        progressBar.setVisible(false);
        progressBar.setBorderPainted(false);
        setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(AppCoreView.class.getResource("resources/icon.gif")));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        mainToolBar = new javax.swing.JToolBar();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        schedaButton = new javax.swing.JButton();
        historyButton = new javax.swing.JButton();
        habitatTreeButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        pageToolBar = new javax.swing.JToolBar();
        selectAll = new javax.swing.JButton();
        selectNone = new javax.swing.JButton();
        invertSelected = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        prevPage = new javax.swing.JButton();
        nextPage = new javax.swing.JButton();
        recordsPerPage = new javax.swing.JFormattedTextField();
        refreshButton = new javax.swing.JButton();
        overviewPanel = new javax.swing.JPanel();
        overviewScrollPane = new javax.swing.JScrollPane();
        overview = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        recordsCount = new javax.swing.JLabel();
        pageStatus = new javax.swing.JLabel();
        recordsCountLabel = new javax.swing.JLabel();
        pageStatusLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileLogin = new javax.swing.JMenuItem();
        fileLogout = new javax.swing.JMenuItem();
        fileReconnect = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        dataImport = new javax.swing.JMenuItem();
        dataImportTable = new javax.swing.JMenuItem();
        dataExport = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        fileSettings = new javax.swing.JMenuItem();
        filePrint = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        fileExit = new javax.swing.JMenuItem();
        dataMenu = new javax.swing.JMenu();
        dataAuthors = new javax.swing.JMenuItem();
        dataPublication = new javax.swing.JMenuItem();
        dataMetadata = new javax.swing.JMenuItem();
        dataUser = new javax.swing.JMenuItem();
        dataWholeHistory = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        dataCreateDatabase = new javax.swing.JMenuItem();
        occurrencesMenu = new javax.swing.JMenu();
        occurrencesAdd = new javax.swing.JMenuItem();
        occurrencesEdit = new javax.swing.JMenuItem();
        occurrencesDelete = new javax.swing.JMenuItem();
        occurrencesScheda = new javax.swing.JMenuItem();
        occurrencesHistory = new javax.swing.JMenuItem();
        occurrencesSearch = new javax.swing.JMenuItem();
        occurrencesRefresh = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpContents = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        helpAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(L10n.getString("Plantlore.Title"));
        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Add24.gif")));
        addButton.setToolTipText(L10n.getString("Overview.AddTT"));
        mainToolBar.add(addButton);

        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Edit24.gif")));
        mainToolBar.add(editButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Delete24.gif")));
        deleteButton.setToolTipText(L10n.getString("Overview.DeleteTT"));
        mainToolBar.add(deleteButton);

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Search24.gif")));
        mainToolBar.add(searchButton);

        schedaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ComposeMail24.gif")));
        mainToolBar.add(schedaButton);

        historyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/History24.gif")));
        mainToolBar.add(historyButton);

        habitatTreeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/development/Application24.gif")));
        mainToolBar.add(habitatTreeButton);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusLabel.setText(L10n.getString("Overview.StatusReady"));

        progressBar.setForeground(new java.awt.Color(51, 255, 51));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(statusLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 424, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 314, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(statusLabel)
                .addContainerGap())
            .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
        );

        pageToolBar.setFloatable(false);
        pageToolBar.setRollover(true);
        selectAll.setText("All");
        pageToolBar.add(selectAll);

        selectNone.setText("None");
        pageToolBar.add(selectNone);

        invertSelected.setText("Invert");
        pageToolBar.add(invertSelected);

        jPanel3.setPreferredSize(new java.awt.Dimension(100, 70));
        prevPage.setText("Prev");
        prevPage.setMaximumSize(new java.awt.Dimension(60, 26));
        prevPage.setMinimumSize(new java.awt.Dimension(60, 26));
        prevPage.setPreferredSize(new java.awt.Dimension(60, 26));

        nextPage.setText("Next");
        nextPage.setMaximumSize(new java.awt.Dimension(62, 26));
        nextPage.setMinimumSize(new java.awt.Dimension(62, 26));
        nextPage.setPreferredSize(new java.awt.Dimension(62, 26));

        recordsPerPage.setText("jFormattedTextField1");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(123, 123, 123)
                .add(prevPage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(recordsPerPage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(nextPage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(317, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(recordsPerPage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(nextPage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(prevPage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {nextPage, recordsPerPage}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pageToolBar.add(jPanel3);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Refresh24.gif")));
        refreshButton.setToolTipText(L10n.getString("Overview.RefreshTT"));
        pageToolBar.add(refreshButton);

        overview.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        overviewScrollPane.setViewportView(overview);

        org.jdesktop.layout.GroupLayout overviewPanelLayout = new org.jdesktop.layout.GroupLayout(overviewPanel);
        overviewPanel.setLayout(overviewPanelLayout);
        overviewPanelLayout.setHorizontalGroup(
            overviewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, overviewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
        );
        overviewPanelLayout.setVerticalGroup(
            overviewPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(overviewScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
        );

        recordsCount.setText("-");

        pageStatus.setText("-/-");

        recordsCountLabel.setText(L10n.getString("Overview.RecordsCount"));

        pageStatusLabel.setText(L10n.getString("Overview.PageStatus"));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(704, Short.MAX_VALUE)
                .add(recordsCountLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(recordsCount)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pageStatusLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pageStatus)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(pageStatusLabel)
                    .add(pageStatus)
                    .add(recordsCount)
                    .add(recordsCountLabel))
                .addContainerGap())
        );

        fileMenu.setMnemonic(L10n.getMnemonic("Overview.MenuFile"));
        fileMenu.setText(L10n.getString("Overview.MenuFile"));
        fileLogin.setText("Item");
        fileMenu.add(fileLogin);

        fileLogout.setText("Item");
        fileMenu.add(fileLogout);

        fileReconnect.setText("Item");
        fileMenu.add(fileReconnect);

        fileMenu.add(jSeparator2);

        dataImport.setText("Item");
        fileMenu.add(dataImport);

        dataImportTable.setText("Item");
        fileMenu.add(dataImportTable);

        dataExport.setText("Item");
        fileMenu.add(dataExport);

        fileMenu.add(jSeparator3);

        fileSettings.setText("Item");
        fileMenu.add(fileSettings);

        filePrint.setText("Item");
        fileMenu.add(filePrint);

        fileMenu.add(jSeparator1);

        fileExit.setText("Item");
        fileMenu.add(fileExit);

        jMenuBar1.add(fileMenu);

        dataMenu.setMnemonic(L10n.getMnemonic("Overview.MenuData"));
        dataMenu.setText(L10n.getString("Overview.MenuData"));
        dataAuthors.setText("Item");
        dataMenu.add(dataAuthors);

        dataPublication.setText("Item");
        dataMenu.add(dataPublication);

        dataMetadata.setText("Item");
        dataMenu.add(dataMetadata);

        dataUser.setText("Item");
        dataMenu.add(dataUser);

        dataWholeHistory.setText("Item");
        dataMenu.add(dataWholeHistory);

        dataMenu.add(jSeparator5);

        dataCreateDatabase.setText("Item");
        dataMenu.add(dataCreateDatabase);

        jMenuBar1.add(dataMenu);

        occurrencesMenu.setMnemonic(L10n.getMnemonic("Overview.MenuOccurrences"));
        occurrencesMenu.setText(L10n.getString("Overview.MenuOccurrences"));
        occurrencesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                occurrencesMenuActionPerformed(evt);
            }
        });

        occurrencesAdd.setText("Item");
        occurrencesMenu.add(occurrencesAdd);

        occurrencesEdit.setText("Item");
        occurrencesMenu.add(occurrencesEdit);

        occurrencesDelete.setText("Item");
        occurrencesMenu.add(occurrencesDelete);

        occurrencesScheda.setText("Item");
        occurrencesMenu.add(occurrencesScheda);

        occurrencesHistory.setText("Item");
        occurrencesMenu.add(occurrencesHistory);

        occurrencesSearch.setText("Item");
        occurrencesMenu.add(occurrencesSearch);

        occurrencesRefresh.setText("Item");
        occurrencesMenu.add(occurrencesRefresh);

        jMenuBar1.add(occurrencesMenu);

        helpMenu.setMnemonic(L10n.getMnemonic("Overview.MenuHelp"));
        helpMenu.setText(L10n.getString("Overview.MenuHelp"));
        helpContents.setText("Item");
        helpMenu.add(helpContents);

        helpMenu.add(jSeparator4);

        helpAbout.setText("Item");
        helpMenu.add(helpAbout);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, overviewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pageToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(mainToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(overviewPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(14, 14, 14)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pageToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void occurrencesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_occurrencesMenuActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_occurrencesMenuActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])  throws DocumentException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new AppCoreView(new AppCore(new MainConfig("xx")));
                } catch (DocumentException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void update(final Observable observable, final Object object) {
                if (object != null && object instanceof String) {
                    String arg = (String) object;
                    if (arg.equals("PAGE_CHANGED")||arg.equals("RECORDS_PER_PAGE")) {
                        try {
                            recordsCount.setText(""+model.getResultsCount());
                        } catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(this, ex);
                            return;
                        }
                        pageStatus.setText(""+model.getCurrentPage()+"/"+model.getPagesCount());
                        //TODO: change selection only if really required
                        overview.changeSelection(model.getSelectedRowNumber(),0,false,false);
                        return;
                    }
                    if (arg.equals("NEW_QUERY")) {
                        //setPreferredColumnSizes();
                        overview.setEnabled(true);
                        overview.setVisible(true);
                        try {
                            recordsCount.setText(""+model.getResultsCount());
                        } catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(this, ex);
                            return;
                        }
                        pageStatus.setText(""+model.getCurrentPage()+"/"+model.getPagesCount()); 
                        overview.getSelectionModel().setSelectionInterval(0,0);
                        return;
                    }
                    if (arg.equals("LOADING_NEW_DATA")) {
                        //TableSorter and OverviewTable model threw exceptions while loading data
                        //because they were trying to display data in possibly inconsistent state
                        //I hope that I've fixed it at least in OverviewTableModel where data are now
                        //loaded into a new variable and then put in place of the working data at one point
                        //
                        //However now I noticed a NullPointerException from TableSorter, which is not my work
                        //and I don't want to study it now, so we'll try to ensure that overview doesn't ask
                        //for any data during the loading:
                        overview.setVisible(false);
                        overview.setEnabled(false);
                    }
                    if (arg.equals("SELECTION_CHANGED")) {
                        overview.getSelectionModel().setSelectionInterval(model.getSelectedRowNumber(),model.getSelectedRowNumber());
                    }
                }//if instanceof String        
    }//update()

    private void setPreferredColumnSizes() {
        OverviewTableModel otm = model.getTableModel();
        TableColumn tc;
        for (int i = 0; i < otm.getColumnCount(); i++) {
            tc = overview.getColumnModel().getColumn(i);
            tc.setPreferredWidth(otm.getColumnSize(i));
        }        
    }
    
    public void initOverview()
    {
        TableColumn tc;
        OverviewTableModel otm = model.getTableModel();
        TableSorter tableSorter = model.getTableSorter();

        overview.setModel(tableSorter);
        tableSorter.setTableHeader(overview.getTableHeader());
        overview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Comment to established db connection automatically without the login procedure        
        //overviewScrollPane.setPreferredSize(new Dimension(800, (otm.getRowCount()+1)*19));
       
        setPreferredColumnSizes();
        recordsPerPage.setValue(new Integer(model.getRecordsPerPage()));  
        overview.getSelectionModel().setSelectionInterval(0,0);
        overview.setAutoCreateColumnsFromModel(true);
        overviewScrollPane.getViewport().setBackground(Color.WHITE);        
    }

    /** Returns the main window <code>StatusBarManager</code>.
     *
     */
    public StatusBarManager getSBM() 
    {
        return sbm;
    }
    
    
    /**
     * Set an action to the login menu item.
     * 
     * @param a The action that should be called when clicked on this menuitem.
     */
    public void setLoginAction(Action a) {
    	fileLogin.setAction(a);
    }
    
    /** Sets an action to the settings menu item.
     *
     */
    public void setSettingsAction(AbstractAction a) {
        fileSettings.setAction(a);
    }
    /** Sets an action to the print menu item.
     *
     */
    public void setPrintAction(AbstractAction a) {
        filePrint.setAction(a);
    }
    /** Adds listener to the exit menu item.
     *
     */
    public void addExitAction(ActionListener al) {
        fileExit.addActionListener(al);
    }
    /** Adds listener to the HelpContents menu item.
     *
     */
    public void setHelpContentsAction(AbstractAction a) {
        helpContents.setAction(a);
    }
    /** Adds listener to the HelpAbout menu item.
     *
     */
    public void setHelpAboutAction(AbstractAction a) {
        helpAbout.setAction(a);
    }

    /** Sets an action to the addButton.
     *
     */
    public void setAddAction(AbstractAction a) {
        addButton.setAction(a);
        occurrencesAdd.setAction(a);
    }
    
    /** Sets an action to the editButton.
     *
     */
    public void setEditAction(AbstractAction a) {
        editButton.setAction(a);
        occurrencesEdit.setAction(a);
    }
    
    /** Sets an action to the deleteButton.
     *
     */
    public void setDeleteAction(AbstractAction a) {
        deleteButton.setAction(a);
        occurrencesDelete.setAction(a);
    }
    
    
    /** Sets an action to the Data->Search menu item and to the Search toolbar button.
     *
     */
    public void setSearchAction(AbstractAction a) {
        searchButton.setAction(a);
        occurrencesSearch.setAction(a);
    }

    /** Sets an action to the Data->Search menu item and to the Search toolbar button.
     *
     */
    public void setSchedaAction(AbstractAction a) {
        schedaButton.setAction(a);
        occurrencesScheda.setAction(a);
    }

     /** Sets an action to the History toolbar button.
     *
     */
    public void setHistoryRecordAction(AbstractAction a) {
        historyButton.setAction(a);
        occurrencesHistory.setAction(a);
    }
    
    /** Sets an action to the selectAll button.
     *
     */
    public void setSelectAllAction(AbstractAction a) {
        selectAll.setAction(a);
    }

    /** Sets an action to the selectAll button.
     *
     */
    public void setSelectNoneAction(AbstractAction a) {
        selectNone.setAction(a);
    }

    /** Sets an action to the selectAll button.
     *
     */
    public void setInvertSelectedAction(AbstractAction a) {
        invertSelected.setAction(a);
    }

    /** Sets an action to the Data->import menu item and to the Import toolbar button.
     *
     */
    public void setImportAction(AbstractAction a) {
        dataImport.setAction(a);
    }
    
    /** Sets an action to the Data->TableImport menu item and to the Import toolbar button.
    *
    */
   public void setTableImportAction(AbstractAction a) {
       dataImportTable.setAction(a);
   }

    /** Sets an action to the Data->export menu item and to the Export toolbar button.
     *
     */
    public void setExportAction(AbstractAction a) {
        dataExport.setAction(a);
    }

    /** Sets an action to the previous page button.
     *
     */
    public void setPrevPageAction(AbstractAction a) {
        prevPage.setAction(a);
    }

    /** Sets an action to the next page button.
     *
     */
    public void setNextPageAction(AbstractAction a) {
        nextPage.setAction(a);
    }

    /** Adds a listener to the main window frame.
     *
     */
    /*public void addWindowListener(WindowAdapter wa) {
        this.addWindowListener(wa);
    }*/

    /** Adds a listener to the Author manager menu item.
     *
     */
    public void addDataAuthorsAction(AbstractAction a) {
        dataAuthors.setAction(a);
    }
    
    /** Adds a listener to the Publication manager menu item.
     *
     */
    public void addDataPublicationsAction(AbstractAction a) {
        dataPublication.setAction(a);
    }    
    
    /** Adds a listener to the User manager menu item.
     *
     */
    public void addDataUserAction(AbstractAction a) {
        dataUser.setAction(a);
    }   
    
    /** Adds a listener to the HistoryData menu item.
    *
    */
    public void addDataHistoryAction(AbstractAction a) {
        historyButton.setAction(a);
    }
    
    /** Sets an action to the MetadataData menu item.
    *
    */
    public void addDataMetadataAction(AbstractAction a) {
        dataMetadata.setAction(a);
    }
    
    
     /** Sets an action to the HistoryData menu item.
    *
    */
    public void addDataWholeHistoryAction(AbstractAction a) {
        dataWholeHistory.setAction(a);
    }
    
    public void setRecordsPerPageListener(PropertyChangeListener p)
    {
        recordsPerPage.addPropertyChangeListener(p);
    }
    
    public void setSelectedRowListener(ListSelectionListener l)
    {
        overview.getSelectionModel().addListSelectionListener(l);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton addButton;
    protected javax.swing.JMenuItem dataAuthors;
    protected javax.swing.JMenuItem dataCreateDatabase;
    private javax.swing.JMenuItem dataExport;
    private javax.swing.JMenuItem dataImport;
    protected javax.swing.JMenuItem dataImportTable;
    protected javax.swing.JMenu dataMenu;
    protected javax.swing.JMenuItem dataMetadata;
    protected javax.swing.JMenuItem dataPublication;
    protected javax.swing.JMenuItem dataUser;
    protected javax.swing.JMenuItem dataWholeHistory;
    protected javax.swing.JButton deleteButton;
    protected javax.swing.JButton editButton;
    private javax.swing.JMenuItem fileExit;
    private javax.swing.JMenuItem fileLogin;
    protected javax.swing.JMenuItem fileLogout;
    protected javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem filePrint;
    protected javax.swing.JMenuItem fileReconnect;
    private javax.swing.JMenuItem fileSettings;
    protected javax.swing.JButton habitatTreeButton;
    protected javax.swing.JMenuItem helpAbout;
    protected javax.swing.JMenuItem helpContents;
    protected javax.swing.JMenu helpMenu;
    protected javax.swing.JButton historyButton;
    private javax.swing.JButton invertSelected;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    protected javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JButton nextPage;
    private javax.swing.JMenuItem occurrencesAdd;
    private javax.swing.JMenuItem occurrencesDelete;
    private javax.swing.JMenuItem occurrencesEdit;
    private javax.swing.JMenuItem occurrencesHistory;
    private javax.swing.JMenu occurrencesMenu;
    protected javax.swing.JMenuItem occurrencesRefresh;
    private javax.swing.JMenuItem occurrencesScheda;
    private javax.swing.JMenuItem occurrencesSearch;
    protected javax.swing.JTable overview;
    protected javax.swing.JPanel overviewPanel;
    protected javax.swing.JScrollPane overviewScrollPane;
    private javax.swing.JLabel pageStatus;
    private javax.swing.JLabel pageStatusLabel;
    private javax.swing.JToolBar pageToolBar;
    private javax.swing.JButton prevPage;
    protected javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel recordsCount;
    private javax.swing.JLabel recordsCountLabel;
    protected javax.swing.JFormattedTextField recordsPerPage;
    protected javax.swing.JButton refreshButton;
    protected javax.swing.JButton schedaButton;
    protected javax.swing.JButton searchButton;
    private javax.swing.JButton selectAll;
    private javax.swing.JButton selectNone;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
    
}
