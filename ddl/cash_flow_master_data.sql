-- ============================================================================
-- Tabella intermedia per gli strumenti con flussi
--
--   master_data                 dati comuni: codice, descrizione, valuta, asset class,
--     │                         date di emissione/scadenza, business_days, multiplier
--     ├── cash_flow_master_data convenzioni dei flussi, prezzi di emissione e rimborso,
--     │     │                   forma; piano dei flussi (cash_flow_item, cash_flow_reset)
--     │     ├── security_master_data -> flt_security_master_data
--     │     └── loan_master_data
--     ├── future_master_data
--     └── forex_master_data
--
-- Le colonne dei flussi escono da master_data: per future e cambi erano solo
-- valori fissi imposti dai vincoli NOT NULL.
-- Stato di partenza: 23/10/2026 (67 titoli: XRB 55, BLL 10, FRB 1, XRN 1; mutui 0).
-- Tutto in una transazione.
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Tabella intermedia
-- ----------------------------------------------------------------------------
CREATE TABLE cash_flow_master_data (
    id_master_data         INTEGER        NOT NULL,
    type_of_interest       INTEGER        NOT NULL,
    form                   INTEGER        NOT NULL,
    daycount               INTEGER        NOT NULL,
    accrual_daycount       INTEGER        NOT NULL,
    frequency              INTEGER        NOT NULL,
    roll_convention        INTEGER        NOT NULL DEFAULT 0,
    accrual_schedule_type  INTEGER        NOT NULL DEFAULT 0,
    interest_rate          NUMERIC(23,10) NOT NULL,
    issue_price            NUMERIC(15,5)  NOT NULL,
    redempion_price        NUMERIC(15,5)  NOT NULL,
    amortization_schedule  INTEGER        NOT NULL,
    compounding            INTEGER        NOT NULL DEFAULT 2,
    CONSTRAINT pk_cash_flow_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_cash_flow_master_data_parent FOREIGN KEY (id_master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_cfmd_type_of_interest FOREIGN KEY (type_of_interest) REFERENCES type_of_interest (id_type_of_interest),
    CONSTRAINT fk_cfmd_form FOREIGN KEY (form) REFERENCES form (id_form),
    CONSTRAINT fk_cfmd_daycount FOREIGN KEY (daycount) REFERENCES daycount (id_daycount),
    CONSTRAINT fk_cfmd_accrual_daycount FOREIGN KEY (accrual_daycount) REFERENCES daycount (id_daycount),
    CONSTRAINT fk_cfmd_frequency FOREIGN KEY (frequency) REFERENCES frequency (id_frequency),
    CONSTRAINT fk_cfmd_roll_convention FOREIGN KEY (roll_convention) REFERENCES roll_convention (id_roll_convention),
    CONSTRAINT fk_cfmd_accrual_schedule_type FOREIGN KEY (accrual_schedule_type) REFERENCES accrual_schedule_type (id_accrual_schedule_type),
    CONSTRAINT fk_cfmd_amortization_schedule FOREIGN KEY (amortization_schedule) REFERENCES amortization_schedule (id_amortization_schedule),
    CONSTRAINT fk_cfmd_compounding FOREIGN KEY (compounding) REFERENCES compounding (id_compounding)
);
ALTER TABLE cash_flow_master_data OWNER TO sofie;


-- ----------------------------------------------------------------------------
-- 2. Dati: titoli (compresi i floating) e mutui
-- ----------------------------------------------------------------------------
INSERT INTO cash_flow_master_data (id_master_data, type_of_interest, form, daycount, accrual_daycount, frequency,
                                   roll_convention, accrual_schedule_type, interest_rate, issue_price, redempion_price,
                                   amortization_schedule, compounding)
SELECT md.id_master_data, md.type_of_interest, md.form, md.daycount, md.accrual_daycount, md.frequency,
       md.roll_convention, md.accrual_schedule_type, md.interest_rate, md.issue_price, md.redempion_price,
       md.amortization_schedule, md.compounding
FROM master_data md
WHERE md.id_master_data IN (SELECT id_master_data FROM security_master_data
                            UNION SELECT id_master_data FROM loan_master_data);


-- ----------------------------------------------------------------------------
-- 3. Gerarchia e flussi puntano alla tabella intermedia
-- ----------------------------------------------------------------------------
-- Righe di dettaglio senza anagrafica in master_data (23/10/2026: 5 titoli,
-- id 1, 3, 10, 12, 15, senza flussi ne' posizioni): inutilizzabili, si eliminano
-- se nessuno le referenzia, altrimenti la FK seguente fallisce e si annulla tutto.
DELETE FROM security_master_data s
WHERE NOT EXISTS (SELECT 1 FROM master_data m WHERE m.id_master_data = s.id_master_data)
  AND NOT EXISTS (SELECT 1 FROM flt_security_master_data f WHERE f.id_master_data = s.id_master_data)
  AND NOT EXISTS (SELECT 1 FROM cash_flow_item c WHERE c.master_data = s.id_master_data)
  AND NOT EXISTS (SELECT 1 FROM cash_flow_reset r WHERE r.master_data = s.id_master_data);

ALTER TABLE security_master_data ADD CONSTRAINT fk_security_master_data_parent
    FOREIGN KEY (id_master_data) REFERENCES cash_flow_master_data (id_master_data);
ALTER TABLE loan_master_data ADD CONSTRAINT fk_loan_master_data_parent
    FOREIGN KEY (id_master_data) REFERENCES cash_flow_master_data (id_master_data);

ALTER TABLE cash_flow_item DROP CONSTRAINT fk_master_data;
ALTER TABLE cash_flow_item ADD CONSTRAINT fk_cash_flow_item_master_data
    FOREIGN KEY (master_data) REFERENCES cash_flow_master_data (id_master_data);
ALTER TABLE cash_flow_reset DROP CONSTRAINT fk_master_data;
ALTER TABLE cash_flow_reset ADD CONSTRAINT fk_cash_flow_reset_master_data
    FOREIGN KEY (master_data) REFERENCES cash_flow_master_data (id_master_data);


-- ----------------------------------------------------------------------------
-- 4. Colonne dei flussi fuori da master_data (le FK verso i lookup cadono con le colonne)
-- ----------------------------------------------------------------------------
ALTER TABLE master_data
    DROP COLUMN type_of_interest,
    DROP COLUMN form,
    DROP COLUMN daycount,
    DROP COLUMN accrual_daycount,
    DROP COLUMN frequency,
    DROP COLUMN roll_convention,
    DROP COLUMN accrual_schedule_type,
    DROP COLUMN interest_rate,
    DROP COLUMN issue_price,
    DROP COLUMN redempion_price,
    DROP COLUMN amortization_schedule,
    DROP COLUMN compounding;


-- ----------------------------------------------------------------------------
-- 5. Controllo
-- ----------------------------------------------------------------------------
SELECT ac.code AS asset_class, count(*) AS strumenti, count(cf.id_master_data) AS con_flussi
FROM master_data md
JOIN asset_class ac ON ac.id_asset_class = md.asset_class
LEFT JOIN cash_flow_master_data cf ON cf.id_master_data = md.id_master_data
GROUP BY ac.code
ORDER BY ac.code;

COMMIT;
