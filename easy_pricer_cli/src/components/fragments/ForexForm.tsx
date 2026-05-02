import { InputNumber } from 'primereact/inputnumber';
import { Calendar } from 'primereact/calendar';
import { InputText } from 'primereact/inputtext';
import { SideSelector } from './SideSelector';
import { InstrumentField, CounterpartyField, PositionField } from './FormFields';

//Il modulo riceve l'oggetto trade come prop
export const ForexForm = ({ data, masterDataList, positions, counterparties, onChange }: any) => {

    // Funzione helper per aggiornare solo un pezzo del deal
    const updateField = (field: string, value: any) => {
        if (data) onChange({ ...data, [field]: value });
    };

    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            <div className="grid p-fluid align-items-end">

                {/* 1. Anagrafica (Dinamica) */}
                <InstrumentField
                    label="Currency Pairs"
                    value={data?.masterData}
                    options={masterDataList}
                    onChange={(val) => updateField('masterData', val)}
                />

                <div className="col-12 md:col-3">
                    <label className="text-sm font-bold block mb-2">Side</label>
                    <SideSelector
                        value={data?.txnSide ?? null}
                        onChange={(val: number) => onChange({ ...data!, txnSide: val })}
                    />
                </div>

                {/* 3. Campi standard riutilizzabili */}
                <CounterpartyField
                    value={data?.counterparty}
                    options={counterparties}
                    onChange={(val) => updateField('counterparty', val)}
                />

                <PositionField
                    value={data?.positionMd}
                    options={positions}
                    onChange={(val) => updateField('positionMd', val)}
                />

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Price</label>
                    <InputNumber value={data?.price}
                        onValueChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, price: e.value ?? 0 });
                            }
                        }}
                        mode="decimal" minFractionDigits={5} placeholder="0.00000"
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Units</label>
                    <InputNumber value={data?.quantity || 0}
                        onValueChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, quantity: e.value ?? 0 });
                            }
                        }}
                        useGrouping={false} placeholder="1000"
                        mode="decimal" minFractionDigits={5}
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Settlement</label>
                    <Calendar value={data?.tradeDate ? new Date(data.tradeDate) : null}
                        onChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, tradeDate: e.value as Date | null });
                            }
                        }}
                        showIcon dateFormat="dd/mm/yy" />
                </div>
                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Description</label>
                    <InputText value={data?.description || ''}
                        onChange={(e) => {
                            // Verifichiamo che data non sia null prima di chiamare onChange
                            if (data) {
                                onChange({ ...data, description: e.target.value ?? '---' });
                            }
                        }}
                        placeholder="Short description..." />

                </div>
            </div>
        </div>
    );
};
