import type { ReactNode } from 'react';
import { Calendar } from 'primereact/calendar';
import { Checkbox } from 'primereact/checkbox';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Choice, Field, FormTabs, Grid, NumberInput, Section, type TabDef } from '../components/ui';
import type { AccumulatorFormState } from '../model/accumulatorForm';
import { useEnums } from '../model/enums';
import { PREMIUM_MODES, QUANTITY_MODES } from '../model/options';

type FormProps = {
    value: AccumulatorFormState;
    onChange: (v: AccumulatorFormState) => void;
    readOnly?: boolean;
};

/** Premio della registrazione: nella scheda Booking insieme ai dati dell'operazione. */
export const AccumulatorPremium = ({ value, onChange, readOnly = false }: FormProps) => (
    <Section title="Premium">
        <Field label="Form" col={3}>
            <Choice value={value.premiumMode} options={PREMIUM_MODES} disabled={readOnly} onChange={(v) => onChange({ ...value, premiumMode: v })} />
        </Field>
        <Field label={value.premiumMode === 'PERCENT' ? 'Premium % of maximum quantity × strike' : 'Premium (paid by the buyer)'} col={4}>
            <NumberInput value={value.premium} disabled={readOnly} onChange={(v) => onChange({ ...value, premium: v })}
                digits={value.premiumMode === 'PERCENT' ? 4 : 2} maxDigits={6} />
        </Field>
    </Section>
);

/**
 * Form a schede di un accumulator: contratto, barriere e garanzia,
 * registrazione (booking, solo per un contratto nuovo), valutazione e
 * risultati. readOnly blocca i termini (contratto registrato); pricing =
 * false toglie valutazione e risultati (data entry dei trade).
 */
export const AccumulatorFormTabs = ({ value, onChange, assetClass, readOnly = false, pricing = true, booking, results, tab, onTabChange }: FormProps & {
    assetClass: string;
    pricing?: boolean;
    booking?: ReactNode;
    results?: ReactNode;
    tab: string;
    onTabChange: (key: string) => void;
}) => {
    const set = <K extends keyof AccumulatorFormState>(key: K, v: AccumulatorFormState[K]) => onChange({ ...value, [key]: v });
    const priceDigits = assetClass === 'FXA' ? 5 : 4;
    const enums = useEnums('accumulatorType', 'settlementType', 'accumulatorFrequency', 'barrierObservation', 'knockInEffect', 'guaranteeType');

    const contract = (
        <Grid>
            <Field label="Underlying" col={3}
                hint={assetClass === 'FXA' ? 'Currency pair (e.g. EURUSD)' : 'Code of the commodity future expiry'}>
                <InputText value={value.underlyingCode} disabled={readOnly} onChange={(e) => set('underlyingCode', e.target.value.toUpperCase())} />
            </Field>
            <Field label="Type" col={4}>
                <Choice value={value.accumulatorType} options={enums.accumulatorType ?? []} disabled={readOnly} onChange={(v) => set('accumulatorType', v)} />
            </Field>
            <Field label="Settlement" col={2}>
                <Dropdown value={value.settlementType} options={enums.settlementType} disabled={readOnly} onChange={(e) => set('settlementType', e.value)} />
            </Field>
            <Field label="Settlement frequency" col={3}>
                <Dropdown value={value.settlementFrequency} options={enums.accumulatorFrequency} disabled={readOnly} onChange={(e) => set('settlementFrequency', e.value)} />
            </Field>
            <Field label="Strike" col={3}>
                <NumberInput value={value.strike} disabled={readOnly} onChange={(v) => set('strike', v)} digits={priceDigits} maxDigits={8} />
            </Field>
            <Field label="Quantity" col={3} hint="Maximum without leverage (divided by the number of fixings) or per fixing">
                <Choice value={value.quantityMode} options={QUANTITY_MODES} disabled={readOnly} onChange={(v) => set('quantityMode', v)} />
            </Field>
            <Field label="Amount" col={3}>
                <NumberInput value={value.quantity} disabled={readOnly} onChange={(v) => set('quantity', v)} />
            </Field>
            <Field label="Leverage" col={3}>
                <NumberInput value={value.leverage} disabled={readOnly} onChange={(v) => set('leverage', v)} min={1} />
            </Field>
            <Field label="First fixing" col={3}>
                <Calendar value={value.firstFixingDate} disabled={readOnly} onChange={(e) => set('firstFixingDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
            </Field>
            <Field label="Last fixing" col={3}>
                <Calendar value={value.lastFixingDate} disabled={readOnly} onChange={(e) => set('lastFixingDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon />
            </Field>
            <Field label="Description" col={6} hint="Empty = generated from the terms">
                <InputText value={value.description} disabled={readOnly} onChange={(e) => set('description', e.target.value)} />
            </Field>
        </Grid>
    );

    const barriers = (
        <>
            <Section title="Knock-out">
                <Field label="Barrier" col={3}>
                    <NumberInput value={value.koBarrier} disabled={readOnly} onChange={(v) => set('koBarrier', v)} digits={priceDigits} maxDigits={8} />
                </Field>
                <Field label="Observation" col={3}>
                    <Dropdown value={value.koObservation} options={enums.barrierObservation} disabled={readOnly} onChange={(e) => set('koObservation', e.value)} />
                </Field>
                <Field label="KO fixing accrues" col={3} hint="The quantity of the knock-out fixing is still accrued">
                    <Checkbox checked={value.koAccrual} disabled={readOnly} onChange={(e) => set('koAccrual', !!e.checked)} />
                </Field>
            </Section>
            <Section title="Knock-in">
                <Field label="Effect" col={3}>
                    <Dropdown value={value.kiEffect} options={enums.knockInEffect} placeholder="No knock-in" showClear disabled={readOnly} onChange={(e) => set('kiEffect', e.value ?? '')} />
                </Field>
                <Field label="Barrier" col={3}>
                    <NumberInput value={value.kiBarrier} disabled={readOnly || !value.kiEffect} onChange={(v) => set('kiBarrier', v)} digits={priceDigits} maxDigits={8} />
                </Field>
            </Section>
            <Section title="Guarantee">
                <Field label="Guaranteed periods" col={3} hint="First settlement periods without knock-out">
                    <NumberInput value={value.guaranteedPeriods} disabled={readOnly} onChange={(v) => set('guaranteedPeriods', v ?? 0)}
                        digits={0} grouping={false} min={0} />
                </Field>
                <Field label="Guarantee type" col={4}>
                    <Choice value={value.guaranteeType} options={enums.guaranteeType ?? []} disabled={readOnly || !value.guaranteedPeriods}
                        onChange={(v) => set('guaranteeType', v)} />
                </Field>
            </Section>
        </>
    );

    const valuation = (
        <Grid>
            <Field label="Date" col={3} hint="Empty = official date">
                <Calendar value={value.valuationDate} onChange={(e) => set('valuationDate', e.value ?? null)} dateFormat="dd/mm/yy" showIcon showButtonBar />
            </Field>
            <Field label="Spot" col={3} hint="Empty = market quote">
                <NumberInput value={value.spot} onChange={(v) => set('spot', v)} digits={priceDigits} maxDigits={8} />
            </Field>
            <Field label="Volatility %" col={3} hint="Empty = volatility surface of the underlying">
                <NumberInput value={value.volatilityPct} onChange={(v) => set('volatilityPct', v)} maxDigits={4} />
            </Field>
            <Field label="Paths" col={3}>
                <NumberInput value={value.paths} onChange={(v) => set('paths', v)} digits={0} min={2} />
            </Field>
        </Grid>
    );

    const tabs: TabDef[] = [
        { key: 'terms', header: 'Contract', icon: 'pi pi-file-edit', content: contract },
        { key: 'barriers', header: 'Barriers & guarantee', icon: 'pi pi-shield', content: barriers },
        ...(booking ? [{ key: 'booking', header: 'Booking', icon: 'pi pi-book', content: booking }] : []),
        ...(pricing ? [
            { key: 'valuation', header: 'Valuation', icon: 'pi pi-sliders-h', content: valuation },
            { key: 'results', header: 'Results', icon: 'pi pi-chart-bar', content: results, disabled: !results },
        ] : []),
    ];

    return <FormTabs tabs={tabs} active={tab} onChange={onTabChange} />;
};
