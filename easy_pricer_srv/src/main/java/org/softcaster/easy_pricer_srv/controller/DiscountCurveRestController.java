package org.softcaster.easy_pricer_srv.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.SpreadCurveEntity;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntity;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_srv.dto.DiscountCurveDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Curve di sconto per le combo dei client: yield_curve e spread_curve
 * insieme, con lo stesso codice usato dai calcolatori
 * (MarketDataService.getDiscountCurve).
 */
@RestController
public class DiscountCurveRestController {

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private YieldCurveEntityDAO yieldCurveEntityDAO;
    @Autowired
    private SpreadCurveEntityDAO spreadCurveEntityDAO;

    @GetMapping("/discount_curve/r01")
    public ResponseEntity<List<DiscountCurveDto>> findAll() {
        return ResponseEntity.ok(transactionTemplate.execute(status -> {
            List<DiscountCurveDto> curves = new ArrayList<>();
            for (YieldCurveEntity yc : yieldCurveEntityDAO.findAll()) {
                curves.add(new DiscountCurveDto(yc.getCode(), yc.getDescription(), "YIELD", isoCode(yc.getCurrency())));
            }
            // valuta della curva base
            for (String code : spreadCurveEntityDAO.findNames()) {
                SpreadCurveEntity sc = spreadCurveEntityDAO.findByCodeWithBase(code);
                curves.add(new DiscountCurveDto(sc.getCode(), sc.getDescription(), "SPREAD", isoCode(sc.getBaseCurve().getCurrency())));
            }
            curves.sort(Comparator.comparing(DiscountCurveDto::code));
            return curves;
        }));
    }

    private static String isoCode(Currency currency) {
        return currency == null ? null : currency.getIsoCode();
    }
}
