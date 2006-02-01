/*
 * AppCoreView.java
 *
 * Created on 14. leden 2006, 17:58
 *
 */

package net.sf.plantlore.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
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
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import net.sf.plantlore.common.ComponentAdjust;
import net.sf.plantlore.common.StatusBarManager;

import net.sf.plantlore.l10n.L10n;

/** Application core view
 *
 * @author Jakub
 */
public class AppCoreView implements Observer
{
    private Preferences prefs;
    private AppCore model;
    private JFrame frame;
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
    
    private JLabel statusLabel;
    
    private JTable overview;
    private JToolBar mainToolBar;
    private JToolBar pageToolBar;
    private JFormattedTextField recordsPerPage;
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
    }

    public void init()
    {
        initFrame();
        initStatusBar();
        initMenu();
        initOverview();
        initMainToolBar();
    }

    /** Constructs the main Plantlore JFrame.
     *
     */
    public void initFrame() 
    {
        frame = new JFrame(L10n.getString("plantlore"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640,480);
        frame.setVisible(false);
        frame.setJMenuBar(menuBar);
        container = frame.getContentPane();
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
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(settings);
        fileMenu.add(print);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        
        dataMenu.setMnemonic(KeyEvent.VK_D);
        dataMenu.add(dataAuthors);
        dataMenu.add(dataPublications);        

        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(helpContents);
        helpMenu.addSeparator();
        helpMenu.add(helpAbout);
        
        menuBar.add(fileMenu);
        menuBar.add(dataMenu);
        menuBar.add(helpMenu);
    }


    private void initMainToolBar()
    {
        mainToolBar = new JToolBar();
        JButton importButton = new JButton("Import");
        mainToolBar.add(importButton);
        JButton exportButton = new JButton("Export");
        mainToolBar.add(exportButton);
        JButton searchButton = new JButton("Search");
        mainToolBar.add(searchButton);
        container.add(mainToolBar, BorderLayout.NORTH);
        
        sbm.add(importButton, "Import some records");
        sbm.add(exportButton, "Export some records");
        sbm.add(searchButton, "Search for records");
    }
    
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
    
    private void initOverview()
    {
        overview = new JTable(new OverviewTableModel());
        overview.setPreferredScrollableViewportSize(new Dimension(600,70));
        JScrollPane sp = new JScrollPane(overview);
        mainPane.add(sp, BorderLayout.CENTER);
        
        pageToolBar = new JToolBar();
        JButton prev = new JButton(L10n.getString("prevButton"));
        recordsPerPage = new JFormattedTextField();
        recordsPerPage.setValue(new Integer(prefs.getInt("recordsPerPage", 10)));
        JButton next = new JButton(L10n.getString("nextButton"));
        pageToolBar.add(prev);
        pageToolBar.add(recordsPerPage);
        pageToolBar.add(next);
//        pageToolBar.setPreferredSize(new Dimension(150, pageToolBar.getPreferredSize().height));
        recordsPerPage.setPreferredSize(new Dimension(50, recordsPerPage.getPreferredSize().height));
        recordsPerPage.setHorizontalAlignment(JTextField.CENTER);
        pageToolBar.setFloatable(false);
        pageToolBar.setRollover(true);

        JPanel toolBarPane = new JPanel(new FlowLayout());
        toolBarPane.add(pageToolBar);
        mainPane.add(toolBarPane, BorderLayout.SOUTH);
        ComponentAdjust ca = new ComponentAdjust();
        ca.add(prev);
        ca.add(next);
        ca.setMaxWidth();
        
        sbm.add(prev, "Previous page");
        sbm.add(next, "Next page");
        sbm.add(recordsPerPage, "Number of records per page");
    }
    
    public StatusBarManager getSBM() 
    {
        return sbm;
    }
    
    /** Hides and shows the main Plantlore window.
     *
     */
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    /** Adds listener to the settings menu item.
     *
     */
    public void addSettingsListener(ActionListener al) {
        settings.addActionListener(al);
    }
    /** Adds listener to the print menu item.
     *
     */
    public void addPrintListener(ActionListener al) {
        print.addActionListener(al);
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
    public void addHelpContentsListener(ActionListener al) {
        helpContents.addActionListener(al);
    }
    /** Adds listener to the HelpAbout menu item.
     *
     */
    public void addHelpAboutListener(ActionListener al) {
        helpAbout.addActionListener(al);
    }

    public void addWindowListener(WindowAdapter wa) {
        frame.addWindowListener(wa);
    }

    public void addDataAuthorsListener(ActionListener a1) {
        dataAuthors.addActionListener(a1);
    }
    
    public void addDataPublicationsListener(ActionListener al) {
        dataPublications.addActionListener(al);
    }    
    
    protected JFrame getFrame() {
        return this.frame;
    }     
}
