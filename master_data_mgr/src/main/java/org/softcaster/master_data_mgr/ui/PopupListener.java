/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.master_data_mgr.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 *
 * @author Emy
 */
public class PopupListener extends MouseAdapter {

    private JPopupMenu popUp = null;

    public PopupListener(JPopupMenu popUp) {
        super();
        this.popUp = popUp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        showPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        showPopup(e);
    }

    private void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popUp.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
