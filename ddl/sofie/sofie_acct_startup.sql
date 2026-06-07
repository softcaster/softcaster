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
INSERT INTO accounting_event_status(event_status_id,code, description) VALUES (2,'PROCESSED','Processed');
INSERT INTO accounting_event_status(event_status_id,code, description) VALUES (3,'FAILED','Failed');

-- source_event_types
INSERT INTO event_source_types(source_type_id,code, description) VALUES (1,'TRADE','Trade');
INSERT INTO event_source_types(source_type_id,code, description) VALUES (2,'INSTRUMENT','Instrument');
INSERT INTO event_source_types(source_type_id,code, description) VALUES (3,'POSITION_DETAIL','Position Detail');
