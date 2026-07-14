
-- ----------------------------------------------------------------------------
-- daycount
-- ----------------------------------------------------------------------------
CREATE TABLE daycount (
    id_daycount integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (id_daycount)
);

CREATE UNIQUE INDEX idx_daycount_code ON daycount (code);

ALTER TABLE daycount OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE daycount_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE daycount_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- frequency
-- ----------------------------------------------------------------------------
CREATE TABLE frequency (
    id_frequency integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    year_fraction smallint NOT NULL DEFAULT 2,
    PRIMARY KEY (id_frequency)
);

CREATE UNIQUE INDEX idx_frequency_code ON frequency (code);

ALTER TABLE frequency OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE frequency_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE frequency_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- form
-- ----------------------------------------------------------------------------
CREATE TABLE form (
    id_form integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (id_form)
);

CREATE UNIQUE INDEX idx_form_code ON form (code);

ALTER TABLE form OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE form_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE form_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- roll_convention
-- ----------------------------------------------------------------------------
CREATE TABLE roll_convention (
    id_roll_convention integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (id_roll_convention)
);

CREATE UNIQUE INDEX idx_roll_convention_code ON roll_convention (code);

ALTER TABLE roll_convention OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE roll_convention_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE roll_convention_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- type_of_interest
-- ----------------------------------------------------------------------------
CREATE TABLE type_of_interest (
    id_type_of_interest integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (id_type_of_interest)
);

CREATE UNIQUE INDEX idx_type_of_interest_code ON type_of_interest (code);

ALTER TABLE type_of_interest OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE type_of_interest_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE type_of_interest_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- accrual_schedule_type
-- ----------------------------------------------------------------------------
CREATE TABLE accrual_schedule_type (
    id_accrual_schedule_type integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (id_accrual_schedule_type)
);

CREATE UNIQUE INDEX idx_accrual_schedule_type_code ON accrual_schedule_type (code);

ALTER TABLE accrual_schedule_type OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE accrual_schedule_type_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE accrual_schedule_type_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- amortization_schedule
-- ----------------------------------------------------------------------------
CREATE TABLE amortization_schedule (
    id_amortization_schedule integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (id_amortization_schedule)
);

CREATE UNIQUE INDEX idx_amortization_schedule_code ON amortization_schedule (code);

ALTER TABLE amortization_schedule OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE amortization_schedule_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE amortization_schedule_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- compounding
-- ----------------------------------------------------------------------------
CREATE TABLE compounding (
    id_compounding integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (id_compounding)
);

CREATE UNIQUE INDEX idx_compounding_code ON compounding (code);

ALTER TABLE compounding OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE compounding_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE compounding_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- calendar
-- ----------------------------------------------------------------------------
CREATE TABLE calendar (
    id_calendar integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (id_calendar)
);

CREATE UNIQUE INDEX idx_calendar_code ON calendar (code);

ALTER TABLE calendar OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE calendar_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE calendar_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- holiday
-- ----------------------------------------------------------------------------
CREATE TABLE holiday (
    id_holiday integer NOT NULL,
    calendar integer NOT NULL,
    holiday_day smallint NOT NULL,
    holiday_month smallint NOT NULL,
    description varchar(50) NOT NULL DEFAULT '',
    PRIMARY KEY (id_holiday),
    CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES calendar (id_calendar)
);

ALTER TABLE holiday OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE holiday_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE holiday_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- currency
-- ----------------------------------------------------------------------------
CREATE TABLE currency (
    id_currency integer NOT NULL,
    calendar integer NOT NULL,
    daycount integer NOT NULL,
    iso_code varchar(3) NOT NULL,
    currency_numeric_code smallint NOT NULL,
    description varchar(50) NOT NULL
    -- the smallest possible denomination of the given currency
,
    -- USD 50.50: Submit 5050 since US Dollars (USD) have two decimals
    minor_unit smallint NOT NULL DEFAULT 2,
    -- the System currency is an additional currency that is used parallel to the Local currency is used
    system_curr smallint NOT NULL DEFAULT 0,
    -- Physical currency refers to tangible money, like banknotes (paper money) and coins
    physical_curr smallint NOT NULL DEFAULT 1,
    business_days integer NOT NULL DEFAULT 2,
    CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES calendar (id_calendar),
    CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES daycount (id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_currency)
);

CREATE UNIQUE INDEX idx_iso_code ON currency (iso_code);

CREATE UNIQUE INDEX idx_currency_numeric_code ON currency (currency_numeric_code);

ALTER TABLE public.currency OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE currency_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE public.currency_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- country
-- ----------------------------------------------------------------------------
CREATE TABLE country (
    id_country integer NOT NULL,
    country_name varchar(100) NOT NULL,
    official_state_name varchar(255) NOT NULL,
    alfa_2_code varchar(2) NOT NULL,
    alfa_3_code varchar(3) NOT NULL,
    country_numeric_code smallint NOT NULL,
    sovereign varchar(25) DEFAULT 'UN Member State',
    subdivision_code_links varchar(25) DEFAULT '',
    internet_cc_TLD varchar(10) DEFAULT '',
    currency integer NOT NULL,
    calendar integer NOT NULL,
    PRIMARY KEY (id_country),
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES calendar (id_calendar) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX idx_country_numeric_code ON country (country_numeric_code);

CREATE UNIQUE INDEX alfa_2_code ON country (alfa_2_code);

CREATE UNIQUE INDEX alfa_3_code ON country (alfa_3_code);

ALTER TABLE public.country OWNER TO sofie;

CREATE SEQUENCE country_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE public.country_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- issuer
-- ----------------------------------------------------------------------------
CREATE TABLE issuer (
    id_issuer integer NOT NULL,
    short_issuer_name varchar(25) NOT NULL DEFAULT '',
    long_issuer_name varchar(255) NOT NULL,
    country integer NOT NULL,
    PRIMARY KEY (id_issuer),
    CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES country (id_country) ON DELETE NO ACTION ON UPDATE NO ACTION
);

ALTER TABLE public.issuer OWNER TO sofie;

CREATE SEQUENCE issuer_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE public.issuer_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- super_class
-- ----------------------------------------------------------------------------
CREATE TABLE super_class (
    id_super_class integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(225) NOT NULL DEFAULT '',
    PRIMARY KEY (id_super_class)
);

CREATE UNIQUE INDEX idx_super_class_code ON super_class (code);

ALTER TABLE super_class OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE super_class_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE super_class_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- asset_class
-- ----------------------------------------------------------------------------
CREATE TABLE asset_class (
    id_asset_class integer NOT NULL,
    super_class integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(225) NOT NULL DEFAULT '',
    PRIMARY KEY (id_asset_class),
    CONSTRAINT fk_super_class FOREIGN KEY (super_class) REFERENCES super_class (id_super_class) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX idx_asset_class_code ON asset_class (code);

ALTER TABLE asset_class OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE asset_class_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE asset_class_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- master_data tabella base per anagrafica (securities,loan...)
-- ----------------------------------------------------------------------------
CREATE TABLE master_data (
    id_master_data integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL DEFAULT '',
    currency integer NOT NULL,
    issue_date date NOT NULL,
    maturity_date date NOT NULL,
    type_of_interest integer NOT NULL,
    form integer NOT NULL,
    daycount integer NOT NULL,
    accrual_daycount integer NOT NULL,
    frequency integer NOT NULL,
    roll_convention integer NOT NULL DEFAULT 0,
    accrual_schedule_type integer NOT NULL DEFAULT 0,
    interest_rate numeric(23, 10) NOT NULL,
    issue_price numeric(15, 5) NOT NULL,
    redempion_price numeric(15, 5) NOT NULL,
    business_days integer NOT NULL DEFAULT 2,
    asset_class integer NOT NULL,
    amortization_schedule integer NOT NULL,
    multiplier numeric(15, 5) NOT NULL DEFAULT 1, -- nel caso dei Bond sara 0.01
    PRIMARY KEY (id_master_data),
    CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES daycount (id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_accrual_daycount FOREIGN KEY (accrual_daycount) REFERENCES daycount (id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_frequency FOREIGN KEY (frequency) REFERENCES frequency (id_frequency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_form FOREIGN KEY (form) REFERENCES form (id_form) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_roll_convention FOREIGN KEY (roll_convention) REFERENCES roll_convention (id_roll_convention) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_type_of_interest FOREIGN KEY (type_of_interest) REFERENCES type_of_interest (id_type_of_interest) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_accrual_schedule_type FOREIGN KEY (accrual_schedule_type) REFERENCES accrual_schedule_type (id_accrual_schedule_type) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_asset_class FOREIGN KEY (asset_class) REFERENCES asset_class (id_asset_class) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_amortization_schedule FOREIGN KEY (amortization_schedule) REFERENCES amortization_schedule (id_amortization_schedule) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX idx_master_data_code ON master_data (code);

ALTER TABLE master_data OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE master_data_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE master_data_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- instrument_valuation tabella valutazione strumento relativo a master_data
-- ----------------------------------------------------------------------------
CREATE TABLE instrument_valuation(
    instrument_valuation_id integer NOT NULL,   
    master_data integer NOT NULL,   
    market_price numeric(15, 5) NOT NULL, -- prezzo di mercato a cui potrei vendere (sempre bid)
    accrued_interest numeric(15, 5) NOT NULL DEFAULT 0, -- accruals
    ytm numeric(15, 5) NOT NULL DEFAULT 0, -- yield-to-maturity
    duration numeric(15, 5) NOT NULL DEFAULT 0, -- duration 
    mod_duration numeric(15, 5) NOT NULL DEFAULT 0, -- modified duration
    theoretical_price numeric(15, 5) NOT NULL DEFAULT 0, -- prezzo teorico
    dv01 numeric(15, 5) NOT NULL DEFAULT 0, -- dv01
    -- corrisponde a official date del sistema. Da questa si calcola settlement 
    -- aggiungendo business days
    valuation_date date NOT NULL DEFAULT NOW(), 
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
    PRIMARY KEY (instrument_valuation_id)
);
ALTER TABLE instrument_valuation OWNER TO sofie;
-- Creo sequenza
CREATE SEQUENCE instrument_valuation_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE instrument_valuation_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- loan_master_data - anagrafica mutui
-- ----------------------------------------------------------------------------
-- code = FRM20460700010
-- FRM mutuo a tasso fisso (VRM variable rate mortage mutuo a tasso variabile) type_of_interest
-- 2046 anno scadenza
-- 07 mese scadenza
-- 000010 valore da sequenza master_data_code_s
CREATE TABLE loan_master_data (
    id_master_data integer NOT NULL,
    description varchar(255) NOT NULL DEFAULT '',
    processing_fees numeric(23, 10) NOT NULL DEFAULT 0, -- Spese di incasso rata
    management_fees numeric(23, 10) NOT NULL DEFAULT 0, -- Spese di gestione pratica
    incidental_expenses numeric(23, 10) NOT NULL DEFAULT 0, -- Oneri accessori
    default_interest numeric(23, 10) NOT NULL DEFAULT 0, -- Interessi di mora
    late_payment_fee numeric(23, 10) NOT NULL DEFAULT 0, -- Penale di mora
    underwriting_fee numeric(23, 10) NOT NULL DEFAULT 0, -- Commissione di istruttoria
    insurance_premium numeric(23, 10) NOT NULL DEFAULT 0, -- Premio assicurativo (scoppio/incendio)
    tax_charges numeric(23, 10) NOT NULL DEFAULT 0, -- Imposta sostitutiva
    PRIMARY KEY (id_master_data)
);

ALTER TABLE loan_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- security_master_data - anagrafica titoli di stato
-- ----------------------------------------------------------------------------
CREATE TABLE security_master_data (
    id_master_data integer NOT NULL,
    isin varchar(25) NOT NULL,
    cfi_code varchar(25) NOT NULL,
    fisn varchar(255) NOT NULL,
    lei varchar(255) NOT NULL,
    issuer integer NOT NULL,
    nominal_value numeric(23, 10) NOT NULL,
    first_coupon_rate numeric(23, 10) NOT NULL,
    first_coupon_payment_date date NOT NULL,
    CONSTRAINT fk_issuer FOREIGN KEY (issuer) REFERENCES issuer (id_issuer) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);

CREATE UNIQUE INDEX idx_security_master_data_isin ON security_master_data (isin);

ALTER TABLE security_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- forex_master_data - anagrafica forex
-- ----------------------------------------------------------------------------
CREATE TABLE forex_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL, -- link alla bcy
    ccy integer NOT NULL, -- link alla ccy
    bcy_irc varchar(25) NOT NULL DEFAULT '', -- bcy interest rate curve
    ccy_irc varchar(25) NOT NULL DEFAULT '', -- ccy interest rate curve
    CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);

CREATE UNIQUE INDEX idx_forex_bcy_ccy ON forex_master_data (bcy, ccy);

ALTER TABLE forex_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- cash_flow_item
-- ----------------------------------------------------------------------------
CREATE TABLE cash_flow_item (
    id_cash_flow_item integer NOT NULL,
    master_data integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    interest numeric(15, 5) NOT NULL,
    amount numeric(15, 5) NOT NULL,
    known smallint NOT NULL DEFAULT 1, -- cedola fissata
    PRIMARY KEY (id_cash_flow_item),
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX idx_md_ed ON cash_flow_item (master_data, end_date);

ALTER TABLE cash_flow_item OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE cash_flow_item_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE cash_flow_item_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- cash_flow_reset
-- ----------------------------------------------------------------------------
CREATE TABLE cash_flow_reset (
    id_cash_flow_reset integer NOT NULL,
    master_data integer NOT NULL,
    start_date_reset date NOT NULL, -- data reset nuovo coupon
    interest_reset numeric(15, 5) NOT NULL, -- valore nuovo coupon
    PRIMARY KEY (id_cash_flow_reset),
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX idx_md_sdr ON cash_flow_reset (master_data, start_date_reset);

ALTER TABLE cash_flow_reset OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE cash_flow_reset_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE cash_flow_reset_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- yield_curve
-- ----------------------------------------------------------------------------
CREATE TABLE yield_curve (
    id_yield_curve integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(225) NOT NULL DEFAULT '',
    currency integer NOT NULL,
    calendar integer NOT NULL,
    compounding smallint NOT NULL DEFAULT 1,
    provider varchar(50) NOT NULL DEFAULT '',
    PRIMARY KEY (id_yield_curve),
    CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES calendar (id_calendar) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX idx_yield_curve_code ON yield_curve (code);

ALTER TABLE yield_curve OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE yield_curve_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE yield_curve_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- yield_curve_item
-- ----------------------------------------------------------------------------
CREATE TABLE yield_curve_item (
    id_yield_curve_item integer NOT NULL,
    yield_curve integer NOT NULL,
    ric varchar(25) NOT NULL,
    offset_type smallint NOT NULL,
    offset_value smallint NOT NULL,
    bid numeric(15, 5) NOT NULL,
    ask numeric(15, 5) NOT NULL,
    compounding smallint NOT NULL DEFAULT 1,
    daycount smallint NOT NULL,
    PRIMARY KEY (id_yield_curve_item),
    CONSTRAINT fk_yield_curve FOREIGN KEY (yield_curve) REFERENCES yield_curve (id_yield_curve) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES daycount (id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE UNIQUE INDEX idx_yield_curve_item_ric ON yield_curve_item (ric, yield_curve);

ALTER TABLE yield_curve_item OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE yield_curve_item_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE yield_curve_item_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- settlement_type
-- ----------------------------------------------------------------------------
CREATE TABLE settlement_type (
    id_settlement_type integer NOT NULL,
    code varchar(25),
    description varchar(25) NOT NULL DEFAULT '',
    PRIMARY KEY (id_settlement_type)
);

CREATE UNIQUE INDEX idx_settlement_type_code ON settlement_type (code);

ALTER TABLE settlement_type OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE settlement_type_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE settlement_type_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- future_master_data - anagrafica future
-- ----------------------------------------------------------------------------
CREATE TABLE future_master_data (
    id_master_data integer NOT NULL,
    isin varchar(25) NOT NULL, -- isin contratto principale
    exchange_contract_code varchar(25) NOT NULL DEFAULT '',
    settlement_type integer NOT NULL,
    last_trading_date date NOT NULL,
    CONSTRAINT fk_settlement_type FOREIGN KEY (settlement_type) REFERENCES settlement_type (id_settlement_type) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);

CREATE UNIQUE INDEX idx_future_master_data_isin ON future_master_data (isin);

ALTER TABLE future_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- bond_future_master_data - anagrafica bond future
-- ----------------------------------------------------------------------------
CREATE TABLE bond_future_master_data (
    id_master_data integer NOT NULL,
    contract_value numeric(15, 5) NOT NULL,
    tick_size numeric(15, 5) NOT NULL,
    initial_margin numeric(15, 5) NOT NULL,
    PRIMARY KEY (id_master_data)
);

ALTER TABLE bond_future_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- deliverable_bonds - consegnabili
-- ----------------------------------------------------------------------------
CREATE TABLE deliverable_bonds (
    id_deliverable_bonds integer NOT NULL,
    master_data integer NOT NULL, -- future
    expiration_date date NOT NULL, -- scadenza future
    isin varchar(25) NOT NULL, -- isin consegnabile
    coupon_rate numeric(15, 5) NOT NULL, -- coupon consegnabile
    bond_maturity date NOT NULL, -- scadenza consegnabile
    bond_cf numeric(15, 5) NOT NULL, -- conversion factor
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES bond_future_master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_deliverable_bonds)
);

CREATE UNIQUE INDEX idx_deliverable_bonds_isin ON deliverable_bonds (master_data, isin);

ALTER TABLE bond_future_master_data OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE deliverable_bonds_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE deliverable_bonds_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- fx_future_master_data - anagrafica forex future
-- ----------------------------------------------------------------------------
CREATE TABLE fx_future_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL, -- coppia sottostante il contratto
    contract_value numeric(15, 5) NOT NULL,
    tick_size numeric(15, 5) NOT NULL,
    initial_margin numeric(15, 5) NOT NULL,
    maintenance_margin numeric(15, 5) NOT NULL,
    CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES forex_master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);

ALTER TABLE fx_future_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- mm_future_master_data - anagrafica money market future
-- ----------------------------------------------------------------------------
CREATE TABLE mm_future_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL, -- coppia sottostante il contratto
    contract_value numeric(15, 5) NOT NULL,
    tick_size numeric(15, 5) NOT NULL,
    initial_margin numeric(15, 5) NOT NULL,
    maintenance_margin numeric(15, 5) NOT NULL,
    CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES forex_master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);

ALTER TABLE mm_future_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- instrument_quote
-- ----------------------------------------------------------------------------
CREATE TABLE instrument_quote (
    id_instrument_quote integer NOT NULL,
    master_data integer NOT NULL, 
    provider varchar(50) NOT NULL,
    code varchar(255) NOT NULL,
    bid numeric(15, 5) NOT NULL,
    ask numeric(15, 5) NOT NULL,
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_instrument_quote)
);

CREATE UNIQUE INDEX idx_instrument_quote_code ON instrument_quote (code);

ALTER TABLE instrument_quote OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE instrument_quote_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE instrument_quote_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- instrument_quote_hist
-- ----------------------------------------------------------------------------
CREATE TABLE instrument_quote_hist (
    id_instrument_quote_hist integer NOT NULL,
    instrument_quote integer NOT NULL,
    master_data integer NOT NULL, -- future
    code varchar(25) NOT NULL,
    bid numeric(15, 5) NOT NULL,
    ask numeric(15, 5) NOT NULL,
    update_date date NOT NULL,
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_instrument_quote FOREIGN KEY (instrument_quote) REFERENCES instrument_quote (id_instrument_quote) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_instrument_quote_hist)
);

CREATE UNIQUE INDEX idx_instrument_quote_hist_iqud ON instrument_quote_hist (instrument_quote, update_date);

ALTER TABLE instrument_quote_hist OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE instrument_quote_hist_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE instrument_quote_hist_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- currpair_master_data - anagrafica currency pair
-- ----------------------------------------------------------------------------
CREATE TABLE currpair_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL, -- link alla bcy
    ccy integer NOT NULL, -- link alla ccy
    bcy_irc varchar(25) NOT NULL DEFAULT '', -- bcy interest rate curve
    ccy_irc varchar(25) NOT NULL DEFAULT '', -- ccy interest rate curve
    CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);

CREATE UNIQUE INDEX idx_currpair_bcy_ccy ON currpair_master_data (bcy, ccy);

ALTER TABLE currpair_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- counterparty_type - tipologia controparte
-- ----------------------------------------------------------------------------
CREATE TABLE counterparty_type (
    id_counterparty_type integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (id_counterparty_type)
);

CREATE UNIQUE INDEX idx_counterparty_type_code ON counterparty_type (code);

ALTER TABLE counterparty_type OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE counterparty_type_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE counterparty_type_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- counterparty - controparte della transazione
-- ----------------------------------------------------------------------------
CREATE TABLE counterparty (
    id_counterparty integer NOT NULL,
    ctp_type integer NOT NULL,
    lei_code varchar(50),
    country integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    CONSTRAINT fk_ctp_type FOREIGN KEY (ctp_type) REFERENCES counterparty_type (id_counterparty_type) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES country (id_country) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_counterparty)
);

CREATE UNIQUE INDEX idx_counterparty_code ON counterparty (code);

ALTER TABLE counterparty OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE counterparty_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE counterparty_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- portfolio_master_data - anagrafica posizione
-- ----------------------------------------------------------------------------
CREATE TABLE portfolio_master_data (
    id_portfolio integer NOT NULL,
    currency integer NOT NULL, -- divisa del portfolio
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL, -- legal name
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_portfolio)
);

CREATE UNIQUE INDEX idx_portfolio_code ON portfolio_master_data (code);

ALTER TABLE portfolio_master_data OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE portfolio_master_data_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE portfolio_master_data_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- position_master_data - anagrafica posizione
-- ----------------------------------------------------------------------------
CREATE TABLE position_master_data (
    id_position integer NOT NULL,
    portfolio integer NOT NULL,
    currency integer NOT NULL, -- divisa della posizione
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL, -- legal name
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_portfolio FOREIGN KEY (portfolio) REFERENCES portfolio_master_data (id_portfolio) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_position)
);

CREATE UNIQUE INDEX idx_position_code ON position_master_data (code);

ALTER TABLE position_master_data OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE position_master_data_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE position_master_data_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- position_detail - dettaglio posizione per strumento
-- ----------------------------------------------------------------------------
CREATE TABLE position_detail (
    id_position_detail integer NOT NULL,
    position_md integer NOT NULL, -- posizione
    master_data integer NOT NULL, -- identificativo strumento (ogni strumento lavora con la propria divisa)
    counterparty integer NOT NULL,
    realized_pnl numeric(15, 5) NOT NULL,
    unrealized_pnl numeric(15, 5) NOT NULL,
    buy_qty numeric(15, 5) NOT NULL, -- quantita acquistata (incrementale)
    notional_value_buy numeric(15, 5) NOT NULL, -- controvalore di acquisto (incrementale)
    buy_fees numeric(15, 5) NOT NULL, -- commissioni di acquisto (incrementale)
    buy_taxes numeric(15, 5) NOT NULL, -- tasse di transazione sull'acquisto
    sell_qty numeric(15, 5) NOT NULL, -- quantita venduta (incrementale)
    notional_value_sell numeric(15, 5) NOT NULL, -- controvalore di vendita (incrementale)
    sell_fees numeric(15, 5) NOT NULL, -- commissioni di vendita (incrementale)
    sell_taxes numeric(15, 5) NOT NULL, -- tasse di transazione sulla vendita
    market_price numeric(15, 5) NOT NULL, -- prezzo di mercato a cui potrei vendere (sempre bid)
    buy_accrual numeric(15, 5) NOT NULL DEFAULT 0, -- rateo maturato posizione long
    sell_accrual numeric(15, 5) NOT NULL DEFAULT 0, -- rateo maturato posizione short
    ytm numeric(15, 5) NOT NULL DEFAULT 0, -- yield-to-maturity
    duration numeric(15, 5) NOT NULL DEFAULT 0, -- duration 
    mod_duration numeric(15, 5) NOT NULL DEFAULT 0, -- modified duration
    theoretical_price numeric(15, 5) NOT NULL DEFAULT 0, -- prezzo teorico
    official_date date NOT NULL DEFAULT NOW(), 
    last_mtm_executed TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() - INTERVAL '1 hour',
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES counterparty (id_counterparty) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES position_master_data (id_position) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_position_detail)
);

ALTER TABLE position_detail OWNER TO sofie;
CREATE UNIQUE INDEX idx_position_detail_pmc ON position_detail (position_md, master_data, counterparty);

-- Creo sequenza
CREATE SEQUENCE position_detail_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE position_detail_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- txn_status - stato
-- ----------------------------------------------------------------------------
CREATE TABLE txn_status (
    id_txn_status integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (id_txn_status)
);
CREATE UNIQUE INDEX idx_txn_status_code ON txn_status (code);
ALTER TABLE txn_status OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- txn_side - side
-- ----------------------------------------------------------------------------
CREATE TABLE txn_side (
    id_txn_side integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (id_txn_side)
);
CREATE UNIQUE INDEX idx_txn_side_code ON txn_side (code);
ALTER TABLE txn_side OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- txn_accounting_phase - stato contabile
-- ----------------------------------------------------------------------------
CREATE TABLE txn_accounting_phase (
    acct_phase_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (acct_phase_id)
);
CREATE UNIQUE INDEX idx_acct_phase_code ON txn_accounting_phase (code);
ALTER TABLE txn_accounting_phase OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- financial_txn - transazione finanziaria
-- ----------------------------------------------------------------------------
CREATE TABLE financial_txn (
    id_financial_txn integer NOT NULL,
    counterparty integer NOT NULL,
    position_md integer NOT NULL, -- position
    master_data integer NOT NULL, -- identificativo strumento
    txn_status integer NOT NULL, -- stato transazione
    txn_status_pre_elab integer NOT NULL, -- stato transazione pre processamento (iniziale)
    txn_acct_phase integer NOT NULL DEFAULT 1, -- stato contabile transazione
    txn_side smallint NOT NULL, -- (Buy/Sell)
    description varchar(255) NOT NULL,
    trade_date date NOT NULL DEFAULT NOW(), -- esecuzione deal
    value_date date NOT NULL, -- data valuta cash
    settlement date NOT NULL, -- regolamento/contabilizzazione
    quantity numeric(15, 5) NOT NULL,
    price numeric(15, 5) NOT NULL,
    fx_rate numeric(15, 5) NOT NULL DEFAULT 1, -- cambio al momento del trade
    ref_id integer NOT NULL,
    version integer DEFAULT 0,
    PRIMARY KEY (id_financial_txn),
    CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES counterparty (id_counterparty) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES position_master_data (id_position) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_txn_status FOREIGN KEY (txn_status) REFERENCES txn_status (id_txn_status) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_txn_acct_phase FOREIGN KEY (txn_acct_phase) REFERENCES txn_accounting_phase(acct_phase_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

ALTER TABLE financial_txn OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE financial_txn_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE financial_txn_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- position_txn_links - link position txn
-- ----------------------------------------------------------------------------
CREATE TABLE position_txn_links (
    pos_txn_link_id integer NOT NULL,
    position_detail integer NOT NULL,
    financial_txn integer NOT NULL,
    txn_acct_phase integer NOT NULL, 
    settlement date NOT NULL, -- regolamento/contabilizzazione
    quantity numeric(15, 5) NOT NULL,
    price numeric(15, 5) NOT NULL,
    fx_rate numeric(15, 5) NOT NULL DEFAULT 1, -- cambio al momento del trade
    PRIMARY KEY (pos_txn_link_id),
    CONSTRAINT fk_link_pos FOREIGN KEY (position_detail) REFERENCES position_detail (id_position_detail) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_link_txn FOREIGN KEY (financial_txn) REFERENCES financial_txn (id_financial_txn) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_link_acct_phase FOREIGN KEY (txn_acct_phase) REFERENCES txn_accounting_phase(acct_phase_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);
ALTER TABLE position_txn_links OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE position_txn_links_s
   START WITH 1
   INCREMENT BY 1;

ALTER SEQUENCE position_txn_links_s
   OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- txn_component_type - tabella lookup component type mappada da enum ComponentType
-- ----------------------------------------------------------------------------
CREATE TABLE txn_component_types (
    component_type_id integer NOT NULL,
    code varchar(25) NOT NULL UNIQUE, -- 'BROKER_FEE', 'INITIAL_MARGIN', 'EXCHANGE_FEE', 'PREMIUM'
    description varchar(150) NOT NULL,
    PRIMARY KEY (component_type_id)
);

ALTER TABLE txn_component_types OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- financial_txn_component - tabella di dettaglio che si aggancia alla financial_txn
-- ----------------------------------------------------------------------------
CREATE TABLE financial_txn_components (
    txn_component_id integer NOT NULL,
    financial_txn integer NOT NULL,
    component_type integer NOT NULL,
    currency integer NOT NULL, -- Fondamentale: le commissioni potrebbero essere in EUR anche se il trade è in USD
    amount numeric(15, 5) NOT NULL,
    description varchar(255) NOT NULL DEFAULT '',
    PRIMARY KEY (txn_component_id),
    CONSTRAINT fk_financial_txn FOREIGN KEY (financial_txn) REFERENCES financial_txn (id_financial_txn) ON DELETE CASCADE,
    CONSTRAINT fk_component_type FOREIGN KEY (component_type) REFERENCES txn_component_types (component_type_id),
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency)
);

ALTER TABLE financial_txn_components OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE financial_txn_components_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE financial_txn_components_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- counterparty_roles
-- ----------------------------------------------------------------------------
CREATE TABLE counterparty_roles (
    role_id integer NOT NULL,
    code varchar(50) NOT NULL UNIQUE,
    description varchar(150) NOT NULL,
    PRIMARY KEY (role_id)
);

ALTER TABLE counterparty_roles OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- counterparty_roles
-- Tabella di raccordo Molti-a-Molti (Una controparte può essere sia VENDOR che BROKER)
-- ----------------------------------------------------------------------------
CREATE TABLE counterparty_role_mapping (
    counterparty_role_mapping_id integer NOT NULL,
    counterparty integer NOT NULL,
    ctp_role integer NOT NULL,
    PRIMARY KEY (counterparty_role_mapping_id),
    CONSTRAINT fk_mapping_counterparty FOREIGN KEY (counterparty) REFERENCES counterparty (id_counterparty) ON DELETE CASCADE,
    CONSTRAINT fk_mapping_role FOREIGN KEY (ctp_role) REFERENCES counterparty_roles (role_id)
);

CREATE UNIQUE INDEX idx_ctp_role ON counterparty_role_mapping (counterparty, ctp_role);

ALTER TABLE counterparty_role_mapping OWNER TO sofie;

CREATE SEQUENCE counterparty_role_mapping_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE counterparty_role_mapping_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- broker_instrument_rules
-- definisce le regole di costo per quella specifica combinazione di Broker, Strumento e Lato (Buy/Sell).
-- ----------------------------------------------------------------------------
CREATE TABLE broker_instrument_rules (
    broker_rule_id integer NOT NULL,
    broker integer NOT NULL, -- Punta a counterparty(id_counterparty)
    master_data integer NOT NULL, -- Punta a master_data(id_master_data)
    txn_side smallint NOT NULL, -- (1 = Buy, 2 = Sell, o un Enum)
    initial_margin numeric(15, 5) NOT NULL DEFAULT 0.0,
    maintenance_margin numeric(15, 5) NOT NULL DEFAULT 0.0,
    broker_fee numeric(15, 5) NOT NULL DEFAULT 0.0,
    exchange_fee numeric(15, 5) NOT NULL DEFAULT 0.0,
    currency integer NOT NULL, -- Valuta della fee (es. USD per CME)
    PRIMARY KEY (broker_rule_id),
    CONSTRAINT fk_rule_broker FOREIGN KEY (broker) REFERENCES counterparty (id_counterparty),
    CONSTRAINT fk_rule_instrument FOREIGN KEY (master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_rule_currency FOREIGN KEY (currency) REFERENCES currency (id_currency),
    -- Vincolo di unicità: non possono esserci due regole identiche per lo stesso broker/strumento/lato
    CONSTRAINT idx_broker_inst_side UNIQUE (broker, master_data, txn_side)
);

ALTER TABLE broker_instrument_rules OWNER TO sofie;

CREATE SEQUENCE IF NOT EXISTS broker_instrument_rules_s START WITH 1
INCREMENT BY 1;

ALTER SEQUENCE broker_instrument_rules_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- sbc_status
-- ----------------------------------------------------------------------------
CREATE TABLE sbc_status (
    sbc_status_id integer NOT NULL,
    code varchar(50) NOT NULL UNIQUE, -- OPEN CLOSING CLOSED LOCKED
    description varchar(150) NOT NULL,
    PRIMARY KEY (sbc_status_id)
);
ALTER TABLE sbc_status OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- system_business_calendar
-- ----------------------------------------------------------------------------
CREATE TABLE system_business_calendar (
    sbc_id integer NOT NULL,
    description varchar(50) NOT NULL,
    calendar integer, 
    currency integer NOT NULL UNIQUE, 
    status integer NOT NULL,  
    last_official_date  DATE NOT NULL,
    official_date       DATE NOT NULL,
    next_business_date  DATE,
    version INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (sbc_id),
    CONSTRAINT fk_status FOREIGN KEY (status) REFERENCES sbc_status (sbc_status_id),
    CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES calendar (id_calendar),
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency)
);
ALTER TABLE system_business_calendar OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- commodity_type
-- ----------------------------------------------------------------------------
CREATE TABLE commodity_type (
    commodity_type_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (commodity_type_id)
);
CREATE UNIQUE INDEX commodity_type_code ON daycount (code);
ALTER TABLE commodity_type OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- cmd_future_master_data - anagrafica commodity future
-- ----------------------------------------------------------------------------
CREATE TABLE cmd_future_master_data (
    id_master_data integer NOT NULL,
    commodity_type integer NOT NULL,
    contract_value numeric(15, 5) NOT NULL,
    tick_size numeric(15, 5) NOT NULL,
    initial_margin numeric(15, 5) NOT NULL,
    maintenance_margin numeric(15, 5) NOT NULL,
    CONSTRAINT fk_commodity_type FOREIGN KEY (commodity_type) REFERENCES commodity_type (commodity_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);

ALTER TABLE cmd_future_master_data OWNER TO sofie;
