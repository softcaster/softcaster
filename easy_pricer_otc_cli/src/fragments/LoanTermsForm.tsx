import { Button } from 'primereact/button';
import { Calendar } from 'primereact/calendar';
import { Dropdown } from 'primereact/dropdown';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { SelectButton } from 'primereact/selectbutton';
import type { LoanFeeInput } from '../api/types';
import { Field, Section } from '../components/ui';
import type { LoanFormState } from '../model/loanForm';
import {
    AMORTIZATIONS, DAYCOUNTS, FEE_BASES, FEE_TIMINGS, FEE_TYPES, LOAN_FREQUENCIES, RATE_TYPES, ROLLS,
} from '../model/options';

/**
 * Termini, spese, condizioni del variabile e parametri di valutazione di un
 * mutuo. readOnly blocca i termini (contratto registrato); la valutazione
 * resta modificabile. rateTypeLocked nasconde la scelta fisso/variabile
 * (viste Loan Fixed e Loan Floating).
 */
export const LoanTermsForm = ({ value, onChange, readOnly = false, rateTypeLocked = false }: {
    value: LoanFormState;
    onChange: (v: LoanFormState) => void;
    readOnly?: boolean;
    rateTypeLocked?: boolean;
}) => {
    const set = <K extends keyof LoanFormState>(key: K, v: LoanFormState[K]) => onChange({ ...value, [key]: v });
    const setFee = (index: number, fee: LoanFeeInput) => set('fees', value.fees.map((x, i) => (i === index ? fee : x)));
    const floating = value.rateType === 'FLOATING';

    return (
        <>
            <Section title="Termini">
                {!rateTypeLocked && (
                    <Field label="Tasso" col={3}>
                        <SelectButton value={value.rateType} options={RATE_TYPES} disabled={readOnly} onChange={(e) => e.value && set('rateType', e.value)} />
                    </Field>
                )}
                <Field label="Capitale" col={3}>
                    <InputNumber value={value.principalAmount} disabled={readOnly} onValueChange={(e) => set('principalAmount', e.value ?? null)}
                        mode="decimal" locale="it-IT" minFractionDigits={2} />
                </Field>
                <Field label="Valuta" col={2}>
                    <InputText value={value.currency} disabled={readOnly} onChange={(e) => set('currency', e.target.value.toUpperCase())} maxLength={3} />
                </Field>
                <Field label={floating ? 'Tasso corrente %' : 'TAN %'} col={2}
                    hint={floating ? 'Indice + spread alla data: applicato alle rate future' : 'Tasso annuo nominale'}>
                    <InputNumber value={value.annualRate} disabled={readOnly} onValueChange={(e) => set('annualRate', e.value ?? null)}
                        mode="decimal" locale="it-IT" minFractionDigits={2} maxFractionDigits={6} suffix=" %" />
                </Field>
                <Field label="Preammortamento" col={2} hint="Rate iniziali di soli interessi">
                    <InputNumber value={value.preAmortizationPeriods} disabled={readOnly} onValueChange={(e) => set('preAmortizationPeriods', e.value ?? 0)} min={0} />
                </Field>
                <Field label="Erogazione" col={3}>
                    <Calendar value={value.disbursementDate} disabled={readOnly} onChange={(e) => set('disbursementDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
                </Field>
                <Field label="Prima rata" col={3} hint="Vuota = erogazione + un periodo; se non cade a un periodo pieno il primo periodo e' di soli interessi">
                    <Calendar value={value.firstPaymentDate} disabled={readOnly} onChange={(e) => set('firstPaymentDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon showButtonBar />
                </Field>
                <Field label="Scadenza" col={3}>
                    <Calendar value={value.maturityDate} disabled={readOnly} onChange={(e) => set('maturityDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
                </Field>
                <Field label="Frequenza rate" col={3}>
                    <Dropdown value={value.frequency} options={LOAN_FREQUENCIES} disabled={readOnly} onChange={(e) => set('frequency', e.value)} />
                </Field>
                <Field label="Piano" col={4}>
                    <Dropdown value={value.amortization} options={AMORTIZATIONS} disabled={readOnly} onChange={(e) => set('amortization', e.value)} />
                </Field>
                <Field label="Daycount" col={4}>
                    <Dropdown value={value.daycount} options={DAYCOUNTS} disabled={readOnly} onChange={(e) => set('daycount', e.value)} />
                </Field>
                <Field label="Date di pagamento" col={4}>
                    <Dropdown value={value.rollConvention} options={ROLLS} disabled={readOnly} onChange={(e) => set('rollConvention', e.value)} />
                </Field>
                <Field label="Descrizione" col={12} hint="Vuota = generata dai termini">
                    <InputText value={value.description} disabled={readOnly} onChange={(e) => set('description', e.target.value)} />
                </Field>
            </Section>

            {floating && (
                <Section title="Tasso variabile">
                    <Field label="Indice" col={3} hint="Codice dell'indice di riferimento (facoltativo)">
                        <InputText value={value.indexCode} disabled={readOnly} onChange={(e) => set('indexCode', e.target.value)} />
                    </Field>
                    <Field label="Spread %" col={3}>
                        <InputNumber value={value.spreadPct} disabled={readOnly} onValueChange={(e) => set('spreadPct', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={4} suffix=" %" />
                    </Field>
                    <Field label="Floor %" col={3}>
                        <InputNumber value={value.floorPct} disabled={readOnly} onValueChange={(e) => set('floorPct', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={4} suffix=" %" />
                    </Field>
                    <Field label="Cap %" col={3}>
                        <InputNumber value={value.capPct} disabled={readOnly} onValueChange={(e) => set('capPct', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={4} suffix=" %" />
                    </Field>
                </Section>
            )}

            <Section title="Spese">
                <div className="col-12">
                    {value.fees.map((fee, i) => (
                        <div key={i} className="flex gap-2 align-items-center mb-2 flex-wrap">
                            <Dropdown value={fee.type} options={FEE_TYPES} disabled={readOnly} onChange={(e) => setFee(i, { ...fee, type: e.value })} className="w-12rem" />
                            <Dropdown value={fee.basis} options={FEE_BASES} disabled={readOnly} onChange={(e) => setFee(i, { ...fee, basis: e.value })} className="w-9rem" />
                            <Dropdown value={fee.timing} options={FEE_TIMINGS} disabled={readOnly} onChange={(e) => setFee(i, { ...fee, timing: e.value })} className="w-11rem" />
                            <InputNumber value={fee.value} disabled={readOnly} onValueChange={(e) => setFee(i, { ...fee, value: e.value ?? null })} className="w-10rem"
                                mode="decimal" locale="it-IT" maxFractionDigits={4} suffix={fee.basis === 'PERCENT' ? ' %' : ''} />
                            {!readOnly && <Button icon="pi pi-trash" text severity="danger" onClick={() => set('fees', value.fees.filter((_, k) => k !== i))} />}
                        </div>
                    ))}
                    {!readOnly && (
                        <Button label="Aggiungi spesa" icon="pi pi-plus" text
                            onClick={() => set('fees', [...value.fees, { type: 'INCIDENTAL', basis: 'AMOUNT', timing: 'UPFRONT', value: 0 }])} />
                    )}
                </div>
            </Section>

            <Section title="Valutazione">
                <Field label="Data" col={3} hint="Vuota = data ufficiale">
                    <Calendar value={value.valuationDate} onChange={(e) => set('valuationDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon showButtonBar />
                </Field>
                <Field label="Curva di sconto" col={3} hint="Vuota = curva DISCOUNT configurata, altrimenti il tasso piatto">
                    <InputText value={value.discountCurve} onChange={(e) => set('discountCurve', e.target.value)} />
                </Field>
                <Field label="Tasso di sconto %" col={3} hint="Continuo, se manca la curva">
                    <InputNumber value={value.discountRatePct} onValueChange={(e) => set('discountRatePct', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={4} suffix=" %" />
                </Field>
                <Field label="Spread di credito %" col={3}>
                    <InputNumber value={value.creditSpreadPct} onValueChange={(e) => set('creditSpreadPct', e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={4} suffix=" %" />
                </Field>
            </Section>
        </>
    );
};
