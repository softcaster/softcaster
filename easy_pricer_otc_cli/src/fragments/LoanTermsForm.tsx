import { useEffect, useState, type ReactNode } from 'react';
import { Button } from 'primereact/button';
import { Calendar } from 'primereact/calendar';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { fetchCurrencies, fetchDiscountCurves } from '../api/services';
import type { CurrencyDto, DiscountCurveDto, LoanFeeInput } from '../api/types';
import { Choice, Field, FormTabs, Grid, NumberInput, type TabDef } from '../components/ui';
import { useEnums } from '../model/enums';
import type { LoanFormState } from '../model/loanForm';
import { RATE_TYPES } from '../model/options';

/**
 * Form a schede di un mutuo: termini, tasso variabile, spese, registrazione
 * (booking, solo per un contratto nuovo), valutazione e risultati.
 * readOnly blocca i termini (contratto registrato); la valutazione resta
 * modificabile. rateTypeLocked nasconde la scelta fisso/variabile
 * (viste Loan Fixed e Loan Floating). pricing = false toglie valutazione e
 * risultati (data entry dei trade: il pricing ha le sue pagine).
 */
export const LoanFormTabs = ({ value, onChange, readOnly = false, rateTypeLocked = false, pricing = true, booking, cashFlows, results, tab, onTabChange }: {
    value: LoanFormState;
    onChange: (v: LoanFormState) => void;
    readOnly?: boolean;
    rateTypeLocked?: boolean;
    pricing?: boolean;
    booking?: ReactNode;
    /** piano salvato, solo per un contratto registrato */
    cashFlows?: ReactNode;
    results?: ReactNode;
    tab: string;
    onTabChange: (key: string) => void;
}) => {
    const set = <K extends keyof LoanFormState>(key: K, v: LoanFormState[K]) => onChange({ ...value, [key]: v });
    const setFee = (index: number, fee: LoanFeeInput) => set('fees', value.fees.map((x, i) => (i === index ? fee : x)));
    const floating = value.rateType === 'FLOATING';
    const enums = useEnums('amortizationSchedule', 'loanFrequency', 'daycountBasis', 'rollConvention', 'loanFeeType', 'feeBasis', 'feeTiming');
    const [curves, setCurves] = useState<DiscountCurveDto[]>([]);
    const [currencies, setCurrencies] = useState<CurrencyDto[]>([]);

    useEffect(() => {
        fetchCurrencies()
            .then((list) => setCurrencies([...list].sort((a, b) => a.isoCode.localeCompare(b.isoCode))))
            .catch(() => setCurrencies([]));
    }, []);

    // valute dell'anagrafica; quella del form resta visibile anche se la lista non e' caricata
    const currencyOptions = (currencies.some((c) => c.isoCode === value.currency) || !value.currency
        ? currencies
        : [{ idCurrency: 0, isoCode: value.currency, description: null }, ...currencies])
        .map((c) => ({ label: c.isoCode, value: c.isoCode }));

    useEffect(() => {
        if (pricing) {
            fetchDiscountCurves().then(setCurves).catch(() => setCurves([]));
        }
    }, [pricing]);

    // curve nella valuta del mutuo (o senza valuta), piu' quella gia' scelta
    const curveOptions = curves
        .filter((c) => !c.currency || c.currency === value.currency || c.code === value.discountCurve)
        .map((c) => ({
            label: `${c.code}${c.description ? ` — ${c.description}` : ''}${c.kind === 'SPREAD' ? ' (spread)' : ''}`,
            value: c.code,
        }));

    const terms = (
        <Grid>
            {!rateTypeLocked && (
                <Field label="Rate" col={3}>
                    <Choice value={value.rateType} options={RATE_TYPES} disabled={readOnly} onChange={(v) => set('rateType', v)} />
                </Field>
            )}
            <Field label="Principal" col={3}>
                <NumberInput value={value.principalAmount} disabled={readOnly} onChange={(v) => set('principalAmount', v)} />
            </Field>
            <Field label="Currency" col={2}>
                <Dropdown value={value.currency} options={currencyOptions} disabled={readOnly} filter onChange={(e) => set('currency', e.value)} />
            </Field>
            <Field label={floating ? 'Current rate %' : 'Nominal rate %'} col={2}
                hint={floating ? 'Index + spread at the date: applied to future installments' : 'Annual nominal rate'}>
                <NumberInput value={value.annualRate} disabled={readOnly} onChange={(v) => set('annualRate', v)} digits={4} maxDigits={6} />
            </Field>
            <Field label="Pre-amortization" col={2} hint="Initial interest-only installments">
                <NumberInput value={value.preAmortizationPeriods} disabled={readOnly} onChange={(v) => set('preAmortizationPeriods', v ?? 0)}
                    digits={0} grouping={false} min={0} />
            </Field>
            <Field label="Disbursement" col={3}>
                <Calendar value={value.disbursementDate} disabled={readOnly} onChange={(e) => set('disbursementDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
            </Field>
            <Field label="First payment" col={3} hint="Empty = disbursement + one period; if it does not fall on a full period, the first period is interest-only">
                <Calendar value={value.firstPaymentDate} disabled={readOnly} onChange={(e) => set('firstPaymentDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon showButtonBar />
            </Field>
            <Field label="Maturity" col={3}>
                <Calendar value={value.maturityDate} disabled={readOnly} onChange={(e) => set('maturityDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
            </Field>
            <Field label="Installment frequency" col={3}>
                <Dropdown value={value.frequency} options={enums.loanFrequency} disabled={readOnly} onChange={(e) => set('frequency', e.value)} />
            </Field>
            <Field label="Amortization" col={4}>
                <Dropdown value={value.amortization} options={enums.amortizationSchedule} disabled={readOnly} onChange={(e) => set('amortization', e.value)} />
            </Field>
            <Field label="Daycount" col={4}>
                <Dropdown value={value.daycount} options={enums.daycountBasis} disabled={readOnly} onChange={(e) => set('daycount', e.value)} />
            </Field>
            <Field label="Payment dates" col={4}>
                <Dropdown value={value.rollConvention} options={enums.rollConvention} disabled={readOnly} onChange={(e) => set('rollConvention', e.value)} />
            </Field>
            <Field label="Description" col={12} hint="Empty = generated from the terms">
                <InputText value={value.description} disabled={readOnly} onChange={(e) => set('description', e.target.value)} />
            </Field>
        </Grid>
    );

    const floatingTerms = (
        <Grid>
            <Field label="Index" col={3} hint="Reference index code (optional)">
                <InputText value={value.indexCode} disabled={readOnly} onChange={(e) => set('indexCode', e.target.value)} />
            </Field>
            <Field label="Spread %" col={3}>
                <NumberInput value={value.spreadPct} disabled={readOnly} onChange={(v) => set('spreadPct', v)} digits={4} />
            </Field>
            <Field label="Floor %" col={3}>
                <NumberInput value={value.floorPct} disabled={readOnly} onChange={(v) => set('floorPct', v)} digits={4} />
            </Field>
            <Field label="Cap %" col={3}>
                <NumberInput value={value.capPct} disabled={readOnly} onChange={(v) => set('capPct', v)} digits={4} />
            </Field>
        </Grid>
    );

    const fees = (
        <>
            {value.fees.length > 0 && (
                <Grid>
                    <div className="col-3 pb-0 font-bold text-sm">Fee</div>
                    <div className="col-3 pb-0 font-bold text-sm">Basis</div>
                    <div className="col-3 pb-0 font-bold text-sm">Timing</div>
                    <div className="col-2 pb-0 font-bold text-sm text-right">Value</div>
                </Grid>
            )}
            {value.fees.map((fee, i) => (
                <Grid key={i}>
                    <div className="col-3">
                        <Dropdown value={fee.type} options={enums.loanFeeType} disabled={readOnly} onChange={(e) => setFee(i, { ...fee, type: e.value })} />
                    </div>
                    <div className="col-3">
                        <Dropdown value={fee.basis} options={enums.feeBasis} disabled={readOnly} onChange={(e) => setFee(i, { ...fee, basis: e.value })} />
                    </div>
                    <div className="col-3">
                        <Dropdown value={fee.timing} options={enums.feeTiming} disabled={readOnly} onChange={(e) => setFee(i, { ...fee, timing: e.value })} />
                    </div>
                    <div className="col-2">
                        <NumberInput value={fee.value} disabled={readOnly} onChange={(v) => setFee(i, { ...fee, value: v })}
                            digits={fee.basis === 'PERCENT' ? 4 : 2} />
                    </div>
                    <div className="col-1">
                        {!readOnly && <Button icon="pi pi-trash" text severity="danger" onClick={() => set('fees', value.fees.filter((_, k) => k !== i))} />}
                    </div>
                </Grid>
            ))}
            {!readOnly && (
                <Button label="Add fee" icon="pi pi-plus" text
                    onClick={() => set('fees', [...value.fees, { type: 'INCIDENTAL', basis: 'AMOUNT', timing: 'UPFRONT', value: 0 }])} />
            )}
        </>
    );

    const valuation = (
        <Grid>
            <Field label="Date" col={3} hint="Empty = official date">
                <Calendar value={value.valuationDate} onChange={(e) => set('valuationDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon showButtonBar />
            </Field>
            <Field label="Discount curve" col={3} hint="Yield and spread curves in the loan currency. Empty = configured DISCOUNT curve, otherwise the flat rate">
                <Dropdown value={value.discountCurve || null} options={curveOptions} filter showClear placeholder="Configured curve"
                    emptyMessage={`No curves in ${value.currency}`} onChange={(e) => set('discountCurve', e.value ?? '')} />
            </Field>
            <Field label="Discount rate %" col={3} hint="Continuous, used when there is no curve">
                <NumberInput value={value.discountRatePct} onChange={(v) => set('discountRatePct', v)} digits={4} />
            </Field>
            <Field label="Credit spread %" col={3}>
                <NumberInput value={value.creditSpreadPct} onChange={(v) => set('creditSpreadPct', v)} digits={4} />
            </Field>
        </Grid>
    );

    const tabs: TabDef[] = [
        { key: 'terms', header: 'Terms', icon: 'pi pi-file-edit', content: terms },
        ...(floating ? [{ key: 'floating', header: 'Floating rate', icon: 'pi pi-chart-line', content: floatingTerms }] : []),
        { key: 'fees', header: `Fees (${value.fees.length})`, icon: 'pi pi-wallet', content: fees },
        ...(booking ? [{ key: 'booking', header: 'Booking', icon: 'pi pi-book', content: booking }] : []),
        ...(cashFlows ? [{ key: 'cashflows', header: 'Cash flows', icon: 'pi pi-list', content: cashFlows }] : []),
        ...(pricing ? [
            { key: 'valuation', header: 'Valuation', icon: 'pi pi-sliders-h', content: valuation },
            { key: 'results', header: 'Results', icon: 'pi pi-chart-bar', content: results, disabled: !results },
        ] : []),
    ];

    return <FormTabs tabs={tabs} active={tab} onChange={onTabChange} />;
};
