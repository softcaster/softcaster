import { Dropdown } from 'primereact/dropdown';
import { InputNumber } from 'primereact/inputnumber';
import { Calendar } from 'primereact/calendar';
import { InputText } from 'primereact/inputtext';
import { SideSelector } from './SideSelector';
import type { FxFutureMasterData, PositionMasterData, FinancialTxn, Counterparty } from '../data/schema';

interface FxFutFormProps {
    data: FinancialTxn | null;
    fxFutures: FxFutureMasterData[];
    positions: PositionMasterData[],
    counterparties: Counterparty[],
    onChange: (data: FinancialTxn) => void;
}

//Il modulo riceve l'oggetto trade come prop
export const FxFutureForm = ({ data, fxFutures, positions, counterparties, onChange }: FxFutFormProps) => {
    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            <div className="grid p-fluid">

                <div className="col-12 md:col-3">
                    <label className="text-sm font-bold block mb-2">Contract Code</label>

                    <Dropdown
                        value={data?.masterData}
                        options={fxFutures}
                        dataKey="idMasterData" // Indispensabile per confrontare gli oggetti
                        optionLabel="code"
                        onChange={(e) => {
                            if (data) {
                                //console.log("New MasterData selected:", e.value); // Verifica cosa arriva qui
                                onChange({ ...data, masterData: e.value });
                            }
                        }}
                        placeholder="Select Contract"
                        className="w-full"
                        filter

                        // 1. Cosa mostrare quando la combo è chiusa (elemento selezionato)
                        valueTemplate={(option, props) => {
                            if (option) {
                                return <span>{option.code}</span>;
                            }
                            return <span>{props.placeholder}</span>;
                        }}

                        // 2. Cosa mostrare nelle righe della lista quando è aperta
                        itemTemplate={(option) => {
                            return (
                                <div className="flex flex-column">
                                    <span className="font-bold">{option.code}</span>
                                    <small className="text-500">{option.description}</small>
                                </div>
                            );
                        }}
                    />
                </div>

                <div className="col-12 md:col-3">
                    <label className="text-sm font-bold block mb-2">Side</label>
                    <SideSelector                   
                        value={data?.txnSide ?? null}
                        onChange={(val: number) => onChange({ ...data!, txnSide: val })}
                    />
                </div>

                <div className="col-12 md:col-3">
                    <label className="text-sm font-bold block mb-2">Position</label>
                    <Dropdown
                        value={data?.positionMd}
                        options={positions} // La lista di ForexMasterData caricata dal server
                        dataKey="idPosition"
                        onChange={(e) => {
                            if (data) {
                                onChange({ ...data, positionMd: e.value });
                            }
                        }}
                        placeholder="Select Position"
                        filter
                        className="w-full"

                        // 1. Cosa mostrare quando la combo è chiusa (elemento selezionato)
                        valueTemplate={(option, props) => {
                            if (option) {
                                return <span>{option.code} - {option.description}</span>;
                            }
                            return <span>{props.placeholder}</span>;
                        }}

                        // 2. Cosa mostrare nelle righe della lista quando è aperta
                        itemTemplate={(option) => {
                            return (
                                <div className="flex flex-column">
                                    <span className="font-bold">{option.code}</span>
                                    <small className="text-500">{option.description}</small>
                                </div>
                            );
                        }}

                    />
                </div>

                <div className="col-12 md:col-3">
                    <label className="text-sm font-bold block mb-2">Counterparty</label>
                    <Dropdown
                        value={data?.counterparty}
                        options={counterparties} // La lista di ForexMasterData caricata dal server
                        dataKey="idCounterparty"
                        onChange={(e) => {
                            if (data) {
                                onChange({ ...data, counterparty: e.value });
                            }
                        }}
                        placeholder="Select Counterparty"
                        filter
                        className="w-full"

                        // 1. Cosa mostrare quando la combo è chiusa (elemento selezionato)
                        valueTemplate={(option, props) => {
                            if (option) {
                                return <span>{option.code} - {option.description}</span>;
                            }
                            return <span>{props.placeholder}</span>;
                        }}

                        // 2. Cosa mostrare nelle righe della lista quando è aperta
                        itemTemplate={(option) => {
                            return (
                                <div className="flex flex-column">
                                    <span className="font-bold">{option.code}</span>
                                    <small className="text-500">{option.description}</small>
                                </div>
                            );
                        }}

                    />
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

