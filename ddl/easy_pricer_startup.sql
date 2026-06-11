-------------------------------------------------------------------------------
-- STARTUP
-------------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
-- daycount
-- ----------------------------------------------------------------------------
INSERT INTO daycount(id_daycount,code, description) VALUES (1,'NASD_30_360','30/360 NASD');
INSERT INTO daycount(id_daycount,code, description) VALUES (2,'EUR_30_360','30/360 EUR');
INSERT INTO daycount(id_daycount,code, description) VALUES (3,'ACT_360','Actual/360');
INSERT INTO daycount(id_daycount,code, description) VALUES (4,'ACT_365','Actual/365');
INSERT INTO daycount(id_daycount,code, description) VALUES (5,'ACT_ACT_ICMA','Actual/Actual');

-- ----------------------------------------------------------------------------
-- roll_convention
-- ----------------------------------------------------------------------------
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (1,'PREVIOUS','Previus');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (2,'PREVIOUS-MODIFIED','Previus Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (3,'FORWARD','Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (4,'FORWARD-MODIFIED','Modified Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (100,'RC_NONE','None');

-- ----------------------------------------------------------------------------
-- frequency
-- ----------------------------------------------------------------------------
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (1,'ANNUAL','Annual',1);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (2,'SEMI-ANNUAL','Semi Annual',2);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (3,'E4M','Every 4 months',3);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (4,'QUARTERLY','Quarterly',4);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (5,'BI-MONTHLY','Every two months',6);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (6,'MONTHLY','Monthly',12);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (100,'CUSTOM','Custom',0);

-- ----------------------------------------------------------------------------
-- form
-- ----------------------------------------------------------------------------
INSERT INTO form(id_form,code, description) VALUES (1,'BEARER','Bearer');
INSERT INTO form(id_form,code, description) VALUES (2,'REGISTERED','Registered');
INSERT INTO form(id_form,code, description) VALUES (3,'BOOK-ENTRY-BOND','Book-entry Bond');

-- ----------------------------------------------------------------------------
-- type_of_interest
-- ----------------------------------------------------------------------------
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (1,'FIXED','Fixed-Rate');
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (2,'FLOATING','Floating-Rate');
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (3,'ZERO-COUPON','Zero-Coupon');
-- Inflation-Linked Interest Rate (Tasso indicizzato all'inflazione)
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (4,'INFLATION','Inflation-Linked');
-- Convertible Bond (Obbligazione Convertibile): 
-- Il tasso (spesso chiamato coupon rate) è generalmente più basso rispetto alle obbligazioni standard 
-- perché l'investitore riceve in cambio il diritto di convertire il titolo in azioni della società.
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (5,'CONVERTIBLE','Convertible');
-- Il termine si applica solitamente a obbligazioni (callable bonds)
-- L'emittente ha la facoltà (ma non l'obbligo) di rimborsare il capitale ai creditori prima della scadenza
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (6,'CALLABLE','Callable');
INSERT INTO type_of_interest(id_type_of_interest,code, description) VALUES (100,'NONE','None');
s
-- ----------------------------------------------------------------------------
-- accrual_schedule_type
-- ----------------------------------------------------------------------------
INSERT INTO accrual_schedule_type(id_accrual_schedule_type,code, description) VALUES (100,'AST_NONE','None');

-- ----------------------------------------------------------------------------
-- market_segment
-- ----------------------------------------------------------------------------
INSERT INTO market_segment(id_market_segment,code, description) VALUES (1,'CB','Currency bonds');

-- ----------------------------------------------------------------------------
-- amortization_schedule
-- ----------------------------------------------------------------------------
-- Piano Francese
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (1,'SAS','Standard Amortization Schedule');
-- Piano Italiano
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (2,'SLP','Straight-line Principal');
-- Piano Bullet
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (3,'IOL','Interest Only Loan');
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (100,'NONE','None');

-- ----------------------------------------------------------------------------
-- settlement_type - tipo consegna
-- ----------------------------------------------------------------------------
INSERT INTO settlement_type(id_settlement_type,code, description) VALUES (1,'PHYSICAL','Physical Settlement');
INSERT INTO settlement_type(id_settlement_type,code, description) VALUES (2,'CASH','Cash Settlement');

-- ----------------------------------------------------------------------------
-- txn_status
-- ----------------------------------------------------------------------------
INSERT INTO txn_status(id_txn_status,code, description) VALUES (1,'PENDING','Pending');
-- in processanto
INSERT INTO txn_status(id_txn_status,code, description) VALUES (2,'VALIDATING','Validating');
-- processata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (3,'EXECUTED','Executed');
-- rifiutata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (4,'REJECTED','Rejected');
-- da modificare
INSERT INTO txn_status(id_txn_status,code, description) VALUES (5,'TO_AMEND','To Amend');
-- modificata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (6,'AMENDED','Amended');
-- da cancellare
INSERT INTO txn_status(id_txn_status,code, description) VALUES (7,'TO_CANCEL','To Cancel');
-- cancellata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (8,'CANCELLED','Cancelled');
-- restart transazione rejected
INSERT INTO txn_status(id_txn_status,code, description) VALUES (9,'RESTARTING','Restartin');

-- ----------------------------------------------------------------------------
-- accounting_status - stato contabile
-- ----------------------------------------------------------------------------
INSERT INTO accounting_status(accounting_status_id,code, description) VALUES (1,'NONE','None');
INSERT INTO accounting_status(accounting_status_id,code, description) VALUES (2,'MEMO_BOOKED','Memo Booked');
INSERT INTO accounting_status(accounting_status_id,code, description) VALUES (3,'POSTED','Posted');

-- ----------------------------------------------------------------------------
-- counterparty_type
-- ----------------------------------------------------------------------------
-- Persone fisiche
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (1,'NATURAL_PERSON','Retail Clients');
-- Fondi d'investimento, fondi pensione o assicurazioni
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (2,'LEGAL_ENTITY','Institutional Clients');
-- Aziende che usano il software per hedging 
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (3,'INVESTMENT_FUND','Corporate');
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (4,'SOVEREIGN_PUBLIC','Bank');
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (5,'CHOUSE','Clearing Houses');
-- Banche Depositarie
INSERT INTO counterparty_type(id_counterparty_type,code, description) VALUES (6,'CUSTODIAN','Custodians');

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
-- currency_pair
-- ----------------------------------------------------------------------------
INSERT INTO currency_pair(id_currency_pair,code,bcy,ccy,bid,ask) 
    VALUES (nextval('currency_pair_s'),'EURUSD',(SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_currency FROM currency WHERE iso_code='USD'),0,0);
INSERT INTO currency_pair(id_currency_pair,code,bcy,ccy,bid,ask) 
    VALUES (nextval('currency_pair_s'),'EURCHF',(SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_currency FROM currency WHERE iso_code='CHF'),0,0);
INSERT INTO currency_pair(id_currency_pair,code,bcy,ccy,bid,ask) 
    VALUES (nextval('currency_pair_s'),'EURJPY',(SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_currency FROM currency WHERE iso_code='JPY'),0,0);
INSERT INTO currency_pair(id_currency_pair,code,bcy,ccy,bid,ask) 
    VALUES (nextval('currency_pair_s'),'EURCAD',(SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_currency FROM currency WHERE iso_code='CAD'),0,0);
INSERT INTO currency_pair(id_currency_pair,code,bcy,ccy,bid,ask) 
    VALUES (nextval('currency_pair_s'),'EURAUD',(SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_currency FROM currency WHERE iso_code='AUD'),0,0);
INSERT INTO currency_pair(id_currency_pair,code,bcy,ccy,bid,ask) 
    VALUES (nextval('currency_pair_s'),'EURGBP',(SELECT id_currency FROM currency WHERE iso_code='EUR'),
    (SELECT id_currency FROM currency WHERE iso_code='GBP'),0,0);

-- ----------------------------------------------------------------------------
