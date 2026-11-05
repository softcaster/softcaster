-- ============================================================================
-- Indice sui componenti di una transazione. Serve alla ricostruzione della
-- posizione su cancel e amend: senza, ogni ricostruzione scorre tutti i
-- componenti, anche per una posizione con pochi deal.
-- Riportata in sofie.sql.
-- ============================================================================

CREATE INDEX idx_financial_txn_components_txn ON financial_txn_components (financial_txn);
