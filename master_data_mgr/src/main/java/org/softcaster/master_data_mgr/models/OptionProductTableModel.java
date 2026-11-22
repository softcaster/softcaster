package org.softcaster.master_data_mgr.models;

import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.master_data_mgr.models.beans.OptionProductBean;

/**
 * Lista delle testate delle opzioni quotate.
 */
public class OptionProductTableModel extends FndtTableModel<OptionProductBean> {

    public OptionProductTableModel(OptionProductBean prototype) {
        super(prototype);
    }
}
