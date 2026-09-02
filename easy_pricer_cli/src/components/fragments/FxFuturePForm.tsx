import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { InstrumentField, YCurveField } from './FormFields.tsx';
import { useState } from 'react';

import type { ForwardPricingRequest, ForwardPricingResponse, YieldCurveDto, GenericMasterDataDto } from '../services/dto.ts';
 '../data/schema.ts';

interface FxFuturePricingFormProps {
    masterDataList: GenericMasterDataDto[];
    yieldCurveList: YieldCurveDto[];
    data: ForwardPricingRequest;
    results: ForwardPricingResponse;
    onChange: (data: ForwardPricingRequest) => void;
}

//Il modulo riceve l'oggetto trade come prop
export const FxFuturePForm = ({ masterDataList, yieldCurveList, data, results, onChange }: FxFuturePricingFormProps) => {

    // Funzione helper per aggiornare Isin e Issue description
    const updateIsin = (value: any) => {
        setMasterData(value);
        onChange({ ...data, ['isin']: value.code });
    };

    // Funzione helper per aggiornare solo un campo della request
    const updateRequest = (field: string, value: any) => {
        onChange({ ...data, [field]: value });
    };
    const [masterData, setMasterData] = useState<GenericMasterDataDto>();

    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            {/* align-items-end è la chiave per la linea dritta */}
            <div className="grid p-fluid align-items-end">

                <InstrumentField
                    label="Fx Future Contract"
                    value={masterData}
                    options={masterDataList}
                    onChange={(val) => updateIsin(val)}
                />

                <div className="col-12 md:col-6">
                    {/* Usiamo una classe per le label per controllarne l'altezza nel CSS */}
                    <label className="block mb-2 font-bold text-sm">Description</label>
                    <InputText
                        value={masterData?.description || ''}
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
                    <label className="block mb-2 font-bold text-sm">Spot Price</label>
                    <InputNumber
                        value={data.referencePrice}
                        onValueChange={(e) => updateRequest('referencePrice', e.value)}
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>

                <YCurveField
                    label={'Domestic Yield Curve'}
                    // Trova l'oggetto completo nella lista che corrisponde alla stringa salvata in data.domesticRCurve
                    value={yieldCurveList.find(c => c.code === data.domesticRCurve) || null}
                    options={yieldCurveList}
                    onChange={(val) => updateRequest('domesticRCurve', val.code)}
                />

                <YCurveField
                    label={'Foreign Yield Curve'}
                    // Trova l'oggetto completo nella lista che corrisponde alla stringa salvata in data.domesticRCurve
                    value={yieldCurveList.find(c => c.code === data.foreignRCurve) || null}
                    options={yieldCurveList}
                    onChange={(val) => updateRequest('foreignRCurve', val.code)}
                />

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Theoretical Price</label>
                    <InputNumber
                        value={results.theoreticalPrice}
                        readOnly
                        disabled
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Basis</label>
                    <InputNumber
                        value={results.theoreticalPrice}
                        readOnly
                        disabled
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>
            </div>
        </div >
    );
};
