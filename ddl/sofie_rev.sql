CREATE TABLE journal_entry_status (
    entry_status_id integer NOT NULL,
    code varchar(30) NOT NULL,
    description varchar(100),
    PRIMARY KEY (entry_status_id)
);
ALTER TABLE journal_entry_status OWNER TO sofie;
-- journal_entry_status
INSERT INTO journal_entry_status VALUES (1,'UNCONSOLIDATED','Unconsolidated');
INSERT INTO journal_entry_status VALUES (2,'CONSOLIDATED','Consolidated');
INSERT INTO journal_entry_status VALUES (3,'ERROR','Error');


alter table journal_entries add column entry_status integer NOT NULL default 1;
alter table journal_entries add CONSTRAINT fk_je_status FOREIGN KEY (entry_status) REFERENCES journal_entry_status (entry_status_id);
alter table journal_entries drop column entry_date;

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES
    (nextval('gl_accounts_s'),
        (
            SELECT
                account_id
            FROM
                gl_accounts
            WHERE
                code = '24'), '240060', 'FX Settlement Payable', TRUE, (
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
                code = '19'), '190030', 'FX Settlement Receivable', TRUE, (
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

----------------------------------------------------------------------------------------------
alter table position_detail add column accrual numeric(15, 5) NOT NULL DEFAULT 0;
alter table position_detail add column ytm numeric(15, 5) NOT NULL DEFAULT 0;
alter table position_detail add column duration numeric(15, 5) NOT NULL DEFAULT 0;
alter table position_detail add column time_to_maturity numeric(15, 5) NOT NULL DEFAULT 0;
alter table position_detail add column mod_duration numeric(15, 5) NOT NULL DEFAULT 0;
alter table position_detail add column theoretical_price numeric(15, 5) NOT NULL DEFAULT 0;

----------------------------------------------------------------------------------------------
alter table position_detail drop column accrual;
alter table position_detail add column buy_accrual numeric(15, 5) NOT NULL DEFAULT 0;
alter table position_detail add column sell_accrual numeric(15, 5) NOT NULL DEFAULT 0;

----------------------------------------------------------------------------------------------
alter table position_detail drop column multiplier;

----------------------------------------------------------------------------------------------
alter table financial_txn add column txn_status_pre_elab integer NOT NULL DEFAULT 1;

----------------------------------------------------------------------------------------------
alter table instrument_valuation add column valuation_date date NOT NULL DEFAULT NOW();
INSERT INTO txn_status(id_txn_status,code, description) VALUES (11,'POSTED','Posted');
INSERT INTO txn_status(id_txn_status,code, description) VALUES (12,'SETTLED','Settled');
delete from txn_status where id_txn_status in(11,12);

----------------------------------------------------------------------------------------------
alter table position_detail add column official_date date NOT NULL DEFAULT NOW();
update position_detail set official_date=(select official_date from system_business_calendar where sbc_id=1);

----------------------------------------------------------------------------------------------
ALTER TABLE position_detail 
ADD COLUMN last_mtm_executed TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() - INTERVAL '1 hour';

----------------------------------------------------------------------------------------------
update position_detail set last_mtm_executed=now() where last_mtm_executed is null;

----------------------------------------------------------------------------------------------
INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '9'), '120090', 'Currency Position - EUR', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'ASSET'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '9'), '120091', 'Currency Position - USD', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'ASSET'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '9'), '120092', 'Currency Position - CHF', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'ASSET'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '14'), '240091', 'Currency Position Control - USD', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'LIABILITY'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '14'), '240092', 'Currency Position Control - CHF', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'LIABILITY'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '40'), '400025', 'Realized Gain on Financial Derivatives - EUR', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'EUR'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'INCOME_STATEMENT'), 
    (SELECT nature_id FROM account_natures WHERE code = 'INCOME'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '40'), '400026', 'Realized Gain on Financial Derivatives - USD', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'INCOME_STATEMENT'), 
    (SELECT nature_id FROM account_natures WHERE code = 'INCOME'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'CREDIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '13'), '130055', 'FX Spot Contracts - USD', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'USD'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'ASSET'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '13'), '130056', 'FX Spot Contracts - CHF', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'ASSET'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'DEBIT'));

INSERT INTO gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance)
VALUES(nextval('gl_accounts_s'),(SELECT account_id FROM gl_accounts WHERE code = '13'), '130056', 'FX Spot Contracts - CHF', TRUE, 
    (SELECT id_currency FROM currency WHERE iso_code = 'CHF'), 
    (SELECT statement_type_id FROM financial_statement_types WHERE code = 'BALANCE_SHEET'), 
    (SELECT nature_id FROM account_natures WHERE code = 'ASSET'), 
    (SELECT balance_id FROM normal_balances WHERE code = 'DEBIT'));
