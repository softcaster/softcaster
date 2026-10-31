import { useCallback, useEffect, useState } from 'react';
import { Message } from 'primereact/message';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Tag } from 'primereact/tag';
import { errorMessage, isoDate } from '../api/client';
import { bookAccumulator, fetchAccumulators, fetchTxns, priceAccumulator, valueAccumulator } from '../api/services';
import type { AccumulatorDto, AccumulatorPricingResponse, FinancialTxnDto } from '../api/types';
import { fromPercent } from '../components/format';
import { RegistrationPanel, type Registration } from '../components/RegistrationPanel';
import { TxnTable } from '../components/TxnTable';
import { useActions } from '../context/ActionContext';
import { AccumulatorResults } from '../fragments/AccumulatorResults';
import { AccumulatorFormTabs, AccumulatorPremium } from '../fragments/AccumulatorTermsForm';
import { accumulatorFormFromDto, accumulatorRequest, newAccumulatorForm } from '../model/accumulatorForm';
import { registrationFromTxn } from '../model/txn';

const NEW_REGISTRATION: Registration = { side: 'SELL', counterpartyId: null, positionMdId: null, tradeDate: null };

/**
 * Deal capture degli accumulator di una asset class (FXA su cambi, CMA su
 * commodity): in alto il contratto, in basso le transazioni registrate;
 * la transazione selezionata carica i suoi dati e l'anagrafica collegata.
 */
const AccumulatorTradeView = ({ assetClass }: { assetClass: 'FXA' | 'CMA' }) => {
    const { setAction, showToast } = useActions();
    const [form, setForm] = useState(() => newAccumulatorForm(assetClass));
    const [registration, setRegistration] = useState<Registration>(NEW_REGISTRATION);
    const [contracts, setContracts] = useState<AccumulatorDto[]>([]);
    const [txns, setTxns] = useState<FinancialTxnDto[]>([]);
    const [selectedTxn, setSelectedTxn] = useState<FinancialTxnDto | null>(null);
    const [result, setResult] = useState<AccumulatorPricingResponse | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [tab, setTab] = useState('terms');

    // anagrafica collegata alla transazione selezionata
    const selected = selectedTxn ? contracts.find((a) => a.code === selectedTxn.masterDataCode) ?? null : null;

    const load = useCallback(async () => {
        const [all, allTxns] = await Promise.all([fetchAccumulators(), fetchTxns(assetClass)]);
        setContracts(all.filter((a) => a.assetClass === assetClass));
        setTxns(allTxns);
        return allTxns;
    }, [assetClass]);

    const refresh = useCallback(async () => {
        try {
            await load();
        } catch (e) {
            setError(errorMessage(e));
        }
    }, [load]);

    useEffect(() => {
        // caricamento iniziale di anagrafiche e transazioni dal server
        // eslint-disable-next-line react-hooks/set-state-in-effect
        refresh();
    }, [refresh]);

    const newDeal = () => {
        setSelectedTxn(null);
        setForm(newAccumulatorForm(assetClass));
        setRegistration(NEW_REGISTRATION);
        setResult(null);
        setError(null);
        setTab('terms');
    };

    const calculate = async () => {
        setError(null);
        try {
            setResult(selected
                ? await valueAccumulator(selected.code,
                    { spot: form.spot, volatility: fromPercent(form.volatilityPct), paths: form.paths }, isoDate(form.valuationDate))
                : await priceAccumulator(accumulatorRequest(form)));
            setTab('results');
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const save = async () => {
        setError(null);
        try {
            const response = await bookAccumulator(accumulatorRequest(form, registration));
            setResult(response.pricing);
            setTab(response.pricing ? 'results' : 'terms');
            showToast({ severity: 'success', summary: 'Accumulator booked', detail: response.contract.code });
            if (!response.pricing) {
                setError(`${response.contract.code} booked without valuation (missing volatility or curves)`);
            }
            const allTxns = await load();
            const txn = allTxns.filter((t) => t.masterDataCode === response.contract.code)
                .sort((a, b) => b.financialTxnId - a.financialTxnId)[0];
            if (txn) {
                setSelectedTxn(txn);
                setRegistration(registrationFromTxn(txn));
            }
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const select = (txn: FinancialTxnDto | null) => {
        if (!txn) {
            newDeal();
            return;
        }
        setSelectedTxn(txn);
        setRegistration(registrationFromTxn(txn));
        setResult(null);
        const dto = contracts.find((a) => a.code === txn.masterDataCode);
        if (dto) {
            setForm((f) => accumulatorFormFromDto(dto, f));
            setError(null);
        } else {
            setError(`Master data ${txn.masterDataCode ?? '?'} of trade ${txn.financialTxnId} not found`);
        }
        // i risultati non valgono per la transazione selezionata
        setTab((t) => (t === 'results' ? 'terms' : t));
    };

    const canSave = !selectedTxn && registration.counterpartyId !== null && registration.positionMdId !== null;

    useEffect(() => {
        setAction({ new: newDeal, save: canSave ? save : undefined, calculate, refresh });
        // le azioni della toolbar si aggiornano con lo stato del form
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [form, registration, selectedTxn, contracts, canSave]);

    // toolbar vuota quando si lascia la vista
    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => () => setAction({}), []);

    return (
        <div style={{ height: 'calc(100vh - 45px)', padding: '1rem', boxSizing: 'border-box' }} className="flex flex-column">
            <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
                <SplitterPanel size={65} minSize={25} className="block overflow-auto bg-white p-2">
                    <div className="flex align-items-center gap-2 mb-2">
                        <span className="font-semibold">{assetClass === 'FXA' ? 'FX Accumulators' : 'Commodity Accumulators'}</span>
                        {selectedTxn
                            ? <Tag value={`Trade ${String(selectedTxn.financialTxnId).padStart(5, '0')} · ${selectedTxn.masterDataCode ?? ''} · ${selectedTxn.txnStatusDescription ?? ''}`} severity="info" />
                            : <Tag value="New contract" severity="success" />}
                    </div>
                    {error && <Message severity="error" text={error} className="w-full mb-2" />}
                    <AccumulatorFormTabs value={form} onChange={setForm} assetClass={assetClass} readOnly={!!selectedTxn} tab={tab} onTabChange={setTab}
                        booking={(
                            <>
                                {!selectedTxn && <AccumulatorPremium value={form} onChange={setForm} />}
                                <RegistrationPanel value={registration} onChange={setRegistration} readOnly={!!selectedTxn}
                                    sideHint="SELL = the bank sells the contract to the client" />
                            </>
                        )}
                        results={result ? <AccumulatorResults result={result} strike={form.strike} /> : undefined} />
                </SplitterPanel>
                <SplitterPanel size={35} minSize={15} className="p-2 flex flex-column overflow-hidden">
                    <TxnTable data={txns} selection={selectedTxn} onSelectionChange={select} emptyMessage="No accumulator trades" />
                </SplitterPanel>
            </Splitter>
        </div>
    );
};

export const FxAccumulatorView = () => <AccumulatorTradeView assetClass="FXA" />;
export const CmdAccumulatorView = () => <AccumulatorTradeView assetClass="CMA" />;
