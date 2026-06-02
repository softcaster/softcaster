package org.softcaster.core.data;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface PositionDetailRepository extends JpaRepository<PositionDetail, Integer> {

    public PositionDetail findByIdPositionDetail(Integer idPositionDetail);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // Genera la "SELECT ... FOR UPDATE" su Postgres    
    // Optional permette di usare orElseGet o ifPresent
    public Optional<PositionDetail> findByPositionMdAndMasterDataAndCounterparty(Integer positionMd, Integer masterData, Integer counterparty);
}
