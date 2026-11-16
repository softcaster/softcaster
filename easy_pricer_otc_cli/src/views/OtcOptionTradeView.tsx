import { useCallback, useEffect, useState } from 'react';
import { Message } from 'primereact/message';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Tag } from 'primereact/tag';
import { errorMessage } from '../api/client';
import { amendOtcOption, bookOtcOption, cancelTxn, fetchOtcOptions, fetchTxns } from '../api/services';
import type { FinancialTxnDto, OtcOptionDto } from '../api/types';
import { RegistrationPanel, type Registration } from '../components/RegistrationPanel';
import { TxnTable } from '../components/TxnTable';
import { useActions } from '../context/ActionContext';
import { OtcOptionFormTabs, OtcOptionPremium } from '../fragments/OtcOptionTermsForm';
import { newOtcOptionForm, otcOptionFormFromDto, otcOptionRequest, type OtcOptionAssetClass } from '../model/otcOptionForm';
import { isEditableTxn, registrationFromTxn } from '../model/txn';

const NEW_REGISTRATION: Registration = { side: 'SELL', counterpartyId: null, positionMdId: null, tradeDate: null };

/**
 * Deal capture delle opzioni OTC di una asset class (OFX su cambi, OCM su
 * commodity): in alto il contratto, in basso le transazioni registrate; la
 * transazione selezionata carica i suoi dati e l'anagrafica collegata
 * (premio totale = prezzo della transazione). Salva registra un contratto
 * nuovo o modifica quello selezionato (PENDING aggiornato, EXECUTED ->
 * TO_AMEND), Cancella lo porta a TO_CANCEL. Solo data entry: simulazione e
 * valutazione sono nelle pagine Pricing e Contracts.
 */
const OtcOptionTradeView = ({ assetClass }: { assetClass: OtcOptionAssetClass }) => {
    const { setAction, showToast } = useActions();
    const [form, setForm] = useState(() => newOtcOptionForm(assetClass));
    const [registration, setRegistration] = useState<Registration>(NEW_REGISTRATION);
    const [contracts, setContracts] = useState<OtcOptionDto[]>([]);
    const [txns, setTxns] = useState<FinancialTxnDto[]>([]);
    const [selectedTxn, setSelectedTxn] = useState<FinancialTxnDto | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [tab, setTab] = useState('terms');

    // contratto nuovo, oppure transazione PENDING / EXECUTED
    const editable = !selectedTxn || isEditableTxn(selectedTxn);
    const currency = contracts.find((c) => c.code === selectedTxn?.masterDataCode)?.currency
        ?? (assetClass === 'OFX' && form.underlyingCode.length === 6 ? form.underlyingCode.substring(3) : undefined);

    const load = useCallback(async () => {
        const [all, allTxns] = await Promise.all([fetchOtcOptions(), fetchTxns(assetClass)]);
        setContracts(all.filter((o) => o.assetClass === assetClass));
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
        setForm(newOtcOptionForm(assetClass));
        setRegistration(NEW_REGISTRATION);
        setError(null);
        setTab('terms');
    };

    const save = async () => {
        setError(null);
        try {
            const request = otcOptionRequest(form, registration);
            if (selectedTxn) {
                const response = await amendOtcOption(selectedTxn.financialTxnId, request);
                showToast({
                    severity: 'success', summary: selectedTxn.txnStatusId === 1 ? 'Trade updated' : 'Trade amended (To Amend)',
                    detail: response.contract.code,
                });
            } else {
                const response = await bookOtcOption(request);
                showToast({ severity: 'success', summary: 'Option booked', detail: response.contract.code });
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
        const dto = contracts.find((o) => o.code === txn.masterDataCode);
        if (dto) {
            // premio totale = prezzo della transazione (quantita' 1)
            setForm((f) => ({ ...otcOptionFormFromDto(dto, f), premiumMode: 'AMOUNT', premium: txn.price }));
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
                        <span className="font-semibold">{assetClass === 'OFX' ? 'FX Options' : 'Commodity Options'}</span>
                        {selectedTxn
                            ? <Tag value={`Trade ${String(selectedTxn.financialTxnId).padStart(5, '0')} · ${selectedTxn.masterDataCode ?? ''} · ${selectedTxn.txnStatusDescription ?? ''}`} severity="info" />
                            : <Tag value="New contract" severity="success" />}
                    </div>
                    {error && <Message severity="error" text={error} className="w-full mb-2" />}
                    <OtcOptionFormTabs value={form} onChange={setForm} assetClass={assetClass} readOnly={!editable} pricing={false}
                        tab={tab} onTabChange={setTab}
                        booking={(
                            <>
                                <OtcOptionPremium value={form} onChange={setForm} readOnly={!editable} currency={currency} />
                                <RegistrationPanel value={registration} onChange={setRegistration} readOnly={!editable}
                                    sideHint="SELL = the bank sells (writes) the option to the client" />
                            </>
                        )} />
                </SplitterPanel>
                <SplitterPanel size={35} minSize={15} className="p-2 flex flex-column overflow-hidden">
                    <TxnTable data={txns} selection={selectedTxn} onSelectionChange={select} emptyMessage="No option trades" />
                </SplitterPanel>
            </Splitter>
        </div>
    );
};

export const FxOptionView = () => <OtcOptionTradeView assetClass="OFX" />;
export const CmdOptionView = () => <OtcOptionTradeView assetClass="OCM" />;
