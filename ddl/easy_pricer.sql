-- ----------------------------------------------------------------------------
-- daycount
-- ----------------------------------------------------------------------------
CREATE TABLE daycount
(
    id_daycount INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(25) NOT NULL
    , PRIMARY KEY (id_daycount)
);
CREATE UNIQUE INDEX idx_daycount_code ON daycount(code);
ALTER TABLE daycount OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE daycount_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE daycount_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- frequency
-- ----------------------------------------------------------------------------
CREATE TABLE frequency
(
    id_frequency INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(25) NOT NULL
    , year_fraction SMALLINT NOT NULL DEFAULT 2
    , PRIMARY KEY (id_frequency)
);
CREATE UNIQUE INDEX idx_frequency_code ON frequency(code);
ALTER TABLE frequency OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE frequency_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE frequency_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- form
-- ----------------------------------------------------------------------------
CREATE TABLE form
(
    id_form INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(25) NOT NULL
    , PRIMARY KEY (id_form)
);
CREATE UNIQUE INDEX idx_form_code ON form(code);
ALTER TABLE form OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE form_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE form_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- roll_convention
-- ----------------------------------------------------------------------------
CREATE TABLE roll_convention
(
    id_roll_convention INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(25) NOT NULL
    , PRIMARY KEY (id_roll_convention)
);
CREATE UNIQUE INDEX idx_roll_convention_code ON roll_convention(code);
ALTER TABLE roll_convention OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE roll_convention_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE roll_convention_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- type_of_interest
-- ----------------------------------------------------------------------------
CREATE TABLE type_of_interest
(
    id_type_of_interest INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(25) NOT NULL
    , PRIMARY KEY (id_type_of_interest)
);
CREATE UNIQUE INDEX idx_type_of_interest_code ON type_of_interest(code);
ALTER TABLE type_of_interest OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE type_of_interest_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE type_of_interest_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- accrual_schedule_type
-- ----------------------------------------------------------------------------
CREATE TABLE accrual_schedule_type
(
    id_accrual_schedule_type INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(25) NOT NULL
    , PRIMARY KEY (id_accrual_schedule_type)
);
CREATE UNIQUE INDEX idx_accrual_schedule_type_code ON accrual_schedule_type(code);
ALTER TABLE accrual_schedule_type OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE accrual_schedule_type_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE accrual_schedule_type_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- market_segment
-- ----------------------------------------------------------------------------
CREATE TABLE market_segment
(
    id_market_segment INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(255) NOT NULL
    , PRIMARY KEY (id_market_segment)
);
CREATE UNIQUE INDEX idx_market_segment_code ON market_segment(code);
ALTER TABLE market_segment OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE market_segment_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE market_segment_s OWNER TO easypricer;


-- ----------------------------------------------------------------------------
-- amortization_schedule
-- ----------------------------------------------------------------------------
CREATE TABLE amortization_schedule
(
    id_amortization_schedule INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(255) NOT NULL
    , PRIMARY KEY (id_amortization_schedule)
);
CREATE UNIQUE INDEX idx_amortization_schedule_code ON amortization_schedule(code);
ALTER TABLE amortization_schedule OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE amortization_schedule_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE amortization_schedule_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- calendar
-- ----------------------------------------------------------------------------
CREATE TABLE calendar
(
    id_calendar INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(25) NOT NULL
    , PRIMARY KEY (id_calendar)
);
CREATE UNIQUE INDEX idx_calendar_code ON calendar(code);
ALTER TABLE calendar OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE calendar_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE calendar_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- holiday
-- ----------------------------------------------------------------------------
CREATE TABLE holiday
(
    id_holiday INTEGER NOT NULL
    , calendar INTEGER NOT NULL
    , holiday_day SMALLINT NOT NULL
    , holiday_month SMALLINT NOT NULL
    , description VARCHAR(50) NOT NULL DEFAULT ''
    , PRIMARY KEY (id_holiday)
    , CONSTRAINT fk_calendar FOREIGN KEY (calendar)
              REFERENCES calendar(id_calendar)
);
ALTER TABLE holiday OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE holiday_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE holiday_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- currency
-- ----------------------------------------------------------------------------
CREATE TABLE currency
(
    id_currency INTEGER NOT NULL
    , calendar INTEGER NOT NULL
    , daycount INTEGER NOT NULL
    , iso_code VARCHAR(3) NOT NULL
    , currency_numeric_code SMALLINT NOT NULL
    , description VARCHAR(50) NOT NULL
    -- the smallest possible denomination of the given currency
    -- USD 50.50: Submit 5050 since US Dollars (USD) have two decimals
    , minor_unit SMALLINT NOT NULL DEFAULT 2 
    -- the System currency is an additional currency that is used parallel to the Local currency is used
    , system_curr SMALLINT NOT NULL DEFAULT 0
    -- Physical currency refers to tangible money, like banknotes (paper money) and coins
    , physical_curr SMALLINT NOT NULL DEFAULT 1
    , business_days INTEGER NOT NULL DEFAULT 2
    , CONSTRAINT fk_calendar FOREIGN KEY (calendar)
              REFERENCES calendar(id_calendar)
    , CONSTRAINT fk_daycount FOREIGN KEY (daycount)
        REFERENCES daycount(id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_currency)
);
CREATE UNIQUE INDEX idx_iso_code ON currency(iso_code);
CREATE UNIQUE INDEX idx_currency_numeric_code ON currency(currency_numeric_code);
ALTER TABLE public.currency OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE currency_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE public.currency_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- country
-- ----------------------------------------------------------------------------
CREATE TABLE country
(
    id_country INTEGER NOT NULL
    , country_name VARCHAR(100) NOT NULL
    , official_state_name VARCHAR(255) NOT NULL
    , alfa_2_code VARCHAR(2) NOT NULL
    , alfa_3_code VARCHAR(3) NOT NULL
    , country_numeric_code SMALLINT NOT NULL
    , sovereign VARCHAR(25) DEFAULT 'UN Member State'
    , subdivision_code_links VARCHAR(25)
    , internet_cc_TLD VARCHAR(10)
    , currency INTEGER  NOT NULL
    , calendar INTEGER  NOT NULL
    , PRIMARY KEY (id_country)
    , CONSTRAINT fk_currency FOREIGN KEY (currency)
              REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_calendar FOREIGN KEY (calendar)
              REFERENCES calendar(id_calendar) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_country_numeric_code ON country(country_numeric_code);
CREATE UNIQUE INDEX alfa_2_code ON country(alfa_2_code);
CREATE UNIQUE INDEX alfa_3_code ON country(alfa_3_code);
ALTER TABLE public.country OWNER TO easypricer;
CREATE SEQUENCE country_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE public.country_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- issuer
-- ----------------------------------------------------------------------------
CREATE TABLE issuer
(
    id_issuer INTEGER NOT NULL
    , short_issuer_name VARCHAR(25) NOT NULL DEFAULT ''
    , long_issuer_name VARCHAR(255) NOT NULL
    , country INTEGER NOT NULL
    , PRIMARY KEY (id_issuer)
    , CONSTRAINT fk_country FOREIGN KEY (country)
              REFERENCES country(id_country) ON DELETE NO ACTION ON UPDATE NO ACTION
);
ALTER TABLE public.issuer OWNER TO easypricer;
CREATE SEQUENCE issuer_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE public.issuer_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- super_class
-- ----------------------------------------------------------------------------
CREATE TABLE super_class
(
    id_super_class INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(225) NOT NULL DEFAULT '' 
    , PRIMARY KEY (id_super_class)
);
CREATE UNIQUE INDEX idx_super_class_code ON super_class(code);
ALTER TABLE super_class OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE super_class_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE super_class_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- asset_class
-- ----------------------------------------------------------------------------
CREATE TABLE asset_class
(
    id_asset_class INTEGER NOT NULL
    , super_class INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(225) NOT NULL DEFAULT '' 
    , PRIMARY KEY (id_asset_class)
    , CONSTRAINT fk_super_class FOREIGN KEY (super_class)
        REFERENCES super_class(id_super_class) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_asset_class_code ON asset_class(code);
ALTER TABLE asset_class OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE asset_class_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE asset_class_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- master_data tabella base per anagrafica (securities,loan...)
-- ----------------------------------------------------------------------------
CREATE TABLE master_data
(
    id_master_data INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , currency INTEGER NOT NULL
    , calendar INTEGER NOT NULL
    , issue_date DATE NOT NULL
    , maturity_date DATE NOT NULL
    , type_of_interest INTEGER NOT NULL
    , form INTEGER NOT NULL
    , daycount INTEGER NOT NULL
    , frequency INTEGER NOT NULL
    , roll_convention INTEGER NOT NULL DEFAULT 0
    , accrual_schedule_type INTEGER NOT NULL DEFAULT 0
    , interest_rate NUMERIC(23,10) NOT NULL
    , issue_price NUMERIC(15,5) NOT NULL  
    , redempion_price NUMERIC(15,5) NOT NULL  
    , business_days INTEGER NOT NULL DEFAULT 2
    , asset_class INTEGER NOT NULL 
    , amortization_schedule INTEGER NOT NULL 
    , PRIMARY KEY (id_master_data)
    , CONSTRAINT fk_daycount FOREIGN KEY (daycount)
        REFERENCES daycount(id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_frequency FOREIGN KEY (frequency)
        REFERENCES frequency(id_frequency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_form FOREIGN KEY (form)
        REFERENCES form(id_form) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_calendar FOREIGN KEY (calendar)
        REFERENCES calendar(id_calendar) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_currency FOREIGN KEY (currency)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_roll_convention FOREIGN KEY (roll_convention)
        REFERENCES roll_convention(id_roll_convention) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_type_of_interest FOREIGN KEY (type_of_interest)
        REFERENCES type_of_interest(id_type_of_interest) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_accrual_schedule_type FOREIGN KEY (accrual_schedule_type)
        REFERENCES accrual_schedule_type(id_accrual_schedule_type) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_asset_class FOREIGN KEY (asset_class)
        REFERENCES asset_class(id_asset_class) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_amortization_schedule FOREIGN KEY (amortization_schedule)
        REFERENCES amortization_schedule(id_amortization_schedule) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_master_data_code ON master_data(code);
ALTER TABLE master_data OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE master_data_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE master_data_s OWNER TO easypricer;
CREATE SEQUENCE master_data_code_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE master_data_code_s OWNER TO easypricer;
-- select ltrim(to_char(interest_rate , '000000'))from master_data
-- select ltrim(to_char(nextval('loan_code_s'), '000000'))

-- ----------------------------------------------------------------------------
-- loan_master_data - anagrafica mutui
-- ----------------------------------------------------------------------------
-- code = FRM20460700010
-- FRM mutuo a tasso fisso (VRM variable rate mortage mutuo a tasso variabile) type_of_interest
-- 2046 anno scadenza
-- 07 mese scadenza
-- 000010 valore da sequenza master_data_code_s
CREATE TABLE loan_master_data
(
    id_master_data INTEGER NOT NULL
    , description VARCHAR(255) NOT NULL DEFAULT ''
    , processing_fees NUMERIC(23,10) NOT NULL DEFAULT 0 -- Spese di incasso rata
    , management_fees NUMERIC(23,10) NOT NULL DEFAULT 0 -- Spese di gestione pratica
    , incidental_expenses NUMERIC(23,10) NOT NULL DEFAULT 0 -- Oneri accessori
    , default_interest NUMERIC(23,10) NOT NULL DEFAULT 0 -- Interessi di mora
    , late_payment_fee NUMERIC(23,10) NOT NULL DEFAULT 0 -- Penale di mora
    , underwriting_fee NUMERIC(23,10) NOT NULL DEFAULT 0 -- Commissione di istruttoria
    , insurance_premium NUMERIC(23,10) NOT NULL DEFAULT 0 -- Premio assicurativo (scoppio/incendio)
    , tax_charges NUMERIC(23,10) NOT NULL DEFAULT 0 -- Imposta sostitutiva
    , PRIMARY KEY (id_master_data)
);
ALTER TABLE loan_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- security_master_data - anagrafica titoli di stato
-- ----------------------------------------------------------------------------
CREATE TABLE security_master_data
(
    id_master_data INTEGER NOT NULL
    , isin VARCHAR(25) NOT NULL
    , cfi_code VARCHAR(25) NOT NULL
    , fisn VARCHAR(255) NOT NULL
    , lei VARCHAR(255) NOT NULL
    , issuer INTEGER NOT NULL
    , nominal_value NUMERIC(23,10) NOT NULL
    , first_coupon_rate NUMERIC(23,10) NOT NULL
    , first_coupon_payment_date DATE NOT NULL
    , CONSTRAINT fk_issuer FOREIGN KEY (issuer)
        REFERENCES issuer(id_issuer) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_master_data)
);
CREATE UNIQUE INDEX idx_security_master_data_isin ON security_master_data(isin);
ALTER TABLE security_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- cash_flow_item
-- ----------------------------------------------------------------------------
CREATE TABLE cash_flow_item
(
    id_cash_flow_item INTEGER NOT NULL
    , master_data INTEGER NOT NULL
    , start_date DATE NOT NULL
    , end_date DATE NOT NULL
    , interest NUMERIC(15,5) NOT NULL  
    , amount NUMERIC(15,5) NOT NULL  
    , PRIMARY KEY (id_cash_flow_item)
    , CONSTRAINT fk_master_data FOREIGN KEY (master_data)
        REFERENCES master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_md_ed ON cash_flow_item(master_data,end_date);
ALTER TABLE cash_flow_item OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE cash_flow_item_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE cash_flow_item_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- cash_flow_reset 
-- ----------------------------------------------------------------------------
CREATE TABLE cash_flow_reset
(
    id_cash_flow_reset INTEGER NOT NULL
    , master_data INTEGER NOT NULL
    , start_date_reset DATE NOT NULL -- data reset nuovo coupon
    , interest_reset NUMERIC(15,5) NOT NULL  -- valore nuovo coupon
    , PRIMARY KEY (id_cash_flow_reset)
    , CONSTRAINT fk_master_data FOREIGN KEY (master_data)
        REFERENCES master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_md_sdr ON cash_flow_reset(master_data,start_date_reset);
ALTER TABLE cash_flow_reset OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE cash_flow_reset_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE cash_flow_reset_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- yield_curve
-- ----------------------------------------------------------------------------
CREATE TABLE yield_curve
(
    id_yield_curve INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL
    , description VARCHAR(225) NOT NULL DEFAULT '' 
    , currency INTEGER NOT NULL
    , calendar INTEGER NOT NULL
    , compounding SMALLINT NOT NULL DEFAULT 1;
    , PRIMARY KEY (id_yield_curve)
    , CONSTRAINT fk_calendar FOREIGN KEY (calendar)
        REFERENCES calendar(id_calendar) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_currency FOREIGN KEY (currency)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION

);
CREATE UNIQUE INDEX idx_yield_curve_code ON yield_curve(code);
ALTER TABLE yield_curve OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE yield_curve_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE yield_curve_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- yield_curve_item
-- ----------------------------------------------------------------------------
CREATE TABLE yield_curve_item
(
    id_yield_curve_item INTEGER NOT NULL
    , yield_curve INTEGER NOT NULL
    , ric VARCHAR(25) NOT NULL
    , offset_type SMALLINT NOT NULL
    , offset_value SMALLINT NOT NULL
    , bid NUMERIC(15,5) NOT NULL  
    , ask NUMERIC(15,5) NOT NULL  
    , PRIMARY KEY (id_yield_curve_item)
    , CONSTRAINT fk_yield_curve FOREIGN KEY (yield_curve)
        REFERENCES yield_curve(id_yield_curve) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE UNIQUE INDEX idx_yield_curve_item_ric ON yield_curve_item(ric);
ALTER TABLE yield_curve_item OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE yield_curve_item_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE yield_curve_item_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- settlement_type
-- ----------------------------------------------------------------------------
CREATE TABLE settlement_type
(
    id_settlement_type INTEGER NOT NULL
    , code VARCHAR(25)
    , description VARCHAR(25) NOT NULL DEFAULT ''
    , PRIMARY KEY (id_settlement_type)
);
CREATE UNIQUE INDEX idx_settlement_type_code ON settlement_type(code);
ALTER TABLE settlement_type OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE settlement_type_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE settlement_type_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- future_master_data - anagrafica future
-- ----------------------------------------------------------------------------
CREATE TABLE future_master_data
(
    id_master_data INTEGER NOT NULL
    , isin VARCHAR(25) NOT NULL -- isin contratto principale
    , exchange_contract_code VARCHAR(25) NOT NULL DEFAULT ''
    , settlement_type INTEGER NOT NULL
    , CONSTRAINT fk_settlement_type FOREIGN KEY (settlement_type)
        REFERENCES settlement_type(id_settlement_type) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_master_data)
);
CREATE UNIQUE INDEX idx_future_master_data_isin ON future_master_data(isin);
ALTER TABLE future_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- bond_future_master_data - anagrafica bond future
-- ----------------------------------------------------------------------------
CREATE TABLE bond_future_master_data
(
    id_master_data INTEGER NOT NULL
    , contract_value NUMERIC(15,5) NOT NULL  
    , tick_size NUMERIC(15,5) NOT NULL  
    , initial_margin NUMERIC(15,5) NOT NULL  
    , PRIMARY KEY (id_master_data)
);
ALTER TABLE bond_future_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- deliverable_bonds - consegnabili
-- ----------------------------------------------------------------------------
CREATE TABLE deliverable_bonds
(
    id_deliverable_bonds INTEGER NOT NULL
    , master_data INTEGER NOT NULL -- future 
    , expiration_date DATE NOT NULL -- scadenza future 
    , isin VARCHAR(25) NOT NULL -- isin consegnabile
    , coupon_rate NUMERIC(15,5) NOT NULL  -- coupon consegnabile 
    , bond_maturity DATE NOT NULL -- scadenza consegnabile 
    , bond_cf NUMERIC(15,5) NOT NULL  -- conversion factor
    , CONSTRAINT fk_master_data FOREIGN KEY (master_data)
        REFERENCES bond_future_master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_deliverable_bonds)
);
CREATE UNIQUE INDEX idx_deliverable_bonds_isin ON deliverable_bonds(master_data,isin);
ALTER TABLE bond_future_master_data OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE deliverable_bonds_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE deliverable_bonds_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- fx_future_master_data - anagrafica forex future
-- ----------------------------------------------------------------------------
CREATE TABLE fx_future_master_data
(
    id_master_data INTEGER NOT NULL
    , underlying INTEGER NOT NULL -- coppia sottostante il contratto
    , contract_value NUMERIC(15,5) NOT NULL  
    , tick_size NUMERIC(15,5) NOT NULL  
    , initial_margin NUMERIC(15,5) NOT NULL  
    , maintenance_margin NUMERIC(15,5) NOT NULL  
    , CONSTRAINT fk_underlying FOREIGN KEY (underlying)
        REFERENCES forex_master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_master_data)
);
ALTER TABLE fx_future_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- mm_future_master_data - anagrafica money market future
-- ----------------------------------------------------------------------------
CREATE TABLE mm_future_master_data
(
    id_master_data INTEGER NOT NULL
    , underlying INTEGER NOT NULL -- coppia sottostante il contratto
    , contract_value NUMERIC(15,5) NOT NULL  
    , tick_size NUMERIC(15,5) NOT NULL  
    , initial_margin NUMERIC(15,5) NOT NULL  
    , maintenance_margin NUMERIC(15,5) NOT NULL  
    , CONSTRAINT fk_underlying FOREIGN KEY (underlying)
        REFERENCES forex_master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_master_data)
);
ALTER TABLE mm_future_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- instrument_quote
-- ----------------------------------------------------------------------------
CREATE TABLE instrument_quote
(
    id_instrument_quote INTEGER NOT NULL
    , master_data INTEGER NOT NULL -- future 
    , provider VARCHAR(50) NOT NULL 
    , code VARCHAR(255) NOT NULL 
    , bid NUMERIC(15,5) NOT NULL  
    , ask NUMERIC(15,5) NOT NULL  
    , CONSTRAINT fk_master_data FOREIGN KEY (master_data)
        REFERENCES master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_instrument_quote)
);
CREATE UNIQUE INDEX idx_instrument_quote_code ON instrument_quote(code);
ALTER TABLE instrument_quote OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE instrument_quote_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE instrument_quote_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- instrument_quote_hist
-- ----------------------------------------------------------------------------
CREATE TABLE instrument_quote_hist
(
    id_instrument_quote_hist INTEGER NOT NULL
    , instrument_quote INTEGER NOT NULL
    , master_data INTEGER NOT NULL -- future 
    , code VARCHAR(25) NOT NULL 
    , bid NUMERIC(15,5) NOT NULL  
    , ask NUMERIC(15,5) NOT NULL  
    , update_date DATE NOT NULL  
    , CONSTRAINT fk_master_data FOREIGN KEY (master_data)
        REFERENCES master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_instrument_quote FOREIGN KEY (instrument_quote)
        REFERENCES instrument_quote(id_instrument_quote) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_instrument_quote_hist)
);
CREATE UNIQUE INDEX idx_instrument_quote_hist_iqud ON instrument_quote_hist(instrument_quote,update_date);
ALTER TABLE instrument_quote_hist OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE instrument_quote_hist_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE instrument_quote_hist_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- currpair_master_data - anagrafica currency pair
-- ----------------------------------------------------------------------------
CREATE TABLE currpair_master_data
(
    id_master_data INTEGER NOT NULL
    , bcy INTEGER NOT NULL -- link alla bcy
    , ccy INTEGER NOT NULL -- link alla ccy
    , bcy_irc VARCHAR(25) NOT NULL DEFAULT '' -- bcy interest rate curve
    , ccy_irc VARCHAR(25) NOT NULL DEFAULT '' -- ccy interest rate curve
    , CONSTRAINT fk_bcy FOREIGN KEY (bcy)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_ccy FOREIGN KEY (ccy)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_master_data)
);
CREATE UNIQUE INDEX idx_currpair_bcy_ccy ON currpair_master_data(bcy,ccy);
ALTER TABLE currpair_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- forex_master_data - anagrafica forex
-- ----------------------------------------------------------------------------
CREATE TABLE forex_master_data
(
    id_master_data INTEGER NOT NULL
    , bcy INTEGER NOT NULL -- link alla bcy
    , ccy INTEGER NOT NULL -- link alla ccy
    , bcy_irc VARCHAR(25) NOT NULL DEFAULT '' -- bcy interest rate curve
    , ccy_irc VARCHAR(25) NOT NULL DEFAULT '' -- ccy interest rate curve
    , CONSTRAINT fk_bcy FOREIGN KEY (bcy)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_ccy FOREIGN KEY (ccy)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_master_data)
);
CREATE UNIQUE INDEX idx_forex_bcy_ccy ON forex_master_data(bcy,ccy);
ALTER TABLE forex_master_data OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- counterparty_type - tipologia controparte
-- ----------------------------------------------------------------------------
CREATE TABLE counterparty_type
(
    id_counterparty_type INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL 
    , description VARCHAR(255) NOT NULL 
    , PRIMARY KEY (id_counterparty_type)
);
CREATE UNIQUE INDEX idx_counterparty_type_code ON counterparty_type(code);
ALTER TABLE counterparty_type OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE counterparty_type_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE counterparty_type_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- counterparty - controparte della transazione
-- ----------------------------------------------------------------------------
CREATE TABLE counterparty
(
    id_counterparty INTEGER NOT NULL
    , ctp_type INTEGER NOT NULL 
    , lei_code VARCHAR(50)  
    , country INTEGER NOT NULL 
    , code VARCHAR(25) NOT NULL 
    , description VARCHAR(255) NOT NULL 
    , CONSTRAINT fk_ctp_type FOREIGN KEY (ctp_type)
        REFERENCES counterparty_type(id_counterparty_type) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_country FOREIGN KEY (country)
        REFERENCES country(id_country) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_counterparty)
);
CREATE UNIQUE INDEX idx_counterparty_code ON counterparty(code);
ALTER TABLE counterparty OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE counterparty_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE counterparty_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- portfolio_master_data - anagrafica posizione 
-- ----------------------------------------------------------------------------
CREATE TABLE portfolio_master_data
(
    id_portfolio INTEGER NOT NULL
    , currency  INTEGER NOT NULL -- divisa del portfolio
    , code VARCHAR(25) NOT NULL 
    , description VARCHAR(255) NOT NULL  -- legal name
    , CONSTRAINT fk_currency FOREIGN KEY (currency)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_portfolio)
);
CREATE UNIQUE INDEX idx_portfolio_code ON portfolio_master_data(code);
ALTER TABLE portfolio_master_data OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE portfolio_master_data_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE portfolio_master_data_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- position_master_data - anagrafica posizione 
-- ----------------------------------------------------------------------------
CREATE TABLE position_master_data
(
    id_position INTEGER NOT NULL
    , portfolio INTEGER NOT NULL
    , currency  INTEGER NOT NULL -- divisa della posizione
    , code VARCHAR(25) NOT NULL 
    , description VARCHAR(255) NOT NULL  -- legal name
    , CONSTRAINT fk_currency FOREIGN KEY (currency)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_portfolio FOREIGN KEY (portfolio)
        REFERENCES portfolio_master_data(id_portfolio) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_position)
);
CREATE UNIQUE INDEX idx_position_code ON position_master_data(code);
ALTER TABLE position_master_data OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE position_master_data_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE position_master_data_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- position_detail - dettaglio posizione per strumento
-- ----------------------------------------------------------------------------
CREATE TABLE position_detail
(
    id_position_detail INTEGER NOT NULL
    , position_md INTEGER NOT NULL -- posizione
    , master_data  INTEGER NOT NULL -- identificativo strumento (ogni strumento lavora con la propria divisa)
    , counterparty INTEGER NOT NULL
    , realized_pnl NUMERIC(15,5) NOT NULL  
    , unrealized_pnl NUMERIC(15,5) NOT NULL  
    , buy_qty      NUMERIC(15,5) NOT NULL  -- quantita acquistata (incrementale)
    , notional_value_buy NUMERIC(15,5) NOT NULL  -- controvalore di acquisto (incrementale)
    , buy_fees NUMERIC(15,5) NOT NULL  -- commissioni di acquisto (incrementale)
    , buy_taxes NUMERIC(15,5) NOT NULL  -- tasse di transazione sull'acquisto
    , sell_qty      NUMERIC(15,5) NOT NULL  -- quantita venduta (incrementale)
    , notional_value_sell NUMERIC(15,5) NOT NULL  -- controvalore di vendita (incrementale)
    , sell_fees NUMERIC(15,5) NOT NULL  -- commissioni di vendita (incrementale)
    , sell_taxes NUMERIC(15,5) NOT NULL  -- tasse di transazione sulla vendita
    , multiplier NUMERIC(15,5) NOT NULL  DEFAULT 1 -- nel caso dei Bond sara 0.01
    , market_price NUMERIC(15,5) NOT NULL  -- prezzo di mercato a cui potrei vendere (sempre bid)
    , CONSTRAINT fk_master_data FOREIGN KEY (master_data)
        REFERENCES master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_counterparty FOREIGN KEY (counterparty)
        REFERENCES counterparty(id_counterparty) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_position_md FOREIGN KEY (position_md)
        REFERENCES position_master_data(id_position) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_position_detail)
);
ALTER TABLE position_detail OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE position_detail_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE position_detail_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- txn_status - stato
-- ----------------------------------------------------------------------------
CREATE TABLE txn_status
(
    id_txn_status INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL 
    , description VARCHAR(255) NOT NULL 
    , PRIMARY KEY (id_txn_status)
);
CREATE UNIQUE INDEX idx_txn_status_code ON txn_status(code);
ALTER TABLE txn_status OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE txn_status_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE txn_status_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- financial_txn - transazione finanziaria
-- ----------------------------------------------------------------------------
CREATE TABLE financial_txn
(
    id_financial_txn INTEGER NOT NULL
    , counterparty  INTEGER NOT NULL
    , position_md  INTEGER NOT NULL -- position
    , master_data  INTEGER NOT NULL -- identificativo strumento
    , txn_status  INTEGER NOT NULL -- stato transazione
    , txn_side SMALLINT NOT NULL -- (Buy/Sell)
    , description VARCHAR(255) NOT NULL 
    , trade_date DATE NOT NULL DEFAULT NOW()
    , settlement DATE NOT NULL -- valuta
    , quantity NUMERIC(15,5) NOT NULL  
    , price NUMERIC(15,5) NOT NULL  
    , PRIMARY KEY (id_financial_txn)
    , CONSTRAINT fk_counterparty FOREIGN KEY (counterparty)
        REFERENCES counterparty(id_counterparty) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_position_md FOREIGN KEY (position_md)
        REFERENCES position_master_data(id_position) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_master_data FOREIGN KEY (master_data)
        REFERENCES master_data(id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_txn_status FOREIGN KEY (txn_status)
        REFERENCES txn_status(id_txn_status) ON DELETE NO ACTION ON UPDATE NO ACTION
);
ALTER TABLE financial_txn OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE financial_txn_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE financial_txn_s OWNER TO easypricer;


---------------------------------------------------------------------
-- Tabelle obsolete
---------------------------------------------------------------------

-- ----------------------------------------------------------------------------
-- currency_pair
-- ----------------------------------------------------------------------------
CREATE TABLE currency_pair
(
    id_currency_pair INTEGER NOT NULL
    , code VARCHAR(25) NOT NULL 
    , bcy INTEGER NOT NULL
    , ccy INTEGER NOT NULL
    , bid NUMERIC(15,5) NOT NULL  
    , ask NUMERIC(15,5) NOT NULL  
    , CONSTRAINT fk_bcy FOREIGN KEY (bcy)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , CONSTRAINT fk_ccy FOREIGN KEY (ccy)
        REFERENCES currency(id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
    , PRIMARY KEY (id_currency_pair)
);
CREATE UNIQUE INDEX idx_currency_pair_code ON currency_pair(code);
ALTER TABLE currency_pair OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE currency_pair_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE currency_pair_s OWNER TO easypricer;

-- ----------------------------------------------------------------------------
-- ec_exchange_rate
-- ----------------------------------------------------------------------------
CREATE TABLE ec_exchange_rate
(
    id_ec_exchange_rate INTEGER NOT NULL
    , country VARCHAR(25)
    , currency VARCHAR(25) NOT NULL DEFAULT ''
    , isoA3Code VARCHAR(5) NOT NULL DEFAULT ''
    , isoA2Code VARCHAR(5) NOT NULL DEFAULT ''
    , rate NUMERIC(15,5) NOT NULL  
    , PRIMARY KEY (id_ec_exchange_rate)
);
ALTER TABLE ec_exchange_rate OWNER TO easypricer;
-- Creo sequenza
CREATE SEQUENCE ec_exchange_rate_s START WITH 1 INCREMENT BY 1; 
ALTER SEQUENCE ec_exchange_rate_s OWNER TO easypricer;


