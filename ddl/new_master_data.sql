-- ============================================================================
-- Derivati quotati: testata in master_data, dettaglio in listed_contract
--
--   master_data                       testata 6E / EUU / MBTP: valuta, asset class, daycount...
--     ├── future_master_data          dati di prodotto del future (gia' esistente)
--     └── option_master_data          dati di prodotto dell'opzione (nuova)
--           └── option_expiry         scadenza (dic-2026): data e future sottostante
--
--   listed_contract                   dettaglio NEGOZIABILE (SINGLE_TABLE), discriminatore
--                                     contract_type -> lookup contract_type (enum ContractType):
--     1  BOND_FUTURE                  MBTP dic-2026: mese, ultimo giorno, consegna, paniere
--     2  FX_FUTURE                    M6EZ6: mese, ultimo giorno, consegna
--     3  MM_FUTURE, 4 CMD_FUTURE      come FX_FUTURE
--     11 OPTION                       call 1,135 dic-2026: scadenza, tipo, strike
--     id 1-9 future, 11-19 opzioni: i CHECK e gli indici usano questi intervalli
--
--   instrument_quote                  master_data (testata) + listed_contract (facoltativo)
--
-- Colonne di master_data senza significato per la testata: valori di default
--   issue_date = data di inserimento, maturity_date = 2070-01-01
--
-- Tutto in una transazione: in caso di errore non resta nulla a meta'.
-- Rieseguibile: il passo 0 elimina gli oggetti creati da un'esecuzione precedente
-- (solo prima di caricare dati nelle nuove tabelle).
-- Dopo questo script: future_contracts_migration.sql
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 0. Pulizia: oggetti di esecuzioni precedenti e della proposta precedente
-- ----------------------------------------------------------------------------
ALTER TABLE instrument_quote  DROP COLUMN IF EXISTS listed_contract;   -- elimina anche FK e indice
ALTER TABLE deliverable_bonds DROP COLUMN IF EXISTS listed_contract;
DROP TABLE IF EXISTS fx_future_option_master_data;                     -- proposta precedente
ALTER TABLE IF EXISTS listed_contract DROP CONSTRAINT IF EXISTS fk_listed_contract_option_expiry;
DROP TABLE IF EXISTS option_expiry;
DROP TABLE IF EXISTS listed_contract;
DROP TABLE IF EXISTS contract_type;
DROP TABLE IF EXISTS option_master_data;
DROP SEQUENCE IF EXISTS listed_contract_s;
DROP SEQUENCE IF EXISTS option_expiry_s;


-- ----------------------------------------------------------------------------
-- 1. Testata opzioni (sottoclasse di master_data, strategia JOINED)
--    Gli enum sono salvati con il loro id (IdentifiableEnum), come gli altri converter.
-- ----------------------------------------------------------------------------
CREATE TABLE option_master_data (
    id_master_data          INTEGER      NOT NULL,
    exchange_contract_code  VARCHAR(20)  NOT NULL,   -- es. 'EUU'
    exercise_style          INTEGER      NOT NULL,   -- ExerciseStyle
    premium_style           INTEGER      NOT NULL,   -- PremiumStyle
    settlement_type         INTEGER,                 -- SettlementType
    contract_size           NUMERIC      NOT NULL,   -- nozionale per contratto (125000 per EUU)
    tick_size               NUMERIC,
    expiry_time             TIME,                    -- es. 10:00
    expiry_time_zone        VARCHAR(40),             -- es. 'America/New_York'
    underlying_product      INTEGER,                 -- testata del future sottostante (6E)
    CONSTRAINT pk_option_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_option_master_data_master_data
        FOREIGN KEY (id_master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_option_master_data_underlying
        FOREIGN KEY (underlying_product) REFERENCES future_master_data (id_master_data),
    CONSTRAINT ck_option_master_data_size CHECK (contract_size > 0),
    CONSTRAINT ck_option_master_data_expiry_time
        CHECK ((expiry_time IS NULL) = (expiry_time_zone IS NULL))
);
ALTER TABLE option_master_data OWNER TO sofie;


-- ----------------------------------------------------------------------------
-- 2. Dettaglio negoziabile: future e serie di opzioni
-- ----------------------------------------------------------------------------
-- lookup del discriminatore: stessi id dell'enum ContractType
CREATE TABLE contract_type (
    id_contract_type  INTEGER      NOT NULL,
    code              VARCHAR(25)  NOT NULL,
    description       VARCHAR(50)  NOT NULL DEFAULT '',
    CONSTRAINT pk_contract_type PRIMARY KEY (id_contract_type),
    CONSTRAINT uq_contract_type_code UNIQUE (code)
);
ALTER TABLE contract_type OWNER TO sofie;

INSERT INTO contract_type (id_contract_type, code, description) VALUES
    (1,  'BOND_FUTURE', 'Bond future'),
    (2,  'FX_FUTURE',   'Currency future'),
    (3,  'MM_FUTURE',   'Money market future'),
    (4,  'CMD_FUTURE',  'Commodity future'),
    (11, 'OPTION',      'Listed option');

CREATE SEQUENCE listed_contract_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE listed_contract_s OWNER TO sofie;

CREATE SEQUENCE option_expiry_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE option_expiry_s OWNER TO sofie;

CREATE TABLE listed_contract (
    id_listed_contract  INTEGER      NOT NULL,
    contract_type       INTEGER      NOT NULL,       -- discriminatore JPA: ContractType (lookup contract_type)
    master_data         INTEGER      NOT NULL,       -- testata (6E per 6EZ6, EUU per le serie)
    code                VARCHAR(40)  NOT NULL,       -- codice univoco del contratto
    isin                VARCHAR(12),
    -- future
    contract_month      DATE,                        -- primo giorno del mese di scadenza
    last_trading_date   DATE,
    delivery_date       DATE,
    -- opzione
    option_expiry       INTEGER,
    option_type         INTEGER,                     -- OptionType
    strike              NUMERIC,
    CONSTRAINT pk_listed_contract PRIMARY KEY (id_listed_contract),
    CONSTRAINT uq_listed_contract_code UNIQUE (code),
    CONSTRAINT fk_listed_contract_master_data
        FOREIGN KEY (master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_listed_contract_contract_type
        FOREIGN KEY (contract_type) REFERENCES contract_type (id_contract_type),
    CONSTRAINT ck_listed_contract_future CHECK (                -- future: id 1-9
        contract_type NOT BETWEEN 1 AND 9
        OR (last_trading_date IS NOT NULL AND option_expiry IS NULL AND option_type IS NULL AND strike IS NULL)
    ),
    CONSTRAINT ck_listed_contract_option CHECK (                -- opzioni: id 11-19
        contract_type NOT BETWEEN 11 AND 19
        OR (option_expiry IS NOT NULL AND option_type IS NOT NULL AND strike > 0 AND last_trading_date IS NULL)
    )
);
ALTER TABLE listed_contract OWNER TO sofie;

CREATE TABLE option_expiry (
    id_option_expiry  INTEGER NOT NULL,
    master_data       INTEGER NOT NULL,              -- testata opzione (EUU)
    expiry_date       DATE    NOT NULL,              -- data esplicita, anche se generata da una regola
    underlying        INTEGER,                       -- future sottostante di questa scadenza (6EZ6)
    CONSTRAINT pk_option_expiry PRIMARY KEY (id_option_expiry),
    CONSTRAINT uq_option_expiry UNIQUE (master_data, expiry_date),
    CONSTRAINT fk_option_expiry_master_data
        FOREIGN KEY (master_data) REFERENCES option_master_data (id_master_data),
    CONSTRAINT fk_option_expiry_underlying
        FOREIGN KEY (underlying) REFERENCES listed_contract (id_listed_contract)
);
ALTER TABLE option_expiry OWNER TO sofie;

-- piu' scadenze di opzioni (ott, nov, dic) sullo stesso future: ricerca per sottostante
CREATE INDEX ix_option_expiry_underlying ON option_expiry (underlying);

ALTER TABLE listed_contract ADD CONSTRAINT fk_listed_contract_option_expiry
    FOREIGN KEY (option_expiry) REFERENCES option_expiry (id_option_expiry);

-- univocita' per tipo (indici parziali PostgreSQL)
CREATE UNIQUE INDEX ux_listed_contract_future_month
    ON listed_contract (master_data, contract_month) WHERE contract_type BETWEEN 1 AND 9;
CREATE UNIQUE INDEX ux_listed_contract_option_series
    ON listed_contract (option_expiry, option_type, strike) WHERE contract_type BETWEEN 11 AND 19;
CREATE INDEX ix_listed_contract_master_data ON listed_contract (master_data);


-- ----------------------------------------------------------------------------
-- 3. Paniere dei bond future: dipende dalla singola scadenza
--    NOT NULL e rimozione di master_data: in future_contracts_migration.sql
-- ----------------------------------------------------------------------------
ALTER TABLE deliverable_bonds ADD COLUMN listed_contract INTEGER;
ALTER TABLE deliverable_bonds ADD CONSTRAINT fk_deliverable_bonds_listed_contract
    FOREIGN KEY (listed_contract) REFERENCES listed_contract (id_listed_contract);


-- ----------------------------------------------------------------------------
-- 4. Quotazioni: contratto specifico facoltativo
-- ----------------------------------------------------------------------------
ALTER TABLE instrument_quote ADD COLUMN listed_contract INTEGER;
ALTER TABLE instrument_quote ADD CONSTRAINT fk_instrument_quote_listed_contract
    FOREIGN KEY (listed_contract) REFERENCES listed_contract (id_listed_contract);
CREATE INDEX ix_instrument_quote_listed_contract ON instrument_quote (listed_contract);

-- Se esiste un vincolo univoco sulle quotazioni deve includere il contratto
-- (NULL per bond e azioni). PostgreSQL 15+:
--   ALTER TABLE instrument_quote DROP CONSTRAINT <vincolo univoco attuale>;
--   ALTER TABLE instrument_quote ADD CONSTRAINT uq_instrument_quote
--       UNIQUE NULLS NOT DISTINCT (master_data, listed_contract, <colonna data>);

COMMIT;


-- ----------------------------------------------------------------------------
-- 5. Prima di eliminare i vecchi future "per scadenza": cosa li referenzia?
--    Elenca tutte le chiavi esterne verso master_data, per non lasciare righe orfane.
-- ----------------------------------------------------------------------------
--    (pg_constraint: information_schema duplica le righe quando piu' tabelle
--    hanno FK con lo stesso nome, es. fk_master_data)
SELECT c.conrelid::regclass AS table_name, a.attname AS column_name, c.conname AS constraint_name
FROM pg_constraint c
JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = c.conkey[1]
WHERE c.contype = 'f'
  AND c.confrelid = 'master_data'::regclass
ORDER BY 1;
