/*
 * PrintCtrl.java
 *
 * Created on 19. květen 2006, 17:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.plantlore.l10n.L10n;
import org.apache.log4j.Logger;
import org.xml.sax.SAXParseException;

/**
 *
 * @author fraktalek
 */
public class PrintCtrl {
    Logger logger;
    Print model;
    PrintView view;
    String description = "JasperReport's .jasper reports";
    JasperReport jasperReport;
    JasperPrint jasperPrint;
    
    /** Creates a new instance of PrintCtrl */
    public PrintCtrl(Print model, PrintView view) {
        this.model = model;
        this.view = view;
        logger = Logger.getLogger(this.getClass().getPackage().getName());        
        view.chooseButton.setAction(new ChooseAction());
        view.printButton.setAction(new PrintAction());
        view.previewButton.setAction(new PreviewAction());
        view.helpButton.setAction(new HelpAction());
        view.cancelButton.setAction(new CancelAction());
        view.reportField.setEditable(false);
        
        view.schedaRadioButton.addActionListener(new ReportButtonListener());
        view.listRadioButton.addActionListener(new ReportButtonListener());
        view.ownReportRadioButton.addActionListener(new ReportButtonListener());
        
    }
    
    class ChooseAction extends AbstractAction {
        JFileChooser chooser = new JFileChooser();
        String brokenReport = L10n.getString("Print.Message.BrokenReport");
        String brokenReportTitle = L10n.getString("Print.Message.BrokenReportTitle");
        
        public ChooseAction() {
            chooser.setFileFilter(new PrintFileFilter());
            putValue(NAME, L10n.getString("Print.Choose"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Print.ChooseTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Print.Choose"));           
            setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent e) {
            int choice = chooser.showOpenDialog(view);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try {
                    model.setTheChosenOne(chooser.getSelectedFile());                    
                } catch (JRException ex) {
                    logger.warn("Broken report: "+ex);
                    JOptionPane.showMessageDialog(view, brokenReport+"\n"+ex.getMessage(),brokenReportTitle,JOptionPane.WARNING_MESSAGE);
                    return;
                } catch (FileNotFoundException ex) {
                    logger.warn("Broken report: "+ex);
                    JOptionPane.showMessageDialog(view, brokenReport+"\n"+ex.getMessage(),brokenReportTitle,JOptionPane.WARNING_MESSAGE);
                    return;                    
                } catch (IOException ex) {                    
                    logger.warn("Broken report: "+ex);
                    JOptionPane.showMessageDialog(view, brokenReport+"\n"+ex.getMessage(),brokenReportTitle,JOptionPane.WARNING_MESSAGE);
                    return;
                } catch (ClassNotFoundException ex) {
                    logger.warn("Broken report: "+ex);
                    JOptionPane.showMessageDialog(view, brokenReport+"\n"+ex.getMessage(),brokenReportTitle,JOptionPane.WARNING_MESSAGE);
                    return;                    
                }
                view.printButton.setEnabled(true);
                view.previewButton.setEnabled(true);                
            }//if
        }//actionPerformed        
    }
    
    class PrintAction extends AbstractAction {
        public PrintAction() {
            putValue(NAME, L10n.getString("Print.Print"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Print.PrintTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Print.Print"));                                    
        }
        public void actionPerformed(ActionEvent e) {
            try {
                model.createJasperPrint();
            } catch (JRException ex) {
                logger.error("Broken report: "+ex);
                JOptionPane.showMessageDialog(view.getParent(), L10n.getString("Print.Message.BrokenReport")+"\n"+ex.getMessage(),L10n.getString("Print.Message.BrokenReport"),JOptionPane.WARNING_MESSAGE);            
                return;
            }
            
            try {
                JasperPrintManager.printReport(model.getJasperPrint(), true);
            } catch (JRException ex) {
                logger.error("Problem while trying to print: "+ex);
                JOptionPane.showMessageDialog(view.getParent(), L10n.getString("Print.Message.PrintProblem")+"\n"+ex.getMessage(),L10n.getString("Print.Message.PrintingProblemTitle"),JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
    }
    class CancelAction extends AbstractAction {
        public CancelAction() {
            putValue(NAME, L10n.getString("Common.Cancel"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Common.CancelTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Common.Cancel"));                                    
        }
        public void actionPerformed(ActionEvent e) {
            view.setVisible(false);
        }
        
    }
    class HelpAction extends AbstractAction {
        public HelpAction() {
            putValue(NAME, L10n.getString("Common.Help"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Common.HelpTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Common.Help"));                                    
        }
        public void actionPerformed(ActionEvent e) {
            System.out.println("HELP!");
        }
        
    }
    class PreviewAction extends AbstractAction {
        public PreviewAction() {
            putValue(NAME, L10n.getString("Print.Preview"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Print.PreviewTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Print.Preview"));                                    
        }
        public void actionPerformed(ActionEvent e) {     
            try {
                model.createJasperPrint();
            } catch (JRException ex) {
                logger.error("Broken report: "+ex);
                JOptionPane.showMessageDialog(view.getParent(), L10n.getString("Print.Message.BrokenReport")+"\n"+ex.getMessage(),L10n.getString("Print.Message.BrokenReport"),JOptionPane.WARNING_MESSAGE);                            
                return;
            }
            new SchedaView((Frame) view.getParent(), true, model.getJasperPrint()).setVisible(true);
        }
        
    }
    
    class PrintFileFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            if (f.exists()) {                
                String name = f.getName();
                int dot = name.lastIndexOf(".");
                if (dot < 0)
                    return false;
                if (!name.substring(dot).equals(".jasper"))
                    return false;
                else return true;
            } else
                return false;
        }

        public String getDescription() {
            return description;
        }
    }
    
    class ReportButtonListener implements ActionListener {
        
        private void setControlsEnabled(boolean enabled) {
            view.useReportLabel.setEnabled(enabled);
            view.chooseButton.setEnabled(enabled);
        }
        
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("SCHEDA")) {
                model.setReportToUse(Print.SCHEDA);
                setControlsEnabled(false);
                view.printButton.setEnabled(true);
                view.previewButton.setEnabled(true);
            }
            
            if (command.equals("A4LIST")) {
                model.setReportToUse(Print.A4LIST);
                setControlsEnabled(false);
                view.printButton.setEnabled(true);
                view.previewButton.setEnabled(true);
            }
            
            if (command.equals("OWNREPORT")) {
                model.setReportToUse(Print.OWNREPORT);
                setControlsEnabled(true);
                if (model.getTheChosenOne() == null) {
                    view.printButton.setEnabled(false);
                    view.previewButton.setEnabled(false);                    
                }
            }
        }
    }//CoordinateSystemListener
    
}
