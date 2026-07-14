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

    // clausola WHERE (:param IS NULL OR colonna = :param)
    // Se l'utente nel form React clicca su "Cerca" lasciando la combo di Counterparty o Position vuota, 
    // il client invierà null. Questa specifica sintassi SQL permette a PostgreSQL di ignorare 
    // il filtro se il parametro è nullo, comportandosi come un filtro dinamico opzionale: 
    // se selezionata la controparte filtra, altrimenti mostra tutto.
    @Query(value = "SELECT pmd.code AS positionCode, "
            + "       md.code AS assetCode, "
            + "       md.description AS assetDescription, "
            + "       ctp.code AS counterpartyCode, "
            + "       (pd.buy_qty - pd.sell_qty) AS totalQuantity, "
            + "       COALESCE(((notional_value_buy - notional_value_sell) / NULLIF(pd.buy_qty - pd.sell_qty, 0)) / md.multiplier, 0) AS averagePrice, "
            + "       pd.market_price AS marketPrice, "
            + "       ((pd.buy_qty - pd.sell_qty) * (pd.market_price * md.multiplier)) AS marketValue, "
            + "       pd.realized_pnl AS realizedPnL, "
            + "       pd.unrealized_pnl AS unrealizedPnL "
            + "FROM position_detail pd "
            + "JOIN position_master_data pmd ON pd.position_md = pmd.id_position "
            + "JOIN master_data md ON pd.master_data = md.id_master_data "
            + "JOIN counterparty ctp ON pd.counterparty = ctp.id_counterparty "
            + "WHERE (:positionMdId IS NULL OR pd.position_md = :positionMdId) "
            + "  AND (:counterpartyId IS NULL OR pd.counterparty = :counterpartyId)"
            + "  AND (:assetClassId IS NULL OR md.asset_class = :assetClassId)",
            nativeQuery = true)
    List<Object[]> findPositionProspect(
            @Param("positionMdId") Integer positionMdId,
            @Param("counterpartyId") Integer counterpartyId,
            @Param("assetClassId") Integer assetClassId
    );

    @Query(value = """
    SELECT pd.id_position_detail AS id 
    FROM position_detail pd 
    JOIN position_master_data pmd ON pd.position_md = pmd.id_position 
    JOIN master_data md ON pd.master_data = md.id_master_data 
    JOIN counterparty ctp ON pd.counterparty = ctp.id_counterparty 
    WHERE (:positionMdId IS NULL OR pd.position_md = :positionMdId) 
      AND (:counterpartyId IS NULL OR pd.counterparty = :counterpartyId)
      AND (:assetClassId IS NULL OR md.asset_class = :assetClassId)
    """,
            nativeQuery = true)
    // Non torno Integer perchè Spring Data vede un tipo singolo non-entità, attiva un meccanismo interno di unwrap e 
    // conversione del tipo (Type Boxing/Unwrapping Analysis) che rallenta mortalmente la start in modalita debug
    List<Object[]> findPositionId(
            @Param("positionMdId") Integer positionMdId,
            @Param("counterpartyId") Integer counterpartyId,
            @Param("assetClassId") Integer assetClassId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
        @QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")}) // SKIP LOCKED su Postgres
    @Query("SELECT pd FROM PositionDetail pd "
            + "WHERE pd.positionMd = :pmdId "
            + "ORDER BY pd.idPositionDetail ASC")
    public List<PositionDetail> getAndLockByPositionMasterData(
            @Param("pmdId") Integer pmdId);

}
