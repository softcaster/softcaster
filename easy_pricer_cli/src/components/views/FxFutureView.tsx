import React from 'react';
import { useActions } from '../../context/ActionContext';
import { useState, useEffect } from 'react';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { fetchFxFutureMasterData, fetchPositionMasterData, fetchCounterparty, saveFinacialTxn, fetchFinacialTxn } from '../services/services';
import type { FxFutureMasterData,ForexMasterData, PositionMasterData, FinacialTxn, Counterparty, TxnStatus } from '../data/schema';
import { FxFutureForm } from '../fragments/FxFutureForm';

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

const FxFutureView: React.FC = () => {
     const [fxFutMasterDataList, setFxFutMasterDataList] = useState<FxFutureMasterData[]>([]);
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
                    fetchFxFutureMasterData(),
                    fetchPositionMasterData(),
                    fetchCounterparty(),
                    fetchFinacialTxn()
                ]);

                // Aggiorna gli stati
                setFxFutMasterDataList(fxData);
                setPositionMasterDataList(posData);
                setCounterpartyList(cpData);
                setTrades(txData);
            } catch (err) {
                console.error("Errore loading data:", err);
            }
        };

        loadData();
    }, []);

    const handleSave = async () => {
         alert("handleSave");
    };

    const handleNew = () => {
         alert("handleNew");
    };

    const handleDel = () => {
         alert("handleDel");
    };

    useEffect(() => {
        setAction({ save: handleSave, new: handleNew, del: handleDel });
    }, [/*selectedTrade*/]); // Aggiorna il riferimento così Save/Del leggono sempre i dati aggiornati 
    
return (
        <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
            {/* PANNELLO SUPERIORE: FORM */}
            <SplitterPanel size={30} minSize={20} className="overflow-auto">
                <FxFutureForm
                    data={selectedTrade}
                    fxFutures={fxFutMasterDataList}
                    positions={positionMasterDataList}
                    counterparties={counterpartyList}
                    onChange={setSelectedTrade}
                />
            </SplitterPanel>
        </Splitter>
    );
};

export default FxFutureView;
