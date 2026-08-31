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
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountResolver")
public class AccountResolverService {

    private static final Logger log = LoggerFactory.getLogger(AccountResolverService.class);

    @Autowired
    private AccountMappingDAO accountMappingDAO;

    // La cache thread-safe in memoria
    // Chiave: "MAPPINGKEY_CURRENCYID" (es. "FX_SPOT_CONTRACT_2"), Valore: Codice Conto (es. "130055")
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void initCache() {
        refreshCache();
    }

    /**
     * Svuota e ricarica la cache dal database. Può essere chiamato a run-time
     * tramite un endpoint REST se l'utente modifica la tabella.
     */
    public synchronized void refreshCache() {
        log.info("=== [AccountResolverService] Account Mapping cache loading in progress... ===");
        try {
            // Carichiamo tutti i record pre-associati in un'unica query ad alte prestazioni
            List<AccountMapping> allMappings = accountMappingDAO.findAllWithAssociations();

            // Puliamo la vecchia cache in modo atomico
            cache.clear();

            for (AccountMapping mapping : allMappings) {
                if (mapping.getCurrency() != null && mapping.getGlAccount() != null) {
                    // Costruiamo la chiave unica
                    String key = buildCacheKey(mapping.getMappingKey(), mapping.getCurrency().getIdCurrency());
                    // Otteniamo il codice del conto tramite l'anagrafica del GlAccount
                    String accountCode = mapping.getGlAccount().getCode();

                    cache.put(key, accountCode);
                }
            }
            log.info("=== [AccountResolverService] Cache successfully loaded. Inserted records: {} ===", cache.size());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to load account slot mapping cache!", e);
        }
    }

    /**
     * Metodo velocissimo invocato dallo script Groovy. Cerca SOLO in memoria.
     *
     * @param mappingKey
     * @param currencyId
     * @return
     */
    public String resolve(String mappingKey, int currencyId) {
        String key = buildCacheKey(mappingKey, currencyId);
        String accountCode = cache.get(key);

        if (accountCode == null) {
            String errorMsg = String.format("### ACCOUNTING ERROR: No General Ledger Account mapped for rule '%s' and currency ID '%d' in memory cache!",
                    mappingKey, currencyId);
            log.error(errorMsg);
            throw new AccountingException(errorMsg);
        }

        return accountCode;
    }

    private String buildCacheKey(String mappingKey, int currencyId) {
        return mappingKey.trim().toUpperCase() + "_" + currencyId;
    }
}
