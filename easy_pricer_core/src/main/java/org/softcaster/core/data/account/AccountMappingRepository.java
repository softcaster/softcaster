package org.softcaster.core.data.account;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface AccountMappingRepository extends JpaRepository<AccountMapping, Integer> {

    public AccountMapping findByAccountMappingId(Integer accountMappingId);

    /**
     * 1. QUERY NATIVA Recupera il codice del conto passando
     * attraverso la tabella intermedia degli slot (gl_account_slots).
     */
    @Query(value = """
        SELECT g.code 
        FROM account_mapping am
        JOIN gl_account_slots gas ON am.account_slot = gas.account_slot_id
        JOIN gl_accounts g ON gas.account = g.account_id
        WHERE am.mapping_key = :key AND gas.currency = :ccy
    """, nativeQuery = true)
    public String findAccount(@Param("key") String mappingKey, @Param("ccy") int currencyId);

    /**
     * 2. QUERY JPQL (Late Binding / Inizializzazione Cache) Carica
     * tutti i mapping effettuando una JOIN FETCH immediata sullo slot
     * (accountSlot) e, a cascata, sul conto base (glAccount) ad essa associato.
     * Questo risolve a monte le eccezioni di tipo LazyInitializationException.
     */
    @Query("""
        SELECT am 
        FROM AccountMapping am 
        JOIN FETCH am.glAccountSlot s
        JOIN FETCH s.account g
    """)
    public List<AccountMapping> findAllWithAssociations();
}
