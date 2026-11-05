-- ============================================================================
-- Indice sui link di una posizione, nell'ordine in cui i deal vi sono entrati.
-- Serve alla ricostruzione della posizione su cancel e amend e ai ratei di ogni
-- chiusura: senza, ogni ricerca per position_detail scorre tutta la tabella.
-- Riportata in sofie.sql.
-- ============================================================================

CREATE INDEX idx_position_txn_links_detail ON position_txn_links (position_detail, pos_txn_link_id);
