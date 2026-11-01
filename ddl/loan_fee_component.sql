-- ============================================================================
-- Mutui OTC: componente della transazione per le spese all'erogazione
--
-- LoanTxnProcessor registra le spese una tantum del mutuo (loan_fee con
-- fee_timing UPFRONT) come componente LOAN_FEE del deal, importo positivo:
-- incassate se la banca presta (BUY), pagate se prende a prestito (SELL).
-- Solo per i deal non successivi all'erogazione.
-- Enum Java: TxnComponentType.LOAN_FEE (8). Riportata in sofie_startup.sql.
-- ============================================================================

BEGIN;

INSERT INTO txn_component_types(component_type_id, code, description)
VALUES (8, 'LOAN_FEE', 'Loan fees charged at disbursement')
ON CONFLICT (component_type_id) DO NOTHING;

COMMIT;
