---------------------------------------------------------------------
-- Tabelle modulo contabile
---------------------------------------------------------------------
-- Tabella Anagrafica dei Tipi di Rendiconto Finanziario
CREATE TABLE financial_statement_types (
    statement_type_id INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL -- 'BALANCE_SHEET', 'INCOME_STATEMENT', 'OFF_BALANCE_SHEET'
    , description VARCHAR(50) NOT NULL DEFAULT ''
    , PRIMARY KEY (statement_type_id)
);
ALTER TABLE financial_statement_types OWNER TO sofie;

-- Tabella Anagrafica della Natura del Conto
CREATE TABLE account_natures (
    nature_id INTEGER NOT NULL 
    , code VARCHAR(25) NOT NULL -- 'ASSET', 'LIABILITY', 'INCOME', 'EXPENSE', 'MEMORANDUM'
    , description VARCHAR(50) NOT NULL DEFAULT ''
    , PRIMARY KEY (nature_id)
);
ALTER TABLE account_natures OWNER TO sofie;

-- Tabella Anagrafica del Segno Algebrico Naturale (Sezione Contabile)
CREATE TABLE normal_balances (
    balance_id INTEGER NOT NULL 
    , code VARCHAR(25) NOT NULL  -- 'DEBIT', 'CREDIT'
    , description VARCHAR(50) NOT NULL DEFAULT ''
    , PRIMARY KEY (balance_id)
);
ALTER TABLE normal_balances OWNER TO sofie;

-- Tabella Anagrafica Chart Of Accounts
CREATE TABLE gl_accounts (
    account_id INTEGER NOT NULL 
    , parent INTEGER NULL
    , code VARCHAR(50) NOT NULL UNIQUE
    , description VARCHAR(150) NOT NULL DEFAULT ''
    , is_postable BOOLEAN NOT NULL DEFAULT FALSE
    , currency INTEGER NULL
    , statement_type INTEGER NOT NULL
    , nature INTEGER NOT NULL
    , balance INTEGER NOT NULL
    , created_at TIMESTAMP NOT NULL DEFAULT now()
    , updated_at TIMESTAMP NOT NULL DEFAULT now()
    , PRIMARY KEY (account_id)
    , CONSTRAINT fk_parent FOREIGN KEY (parent)
              REFERENCES gl_accounts(account_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_statement_type FOREIGN KEY (statement_type)
              REFERENCES financial_statement_types(statement_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_nature FOREIGN KEY (nature)
              REFERENCES account_natures(nature_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_balance FOREIGN KEY (balance)
              REFERENCES normal_balances(balance_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_currency FOREIGN KEY (currency)
              REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_account_code ON gl_accounts(code);
ALTER TABLE gl_accounts OWNER TO sofie;

CREATE SEQUENCE gl_accounts_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE gl_accounts_s OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- accounting_event_types
-- ----------------------------------------------------------------------------
CREATE TABLE accounting_event_types (
   event_type_id INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL -- TRADE_EXECUTED TRADE_CANCEL MTM COUPON ACCRUAL SETTLEMENT MATURITY FX_REVALUATION
    , description VARCHAR(225) NOT NULL DEFAULT '' 
    , PRIMARY KEY (event_type_id)
);
CREATE UNIQUE INDEX idx_event_types_code ON accounting_event_types(code);
ALTER TABLE accounting_event_types OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- accounting_event_status
-- ----------------------------------------------------------------------------
CREATE TABLE accounting_event_status (
   event_status_id INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL -- NEW IN_PROGRESS PROCESSED FAILED
    , description VARCHAR(225) NOT NULL DEFAULT '' 
    , PRIMARY KEY (event_status_id)
);
CREATE UNIQUE INDEX idx_event_status_code ON accounting_event_status(code);
ALTER TABLE accounting_event_status OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- source_event_types
-- ----------------------------------------------------------------------------
CREATE TABLE event_source_types (
   source_type_id INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL  -- TRADE INSTRUMENT POSITION_DETAIL(caso MTM)->  id_position_detail
    , description VARCHAR(225) NOT NULL DEFAULT '' 
    , PRIMARY KEY (source_type_id)
);
CREATE UNIQUE INDEX idx_source_code ON event_source_types(code);
ALTER TABLE event_source_types OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- accounting_events - Tabella evento contabile
-- ----------------------------------------------------------------------------
CREATE TABLE accounting_events (
    event_id INTEGER NOT NULL 
    , event_type INTEGER NOT NULL-- TRADE_EXECUTION TRADE_CANCEL MTM COUPON ACCRUAL SETTLEMENT MATURITY FX_REVALUATION
    , event_status INTEGER NOT NULL -- NEW IN_PROGRESS PROCESSED FAILED
    , source_type INTEGER NOT NULL -- TRADE INSTRUMENT POSITION_DETAIL(caso MTM)->  id_position_detail
    , source_id INTEGER NOT NULL -- 12345 (txn)
    , event_key VARCHAR(100) NOT NULL -- es 'TRADE_EXECUTION:txn12345' garantisce idemponenza
    , generated_by INTEGER NOT NULL -- POSITION_ENGINE/LAYER (FinTxnPollingJob) / SCHEDULER_ENGINE, VALUATION_ENGINE
    , generated_ref VARCHAR (100) -- batch_20260530_01
    , created_at TIMESTAMP NOT NULL DEFAULT now()
    , processed_at TIMESTAMP NULL -- null fino a quando event nonè processato
    , PRIMARY KEY (event_id)
    , CONSTRAINT fk_event_type FOREIGN KEY (event_type)
              REFERENCES accounting_event_types(event_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_event_status FOREIGN KEY (event_status)
              REFERENCES accounting_event_status(event_status_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_source_type FOREIGN KEY (source_type)
              REFERENCES event_source_types(source_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);
-- Per garantire idemponenza (1 event_key per record)
CREATE UNIQUE INDEX idx_event_key ON accounting_events(event_key);
-- Per query tipo: SELECT * FROM accounting_events WHERE event_status = NEW ORDER BY created_at
CREATE INDEX idx_event_status_created ON accounting_events(event_status, created_at);
-- Per query tipo: SELECT * FROM accounting_events WHERE event_status = NEW AND event_type = ?
CREATE INDEX idx_event_status_type ON accounting_events(event_status, event_type);
-- Per tracciamento source: SELECT * FROM accounting_events WHERE source_type = ? AND source_id = ?; 
CREATE INDEX idx_source ON accounting_events(source_type, source_id);
ALTER TABLE accounting_events OWNER TO sofie;

CREATE SEQUENCE accounting_events_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE accounting_events_s OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- journal_entry_types 
-- ----------------------------------------------------------------------------
CREATE TABLE journal_entry_types (
    entry_type_id INTEGER NOT NULL,
    code VARCHAR(30) NOT NULL,
    description VARCHAR(100),

    PRIMARY KEY (entry_type_id)
);
ALTER TABLE journal_entry_types OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- journal_entries 
-- ----------------------------------------------------------------------------
CREATE TABLE journal_entries (
    journal_entry_id INTEGER NOT NULL
    , accounting_event INTEGER NOT NULL
    , entry_type INTEGER NOT NULL -- ACCOUNTING MEMO  REVERSAL ADJUSTMENT
    , business_date DATE NOT NULL
    , entry_date TIMESTAMP NOT NULL DEFAULT now()
    , reference VARCHAR(100)
    , description VARCHAR(500)
    , reversal_of INTEGER NULL
    , created_at TIMESTAMP NOT NULL DEFAULT now()

    , PRIMARY KEY (journal_entry_id)
    , CONSTRAINT fk_je_event FOREIGN KEY (accounting_event)
        REFERENCES accounting_events(event_id)
    , CONSTRAINT fk_je_reversal FOREIGN KEY (reversal_of)
        REFERENCES journal_entries(journal_entry_id)
    , CONSTRAINT fk_je_type FOREIGN KEY (entry_type)
        REFERENCES journal_entry_types(entry_type_id)
);
ALTER TABLE journal_entries OWNER TO sofie;

CREATE SEQUENCE journal_entries_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE journal_entries_s OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- journal_entry_lines 
-- ----------------------------------------------------------------------------
CREATE TABLE journal_entry_lines (
    journal_entry_line_id INTEGER NOT NULL
    , journal_entry INTEGER NOT NULL
    , line_no INTEGEr NOT NULL
    , gl_account INTEGER NOT NULL
    , debit_amount NUMERIC(20,8)
    , credit_amount NUMERIC(20,8)
    , currency INTEGER NOT NULL
    , description VARCHAR(250)

    , PRIMARY KEY (journal_entry_line_id)

    , CONSTRAINT fk_jel_entry FOREIGN KEY (journal_entry)
        REFERENCES journal_entries(journal_entry_id)

    , CONSTRAINT fk_jel_account FOREIGN KEY (gl_account)
        REFERENCES gl_accounts(account_id)

    , CONSTRAINT fk_jel_currency FOREIGN KEY (currency)
        REFERENCES currency(id_currency)
);
ALTER TABLE journal_entry_lines OWNER TO sofie;

CREATE SEQUENCE journal_entry_lines_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE journal_entry_lines_s OWNER TO sofie;

/*
DSL significa Domain Specific Language, cioè:
un linguaggio progettato per descrivere un problema specifico di un dominio.
Nel tuo caso il dominio è:
Contabilità finanziaria / Posting Engine
e quindi Groovy non viene usato come linguaggio generico, ma come linguaggio per esprimere:
quali conti movimentare
con quali importi
in quali eventi
JournalDsl è l'API che esponi a Groovy
*/