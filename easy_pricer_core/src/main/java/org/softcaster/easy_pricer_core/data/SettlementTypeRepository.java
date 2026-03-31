package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementTypeRepository extends JpaRepository<SettlementType, Integer> {

    public SettlementType findByIdSettlementType(Integer idSettlementType);
}
