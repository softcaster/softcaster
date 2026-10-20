package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrokerInstrumentRulesRepository extends JpaRepository<BrokerInstrumentRules, Integer> {

    public BrokerInstrumentRules findByBrokerRuleId(Integer brokerRuleId);

    /**
     * Seleziona tutte le regole tariffarie di un determinato Broker, caricando
     * in anticipo i dati dello strumento (MasterData) per evitare
     * LazyInitializationException.
     *
     * @param broker
     * @return
     */
    @Query("SELECT r FROM BrokerInstrumentRules r "
            + "JOIN FETCH r.masterData "
            + "WHERE r.broker = :broker "
            + "ORDER BY r.masterData.code ASC")
    List<BrokerInstrumentRules> findByBrokerWithMasterData(@Param("broker") Counterparty broker);

    /**
     * Variante alternativa: passa direttamente l'ID numerico
     * del broker invece dell'oggetto entità intero.
     *
     * @param brokerId
     * @return
     */
    @Query("SELECT r FROM BrokerInstrumentRules r "
            + "JOIN FETCH r.masterData "
            + "WHERE r.broker.idCounterparty = :brokerId "
            + "ORDER BY r.masterData.code ASC")
    List<BrokerInstrumentRules> findByBrokerIdWithMasterData(@Param("brokerId") Integer brokerId);
}
