/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.FltSecurityMasterData;

/**
 *
 * @author ep
 */
public class FltSecurityBean extends SecurityBean {

    private final FltSecurityMasterData smd;

    public FltSecurityBean(FltSecurityMasterData smd) {
        super(smd);
        this.smd = smd;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                smd.getIsin();
            case 1 ->
                smd.getDescription();
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
    
    @Override
    public FltSecurityMasterData getSecurityMasterData() {
        return smd;
    }

}
