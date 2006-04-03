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
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;
import net.sf.plantlore.common.ComponentAdjust;
import net.sf.plantlore.common.StatusBarManager;

import net.sf.plantlore.l10n.L10n;

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
    private Preferences prefs;
    private AppCore model;
    //private JFrame frame;
    private Container container;
    private JPanel mainPane;
    private JMenuBar menuBar = new JMenuBar();
    private JMenu
            fileMenu = new JMenu(L10n.getString("File")),
            dataMenu = new JMenu(L10n.getString("Data")),
            helpMenu = new JMenu(L10n.getString("Help")); 
    private JMenuItem settings = new JMenuItem(L10n.getString("Settings"));
    private JMenuItem print = new JMenuItem(L10n.getString("Print"));
    private JMenuItem exit = new JMenuItem(L10n.getString("Exit"));
    private JMenuItem helpContents = new JMenuItem(L10n.getString("helpContents"));
    private JMenuItem helpAbout = new JMenuItem(L10n.getString("helpAbout"));
    private JMenuItem dataAuthors = new JMenuItem(L10n.getString("authorMgr"));
    private JMenuItem dataPublications = new JMenuItem(L10n.getString("publicationMgr"));    
    private JMenuItem dataHistory = new JMenuItem(L10n.getString("History"));
    private JMenuItem dataImport = new JMenuItem(L10n.getString("dataImport"));
    private JMenuItem dataExport = new JMenuItem(L10n.getString("dataExport"));
    private JMenuItem dataSearch = new JMenuItem(L10n.getString("dataSearch"));
    private JMenuItem login = new JMenuItem(L10n.getString("Login"));
    
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
            nextPage = new JButton();
    
    private JLabel statusLabel;
    
    private JTable overview;
    private JToolBar mainToolBar;
    private JToolBar pageToolBar;
    private JFormattedTextField recordsPerPage = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private JLabel pageStatus = new JLabel("-/-");
    private JLabel recordsCount = new JLabel("-");
    private StatusBarManager sbm;
    
    /** Creates a new instance of AppCoreView */
    public AppCoreView(AppCore model)
    {
        this.model = model;
        model.addObserver(this); 
        prefs = Preferences.userNodeForPackage(this.getClass());
    }

    public void update(Observable observable, Object object)
    {
        recordsCount.setText(""+model.getResultsCount());
        pageStatus.setText(""+model.getCurrentPage()+"/"+model.getPagesCount());
    }
    
    /** Calls all the constructing init methods.
     *
     */
    public void init()
    {
        initFrame();
        initStatusBar();
        initMenu();
        initOverview();
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
        fileMenu.setMnemonic(L10n.getMnemonic("File"));
        fileMenu.add(login);
        fileMenu.add(settings);
        fileMenu.add(print);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        
        dataMenu.setMnemonic(L10n.getMnemonic("Data"));
        dataMenu.add(dataAuthors);
        dataMenu.add(dataPublications);   
        dataMenu.add(dataImport);
        dataMenu.add(dataExport);
        dataMenu.add(dataSearch);
        dataMenu.add(dataHistory); 

        helpMenu.setMnemonic(L10n.getMnemonic("Help"));
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
        container.add(mainToolBar, BorderLayout.NORTH);
        
        sbm.add(searchButton, "Search for records");
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
    private void initOverview()
    {
        OverviewTableModel otm = model.getTableModel();
        overview = new JTable(otm);
        TableColumn tc;
        for (int i = 0; i < otm.getColumnCount(); i++) {
            tc = overview.getColumnModel().getColumn(i);
            tc.setPreferredWidth(otm.getColumnSize(i));
        }
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(tablePanel);
        tablePanel.add(overview.getTableHeader(), BorderLayout.PAGE_START);
        tablePanel.add(overview, BorderLayout.CENTER);
        sp.setPreferredSize(new Dimension(800, (otm.getRowCount()+1)*25));
        mainPane.add(sp, BorderLayout.CENTER);
        
        pageToolBar = new JToolBar();
        recordsPerPage.setValue(new Integer(model.getRecordsPerPage()));
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
        
        sbm.add(prevPage, "Previous page");
        sbm.add(nextPage, "Next page");
        sbm.add(recordsPerPage, "Number of records per page");
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
    public void addDataAuthorsListener(ActionListener a1) {
        dataAuthors.addActionListener(a1);
    }
    
    /** Adds a listener to the Publication manager menu item.
     *
     */
    public void addDataPublicationsListener(ActionListener al) {
        dataPublications.addActionListener(al);
    }    
    
    /** Adds a listener to the HistoryData menu item.
    *
    */
    public void addDataHistoryListener(ActionListener al) {
        dataHistory.addActionListener(al);
    }
    
    public void setRecordsPerPageListener(PropertyChangeListener p)
    {
        recordsPerPage.addPropertyChangeListener(p);
    }
    
    /** Returns the frame of the main window.
     *
     */
/*    protected JFrame getFrame() {
        return this.frame;
    }     */
}
