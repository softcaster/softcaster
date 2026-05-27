---------------------------------------------------------------------
-- Tabelle modulo contabile
---------------------------------------------------------------------
-- LIVELLO 0A: Tabella Anagrafica dei Tipi di Rendiconto Finanziario
CREATE TABLE financial_statement_types (
    statement_type_id VARCHAR(20) PRIMARY KEY, -- 'BALANCE_SHEET', 'INCOME_STATEMENT', 'OFF_BALANCE_SHEET'
    description_en VARCHAR(100) NOT NULL,
    description_it VARCHAR(100) NOT NULL
);

-- LIVELLO 0B: Tabella Anagrafica della Natura del Conto
CREATE TABLE account_natures (
    nature_id VARCHAR(20) PRIMARY KEY, -- 'ASSET', 'LIABILITY', 'INCOME', 'EXPENSE', 'MEMORANDUM'
    description_en VARCHAR(100) NOT NULL,
    description_it VARCHAR(100) NOT NULL
);

-- LIVELLO 0C: Tabella Anagrafica del Segno Algebrico Naturale (Sezione Contabile)
CREATE TABLE normal_balances (
    balance_id VARCHAR(6) PRIMARY KEY, -- 'DEBIT', 'CREDIT'
    description_en VARCHAR(100) NOT NULL,
    description_it VARCHAR(100) NOT NULL
);

-- LIVELLO 1: Macro-classi di Bilancio (Ora punta a ben 3 tabelle anagrafiche tramite FK)
CREATE TABLE account_macro_classes (
    macro_id CHAR(1) PRIMARY KEY, -- '1' = Assets, '2' = Liabilities, ecc.
    macro_name VARCHAR(50) NOT NULL,
    statement_type_id VARCHAR(20) NOT NULL REFERENCES financial_statement_types(statement_type_id),
    nature_id VARCHAR(20) NOT NULL REFERENCES account_natures(nature_id),
    balance_id VARCHAR(6) NOT NULL REFERENCES normal_balances(balance_id)
);

-- LIVELLO 2: Categorie / Sotto-classi (Invariata)
CREATE TABLE account_categories (
    category_id VARCHAR(2) PRIMARY KEY, 
    macro_id CHAR(1) NOT NULL REFERENCES account_macro_classes(macro_id),
    category_name VARCHAR(100) NOT NULL,
    CONSTRAINT chk_category_prefix CHECK (LEFT(category_id, 1) = macro_id)
);

-- LIVELLO 3: Conti Operativi di Dettaglio (Invariata)
CREATE TABLE chart_of_accounts (
    account_id VARCHAR(4) PRIMARY KEY, 
    category_id VARCHAR(2) NOT NULL REFERENCES account_categories(category_id),
    account_name VARCHAR(100) NOT NULL,
    currency CHAR(3) NOT NULL DEFAULT 'EUR',
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_account_prefix CHECK (LEFT(account_id, 2) = category_id)
);

-- TABELLA ANAGRAFICA: Centri di Costo / Profitto (Cost Centers / Profit Centers)
CREATE TABLE cost_centers (
    cost_center_id VARCHAR(10) PRIMARY KEY, -- Es: 'CC_MKTG', 'CC_TRADING', 'CC_IT'
    cost_center_name VARCHAR(100) NOT NULL,
    department VARCHAR(50), -- Reparto di afferenza (es: 'Front Office', 'Operations')
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 1. Tabella dei Conti (Chart of Accounts)
CREATE TABLE chart_of_accounts (
    account_id VARCHAR(10) PRIMARY KEY, -- Es: '1015', '7010'
    account_name VARCHAR(100) NOT NULL,
    account_type VARCHAR(20) NOT NULL, -- 'Asset', 'Liability', 'Income', 'Expense', 'Memorandum'
    currency CHAR(3) NOT NULL DEFAULT 'EUR', -- Valuta nativa del conto ('EUR', 'USD')
    is_active BOOLEAN DEFAULT TRUE
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


