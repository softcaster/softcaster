-- ============================================================================
-- Opzioni: serie in master_data, testata in option_product
--
--   option_product (testata, NON in master_data)      EUU: dati comuni alle serie
--     future_product  -> sottostante derivato: le serie puntano a una scadenza
--                        di questa testata (es. EUU -> scadenze M6E)
--     underlying      -> sottostante secco fisso (titolo, azione, coppia di
--                        cambi): le serie puntano a questo strumento
--     (al piu' uno dei due)
--
--   master_data -> option_master_data                 EUUZ6 C1.135: tipo, strike,
--     option_product, underlying (master_data)       sottostante; scadenza = maturity_date
--
-- master_data resta lo strumento negoziabile: terna (controparte, posizione,
-- anagrafica), quotazioni e valutazioni invariate.
-- Tutto in una transazione.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Lookup (IdentifiableEnum dell'engine)
-- ----------------------------------------------------------------------------
CREATE TABLE exercise_style (
    exercise_style_id INTEGER     NOT NULL,
    code              VARCHAR(25) NOT NULL,
    description       VARCHAR(64) NOT NULL,
    CONSTRAINT pk_exercise_style PRIMARY KEY (exercise_style_id)
);
CREATE UNIQUE INDEX exercise_style_code ON exercise_style (code);
ALTER TABLE exercise_style OWNER TO sofie;

CREATE TABLE premium_style (
    premium_style_id INTEGER     NOT NULL,
    code             VARCHAR(25) NOT NULL,
    description      VARCHAR(64) NOT NULL,
    CONSTRAINT pk_premium_style PRIMARY KEY (premium_style_id)
);
CREATE UNIQUE INDEX premium_style_code ON premium_style (code);
ALTER TABLE premium_style OWNER TO sofie;

CREATE TABLE option_type (
    option_type_id INTEGER     NOT NULL,
    code           VARCHAR(25) NOT NULL,
    description    VARCHAR(64) NOT NULL,
    CONSTRAINT pk_option_type PRIMARY KEY (option_type_id)
);
CREATE UNIQUE INDEX option_type_code ON option_type (code);
ALTER TABLE option_type OWNER TO sofie;

INSERT INTO exercise_style VALUES (1, 'EUROPEAN', 'Can only be exercised at the expiration date');
INSERT INTO exercise_style VALUES (2, 'AMERICAN', 'Can be exercised at any time up to the expiration date');
INSERT INTO exercise_style VALUES (3, 'BERMUDAN', 'Can be exercised on specific dates before expiration');
INSERT INTO premium_style VALUES (1, 'EQUITY_STYLE', 'Premium paid upfront');
INSERT INTO premium_style VALUES (2, 'FUTURES_STYLE', 'Premium margined daily like a future');
INSERT INTO option_type VALUES (1, 'CALL', 'Call Option');
INSERT INTO option_type VALUES (2, 'PUT', 'Put Option');

-- asset class delle opzioni su FX future (le altre famiglie quando servono)
INSERT INTO asset_class (id_asset_class, code, description, super_class)
VALUES (nextval('asset_class_s'), 'FFO', 'Forex Future Option', (SELECT id_super_class FROM super_class WHERE code = 'DER'));

-- ----------------------------------------------------------------------------
-- 2. Testata
-- ----------------------------------------------------------------------------
CREATE SEQUENCE option_product_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE option_product_s OWNER TO sofie;

CREATE TABLE option_product (
    id_option_product       INTEGER       NOT NULL,
    code                    VARCHAR(25)   NOT NULL,      -- EUU
    description             VARCHAR(255)  NOT NULL DEFAULT '',
    exchange_contract_code  VARCHAR(25)   NOT NULL DEFAULT '',
    exercise_style          INTEGER       NOT NULL,      -- ExerciseStyle
    premium_style           INTEGER       NOT NULL,      -- PremiumStyle
    settlement_type         INTEGER       NOT NULL,      -- SettlementType
    contract_value          NUMERIC(15,5) NOT NULL,      -- quantita' di sottostante per contratto
    tick_size               NUMERIC(15,5) NOT NULL,
    initial_margin          NUMERIC(15,5),
    maintenance_margin      NUMERIC(15,5),
    expiry_time             TIME,                        -- orario di scadenza (10:00 per EUU)
    expiry_time_zone        VARCHAR(64),                 -- fuso IANA (America/New_York)
    future_product          INTEGER,                     -- sottostante derivato
    underlying              INTEGER,                     -- sottostante secco fisso
    -- valori copiati in master_data quando si crea una serie
    currency                INTEGER       NOT NULL,
    asset_class             INTEGER       NOT NULL,
    business_days           INTEGER       NOT NULL DEFAULT 2,
    multiplier              NUMERIC(15,5) NOT NULL DEFAULT 1,
    CONSTRAINT pk_option_product PRIMARY KEY (id_option_product),
    CONSTRAINT uq_option_product_code UNIQUE (code),
    CONSTRAINT ck_option_product_underlying CHECK (future_product IS NULL OR underlying IS NULL),
    CONSTRAINT fk_option_product_exercise_style FOREIGN KEY (exercise_style) REFERENCES exercise_style (exercise_style_id),
    CONSTRAINT fk_option_product_premium_style FOREIGN KEY (premium_style) REFERENCES premium_style (premium_style_id),
    CONSTRAINT fk_option_product_settlement_type FOREIGN KEY (settlement_type) REFERENCES settlement_type (id_settlement_type),
    CONSTRAINT fk_option_product_future_product FOREIGN KEY (future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_option_product_underlying FOREIGN KEY (underlying) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_option_product_currency FOREIGN KEY (currency) REFERENCES currency (id_currency),
    CONSTRAINT fk_option_product_asset_class FOREIGN KEY (asset_class) REFERENCES asset_class (id_asset_class)
);
ALTER TABLE option_product OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- 3. Serie negoziabile
-- ----------------------------------------------------------------------------
CREATE TABLE option_master_data (
    id_master_data  INTEGER       NOT NULL,
    option_product  INTEGER       NOT NULL,
    underlying      INTEGER       NOT NULL,              -- scadenza del future o sottostante secco
    option_type     INTEGER       NOT NULL,              -- OptionType
    strike          NUMERIC(18,8) NOT NULL,
    isin            VARCHAR(25),
    CONSTRAINT pk_option_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_option_master_data_parent FOREIGN KEY (id_master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_option_master_data_product FOREIGN KEY (option_product) REFERENCES option_product (id_option_product),
    CONSTRAINT fk_option_master_data_underlying FOREIGN KEY (underlying) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_option_master_data_option_type FOREIGN KEY (option_type) REFERENCES option_type (option_type_id)
);
CREATE UNIQUE INDEX idx_option_master_data_isin ON option_master_data (isin);
CREATE INDEX idx_option_master_data_product ON option_master_data (option_product);
CREATE INDEX idx_option_master_data_underlying ON option_master_data (underlying);
ALTER TABLE option_master_data OWNER TO sofie;

COMMIT;
