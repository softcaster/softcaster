/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.shape;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.engine.enums.ShapeGranularity;

public class ShapeProfileCalculator {

    private static final double EPSILON = 1e-10;

    public ShapeCalculationResult calculate(
            ShapeCalculationInput input) {

        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        if (input.observations() == null
                || input.observations().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one observation is required");
        }

        /*
         * 1. Aggregate observations by month + day of week
         */
        Map<ShapeBucket, BucketAggregate> aggregates
                = aggregate(input.observations(), input.granularity());

        /*
         * 2. Calculate monthly averages
         */
        Map<Integer, Double> monthlyAverages
                = calculateMonthlyAverages(aggregates);

        /*
         * 3. Calculate shape factors
         */
        List<ShapeFactor> factors = new ArrayList<>();

        for (Map.Entry<ShapeBucket, BucketAggregate> entry
                : aggregates.entrySet()) {

            ShapeBucket bucket = entry.getKey();
            BucketAggregate aggregate = entry.getValue();

            double monthAverage
                    = monthlyAverages.get(bucket.month());

            if (Math.abs(monthAverage) < EPSILON) {
                throw new IllegalArgumentException(
                        "Monthly average is zero for month "
                        + bucket.month());
            }

            double bucketAverage = aggregate.average();

            double factor = bucketAverage / monthAverage;

            factors.add(new ShapeFactor(
                    bucket.month(),
                    bucket.dayOfWeek(),
                    factor
            ));
        }

        /*
         * 4. Check normalization
         *
         * Weighted average of the shape factors over
         * the observations must be 1.
         */
        Map<Integer, Double> normalizationChecks
                = calculateNormalization(
                        aggregates,
                        monthlyAverages
                );

        for (Map.Entry<Integer, Double> entry
                : normalizationChecks.entrySet()) {

            if (Math.abs(entry.getValue() - 1.0) > EPSILON) {

                throw new IllegalStateException(
                        "Shape factors are not normalized for month "
                        + entry.getKey()
                        + ". Expected 1.0 but got "
                        + entry.getValue()
                );

            }

        }

        return new ShapeCalculationResult(
                factors,
                normalizationChecks
        );
    }

    private Map<ShapeBucket, BucketAggregate> aggregate(
            List<SpotObservation> observations,
            ShapeGranularity granularity) {

        Map<ShapeBucket, BucketAggregate> result = new HashMap<>();

        for (SpotObservation observation : observations) {

            ShapeBucket bucket = createBucket(
                    observation,
                    granularity
            );

            BucketAggregate aggregate
                    = result.computeIfAbsent(
                            bucket,
                            k -> new BucketAggregate()
                    );

            aggregate.add(observation.price());
        }

        return result;
    }

    private Map<Integer, Double> calculateMonthlyAverages(
            Map<ShapeBucket, BucketAggregate> aggregates) {

        Map<Integer, Double> monthlySum = new HashMap<>();
        Map<Integer, Integer> monthlyCount = new HashMap<>();

        for (Map.Entry<ShapeBucket, BucketAggregate> entry
                : aggregates.entrySet()) {

            int month = entry.getKey().month();

            BucketAggregate aggregate = entry.getValue();

            monthlySum.merge(
                    month,
                    aggregate.sum(),
                    Double::sum
            );

            monthlyCount.merge(
                    month,
                    aggregate.count(),
                    Integer::sum
            );
        }

        Map<Integer, Double> result = new HashMap<>();

        for (Integer month : monthlySum.keySet()) {

            double sum = monthlySum.get(month);
            int count = monthlyCount.get(month);

            if (count == 0) {
                throw new IllegalStateException(
                        "No observations for month " + month);
            }

            result.put(month, sum / count);
        }

        return result;
    }

    private Map<Integer, Double> calculateNormalization(
            Map<ShapeBucket, BucketAggregate> aggregates,
            Map<Integer, Double> monthlyAverages) {

        Map<Integer, Integer> totalMonthlyObservations
                = new HashMap<>();

        for (Map.Entry<ShapeBucket, BucketAggregate> entry
                : aggregates.entrySet()) {

            int month = entry.getKey().month();

            totalMonthlyObservations.merge(
                    month,
                    entry.getValue().count(),
                    Integer::sum
            );
        }

        Map<Integer, Double> normalizationChecks
                = new HashMap<>();

        for (Map.Entry<ShapeBucket, BucketAggregate> entry
                : aggregates.entrySet()) {

            ShapeBucket bucket = entry.getKey();
            BucketAggregate aggregate = entry.getValue();

            int month = bucket.month();

            double monthAverage
                    = monthlyAverages.get(month);

            double factor
                    = aggregate.average() / monthAverage;

            int totalDays
                    = totalMonthlyObservations.get(month);

            double weight
                    = (double) aggregate.count() / totalDays;

            normalizationChecks.merge(
                    month,
                    weight * factor,
                    Double::sum
            );
        }

        return normalizationChecks;
    }

    private static class BucketAggregate {

        private int count;
        private double sum;

        void add(double price) {
            count++;
            sum += price;
        }

        int count() {
            return count;
        }

        double sum() {
            return sum;
        }

        double average() {
            return count == 0
                    ? 0.0
                    : sum / count;
        }
    }

    private ShapeBucket createBucket(
            SpotObservation observation,
            ShapeGranularity granularity) {

        LocalDate date = observation.date();

        return switch (granularity) {

            case MONTHLY, WEEKLY ->
                new ShapeBucket(
                date.getMonthValue(),
                null,
                -1
                );

            case MONTH_DOW ->
                new ShapeBucket(
                date.getMonthValue(),
                date.getDayOfWeek(),
                -1
                );

            case MONTH_DOW_HOUR ->
                new ShapeBucket(
                date.getMonthValue(),
                date.getDayOfWeek(),
                -1/*observation.hourOfDay()*/
                );
        };
    }
}
