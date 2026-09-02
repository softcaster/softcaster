import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { InstrumentField } from './FormFields.tsx';
import { useState } from 'react';
import type { ForwardPricingRequest, ForwardPricingResponse, GenericMasterDataDto } from '../services/dto.ts';

interface BondFuturePricingFormProps {
    masterDataList: GenericMasterDataDto[];
    data: ForwardPricingRequest;
    results: ForwardPricingResponse;
    onChange: (data: ForwardPricingRequest) => void;
}

//Il modulo riceve l'oggetto trade come prop
export const BondFuturePForm = ({ masterDataList, data, results, onChange }: BondFuturePricingFormProps) => {

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
                    label="Bond Future Contract"
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
                    <label className="block mb-2 font-bold text-sm">Reference price date</label>
                    <Calendar
                        value={data.referenceDate}
                        onChange={(e) => updateRequest('referenceDate', e.value as Date)}
                        showIcon dateFormat="dd/mm/yy" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Reference price</label>
                    <InputNumber
                        value={data.referencePrice}
                        onValueChange={(e) => updateRequest('referencePrice', e.value)}
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>
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
                    <label className="block mb-2 font-bold text-sm">CTD</label>
                    <InputText
                        value={results.ctd}
                        readOnly
                        disabled
                        className="w-full" />
                </div>
            </div>
        </div >
    );
};
