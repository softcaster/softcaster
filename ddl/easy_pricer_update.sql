
UPDATE master_data SET accrual_schedule_type=100;
ALTER TABLE bond_future_master_data DROP CONSTRAINT IF EXISTS fk_master_data;
ALTER TABLE bond_future_master_data DROP COLUMN master_data;

ALTER TABLE currency ADD COLUMN daycount INTEGER;
UPDATE currency SET daycount = (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1);
ALTER TABLE currency ALTER COLUMN daycount SET NOT NULL;
ALTER TABLE currency ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount)
        REFERENCES daycount(id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE currency ADD COLUMN business_days INTEGER NOT NULL DEFAULT 2;
ALTER TABLE future_master_data ADD COLUMN exchange_contract_code VARCHAR(25) NOT NULL DEFAULT '';

select code,id_master_data from master_data where id_master_data not in(SELECT master_data FROM cash_flow_item where amount=100);
select * from cash_flow_item where master_data=22 order by end_date DESC
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


-- pg_dump -U easypricer -d easypricer > easypricer.pgsql
-- Get-Content .\easypricer.pgsql | Set-Content -Encoding UTF8 .\easypricer_fixed.pgsql
-- psql -U easypricer -d easypricer -f .\easypricer_fixed.pgsql

