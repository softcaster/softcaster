-- ============================================================================
-- Future esistenti: da una riga di master_data per scadenza
-- a testata + contratto (listed_contract, contract_type 1-9 = future)
--
-- Prerequisito: new_master_data.sql eseguito subito prima
--   (listed_contract vuota, listed_contract_s, deliverable_bonds.listed_contract)
-- Da eseguire UNA volta: una seconda esecuzione creerebbe altre testate M6E e MBTP.
--
-- Situazione di partenza (future_master_data, 23/10/2026):
--   61  M6EU6         CME Micro EUR/USD set-2026 (scaduto)
--   94  M6EZ6         CME Micro EUR/USD dic-2026
--   96  IT0026145778  Mini BTP 10y dic-2026
--   exchange_contract_code contiene il codice della scadenza, non del prodotto.
--
-- Per ogni prodotto (M6E, MBTP):
--   - si crea una NUOVA testata copiando la scadenza piu' recente
--     (codice prodotto, niente ISIN ne' ultimo giorno, maturity_date = 2070-01-01)
--   - ogni riga esistente diventa un contratto con codice standard (MBTPZ6,
--     M6EZ6) e l'ISIN che aveva: chi cerca con il vecchio codice-ISIN lo trova per ISIN
--   - le vecchie righe restano invariate: chi le referenzia (operazioni,
--     quotazioni) continua a funzionare; si eliminano al passo 6 dopo aver
--     spostato i riferimenti
--
-- I panieri si reimportano (DeliverableBondsImportMgr), non si convertono.
-- ============================================================================


-- ----------------------------------------------------------------------------
-- 0. Situazione attuale (solo lettura)
-- ----------------------------------------------------------------------------
SELECT md.id_master_data, md.code, md.description, fmd.exchange_contract_code, fmd.isin,
       fmd.last_trading_date, md.maturity_date,
       CASE WHEN b.id_master_data IS NOT NULL THEN 'BOND'
            WHEN f.id_master_data IS NOT NULL THEN 'FX'
            ELSE 'altro' END AS kind
FROM future_master_data fmd
JOIN master_data md USING (id_master_data)
LEFT JOIN bond_future_master_data b USING (id_master_data)
LEFT JOIN fx_future_master_data f USING (id_master_data)
ORDER BY kind, fmd.last_trading_date;


BEGIN;

-- ----------------------------------------------------------------------------
-- 1. La testata non ha ISIN ne' ultimo giorno di negoziazione
-- ----------------------------------------------------------------------------
ALTER TABLE future_master_data ALTER COLUMN isin DROP NOT NULL;
ALTER TABLE future_master_data ALTER COLUMN last_trading_date DROP NOT NULL;


-- ----------------------------------------------------------------------------
-- 2. Scadenze esistenti -> prodotto
-- ----------------------------------------------------------------------------
CREATE TEMP TABLE old_future ON COMMIT DROP AS
SELECT id_master_data, product
FROM (VALUES (61, 'M6E'), (94, 'M6E'), (96, 'MBTP')) AS v (id_master_data, product);


-- ----------------------------------------------------------------------------
-- 3. Testate: copia della scadenza piu' recente con un nuovo id
-- ----------------------------------------------------------------------------
CREATE TEMP TABLE product_header ON COMMIT DROP AS
SELECT product,
       max(id_master_data) AS template_id,
       NULL::integer       AS id_header,
       CASE product WHEN 'M6E'  THEN 'CME Micro EUR/USD future'
                    WHEN 'MBTP' THEN 'Mini BTP 10y future' END AS description
FROM old_future
GROUP BY product;

UPDATE product_header SET id_header = nextval('master_data_s');

-- master_data
CREATE TEMP TABLE md_copy ON COMMIT DROP AS
SELECT md.* FROM master_data md JOIN product_header p ON p.template_id = md.id_master_data;
UPDATE md_copy c
SET id_master_data = p.id_header, code = p.product, description = p.description,
    issue_date = current_date, maturity_date = DATE '2070-01-01'
FROM product_header p
WHERE c.id_master_data = p.template_id;
INSERT INTO master_data SELECT * FROM md_copy;

-- future_master_data
CREATE TEMP TABLE fmd_copy ON COMMIT DROP AS
SELECT f.* FROM future_master_data f JOIN product_header p ON p.template_id = f.id_master_data;
UPDATE fmd_copy c
SET id_master_data = p.id_header, exchange_contract_code = p.product,
    isin = NULL, last_trading_date = NULL
FROM product_header p
WHERE c.id_master_data = p.template_id;
INSERT INTO future_master_data SELECT * FROM fmd_copy;

-- fx_future_master_data (M6E)
CREATE TEMP TABLE fx_copy ON COMMIT DROP AS
SELECT f.* FROM fx_future_master_data f JOIN product_header p ON p.template_id = f.id_master_data;
UPDATE fx_copy c SET id_master_data = p.id_header
FROM product_header p WHERE c.id_master_data = p.template_id;
INSERT INTO fx_future_master_data SELECT * FROM fx_copy;

-- bond_future_master_data (MBTP)
CREATE TEMP TABLE bond_copy ON COMMIT DROP AS
SELECT b.* FROM bond_future_master_data b JOIN product_header p ON p.template_id = b.id_master_data;
UPDATE bond_copy c SET id_master_data = p.id_header
FROM product_header p WHERE c.id_master_data = p.template_id;
INSERT INTO bond_future_master_data SELECT * FROM bond_copy;


-- ----------------------------------------------------------------------------
-- 4. Un contratto per ogni scadenza esistente:
--    FX -> 2 FX_FUTURE (FxFutureContract), bond -> 1 BOND_FUTURE (BondFutureContract)
--    FX:   delivery_date = maturity_date (data usata finora da FxForwardCalculator)
--    bond: delivery_date NULL (regolamento per cassa: expiry + T+2, calcolato)
-- ----------------------------------------------------------------------------
INSERT INTO listed_contract (id_listed_contract, contract_type, master_data, code, isin,
                             contract_month, last_trading_date, delivery_date)
SELECT nextval('listed_contract_s'),
       CASE WHEN fx.id_master_data IS NOT NULL THEN 2 ELSE 1 END,
       p.id_header,
       -- codice standard come FutureContract.standardCode: prodotto + mese (F..Z) + ultima cifra dell'anno
       p.product || substr('FGHJKMNQUVXZ', extract(month FROM fmd.last_trading_date)::int, 1)
                 || (extract(year FROM fmd.last_trading_date)::int % 10),
       fmd.isin,
       date_trunc('month', fmd.last_trading_date)::date, fmd.last_trading_date,
       CASE WHEN fx.id_master_data IS NOT NULL THEN md.maturity_date END
FROM old_future o
JOIN product_header p USING (product)
JOIN master_data md ON md.id_master_data = o.id_master_data
JOIN future_master_data fmd ON fmd.id_master_data = o.id_master_data
LEFT JOIN fx_future_master_data fx ON fx.id_master_data = o.id_master_data
ORDER BY fmd.last_trading_date;


-- ----------------------------------------------------------------------------
-- 5. Paniere legato alla scadenza: si svuota e si reimporta
-- ----------------------------------------------------------------------------
DELETE FROM deliverable_bonds;
ALTER TABLE deliverable_bonds DROP COLUMN IF EXISTS master_data;   -- elimina anche fk_master_data e idx_deliverable_bonds_isin
ALTER TABLE deliverable_bonds ALTER COLUMN listed_contract SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_deliverable_bonds_isin ON deliverable_bonds (listed_contract, isin);

-- controllo prima del COMMIT: testate e contratti creati
SELECT md.id_master_data, md.code AS testata, ct.code AS tipo, lc.code, lc.isin, lc.contract_month,
       lc.last_trading_date, lc.delivery_date
FROM listed_contract lc
JOIN master_data md ON md.id_master_data = lc.master_data
JOIN contract_type ct ON ct.id_contract_type = lc.contract_type
WHERE lc.contract_type BETWEEN 1 AND 9
ORDER BY md.code, lc.last_trading_date;

COMMIT;
-- in caso di risultato inatteso, al posto del COMMIT: ROLLBACK;


-- ----------------------------------------------------------------------------
-- 6. Vecchie righe per scadenza (61, 94, 96)
--    Prima elencare chi le referenzia (query al passo 5 di new_master_data.sql),
--    spostare quei riferimenti su testata + contratto, poi:
-- ----------------------------------------------------------------------------
-- DELETE FROM fx_future_master_data   WHERE id_master_data IN (61, 94);
-- DELETE FROM bond_future_master_data WHERE id_master_data IN (96);
-- DELETE FROM future_master_data      WHERE id_master_data IN (61, 94, 96);
-- DELETE FROM master_data             WHERE id_master_data IN (61, 94, 96);
