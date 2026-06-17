DELETE FROM gl_accounts;

ALTER SEQUENCE gl_accounts_s
    RESTART WITH 1;

-- ==========================================
-- LEVEL 1: MACRO CLASSES (ROOT NODES)
-- ==========================================
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'), NULL, '1', 'Assets', FALSE, NULL, (
            SELECT
                statement_type_id
            FROM
                financial_statement_types
            WHERE
                code = 'BALANCE_SHEET'), (
                SELECT
                    nature_id
                FROM
                    account_natures
                WHERE
                    code = 'ASSET'), (
                    SELECT
                        balance_id
                    FROM
                        normal_balances
                    WHERE
                        code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'), NULL, '2', 'Liabilities', FALSE, NULL, (
            SELECT
                statement_type_id
            FROM
                financial_statement_types
            WHERE
                code = 'BALANCE_SHEET'), (
                SELECT
                    nature_id
                FROM
                    account_natures
                WHERE
                    code = 'LIABILITY'), (
                    SELECT
                        balance_id
                    FROM
                        normal_balances
                    WHERE
                        code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'), NULL, '3', 'Equity', FALSE, NULL, (
            SELECT
                statement_type_id
            FROM
                financial_statement_types
            WHERE
                code = 'BALANCE_SHEET'), (
                SELECT
                    nature_id
                FROM
                    account_natures
                WHERE
                    code = 'EQUITY'), (
                    SELECT
                        balance_id
                    FROM
                        normal_balances
                    WHERE
                        code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'), NULL, '4', 'Income', FALSE, NULL, (
            SELECT
                statement_type_id
            FROM
                financial_statement_types
            WHERE
                code = 'INCOME_STATEMENT'), (
                SELECT
                    nature_id
                FROM
                    account_natures
                WHERE
                    code = 'INCOME'), (
                    SELECT
                        balance_id
                    FROM
                        normal_balances
                    WHERE
                        code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'), NULL, '5', 'Expenses', FALSE, NULL, (
            SELECT
                statement_type_id
            FROM
                financial_statement_types
            WHERE
                code = 'INCOME_STATEMENT'), (
                SELECT
                    nature_id
                FROM
                    account_natures
                WHERE
                    code = 'EXPENSE'), (
                    SELECT
                        balance_id
                    FROM
                        normal_balances
                    WHERE
                        code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'), NULL, '6', 'Off-Balance Commitments', FALSE, NULL, (
            SELECT
                statement_type_id
            FROM
                financial_statement_types
            WHERE
                code = 'OFF_BALANCE_SHEET'), (
                SELECT
                    nature_id
                FROM
                    account_natures
                WHERE
                    code = 'MEMORANDUM'), (
                    SELECT
                        balance_id
                    FROM
                        normal_balances
                    WHERE
                        code = 'DEBIT'));

-- ==========================================
-- LEVEL 2: GROUPS (PARENT LOGIC)
-- ==========================================
-- Groups for Assets (1)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '1'), '10', 'Cash and Cash Equivalents', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'ASSET'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '1'), '11', 'Short-Term Deposits', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'ASSET'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '1'), '12', 'Margin Accounts and Broker Receivables', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'ASSET'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '1'), '13', 'Financial Assets at FVTPL', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'ASSET'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '1'), '14', 'Financial Derivatives - Assets', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'ASSET'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '1'), '19', 'Accruals and Receivables', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'ASSET'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

-- Groups for Liabilities (2)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '2'), '21', 'Short-Term Borrowings', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'LIABILITY'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '2'), '24', 'Financial Derivatives & Settlement Liabilities', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'LIABILITY'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'CREDIT'));

-- Groups for Equity (3)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '3'), '30', 'Capital and Reserves', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'EQUITY'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'CREDIT'));

-- Groups for Income (4)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '4'), '40', 'Gains on Financial Derivatives', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'INCOME_STATEMENT'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'INCOME'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '4'), '41', 'Foreign Exchange Gains', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'INCOME_STATEMENT'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'INCOME'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '4'), '42', 'Interest and Dividend Income', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'INCOME_STATEMENT'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'INCOME'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'CREDIT'));

-- Groups for Expenses (5)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '5'), '50', 'Losses on Financial Derivatives', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'INCOME_STATEMENT'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'EXPENSE'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '5'), '51', 'Foreign Exchange Losses', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'INCOME_STATEMENT'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'EXPENSE'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '5'), '52', 'Interest Expenses and Trading Losses', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'INCOME_STATEMENT'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'EXPENSE'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '5'), '58', 'Trading Fees and Execution Costs', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'INCOME_STATEMENT'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'EXPENSE'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

-- Groups for Off-Balance (6)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '6'), '60', 'Financial Commitments', FALSE, NULL, (
                SELECT
                    statement_type_id
                FROM
                    financial_statement_types
                WHERE
                    code = 'OFF_BALANCE_SHEET'), (
                    SELECT
                        nature_id
                    FROM
                        account_natures
                    WHERE
                        code = 'MEMORANDUM'), (
                        SELECT
                            balance_id
                        FROM
                            normal_balances
                        WHERE
                            code = 'DEBIT'));

-- Children of Cash (10)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '10'), '100010', 'Cash and Cash Equivalents - Base Currency (EUR)', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '10'), '100015', 'Cash and Cash Equivalents - Foreign Currency (USD)', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '10'), '100020', 'Petty Cash', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Children of Deposits (11)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '11'), '110010', 'Short-Term Time Deposits - EUR', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '11'), '110015', 'Short-Term Time Deposits - USD', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Children of Margins (12)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '12'), '120050', 'Initial Margin Deposit - EUR', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '12'), '120055', 'Initial Margin Deposit - USD', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '12'), '120060', 'Variation Margin Account - EUR', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '12'), '120065', 'Variation Margin Account - USD', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Children of Trading (13)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '13'), '130010', 'Debt Securities - Sovereign Bonds (EUR)', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '13'), '130015', 'Debt Securities - U.S. Treasuries (USD)', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '13'), '130020', 'Equity Securities - Domestic Shares (EUR)', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                id_currency
            FROM
                currency
            WHERE
                iso_code = 'USD'), '130025', 'Equity Securities - International Shares (USD)', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Children of Derivatives Assets (14)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '14'), '140010', 'FX Forward Contracts - Asset', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '14'), '140020', 'Options Premium Purchased', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '14'), '140030', 'FX Future Contracts - Asset', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '14'), '140040', 'Financial Futures - Asset', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Children of Receivables (19)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '19'), '190010', 'Accrued Interest Receivable - Debt Securities', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '19'), '190020', 'Dividends Receivable', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'ASSET'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Children of Borrowings (21)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '21'), '210010', 'Bank Overdrafts - EUR', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '21'), '210015', 'Bank Overdrafts - USD', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '21'), '210020', 'Short-Term Repo Loans', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

-- Children of Derivatives Liabilities (24)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '24'), '240010', 'FX Forward Contracts - Liability', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '24'), '240020', 'Options Premium Written', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '24'), '240030', 'FX Future Contracts - Liability', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '24'), '240040', 'Financial Futures - Liability', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '24'), '240050', 'Due to Brokers / Settlement Liabilities', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '24'), '240090', 'Currency Clearing Account', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'LIABILITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

-- Children of Capital (30)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '30'), '300010', 'Share Capital', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EQUITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '30'), '300050', 'Retained Earnings', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EQUITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '30'), '300080', 'FX Translation Reserve', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EQUITY'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

-- Children of Trading Gains (40)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '40'), '400010', 'Realized Gain on Financial Derivatives', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '40'), '400020', 'Unrealized Gain on Financial Derivatives', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

-- Figli di FX Gains (41)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '41'), '410010', 'Realized Foreign Exchange Gains', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '41'), '410020', 'Unrealized Foreign Exchange Gains', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

-- Figli di Financial Income (42)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '42'), '420010', 'Interest Income - Bank & Short-Term Deposits', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '42'), '420020', 'Interest Income - Sovereign Debt (Coupons)', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '42'), '420050', 'Realized Gain on Debt Securities', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '42'), '420060', 'Realized Gain on Equity Securities', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'INCOME'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

-- Figli di Trading Losses (50)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '50'), '500010', 'Realized Loss on Financial Derivatives', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '50'), '500020', 'Unrealized Loss on Financial Derivatives', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Figli di FX Losses (51)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '51'), '510010', 'Realized Foreign Exchange Losses', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '51'), '510020', 'Unrealized Foreign Exchange Losses', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Figli di Financial Expenses (52)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '52'), '520010', 'Interest Expense on Borrowings / Repo', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '52'), '520050', 'Realized Loss on Debt Securities', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '52'), '520060', 'Realized Loss on Equity Securities', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Figli di Fees (58)
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '58'), '580010', 'Brokerage and Execution Fees', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '58'), '580020', 'Clearing and Exchange Fees', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '58'), '580030', 'Custody and Safe-Keeping Fees', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'EUR'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'INCOME_STATEMENT'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'EXPENSE'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

-- Figli di Commitments (60) - Conti d'Ordine / Memorandun
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '60'), '600010', 'Financial Commitments - Long Futures', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'OFF_BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'MEMORANDUM'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '60'), '600015', 'Financial Commitments - Short Futures', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'OFF_BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'MEMORANDUM'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '60'), '600020', 'Financial Commitments - Forward Currency Purchase', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'OFF_BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'MEMORANDUM'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '60'), '690030', 'Counterpart for Financial Commitments', TRUE, (
                SELECT
                    id_currency
                FROM
                    currency
                WHERE
                    iso_code = 'USD'), (
                    SELECT
                        statement_type_id
                    FROM
                        financial_statement_types
                    WHERE
                        code = 'OFF_BALANCE_SHEET'), (
                        SELECT
                            nature_id
                        FROM
                            account_natures
                        WHERE
                            code = 'MEMORANDUM'), (
                            SELECT
                                balance_id
                            FROM
                                normal_balances
                            WHERE
                                code = 'CREDIT'));

