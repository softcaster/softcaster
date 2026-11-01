import { useCallback, useEffect, useState } from 'react';
import { Message } from 'primereact/message';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Tag } from 'primereact/tag';
import { errorMessage } from '../api/client';
import { amendAccumulator, bookAccumulator, cancelTxn, fetchAccumulators, fetchTxns } from '../api/services';
import type { AccumulatorDto, FinancialTxnDto } from '../api/types';
import { RegistrationPanel, type Registration } from '../components/RegistrationPanel';
import { TxnTable } from '../components/TxnTable';
import { useActions } from '../context/ActionContext';
import { AccumulatorFormTabs, AccumulatorPremium } from '../fragments/AccumulatorTermsForm';
import { accumulatorFormFromDto, accumulatorRequest, newAccumulatorForm } from '../model/accumulatorForm';
import { isEditableTxn, registrationFromTxn } from '../model/txn';

const NEW_REGISTRATION: Registration = { side: 'SELL', counterpartyId: null, positionMdId: null, tradeDate: null };

/**
 * Deal capture degli accumulator di una asset class (FXA su cambi, CMA su
 * commodity): in alto il contratto, in basso le transazioni registrate;
 * la transazione selezionata carica i suoi dati e l'anagrafica collegata
 * (premio = prezzo della transazione). Salva registra un contratto nuovo o
 * modifica quello selezionato (PENDING aggiornato, EXECUTED -> TO_AMEND),
 * Cancella lo porta a TO_CANCEL; dopo entrambi il form torna vuoto. Solo
 * data entry: simulazione e valutazione sono nelle pagine Pricing e
 * Contracts.
 */
const AccumulatorTradeView = ({ assetClass }: { assetClass: 'FXA' | 'CMA' }) => {
    const { setAction, showToast } = useActions();
    const [form, setForm] = useState(() => newAccumulatorForm(assetClass));
    const [registration, setRegistration] = useState<Registration>(NEW_REGISTRATION);
    const [contracts, setContracts] = useState<AccumulatorDto[]>([]);
    const [txns, setTxns] = useState<FinancialTxnDto[]>([]);
    const [selectedTxn, setSelectedTxn] = useState<FinancialTxnDto | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [tab, setTab] = useState('terms');

    // contratto nuovo, oppure transazione PENDING / EXECUTED
    const editable = !selectedTxn || isEditableTxn(selectedTxn);

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
        setError(null);
        setTab('terms');
    };

    const save = async () => {
        setError(null);
        try {
            const request = accumulatorRequest(form, registration);
            if (selectedTxn) {
                const response = await amendAccumulator(selectedTxn.financialTxnId, request);
                showToast({
                    severity: 'success', summary: selectedTxn.txnStatusId === 1 ? 'Trade updated' : 'Trade amended (To Amend)',
                    detail: response.contract.code,
                });
            } else {
                const response = await bookAccumulator(request);
                showToast({ severity: 'success', summary: 'Accumulator booked', detail: response.contract.code });
            }
            await load();
            newDeal();
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const del = async () => {
        if (!selectedTxn) {
            return;
        }
        setError(null);
        try {
            await cancelTxn(selectedTxn.financialTxnId);
            showToast({ severity: 'success', summary: 'Trade deleted (To Cancel)', detail: selectedTxn.masterDataCode ?? '' });
            await load();
            newDeal();
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
        const dto = contracts.find((a) => a.code === txn.masterDataCode);
        if (dto) {
            // premio iniziale per contratto = prezzo della transazione
            setForm((f) => ({ ...accumulatorFormFromDto(dto, f), premiumMode: 'AMOUNT', premium: txn.price }));
            setError(null);
        } else {
            setError(`Master data ${txn.masterDataCode ?? '?'} of trade ${txn.financialTxnId} not found`);
        }
    };

    const canSave = editable && registration.counterpartyId !== null && registration.positionMdId !== null;
    const canDelete = !!selectedTxn && editable;

    useEffect(() => {
        setAction({ new: newDeal, save: canSave ? save : undefined, del: canDelete ? del : undefined, refresh });
        // le azioni della toolbar si aggiornano con lo stato del form
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [form, registration, selectedTxn, contracts, canSave, canDelete]);

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
                    <AccumulatorFormTabs value={form} onChange={setForm} assetClass={assetClass} readOnly={!editable} pricing={false}
                        tab={tab} onTabChange={setTab}
                        booking={(
                            <>
                                <AccumulatorPremium value={form} onChange={setForm} readOnly={!editable} />
                                <RegistrationPanel value={registration} onChange={setRegistration} readOnly={!editable}
                                    sideHint="SELL = the bank sells the contract to the client" />
                            </>
                        )} />
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
