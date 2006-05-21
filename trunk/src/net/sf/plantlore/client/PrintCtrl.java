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
import java.io.File;
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
import org.xml.sax.SAXParseException;

/**
 *
 * @author fraktalek
 */
public class PrintCtrl {
    Print model;
    PrintView view;
    String description = "JasperReport's .jrxml reports";
    JasperReport jasperReport;
    JasperPrint jasperPrint;
    
    /** Creates a new instance of PrintCtrl */
    public PrintCtrl(Print model, PrintView view) {
        this.model = model;
        this.view = view;
        view.chooseButton.setAction(new ChooseAction());
        view.printButton.setAction(new PrintAction());
        view.previewButton.setAction(new PreviewAction());
        view.helpButton.setAction(new HelpAction());
        view.cancelButton.setAction(new CancelAction());
        view.reportField.setEditable(false);
        
        view.printButton.setEnabled(false);
        view.previewButton.setEnabled(false);
    }
    
    class ChooseAction extends AbstractAction {
        JFileChooser chooser = new JFileChooser();
        
        public ChooseAction() {
            chooser.setFileFilter(new PrintFileFilter());
            putValue(NAME, L10n.getString("Print.Choose"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Print.ChooseTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Print.Choose"));                                    
        }
        
        public void actionPerformed(ActionEvent e) {
            int choice = chooser.showOpenDialog(view);
            if (choice == JFileChooser.APPROVE_OPTION) {
                try {
                    model.setTheChosenOne(chooser.getSelectedFile());                    
                } catch (JRException ex) {
                    JOptionPane.showMessageDialog(view, "The report is probably broken:\n "+ex);
                    return;
                }            
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
                JOptionPane.showMessageDialog(view.getParent(), "The report is probably broken:\n"+ex);            
            }
            
            try {
                JasperPrintManager.printReport(model.getJasperPrint(), true);
            } catch (JRException ex) {
                JOptionPane.showMessageDialog(view.getParent(), "A problem appeared while trying to print:\n"+ex);
            }
        }
        
    }
    class CancelAction extends AbstractAction {
        public CancelAction() {
            putValue(NAME, L10n.getString("Print.Cancel"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Print.CancelTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Print.Cancel"));                                    
        }
        public void actionPerformed(ActionEvent e) {
            view.setVisible(false);
        }
        
    }
    class HelpAction extends AbstractAction {
        public HelpAction() {
            putValue(NAME, L10n.getString("Print.Help"));
            putValue(SHORT_DESCRIPTION, L10n.getString("Print.HelpTT"));
            putValue(MNEMONIC_KEY, L10n.getMnemonic("Print.Help"));                                    
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
                JOptionPane.showMessageDialog(view.getParent(), "The report is probably broken:\n"+ex);                            
            }
            new SchedaView((Frame) view.getParent(), true, model.getJasperPrint()).setVisible(true);
        }
        
    }
    
    class PrintFileFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.exists() && !f.isDirectory()) {                
                String name = f.getName();
                int dot = name.lastIndexOf(".");
                if (dot < 0)
                    return false;
                if (!name.substring(dot).equals(".jrxml"))
                    return false;
                else return true;
            } else
                return false;
        }

        public String getDescription() {
            return description;
        }
    }
    
}
