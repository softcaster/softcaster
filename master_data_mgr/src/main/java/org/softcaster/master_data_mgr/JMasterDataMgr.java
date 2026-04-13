/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.softcaster.master_data_mgr;

import jakarta.annotation.PostConstruct;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.master_data_mgr.models.MasterDataNode;
import org.softcaster.master_data_mgr.models.TreeModel;
import org.softcaster.master_data_mgr.ui.MasterDataTreeCellRenderer;
import org.softcaster.master_data_mgr.views.AbstactMDPanel;
import org.softcaster.master_data_mgr.views.BondFuturePanel;
import org.softcaster.master_data_mgr.views.BondPanel;
import org.softcaster.master_data_mgr.views.CounterpartyPanel;
import org.softcaster.master_data_mgr.views.CurrPairPanel;
import org.softcaster.master_data_mgr.views.ForexPanel;
import org.softcaster.master_data_mgr.views.HomePanel;
import org.softcaster.master_data_mgr.views.IssuerPanel;
import org.softcaster.master_data_mgr.views.PortfolioPanel;
import org.softcaster.master_data_mgr.views.PositionPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component
public class JMasterDataMgr extends javax.swing.JFrame {

    @Autowired
    private MasterDataFacade masterDataFacade;

    public static final String TITLE = "Master Data Version 1.0";
    private AppCard currentCard;
    private Map<AppCard, javax.swing.JPanel> cardMap = new HashMap<>();

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
                        case BOND -> {
                            cl.show(mainPanel, AppCard.BOND_CARD.name());
                            currentCard = AppCard.BOND_CARD;
                        }
                        case CURRENCY -> {
                            cl.show(mainPanel, AppCard.CURRENCY_CARD.name());
                            currentCard = AppCard.CURRENCY_CARD;
                        }
                        case BOND_FUTURE -> {
                            cl.show(mainPanel, AppCard.BOND_FUTURE_CARD.name());
                            currentCard = AppCard.BOND_FUTURE_CARD;
                        }
                        case CURR_PAIR -> {
                            cl.show(mainPanel, AppCard.CURR_PAIR_CARD.name());
                            currentCard = AppCard.CURR_PAIR_CARD;
                        }
                        case COUNTERPARTY -> {
                            cl.show(mainPanel, AppCard.COUNTERPARTY_CARD.name());
                            currentCard = AppCard.COUNTERPARTY_CARD;
                        }
                        case PORTFOLIO -> {
                            cl.show(mainPanel, AppCard.PORTFOLIO_CARD.name());
                            currentCard = AppCard.PORTFOLIO_CARD;
                        }
                        case POSITION -> {
                            cl.show(mainPanel, AppCard.POSITION_CARD.name());
                            currentCard = AppCard.POSITION_CARD;
                        }
                        case ISSUER -> {
                            cl.show(mainPanel, AppCard.ISSUER_CARD.name());
                            currentCard = AppCard.ISSUER_CARD;
                        }
                        default -> {
                            cl.show(mainPanel, "DEFAULT");
                            currentCard = AppCard.DEFAULT;
                        }
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
        itemFilter = new javax.swing.JMenuItem();
        itemDownLoad = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        itemExit = new javax.swing.JMenuItem();

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
        btnFilter.setToolTipText("Filter Data");
        btnFilter.setFocusable(false);
        btnFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnFilter);

        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/download_16dp.png"))); // NOI18N
        btnDown.setToolTipText("Download Data");
        btnDown.setFocusable(false);
        btnDown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDown.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });
        toolBar.add(btnDown);

        getContentPane().add(toolBar, java.awt.BorderLayout.PAGE_START);

        fileMenu.setText("File");

        itemFilter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_DOWN_MASK));
        itemFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/filter_alt_16dp.png"))); // NOI18N
        itemFilter.setText("Filter");
        fileMenu.add(itemFilter);

        itemDownLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_DOWN_MASK));
        itemDownLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/download_16dp.png"))); // NOI18N
        itemDownLoad.setText("Download");
        itemDownLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemDownLoadActionPerformed(evt);
            }
        });
        fileMenu.add(itemDownLoad);
        fileMenu.add(jSeparator2);

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

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExitActionPerformed
        exitAction();
    }//GEN-LAST:event_itemExitActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        exitAction();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        downloadAction();
    }//GEN-LAST:event_btnDownActionPerformed

    private void itemDownLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemDownLoadActionPerformed
        downloadAction();
    }//GEN-LAST:event_itemDownLoadActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFilter;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem itemDownLoad;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemFilter;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
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

    private void downloadAction() {
        AbstactMDPanel activePanel = getActiveCard();
        if (activePanel != null) {
            activePanel.downloadAction();
        }
    }

    private void filterAction() {

    }

    private AbstactMDPanel getActiveCard() {
        JPanel panel = cardMap.get(currentCard);
        if (panel instanceof AbstactMDPanel abstactMDPanel) {
            return abstactMDPanel;
        } else {
            return null;
        }
    }

    private void addPanels() {
        // 1. Istanzia i pannelli
        JPanel bondPanel = new BondPanel(masterDataFacade);
        cardMap.put(AppCard.BOND_CARD, bondPanel);

        JPanel fxPanel = new ForexPanel(masterDataFacade.getCurrencyDAO());
        cardMap.put(AppCard.CURRENCY_CARD, fxPanel);

        JPanel currPairPanel = new CurrPairPanel(masterDataFacade);
        cardMap.put(AppCard.CURR_PAIR_CARD, currPairPanel);

        JPanel futBondPanel = new BondFuturePanel(masterDataFacade);
        cardMap.put(AppCard.BOND_FUTURE_CARD, futBondPanel);

        JPanel defaultPanel = new HomePanel();
        cardMap.put(AppCard.DEFAULT, defaultPanel);
        
        JPanel counterpartyPanel = new CounterpartyPanel(masterDataFacade);
        cardMap.put(AppCard.COUNTERPARTY_CARD, counterpartyPanel);

        JPanel portfolioPanel = new PortfolioPanel(masterDataFacade);
        cardMap.put(AppCard.PORTFOLIO_CARD, portfolioPanel);

        JPanel positionPanel = new PositionPanel(masterDataFacade);
        cardMap.put(AppCard.POSITION_CARD, positionPanel);

        JPanel issuerPanel = new IssuerPanel(masterDataFacade);
        cardMap.put(AppCard.ISSUER_CARD, issuerPanel);

        // 2. Aggiunge al mainPanel assegnando un nome (la "Chiave" della Card)
        mainPanel.add(defaultPanel, AppCard.DEFAULT.name());
        mainPanel.add(bondPanel, AppCard.BOND_CARD.name());
        mainPanel.add(fxPanel, AppCard.CURRENCY_CARD.name());
        mainPanel.add(currPairPanel, AppCard.CURR_PAIR_CARD.name());
        mainPanel.add(futBondPanel, AppCard.BOND_FUTURE_CARD.name());
        mainPanel.add(counterpartyPanel, AppCard.COUNTERPARTY_CARD.name());
        mainPanel.add(portfolioPanel, AppCard.PORTFOLIO_CARD.name());
        mainPanel.add(positionPanel, AppCard.POSITION_CARD.name());
        mainPanel.add(issuerPanel, AppCard.ISSUER_CARD.name());

        // 3. Mostra la card iniziale
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, AppCard.DEFAULT.name());
    }

}
