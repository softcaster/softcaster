import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { InstrumentField, YCurveField } from './FormFields.tsx';
import { useState } from 'react';
import type { PricingRequest, PricingResponse, YieldCurveDto } from '../services/dto.ts';
import type { OptionSeriesDto } from '../services/services.ts';

interface OptionPricingFormProps {
    masterDataList: OptionSeriesDto[];
    yieldCurveList: YieldCurveDto[];
    data: PricingRequest;
    results: PricingResponse;
    onChange: (data: PricingRequest) => void;
}

// Campi di mercato per famiglia: il server sceglie il modello dalla serie
//  FFO, BFO: opzione su future -> prezzo del future, curva/tasso di sconto del premio
//  FXO:      opzione su cambio -> spot, curva domestica ed estera (default: quelle della coppia)
//  BDO:      opzione su titolo -> prezzo secco, curva/tasso repo (anche per lo sconto del premio)
const underlyingLabel = (assetClass?: string) => {
    switch (assetClass) {
        case 'FXO': return 'Spot Rate';
        case 'BDO': return 'Bond Clean Price';
        default: return 'Future Price';
    }
};

// Campo numerico di sola lettura per i risultati
const ResultField = ({ label, value, digits = 5 }: { label: string, value?: number | null, digits?: number }) => (
    <div className="col-12 md:col-3">
        <label className="block mb-2 font-bold text-sm">{label}</label>
        <InputNumber
            value={value ?? null}
            readOnly
            disabled
            mode="decimal" minFractionDigits={digits} maxFractionDigits={digits}
            inputStyle={{ textAlign: 'right' }}
            className="w-full" />
    </div>
);

export const OptionPForm = ({ masterDataList, yieldCurveList, data, results, onChange }: OptionPricingFormProps) => {

    const [masterData, setMasterData] = useState<OptionSeriesDto>();
    const assetClass = masterData?.assetClass;
    const isFx = assetClass === 'FXO';
    const isBond = assetClass === 'BDO';
    // con la curva selezionata il server ignora il tasso
    const hasCurve = !!data.domesticRCurve;
    // con il premio di mercato la volatilita' e' solo il primo tentativo: il server calcola l'implicita
    const hasMarketPremium = data.optionPrice != null && data.optionPrice > 0;

    const updateIsin = (value: OptionSeriesDto) => {
        setMasterData(value);
        // i campi di mercato della famiglia precedente non valgono per la nuova serie
        onChange({ ...data, isin: value.code, referencePrice: undefined, domesticRCurve: '', domesticRate: undefined, foreignRCurve: '' });
    };

    const updateRequest = <K extends keyof PricingRequest>(field: K, value: PricingRequest[K]) => {
        onChange({ ...data, [field]: value });
    };

    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            <div className="grid p-fluid align-items-end">

                <InstrumentField
                    label="Option Series"
                    value={masterData}
                    options={masterDataList}
                    onChange={(val) => updateIsin(val)}
                />

                <div className="col-12 md:col-6">
                    <label className="block mb-2 font-bold text-sm">Description</label>
                    <InputText
                        value={masterData?.description || ''}
                        readOnly
                        className="surface-100"
                    />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Family</label>
                    <InputText
                        value={assetClass || ''}
                        readOnly
                        className="surface-100"
                    />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Reference Date</label>
                    <Calendar
                        value={data.referenceDate}
                        onChange={(e) => updateRequest('referenceDate', e.value as Date)}
                        showIcon dateFormat="dd/mm/yy" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">{underlyingLabel(assetClass)} (empty = quote)</label>
                    <InputNumber
                        value={data.referencePrice ?? null}
                        onValueChange={(e) => updateRequest('referencePrice', e.value ?? undefined)}
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        tooltip="Price of the underlying (future, spot rate or bond clean price). If empty, the server uses the current market quote."
                        tooltipOptions={{ position: 'top' }}
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">{hasMarketPremium ? 'Volatility % (first guess)' : 'Volatility %'}</label>
                    <InputNumber
                        value={data.volatility != null ? data.volatility * 100. : null}
                        onValueChange={(e) => updateRequest('volatility', e.value != null ? e.value / 100. : undefined)}
                        mode="decimal" minFractionDigits={4} placeholder="0.0000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Market Premium → implied vol</label>
                    <InputNumber
                        value={data.optionPrice ?? null}
                        onValueChange={(e) => updateRequest('optionPrice', e.value ?? undefined)}
                        mode="decimal" minFractionDigits={6} placeholder="0.000000"
                        tooltip="Optional: option price observed on the market (e.g. exchange settlement). If set, the implied volatility is computed from it instead of using Volatility %."
                        tooltipOptions={{ position: 'top' }}
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>

                <YCurveField
                    label={isFx ? 'Domestic Curve' : isBond ? 'Repo Curve' : 'Discount Curve'}
                    value={yieldCurveList.find(c => c.code === data.domesticRCurve) || null}
                    options={yieldCurveList}
                    onChange={(val) => updateRequest('domesticRCurve', val ? val.code : '')}
                    showClear
                />

                {isFx ? (
                    <YCurveField
                        label={'Foreign Curve'}
                        value={yieldCurveList.find(c => c.code === data.foreignRCurve) || null}
                        options={yieldCurveList}
                        onChange={(val) => updateRequest('foreignRCurve', val ? val.code : '')}
                        showClear
                    />
                ) : (
                    <div className="col-12 md:col-3">
                        <label className="block mb-2 font-bold text-sm">{isBond ? 'Repo rate % (ACT/360)' : 'Discount rate % (cont.)'}</label>
                        <InputNumber
                            value={data.domesticRate != null ? data.domesticRate * 100. : null}
                            onValueChange={(e) => updateRequest('domesticRate', e.value != null ? e.value / 100. : undefined)}
                            disabled={hasCurve}
                            mode="decimal" minFractionDigits={5} placeholder="0.00000"
                            inputStyle={{ textAlign: 'right' }}
                            className="w-full" />
                    </div>
                )}

                {/* risultati */}
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Model</label>
                    <InputText
                        value={results.optionModel || ''}
                        readOnly
                        disabled
                        className="w-full" />
                </div>
                <ResultField label="Premium" value={results.optionPremium} digits={6} />
                <ResultField label="Position Value" value={results.optionValue} digits={2} />
                <ResultField label={hasMarketPremium ? 'Implied Volatility %' : 'Volatility %'}
                    value={results.volatility != null ? results.volatility * 100. : null} digits={4} />
                <ResultField label="Underlying Forward" value={results.underlyingForward} />
                <ResultField label="Discount Factor" value={results.discountFactor} digits={6} />
                <ResultField label="Time to Expiry (years)" value={results.timeToExpiry} />
                <ResultField label="Delta" value={results.delta} />
                <ResultField label="Gamma" value={results.gamma} />
                <ResultField label="Vega (1 vol pt)" value={results.vega} digits={6} />
                <ResultField label="Theta (1 day)" value={results.theta} digits={6} />
                <ResultField label="Rho (1 rate pt)" value={results.rho} digits={6} />
            </div>
        </div >
    );
};
