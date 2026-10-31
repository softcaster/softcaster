import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import type { LoanPricingResponse } from '../api/types';
import { fmt, fmtDate, pct } from '../components/format';
import { KpiGrid } from '../components/ui';

/** Valutazione di un mutuo e piano delle rate. */
export const LoanResults = ({ result, rows = 12 }: { result: LoanPricingResponse; rows?: number }) => (
    <>
        <KpiGrid items={[
            { label: 'Rata', value: fmt(result.installment) },
            { label: 'TAEG', value: pct(result.taeg), hint: 'Netto erogato = rate + spese per rata, composto annuo ACT/365' },
            { label: 'Fair value', value: fmt(result.fairValue) },
            { label: 'Fair value %', value: pct(result.fairValuePercent, 3), hint: 'Sul debito residuo' },
            { label: 'Debito residuo', value: fmt(result.outstandingPrincipal) },
            { label: 'Rateo', value: fmt(result.accruedInterest) },
            { label: 'Prossima rata', value: `${fmtDate(result.nextPaymentDate)} · ${fmt(result.nextPaymentAmount)}` },
            { label: 'DV01', value: fmt(result.dv01), hint: 'Variazione del fair value per +1 bp' },
            { label: 'Duration mod.', value: fmt(result.modifiedDuration, 3) },
            { label: 'Interessi totali', value: fmt(result.totalInterest) },
            { label: 'Spese totali', value: fmt(result.totalFees) },
            { label: 'Sconto', value: result.discountSource },
        ]} />
        <DataTable value={result.schedule} paginator rows={rows} size="small" stripedRows className="mt-3"
            header={`Piano delle rate (${result.schedule.length})`}>
            <Column field="number" header="#" />
            <Column header="Pagamento" body={(r) => fmtDate(r.paymentDate)} />
            <Column header="Capitale" body={(r) => fmt(r.principal)} className="text-right" />
            <Column header="Interessi" body={(r) => fmt(r.interest)} className="text-right" />
            <Column header="Spese" body={(r) => fmt(r.fees)} className="text-right" />
            <Column header="Totale" body={(r) => fmt(r.total)} className="text-right" />
            <Column header="Residuo" body={(r) => fmt(r.outstanding)} className="text-right" />
        </DataTable>
    </>
);
