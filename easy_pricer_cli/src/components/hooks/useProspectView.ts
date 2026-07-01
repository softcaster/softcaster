import { useState, useEffect, useCallback } from 'react';
import { useActions } from '../../context/ActionContext';
import type { PositionMasterData, Counterparty, AssetClass } from '../data/schema';
import type {
    ProspectFilter
} from '../services/dto';
import { downloadPositionProspectPdf } from '../services/services';

export function useProspectView(fetchProspectData: (filter: ProspectFilter) => Promise<any[]>) {
    const [positionList, setPositionList] = useState<PositionMasterData[]>([]);
    const [counterpartyList, setCounterpartyList] = useState<Counterparty[]>([]);
    const [assetClassList, setAssetClassList] = useState<AssetClass[]>([]);
    const [prospectData, setProspectData] = useState<any[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [isExporting, setIsExporting] = useState<boolean>(false);
    // Stato del filtro superiore
    const [filter, setFilter] = useState<ProspectFilter>({ positionId: null, counterpartyId: null, assetClassId: null });
    const { setAction } = useActions(); // Recupera setAction

    const loadAnagrafiche = async () => {
        try {
            const { fetchPositionMasterData, fetchCounterparty, fetchAssetClass } = await import('../services/services');

            // Blindiamo il Promise.all inserendo un fallback per ciascuna chiamata.
            // Se il server è spento, restituiscono un array vuoto [] anziché far saltare l'intero hook.
            const [pos, cp, ac] = await Promise.all([
                fetchPositionMasterData().catch(() => []),
                fetchCounterparty().catch(() => []),
                fetchAssetClass().catch(() => [])
            ]);

            setPositionList(pos || []);
            setCounterpartyList(cp || []);
            setAssetClassList(ac || []);
        } catch (err) {
            console.error("Errore critico caricamento anagrafiche:", err);
            // Inizializziamo comunque gli stati a vuoto per sbloccare i render successivi
            setPositionList([]);
            setCounterpartyList([]);
            setAssetClassList([]);
        }
    };

    const handleSearch = useCallback(async (activeFilter = filter) => {
        try {
            setLoading(true);
            const data = await fetchProspectData(activeFilter).catch(() => []);
            setProspectData(data || []);
        } catch (err) {
            console.error("Errore caricamento dati prospetto:", err);
            setProspectData([]);
        } finally {
            setLoading(false);
        }
    }, [fetchProspectData, filter]); // Si aggiorna solo se cambiano questi elementi

    const handleExport = useCallback(async () => {
        try {
            setIsExporting(true);
            const blob = await downloadPositionProspectPdf(filter);

            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `prospect_posizioni_${new Date().getTime()}.pdf`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error("Errore durante la generazione del PDF:", err);
        } finally {
            setIsExporting(false);
        }
    }, [filter]);

    useEffect(() => {
        // Registriamo le azioni una sola volta all'avvio.
        // Usando le arrow function () =>, la Toolbar leggerà sempre lo stato del filtro aggiornato
        // al momento ESATTO del clic sul pulsante, senza costringere l'hook a ri-eseguire 
        // setAction a ogni singolo micro-render del componente.
        setAction({
            refresh: () => handleSearch(),
            print: () => handleExport(),
            export: undefined,
            calculate: undefined,
            isExporting: isExporting,
            new: undefined,
            save: undefined,
            del: undefined
        });

        // Funzione di pulizia totale ed immediata quando l'utente clicca su un altro link del menu
        return () => {
            setAction({
                refresh: undefined,
                print: undefined,
                export: undefined,
                isExporting: false,
                calculate: undefined,
                new: undefined,
                save: undefined,
                del: undefined
            });
        };
        // 💡 FONDAMENTALE: Array di dipendenze COMPLETAMENTE VUOTO! 
    }, []);

    useEffect(() => {
        loadAnagrafiche();
        handleSearch({ positionId: null, counterpartyId: null, assetClassId: null });
    }, []);

    return {
        positionList,
        counterpartyList,
        assetClassList,
        prospectData,
        filter,
        setFilter, // Sblocca l'errore del build!
        loading,
        handleSearch,
        handleExport,
        handleReset: () => {
            const resetFilter = { positionId: null, counterpartyId: null, assetClassId: null };
            setFilter(resetFilter);
            handleSearch(resetFilter);
        }
    };
}
