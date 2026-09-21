/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import java.util.Optional;
import org.softcaster.core.data.AssetClass;
import org.softcaster.core.data.account.AssetClassScriptMapping;
import org.softcaster.core.data.account.AssetClassScriptMappingDAO;
import org.softcaster.engine.enums.EventType;
import org.springframework.stereotype.Service;

@Service("accountingScriptResolver")
public class AccountingScriptResolver {

    private final AssetClassScriptMappingDAO mappingDAO;

    public AccountingScriptResolver(AssetClassScriptMappingDAO mappingDAO) {
        this.mappingDAO = mappingDAO;
    }

    public String resolveScriptCode(AssetClass assetClass, EventType eventType) {
        Optional<AssetClassScriptMapping> specific = mappingDAO.findByAssetClassAndEventType(assetClass, eventType);
        if (specific.isPresent()) {
            return specific.get().getScriptCode();
        }

        Optional<AssetClassScriptMapping> wildcard = mappingDAO.findByAssetClassAndEventTypeIsNull(assetClass);
        if (wildcard.isPresent()) {
            return wildcard.get().getScriptCode();
        }

        return assetClass.getCode(); // fallback finale: il code testuale (es. "XRB"), non l'id numerico
    }
}
