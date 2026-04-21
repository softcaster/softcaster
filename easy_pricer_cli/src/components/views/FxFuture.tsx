import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';

// Definiamo il tipo di dato per i Forex Future
interface Forex {
    isin: string;
    description: string;
    bid: number;
    ask: number;
}

const FxFuture: React.FC = () => {
    // Dati tipizzati
    const pairs: Forex[] = [
        { isin: 'EURUSD', description: 'Eur vs Usd',bid: 1.172, ask: 1.173},
        { isin: 'EURCHF', description: 'Eur vs Chf',bid: 0.985, ask: 0.987 }
    ];

    return (
        <div className="card">
            FxFuture
        </div>
    );
};

export default FxFuture;
