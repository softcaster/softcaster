import { useEffect, useState } from 'react';
import { Message } from 'primereact/message';
import { errorMessage } from '../api/client';
import { priceAccumulator, priceLoan } from '../api/services';
import type { AccumulatorPricingResponse, LoanPricingResponse } from '../api/types';
import { useActions } from '../context/ActionContext';
import { AccumulatorResults } from '../fragments/AccumulatorResults';
import { AccumulatorTermsForm } from '../fragments/AccumulatorTermsForm';
import { LoanResults } from '../fragments/LoanResults';
import { LoanTermsForm } from '../fragments/LoanTermsForm';
import { accumulatorRequest, newAccumulatorForm } from '../model/accumulatorForm';
import { loanRequest, newLoanForm } from '../model/loanForm';

const pageStyle = { height: 'calc(100vh - 45px)', padding: '1rem', boxSizing: 'border-box' as const };

/**
 * Pricing dei mutui: simulazione del piano e valutazione dei termini, senza
 * registrare. Toolbar: Nuovo, Calcola.
 */
export const LoanPricingView = () => {
    const { setAction } = useActions();
    const [form, setForm] = useState(() => newLoanForm());
    const [result, setResult] = useState<LoanPricingResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    const calculate = async () => {
        setError(null);
        try {
            setResult(await priceLoan(loanRequest(form)));
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const reset = () => {
        setForm(newLoanForm());
        setResult(null);
        setError(null);
    };

    useEffect(() => {
        setAction({ new: reset, calculate });
        // le azioni della toolbar si aggiornano con lo stato del form
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [form]);

    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => () => setAction({}), []);

    return (
        <div style={pageStyle} className="overflow-auto">
            <div className="font-semibold mb-2">Pricing · Loans</div>
            <LoanTermsForm value={form} onChange={setForm} />
            {error && <Message severity="error" text={error} className="w-full mb-3" />}
            {result && <LoanResults result={result} />}
        </div>
    );
};

const AccumulatorPricingView = ({ assetClass }: { assetClass: 'FXA' | 'CMA' }) => {
    const { setAction } = useActions();
    const [form, setForm] = useState(() => newAccumulatorForm(assetClass));
    const [result, setResult] = useState<AccumulatorPricingResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    const calculate = async () => {
        setError(null);
        try {
            setResult(await priceAccumulator(accumulatorRequest(form)));
        } catch (e) {
            setError(errorMessage(e));
        }
    };

    const reset = () => {
        setForm(newAccumulatorForm(assetClass));
        setResult(null);
        setError(null);
    };

    useEffect(() => {
        setAction({ new: reset, calculate });
        // le azioni della toolbar si aggiornano con lo stato del form
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [form]);

    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => () => setAction({}), []);

    return (
        <div style={pageStyle} className="overflow-auto">
            <div className="font-semibold mb-2">Pricing · {assetClass === 'FXA' ? 'FX Accumulators' : 'Commodity Accumulators'}</div>
            <AccumulatorTermsForm value={form} onChange={setForm} assetClass={assetClass} />
            {error && <Message severity="error" text={error} className="w-full mb-3" />}
            {result && <AccumulatorResults result={result} strike={form.strike} />}
        </div>
    );
};

export const FxAccumulatorPricingView = () => <AccumulatorPricingView assetClass="FXA" />;
export const CmdAccumulatorPricingView = () => <AccumulatorPricingView assetClass="CMA" />;
