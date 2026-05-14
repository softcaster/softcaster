/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.math;

import java.time.LocalDate;
import org.softcaster.engine.enums.Compounding;
import static org.softcaster.engine.enums.Compounding.COMPOUNDED;
import static org.softcaster.engine.enums.Compounding.CONTINUOUS;
import static org.softcaster.engine.enums.Compounding.SIMPLE;
import static org.softcaster.engine.enums.Compounding.SIMPLE_THEN_COMPOUNDED;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public final class MathUtil {

    public static final double DEFAULT_H = 0.000001;
    public static final int DEFAULT_MAX_ITERATES = 10000;
    public static final double EPSILON = 1e-11;
    
    public static interface Function1 {

        double f(double x);

        double f(double x, Compounding compounding);
    }

    /**
     * Returns sum of \sum_{i=from}^{to} base * r^{i}.
     *
     * @param base
     * @param r
     * @param from
     * @param to
     * @return sum of a geometric series.
     */
    public static double geometricSeriesSum(double base, double r, int from, int to) {
        double sum = 0.0;

        for (int i = from; i <= to; i++) {
            sum += Math.pow(r, i);
        }

        return base * sum;
    }

    /**
     * Differentiate based on 1st order Sterling formula.
     *
     * @param function
     * @param x0
     * @param h
     * @return numerical derivative of function.f() at x0
     */
    public static double differentiateSterling0(Function1 function, double x0, double h) {
        return (function.f(x0 + h) - function.f(x0 - h)) / (2 * h);
    }

    /**
     * Differentiate based on 1st order Sterling formula using DEFAULT_H.
     *
     * @param function
     * @param x0
     * @return numerical derivative of function.f() at x0
     */
    public static double differentiateSterling0(Function1 function, double x0) {
        return differentiateSterling0(function, x0, DEFAULT_H);
    }

    /**
     * Find root of function using Newton's method. The root found is sensitive
     * to the initial guess, and the direction of the search cannot be
     * controlled. Based on the function parameter list in Excel's RATE() and
     * IRR() functions, I think they are also using this method.
     *
     * @param function
     * @param guess initial guess of the location of the root. Excel's RATE() &
     * IRR() default is 0.10
     * @param h for convergence criteria and computing numerical derivative
     * @param maxIterates max number of iterates before bailing out
     * @param compounding
     * @return
     */
    public static double rootNewton(Function1 function, double guess, double h, int maxIterates, Compounding compounding) {
        double x0 = guess * 100;                  // dummy default
        double x1 = guess;
        int iterates = 0;

        while (Math.abs(x1 - x0) > h) {
            x0 = x1;
            x1 = x0 - function.f(x0, compounding) / differentiateSterling0(function, x0, h);
            iterates++;
            if (iterates > maxIterates) {
                throw new ArithmeticException("Failed to converge after " + Integer.toString(maxIterates) + " iterations.");
            }
        }

        return x1;
    }

    /**
     *
     * @param function
     * @param guess
     * @param compounding
     * @return
     */
    public static double rootNewton(Function1 function, double guess, Compounding compounding) {
        return rootNewton(function, guess, DEFAULT_H, DEFAULT_MAX_ITERATES, compounding);
    }

    public static double getDiscountFactor(Compounding compounding, double rate, double t) {

        double discountFactor = 0;
        switch (compounding) {
            case SIMPLE ->
                discountFactor = 1. / (1 + rate * t);
            case COMPOUNDED ->
                discountFactor = 1. / Math.pow(1 + rate, t);
            case CONTINUOUS ->
                discountFactor = Math.exp(-rate * t);
            case SIMPLE_THEN_COMPOUNDED -> {
                if (t <= 1) {
                    discountFactor = 1. / (1 + rate * t);
                } else {
                    discountFactor = 1. / Math.pow(1 + rate, t);
                }
            }
        }

        return discountFactor;
    }

    public static double getTimeToMaturity(DaycountBasis daycount, Frequency frequency, LocalDate from, LocalDate to) {
        return daycount.calculate(from, to, frequency);
    }

    public static boolean isZero(double x) {
        return Math.abs(x) < EPSILON;
    }    
}
