package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import org.softcaster.core.data.OtcOptionMasterData;

/**
 * Contratto di un'opzione OTC (OFX, OCM) per il client.
 */
public record OtcOptionDto(
        Integer masterDataId,
        String code,
        String description,
        String assetClass,
        String underlyingCode,
        String currency,
        String optionType,
        String exerciseStyle,
        String settlementType,
        Double strike,
        Double notional,
        LocalDate tradeDate,
        LocalDate expiryDate,
        LocalTime expiryTime,
        String expiryTimeZone,
        LocalDate deliveryDate,
        Integer spotLag) {

    public static OtcOptionDto of(OtcOptionMasterData omd) {
        return new OtcOptionDto(
                omd.getIdMasterData(),
                omd.getCode(),
                omd.getDescription(),
                omd.getAssetClass().getCode(),
                omd.getUnderlying().getCode(),
                omd.getCurrency().getIsoCode(),
                omd.getOptionType().getCode(),
                omd.getExerciseStyle().getCode(),
                omd.getSettlementType().getCode(),
                omd.getStrike(),
                omd.getNotional(),
                omd.getIssueDate() == null ? null : omd.getIssueDate().toLocalDate(),
                omd.getExpiryLocalDate(),
                omd.getExpiryTime(),
                omd.getExpiryTimeZone(),
                omd.getDeliveryLocalDate(),
                omd.getBusinessDays());
    }
}
