/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.futures;

import org.softcaster.commons.utils.NumberUtils;
import ph.alephzero.finance.products.forward.BondForward;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @author softc
 */
public class BtpMiniFuture extends BondForward {

    private double positionValue(BtpMiniFutureInputData input) {
        double positionValue = 0;
        positionValue = input.getTickValue() * (input.getFuturePrice() - input.getInvoicePrice());
        return positionValue;
    }

    private double irr(BtpMiniFutureInputData input) {
        double r = 0.;
        double capitalizedCoupon = getCapitalizedCoupon(input.getSettlementDate(), input.getMaturityDate(), input.getUnderliyngCashFlows(), input.getRate());
        double accrual = getAccrual(input.getSettlementDate(), input.getMaturityDate(), input.getUnderliyngCashFlows(), false);
        double dirtyFutPrice = input.getFuturePrice() * input.getConversionFactor() + (capitalizedCoupon + accrual);
        int totalDays = DateUtil.diffDays(input.getSettlementDate(), input.getMaturityDate(), input.getDaycount());
        double tenor = 360. / totalDays;
        double dirtyPrice = input.getSpotPrice() + getAccrual(input.getSettlementDate(), input.getMaturityDate(), input.getUnderliyngCashFlows(), true);
        r = ((dirtyFutPrice - dirtyPrice) / dirtyPrice) * tenor;
        return r;
    }

    public BtpMiniFutureOutputData valuation(BtpMiniFutureInputData input) {

        BtpMiniFutureOutputData output = new BtpMiniFutureOutputData();
        output.setPositionValue(positionValue(input));
        double thPrice = 0.;
        if (!NumberUtils.isZero(input.getConversionFactor())) {
            thPrice = theoreticalPrice(input.getSettlementDate(), input.getMaturityDate(), input.getUnderliyngCashFlows(),
                    input.getSpotPrice(), input.getRate(), input.getDaycount(), input.getCompounding());
            thPrice /= input.getConversionFactor();
        }

        output.setTheoreticalPrice(thPrice);
        output.setBasis(input.getSpotPrice() - input.getFuturePrice() * input.getConversionFactor());
        output.setIrr(irr(input));
        return output;
    }

}
