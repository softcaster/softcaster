/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.softcaster.master_data_mgr;

import jakarta.annotation.PostConstruct;
import java.awt.CardLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_core.data.BondFutureMasterDataDAO;
import org.softcaster.easy_pricer_core.data.CurrencyDAO;
import org.softcaster.easy_pricer_core.data.SecurityMasterDataDAO;
import org.softcaster.master_data_mgr.models.MasterDataNode;
import org.softcaster.master_data_mgr.models.TreeModel;
import org.softcaster.master_data_mgr.ui.MasterDataTreeCellRenderer;
import org.softcaster.master_data_mgr.views.BondFuturePanel;
import org.softcaster.master_data_mgr.views.BondPanel;
import org.softcaster.master_data_mgr.views.ForexPanel;
import org.softcaster.master_data_mgr.views.HomePanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component
public class JMasterDataMgr extends javax.swing.JFrame {

    @Autowired
    private SecurityMasterDataDAO securityMasterDataDAO;
    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    private BondFutureMasterDataDAO bondFutureMasterDataDAO;

    public static final String TITLE = "Master Data Versione 1.0";

    /**
     * Creates new form JMasterDataMgr
     */
    public JMasterDataMgr() {
        try {
            // Imposta il tema Windows
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            initComponents();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(JMasterDataMgr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init() {

        // Forza l'esecuzione sul thread di Swing per evitare race conditions
        java.awt.EventQueue.invokeLater(() -> {

            // Titolo
            this.setTitle(TITLE);

            // Icona
            ImageIcon img = new ImageIcon(getClass().getResource("/images/favicon.png"));
            setIconImage(img.getImage());

            // Set the initial size of the window
            setSize(1200, 800);

            // Finestra centrata nello schermo
            setLocationRelativeTo(null);

            navigator.setModel(TreeModel.buildTree());
            navigator.setCellRenderer(new MasterDataTreeCellRenderer());
            navigator.addTreeSelectionListener(e -> {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) navigator.getLastSelectedPathComponent();

                if (node == null) {
                    return; // Nessuna selezione
                }
                Object userObject = node.getUserObject();
                if (node.isLeaf() && userObject instanceof MasterDataNode data) {

                    // Recupera il CardLayout dal mainPanel
                    CardLayout cl = (CardLayout) (mainPanel.getLayout());

                    // Logica per decidere quale pannello mostrare in base al tipo
                    switch (data.getType()) {
                        case "BOND" ->
                            cl.show(mainPanel, "BOND_CARD");
                        case "CURRENCIES" ->
                            cl.show(mainPanel, "CURRENCY_CARD");
                        case "BOND_FUTURE" ->
                            cl.show(mainPanel, "BOND_FUTURE_CARD");
                        default ->
                            cl.show(mainPanel, "DEFAULT");
                    }

                    // Opzionale: Carica i dati specifici nel pannello appena mostrato
                    // bondPanel.loadData(data.getId()); 
                }
            });

            addPanels();
            // Sovrascrive qualsiasi impostazione precedente
            this.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);

            // Rimuovi eventuali listener esistenti per evitare doppie chiamate
            for (java.awt.event.WindowListener wl : this.getWindowListeners()) {
                this.removeWindowListener(wl);
            }

            this.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    exitAction();
                }
            });
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        sideNav = new javax.swing.JPanel();
        sideNavScrollPane = new javax.swing.JScrollPane();
        navigator = new javax.swing.JTree();
        mainPanel = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        btnExit = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnFilter = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        itemExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        splitPane.setDividerLocation(200);

        sideNav.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sideNav.setLayout(new java.awt.BorderLayout());

        sideNavScrollPane.setViewportView(navigator);

        sideNav.add(sideNavScrollPane, java.awt.BorderLayout.CENTER);

        splitPane.setLeftComponent(sideNav);

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 5));
        mainPanel.setLayout(new java.awt.CardLayout());
        splitPane.setRightComponent(mainPanel);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        toolBar.setRollover(true);

        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/close16dp.png"))); // NOI18N
        btnExit.setToolTipText("Exit App");
        btnExit.setFocusable(false);
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        toolBar.add(btnExit);
        toolBar.add(jSeparator1);

        btnFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/filter_alt_16dp.png"))); // NOI18N
        btnFilter.setToolTipText("Add new Item");
        btnFilter.setFocusable(false);
        btnFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnFilter);

        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/download_16dp.png"))); // NOI18N
        btnDown.setToolTipText("Edit selected Item");
        btnDown.setFocusable(false);
        btnDown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDown.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnDown);

        getContentPane().add(toolBar, java.awt.BorderLayout.PAGE_START);

        fileMenu.setText("File");

        itemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_DOWN_MASK));
        itemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/close16dp.png"))); // NOI18N
        itemExit.setText("Exit");
        itemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemExitActionPerformed(evt);
            }
        });
        fileMenu.add(itemExit);

        menuBar.add(fileMenu);

        jMenu2.setText("Edit");
        menuBar.add(jMenu2);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExitActionPerformed
        // TODO add your handling code here:
        exitAction();
    }//GEN-LAST:event_itemExitActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        exitAction();
    }//GEN-LAST:event_btnExitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFilter;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTree navigator;
    private javax.swing.JPanel sideNav;
    private javax.swing.JScrollPane sideNavScrollPane;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    private void exitAction() {
        if (JOptionPane.showConfirmDialog(this,
                "Are you sure?", TITLE,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            LoggerMgr.logInfo("Bye bye!");
            System.exit(0);
        }
    }

    private void addPanels() {
        // 1. Istanzia i pannelli
        JPanel bondPanel = new BondPanel(securityMasterDataDAO);
        JPanel fxPanel = new ForexPanel(currencyDAO);
        JPanel futBondPanel = new BondFuturePanel(bondFutureMasterDataDAO);
        JPanel defaultPanel = new HomePanel();

        // 2. Aggiunge al mainPanel assegnando un nome (la "Chiave" della Card)
        mainPanel.add(defaultPanel, "DEFAULT");
        mainPanel.add(bondPanel, "BOND_CARD");
        mainPanel.add(fxPanel, "CURRENCY_CARD");
        mainPanel.add(futBondPanel, "BOND_FUTURE_CARD");

        // 3. Mostra la card iniziale
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "DEFAULT");
    }

}
