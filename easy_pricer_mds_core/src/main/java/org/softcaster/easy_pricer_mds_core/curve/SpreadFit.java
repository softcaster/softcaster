/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.curve;

/**
 * Least-squares calibration of the spread buckets.
 *
 * Finds the bucket spreads s (in bp) that minimise sum_i ( (model_i(s) -
 * market_i) / |dP_i/ds| )^2 + lambda * sum_j curvature_j(s)^2 i.e. the price
 * residuals expressed in spread units (every bond weighs the same, whatever its
 * duration) plus a penalty on the curvature of the spread curve. The penalty
 * makes buckets with no or few bonds follow a smooth line between their
 * neighbours instead of being noise.
 *
 * Gauss-Newton: the model is almost linear in the spreads, so it converges in
 * 2-4 iterations. The pricing is supplied by the caller (Pricer), so this class
 * knows nothing about bonds, curves or the database.
 */
public final class SpreadFit {

    private SpreadFit() {
    }

    /**
     * Model clean prices of all the calibration bonds (same order as the market
     * prices) for a spread vector in bp.
     */
    public interface Pricer {

        double[] prices(double[] spreadsBp);
    }

    /**
     * @param tenors bucket tenors in years, strictly ascending (at least 3)
     * @param market market clean prices, same scale and order as the Pricer
     * output
     * @param pricer
     * @param lambda smoothness weight (>= 0); 0 = no regularisation
     * @param maxIterations
     * @param toleranceBp
     * @return fitted spreads in bp, one per tenor
     */
    public static double[] fit(double[] tenors, double[] market, Pricer pricer,
            double lambda, int maxIterations, double toleranceBp) {
        final int m = tenors.length;
        final int n = market.length;
        if (m < 3) {
            throw new IllegalArgumentException("At least 3 buckets are needed");
        }
        for (int j = 1; j < m; j++) {
            if (!(tenors[j] > tenors[j - 1])) {
                throw new IllegalArgumentException("Tenors must be strictly ascending");
            }
        }
        final double[][] dtd = curvaturePenalty(tenors);
        double[] s = new double[m];                                   // start from zero spread

        for (int it = 0; it < maxIterations; it++) {
            double[] p0 = pricer.prices(s);

            // price sensitivity of each bond to a parallel 1 bp move (price change per bp, negative)
            double[] par = s.clone();
            for (int j = 0; j < m; j++) {
                par[j] += 1.0;
            }
            double[] pPar = pricer.prices(par);
            double[] scale = new double[n];                           // 1 / |slope|; 0 = bond ignored
            for (int i = 0; i < n; i++) {
                double slope = Math.abs(pPar[i] - p0[i]);
                scale[i] = (slope > 1e-9 && Double.isFinite(p0[i])) ? 1.0 / slope : 0.0;
            }

            // Jacobian, one bucket at a time, rows scaled to spread units
            double[][] jac = new double[n][m];
            for (int j = 0; j < m; j++) {
                double[] sj = s.clone();
                sj[j] += 1.0;
                double[] pj = pricer.prices(sj);
                for (int i = 0; i < n; i++) {
                    jac[i][j] = (pj[i] - p0[i]) * scale[i];
                }
            }
            double[] res = new double[n];
            for (int i = 0; i < n; i++) {
                res[i] = (p0[i] - market[i]) * scale[i];
            }

            // normal equations: (J'J + lambda D'D + eps I) delta = -J'res - lambda D'D s
            double[][] a = new double[m][m];
            double[] b = new double[m];
            for (int i = 0; i < n; i++) {
                if (scale[i] == 0.0) {
                    continue;
                }
                for (int j = 0; j < m; j++) {
                    b[j] -= jac[i][j] * res[i];
                    for (int k = 0; k < m; k++) {
                        a[j][k] += jac[i][j] * jac[i][k];
                    }
                }
            }
            for (int j = 0; j < m; j++) {
                for (int k = 0; k < m; k++) {
                    a[j][k] += lambda * dtd[j][k];
                    b[j] -= lambda * dtd[j][k] * s[k];
                }
                a[j][j] += 1e-6;                                      // numerical safety for unconstrained buckets
            }
            double[] delta = solve(a, b);

            double step = 0.0;
            for (int j = 0; j < m; j++) {
                s[j] += delta[j];
                step = Math.max(step, Math.abs(delta[j]));
            }
            if (step < toleranceBp) {
                break;
            }
        }
        return s;
    }

    /**
     * D'D where D holds the second divided differences of the spreads over the
     * (non uniform) grid.
     */
    private static double[][] curvaturePenalty(double[] t) {
        int m = t.length;
        double[][] dtd = new double[m][m];
        for (int j = 1; j < m - 1; j++) {
            double h0 = t[j] - t[j - 1];
            double h1 = t[j + 1] - t[j];
            double f = 2.0 / (h0 + h1);
            double[] row = new double[m];
            row[j - 1] = f / h0;
            row[j] = -f * (1.0 / h0 + 1.0 / h1);
            row[j + 1] = f / h1;
            for (int a = j - 1; a <= j + 1; a++) {
                for (int c = j - 1; c <= j + 1; c++) {
                    dtd[a][c] += row[a] * row[c];
                }
            }
        }
        return dtd;
    }

    /**
     * Gaussian elimination with partial pivoting (small dense systems).
     */
    private static double[] solve(double[][] a, double[] b) {
        int n = b.length;
        double[][] m = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, m[i], 0, n);
            m[i][n] = b[i];
        }
        for (int c = 0; c < n; c++) {
            int p = c;
            for (int r = c + 1; r < n; r++) {
                if (Math.abs(m[r][c]) > Math.abs(m[p][c])) {
                    p = r;
                }
            }
            if (Math.abs(m[p][c]) < 1e-14) {
                throw new ArithmeticException("Singular system in the spread fit");
            }
            double[] tmp = m[c];
            m[c] = m[p];
            m[p] = tmp;
            for (int r = c + 1; r < n; r++) {
                double f = m[r][c] / m[c][c];
                for (int k = c; k <= n; k++) {
                    m[r][k] -= f * m[c][k];
                }
            }
        }
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double s = m[i][n];
            for (int k = i + 1; k < n; k++) {
                s -= m[i][k] * x[k];
            }
            x[i] = s / m[i][i];
        }
        return x;
    }
}
