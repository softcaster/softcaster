-- ============================================================================
-- Asset class delle opzioni su strumento secco (ramo spot_option_*):
--   FXO  Forex Option  (sottostante: coppia di cambi)
--   BDO  Bond Option   (sottostante: titolo)
-- ============================================================================

BEGIN;

INSERT INTO asset_class (id_asset_class, code, description, super_class)
VALUES (nextval('asset_class_s'), 'FXO', 'Forex Option', (SELECT id_super_class FROM super_class WHERE code = 'DER'));

INSERT INTO asset_class (id_asset_class, code, description, super_class)
VALUES (nextval('asset_class_s'), 'BDO', 'Bond Option', (SELECT id_super_class FROM super_class WHERE code = 'DER'));

COMMIT;
