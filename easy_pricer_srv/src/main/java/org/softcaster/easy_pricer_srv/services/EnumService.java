package org.softcaster.easy_pricer_srv.services;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.softcaster.easy_pricer_srv.dto.EnumDto;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.FeeBasis;
import org.softcaster.engine.enums.FeeTiming;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.GuaranteeType;
import org.softcaster.engine.enums.IdentifiableEnum;
import org.softcaster.engine.enums.KnockInEffect;
import org.softcaster.engine.enums.LoanFeeType;
import org.softcaster.engine.enums.RollConvention;
import org.softcaster.engine.enums.SettlementType;
import org.springframework.stereotype.Service;

/**
 * Enum esposti ai client, per nome. Solo quelli elencati qui; alcune liste
 * sono ristrette al contesto d'uso (es. frequenze delle rate dei mutui).
 */
@Service
public class EnumService {

    private static final Map<String, Supplier<List<EnumDto>>> CATALOG = new LinkedHashMap<>();

    static {
        CATALOG.put("daycountBasis", () -> all(DaycountBasis.class));
        CATALOG.put("rollConvention", () -> all(RollConvention.class));
        CATALOG.put("amortizationSchedule", () -> of(AmortizationSchedule.class, a -> a != AmortizationSchedule.NONE));
        // rate dei mutui: solo frequenze cedolari (mensili e multipli)
        CATALOG.put("loanFrequency", () -> of(Frequency.class, Frequency::isCouponFrequency));
        CATALOG.put("loanFeeType", () -> all(LoanFeeType.class));
        CATALOG.put("feeBasis", () -> all(FeeBasis.class));
        CATALOG.put("feeTiming", () -> all(FeeTiming.class));
        CATALOG.put("accumulatorType", () -> all(AccumulatorType.class));
        CATALOG.put("settlementType", () -> all(SettlementType.class));
        // regolamenti degli accumulator
        CATALOG.put("accumulatorFrequency", () -> of(Frequency.class,
                EnumSet.of(Frequency.DAILY, Frequency.WEEKLY, Frequency.MONTHLY, Frequency.NONE)::contains));
        CATALOG.put("barrierObservation", () -> all(BarrierObservation.class));
        CATALOG.put("knockInEffect", () -> all(KnockInEffect.class));
        // NONE corrisponde a zero periodi garantiti, non si sceglie
        CATALOG.put("guaranteeType", () -> of(GuaranteeType.class, g -> g != GuaranteeType.NONE));
    }

    public List<String> names() {
        return List.copyOf(CATALOG.keySet());
    }

    public List<EnumDto> find(String name) {
        Supplier<List<EnumDto>> s = CATALOG.get(name);
        if (s == null) {
            throw new NoSuchElementException("Unknown enum: " + name + " (available: " + String.join(", ", CATALOG.keySet()) + ")");
        }
        return s.get();
    }

    public Map<String, List<EnumDto>> find(List<String> names) {
        Map<String, List<EnumDto>> result = new LinkedHashMap<>();
        for (String name : names) {
            result.put(name, find(name));
        }
        return result;
    }

    private static <E extends Enum<E> & IdentifiableEnum> List<EnumDto> all(Class<E> type) {
        return of(type, e -> true);
    }

    private static <E extends Enum<E> & IdentifiableEnum> List<EnumDto> of(Class<E> type, Predicate<E> filter) {
        return Arrays.stream(type.getEnumConstants()).filter(filter).map(EnumDto::of).toList();
    }
}
