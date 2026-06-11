/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import org.softcaster.provider.bondblox.BondBloxProvider;
import org.softcaster.provider.bondblox.RefDatum;
import org.softcaster.provider.enums.Market;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service("Bonds Usa")
public class SecurityUsdImportMgr implements IImportMgr {

    @Override
    public void start(IProgressInfo progressInfo) {
        BondBloxProvider provider = BondBloxProvider.getInstance();
        RefDatum value = provider.getRefDatum("US912810QL52", Market.BONDS);
        System.out.println(value.coupon);
        System.out.println(value.issueDate);
        System.out.println(value.maturityDate);
    }

    @Override
    public void terminate() {
    }
    
}
