-- Descrizioni delle tabelle di lookup allineate agli enum (fonte autorevole):
-- le descrizioni sono le etichette mostrate dai client (endpoint /enums).
-- Aggiornamento per codice, ripetibile.

BEGIN;

UPDATE daycount SET description = '30/360 US (NASD)' WHERE code = 'NASD_30_360';
UPDATE daycount SET description = 'ACT/360'          WHERE code = 'ACT_360';
UPDATE daycount SET description = 'ACT/365 Fixed'    WHERE code = 'ACT_365';
UPDATE daycount SET description = 'ACT/ACT ISDA'     WHERE code = 'ACT_ACT_ISDA';
UPDATE daycount SET description = 'ACT/ACT ICMA'     WHERE code = 'ACT_ACT_ICMA';
UPDATE daycount SET description = '30/360 European'  WHERE code = 'EUR_30_360';

UPDATE roll_convention SET description = 'Preceding'          WHERE code = 'PREVIOUS';
UPDATE roll_convention SET description = 'Modified preceding' WHERE code = 'PREVIOUS-MODIFIED';
UPDATE roll_convention SET description = 'Following'          WHERE code = 'FORWARD';
UPDATE roll_convention SET description = 'Modified following' WHERE code = 'FORWARD-MODIFIED';
UPDATE roll_convention SET description = 'Unadjusted'         WHERE code = 'UNADJUSTED';

UPDATE frequency SET description = 'Semi-annual'      WHERE code = 'SEMI-ANNUAL';
UPDATE frequency SET description = 'Bi-monthly'       WHERE code = 'BI-MONTHLY';
UPDATE frequency SET description = 'At maturity only' WHERE code = 'NONE';

UPDATE amortization_schedule SET description = 'French (level installment)'     WHERE code = 'SAS';
UPDATE amortization_schedule SET description = 'Italian (level principal)'      WHERE code = 'SLP';
UPDATE amortization_schedule SET description = 'Bullet (principal at maturity)' WHERE code = 'IOL';

UPDATE settlement_type SET description = 'Physical' WHERE code = 'PHYSICAL';
UPDATE settlement_type SET description = 'Cash'     WHERE code = 'CASH';

UPDATE accumulator_type SET description = 'Accumulator' WHERE code = 'ACCUMULATOR';
UPDATE accumulator_type SET description = 'Decumulator' WHERE code = 'DECUMULATOR';

UPDATE guarantee_type SET description = 'Leveraged'   WHERE code = 'LEVERAGED';
UPDATE guarantee_type SET description = 'Unleveraged' WHERE code = 'UNLEVERAGED';

UPDATE knock_in_effect SET description = 'Activates the leverage' WHERE code = 'LEVERAGE';

UPDATE fee_basis SET description = 'Amount'     WHERE code = 'AMOUNT';
UPDATE fee_basis SET description = '% (points)' WHERE code = 'PERCENT';

UPDATE fee_timing SET description = 'Upfront'         WHERE code = 'UPFRONT';
UPDATE fee_timing SET description = 'Per installment' WHERE code = 'PER_INSTALLMENT';

COMMIT;
