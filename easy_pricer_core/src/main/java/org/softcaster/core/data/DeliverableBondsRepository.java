package org.softcaster.core.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverableBondsRepository extends JpaRepository<DeliverableBonds, Integer> {

    public DeliverableBonds findByIdDeliverableBonds(Integer idDeliverableBonds);

    // Ritorna un Optional, un solo record o nessuno
    public Optional<DeliverableBonds> findByMasterDataAndIsin(Integer masterData, String isin);
}
