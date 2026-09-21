/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data.account;

// nel core, stesso stile di AccountMappingDAO

import java.util.Optional;
import org.softcaster.core.data.AssetClass;
import org.softcaster.engine.enums.EventType;
import org.springframework.stereotype.Repository;

@Repository
public class AssetClassScriptMappingDAO {

    private final AssetClassScriptMappingRepository repository;

    public AssetClassScriptMappingDAO(AssetClassScriptMappingRepository mappingRepository) {
        this.repository = mappingRepository;
    }
    
    public Optional<AssetClassScriptMapping> findByAssetClassAndEventType(AssetClass assetClass, EventType eventType) {
        return repository.findByAssetClassAndEventType(assetClass, eventType);
    }

    public Optional<AssetClassScriptMapping> findByAssetClassAndEventTypeIsNull(AssetClass assetClass) {
        return repository.findByAssetClassAndEventTypeIsNull(assetClass);
    }
}