-- ALTER SEQUENCE daycount_s RESTART WITH 1;
-- ----------------------------------------------------------------------------
-- daycount
-- ----------------------------------------------------------------------------
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'NASD_30_360','30/360 NASD');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'ACT_360','Actual/360');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'ACT_365','Actual/365');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'ACT_ACT_ISDA','Actual/Actual ISDA');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'ACT_ACT_ICMA','Actual/Actual ICMA');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'EUR_30_360','30/360 EUR');

-- ----------------------------------------------------------------------------
-- roll_convention
-- ----------------------------------------------------------------------------
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'PREVIOUS','Previus');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'PREVIOUS-MODIFIED','Previus Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'FORWARD','Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'FORWARD-MODIFIED','Modified Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (100,'UNADJUSTED','Unadjusted');

-- ----------------------------------------------------------------------------
-- frequency
-- ----------------------------------------------------------------------------
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'ANNUAL','Annual',1);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'SEMI-ANNUAL','Semi Annual',2);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'E4M','Every 4 months',3);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'QUARTERLY','Quarterly',4);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'BI-MONTHLY','Every two months',6);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'MONTHLY','Monthly',12);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (100,'NONE','None',0);

-- ----------------------------------------------------------------------------
-- form
-- ----------------------------------------------------------------------------
INSERT INTO form(id_form,code, description) VALUES (nextval('form_s'),'BEARER','Bearer');
INSERT INTO form(id_form,code, description) VALUES (nextval('form_s'),'REGISTERED','Registered');
INSERT INTO form(id_form,code, description) VALUES (nextval('form_s'),'BOOK-ENTRY-BOND','Book-entry Bond');

-- ----------------------------------------------------------------------------
-- type_of_interest
-- ----------------------------------------------------------------------------
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (nextval('type_of_interest_s'),'FIXED','Fixed-Rate');
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (nextval('type_of_interest_s'),'FLOATING','Floating-Rate');
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (nextval('type_of_interest_s'),'ZERO-COUPON','Zero-Coupon');
-- Inflation-Linked Interest Rate (Tasso indicizzato all'inflazione)
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (nextval('type_of_interest_s'),'INFLATION','Inflation-Linked');
-- Convertible Bond (Obbligazione Convertibile): 
-- Il tasso (spesso chiamato coupon rate) è generalmente più basso rispetto alle obbligazioni standard 
-- perché l'investitore riceve in cambio il diritto di convertire il titolo in azioni della società.
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (nextval('type_of_interest_s'),'CONVERTIBLE','Convertible');
-- Il termine si applica solitamente a obbligazioni (callable bonds)
-- L'emittente ha la facoltà (ma non l'obbligo) di rimborsare il capitale ai creditori prima della scadenza
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (nextval('type_of_interest_s'),'CALLABLE','Callable');
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (100,'NONE','None');

-- ----------------------------------------------------------------------------
-- accrual_schedule_type
-- ----------------------------------------------------------------------------
INSERT INTO accrual_schedule_type(id_accrual_schedule_type,code, description) VALUES (100,'NONE','None');

-- ----------------------------------------------------------------------------
-- compounding
-- ----------------------------------------------------------------------------
INSERT INTO compounding(id_compounding,code, description) VALUES (nextval('compounding_s'),'SIMPLE','Linear');
INSERT INTO compounding(id_compounding,code, description) VALUES (nextval('compounding_s'),'COMPOUNDED','Compounded');
INSERT INTO compounding(id_compounding,code, description) VALUES (nextval('compounding_s'),'CONTINUOUS','Continuous');
INSERT INTO compounding(id_compounding,code, description) VALUES (nextval('compounding_s'),'SIMPLE_THEN_COMPOUNDED','Linear then Compounded');

-- ----------------------------------------------------------------------------
-- amortization_schedule
-- ----------------------------------------------------------------------------
-- Piano Francese
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (nextval('amortization_schedule_s'),'SAS','Standard Amortization Schedule');
-- Piano Italiano
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (nextval('amortization_schedule_s'),'SLP','Straight-line Principal');
-- Piano Bullet
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (nextval('amortization_schedule_s'),'IOL','Interest Only Loan');
-- None
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (100,'NONE','None');


-- ----------------------------------------------------------------------------
-- super_class
-- ----------------------------------------------------------------------------
INSERT INTO super_class(id_super_class,code, description) VALUES (nextval('super_class_s'),'EQU','Equities');
-- titoli di debito che prevedono un obbligo contrattuale di pagamento di interessi e rimborso del capitale, 
-- differenziandosi dalle azioni.
INSERT INTO super_class(id_super_class,code, description) VALUES (nextval('super_class_s'),'FIN','Fixed Income');
INSERT INTO super_class(id_super_class,code, description) VALUES (nextval('super_class_s'),'CCE',' Cash and Cash Equivalents (Money Market)');
INSERT INTO super_class(id_super_class,code, description) VALUES (nextval('super_class_s'),'DER',' Derivatives');
INSERT INTO super_class(id_super_class,code, description) VALUES (nextval('super_class_s'),'FX',' Forex');

-- ----------------------------------------------------------------------------
-- asset_class
-- ----------------------------------------------------------------------------
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'FRN','Floating Rate Notes',(SELECT id_super_class FROM super_class WHERE code='FIN'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'XRN','Fixed Rate Notes',(SELECT id_super_class FROM super_class WHERE code='FIN'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'FRB','Floating Rate Bonds',(SELECT id_super_class FROM super_class WHERE code='FIN'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'XRB','Fixed Rate Bonds',(SELECT id_super_class FROM super_class WHERE code='FIN'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'BLL','Bills',(SELECT id_super_class FROM super_class WHERE code='FIN'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'FRM','Floating Rate Mortages',(SELECT id_super_class FROM super_class WHERE code='CCE'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'XRM','Fixed Rate Mortages',(SELECT id_super_class FROM super_class WHERE code='CCE'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'BFU','Bond Futures',(SELECT id_super_class FROM super_class WHERE code='DER'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'FSP','Spot Forex',(SELECT id_super_class FROM super_class WHERE code='FX'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'FFW','Forex Forward',(SELECT id_super_class FROM super_class WHERE code='FX'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'FFU','Forex Future',(SELECT id_super_class FROM super_class WHERE code='FX'));
INSERT INTO asset_class(id_asset_class,code, description, super_class) VALUES (nextval('asset_class_s'),'MFU','MM Future',(SELECT id_super_class FROM super_class WHERE code='CCE'));

-- ----------------------------------------------------------------------------
-- settlement_type - tipo consegna
-- ----------------------------------------------------------------------------
INSERT INTO settlement_type(id_settlement_type,code, description) VALUES (nextval('settlement_type_s'),'PHYSICAL','Physical Settlement');
INSERT INTO settlement_type(id_settlement_type,code, description) VALUES (nextval('settlement_type_s'),'CASH','Cash Settlement');

-- ----------------------------------------------------------------------------
-- txn_status
-- ----------------------------------------------------------------------------
-- in processanto
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'PENDING','Pending');
-- Da validare
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'VALIDATING','Validating');
-- processata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'EXECUTED','Executed');
-- rifiutata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'REJECTED','Rejected');
-- da modificare
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'TO_AMEND','To Amend');
-- modificata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'AMENDED','Amended');
-- da cancellare
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'TO_CANCEL','To Cancel');
-- cancellata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'CANCELLED','Cancelled');
-- restart transazione rejected
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'RESTARTING','Restartin');


-- ----------------------------------------------------------------------------
-- txn_status
-- ----------------------------------------------------------------------------
INSERT INTO txn_component_types(component_type_id,code, description) VALUES (1, 'BROKER_FEE', 'Broker Transaction and Execution Fees');
INSERT INTO txn_component_types(component_type_id,code, description) VALUES (2, 'EXCHANGE_FEE', 'CME / Clearing House Regulatory Fees');
INSERT INTO txn_component_types(component_type_id,code, description) VALUES (3, 'INITIAL_MARGIN', 'Initial Margin Deposit Requirement');
INSERT INTO txn_component_types(component_type_id,code, description) VALUES (4, 'MAINTENANCE_MARGIN', 'Maintenance Margin Requirement');
INSERT INTO txn_component_types(component_type_id,code, description) VALUES (5, 'OPTION_PREMIUM', 'Option Premium Paid or Written');

-- ----------------------------------------------------------------------------
-- counterparty_type
-- ----------------------------------------------------------------------------
-- Persone fisiche
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (nextval('counterparty_type_s'),'RCLIENT','Retail Clients');
-- Fondi d'investimento, fondi pensione o assicurazioni
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (nextval('counterparty_type_s'),'ICLIENT','Institutional Clients');
-- Aziende che usano il software per hedging 
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (nextval('counterparty_type_s'),'CORPORATE','Corporate');
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (nextval('counterparty_type_s'),'BANK','Bank');
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (nextval('counterparty_type_s'),'CHOUSE','Clearing Houses');
-- Banche Depositarie
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (nextval('counterparty_type_s'),'CUSTODIAN','Custodians');

-- ----------------------------------------------------------------------------
-- calendar
-- ----------------------------------------------------------------------------
INSERT INTO calendar(id_calendar,code, description) VALUES (nextval('calendar_s'),'EUR','Euro Area Calendar');
INSERT INTO calendar(id_calendar,code, description) VALUES (nextval('calendar_s'),'USD','Usd Area Calendar');
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
-- holiday
-- ----------------------------------------------------------------------------
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),1,8,'New Year Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),3,4,'Good Friday');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),6,4,'Easter Monday');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),1,5,'Labor Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),25,12,'Christmas Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),26,12,'Christmas Holiday');

INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),1,1,'New Year Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),19,1,'Martin Luther King Jr. Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),16,2,'Presidents Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),3,4,'Good Friday');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),25,5,'Memorial Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),19,6,'Juneteenth National Independence Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),3,7,'Independence Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),7,9,'Labor Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),12,10,'Columbus Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),11,11,'Veterans Day');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),26,11,'Thanksgiving');
INSERT INTO holiday(id_holiday,calendar, holiday_day,holiday_month,description) VALUES (nextval('holiday_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),25,12,'Christmas Day');

-- ----------------------------------------------------------------------------
-- currency
-- ----------------------------------------------------------------------------
INSERT INTO currency(id_currency,calendar,daycount,iso_code,currency_numeric_code,description) 
    VALUES (nextval('currency_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1),'EUR',978,'EUR');
INSERT INTO currency(id_currency,calendar,daycount,iso_code,currency_numeric_code,description) 
    VALUES (nextval('currency_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),
    (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1),'USD',840,'USD');
INSERT INTO currency(id_currency,calendar,daycount,iso_code,currency_numeric_code,description) 
    VALUES (nextval('currency_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1),'GBP',826,'GBP');
INSERT INTO currency(id_currency,calendar,daycount,iso_code,currency_numeric_code,description) 
    VALUES (nextval('currency_s'),(SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1),'CHF',576,'CHF');
INSERT INTO currency(id_currency,calendar,daycount,iso_code,currency_numeric_code,description) 
    VALUES (nextval('currency_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),
    (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1),'CAD',124,'CAD');
INSERT INTO currency(id_currency,calendar,daycount,iso_code,currency_numeric_code,description) 
    VALUES (nextval('currency_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),
    (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1),'AUD',36,'AUD');
INSERT INTO currency(id_currency,calendar,daycount,iso_code,currency_numeric_code,description) 
    VALUES (nextval('currency_s'),(SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),
    (SELECT id_daycount FROM daycount WHERE code='ACT_360' LIMIT 1),'JPY',392,'JPY');

-- ----------------------------------------------------------------------------
-- country
-- ----------------------------------------------------------------------------
INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,
    currency,calendar) 
    VALUES (nextval('country_s'),'Italy','Italy','IT','ITA',381,
    (SELECT id_currency FROM currency WHERE iso_code='EUR' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1));
INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,
    currency,calendar) 
    VALUES (nextval('country_s'),'USA','United States of America (the)','US','USA',840,
    (SELECT id_currency FROM currency WHERE iso_code='USD' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1));
INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,
    currency,calendar) 
    VALUES (nextval('country_s'),'GBR','United Kingdom of Great Britain and Northern Ireland (the)','GB','GBR',826,
    (SELECT id_currency FROM currency WHERE iso_code='GBP' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1));
INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,
    currency,calendar) 
    VALUES (nextval('country_s'),'CHE','Swiss Confederation (the)','CH','CHE',756,
    (SELECT id_currency FROM currency WHERE iso_code='CHF' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1));
INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,
    currency,calendar) 
    VALUES (nextval('country_s'),'CAN','Canada','CA','CAN',124,
    (SELECT id_currency FROM currency WHERE iso_code='CAD' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1));
INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,
    currency,calendar) 
    VALUES (nextval('country_s'),'AUS','Australia','AU','AUS',36,
    (SELECT id_currency FROM currency WHERE iso_code='AUD' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1));
INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,
    currency,calendar) 
    VALUES (nextval('country_s'),'JP','Japan','JP','JPN',392,
    (SELECT id_currency FROM currency WHERE iso_code='JPY' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1));

-- ----------------------------------------------------------------------------
-- issuer
-- ----------------------------------------------------------------------------
INSERT INTO issuer(id_issuer,short_issuer_name,long_issuer_name,country) VALUES(nextval('issuer_s'),'REP ITA','Repubblica Italiana',
    (SELECT id_country FROM country WHERE alfa_3_code='ITA' LIMIT 1));

-- ----------------------------------------------------------------------------
-- yield_curve
-- ----------------------------------------------------------------------------
INSERT INTO yield_curve (id_yield_curve, code, description, currency, calendar, compounding, provider) VALUES
(nextval('yield_curve_s'), 'TERMESTR', 'Estr Averages Rates', 
    (SELECT id_currency FROM currency WHERE iso_code='EUR' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_compounding FROM compounding WHERE code='SIMPLE' LIMIT 1),
    'CmeGroupProvider'),
(nextval('yield_curve_s'), 'TERMSOFR', 'Sofr Averages Rates', 
    (SELECT id_currency FROM currency WHERE iso_code='USD' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),
    (SELECT id_compounding FROM compounding WHERE code='SIMPLE' LIMIT 1),
    'CmeGroupProvider'),
(nextval('yield_curve_s'), 'ITYIELD', 'Italy - Government Bonds', 
    (SELECT id_currency FROM currency WHERE iso_code='EUR' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_compounding FROM compounding WHERE code='COMPOUNDED' LIMIT 1),
    'InvestingComProvider'),
(nextval('yield_curve_s'), 'USYIELD', 'United States - Government Bonds', 
    (SELECT id_currency FROM currency WHERE iso_code='USD' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='USD' LIMIT 1),
    (SELECT id_compounding FROM compounding WHERE code='COMPOUNDED' LIMIT 1),
    'InvestingComProvider'),
(nextval('yield_curve_s'), 'EURIBOR', 'Euribor Rates', 
    (SELECT id_currency FROM currency WHERE iso_code='EUR' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_compounding FROM compounding WHERE code='SIMPLE' LIMIT 1),
    ''),
(nextval('yield_curve_s'), 'FMIRS', 'Eurirs Rates', 
    (SELECT id_currency FROM currency WHERE iso_code='EUR' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_compounding FROM compounding WHERE code='COMPOUNDED' LIMIT 1),
    'Sole24hProvider'),
(nextval('yield_curve_s'), 'ECBYC', 'European Central Bank Yield Curve',
    (SELECT id_currency FROM currency WHERE iso_code='EUR' LIMIT 1),
    (SELECT id_calendar FROM calendar WHERE code='EUR' LIMIT 1),
    (SELECT id_compounding FROM compounding WHERE code='CONTINUOUS' LIMIT 1),
    '');
