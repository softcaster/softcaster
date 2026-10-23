-- ============================================================================
-- listed_contract.contract_type: da stringa a intero con lookup contract_type
-- (enum ContractType, stessi id)
--
--   1 BOND_FUTURE   2 FX_FUTURE   3 MM_FUTURE   4 CMD_FUTURE   11 OPTION
--   id 1-9 future, 11-19 opzioni: i CHECK e gli indici usano questi intervalli
--
-- Per database con contract_type ancora VARCHAR ('FUTURE', 'BOND_FUTURE').
-- Le versioni attuali di new_master_data.sql e future_contracts_migration.sql
-- creano gia' questa struttura. Sostituisce bond_future_contract.sql.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Lookup
-- ----------------------------------------------------------------------------
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


-- ----------------------------------------------------------------------------
-- 2. Vincoli e indici che usano la colonna stringa
-- ----------------------------------------------------------------------------
ALTER TABLE listed_contract DROP CONSTRAINT IF EXISTS ck_listed_contract_type;
ALTER TABLE listed_contract DROP CONSTRAINT IF EXISTS ck_listed_contract_future;
ALTER TABLE listed_contract DROP CONSTRAINT IF EXISTS ck_listed_contract_option;
DROP INDEX IF EXISTS ux_listed_contract_future_month;
DROP INDEX IF EXISTS ux_listed_contract_option_series;


-- ----------------------------------------------------------------------------
-- 3. Conversione: il tipo del future dipende dalla testata
--    (ALTER ... USING non ammette sottoquery: colonna di appoggio)
-- ----------------------------------------------------------------------------
ALTER TABLE listed_contract ADD COLUMN contract_type_id INTEGER;

UPDATE listed_contract lc SET contract_type_id = CASE
    WHEN lc.contract_type = 'OPTION' THEN 11
    WHEN EXISTS (SELECT 1 FROM bond_future_master_data x WHERE x.id_master_data = lc.master_data) THEN 1
    WHEN EXISTS (SELECT 1 FROM fx_future_master_data   x WHERE x.id_master_data = lc.master_data) THEN 2
    WHEN EXISTS (SELECT 1 FROM mm_future_master_data   x WHERE x.id_master_data = lc.master_data) THEN 3
    WHEN EXISTS (SELECT 1 FROM cmd_future_master_data  x WHERE x.id_master_data = lc.master_data) THEN 4
END;

ALTER TABLE listed_contract DROP COLUMN contract_type;
ALTER TABLE listed_contract RENAME COLUMN contract_type_id TO contract_type;
ALTER TABLE listed_contract ALTER COLUMN contract_type SET NOT NULL;   -- fallisce se un contratto non e' stato riconosciuto


-- ----------------------------------------------------------------------------
-- 4. Nuovi vincoli e indici
-- ----------------------------------------------------------------------------
ALTER TABLE listed_contract ADD CONSTRAINT fk_listed_contract_contract_type
    FOREIGN KEY (contract_type) REFERENCES contract_type (id_contract_type);

ALTER TABLE listed_contract ADD CONSTRAINT ck_listed_contract_future CHECK (
    contract_type NOT BETWEEN 1 AND 9
    OR (last_trading_date IS NOT NULL AND option_expiry IS NULL AND option_type IS NULL AND strike IS NULL)
);
ALTER TABLE listed_contract ADD CONSTRAINT ck_listed_contract_option CHECK (
    contract_type NOT BETWEEN 11 AND 19
    OR (option_expiry IS NOT NULL AND option_type IS NOT NULL AND strike > 0 AND last_trading_date IS NULL)
);

CREATE UNIQUE INDEX ux_listed_contract_future_month
    ON listed_contract (master_data, contract_month) WHERE contract_type BETWEEN 1 AND 9;
CREATE UNIQUE INDEX ux_listed_contract_option_series
    ON listed_contract (option_expiry, option_type, strike) WHERE contract_type BETWEEN 11 AND 19;


-- controllo
SELECT lc.id_listed_contract, lc.contract_type, ct.code AS tipo, md.code AS testata, lc.code, lc.isin
FROM listed_contract lc
JOIN contract_type ct ON ct.id_contract_type = lc.contract_type
JOIN master_data md ON md.id_master_data = lc.master_data
ORDER BY md.code, lc.last_trading_date;

COMMIT;
