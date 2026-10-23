package org.softcaster.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Testata di un bond future (MBTP).
 */
@Entity
@Table(name = "bond_future_product")
@PrimaryKeyJoinColumn(name = "id_future_product")
@SuppressWarnings("PersistenceUnitPresent")
public class BondFutureProduct extends FutureProduct {

    private static final long serialVersionUID = 1L;
}
