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
ALTER TABLE financial_statement_types OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE financial_statement_types_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE financial_statement_types_s OWNER TO easypricer;

-- Tabella Anagrafica della Natura del Conto
CREATE TABLE account_natures (
    nature_id INTEGER NOT NULL 
    , code VARCHAR(25) NOT NULL -- 'ASSET', 'LIABILITY', 'INCOME', 'EXPENSE', 'MEMORANDUM'
    , description VARCHAR(50) NOT NULL DEFAULT ''
    , PRIMARY KEY (nature_id)
);
ALTER TABLE account_natures OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE account_natures_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE account_natures_s OWNER TO easypricer;

-- Tabella Anagrafica del Segno Algebrico Naturale (Sezione Contabile)
CREATE TABLE normal_balances (
    balance_id INTEGER NOT NULL 
    , code VARCHAR(25) NOT NULL  -- 'DEBIT', 'CREDIT'
    , description VARCHAR(50) NOT NULL DEFAULT ''
    , PRIMARY KEY (balance_id)
);
ALTER TABLE normal_balances OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE normal_balances_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE normal_balances_s OWNER TO easypricer;

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
ALTER TABLE gl_accounts OWNER TO easypricer;

CREATE SEQUENCE gl_accounts_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE gl_accounts_s OWNER TO easypricer;


accounting_event
event_id
event_type -- TRADE_EXECUTION TRADE_CANCEL MTM COUPON ACCRUAL SETTLEMENT MATURITY FX_REVALUATION
event_status -- NEW PROCESSED FAILED

source_type -- TRADE INSTRUMENT POSITION_DETAIL(caso MTM)->  id_position_detail
source_id -- txn 12345

generated_by -- POSITION_ENGINE/LAYER (FinTxnPollingJob) / SCHEDULER_ENGINE, VALUATION_ENGINE
generated_ref -- batch_20260530_01

created_at
processed_at


event_type=MTM

source_type=POSITION_DETAIL
source_id=position_77

generated_by=VALUATION_LAYER
generated_ref=valuation_run_20260530














-- =========================================================================
-- LIVELLO 1: MACRO-CLASSI
-- =========================================================================
CREATE TABLE account_macro_classes (
    macro_id INTEGER NOT NULL
    , macro_code CHAR(1) NOT NULL -- '1' = Assets, '2' = Liabilities, ecc.
    , macro_name VARCHAR(50) NOT NULL
    , statement_type INTEGER NOT NULL 
    , nature INTEGER NOT NULL 
    , balance INTEGER NOT NULL 
    , PRIMARY KEY (macro_id)
    , CONSTRAINT fk_statement_type FOREIGN KEY (statement_type)
              REFERENCES statement_type(statement_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_nature FOREIGN KEY (nature)
              REFERENCES account_natures(nature_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_balance FOREIGN KEY (balance)
              REFERENCES normal_balances(balance_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_macro_code ON account_macro_classes(macro_code);
ALTER TABLE account_macro_classes OWNER TO easypricer;

CREATE SEQUENCE account_macro_classes_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE account_macro_classes_s OWNER TO easypricer;


-- =========================================================================
-- LIVELLO 2: CATEGORIE (Controlla che la 1ª cifra del codice sia uguale alla macro)
-- =========================================================================
CREATE OR REPLACE FUNCTION check_category_prefix_matches_macro(p_macro_id INTEGER, p_category_code VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN (SELECT LEFT(p_category_code, 1) = macro_code 
            FROM account_macro_classes 
            WHERE macro_id = p_macro_id);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE TABLE account_categories (
    category_id INTEGER NOT NULL
    , category_code VARCHAR(2) NOT NULL -- Es: '10' (Cash), '12' (Margins)
    , macro INTEGER NOT NULL 
    , category_name VARCHAR(50) NOT NULL
    , PRIMARY KEY (category_id)
    , CONSTRAINT fk_macro FOREIGN KEY (macro)
              REFERENCES account_macro_classes(macro_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT chk_category_prefix CHECK (check_category_prefix_matches_macro(macro, category_code))
);
CREATE UNIQUE INDEX idx_category_code ON account_categories(category_code);
ALTER TABLE account_categories OWNER TO easypricer;

CREATE SEQUENCE account_categories_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE account_categories_s OWNER TO easypricer;


-- =========================================================================
-- LIVELLO 3: PIANO DEI CONTI (Controlla che le prime 2 cifre siano uguali alla categoria)
-- =========================================================================
CREATE OR REPLACE FUNCTION check_account_prefix_matches_category(p_category_id INTEGER, p_account_code VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
    -- Isola i primi due caratteri del codice a 6 cifre e verifica la corrispondenza con la categoria
    RETURN (SELECT LEFT(p_account_code, 2) = category_code 
            FROM account_categories 
            WHERE category_id = p_category_id);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

CREATE TABLE chart_of_accounts (
    account_id INTEGER NOT NULL
    , account_code VARCHAR(6) NOT NULL -- (Es: '100015' per Cassa USD)
    , category INTEGER NOT NULL 
    , account_name VARCHAR(100) NOT NULL
    , currency INTEGER NOT NULL
    , is_active BOOLEAN DEFAULT TRUE
    , PRIMARY KEY (account_id)
    , CONSTRAINT fk_currency FOREIGN KEY (currency)
              REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_category FOREIGN KEY (category)
              REFERENCES account_categories(category_id) ON DELETE NO ACTION ON UPDATE NO ACTION
    CONSTRAINT chk_account_prefix CHECK (check_account_prefix_matches_category(category, account_code))
);
CREATE UNIQUE INDEX idx_account_code ON chart_of_accounts(account_code);
ALTER TABLE chart_of_accounts OWNER TO easypricer;

CREATE SEQUENCE chart_of_accounts_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE chart_of_accounts_s OWNER TO easypricer;



-- TABELLA ANAGRAFICA: Centri di Costo / Profitto (Cost Centers / Profit Centers)
CREATE TABLE cost_centers (
    cost_center_id VARCHAR(10) PRIMARY KEY, -- Es: 'CC_MKTG', 'CC_TRADING', 'CC_IT'
    cost_center_name VARCHAR(100) NOT NULL,
    department VARCHAR(50), -- Reparto di afferenza (es: 'Front Office', 'Operations')
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabella delle Testate delle Scritture (Accounting Entries Vouchers)
CREATE TABLE journal_vouchers (
    voucher_id BIGSERIAL PRIMARY KEY,
    booking_date DATE NOT NULL, -- Data di registrazione contabile
    valuation_date DATE NOT NULL, -- Data di calcolo del prezzo/valutazione (T+0 per futures, T+2 per spot/titoli)
    description TEXT,
    source_txn_id, -- punta alla transazione che ha generato il blocchetto journal_vouchers
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabella dei Righe delle Scritture (Journal Entry Lines)
-- Implementa il multi-currency ledger: traccia sia l'importo in valuta originale che il controvalore nella valuta di bilancio (EUR)
CREATE TABLE journal_lines (
    line_id BIGSERIAL PRIMARY KEY,
    voucher_id BIGINT REFERENCES journal_vouchers(voucher_id) ON DELETE CASCADE,
    account_id VARCHAR(10) REFERENCES chart_of_accounts(account_id),
    
    -- Flussi finanziari in valuta originale del conto (Es: movimenti del margine in USD)
    amount_currency NUMERIC(18, 4) NOT NULL, -- Positivo per DARE (Debit), Negativo per AVERE (Credit)
    currency CHAR(3) NOT NULL,
    
    -- Tasso di cambio applicato alla data dell'operazione
    exchange_rate NUMERIC(12, 6) NOT NULL DEFAULT 1.000000, 
    
    -- Controvalore convertito nella valuta di bilancio aziendale (EUR)
    amount_domestic NUMERIC(18, 4) NOT NULL, 
    
    -- Campi analitici opzionali per legare la scrittura a uno specifico asset/contratto
    instrument_ticker VARCHAR(20), -- Es: 'ZNM6', 'US10YT=RR' (ISIN o CUSIP per i titoli)
    contract_count INT, -- Numero di contratti (es: 4 per il tuo future)
    
    CONSTRAINT check_amount_direction CHECK (
        (amount_currency > 0 AND amount_eur > 0) OR 
        (amount_currency < 0 AND amount_eur < 0) OR 
        (amount_currency = 0 AND amount_eur = 0)
    )
);

-- Indici per ottimizzare i report finanziari, bilanci di verifica e calcolo dei saldi
CREATE INDEX idx_journal_lines_account ON journal_lines(account_id);
CREATE INDEX idx_journal_vouchers_date ON journal_vouchers(booking_date);
CREATE INDEX idx_journal_lines_ticker ON journal_lines(instrument_ticker);

-- MODIFICA/ESTENSIONE DELLA TABELLA DELLE RIGHE DI GIORNALE (journal_lines)
-- Aggiungiamo la FK verso i centri di costo. 
-- È NULLABLE perché i conti patrimoniali (es: Cassa, Margini) non usano i centri di costo.
ALTER TABLE journal_lines 
ADD COLUMN cost_center_id VARCHAR(10) REFERENCES cost_centers(cost_center_id) ON UPDATE CASCADE ON DELETE SET NULL;

accounting_event
event_id
event_type -- TRADE_EXECUTION TRADE_CANCEL MTM COUPON ACCRUAL SETTLEMENT MATURITY FX_REVALUATION
event_status -- NEW PROCESSED FAILED

source_type -- TRADE INSTRUMENT POSITION_DETAIL(caso MTM)->  id_position_detail
source_id -- txn 12345

generated_by -- POSITION_ENGINE/LAYER (FinTxnPollingJob) / SCHEDULER_ENGINE, VALUATION_ENGINE
generated_ref -- batch_20260530_01

created_at
processed_at


event_type=MTM

source_type=POSITION_DETAIL
source_id=position_77

generated_by=VALUATION_LAYER
generated_ref=valuation_run_20260530
-------------------------------------------------------------------------------
Asset 1
Cash 10: Cash and Cash Equivalents (cash and banks)
├── 100010 - Cash and Cash Equivalents - Base Currency (EUR)
├── 100015 - Cash and Cash Equivalents - Foreign Currency (USD)
└── 100020 - Petty Cash (Cassa contanti interna)

Categoria 11: Short-Term Deposits (Depositi monetari vincolati)
├── 110010 - Short-Term Time Deposits - EUR
└── 110015 - Short-Term Time Deposits - USD

Categoria 12: Margin Accounts and Broker Receivables (Conti di marginatura e crediti vs broker)
├── 120050 - Initial Margin Deposit - EUR
├── 120055 - Initial Margin Deposit - USD  <-- [Uso: Deposito cauzionale iniziale per Future ZNM6]
├── 120060 - Variation Margin Account - EUR
└── 120065 - Variation Margin Account - USD  <-- [Uso: Accredito/Addebito Mark-to-Market giornaliero]

Categoria 13: Financial Assets at FVTPL (Titoli detenuti per trading)
├── 130010 - Debt Securities - Sovereign Bonds (EUR)
├── 130015 - Debt Securities - U.S. Treasuries (USD) <-- [Uso: Acquisto bond fisico sottostante o CTD]
├── 130020 - Equity Securities - Domestic Shares (EUR)
└── 130025 - Equity Securities - International Shares (USD)

Categoria 14: Financial Derivatives - Assets (Derivati attivi con Fair Value positivo)
├── 140010 - FX Forward Contracts - Asset  <-- [Uso: Fair Value positivo fine mese contratti a termine]
└── 140020 - Options Premium Purchased (Opzioni comprate - valore di mercato)

Categoria 19: Accruals and Receivables (Ratei e crediti commerciali/finanziari)
├── 190010 - Accrued Interest Receivable - Debt Securities <-- [Uso: Rateo attivo cedole bond in maturazione]
└── 190020 - Dividends Receivable (Dividendi deliberati da incassare)

Categoria 21: Short-Term Borrowings (Finanziamenti e scoperti a breve termine)
├── 210010 - Bank Overdrafts - EUR (Scoperti di conto corrente)
├── 210015 - Bank Overdrafts - USD
└── 210020 - Short-Term Repo Loans (Finanziamenti da operazioni Pronti contro Termine)

Categoria 24: Financial Derivatives & Settlement Liabilities (Derivati passivi e debiti tecnici)
├── 240010 - FX Forward Contracts - Liability <-- [Uso: Fair Value negativo fine mese contratti a termine]
├── 240020 - Options Premium Written (Opzioni vendute/scoperte)
└── 240050 - Due to Brokers / Settlement Liabilities <-- [Uso: Debiti tecnici vs broker per transazioni T+2]

Categoria 30: Capital and Reserves (Capitale sociale e riserve)
├── 300010 - Share Capital (Capitale sociale)
├── 300050 - Retained Earnings (Utili/Perdite portati a nuovo dagli esercizi precedenti)
└── 300080 - FX Translation Reserve (Riserva da conversione per utili/perdite latenti di bilancio)

Categoria 70: Gains on Financial Derivatives (Utili e profitti da strumenti derivati)
├── 700010 - Realized Gain on Financial Derivatives <-- [Uso: Chiusura Future o Forward in profitto]
└── 700020 - Unrealized Gain on Financial Derivatives <-- [Uso: Stima Fair Value positivo fine anno]

Categoria 71: Foreign Exchange Gains (Profitti sui cambi valutari)
├── 710010 - Realized Foreign Exchange Gains <-- [Uso: Guadagno effettivo da conversione fisica USD -> EUR]
└── 710020 - Unrealized Foreign Exchange Gains <-- [Uso: Rivalutazione saldi dei conti liquidi USD a fine mese]

Categoria 72: Interest and Dividend Income (Interessi attivi e cedole)
├── 720010 - Interest Income - Bank & Short-Term Deposits
├── 720020 - Interest Income - Sovereign Debt (Coupons) <-- [Uso: Cedole incassate su Bond/Treasuries]
├── 720050 - Realized Gain on Debt Securities (Utili da compravendita Bond/Treasuries)
└── 720060 - Realized Gain on Equity Securities (Utili da compravendita Azioni/ETF)

Categoria 80: Losses on Financial Derivatives (Perdite subite su strumenti derivati)
├── 800010 - Realized Loss on Financial Derivatives <-- [Uso: Chiusura Future o Forward in perdita]
└── 800020 - Unrealized Loss on Financial Derivatives <-- [Uso: Stima Fair Value negativo fine anno]

Categoria 81: Foreign Exchange Losses (Perdite sui cambi valutari)
├── 810010 - Realized Foreign Exchange Losses <-- [Uso: Perdita effettiva da conversione fisica USD -> EUR]
└── 810020 - Unrealized Foreign Exchange Losses <-- [Uso: Svalutazione saldi dei conti liquidi USD a fine mese]

Categoria 82: Interest Expenses and Trading Losses (Interessi passivi e perdite su titoli)
├── 820010 - Interest Expense on Borrowings / Repo
├── 820050 - Realized Loss on Debt Securities (Perdite da compravendita Bond/Treasuries)
└── 820060 - Realized Loss on Equity Securities (Perdite da compravendita Azioni/ETF)

Categoria 88: Trading Fees and Execution Costs (Commissioni e spese di negoziazione)
├── 880010 - Brokerage and Execution Fees <-- [Uso: Costo vivo di apertura/chiusura contratti ZNM6]
├── 880020 - Clearing and Exchange Fees (Spese di regolamento della Cassa di Compensazione / CME)
└── 880030 - Custody and Safe-Keeping Fees (Spese di custodia dei titoli di Stato fisici)

Categoria 90: Financial Commitments (Tracciabilità del valore nozionale aperto sul mercato)
├── 900010 - Financial Commitments - Long Futures <-- [Uso: Valore nozionale contratti Future acquistati]
├── 900015 - Financial Commitments - Short Futures (Valore nozionale contratti Future venduti)
├── 900020 - Financial Commitments - Forward Currency Purchase <-- [Uso: Nozionale contratti Forward Forex]
└── 990030 - Counterpart for Financial Commitments <-- [Uso: Contropartita tecnica obbligatoria per far quadrare il pacchetto 9xxx a zero]



-- Inserimento Livello 1 (Macro-classi)
INSERT INTO account_macro_classes (macro_id, macro_name, statement_type) VALUES
('1', 'Assets', 'BALANCE_SHEET'),
('2', 'Liabilities', 'BALANCE_SHEET'),
('7', 'Financial Income', 'INCOME_STATEMENT'),
('7', 'Financial Expenses', 'INCOME_STATEMENT'),
('9', 'Memorandum Accounts', 'OFF_BALANCE_SHEET');

-- Inserimento Livello 2 (Categorie)
INSERT INTO account_categories (category_id, macro_id, category_name) VALUES
('10', '1', 'Cash and Cash Equivalents'),
('12', '1', 'Margin Accounts and Broker Receivables'),
('24', '2', 'Financial Derivatives and Settlement Liabilities'),
('70', '7', 'Gains on Financial Derivatives'),
('90', '9', 'Financial Commitments');

-- Inserimento Livello 3 (Conti operativi finali)
INSERT INTO chart_of_accounts (account_id, category_id, account_name, currency) VALUES
('1010', '10', 'Cash and Cash Equivalents - EUR', 'EUR'),
('1015', '10', 'Cash and Cash Equivalents - USD Account', 'USD'),
('1255', '12', 'Margin Account with Broker - USD', 'USD'),
('2410', '24', 'Financial Derivatives - Liability (Forward Forex)', 'EUR'),
('7010', '70', 'Realized Gain on Financial Derivatives', 'EUR'),
('9010', '90', 'Financial Commitments - Long Futures', 'USD');
 