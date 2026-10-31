import { useEffect, useState } from 'react';
import { Calendar } from 'primereact/calendar';
import { Dropdown } from 'primereact/dropdown';
import { SelectButton } from 'primereact/selectbutton';
import { fetchCounterparties, fetchPositions } from '../api/services';
import type { GenericMasterDataDto } from '../api/types';
import { Field, Section } from './ui';

export interface Registration {
    side: string;
    counterpartyId: number | null;
    positionMdId: number | null;
    tradeDate: Date | null;
}

const SIDES = [
    { label: 'BUY', value: 'BUY' },
    { label: 'SELL', value: 'SELL' },
];

/**
 * Dati dell'operazione per la registrazione: lato (punto di vista della
 * banca), controparte, posizione, data.
 */
export const RegistrationPanel = ({ value, onChange, sideHint }: {
    value: Registration;
    onChange: (r: Registration) => void;
    sideHint: string;
}) => {
    const [counterparties, setCounterparties] = useState<GenericMasterDataDto[]>([]);
    const [positions, setPositions] = useState<GenericMasterDataDto[]>([]);

    useEffect(() => {
        fetchCounterparties().then(setCounterparties).catch(() => setCounterparties([]));
        fetchPositions().then(setPositions).catch(() => setPositions([]));
    }, []);

    const option = (d: GenericMasterDataDto) => ({ label: `${d.code} — ${d.description}`, value: d.genericMasterDataId });

    return (
        <Section title="Operazione (per la registrazione)">
            <Field label="Lato" hint={sideHint} col={3}>
                <SelectButton value={value.side} options={SIDES} onChange={(e) => e.value && onChange({ ...value, side: e.value })} />
            </Field>
            <Field label="Controparte" col={3}>
                <Dropdown value={value.counterpartyId} options={counterparties.map(option)} filter placeholder="Controparte"
                    onChange={(e) => onChange({ ...value, counterpartyId: e.value })} />
            </Field>
            <Field label="Posizione" col={3}>
                <Dropdown value={value.positionMdId} options={positions.map(option)} filter placeholder="Posizione"
                    onChange={(e) => onChange({ ...value, positionMdId: e.value })} />
            </Field>
            <Field label="Data operazione" col={3} hint="Vuota = data ufficiale (accumulator) o erogazione (mutui)">
                <Calendar value={value.tradeDate} onChange={(e) => onChange({ ...value, tradeDate: e.value ?? null })}
                    dateFormat="dd/mm/yy" showIcon showButtonBar />
            </Field>
        </Section>
    );
};
