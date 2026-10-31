import { useEffect, useState } from 'react';
import { Calendar } from 'primereact/calendar';
import { Dropdown } from 'primereact/dropdown';
import { fetchCounterparties, fetchPositions } from '../api/services';
import type { GenericMasterDataDto } from '../api/types';
import { SIDES } from '../model/options';
import { Choice, Field, Section } from './ui';

export interface Registration {
    side: string;
    counterpartyId: number | null;
    positionMdId: number | null;
    tradeDate: Date | null;
}

/**
 * Dati dell'operazione per la registrazione: lato (punto di vista della
 * banca), controparte, posizione, data.
 */
export const RegistrationPanel = ({ value, onChange, sideHint, readOnly = false }: {
    value: Registration;
    onChange: (r: Registration) => void;
    sideHint: string;
    /** transazione gia' registrata: dati in sola lettura */
    readOnly?: boolean;
}) => {
    const [counterparties, setCounterparties] = useState<GenericMasterDataDto[]>([]);
    const [positions, setPositions] = useState<GenericMasterDataDto[]>([]);

    useEffect(() => {
        fetchCounterparties().then(setCounterparties).catch(() => setCounterparties([]));
        fetchPositions().then(setPositions).catch(() => setPositions([]));
    }, []);

    const option = (d: GenericMasterDataDto) => ({ label: `${d.code} — ${d.description}`, value: d.genericMasterDataId });

    return (
        <Section title="Trade">
            <Field label="Side" hint={sideHint} col={3}>
                <Choice value={value.side} options={SIDES} disabled={readOnly} onChange={(side) => onChange({ ...value, side })} />
            </Field>
            <Field label="Counterparty" col={3}>
                <Dropdown value={value.counterpartyId} options={counterparties.map(option)} disabled={readOnly} filter placeholder="Counterparty"
                    onChange={(e) => onChange({ ...value, counterpartyId: e.value })} />
            </Field>
            <Field label="Position" col={3}>
                <Dropdown value={value.positionMdId} options={positions.map(option)} disabled={readOnly} filter placeholder="Position"
                    onChange={(e) => onChange({ ...value, positionMdId: e.value })} />
            </Field>
            <Field label="Trade date" col={3} hint="Empty = official date (accumulators) or disbursement (loans)">
                <Calendar value={value.tradeDate} disabled={readOnly} onChange={(e) => onChange({ ...value, tradeDate: e.value ?? null })}
                    dateFormat="dd/mm/yy" showIcon showButtonBar />
            </Field>
        </Section>
    );
};
