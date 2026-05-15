package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CashFlowItemRepository extends JpaRepository<CashFlowItem, Integer> {

    public CashFlowItem findByIdCashFlowItem(Integer idCashFlowItem);
}
