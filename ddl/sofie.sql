
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
-- CouponProjectionMethod
-- ----------------------------------------------------------------------------
CREATE TABLE projection_method (
    projection_method_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(50) NOT NULL,
    PRIMARY KEY (projection_method_id)
);
CREATE UNIQUE INDEX idx_pm_code ON projection_method (code);
ALTER TABLE projection_method OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- CurveNodeType
-- ----------------------------------------------------------------------------
CREATE TABLE node_type (
    node_type_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(50) NOT NULL,
    PRIMARY KEY (node_type_id)
);
CREATE UNIQUE INDEX idx_node_type_code ON node_type(code);
ALTER TABLE node_type OWNER TO sofie;

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
    decimal_places smallint NOT NULL DEFAULT 2,
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
-- master_data tabella base per anagrafica: strumento negoziabile
--   master_data
--     ├── cash_flow_master_data    strumenti con flussi
--     │     ├── security_master_data -> flt_security_master_data
--     │     └── loan_master_data
--     ├── future_master_data       (sezione FUTURE)
--     └── forex_master_data
-- ----------------------------------------------------------------------------
CREATE TABLE master_data (
    id_master_data integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) DEFAULT '',
    currency integer NOT NULL,
    issue_date date NOT NULL,
    maturity_date date NOT NULL,
    business_days integer NOT NULL DEFAULT 2,
    asset_class integer NOT NULL,
    multiplier numeric(15, 5) NOT NULL DEFAULT 1, -- nel caso dei Bond sara 0.01
    PRIMARY KEY (id_master_data),
    CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES currency (id_currency) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_asset_class FOREIGN KEY (asset_class) REFERENCES asset_class (id_asset_class) ON DELETE NO ACTION ON UPDATE NO ACTION
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
-- cash_flow_master_data - strumenti con flussi (titoli, mutui): convenzioni
-- di cedole e ratei, prezzi di emissione e rimborso, forma; piano dei flussi
-- in cash_flow_item e cash_flow_reset
-- ----------------------------------------------------------------------------
CREATE TABLE cash_flow_master_data (
    id_master_data integer NOT NULL,
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
    amortization_schedule integer NOT NULL,
    compounding integer NOT NULL DEFAULT 2,
    CONSTRAINT pk_cash_flow_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_cash_flow_master_data_parent FOREIGN KEY (id_master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_cfmd_type_of_interest FOREIGN KEY (type_of_interest) REFERENCES type_of_interest (id_type_of_interest),
    CONSTRAINT fk_cfmd_form FOREIGN KEY (form) REFERENCES form (id_form),
    CONSTRAINT fk_cfmd_daycount FOREIGN KEY (daycount) REFERENCES daycount (id_daycount),
    CONSTRAINT fk_cfmd_accrual_daycount FOREIGN KEY (accrual_daycount) REFERENCES daycount (id_daycount),
    CONSTRAINT fk_cfmd_frequency FOREIGN KEY (frequency) REFERENCES frequency (id_frequency),
    CONSTRAINT fk_cfmd_roll_convention FOREIGN KEY (roll_convention) REFERENCES roll_convention (id_roll_convention),
    CONSTRAINT fk_cfmd_accrual_schedule_type FOREIGN KEY (accrual_schedule_type) REFERENCES accrual_schedule_type (id_accrual_schedule_type),
    CONSTRAINT fk_cfmd_amortization_schedule FOREIGN KEY (amortization_schedule) REFERENCES amortization_schedule (id_amortization_schedule),
    CONSTRAINT fk_cfmd_compounding FOREIGN KEY (compounding) REFERENCES compounding (id_compounding)
);
ALTER TABLE cash_flow_master_data OWNER TO sofie;

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
    CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (instrument_valuation_id)
);
ALTER TABLE instrument_valuation OWNER TO sofie;
-- nessuna sequenza: instrument_valuation_id = id_master_data dello strumento

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
    CONSTRAINT fk_loan_master_data_parent FOREIGN KEY (id_master_data) REFERENCES cash_flow_master_data (id_master_data),
    PRIMARY KEY (id_master_data)
);
ALTER TABLE loan_master_data OWNER TO sofie;

-- projection_method: definita nella sezione CouponProjectionMethod

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
    CONSTRAINT fk_security_master_data_parent FOREIGN KEY (id_master_data) REFERENCES cash_flow_master_data (id_master_data),
    PRIMARY KEY (id_master_data)
);
CREATE UNIQUE INDEX idx_security_master_data_isin ON security_master_data (isin);
ALTER TABLE security_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- tenor - anagrafica tenor
-- ----------------------------------------------------------------------------
CREATE TABLE tenor (
    tenor_id    INTEGER NOT NULL,
    code        VARCHAR(20) NOT NULL,
    description VARCHAR(50) NOT NULL,

    CONSTRAINT pk_tenor PRIMARY KEY (tenor_id),
    CONSTRAINT uk_tenor_code UNIQUE (code)
);
ALTER TABLE tenor OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- ref_rate_index - anagrafica indici
-- ----------------------------------------------------------------------------
CREATE TABLE ref_rate_index (
    ref_rate_index_id INTEGER NOT NULL,
    code              VARCHAR(25) NOT NULL,
    description       VARCHAR(100) NOT NULL,
    currency          INTEGER NOT NULL,
    daycount          INTEGER NOT NULL,
    tenor             INTEGER NOT NULL,

    CONSTRAINT pk_ref_rate_index
        PRIMARY KEY (ref_rate_index_id),

    CONSTRAINT fk_ref_rate_index_currency
        FOREIGN KEY (currency)
        REFERENCES currency (id_currency),

    CONSTRAINT fk_ref_rate_index_daycount
        FOREIGN KEY (daycount)
        REFERENCES daycount (id_daycount),

    CONSTRAINT fk_ref_rate_index_tenor
        FOREIGN KEY (tenor)
        REFERENCES tenor (tenor_id),
    
    CONSTRAINT uk_ref_rate_index_code_tenor
        UNIQUE (code, tenor)
);
ALTER TABLE ref_rate_index OWNER TO sofie;
-- Creo sequenza
CREATE SEQUENCE ref_rate_index_s
    START WITH 1
    INCREMENT BY 1;
ALTER SEQUENCE ref_rate_index_s OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- ref_rate_fixing - valori corrispondenti all'indici
-- ----------------------------------------------------------------------------
CREATE TABLE ref_rate_fixing (
    ref_rate_fixing_id INTEGER NOT NULL,
    ref_rate_index     INTEGER NOT NULL,
    fixing_date        DATE NOT NULL,
    rate               NUMERIC(18,10) NOT NULL,

    CONSTRAINT pk_ref_rate_fixing
        PRIMARY KEY (ref_rate_fixing_id),

    CONSTRAINT fk_ref_rate_fixing_index
        FOREIGN KEY (ref_rate_index)
        REFERENCES ref_rate_index (ref_rate_index_id),

    CONSTRAINT uk_ref_rate_fixing
        UNIQUE (ref_rate_index, fixing_date)
);
ALTER TABLE ref_rate_fixing OWNER TO sofie;
-- Creo sequenza
CREATE SEQUENCE ref_rate_fixing_s
    START WITH 1
    INCREMENT BY 1;
ALTER SEQUENCE ref_rate_fixing_s OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- coupon_pm - Coupon Projection Method
-- ----------------------------------------------------------------------------
CREATE TABLE coupon_pm (
    coupon_pm_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(50) NOT NULL,
    PRIMARY KEY (coupon_pm_id)
);
ALTER TABLE coupon_pm OWNER TO sofie;
CREATE UNIQUE INDEX coupon_pm_code ON coupon_pm (code);

-- ----------------------------------------------------------------------------
-- fixing_day_type 
-- ----------------------------------------------------------------------------
CREATE TABLE fixing_day_type (
    fixing_day_type_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(50) NOT NULL,
    PRIMARY KEY (fixing_day_type_id)
);
ALTER TABLE fixing_day_type OWNER TO sofie;
CREATE UNIQUE INDEX coupon_FDT_code ON fixing_day_type (code);

-- ----------------------------------------------------------------------------
-- flt_security_master_data - anagrafica titoli di stato floating
-- ----------------------------------------------------------------------------
CREATE TABLE flt_security_master_data (
    id_master_data integer NOT NULL,
    spread numeric(23, 10) NOT NULL,
    floor numeric(18,10),
    fixing_lag_days integer not null default 2,
    index_months integer not null default 6,
    coupon_pm integer NOT NULL,
    projection_index INTEGER,
    projection_curve  integer,
    fixing_day_type INTEGER NOT NULL DEFAULT 2,
    CONSTRAINT fk_coupon_pm FOREIGN KEY (coupon_pm) REFERENCES coupon_pm (coupon_pm_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_projection_index FOREIGN KEY (projection_index) REFERENCES ref_rate_index (ref_rate_index_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (id_master_data)
);
ALTER TABLE flt_security_master_data OWNER TO sofie;

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
-- cash_flow_status
-- ----------------------------------------------------------------------------
CREATE TABLE cash_flow_status (
    cash_flow_status_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(50) NOT NULL,
    PRIMARY KEY (cash_flow_status_id)
);
ALTER TABLE cash_flow_status OWNER TO sofie;
CREATE UNIQUE INDEX cash_flow_status_code ON cash_flow_status (code);

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
    cash_flow_status integer NOT NULL DEFAULT 2, -- cedola fissata
    PRIMARY KEY (id_cash_flow_item),
    CONSTRAINT fk_cash_flow_item_master_data FOREIGN KEY (master_data) REFERENCES cash_flow_master_data (id_master_data),
    CONSTRAINT cash_flow_status FOREIGN KEY (cash_flow_status) REFERENCES cash_flow_status (cash_flow_status_id) ON DELETE NO ACTION ON UPDATE NO ACTION
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
    CONSTRAINT fk_cash_flow_reset_master_data FOREIGN KEY (master_data) REFERENCES cash_flow_master_data (id_master_data)
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
    compounding integer NOT NULL DEFAULT 1,
    daycount integer NOT NULL,
    node_type integer not null default 1,
    PRIMARY KEY (id_yield_curve_item),
    CONSTRAINT fk_yield_curve FOREIGN KEY (yield_curve) REFERENCES yield_curve (id_yield_curve) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES daycount (id_daycount) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_node_type FOREIGN KEY (node_type) REFERENCES node_type (node_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION
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
-- calibration_profile
-- ----------------------------------------------------------------------------
CREATE TABLE calibration_profile (
    calibration_profile_id INTEGER NOT NULL,
    code VARCHAR(25),  
    description VARCHAR(200),  
    currency INTEGER NOT NULL,
    last_update DATE NOT NULL, -- reference date of the calibration
    CONSTRAINT pk_calibration_profile PRIMARY KEY (calibration_profile_id),
    CONSTRAINT uq_calibration_profile_code UNIQUE (code),
    CONSTRAINT fk_cpi_currency FOREIGN KEY (currency) REFERENCES currency (id_currency)
);
ALTER TABLE calibration_profile OWNER TO sofie;
CREATE SEQUENCE calibration_profile_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE calibration_profile_s OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- calibration_profile_item
-- ----------------------------------------------------------------------------
CREATE TABLE calibration_profile_item (
    calibration_profile_item_id INTEGER NOT NULL,
    calibration_profile INTEGER NOT NULL,
    master_data INTEGER NOT NULL,
    instrument_quote INTEGER NOT NULL,
    code VARCHAR(25)  NOT NULL,            
    bid numeric(18,10) NOT NULL DEFAULT 0,
    ask numeric(18,10) NOT NULL DEFAULT 0,
    CONSTRAINT pk_calibration_profile_item PRIMARY KEY (calibration_profile_item_id),
    CONSTRAINT uq_item_cp_md UNIQUE (calibration_profile,master_data),
    CONSTRAINT fk_cpi_calibration_profile FOREIGN KEY (calibration_profile) REFERENCES calibration_profile (calibration_profile_id),
    CONSTRAINT fk_cpi_master_data FOREIGN KEY (master_data) REFERENCES master_data (id_master_data)
    -- fk_cpi_instrument_quote: aggiunta dopo la creazione di instrument_quote
);
ALTER TABLE calibration_profile_item OWNER TO sofie;
CREATE SEQUENCE calibration_profile_item_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE calibration_profile_item_s OWNER TO sofie;

-- A spread curve (e.g. ITA_SPREADED = ECB + calibrated z-spreads) is DEFINED by a base curve plus spreads.
-- It owns no rows in yield_curve_item: its rates come from the base curve, its spreads from yield_curve_spread.
-- PostgreSQL syntax: adapt to your DBMS.
CREATE TABLE spread_curve (
    id_spread_curve    INTEGER      NOT NULL,
    code               VARCHAR(50)  NOT NULL,            -- e.g. ITA_SPREADED (must differ from every yield_curve.code)
    description        VARCHAR(200),
    base_curve         INTEGER      NOT NULL,            -- FK: spread-free curve (e.g. ECB)
    max_age_days       INTEGER      NOT NULL DEFAULT 5,  -- reject spreads calibrated more than N days ago
    min_bonds          INTEGER      NOT NULL DEFAULT 3,  -- calibration: minimum bonds per bucket
    min_valid_buckets  INTEGER      NOT NULL DEFAULT 3,  -- calibration: reject if fewer valid buckets
    calibration_profile INTEGER      NOT NULL DEFAULT 1,
    CONSTRAINT pk_spread_curve PRIMARY KEY (id_spread_curve),
    CONSTRAINT uq_spread_curve_code UNIQUE (code),
    CONSTRAINT fk_sc_base_curve FOREIGN KEY (base_curve) REFERENCES yield_curve (id_yield_curve),
    CONSTRAINT fk_sc_profile FOREIGN KEY (calibration_profile) REFERENCES calibration_profile (calibration_profile_id)
);
ALTER TABLE spread_curve OWNER TO sofie;

CREATE SEQUENCE spread_curve_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE spread_curve_s OWNER TO sofie;


-- ---------------------------------------------------------------------------------------------
-- New installations: create yield_curve_spread directly in its final shape.
-- ---------------------------------------------------------------------------------------------
CREATE TABLE yield_curve_spread (
    id_yield_curve_spread  INTEGER        NOT NULL,
    spread_curve           INTEGER        NOT NULL,   -- FK spread_curve
    as_of_date             DATE           NOT NULL,   -- reference date of the prices / base curve used
    base_curve_code        VARCHAR(50)    NOT NULL,   -- base curve the spreads were CALIBRATED on (audit + load-time check)
    offset_type            SMALLINT       NOT NULL,   -- same coding as yield_curve_item.offset_type
    offset_value           SMALLINT       NOT NULL,   -- bucket tenor (e.g. 5 with offset_type YEARS)
    z_spread               NUMERIC(14,10) NOT NULL,   -- continuous, ACT/365, decimal (0.0095 = 95 bp)
    n_bonds                INTEGER,                   -- bonds used for the bucket (audit)
    dispersion             NUMERIC(14,10),            -- e.g. median absolute deviation (audit)
    calibrated_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_yield_curve_spread PRIMARY KEY (id_yield_curve_spread),
    CONSTRAINT fk_ycs_spread_curve FOREIGN KEY (spread_curve) REFERENCES spread_curve (id_spread_curve),
    CONSTRAINT uq_ycs_bucket UNIQUE (spread_curve, as_of_date, offset_type, offset_value),
    CONSTRAINT ck_ycs_offset_value CHECK (offset_value > 0)
);
CREATE INDEX ix_ycs_curve_date ON yield_curve_spread (spread_curve, as_of_date);
ALTER TABLE yield_curve_spread OWNER TO sofie;

CREATE SEQUENCE yield_curve_spread_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE yield_curve_spread_s OWNER TO sofie;

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

-- ============================================================================
-- FUTURE
--   future_product (testata, NON in master_data)      MBTP, M6E: dati comuni alle scadenze
--     ├── bond_future_product
--     ├── fx_future_product     underlying (coppia)
--     ├── mm_future_product     underlying
--     └── cmd_future_product    commodity_type          (sezione COMMODITY FUTURE)
--           └── power_future_product   load_type
--
--   master_data -> future_master_data                 MBTPZ6: ISIN, mese, ultimo giorno, consegna
--     ├── bond_future_master_data  future_product -> bond_future_product, paniere (deliverable_bonds)
--     ├── fx_future_master_data    future_product -> fx_future_product
--     ├── mm_future_master_data    future_product -> mm_future_product
--     └── cmd_future_master_data   future_product -> cmd_future_product (sezione COMMODITY FUTURE)
--           └── power_future_master_data
--
-- master_data e' la scadenza negoziabile: terna (controparte, posizione,
-- anagrafica), quotazioni e valutazioni sono per scadenza.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- future_product - testata future (FutureProduct)
-- currency, asset_class, business_days, multiplier: valori copiati in
-- master_data quando si crea una scadenza
-- ----------------------------------------------------------------------------
CREATE TABLE future_product (
    id_future_product integer NOT NULL,
    code varchar(25) NOT NULL, -- MBTP, M6E
    description varchar(255) NOT NULL DEFAULT '',
    exchange_contract_code varchar(25) NOT NULL DEFAULT '',
    settlement_type integer NOT NULL,
    contract_value numeric(15, 5) NOT NULL,
    tick_size numeric(15, 5) NOT NULL,
    initial_margin numeric(15, 5) NOT NULL,
    maintenance_margin numeric(15, 5), -- assente per i bond future
    currency integer NOT NULL,
    asset_class integer NOT NULL,
    business_days integer NOT NULL DEFAULT 2,
    multiplier numeric(15, 5) NOT NULL DEFAULT 1,
    CONSTRAINT pk_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT uq_future_product_code UNIQUE (code),
    CONSTRAINT fk_future_product_settlement_type FOREIGN KEY (settlement_type) REFERENCES settlement_type (id_settlement_type),
    CONSTRAINT fk_future_product_currency FOREIGN KEY (currency) REFERENCES currency (id_currency),
    CONSTRAINT fk_future_product_asset_class FOREIGN KEY (asset_class) REFERENCES asset_class (id_asset_class)
);
ALTER TABLE future_product OWNER TO sofie;

CREATE SEQUENCE future_product_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE future_product_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- bond_future_product - testata bond future
-- ----------------------------------------------------------------------------
CREATE TABLE bond_future_product (
    id_future_product integer NOT NULL,
    CONSTRAINT pk_bond_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_bond_future_product_parent FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product)
);
ALTER TABLE bond_future_product OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- fx_future_product - testata forex future
-- ----------------------------------------------------------------------------
CREATE TABLE fx_future_product (
    id_future_product integer NOT NULL,
    underlying integer NOT NULL, -- coppia di cambi
    CONSTRAINT pk_fx_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_fx_future_product_parent FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_fx_future_product_underlying FOREIGN KEY (underlying) REFERENCES forex_master_data (id_master_data)
);
ALTER TABLE fx_future_product OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- mm_future_product - testata money market future
-- ----------------------------------------------------------------------------
CREATE TABLE mm_future_product (
    id_future_product integer NOT NULL,
    underlying integer NOT NULL,
    CONSTRAINT pk_mm_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_mm_future_product_parent FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_mm_future_product_underlying FOREIGN KEY (underlying) REFERENCES forex_master_data (id_master_data)
);
ALTER TABLE mm_future_product OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- future_master_data - scadenza future
-- ----------------------------------------------------------------------------
CREATE TABLE future_master_data (
    id_master_data integer NOT NULL,
    isin varchar(25), -- isin della scadenza
    contract_month date NOT NULL, -- primo giorno del mese di scadenza
    last_trading_date date NOT NULL,
    delivery_date date, -- null per i future regolati per cassa (data valuta calcolata)
    PRIMARY KEY (id_master_data)
);

CREATE UNIQUE INDEX idx_future_master_data_isin ON future_master_data (isin);

ALTER TABLE future_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- bond_future_master_data - scadenza bond future
-- ----------------------------------------------------------------------------
CREATE TABLE bond_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL, -- testata
    CONSTRAINT fk_bond_future_master_data_product FOREIGN KEY (future_product) REFERENCES bond_future_product (id_future_product),
    PRIMARY KEY (id_master_data)
);

ALTER TABLE bond_future_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- deliverable_bonds - consegnabili
-- ----------------------------------------------------------------------------
CREATE TABLE deliverable_bonds (
    id_deliverable_bonds integer NOT NULL,
    expiration_date date NOT NULL, -- scadenza future
    isin varchar(25) NOT NULL, -- isin consegnabile
    coupon_rate numeric(15, 5) NOT NULL, -- coupon consegnabile
    bond_maturity date NOT NULL, -- scadenza consegnabile
    bond_cf numeric(15, 5) NOT NULL, -- conversion factor
    master_data integer NOT NULL, -- scadenza del bond future
    CONSTRAINT fk_deliverable_bonds_master_data FOREIGN KEY (master_data) REFERENCES bond_future_master_data (id_master_data),
    PRIMARY KEY (id_deliverable_bonds)
);

CREATE UNIQUE INDEX ux_deliverable_bonds_isin ON deliverable_bonds (master_data, isin);

ALTER TABLE deliverable_bonds OWNER TO sofie;

-- Creo sequenza
CREATE SEQUENCE deliverable_bonds_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE deliverable_bonds_s
    OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- fx_future_master_data - scadenza forex future
-- ----------------------------------------------------------------------------
CREATE TABLE fx_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL, -- testata (con la coppia sottostante)
    CONSTRAINT fk_fx_future_master_data_product FOREIGN KEY (future_product) REFERENCES fx_future_product (id_future_product),
    PRIMARY KEY (id_master_data)
);

ALTER TABLE fx_future_master_data OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- mm_future_master_data - scadenza money market future
-- ----------------------------------------------------------------------------
CREATE TABLE mm_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL, -- testata (con il sottostante)
    CONSTRAINT fk_mm_future_master_data_product FOREIGN KEY (future_product) REFERENCES mm_future_product (id_future_product),
    PRIMARY KEY (id_master_data)
);

ALTER TABLE mm_future_master_data OWNER TO sofie;

-- ============================================================================
-- OPZIONI
--   option_product (testata, NON in master_data)      EUU: dati comuni alle serie
--     future_product -> sottostante derivato (le serie puntano a una sua scadenza)
--     underlying     -> sottostante secco fisso (titolo, azione, coppia di cambi)
--   master_data -> option_master_data                 EUUZ6 C1.135: tipo, strike, sottostante
--
-- Lookup exercise_style, premium_style, option_type: righe in sofie_startup.sql
-- ============================================================================
CREATE TABLE exercise_style (
    exercise_style_id INTEGER     NOT NULL,
    code              VARCHAR(25) NOT NULL,
    description       VARCHAR(64) NOT NULL,
    CONSTRAINT pk_exercise_style PRIMARY KEY (exercise_style_id)
);
CREATE UNIQUE INDEX exercise_style_code ON exercise_style (code);
ALTER TABLE exercise_style OWNER TO sofie;

CREATE TABLE premium_style (
    premium_style_id INTEGER     NOT NULL,
    code             VARCHAR(25) NOT NULL,
    description      VARCHAR(64) NOT NULL,
    CONSTRAINT pk_premium_style PRIMARY KEY (premium_style_id)
);
CREATE UNIQUE INDEX premium_style_code ON premium_style (code);
ALTER TABLE premium_style OWNER TO sofie;

CREATE TABLE option_type (
    option_type_id INTEGER     NOT NULL,
    code           VARCHAR(25) NOT NULL,
    description    VARCHAR(64) NOT NULL,
    CONSTRAINT pk_option_type PRIMARY KEY (option_type_id)
);
CREATE UNIQUE INDEX option_type_code ON option_type (code);
ALTER TABLE option_type OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- 2. Testata
-- ----------------------------------------------------------------------------
CREATE SEQUENCE option_product_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE option_product_s OWNER TO sofie;

CREATE TABLE option_product (
    id_option_product       INTEGER       NOT NULL,
    code                    VARCHAR(25)   NOT NULL,      -- EUU
    description             VARCHAR(255)  NOT NULL DEFAULT '',
    exchange_contract_code  VARCHAR(25)   NOT NULL DEFAULT '',
    exercise_style          INTEGER       NOT NULL,      -- ExerciseStyle
    premium_style           INTEGER       NOT NULL,      -- PremiumStyle
    settlement_type         INTEGER       NOT NULL,      -- SettlementType
    contract_value          NUMERIC(15,5) NOT NULL,      -- quantita' di sottostante per contratto
    tick_size               NUMERIC(15,5) NOT NULL,
    initial_margin          NUMERIC(15,5),
    maintenance_margin      NUMERIC(15,5),
    expiry_time             TIME,                        -- orario di scadenza (10:00 per EUU)
    expiry_time_zone        VARCHAR(64),                 -- fuso IANA (America/New_York)
    future_product          INTEGER,                     -- sottostante derivato
    underlying              INTEGER,                     -- sottostante secco fisso
    -- valori copiati in master_data quando si crea una serie
    currency                INTEGER       NOT NULL,
    asset_class             INTEGER       NOT NULL,
    business_days           INTEGER       NOT NULL DEFAULT 2,
    multiplier              NUMERIC(15,5) NOT NULL DEFAULT 1,
    CONSTRAINT pk_option_product PRIMARY KEY (id_option_product),
    CONSTRAINT uq_option_product_code UNIQUE (code),
    CONSTRAINT ck_option_product_underlying CHECK (future_product IS NULL OR underlying IS NULL),
    CONSTRAINT fk_option_product_exercise_style FOREIGN KEY (exercise_style) REFERENCES exercise_style (exercise_style_id),
    CONSTRAINT fk_option_product_premium_style FOREIGN KEY (premium_style) REFERENCES premium_style (premium_style_id),
    CONSTRAINT fk_option_product_settlement_type FOREIGN KEY (settlement_type) REFERENCES settlement_type (id_settlement_type),
    CONSTRAINT fk_option_product_future_product FOREIGN KEY (future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_option_product_underlying FOREIGN KEY (underlying) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_option_product_currency FOREIGN KEY (currency) REFERENCES currency (id_currency),
    CONSTRAINT fk_option_product_asset_class FOREIGN KEY (asset_class) REFERENCES asset_class (id_asset_class)
);
ALTER TABLE option_product OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- 3. Serie negoziabile
-- ----------------------------------------------------------------------------
CREATE TABLE option_master_data (
    id_master_data  INTEGER       NOT NULL,
    option_product  INTEGER       NOT NULL,
    underlying      INTEGER       NOT NULL,              -- scadenza del future o sottostante secco
    option_type     INTEGER       NOT NULL,              -- OptionType
    strike          NUMERIC(18,8) NOT NULL,
    isin            VARCHAR(25),
    CONSTRAINT pk_option_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_option_master_data_parent FOREIGN KEY (id_master_data) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_option_master_data_product FOREIGN KEY (option_product) REFERENCES option_product (id_option_product),
    CONSTRAINT fk_option_master_data_underlying FOREIGN KEY (underlying) REFERENCES master_data (id_master_data),
    CONSTRAINT fk_option_master_data_option_type FOREIGN KEY (option_type) REFERENCES option_type (option_type_id)
);
CREATE UNIQUE INDEX idx_option_master_data_isin ON option_master_data (isin);
CREATE INDEX idx_option_master_data_product ON option_master_data (option_product);
CREATE INDEX idx_option_master_data_underlying ON option_master_data (underlying);
ALTER TABLE option_master_data OWNER TO sofie;


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

-- calibration_profile_item e' creata prima di instrument_quote
ALTER TABLE calibration_profile_item ADD CONSTRAINT fk_cpi_instrument_quote
    FOREIGN KEY (instrument_quote) REFERENCES instrument_quote (id_instrument_quote);

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
    time_to_maturity numeric(15, 5) NOT NULL DEFAULT 0, -- vita residua
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

CREATE SEQUENCE txn_status_s
    START WITH 1
    INCREMENT BY 1;

ALTER SEQUENCE txn_status_s
    OWNER TO sofie;

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
    txn_status_pre_elab integer NOT NULL DEFAULT 1, -- stato transazione pre processamento (iniziale)
    txn_acct_phase integer NOT NULL DEFAULT 1, -- stato contabile transazione
    txn_side integer NOT NULL, -- (Buy/Sell)
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
    fx_rate numeric(15, 5) NOT NULL, -- cambio al momento del trade
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
    calendar integer UNIQUE,
    currency integer NOT NULL DEFAULT 1,
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
-- service_types
-- ----------------------------------------------------------------------------
CREATE TABLE service_types (
    service_type_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(255) NOT NULL,
    PRIMARY KEY (service_type_id)
);
ALTER TABLE service_types OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- descriptors
-- ----------------------------------------------------------------------------
CREATE TABLE descriptors (
    descriptor_id integer NOT NULL,
    service_type integer NOT NULL,
    jar_path varchar(255) NOT NULL,
    active_profile varchar(25) NOT NULL,
    port varchar(5) NOT NULL,
    CONSTRAINT fk_service_types FOREIGN KEY (service_type) REFERENCES service_types (service_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    PRIMARY KEY (descriptor_id)
);
ALTER TABLE descriptors OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS descriptors_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE descriptors_s OWNER TO sofie;

--
-- COMMODITY FUTURE
--
--
-- Lookup Tables (usate dalle tabelle power e dai future su commodity)
--
-- ----------------------------------------------------------------------------
-- commodity_type
-- ----------------------------------------------------------------------------
CREATE TABLE commodity_type (
    commodity_type_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(25) NOT NULL,
    PRIMARY KEY (commodity_type_id)
);
CREATE UNIQUE INDEX commodity_type_code ON commodity_type (code);
ALTER TABLE commodity_type OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- load_type
-- ----------------------------------------------------------------------------
CREATE TABLE load_type (
    load_type_id INT PRIMARY KEY,
    code VARCHAR(16) NOT NULL,
    description VARCHAR(64)
);
CREATE UNIQUE INDEX load_type_code ON load_type (code);
ALTER TABLE load_type OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- delivery_period_type
-- ----------------------------------------------------------------------------
CREATE TABLE delivery_period_type (
    delivery_period_type_id INT PRIMARY KEY,
    code VARCHAR(16) NOT NULL,
    description VARCHAR(64)
);
CREATE UNIQUE INDEX load_dpt_code ON delivery_period_type (code);
ALTER TABLE delivery_period_type OWNER TO sofie;

-- ==================================================================
-- Tabelle gestione power futures
-- ==================================================================
-- Tabelle lookup
CREATE TABLE shape_calculation_method (
    calculation_method_id INTEGER NOT NULL,
    code                  VARCHAR(32) NOT NULL,
    description           VARCHAR(255) NOT NULL,

    CONSTRAINT pk_shape_calculation_method
        PRIMARY KEY (calculation_method_id),

    CONSTRAINT uq_shape_calculation_method_code
        UNIQUE (code)
);
ALTER TABLE shape_calculation_method OWNER TO sofie;

CREATE TABLE shape_granularity (
    granularity_id INTEGER NOT NULL,
    code           VARCHAR(32) NOT NULL,
    description    VARCHAR(255) NOT NULL,

    CONSTRAINT pk_shape_granularity
        PRIMARY KEY (granularity_id),

    CONSTRAINT uq_shape_granularity_code
        UNIQUE (code)
);
ALTER TABLE shape_granularity OWNER TO sofie;

CREATE TABLE market_data_source (
    data_source_id INTEGER NOT NULL,
    code           VARCHAR(32) NOT NULL,
    description    VARCHAR(255) NOT NULL,

    CONSTRAINT pk_market_data_source
        PRIMARY KEY (data_source_id),

    CONSTRAINT uq_market_data_source_code
        UNIQUE (code)
);
ALTER TABLE market_data_source OWNER TO sofie;

-- ============================================================
-- PREZZI SPOT: usati per costruire i fattori di forma
-- ============================================================
--
-- power_spot_price_definition - La testata identifica quale serie storica stiamo osservando
--
CREATE TABLE power_spot_price_definition (
    spot_price_definition_id INTEGER NOT NULL,
    code                  VARCHAR(32) NOT NULL,
    description           VARCHAR(255) NOT NULL,
    id_country               INTEGER NOT NULL,
    load_type                INTEGER NOT NULL,
    data_source              INTEGER NOT NULL,

    CONSTRAINT pk_power_spot_price_definition
        PRIMARY KEY (spot_price_definition_id),

    CONSTRAINT fk_pspd_country
        FOREIGN KEY (id_country)
        REFERENCES country(id_country),

    CONSTRAINT fk_pspd_load_type
        FOREIGN KEY (load_type)
        REFERENCES load_type(load_type_id),

    CONSTRAINT fk_pspd_data_source
        FOREIGN KEY (data_source)
        REFERENCES market_data_source(data_source_id),

    CONSTRAINT uq_pspd_code UNIQUE (code),

    CONSTRAINT uq_power_spot_price_definition
        UNIQUE (
            id_country,
            load_type,
            data_source
        )
);
ALTER TABLE power_spot_price_definition OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS power_spot_price_definition_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE power_spot_price_definition_s OWNER TO sofie;

--
-- power_spot_price - prezzi spot effettivamente osservati.
--
CREATE TABLE power_spot_price (
    spot_price_id            INTEGER NOT NULL,
    spot_price_definition_id INTEGER NOT NULL,
    delivery_date            DATE NOT NULL,
    price                    NUMERIC(15,5) NOT NULL,

    CONSTRAINT pk_power_spot_price
        PRIMARY KEY (spot_price_id),

    CONSTRAINT fk_power_spot_definition
        FOREIGN KEY (spot_price_definition_id)
        REFERENCES power_spot_price_definition(spot_price_definition_id),

    CONSTRAINT uq_power_spot_price
        UNIQUE (
            spot_price_definition_id,
            delivery_date
        )
);
ALTER TABLE power_spot_price OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS power_spot_price_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE power_spot_price_s OWNER TO sofie;

-- ============================================================
-- SHAPE PROFILE: fattori di forma per il bootstrap
-- ============================================================--
-- shape_profile_definition - testata shape.
--
CREATE TABLE shape_profile_definition (
    shape_profile_id       INTEGER NOT NULL,
    profile_code           VARCHAR(32) NOT NULL,
    description            VARCHAR(255) NOT NULL,
    id_country             INTEGER NOT NULL,
    load_type              INTEGER NOT NULL,
    calculation_method     INTEGER NOT NULL,
    granularity            INTEGER NOT NULL,
    data_source            INTEGER NOT NULL,
    historical_years       INTEGER,
    valid_from             DATE NOT NULL,
    valid_to               DATE, -- null -> ancora valida

    CONSTRAINT pk_shape_profile_definition
        PRIMARY KEY (shape_profile_id),
    CONSTRAINT uq_shape_profile_code
        UNIQUE (profile_code),
    CONSTRAINT fk_shape_profile_country
        FOREIGN KEY (id_country)
        REFERENCES country(id_country),
    CONSTRAINT fk_shape_profile_load_type
        FOREIGN KEY (load_type)
        REFERENCES load_type(load_type_id),
    CONSTRAINT fk_shape_profile_calc_method
        FOREIGN KEY (calculation_method)
        REFERENCES shape_calculation_method(calculation_method_id),
    CONSTRAINT fk_shape_profile_granularity
        FOREIGN KEY (granularity)
        REFERENCES shape_granularity(granularity_id),
    CONSTRAINT fk_shape_profile_data_source
        FOREIGN KEY (data_source)
        REFERENCES market_data_source(data_source_id),
    CONSTRAINT chk_shape_profile_years
        CHECK (
            historical_years IS NULL
            OR historical_years > 0
        ),
    CONSTRAINT chk_shape_profile_dates
        CHECK (
            valid_to IS NULL
            OR valid_to >= valid_from
        ),
    CONSTRAINT uq_shape_profile_code UNIQUE (profile_code)
);
ALTER TABLE shape_profile_definition OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS shape_profile_definition_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE shape_profile_definition_s OWNER TO sofie;

--
-- shape_profile_spot_data - link osservazioni spot e shape.
--
CREATE TABLE shape_profile_spot_series (
    shape_profile_spot_series_id BIGINT NOT NULL,
    shape_profile_id             INTEGER NOT NULL,
    spot_price_definition_id     INTEGER NOT NULL,
    observation_from              DATE,
    observation_to                DATE,

    CONSTRAINT pk_shape_profile_spot_series
        PRIMARY KEY (shape_profile_spot_series_id),

    CONSTRAINT uq_shape_profile_spot_series
        UNIQUE (
            shape_profile_id,
            spot_price_definition_id
        ),

    CONSTRAINT fk_spss_shape_profile
        FOREIGN KEY (shape_profile_id)
        REFERENCES shape_profile_definition(shape_profile_id),

    CONSTRAINT fk_spss_spot_definition
        FOREIGN KEY (spot_price_definition_id)
        REFERENCES power_spot_price_definition(spot_price_definition_id)
);
ALTER TABLE shape_profile_spot_series OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS shape_profile_spot_series_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE shape_profile_spot_series_s OWNER TO sofie;

--
-- shape_profile_factor - coefficienti calcolati a partire dagli spot storici
--
CREATE TABLE shape_profile_factor (
    shape_profile_factor_id BIGINT NOT NULL,
    shape_profile_id        INTEGER NOT NULL,
    month_of_year           SMALLINT,
    day_of_week             SMALLINT,
    hour_of_day             SMALLINT,
    is_holiday              BOOLEAN,
    weight_factor           NUMERIC(18,10) NOT NULL,

    CONSTRAINT pk_shape_profile_factor
        PRIMARY KEY (shape_profile_factor_id),

    CONSTRAINT fk_shape_profile_factor_profile
        FOREIGN KEY (shape_profile_id)
        REFERENCES shape_profile_definition(shape_profile_id),

    CONSTRAINT chk_shape_factor_month
        CHECK (
            month_of_year IS NULL
            OR month_of_year BETWEEN 1 AND 12
        ),

    CONSTRAINT chk_shape_factor_dow
        CHECK (
            day_of_week IS NULL
            OR day_of_week BETWEEN 1 AND 7
        ),

    CONSTRAINT chk_shape_factor_hour
        CHECK (
            hour_of_day IS NULL
            OR hour_of_day BETWEEN 0 AND 23
        )
);
ALTER TABLE shape_profile_factor OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS shape_profile_factor_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE shape_profile_factor_s OWNER TO sofie;

-- ============================================================
-- MARKET DATA: quotazioni blocco (time-series, non yield curve)
-- ============================================================
-- market_quote_definition - testata della serie storica delle quotazioni forward/future
CREATE TABLE market_quote_definition (
    market_quote_definition_id INTEGER NOT NULL,
    code                       VARCHAR(32) NOT NULL,
    description                VARCHAR(255) NOT NULL,
    id_country                 INTEGER NOT NULL,
    data_source                INTEGER NOT NULL,

    CONSTRAINT pk_market_quote_definition
        PRIMARY KEY (market_quote_definition_id),

    CONSTRAINT fk_mqd_country
        FOREIGN KEY (id_country)
        REFERENCES country(id_country),

    CONSTRAINT fk_mqd_data_source
        FOREIGN KEY (data_source)
        REFERENCES market_data_source(data_source_id),

    CONSTRAINT uq_mqd_code UNIQUE (code)
);
ALTER TABLE market_quote_definition OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS market_quote_definition_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE market_quote_definition_s OWNER TO sofie;

-- market_quote - Sono le osservazioni storiche della quotazione.
CREATE TABLE market_quote (
    market_quote_id            INTEGER NOT NULL,
    market_quote_definition_id INTEGER NOT NULL,
    business_date              DATE NOT NULL,
    price                      NUMERIC(15,5) NOT NULL,

    CONSTRAINT pk_market_quote
        PRIMARY KEY (market_quote_id),

    CONSTRAINT fk_market_quote_definition
        FOREIGN KEY (market_quote_definition_id)
        REFERENCES market_quote_definition(market_quote_definition_id),

    CONSTRAINT uq_market_quote
        UNIQUE (market_quote_definition_id, business_date)
);
ALTER TABLE market_quote OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS market_quote_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE market_quote_s OWNER TO sofie;

-- ============================================================
-- GRANULAR CURVE: output del bootstrap+shaping, un prezzo per delivery_date
-- ============================================================
--
-- granular_curve_definition
--
CREATE TABLE granular_curve_definition (
    granular_curve_id       INTEGER NOT NULL,
    code                       VARCHAR(32) NOT NULL,
    description                VARCHAR(255) NOT NULL,
    market_quote_definition_id INTEGER NOT NULL,
    shape_profile_id        INTEGER NOT NULL,

    CONSTRAINT pk_granular_curve_definition
        PRIMARY KEY (granular_curve_id),

    CONSTRAINT fk_gcd_quote_definition
        FOREIGN KEY (market_quote_definition_id)
        REFERENCES market_quote_definition(market_quote_definition_id),

    CONSTRAINT fk_gcd_shape_profile
        FOREIGN KEY (shape_profile_id)
        REFERENCES shape_profile_definition(shape_profile_id),

    CONSTRAINT uq_granular_curve
        UNIQUE (
            market_quote_definition_id,
            shape_profile_id
        ),
    CONSTRAINT uq_gcd_code UNIQUE (code)
);
ALTER TABLE granular_curve_definition OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS granular_curve_definition_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE granular_curve_definition_s OWNER TO sofie;

--
-- granular_curve_point
--
CREATE TABLE granular_curve_point (
    granular_curve_point_id BIGINT NOT NULL,
    granular_curve_id       INTEGER NOT NULL,
    delivery_date           DATE NOT NULL,
    forward_price           NUMERIC(15,5) NOT NULL,

    CONSTRAINT pk_granular_curve_point
        PRIMARY KEY (granular_curve_point_id),

    CONSTRAINT uq_granular_curve_point
        UNIQUE (
            granular_curve_id,
            delivery_date
        )
);
ALTER TABLE granular_curve_point OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS granular_curve_point_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE granular_curve_point_s OWNER TO sofie;

--
-- Livello 1: comune a TUTTI i future su commodity (testata e scadenza,
-- vedi schema nella sezione FUTURE)
--
-- ----------------------------------------------------------------------------
-- cmd_future_product - testata commodity future
-- ----------------------------------------------------------------------------
CREATE TABLE cmd_future_product (
    id_future_product integer NOT NULL,
    commodity_type integer NOT NULL,
    CONSTRAINT pk_cmd_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_cmd_future_product_parent FOREIGN KEY (id_future_product) REFERENCES future_product (id_future_product),
    CONSTRAINT fk_cmd_future_product_commodity_type FOREIGN KEY (commodity_type) REFERENCES commodity_type (commodity_type_id)
);
ALTER TABLE cmd_future_product OWNER TO sofie;

-- ----------------------------------------------------------------------------
-- cmd_future_master_data - scadenza commodity future
-- ----------------------------------------------------------------------------
CREATE TABLE cmd_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL, -- testata (con il tipo di commodity)
    CONSTRAINT fk_cmd_future_master_data_product FOREIGN KEY (future_product) REFERENCES cmd_future_product (id_future_product),
    PRIMARY KEY (id_master_data)
);
ALTER TABLE cmd_future_master_data OWNER TO sofie;

-- ============================================================
-- ANAGRAFICA FUTURE ET
-- ============================================================
-- power_future_product: testata, profilo di carico (base, peak)
-- power_future_master_data: scadenza; il periodo di consegna e' in commodity_delivery_profile

CREATE TABLE power_future_product (
    id_future_product INTEGER NOT NULL,
    load_type         INTEGER NOT NULL,
    CONSTRAINT pk_power_future_product PRIMARY KEY (id_future_product),
    CONSTRAINT fk_power_future_product_parent
        FOREIGN KEY (id_future_product) REFERENCES cmd_future_product (id_future_product),
    CONSTRAINT fk_power_future_product_load_type
        FOREIGN KEY (load_type) REFERENCES load_type (load_type_id)
);
ALTER TABLE power_future_product OWNER TO sofie;

CREATE TABLE power_future_master_data (
    id_master_data    INTEGER NOT NULL,
    CONSTRAINT pk_power_future_master_data PRIMARY KEY (id_master_data),
    CONSTRAINT fk_power_future_master_data_parent_table
        FOREIGN KEY (id_master_data) REFERENCES cmd_future_master_data (id_master_data)
);
ALTER TABLE power_future_master_data OWNER TO sofie;

-- ============================================================
-- ANAGRAFICA FORWARD OTC (stesso pattern minimale)
-- ============================================================
-- cmd_forward_master_data: analogo a cmd_future_master_data ma per il ramo OTC
-- (id_master_data, commodity_type — niente contract_value/tick_size/margini, non applicabili a OTC)
-- NON ANCORA IMPLEMENTATO: cmd_forward_master_data non e' definita e le due
-- tabelle non esistono sul database. Da completare prima di attivarle.
-- CREATE TABLE power_forward_master_data (
--     id_master_data    INTEGER NOT NULL,
--     load_type         INTEGER NOT NULL,
--     CONSTRAINT pk_power_forward_master_data PRIMARY KEY (id_master_data),
--     CONSTRAINT fk_power_forward_master_data_parent_table
--         FOREIGN KEY (id_master_data) REFERENCES cmd_forward_master_data (id_master_data),
--     CONSTRAINT fk_power_forward_load_type
--         FOREIGN KEY (load_type) REFERENCES load_type (load_type_id)
-- );
-- ALTER TABLE power_forward_master_data OWNER TO sofie;

-- ============================================================
-- DELIVERY PROFILE: entita' satellite condivisa (composizione)
-- ============================================================
CREATE TABLE commodity_delivery_profile (
    id_master_data          INTEGER NOT NULL,   -- FK 1-1 verso power_future_master_data o power_forward_master_data
    delivery_period_type    INTEGER NOT NULL,
    delivery_start          DATE NOT NULL,
    delivery_end            DATE NOT NULL,
    total_delivery_hours    INTEGER NOT NULL,
    market                  VARCHAR(16) NOT NULL,
    notional_mw             NUMERIC NOT NULL,
    notional_mwh            NUMERIC NOT NULL,
    parent_profile          INTEGER,             -- self-ref cascading; NULL per Year e per i Forward OTC
    market_quote_definition_id INTEGER,

    CONSTRAINT pk_commodity_delivery_profile PRIMARY KEY (id_master_data),
    CONSTRAINT fk_delivery_profile_mdp
        FOREIGN KEY (market_quote_definition_id) REFERENCES market_quote_definition (market_quote_definition_id),
    CONSTRAINT fk_delivery_profile_period_type
        FOREIGN KEY (delivery_period_type) REFERENCES delivery_period_type (delivery_period_type_id),
    CONSTRAINT fk_delivery_profile_parent
        FOREIGN KEY (parent_profile) REFERENCES commodity_delivery_profile (id_master_data),
    CONSTRAINT uq_delivery_profile_natural_key
        UNIQUE (market, delivery_period_type, delivery_start, delivery_end),
    CONSTRAINT chk_delivery_profile_dates CHECK (delivery_end >= delivery_start),
    CONSTRAINT chk_delivery_profile_notional_positive
        CHECK (notional_mw > 0 AND notional_mwh > 0)
);

CREATE INDEX idx_delivery_profile_parent ON commodity_delivery_profile (parent_profile);
CREATE INDEX idx_delivery_profile_range ON commodity_delivery_profile (delivery_start, delivery_end);

ALTER TABLE commodity_delivery_profile OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS commodity_delivery_profile_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE commodity_delivery_profile_s OWNER TO sofie;

-- ============================================================
-- ac_dc_bcy_link
-- ============================================================
CREATE TABLE curve_role (
    curve_role_id integer NOT NULL,
    code varchar(25) NOT NULL,
    description varchar(50) NOT NULL,
    PRIMARY KEY (curve_role_id)
);
CREATE UNIQUE INDEX idx_curve_role_code ON curve_role (code);
ALTER TABLE curve_role OWNER TO sofie;

-- ============================================================
-- ac_dc_bcy_link
-- ============================================================
CREATE TABLE ac_dc_bcy_link (
    ac_dc_bcy_link_id INTEGER NOT NULL,   
    asset_class INTEGER NOT NULL,   
    currency INTEGER NOT NULL,   
    curve_role INTEGER NOT NULL,   
    yield_curve INTEGER,   
    spread_curve INTEGER,   
    CONSTRAINT pk_ac_dc_bcy_link PRIMARY KEY (ac_dc_bcy_link_id),
    CONSTRAINT fk_ac_dc_bcy_asset_class
        FOREIGN KEY (asset_class) REFERENCES asset_class (id_asset_class),
    CONSTRAINT fk_ac_dc_bcy_curve_role
        FOREIGN KEY (curve_role) REFERENCES curve_role (curve_role_id),
    CONSTRAINT fk_ac_dc_bcy_currency
        FOREIGN KEY (currency) REFERENCES currency (id_currency),
    CONSTRAINT fk_ac_dc_bcy_yield_curve  FOREIGN KEY (yield_curve)  REFERENCES yield_curve (id_yield_curve),
    CONSTRAINT fk_ac_dc_bcy_spread_curve FOREIGN KEY (spread_curve) REFERENCES spread_curve (id_spread_curve),
    CONSTRAINT ck_ac_dc_bcy_one_curve    CHECK (num_nonnulls(yield_curve, spread_curve) = 1),    CONSTRAINT uq_ac_dc_bcy_natural_key
        UNIQUE (asset_class, curve_role, currency)
);
ALTER TABLE ac_dc_bcy_link OWNER TO sofie;
CREATE SEQUENCE IF NOT EXISTS ac_dc_bcy_link_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE ac_dc_bcy_link_s OWNER TO sofie;
