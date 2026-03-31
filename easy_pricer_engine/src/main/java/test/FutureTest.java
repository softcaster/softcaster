/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.Converter;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.products.futures.BtpMiniFuture;
import ph.alephzero.finance.products.futures.BtpMiniFutureInputData;
import ph.alephzero.finance.products.futures.BtpMiniFutureOutputData;

/**
 *
 * @author softc
 */
public class FutureTest {

    public static void main(String[] args) {
        BtpMiniFutureInputData input = new BtpMiniFutureInputData();
        input.setContractValue(25000.);
        input.setInvoicePrice(115);
        input.setFuturePrice(115.8);
        input.setNrOfContracts(3);
        input.setTick(0.01);

        Date _settlement = new Date();
        input.setSettlementDate(_settlement.sqlDate());
        _settlement.addMonths(3);
        input.setMaturityDate(_settlement.sqlDate());

        input.setSpotPrice(95.12);
        input.setRate(0.02);

        BtpMiniFuture future = new BtpMiniFuture();
        BtpMiniFutureOutputData output = future.valuation(input);
        System.out.println(Converter.fromDouble(output.getPositionValue()));

        double invoicePrice = input.getFuturePrice() * input.getConversionFactor();
        double basis = input.getSpotPrice() - invoicePrice;
        System.out.println(Converter.fromDouble(basis));
    }
}
