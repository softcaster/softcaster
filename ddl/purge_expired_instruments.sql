-- ============================================================================
-- Pulizia degli strumenti scaduti: anagrafica, quotazioni, deal, posizioni e
-- valutazioni degli strumenti con maturity_date precedente alla data ufficiale
-- (system_business_calendar). Per i future maturity_date = ultimo giorno di
-- negoziazione.
--
-- Esclusi:
--   - le asset class in excluded_classes (forex spot; aggiungere le equity
--     quando ci saranno in anagrafica);
--   - gli strumenti con una posizione ancora aperta (buy_qty <> sell_qty):
--     vanno prima chiusi (rimborso, consegna);
--   - gli strumenti con deal non in uno stato finale (PENDING, VALIDATING,
--     TO_AMEND, TO_CANCEL, RESTARTING, CLAIMED).
--
-- NON tocca:
--   - la contabilita' (accounting_events, journal_entries, journal_entry_lines,
--     ledger_balances): gli eventi conservano l'id della posizione cancellata
--     (accounting_events.position_detail, senza FK);
--   - le testate dei future (future_product e sottotabelle), usate dalle altre
--     scadenze;
--   - deliverable_bonds dei future attivi che citano per ISIN un titolo scaduto.
--
-- Uso: con dry_run = true esegue tutto, mostra i conteggi (NOTICE) e annulla
-- le modifiche. Con dry_run = false le rende definitive.
-- ============================================================================
DO $$
DECLARE
    dry_run          constant boolean := true;
    excluded_classes constant text[]  := ARRAY['FSP'];
    -- deal in uno stato finale: EXECUTED, REJECTED, AMENDED, CANCELLED
    final_status     constant text[]  := ARRAY['EXECUTED', 'REJECTED', 'AMENDED', 'CANCELLED'];
    v_official_date  date;
    v_rows           integer;
    r                record;
BEGIN
    SELECT official_date INTO v_official_date FROM system_business_calendar LIMIT 1;
    IF v_official_date IS NULL THEN
        RAISE EXCEPTION 'Data ufficiale non trovata in system_business_calendar';
    END IF;
    RAISE NOTICE 'Data ufficiale: %', v_official_date;

    -- strumenti scaduti e motivo dell'eventuale esclusione
    CREATE TEMP TABLE tmp_expired ON COMMIT DROP AS
    SELECT md.id_master_data, md.code, ac.code AS asset_class, md.maturity_date,
           CASE
               WHEN EXISTS (SELECT 1 FROM position_detail p
                            WHERE p.master_data = md.id_master_data
                              AND abs(p.buy_qty - p.sell_qty) > 1e-9)
                   THEN 'posizione aperta'
               WHEN EXISTS (SELECT 1 FROM financial_txn t
                            JOIN txn_status s ON s.id_txn_status = t.txn_status
                            WHERE t.master_data = md.id_master_data
                              AND s.code <> ALL (final_status))
                   THEN 'deal non in stato finale'
           END AS skip_reason
    FROM master_data md
    JOIN asset_class ac ON ac.id_asset_class = md.asset_class
    WHERE md.maturity_date < v_official_date
      AND ac.code <> ALL (excluded_classes);

    FOR r IN SELECT * FROM tmp_expired ORDER BY asset_class, maturity_date, code LOOP
        RAISE NOTICE '% % % (scadenza %)%', CASE WHEN r.skip_reason IS NULL THEN 'CANCELLO' ELSE 'SALTO   ' END,
            r.asset_class, r.code, r.maturity_date, COALESCE(' -> ' || r.skip_reason, '');
    END LOOP;

    CREATE TEMP TABLE tmp_md ON COMMIT DROP AS
    SELECT id_master_data FROM tmp_expired WHERE skip_reason IS NULL;

    SELECT count(*) INTO v_rows FROM tmp_md;
    RAISE NOTICE 'Strumenti da cancellare: %', v_rows;

    -- -- deal e posizioni ----------------------------------------------------
    DELETE FROM position_txn_links l
    WHERE l.financial_txn IN (SELECT t.id_financial_txn FROM financial_txn t WHERE t.master_data IN (SELECT id_master_data FROM tmp_md))
       OR l.position_detail IN (SELECT p.id_position_detail FROM position_detail p WHERE p.master_data IN (SELECT id_master_data FROM tmp_md));
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  position_txn_links: %', v_rows;

    -- financial_txn_components: ON DELETE CASCADE
    DELETE FROM financial_txn WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  financial_txn: %', v_rows;

    DELETE FROM position_detail WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  position_detail: %', v_rows;

    DELETE FROM instrument_valuation WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  instrument_valuation: %', v_rows;

    -- -- quotazioni e regole --------------------------------------------------
    DELETE FROM calibration_profile_item
    WHERE master_data IN (SELECT id_master_data FROM tmp_md)
       OR instrument_quote IN (SELECT q.id_instrument_quote FROM instrument_quote q WHERE q.master_data IN (SELECT id_master_data FROM tmp_md));
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  calibration_profile_item: %', v_rows;

    DELETE FROM instrument_quote_hist
    WHERE master_data IN (SELECT id_master_data FROM tmp_md)
       OR instrument_quote IN (SELECT q.id_instrument_quote FROM instrument_quote q WHERE q.master_data IN (SELECT id_master_data FROM tmp_md));
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  instrument_quote_hist: %', v_rows;

    DELETE FROM instrument_quote WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  instrument_quote: %', v_rows;

    DELETE FROM broker_instrument_rules WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  broker_instrument_rules: %', v_rows;

    -- -- anagrafica: dalle sottotabelle a master_data (molte senza FK verso il padre)
    DELETE FROM deliverable_bonds WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  deliverable_bonds: %', v_rows;

    DELETE FROM cash_flow_item WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  cash_flow_item: %', v_rows;

    DELETE FROM cash_flow_reset WHERE master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  cash_flow_reset: %', v_rows;

    DELETE FROM flt_security_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  flt_security_master_data: %', v_rows;

    DELETE FROM security_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  security_master_data: %', v_rows;

    DELETE FROM loan_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  loan_master_data: %', v_rows;

    DELETE FROM cash_flow_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  cash_flow_master_data: %', v_rows;

    DELETE FROM bond_future_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  bond_future_master_data: %', v_rows;

    DELETE FROM fx_future_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  fx_future_master_data: %', v_rows;

    DELETE FROM mm_future_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  mm_future_master_data: %', v_rows;

    DELETE FROM power_future_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  power_future_master_data: %', v_rows;

    DELETE FROM commodity_delivery_profile WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  commodity_delivery_profile: %', v_rows;

    DELETE FROM cmd_future_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  cmd_future_master_data: %', v_rows;

    DELETE FROM future_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  future_master_data: %', v_rows;

    DELETE FROM currpair_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  currpair_master_data: %', v_rows;

    DELETE FROM forex_master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  forex_master_data: %', v_rows;

    DELETE FROM master_data WHERE id_master_data IN (SELECT id_master_data FROM tmp_md);
    GET DIAGNOSTICS v_rows = ROW_COUNT; RAISE NOTICE '  master_data: %', v_rows;

    IF dry_run THEN
        RAISE EXCEPTION 'DRY RUN: nessuna modifica salvata (impostare dry_run = false per cancellare)';
    END IF;
    RAISE NOTICE 'Cancellazione completata';
END $$;
