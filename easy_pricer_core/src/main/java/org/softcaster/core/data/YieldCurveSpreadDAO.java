package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Z-spread calibrati delle spread curve.
 *
 * yield_curve_spread contiene solo la calibrazione corrente di ogni curva,
 * senza data: la data e' in testata (spread_curve.as_of_date). Le
 * calibrazioni precedenti stanno in yield_curve_spread_hist con la loro data.
 * Le letture a una data usano la corrente se non e' successiva alla data,
 * altrimenti l'ultima storica non successiva.
 */
@Component("yieldCurveSpreadDAO")
public class YieldCurveSpreadDAO {

    protected final YieldCurveSpreadRepository repository;
    protected final YieldCurveSpreadHistRepository histRepository;

    public YieldCurveSpreadDAO(YieldCurveSpreadRepository repository, YieldCurveSpreadHistRepository histRepository) {
        this.repository = repository;
        this.histRepository = histRepository;
    }

    /**
     * Calibrazione di una data: con data uguale o successiva alla corrente
     * diventa la corrente (una corrente piu' vecchia va prima nello storico),
     * con data precedente sostituisce la calibrazione storica di quella data.
     */
    @Transactional
    public void replaceCalibration(Integer spreadCurveId, LocalDate asOfDate, List<YieldCurveSpread> rows) {
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("No z-spreads to save");
        }
        for (YieldCurveSpread r : rows) {
            if (!spreadCurveId.equals(r.getSpreadCurveId())) {
                throw new IllegalArgumentException("Z-spread of another spread curve: " + r.getSpreadCurveId());
            }
        }
        Optional<LocalDate> current = repository.findAsOfDate(spreadCurveId);
        if (current.isPresent() && asOfDate.isBefore(current.get())) {
            histRepository.deleteByAsOfDate(spreadCurveId, asOfDate);
            histRepository.saveAll(rows.stream().map(r -> YieldCurveSpreadHist.of(r, asOfDate)).toList());
            return;
        }
        if (current.isPresent() && asOfDate.isAfter(current.get())) {
            LocalDate archived = current.get();
            histRepository.deleteByAsOfDate(spreadCurveId, archived);
            histRepository.saveAll(repository.findByCurve(spreadCurveId).stream()
                    .map(r -> YieldCurveSpreadHist.of(r, archived)).toList());
            histRepository.flush();
        }
        repository.deleteByCurve(spreadCurveId);
        repository.saveAll(rows);
        repository.updateAsOfDate(spreadCurveId, asOfDate);
        rows.forEach(r -> r.setAsOfDate(asOfDate));
    }

    /**
     * Z-spread dell'ultima calibrazione non successiva a date (vuoto se non ce
     * ne sono); getAsOfDate() da' la data della calibrazione.
     */
    @Transactional(readOnly = true)
    public List<? extends AbstractYieldCurveSpread> findLatest(Integer spreadCurveId, LocalDate date) {
        Optional<LocalDate> current = repository.findAsOfDate(spreadCurveId);
        if (current.isPresent() && !current.get().isAfter(date)) {
            List<YieldCurveSpread> rows = repository.findByCurve(spreadCurveId);
            rows.forEach(r -> r.setAsOfDate(current.get()));
            return rows;
        }
        return histRepository.findLastAsOfDate(spreadCurveId, date)
                .map(last -> histRepository.findByAsOfDate(spreadCurveId, last))
                .orElse(List.of());
    }
}
