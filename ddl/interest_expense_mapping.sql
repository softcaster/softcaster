-- ============================================================================
-- INTEREST_EXPENSE (520020 Interest Expense - Sovereign Debt) in USD e CHF
--
-- Il rateo e la cedola di una posizione corta in titoli (XRB.groovy) usano
-- INTEREST_EXPENSE nella valuta del titolo: c'era solo EUR, come gli altri
-- conti del bond ora in EUR, USD, CHF.
--
-- Il conto 520020 non aveva slot (gl_account_slots), quindi anche l'EUR
-- falliva ("Invalid slot"): si aggiungono gli slot per tutte le valute, come
-- fa DatabaseHelper.updateGlAccountSlot per ogni conto postable.
-- ============================================================================

BEGIN;

INSERT INTO gl_account_slots (account_slot_id, account, currency)
SELECT nextval('gl_account_slots_s'), g.account_id, c.id_currency
FROM gl_accounts g
CROSS JOIN currency c
WHERE g.code = '520020'
  AND NOT EXISTS (SELECT 1 FROM gl_account_slots s WHERE s.account = g.account_id AND s.currency = c.id_currency)
ORDER BY c.id_currency;

INSERT INTO account_mapping (account_mapping_id, mapping_key, currency, gl_account)
SELECT nextval('account_mapping_s'), 'INTEREST_EXPENSE', c.id_currency, g.account_id
FROM gl_accounts g
JOIN currency c ON c.iso_code IN ('USD', 'CHF')
WHERE g.code = '520020'
  AND NOT EXISTS (SELECT 1 FROM account_mapping am WHERE am.mapping_key = 'INTEREST_EXPENSE' AND am.currency = c.id_currency)
ORDER BY c.id_currency;

COMMIT;
