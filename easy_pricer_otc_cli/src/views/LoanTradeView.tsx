import { useCallback, useEffect, useState } from 'react';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Message } from 'primereact/message';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Tag } from 'primereact/tag';
import { errorMessage } from '../api/client';
import { bookLoan, fetchLoans, priceLoan, valueLoan } from '../api/services';
import type { LoanDto, LoanPricingResponse } from '../api/types';
import { fmt, fmtDate, pct } from '../components/format';
import { RegistrationPanel, type Registration } from '../components/RegistrationPanel';
import { useActions } from '../context/ActionContext';
import { LoanResults } from '../fragments/LoanResults';
import { LoanTermsForm } from '../fragments/LoanTermsForm';
import { loanFormFromDto, loanRequest, newLoanForm } from '../model/loanForm';

const NEW_REGISTRATION: Registration = { side: 'BUY', counterpartyId: null, positionMdId: null, tradeDate: null };

/**
 * Deal capture dei mutui di un tipo di tasso (Loan Fixed = XRM, Loan
 * Floating = FRM): in alto il contratto, in basso quelli registrati.
 * Toolbar: Nuovo, Salva (registra un contratto nuovo), Aggiorna, Calcola
 * (simula i termini o valuta il contratto selezionato).
 */
const LoanTradeView = ({ rateType }: { rateType: 'FIXED' | 'FLOATING' }) => {
    const assetClass = rateType === 'FIXED' ? 'XRM' : 'FRM';
    const { setAction, showToast } = useActions();
    const [form, setForm] = useState(() => newLoanForm(rateType));
    const [registration, setRegistration] = useState<Registration>(NEW_REGISTRATION);
    const [loans, setLoans] = useState<LoanDto[]>([]);
    const [selected, setSelected] = useState<LoanDto | null>(null);
    const [result, setResult] = useState<LoanPricingResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    const refresh = useCallback(async () => {
        try {
            const all = await fetchLoans();
            setLoans(all.filter((l) => l.assetClass === assetClass));
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
        setForm(newLoanForm(rateType));
        setRegistration(NEW_REGISTRATION);
        setResult(null);
        setError(null);
    };

    const calculate = async () => {
        setError(null);
        try {
            setResult(selected
                ? await valueLoan(selected.code, loanRequest(form))
                : await priceLoan(loanRequest(form)));
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const save = async () => {
        setError(null);
        try {
            const response = await bookLoan(loanRequest(form, registration));
            setResult(response);
            showToast({ severity: 'success', summary: 'Mutuo registrato', detail: response.code ?? '' });
            const all = await fetchLoans();
            const mine = all.filter((l) => l.assetClass === assetClass);
            setLoans(mine);
            setSelected(mine.find((l) => l.code === response.code) ?? null);
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const select = (dto: LoanDto | null) => {
        if (!dto) {
            newDeal();
            return;
        }
        setSelected(dto);
        setForm((f) => loanFormFromDto(dto, f));
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
                        <span className="font-semibold">{rateType === 'FIXED' ? 'Loan Fixed' : 'Loan Floating'}</span>
                        {selected
                            ? <Tag value={`${selected.code} · registrato`} severity="info" />
                            : <Tag value="Nuovo contratto" severity="success" />}
                    </div>
                    <LoanTermsForm value={form} onChange={setForm} readOnly={!!selected} rateTypeLocked />
                    {!selected && <RegistrationPanel value={registration} onChange={setRegistration} sideHint="BUY = la banca presta" />}
                    {error && <Message severity="error" text={error} className="w-full mb-3" />}
                    {result && <LoanResults result={result} />}
                </SplitterPanel>
                <SplitterPanel size={35} minSize={15} className="p-2 flex flex-column overflow-hidden">
                    <DataTable value={loans} selectionMode="single" selection={selected} dataKey="code"
                        onSelectionChange={(e) => select(e.value as LoanDto | null)}
                        size="small" stripedRows scrollable scrollHeight="flex" emptyMessage="Nessun mutuo registrato">
                        <Column field="code" header="Codice" />
                        <Column field="description" header="Descrizione" />
                        <Column header="Capitale" body={(r: LoanDto) => `${fmt(r.principalAmount)} ${r.currency}`} className="text-right" />
                        <Column header="Tasso" body={(r: LoanDto) => pct(r.annualRate, 3)} className="text-right" />
                        <Column header="Erogazione" body={(r: LoanDto) => fmtDate(r.disbursementDate)} />
                        <Column header="Scadenza" body={(r: LoanDto) => fmtDate(r.maturityDate)} />
                        <Column field="installments" header="Rate" />
                    </DataTable>
                </SplitterPanel>
            </Splitter>
        </div>
    );
};

export const LoanFixedView = () => <LoanTradeView rateType="FIXED" />;
export const LoanFloatingView = () => <LoanTradeView rateType="FLOATING" />;
