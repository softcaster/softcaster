/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.services;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.account.AccountMapping;
import org.softcaster.core.data.account.AccountMappingDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountResolver")
public class AccountResolverService {

    private static final Logger log = LoggerFactory.getLogger(AccountResolverService.class);

    @Autowired
    private AccountMappingDAO accountMappingDAO;

    // Cache thread-safe in memoria
    private final Map<String, Integer> cache = new ConcurrentHashMap<>();

    /**
     * Carica tutti i record dal database nella cache all'avvio
     * dell'applicazione.
     */
    @PostConstruct
    public void initCache() {
        refreshCache();
    }

    /**
     * Svuota e ricarica la cache dal database. Può essere chiamato a run-time
     * tramite un endpoint REST se l'utente modifica la tabella.
     */
    public synchronized void refreshCache() {
        log.info("=== [MMS] Account Mapping (Slots) cache loading in progress... ===");
        try {
            // Carichiamo tutti i record pre-associati in un'unica query ad alte prestazioni
            List<AccountMapping> allMappings = accountMappingDAO.findAllWithAssociations();

            cache.clear();

            for (AccountMapping mapping : allMappings) {
                if (mapping.getGlAccountSlot() != null) {
                    // Chiave geometrica della cache: "BOND_ASSET_2" o "CURRENCY_POSITION_1"
                    String key = mapping.getMappingKey().trim().toUpperCase() + "_" + mapping.getGlAccountSlot().getCurrency();

                    // VALORE: Salviamo direttamente l'ID dello slot fisico (account_slot_id)
                    Integer slotId = mapping.getGlAccountSlot().getAccountSlotId();

                    cache.put(key, slotId);
                }
            }
            log.info("=== [MMS] Slots Cache successfully loaded. Inserted records: {} ===", cache.size());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to load account slot mapping cache!", e);
        }
    }
}
