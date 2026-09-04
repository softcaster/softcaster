package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CounterpartyRepository extends JpaRepository<Counterparty, Integer> {

    @Query("SELECT c FROM Counterparty c LEFT JOIN FETCH c.roles WHERE c.idCounterparty = :idCounterparty")
    public Counterparty findByIdCounterparty(Integer idCounterparty);

    // Con la FETCH JOIN (SELECT DISTINCT c FROM Counterparty c LEFT JOIN FETCH c.roles), 
    // PostgreSQL esegue una sola query atomica sul disco, unendo i dati tramite l'algoritmo 
    // nativo di JOIN del database e restituendo tutto istantaneamente. Molto più efficiente
    // che usare EAGER (n+1 query). Prima select di tutte le controparti, poi per ogni controparte
    // select dei roles. 
    @Query("SELECT DISTINCT c FROM Counterparty c LEFT JOIN FETCH c.roles LEFT JOIN FETCH c.country")
    public List<Counterparty> findAllWithRoles(Sort sort);

    public Counterparty findByCode(String code);
}
