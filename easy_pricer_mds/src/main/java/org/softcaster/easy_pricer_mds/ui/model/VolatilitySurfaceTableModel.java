package org.softcaster.easy_pricer_mds.ui.model;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.easy_pricer_mds.bean.VolatilitySurfaceBean;

/**
 * Superfici di volatilita' del batch delle volatilita'.
 */
public class VolatilitySurfaceTableModel extends FndtTableModel<VolatilitySurfaceBean> {

    public VolatilitySurfaceTableModel(VolatilitySurfaceBean prototype) {
        super(prototype);
    }

    @Override
    public void setData(List<VolatilitySurfaceBean> newData) {
        super.setData(newData); // Questo chiama fireTableDataChanged()
    }
}
