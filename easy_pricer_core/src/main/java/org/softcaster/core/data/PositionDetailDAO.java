package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("positionDetailDAO")
public class PositionDetailDAO {

    @Resource
    private PositionDetailRepository repository;

    @Transactional(readOnly = true)
    public PositionDetail findByIdPositionDetail(Integer idPositionDetail) {
        return repository.findByIdPositionDetail(idPositionDetail);
    }

    @Transactional
    public Optional<PositionDetail> findByPositionMdAndMasterDataAndCounterparty(Integer positionMd,
            Integer masterData,
            Integer counterparty) {
        return repository.findByPositionMdAndMasterDataAndCounterparty(positionMd,
                masterData,
                counterparty);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<List<PositionDetail>> fetchAndClaimByPositionMasterData(PositionMasterData pmd, int intervalSeconds) {
        // Calcoliamo la soglia temporale: es. "ora meno 15 secondi"
        LocalDateTime thresholdTime = LocalDateTime.now().minusSeconds(intervalSeconds);

        // 1. Legge e blocca i dettagli scaduti, ignorando quelli bloccatida eventuale altro server
        List<PositionDetail> details = repository.getAndLockByPositionMasterDataAndInterval(pmd.getIdPosition(), thresholdTime);

        if (details.isEmpty()) {
            return Optional.empty();
        }

        // 2. CRITICO PER IL CLUSTER: Aggiorna immediatamente il timestamp a "adesso"
        // per prenotare il record prima del commit. L'altro (eventuale) server non vedrà più queste righe.
        LocalDateTime now = LocalDateTime.now();
        for (PositionDetail pd : details) {
            pd.setLastMtmExecuted(now);
            repository.save(pd);
        }

        return Optional.of(details);
    }

    @Transactional(readOnly = true)
    public List<PositionDetail> findAll() {
        return repository.findAll();
    }

    @Transactional
    public PositionDetail saveOrUpdate(PositionDetail positionDetail) {
        return repository.save(positionDetail);
    }

    @Transactional
    public void delete(PositionDetail positionDetail) {
        repository.delete(positionDetail);
    }

}
