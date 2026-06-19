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

