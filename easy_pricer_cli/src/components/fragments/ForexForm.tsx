// src/components/ForexForm.tsx
import { InputNumber } from 'primereact/inputnumber';
import { Calendar } from 'primereact/calendar';
import { InputText } from 'primereact/inputtext';
import type { ForexTrade } from '../data/fxtrade';
import { Dropdown } from 'primereact/dropdown';
import type { ForexMasterData } from '../data/schema';

interface ForexFormProps {
    data: ForexTrade | null;
    currencies: ForexMasterData[];
    onChange: (data: ForexTrade) => void;
}

//Il modulo riceve l'oggetto trade come prop
export const ForexForm = ({ data, currencies, onChange }: ForexFormProps) => {
    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            <div className="grid p-fluid">

                <div className="col-12 md:col-3">
                    <label className="text-sm font-bold block mb-2">Currency Pair</label>
                    <Dropdown
                        value={data?.currPair} // Il trade selezionato
                        options={currencies} // La lista di ForexMasterData caricata dal server
                        onChange={(e) => {
                            if (data) {
                                onChange({ ...data, currPair: e.value });
                            }
                        }}
                        optionLabel="description" // O il campo che vuoi visualizzare (es. "EUR/USD Spot")
                        placeholder="Select Forex Instrument"
                        filter
                        className="w-full"
                        // Opzionale: un template per mostrare BCY e CCY nella lista
                        itemTemplate={(option: ForexMasterData) => (
                            <div>
                                <span className="font-bold">{option?.code}</span>
                                <small className="ml-2 text-500">({option.bcy?.isoCode}/{option.ccy?.isoCode})</small>
                            </div>
                        )} />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Price</label>
                    <InputNumber value={data?.price}
                        onValueChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, price: e.value ?? 0 });
                            }
                        }}
                        mode="decimal" minFractionDigits={2} placeholder="0.00" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Units</label>
                    <InputNumber value={data?.units || 0}
                        onValueChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, units: e.value ?? 0 });
                            }
                        }}
                        useGrouping={false} placeholder="1000" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Value Date</label>
                    <Calendar value={data?.valueDate}
                        onChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, valueDate: e.value as Date | null });
                            }
                        }}
                        showIcon dateFormat="dd/mm/yy" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Reference</label>
                    <InputText value={data?.reference || ''}
                        onChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, reference: e.target.value ?? 0 });
                            }
                        }}
                        placeholder="Trade ID..." />

                </div>
            </div>
        </div>
    );
};
