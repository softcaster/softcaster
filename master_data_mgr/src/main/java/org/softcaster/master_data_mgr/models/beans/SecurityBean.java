/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.easy_pricer_core.data.SecurityMasterData;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class SecurityBean implements IMasterDataModel {

    private final SecurityMasterData smd;

    public SecurityBean(SecurityMasterData smd) {
        this.smd = smd;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                smd.getIsin();
            case 1 ->
                smd.getIssueDescription();
            case 2 ->
                smd.getIssueDate();
            case 3 ->
                smd.getIssuePrice();
            case 4 ->
                smd.getMaturityDate();
            case 5 ->
                smd.getRedempionPrice();
            case 6 ->
                smd.getInterestRate();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Isin", "Description", "Issue Date", "Issue Price", "Maturity", "Redemption Price", "Coupon"};
    }

}
