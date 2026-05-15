/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.Issuer;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class IssuerBean implements IMasterDataModel {

    private final Issuer issuer;

    public IssuerBean(Issuer issuer) {
        this.issuer = issuer;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                issuer.getShortIssuerName();
            case 1 ->
                issuer.getLongIssuerName();
            case 2 ->
                issuer.getCountry().getAlfa3Code();
             default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Short Name", "Long Name", "Country"};
    }

    /**
     * @return the portfolioMasterData
     */
    public Issuer getIssuer() {
        return issuer;
    }

}
