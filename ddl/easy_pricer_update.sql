ALTER TABLE master_data 
ADD COLUMN description VARCHAR(255) DEFAULT '';

UPDATE master_data
SET description = smd.issue_description
FROM security_master_data smd
WHERE master_data.id_master_data = smd.id_master_data;

ALTER TABLE finacial_txn RENAME to financial_txn;
ALTER TABLE financial_txn OWNER TO easypricer;
ALTER TABLE financial_txn RENAME COLUMN id_finacial_txn TO id_financial_txn;
ALTER SEQUENCE finacial_txn_s RENAME TO financial_txn_s;

ALTER TABLE financial_txn ADD COLUMN ref_id INTEGER;
-- 2. Popola la colonna con i valori esistenti
UPDATE financial_txn SET ref_id = id_financial_txn;
-- 3. Rende la colonna obbligatoria per il futuro
ALTER TABLE financial_txn ALTER COLUMN ref_id SET NOT NULL;

ALTER TABLE instrument_quote ADD COLUMN quote provider VARCHAR(50) NOT NULL DEFAULT='EuroNextProvider';
UPDATE instrument_quote 
SET code = code || '-MOTX'
WHERE provider = 'EuroNextProvider';

UPDATE master_data SET accrual_schedule_type=100;
ALTER TABLE bond_future_master_data DROP CONSTRAINT IF EXISTS fk_master_data;
ALTER TABLE bond_future_master_data DROP COLUMN master_data;
ALTER TABLE instrument_quote ADD COLUMN provider VARCHAR(50) NOT NULL DEFAULT '';
ALTER TABLE currency ADD COLUMN daycount INTEGER;
UPDATE currency SET daycount = (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1);
ALTER TABLE currency ALTER COLUMN daycount SET NOT NULL;

ALTER TABLE position_master_data ADD COLUMN portfolio INTEGER NOT NULL;
ALTER TABLE position_master_data ADD CONSTRAINT fk_portfolio FOREIGN KEY (portfolio)
        REFERENCES portfolio_master_data(id_portfolio) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE counterparty DROP COLUMN leiCode;
ALTER TABLE counterparty ADD COLUMN lei_code VARCHAR(50) DEFAULT '';

ALTER TABLE future_master_data ADD COLUMN exchange_contract_code VARCHAR(25) NOT NULL DEFAULT '';

select code,id_master_data from master_data where id_master_data not in(SELECT master_data FROM cash_flow_item where amount=100);
select * from cash_flow_item where master_data=22 order by end_date DESC


ALTER TABLE yield_curve_item ADD COLUMN compounding SMALLINT NOT NULL DEFAULT 1;
ALTER TABLE yield_curve_item ADD COLUMN daycount SMALLINT;
-- 2. Popola la colonna con i valori esistenti
UPDATE yield_curve_item SET daycount = (SELECT id_daycount FROM daycount WHERE code='ACT_365');
-- 3. Rende la colonna obbligatoria per il futuro
ALTER TABLE yield_curve_item ALTER COLUMN daycount SET NOT NULL;
ALTER TABLE yield_curve_item ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount)
        REFERENCES daycount(id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION;

------------------------------------------------------------------------------
-- issuer
-- ----------------------------------------------------------------------------
INSERT INTO issuer(id_issuer,short_issuer_name,long_issuer_name, country) VALUES (nextval('issuer_s'),'REP ITA','REPUBBLICA ITALIANA',(SELECT id_country FROM country WHERE alfa_3_code='ITA'));
INSERT INTO issuer(id_issuer,short_issuer_name,long_issuer_name, country) VALUES (nextval('issuer_s'),'MEDIOBANCA','MEDIOBANCA BANCA DI CREDITO FINANZIARIO SOCIETA  PER AZIONI',(SELECT id_country FROM country WHERE alfa_3_code='ITA'));
INSERT INTO issuer(id_issuer,short_issuer_name,long_issuer_name, country) VALUES (nextval('issuer_s'),'MARKET','MARKET',(SELECT id_country FROM country WHERE alfa_3_code='ITA'));
------------------------------------------------------------------------------

------------------------------------------------------------------------------
-- yield_curve
-- ----------------------------------------------------------------------------
INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) 
    VALUES (nextval('yield_curve_s'),'ECBYC','European Central Bank Yield Curve',
    (SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_calendar FROM calendar WHERE code='EUR'),3);

INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) 
    VALUES (nextval('yield_curve_s'),'ITAYC','Investing.com - Italy - Government Bonds',
    (SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_calendar FROM calendar WHERE code='EUR'),1);

INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) 
    VALUES (nextval('yield_curve_s'),'USAYC','Investing.com - United States - Government Bonds',
    (SELECT id_currency FROM currency WHERE iso_code='USD'),
    (SELECT id_calendar FROM calendar WHERE code='USD'),1);

INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) 
    VALUES (nextval('yield_curve_s'),'EURIBOR','Euribor Rates',
    (SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_calendar FROM calendar WHERE code='EUR'),0);

INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) 
    VALUES (nextval('yield_curve_s'),'EURIRS','Eurirs Rates',
    (SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_calendar FROM calendar WHERE code='EUR'),1);

INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) 
    VALUES (nextval('yield_curve_s'),'SOFR','SOFR Averages Rates',
    (SELECT id_currency FROM currency WHERE iso_code='USD'),
    (SELECT id_calendar FROM calendar WHERE code='USD'),1);
INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) 
    VALUES (nextval('yield_curve_s'),'ESTER','ESTER Averages Rates',
    (SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_calendar FROM calendar WHERE code='EUR'),1);

delete from position_detail;
delete from financial_txn;

-- pg_dump -U easypricer -d easypricer > easypricer.pgsql
-- Get-Content .\easypricer.pgsql | Set-Content -Encoding UTF8 .\easypricer_fixed.pgsql
-- psql -U easypricer -d easypricer -f .\easypricer_fixed.pgsql

