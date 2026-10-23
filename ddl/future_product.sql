-- ============================================================================
-- Future: scadenza in master_data, testata in future_product
--
--   future_product (testata, NON in master_data)      MBTP, M6E: dati comuni alle scadenze
--     ├── bond_future_product
--     ├── fx_future_product     underlying (coppia)
--     ├── mm_future_product     underlying
--     └── cmd_future_product    commodity_type
--           └── power_future_product   load_type
--
--   master_data -> future_master_data                 MBTPZ6: ISIN, mese, ultimo giorno, consegna
--     ├── bond_future_master_data  future_product -> bond_future_product, paniere (deliverable_bonds)
--     ├── fx_future_master_data    future_product -> fx_future_product
--     ├── mm_future_master_data    future_product -> mm_future_product
--     └── cmd_future_master_data   future_product -> cmd_future_product
--           └── power_future_master_data
--
-- master_data resta lo strumento negoziabile: terna (controparte, posizione,
-- anagrafica), quotazioni e valutazioni invariate.
--
-- Parte dallo stato del 23/10/2026 (listed_contract, contract_type, righe testata
-- 97 M6E e 98 MBTP in master_data). Sostituisce new_master_data.sql,
-- future_contracts_migration.sql e contract_type_lookup.sql.
-- Tutto in una transazione.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Testate
-- ----------------------------------------------------------------------------
CREATE SEQUENCE future_product_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE future_product_s OWNER TO sofie;

CREATE TABLE future_product (
    id_future_product       INTEGER       NOT NULL,
    code                    VARCHAR(25)   NOT NULL,      -- MBTP, M6E
    description             VARCHAR(255)  NOT NULL DEFAULT '',
    exchange_contract_code  VARCHAR(25)   NOT NULL DEFAULT '',
    settlement_type         INTEGER       NOT NULL,      -- SettlementType
    contract_value          NUMERIC(15,5) NOT NULL,
    tick_size               NUMERIC(15,5) NOT NULL,
    initial_margin          NUMERIC(15,5) NOT NULL,
    maintenance_margin      NUMERIC(15,5),               -- assente per i bond future
    -- valori copiati in master_data quando si crea una scadenza
    currency                INTEGER       NOT NULL,
    asset_class             INTEGER       NOT NULL,
    business_days           INTEGER       NOT NULL DEFAULT 2,
    multiplier              NUMERIC(15,5) NOT NULL DEFAULT 1,
    CONSTRAINT pk_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT uq_future_product_code UNIQUE (code),
    CONSTRAINT fk_future_product_settlement_type
        FOREIGN KEY (settlement_type) REFERENCES settlement_type (id_settlement_type),
    CONSTRAINT fk_future_product_currency FOREIGN KEY (currency) REFERENCES currency (id_currency),
    CONSTRAINT fk_future_product_asset_class FOREIGN KEY (asset_class) REFERENCES asset_class (id_asset_class)
);
ALTER TABLE future_product OWNER TO sofie;

CREATE TABLE bond_future_product (
    id_future_product  INTEGER NOT NULL,
    CONSTRAINT pk_bond_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_bond_future_product_parent
        FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product)
);
ALTER TABLE bond_future_product OWNER TO sofie;

CREATE TABLE fx_future_product (
    id_future_product  INTEGER NOT NULL,
    underlying         INTEGER NOT NULL,                 -- coppia di cambi
    CONSTRAINT pk_fx_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_fx_future_product_parent
        FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_fx_future_product_underlying
        FOREIGN KEY (underlying) REFERENCES forex_master_data (id_master_data)
);
ALTER TABLE fx_future_product OWNER TO sofie;

CREATE TABLE mm_future_product (
    id_future_product  INTEGER NOT NULL,
    underlying         INTEGER NOT NULL,
    CONSTRAINT pk_mm_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_mm_future_product_parent
        FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_mm_future_product_underlying
        FOREIGN KEY (underlying) REFERENCES forex_master_data (id_master_data)
);
ALTER TABLE mm_future_product OWNER TO sofie;

CREATE TABLE cmd_future_product (
    id_future_product  INTEGER NOT NULL,
    commodity_type     INTEGER NOT NULL,                 -- CommodityType
    CONSTRAINT pk_cmd_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_cmd_future_product_parent
        FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_cmd_future_product_commodity_type
        FOREIGN KEY (commodity_type) REFERENCES commodity_type (commodity_type_id)
);
ALTER TABLE cmd_future_product OWNER TO sofie;

CREATE TABLE power_future_product (
    id_future_product  INTEGER NOT NULL,
    load_type          INTEGER NOT NULL,                 -- LoadType
    CONSTRAINT pk_power_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_power_future_product_parent
        FOREIGN KEY (id_future_product) REFERENCES cmd_future_product (id_future_product),
    CONSTRAINT fk_power_future_product_load_type
        FOREIGN KEY (load_type) REFERENCES load_type (load_type_id)
);
ALTER TABLE power_future_product OWNER TO sofie;


-- ----------------------------------------------------------------------------
-- 2. Testate esistenti M6E e MBTP, dalle righe 97 e 98 di master_data
-- ----------------------------------------------------------------------------
CREATE TEMP TABLE header_map ON COMMIT DROP AS
SELECT md.id_master_data AS old_id, nextval('future_product_s') AS id_future_product
FROM master_data md
WHERE md.id_master_data IN (97, 98);

INSERT INTO future_product (id_future_product, code, description, exchange_contract_code, settlement_type,
                            contract_value, tick_size, initial_margin, maintenance_margin,
                            currency, asset_class, business_days, multiplier)
SELECT h.id_future_product, md.code, md.description, f.exchange_contract_code, f.settlement_type,
       COALESCE(fx.contract_value, b.contract_value), COALESCE(fx.tick_size, b.tick_size),
       COALESCE(fx.initial_margin, b.initial_margin), fx.maintenance_margin,
       md.currency, md.asset_class, md.business_days, md.multiplier
FROM header_map h
JOIN master_data md ON md.id_master_data = h.old_id
JOIN future_master_data f ON f.id_master_data = h.old_id
LEFT JOIN fx_future_master_data fx ON fx.id_master_data = h.old_id
LEFT JOIN bond_future_master_data b ON b.id_master_data = h.old_id;

INSERT INTO fx_future_product (id_future_product, underlying)
SELECT h.id_future_product, fx.underlying
FROM header_map h JOIN fx_future_master_data fx ON fx.id_master_data = h.old_id;

INSERT INTO bond_future_product (id_future_product)
SELECT h.id_future_product
FROM header_map h JOIN bond_future_master_data b ON b.id_master_data = h.old_id;


-- ----------------------------------------------------------------------------
-- 3. Scadenze: collegamento alla testata e nuove colonne
-- ----------------------------------------------------------------------------
ALTER TABLE future_master_data ADD COLUMN contract_month DATE;
ALTER TABLE future_master_data ADD COLUMN delivery_date  DATE;

ALTER TABLE bond_future_master_data ADD COLUMN future_product INTEGER;
ALTER TABLE fx_future_master_data   ADD COLUMN future_product INTEGER;
ALTER TABLE mm_future_master_data   ADD COLUMN future_product INTEGER;
ALTER TABLE cmd_future_master_data  ADD COLUMN future_product INTEGER;

-- 61 M6EU6, 94 M6EZ6 -> M6E;  96 -> MBTP
UPDATE fx_future_master_data SET future_product = (SELECT id_future_product FROM future_product WHERE code = 'M6E')
WHERE id_master_data IN (61, 94);
UPDATE bond_future_master_data SET future_product = (SELECT id_future_product FROM future_product WHERE code = 'MBTP')
WHERE id_master_data = 96;

UPDATE future_master_data f
SET contract_month = date_trunc('month', f.last_trading_date)::date,
    delivery_date  = CASE WHEN f.id_master_data IN (61, 94) THEN md.maturity_date END
FROM master_data md
WHERE md.id_master_data = f.id_master_data AND f.id_master_data IN (61, 94, 96);

-- MBTP dicembre: codice standard, ISIN invariato
UPDATE master_data SET code = 'MBTPZ6' WHERE id_master_data = 96;


-- ----------------------------------------------------------------------------
-- 4. Struttura precedente: listed_contract, contract_type, opzioni, righe testata
-- ----------------------------------------------------------------------------
DELETE FROM deliverable_bonds;                                    -- si reimporta
ALTER TABLE deliverable_bonds DROP COLUMN IF EXISTS listed_contract;
ALTER TABLE deliverable_bonds ADD COLUMN master_data INTEGER NOT NULL;
ALTER TABLE deliverable_bonds ADD CONSTRAINT fk_deliverable_bonds_master_data
    FOREIGN KEY (master_data) REFERENCES bond_future_master_data (id_master_data);
DROP INDEX IF EXISTS ux_deliverable_bonds_isin;
CREATE UNIQUE INDEX ux_deliverable_bonds_isin ON deliverable_bonds (master_data, isin);

ALTER TABLE instrument_quote DROP COLUMN IF EXISTS listed_contract;
ALTER TABLE IF EXISTS listed_contract DROP CONSTRAINT IF EXISTS fk_listed_contract_option_expiry;
DROP TABLE IF EXISTS option_expiry;
DROP TABLE IF EXISTS listed_contract;
DROP TABLE IF EXISTS contract_type;
DROP TABLE IF EXISTS option_master_data;
DROP SEQUENCE IF EXISTS listed_contract_s;
DROP SEQUENCE IF EXISTS option_expiry_s;

-- righe testata 97 e 98: nessun riferimento (verificato il 23/10/2026)
DELETE FROM fx_future_master_data   WHERE id_master_data = 97;
DELETE FROM bond_future_master_data WHERE id_master_data = 98;
DELETE FROM future_master_data      WHERE id_master_data IN (97, 98);
DELETE FROM master_data             WHERE id_master_data IN (97, 98);


-- ----------------------------------------------------------------------------
-- 5. Colonne passate alla testata, nuovi vincoli
-- ----------------------------------------------------------------------------
ALTER TABLE future_master_data DROP COLUMN exchange_contract_code;
ALTER TABLE future_master_data DROP COLUMN settlement_type;       -- elimina anche fk_settlement_type
ALTER TABLE future_master_data ALTER COLUMN last_trading_date SET NOT NULL;
ALTER TABLE future_master_data ALTER COLUMN contract_month SET NOT NULL;

ALTER TABLE bond_future_master_data DROP COLUMN contract_value, DROP COLUMN tick_size, DROP COLUMN initial_margin;
ALTER TABLE fx_future_master_data   DROP COLUMN underlying, DROP COLUMN contract_value, DROP COLUMN tick_size,
                                    DROP COLUMN initial_margin, DROP COLUMN maintenance_margin;
ALTER TABLE mm_future_master_data   DROP COLUMN underlying, DROP COLUMN contract_value, DROP COLUMN tick_size,
                                    DROP COLUMN initial_margin, DROP COLUMN maintenance_margin;
ALTER TABLE cmd_future_master_data  DROP COLUMN commodity_type, DROP COLUMN contract_value, DROP COLUMN tick_size,
                                    DROP COLUMN initial_margin, DROP COLUMN maintenance_margin;
ALTER TABLE power_future_master_data DROP COLUMN load_type;

ALTER TABLE bond_future_master_data ALTER COLUMN future_product SET NOT NULL;
ALTER TABLE fx_future_master_data   ALTER COLUMN future_product SET NOT NULL;
ALTER TABLE mm_future_master_data   ALTER COLUMN future_product SET NOT NULL;
ALTER TABLE cmd_future_master_data  ALTER COLUMN future_product SET NOT NULL;

ALTER TABLE bond_future_master_data ADD CONSTRAINT fk_bond_future_master_data_product
    FOREIGN KEY (future_product) REFERENCES bond_future_product (id_future_product);
ALTER TABLE fx_future_master_data ADD CONSTRAINT fk_fx_future_master_data_product
    FOREIGN KEY (future_product) REFERENCES fx_future_product (id_future_product);
ALTER TABLE mm_future_master_data ADD CONSTRAINT fk_mm_future_master_data_product
    FOREIGN KEY (future_product) REFERENCES mm_future_product (id_future_product);
ALTER TABLE cmd_future_master_data ADD CONSTRAINT fk_cmd_future_master_data_product
    FOREIGN KEY (future_product) REFERENCES cmd_future_product (id_future_product);

-- Univocita' (testata, mese): testata e mese stanno in tabelle diverse, un indice
-- non basta; la verifica e' nel DAO (FutureMasterData della stessa testata e mese).


-- ----------------------------------------------------------------------------
-- 6. Controllo
-- ----------------------------------------------------------------------------
SELECT p.code AS testata, md.id_master_data, md.code, f.isin, f.contract_month, f.last_trading_date, f.delivery_date
FROM future_master_data f
JOIN master_data md USING (id_master_data)
LEFT JOIN bond_future_master_data b USING (id_master_data)
LEFT JOIN fx_future_master_data fx USING (id_master_data)
JOIN future_product p ON p.id_future_product = COALESCE(b.future_product, fx.future_product)
ORDER BY p.code, f.last_trading_date;

COMMIT;
