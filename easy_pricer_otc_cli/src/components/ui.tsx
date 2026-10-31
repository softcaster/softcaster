import type { ReactNode } from 'react';
import { InputNumber } from 'primereact/inputnumber';
import { SelectButton } from 'primereact/selectbutton';
import { TabPanel, TabView } from 'primereact/tabview';
import type { Option } from '../api/types';

/** Etichetta sopra il campo (stile di easy_pricer_cli), su una colonna della griglia PrimeFlex. */
export const Field = ({ label, children, col = 3, hint }: { label: string; children: ReactNode; col?: number; hint?: string }) => (
    <div className={`col-12 md:col-${col}`}>
        <label className="block mb-2 font-bold text-sm" title={hint}>{label}{hint ? ' ⓘ' : ''}</label>
        {children}
    </div>
);

/** Griglia dei campi: p-fluid rende i campi a tutta colonna e affianca i pulsanti delle scelte. */
export const Grid = ({ children }: { children: ReactNode }) => (
    <div className="grid p-fluid align-items-end">{children}</div>
);

export const Section = ({ title, children }: { title: string; children: ReactNode }) => (
    <fieldset className="otc-section">
        <legend>{title}</legend>
        <Grid>{children}</Grid>
    </fieldset>
);

/**
 * Campo numerico come in easy_pricer_cli: allineato a destra, punto
 * decimale, numero fisso di decimali.
 */
export const NumberInput = ({ value, onChange, digits = 2, maxDigits, suffix, disabled, min, grouping = true }: {
    value: number | null | undefined;
    onChange: (v: number | null) => void;
    digits?: number;
    maxDigits?: number;
    suffix?: string;
    disabled?: boolean;
    min?: number;
    grouping?: boolean;
}) => (
    <InputNumber value={value ?? null} onValueChange={(e) => onChange(e.value ?? null)} disabled={disabled} min={min}
        mode="decimal" locale="en-US" minFractionDigits={digits} maxFractionDigits={Math.max(digits, maxDigits ?? digits)}
        useGrouping={grouping} suffix={suffix} inputStyle={{ textAlign: 'right' }} className="w-full" />
);

/** Scelta a pulsanti con icona ed etichetta (come SideSelector di easy_pricer_cli). */
export const Choice = ({ value, options, onChange, disabled }: {
    value: string;
    options: Option[];
    onChange: (v: string) => void;
    disabled?: boolean;
}) => (
    <SelectButton value={value} options={options} disabled={disabled} onChange={(e) => e.value && onChange(e.value)}
        itemTemplate={(option: Option) => (
            <div className="flex align-items-center justify-content-center gap-2">
                {option.icon && <i className={option.icon}></i>}
                <span>{option.label}</span>
            </div>
        )} />
);

export interface TabDef {
    key: string;
    header: string;
    icon?: string;
    content: ReactNode;
    disabled?: boolean;
}

/** Schede del form; la scheda attiva e' identificata dalla chiave (le schede possono comparire e sparire). */
export const FormTabs = ({ tabs, active, onChange }: { tabs: TabDef[]; active: string; onChange: (key: string) => void }) => {
    const index = Math.max(0, tabs.findIndex((t) => t.key === active));
    return (
        <TabView activeIndex={index} onTabChange={(e) => onChange(tabs[e.index].key)} renderActiveOnly={false} className="otc-tabs">
            {tabs.map((t) => (
                <TabPanel key={t.key} header={t.header} leftIcon={t.icon ? `${t.icon} mr-2` : undefined} disabled={t.disabled}>
                    {t.content}
                </TabPanel>
            ))}
        </TabView>
    );
};

export interface KpiItem {
    label: string;
    value: string;
    tone?: 'pos' | 'neg';
    hint?: string;
}

export const KpiGrid = ({ items }: { items: KpiItem[] }) => (
    <div className="kpi-grid">
        {items.map((k) => (
            <div key={k.label} className="kpi" title={k.hint}>
                <div className="kpi-label">{k.label}</div>
                <div className={`kpi-value ${k.tone ?? ''}`}>{k.value}</div>
            </div>
        ))}
    </div>
);
