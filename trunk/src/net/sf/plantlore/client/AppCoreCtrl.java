/*
 * AppCoreCtrl.java
 *
 * Created on 14. leden 2006, 18:31
 *
 */

package net.sf.plantlore.client;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import net.sf.plantlore.client.history.History;
import net.sf.plantlore.client.history.WholeHistoryCtrl;
import net.sf.plantlore.client.history.WholeHistoryView;
import net.sf.plantlore.client.metadata.MetadataManager;
import net.sf.plantlore.client.metadata.MetadataManagerCtrl;
import net.sf.plantlore.client.metadata.MetadataManagerView;
import net.sf.plantlore.client.occurrenceimport.OccurrenceImportMng;
import net.sf.plantlore.client.occurrenceimport.OccurrenceImportMngCtrl;
import net.sf.plantlore.client.overview.*;
import net.sf.plantlore.client.overview.detail.Detail;
import net.sf.plantlore.client.overview.detail.DetailCtrl;
import net.sf.plantlore.client.overview.detail.DetailView;
import net.sf.plantlore.client.overview.search.Search;
import net.sf.plantlore.client.overview.search.SearchCtrl;
import net.sf.plantlore.client.overview.search.SearchView;
import net.sf.plantlore.client.overview.tree.HabitatTree;
import net.sf.plantlore.client.overview.tree.HabitatTreeCtrl;
import net.sf.plantlore.client.overview.tree.HabitatTreeView;
import net.sf.plantlore.client.overview.tree.NodeInfo;
import net.sf.plantlore.client.print.Print;
import net.sf.plantlore.client.print.PrintCtrl;
import net.sf.plantlore.client.print.PrintView;
import net.sf.plantlore.client.publications.PublicationManager;
import net.sf.plantlore.client.publications.PublicationManagerCtrl;
import net.sf.plantlore.client.publications.PublicationManagerView;
import net.sf.plantlore.client.resources.Resource;
import net.sf.plantlore.client.settings.Settings;
import net.sf.plantlore.client.settings.SettingsCtrl;
import net.sf.plantlore.client.settings.SettingsView;
import net.sf.plantlore.client.tableimport.TableImportMng;
import net.sf.plantlore.client.tableimport.TableImportMngCtrl;
import net.sf.plantlore.client.user.UserManager;
import net.sf.plantlore.client.user.UserManagerCtrl;
import net.sf.plantlore.client.user.UserManagerView;
import net.sf.plantlore.common.DefaultExceptionHandler;
import net.sf.plantlore.common.DefaultProgressBar;
import net.sf.plantlore.common.DefaultReconnectDialog;
import net.sf.plantlore.common.Pair;
import net.sf.plantlore.common.PlantloreConstants;
import net.sf.plantlore.common.ProgressBar;
import net.sf.plantlore.common.Selection;
import net.sf.plantlore.common.StandardAction;
import net.sf.plantlore.common.StatusBarManager;
import net.sf.plantlore.common.Task;
import net.sf.plantlore.common.exception.DBLayerException;
import net.sf.plantlore.client.authors.AuthorManager;
import net.sf.plantlore.client.authors.AuthorManagerCtrl;
import net.sf.plantlore.client.authors.AuthorManagerView;
import net.sf.plantlore.client.createdb.CreateDB;
import net.sf.plantlore.client.createdb.CreateDBCtrl;
import net.sf.plantlore.client.createdb.CreateDBView;
import net.sf.plantlore.client.export.ExportMng2;
import net.sf.plantlore.client.export.ExportMngCtrl2;
import net.sf.plantlore.client.history.HistoryCtrl;
import net.sf.plantlore.client.history.HistoryView;
import net.sf.plantlore.client.login.Login;
import net.sf.plantlore.client.login.LoginCtrl;
import net.sf.plantlore.client.login.LoginView;
import net.sf.plantlore.l10n.L10n;
import net.sf.plantlore.middleware.DBLayer;
import net.sf.plantlore.middleware.SelectQuery;
import net.sf.plantlore.middleware.RMIDBLayerFactory;

import org.apache.log4j.Logger;

/**
 * Application core controller.
 * 
 * Creates and sets listeners for components in <code>AppCoreView</code>.
 * 
 * @author Jakub
 */
public class AppCoreCtrl {
	Logger logger;

	Preferences prefs;

	private final static int MAX_RECORDS_PER_PAGE = 1000;

	private boolean showButtonText = true;

	// --------------SUPPLIED MODELS AND VIEWS-----------------
	AppCore model;

	AppCoreView view;

	// --------------MODELS AND VIEWS THIS CONTROLLER CREATES-----------------
	AddEdit addModel;

	AddEdit editModel;

	AddEditView addView;

	AddEditView editView;

	AddEditCtrl addCtrl;

	AddEditCtrl editCtrl;

	Search searchModel;

	SearchView searchView;

	SearchCtrl searchCtrl;

	Settings settingsModel;

	SettingsView settingsView;

	SettingsCtrl settingsCtrl;

	Print printModel;

	PrintView printView;

	PrintCtrl printCtrl;

	// History of one occurrence
	History historyModel;

	HistoryView historyView;

	HistoryCtrl historyCtrl;

	// History of "database"
	History wholeHistoryModel;

	WholeHistoryView wholeHistoryView;

	WholeHistoryCtrl wholeHistoryCtrl;

	// MetadataManager
	MetadataManager metadataManagerModel;

	MetadataManagerView metadataManagerView;

	MetadataManagerCtrl metadataManagerCtrl;

	// PublicationManager
	PublicationManager publicationManagerModel;

	PublicationManagerView publicationManagerView;

	PublicationManagerCtrl publicationManagerCtrl;

	// UserManager
	UserManager userManagerModel;

	UserManagerView userManagerView;

	UserManagerCtrl userManagerCtrl;

	// Login
	Login loginModel;

	LoginView loginView;

	LoginCtrl loginCtrl;

	// Export
	ExportMng2 exportModel;

	ExportMngCtrl2 exportCtrl;

	// Import
	OccurrenceImportMng importModel;

	OccurrenceImportMngCtrl importCtrl;


	// Immutable Table Import
	TableImportMng tableImportModel;

	TableImportMngCtrl tableImportCtrl;
	
	// Create new database
	CreateDB newDBModel;
	CreateDBView newDBView;
	CreateDBCtrl newDBCtrl;

	// Detail
	Detail detailModel;

	DetailView detailView;

	DetailCtrl detailCtrl;
        
        //HabitatTree
        HabitatTree habitatTreeModel;
        HabitatTreeView habitatTreeView;
        HabitatTreeCtrl habitatTreeCtrl;

	// Bridges
	ManagerBridge managerBridge = new ManagerBridge();
        HabitatTreeBridge habitatTreeBridge = new HabitatTreeBridge();

	// Actions
	AbstractAction settingsAction = new SettingsAction();

	AbstractAction printAction = new PrintAction();

	AbstractAction helpContentsAction = new HelpContentsAction();

	AbstractAction helpAboutAction = new HelpAboutAction();

	AbstractAction exportAction = new ExportAction2();

	AbstractAction importAction = new ImportAction();

	AbstractAction tableImportAction = new TableImportAction();

	AbstractAction dataAuthorsAction = new DataAuthorsAction();

	AbstractAction dataPublicationsAction = new DataPublicationsAction();

	AbstractAction dataMetadataAction = new DataMetadataAction();

	AbstractAction dataHistoryAction = new DataHistoryAction();

	AbstractAction dataWholeHistoryAction = new DataWholeHistoryAction();

	AbstractAction dataUserAction = new DataUserAction();

	AbstractAction historyAction = new DataHistoryAction();

	AbstractAction schedaAction = new SchedaAction();

	AbstractAction searchAction = new SearchAction();
        AbstractAction habitatTreeAction = new HabitatTreeAction();

	AbstractAction addAction = new AddAction();

	AbstractAction editAction = new EditAction();

	AbstractAction deleteAction = new DeleteAction();

	AbstractAction selectAllAction = new SelectAllAction();

	AbstractAction selectNoneAction = new SelectNoneAction();

	AbstractAction invertSelectedAction = new InvertSelectedAction();

	AbstractAction nextPageAction = new NextPageAction();

	AbstractAction prevPageAction = new PreviousPageAction();

	AbstractAction refreshAction = new RefreshAction();

	AbstractAction loginAction = new LoginAction();

	AbstractAction logoutAction = new LogoutAction();

	ReconnectAction reconnectAction = new ReconnectAction();
	
	AbstractAction createNewDatabaseAction = new CreateNewDatabaseAction();
	
	ActionListener silentFinalAction = new ExitListener();
        OverviewResizeListener overviewResizeListener = new OverviewResizeListener();

	/** Creates a new instance of AppCoreCtrl */
	public AppCoreCtrl(AppCore model, AppCoreView view) {
		logger = Logger.getLogger(this.getClass().getPackage().getName());
		prefs = Preferences.userNodeForPackage(this.getClass());

		this.model = model;
		this.view = view;
		view.setSettingsAction(settingsAction);
		view.setPrintAction(printAction);
		view.addExitAction(silentFinalAction);
		view.setHelpContentsAction(helpContentsAction);
		view.setHelpAboutAction(helpAboutAction);
		view.setExportAction(exportAction);
		view.setImportAction(importAction);
		view.setTableImportAction(tableImportAction);

		view.addDataAuthorsAction(dataAuthorsAction);
		view.addDataPublicationsAction(dataPublicationsAction);
		view.addDataMetadataAction(dataMetadataAction);
		view.addDataHistoryAction(dataHistoryAction);
		view.addDataWholeHistoryAction(dataWholeHistoryAction);
		view.addDataUserAction(dataUserAction);

                view.habitatTreeButton.setAction(habitatTreeAction);
                
		view.setSearchAction(searchAction);
		view.setAddAction(addAction);
		view.setEditAction(editAction);
		view.setDeleteAction(deleteAction);
		view.setSchedaAction(schedaAction);
		view.setHistoryRecordAction(historyAction);

		view.setSelectAllAction(selectAllAction);
		view.setSelectNoneAction(selectNoneAction);
		view.setInvertSelectedAction(invertSelectedAction);
		view.setNextPageAction(nextPageAction);
		view.setPrevPageAction(prevPageAction);
		view.setSelectedRowListener(new OverviewSelectionListener());
		view.overview.addMouseListener(new OverviewMouseListener());
		view.refreshButton.setAction(refreshAction);
		view.occurrencesRefresh.setAction(refreshAction);

		view.overview.addKeyListener(new OverviewKeyListener());

		view.addWindowListener(new AppWindowListener());
		view.setRecordsPerPageListener(new RecordsPerPagePropertyChangeListener());

		view.setLoginAction(loginAction);
                view.fileLogout.setAction(logoutAction);
                view.fileReconnect.setAction(reconnectAction);
                
		constructDialogs();

		setDatabaseDependentCommandsEnabled(false);
		
		DefaultReconnectDialog.setDefaultReconnectAction( reconnectAction );
	}

	private void constructDialogs() {
		// --- Add ---
		addModel = new AddEdit(model.getDatabase(), false);
                addModel.addObserver(managerBridge);
		addView = new AddEditView(view, true, addModel, false);
		addView.setTitle(L10n.getString("AddEdit.AddDialogTitle"));
		addCtrl = new AddEditCtrl(addModel, addView, false);

		// --- Edit ---
		editModel = new AddEdit(model.getDatabase(), true);
                editModel.addObserver(managerBridge);
		editView = new AddEditView(view, true, editModel, true);
		editView.setTitle(L10n.getString("AddEdit.EditDialogTitle"));
		editCtrl = new AddEditCtrl(editModel, editView, true);

		// --- Search ---
		searchModel = new Search(model.getDatabase());
		StatusBarManager sbm = view.getSBM();
		searchModel.setColumns(model.getTableModel().getColumns());
		searchView = new SearchView(view, true, searchModel);
		searchView.setTitle(L10n.getString("Search.DialogTitle"));
		searchCtrl = new SearchCtrl(searchModel, searchView);
		searchModel.addObserver(new SearchBridge());

		// --- Detail ---
		detailModel = new Detail(model);
		detailView = new DetailView(detailModel, view, true);
		detailCtrl = new DetailCtrl(detailModel, detailView);
                
                // --- HabitatTree ---
                habitatTreeModel = new HabitatTree();
                habitatTreeView = new HabitatTreeView(view,true,habitatTreeModel);
                habitatTreeCtrl = new HabitatTreeCtrl(habitatTreeModel, habitatTreeView);
                habitatTreeModel.addObserver(new HabitatTreeBridge());
	}

	private void setDatabaseDependentCommandsEnabled(boolean enabled) {
		settingsAction.setEnabled(enabled);
		printAction.setEnabled(enabled);
		exportAction.setEnabled(enabled);
		importAction.setEnabled(enabled);
		tableImportAction.setEnabled(enabled);

		dataAuthorsAction.setEnabled(enabled);
		dataPublicationsAction.setEnabled(enabled);
		dataMetadataAction.setEnabled(enabled);
		dataHistoryAction.setEnabled(enabled);
		dataWholeHistoryAction.setEnabled(enabled);
		dataUserAction.setEnabled(enabled);

		historyAction.setEnabled(enabled);
		schedaAction.setEnabled(enabled);
		searchAction.setEnabled(enabled);
                habitatTreeAction.setEnabled(enabled);
		addAction.setEnabled(enabled);
		editAction.setEnabled(enabled);
		deleteAction.setEnabled(enabled);

		selectAllAction.setEnabled(enabled);
		selectNoneAction.setEnabled(enabled);
		invertSelectedAction.setEnabled(enabled);
		nextPageAction.setEnabled(enabled);
		prevPageAction.setEnabled(enabled);
		view.recordsPerPage.setEnabled(enabled);
		refreshAction.setEnabled(enabled);

                logoutAction.setEnabled(enabled);
                reconnectAction.setEnabled(enabled);
                loginAction.setEnabled(!enabled);
                
		if (model.getAccessRights() != null)
			if (model.getAccessRights().getAdministrator() != 1) {
				dataMetadataAction.setEnabled(false);
				dataWholeHistoryAction.setEnabled(false);
				dataUserAction.setEnabled(false);
			}
	}

	/**
	 * Handles click to menu item Settings.
	 * 
	 */
	class SettingsAction extends AbstractAction {
		public SettingsAction() {
			putValue(NAME, L10n.getString("Settings"));
			putValue(SHORT_DESCRIPTION, L10n.getString("SettingsTooltip"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Settings"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			logger.info("Settings selected");
			// If the dialog is already constructed then use it. Otherwise
			// construct it first.
			if (settingsModel == null) {
				settingsModel = new Settings();
				settingsModel.setSelectedColumns(model.getTableModel()
						.getColumns());
				settingsModel.addObserver(new SettingsBridge());
				settingsView = new SettingsView(view, true, settingsModel);
				settingsCtrl = new SettingsCtrl(settingsModel, settingsView);
			}
			// settingsView.loadValues();
			settingsView.setVisible(true);
		}
	}

	/**
	 * Assumes that user doesn't work with the Search dialog at time of the
	 * update.
	 * 
	 */
	class SettingsBridge implements Observer {
		public void update(Observable o, Object arg) {
			if (arg instanceof String) {
				String s = (String) arg;
				if (s.equals("COLUMNS")) {
					logger
							.debug("User changed columns in settings. Propagating the change to overview and config.");
					ArrayList<Column> columns = settingsModel
							.getSelectedColumns();
					model.getTableSorter().setColumns(columns);
					model.getMainConfig().setColumns(columns);

					searchModel.setColumns(columns);
					searchModel.clear();
					searchModel.constructQuery();
				}
				if (s.equals("DYNAMIC_PAGE_LOADING")) {
					model.dynamicPageLoading = prefs.getBoolean(
							PlantloreConstants.PREF_DYNAMIC_PAGE_SIZE, false);
				}
			}
		}
	}

	class PrintAction extends AbstractAction {
		public PrintAction() {
			putValue(NAME, L10n.getString("Print"));
			putValue(SHORT_DESCRIPTION, L10n.getString("PrintTooltip"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Print"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if (printModel == null) {
				printModel = new Print();
				printView = new PrintView(view, true, printModel);
				printCtrl = new PrintCtrl(printModel, printView);
			}
			Selection sel = model.getTableModel().getSelection();
			if (sel.values().size() < 1) {
				JOptionPane.showMessageDialog(view,
						"Please check at least one record.");
				return;
			}
			printModel.setSource(model.getDatabase(), sel);
			printView.setVisible(true);
		}
	}

	/**
	 * Handles the exit command.
	 * 
	 * Maybe settings should be stored first?
	 */
	class ExitListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			try {
				model.savePreferences();
			} catch (IOException ex) {
                            //FIXME - aspon lokalizovat
				JOptionPane.showMessageDialog(view,
						"Problem while saving configuration: "
								+ ex.getMessage());
			}

                        model.logout();
                                
			// The database layer created by a DBLayerFactory MUST be
			// destroyed by that factory. There is a method which will do the
			// trick.
			if (loginModel != null)
				loginModel.logout();

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

		public void actionPerformed(ActionEvent actionEvent) {
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
			if (importModel == null) {
					importModel = new OccurrenceImportMng(model.getDatabase(), managerBridge);
					importCtrl = new OccurrenceImportMngCtrl(importModel, view);
			}
			importCtrl.setVisible(true);
		}
	}

	class TableImportAction extends AbstractAction {
		public TableImportAction() {
			putValue(NAME, L10n.getString("Overview.TableImport"));
			putValue(SHORT_DESCRIPTION, L10n
					.getString("Overview.TableImportTT"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.TableImport"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if (tableImportModel == null) {
					tableImportModel = new TableImportMng(model.getDatabase(), managerBridge);
					tableImportCtrl = new TableImportMngCtrl(tableImportModel, view);
			}
			tableImportCtrl.setVisible(true);
		}
	}



	class ExportAction2 extends AbstractAction {

		public ExportAction2() {
			putValue(NAME, L10n.getString("dataExport"));
			putValue(SHORT_DESCRIPTION, L10n.getString("dataExportTooltip"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("dataExport"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			// Create a new dialog if it already doesn't exist.
			if (exportModel == null) {
				exportModel = new ExportMng2(model.getDatabase());
				exportCtrl = new ExportMngCtrl2(exportModel, view);
			}
			try {
				SelectQuery query;
				Object[] queryParam = searchModel.constructExportQuery();
				query = (SelectQuery) queryParam[0];
				if ((Boolean) queryParam[1]) { // use projections
					exportModel.useProjections(true);
					exportModel.setRootTable((Class) queryParam[2]);
				}
				
				exportModel.setSelectQuery(query);
				exportModel.setSelection(model.getTableModel().getSelection());
			} catch (DBLayerException e) {
				// TODO: Some errors may lead to ReconnectDialog.show()!
				JOptionPane.showMessageDialog(view, "DBLayer Exception: "
						+ e.getMessage());
				return;
			} catch (RemoteException e) {
				DefaultReconnectDialog.show(view, e);
				return;
			}

			exportCtrl.setVisible(true);
		}
	}

	class AddAction extends AbstractAction {
		public AddAction() {
			if (showButtonText)
				putValue(NAME, L10n.getString("Overview.Add"));
			putValue(SMALL_ICON, Resource
					.createIcon("/toolbarButtonGraphics/general/Add24.gif"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.AddTT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A,
					ActionEvent.CTRL_MASK));
			// putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.Add"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			try {
				if (model.getDatabase().getUserRights().getAdd() != 1) {
					JOptionPane.showMessageDialog(view, L10n
							.getString("AddEdit.InsufficientAddRights"), L10n
							.getString("AddEdit.InsufficientRightsTitle"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			} catch (RemoteException ex) {
				JOptionPane.showMessageDialog(view, L10n
						.getString("Error.RemoteException")
						+ "\n" + ex.getMessage(), L10n
						.getString("Error.RemoteExceptionTitle"),
						JOptionPane.WARNING_MESSAGE);
				logger.error(ex);
			}
			addModel.clear();
			addView.setVisible(true);
		}
	}

        /** Action for handling edit commands.
         *
         * Determines the selected occurrence in overview and then invokes edit dialog
         * on that occurrence.
         */
	class EditAction extends AbstractAction {
		public EditAction() {
			if (showButtonText)
				putValue(NAME, L10n.getString("Overview.Edit"));
			putValue(SMALL_ICON, Resource
					.createIcon("/toolbarButtonGraphics/general/Edit24.gif"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.EditTT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,
					ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent actionEvent) {                    
                    if (model.getResultsCount() == 0) {
                        JOptionPane.showMessageDialog(view,L10n.getString("Overview.NothingToEdit"),L10n.getString("Overview.NothingToEditTitle"),JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                        
                    final Object[] row = model.getSelectedRow();

                    try {
                        if (!model.isEditAllowed(model.getSelectedOccurrence())) {
                                JOptionPane.showMessageDialog(view, L10n
                                                .getString("AddEdit.InsufficientEditRights"), L10n
                                                .getString("AddEdit.InsufficientRightsTitle"),
                                                JOptionPane.INFORMATION_MESSAGE);
                                return;
                        }
                    } catch(RemoteException ex) {
                        DefaultExceptionHandler.handle(view,ex);  
                        return;
                    } catch (DBLayerException ex) {
                        DefaultExceptionHandler.handle(view,ex);                            
                        return;
                    }

                    Task t = new Task() {
                        public Object task() throws DBLayerException, RemoteException {
                            setStatusMessage(L10n.getString("Overview.LoadingOccurrenceRecord"));
                            return editModel.loadRecord((Integer) row[row.length - 1]);
                        }
                    };

                    new DefaultProgressBar(t, view, true) {
                        public void afterStopping() {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    editView.loadComponentData();
                                    editView.setVisible(true);    
                                }
                            });
                        }
                    };
                    t.start();
		}//actionPerformed()
	}//EditAction

	class DeleteAction extends AbstractAction {
		public DeleteAction() {
			if (showButtonText)
				putValue(NAME, L10n.getString("Overview.Delete"));
			putValue(SMALL_ICON, Resource
					.createIcon("/toolbarButtonGraphics/general/Delete24.gif"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.DeleteTT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_DELETE, 0));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			Selection selection = model.getTableModel().getSelection();
			Object[] arg = { selection.values().size() };

                        //Nothing checked in the overview -> tell user to check something first
			if (arg[0].equals(0)) {
                            JOptionPane.showMessageDialog(view, 
                                    L10n.getString("Message.CheckAnOccurrence"), 
                                    L10n.getString("Message.CheckAnOccurrenceTitle"),
                                    JOptionPane.INFORMATION_MESSAGE);
                            return;
			}

			try {
                            for (Integer occId : selection.values())
                                    if (!model.isEditAllowed(occId)) {
                                            JOptionPane.showMessageDialog(view, 
                                                    L10n.getString("Delete.InsufficientRights"), 
                                                    L10n.getString("Delete.InsufficientRightsTitle"),
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                    }
			} catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view, ex);
                            return;
			} catch (DBLayerException ex) {
                            DefaultExceptionHandler.handle(view, ex);
                            return;
			}

                        int choice = JOptionPane.showConfirmDialog(view, L10n
                                .getFormattedString("Message.DeleteRecords", arg), L10n
                                .getString("Message.DeleteRecordsTitle"),
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        
                        switch (choice) {
                            case JOptionPane.CANCEL_OPTION:
                                return;
                            case JOptionPane.OK_OPTION:
                                logger.info("Deleting " + arg[0] + " records.");
                                
                                Task task = model.deleteSelected();
                                
                                ProgressBar progressBar = new DefaultProgressBar(task, view, true) {                                    
                                    public void afterStopped(Object value) {
                                        refreshOverview(false); // false -> do not create task,
                                        // refresh the overview directly
                                        // in this thread
                                    }
                                };                                
                                task.start();
                                break;
                        }// switch
		}//actionPerformed
	}//DeleteAction

        /** Overview select all action.
         * Selects all occurrences on current page.
         */
	class SelectAllAction extends AbstractAction {
		public SelectAllAction() {
			putValue(NAME, L10n.getString("Overview.SelectAll"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.SelectAllTT"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if (model.getResultsCount() > 0)
				model.selectAll();
		}
	}

        /** Overview select none action.
         * Unselects all occurrences on current page.
         */
	class SelectNoneAction extends AbstractAction {
		public SelectNoneAction() {
			putValue(NAME, L10n.getString("Overview.SelectNone"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.SelectNoneTT"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if (model.getResultsCount() > 0)
				model.selectNone();
		}
	}

        /** Overview invert selected action.
         * Inverts the selection of occurrences on current page.
         */
	class InvertSelectedAction extends AbstractAction {
		public InvertSelectedAction() {
			putValue(NAME, L10n.getString("Overview.InvertSelected"));
			putValue(SHORT_DESCRIPTION, L10n
					.getString("Overview.InvertSelectedTT"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if (model.getResultsCount() > 0)
				model.invertSelected();
		}
	}

        /** Handles the overview's Search action.
         */
	class SearchAction extends AbstractAction {
		public SearchAction() {
			if (showButtonText)
				putValue(NAME, L10n.getString("Overview.Search"));
			putValue(SMALL_ICON, Resource.createIcon("/toolbarButtonGraphics/general/Search24.gif"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.SearchTT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			searchModel.clear();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                searchView.setVisible(true);
                            }
                        });
		}//actionPerformed
	}//SearchAction

        /** Handles the invoking of habitat tree from overview.
         *
         */
	class HabitatTreeAction extends AbstractAction {
		public HabitatTreeAction() {
			if (showButtonText)
				putValue(NAME, L10n.getString("Overview.HabitatTree"));
			putValue(SMALL_ICON, Resource.createIcon("/toolbarButtonGraphics/development/Application24.gif"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.HabitatTreeTT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T,ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent actionEvent) {
                        habitatTreeModel.setDBLayer(model.getDatabase());
                        try {
                            habitatTreeModel.loadData();
                        } catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
                        } catch (DBLayerException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                habitatTreeView.setVisible(true);
                            }
                        });
		}//actionPerformed
	}// HabitatTreeAction

        /** Bridge that informs overview about a new query.
         *
         */
	class SearchBridge implements Observer {
		public void update(Observable o, Object arg) {
                    if (arg != null && arg instanceof Integer) {
                        logger.debug("Fetching new result id from Search model. Storing it to AppCore model.");
                        try {
                            model.setResultId(searchModel.getNewResultId(), searchModel.getNewSelectQuery());
                        } catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
                        } catch (DBLayerException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
                        }
                    }
		}//update()
	}//SearchBridge
        
        /** Bridge from HabitatTree to overview, search and add.
         *
         */
        class HabitatTreeBridge implements Observer {
            public void update(Observable o, Object arg) {
                if (arg != null && arg instanceof Pair) {                    
                    Pair<String,NodeInfo> pair = (Pair<String,NodeInfo>) arg;
                    String message = pair.getFirst();
                    NodeInfo nodeInfo = pair.getSecond();
                    
                    if (message.equals("SEARCH")) {
                        switch (nodeInfo.getType()) {
                            case HABITAT:
                                searchModel.clear();
                                searchModel.setHabitatId(nodeInfo.getId());
                                searchModel.constructQuery();
                        }//switch
                    }//if search
                    
                    if (message.equals("ADD")) {
			try {
				if (model.getDatabase().getUserRights().getAdd() != 1) {
					JOptionPane.showMessageDialog(view, 
                                                L10n.getString("AddEdit.InsufficientAddRights"), 
                                                L10n.getString("AddEdit.InsufficientRightsTitle"),
                                                JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			} catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view, ex);
                            return;
			}
			addModel.clear();
                        try {
                            addModel.setHabitat(nodeInfo.getId());
                        } catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
                        } catch (DBLayerException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
                        }
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                addView.loadComponentData();
                                addView.setVisible(true);   
                            }
                        });//invokeLater
                    }//if ADD
                }//if
            }//update
        }//class HabitatTreeBridge

        /** Handles the overview scheda action.
         *
         */
	class SchedaAction extends AbstractAction {
		public SchedaAction() {
			putValue(NAME, L10n.getString("Overview.Scheda"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.SchedaTT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X,	ActionEvent.CTRL_MASK));
			putValue(SMALL_ICON,Resource.createIcon("/toolbarButtonGraphics/general/ComposeMail24.gif"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			try {
				if (model.getTableModel().getSelection().values().size() < 1) {
					JOptionPane.showMessageDialog(view, 
                                                L10n.getString("Message.CheckAnOccurrence"), 
                                                L10n.getString("Message.CheckAnOccurrenceTitle"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				final JasperReport schedaReport;
				InputStream schedaIs = this.getClass().getClassLoader().getResourceAsStream(
								"net/sf/plantlore/client/resources/SchedaA6.jasper");

				try {
					ObjectInputStream schedaOis = new ObjectInputStream(
							schedaIs);
					schedaReport = (JasperReport) schedaOis.readObject();
				} catch (FileNotFoundException ex) {
					logger.error("Problem loading jasper report resource: "
							+ ex);
					JOptionPane.showMessageDialog(view, L10n
							.getString("Error.InternalProblem")
							+ "\n" + ex.getMessage(), L10n
							.getString("Error.InternalProblemTitle"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				} catch (IOException ex) {
					logger.error("Problem loading jasper report resource: "
							+ ex);
					JOptionPane.showMessageDialog(view, L10n
							.getString("Error.InternalProblem")
							+ ex.getMessage(), L10n
							.getString("Error.InternalProblemTitle"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				} catch (ClassNotFoundException ex) {
					logger.error("Problem loading jasper report resource: "
							+ ex);
					JOptionPane.showMessageDialog(view, L10n
							.getString("Error.InternalProblem")
							+ ex.getMessage(), L10n
							.getString("Error.InternalProblemTitle"),
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				prefs = Preferences.userNodeForPackage(AppCoreCtrl.class);
				String h1 = prefs.get("HEADER_ONE",
						"Set the first header in settings, please.");
				String h2 = prefs.get("HEADER_TWO",
						"Set the second header in settings, please.");
				final HashMap params = new HashMap();
				params.put("HEADER_ONE", h1);
				params.put("HEADER_TWO", h2);

				Task task = new Task() {
					JasperPrint jasperPrint;

					public Object task() throws JRException {
						jasperPrint = JasperFillManager.fillReport(
								schedaReport, params,
								new JasperDataSource(model.getDatabase(), model
										.getTableModel().getSelection()));
						fireStopped(jasperPrint);
						return jasperPrint;
					}
				};

				ProgressBar pb = new ProgressBar(task, view, true) {
					public void exceptionHandler(final Exception ex) {
						logger.error("Error while filling jasper report in SchedaAction: "+ ex);
						ex.printStackTrace();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								JOptionPane.showMessageDialog(view.getParent(),
											L10n.getString("Print.Message.BrokenReport")
														+ "\n"
														+ ex.getMessage(),
											L10n.getString("Print.Message.BrokenReport"),
												JOptionPane.WARNING_MESSAGE);
							}
						});
						getTask().stop();
					}

					public void afterStopped(final Object value) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								new SchedaView(view, true, (JasperPrint) value)
										.setVisible(true);
							}
						});
					}
				};
				task.start();
			} catch (Exception ex) { 
				logger.error("Broken report: " + ex);
				JOptionPane.showMessageDialog(view, L10n
						.getString("Print.Message.BrokenReport")
						+ "\n" + ex.getMessage(), L10n
						.getString("Print.Message.BrokenReport"),
						JOptionPane.WARNING_MESSAGE);
				ex.printStackTrace();
			}
		}
	}

        /** Handles the previous page overview action */
	class PreviousPageAction extends AbstractAction {
		public PreviousPageAction() {
			putValue(NAME, L10n.getString("Overview.PrevPage"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.PrevPageTT"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			try {
				model.prevPage();
			} catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view, ex);
                            return;
			} catch (DBLayerException ex) {
                            DefaultExceptionHandler.handle(view, ex);
                            return;
			}
		}
	}

        /** Handles the next page overview action */        
	class NextPageAction extends AbstractAction {
		public NextPageAction() {
			putValue(NAME, L10n.getString("Overview.NextPage"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.NextPageTT"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			try {
				model.nextPage();
			} catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view, ex);
                            return;
			} catch (DBLayerException ex) {
                            DefaultExceptionHandler.handle(view, ex);
                            return;
			}
		}
	}

        /** Window closing listener.
         * Saves preferences on that event.
         */
	class AppWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			try {
				if(loginModel != null)
					loginModel.logout();
				
				model.savePreferences();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(view,L10n.getString("Overview.ProblemSavingPreferences")+": "
								+ ex.getMessage());
			}
		}
	}

        /** Handles author manager action invoked from overview. */
	class DataAuthorsAction extends AbstractAction {
		public DataAuthorsAction() {
			putValue(NAME, L10n.getString("Overview.MenuDataAuthors"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.MenuDataAuthorsTT"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.MenuDataAuthors"));
		}

		public void actionPerformed(ActionEvent e) {
			AuthorManager authModel = new AuthorManager(model.getDatabase());
			final AuthorManagerView authView = new AuthorManagerView(authModel, view,
					true);
			AuthorManagerCtrl authCtrl = new AuthorManagerCtrl(authModel,
					authView);
			authModel.addObserver(managerBridge);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                authView.setVisible(true);
                            }
                        });
		}

	}

        /** Handles publication manager action invoked from overview. */
	class DataPublicationsAction extends AbstractAction {
		public DataPublicationsAction() {
			putValue(NAME, L10n.getString("Overview.MenuDataPublications"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.MenuDataPublicationsTT"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.MenuDataPublications"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			publicationManagerModel = new PublicationManager(model
					.getDatabase());
			publicationManagerView = new PublicationManagerView(
					publicationManagerModel, view, true);
			publicationManagerCtrl = new PublicationManagerCtrl(
					publicationManagerModel, publicationManagerView);
			publicationManagerModel.addObserver(managerBridge);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                publicationManagerView.setVisible(true);
                            }
                        });
		}
	}

        /** Handles user manager action invoked from overview. */        
	class DataUserAction extends AbstractAction {
		public DataUserAction() {
			putValue(NAME, L10n.getString("userManager"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			logger.info("Starting UserManager");

			if (userManagerModel == null) {
				userManagerModel = new UserManager(model.getDatabase());
				userManagerView = new UserManagerView(userManagerModel, view, true);
				userManagerCtrl = new UserManagerCtrl(userManagerModel,userManagerView);
			}
			Task task = userManagerModel.searchUser(true);
			new DefaultProgressBar(task, view, true) {
				@Override
	 	    	public void afterStopping() {
					if (! userManagerModel.isFinishedTask()) return;
					userManagerModel.setInfoFinishedTask(false);
					userManagerCtrl.reloadData(1, UserManager.DEFAULT_DISPLAY_ROWS);
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                userManagerView.setVisible(true);	
                                            }
                                        });
	 	    	}
			};			
			task.start();					
		}
	}
        
        /** Handles history action invoked from overview. */
	class DataHistoryAction extends AbstractAction {
		public DataHistoryAction() {
			if (showButtonText)
				putValue(NAME, L10n.getString("Overview.History"));
			putValue(SMALL_ICON, Resource
					.createIcon("/toolbarButtonGraphics/general/History24.gif"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.HistoryTT"));
		}

		public void actionPerformed(ActionEvent e) {
			logger.debug("Showing history of record: "+ model.getSelectedOccurrence());	
			
                        if (model.getResultsCount() == 0) {
                            JOptionPane.showMessageDialog(view,L10n.getString("Overview.NoOccurrence"),L10n.getString("Overview.NoOccurrenceTitle"),JOptionPane.INFORMATION_MESSAGE);                            
                            return;
                        }
                        
			if (historyModel == null) {
				historyModel = new History(model.getDatabase(), model.getSelectedOccurrence());								
				historyView = new HistoryView(historyModel, view, true);
				historyCtrl = new HistoryCtrl(historyModel, historyView);
				historyModel.addObserver(managerBridge);
			}
			Task task = historyModel.initialize(model.getSelectedOccurrence());
			new DefaultProgressBar(task, view, true) {
				@Override
	 	    	public void afterStopping() {
					if (! historyModel.isFinishedTask()) return;
					historyModel.setInfoFinishedTask(false);
                                            historyView.setVisible(true);		
	 	    	}				
			};			
			task.start();							
		}
	}

        /** Handles the whole history action invoked from overview. */
	class DataWholeHistoryAction extends AbstractAction {
		public DataWholeHistoryAction() {
			putValue(NAME, L10n.getString("wholeHistory"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			logger.info("Starting Whole history");

			if (wholeHistoryModel == null) {
				wholeHistoryModel = new History(model.getDatabase());				
                                wholeHistoryView = new WholeHistoryView(wholeHistoryModel, view,true);
				wholeHistoryCtrl = new WholeHistoryCtrl(wholeHistoryModel, wholeHistoryView);
				wholeHistoryModel.addObserver(managerBridge);
			}
			Task task = wholeHistoryModel.initializeWH();
			new DefaultProgressBar(task, view, true) {
				@Override
	 	    	public void afterStopping() {	
					if (! wholeHistoryModel.isFinishedTask()) return;
					wholeHistoryModel.setInfoFinishedTask(false);
                                        wholeHistoryView.setVisible(true);
	 	    	}
			};			
			task.start();							
		}
	}
	
        /** Handles metadata manager action invoked from overview. */
	class DataMetadataAction extends AbstractAction {
		public DataMetadataAction() {
			putValue(NAME, L10n.getString("metadataManager"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M,
					ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			System.out.println("Metadata Manager");

			if (metadataManagerModel == null) {
				metadataManagerModel = new MetadataManager(model.getDatabase());								
				metadataManagerView = new MetadataManagerView(metadataManagerModel,	view, true);
				metadataManagerCtrl = new MetadataManagerCtrl(metadataManagerModel,metadataManagerView);
				metadataManagerModel.addObserver(managerBridge);								
			}
			Task task = metadataManagerModel.searchMetadata(true);
			new DefaultProgressBar(task, view, true) {
				@Override
	 	    	public void afterStopping() {	
					if (! metadataManagerModel.isFinishedTask()) return;
					metadataManagerModel.setInfoFinishedTask(false);
					metadataManagerCtrl.reloadData(1, MetadataManager.DEFAULT_DISPLAY_ROWS);
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                metadataManagerView.setVisible(true);
                                            }
                                        });
	 	    	}
			};			
			task.start();						
		}
	}

        /** Handles changes of the page size invoked from overview. */
	class RecordsPerPagePropertyChangeListener implements
			PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			JFormattedTextField tf = (JFormattedTextField) e.getSource();
			if (e != null && e.getPropertyName().equals("value")) {
				int i = ((Number) tf.getValue()).intValue();
				if (i > MAX_RECORDS_PER_PAGE) {
					JOptionPane.showMessageDialog(view, L10n
							.getString("Overview.Warning.MaxRecordsPerPage")
							+ " " + MAX_RECORDS_PER_PAGE);
					Object obj = e.getOldValue();                                        
					if (obj != null) 
						tf.setValue(obj);
					else
						// either multiple properties changed or there was no
						// previous value - the value should better be at least
						// 1 anyway...
						tf.setValue(prefs.getInt("recordsPerPage", 30));
					return;
				}

				if (i < 1) {
					Object obj = e.getOldValue();
					if (obj != null)
						tf.setValue(obj);
					else
						// either multiple properties changed or there was no
						// previous value - the value should better be at least
						// 1 anyway...
						tf.setValue(1);
				} else {
					try {
						model.setRecordsPerPage(i);
					} catch (RemoteException ex) {
                                            DefaultExceptionHandler.handle(view, ex);
                                            return;
					} catch (DBLayerException ex) {
                                            DefaultExceptionHandler.handle(view, ex);
                                            return;
					}
				}
			}
		}
	}

        /** Keeps track of selected row in overview. */
	class OverviewSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			// Ignore extra messages.
			if (e.getValueIsAdjusting())
				return;

			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			if (lsm.isSelectionEmpty()) {
				// no rows are selected
			} else {
				int selectedRow = lsm.getMinSelectionIndex();
				model.setSelectedRow(selectedRow);
				// selectedRow is selected
			}
		}
	}

        /** Handles double clicks in overview to invoke the Detail. */
	class OverviewMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= 2)
				try {
					int resultNumber = model.getSelectedResultNumber();
                                        if (resultNumber > model.getResultsCount()) {
                                            logger.error("Trying to show detail for a record number of which is bigger than results count. Have we been disconnected?");
                                            JOptionPane.showMessageDialog(view,L10n.getString("Error.LostConnection"));
                                            return;
                                        }
                                        
					if (resultNumber != model.getResultsCount())
						model.selectAndShow(resultNumber);
					detailModel.load(model.getSelectedResultNumber());
                                        SwingUtilities.invokeLater( new Runnable() {
                                            public void run() {
                                                detailView.setVisible(true);
                                            }
                                        });
				} catch (RemoteException ex) {
                                    DefaultExceptionHandler.handle(view,ex);
                                    return;
				} catch (DBLayerException ex) {
                                    DefaultExceptionHandler.handle(view,ex);
                                    return;
				}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

	}

        /** Listnes to the enter key pressed in overview and invokes Detail. */
	class OverviewKeyListener implements KeyListener {
		public void keyTyped(KeyEvent e) {
			if (e.getKeyText(e.getKeyChar()).equals("Space"))
				model.invertSelectedOnCurrentRow();
			if (e.getKeyText(e.getKeyChar()).equals("Enter")) {
				try {
					int resultNumber = model.getSelectedResultNumber();
                                        if (resultNumber > model.getResultsCount()) {
                                            logger.error("Trying to show detail for a record number of which is bigger than results count. Have we been disconnected?");
                                            JOptionPane.showMessageDialog(view,"The connection has been probably lost.");
                                            return;
                                        }
                                        
					if (resultNumber != model.getResultsCount())
						model.selectAndShow(resultNumber - 1);// After Enter
					// the
					// hyperactive
					// JTable moves
					// selection to
					// next row, so
					// we need to
					// correct that
					detailModel.load(model.getSelectedResultNumber());
                                        SwingUtilities.invokeLater( new Runnable() {
                                            public void run() {
                                                detailView.setVisible(true);
                                            }
                                        });
				} catch (RemoteException ex) {
                                    DefaultExceptionHandler.handle(view,ex);
                                    return;
				} catch (DBLayerException ex) {
                                    DefaultExceptionHandler.handle(view,ex);
                                    return;
				}
			}
		}

		public void keyPressed(KeyEvent e) {

		}

		public void keyReleased(KeyEvent e) {
		}

	}

        /** Handles the login action. */
	class LoginAction extends AbstractAction {
		public LoginAction() {
			putValue(NAME, L10n.getString("Overview.MenuFileLogin"));
			putValue(SHORT_DESCRIPTION, L10n
					.getString("Overview.MenuFileLoginTT"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.MenuFileLogin"));
		}

		public void actionPerformed(ActionEvent arg0) {
			// Reuse the existing dialogs, hide'em when they're no longer
			// needed.
			if (loginModel == null) {
				loginModel = new Login(
						new RMIDBLayerFactory( model.getMainConfig().getCodebase() ), 
						model.getMainConfig());
				loginModel.addObserver(new DatabaseChange()); // Callback ~ redistribution of DBLayer.
				loginView = new LoginView(view, loginModel);
				loginCtrl = new LoginCtrl(loginModel, loginView);
			}
			loginCtrl.setVisible(true);
		}
	}
	
	
	class CreateNewDatabaseAction extends StandardAction {
		public CreateNewDatabaseAction() {
			super("Overview.MenuFileCreateDB");
		}
		public void actionPerformed(ActionEvent arg0) {
			if( newDBModel == null ) {
				newDBModel = new CreateDB(model.getMainConfig());
				newDBView = new CreateDBView(view, newDBModel);
				newDBCtrl = new CreateDBCtrl(newDBModel, newDBView);
			}
                        view.setVisible(true);
		}
	}

	class LogoutAction extends AbstractAction {

		public LogoutAction() {
			putValue(NAME, L10n.getString("Overview.MenuFileLogout"));
			putValue(SHORT_DESCRIPTION, L10n
					.getString("Overview.MenuFileLogoutTT"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.MenuFileLogout"));
		}

		public void actionPerformed(ActionEvent arg0) {
                        //must be done to prevent problems when this listener would send commands to load data for a query
                        //that doesn't exist, or when the dblayer doesn't exist..
                        //it's again installed in the DatabaseChange bridge
                        view.overviewScrollPane.removeComponentListener(overviewResizeListener);

                        model.logout();
                    
			if (loginModel != null)
				loginModel.logout();

			/*
			 * Do some more work here (switch the enabled/disabled menu items,
			 * etc.)
			 */

                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                setDatabaseDependentCommandsEnabled(false);
                            }
                        });
		}

	}

	
	/** Handles the reconnect action invoked from overview. */
	public class ReconnectAction extends AbstractAction {

		private Component parent = view;

		public ReconnectAction() {
			putValue(NAME, L10n.getString("Overview.MenuFileReconnect"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.MenuFileReconnectTT"));
			putValue(MNEMONIC_KEY, L10n.getMnemonic("Overview.MenuFileReconnect"));
		}

		public ReconnectAction(Component parent) {
			this();
			this.parent = parent;
		}

		public void setParent(Component parent) {
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent arg0) {
			if (loginModel != null) {
				Task t = loginModel.getLastConnectionTask();
				if(t != null) {
					// 1. Log out ( ~ dispose of the current DBLayer)
					logger.debug("Logging out...");
					loginModel.logout();
					// 2. Log in again ( ~ create a new DBLayer)
					// Note that the ProgressBar is no longer needed - 
					// the last task surely had one and it will become
					// active once we start the task again.
					logger.debug("Performing the connection procedure again...");
					t.start(); 
				}
			}
		}
	}
	

	/** Bridge from login / reconnect(?) to the rest of the application.
         * Updates all information about the database layer and informs everyone who
	 * has to be informed.
         */
	class DatabaseChange implements Observer {
		/**
		 * Fetches combobox items from AppCore and stores them to dialog models.
		 * 
		 */
		private void fillDialogModels() {
			logger.debug("Filling Add model.");
			addModel.setAuthors(model.getAuthors());
			addModel.setAuthorRoles(model.getAuthorRoles());
			addModel.setPlants(model.getPlants());
			addModel.setVillages(model.getVillages());
			addModel.setPhytNames(model.getPhytNames());
			addModel.setPhytCodes(model.getPhytCodes());
			addModel.setCountries(model.getCountries());
			addModel.setSources(model.getSources());
			addModel.setPublications(model.getPublications());
			addModel.setProjects(model.getProjects());
			addModel.setTerritories(model.getTerritories());

			logger.debug("Filling Edit model.");
			editModel.setAuthors(model.getAuthors());
			editModel.setAuthorRoles(model.getAuthorRoles());
			editModel.setPlants(model.getPlants());
			editModel.setVillages(model.getVillages());
			editModel.setPhytNames(model.getPhytNames());
			editModel.setPhytCodes(model.getPhytCodes());
			editModel.setCountries(model.getCountries());
			editModel.setSources(model.getSources());
			editModel.setPublications(model.getPublications());
			editModel.setProjects(model.getProjects());
			editModel.setTerritories(model.getTerritories());

			logger.debug("Filling Search model.");
			searchModel.setAuthors(model.getAuthors());
			searchModel.setAuthorRoles(model.getAuthorRoles());
			searchModel.setPlants(model.getPlants());
			searchModel.setVillages(model.getVillages());
			searchModel.setPhytNames(model.getPhytNames());
			searchModel.setPhytCodes(model.getPhytCodes());
			searchModel.setCountries(model.getCountries());
			searchModel.setSources(model.getSources());
			searchModel.setPublications(model.getPublications());
			searchModel.setProjects(model.getProjects());
			searchModel.setTerritories(model.getTerritories());
		}

		public void update(final Observable targer, final Object parameter) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            if (parameter != null && parameter instanceof DBLayer) {
                                    loginAction.setEnabled(false);
                                    view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                                    logger.debug("Database layer retrieval.");
                                    DBLayer dblayer = loginModel.getDBLayer();

                                    // FIXME: neni potreba zresetovat stav treba loginModelu, pokdu
                                    // neco takhle selze? pripadne stav jinyho objektu?
                                    try {
                                            model.setDatabase(dblayer);
                                    } catch (RemoteException ex) {
                                        logger.error("Caught an error in AppCoreCtrl update(): "+ex.getMessage());
                                        ex.printStackTrace();
                                        DefaultReconnectDialog.show(view,ex);
                                        return;
                                    } catch (DBLayerException ex) {
                                        logger.error("Caught an error in AppCoreCtrl update(): "+ex.getMessage());
                                        ex.printStackTrace();
                                        DefaultReconnectDialog.show(view,ex);
                                        return;
                                    }
                                    // distribute database to dialogs
                                    addModel.setDatabase(dblayer);
                                    editModel.setDatabase(dblayer);
                                    searchModel.setDatabase(dblayer);
                                    detailModel.setDatabase(dblayer);

                                    model.setAccessRights(loginModel.getAccessRights());
                                    model.login();

                                    view.getSBM().display(L10n.getString("Message.FillingDialogs"));
                                    fillDialogModels();

                                    view.getSBM().display(L10n.getString("Message.LoadingOverviewData"));
                                    searchModel.clear();
                                    searchModel.constructQuery();

                                    view.getSBM().displayDefaultText();

                                    view.initOverview();
                                    view.setCursor(Cursor.getDefaultCursor());
                                    setDatabaseDependentCommandsEnabled(true);

                                    //this can't be done earlier. must be done after the query is created
                                    //otherwise this listener would give the overview table model commands to load data
                                    //for a query that doesn't exist yet
                                    view.overviewScrollPane.addComponentListener(overviewResizeListener);

                                    /*-------------------------------------------------------------------
                                     *  This may no longer be necessary:
                                     *------------------------------------------------------------------*/
                    logger.debug("Distributing the new database layer to:");
                    logger.debug(" # export ");
                                    if( exportModel != null )
                                            exportModel.setDBLayer( dblayer );
                                    logger.debug(" # occurrence data import ");
                                    if( importModel != null )
                                            importModel.setDBLayer( dblayer );
                                    logger.debug(" # table data import ");
                                    if( tableImportModel != null )
                                            tableImportModel.setDBLayer( dblayer );
                                    logger.debug(" # record history ");
                                    if (historyModel != null ) 
                                        historyModel.restartQuery(History.HISTORY_RECORD);
                                    logger.debug(" # complete history ");
                                    if (wholeHistoryModel != null ) 
                                         wholeHistoryModel.restartQuery(History.HISTORY_WHOLE);
                                    logger.debug(" # user manager ");
                                    if (userManagerModel != null )
                                            userManagerModel.setDBLayer( dblayer );
                                    logger.debug(" # metadata manager ");
                                    if (metadataManagerModel != null )
                                            metadataManagerModel.setDBLayer( dblayer );							
                            }//if
                        }//run()
                    });//invokeLater
		}//update()
	}//DatabaseChange


        /** Helper class to refresh overview or to create a task that refreshes overview.
         *
         */
	private Task refreshOverview(boolean createTask) {
		if (createTask) {
			Task task = new Task() {
				public Object task() throws DBLayerException, RemoteException {
					searchModel.clear();
					searchModel.constructQuery();
					fireStopped(null);
					return null;
				}
			};

			return task;
		} else {
			searchModel.clear();
			searchModel.constructQuery();
			return null;
		}
	}

        /** Handles the refresh action invoked from overview. */
	class RefreshAction extends AbstractAction {
		public RefreshAction() {
			if (showButtonText)
				putValue(NAME, L10n.getString("Overview.Refresh"));
			putValue(SMALL_ICON, Resource
					.createIcon("/toolbarButtonGraphics/general/Refresh24.gif"));
			putValue(SHORT_DESCRIPTION, L10n.getString("Overview.RefreshTT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R,
					ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			// e can be null !!! - we call actionPerformed(null) in DeleteAction
			Task task = refreshOverview(true);
			ProgressBar progressBar = new DefaultProgressBar(task, view, true);

			task.start();
		}

	}

	/** Bridge between mostly managers and the rest of the application. 
	 * Propagates changes made in managers to the rest of the application.
	 * 
	 */
	class ManagerBridge implements Observer {
		public void update(Observable o, Object arg) {
			if (arg instanceof PlantloreConstants.Table[]) {
				PlantloreConstants.Table[] tables = (PlantloreConstants.Table[]) arg;
				try {
					for (PlantloreConstants.Table table : tables) {
						logger.debug("ManagerBridge received message that "
								+ table + " has been updated.");
						switch (table) {
						case AUTHOR:
							model.loadAuthors();
							addModel.setAuthors(model.getAuthors());
							editModel.setAuthors(model.getAuthors());
							searchModel.setAuthors(model.getAuthors());
							break;
						case AUTHOROCCURRENCE:
							model.loadAuthorRoles();
							addModel.setAuthorRoles(model.getAuthorRoles());
							editModel.setAuthorRoles(model.getAuthorRoles());
							searchModel.setAuthorRoles(model.getAuthorRoles());
							break;
						case METADATA:
							model.loadProjects();
							addModel.setProjects(model.getProjects());
							editModel.setProjects(model.getProjects());
							searchModel.setProjects(model.getProjects());
							break;
						case OCCURRENCE:
                                                    model.loadSources();
                                                    addModel.setSources(model.getSources());
                                                    editModel.setSources(model.getSources());
                                                    searchModel.setSources(model.getSources());
                                                    refreshAction.actionPerformed(null);
                                                    break;
                                                case HABITAT:
                                                        model.loadCountries();
                                                        addModel.setCountries(model.getCountries());
                                                        editModel.setCountries(model.getCountries());
                                                        searchModel.setCountries(model.getCountries());
                                                        break;
						case PHYTOCHORION:
							model.loadPhytCodes();
							model.loadPhytNames();
							addModel.setPhytCodes(model.getPhytCodes());
							addModel.setPhytNames(model.getPhytNames());
							editModel.setPhytCodes(model.getPhytCodes());
							editModel.setPhytNames(model.getPhytNames());
							searchModel.setPhytCodes(model.getPhytCodes());
							searchModel.setPhytNames(model.getPhytNames());
							break;
						case PLANT:
							model.loadPlants();
							addModel.setPlants(model.getPlants());
							editModel.setPlants(model.getPlants());
							searchModel.setPlants(model.getPlants());
							break;
						case PUBLICATION:
							model.loadPublications();
							addModel.setPublications(model.getPublications());
							editModel.setPublications(model.getPublications());
							searchModel
									.setPublications(model.getPublications());
							break;
						case RIGHT: // ???
							logger
									.warn("Reaction to changes in RIGHTS table is NOT IMPLEMENTED.");
							break;
						case TERRITORY:
							model.loadTerritories();
							addModel.setTerritories(model.getTerritories());
							editModel.setTerritories(model.getTerritories());
							searchModel.setTerritories(model.getTerritories());
							break;
						case USER: // ???
							logger
									.warn("Reaction to changes in USERS table is NOT IMPLEMENTED.");
							break;
						case VILLAGE:
							model.loadVillages();
							addModel.setVillages(model.getVillages());
							editModel.setVillages(model.getVillages());
							searchModel.setVillages(model.getVillages());
							break;
						}// switch table
					}// for
				} catch (DBLayerException ex) {
                                    DefaultExceptionHandler.handle(view,ex);
                                    return;
				} catch (RemoteException ex) {
                                    DefaultExceptionHandler.handle(view,ex);
                                    return;
				}
			}// if arg instanceof Table[]
		}// update()
	}// class ManagerBridge

        /** Handles resizing of Plantlore's main window.
         *
         * Computes and changes the page size if needed.
         */
	class OverviewResizeListener implements ComponentListener {
		private final static int sub = 20; // height of the header row perhaps

		public void componentResized(ComponentEvent e) {
			if (!model.loggedIn() || !model.dynamicPageLoading)
				return;
			Component c = e.getComponent();
			int tableHeight = c.getSize().height - sub; // height of the row
														// part of JTable ( -->
														// without header)
			int rowHeight = view.overview.getRowHeight();
			int newRecordsCount = tableHeight / rowHeight;
			try {
				model.setRecordsPerPage(newRecordsCount);
				view.recordsPerPage.setValue(newRecordsCount);
                        } catch (RemoteException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
			} catch (DBLayerException ex) {
                            DefaultExceptionHandler.handle(view,ex);
                            return;
			}
		}

		public void componentMoved(ComponentEvent e) {
		}

		public void componentShown(ComponentEvent e) {
		}

		public void componentHidden(ComponentEvent e) {
		}
	}

}
