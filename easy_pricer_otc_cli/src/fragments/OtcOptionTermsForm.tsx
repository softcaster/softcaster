import type { ReactNode } from 'react';
import { Calendar } from 'primereact/calendar';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import type { OptionPricingResponse } from '../api/types';
import { fmt, pct, tone } from '../components/format';
import { Choice, Field, FormTabs, Grid, KpiGrid, NumberInput, Section, type TabDef } from '../components/ui';
import { useEnums } from '../model/enums';
import { OPTION_PREMIUM_MODES } from '../model/options';
import { totalPremium, type OtcOptionAssetClass, type OtcOptionFormState } from '../model/otcOptionForm';

type FormProps = {
    value: OtcOptionFormState;
    onChange: (v: OtcOptionFormState) => void;
    readOnly?: boolean;
};

// Cut di scadenza piu' usati (ora locale, fuso)
const TIME_ZONES = ['America/New_York', 'Europe/London', 'Europe/Rome', 'Asia/Tokyo', 'UTC'];

const PREMIUM_LABELS: Record<string, string> = {
    AMOUNT: 'Premium (total, paid by the buyer)',
    UNIT: 'Premium per unit of underlying',
    PERCENT: 'Premium % of notional × strike',
};

/** Premio della registrazione: nella scheda Booking insieme ai dati dell'operazione. */
export const OtcOptionPremium = ({ value, onChange, readOnly = false, currency }: FormProps & { currency?: string }) => {
    const total = totalPremium(value);
    return (
        <Section title="Premium">
            <Field label="Form" col={4}>
                <Choice value={value.premiumMode} options={OPTION_PREMIUM_MODES} disabled={readOnly}
                    onChange={(v) => onChange({ ...value, premiumMode: v, premium: null })} />
            </Field>
            <Field label={PREMIUM_LABELS[value.premiumMode] ?? 'Premium'} col={4}>
                <NumberInput value={value.premium} disabled={readOnly} onChange={(v) => onChange({ ...value, premium: v })}
                    digits={value.premiumMode === 'AMOUNT' ? 2 : 4} maxDigits={value.premiumMode === 'UNIT' ? 8 : 6} />
            </Field>
            <Field label="Total premium" col={4} hint="Transaction price: one contract on the whole notional">
                <div className="p-inputtext p-component text-right surface-100">{fmt(total)}{currency ? ` ${currency}` : ''}</div>
            </Field>
        </Section>
    );
};

/**
 * Form a schede di un'opzione OTC: contratto, registrazione (booking),
 * valutazione e risultati. readOnly blocca i termini; pricing = false toglie
 * valutazione e risultati (data entry dei trade).
 */
export const OtcOptionFormTabs = ({ value, onChange, assetClass, readOnly = false, pricing = true, booking, results, tab, onTabChange }: FormProps & {
    assetClass: OtcOptionAssetClass;
    pricing?: boolean;
    booking?: ReactNode;
    results?: ReactNode;
    tab: string;
    onTabChange: (key: string) => void;
}) => {
    const set = <K extends keyof OtcOptionFormState>(key: K, v: OtcOptionFormState[K]) => onChange({ ...value, [key]: v });
    const fx = assetClass === 'OFX';
    const priceDigits = fx ? 5 : 4;
    const enums = useEnums('optionType', 'otcExerciseStyle', 'settlementType');

    const contract = (
        <>
            <Grid>
                <Field label="Underlying" col={3} hint={fx ? 'Currency pair (e.g. EURUSD)' : 'Code of the commodity future expiry'}>
                    <InputText value={value.underlyingCode} disabled={readOnly} onChange={(e) => set('underlyingCode', e.target.value.toUpperCase())} />
                </Field>
                <Field label="Type" col={3}>
                    <Choice value={value.optionType} options={enums.optionType ?? []} disabled={readOnly} onChange={(v) => set('optionType', v)} />
                </Field>
                <Field label="Exercise" col={3}>
                    <Dropdown value={value.exerciseStyle} options={enums.otcExerciseStyle} optionLabel="value" disabled={readOnly}
                        itemTemplate={(o: { value: string; label: string }) => <span title={o.label}>{o.value}</span>}
                        onChange={(e) => set('exerciseStyle', e.value)} />
                </Field>
                <Field label="Settlement" col={3} hint={fx ? 'Physical = exchange of the notional at the strike' : 'Physical = future at market'}>
                    <Dropdown value={value.settlementType} options={enums.settlementType} disabled={readOnly} onChange={(e) => set('settlementType', e.value)} />
                </Field>
                <Field label="Strike" col={3}>
                    <NumberInput value={value.strike} disabled={readOnly} onChange={(v) => set('strike', v)} digits={priceDigits} maxDigits={8} />
                </Field>
                <Field label={fx ? 'Notional (base currency)' : 'Notional (units)'} col={3}>
                    <NumberInput value={value.notional} disabled={readOnly} onChange={(v) => set('notional', v)} />
                </Field>
                <Field label="Description" col={6} hint="Empty = generated from the terms">
                    <InputText value={value.description} disabled={readOnly} onChange={(e) => set('description', e.target.value)} />
                </Field>
            </Grid>
            <Section title="Expiry and delivery">
                <Field label="Expiry date" col={3}>
                    <Calendar value={value.expiryDate} disabled={readOnly} onChange={(e) => set('expiryDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
                </Field>
                <Field label="Cut time" col={2} hint="Local time of the expiry cut (HH:mm); empty = end of day">
                    <InputText value={value.expiryTime} disabled={readOnly} placeholder="HH:mm" keyfilter={/[\d:]/}
                        onChange={(e) => set('expiryTime', e.target.value)} />
                </Field>
                <Field label="Time zone" col={3}>
                    <Dropdown value={value.expiryTimeZone} options={TIME_ZONES} editable disabled={readOnly || !value.expiryTime}
                        onChange={(e) => set('expiryTimeZone', e.value ?? '')} />
                </Field>
                <Field label="Delivery date" col={2} hint={fx ? 'Empty = expiry + spot lag' : 'Empty = expiry date'}>
                    <Calendar value={value.deliveryDate} disabled={readOnly} onChange={(e) => set('deliveryDate', e.value ?? null)}
                        dateFormat="dd/mm/yy" showIcon showButtonBar />
                </Field>
                <Field label="Spot lag" col={2} hint="Business days of premium and delivery; empty = those of the underlying">
                    <NumberInput value={value.spotLag} disabled={readOnly} onChange={(v) => set('spotLag', v)} digits={0} grouping={false} min={0} />
                </Field>
            </Section>
        </>
    );

    const valuation = (
        <Grid>
            <Field label="Date" col={3} hint="Empty = official date">
                <Calendar value={value.valuationDate} onChange={(e) => set('valuationDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon showButtonBar />
            </Field>
            <Field label={fx ? 'Spot' : 'Future price'} col={3} hint="Empty = market quote">
                <NumberInput value={value.spot} onChange={(v) => set('spot', v)} digits={priceDigits} maxDigits={8} />
            </Field>
            <Field label="Volatility %" col={3} hint="Empty = volatility surface of the underlying at expiry and strike">
                <NumberInput value={value.volatilityPct} onChange={(v) => set('volatilityPct', v)} maxDigits={4} />
            </Field>
            <Field label="Market premium per unit" col={3} hint="If given: valuation at the implied volatility">
                <NumberInput value={value.optionPrice} onChange={(v) => set('optionPrice', v)} digits={4} maxDigits={8} />
            </Field>
        </Grid>
    );

    const tabs: TabDef[] = [
        { key: 'terms', header: 'Contract', icon: 'pi pi-file-edit', content: contract },
        ...(booking ? [{ key: 'booking', header: 'Booking', icon: 'pi pi-book', content: booking }] : []),
        ...(pricing ? [
            { key: 'valuation', header: 'Valuation', icon: 'pi pi-sliders-h', content: valuation },
            { key: 'results', header: 'Results', icon: 'pi pi-chart-bar', content: results, disabled: !results },
        ] : []),
    ];

    return <FormTabs tabs={tabs} active={tab} onChange={onTabChange} />;
};

/**
 * Valutazione di un'opzione OTC: premio unitario e del contratto, greche
 * per unita' e sul nozionale (delta nel sottostante, vega e theta nella
 * valuta di regolamento).
 */
export const OtcOptionResults = ({ result, notional, strike }: {
    result: OptionPricingResponse;
    notional: number | null;
    strike: number | null;
}) => {
    const n = notional ?? 0;
    return (
        <KpiGrid items={[
            { label: 'Value (holder)', value: fmt(result.optionValue), tone: tone(result.optionValue), hint: 'Premium per unit × notional' },
            { label: 'Premium per unit', value: fmt(result.optionPremium, 6) },
            { label: 'Premium % of notional × strike', value: strike && n ? pct(100 * result.optionValue / (n * strike)) : '—' },
            { label: 'Model', value: result.optionModel },
            { label: 'Volatility', value: `${pct(100 * result.volatility, 3)} (${result.volatilitySource})` },
            { label: 'Forward', value: fmt(result.underlyingForward, 6) },
            { label: 'Delta', value: `${pct(100 * result.delta, 2)} · ${fmt(result.delta * n, 0)}`, hint: 'Per unit and on the notional (units of underlying)' },
            { label: 'Gamma', value: fmt(result.gamma, 4), hint: 'Per unit' },
            { label: 'Vega (per point)', value: fmt(result.vega * n), hint: 'On the notional, settlement currency' },
            { label: 'Theta (per day)', value: fmt(result.theta * n), tone: tone(result.theta), hint: 'On the notional, settlement currency' },
            { label: 'Rho (per point)', value: fmt(result.rho * n), hint: 'On the notional, settlement currency' },
            { label: 'Time to expiry', value: `${fmt(result.timeToExpiry, 4)} y` },
            { label: 'Discount factor', value: fmt(result.discountFactor, 6) },
        ]} />
    );
};
