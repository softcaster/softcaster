// src/components/ForexView.tsx
import React from 'react';
import { useState, useEffect } from 'react';
import { ForexForm } from '../fragments/ForexForm';
import { ForexTable } from '../fragments/ForexTable';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { useActions } from '../../context/ActionContext';
import { fetchForexMasterData, fetchPositionMasterData, fetchCounterparty, saveFinacialTxn, fetchFinacialTxn } from '../services/services';
import type { ForexMasterData, PositionMasterData, FinacialTxn, Counterparty, TxnStatus } from '../data/schema';

const DEFAULT_TXN: FinacialTxn = {
    idFinacialTxn: 0,
    counterparty: {} as Counterparty,
    positionMd: {} as PositionMasterData,
    masterData: { code: '' } as ForexMasterData, // Inizializzato come ForexMD
    txnStatus: {} as TxnStatus,
    txnSide: 1,
    description: '',
    tradeDate: new Date(),
    settlement: new Date(),
    quantity: 0,
    price: 0
};

const ForexView: React.FC = () => {
    const [fxMasterDataList, setFxMasterDataList] = useState<ForexMasterData[]>([]);
    const [positionMasterDataList, setPositionMasterDataList] = useState<PositionMasterData[]>([]);
    const [counterpartyList, setCounterpartyList] = useState<Counterparty[]>([]);
    const [trades, setTrades] = useState<FinacialTxn[]>([/* dati iniziali */]);
    // Stato condiviso
    const [selectedTrade, setSelectedTrade] = useState<FinacialTxn>(DEFAULT_TXN);
    const { setAction } = useActions();

    useEffect(() => {
        const loadData = async () => {
            try {
                // Partono tutte insieme, aspettiamo che finiscano tutte
                const [fxData, posData, cpData, txData] = await Promise.all([
                    fetchForexMasterData(),
                    fetchPositionMasterData(),
                    fetchCounterparty(),
                    fetchFinacialTxn()
                ]);

                // Aggiorna gli stati
                setFxMasterDataList(fxData);
                setPositionMasterDataList(posData);
                setCounterpartyList(cpData);
                setTrades(txData);
            } catch (err) {
                console.error("Errore loading data:", err);
            }
        };

        loadData();
    }, []);

    // Funzione specifica per il Forex
    const handleSave = async () => {
        // 1. Validazione minima
        if (selectedTrade.price <= 0) return;

        selectedTrade.settlement = selectedTrade.tradeDate;

        try {
            // 2. Aspettiamo che il salvataggio finisca
            const newTrade = await saveFinacialTxn(selectedTrade);
            console.log("Saved ID: " + newTrade?.idFinacialTxn);

            // 3. Solo dopo il salvataggio Aggiornamento Tabella
            fetchFinacialTxn().then(data => setTrades(data));

            // 4. Reset del form ai valori di default
            setSelectedTrade(DEFAULT_TXN);

            alert("Deal salvato con successo!");
        } catch (err) {
            console.log(err);
        }

    };

    const handleNew = () => {
        setSelectedTrade(DEFAULT_TXN);
    };

    const handleDel = () => {
        console.log(selectedTrade.idFinacialTxn);
    };

    // Registriamo queste funzioni nel Context globale
    useEffect(() => {
        setAction({ save: handleSave, new: handleNew, del: handleDel });
    }, [selectedTrade]); // Aggiorna il riferimento così Save/Del leggono sempre i dati aggiornati 

    return (
        <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
            {/* PANNELLO SUPERIORE: FORM */}
            <SplitterPanel size={30} minSize={20} className="overflow-auto">
                <ForexForm
                    data={selectedTrade}
                    currencies={fxMasterDataList}
                    positions={positionMasterDataList}
                    counterparties={counterpartyList}
                    onChange={setSelectedTrade}
                />
            </SplitterPanel>

            {/* PANNELLO INFERIORE: TABELLA */}
            <SplitterPanel size={70} minSize={30} className="p-3 flex flex-column">
                <ForexTable
                    data={trades}
                    selection={selectedTrade}
                    onSelectionChange={(val) => setSelectedTrade(val ?? DEFAULT_TXN)}
                />
            </SplitterPanel>

        </Splitter>
    );
};

export default ForexView;
