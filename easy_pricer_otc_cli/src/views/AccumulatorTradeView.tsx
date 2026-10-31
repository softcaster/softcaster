import { useCallback, useEffect, useState } from 'react';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Message } from 'primereact/message';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Tag } from 'primereact/tag';
import { errorMessage, isoDate } from '../api/client';
import { bookAccumulator, fetchAccumulators, priceAccumulator, valueAccumulator } from '../api/services';
import type { AccumulatorDto, AccumulatorPricingResponse } from '../api/types';
import { fmt, fmtDate } from '../components/format';
import { RegistrationPanel, type Registration } from '../components/RegistrationPanel';
import { useActions } from '../context/ActionContext';
import { AccumulatorResults } from '../fragments/AccumulatorResults';
import { AccumulatorTermsForm } from '../fragments/AccumulatorTermsForm';
import { accumulatorFormFromDto, accumulatorRequest, newAccumulatorForm } from '../model/accumulatorForm';
import { fromPercent } from '../components/format';

const NEW_REGISTRATION: Registration = { side: 'SELL', counterpartyId: null, positionMdId: null, tradeDate: null };

/**
 * Deal capture degli accumulator di una asset class (FXA su cambi, CMA su
 * commodity): in alto il contratto, in basso quelli registrati.
 */
const AccumulatorTradeView = ({ assetClass }: { assetClass: 'FXA' | 'CMA' }) => {
    const { setAction, showToast } = useActions();
    const [form, setForm] = useState(() => newAccumulatorForm(assetClass));
    const [registration, setRegistration] = useState<Registration>(NEW_REGISTRATION);
    const [contracts, setContracts] = useState<AccumulatorDto[]>([]);
    const [selected, setSelected] = useState<AccumulatorDto | null>(null);
    const [result, setResult] = useState<AccumulatorPricingResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    const refresh = useCallback(async () => {
        try {
            const all = await fetchAccumulators();
            setContracts(all.filter((a) => a.assetClass === assetClass));
        } catch (e) {
            setError(errorMessage(e));
        }
    }, [assetClass]);

    useEffect(() => {
        // caricamento iniziale dei contratti dal server
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refresh();
    }, [refresh]);

    const newDeal = () => {
        setSelected(null);
        setForm(newAccumulatorForm(assetClass));
        setRegistration(NEW_REGISTRATION);
        setResult(null);
        setError(null);
    };

    const calculate = async () => {
        setError(null);
        try {
            setResult(selected
                ? await valueAccumulator(selected.code,
                    { spot: form.spot, volatility: fromPercent(form.volatilityPct), paths: form.paths }, isoDate(form.valuationDate))
                : await priceAccumulator(accumulatorRequest(form)));
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const save = async () => {
        setError(null);
        try {
            const response = await bookAccumulator(accumulatorRequest(form, registration));
            setResult(response.pricing);
            showToast({ severity: 'success', summary: 'Accumulator registrato', detail: response.contract.code });
            if (!response.pricing) {
                setError(`${response.contract.code} registrato senza valutazione (volatilita' o curve mancanti)`);
            }
            const all = await fetchAccumulators();
            const mine = all.filter((a) => a.assetClass === assetClass);
            setContracts(mine);
            setSelected(mine.find((a) => a.code === response.contract.code) ?? null);
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const select = (dto: AccumulatorDto | null) => {
        if (!dto) {
            newDeal();
            return;
        }
        setSelected(dto);
        setForm((f) => accumulatorFormFromDto(dto, f));
        setResult(null);
        setError(null);
    };

    const canSave = !selected && registration.counterpartyId !== null && registration.positionMdId !== null;

    useEffect(() => {
        setAction({ new: newDeal, save: canSave ? save : undefined, calculate, refresh });
        // le azioni della toolbar si aggiornano con lo stato del form
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [form, registration, selected, canSave]);

    // toolbar vuota quando si lascia la vista
    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => () => setAction({}), []);

    return (
        <div style={{ height: 'calc(100vh - 45px)', padding: '1rem', boxSizing: 'border-box' }} className="flex flex-column">
            <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
                <SplitterPanel size={65} minSize={25} className="block overflow-auto bg-white p-2">
                    <div className="flex align-items-center gap-2 mb-2">
                        <span className="font-semibold">{assetClass === 'FXA' ? 'FX Accumulators' : 'Commodity Accumulators'}</span>
                        {selected
                            ? <Tag value={`${selected.code} · registrato`} severity="info" />
                            : <Tag value="Nuovo contratto" severity="success" />}
                    </div>
                    <AccumulatorTermsForm value={form} onChange={setForm} assetClass={assetClass} readOnly={!!selected} showPremium={!selected} />
                    {!selected && <RegistrationPanel value={registration} onChange={setRegistration} sideHint="SELL = la banca vende il contratto al cliente" />}
                    {error && <Message severity="error" text={error} className="w-full mb-3" />}
                    {result && <AccumulatorResults result={result} strike={form.strike} />}
                </SplitterPanel>
                <SplitterPanel size={35} minSize={15} className="p-2 flex flex-column overflow-hidden">
                    <DataTable value={contracts} selectionMode="single" selection={selected} dataKey="code"
                        onSelectionChange={(e) => select(e.value as AccumulatorDto | null)}
                        size="small" stripedRows scrollable scrollHeight="flex" emptyMessage="Nessun accumulator registrato">
                        <Column field="code" header="Codice" />
                        <Column field="description" header="Descrizione" />
                        <Column field="accumulatorType" header="Tipo" />
                        <Column header="Strike" body={(r: AccumulatorDto) => fmt(r.strike, 5)} className="text-right" />
                        <Column header="KO" body={(r: AccumulatorDto) => fmt(r.koBarrier, 5)} className="text-right" />
                        <Column header="Q. massima" body={(r: AccumulatorDto) => fmt(r.maxQuantity, 0)} className="text-right" />
                        <Column header="Fixing" body={(r: AccumulatorDto) => `${fmtDate(r.firstFixingDate)} – ${fmtDate(r.lastFixingDate)}`} />
                    </DataTable>
                </SplitterPanel>
            </Splitter>
        </div>
    );
};

export const FxAccumulatorView = () => <AccumulatorTradeView assetClass="FXA" />;
export const CmdAccumulatorView = () => <AccumulatorTradeView assetClass="CMA" />;
