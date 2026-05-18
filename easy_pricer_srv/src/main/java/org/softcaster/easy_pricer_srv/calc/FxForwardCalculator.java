/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.engine.analytics.FxForwardPricer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author softc
 */
@Service("fxForwardCalculator")
public class FxForwardCalculator {

    @Autowired
    private CurrencyDAO dao;

    @Autowired
    @Qualifier("fxFwdPricer")
    private FxForwardPricer fxForwardPricer;

    /*
    public ForexFwdOutputData forexFwdValuation(ForexFwdPriceRequest request) {

        ForexFwdOutputData output = null;

        ForexFwdInputData input = new ForexFwdInputData();
        input.setRate(request.getBcyRate());
        input.setRateCcy(request.getCcyRate());
        input.setCompounding(Compounding.SIMPLE);
        input.setDaycount(DayCountBasis.ACT_ACT);
        input.setMaturityDate(request.getMaturityDate());
        input.setSpotPrice(request.getReferencePrice());

        Currency bcy = dao.findByIsoCode(request.getIsin());
        input.setDaycount(DayCountHelper.decode(bcy.getDaycount().getCode()));

        Currency ccy = dao.findByIsoCode(request.getCcy());
        input.setDaycountCcy(DayCountHelper.decode(ccy.getDaycount().getCode()));

        List<Calendar> calendars = new ArrayList<>();
        calendars.add(bcy.getCalendar());
        calendars.add(ccy.getCalendar());

        input.setSettlementDate(CalendarHelper.getNextBusinessDate(request.getReferenceDate(),
                calendars, bcy.getBusinessDays()));

        output = calculator.valuation(input);

        return output;
    }
*/
}
