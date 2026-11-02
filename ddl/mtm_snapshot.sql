-- ============================================================================
-- MTM di fine giornata: fotografia della posizione per la contabilita'
--
-- accounting_event_mtm: dettaglio dell'evento MTM (come
-- accounting_event_accruals per i ratei), scritto solo dal MtM di end of day
-- (POST /internal/mtm/eod di easy_pricer_mtm), uno per position_detail e
-- data ufficiale. La contabilita' registra la variazione rispetto all'ultima
-- fotografia PROCESSED della stessa posizione:
--   future (FUT.groovy): realizzato + non realizzato -> margine di variazione
--   titoli (XRB.groovy): non realizzato -> rettifica di fair value del titolo
--
-- Conti nuovi per i titoli al fair value (FVTPL), fratelli di 130010, 420050,
-- 520050, con gli slot in tutte le valute; chiavi di mapping in EUR, USD, CHF:
--   BOND_FV_ADJUSTMENT    130030 Fair Value Adjustment - Debt Securities
--   BOND_UNREALIZED_GAIN  420070 Unrealized Gain on Debt Securities
--   BOND_UNREALIZED_LOSS  520070 Unrealized Loss on Debt Securities
-- ============================================================================

BEGIN;

-- ----------------------------------------------------------------------------
-- 1. Fotografia MTM
-- ----------------------------------------------------------------------------
CREATE TABLE accounting_event_mtm (
    event_id        INTEGER NOT NULL,
    valuation_date  DATE NOT NULL,               -- data ufficiale del MtM di fine giornata
    market_price    NUMERIC(23, 10) NOT NULL,    -- prezzo quotato (titoli: secco, base 100)
    net_quantity    NUMERIC(23, 5) NOT NULL,     -- con segno: negativa = posizione corta
    average_price   NUMERIC(23, 10) NOT NULL,    -- prezzo medio unitario del lato aperto, 0 se chiusa
    realized_pnl    NUMERIC(23, 5) NOT NULL,     -- realizzato cumulato della posizione
    unrealized_pnl  NUMERIC(23, 5) NOT NULL,     -- non realizzato al prezzo di mercato
    CONSTRAINT pk_accounting_event_mtm PRIMARY KEY (event_id),
    CONSTRAINT fk_accounting_event_mtm_event
        FOREIGN KEY (event_id) REFERENCES accounting_events (event_id) ON DELETE CASCADE
);
ALTER TABLE accounting_event_mtm OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- 2. Conti dei titoli al fair value
-- ----------------------------------------------------------------------------
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance)
SELECT nextval('gl_accounts_s'), s.parent, n.code, n.description, TRUE, s.statement_type, s.nature, s.balance
FROM (VALUES ('130030', 'Fair Value Adjustment - Debt Securities', '130010'),
             ('420070', 'Unrealized Gain on Debt Securities', '420050'),
             ('520070', 'Unrealized Loss on Debt Securities', '520050')) AS n(code, description, sibling)
JOIN gl_accounts s ON s.code = n.sibling
WHERE NOT EXISTS (SELECT 1 FROM gl_accounts g WHERE g.code = n.code)
ORDER BY n.code;

INSERT INTO gl_account_slots (account_slot_id, account, currency)
SELECT nextval('gl_account_slots_s'), g.account_id, c.id_currency
FROM gl_accounts g
CROSS JOIN currency c
WHERE g.code IN ('130030', '420070', '520070')
  AND NOT EXISTS (SELECT 1 FROM gl_account_slots s WHERE s.account = g.account_id AND s.currency = c.id_currency)
ORDER BY g.code, c.id_currency;

INSERT INTO account_mapping (account_mapping_id, mapping_key, currency, gl_account)
SELECT nextval('account_mapping_s'), m.mapping_key, c.id_currency, g.account_id
FROM (VALUES ('BOND_FV_ADJUSTMENT', '130030'),
             ('BOND_UNREALIZED_GAIN', '420070'),
             ('BOND_UNREALIZED_LOSS', '520070')) AS m(mapping_key, code)
JOIN gl_accounts g ON g.code = m.code
JOIN currency c ON c.iso_code IN ('EUR', 'USD', 'CHF')
WHERE NOT EXISTS (SELECT 1 FROM account_mapping am WHERE am.mapping_key = m.mapping_key AND am.currency = c.id_currency)
ORDER BY g.code, c.id_currency;

COMMIT;
