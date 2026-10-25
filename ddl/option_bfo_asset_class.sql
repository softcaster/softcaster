-- ============================================================================
-- Asset class delle opzioni su bond future (ramo future_option_*):
--   BFO  Bond Future Option  (sottostante: serie di un bond future)
-- ============================================================================

BEGIN;

INSERT INTO asset_class (id_asset_class, code, description, super_class)
VALUES (nextval('asset_class_s'), 'BFO', 'Bond Future Option', (SELECT id_super_class FROM super_class WHERE code = 'DER'));

COMMIT;
