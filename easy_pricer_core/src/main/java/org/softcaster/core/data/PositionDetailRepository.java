package org.softcaster.core.data;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface PositionDetailRepository extends JpaRepository<PositionDetail, Integer> {

    public PositionDetail findByIdPositionDetail(Integer idPositionDetail);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // Genera la "SELECT ... FOR UPDATE" su Postgres    
    // Optional permette di usare orElseGet o ifPresent
    public Optional<PositionDetail> findByPositionMdAndMasterDataAndCounterparty(Integer positionMd, Integer masterData, Integer counterparty);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // SKIP LOCKED su Postgres
    @Query("SELECT pd FROM PositionDetail pd "
            + "WHERE pd.positionMd = :pmdId "
            + "AND pd.lastMtmExecuted < :thresholdTime "
            + "ORDER BY pd.idPositionDetail ASC")
    public List<PositionDetail> getAndLockByPositionMasterDataAndInterval(
            @Param("pmdId") Integer pmdId,
            @Param("thresholdTime") LocalDateTime thresholdTime);
}
