package org.softcaster.easy_pricer_core.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionDetailRepository extends JpaRepository<PositionDetail, Integer> {

    public PositionDetail findByIdPositionDetail(Integer idPositionDetail);

    // Optional permette di usare orElseGet o ifPresent
    public Optional<PositionDetail> findByPositionMdAndMasterDataAndCounterparty(Integer positionMd, Integer masterData, Integer counterparty);
}
