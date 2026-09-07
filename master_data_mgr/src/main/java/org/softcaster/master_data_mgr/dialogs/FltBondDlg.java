/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.dialogs;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.AssetClass;
import org.softcaster.core.data.FltSecurityMasterData;
import org.softcaster.core.data.RefRateIndex;
import org.softcaster.engine.enums.CouponProjectionMethod;
import org.softcaster.master_data_mgr.MasterDataFacade;
import org.softcaster.master_data_mgr.models.beans.FltSecurityBean;

/**
 *
 * @author ep
 */
public class FltBondDlg extends BondDlg {

    public FltBondDlg(java.awt.Frame parent, boolean modal, FltSecurityBean bean, MasterDataFacade masterDataFacade) {
        super(parent, modal, bean, masterDataFacade);
        postInit();
    }

    @Override
    protected void postInit() {
        super.postInit();
        setUpFltCombos();
    }

    @Override
    protected void removeTabIndex() {
    }

    private void setUpFltCombos() {
        setUpIndexCombo();
        setUpProjectionCombo();
    }

    private void setUpIndexCombo() {
        List<RefRateIndex> indexes = masterDataFacade.getRefRateIndexDAO().findAll();

        // 2. Crea il modello partendo dalla lista
        DefaultComboBoxModel<RefRateIndex> model = new DefaultComboBoxModel<>(indexes.toArray(RefRateIndex[]::new));
        cfRefIndex.setModel(model);
    }

    private void setUpProjectionCombo() {
        List<CouponProjectionMethod> projectionMethods = List.of(CouponProjectionMethod.values());

        // Crea il modello partendo dalla lista
        DefaultComboBoxModel<CouponProjectionMethod> model = new DefaultComboBoxModel<>(projectionMethods.toArray(CouponProjectionMethod[]::new));
        cbProjectiooMethod.setModel(model);
    }

    @Override
    protected AssetClass getAssetClass() {
        return masterDataFacade.findAssetClass("FRB");
    }

    private FltSecurityBean getFltSecurityBean() {
        if (isInsert) {
            bean = new FltSecurityBean(new FltSecurityMasterData());
            fillDefaultFields();
            return (FltSecurityBean) bean;
        } else {
            if (bean instanceof FltSecurityBean fltSecurityBean) {
                if (!fillSecurityMasterData(fltSecurityBean.getSecurityMasterData())) {
                    return null;
                }
                return fltSecurityBean;
            }
        }
        return null;
    }

    @Override
    protected boolean saveBean() {

        try {

            FltSecurityMasterData smd = getFltSecurityBean().getSecurityMasterData();
            if (!fillSecurityMasterData(smd)) {
                return false;
            }

            RefRateIndex index = (RefRateIndex) cfRefIndex.getSelectedItem();
            smd.setRefRateIndex(index);
            smd.setSpread(Converter.toDouble(txtSpread.getText(), false));
            smd.setCouponPm((CouponProjectionMethod) cbProjectiooMethod.getSelectedItem());

            masterDataFacade.getFltSecurityMasterDataDAO().saveOrUpdate(smd);
            return true;
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return false;
        }
    }

}
