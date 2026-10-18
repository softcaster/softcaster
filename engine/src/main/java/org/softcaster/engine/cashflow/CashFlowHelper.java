/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

// package e import come nel file originale

/**
 * Calcoli su strumenti a flussi: rateo, yield, prezzo, z-spread, duration, convexity.
 *
 * Convenzioni:
 *  - tutti i tassi (ytm, spread) sono in DECIMALE (0.0425 = 4,25%);
 *  - prezzo, yield, duration e convexity usano TUTTI la stessa funzione di sconto
 *    (MathUtil.getDiscountFactor con lo stesso Compounding), cosi' sono coerenti:
 *    la duration e' esattamente la sensitivita' del prezzo che il solver del yield riproduce.
 */
public final class CashFlowHelper {

    /** Passo per le derivate numeriche sul yield: 0,1 bp in decimale. */
    private static final double YIELD_BUMP = 1e-5;

    private CashFlowHelper() {
    }

    // ------------------------------------------------------------------------
    // -- Flussi
    // ------------------------------------------------------------------------

    public static List<CashFlow> futureFlows(List<CashFlow> flows, LocalDate settlement) {
        return flows.stream().filter(cf -> cf.paymentDate().isAfter(settlement)).toList();
    }

    /**
     * Valore attuale dei flussi (gia' filtrati: solo futuri) a un dato yield.
     * Unica funzione di sconto usata da prezzo, yield, duration e convexity.
     */
    private static double pvAtYield(List<CashFlow> futureFlows, double ytm, LocalDate valuationDate,
                                    DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        double pv = 0.0;
        for (CashFlow cf : futureFlows) {
            double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);
            pv += cf.getTotalAmount() * MathUtil.getDiscountFactor(compounding, ytm, t);
        }
        return pv;
    }

    // ------------------------------------------------------------------------
    // -- Rateo
    // ------------------------------------------------------------------------

    public static double calculateAccruedInterest(List<CashFlow> flows, LocalDate valuationDate,
                                                  DaycountBasis dcb, Frequency freq) {
        // Trova la cedola in corso (quella il cui periodo include la valuationDate)
        return flows.stream()
                .filter(cf -> !valuationDate.isBefore(cf.accrualStart()) && valuationDate.isBefore(cf.accrualEnd()))
                .findFirst()
                .map(cf -> {
                    long daysFromStart = ChronoUnit.DAYS.between(cf.accrualStart(), valuationDate);
                    double theoreticalDaysInPeriod;

                    if (dcb == DaycountBasis.ACT_360) {
                        // Regola CCT: base commerciale fissa (360 / frequenza, es. 180 per semestrale)
                        theoreticalDaysInPeriod = dcb.getTime() / freq.getYearFraction();
                    } else {
                        // Regola BTP (ACT/ACT ICMA): giorni esatti del periodo cedolare.
                        // ATTENZIONE: qualunque altro daycount (30/360, ACT/365...) finisce qui
                        // e viene trattato come ACT/ACT.
                        theoreticalDaysInPeriod = ChronoUnit.DAYS.between(cf.accrualStart(), cf.accrualEnd());
                    }

                    return cf.interest() * (daysFromStart / theoreticalDaysInPeriod);
                })
                .orElse(0.0); // Nessun rateo se siamo fuori dai periodi o il bond e' scaduto
    }

    // ------------------------------------------------------------------------
    // -- Yield e prezzo
    // ------------------------------------------------------------------------

    public static double solveInternalRateOfReturn(List<CashFlow> cashflows, double dirtyPrice,
                                                   LocalDate valuationDate, DaycountBasis dcb,
                                                   Compounding compounding, Frequency frequency) {
        MathUtil.Function1 objective = new MathUtil.Function1() {
            @Override
            public double f(double rate) {
                return f(rate, compounding);
            }

            @Override
            public double f(double rate, Compounding c) {
                return pvAtYield(cashflows, rate, valuationDate, dcb, c, frequency) - dirtyPrice;
            }
        };
        return MathUtil.rootNewton(objective, 0.10, compounding);
    }

    public static double calculateYtm(List<CashFlow> flows, double cleanPrice, LocalDate valuationDate,
                                      DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        double dirtyPrice = cleanPrice + calculateAccruedInterest(flows, valuationDate, dcb, frequency);
        return solveInternalRateOfReturn(futureFlows(flows, valuationDate), dirtyPrice,
                valuationDate, dcb, compounding, frequency);
    }

    /** Clean price a un dato yield.
     * @param flows
     * @param ytm
     * @param valuationDate
     * @param dcb
     * @param compounding
     * @param frequency
     * @return  */
    public static double calculatePrice(List<CashFlow> flows, double ytm, LocalDate valuationDate,
                                        DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        double dirty = pvAtYield(futureFlows(flows, valuationDate), ytm, valuationDate, dcb, compounding, frequency);
        return dirty - calculateAccruedInterest(flows, valuationDate, dcb, frequency);
    }

    // ------------------------------------------------------------------------
    // -- Curva e z-spread
    // ------------------------------------------------------------------------

    /**
     * PV al settlement dei flussi indicati, con uno spread continuo additivo
     * sul tempo ACT/365 (convenzione della curva, indipendente dal daycount del titolo).
     * Il chiamante deve passare solo flussi pagati dopo il settlement.
     * @param futureFlows
     * @param curve
     * @param settlement
     * @param spread
     * @return 
     */
    public static double presentValue(List<CashFlow> futureFlows, DiscountCurve curve,
                                      LocalDate settlement, double spread) {
        double dfSettle = curve.getDiscountFactor(settlement);
        double pv = 0.0;
        for (CashFlow cf : futureFlows) {
            if (!cf.paymentDate().isAfter(settlement)) {
                throw new IllegalArgumentException("Cashflow on " + cf.paymentDate()
                        + " is not after settlement " + settlement);
            }
            double t = ChronoUnit.DAYS.between(settlement, cf.paymentDate()) / 365.0;
            pv += cf.getTotalAmount()
                    * curve.getDiscountFactor(cf.paymentDate()) / dfSettle
                    * Math.exp(-spread * t);
        }
        return pv;
    }

    public static double solveZSpread(List<CashFlow> cashflows, double dirtyPrice,
                                      LocalDate valuationDate, DiscountCurve curve) {
        List<CashFlow> future = futureFlows(cashflows, valuationDate);   // filtrati una volta sola

        MathUtil.Function1 objective = new MathUtil.Function1() {
            @Override
            public double f(double s) {
                return f(s, Compounding.CONTINUOUS);
            }

            @Override
            public double f(double s, Compounding c) {
                double pv = presentValue(future, curve, valuationDate, s);
                if (FileUtil.dumpDebugInfo()) {
                    System.out.println("Present Value: " + pv + "\tDirty Price: " + dirtyPrice + "\tSpread: " + s);
                }
                return pv - dirtyPrice;
            }
        };
        return MathUtil.rootNewton(objective, 0.001, 1e-8, 150, Compounding.CONTINUOUS);
    }

    /** Clean price scontando con la curva (spread nullo).
     * @param flows
     * @param curve
     * @param valuationDate
     * @param dcb
     * @param frequency
     * @return  */
    public static double calculatePrice(List<CashFlow> flows, DiscountCurve curve, LocalDate valuationDate,
                                        DaycountBasis dcb, Frequency frequency) {
        double dirty = presentValue(futureFlows(flows, valuationDate), curve, valuationDate, 0.0);
        return dirty - calculateAccruedInterest(flows, valuationDate, dcb, frequency);
    }

    // ------------------------------------------------------------------------
    // -- Duration e convexity
    // ------------------------------------------------------------------------

    /**
     * Macaulay duration: media dei tempi dei flussi pesata per il loro valore attuale,
     * con la stessa funzione di sconto del solver del yield.
     * @param flows
     * @param ytm
     * @param valuationDate
     * @param dcb
     * @param compounding
     * @param freq
     * @return 
     */
    public static double calculateMacaulayDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate,
                                                   DaycountBasis dcb, Compounding compounding, Frequency freq) {
        double pvSum = 0.0;
        double weightedSum = 0.0;

        for (CashFlow cf : futureFlows(flows, valuationDate)) {
            double t = dcb.calculate(valuationDate, cf.paymentDate(), freq);
            double pv = cf.getTotalAmount() * MathUtil.getDiscountFactor(compounding, ytm, t);
            pvSum += pv;
            weightedSum += t * pv;
        }

        requirePositive(pvSum);
        return weightedSum / pvSum;
    }

    /**
     * Modified duration = -(1/P) dP/dy, calcolata per differenze centrali.
     * Coerente con qualunque Compounding, perche' usa la stessa funzione di sconto del prezzo.
     * @param flows
     * @param ytm
     * @param valuationDate
     * @param dcb
     * @param compounding
     * @param freq
     * @return 
     */
    public static double calculateModifiedDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate,
                                                   DaycountBasis dcb, Compounding compounding, Frequency freq) {
        List<CashFlow> future = futureFlows(flows, valuationDate);
        double p0 = pvAtYield(future, ytm, valuationDate, dcb, compounding, freq);
        requirePositive(p0);
        double pUp = pvAtYield(future, ytm + YIELD_BUMP, valuationDate, dcb, compounding, freq);
        double pDown = pvAtYield(future, ytm - YIELD_BUMP, valuationDate, dcb, compounding, freq);
        return -(pUp - pDown) / (2 * YIELD_BUMP * p0);
    }

    /**
     * Convexity = (1/P) d2P/dy2, calcolata per differenze centrali.
     * Il dirty price e' ricalcolato internamente, cosi' e' sempre coerente con ytm.
     * @param flows
     * @param ytm
     * @param valuationDate
     * @param dcb
     * @param compounding
     * @param freq
     * @return 
     */
    public static double calculateConvexity(List<CashFlow> flows, double ytm, LocalDate valuationDate,
                                            DaycountBasis dcb, Compounding compounding, Frequency freq) {
        List<CashFlow> future = futureFlows(flows, valuationDate);
        double p0 = pvAtYield(future, ytm, valuationDate, dcb, compounding, freq);
        requirePositive(p0);
        double pUp = pvAtYield(future, ytm + YIELD_BUMP, valuationDate, dcb, compounding, freq);
        double pDown = pvAtYield(future, ytm - YIELD_BUMP, valuationDate, dcb, compounding, freq);
        return (pUp + pDown - 2 * p0) / (YIELD_BUMP * YIELD_BUMP * p0);
    }

    private static void requirePositive(double pv) {
        if (pv <= 0.0) {
            throw new IllegalArgumentException(
                    "Valore attuale dei flussi non positivo (" + pv + "): verificare flussi e yield");
        }
    }
}