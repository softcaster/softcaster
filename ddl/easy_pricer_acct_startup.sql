-- financial_statement_types
INSERT INTO financial_statement_types(statement_type_id,code, description) VALUES (nextval('financial_statement_types_s'),'BALANCE_SHEET','Balance Sheet');
INSERT INTO financial_statement_types(statement_type_id,code, description) VALUES (nextval('financial_statement_types_s'),'INCOME_STATEMENT','Income Statement');
INSERT INTO financial_statement_types(statement_type_id,code, description) VALUES (nextval('financial_statement_types_s'),'OFF_BALANCE_SHEET','Off Balance Sheet');

-- account_natures
INSERT INTO account_natures(nature_id,code, description) VALUES (nextval('account_natures_s'),'ASSET','Asset');
INSERT INTO account_natures(nature_id,code, description) VALUES (nextval('account_natures_s'),'LIABILITY','Liability');
INSERT INTO account_natures(nature_id,code, description) VALUES (nextval('account_natures_s'),'EQUITY','Equity');
INSERT INTO account_natures(nature_id,code, description) VALUES (nextval('account_natures_s'),'INCOME','Income');
INSERT INTO account_natures(nature_id,code, description) VALUES (nextval('account_natures_s'),'EXPENSE','Expense');
INSERT INTO account_natures(nature_id,code, description) VALUES (nextval('account_natures_s'),'MEMORANDUM','Memorandum');

-- normal_balances
INSERT INTO normal_balances(balance_id,code, description) VALUES (nextval('normal_balances_s'),'DEBIT','Dr');
INSERT INTO normal_balances(balance_id,code, description) VALUES (nextval('normal_balances_s'),'CREDIT','Cr');

-- ASSET
INSERT INTO gl_accounts (
    account_id,
    parent,
    code,
    description,
    is_postable,
    currency,
    statement_type,
    nature,
    balance
)
VALUES (
    nextval('gl_accounts_s'),
    NULL,
    '1',
    'Assets',
    FALSE,
    NULL,
    1,  -- BALANCE_SHEET
    1,  -- ASSET
    1   -- DE
);

-- CASH
INSERT INTO gl_accounts (
    account_id,
    parent,
    code,
    description,
    is_postable,
    currency,
    statement_type,
    nature,
    balance
)
VALUES (
    nextval('gl_accounts_s'),
    (
        SELECT account_id
        FROM gl_accounts
        WHERE code = '1'
    ),    
    '10',
    'Cash and Cash Equivalents',
    FALSE,
    NULL,
    1,  -- BALANCE_SHEET
    1,  -- ASSET
    1   -- DE
);

-- CASH Eur
INSERT INTO gl_accounts (
    account_id,
    parent,
    code,
    description,
    is_postable,
    currency,
    statement_type,
    nature,
    balance
)
VALUES (
    nextval('gl_accounts_s'),
    (
        SELECT g.account_id
        FROM gl_accounts g
        WHERE g.code = '10'
    ),
    '100010',
    'Cash and Cash Equivalents - Base Currency (EUR)',
    TRUE,
    (
        SELECT c.id_currency
        FROM currency c
        WHERE c.iso_code = 'EUR'
    ),
    1,  -- BALANCE_SHEET
    1,  -- ASSET
    1   -- DEBIT
);