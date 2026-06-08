/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package org.softcaster.easy_pricer_eod;

import jakarta.annotation.PostConstruct;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.softcaster.commons.ui.model.FndtNode;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_eod.services.MicroserviceLauncher;
import org.softcaster.easy_pricer_eod.ui.models.TreeModel;
import org.softcaster.easy_pricer_eod.ui.views.HomePanel;
import org.softcaster.easy_pricer_eod.ui.views.RestEnginePanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JEodOrchestrator extends javax.swing.JFrame {

    public static final String TITLE = "E.O.D Orchestrator 1.0";

    private AppCard currentCard;
    private Map<AppCard, javax.swing.JPanel> cardMap = new HashMap<>();

    @Autowired
    private MicroserviceLauncher microserviceLauncher;

    /**
     * Creates new form JEodOrchestrator
     */
    public JEodOrchestrator() {
        try {
            // Imposta il tema Windows
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            initComponents();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
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
            //navigator.setCellRenderer(new MDSTreeCellRenderer());
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
                            case REST_ENGINE -> {
                                cl.show(mainPanel, AppCard.REST_ENGINE_CARD.name());
                                currentCard = AppCard.REST_ENGINE_CARD;
                            }
                            case TRADE_PROCESSOR -> {
                                cl.show(mainPanel, AppCard.TRADE_PROCESSOR_CARD.name());
                                currentCard = AppCard.TRADE_PROCESSOR_CARD;
                            }
                            case MTM_ENGINE -> {
                                cl.show(mainPanel, AppCard.MTM_ENGINE_CARD.name());
                                currentCard = AppCard.MTM_ENGINE_CARD;
                            }
                            case SCHEDULER -> {
                                cl.show(mainPanel, AppCard.SCHEDULER_CARD.name());
                                currentCard = AppCard.SCHEDULER_CARD;
                            }
                            case ACCT_ENGINE -> {
                                cl.show(mainPanel, AppCard.ACCT_ENGINE_CARD.name());
                                currentCard = AppCard.ACCT_ENGINE_CARD;
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

    private void addPanels() {
        // 1. Istanzia i pannelli
        JPanel defaultPanel = new HomePanel();
        cardMap.put(AppCard.DEFAULT_CARD, defaultPanel);
        JPanel rePanel = new RestEnginePanel();
        cardMap.put(AppCard.REST_ENGINE_CARD, rePanel);

        // 2. Aggiunge al mainPanel assegnando un nome (la "Chiave" della Card)
        mainPanel.add(defaultPanel, AppCard.DEFAULT_CARD.name());
        mainPanel.add(rePanel, AppCard.REST_ENGINE_CARD.name());

        // 3. Mostra la card iniziale
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, AppCard.DEFAULT_CARD.name());
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
        btnExit.addActionListener(this::btnExitActionPerformed);
        toolBar.add(btnExit);
        toolBar.add(jSeparator1);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/file_open_16dp.png"))); // NOI18N
        btnRefresh.setToolTipText("Refresh");
        btnRefresh.setFocusable(false);
        btnRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        toolBar.add(btnRefresh);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/save_16dp.png"))); // NOI18N
        btnSave.setToolTipText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(this::btnSaveActionPerformed);
        toolBar.add(btnSave);
        toolBar.add(jSeparator3);

        btnCalc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/analytics_16dp.png"))); // NOI18N
        btnCalc.setToolTipText("Calculate");
        btnCalc.setFocusable(false);
        btnCalc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCalc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCalc.addActionListener(this::btnCalcActionPerformed);
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
        itemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close16dp.png"))); // NOI18N
        itemExit.setText("Exit");
        itemExit.addActionListener(this::itemExitActionPerformed);
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

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshAction();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveAction();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcActionPerformed
        calculateAction();
    }//GEN-LAST:event_btnCalcActionPerformed


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

    private void refreshAction() {
        // Definisci dove si trova il JAR del servizio MTM (puoi metterlo anche nell'application.properties)
        String jarPath = "C:/test/rsrv/easy_pricer_srv-1.0.jar";

        // Avvia il servizio
        microserviceLauncher.startMtmService(jarPath, "dev");
    }

    private void saveAction() {
    }

    private void calculateAction() {
    }
}
