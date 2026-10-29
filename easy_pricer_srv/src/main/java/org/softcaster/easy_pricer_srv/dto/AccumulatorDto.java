package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.AccumulatorFixing;
import org.softcaster.core.data.AccumulatorMasterData;

/**
 * Contratto accumulator per il client: termini e, nel dettaglio, calendario
 * dei fixing.
 */
public record AccumulatorDto(
        Integer masterDataId,
        String code,
        String description,
        String assetClass,
        String underlyingCode,
        String currency,
        String accumulatorType,
        String settlementType,
        String settlementFrequency,
        Double strike,
        Double koBarrier,
        String koObservation,
        Double kiBarrier,
        String kiEffect,
        Double leverage,
        Double dailyQuantity,
        Integer guaranteedFixings,
        Boolean koAccrual,
        LocalDate issueDate,
        LocalDate firstFixingDate,
        LocalDate lastFixingDate,
        LocalDate maturityDate,
        LocalDate koDate,
        LocalDate kiDate,
        int totalFixings,
        double maxQuantity,
        List<Fixing> fixings) {

    public record Fixing(LocalDate fixingDate, LocalDate settlementDate, Double fixingPrice, Double accumulatedQuantity) {
    }

    /**
     * Da chiamare in transazione (sottostante, valuta e fixing LAZY).
     */
    public static AccumulatorDto of(AccumulatorMasterData amd, boolean withFixings) {
        List<Fixing> fixings = withFixings
                ? amd.getFixings().stream().map(AccumulatorDto::fixing).toList()
                : List.of();
        return new AccumulatorDto(
                amd.getIdMasterData(),
                amd.getCode(),
                amd.getDescription(),
                amd.getAssetClass().getCode(),
                amd.getUnderlying().getCode(),
                amd.getCurrency().getIsoCode(),
                amd.getAccumulatorType().getCode(),
                amd.getSettlementType().getCode(),
                amd.getSettlementFrequency().getCode(),
                amd.getStrike(),
                amd.getKoBarrier(),
                amd.getKoObservation().getCode(),
                amd.getKiBarrier(),
                amd.getKiEffect() == null ? null : amd.getKiEffect().getCode(),
                amd.getLeverage(),
                amd.getDailyQuantity(),
                amd.getGuaranteedFixings(),
                amd.getKoAccrual(),
                local(amd.getIssueDate()),
                amd.getFirstFixingLocalDate(),
                amd.getLastFixingLocalDate(),
                local(amd.getMaturityDate()),
                local(amd.getKoDate()),
                local(amd.getKiDate()),
                amd.getFixings().size(),
                amd.getMaxQuantity(),
                fixings);
    }

    private static Fixing fixing(AccumulatorFixing f) {
        return new Fixing(f.getFixingLocalDate(), f.getSettlementLocalDate(), f.getFixingPrice(), f.getAccumulatedQuantity());
    }

    private static LocalDate local(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
