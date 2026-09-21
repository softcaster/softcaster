/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.softcaster.core.data.AssetClass;
import org.softcaster.core.data.converters.AcctEventTypeConverter;
import org.softcaster.engine.enums.EventType;

@Entity
@Table(name = "asset_class_script_mapping")
public class AssetClassScriptMapping implements Serializable {

    @Id
    @SequenceGenerator(name = "asset_class_script_mapping_seq", sequenceName = "asset_class_script_mapping_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asset_class_script_mapping_seq")
    @Column(name = "asset_class_script_mapping_id")
    private Integer idAssetClassScriptMapping;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_class", nullable = false)
    private AssetClass assetClass;   // stesso tipo usato in MasterData, non piu' String

    @Convert(converter = AcctEventTypeConverter.class)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "script_code", length = 16, nullable = false)
    private String scriptCode;

    protected AssetClassScriptMapping() {
    }

    public AssetClassScriptMapping(AssetClass assetClass, EventType eventType, String scriptCode) {
        this.assetClass = assetClass;
        this.eventType = eventType;
        this.scriptCode = scriptCode;
    }

    public Integer getIdAssetClassScriptMapping() { return idAssetClassScriptMapping; }
    public AssetClass getAssetClass() { return assetClass; }
    public EventType getEventType() { return eventType; }
    public String getScriptCode() { return scriptCode; }
}