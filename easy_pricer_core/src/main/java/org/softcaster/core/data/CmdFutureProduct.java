package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import org.softcaster.core.data.converters.CommodityTypeConverter;
import org.softcaster.engine.enums.CommodityType;

/**
 * Testata di un future su commodity: tipo di commodity.
 */
@Entity
@Table(name = "cmd_future_product")
@PrimaryKeyJoinColumn(name = "id_future_product")
@SuppressWarnings("PersistenceUnitPresent")
public class CmdFutureProduct extends FutureProduct {

    private static final long serialVersionUID = 1L;

    @Convert(converter = CommodityTypeConverter.class)
    @Column(name = "commodity_type")
    private CommodityType commodityType;

    public CommodityType getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(CommodityType commodityType) {
        this.commodityType = commodityType;
    }
}
