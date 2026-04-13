/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.softcaster.easy_pricer_mds.model.MDSTableModel;
import org.softcaster.easy_pricer_mds.ui.DecimalRenderer;
import org.softcaster.easy_pricer_mds.ui.PopupListener;

/**
 *
 * @author ep
 */
public abstract class AbstactMDPanel extends javax.swing.JPanel {

    private javax.swing.JPopupMenu popUp;
    private javax.swing.JMenuItem acDel;
    private javax.swing.JMenuItem acMod;
    private javax.swing.JMenuItem acNew;

    protected abstract void fillModelList();

    protected abstract void refreshModel(MDSTableModel model);
    
    protected abstract void acNewActionPerformed(java.awt.event.ActionEvent evt);

    protected abstract void acModActionPerformed(java.awt.event.ActionEvent evt);

    protected abstract void acDelActionPerformed(java.awt.event.ActionEvent evt);
    
    public abstract void downloadAction();
    public abstract void filterAction();

    protected void postInitComponents(JTable table) {
        initTable(table);
        initPopUp(table);
    }
    
    protected void initPopUp(JTable table) {
        popUp = new javax.swing.JPopupMenu();
        acNew = new javax.swing.JMenuItem();
        acMod = new javax.swing.JMenuItem();
        acDel = new javax.swing.JMenuItem();

        acNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/draft_16dp.png"))); // NOI18N
        acNew.setText("New");
        acNew.addActionListener(this::acNewActionPerformed);
        popUp.add(acNew);

        acMod.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/edit_16dp.png"))); // NOI18N
        acMod.setText("Edit");
        acMod.addActionListener(this::acModActionPerformed);
        popUp.add(acMod);

        acDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/angular/delete_16dp.png"))); // NOI18N
        acDel.setText("Delete");
        acDel.addActionListener(this::acDelActionPerformed);
        popUp.add(acDel);
        
        table.addMouseListener(new PopupListener(popUp));
    }

    protected void initTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Opzionale: Rimuovi le linee della griglia per un look più moderno (flat)
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Header Elegante
        table.getTableHeader().setOpaque(false);
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Più spazio rende i dati più leggibili
        table.setRowHeight(30);

        // Selezione
        table.setSelectionBackground(new Color(184, 207, 229)); // Un blu delicato per la riga selezionata
        table.setSelectionForeground(Color.BLACK);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);

        // Valido per tutti i campi double
        table.setDefaultRenderer(Double.class, new DecimalRenderer());

        // Rendo la tabella sortabile
        table.setAutoCreateRowSorter(true);

        // Setta il model e popola la tabella
        fillModelList();

        // 2. Forza il ricalcolo delle larghezze (IMPORTANTE: farlo dopo che la tabella ha i dati)
        java.awt.EventQueue.invokeLater(() -> {
            autoResizeColumns(table);
        });
    }

    protected void autoResizeColumns(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Larghezza minima iniziale

            // 1. Controlla la larghezza dell'Header
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(table,
                    columnModel.getColumn(column).getHeaderValue(), false, false, 0, column);
            width = Math.max(headerComp.getPreferredSize().width + 20, width); // +20 per padding

            // 2. Controlla la larghezza dei dati (limita a un numero di righe se la tabella è enorme)
            for (int row = 0; row < Math.min(table.getRowCount(), 100); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 20, width);
            }

            // Imposta la larghezza calcolata
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
}
