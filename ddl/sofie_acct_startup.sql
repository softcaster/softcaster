-- financial_statement_types
INSERT INTO financial_statement_types(statement_type_id,code, description) VALUES (1,'BALANCE_SHEET','Balance Sheet');
INSERT INTO financial_statement_types(statement_type_id,code, description) VALUES (2,'INCOME_STATEMENT','Income Statement');
INSERT INTO financial_statement_types(statement_type_id,code, description) VALUES (3,'OFF_BALANCE_SHEET','Off Balance Sheet');

-- account_natures
INSERT INTO account_natures(nature_id,code, description) VALUES (1,'ASSET','Asset');
INSERT INTO account_natures(nature_id,code, description) VALUES (2,'LIABILITY','Liability');
INSERT INTO account_natures(nature_id,code, description) VALUES (3,'EQUITY','Equity');
INSERT INTO account_natures(nature_id,code, description) VALUES (4,'INCOME','Income');
INSERT INTO account_natures(nature_id,code, description) VALUES (5,'EXPENSE','Expense');
INSERT INTO account_natures(nature_id,code, description) VALUES (6,'MEMORANDUM','Memorandum');

-- normal_balances
INSERT INTO normal_balances(balance_id,code, description) VALUES (1,'DEBIT','Dr');
INSERT INTO normal_balances(balance_id,code, description) VALUES (2,'CREDIT','Cr');

-- accounting_event_types
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (1,'TRADE_EXECUTED','Trade Executed');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (2,'TRADE_AMENDED','Trade Amended');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (3,'TRADE_CANCELED','Trade Cancelled');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (4,'MTM','Mtm');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (5,'COUPON','Coupon');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (6,'ACCRUAL','Accrual');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (7,'SETTLEMENT','Settlement');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (8,'MATURITY','Maturity');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (9,'ROLLOVER','Rollover');

-- accounting_event_status
INSERT INTO accounting_event_status(event_status_id,code, description) VALUES (1,'NEW','New');
INSERT INTO accounting_event_status(event_status_id,code, description) VALUES (2,'PROCESSING','Processing');
INSERT INTO accounting_event_status(event_status_id,code, description) VALUES (3,'PROCESSED','Processed');
INSERT INTO accounting_event_status(event_status_id,code, description) VALUES (4,'FAILED','Failed');

-- source_event_types
INSERT INTO event_source_types(source_type_id,code, description) VALUES (1,'TRADE','Trade');
INSERT INTO event_source_types(source_type_id,code, description) VALUES (2,'INSTRUMENT','Instrument');
INSERT INTO event_source_types(source_type_id,code, description) VALUES (3,'POSITION_DETAIL','Position Detail');

-- journal_entry_types
INSERT INTO journal_entry_types VALUES (1,'ACCOUNTING','Official accounting entry');
INSERT INTO journal_entry_types VALUES (2,'MEMO','Memorandum entry');
INSERT INTO journal_entry_types VALUES (3,'REVERSAL','Reversal entry');
INSERT INTO journal_entry_types VALUES (4,'ADJUSTMENT','Adjustment entry');

-- journal_entry_status
INSERT INTO journal_entry_status VALUES (1,'UNCONSOLIDATED','Unconsolidated');
INSERT INTO journal_entry_status VALUES (2,'CONSOLIDATED','Consolidated');
INSERT INTO journal_entry_status VALUES (3,'ERROR','Error');
 
-- account_mapping: chiave di mappatura + valuta -> conto reale, usata dagli script groovy
-- (AccountResolverService). Allineato al DB il 02/11/2026; conti e valute per codice.
-- 100010 Cash and Cash Equivalents
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'CASH_ACCOUNT',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '100010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'CASH_ACCOUNT',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '100010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'CASH_ACCOUNT',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '100010'));
-- 120050 Initial Margin Deposit
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INITIAL_MARGIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '120050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INITIAL_MARGIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '120050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INITIAL_MARGIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '120050'));
-- 120060 Variation Margin Account
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'VARIATION_MARGIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '120060'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'VARIATION_MARGIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '120060'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'VARIATION_MARGIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '120060'));
-- 120090 Currency Position
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'CURRENCY_POSITION',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '120090'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'CURRENCY_POSITION',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '120090'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'CURRENCY_POSITION',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '120090'));
-- 130010 Debt Securities - Sovereign Bonds
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_ASSET',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '130010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_ASSET',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '130010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_ASSET',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '130010'));
-- 130050 FX Spot Position
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FX_SPOT_ASSET',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '130050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FX_SPOT_ASSET',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '130050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FX_SPOT_ASSET',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '130050'));
-- 190010 Accrued Interest
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'ACCRUED_INTEREST',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '190010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'ACCRUED_INTEREST',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '190010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'ACCRUED_INTEREST',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '190010'));
-- 240050 Due to Brokers / Settlement Liabilities
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'SETTLEMENT_LIAB',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '240050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'SETTLEMENT_LIAB',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '240050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'SETTLEMENT_LIAB',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '240050'));
-- 240090 Currency Position Control
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'POSITION_CONTROL',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '240090'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'POSITION_CONTROL',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '240090'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'POSITION_CONTROL',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '240090'));
-- 400010 Realized Gain on Financial Derivatives
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FUT_REALIZED_GAIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '400010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FUT_REALIZED_GAIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '400010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FUT_REALIZED_GAIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '400010'));
-- 420020 Interest Income - Sovereign Debt (Coupons)
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INTEREST_INCOME',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '420020'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INTEREST_INCOME',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '420020'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INTEREST_INCOME',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '420020'));
-- 500010 Realized Loss on Financial Derivatives
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FUT_REALIZED_LOSS',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '500010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FUT_REALIZED_LOSS',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '500010'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FUT_REALIZED_LOSS',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '500010'));
-- 520020 Interest Expense - Sovereign Debt (Coupons)
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INTEREST_EXPENSE',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '520020'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INTEREST_EXPENSE',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '520020'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'INTEREST_EXPENSE',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '520020'));
-- 600040 Financial Commitments - Bonds
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_COMMITMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '600040'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_COMMITMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '600040'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_COMMITMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '600040'));
-- 600050 Financial Commitments - Fx-Spot
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FXSPOT_COMMITMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '600050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FXSPOT_COMMITMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '600050'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'FXSPOT_COMMITMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '600050'));
-- 600090 Off-Balance Sheet Clearing Account
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'OBS_CLEARING',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '600090'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'OBS_CLEARING',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '600090'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'OBS_CLEARING',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '600090'));

-- 130030 Fair Value Adjustment - Debt Securities (ddl/mtm_snapshot.sql)
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_FV_ADJUSTMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '130030'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_FV_ADJUSTMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '130030'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_FV_ADJUSTMENT',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '130030'));
-- 420070 Unrealized Gain on Debt Securities
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_UNREALIZED_GAIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '420070'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_UNREALIZED_GAIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '420070'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_UNREALIZED_GAIN',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '420070'));
-- 520070 Unrealized Loss on Debt Securities
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_UNREALIZED_LOSS',
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), (SELECT account_id FROM gl_accounts WHERE code = '520070'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_UNREALIZED_LOSS',
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), (SELECT account_id FROM gl_accounts WHERE code = '520070'));
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) VALUES (nextval('account_mapping_s'), 'BOND_UNREALIZED_LOSS',
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), (SELECT account_id FROM gl_accounts WHERE code = '520070'));

-- asset_class_script_mapping: strategia Groovy per asset class (event_type NULL = tutti gli eventi);
-- senza riga si usa lo script col codice dell'asset class (es. FSP.groovy).
-- Titoli fissi e variabili: XRB.groovy; future marginati: FUT.groovy.
INSERT INTO asset_class_script_mapping(asset_class_script_mapping_id, asset_class, event_type, script_code)
    VALUES(nextval('asset_class_script_mapping_s'), (SELECT id_asset_class FROM asset_class WHERE code='FRN'), NULL, 'XRB');
INSERT INTO asset_class_script_mapping(asset_class_script_mapping_id, asset_class, event_type, script_code)
    VALUES(nextval('asset_class_script_mapping_s'), (SELECT id_asset_class FROM asset_class WHERE code='XRN'), NULL, 'XRB');
INSERT INTO asset_class_script_mapping(asset_class_script_mapping_id, asset_class, event_type, script_code)
    VALUES(nextval('asset_class_script_mapping_s'), (SELECT id_asset_class FROM asset_class WHERE code='FRB'), NULL, 'XRB');
INSERT INTO asset_class_script_mapping(asset_class_script_mapping_id, asset_class, event_type, script_code)
    VALUES(nextval('asset_class_script_mapping_s'), (SELECT id_asset_class FROM asset_class WHERE code='XRB'), NULL, 'XRB');
INSERT INTO asset_class_script_mapping(asset_class_script_mapping_id, asset_class, event_type, script_code)
    VALUES(nextval('asset_class_script_mapping_s'), (SELECT id_asset_class FROM asset_class WHERE code='BFU'), NULL, 'FUT');
INSERT INTO asset_class_script_mapping(asset_class_script_mapping_id, asset_class, event_type, script_code)
    VALUES(nextval('asset_class_script_mapping_s'), (SELECT id_asset_class FROM asset_class WHERE code='FFU'), NULL, 'FUT');
