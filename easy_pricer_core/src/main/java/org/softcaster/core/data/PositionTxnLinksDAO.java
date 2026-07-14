package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("positionTxnLinksDAO")
public class PositionTxnLinksDAO {

    private final PositionTxnLinksRepository repository;

    // Iniezione tramite costruttore (Best Practice per Spring)
    public PositionTxnLinksDAO(PositionTxnLinksRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PositionTxnLinks findByPosTxnLinkId(Integer posTxnLinkId) {
        return repository.findByPosTxnLinkId(posTxnLinkId);
    }

    @Transactional
    public PositionTxnLinks saveOrUpdate(PositionTxnLinks positionTxnLinks) {
        return repository.save(positionTxnLinks);
    }

    @Transactional
    public void delete(PositionTxnLinks positionTxnLinks) {
        repository.delete(positionTxnLinks);
    }

    @Transactional // Transactional mantiene attivo il lock su Postgres
    public List<PositionTxnLinks> fetchAndClaimLinks(LocalDate officialDate) {
        return repository.fetchAndClaimLinks(officialDate);
    }

    @Transactional(readOnly = true)
    public Integer findMasterDataIdByTxnLinkId(Integer posTxnLinkId) {
        if (posTxnLinkId == null) {
            return null;
        }

        // Ritorna l'id se presente, altrimenti null (cardinalità singola)
        return repository.findMasterDataIdByTxnLinkIdNative(posTxnLinkId)
                .orElse(null);
    }
}
