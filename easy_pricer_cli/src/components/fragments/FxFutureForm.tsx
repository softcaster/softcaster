import { Dropdown } from 'primereact/dropdown';
import { SideSelector } from './SideSelector';
import type { FxFutureMasterData, PositionMasterData, FinacialTxn, Counterparty } from '../data/schema';

interface FxFutFormProps {
    data: FinacialTxn | null;
    fxFutures: FxFutureMasterData[];
    positions: PositionMasterData[],
    counterparties: Counterparty[],
    onChange: (data: FinacialTxn) => void;
}

//Il modulo riceve l'oggetto trade come prop
export const FxFutureForm = ({ data, fxFutures, positions, counterparties, onChange }: FxFutFormProps) => {
    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            <div className="grid p-fluid">

                <div className="col-12 md:col-3">
                    <label className="text-sm font-bold block mb-2">Currency Pair</label>

                    <Dropdown
                        value={data?.masterData}
                        options={fxFutures}
                        dataKey="idMasterData" // Indispensabile per confrontare gli oggetti
                        optionLabel="code"
                        onChange={(e) => {
                            if (data) {
                                console.log("Nuovo MasterData selezionato:", e.value); // Verifica cosa arriva qui
                                onChange({ ...data, masterData: e.value });
                            }
                        }}
                        placeholder="Select Currency Pair"
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
                                </div>
                            );
                        }}
                    />
                </div>
            </div>
        </div>
    );
};

