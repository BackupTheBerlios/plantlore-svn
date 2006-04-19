/*
 * AppCoreCtrl.java
 *
 * Created on 14. leden 2006, 18:31
 *
 */

package net.sf.plantlore.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.Integer;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sf.plantlore.client.history.WholeHistory;
import net.sf.plantlore.client.history.WholeHistoryCtrl;
import net.sf.plantlore.client.history.WholeHistoryView;
import net.sf.plantlore.common.record.Author;
import net.sf.plantlore.common.record.AuthorOccurrence;
import net.sf.plantlore.common.record.Occurrence;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.client.authors.AuthorManager;
import net.sf.plantlore.client.authors.AuthorManagerCtrl;
import net.sf.plantlore.client.authors.AuthorManagerView;
import net.sf.plantlore.client.history.History;
import net.sf.plantlore.client.history.HistoryCtrl;
import net.sf.plantlore.client.history.HistoryView;
import net.sf.plantlore.client.login.Login;
import net.sf.plantlore.client.login.LoginCtrl;
import net.sf.plantlore.client.login.LoginView;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.HibernateDBLayer;
import net.sf.plantlore.middleware.RMIDBLayerFactory;

import org.apache.log4j.Logger;

/** Application core controller.
 *
 * Creates and sets listeners for components in <code>AppCoreView</code>.
 *
 * @author Jakub
 */
public class AppCoreCtrl
{
    Logger logger;
    //--------------SUPPLIED MODELS AND VIEWS-----------------
    AppCore model;
    AppCoreView view;
    
    //--------------MODELS AND VIEWS THIS CONTROLLER CREATES-----------------
    Settings settingsModel;
    SettingsView settingsView;
    SettingsCtrl settingsCtrl;
    Preferences prefs;
    
    // History of one occurrence
    History historyModel;
    HistoryView historyView;
    HistoryCtrl historyCtrl;
    
    //History of "database" 
    WholeHistory wholeHistoryModel;
    WholeHistoryView wholeHistoryView;
    WholeHistoryCtrl wholeHistoryCtrl;
    
    // Login
    Login loginModel;
    LoginView loginView;
    LoginCtrl loginCtrl;
    
    /** Creates a new instance of AppCoreCtrl */
    public AppCoreCtrl(AppCore model, AppCoreView view)
    {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        this.model = model;
        this.view = view;
        prefs = Preferences.userNodeForPackage(AppCoreView.class);
        view.setSettingsAction(new SettingsAction());
        view.setPrintAction(new PrintAction());
        view.addExitListener(new ExitListener());
        view.setHelpContentsAction(new HelpContentsAction());
        view.setHelpAboutAction(new HelpAboutAction());
        view.setExportAction(new ExportAction());
        view.setImportAction(new ImportAction());
        
        view.addDataAuthorsListener(new DataAuthorsListener());
        view.addDataPublicationsListener(new DataPublicationsListener());
        view.addDataHistoryListener(new DataHistoryListener());
        view.addDataWholeHistoryListener(new DataWholeHistoryListener());
        
        view.setSearchAction(new SearchAction());
        view.setAddAction(new AddAction());
        view.setEditAction(new EditAction());
        view.setDeleteAction(new DeleteAction());

        view.setSelectAllAction(new SelectAllAction());
        view.setSelectNoneAction(new SelectNoneAction());
        view.setInvertSelectedAction(new InvertSelectedAction());
        view.setNextPageAction(new NextPageAction());
        view.setPrevPageAction(new PreviousPageAction());
        view.setSelectedRowListener(new OverviewSelectionListener());

        view.addWindowListener(new AppWindowListener());
        view.setRecordsPerPageListener(new RecordsPerPagePropertyChangeListener());
        
        // TODO: Comb the code here KR@TER
        view.setLoginAction(new LoginAction());
        
        // This is here in order to skip login procedure and connect to the database automatically
        // For developement purposes only - so that we don't have to go through login each time we run Plantlore 
        // view.initOverview();
    }
    
    /** Handles click to menu item Settings.
     *
     */
    class SettingsAction extends AbstractAction {
        public SettingsAction() {
            putValue(NAME, L10n.getString("Settings"));
            putValue(SHORT_DESCRIPTION, L10n.getString("SettingsTooltip"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Settings"));            
        }
        public void actionPerformed(ActionEvent actionEvent)
        {
            logger.info("Settings selected");
            //If the dialog is already constructed then use it. Otherwise construct it first.
            //if (settingsModel == null) {
                settingsModel = new Settings();
                settingsView = new SettingsView(settingsModel, view.getSBM());
                settingsCtrl = new SettingsCtrl(settingsModel, settingsView);
                settingsView.setVisible(true);
            /*} else {
                settingsView.setVisible(true);
            }*/
        }
    }
    
    class PrintAction extends AbstractAction {
        public PrintAction() {
            putValue(NAME, L10n.getString("Print"));
            putValue(SHORT_DESCRIPTION, L10n.getString("PrintTooltip"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Print"));                        
        }
        public void actionPerformed(ActionEvent actionEvent)
        {
            System.out.println("Print selected");
            DBLayer dbl = new HibernateDBLayer();
            try
            {
                dbl.initialize(null, null, null); // FIXME inicializace DB na dvou mistech?? zjistit proc
            } catch (DBLayerException ex)
            {
                System.out.println("Exception while initializing DBLayer: "+ex.getMessage());
                ex.printStackTrace();
            } catch(RemoteException e) {
            	System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
            }
            /*
            Query sq = new SelectQuery();
            try
            {
                sq.setType(DBMapping.PLANTRECORD);
                Result qr = dbl.executeQuery(sq);
                System.out.println("There are "+qr.getNumRows()+" plants in the db.");
                Plant p = (Plant) dbl.next(qr);
                do {
                    System.out.println("-----------------");
                System.out.println("Plant is "+p.getPublishableName());
                System.out.println("Czech name "+p.getCzechName());
                System.out.println("Abbrev. "+p.getAbbreviation());
                System.out.println("Adopted name "+p.getAdoptedName());
                System.out.println("Note "+p.getNote());
                p = (Plant) dbl.next(qr);
                } while (p!=null);
            } catch (DBLayerException ex)
            {
                System.out.println("Msg: "+ex.getMessage());
                ex.printStackTrace();
            }
            */
        }
    }
    
    
    /** Handles the exit command.
     *
     * Maybe settings should be stored first?
     */
    class ExitListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent)
        {
            model.savePreferences();
            System.exit(0);
        }
    }

    class HelpContentsAction extends AbstractAction {
        public HelpContentsAction() {
            putValue(NAME, L10n.getString("helpContents"));
            putValue(SHORT_DESCRIPTION, "Help contents");
            putValue(MNEMONIC_KEY, L10n.getMnemonic("helpContents"));
        }
        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Help contents selected");
        }
    }
        
    class HelpAboutAction extends AbstractAction {
        public HelpAboutAction() {
            putValue(NAME, L10n.getString("helpAbout"));
            putValue(SHORT_DESCRIPTION, "Help about tooltip");
            putValue(MNEMONIC_KEY, L10n.getMnemonic("helpAbout"));
        }
        public void actionPerformed(ActionEvent actionEvent)
        {
            System.out.println("Help about selected");
        }
    }
    
    class ImportAction extends AbstractAction {
        public ImportAction() {
            putValue(NAME, L10n.getString("dataImport"));
            putValue(SHORT_DESCRIPTION, L10n.getString("dataImportTooltip"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("dataImport"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Import pressed");
        }
    }
    
    class ExportAction extends AbstractAction {
        public ExportAction() {
            putValue(NAME, L10n.getString("dataExport"));
            putValue(SHORT_DESCRIPTION, L10n.getString("dataExportTooltip"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("dataExport"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Export pressed");
        }
    }

    class AddAction extends AbstractAction {
        public AddAction() {
            putValue(NAME, L10n.getString("recordAdd"));
            putValue(SHORT_DESCRIPTION, L10n.getString("recordAddTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("recordAdd"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            AddEditView addEdit = new AddEditView(view, true, model, false);
            addEdit.setTitle("Add a new occurrence");
            addEdit.setVisible(true);
        }
    }
    
    class EditAction extends AbstractAction {
        public EditAction() {
            putValue(NAME, L10n.getString("recordEdit"));
            putValue(SHORT_DESCRIPTION, L10n.getString("recordEditTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("recordEdit"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            AddEditView addEdit = new AddEditView(view, true, model, true);
            addEdit.setTitle("Edit occurrence");
            addEdit.setVisible(true);
        }
    }
    
    class DeleteAction extends AbstractAction {
        public DeleteAction() {
            putValue(NAME, L10n.getString("recordDelete"));
            putValue(SHORT_DESCRIPTION, L10n.getString("recordDeleteTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("recordDelete"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Delete pressed");
        }
    }

    class SelectAllAction extends AbstractAction {
        public SelectAllAction() {
            putValue(NAME, L10n.getString("selectAll"));
            putValue(SHORT_DESCRIPTION, L10n.getString("selectAllTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("selectAll"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            model.selectAll();
        }
    }

    class SelectNoneAction extends AbstractAction {
        public SelectNoneAction() {
            putValue(NAME, L10n.getString("selectNone"));
            putValue(SHORT_DESCRIPTION, L10n.getString("selectNoneTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("selectNone"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            model.selectNone();
        }
    }
    
    class InvertSelectedAction extends AbstractAction {
        public InvertSelectedAction() {
            putValue(NAME, L10n.getString("invertSelected"));
            putValue(SHORT_DESCRIPTION, L10n.getString("invertSelectedTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("invertSelected"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            model.invertSelected();
        }
    }

    class SearchAction extends AbstractAction {
        public SearchAction() {
            putValue(NAME, L10n.getString("dataSearch"));
            putValue(SHORT_DESCRIPTION, L10n.getString("dataSearchTooltip"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("dataSearch"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Search pressed");
        }
    }
    
    class PreviousPageAction extends AbstractAction {
        public PreviousPageAction() {
            putValue(NAME, L10n.getString("prevButton"));
            putValue(SHORT_DESCRIPTION, L10n.getString("prevButtonTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("prevButton"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            model.prevPage();
        }
    }
    
    class NextPageAction extends AbstractAction {
        public NextPageAction() {
            putValue(NAME, L10n.getString("nextButton"));
            putValue(SHORT_DESCRIPTION, L10n.getString("nextButtonTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("nextButton"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            model.nextPage();
        }
    }

    class AppWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e)
        {
            model.savePreferences();
        }
    }

    class DataAuthorsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            //try {
                AuthorManager authModel = new AuthorManager(model.getDatabase());
                AuthorManagerView authView = new AuthorManagerView(authModel, view);
                AuthorManagerCtrl authCtrl = new AuthorManagerCtrl(authModel, authView);
                //authModel.pokus();
                authView.show();                
            //} catch(RemoteException e) {
            //	System.err.println("Kdykoliv se pracuje s DBLayer nebo SelectQuery, musite hendlovat RemoteException");
            //}                
        }
    }    

    class DataPublicationsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            // Open publication manager - not implemented yet
        }
    }   
    
    class DataHistoryListener implements ActionListener {
    	public void actionPerformed(ActionEvent actionEvent)
        {
            System.out.println("Undo selected");
            //toto volani historie nebude v menu, ale jako tlacitko pro vybrany zaznam        
            //o vybranem zaznamu predame informace, ktere chceme o nem v historii zobrazit
            //jmeno rosliny, jmeno autora a lokaci a idOccurrences
            
            historyModel = new History(model.getDatabase(),"Adis Abeba", "Lada", "Praha východ", 1);
            historyView = new HistoryView(historyModel, view, true);
            historyCtrl = new HistoryCtrl(historyModel, historyView);
            historyView.setVisible(true);                         
        }
    }    
    
        class DataWholeHistoryListener implements ActionListener {
    	public void actionPerformed(ActionEvent actionEvent)
        {
            System.out.println("Whole history - Undo selected");
            
            wholeHistoryModel = new WholeHistory(model.getDatabase());
            wholeHistoryView = new WholeHistoryView(wholeHistoryModel, view, true);
            wholeHistoryCtrl = new WholeHistoryCtrl(wholeHistoryModel, wholeHistoryView);
            wholeHistoryView.setVisible(true); 
        }
    }    
    
    class RecordsPerPagePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            JFormattedTextField tf = (JFormattedTextField)e.getSource();
            if (e != null && e.getPropertyName().equals("value")) 
            {
                int i = ((Number)tf.getValue()).intValue(); 
                if (i < 1)
                {
                    Object obj = e.getOldValue();
                    if (obj != null)
                        tf.setValue(obj);
                    else // either multiple properties changed or there was no previous value - the value should better be at least 1 anyway...
                        tf.setValue(1);
                } else {
                    model.setRecordsPerPage(i);                    
                }
            }
        }  
    }
    
    class OverviewSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) return;
            
            ListSelectionModel lsm =
                    (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
                //no rows are selected
            } else {
                int selectedRow = lsm.getMinSelectionIndex();
                model.setSelectedRow(selectedRow);
                //selectedRow is selected
            }
        }
    }
    
    class LoginAction extends AbstractAction {
        public LoginAction() {
            putValue(NAME, L10n.getString("Login"));
            //putValue(SHORT_DESCRIPTION, L10n.getString("nextButtonTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Login"));                        
        }
        public void actionPerformed(ActionEvent arg0) {
                // Reuse the existing dialogs, hide'em when they're no longer needed.
                if(loginModel == null) {
                	loginModel = new Login(new RMIDBLayerFactory());
                	loginModel.addObserver(new DatabaseChange());
                }
                if(loginView == null) loginView = new LoginView(loginModel);
                if(loginCtrl == null) loginCtrl = new LoginCtrl(loginModel, loginView);
                
                loginCtrl.setVisible(true);
        }
    }
    
    // Update all information about the database layer and inform everyone who has to be informed 
    class DatabaseChange implements Observer {
    	public void update(Observable targer, Object parameter) {
    		if(parameter != null && parameter instanceof DBLayer) {
    			System.out.println("[!] DBLayer retrieval.");
    			DBLayer dblayer = loginModel.getDBLayer();
    			model.setDatabase(dblayer);
    			view.initOverview();
    		}
    	}
    }
    
    
}
