-- ============================================================================
-- Stato della chiusura di giornata
--
-- eod_run       un lancio della chiusura. Una chiusura fallita si rilancia con
--               una riga nuova, cosi' resta lo storico dei tentativi.
-- eod_step_run  esecuzione di uno step in un lancio, con i contatori di
--               EodTaskResult. Al rilancio gli step riusciti per la stessa
--               data (OK, WARNING) non si rieseguono.
--
-- Vincoli: al massimo un lancio in corso; al massimo un lancio riuscito per data.
-- Enum Java: EodStatus, EodStep. Riportata in sofie.sql e sofie_startup.sql.
-- ============================================================================

BEGIN;

CREATE TABLE eod_status (
    eod_status_id  INTEGER NOT NULL,
    code           VARCHAR(25) NOT NULL,
    description    VARCHAR(64) NOT NULL,
    CONSTRAINT pk_eod_status PRIMARY KEY (eod_status_id),
    CONSTRAINT uq_eod_status_code UNIQUE (code)
);
ALTER TABLE eod_status OWNER TO sofie;

INSERT INTO eod_status VALUES (1, 'RUNNING', 'Running');
INSERT INTO eod_status VALUES (2, 'OK', 'Completed');
INSERT INTO eod_status VALUES (3, 'WARNING', 'Completed with warnings');
INSERT INTO eod_status VALUES (4, 'FAILED', 'Failed');

CREATE TABLE eod_step (
    eod_step_id  INTEGER NOT NULL,
    code         VARCHAR(25) NOT NULL,
    description  VARCHAR(64) NOT NULL,
    CONSTRAINT pk_eod_step PRIMARY KEY (eod_step_id),
    CONSTRAINT uq_eod_step_code UNIQUE (code)
);
ALTER TABLE eod_step OWNER TO sofie;

INSERT INTO eod_step VALUES (1, 'OPEN_CLOSING', 'Set the next business date');
INSERT INTO eod_step VALUES (2, 'COUPON', 'Coupon events');
INSERT INTO eod_step VALUES (3, 'SETTLEMENT', 'Settlement events');
INSERT INTO eod_step VALUES (4, 'WAIT_SETTLEMENT_POSTED', 'Wait for settlement accounting');
INSERT INTO eod_step VALUES (5, 'FX_ROLLING', 'FX spot rolling');
INSERT INTO eod_step VALUES (6, 'ACCRUAL', 'Accrual events');
INSERT INTO eod_step VALUES (7, 'MTM', 'End of day mark to market');
INSERT INTO eod_step VALUES (8, 'WAIT_EOD_POSTED', 'Wait for end of day accounting');
INSERT INTO eod_step VALUES (9, 'CONTROLS', 'Closing controls');
INSERT INTO eod_step VALUES (10, 'DATE_ROLL', 'Roll the official date');

CREATE TABLE eod_run (
    eod_run_id          INTEGER NOT NULL,
    business_date       DATE NOT NULL,
    next_business_date  DATE NOT NULL,
    status              INTEGER NOT NULL,
    started_at          TIMESTAMP NOT NULL,
    ended_at            TIMESTAMP,
    message             VARCHAR(2000),
    CONSTRAINT pk_eod_run PRIMARY KEY (eod_run_id),
    CONSTRAINT fk_eod_run_status FOREIGN KEY (status) REFERENCES eod_status (eod_status_id)
);
ALTER TABLE eod_run OWNER TO sofie;

CREATE UNIQUE INDEX uq_eod_run_running ON eod_run ((status)) WHERE status = 1;
CREATE UNIQUE INDEX uq_eod_run_completed ON eod_run (business_date) WHERE status IN (2, 3);

CREATE SEQUENCE eod_run_s START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE eod_run_s OWNER TO sofie;

CREATE TABLE eod_step_run (
    eod_step_run_id  INTEGER NOT NULL,
    eod_run          INTEGER NOT NULL,
    step             INTEGER NOT NULL,
    status           INTEGER NOT NULL,
    processed        INTEGER NOT NULL DEFAULT 0,
    created          INTEGER NOT NULL DEFAULT 0,
    skipped          INTEGER NOT NULL DEFAULT 0,
    errors           INTEGER NOT NULL DEFAULT 0,
    message          VARCHAR(2000),
    started_at       TIMESTAMP NOT NULL,
    ended_at         TIMESTAMP,
    CONSTRAINT pk_eod_step_run PRIMARY KEY (eod_step_run_id),
    CONSTRAINT fk_eod_step_run_run FOREIGN KEY (eod_run) REFERENCES eod_run (eod_run_id),
    CONSTRAINT fk_eod_step_run_step FOREIGN KEY (step) REFERENCES eod_step (eod_step_id),
    CONSTRAINT fk_eod_step_run_status FOREIGN KEY (status) REFERENCES eod_status (eod_status_id),
    CONSTRAINT uq_eod_step_run UNIQUE (eod_run, step)
);
ALTER TABLE eod_step_run OWNER TO sofie;

CREATE SEQUENCE eod_step_run_s START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
ALTER SEQUENCE eod_step_run_s OWNER TO sofie;

COMMIT;
