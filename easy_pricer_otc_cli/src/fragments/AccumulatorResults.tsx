import type { AccumulatorPricingResponse } from '../api/types';
import { fmt, pct, tone } from '../components/format';
import { KpiGrid } from '../components/ui';

/** Valutazione Monte Carlo di un accumulator. strike: per il valore in % dell'importo massimo. */
export const AccumulatorResults = ({ result, strike }: { result: AccumulatorPricingResponse; strike: number | null }) => {
    const ccyMax = strike ? result.maxQuantity * strike : null;
    return (
        <KpiGrid items={[
            { label: 'Value (holder)', value: fmt(result.value), tone: tone(result.value) },
            { label: 'Standard error', value: fmt(result.standardError) },
            { label: 'Value % of max amount', value: ccyMax ? pct(100 * result.value / ccyMax) : '—', hint: 'Value / (maximum quantity × strike)' },
            { label: 'Pending settlement', value: fmt(result.pendingValue), hint: 'Quantities already accrued and not yet settled' },
            { label: 'P(knock-out)', value: pct(100 * result.knockOutProbability, 2) },
            { label: 'Maximum quantity', value: fmt(result.maxQuantity) },
            { label: 'Expected quantity', value: fmt(result.expectedQuantity, 0) },
            { label: 'Delta', value: fmt(result.delta, 0) },
            { label: 'Gamma', value: fmt(result.gamma, 0) },
            { label: 'Vega (per point)', value: fmt(result.vega) },
            { label: 'Fixings', value: `${result.futureFixings} / ${result.totalFixings}` },
            { label: 'Volatility', value: `${pct(100 * result.volatility, 2)} (${result.volatilitySource})` },
            { label: 'Spot', value: fmt(result.spot, 5) },
        ]} />
    );
};
