-- ============================================================================
-- Tabelle di decodifica allineate agli enum Java (fanno fede gli enum)
--
--   compounding            id 3 = SIMPLE-THEN-COMPOUNDED, id 4 = CONTINUOUS
--                          (erano invertiti). cash_flow_master_data non usava 3
--                          o 4; yield_curve_item e' letto via enum (id 4 della
--                          curva BCE = CONTINUOUS, corretto) e resta com'e';
--                          l'intestazione yield_curve.compounding, valorizzata
--                          per codice e non letta da Java, si scambia 3 <-> 4
--                          per conservarne il significato.
--   form                   aggiunto 100 NONE
--   accrual_schedule_type  codice dell'id 100: NONE -> AST_NONE
--   coupon_pm              aggiunti 3 HIST_SERIE e 4 NONE
-- ============================================================================

BEGIN;

UPDATE compounding SET code = 'TMP_3' WHERE id_compounding = 3;
UPDATE compounding SET code = 'CONTINUOUS', description = 'Continuous' WHERE id_compounding = 4;
UPDATE compounding SET code = 'SIMPLE-THEN-COMPOUNDED', description = 'Linear then Compounded' WHERE id_compounding = 3;
UPDATE yield_curve SET compounding = CASE compounding WHEN 3 THEN 4 WHEN 4 THEN 3 END WHERE compounding IN (3, 4);

INSERT INTO form (id_form, code, description) VALUES (100, 'NONE', 'None');

UPDATE accrual_schedule_type SET code = 'AST_NONE' WHERE id_accrual_schedule_type = 100;

INSERT INTO coupon_pm (coupon_pm_id, code, description) VALUES (3, 'HIST_SERIE', 'Historical Serie');
INSERT INTO coupon_pm (coupon_pm_id, code, description) VALUES (4, 'NONE', 'None');

COMMIT;
