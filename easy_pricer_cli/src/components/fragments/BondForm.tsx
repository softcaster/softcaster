import { InputNumber } from 'primereact/inputnumber';
import { Calendar } from 'primereact/calendar';
import { InputText } from 'primereact/inputtext';
import { SideSelector } from './SideSelector';
import { InstrumentField, CounterpartyField, PositionField } from './FormFields';
import type { FinancialTxnDto } from '../services/dto';
// import { useEffect } from 'react'; // <-- Importa useEffect per gestire l'inizializzazione
// import { useSystemDate } from '../../context/SystemDateContext';

interface BondFormProps {
    data: FinancialTxnDto | null;
    masterDataList: any[]; // Lista di ForexMasterData[]
    positions: any[];
    counterparties: any[];
    onChange: (value: FinancialTxnDto) => void;
}
//Il modulo riceve l'oggetto trade come prop
export const BondForm = ({ data, masterDataList, positions, counterparties, onChange }: BondFormProps) => {

    /*
    const { businessDate, loading } = useSystemDate();
    // EFFECT DI INIZIALIZZAZIONE: Se stiamo creando un nuovo trade (tradeDate vuoto),
    // iniettiamo automaticamente la data ufficiale di sistema non appena è disponibile.
    useEffect(() => {
        if (data && data.financialTxnId === 0 && businessDate && !loading) {
            // Spezziamo la stringa, esempio"2026-06-21". per evitare problemi di fuso orario (timezone)
            const [year, month, day] = businessDate.split('-').map(Number);
            const officialSystemDate = new Date(year, month - 1, day);
            if (!data.tradeDate || data.tradeDate.getTime() !== officialSystemDate.getTime()) {
                onChange({
                    ...data,
                    tradeDate: officialSystemDate,
                    settlement: officialSystemDate // Se ti serve allineare anche la data di settlement
                });
            }
        }
    }, [businessDate, loading, data?.financialTxnId]); // Scatta all'avvio o se cambia la transazione aperta
*/
    if (!data) return null;

    // 1. ADATTATORI IN LETTURA: Trovano l'oggetto intero nelle liste usando gli ID del DTO
    const currentInstrument = masterDataList.find(m => m.genericMasterDataId === data.masterDataId) || null;
    const currentCounterparty = counterparties.find(c => c.genericMasterDataId === data.counterpartyId) || null;
    const currentPosition = positions.find(p => p.genericMasterDataId === data.positionMdId) || null;

    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            <div className="grid p-fluid">

                {/* 1. Anagrafica (Dinamica) */}
                <InstrumentField
                    label="Bond Contract"
                    value={currentInstrument} // <-- Passa l'intero oggetto ForexMasterData trovato
                    options={masterDataList}
                    onChange={(selectedInstrument: any) => {
                        // Sincronizza l'ID e i campi descrittivi ibridi nel DTO
                        onChange({
                            ...data,
                            masterDataId: selectedInstrument ? selectedInstrument.genericMasterDataId : null,
                            masterDataCode: selectedInstrument ? selectedInstrument.code : null,
                            masterDataDesc: selectedInstrument ? selectedInstrument.description : null
                        });
                    }}
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
                    value={currentCounterparty} // <-- Passa l'oggetto Counterparty intero
                    options={counterparties}
                    onChange={(selectedCounterparty: any) => {
                        onChange({
                            ...data,
                            counterpartyId: selectedCounterparty ? selectedCounterparty.genericMasterDataId : null,
                            counterpartyCode: selectedCounterparty ? selectedCounterparty.code : null,
                            counterpartyDesc: selectedCounterparty ? selectedCounterparty.description : null
                        });
                    }}
                />

                <PositionField
                    value={currentPosition} // <-- Passa l'oggetto PositionMasterData intero
                    options={positions}
                    onChange={(selectedPosition: any) => {
                        onChange({
                            ...data,
                            positionMdId: selectedPosition ? selectedPosition.genericMasterDataId : null,
                            positionMdCode: selectedPosition ? selectedPosition.code : null
                        });
                    }}
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
                                onChange({ ...data, tradeDate: (e.value as Date) ?? new Date() });
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
