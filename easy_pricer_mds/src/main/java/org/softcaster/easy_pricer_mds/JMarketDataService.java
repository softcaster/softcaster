/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.softcaster.easy_pricer_mds;

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
import org.softcaster.commons.ui.model.FndtNode;
import org.softcaster.commons.ui.view.FndtAbstactPanel;
import org.softcaster.commons.utils.LoggerMgr;
import static org.softcaster.easy_pricer_mds.AppTreeItem.BOND;
import static org.softcaster.easy_pricer_mds.AppTreeItem.BOND_FUTURE;
import static org.softcaster.easy_pricer_mds.AppTreeItem.CURR_PAIR;
import static org.softcaster.easy_pricer_mds.AppTreeItem.FX_FUTURE;
import static org.softcaster.easy_pricer_mds.AppTreeItem.YC_DEFINE;
import org.softcaster.easy_pricer_mds.ui.model.TreeModel;
import org.softcaster.easy_pricer_mds.ui.MDSTreeCellRenderer;
import org.softcaster.easy_pricer_mds.view.BondFutPanel;
import org.softcaster.easy_pricer_mds.view.BondPanel;
import org.softcaster.easy_pricer_mds.view.CmdFutPanel;
import org.softcaster.easy_pricer_mds.view.CurrPairPanel;
import org.softcaster.easy_pricer_mds.view.FxFutPanel;
import org.softcaster.easy_pricer_mds.view.HomePanel;
import org.softcaster.easy_pricer_mds.view.MmFutPanel;
import org.softcaster.easy_pricer_mds.view.YieldCurveDefPanel;
import org.softcaster.easy_pricer_mds.view.YieldCurvePanel;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component
public class JMarketDataService extends javax.swing.JFrame {

    public static final String TITLE = "Market Data Service 1.0";

    @Autowired
    private MDSFacade mDSFacade;

    private AppCard currentCard;
    private Map<AppCard, javax.swing.JPanel> cardMap = new HashMap<>();
    private MarketDataService service = null;

    /**
     * Creates new form JMarketDataService
     */
    public JMarketDataService() {
        try {
            // Imposta il tema Windows
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            initComponents();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(JMarketDataService.class.getName()).log(Level.SEVERE, null, ex);
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
            navigator.setCellRenderer(new MDSTreeCellRenderer());
            navigator.addTreeSelectionListener(e -> {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) navigator.getLastSelectedPathComponent();

                if (node == null) {
                    return; // Nessuna selezione
                }
                Object userObject = node.getUserObject();
                if (node.isLeaf() && userObject instanceof FndtNode data) {

                    // Verifico il tipo esplicito
                    if (data.getType() instanceof AppTreeItem item) {
                        // Recupera il CardLayout dal mainPanel
                        CardLayout cl = (CardLayout) (mainPanel.getLayout());

                        // Logica per decidere quale pannello mostrare in base al tipo
                        switch (item) {
                            case CURR_PAIR -> {
                                cl.show(mainPanel, AppCard.CURR_PAIR_CARD.name());
                                currentCard = AppCard.CURR_PAIR_CARD;
                            }
                            case FX_FUTURE -> {
                                cl.show(mainPanel, AppCard.FX_FUTURE_CARD.name());
                                currentCard = AppCard.FX_FUTURE_CARD;
                            }
                            case CMD_FUTURE -> {
                                cl.show(mainPanel, AppCard.CMD_FUTURE_CARD.name());
                                currentCard = AppCard.CMD_FUTURE_CARD;
                            }
                            case MM_FUTURE -> {
                                cl.show(mainPanel, AppCard.MM_FUTURE_CARD.name());
                                currentCard = AppCard.MM_FUTURE_CARD;
                            }
                            case BOND_FUTURE -> {
                                cl.show(mainPanel, AppCard.BOND_FUTURE_CARD.name());
                                currentCard = AppCard.BOND_FUTURE_CARD;
                            }
                            case BOND -> {
                                cl.show(mainPanel, AppCard.BOND_CARD.name());
                                currentCard = AppCard.BOND_CARD;
                            }
                            case YC_DEFINE -> {
                                cl.show(mainPanel, AppCard.YC_DEFINE_CARD.name());
                                currentCard = AppCard.YC_DEFINE_CARD;
                            }
                            case YC_UPDATE -> {
                                cl.show(mainPanel, AppCard.YC_UPDATE_CARD.name());
                                currentCard = AppCard.YC_UPDATE_CARD;
                            }
                            default -> {
                                cl.show(mainPanel, AppCard.DEFAULT_CARD.name());
                                currentCard = AppCard.DEFAULT_CARD;
                            }
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
        btnRefresh = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnCalc = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        itemRefresh = new javax.swing.JMenuItem();
        itemSave = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        itemExit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        splitPane.setDividerLocation(200);

        sideNav.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sideNav.setLayout(new java.awt.BorderLayout(10, 0));

        sideNavScrollPane.setPreferredSize(new java.awt.Dimension(80, 362));
        sideNavScrollPane.setViewportView(navigator);

        sideNav.add(sideNavScrollPane, java.awt.BorderLayout.CENTER);

        splitPane.setLeftComponent(sideNav);

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new java.awt.CardLayout());
        splitPane.setRightComponent(mainPanel);

        getContentPane().add(splitPane, java.awt.BorderLayout.CENTER);

        toolBar.setRollover(true);

        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/close16dp.png"))); // NOI18N
        btnExit.setToolTipText("Exit");
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

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/file_open_16dp.png"))); // NOI18N
        btnRefresh.setToolTipText("Refresh");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        toolBar.add(btnRefresh);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/save_16dp.png"))); // NOI18N
        btnSave.setToolTipText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        toolBar.add(btnSave);
        toolBar.add(jSeparator3);

        btnCalc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/analytics_16dp.png"))); // NOI18N
        btnCalc.setToolTipText("Calculate");
        btnCalc.setFocusable(false);
        btnCalc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCalc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcActionPerformed(evt);
            }
        });
        toolBar.add(btnCalc);

        getContentPane().add(toolBar, java.awt.BorderLayout.PAGE_START);

        fileMenu.setText("File");

        itemRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_DOWN_MASK));
        itemRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/file_open_16dp.png"))); // NOI18N
        itemRefresh.setText("Refresh");
        fileMenu.add(itemRefresh);

        itemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK));
        itemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/save_16dp.png"))); // NOI18N
        itemSave.setText("Save");
        fileMenu.add(itemSave);
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

        setBounds(0, 0, 571, 424);
    }// </editor-fold>//GEN-END:initComponents

    private void itemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExitActionPerformed
        exitAction();
    }//GEN-LAST:event_itemExitActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        exitAction();
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshAction();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcActionPerformed
        calculateAction();
    }//GEN-LAST:event_btnCalcActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveAction();
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalc;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemRefresh;
    private javax.swing.JMenuItem itemSave;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
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
        JPanel defaultPanel = new HomePanel();
        cardMap.put(AppCard.DEFAULT_CARD, defaultPanel);
        JPanel cpPanel = new CurrPairPanel(mDSFacade);
        cardMap.put(AppCard.CURR_PAIR_CARD, cpPanel);
        JPanel fxFutPanel = new FxFutPanel(mDSFacade);
        cardMap.put(AppCard.FX_FUTURE_CARD, fxFutPanel);
        JPanel mmFutPanel = new MmFutPanel(mDSFacade);
        cardMap.put(AppCard.MM_FUTURE_CARD, mmFutPanel);
        JPanel cmdFutPanel = new CmdFutPanel(mDSFacade);
        cardMap.put(AppCard.CMD_FUTURE_CARD, cmdFutPanel);
        JPanel bondFutPanel = new BondFutPanel(mDSFacade);
        cardMap.put(AppCard.BOND_FUTURE_CARD, bondFutPanel);
        JPanel bondPanel = new BondPanel(mDSFacade);
        cardMap.put(AppCard.BOND_CARD, bondPanel);
        JPanel ycPanel = new YieldCurveDefPanel(mDSFacade);
        cardMap.put(AppCard.YC_DEFINE_CARD, ycPanel);
        JPanel ycUpdatePanel = new YieldCurvePanel(mDSFacade);
        cardMap.put(AppCard.YC_UPDATE_CARD, ycUpdatePanel);

        // 2. Aggiunge al mainPanel assegnando un nome (la "Chiave" della Card)
        mainPanel.add(defaultPanel, AppCard.DEFAULT_CARD.name());
        mainPanel.add(cpPanel, AppCard.CURR_PAIR_CARD.name());
        mainPanel.add(fxFutPanel, AppCard.FX_FUTURE_CARD.name());
        mainPanel.add(mmFutPanel, AppCard.MM_FUTURE_CARD.name());
        mainPanel.add(cmdFutPanel, AppCard.CMD_FUTURE_CARD.name());
        mainPanel.add(bondFutPanel, AppCard.BOND_FUTURE_CARD.name());
        mainPanel.add(bondPanel, AppCard.BOND_CARD.name());
        mainPanel.add(ycPanel, AppCard.YC_DEFINE_CARD.name());
        mainPanel.add(ycUpdatePanel, AppCard.YC_UPDATE_CARD.name());

        // 3. Mostra la card iniziale
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, AppCard.DEFAULT_CARD.name());
    }

    private FndtAbstactPanel getActiveCard() {
        JPanel panel = cardMap.get(currentCard);
        if (panel instanceof FndtAbstactPanel abstactMDPanel) {
            return abstactMDPanel;
        } else {
            return null;
        }
    }

    private void refreshAction() {
        FndtAbstactPanel activePanel = getActiveCard();
        if (activePanel != null) {
            activePanel.refreshAction();
        }
    }

    private void calculateAction() {
        FndtAbstactPanel activePanel = getActiveCard();
        if (activePanel != null && activePanel instanceof BondPanel bondPanel) {
            bondPanel.calculateAction();
        }
    }

    private void saveAction() {
        FndtAbstactPanel activePanel = getActiveCard();
        if (activePanel != null) {
            activePanel.downloadAction();
        }
    }

}
