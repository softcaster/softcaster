package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import org.softcaster.core.dto.PositionProspectDto;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<List<PositionDetail>> fetchAndClaimByPositionMasterData(PositionMasterData pmd) {

        // 1. Legge e blocca i dettagli scaduti, ignorando quelli bloccatida eventuale altro server
        List<PositionDetail> details = repository.getAndLockByPositionMasterData(pmd.getIdPosition());

        if (details.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(details);
    }

    @Transactional(readOnly = true)
    public List<PositionDetail> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Integer> findPositionId(Integer positionMdId, Integer counterpartyId, Integer assetClassId) {
        List<Object[]> rows = repository.findPositionId(positionMdId, counterpartyId, assetClassId);
        List<Integer> ids = rows.stream()
                .map(row -> (Integer) row[0])
                .toList();
        return ids;
    }

    @Transactional
    public PositionDetail saveOrUpdate(PositionDetail positionDetail) {
        return repository.save(positionDetail);
    }

    @Transactional
    public void delete(PositionDetail positionDetail) {
        repository.delete(positionDetail);
    }

    @Transactional(readOnly = true)
    public List<PositionProspectDto> getPositionProspect(Integer positionMdId, Integer counterpartyId, Integer assetClassId) {
        List<Object[]> rawResults = repository.findPositionProspect(positionMdId, counterpartyId, assetClassId);

        return rawResults.stream().map(row -> new PositionProspectDto(
                (String) row[0], // positionCode
                (String) row[1], // assetCode
                (String) row[2], // assetDescription
                (String) row[3], // counterpartyCode
                row[4] != null ? ((Number) row[4]).doubleValue() : 0.0, // totalQuantity
                row[5] != null ? ((Number) row[5]).doubleValue() : 0.0, // averagePrice
                row[6] != null ? ((Number) row[6]).doubleValue() : 0.0, // marketPrice
                row[7] != null ? ((Number) row[7]).doubleValue() : 0.0, // marketValue
                row[8] != null ? ((Number) row[8]).doubleValue() : 0.0, // realizedPnL
                row[9] != null ? ((Number) row[9]).doubleValue() : 0.0 // unrealizedPnL
        )).collect(Collectors.toList());
    }
}
