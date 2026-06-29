import { useState, useEffect } from 'react';
import { useActions } from '../../context/ActionContext'; 
import type { PositionMasterData, Counterparty } from '../data/schema';
import type {
    ProspectFilter
} from '../services/dto';

export function useProspectView(fetchProspectData: (filter: ProspectFilter) => Promise<any[]>) {
    const [positionList, setPositionList] = useState<PositionMasterData[]>([]);
    const [counterpartyList, setCounterpartyList] = useState<Counterparty[]>([]);
    const [prospectData, setProspectData] = useState<any[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    // Stato del filtro superiore
    const [filter, setFilter] = useState<ProspectFilter>({ positionId: null, counterpartyId: null });
    const { setAction } = useActions(); // Recupera setAction

    const loadAnagrafiche = async () => {
        try {
            // Importiamo dinamicamente le stesse funzioni che usi nell'altro hook
            const { fetchPositionMasterData, fetchCounterparty } = await import('../services/services');
            const [pos, cp] = await Promise.all([
                fetchPositionMasterData(),
                fetchCounterparty()
            ]);
            setPositionList(pos);
            setCounterpartyList(cp);
        } catch (err) {
            console.error("Errore caricamento anagrafiche prospetto:", err);
        }
    };

    const handleSearch = async (activeFilter = filter) => {
        try {
            setLoading(true);
            const data = await fetchProspectData(activeFilter);
            setProspectData(data);
        } catch (err) {
            console.error("Errore caricamento dati prospetto:", err);
        } finally {
            setLoading(false);
        }
    };

    // Sincronizza l'azione di refresh della Toolbar con la ricerca attuale
    useEffect(() => {
        setAction({
            refresh: () => handleSearch(filter), // Mappa la funzione sulla toolbar
            new: undefined,  // Disattiva i bottoni di scrittura non necessari in questa vista analitica
            save: undefined,
            del: undefined
        });
    }, [filter]); // Si aggiorna se l'utente cambia i parametri nei dropdown

    useEffect(() => {
        loadAnagrafiche();
        handleSearch({ positionId: null, counterpartyId: null }); // Carica tutto all'avvio
    }, []);

    return {
        positionList,
        counterpartyList,
        prospectData,
        filter,
        setFilter,
        loading,
        handleSearch,
        handleReset: () => {
            const resetFilter = { positionId: null, counterpartyId: null };
            setFilter(resetFilter);
            handleSearch(resetFilter);
        }
    };
}
