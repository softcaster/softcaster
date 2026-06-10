-------------------------------------------------------------------------------
-- STARTUP
-------------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
-- daycount
-- ----------------------------------------------------------------------------
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'NASD_30_360','30/360 NASD');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'EUR_30_360','30/360 EUR');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'ACT_360','Actual/360');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'ACT_365','Actual/365');
INSERT INTO daycount(id_daycount,code, description) VALUES (nextval('daycount_s'),'ACT_ACT_ICMA','Actual/Actual');

-- ----------------------------------------------------------------------------
-- roll_convention
-- ----------------------------------------------------------------------------
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'PREVIOUS','Previus');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'PREVIOUS-MODIFIED','Previus Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'FORWARD','Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (nextval('roll_convention_s'),'FORWARD-MODIFIED','Modified Following');
INSERT INTO roll_convention(id_roll_convention,code, description) VALUES (100,'RC_NONE','None');

-- ----------------------------------------------------------------------------
-- frequency
-- ----------------------------------------------------------------------------
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'ANNUAL','Annual',1);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'SEMI-ANNUAL','Semi Annual',2);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'E4M','Every 4 months',3);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'QUARTERLY','Quarterly',4);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'BI-MONTHLY','Every two months',6);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (nextval('frequency_s'),'MONTHLY','Monthly',12);
INSERT INTO frequency(id_frequency,code, description,year_fraction) VALUES (100,'CUSTOM','Custom',0);

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
s
-- ----------------------------------------------------------------------------
-- accrual_schedule_type
-- ----------------------------------------------------------------------------
INSERT INTO accrual_schedule_type(id_accrual_schedule_type,code, description) VALUES (100,'AST_NONE','None');

-- ----------------------------------------------------------------------------
-- market_segment
-- ----------------------------------------------------------------------------
INSERT INTO market_segment(id_market_segment,code, description) VALUES (nextval('market_segment_s'),'CB','Currency bonds');

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
-- amortization_schedule
-- ----------------------------------------------------------------------------
-- Piano Francese
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (nextval('amortization_schedule_s'),'SAS','Standard Amortization Schedule');
-- Piano Italiano
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (nextval('amortization_schedule_s'),'SLP','Straight-line Principal');
-- Piano Bullet
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (nextval('amortization_schedule_s'),'IOL','Interest Only Loan');
INSERT INTO amortization_schedule(id_amortization_schedule,code, description) VALUES (100,'NONE','None');

-- ----------------------------------------------------------------------------
-- settlement_type - tipo consegna
-- ----------------------------------------------------------------------------
INSERT INTO settlement_type(id_settlement_type,code, description) VALUES (nextval('settlement_type_s'),'PHYSICAL','Physical Settlement');
INSERT INTO settlement_type(id_settlement_type,code, description) VALUES (nextval('settlement_type_s'),'CASH','Cash Settlement');

-- ----------------------------------------------------------------------------
-- txn_status
-- ----------------------------------------------------------------------------
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'PENDING','Pending');
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
-- ----------------------------------------------------------------------------
-- restart transazione rejected
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'RESTARTING','Restartin');
-- cancellata e processata
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'CANCELLED_EXECUTED','Cancelled and Executed');
-- in processanto
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'VALIDATING','Validating');
-- contabilizzata (scambio denaro titoli a T+bussinnes days
INSERT INTO txn_status(id_txn_status,code, description) VALUES (nextval('txn_status_s'),'SETTLED','Settled');

-- ----------------------------------------------------------------------------
-- accounting_status - stato contabile
-- ----------------------------------------------------------------------------
INSERT INTO accounting_status(accounting_status_id,code, description) VALUES (nextval('accounting_status_s'),'NONE','None');
INSERT INTO accounting_status(accounting_status_id,code, description) VALUES (nextval('accounting_status_s'),'MEMO_BOOKED','Memo Booked');
INSERT INTO accounting_status(accounting_status_id,code, description) VALUES (nextval('accounting_status_s'),'POSTED','Posted');

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


-- type
--'Structured Bond Interest Rates'
--'Fixed Rate Bond'
--'Floating Rate Bond'
--Step-Up Structure: Coupon rates increase over specific periods, such as a 5-year bond offering 4.10% for the first three years and 4.50% for the final two years, as seen in the Btp Valore Oct28.
--Variable Component: Some bonds, like Cdp March 2024 to Dec 2026 bonds, pay a fixed rate initially, followed by a rate linked to the Euribor 3-month index plus a spread (e.g., Euribor + 0.9%).
--Loyalty Premium: Extra returns, such as a 0.5% gross bonus, are sometimes awarded to investors holding the bond from issuance to maturity.
--Alternative Structures: Other structures include inflation-linked, which adjust based on cost-of-living indices, or fixed-rate structures with varying maturity options.
--Valuation: The price of these bonds is highly sensitive to interest rate changes, with an inverse relationship; as market interest rates rise, the price of these bonds tends to decrease, as explained by YouTube video on interest rate impacts
-- ----------------------------------------------------------------------------
