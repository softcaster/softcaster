/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.core.data.account;

import java.util.Optional;
import org.softcaster.core.data.AssetClass;
import org.softcaster.engine.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetClassScriptMappingRepository extends JpaRepository<AssetClassScriptMapping, Integer> {
    Optional<AssetClassScriptMapping> findByAssetClassAndEventType(AssetClass assetClass, EventType eventType);
    Optional<AssetClassScriptMapping> findByAssetClassAndEventTypeIsNull(AssetClass assetClass);
}