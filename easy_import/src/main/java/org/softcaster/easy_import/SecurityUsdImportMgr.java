/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.AssetClassDAO;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.Issuer;
import org.softcaster.core.data.IssuerDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.engine.enums.AccrualScheduleType;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Form;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.RollConvention;
import org.softcaster.engine.enums.TypeOfInterest;
import org.softcaster.provider.bondblox.BondBloxProvider;
import org.softcaster.provider.bondblox.RefDatum;
import org.softcaster.provider.enums.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service("Bonds Usa")
public class SecurityUsdImportMgr implements IImportMgr {

    @Autowired
    SecurityMasterDataDAO securityMasterDataDAO;
    @Autowired
    AssetClassDAO assetClassDAO;
    @Autowired
    CurrencyDAO currencyDAO;
    @Autowired
    IssuerDAO issuerDAO;

    SecurityMasterData securityMasterData = null;

    private final DaycountBasis daycount = DaycountBasis.ACT_ACT_ICMA;
    private final DaycountBasis accrualDaycount = DaycountBasis.ACT_365;
    private final AmortizationSchedule amortizationSchedule = AmortizationSchedule.IOL;
    private final AccrualScheduleType accrualScheduleType = AccrualScheduleType.NONE;
    private final Form form = Form.BEARER;
    private final RollConvention rollConvention = RollConvention.UNADJUSTED;

    private void fillAndSaveSecurityMasterData(RefDatum refDatum) {
        securityMasterData = securityMasterDataDAO.findByIsin(refDatum.isin);
        if (securityMasterData == null) {
            createEmptySecurityMasterData();
            securityMasterData.setCode(refDatum.isin);
            securityMasterData.setIsin(refDatum.isin);
            securityMasterData.setLei("");
            securityMasterData.setCfiCode("");
            securityMasterData.setFisn("");
            securityMasterData.setDescription(getDescription(refDatum));
            securityMasterData.setMultiplier(0.01);
            securityMasterData.setBusinessDays(2);
            securityMasterData.setRollConvention(rollConvention);
        }
        securityMasterData.setFrequency(getFrequency(refDatum));
        securityMasterData.setTypeOfInterest(getTypeOfInterest(refDatum));
        securityMasterData.setInterestRate(getCoupon(refDatum));
        securityMasterData.setIssuePrice(refDatum.issuePrice);
        securityMasterData.setRedempionPrice(getRedempionPrice(refDatum));
        securityMasterData.setNominalValue(getRedempionPrice(refDatum));
        securityMasterData.setIssueDate(getIssueDate(refDatum));
        securityMasterData.setMaturityDate(getMaturityDate(refDatum));

        securityMasterData.setIssuer(getDefaultIssuer());
        securityMasterData.setCurrency(getCurrency(refDatum));
        securityMasterData.setFirstCouponPaymentDate(getFirstCouponDate(refDatum));
        securityMasterData.setFirstCouponRate(getFirstCouponValue(refDatum));
        
        securityMasterDataDAO.saveOrUpdate(securityMasterData);
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        BondBloxProvider provider = BondBloxProvider.getInstance();
        RefDatum refDatum = provider.getRefDatum("US912810QL52", Market.BONDS);

        fillAndSaveSecurityMasterData(refDatum);
    }

    @Override
    public void terminate() {
    }

    private void createEmptySecurityMasterData() {
        securityMasterData = new SecurityMasterData();
        securityMasterData.setIdMasterData(0);
        securityMasterData.setDaycount(daycount);
        securityMasterData.setAccrualDaycount(accrualDaycount);
        securityMasterData.setAmortizationSchedule(amortizationSchedule);
        securityMasterData.setAccrualScheduleType(accrualScheduleType);
        securityMasterData.setForm(form);
        securityMasterData.setAssetClass(assetClassDAO.findByCode("XRN"));
    }

    private Issuer getDefaultIssuer() {
        return issuerDAO.findByShortIssuerName("USDT");
    }

    private Currency getCurrency(RefDatum refDatum) {
        return currencyDAO.findByIsoCode(refDatum.issuedCurrency);
    }

    private Frequency getFrequency(RefDatum refDatum) {
        return switch (refDatum.cpnFreq) {
            case 1 ->
                Frequency.ANNUAL;
            default ->
                Frequency.SEMI_ANNUAL;
        };
    }

    private TypeOfInterest getTypeOfInterest(RefDatum refDatum) {
        return TypeOfInterest.FIXED;
    }

    private String getDescription(RefDatum refDatum) {
        return refDatum.issuerNameBold + " " + refDatum.coupon + " " + refDatum.maturityDate;
    }

    private Double getCoupon(RefDatum refDatum) {
        String[] result = refDatum.coupon.split("%");
        if (result[0] != null) {
            try {
                double coupon = Converter.toDouble(result[0], false);
                return coupon;
            } catch (ParseException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return 0.;
            }
        } else {
            return 0.;
        }
    }

    private java.sql.Date getIssueDate(RefDatum refDatum) {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ld = LocalDate.parse(refDatum.issueDate, parser);
        return java.sql.Date.valueOf(ld);
    }

    private java.sql.Date getFirstCouponDate(RefDatum refDatum) {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ld = LocalDate.parse(refDatum.issueDate, parser);
        ld = ld.plusMonths(6);
        return java.sql.Date.valueOf(ld);
    }

    private Double getFirstCouponValue(RefDatum refDatum) {
        return getCoupon(refDatum) / 2.;
    }

    private java.sql.Date getMaturityDate(RefDatum refDatum) {
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ld = LocalDate.parse(refDatum.maturityDate, parser);
        return java.sql.Date.valueOf(ld);
    }

    private Double getRedempionPrice(RefDatum refDatum) {
        try {
            double redempionPrice = Converter.toDouble(refDatum.redemptionValue, false);
            return redempionPrice;
        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return 0.;
        }
    }
}
