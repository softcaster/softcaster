import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { InstrumentField } from './FormFields';
import { useState } from 'react';
import type { BondPricingRequest, BondPricingResponse } from '../services/dto.ts';
import type {
    MasterData
} from '../data/schema';

interface BondPricingFormProps {
    masterDataList: MasterData[];
    data: BondPricingRequest;
    results: BondPricingResponse;
    onChange: (data: BondPricingRequest) => void;
}

//Il modulo riceve l'oggetto trade come prop
export const BondPForm = ({ masterDataList , data, results, onChange }: BondPricingFormProps) => {

    // Funzione helper per aggiornare Isin e Issue description
    const updateIsin = (field: string, value: any) => {
        console.log(field + " " + value.code);
        setMasterData(value);
        onChange({ ...data, ['isin']: value.code });
    };
    
    // Funzione helper per aggiornare solo un campo della request
    const updateRequest = (field: string, value: any) => {
        onChange({ ...data, [field]: value });
    };
    const [masterData, setMasterData] = useState<MasterData>();

    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            {/* align-items-end è la chiave per la linea dritta */}
            <div className="grid p-fluid align-items-end">

                <InstrumentField
                    label="Bond Contract"
                    value={masterData}
                    options={masterDataList}
                    onChange={(val) => updateIsin('masterData', val)}
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
                    <label className="block mb-2 font-bold text-sm">Gross accrued interest</label>
                    <InputNumber
                        value={results.accruedInterest}
                        readOnly
                        disabled
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Gross yield to maturity</label>
                    <InputNumber
                        value={results.yieldToMaturity * 100.}
                        readOnly
                        disabled
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Modified Duration</label>
                    <InputNumber 
                        value={results.modifiedDuration}
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
