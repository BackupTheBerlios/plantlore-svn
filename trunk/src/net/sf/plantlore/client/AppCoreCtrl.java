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
import java.rmi.RemoteException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import net.sf.plantlore.server.DBLayerException;
import net.sf.plantlore.client.dblayer.FirebirdDBLayer;
import net.sf.plantlore.common.record.Plant;
import net.sf.plantlore.client.dblayer.result.QueryResult;
import net.sf.plantlore.client.dblayer.result.Result;
import net.sf.plantlore.client.dblayer.query.SelectQuery;
import net.sf.plantlore.client.dblayer.query.Query;
import net.sf.plantlore.client.authors.AuthorManager;
import net.sf.plantlore.client.authors.AuthorManagerCtrl;
import net.sf.plantlore.client.authors.AuthorManagerView;
import net.sf.plantlore.client.history.History;
import net.sf.plantlore.client.history.HistoryCtrl;
import net.sf.plantlore.client.history.HistoryView;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.server.HibernateDBLayer;

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
    AppCore model;
    AppCoreView view;
    Settings settingsModel;
    SettingsView settingsView;
    SettingsCtrl settingsCtrl;
    Preferences prefs;
    
    History historyModel;
    HistoryView historyView;
    HistoryCtrl historyCtrl;
    
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
        view.setSearchAction(new SearchAction());
        view.addDataAuthorsListener(new DataAuthorsListener());
        view.addDataPublicationsListener(new DataPublicationsListener());
        view.addDataHistoryListener(new DataHistoryListener());
        view.setAddAction(new AddAction());
        view.setEditAction(new EditAction());
        view.setDeleteAction(new DeleteAction());
        view.setSelectAllAction(new SelectAllAction());
        view.setSelectNoneAction(new SelectNoneAction());
        view.setInvertSelectedAction(new InvertSelectedAction());
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
            System.out.println("Add pressed");
        }
    }
    
    class EditAction extends AbstractAction {
        public EditAction() {
            putValue(NAME, L10n.getString("recordEdit"));
            putValue(SHORT_DESCRIPTION, L10n.getString("recordEditTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("recordEdit"));            
        } 

        public void actionPerformed(ActionEvent actionEvent) {
            System.out.println("Edit pressed");
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
    
    
    class AppWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e)
        {
            logger.info("Saving main window preferences.");
            
        }
    }

    class DataAuthorsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            AuthorManager authModel = new AuthorManager(model.getDatabase());
            AuthorManagerView authView = new AuthorManagerView(authModel, view.getFrame());
            AuthorManagerCtrl authCtrl = new AuthorManagerCtrl(authModel, authView);
            authView.show();
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
            historyView = new HistoryView(historyModel, view.getFrame());
            historyCtrl = new HistoryCtrl(historyModel, historyView);
            historyView.show();  
        }
    }    
}
