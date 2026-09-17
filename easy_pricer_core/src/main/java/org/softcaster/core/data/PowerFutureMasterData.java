/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.engine.enums.LoadType;

@Entity
@Table(name = "power_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public class PowerFutureMasterData extends ExtendedDeliveryFutureMasterData {

    @Convert(converter = LoadTypeConverter.class)
    @Column(name = "load_type", nullable = false)
    private LoadType loadType;

    /**
     * @return the loadType
     */
    public LoadType getLoadType() {
        return loadType;
    }

    /**
     * @param loadType the loadType to set
     */
    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }
}