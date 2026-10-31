-- ============================================================================
-- Mutui OTC: anagrafica completa
--
--   master_data -> cash_flow_master_data -> loan_master_data      mutuo (XRM)
--                                            ├─ loan_fee           spese (1-N)
--                                            └─ flt_loan_master_data  variabile (FRM)
--
-- loan_master_data
--   - via description: la descrizione e' quella di master_data (la colonna
--     duplicata la nascondeva);
--   - principal_amount: capitale finanziato; first_payment_date: prima rata
--     (vuota = data di erogazione + un periodo); pre_amortization_periods:
--     rate iniziali di soli interessi;
--   - le spese passano in loan_fee: ciascuna come importo o percentuale, una
--     tantum o per rata. Restano le condizioni di servizio, fuori dal prezzo:
--     default_interest (tasso di mora, punti percentuali), late_payment_fee
--     (penale per rata, importo).
--
-- loan_fee: value in valuta del mutuo (AMOUNT) o in punti percentuali
-- (PERCENT, 0.25 = 0,25%, come interest_rate): UPFRONT -> % del capitale,
-- PER_INSTALLMENT -> % del debito residuo a inizio periodo, per rata.
--
-- flt_loan_master_data: condizioni del variabile come flt_security_master_data
-- (stessa classe Java @Embeddable FloatingRateTerms), piu' il cap, aggiunto
-- anche ai titoli.
--
-- Tabelle dei mutui vuote alla data della migrazione: nessun dato da migrare.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Decodifiche delle spese
-- ----------------------------------------------------------------------------
CREATE TABLE loan_fee_type (
    loan_fee_type_id  INTEGER NOT NULL,
    code              VARCHAR(25) NOT NULL,
    description       VARCHAR(64) NOT NULL,
    CONSTRAINT pk_loan_fee_type PRIMARY KEY (loan_fee_type_id),
    CONSTRAINT uq_loan_fee_type_code UNIQUE (code)
);
ALTER TABLE loan_fee_type OWNER TO sofie;

INSERT INTO loan_fee_type VALUES (1, 'PROCESSING', 'Processing fee');
INSERT INTO loan_fee_type VALUES (2, 'UNDERWRITING', 'Underwriting / appraisal fee');
INSERT INTO loan_fee_type VALUES (3, 'TAX', 'Tax charges');
INSERT INTO loan_fee_type VALUES (4, 'INCIDENTAL', 'Incidental expenses');
INSERT INTO loan_fee_type VALUES (5, 'MANAGEMENT', 'Management / collection fee');
INSERT INTO loan_fee_type VALUES (6, 'INSURANCE', 'Insurance premium');

CREATE TABLE fee_basis (
    fee_basis_id  INTEGER NOT NULL,
    code          VARCHAR(25) NOT NULL,
    description   VARCHAR(64) NOT NULL,
    CONSTRAINT pk_fee_basis PRIMARY KEY (fee_basis_id),
    CONSTRAINT uq_fee_basis_code UNIQUE (code)
);
ALTER TABLE fee_basis OWNER TO sofie;

INSERT INTO fee_basis VALUES (1, 'AMOUNT', 'Amount in loan currency');
INSERT INTO fee_basis VALUES (2, 'PERCENT', 'Percentage of principal or outstanding');

CREATE TABLE fee_timing (
    fee_timing_id  INTEGER NOT NULL,
    code           VARCHAR(25) NOT NULL,
    description    VARCHAR(64) NOT NULL,
    CONSTRAINT pk_fee_timing PRIMARY KEY (fee_timing_id),
    CONSTRAINT uq_fee_timing_code UNIQUE (code)
);
ALTER TABLE fee_timing OWNER TO sofie;

INSERT INTO fee_timing VALUES (1, 'UPFRONT', 'Once, at disbursement');
INSERT INTO fee_timing VALUES (2, 'PER_INSTALLMENT', 'At every installment');

-- ----------------------------------------------------------------------------
-- 2. loan_master_data
-- ----------------------------------------------------------------------------
ALTER TABLE loan_master_data
    DROP COLUMN description,
    DROP COLUMN processing_fees,
    DROP COLUMN management_fees,
    DROP COLUMN incidental_expenses,
    DROP COLUMN underwriting_fee,
    DROP COLUMN insurance_premium,
    DROP COLUMN tax_charges,
    ADD COLUMN principal_amount NUMERIC(23,5) NOT NULL,
    ADD COLUMN first_payment_date DATE,
    ADD COLUMN pre_amortization_periods INTEGER NOT NULL DEFAULT 0,
    ADD CONSTRAINT ck_loan_master_data_values
        CHECK (principal_amount > 0 AND pre_amortization_periods >= 0 AND default_interest >= 0 AND late_payment_fee >= 0);

-- ----------------------------------------------------------------------------
-- 3. Spese
-- ----------------------------------------------------------------------------
CREATE TABLE loan_fee (
    id_loan_fee   INTEGER NOT NULL,
    master_data   INTEGER NOT NULL,
    fee_type      INTEGER NOT NULL,
    fee_basis     INTEGER NOT NULL,
    fee_timing    INTEGER NOT NULL,
    value         NUMERIC(23,10) NOT NULL,         -- importo o punti percentuali
    CONSTRAINT pk_loan_fee PRIMARY KEY (id_loan_fee),
    CONSTRAINT fk_loan_fee_master_data
        FOREIGN KEY (master_data) REFERENCES loan_master_data (id_master_data) ON DELETE CASCADE,
    CONSTRAINT fk_loan_fee_type FOREIGN KEY (fee_type) REFERENCES loan_fee_type (loan_fee_type_id),
    CONSTRAINT fk_loan_fee_basis FOREIGN KEY (fee_basis) REFERENCES fee_basis (fee_basis_id),
    CONSTRAINT fk_loan_fee_timing FOREIGN KEY (fee_timing) REFERENCES fee_timing (fee_timing_id),
    CONSTRAINT uq_loan_fee UNIQUE (master_data, fee_type),
    CONSTRAINT ck_loan_fee_value CHECK (value >= 0)
);
ALTER TABLE loan_fee OWNER TO sofie;

CREATE SEQUENCE IF NOT EXISTS loan_fee_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE loan_fee_s OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- 4. Variabile: cap anche sui titoli, nuova foglia per i mutui
-- ----------------------------------------------------------------------------
ALTER TABLE flt_security_master_data ADD COLUMN cap NUMERIC(18,10);

CREATE TABLE flt_loan_master_data (
    id_master_data    INTEGER NOT NULL,
    spread            NUMERIC(23,10) NOT NULL,       -- decimale, come per i titoli (0.0075)
    projection_index  INTEGER,
    coupon_pm         INTEGER NOT NULL,
    fixing_lag_days   INTEGER NOT NULL DEFAULT 2,
    fixing_day_type   INTEGER NOT NULL DEFAULT 2,
    floor             NUMERIC(18,10),
    cap               NUMERIC(18,10),
    index_months      INTEGER NOT NULL DEFAULT 6,
    projection_curve  INTEGER,
    CONSTRAINT pk_flt_loan_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_flt_loan_master_data_parent
        FOREIGN KEY (id_master_data) REFERENCES loan_master_data (id_master_data),
    CONSTRAINT fk_flt_loan_projection_index
        FOREIGN KEY (projection_index) REFERENCES ref_rate_index (ref_rate_index_id),
    CONSTRAINT fk_flt_loan_coupon_pm
        FOREIGN KEY (coupon_pm) REFERENCES coupon_pm (coupon_pm_id),
    CONSTRAINT fk_flt_loan_fixing_day_type
        FOREIGN KEY (fixing_day_type) REFERENCES fixing_day_type (fixing_day_type_id),
    CONSTRAINT ck_flt_loan_floor_cap CHECK (floor IS NULL OR cap IS NULL OR floor <= cap)
);
ALTER TABLE flt_loan_master_data OWNER TO sofie;

COMMIT;
