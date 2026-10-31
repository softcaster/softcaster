import { useCallback, useEffect, useState } from 'react';
import { Message } from 'primereact/message';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Tag } from 'primereact/tag';
import { errorMessage } from '../api/client';
import { bookLoan, fetchLoanCashFlows, fetchLoans, fetchTxns, priceLoan, valueLoan } from '../api/services';
import type { FinancialTxnDto, LoanCashFlow, LoanDto, LoanPricingResponse } from '../api/types';
import { RegistrationPanel, type Registration } from '../components/RegistrationPanel';
import { TxnTable } from '../components/TxnTable';
import { useActions } from '../context/ActionContext';
import { LoanCashFlows, LoanResults } from '../fragments/LoanResults';
import { LoanFormTabs } from '../fragments/LoanTermsForm';
import { loanFormFromDto, loanRequest, newLoanForm } from '../model/loanForm';
import { registrationFromTxn } from '../model/txn';

const NEW_REGISTRATION: Registration = { side: 'BUY', counterpartyId: null, positionMdId: null, tradeDate: null };

/**
 * Deal capture dei mutui di un tipo di tasso (Loan Fixed = XRM, Loan
 * Floating = FRM): in alto il contratto, in basso le transazioni registrate;
 * la transazione selezionata carica i suoi dati e l'anagrafica collegata.
 * Toolbar: Nuovo, Salva (registra un contratto nuovo), Aggiorna, Calcola
 * (simula i termini o valuta il contratto selezionato).
 */
const LoanTradeView = ({ rateType }: { rateType: 'FIXED' | 'FLOATING' }) => {
    const assetClass = rateType === 'FIXED' ? 'XRM' : 'FRM';
    const { setAction, showToast } = useActions();
    const [form, setForm] = useState(() => newLoanForm(rateType));
    const [registration, setRegistration] = useState<Registration>(NEW_REGISTRATION);
    const [loans, setLoans] = useState<LoanDto[]>([]);
    const [txns, setTxns] = useState<FinancialTxnDto[]>([]);
    const [selectedTxn, setSelectedTxn] = useState<FinancialTxnDto | null>(null);
    const [result, setResult] = useState<LoanPricingResponse | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [tab, setTab] = useState('terms');
    // piano salvato del contratto selezionato, con il codice a cui si riferisce
    const [cashFlows, setCashFlows] = useState<{ code: string; items: LoanCashFlow[] } | null>(null);

    // anagrafica collegata alla transazione selezionata
    const selected = selectedTxn ? loans.find((l) => l.code === selectedTxn.masterDataCode) ?? null : null;
    const selectedCode = selected?.code;

    useEffect(() => {
        if (!selectedCode) {
            return;
        }
        let active = true;
        fetchLoanCashFlows(selectedCode)
            .then((items) => active && setCashFlows({ code: selectedCode, items }))
            .catch((e) => active && setError(errorMessage(e)));
        return () => {
            active = false;
        };
    }, [selectedCode]);

    const load = useCallback(async () => {
        const [allLoans, allTxns] = await Promise.all([fetchLoans(), fetchTxns(assetClass)]);
        const mine = allLoans.filter((l) => l.assetClass === assetClass);
        setLoans(mine);
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
        setForm(newLoanForm(rateType));
        setRegistration(NEW_REGISTRATION);
        setResult(null);
        setError(null);
        setTab('terms');
    };

    const calculate = async () => {
        setError(null);
        try {
            setResult(selected
                ? await valueLoan(selected.code, loanRequest(form))
                : await priceLoan(loanRequest(form)));
            setTab('results');
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const save = async () => {
        setError(null);
        try {
            const response = await bookLoan(loanRequest(form, registration));
            setResult(response);
            setTab('results');
            showToast({ severity: 'success', summary: 'Loan booked', detail: response.code ?? '' });
            const allTxns = await load();
            const txn = allTxns.filter((t) => t.masterDataCode === response.code)
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
        const dto = loans.find((l) => l.code === txn.masterDataCode);
        if (dto) {
            setForm((f) => loanFormFromDto(dto, f));
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
    }, [form, registration, selectedTxn, loans, canSave]);

    // toolbar vuota quando si lascia la vista
    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => () => setAction({}), []);

    return (
        <div style={{ height: 'calc(100vh - 45px)', padding: '1rem', boxSizing: 'border-box' }} className="flex flex-column">
            <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
                <SplitterPanel size={65} minSize={25} className="block overflow-auto bg-white p-2">
                    <div className="flex align-items-center gap-2 mb-2">
                        <span className="font-semibold">{rateType === 'FIXED' ? 'Loan Fixed' : 'Loan Floating'}</span>
                        {selectedTxn
                            ? <Tag value={`Trade ${String(selectedTxn.financialTxnId).padStart(5, '0')} · ${selectedTxn.masterDataCode ?? ''} · ${selectedTxn.txnStatusDescription ?? ''}`} severity="info" />
                            : <Tag value="New contract" severity="success" />}
                    </div>
                    {error && <Message severity="error" text={error} className="w-full mb-2" />}
                    <LoanFormTabs value={form} onChange={setForm} readOnly={!!selectedTxn} rateTypeLocked tab={tab} onTabChange={setTab}
                        booking={<RegistrationPanel value={registration} onChange={setRegistration} readOnly={!!selectedTxn} sideHint="BUY = the bank lends" />}
                        cashFlows={selected && cashFlows?.code === selected.code ? <LoanCashFlows items={cashFlows.items} /> : undefined}
                        results={result ? <LoanResults result={result} /> : undefined} />
                </SplitterPanel>
                <SplitterPanel size={35} minSize={15} className="p-2 flex flex-column overflow-hidden">
                    <TxnTable data={txns} selection={selectedTxn} onSelectionChange={select} emptyMessage="No loan trades" />
                </SplitterPanel>
            </Splitter>
        </div>
    );
};

export const LoanFixedView = () => <LoanTradeView rateType="FIXED" />;
export const LoanFloatingView = () => <LoanTradeView rateType="FLOATING" />;
