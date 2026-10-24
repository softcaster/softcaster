package org.softcaster.master_data_mgr.models;

import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.master_data_mgr.models.beans.OptionBean;

/**
 * Lista delle serie di opzioni.
 */
public class OptionTableModel extends FndtTableModel<OptionBean> {

    public OptionTableModel(OptionBean prototype) {
        super(prototype);
    }
}