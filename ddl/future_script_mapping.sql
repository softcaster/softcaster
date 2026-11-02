-- ============================================================================
-- Future marginati: una sola strategia contabile, FUT.groovy
--
-- BFU.groovy e FFU.groovy facevano la stessa cosa in due modi: BFU tutto nella
-- settlement currency (conversione nella System Currency demandata al Risk
-- Engine Forex, come XRB), FFU con l'EUR fisso (id 1) e il cambio della
-- transazione. FUT.groovy segue BFU, con il rilascio del margine a scadenza
-- preso da FFU. Riportata in sofie_acct_startup.sql.
-- ============================================================================

BEGIN;

INSERT INTO asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code)
SELECT nextval('asset_class_script_mapping_s'), ac.id_asset_class, NULL, 'FUT'
FROM asset_class ac
WHERE ac.code IN ('BFU', 'FFU')
  AND NOT EXISTS (SELECT 1 FROM asset_class_script_mapping m WHERE m.asset_class = ac.id_asset_class AND m.event_type IS NULL)
ORDER BY ac.code;

COMMIT;
