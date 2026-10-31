import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import type { LoanCashFlow, LoanPricingResponse } from '../api/types';
import { fmt, fmtDate, pct } from '../components/format';
import { KpiGrid } from '../components/ui';

/** Valutazione di un mutuo e piano delle rate. */
export const LoanResults = ({ result, rows = 12 }: { result: LoanPricingResponse; rows?: number }) => (
    <>
        <KpiGrid items={[
            { label: 'Installment', value: fmt(result.installment) },
            { label: 'APR (TAEG)', value: pct(result.taeg), hint: 'Net disbursed = installments + per-installment fees, annual compounding ACT/365' },
            { label: 'Fair value', value: fmt(result.fairValue) },
            { label: 'Fair value %', value: pct(result.fairValuePercent, 3), hint: 'Of the outstanding principal' },
            { label: 'Outstanding', value: fmt(result.outstandingPrincipal) },
            { label: 'Accrued interest', value: fmt(result.accruedInterest) },
            { label: 'Next payment', value: `${fmtDate(result.nextPaymentDate)} · ${fmt(result.nextPaymentAmount)}` },
            { label: 'DV01', value: fmt(result.dv01), hint: 'Fair value change for +1 bp' },
            { label: 'Mod. duration', value: fmt(result.modifiedDuration, 3) },
            { label: 'Total interest', value: fmt(result.totalInterest) },
            { label: 'Total fees', value: fmt(result.totalFees) },
            { label: 'Discounting', value: result.discountSource },
        ]} />
        <DataTable value={result.schedule} paginator rows={rows} size="small" stripedRows className="mt-3"
            header={`Installment schedule (${result.schedule.length})`}>
            <Column field="number" header="#" />
            <Column header="Payment" body={(r) => fmtDate(r.paymentDate)} />
            <Column header="Principal" body={(r) => fmt(r.principal)} className="text-right" />
            <Column header="Interest" body={(r) => fmt(r.interest)} className="text-right" />
            <Column header="Fees" body={(r) => fmt(r.fees)} className="text-right" />
            <Column header="Total" body={(r) => fmt(r.total)} className="text-right" />
            <Column header="Outstanding" body={(r) => fmt(r.outstanding)} className="text-right" />
        </DataTable>
    </>
);

/** Piano salvato di un mutuo registrato (cash_flow_item). */
export const LoanCashFlows = ({ items, rows = 12 }: { items: LoanCashFlow[]; rows?: number }) => (
    <DataTable value={items} paginator rows={rows} size="small" stripedRows
        header={`Booked cash flows (${items.length})`} emptyMessage="No cash flows">
        <Column field="number" header="#" />
        <Column header="Start" body={(r: LoanCashFlow) => fmtDate(r.startDate)} />
        <Column header="End" body={(r: LoanCashFlow) => fmtDate(r.endDate)} />
        <Column header="Principal" body={(r: LoanCashFlow) => fmt(r.principal)} className="text-right" />
        <Column header="Interest" body={(r: LoanCashFlow) => fmt(r.interest)} className="text-right" />
        <Column header="Total" body={(r: LoanCashFlow) => fmt(r.total)} className="text-right" />
        <Column header="Outstanding" body={(r: LoanCashFlow) => fmt(r.outstanding)} className="text-right" />
        <Column field="status" header="Status" />
    </DataTable>
);
