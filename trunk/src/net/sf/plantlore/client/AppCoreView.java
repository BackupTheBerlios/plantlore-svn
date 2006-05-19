/*
 * AppCoreView.java
 *
 * Created on 14. leden 2006, 17:58
 *
 */

package net.sf.plantlore.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import net.sf.plantlore.common.ComponentAdjust;
import net.sf.plantlore.common.StatusBarManager;

import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;

/** Application core view
 *
 * Creates the main application window and its contents like data overview, menu,
 * toolbars, etc.
 * Listeners of the components are set by the <code>AppCoreCtrl</code> class.
 * Sets itself as observer of AppCore.
 *
 * @author Jakub
 */
public class AppCoreView extends JFrame implements Observer 
{
    private Logger logger;
    private Preferences prefs;
    private AppCore model;
    //private JFrame frame;
    private Container container;
    private JPanel mainPane;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu
            fileMenu = new JMenu(L10n.getString("Overview.MenuFile")),
            dataMenu = new JMenu(L10n.getString("Overview.MenuData")),
            helpMenu = new JMenu(L10n.getString("Overview.MenuHelp")); 
    private JMenuItem settings = new JMenuItem(L10n.getString("Overview.MenuSettings"));
    private JMenuItem print = new JMenuItem(L10n.getString("Overview.MenuPrint"));
    private JMenuItem exit = new JMenuItem(L10n.getString("Overview.MenuExit"));
    private JMenuItem helpContents = new JMenuItem(L10n.getString("Overview.MenuHelpContents"));
    private JMenuItem helpAbout = new JMenuItem(L10n.getString("Overview.MenuHelpAbout"));
    private JMenuItem dataAuthors = new JMenuItem(L10n.getString("Overview.MenuAuthorManager"));
    private JMenuItem dataPublications = new JMenuItem(L10n.getString("Overview.MenuPublicationManager")); 
    private JMenuItem dataUser = new JMenuItem(L10n.getString("Overview.MenuUserManager"));
    private JMenuItem dataMetadata = new JMenuItem(L10n.getString("Overview.MenuMetadataManager"));
    private JMenuItem dataHistory = new JMenuItem(L10n.getString("Overview.MenuHistory"));
    private JMenuItem dataWholeHistory = new JMenuItem(L10n.getString("Overview.MenuwholeHistory"));
    private JMenuItem dataImport = new JMenuItem(L10n.getString("Overview.MenuDataImport"));
    private JMenuItem dataExport = new JMenuItem(L10n.getString("Overview.MenuDataExport"));
    private JMenuItem dataSearch = new JMenuItem(L10n.getString("Overview.MenuDataSearch"));
    private JMenuItem login = new JMenuItem(L10n.getString("Overview.MenuLogin"));
    
    private JButton 
            importButton = new JButton(),
            exportButton = new JButton(), 
            searchButton = new JButton(),
            addButton = new JButton(),
            editButton = new JButton(),
            deleteButton = new JButton(),
            selectAll = new JButton(),
            selectNone = new JButton(),
            invertSelected  = new JButton(),
            prevPage = new JButton(),
            nextPage = new JButton(),
            schedaButton = new JButton();
    
    private JScrollPane overviewScrollPane;
    private JLabel statusLabel;
    
    protected JTable overview = new JTable();
    private JToolBar mainToolBar;
    private JToolBar pageToolBar;
    private JFormattedTextField recordsPerPage = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JLabel pageStatus = new JLabel("-/-");
    private JLabel recordsCount = new JLabel("-");
    private StatusBarManager sbm;
    
    /** Creates a new instance of AppCoreView */
    public AppCoreView(AppCore model)
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());                
        this.model = model;
        model.addObserver(this); 
        prefs = Preferences.userNodeForPackage(this.getClass());
    }

    public void update(Observable observable, Object object)
    {
        if (object != null && object instanceof String) {
            String arg = (String) object;
            if (arg.equals("PAGE_CHANGED")||arg.equals("RECORDS_PER_PAGE")) {
                recordsCount.setText(""+model.getResultsCount());
                pageStatus.setText(""+model.getCurrentPage()+"/"+model.getPagesCount());
                //FIXME: change selection only if really required
                overview.changeSelection(model.getSelectedRowNumber(),0,false,false);
                return;
            }
            if (arg.equals("NEW_QUERY")) {
                recordsCount.setText(""+model.getResultsCount());
                pageStatus.setText(""+model.getCurrentPage()+"/"+model.getPagesCount());                
                return;
            }
        }
    }
    
    /** Calls all the constructing init methods.
     *
     */
    public void init()
    {
        initFrame();
        initStatusBar();
        initMenu();
        constructOverview();
        initMainToolBar();
        this.pack();
    }

    /** Constructs the main Plantlore JFrame.
     *
     */
    public void initFrame() 
    {
//        frame = new JFrame(L10n.getString("plantlore"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        this.setVisible(false);
        this.setJMenuBar(menuBar);
        container = this.getContentPane();
        container.setLayout(new BorderLayout());
        mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());
        container.add(mainPane, BorderLayout.CENTER);
    }

    /** Constructs main menu. Mnemonics should be fixed according to L10n!
     *
     */
    private void initMenu()
    {
        fileMenu.setMnemonic(L10n.getMnemonic("Overview.MenuFile"));
        fileMenu.add(login);
        fileMenu.add(settings);
        fileMenu.add(print);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        
        dataMenu.setMnemonic(L10n.getMnemonic("Overview.MenuData"));
        dataMenu.add(dataAuthors);
        dataMenu.add(dataPublications);  
        dataMenu.add(dataMetadata);
        dataMenu.add(dataImport);
        dataMenu.add(dataExport);
        dataMenu.add(dataSearch);
        dataMenu.add(dataHistory); 
        dataMenu.add(dataWholeHistory); 
        dataMenu.add(dataUser);

        helpMenu.setMnemonic(L10n.getMnemonic("Overview.MenuHelp"));
        helpMenu.add(helpContents);
        helpMenu.addSeparator();
        helpMenu.add(helpAbout);
        
        menuBar.add(fileMenu);
        menuBar.add(dataMenu);
        menuBar.add(helpMenu);
    }


    /** Constructs the main toolbar.
     *
     */
    private void initMainToolBar()
    {
        mainToolBar = new JToolBar();
        mainToolBar.add(addButton);
        mainToolBar.add(editButton);
        mainToolBar.add(deleteButton);
        mainToolBar.add(searchButton);
        mainToolBar.add(schedaButton);
        container.add(mainToolBar, BorderLayout.NORTH);
        
        sbm.add(searchButton, L10n.getString("Overview.SearchTT"));
    }
    
    /** Constructs the main status bar and initializes the <code>sbm StatusBarManager</code> appropriately.
     *
     */
    private void initStatusBar() 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        statusLabel = new JLabel(L10n.getString("statusReady"));
        panel.add(statusLabel, BorderLayout.CENTER);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        container.add(panel, BorderLayout.SOUTH);
        sbm = new StatusBarManager(statusLabel);    
        sbm.setDefaultText(L10n.getString("statusReady"));
    }
    
    /** Constructs the data overview and adds it to the <code>mainPane</code>.
     *
     */
    private void constructOverview()
    {
        overview.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        overviewScrollPane = new JScrollPane(tablePanel);
        tablePanel.add(overview.getTableHeader(), BorderLayout.PAGE_START);
        tablePanel.add(overview, BorderLayout.CENTER);
        mainPane.add(overviewScrollPane, BorderLayout.CENTER);
        
        pageToolBar = new JToolBar();
        recordsPerPage.setPreferredSize(new Dimension(40, 10));
        recordsPerPage.setHorizontalAlignment(JTextField.CENTER);
        pageToolBar.setFloatable(false);
        pageToolBar.setRollover(true);

        JToolBar selectToolBar = new JToolBar();
        JButton refresh = new JButton("Refresh");
        pageToolBar.add(selectAll);
        pageToolBar.add(selectNone);
        pageToolBar.add(invertSelected);
        pageToolBar.addSeparator();        
        pageToolBar.add(invertSelected);
        pageToolBar.addSeparator();        
        pageToolBar.add(prevPage);
        pageToolBar.add(recordsPerPage);
        pageToolBar.add(nextPage);
        
        recordsCount.setToolTipText(L10n.getString("overviewRecordsCountTT"));
        pageStatus.setToolTipText(L10n.getString("overviewPageStatusTT"));
        JPanel controlPane = new JPanel(new BorderLayout());
        JPanel topControlPane = new JPanel(new FlowLayout());
        JPanel toolBarPane = new JPanel(new FlowLayout());
//        toolBarPane.add(selectToolBar);
        toolBarPane.add(pageToolBar);
        topControlPane.add(new JLabel(L10n.getString("overviewRecordsCount")));
        topControlPane.add(recordsCount);
        topControlPane.add(new JLabel(L10n.getString("overviewPageStatus")));
        topControlPane.add(pageStatus);
        controlPane.add(topControlPane, BorderLayout.NORTH);
        controlPane.add(toolBarPane, BorderLayout.SOUTH);
        mainPane.add(controlPane, BorderLayout.SOUTH);
        ComponentAdjust ca = new ComponentAdjust();
        ca.add(prevPage);
        ca.add(nextPage);
        ca.setMaxWidth();
        ca.clear();
        ca.add(selectAll);
        ca.add(selectNone);
        ca.add(invertSelected);
        ca.setMaxWidth();
        
        sbm.add(prevPage, L10n.getString("Overview.PreviousPage"));
        sbm.add(nextPage, L10n.getString("Overview.NextPage"));
        sbm.add(recordsPerPage, L10n.getString("Overview.RecordsPerPage"));
    }
    
    /** This method should be called right after the user logs into some database.
     *
     */
    public void initOverview()
    {
        TableColumn tc;
        OverviewTableModel otm = model.getTableModel();
        TableSorter tableSorter = new TableSorter(otm);
        //FIXME: what if otm == null ????????????
        overview.setModel(tableSorter);
        tableSorter.setTableHeader(overview.getTableHeader());
        
        // Comment to established db connection automatically without the login procedure        
        overviewScrollPane.setPreferredSize(new Dimension(800, (otm.getRowCount()+1)*25));
       
        for (int i = 0; i < otm.getColumnCount(); i++) {
            tc = overview.getColumnModel().getColumn(i);
            tc.setPreferredWidth(otm.getColumnSize(i));
        }
        recordsPerPage.setValue(new Integer(model.getRecordsPerPage()));        
        pack();
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
    	login.setAction(a);
    }
    
    /** Sets an action to the settings menu item.
     *
     */
    public void setSettingsAction(AbstractAction a) {
        settings.setAction(a);
    }
    /** Sets an action to the print menu item.
     *
     */
    public void setPrintAction(AbstractAction a) {
        print.setAction(a);
    }
    /** Adds listener to the exit menu item.
     *
     */
    public void addExitListener(ActionListener al) {
        exit.addActionListener(al);
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
    }
    
    /** Sets an action to the editButton.
     *
     */
    public void setEditAction(AbstractAction a) {
        editButton.setAction(a);
    }
    
    /** Sets an action to the deleteButton.
     *
     */
    public void setDeleteAction(AbstractAction a) {
        deleteButton.setAction(a);
    }
    
    
    /** Sets an action to the Data->Search menu item and to the Search toolbar button.
     *
     */
    public void setSearchAction(AbstractAction a) {
        dataSearch.setAction(a);
        searchButton.setAction(a);
    }

    /** Sets an action to the Data->Search menu item and to the Search toolbar button.
     *
     */
    public void setSchedaAction(AbstractAction a) {
        schedaButton.setAction(a);
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
        importButton.setAction(a);
    }

    /** Sets an action to the Data->export menu item and to the Export toolbar button.
     *
     */
    public void setExportAction(AbstractAction a) {
        dataExport.setAction(a);
        exportButton.setAction(a);
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
        dataPublications.setAction(a);
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
        dataHistory.setAction(a);
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
    
    /** Returns the frame of the main window.
     *
     */
/*    protected JFrame getFrame() {
        return this.frame;
    }     */
}
