import { useCallback, useEffect, useState } from 'react';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputNumber } from 'primereact/inputnumber';
import { Message } from 'primereact/message';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { TabPanel, TabView } from 'primereact/tabview';
import { errorMessage } from '../api/client';
import { fetchAccumulators, fetchLoans, valueAccumulator, valueLoan } from '../api/services';
import type { AccumulatorDto, LoanDto } from '../api/types';
import { fmt, fmtDate, fromPercent, pct, tone } from '../components/format';
import { KpiGrid, type KpiItem } from '../components/ui';
import { useActions } from '../context/ActionContext';

type Selection = { kind: 'loan'; row: LoanDto } | { kind: 'accumulator'; row: AccumulatorDto } | null;

/**
 * Contratti OTC registrati (mutui e accumulator) e loro valutazione alla data
 * ufficiale: si seleziona una riga e si usa Calcola. Toolbar: Aggiorna, Calcola.
 */
const ContractsView = () => {
    const { setAction } = useActions();
    const [loans, setLoans] = useState<LoanDto[]>([]);
    const [accumulators, setAccumulators] = useState<AccumulatorDto[]>([]);
    const [selection, setSelection] = useState<Selection>(null);
    const [discountRatePct, setDiscountRatePct] = useState<number | null>(3);
    const [valuation, setValuation] = useState<{ code: string; items: KpiItem[] } | null>(null);
    const [error, setError] = useState<string | null>(null);

    const refresh = useCallback(async () => {
        try {
            const [l, a] = await Promise.all([fetchLoans(), fetchAccumulators()]);
            setLoans(l);
            setAccumulators(a);
        } catch (e) {
            setError(errorMessage(e));
        }
    }, []);

    useEffect(() => {
        // caricamento iniziale delle liste dal server
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refresh();
    }, [refresh]);

    const calculate = async () => {
        if (!selection) {
            return;
        }
        setError(null);
        try {
            if (selection.kind === 'loan') {
                const r = await valueLoan(selection.row.code, { discountRate: fromPercent(discountRatePct) });
                setValuation({
                    code: selection.row.code, items: [
                        { label: 'Data', value: fmtDate(r.valuationDate) },
                        { label: 'Debito residuo', value: fmt(r.outstandingPrincipal) },
                        { label: 'Rateo', value: fmt(r.accruedInterest) },
                        { label: 'Fair value', value: fmt(r.fairValue) },
                        { label: 'Fair value %', value: pct(r.fairValuePercent, 3) },
                        { label: 'Prossima rata', value: `${fmtDate(r.nextPaymentDate)} · ${fmt(r.nextPaymentAmount)}` },
                        { label: 'DV01', value: fmt(r.dv01) },
                        { label: 'Sconto', value: r.discountSource },
                    ],
                });
            } else {
                const r = await valueAccumulator(selection.row.code, {});
                setValuation({
                    code: selection.row.code, items: [
                        { label: 'Data', value: fmtDate(r.valuationDate) },
                        { label: 'Valore (detentore)', value: fmt(r.value), tone: tone(r.value) },
                        { label: 'Errore standard', value: fmt(r.standardError) },
                        { label: 'Da regolare', value: fmt(r.pendingValue) },
                        { label: 'P(knock-out)', value: pct(100 * r.knockOutProbability, 2) },
                        { label: 'Fixing futuri', value: `${r.futureFixings} / ${r.totalFixings}` },
                        { label: 'Delta', value: fmt(r.delta, 0) },
                        { label: 'Volatilità', value: `${pct(100 * r.volatility, 2)} (${r.volatilitySource})` },
                    ],
                });
            }
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    useEffect(() => {
        setAction({ refresh, calculate: selection ? calculate : undefined });
        // le azioni della toolbar seguono la selezione
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selection, discountRatePct]);

    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => () => setAction({}), []);

    const select = (s: Selection) => {
        setSelection(s);
        setValuation(null);
        setError(null);
    };

    return (
        <div style={{ height: 'calc(100vh - 45px)', padding: '1rem', boxSizing: 'border-box' }} className="flex flex-column">
            <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
                <SplitterPanel size={30} minSize={15} className="block overflow-auto bg-white p-2">
                    <div className="font-semibold mb-2">Holding Views · Contratti OTC</div>
                    <div className="flex align-items-center gap-2 mb-2">
                        <span className="field-label">Tasso di sconto % dei mutui (se non c'e' una curva configurata)</span>
                        <InputNumber value={discountRatePct} onValueChange={(e) => setDiscountRatePct(e.value ?? null)} mode="decimal" locale="it-IT" maxFractionDigits={4} suffix=" %" />
                    </div>
                    {error && <Message severity="error" text={error} className="w-full mb-2" />}
                    {!valuation && <div className="text-500 text-sm">Selezionare un contratto e usare Calcola.</div>}
                    {valuation && (
                        <>
                            <div className="font-semibold mb-2">Valutazione {valuation.code}</div>
                            <KpiGrid items={valuation.items} />
                        </>
                    )}
                </SplitterPanel>
                <SplitterPanel size={70} minSize={30} className="p-2 flex flex-column overflow-hidden">
                    <TabView>
                        <TabPanel header={`Mutui (${loans.length})`}>
                            <DataTable value={loans} selectionMode="single" dataKey="code"
                                selection={selection?.kind === 'loan' ? selection.row : null}
                                onSelectionChange={(e) => select(e.value ? { kind: 'loan', row: e.value as LoanDto } : null)}
                                size="small" stripedRows paginator rows={15} emptyMessage="Nessun mutuo">
                                <Column field="code" header="Codice" />
                                <Column field="description" header="Descrizione" />
                                <Column header="Capitale" body={(r: LoanDto) => `${fmt(r.principalAmount)} ${r.currency}`} className="text-right" />
                                <Column header="Tasso" body={(r: LoanDto) => pct(r.annualRate, 3)} className="text-right" />
                                <Column header="Scadenza" body={(r: LoanDto) => fmtDate(r.maturityDate)} />
                                <Column field="installments" header="Rate" />
                            </DataTable>
                        </TabPanel>
                        <TabPanel header={`Accumulator (${accumulators.length})`}>
                            <DataTable value={accumulators} selectionMode="single" dataKey="code"
                                selection={selection?.kind === 'accumulator' ? selection.row : null}
                                onSelectionChange={(e) => select(e.value ? { kind: 'accumulator', row: e.value as AccumulatorDto } : null)}
                                size="small" stripedRows paginator rows={15} emptyMessage="Nessun accumulator">
                                <Column field="code" header="Codice" />
                                <Column field="description" header="Descrizione" />
                                <Column field="assetClass" header="Classe" />
                                <Column header="Strike" body={(r: AccumulatorDto) => fmt(r.strike, 5)} className="text-right" />
                                <Column header="KO" body={(r: AccumulatorDto) => fmt(r.koBarrier, 5)} className="text-right" />
                                <Column header="Fixing" body={(r: AccumulatorDto) => `${fmtDate(r.firstFixingDate)} – ${fmtDate(r.lastFixingDate)}`} />
                            </DataTable>
                        </TabPanel>
                    </TabView>
                </SplitterPanel>
            </Splitter>
        </div>
    );
};

export default ContractsView;
