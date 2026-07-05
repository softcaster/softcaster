package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionTxnLinksRepository extends JpaRepository<PositionTxnLinks, Integer> {

    public PositionTxnLinks findByPosTxnLinkId(Integer posTxnLinkId);
}
