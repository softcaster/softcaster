-- ============================================================================
-- Accumulator: periodo garantito in periodi di regolamento e tipo di garanzia
--
--   guaranteed_fixings -> guaranteed_periods  primi N periodi di regolamento
--                                             (es. prime 3 settimane)
--   guarantee_type                            NONE        nessuna garanzia
--                                             LEVERAGED   KO non attivo, leva
--                                                         normale (standard)
--                                             UNLEVERAGED KO non attivo, ogni
--                                                         fixing matura la
--                                                         quantita' base
--
-- Nessuna garanzia <=> 0 periodi. Tabella vuota alla data della migrazione:
-- nessun dato da convertire.
-- ============================================================================

BEGIN;

CREATE TABLE guarantee_type (
    guarantee_type_id  INTEGER NOT NULL,
    code               VARCHAR(25) NOT NULL,
    description        VARCHAR(64) NOT NULL,
    CONSTRAINT pk_guarantee_type PRIMARY KEY (guarantee_type_id),
    CONSTRAINT uq_guarantee_type_code UNIQUE (code)
);
ALTER TABLE guarantee_type OWNER TO sofie;

INSERT INTO guarantee_type VALUES (1, 'NONE', 'No guarantee');
INSERT INTO guarantee_type VALUES (2, 'LEVERAGED', 'KO not active, leverage applies');
INSERT INTO guarantee_type VALUES (3, 'UNLEVERAGED', 'KO not active, base quantity on every fixing');

ALTER TABLE accumulator_master_data DROP CONSTRAINT ck_accumulator_master_data_quantities;
ALTER TABLE accumulator_master_data RENAME COLUMN guaranteed_fixings TO guaranteed_periods;
ALTER TABLE accumulator_master_data ADD COLUMN guarantee_type INTEGER NOT NULL DEFAULT 1;
ALTER TABLE accumulator_master_data ADD CONSTRAINT fk_accumulator_master_data_guarantee_type
    FOREIGN KEY (guarantee_type) REFERENCES guarantee_type (guarantee_type_id);
ALTER TABLE accumulator_master_data ADD CONSTRAINT ck_accumulator_master_data_quantities
    CHECK (strike > 0 AND ko_barrier > 0 AND leverage >= 1 AND daily_quantity > 0 AND guaranteed_periods >= 0);
-- nessuna garanzia se e solo se zero periodi garantiti
ALTER TABLE accumulator_master_data ADD CONSTRAINT ck_accumulator_master_data_guarantee
    CHECK ((guarantee_type = 1) = (guaranteed_periods = 0));

COMMIT;
