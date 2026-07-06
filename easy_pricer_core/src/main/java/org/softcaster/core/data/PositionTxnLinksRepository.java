package org.softcaster.core.data;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface PositionTxnLinksRepository extends JpaRepository<PositionTxnLinks, Integer> {

    public PositionTxnLinks findByPosTxnLinkId(Integer posTxnLinkId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // SKIP LOCKED su Postgres
    @Query("SELECT ptl FROM PositionTxnLinks ptl "
            // + "WHERE ptl.txnAcctPhase NOT IN :phases "
            + "WHERE ptl.txnAcctPhase <> AccountingPhase.OFFICIAL_POSTED "
            + "AND ptl.settlement <= :officialDate")
    public List<PositionTxnLinks> fetchAndClaimLinks(
            //@Param("phases") Collection<AccountingPhase> phases, -> se dovessi usare una collection
            @Param("officialDate") LocalDate officialDate);

}
