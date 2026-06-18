/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author softc
 */
public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new GridBagLayout()); // Per centrare tutto perfettamente
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Icona grande 
        URL logoUrl = getClass().getResource("/images/lizard.png"); 
        if (logoUrl != null) {
            JLabel logoLabel = new JLabel(new ImageIcon(logoUrl));
            add(logoLabel, gbc);
            gbc.gridy++;
        }

        // 2. Titolo Benvenuto
        JLabel title = new JLabel("Welcome to Market Data Service");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(70, 70, 70));
        add(title, gbc);

        // 3. Sottotitolo Istruzioni
        gbc.gridy++;
        JLabel subtitle = new JLabel("Please select an item from the tree on the left to start.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        add(subtitle, gbc);

        // 4. Info di sistema (opzionale)
        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 0, 0);
        JLabel version = new JLabel("Version 1.0 - by Softcaster Financial Engineering");
        version.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        add(version, gbc);
    }
}
