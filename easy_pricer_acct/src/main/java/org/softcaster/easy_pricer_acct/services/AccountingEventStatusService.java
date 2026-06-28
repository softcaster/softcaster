/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountingEventStatusService {

    private static final Logger log = LoggerFactory.getLogger(AccountingEventStatusService.class);

    @Autowired
    private AccountingEventDAO accountingEventDAO;

    /**
     * Essendo in una classe separata, Spring intercetta CORRETTAMENTE la chiamata,
     * apre una nuova transazione isolata su disco, aggiorna a FAILED e fa il commit.
     * @param eventId
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markEventAsFailed(Integer eventId) {
        try {
            log.info("[STATUS-SERVICE] Forcing status to FAILED for event ID: {}", eventId);
            AccountingEvent freshEvent = accountingEventDAO.findByEventId(eventId);
            
            if (freshEvent != null) {
                freshEvent.setEventStatus(AccountingEventStatus.FAILED);
                accountingEventDAO.saveOrUpdate(freshEvent);
                log.info("[STATUS-SERVICE] Event ID {} marked as FAILED and committed to DB.", eventId);
            }
        } catch (Exception e) {
            log.error("[STATUS-SERVICE] CRITICAL: Failed to write FAILED status for ID: " + eventId, e);
        }
    }
}
