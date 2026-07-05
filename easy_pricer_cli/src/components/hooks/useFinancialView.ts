// src/hooks/useFinancialView.ts
import { useState, useEffect } from 'react';
import { useActions } from '../../context/ActionContext';
import { useSystemDate } from '../../context/SystemDateContext';

// Importa solo i tipi comuni a TUTTE le viste
import type {
    PositionMasterData,
    Counterparty
} from '../data/schema';

import type {
    FinancialTxnDto
} from '../services/dto';

import {
    createDefaultTxnDto
} from '../services/dto';

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
} from '../services/apiclient';

export function useFinancialView<TMaster>(
    assetClass: string,
    fetchMasterData: () => Promise<TMaster[]>,
    defaultTxn: FinancialTxnDto
) {
    const { businessDate, loading: dateLoading } = useSystemDate();

    // Prende l'oggetto vuoto di default e sovrascrive tradeDate con la data di sistema ufficiale
    const getSystemDefaultTxn = (): FinancialTxnDto => {
        const baseDto = { ...defaultTxn };
        if (businessDate && !dateLoading) {
            const [year, month, day] = businessDate.split('-').map(Number);
            const officialSystemDate = new Date(year, month - 1, day);
            baseDto.tradeDate = officialSystemDate;
            baseDto.settlement = officialSystemDate;
        }
        return baseDto;
    };

    // Nuovo helper dinamico che pulisce le date su QUALSIASI oggetto DTO passato
    const applySystemDateToDto = (dto: FinancialTxnDto): FinancialTxnDto => {
        if (businessDate && !dateLoading) {
            const [year, month, day] = businessDate.split('-').map(Number);
            const officialSystemDate = new Date(year, month - 1, day);
            dto.tradeDate = officialSystemDate;
            dto.settlement = officialSystemDate;
        }
        return dto;
    };

    const [masterDataList, setMasterDataList] = useState<TMaster[]>([]);
    const [positionList, setPositionList] = useState<PositionMasterData[]>([]);
    const [counterpartyList, setCounterpartyList] = useState<Counterparty[]>([]);
    const [trades, setTrades] = useState<FinancialTxnDto[]>([]);
    const [selectedTrade, setSelectedTrade] = useState<FinancialTxnDto>(applySystemDateToDto(createDefaultTxnDto()));
    const { setAction } = useActions();
    const [isExporting, setIsExporting] = useState(false);
    const { showToast } = useActions(); // Recupero showToast

    // Sincronizza lo stato iniziale del form non appena la data ufficiale di sistema viene scaricata via REST
    useEffect(() => {
        if (selectedTrade.financialTxnId === 0 && businessDate && !dateLoading) {
            setSelectedTrade(getSystemDefaultTxn());
        }
    }, [businessDate, dateLoading]);

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

    const refreshTrades = async () => {
        try {
            // Chiama solo l'endpoint dei trade
            const tx = await findAllByAssetClass(assetClass);
            setTrades(tx); // Aggiorna l'array: React re-renderizza solo la tabella!

            showToast({ severity: 'info', summary: 'Syncronized', detail: 'Grid updated' });
        } catch (err) {
            console.error(err);
            showToast({ severity: 'error', summary: 'Error', detail: 'Can not update grid' });
        }
    };

    const getInitialTxnStatus = (txnId: number, status: number | null): number | null => {
        // Sicuramente nuova transazione
        if (txnId == 0) {
            return 1;
        }

        if (!status)
            return null;

        // Possibili 2 casi accettabili
        // 1) PENDING transazione non ancora processata
        // 2) EXECUTED transazione processata
        switch (status) {
            case 1: // PENDING
                return status;
            case 3: // EXECUTED
                return 5; // TO_AMEND
            default:
                return null;
        }

    };

    const handleSave = async () => {
        if (selectedTrade.price <= 0) return;
        try {

            // setto status transazione
            selectedTrade.txnStatusId = getInitialTxnStatus(selectedTrade.financialTxnId, selectedTrade.txnStatusId);
            if (selectedTrade.txnStatusId == null) {
                showToast({
                    severity: 'warn',
                    summary: 'Warning',
                    detail: 'Invalid Txn status.'
                });
                return; // Interrompe la funzione handleSave e non esegue il salvataggio
            }
            const result = await saveFinancialTxn(selectedTrade);
            if (result == null) {
                showToast({ severity: 'error', summary: 'Error', detail: 'Transaction not registered' });
            } else {
                showToast({ severity: 'success', summary: 'Saved', detail: 'Transaction registered' });
            }
            console.log(result);
            // Ricarica tutto per sicurezza
            await loadAll();
            //Resetta il modulo usando la data ufficiale di sistema invece del defaultTxn sporco
            setSelectedTrade(getSystemDefaultTxn());
        } catch (err: any) {
            showToast({ severity: 'error', summary: 'Error', detail: 'Save failed' });
            console.error(err);
        }
    };

    const handleDelete = async () => {
        if (!selectedTrade.financialTxnId) return; // Non cancellare se non c'è ID

        try {
            await logicalDeleteFinancialTxn(selectedTrade.financialTxnId); // Chiamata API
            await loadAll(); // Ricarica la tabella
            setSelectedTrade(defaultTxn); // Resetta il form
            // Opzionale: mostra un toast di successo
        } catch (err) {
            console.error("Error deleting transaction:", err);
        }
    };

    const handleNew = () => {
        //Usa l'helper sicuro con fuso orario azzerato
        setSelectedTrade(getSystemDefaultTxn());

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
        const isDeletable = selectedTrade.financialTxnId > 0

        setAction({
            save: isSaveable ? handleSave : undefined,
            //new: () => setSelectedTrade(createDefaultTxnDto()), // Crea un oggetto nuovo ogni volta
            //Ritorna l'oggetto di default allineato al sistema anziché chiamare createDefaultTxnDto()
            new: () => setSelectedTrade(getSystemDefaultTxn()),
            del: isDeletable ? handleDelete : undefined,
            export: handleExport,
            isExporting: isExporting, // <--- Passiamo anche lo stato al Context
            refresh: refreshTrades
        });
    }, [selectedTrade, trades, isExporting]);

    return {
        masterDataList, positionList, counterpartyList,
        trades, selectedTrade, setSelectedTrade
    };
}
