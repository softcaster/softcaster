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
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (3,'TRADE_CANCEL','Trade Cancelled');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (4,'MTM','Mtm');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (5,'COUPON','Coupon');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (6,'ACCRUAL','Accrual');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (7,'SETTLEMENT','Settlement');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (8,'MATURITY','Maturity');
INSERT INTO accounting_event_types(event_type_id,code, description) VALUES (9,'FX_REVALUATION','Fx Revaluation');

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
 

-- account_mapping mappatura per il resover utilizzato dagli script groovy
-- 120090 Currency Position
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR
    VALUES(nextval('account_mapping_s'),'CURRENCY_POSITION',1,79);
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- USD 
    VALUES(nextval('account_mapping_s'),'CURRENCY_POSITION',2,80);
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- CHF 
    VALUES(nextval('account_mapping_s'),'CURRENCY_POSITION',4,81);
-- 130050 Fx Spot Asset
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR
    VALUES(nextval('account_mapping_s'),'FX_SPOT_ASSET',1,37);
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- USD 
    VALUES(nextval('account_mapping_s'),'FX_SPOT_ASSET',2,87);
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- CHF 
    VALUES(nextval('account_mapping_s'),'FX_SPOT_ASSET',4,88);
-- 240090 Currency Position Control
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR
    VALUES(nextval('account_mapping_s'),'POSITION_CONTROL',1,51);
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- USD 
    VALUES(nextval('account_mapping_s'),'POSITION_CONTROL',2,82);
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- CHF 
    VALUES(nextval('account_mapping_s'),'POSITION_CONTROL',4,83);

INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR 
    VALUES(nextval('account_mapping_s'),'BOND_ASSET',1,33); -- 130010 Debt Securities - Sovereign Bonds (EUR)
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR 
    VALUES(nextval('account_mapping_s'),'ACCRUED_INTEREST',1,41); -- 190010 Accrued Interest Receivable (EUR)
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR 
    VALUES(nextval('account_mapping_s'),'SETTLEMENT_LIAB',1,50); -- 240050 Due to Brokers / Settlement Liabilities (EUR)
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR 
    VALUES(nextval('account_mapping_s'),'INTEREST_INCOME',1,60); -- 420020 Interest Income - EUR (Coupons)
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR 
    VALUES(nextval('account_mapping_s'),'CASH_ACCOUNT',1,24); -- 100010 Cash and Cash Equivalents - Base Currency (EUR)

INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- USD 
    VALUES(nextval('account_mapping_s'),'INITIAL_MARGIN',2,30); -- 120055 Initial Margin Deposit - USD
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- USD 
    VALUES(nextval('account_mapping_s'),'VARIATION_MARGIN',2,32); -- 120065 Variation Margin Account - USD
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- USD 
    VALUES(nextval('account_mapping_s'),'FUT_REALIZED_LOSS',1,19); -- 500010 Realized Loss on Financial Derivatives - EUR
INSERT INTO account_mapping(account_mapping_id,mapping_key,currency,gl_account) -- EUR 
    VALUES(nextval('account_mapping_s'),'FUT_REALIZED_GAIN ',1,16); -- 400010 Realized Gain on Financial Derivatives - EUR

-- =========================================================================
-- 1. BOND_COMMITMENT_BUY (Impegni di acquisto titoli - es. 600010)
-- =========================================================================
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) 
    VALUES(nextval('account_mapping_s'), 'FXSPOT_COMMITMENT', 1, 99); -- EUR (es. 600040)
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) 
    VALUES(nextval('account_mapping_s'), 'FXSPOT_COMMITMENT', 2, 100); -- USD (es. 600041)
INSERT INTO account_mapping(account_mapping_id, mapping_key, currency, gl_account) 
    VALUES(nextval('account_mapping_s'), 'FXSPOT_COMMITMENT', 4, 101); -- CHF (es. 600042)
