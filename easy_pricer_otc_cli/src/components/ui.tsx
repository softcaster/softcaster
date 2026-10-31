import type { ReactNode } from 'react';

/** Etichetta sopra il campo, su una colonna della griglia PrimeFlex. */
export const Field = ({ label, children, col = 3, hint }: { label: string; children: ReactNode; col?: number; hint?: string }) => (
    <div className={`col-12 md:col-${col} flex flex-column gap-1`}>
        <label className="field-label" title={hint}>{label}{hint ? ' ⓘ' : ''}</label>
        {children}
    </div>
);

export const Section = ({ title, children }: { title: string; children: ReactNode }) => (
    <fieldset className="otc-section">
        <legend>{title}</legend>
        <div className="grid">{children}</div>
    </fieldset>
);

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
