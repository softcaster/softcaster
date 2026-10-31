import { Calendar } from 'primereact/calendar';
import { Checkbox } from 'primereact/checkbox';
import { Dropdown } from 'primereact/dropdown';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { SelectButton } from 'primereact/selectbutton';
import { Field, Section } from '../components/ui';
import type { AccumulatorFormState } from '../model/accumulatorForm';
import {
    ACC_FREQUENCIES, ACC_TYPES, GUARANTEES, KI_EFFECTS, OBSERVATIONS, PREMIUM_MODES, QUANTITY_MODES, SETTLEMENTS,
} from '../model/options';

/**
 * Termini e parametri di valutazione di un accumulator; showPremium per la
 * registrazione. readOnly blocca i termini (contratto registrato).
 */
export const AccumulatorTermsForm = ({ value, onChange, assetClass, readOnly = false, showPremium = false }: {
    value: AccumulatorFormState;
    onChange: (v: AccumulatorFormState) => void;
    assetClass: string;
    readOnly?: boolean;
    showPremium?: boolean;
}) => {
    const set = <K extends keyof AccumulatorFormState>(key: K, v: AccumulatorFormState[K]) => onChange({ ...value, [key]: v });
    const priceDigits = { mode: 'decimal' as const, locale: 'it-IT', minFractionDigits: assetClass === 'FXA' ? 4 : 2, maxFractionDigits: 6 };

    return (
        <>
            <Section title="Termini">
                <Field label="Sottostante" col={3}
                    hint={assetClass === 'FXA' ? 'Coppia di cambi (es. EURUSD)' : 'Codice della scadenza del future su commodity'}>
                    <InputText value={value.underlyingCode} disabled={readOnly} onChange={(e) => set('underlyingCode', e.target.value.toUpperCase())} />
                </Field>
                <Field label="Tipo" col={4}>
                    <SelectButton value={value.accumulatorType} options={ACC_TYPES} disabled={readOnly} onChange={(e) => e.value && set('accumulatorType', e.value)} />
                </Field>
                <Field label="Regolamento" col={2}>
                    <Dropdown value={value.settlementType} options={SETTLEMENTS} disabled={readOnly} onChange={(e) => set('settlementType', e.value)} />
                </Field>
                <Field label="Frequenza regolamento" col={3}>
                    <Dropdown value={value.settlementFrequency} options={ACC_FREQUENCIES} disabled={readOnly} onChange={(e) => set('settlementFrequency', e.value)} />
                </Field>
                <Field label="Strike" col={2}>
                    <InputNumber value={value.strike} disabled={readOnly} onValueChange={(e) => set('strike', e.value ?? null)} {...priceDigits} />
                </Field>
                <Field label="Knock-out" col={2}>
                    <InputNumber value={value.koBarrier} disabled={readOnly} onValueChange={(e) => set('koBarrier', e.value ?? null)} {...priceDigits} />
                </Field>
                <Field label="Osservazione KO" col={2}>
                    <Dropdown value={value.koObservation} options={OBSERVATIONS} disabled={readOnly} onChange={(e) => set('koObservation', e.value)} />
                </Field>
                <Field label="Knock-in" col={3}>
                    <Dropdown value={value.kiEffect} options={KI_EFFECTS} disabled={readOnly} onChange={(e) => set('kiEffect', e.value)} />
                </Field>
                <Field label="Barriera KI" col={2}>
                    <InputNumber value={value.kiBarrier} onValueChange={(e) => set('kiBarrier', e.value ?? null)} disabled={readOnly || !value.kiEffect} {...priceDigits} />
                </Field>
                <Field label="Leva" col={1}>
                    <InputNumber value={value.leverage} disabled={readOnly} onValueChange={(e) => set('leverage', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={2} min={1} />
                </Field>
                <Field label="Quantità" col={3} hint="Massima senza leva (divisa per il numero di fixing) o per fixing">
                    <SelectButton value={value.quantityMode} options={QUANTITY_MODES} disabled={readOnly} onChange={(e) => e.value && set('quantityMode', e.value)} />
                </Field>
                <Field label="Importo" col={3}>
                    <InputNumber value={value.quantity} disabled={readOnly} onValueChange={(e) => set('quantity', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={2} />
                </Field>
                <Field label="Primo fixing" col={3}>
                    <Calendar value={value.firstFixingDate} disabled={readOnly} onChange={(e) => set('firstFixingDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
                </Field>
                <Field label="Ultimo fixing" col={3}>
                    <Calendar value={value.lastFixingDate} disabled={readOnly} onChange={(e) => set('lastFixingDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
                </Field>
                <Field label="Periodi garantiti" col={3} hint="Primi periodi di regolamento senza knock-out">
                    <InputNumber value={value.guaranteedPeriods} disabled={readOnly} onValueChange={(e) => set('guaranteedPeriods', e.value ?? 0)} min={0} />
                </Field>
                <Field label="Garanzia" col={4}>
                    <SelectButton value={value.guaranteeType} options={GUARANTEES} disabled={readOnly || !value.guaranteedPeriods}
                        onChange={(e) => e.value && set('guaranteeType', e.value)} />
                </Field>
                <Field label="Matura il fixing del KO" col={2}>
                    <Checkbox checked={value.koAccrual} disabled={readOnly} onChange={(e) => set('koAccrual', !!e.checked)} />
                </Field>
                <Field label="Descrizione" col={12} hint="Vuota = generata dai termini">
                    <InputText value={value.description} disabled={readOnly} onChange={(e) => set('description', e.target.value)} />
                </Field>
            </Section>

            <Section title="Valutazione">
                <Field label="Data" col={3} hint="Vuota = data ufficiale">
                    <Calendar value={value.valuationDate} onChange={(e) => set('valuationDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon showButtonBar />
                </Field>
                <Field label="Spot" col={3} hint="Vuoto = quotazione">
                    <InputNumber value={value.spot} onValueChange={(e) => set('spot', e.value ?? null)} {...priceDigits} />
                </Field>
                <Field label="Volatilità %" col={3} hint="Vuota = superficie di volatilita' del sottostante">
                    <InputNumber value={value.volatilityPct} onValueChange={(e) => set('volatilityPct', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={4} suffix=" %" />
                </Field>
                <Field label="Sentieri" col={3}>
                    <InputNumber value={value.paths} onValueChange={(e) => set('paths', e.value ?? null)} min={2} />
                </Field>
            </Section>

            {showPremium && (
                <Section title="Premio">
                    <Field label="Forma" col={3}>
                        <SelectButton value={value.premiumMode} options={PREMIUM_MODES} disabled={readOnly} onChange={(e) => e.value && set('premiumMode', e.value)} />
                    </Field>
                    <Field label={value.premiumMode === 'PERCENT' ? 'Premio % dell\'importo massimo × strike' : 'Premio (pagato da chi compra)'} col={4}>
                        <InputNumber value={value.premium} disabled={readOnly} onValueChange={(e) => set('premium', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={6} />
                    </Field>
                </Section>
            )}
        </>
    );
};
