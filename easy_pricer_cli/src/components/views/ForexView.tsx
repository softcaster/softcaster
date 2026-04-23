// src/components/ForexView.tsx
import React from 'react';
import { useState, useEffect } from 'react';
import { ForexForm } from '../fragments/ForexForm';
import { ForexTable } from '../fragments/ForexTable';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { useActions } from '../../context/ActionContext';
import { fetchForexMasterData } from '../services/services';
import type { ForexMasterData } from '../data/schema';
import type { ForexTrade } from '../data/fxtrade';

// oggetto per il reset
const DEFAULT_TRADE: ForexTrade = {
    id: '',
    currPair: '',
    price: 0,
    units: 0,
    valueDate: new Date(),
    reference: ''
};

const ForexView: React.FC = () => {
    const [fxMasterDataList, setFxMasterDataList] = useState<ForexMasterData[]>([]);
    const [trades, setTrades] = useState<ForexTrade[]>([/* dati iniziali */]);
    // Stato condiviso
    const [selectedTrade, setSelectedTrade] = useState<ForexTrade>(DEFAULT_TRADE);
    const { setAction } = useActions();

    useEffect(() => {
        // Carichiamo le valute dal backend Spring Boot
        fetchForexMasterData().then(data => {
            setFxMasterDataList(data);
        }).catch(err => console.error("Errore caricamento divise", err));
    }, []);

    // Funzione specifica per il Forex
    const handleSave = () => {
        // 1. Validazione minima
        if (selectedTrade.price <= 0 || selectedTrade.units <= 0) return;

        // 2. Simulazione salvataggio (qui andrebbe la chiamata API fetch/axios)
        const newTrade = { ...selectedTrade, id: Math.random().toString(36).substr(2, 9) };

        // 3. Aggiornamento Tabella
        setTrades([...trades, newTrade]);

        // 4. Reset del form ai valori di default
        setSelectedTrade(DEFAULT_TRADE);

        alert("Deal salvato con successo!");
    };

    const handleNew = () => {
        setSelectedTrade(DEFAULT_TRADE);
    };

    // Registriamo queste funzioni nel Context globale
    useEffect(() => {
        setAction({ save: handleSave, new: handleNew });
    }, [selectedTrade]); // Aggiorna il riferimento così Save legge sempre i dati aggiornati 

    // Dati 
    /*
    const trades: ForexTrade[] = [
        { id: '1', price: 1.0854, units: 100000, valueDate: new Date(), reference: 'TRD-9901' },
        { id: '2', price: 1.0862, units: 50000, valueDate: new Date(), reference: 'TRD-9902' },
    ];
    */
    return (
        <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
            {/* PANNELLO SUPERIORE: FORM */}
            <SplitterPanel size={30} minSize={20} className="overflow-auto">
                <ForexForm
                    data={selectedTrade}
                    currencies={fxMasterDataList} 
                    onChange={setSelectedTrade}
                />
            </SplitterPanel>

            {/* PANNELLO INFERIORE: TABELLA */}
            <SplitterPanel size={70} minSize={30} className="p-3 flex flex-column">
                <ForexTable
                    data={trades}
                    selection={selectedTrade}
                    onSelectionChange={(val) => setSelectedTrade(val ?? DEFAULT_TRADE)}
                />
            </SplitterPanel>

        </Splitter>
    );
};

export default ForexView;
