package org.softcaster.easy_pricer_mds_core; // adatta al tuo package

/**
 * Funzioni pure, condivise da processor ed evaluator.
 * Lavorano sui totali del ciclo corrente (buyQty, sellQty, notionalBuy, notionalSell).
 * Nessuna dipendenza da Spring/JPA.
 *
 * Invariante: realized(t) + unrealized(t, M) == (notionalSell - notionalBuy) + (buyQty - sellQty) * M
 */
public final class PnlCalc {

    /** Tolleranza sulle quantita' per considerare la posizione flat. */
    public static final double QTY_EPS = 1e-6;

    private PnlCalc() {
    }

    /** P&L realizzato del ciclo: quantita' abbinata x (avgSell - avgBuy).
     * @param buyQty
     * @param sellQty
     * @param notionalBuy
     * @param notionalSell
     * @return  */
    public static double realized(double buyQty, double sellQty, double notionalBuy, double notionalSell) {
        if (buyQty <= 0. || sellQty <= 0.) {
            return 0.;
        }
        double matched = Math.min(buyQty, sellQty);
        return matched * (notionalSell / sellQty - notionalBuy / buyQty);
    }

    /** P&L non realizzato sulla quantita' netta, contro la media del lato che la compone.
     * @param buyQty
     * @param sellQty
     * @param notionalBuy
     * @param notionalSell
     * @param mkt */
    public static double unrealized(double buyQty, double sellQty, double notionalBuy, double notionalSell, double mkt) {
        double net = buyQty - sellQty;
        if (Math.abs(net) < QTY_EPS) {
            return 0.;
        }
        if (net > 0.) {                       // long: costo = avgBuy
            return net * (mkt - notionalBuy / buyQty);
        }
        return -net * (notionalSell / sellQty - mkt);   // short: costo = avgSell
    }

    public static boolean isFlat(double buyQty, double sellQty) {
        return Math.abs(buyQty - sellQty) < QTY_EPS;
    }
}