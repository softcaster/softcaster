-- ============================================================================
-- accounting_event_mtm: net_quantity -> accounting_nominal
--
-- Il nominale della posizione sta in PositionAccountingEvent (@MappedSuperclass
-- di AccountingEventAccruals e AccountingEventMtm), con lo stesso nome di
-- colonna in tutte le foglie: Hibernate non supporta @AttributeOverride su
-- un'entita' con ereditarieta' (HHH000499). Stesso significato: nominale con
-- segno (negativo = posizione corta), senza multiplier.
-- ============================================================================

BEGIN;

ALTER TABLE accounting_event_mtm RENAME COLUMN net_quantity TO accounting_nominal;

COMMIT;
