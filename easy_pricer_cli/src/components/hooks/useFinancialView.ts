// src/hooks/useFinancialView.ts
import { useState, useEffect } from 'react';
import { useActions } from '../../context/ActionContext';

// Importa solo i tipi comuni a TUTTE le viste
import type {
    FinancialTxn,
    PositionMasterData,
    Counterparty
} from '../data/schema';

import {
    createDefaultTxn
} from '../data/schema';

// Importa le funzioni API generiche
import {
    findAllByAssetClass,
    fetchPositionMasterData,
    fetchCounterparty,
    saveFinancialTxn,
    logicalDeleteFinancialTxn
} from '../services/services';


import {
    downloadFinancialTxnCsv
}from '../services/apiclient';

export function useFinancialView<TMaster>(
    assetClass: string,
    fetchMasterData: () => Promise<TMaster[]>,
    defaultTxn: FinancialTxn
) {
    const [masterDataList, setMasterDataList] = useState<TMaster[]>([]);
    const [positionList, setPositionList] = useState<PositionMasterData[]>([]);
    const [counterpartyList, setCounterpartyList] = useState<Counterparty[]>([]);
    const [trades, setTrades] = useState<FinancialTxn[]>([]);
    const [selectedTrade, setSelectedTrade] = useState<FinancialTxn>(defaultTxn);
    const { setAction } = useActions();
    const [isExporting, setIsExporting] = useState(false);

    const loadAll = async () => {
        try {
            const [mats, pos, cp, tx] = await Promise.all([
                fetchMasterData(),
                fetchPositionMasterData(),
                fetchCounterparty(),
                findAllByAssetClass(assetClass)
            ]);
            console.log("Anagrafiche caricate nell'hook:", mats.length);
            setMasterDataList(mats);
            setPositionList(pos);
            setCounterpartyList(cp);
            setTrades(tx);
        } catch (err) { console.error(err); }
    };

    useEffect(() => { loadAll(); }, []);

    const handleSave = async () => {
        if (selectedTrade.price <= 0) return;
        try {
            await saveFinancialTxn(selectedTrade);
            await loadAll(); // Ricarica tutto per sicurezza
            setSelectedTrade(defaultTxn);
            alert("Salvato!");
        } catch (err) { console.error(err); }
    };

    const handleDelete = async () => {
        if (!selectedTrade.idFinancialTxn) return; // Non cancellare se non c'è ID

        try {
            await logicalDeleteFinancialTxn(selectedTrade.idFinancialTxn); // Chiamata API
            await loadAll(); // Ricarica la tabella
            setSelectedTrade(defaultTxn); // Resetta il form
            // Opzionale: mostra un toast di successo
        } catch (err) {
            console.error("Error deleting transaction:", err);
        }
    };

    const handleNew = () => {
        // 1. Resetta lo stato del trade
        setSelectedTrade(defaultTxn);

        // 2. Opzionale: Forza il reset immediato delle azioni per la Toolbar
        // Questo assicura che Save e Delete si spengano all'istante
        setAction({
            new: handleNew,
            save: undefined,
            del: undefined
        });
    };

    const handleExport = async () => {
        try {
            setIsExporting(true); // Attiva lo spinner
            // Chiamiamo una funzione API specifica per il download
            const blob = await downloadFinancialTxnCsv(assetClass);

            // Creiamo un link temporaneo nel browser per scaricare il file
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `transactions_${assetClass}_${new Date().getTime()}.csv`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error("Errore durante il download CSV:", err);
        } finally {
            setIsExporting(false);
        }
    };

    useEffect(() => {

        // Log di debug per capire cosa vede l'hook
        /*
        console.log("CHECK VALIDATION:", {
            id: selectedTrade.idFinancialTxn,
            price: selectedTrade.price,
            quantity: selectedTrade.quantity
        });
        */

        // Un trade è "salvabile" se ha almeno un prezzo e una quantità (o altri campi obbligatori)
        const isSaveable = selectedTrade.price > 0 && selectedTrade.quantity !== 0;

        // Un trade è "eliminabile" solo se esiste già nel database (ID > 0)
        const isDeletable = selectedTrade.idFinancialTxn > 0

        setAction({
            save: isSaveable ? handleSave : undefined,
            new: () => setSelectedTrade(createDefaultTxn()), // Crea un oggetto nuovo ogni volta
            del: isDeletable ? handleDelete : undefined,
            export: handleExport,
            isExporting: isExporting // <--- Passiamo anche lo stato al Context
        });
    }, [selectedTrade, trades, isExporting]);

    return {
        masterDataList, positionList, counterpartyList,
        trades, selectedTrade, setSelectedTrade
    };
}
