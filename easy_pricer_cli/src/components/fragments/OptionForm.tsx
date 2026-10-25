import { InputNumber } from 'primereact/inputnumber';
import { Calendar } from 'primereact/calendar';
import { InputText } from 'primereact/inputtext';
import { SideSelector } from './SideSelector';
import { InstrumentField, CounterpartyField, PositionField } from './FormFields';
import type { FinancialTxnDto, GenericMasterDataDto } from '../services/dto';

interface OptionFormProps {
    data: FinancialTxnDto | null;
    masterDataList: GenericMasterDataDto[]; // serie di opzioni della famiglia
    positions: GenericMasterDataDto[];
    counterparties: GenericMasterDataDto[];
    onChange: (value: FinancialTxnDto) => void;
}

// Deal su una serie di opzioni (su cambio, su titolo, su future):
// prezzo = premio unitario, quantita' = numero di contratti
export const OptionForm = ({ data, masterDataList, positions, counterparties, onChange }: OptionFormProps) => {

    if (!data) return null;

    // Trovano l'oggetto intero nelle liste usando gli ID del DTO
    const currentInstrument = masterDataList.find(m => m.genericMasterDataId === data.masterDataId) || null;
    const currentCounterparty = counterparties.find(c => c.genericMasterDataId === data.counterpartyId) || null;
    const currentPosition = positions.find(p => p.genericMasterDataId === data.positionMdId) || null;

    return (
        <div className="surface-ground p-3 border-bottom-1 surface-border">
            <div className="grid p-fluid align-items-end">

                <InstrumentField
                    label="Option Series"
                    value={currentInstrument}
                    options={masterDataList}
                    onChange={(selected: GenericMasterDataDto | null) => {
                        onChange({
                            ...data,
                            masterDataId: selected ? selected.genericMasterDataId : null,
                            masterDataCode: selected ? selected.code : null,
                            masterDataDesc: selected ? selected.description : null
                        });
                    }}
                />

                <div className="col-12 md:col-3">
                    <SideSelector
                        value={data.txnSide ?? null}
                        onChange={(val: number) => onChange({ ...data, txnSide: val })}
                    />
                </div>

                <CounterpartyField
                    value={currentCounterparty}
                    options={counterparties}
                    onChange={(selected: GenericMasterDataDto | null) => {
                        onChange({
                            ...data,
                            counterpartyId: selected ? selected.genericMasterDataId : null,
                            counterpartyCode: selected ? selected.code : null,
                            counterpartyDesc: selected ? selected.description : null
                        });
                    }}
                />

                <PositionField
                    value={currentPosition}
                    options={positions}
                    onChange={(selected: GenericMasterDataDto | null) => {
                        onChange({
                            ...data,
                            positionMdId: selected ? selected.genericMasterDataId : null,
                            positionMdCode: selected ? selected.code : null
                        });
                    }}
                />

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Premium</label>
                    <InputNumber value={data.price}
                        onValueChange={(e) => onChange({ ...data, price: e.value ?? 0 })}
                        mode="decimal" minFractionDigits={6} placeholder="0.000000"
                        tooltip="Unit premium, in the quotation of the series (e.g. USD per EUR, % of nominal for bond options)"
                        tooltipOptions={{ position: 'top' }}
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Contracts</label>
                    <InputNumber value={data.quantity || 0}
                        onValueChange={(e) => onChange({ ...data, quantity: e.value ?? 0 })}
                        useGrouping={false} placeholder="1"
                        mode="decimal" minFractionDigits={0} maxFractionDigits={2}
                        inputStyle={{ textAlign: 'right' }}
                        className="w-full" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Trade Date</label>
                    <Calendar value={data.tradeDate ? new Date(data.tradeDate) : null}
                        onChange={(e) => onChange({ ...data, tradeDate: (e.value as Date) ?? new Date() })}
                        showIcon dateFormat="dd/mm/yy" />
                </div>

                <div className="col-12 md:col-3">
                    <label className="block mb-2 font-bold text-sm">Description</label>
                    <InputText value={data.description || ''}
                        onChange={(e) => onChange({ ...data, description: e.target.value ?? '---' })}
                        placeholder="Short description..." />
                </div>
            </div>
        </div>
    );
};
