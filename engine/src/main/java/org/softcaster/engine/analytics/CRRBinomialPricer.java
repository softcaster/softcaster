/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import org.softcaster.engine.enums.OptionType;

import java.util.Set;
import org.softcaster.engine.dto.FxOptionInputData;
import org.softcaster.engine.dto.OptionOutputData;
import org.softcaster.engine.math.OptionUtil;

/**
 * Cox-Ross-Rubinstein Binomial Pricer
 *
 * Supporta: - European - American - Bermudan
 */
public class CRRBinomialPricer implements IOptionPricer<FxOptionInputData> {

    private final int steps;

    public CRRBinomialPricer(int steps) {
        if (steps < 1) {
            throw new IllegalArgumentException("Steps must be >= 1");
        }
        this.steps = steps;
    }

    private double price(FxOptionInputData input) {

        double S = input.getUnderlyingReferencePrice();
        double K = input.getStrike();
        double T = OptionUtil.getTimeToMaturity(input);

        double r = input.getDomesticRate();
        double q = input.getForeignRate(); //rf (o dividend yield)

        double sigma = input.getVolatility();

        double dt = T / steps;

        // CRR parameters
        double u = Math.exp(sigma * Math.sqrt(dt));
        double d = 1.0 / u;

        double growth = Math.exp((r - q) * dt);

        double p = (growth - d) / (u - d);

        // Arbitrage check
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException(
                    "Invalid CRR parameters: risk-neutral probability out of range"
            );
        }

        double discountFactor = Math.exp(-r * dt);

        double[] optionValues = new double[steps + 1];

        /*
         * =========================
         * TERMINAL PAYOFFS
         * =========================
         */
        double stockPrice = S * Math.pow(d, steps);

        for (int i = 0; i <= steps; i++) {

            optionValues[i] = payoff(
                    input.getOptionType(),
                    stockPrice,
                    K
            );

            stockPrice *= (u / d);
        }

        /*
         * =========================
         * BACKWARD INDUCTION
         * =========================
         */
        for (int step = steps - 1; step >= 0; step--) {

            // First stock price at this level
            stockPrice = S * Math.pow(d, step);

            for (int i = 0; i <= step; i++) {

                // Continuation value
                double continuationValue
                        = discountFactor
                        * (p * optionValues[i + 1]
                        + (1.0 - p) * optionValues[i]);

                double nodeStockPrice = stockPrice;

                boolean canExercise = canExercise(
                        input,
                        step
                );

                if (canExercise) {

                    double exerciseValue = payoff(
                            input.getOptionType(),
                            nodeStockPrice,
                            K
                    );

                    optionValues[i] = Math.max(
                            continuationValue,
                            exerciseValue
                    );

                } else {

                    optionValues[i] = continuationValue;
                }

                // Move to next node
                stockPrice *= (u / d);
            }
        }

        return optionValues[0];
    }

    /**
     * Vanilla payoff
     */
    private double payoff(
            OptionType type,
            double stockPrice,
            double strike
    ) {

        return switch (type) {

            case CALL ->
                Math.max(0.0, stockPrice - strike);

            case PUT ->
                Math.max(0.0, strike - stockPrice);
        };
    }

    /**
     * Exercise policy
     */
    private boolean canExercise(
            FxOptionInputData input,
            int step
    ) {

        return switch (input.getOptionStyle()) {

            case EUROPEAN ->
                false;

            case AMERICAN ->
                true;

            case BERMUDAN -> {

                Set<Integer> exerciseSteps = mapExerciseDatesToSteps(input);
                yield exerciseSteps != null
                && exerciseSteps.contains(step);
            }
        };
    }

    private Set<Integer> mapExerciseDatesToSteps(FxOptionInputData input) {

        long totalDays
                = ChronoUnit.DAYS.between(
                        input.getValuationDate(),
                        input.getMaturityDate()
                );

        Set<Integer> result = new HashSet<>();

        for (LocalDate date : input.getExerciseDates()) {

            long days = ChronoUnit.DAYS.between(
                    input.getValuationDate(),
                    date
            );

            double fraction
                    = (double) days / totalDays;

            int step
                    = (int) Math.round(fraction * steps);

            result.add(step);
        }

        return result;
    }

    @Override
    public OptionOutputData priceCall(FxOptionInputData input) {
        OptionOutputData output = new OptionOutputData();
        output.setPrice(price(input));
        return output;
    }

    @Override
    public OptionOutputData pricePut(FxOptionInputData input) {
        OptionOutputData output = new OptionOutputData();
        output.setPrice(price(input));
        return output;
    }
}
