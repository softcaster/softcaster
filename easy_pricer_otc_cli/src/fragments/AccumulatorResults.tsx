import type { AccumulatorPricingResponse } from '../api/types';
import { fmt, pct, tone } from '../components/format';
import { KpiGrid } from '../components/ui';

/** Valutazione Monte Carlo di un accumulator. strike: per il valore in % dell'importo massimo. */
export const AccumulatorResults = ({ result, strike }: { result: AccumulatorPricingResponse; strike: number | null }) => {
    const ccyMax = strike ? result.maxQuantity * strike : null;
    return (
        <KpiGrid items={[
            { label: 'Valore (detentore)', value: fmt(result.value), tone: tone(result.value) },
            { label: 'Errore standard', value: fmt(result.standardError) },
            { label: 'Valore % importo max', value: ccyMax ? pct(100 * result.value / ccyMax) : '—', hint: 'Valore / (quantita\' massima x strike)' },
            { label: 'Da regolare', value: fmt(result.pendingValue), hint: 'Quantita\' gia\' maturate e non regolate' },
            { label: 'P(knock-out)', value: pct(100 * result.knockOutProbability, 2) },
            { label: 'Quantità massima', value: fmt(result.maxQuantity) },
            { label: 'Quantità attesa', value: fmt(result.expectedQuantity, 0) },
            { label: 'Delta', value: fmt(result.delta, 0) },
            { label: 'Gamma', value: fmt(result.gamma, 0) },
            { label: 'Vega (per punto)', value: fmt(result.vega) },
            { label: 'Fixing', value: `${result.futureFixings} / ${result.totalFixings}` },
            { label: 'Volatilità', value: `${pct(100 * result.volatility, 2)} (${result.volatilitySource})` },
            { label: 'Spot', value: fmt(result.spot, 5) },
        ]} />
    );
};
