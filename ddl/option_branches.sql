-- ============================================================================
-- Opzioni: due rami separati (dopo option_product.sql)
--
--   option_product                 dati comuni (esercizio, premio, regolamento,
--    │                             controvalore, orario e fuso di scadenza/cut)
--    ├─ future_option_product      future_product NOT NULL (sottostante derivato)
--    └─ spot_option_product        underlying (strumento secco fisso; facoltativo
--                                  per una testata OTC generica)
--
--   master_data -> option_master_data           tipo, strike, isin
--    ├─ future_option_master_data  underlying -> future_master_data NOT NULL
--    └─ spot_option_master_data    underlying -> master_data NOT NULL, delivery_date
--
-- Sostituisce le colonne option_product.future_product/underlying (con il CHECK
-- "al piu' uno dei due") e option_master_data.underlying. I dati esistenti
-- passano nel ramo indicato dalla testata. Tutto in una transazione.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Testate
-- ----------------------------------------------------------------------------
CREATE TABLE future_option_product (
    id_option_product  INTEGER NOT NULL,
    future_product     INTEGER NOT NULL,                 -- testata del future sottostante
    CONSTRAINT pk_future_option_product PRIMARY KEY (id_option_product),
    CONSTRAINT fk_future_option_product_parent
        FOREIGN KEY (id_option_product) REFERENCES option_product (id_option_product),
    CONSTRAINT fk_future_option_product_future_product
        FOREIGN KEY (future_product) REFERENCES future_product (id_future_product)
);
ALTER TABLE future_option_product OWNER TO sofie;

CREATE TABLE spot_option_product (
    id_option_product  INTEGER NOT NULL,
    underlying         INTEGER,                          -- sottostante secco fisso
    CONSTRAINT pk_spot_option_product PRIMARY KEY (id_option_product),
    CONSTRAINT fk_spot_option_product_parent
        FOREIGN KEY (id_option_product) REFERENCES option_product (id_option_product),
    CONSTRAINT fk_spot_option_product_underlying
        FOREIGN KEY (underlying) REFERENCES master_data (id_master_data)
);
ALTER TABLE spot_option_product OWNER TO sofie;

INSERT INTO future_option_product (id_option_product, future_product)
SELECT id_option_product, future_product FROM option_product WHERE future_product IS NOT NULL;

INSERT INTO spot_option_product (id_option_product, underlying)
SELECT id_option_product, underlying FROM option_product WHERE future_product IS NULL;

-- ----------------------------------------------------------------------------
-- 2. Serie
-- ----------------------------------------------------------------------------
CREATE TABLE future_option_master_data (
    id_master_data  INTEGER NOT NULL,
    underlying      INTEGER NOT NULL,                    -- scadenza del future
    CONSTRAINT pk_future_option_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_future_option_master_data_parent
        FOREIGN KEY (id_master_data) REFERENCES option_master_data (id_master_data),
    CONSTRAINT fk_future_option_master_data_underlying
        FOREIGN KEY (underlying) REFERENCES future_master_data (id_master_data)
);
CREATE INDEX idx_future_option_master_data_underlying ON future_option_master_data (underlying);
ALTER TABLE future_option_master_data OWNER TO sofie;

CREATE TABLE spot_option_master_data (
    id_master_data  INTEGER NOT NULL,
    underlying      INTEGER NOT NULL,                    -- strumento secco
    delivery_date   DATE,                                -- consegna (scadenza + spot lag)
    CONSTRAINT pk_spot_option_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_spot_option_master_data_parent
        FOREIGN KEY (id_master_data) REFERENCES option_master_data (id_master_data),
    CONSTRAINT fk_spot_option_master_data_underlying
        FOREIGN KEY (underlying) REFERENCES master_data (id_master_data)
);
CREATE INDEX idx_spot_option_master_data_underlying ON spot_option_master_data (underlying);
ALTER TABLE spot_option_master_data OWNER TO sofie;

INSERT INTO future_option_master_data (id_master_data, underlying)
SELECT o.id_master_data, o.underlying
FROM option_master_data o
JOIN future_option_product p ON p.id_option_product = o.option_product;

INSERT INTO spot_option_master_data (id_master_data, underlying)
SELECT o.id_master_data, o.underlying
FROM option_master_data o
JOIN spot_option_product p ON p.id_option_product = o.option_product;

-- ----------------------------------------------------------------------------
-- 3. Colonne sostituite dai rami
-- ----------------------------------------------------------------------------
DROP INDEX idx_option_master_data_underlying;
ALTER TABLE option_master_data DROP CONSTRAINT fk_option_master_data_underlying;
ALTER TABLE option_master_data DROP COLUMN underlying;

ALTER TABLE option_product DROP CONSTRAINT ck_option_product_underlying;
ALTER TABLE option_product DROP CONSTRAINT fk_option_product_future_product;
ALTER TABLE option_product DROP CONSTRAINT fk_option_product_underlying;
ALTER TABLE option_product DROP COLUMN future_product;
ALTER TABLE option_product DROP COLUMN underlying;

COMMIT;
