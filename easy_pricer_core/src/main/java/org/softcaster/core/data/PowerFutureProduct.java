package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.engine.enums.LoadType;

/**
 * Testata di un future sull'energia elettrica: profilo di carico (base,
 * peak). Il periodo di consegna dipende dalla scadenza
 * (CommodityDeliveryProfile).
 */
@Entity
@Table(name = "power_future_product")
@PrimaryKeyJoinColumn(name = "id_future_product")
@SuppressWarnings("PersistenceUnitPresent")
public class PowerFutureProduct extends CmdFutureProduct {

    private static final long serialVersionUID = 1L;

    @Convert(converter = LoadTypeConverter.class)
    @Column(name = "load_type")
    private LoadType loadType;

    public LoadType getLoadType() {
        return loadType;
    }

    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }
}
