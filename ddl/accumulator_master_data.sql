-- ============================================================================
-- Accumulator / decumulator OTC (FX e commodity): foglia di master_data
--
--   master_data -> accumulator_master_data     termini del contratto
--                   └─ accumulator_fixing      calendario dei fixing (1-N)
--
-- Ogni contratto OTC e' un'anagrafica a se', creata dal deal capture insieme
-- al calendario dei fixing e alla transazione. In master_data:
--   code          FXA-EURUSD-<id>, CMA-<sottostante>-<id>
--   currency      valuta di regolamento (quotata della coppia, valuta della commodity)
--   issue_date    data di conclusione, maturity_date ultimo regolamento
--   business_days spot lag del regolamento
--
-- Barriere: knock-out obbligatorio (osservazione al fixing, in continuo o alle
-- date di regolamento), knock-in facoltativo che attiva la leva o il contratto.
--
-- Asset class: FXA (sottostante forex_master_data), CMA (sottostante qualsiasi,
-- es. scadenza di un future); processor, evaluator e forward per asset class.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Asset class
-- ----------------------------------------------------------------------------
INSERT INTO asset_class (id_asset_class, code, description, super_class)
VALUES (nextval('asset_class_s'), 'FXA', 'Forex Accumulator', (SELECT id_super_class FROM super_class WHERE code = 'DER'));

INSERT INTO asset_class (id_asset_class, code, description, super_class)
VALUES (nextval('asset_class_s'), 'CMA', 'Commodity Accumulator', (SELECT id_super_class FROM super_class WHERE code = 'DER'));

-- ----------------------------------------------------------------------------
-- 2. Tabelle di decodifica
-- ----------------------------------------------------------------------------
CREATE TABLE accumulator_type (
    accumulator_type_id  INTEGER NOT NULL,
    code                 VARCHAR(25) NOT NULL,
    description          VARCHAR(64) NOT NULL,
    CONSTRAINT pk_accumulator_type PRIMARY KEY (accumulator_type_id),
    CONSTRAINT uq_accumulator_type_code UNIQUE (code)
);
ALTER TABLE accumulator_type OWNER TO sofie;

-- chi detiene il contratto compra (ACC) o vende (DEC) il sottostante allo strike
INSERT INTO accumulator_type VALUES (1, 'ACCUMULATOR', 'Accumulator (buys at strike, KO above)');
INSERT INTO accumulator_type VALUES (2, 'DECUMULATOR', 'Decumulator (sells at strike, KO below)');

-- come si osserva il knock-out
CREATE TABLE barrier_observation (
    barrier_observation_id  INTEGER NOT NULL,
    code                    VARCHAR(25) NOT NULL,
    description             VARCHAR(64) NOT NULL,
    CONSTRAINT pk_barrier_observation PRIMARY KEY (barrier_observation_id),
    CONSTRAINT uq_barrier_observation_code UNIQUE (code)
);
ALTER TABLE barrier_observation OWNER TO sofie;

INSERT INTO barrier_observation VALUES (1, 'FIXING', 'At each fixing');
INSERT INTO barrier_observation VALUES (2, 'CONTINUOUS', 'Continuous');
INSERT INTO barrier_observation VALUES (3, 'SETTLEMENT', 'At settlement dates');

-- cosa attiva il knock-in
CREATE TABLE knock_in_effect (
    knock_in_effect_id  INTEGER NOT NULL,
    code                VARCHAR(25) NOT NULL,
    description         VARCHAR(64) NOT NULL,
    CONSTRAINT pk_knock_in_effect PRIMARY KEY (knock_in_effect_id),
    CONSTRAINT uq_knock_in_effect_code UNIQUE (code)
);
ALTER TABLE knock_in_effect OWNER TO sofie;

INSERT INTO knock_in_effect VALUES (1, 'LEVERAGE', 'Activates the leverage on unfavourable fixings');
INSERT INTO knock_in_effect VALUES (2, 'CONTRACT', 'Activates the contract');

-- frequenze di regolamento: si estende frequency (enum Frequency) con
-- settimanale e giornaliera, non ammesse per le cedole; alla scadenza = NONE.
-- CUSTOM (7) e' nell'enum ma mancava sul database.
INSERT INTO frequency (id_frequency, code, description, year_fraction) VALUES (7, 'CUSTOM', 'Custom', 0)
    ON CONFLICT (id_frequency) DO NOTHING;
INSERT INTO frequency (id_frequency, code, description, year_fraction) VALUES (8, 'WEEKLY', 'Weekly', 52);
INSERT INTO frequency (id_frequency, code, description, year_fraction) VALUES (9, 'DAILY', 'Daily', 365);

-- ----------------------------------------------------------------------------
-- 3. Anagrafica
-- ----------------------------------------------------------------------------
CREATE TABLE accumulator_master_data (
    id_master_data        INTEGER NOT NULL,
    underlying            INTEGER NOT NULL,                  -- coppia (FXA) o strumento (CMA)
    accumulator_type      INTEGER NOT NULL,
    settlement_type       INTEGER NOT NULL,                  -- fisico o per differenza
    settlement_frequency  INTEGER NOT NULL,                  -- DAILY, WEEKLY, MONTHLY, NONE (alla scadenza)
    strike               NUMERIC(18,8) NOT NULL,
    ko_barrier            NUMERIC(18,8) NOT NULL,
    ko_observation        INTEGER NOT NULL DEFAULT 1,        -- FIXING, CONTINUOUS, SETTLEMENT
    ki_barrier            NUMERIC(18,8),                     -- knock-in facoltativo
    ki_effect             INTEGER,                           -- LEVERAGE, CONTRACT (con ki_barrier)
    leverage             NUMERIC(15,5) NOT NULL DEFAULT 1,  -- fattore sui fixing sfavorevoli
    daily_quantity        NUMERIC(18,5) NOT NULL,            -- valuta base / unita' di commodity
    first_fixing_date     DATE NOT NULL,
    last_fixing_date      DATE NOT NULL,
    guaranteed_fixings    INTEGER NOT NULL DEFAULT 0,        -- fixing iniziali senza knock-out
    ko_accrual            BOOLEAN NOT NULL DEFAULT FALSE,    -- si accumula il giorno del knock-out
    fixing_time           TIME,
    fixing_time_zone      VARCHAR(64),                       -- IANA, es. Europe/Rome
    ko_date               DATE,                              -- valorizzata al knock-out
    ki_date               DATE,                              -- valorizzata al knock-in
    CONSTRAINT pk_accumulator_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_accumulator_master_data_parent
        FOREIGN KEY (id_master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_accumulator_master_data_underlying
        FOREIGN KEY (underlying) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_accumulator_master_data_type
        FOREIGN KEY (accumulator_type) REFERENCES accumulator_type (accumulator_type_id),
    CONSTRAINT fk_accumulator_master_data_settlement_type
        FOREIGN KEY (settlement_type) REFERENCES settlement_type (id_settlement_type),
    CONSTRAINT fk_accumulator_master_data_settlement_frequency
        FOREIGN KEY (settlement_frequency) REFERENCES frequency (id_frequency),
    CONSTRAINT fk_accumulator_master_data_ko_observation
        FOREIGN KEY (ko_observation) REFERENCES barrier_observation (barrier_observation_id),
    CONSTRAINT fk_accumulator_master_data_ki_effect
        FOREIGN KEY (ki_effect) REFERENCES knock_in_effect (knock_in_effect_id),
    CONSTRAINT ck_accumulator_master_data_quantities
        CHECK (strike > 0 AND ko_barrier > 0 AND leverage >= 1 AND daily_quantity > 0 AND guaranteed_fixings >= 0),
    CONSTRAINT ck_accumulator_master_data_dates
        CHECK (last_fixing_date >= first_fixing_date),
    -- accumulator KI < strike < KO, decumulator KO < strike < KI
    CONSTRAINT ck_accumulator_master_data_barrier
        CHECK ((accumulator_type = 1 AND ko_barrier > strike AND (ki_barrier IS NULL OR ki_barrier < strike))
            OR (accumulator_type = 2 AND ko_barrier < strike AND (ki_barrier IS NULL OR ki_barrier > strike))),
    -- livello ed effetto del knock-in insieme; la data solo con il livello
    CONSTRAINT ck_accumulator_master_data_knock_in
        CHECK ((ki_barrier IS NULL) = (ki_effect IS NULL) AND (ki_date IS NULL OR ki_barrier IS NOT NULL))
);
CREATE INDEX idx_accumulator_master_data_underlying ON accumulator_master_data (underlying);
ALTER TABLE accumulator_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- 4. Calendario dei fixing
-- ----------------------------------------------------------------------------
CREATE TABLE accumulator_fixing (
    id_accumulator_fixing  INTEGER NOT NULL,
    master_data            INTEGER NOT NULL,
    fixing_date            DATE NOT NULL,
    settlement_date        DATE NOT NULL,                    -- regolamento del periodo
    fixing_price           NUMERIC(18,8),                    -- valorizzato al fixing
    accumulated_quantity   NUMERIC(18,5),                    -- quantita' maturata al fixing (leva inclusa)
    CONSTRAINT pk_accumulator_fixing PRIMARY KEY (id_accumulator_fixing),
    CONSTRAINT fk_accumulator_fixing_master_data
        FOREIGN KEY (master_data) REFERENCES accumulator_master_data (id_master_data) ON DELETE CASCADE,
    CONSTRAINT uq_accumulator_fixing UNIQUE (master_data, fixing_date),
    CONSTRAINT ck_accumulator_fixing_dates CHECK (settlement_date >= fixing_date)
);
CREATE INDEX idx_accumulator_fixing_settlement ON accumulator_fixing (settlement_date);
ALTER TABLE accumulator_fixing OWNER TO sofie;

CREATE SEQUENCE IF NOT EXISTS accumulator_fixing_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE accumulator_fixing_s OWNER TO sofie;

COMMIT;
