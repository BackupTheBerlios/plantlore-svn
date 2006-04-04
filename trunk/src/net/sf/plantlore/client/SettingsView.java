/*
 * SettingsView.java
 *
 * Created on 17. leden 2006, 17:27
 *
 */

package net.sf.plantlore.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import net.sf.plantlore.common.ComponentAdjust;
import net.sf.plantlore.common.SpringUtilities;
import net.sf.plantlore.common.StatusBarManager;
import net.sf.plantlore.l10n.L10n;

/** View for the Settings MVC
 *
 * @author Jakub
 */
public class SettingsView extends JDialog implements Observer 
{
    private Settings model;
    /** The panel containing toggle buttons on the left side of this dialog. */
    private JPanel iconsPane;
    /** This panel contains the switchable right side of the dialog. */
    private JPanel mainPane;
    
    private JPanel buttonsPane;
    
    /** Language settings. */
    private JPanel languagesPane;
    /** Other settings. */
    private JPanel otherPane;

    /** Toggle button for language settings. */
    private JToggleButton language;
    /** Toggle button for other settings */
    private JToggleButton other;
    
    /** This maps the buttons to their corresponding right side panels. */
    private HashMap<JToggleButton, JPanel> panels;

    private JButton okButton;
    private JButton cancelButton;
    private JButton helpButton;
    
    private ButtonGroup langGroup;
    
    private StatusBarManager sbm;
    
    /** Creates a new instance of SettingsView */
    public SettingsView(Settings model, StatusBarManager sbm)
    {
        this.model = model;
        this.sbm = sbm;
        init();
    }

    /** The top initializing method.
     *
     * Essentially constructs the whole dialog layout.
     *
     */
    private void init()
    {
        initDialog();
        panels = new HashMap<JToggleButton, JPanel>();
        panels.put(language, languagesPane);
        panels.put(other, otherPane);
    }

    /** Constructs the top dialog layout.
     *
     */
    private void initDialog() 
    {
        setTitle(L10n.getString("Settings"));
        setSize(500,300);
        setVisible(false);
        
        initIconsPane();
        initButtonsPane();
        initLanguagesPane();
        initOtherPane();

        mainPane = new JPanel();
        mainPane.add(languagesPane);
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gbc.weighty = 1;
        add(iconsPane,gbc);
        gbc.gridx = 1;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(mainPane, gbc);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        add(buttonsPane, gbc);
    }

    public void update(Observable observable, Object object)
    {
    }
    
    /** Adds a listener to the whole window events.
     *
     */
    public void addListener(WindowListener wl) 
    {
        addWindowListener(wl);
    }

    /** Sets the al listener to all toggle buttons on the left side.
     *
     */
    public void addIconListener(ActionListener al)
    {
        language.addActionListener(al);
        other.addActionListener(al);
    }
    
    /** Sets a listener to all language radio buttons in langGroup.
     *
     */
    public void addLanguagesListener(ItemListener il) 
    {
        Enumeration<AbstractButton> e = langGroup.getElements();
        AbstractButton ab;
        while (e.hasMoreElements()) {
            ab = e.nextElement();
            ab.addItemListener(il);
        }
    }
    
    /** Sets a listener to okButton, cancelButton and helpButton.
     *
     */
    public void addButtonsListener(ActionListener al) {
        okButton.addActionListener(al);
        cancelButton.addActionListener(al);
        helpButton.addActionListener(al);
    }
    
    /** Switches the right side of the dialog.
     * First removes all compoments from mainPane which should contain only one
     * JPanel. Then adds a new JPanel to the mainPane by translating the JToggleButton
     * received into a JPanel using panels HashMap.
     * Finally it validates the whole dialog so that it repaints itself according
     * to the new layout.
     */
    public void setMainPane(JToggleButton button) 
    {
        JPanel panel = (JPanel)panels.get(button);
        mainPane.removeAll();
        mainPane.add(panel);
        
        /* uuuuuuuuuuuuuuuuuaaaaaaaaaahhhhhhhhhhh!!!!!!!!!! :-(((
         *this whole invalidate, setSize(0,0) is needed only when mainPane
         *is being added into BorderLayout.CENTER or is added to GridBagLayout with
         *GridBagConstraint.widthx set to 1 !!! why? whyyyyyy?!! what have I done to whom??!!
         */
        panel.invalidate();
        mainPane.setSize(0,0);
        mainPane.invalidate();
        
        validate();
    }
    
    /** Constructs the left icons/JToggleButtons pane.
     * Uses Sun's example code for SpringLayout to make the panel look nice - to
     * make all the buttons the same size and distrubuted regurarly on the pane.
     */
    private void initIconsPane()
    {
        language = new JToggleButton();
        other = new JToggleButton();
        ButtonGroup group = new ButtonGroup();
        SpringLayout sl = new SpringLayout();
        
        iconsPane = new JPanel();
        iconsPane.setBackground(new Color(255,255,255));
        iconsPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(153, 153, 255)));
        iconsPane.setLayout(sl);

        language.setText(L10n.getString("Languages"));
        language.setIcon(new ImageIcon(getClass().getResource("/net/sf/plantlore/client/resources/babelfish32.gif")));
        language.setHorizontalTextPosition(SwingConstants.CENTER);
        language.setVerticalTextPosition(SwingConstants.BOTTOM);
        language.setSelected(true);

        other.setText(L10n.getString("Other"));
        other.setIcon(new ImageIcon(getClass().getResource("/net/sf/plantlore/client/resources/mysterio.gif")));
        other.setHorizontalTextPosition(SwingConstants.CENTER);
        other.setVerticalTextPosition(SwingConstants.BOTTOM);
        
        iconsPane.add(language);
        iconsPane.add(other);
        group.add(language);
        group.add(other);
        
        sbm.add(language, L10n.getString("statusLangSettings"));
        sbm.add(other, L10n.getString("statusOtherSettings"));
        
        SpringUtilities.makeGrid(iconsPane, 2, 1, 2, 2, 2, 2); 
    }//initIconsPane();

    /** Constructs the Languages pane.
     *
     */
    private void initLanguagesPane()
    {
        JPanel radioPane = new JPanel();
        
        langGroup = new ButtonGroup();
        JRadioButton defaultLang = new JRadioButton(L10n.getString("defaultLang"));
        JRadioButton czech = new JRadioButton(L10n.getString("Czech"));
        JRadioButton english = new JRadioButton(L10n.getString("English"));

        // We'll set action commands so that we can use only one item listener
        defaultLang.setActionCommand(L10n.DEFAULT_LANGUAGE);
        czech.setActionCommand(L10n.CZECH);
        english.setActionCommand(L10n.ENGLISH);
        
        
        switch (model.getLanguage()) {
            case Settings.DEFAULT_LANGUAGE:
                defaultLang.setSelected(true);
                break;
            case Settings.CZECH:
                czech.setSelected(true);
                break;
            case Settings.ENGLISH:
                english.setSelected(true);
                break;
        }
        
        GridBagConstraints gbc = new GridBagConstraints();
        radioPane.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        radioPane.add(defaultLang, gbc);
        gbc.gridy = 1;
        radioPane.add(czech, gbc);
        gbc.gridy = 2;
        radioPane.add(english, gbc);
        
        langGroup.add(defaultLang);
        langGroup.add(czech);
        langGroup.add(english);
        
        
        languagesPane = new JPanel(new BorderLayout());
        JLabel label = new JLabel(L10n.getString("Languages"));
        languagesPane.add(label, BorderLayout.NORTH);
        languagesPane.add(radioPane, BorderLayout.WEST);
         
    }//initLanguagesPane()

    /** Constructs the Other pane.
     *
     */
    private void initOtherPane()
    {
        otherPane = new JPanel();
        otherPane.setLayout(new FlowLayout());
        otherPane.add(new JLabel("Other"));
    }//initOtherPane()

    /** Constructs the buttons pane.
     *
     */
    private void initButtonsPane()
    {
        ComponentAdjust ca = new ComponentAdjust();
        
        okButton = new JButton(L10n.getString("Ok"));
        cancelButton = new JButton(L10n.getString("Cancel"));
        helpButton = new JButton(L10n.getString("Help"));
        okButton.setActionCommand("OK");
        cancelButton.setActionCommand("CANCEL");
        helpButton.setActionCommand("HELP");
        
        ca.add(okButton);
        ca.add(cancelButton);
        ca.add(helpButton);
        ca.setMaxWidth();
        
        buttonsPane = new JPanel();
        buttonsPane.setLayout(new FlowLayout());
        buttonsPane.add(okButton);
        buttonsPane.add(cancelButton);
        buttonsPane.add(helpButton);
        
        sbm.add(okButton, L10n.getString("statusOkButton"));
        sbm.add(cancelButton, L10n.getString("statusCancelButton"));
        sbm.add(helpButton, L10n.getString("statusHelpButton"));
    }
    
}// class SettingsView


