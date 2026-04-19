--
-- PostgreSQL database dump
--

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: aggiorna_id_instrument_quote_hist(); Type: FUNCTION; Schema: public; Owner: easypricer
--

CREATE FUNCTION public.aggiorna_id_instrument_quote_hist() RETURNS trigger
    LANGUAGE plpgsql STABLE
    AS $$
    BEGIN
        IF (TG_OP = 'INSERT') THEN
           IF(NEW.id_instrument_quote_hist IS NULL) THEN
              NEW.id_instrument_quote_hist = nextval('instrument_quote_hist_s');
            END IF;
        END IF;

    RETURN NEW; -- this is important for a trigger
    END;
    $$;


ALTER FUNCTION public.aggiorna_id_instrument_quote_hist() OWNER TO easypricer;

--
-- Name: upsert_instrument_quote(); Type: FUNCTION; Schema: public; Owner: easypricer
--

CREATE FUNCTION public.upsert_instrument_quote() RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO instrument_quote_hist (instrument_quote, master_data, code, bid, ask, update_date)
    SELECT id_instrument_quote, master_data, code, bid, ask, CURRENT_DATE
    FROM instrument_quote
    ON CONFLICT (instrument_quote,update_date) 
    DO UPDATE SET 
        instrument_quote = EXCLUDED.instrument_quote,
        master_data = EXCLUDED.master_data,
        code = EXCLUDED.code,
        bid = EXCLUDED.bid,
        ask = EXCLUDED.ask;
END;
$$;


ALTER FUNCTION public.upsert_instrument_quote() OWNER TO easypricer;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: accrual_schedule_type; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.accrual_schedule_type (
    id_accrual_schedule_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.accrual_schedule_type OWNER TO easypricer;

--
-- Name: accrual_schedule_type_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.accrual_schedule_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.accrual_schedule_type_s OWNER TO easypricer;

--
-- Name: amortization_schedule; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.amortization_schedule (
    id_amortization_schedule integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.amortization_schedule OWNER TO easypricer;

--
-- Name: amortization_schedule_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.amortization_schedule_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.amortization_schedule_s OWNER TO easypricer;

--
-- Name: asset_class; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.asset_class (
    id_asset_class integer NOT NULL,
    super_class integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.asset_class OWNER TO easypricer;

--
-- Name: asset_class_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.asset_class_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.asset_class_s OWNER TO easypricer;

--
-- Name: bond_future_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.bond_future_master_data (
    id_master_data integer NOT NULL,
    contract_value numeric(15,5) NOT NULL,
    tick_size numeric(15,5) NOT NULL,
    initial_margin numeric(15,5) NOT NULL
);


ALTER TABLE public.bond_future_master_data OWNER TO easypricer;

--
-- Name: calendar; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.calendar (
    id_calendar integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.calendar OWNER TO easypricer;

--
-- Name: calendar_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.calendar_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.calendar_s OWNER TO easypricer;

--
-- Name: cash_flow_item; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.cash_flow_item (
    id_cash_flow_item integer NOT NULL,
    master_data integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    interest numeric(15,5) NOT NULL,
    amount numeric(15,5) NOT NULL
);


ALTER TABLE public.cash_flow_item OWNER TO easypricer;

--
-- Name: cash_flow_item_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.cash_flow_item_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cash_flow_item_s OWNER TO easypricer;

--
-- Name: counterparty; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.counterparty (
    id_counterparty integer NOT NULL,
    ctp_type integer NOT NULL,
    country integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL,
    lei_code character varying(50) DEFAULT ''::character varying
);


ALTER TABLE public.counterparty OWNER TO easypricer;

--
-- Name: counterparty_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.counterparty_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.counterparty_s OWNER TO easypricer;

--
-- Name: counterparty_type; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.counterparty_type (
    id_counterparty_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.counterparty_type OWNER TO easypricer;

--
-- Name: counterparty_type_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.counterparty_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.counterparty_type_s OWNER TO easypricer;

--
-- Name: country; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.country (
    id_country integer NOT NULL,
    country_name character varying(100) NOT NULL,
    official_state_name character varying(255) NOT NULL,
    alfa_2_code character varying(2) NOT NULL,
    alfa_3_code character varying(3) NOT NULL,
    country_numeric_code smallint NOT NULL,
    sovereign character varying(25) DEFAULT 'UN Member State'::character varying,
    subdivision_code_links character varying(25),
    internet_cc_tld character varying(10),
    currency integer NOT NULL,
    calendar integer NOT NULL
);


ALTER TABLE public.country OWNER TO easypricer;

--
-- Name: country_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.country_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.country_s OWNER TO easypricer;

--
-- Name: currency; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.currency (
    id_currency integer NOT NULL,
    iso_code character varying(3) NOT NULL,
    currency_numeric_code smallint NOT NULL,
    description character varying(50) NOT NULL,
    minor_unit smallint DEFAULT 2 NOT NULL,
    system_curr smallint DEFAULT 0 NOT NULL,
    physical_curr smallint DEFAULT 1 NOT NULL,
    calendar integer NOT NULL,
    business_days integer DEFAULT 2 NOT NULL,
    daycount integer NOT NULL
);


ALTER TABLE public.currency OWNER TO easypricer;

--
-- Name: currency_pair; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.currency_pair (
    id_currency_pair integer NOT NULL,
    code character varying(25) NOT NULL,
    bcy integer NOT NULL,
    ccy integer NOT NULL,
    bid numeric(15,5) NOT NULL,
    ask numeric(15,5) NOT NULL
);


ALTER TABLE public.currency_pair OWNER TO easypricer;

--
-- Name: currency_pair_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.currency_pair_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.currency_pair_s OWNER TO easypricer;

--
-- Name: currency_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.currency_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.currency_s OWNER TO easypricer;

--
-- Name: currpair_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.currpair_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL,
    ccy integer NOT NULL,
    bcy_irc character varying(25) DEFAULT ''::character varying NOT NULL,
    ccy_irc character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.currpair_master_data OWNER TO easypricer;

--
-- Name: daycount; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.daycount (
    id_daycount integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.daycount OWNER TO easypricer;

--
-- Name: daycount_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.daycount_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.daycount_s OWNER TO easypricer;

--
-- Name: deliverable_bonds; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.deliverable_bonds (
    id_deliverable_bonds integer NOT NULL,
    master_data integer NOT NULL,
    expiration_date date NOT NULL,
    isin character varying(25) NOT NULL,
    coupon_rate numeric(15,5) NOT NULL,
    bond_maturity date NOT NULL,
    bond_cf numeric(15,5) NOT NULL
);


ALTER TABLE public.deliverable_bonds OWNER TO postgres;

--
-- Name: deliverable_bonds_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.deliverable_bonds_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.deliverable_bonds_s OWNER TO easypricer;

--
-- Name: ec_exchange_rate; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.ec_exchange_rate (
    id_ec_exchange_rate integer NOT NULL,
    country character varying(25),
    currency character varying(25) DEFAULT ''::character varying NOT NULL,
    isoa3code character varying(5) DEFAULT ''::character varying NOT NULL,
    isoa2code character varying(5) DEFAULT ''::character varying NOT NULL,
    rate numeric(15,5) NOT NULL
);


ALTER TABLE public.ec_exchange_rate OWNER TO easypricer;

--
-- Name: ec_exchange_rate_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.ec_exchange_rate_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ec_exchange_rate_s OWNER TO easypricer;

--
-- Name: finacial_txn; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.finacial_txn (
    id_finacial_txn integer NOT NULL,
    counterparty integer NOT NULL,
    position_md integer NOT NULL,
    master_data integer NOT NULL,
    txn_status integer NOT NULL,
    txn_size smallint NOT NULL,
    description character varying(255) NOT NULL,
    trade_date date DEFAULT now() NOT NULL,
    settlement date NOT NULL,
    quantity numeric(15,5) NOT NULL,
    price numeric(15,5) NOT NULL
);


ALTER TABLE public.finacial_txn OWNER TO easypricer;

--
-- Name: finacial_txn_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.finacial_txn_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.finacial_txn_s OWNER TO easypricer;

--
-- Name: forex_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.forex_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL,
    ccy integer NOT NULL,
    bcy_irc character varying(25) DEFAULT ''::character varying NOT NULL,
    ccy_irc character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.forex_master_data OWNER TO easypricer;

--
-- Name: form; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.form (
    id_form integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.form OWNER TO easypricer;

--
-- Name: form_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.form_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.form_s OWNER TO easypricer;

--
-- Name: frequency; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.frequency (
    id_frequency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL,
    year_fraction smallint DEFAULT 2 NOT NULL
);


ALTER TABLE public.frequency OWNER TO easypricer;

--
-- Name: frequency_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.frequency_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.frequency_s OWNER TO easypricer;

--
-- Name: future_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.future_master_data (
    id_master_data integer NOT NULL,
    isin character varying(25) NOT NULL,
    settlement_type integer NOT NULL,
    description character varying(255) DEFAULT ''::character varying NOT NULL,
    exchange_contract_code character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.future_master_data OWNER TO easypricer;

--
-- Name: fx_future_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.fx_future_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL,
    contract_value numeric(15,5) NOT NULL,
    tick_size numeric(15,5) NOT NULL,
    initial_margin numeric(15,5) NOT NULL,
    maintenance_margin numeric(15,5) NOT NULL
);


ALTER TABLE public.fx_future_master_data OWNER TO easypricer;

--
-- Name: holiday; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.holiday (
    id_holiday integer NOT NULL,
    calendar integer NOT NULL,
    holiday_day smallint NOT NULL,
    holiday_month smallint NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.holiday OWNER TO easypricer;

--
-- Name: holiday_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.holiday_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.holiday_s OWNER TO easypricer;

--
-- Name: instrument_quote; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.instrument_quote (
    id_instrument_quote integer NOT NULL,
    master_data integer NOT NULL,
    code character varying(255) NOT NULL,
    bid numeric(15,5) NOT NULL,
    ask numeric(15,5) NOT NULL,
    provider character varying(50) DEFAULT 'EuroNextProvider'::character varying NOT NULL
);


ALTER TABLE public.instrument_quote OWNER TO easypricer;

--
-- Name: instrument_quote_hist; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.instrument_quote_hist (
    id_instrument_quote_hist integer NOT NULL,
    instrument_quote integer NOT NULL,
    master_data integer NOT NULL,
    code character varying(25) NOT NULL,
    bid numeric(15,5) NOT NULL,
    ask numeric(15,5) NOT NULL,
    update_date date NOT NULL
);


ALTER TABLE public.instrument_quote_hist OWNER TO easypricer;

--
-- Name: instrument_quote_hist_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.instrument_quote_hist_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.instrument_quote_hist_s OWNER TO easypricer;

--
-- Name: instrument_quote_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.instrument_quote_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.instrument_quote_s OWNER TO easypricer;

--
-- Name: issuer; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.issuer (
    id_issuer integer NOT NULL,
    short_issuer_name character varying(25) DEFAULT ''::character varying NOT NULL,
    long_issuer_name character varying(255) NOT NULL,
    country integer NOT NULL
);


ALTER TABLE public.issuer OWNER TO easypricer;

--
-- Name: issuer_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.issuer_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.issuer_s OWNER TO easypricer;

--
-- Name: loan_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.loan_master_data (
    id_master_data integer NOT NULL,
    description character varying(255) DEFAULT ''::character varying NOT NULL,
    processing_fees numeric(23,10) DEFAULT 0 NOT NULL,
    management_fees numeric(23,10) DEFAULT 0 NOT NULL,
    incidental_expenses numeric(23,10) DEFAULT 0 NOT NULL,
    default_interest numeric(23,10) DEFAULT 0 NOT NULL,
    late_payment_fee numeric(23,10) DEFAULT 0 NOT NULL,
    underwriting_fee numeric(23,10) DEFAULT 0 NOT NULL,
    insurance_premium numeric(23,10) DEFAULT 0 NOT NULL,
    tax_charges numeric(23,10) DEFAULT 0 NOT NULL
);


ALTER TABLE public.loan_master_data OWNER TO easypricer;

--
-- Name: market_segment; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.market_segment (
    id_market_segment integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.market_segment OWNER TO easypricer;

--
-- Name: market_segment_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.market_segment_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.market_segment_s OWNER TO easypricer;

--
-- Name: master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.master_data (
    id_master_data integer NOT NULL,
    code character varying(25) NOT NULL,
    currency integer NOT NULL,
    calendar integer NOT NULL,
    issue_date date NOT NULL,
    maturity_date date NOT NULL,
    type_of_interest integer NOT NULL,
    form integer NOT NULL,
    daycount integer NOT NULL,
    frequency integer NOT NULL,
    roll_convention integer DEFAULT 0 NOT NULL,
    accrual_schedule_type integer DEFAULT 0 NOT NULL,
    interest_rate numeric(23,10) NOT NULL,
    issue_price numeric(15,5) NOT NULL,
    redempion_price numeric(15,5) NOT NULL,
    business_days integer DEFAULT 2 NOT NULL,
    asset_class integer NOT NULL,
    amortization_schedule integer NOT NULL
);


ALTER TABLE public.master_data OWNER TO easypricer;

--
-- Name: master_data_code_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.master_data_code_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.master_data_code_s OWNER TO easypricer;

--
-- Name: master_data_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.master_data_s OWNER TO easypricer;

--
-- Name: mm_future_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.mm_future_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL,
    contract_value numeric(15,5) NOT NULL,
    tick_size numeric(15,5) NOT NULL,
    initial_margin numeric(15,5) NOT NULL,
    maintenance_margin numeric(15,5) NOT NULL
);


ALTER TABLE public.mm_future_master_data OWNER TO easypricer;

--
-- Name: portfolio_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.portfolio_master_data (
    id_portfolio integer NOT NULL,
    currency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.portfolio_master_data OWNER TO easypricer;

--
-- Name: portfolio_master_data_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.portfolio_master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.portfolio_master_data_s OWNER TO easypricer;

--
-- Name: position_detail; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.position_detail (
    id_position_detail integer NOT NULL,
    position_md integer NOT NULL,
    master_data integer NOT NULL,
    realized_pnl numeric(15,5) NOT NULL,
    unrealized_pnl numeric(15,5) NOT NULL,
    avg_price numeric(15,5) NOT NULL,
    market_value numeric(15,5) NOT NULL,
    net_quantity numeric(15,5) NOT NULL
);


ALTER TABLE public.position_detail OWNER TO easypricer;

--
-- Name: position_detail_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.position_detail_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.position_detail_s OWNER TO easypricer;

--
-- Name: position_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.position_master_data (
    id_position integer NOT NULL,
    currency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL,
    portfolio integer NOT NULL
);


ALTER TABLE public.position_master_data OWNER TO easypricer;

--
-- Name: position_master_data_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.position_master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.position_master_data_s OWNER TO easypricer;

--
-- Name: roll_convention; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.roll_convention (
    id_roll_convention integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.roll_convention OWNER TO easypricer;

--
-- Name: roll_convention_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.roll_convention_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roll_convention_s OWNER TO easypricer;

--
-- Name: security_master_data; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.security_master_data (
    id_master_data integer NOT NULL,
    isin character varying(25) NOT NULL,
    cfi_code character varying(25) NOT NULL,
    fisn character varying(255) NOT NULL,
    lei character varying(255) NOT NULL,
    issuer integer NOT NULL,
    issue_description character varying(255) NOT NULL,
    nominal_value numeric(23,10) NOT NULL,
    first_coupon_rate numeric(23,10) NOT NULL,
    first_coupon_payment_date date NOT NULL
);


ALTER TABLE public.security_master_data OWNER TO easypricer;

--
-- Name: settlement_type; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.settlement_type (
    id_settlement_type integer NOT NULL,
    code character varying(25),
    description character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.settlement_type OWNER TO easypricer;

--
-- Name: settlement_type_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.settlement_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.settlement_type_s OWNER TO easypricer;

--
-- Name: super_class; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.super_class (
    id_super_class integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.super_class OWNER TO easypricer;

--
-- Name: super_class_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.super_class_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.super_class_s OWNER TO easypricer;

--
-- Name: txn_status; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.txn_status (
    id_txn_status integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.txn_status OWNER TO easypricer;

--
-- Name: txn_status_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.txn_status_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.txn_status_s OWNER TO easypricer;

--
-- Name: type_of_interest; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.type_of_interest (
    id_type_of_interest integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.type_of_interest OWNER TO easypricer;

--
-- Name: type_of_interest_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.type_of_interest_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.type_of_interest_s OWNER TO easypricer;

--
-- Name: yield_curve; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.yield_curve (
    id_yield_curve integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL,
    currency integer NOT NULL,
    calendar integer NOT NULL,
    compounding smallint DEFAULT 1 NOT NULL
);


ALTER TABLE public.yield_curve OWNER TO easypricer;

--
-- Name: yield_curve_item; Type: TABLE; Schema: public; Owner: easypricer
--

CREATE TABLE public.yield_curve_item (
    id_yield_curve_item integer NOT NULL,
    yield_curve integer NOT NULL,
    ric character varying(25) NOT NULL,
    offset_type smallint NOT NULL,
    offset_value smallint NOT NULL,
    bid numeric(15,5) NOT NULL,
    ask numeric(15,5) NOT NULL
);


ALTER TABLE public.yield_curve_item OWNER TO easypricer;

--
-- Name: yield_curve_item_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.yield_curve_item_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.yield_curve_item_s OWNER TO easypricer;

--
-- Name: yield_curve_s; Type: SEQUENCE; Schema: public; Owner: easypricer
--

CREATE SEQUENCE public.yield_curve_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.yield_curve_s OWNER TO easypricer;

--
-- Data for Name: accrual_schedule_type; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.accrual_schedule_type (id_accrual_schedule_type, code, description) FROM stdin;
100	AST_NONE	None
\.


--
-- Data for Name: amortization_schedule; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.amortization_schedule (id_amortization_schedule, code, description) FROM stdin;
4	SAS	Standard Amortization Schedule
5	SLP	Straight-line Principal
6	IOL	Interest Only Loan
\.


--
-- Data for Name: asset_class; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.asset_class (id_asset_class, super_class, code, description) FROM stdin;
8	5	FRN	Floating Rate Notes
9	5	XRN	Fixed Rate Notes
10	5	FRB	Floating Rate Bonds
11	5	XRB	Fixed Rate Bonds
12	5	BLL	Bills
13	6	FRM	Floating Rate Mortages
14	6	XRM	Fixed Rate Mortages
15	7	BFU	Bond Future
16	8	FSP	Spot Forex
17	8	FFW	Forex Forward
18	8	FFU	Forex Future
19	6	MFU	MM Future
\.


--
-- Data for Name: bond_future_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.bond_future_master_data (id_master_data, contract_value, tick_size, initial_margin) FROM stdin;
67	25000.00000	0.01000	0.05000
70	25000.00000	0.01000	0.05000
81	100000.00000	0.03125	3000.00000
\.


--
-- Data for Name: calendar; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.calendar (id_calendar, code, description) FROM stdin;
3	EUR	Euro Area Calendar
4	USD	Usd Area Calendar
\.


--
-- Data for Name: cash_flow_item; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.cash_flow_item (id_cash_flow_item, master_data, start_date, end_date, interest, amount) FROM stdin;
2	1	2025-10-01	2026-04-01	2.32500	0.00000
3	1	2026-04-01	2026-10-01	2.32500	0.00000
5	1	2027-04-01	2027-10-01	2.32500	0.00000
6	1	2027-10-01	2028-04-01	2.32500	0.00000
7	1	2028-04-01	2028-10-01	2.32500	0.00000
8	1	2028-10-01	2029-04-01	2.32500	0.00000
9	1	2029-04-01	2029-10-01	2.32500	0.00000
10	1	2029-10-01	2030-04-01	2.32500	0.00000
11	1	2030-04-01	2030-10-01	2.32500	0.00000
12	1	2030-10-01	2031-04-01	2.32500	0.00000
13	1	2031-04-01	2031-10-01	2.32500	0.00000
14	1	2031-10-01	2032-04-01	2.32500	0.00000
15	1	2032-04-01	2032-10-01	2.32500	0.00000
16	1	2032-10-01	2033-04-01	2.32500	0.00000
17	1	2033-04-01	2033-10-01	2.32500	0.00000
18	1	2033-10-01	2034-04-01	2.32500	0.00000
20	1	2034-10-01	2035-04-01	2.32500	0.00000
21	1	2035-04-01	2035-10-01	2.32500	0.00000
22	1	2035-10-01	2036-04-01	2.32500	0.00000
23	1	2036-04-01	2036-10-01	2.32500	0.00000
24	1	2036-10-01	2037-04-01	2.32500	0.00000
25	1	2037-04-01	2037-10-01	2.32500	0.00000
26	1	2037-10-01	2038-04-01	2.32500	0.00000
27	1	2038-04-01	2038-10-01	2.32500	0.00000
28	1	2038-10-01	2039-04-01	2.32500	0.00000
29	1	2039-04-01	2039-10-01	2.32500	0.00000
30	1	2039-10-01	2040-04-01	2.32500	0.00000
31	1	2040-04-01	2040-10-01	2.32500	0.00000
32	1	2040-10-01	2041-04-01	2.32500	0.00000
33	1	2041-04-01	2041-10-01	2.32500	0.00000
35	1	2042-04-01	2042-10-01	2.32500	0.00000
36	1	2042-10-01	2043-04-01	2.32500	0.00000
37	1	2043-04-01	2043-10-01	2.32500	0.00000
38	1	2043-10-01	2044-04-01	2.32500	0.00000
39	1	2044-04-01	2044-10-01	2.32500	0.00000
40	1	2044-10-01	2045-04-01	2.32500	0.00000
41	1	2045-04-01	2045-10-01	2.32500	0.00000
42	1	2045-10-01	2046-04-01	2.32500	0.00000
43	1	2046-04-01	2046-10-01	2.32500	0.00000
44	1	2046-10-01	2047-04-01	2.32500	0.00000
45	1	2047-04-01	2047-10-01	2.32500	0.00000
46	1	2047-10-01	2048-04-01	2.32500	0.00000
47	1	2048-04-01	2048-10-01	2.32500	0.00000
48	1	2048-10-01	2049-04-01	2.32500	0.00000
50	1	2049-10-01	2050-04-01	2.32500	0.00000
51	1	2050-04-01	2050-10-01	2.32500	0.00000
52	1	2050-10-01	2051-04-01	2.32500	0.00000
53	1	2051-04-01	2051-10-01	2.32500	0.00000
54	1	2051-10-01	2052-04-01	2.32500	0.00000
55	1	2052-04-01	2052-10-01	2.32500	0.00000
56	1	2052-10-01	2053-04-01	2.32500	0.00000
57	1	2053-04-01	2053-10-01	2.32500	0.00000
58	1	2053-10-01	2054-04-01	2.32500	0.00000
59	1	2054-04-01	2054-10-01	2.32500	0.00000
60	1	2054-10-01	2055-04-01	2.32500	0.00000
61	1	2055-04-01	2055-10-01	2.32500	100.00000
62	2	2024-09-17	2024-10-01	0.16448	0.00000
63	2	2024-10-01	2025-04-01	2.15000	0.00000
65	2	2025-10-01	2026-04-01	2.15000	0.00000
66	2	2026-04-01	2026-10-01	2.15000	0.00000
67	2	2026-10-01	2027-04-01	2.15000	0.00000
68	2	2027-04-01	2027-10-01	2.15000	0.00000
69	2	2027-10-01	2028-04-01	2.15000	0.00000
70	2	2028-04-01	2028-10-01	2.15000	0.00000
71	2	2028-10-01	2029-04-01	2.15000	0.00000
72	2	2029-04-01	2029-10-01	2.15000	0.00000
73	2	2029-10-01	2030-04-01	2.15000	0.00000
74	2	2030-04-01	2030-10-01	2.15000	0.00000
75	2	2030-10-01	2031-04-01	2.15000	0.00000
76	2	2031-04-01	2031-10-01	2.15000	0.00000
77	2	2031-10-01	2032-04-01	2.15000	0.00000
78	2	2032-04-01	2032-10-01	2.15000	0.00000
80	2	2033-04-01	2033-10-01	2.15000	0.00000
81	2	2033-10-01	2034-04-01	2.15000	0.00000
82	2	2034-04-01	2034-10-01	2.15000	0.00000
83	2	2034-10-01	2035-04-01	2.15000	0.00000
84	2	2035-04-01	2035-10-01	2.15000	0.00000
85	2	2035-10-01	2036-04-01	2.15000	0.00000
86	2	2036-04-01	2036-10-01	2.15000	0.00000
87	2	2036-10-01	2037-04-01	2.15000	0.00000
88	2	2037-04-01	2037-10-01	2.15000	0.00000
89	2	2037-10-01	2038-04-01	2.15000	0.00000
90	2	2038-04-01	2038-10-01	2.15000	0.00000
91	2	2038-10-01	2039-04-01	2.15000	0.00000
92	2	2039-04-01	2039-10-01	2.15000	0.00000
93	2	2039-10-01	2040-04-01	2.15000	0.00000
95	2	2040-10-01	2041-04-01	2.15000	0.00000
96	2	2041-04-01	2041-10-01	2.15000	0.00000
97	2	2041-10-01	2042-04-01	2.15000	0.00000
98	2	2042-04-01	2042-10-01	2.15000	0.00000
99	2	2042-10-01	2043-04-01	2.15000	0.00000
100	2	2043-04-01	2043-10-01	2.15000	0.00000
101	2	2043-10-01	2044-04-01	2.15000	0.00000
102	2	2044-04-01	2044-10-01	2.15000	0.00000
103	2	2044-10-01	2045-04-01	2.15000	0.00000
104	2	2045-04-01	2045-10-01	2.15000	0.00000
105	2	2045-10-01	2046-04-01	2.15000	0.00000
106	2	2046-04-01	2046-10-01	2.15000	0.00000
107	2	2046-10-01	2047-04-01	2.15000	0.00000
108	2	2047-04-01	2047-10-01	2.15000	0.00000
110	2	2048-04-01	2048-10-01	2.15000	0.00000
111	2	2048-10-01	2049-04-01	2.15000	0.00000
112	2	2049-04-01	2049-10-01	2.15000	0.00000
113	2	2049-10-01	2050-04-01	2.15000	0.00000
114	2	2050-04-01	2050-10-01	2.15000	0.00000
115	2	2050-10-01	2051-04-01	2.15000	0.00000
116	2	2051-04-01	2051-10-01	2.15000	0.00000
117	2	2051-10-01	2052-04-01	2.15000	0.00000
118	2	2052-04-01	2052-10-01	2.15000	0.00000
119	2	2052-10-01	2053-04-01	2.15000	0.00000
120	2	2053-04-01	2053-10-01	2.15000	0.00000
121	2	2053-10-01	2054-04-01	2.15000	0.00000
122	2	2054-04-01	2054-10-01	2.15000	100.00000
123	3	2023-02-23	2023-04-01	0.45742	0.00000
125	3	2023-10-01	2024-04-01	2.25000	0.00000
126	3	2024-04-01	2024-10-01	2.25000	0.00000
127	3	2024-10-01	2025-04-01	2.25000	0.00000
128	3	2025-04-01	2025-10-01	2.25000	0.00000
129	3	2025-10-01	2026-04-01	2.25000	0.00000
130	3	2026-04-01	2026-10-01	2.25000	0.00000
131	3	2026-10-01	2027-04-01	2.25000	0.00000
132	3	2027-04-01	2027-10-01	2.25000	0.00000
133	3	2027-10-01	2028-04-01	2.25000	0.00000
134	3	2028-04-01	2028-10-01	2.25000	0.00000
135	3	2028-10-01	2029-04-01	2.25000	0.00000
136	3	2029-04-01	2029-10-01	2.25000	0.00000
138	3	2030-04-01	2030-10-01	2.25000	0.00000
139	3	2030-10-01	2031-04-01	2.25000	0.00000
141	3	2031-10-01	2032-04-01	2.25000	0.00000
142	3	2032-04-01	2032-10-01	2.25000	0.00000
143	3	2032-10-01	2033-04-01	2.25000	0.00000
144	3	2033-04-01	2033-10-01	2.25000	0.00000
145	3	2033-10-01	2034-04-01	2.25000	0.00000
146	3	2034-04-01	2034-10-01	2.25000	0.00000
147	3	2034-10-01	2035-04-01	2.25000	0.00000
148	3	2035-04-01	2035-10-01	2.25000	0.00000
149	3	2035-10-01	2036-04-01	2.25000	0.00000
150	3	2036-04-01	2036-10-01	2.25000	0.00000
151	3	2036-10-01	2037-04-01	2.25000	0.00000
152	3	2037-04-01	2037-10-01	2.25000	0.00000
153	3	2037-10-01	2038-04-01	2.25000	0.00000
154	3	2038-04-01	2038-10-01	2.25000	0.00000
156	3	2039-04-01	2039-10-01	2.25000	0.00000
157	3	2039-10-01	2040-04-01	2.25000	0.00000
158	3	2040-04-01	2040-10-01	2.25000	0.00000
159	3	2040-10-01	2041-04-01	2.25000	0.00000
160	3	2041-04-01	2041-10-01	2.25000	0.00000
161	3	2041-10-01	2042-04-01	2.25000	0.00000
162	3	2042-04-01	2042-10-01	2.25000	0.00000
163	3	2042-10-01	2043-04-01	2.25000	0.00000
164	3	2043-04-01	2043-10-01	2.25000	0.00000
165	3	2043-10-01	2044-04-01	2.25000	0.00000
166	3	2044-04-01	2044-10-01	2.25000	0.00000
167	3	2044-10-01	2045-04-01	2.25000	0.00000
168	3	2045-04-01	2045-10-01	2.25000	0.00000
169	3	2045-10-01	2046-04-01	2.25000	0.00000
171	3	2046-10-01	2047-04-01	2.25000	0.00000
172	3	2047-04-01	2047-10-01	2.25000	0.00000
173	3	2047-10-01	2048-04-01	2.25000	0.00000
174	3	2048-04-01	2048-10-01	2.25000	0.00000
175	3	2048-10-01	2049-04-01	2.25000	0.00000
176	3	2049-04-01	2049-10-01	2.25000	0.00000
177	3	2049-10-01	2050-04-01	2.25000	0.00000
178	3	2050-04-01	2050-10-01	2.25000	0.00000
179	3	2050-10-01	2051-04-01	2.25000	0.00000
180	3	2051-04-01	2051-10-01	2.25000	0.00000
181	3	2051-10-01	2052-04-01	2.25000	0.00000
182	3	2052-04-01	2052-10-01	2.25000	0.00000
183	3	2052-10-01	2053-04-01	2.25000	0.00000
184	3	2053-04-01	2053-10-01	2.25000	100.00000
186	4	2017-03-01	2017-09-01	1.40000	0.00000
187	4	2017-09-01	2018-03-01	1.40000	0.00000
188	4	2018-03-01	2018-09-01	1.40000	0.00000
189	4	2018-09-01	2019-03-01	1.40000	0.00000
190	4	2019-03-01	2019-09-01	1.40000	0.00000
191	4	2019-09-01	2020-03-01	1.40000	0.00000
192	4	2020-03-01	2020-09-01	1.40000	0.00000
193	4	2020-09-01	2021-03-01	1.40000	0.00000
194	4	2021-03-01	2021-09-01	1.40000	0.00000
195	4	2021-09-01	2022-03-01	1.40000	0.00000
196	4	2022-03-01	2022-09-01	1.40000	0.00000
197	4	2022-09-01	2023-03-01	1.40000	0.00000
198	4	2023-03-01	2023-09-01	1.40000	0.00000
199	4	2023-09-01	2024-03-01	1.40000	0.00000
201	4	2024-09-01	2025-03-01	1.40000	0.00000
202	4	2025-03-01	2025-09-01	1.40000	0.00000
203	4	2025-09-01	2026-03-01	1.40000	0.00000
204	4	2026-03-01	2026-09-01	1.40000	0.00000
205	4	2026-09-01	2027-03-01	1.40000	0.00000
206	4	2027-03-01	2027-09-01	1.40000	0.00000
207	4	2027-09-01	2028-03-01	1.40000	0.00000
208	4	2028-03-01	2028-09-01	1.40000	0.00000
209	4	2028-09-01	2029-03-01	1.40000	0.00000
210	4	2029-03-01	2029-09-01	1.40000	0.00000
211	4	2029-09-01	2030-03-01	1.40000	0.00000
212	4	2030-03-01	2030-09-01	1.40000	0.00000
213	4	2030-09-01	2031-03-01	1.40000	0.00000
214	4	2031-03-01	2031-09-01	1.40000	0.00000
216	4	2032-03-01	2032-09-01	1.40000	0.00000
217	4	2032-09-01	2033-03-01	1.40000	0.00000
218	4	2033-03-01	2033-09-01	1.40000	0.00000
219	4	2033-09-01	2034-03-01	1.40000	0.00000
220	4	2034-03-01	2034-09-01	1.40000	0.00000
221	4	2034-09-01	2035-03-01	1.40000	0.00000
222	4	2035-03-01	2035-09-01	1.40000	0.00000
223	4	2035-09-01	2036-03-01	1.40000	0.00000
224	4	2036-03-01	2036-09-01	1.40000	0.00000
225	4	2036-09-01	2037-03-01	1.40000	0.00000
226	4	2037-03-01	2037-09-01	1.40000	0.00000
227	4	2037-09-01	2038-03-01	1.40000	0.00000
228	4	2038-03-01	2038-09-01	1.40000	0.00000
229	4	2038-09-01	2039-03-01	1.40000	0.00000
231	4	2039-09-01	2040-03-01	1.40000	0.00000
232	4	2040-03-01	2040-09-01	1.40000	0.00000
233	4	2040-09-01	2041-03-01	1.40000	0.00000
234	4	2041-03-01	2041-09-01	1.40000	0.00000
235	4	2041-09-01	2042-03-01	1.40000	0.00000
236	4	2042-03-01	2042-09-01	1.40000	0.00000
237	4	2042-09-01	2043-03-01	1.40000	0.00000
238	4	2043-03-01	2043-09-01	1.40000	0.00000
239	4	2043-09-01	2044-03-01	1.40000	0.00000
240	4	2044-03-01	2044-09-01	1.40000	0.00000
241	4	2044-09-01	2045-03-01	1.40000	0.00000
242	4	2045-03-01	2045-09-01	1.40000	0.00000
243	4	2045-09-01	2046-03-01	1.40000	0.00000
244	4	2046-03-01	2046-09-01	1.40000	0.00000
246	4	2047-03-01	2047-09-01	1.40000	0.00000
247	4	2047-09-01	2048-03-01	1.40000	0.00000
248	4	2048-03-01	2048-09-01	1.40000	0.00000
249	4	2048-09-01	2049-03-01	1.40000	0.00000
250	4	2049-03-01	2049-09-01	1.40000	0.00000
251	4	2049-09-01	2050-03-01	1.40000	0.00000
252	4	2050-03-01	2050-09-01	1.40000	0.00000
253	4	2050-09-01	2051-03-01	1.40000	0.00000
254	4	2051-03-01	2051-09-01	1.40000	0.00000
255	4	2051-09-01	2052-03-01	1.40000	0.00000
256	4	2052-03-01	2052-09-01	1.40000	0.00000
257	4	2052-09-01	2053-03-01	1.40000	0.00000
258	4	2053-03-01	2053-09-01	1.40000	0.00000
259	4	2053-09-01	2054-03-01	1.40000	0.00000
261	4	2054-09-01	2055-03-01	1.40000	0.00000
262	4	2055-03-01	2055-09-01	1.40000	0.00000
263	4	2055-09-01	2056-03-01	1.40000	0.00000
264	4	2056-03-01	2056-09-01	1.40000	0.00000
265	4	2056-09-01	2057-03-01	1.40000	0.00000
266	4	2057-03-01	2057-09-01	1.40000	0.00000
267	4	2057-09-01	2058-03-01	1.40000	0.00000
268	4	2058-03-01	2058-09-01	1.40000	0.00000
269	4	2058-09-01	2059-03-01	1.40000	0.00000
270	4	2059-03-01	2059-09-01	1.40000	0.00000
271	4	2059-09-01	2060-03-01	1.40000	0.00000
272	4	2060-03-01	2060-09-01	1.40000	0.00000
274	4	2061-03-01	2061-09-01	1.40000	0.00000
275	4	2061-09-01	2062-03-01	1.40000	0.00000
276	4	2062-03-01	2062-09-01	1.40000	0.00000
277	4	2062-09-01	2063-03-01	1.40000	0.00000
279	4	2063-09-01	2064-03-01	1.40000	0.00000
280	4	2064-03-01	2064-09-01	1.40000	0.00000
281	4	2064-09-01	2065-03-01	1.40000	0.00000
282	4	2065-03-01	2065-09-01	1.40000	0.00000
283	4	2065-09-01	2066-03-01	1.40000	0.00000
284	4	2066-03-01	2066-09-01	1.40000	0.00000
285	4	2066-09-01	2067-03-01	1.40000	100.00000
286	5	2022-01-12	2022-03-01	0.28508	0.00000
287	5	2022-03-01	2022-09-01	1.07500	0.00000
288	5	2022-09-01	2023-03-01	1.07500	0.00000
289	5	2023-03-01	2023-09-01	1.07500	0.00000
290	5	2023-09-01	2024-03-01	1.07500	0.00000
291	5	2024-03-01	2024-09-01	1.07500	0.00000
292	5	2024-09-01	2025-03-01	1.07500	0.00000
294	5	2025-09-01	2026-03-01	1.07500	0.00000
295	5	2026-03-01	2026-09-01	1.07500	0.00000
296	5	2026-09-01	2027-03-01	1.07500	0.00000
297	5	2027-03-01	2027-09-01	1.07500	0.00000
298	5	2027-09-01	2028-03-01	1.07500	0.00000
299	5	2028-03-01	2028-09-01	1.07500	0.00000
300	5	2028-09-01	2029-03-01	1.07500	0.00000
301	5	2029-03-01	2029-09-01	1.07500	0.00000
302	5	2029-09-01	2030-03-01	1.07500	0.00000
303	5	2030-03-01	2030-09-01	1.07500	0.00000
304	5	2030-09-01	2031-03-01	1.07500	0.00000
305	5	2031-03-01	2031-09-01	1.07500	0.00000
306	5	2031-09-01	2032-03-01	1.07500	0.00000
307	5	2032-03-01	2032-09-01	1.07500	0.00000
309	5	2033-03-01	2033-09-01	1.07500	0.00000
310	5	2033-09-01	2034-03-01	1.07500	0.00000
311	5	2034-03-01	2034-09-01	1.07500	0.00000
312	5	2034-09-01	2035-03-01	1.07500	0.00000
313	5	2035-03-01	2035-09-01	1.07500	0.00000
314	5	2035-09-01	2036-03-01	1.07500	0.00000
315	5	2036-03-01	2036-09-01	1.07500	0.00000
316	5	2036-09-01	2037-03-01	1.07500	0.00000
317	5	2037-03-01	2037-09-01	1.07500	0.00000
318	5	2037-09-01	2038-03-01	1.07500	0.00000
319	5	2038-03-01	2038-09-01	1.07500	0.00000
320	5	2038-09-01	2039-03-01	1.07500	0.00000
321	5	2039-03-01	2039-09-01	1.07500	0.00000
322	5	2039-09-01	2040-03-01	1.07500	0.00000
324	5	2040-09-01	2041-03-01	1.07500	0.00000
325	5	2041-03-01	2041-09-01	1.07500	0.00000
326	5	2041-09-01	2042-03-01	1.07500	0.00000
327	5	2042-03-01	2042-09-01	1.07500	0.00000
328	5	2042-09-01	2043-03-01	1.07500	0.00000
329	5	2043-03-01	2043-09-01	1.07500	0.00000
330	5	2043-09-01	2044-03-01	1.07500	0.00000
331	5	2044-03-01	2044-09-01	1.07500	0.00000
332	5	2044-09-01	2045-03-01	1.07500	0.00000
333	5	2045-03-01	2045-09-01	1.07500	0.00000
334	5	2045-09-01	2046-03-01	1.07500	0.00000
335	5	2046-03-01	2046-09-01	1.07500	0.00000
336	5	2046-09-01	2047-03-01	1.07500	0.00000
337	5	2047-03-01	2047-09-01	1.07500	0.00000
339	5	2048-03-01	2048-09-01	1.07500	0.00000
340	5	2048-09-01	2049-03-01	1.07500	0.00000
341	5	2049-03-01	2049-09-01	1.07500	0.00000
342	5	2049-09-01	2050-03-01	1.07500	0.00000
343	5	2050-03-01	2050-09-01	1.07500	0.00000
344	5	2050-09-01	2051-03-01	1.07500	0.00000
345	5	2051-03-01	2051-09-01	1.07500	0.00000
346	5	2051-09-01	2052-03-01	1.07500	0.00000
347	5	2052-03-01	2052-09-01	1.07500	100.00000
348	6	2020-09-01	2021-03-01	0.85000	0.00000
349	6	2021-03-01	2021-09-01	0.85000	0.00000
350	6	2021-09-01	2022-03-01	0.85000	0.00000
351	6	2022-03-01	2022-09-01	0.85000	0.00000
352	6	2022-09-01	2023-03-01	0.85000	0.00000
353	6	2023-03-01	2023-09-01	0.85000	0.00000
354	6	2023-09-01	2024-03-01	0.85000	0.00000
356	6	2024-09-01	2025-03-01	0.85000	0.00000
357	6	2025-03-01	2025-09-01	0.85000	0.00000
358	6	2025-09-01	2026-03-01	0.85000	0.00000
359	6	2026-03-01	2026-09-01	0.85000	0.00000
360	6	2026-09-01	2027-03-01	0.85000	0.00000
361	6	2027-03-01	2027-09-01	0.85000	0.00000
362	6	2027-09-01	2028-03-01	0.85000	0.00000
363	6	2028-03-01	2028-09-01	0.85000	0.00000
364	6	2028-09-01	2029-03-01	0.85000	0.00000
365	6	2029-03-01	2029-09-01	0.85000	0.00000
366	6	2029-09-01	2030-03-01	0.85000	0.00000
367	6	2030-03-01	2030-09-01	0.85000	0.00000
369	6	2031-03-01	2031-09-01	0.85000	0.00000
370	6	2031-09-01	2032-03-01	0.85000	0.00000
371	6	2032-03-01	2032-09-01	0.85000	0.00000
372	6	2032-09-01	2033-03-01	0.85000	0.00000
373	6	2033-03-01	2033-09-01	0.85000	0.00000
374	6	2033-09-01	2034-03-01	0.85000	0.00000
375	6	2034-03-01	2034-09-01	0.85000	0.00000
376	6	2034-09-01	2035-03-01	0.85000	0.00000
377	6	2035-03-01	2035-09-01	0.85000	0.00000
378	6	2035-09-01	2036-03-01	0.85000	0.00000
379	6	2036-03-01	2036-09-01	0.85000	0.00000
380	6	2036-09-01	2037-03-01	0.85000	0.00000
382	6	2037-09-01	2038-03-01	0.85000	0.00000
383	6	2038-03-01	2038-09-01	0.85000	0.00000
384	6	2038-09-01	2039-03-01	0.85000	0.00000
385	6	2039-03-01	2039-09-01	0.85000	0.00000
386	6	2039-09-01	2040-03-01	0.85000	0.00000
387	6	2040-03-01	2040-09-01	0.85000	0.00000
388	6	2040-09-01	2041-03-01	0.85000	0.00000
389	6	2041-03-01	2041-09-01	0.85000	0.00000
390	6	2041-09-01	2042-03-01	0.85000	0.00000
391	6	2042-03-01	2042-09-01	0.85000	0.00000
392	6	2042-09-01	2043-03-01	0.85000	0.00000
393	6	2043-03-01	2043-09-01	0.85000	0.00000
395	6	2044-03-01	2044-09-01	0.85000	0.00000
396	6	2044-09-01	2045-03-01	0.85000	0.00000
397	6	2045-03-01	2045-09-01	0.85000	0.00000
398	6	2045-09-01	2046-03-01	0.85000	0.00000
399	6	2046-03-01	2046-09-01	0.85000	0.00000
400	6	2046-09-01	2047-03-01	0.85000	0.00000
401	6	2047-03-01	2047-09-01	0.85000	0.00000
402	6	2047-09-01	2048-03-01	0.85000	0.00000
403	6	2048-03-01	2048-09-01	0.85000	0.00000
404	6	2048-09-01	2049-03-01	0.85000	0.00000
405	6	2049-03-01	2049-09-01	0.85000	0.00000
406	6	2049-09-01	2050-03-01	0.85000	0.00000
408	6	2050-09-01	2051-03-01	0.85000	0.00000
409	6	2051-03-01	2051-09-01	0.85000	100.00000
410	7	2022-01-22	2022-03-01	1.22500	0.00000
411	7	2022-03-01	2022-09-01	1.22500	0.00000
412	7	2022-09-01	2023-03-01	1.22500	0.00000
413	7	2023-03-01	2023-09-01	1.22500	0.00000
414	7	2023-09-01	2024-03-01	1.22500	0.00000
415	7	2024-03-01	2024-09-01	1.22500	0.00000
416	7	2024-09-01	2025-03-01	1.22500	0.00000
418	7	2025-09-01	2026-03-01	1.22500	0.00000
419	7	2026-03-01	2026-09-01	1.22500	0.00000
421	7	2027-03-01	2027-09-01	1.22500	0.00000
422	7	2027-09-01	2028-03-01	1.22500	0.00000
423	7	2028-03-01	2028-09-01	1.22500	0.00000
424	7	2028-09-01	2029-03-01	1.22500	0.00000
425	7	2029-03-01	2029-09-01	1.22500	0.00000
426	7	2029-09-01	2030-03-01	1.22500	0.00000
427	7	2030-03-01	2030-09-01	1.22500	0.00000
428	7	2030-09-01	2031-03-01	1.22500	0.00000
429	7	2031-03-01	2031-09-01	1.22500	0.00000
430	7	2031-09-01	2032-03-01	1.22500	0.00000
431	7	2032-03-01	2032-09-01	1.22500	0.00000
432	7	2032-09-01	2033-03-01	1.22500	0.00000
433	7	2033-03-01	2033-09-01	1.22500	0.00000
434	7	2033-09-01	2034-03-01	1.22500	0.00000
436	7	2034-09-01	2035-03-01	1.22500	0.00000
437	7	2035-03-01	2035-09-01	1.22500	0.00000
438	7	2035-09-01	2036-03-01	1.22500	0.00000
439	7	2036-03-01	2036-09-01	1.22500	0.00000
440	7	2036-09-01	2037-03-01	1.22500	0.00000
441	7	2037-03-01	2037-09-01	1.22500	0.00000
442	7	2037-09-01	2038-03-01	1.22500	0.00000
443	7	2038-03-01	2038-09-01	1.22500	0.00000
444	7	2038-09-01	2039-03-01	1.22500	0.00000
445	7	2039-03-01	2039-09-01	1.22500	0.00000
446	7	2039-09-01	2040-03-01	1.22500	0.00000
447	7	2040-03-01	2040-09-01	1.22500	0.00000
448	7	2040-09-01	2041-03-01	1.22500	0.00000
449	7	2041-03-01	2041-09-01	1.22500	0.00000
451	7	2042-03-01	2042-09-01	1.22500	0.00000
452	7	2042-09-01	2043-03-01	1.22500	0.00000
453	7	2043-03-01	2043-09-01	1.22500	0.00000
454	7	2043-09-01	2044-03-01	1.22500	0.00000
455	7	2044-03-01	2044-09-01	1.22500	0.00000
456	7	2044-09-01	2045-03-01	1.22500	0.00000
457	7	2045-03-01	2045-09-01	1.22500	0.00000
458	7	2045-09-01	2046-03-01	1.22500	0.00000
459	7	2046-03-01	2046-09-01	1.22500	0.00000
460	7	2046-09-01	2047-03-01	1.22500	0.00000
461	7	2047-03-01	2047-09-01	1.22500	0.00000
462	7	2047-09-01	2048-03-01	1.22500	0.00000
463	7	2048-03-01	2048-09-01	1.22500	0.00000
464	7	2048-09-01	2049-03-01	1.22500	0.00000
466	7	2049-09-01	2050-03-01	1.22500	0.00000
467	7	2050-03-01	2050-09-01	1.22500	100.00000
468	8	2019-02-13	2019-03-01	0.17017	0.00000
469	8	2019-03-01	2019-09-01	1.92500	0.00000
470	8	2019-09-01	2020-03-01	1.92500	0.00000
471	8	2020-03-01	2020-09-01	1.92500	0.00000
472	8	2020-09-01	2021-03-01	1.92500	0.00000
473	8	2021-03-01	2021-09-01	1.92500	0.00000
474	8	2021-09-01	2022-03-01	1.92500	0.00000
475	8	2022-03-01	2022-09-01	1.92500	0.00000
476	8	2022-09-01	2023-03-01	1.92500	0.00000
477	8	2023-03-01	2023-09-01	1.92500	0.00000
478	8	2023-09-01	2024-03-01	1.92500	0.00000
479	8	2024-03-01	2024-09-01	1.92500	0.00000
481	8	2025-03-01	2025-09-01	1.92500	0.00000
482	8	2025-09-01	2026-03-01	1.92500	0.00000
483	8	2026-03-01	2026-09-01	1.92500	0.00000
484	8	2026-09-01	2027-03-01	1.92500	0.00000
485	8	2027-03-01	2027-09-01	1.92500	0.00000
486	8	2027-09-01	2028-03-01	1.92500	0.00000
487	8	2028-03-01	2028-09-01	1.92500	0.00000
488	8	2028-09-01	2029-03-01	1.92500	0.00000
489	8	2029-03-01	2029-09-01	1.92500	0.00000
490	8	2029-09-01	2030-03-01	1.92500	0.00000
491	8	2030-03-01	2030-09-01	1.92500	0.00000
492	8	2030-09-01	2031-03-01	1.92500	0.00000
493	8	2031-03-01	2031-09-01	1.92500	0.00000
494	8	2031-09-01	2032-03-01	1.92500	0.00000
496	8	2032-09-01	2033-03-01	1.92500	0.00000
497	8	2033-03-01	2033-09-01	1.92500	0.00000
498	8	2033-09-01	2034-03-01	1.92500	0.00000
499	8	2034-03-01	2034-09-01	1.92500	0.00000
500	8	2034-09-01	2035-03-01	1.92500	0.00000
501	8	2035-03-01	2035-09-01	1.92500	0.00000
502	8	2035-09-01	2036-03-01	1.92500	0.00000
503	8	2036-03-01	2036-09-01	1.92500	0.00000
504	8	2036-09-01	2037-03-01	1.92500	0.00000
505	8	2037-03-01	2037-09-01	1.92500	0.00000
506	8	2037-09-01	2038-03-01	1.92500	0.00000
507	8	2038-03-01	2038-09-01	1.92500	0.00000
508	8	2038-09-01	2039-03-01	1.92500	0.00000
509	8	2039-03-01	2039-09-01	1.92500	0.00000
511	8	2040-03-01	2040-09-01	1.92500	0.00000
512	8	2040-09-01	2041-03-01	1.92500	0.00000
513	8	2041-03-01	2041-09-01	1.92500	0.00000
514	8	2041-09-01	2042-03-01	1.92500	0.00000
515	8	2042-03-01	2042-09-01	1.92500	0.00000
516	8	2042-09-01	2043-03-01	1.92500	0.00000
517	8	2043-03-01	2043-09-01	1.92500	0.00000
518	8	2043-09-01	2044-03-01	1.92500	0.00000
519	8	2044-03-01	2044-09-01	1.92500	0.00000
520	8	2044-09-01	2045-03-01	1.92500	0.00000
521	8	2045-03-01	2045-09-01	1.92500	0.00000
522	8	2045-09-01	2046-03-01	1.92500	0.00000
523	8	2046-03-01	2046-09-01	1.92500	0.00000
524	8	2046-09-01	2047-03-01	1.92500	0.00000
526	8	2047-09-01	2048-03-01	1.92500	0.00000
527	8	2048-03-01	2048-09-01	1.92500	0.00000
528	8	2048-09-01	2049-03-01	1.92500	0.00000
529	8	2049-03-01	2049-09-01	1.92500	100.00000
530	9	2017-03-01	2017-09-01	1.72500	0.00000
531	9	2017-09-01	2018-03-01	1.72500	0.00000
532	9	2018-03-01	2018-09-01	1.72500	0.00000
533	9	2018-09-01	2019-03-01	1.72500	0.00000
534	9	2019-03-01	2019-09-01	1.72500	0.00000
535	9	2019-09-01	2020-03-01	1.72500	0.00000
536	9	2020-03-01	2020-09-01	1.72500	0.00000
537	9	2020-09-01	2021-03-01	1.72500	0.00000
538	9	2021-03-01	2021-09-01	1.72500	0.00000
539	9	2021-09-01	2022-03-01	1.72500	0.00000
541	9	2022-09-01	2023-03-01	1.72500	0.00000
542	9	2023-03-01	2023-09-01	1.72500	0.00000
543	9	2023-09-01	2024-03-01	1.72500	0.00000
544	9	2024-03-01	2024-09-01	1.72500	0.00000
545	9	2024-09-01	2025-03-01	1.72500	0.00000
546	9	2025-03-01	2025-09-01	1.72500	0.00000
547	9	2025-09-01	2026-03-01	1.72500	0.00000
548	9	2026-03-01	2026-09-01	1.72500	0.00000
549	9	2026-09-01	2027-03-01	1.72500	0.00000
550	9	2027-03-01	2027-09-01	1.72500	0.00000
551	9	2027-09-01	2028-03-01	1.72500	0.00000
552	9	2028-03-01	2028-09-01	1.72500	0.00000
554	9	2029-03-01	2029-09-01	1.72500	0.00000
555	9	2029-09-01	2030-03-01	1.72500	0.00000
557	9	2030-09-01	2031-03-01	1.72500	0.00000
558	9	2031-03-01	2031-09-01	1.72500	0.00000
559	9	2031-09-01	2032-03-01	1.72500	0.00000
560	9	2032-03-01	2032-09-01	1.72500	0.00000
561	9	2032-09-01	2033-03-01	1.72500	0.00000
562	9	2033-03-01	2033-09-01	1.72500	0.00000
563	9	2033-09-01	2034-03-01	1.72500	0.00000
564	9	2034-03-01	2034-09-01	1.72500	0.00000
565	9	2034-09-01	2035-03-01	1.72500	0.00000
566	9	2035-03-01	2035-09-01	1.72500	0.00000
567	9	2035-09-01	2036-03-01	1.72500	0.00000
568	9	2036-03-01	2036-09-01	1.72500	0.00000
569	9	2036-09-01	2037-03-01	1.72500	0.00000
570	9	2037-03-01	2037-09-01	1.72500	0.00000
572	9	2038-03-01	2038-09-01	1.72500	0.00000
573	9	2038-09-01	2039-03-01	1.72500	0.00000
574	9	2039-03-01	2039-09-01	1.72500	0.00000
575	9	2039-09-01	2040-03-01	1.72500	0.00000
576	9	2040-03-01	2040-09-01	1.72500	0.00000
577	9	2040-09-01	2041-03-01	1.72500	0.00000
578	9	2041-03-01	2041-09-01	1.72500	0.00000
579	9	2041-09-01	2042-03-01	1.72500	0.00000
580	9	2042-03-01	2042-09-01	1.72500	0.00000
581	9	2042-09-01	2043-03-01	1.72500	0.00000
582	9	2043-03-01	2043-09-01	1.72500	0.00000
583	9	2043-09-01	2044-03-01	1.72500	0.00000
584	9	2044-03-01	2044-09-01	1.72500	0.00000
585	9	2044-09-01	2045-03-01	1.72500	0.00000
587	9	2045-09-01	2046-03-01	1.72500	0.00000
588	9	2046-03-01	2046-09-01	1.72500	0.00000
589	9	2046-09-01	2047-03-01	1.72500	0.00000
590	9	2047-03-01	2047-09-01	1.72500	0.00000
591	9	2047-09-01	2048-03-01	1.72500	100.00000
592	10	2016-02-09	2016-03-01	0.15577	0.00000
593	10	2016-03-01	2016-09-01	1.35000	0.00000
594	10	2016-09-01	2017-03-01	1.35000	0.00000
595	10	2017-03-01	2017-09-01	1.35000	0.00000
596	10	2017-09-01	2018-03-01	1.35000	0.00000
597	10	2018-03-01	2018-09-01	1.35000	0.00000
598	10	2018-09-01	2019-03-01	1.35000	0.00000
599	10	2019-03-01	2019-09-01	1.35000	0.00000
600	10	2019-09-01	2020-03-01	1.35000	0.00000
602	10	2020-09-01	2021-03-01	1.35000	0.00000
603	10	2021-03-01	2021-09-01	1.35000	0.00000
604	10	2021-09-01	2022-03-01	1.35000	0.00000
605	10	2022-03-01	2022-09-01	1.35000	0.00000
606	10	2022-09-01	2023-03-01	1.35000	0.00000
607	10	2023-03-01	2023-09-01	1.35000	0.00000
608	10	2023-09-01	2024-03-01	1.35000	0.00000
609	10	2024-03-01	2024-09-01	1.35000	0.00000
610	10	2024-09-01	2025-03-01	1.35000	0.00000
611	10	2025-03-01	2025-09-01	1.35000	0.00000
612	10	2025-09-01	2026-03-01	1.35000	0.00000
613	10	2026-03-01	2026-09-01	1.35000	0.00000
614	10	2026-09-01	2027-03-01	1.35000	0.00000
615	10	2027-03-01	2027-09-01	1.35000	0.00000
617	10	2028-03-01	2028-09-01	1.35000	0.00000
618	10	2028-09-01	2029-03-01	1.35000	0.00000
619	10	2029-03-01	2029-09-01	1.35000	0.00000
620	10	2029-09-01	2030-03-01	1.35000	0.00000
621	10	2030-03-01	2030-09-01	1.35000	0.00000
622	10	2030-09-01	2031-03-01	1.35000	0.00000
623	10	2031-03-01	2031-09-01	1.35000	0.00000
624	10	2031-09-01	2032-03-01	1.35000	0.00000
625	10	2032-03-01	2032-09-01	1.35000	0.00000
626	10	2032-09-01	2033-03-01	1.35000	0.00000
627	10	2033-03-01	2033-09-01	1.35000	0.00000
628	10	2033-09-01	2034-03-01	1.35000	0.00000
629	10	2034-03-01	2034-09-01	1.35000	0.00000
630	10	2034-09-01	2035-03-01	1.35000	0.00000
632	10	2035-09-01	2036-03-01	1.35000	0.00000
633	10	2036-03-01	2036-09-01	1.35000	0.00000
634	10	2036-09-01	2037-03-01	1.35000	0.00000
635	10	2037-03-01	2037-09-01	1.35000	0.00000
636	10	2037-09-01	2038-03-01	1.35000	0.00000
637	10	2038-03-01	2038-09-01	1.35000	0.00000
638	10	2038-09-01	2039-03-01	1.35000	0.00000
639	10	2039-03-01	2039-09-01	1.35000	0.00000
640	10	2039-09-01	2040-03-01	1.35000	0.00000
641	10	2040-03-01	2040-09-01	1.35000	0.00000
642	10	2040-09-01	2041-03-01	1.35000	0.00000
643	10	2041-03-01	2041-09-01	1.35000	0.00000
644	10	2041-09-01	2042-03-01	1.35000	0.00000
645	10	2042-03-01	2042-09-01	1.35000	0.00000
647	10	2043-03-01	2043-09-01	1.35000	0.00000
648	10	2043-09-01	2044-03-01	1.35000	0.00000
649	10	2044-03-01	2044-09-01	1.35000	0.00000
650	10	2044-09-01	2045-03-01	1.35000	0.00000
651	10	2045-03-01	2045-09-01	1.35000	0.00000
652	10	2045-09-01	2046-03-01	1.35000	0.00000
653	10	2046-03-01	2046-09-01	1.35000	0.00000
654	10	2046-09-01	2047-03-01	1.35000	100.00000
655	11	2021-03-01	2021-09-01	1.07500	0.00000
656	11	2021-09-01	2022-03-01	1.07500	0.00000
657	11	2022-03-01	2022-09-01	1.07500	0.00000
658	11	2022-09-01	2023-03-01	1.07500	0.00000
659	11	2023-03-01	2023-09-01	1.07500	0.00000
660	11	2023-09-01	2024-03-01	1.07500	0.00000
662	11	2024-09-01	2025-03-01	1.07500	0.00000
663	11	2025-03-01	2025-09-01	1.07500	0.00000
664	11	2025-09-01	2026-03-01	1.07500	0.00000
665	11	2026-03-01	2026-09-01	1.07500	0.00000
666	11	2026-09-01	2027-03-01	1.07500	0.00000
667	11	2027-03-01	2027-09-01	1.07500	0.00000
668	11	2027-09-01	2028-03-01	1.07500	0.00000
669	11	2028-03-01	2028-09-01	1.07500	0.00000
670	11	2028-09-01	2029-03-01	1.07500	0.00000
671	11	2029-03-01	2029-09-01	1.07500	0.00000
672	11	2029-09-01	2030-03-01	1.07500	0.00000
673	11	2030-03-01	2030-09-01	1.07500	0.00000
674	11	2030-09-01	2031-03-01	1.07500	0.00000
675	11	2031-03-01	2031-09-01	1.07500	0.00000
677	11	2032-03-01	2032-09-01	1.07500	0.00000
678	11	2032-09-01	2033-03-01	1.07500	0.00000
679	11	2033-03-01	2033-09-01	1.07500	0.00000
680	11	2033-09-01	2034-03-01	1.07500	0.00000
681	11	2034-03-01	2034-09-01	1.07500	0.00000
682	11	2034-09-01	2035-03-01	1.07500	0.00000
683	11	2035-03-01	2035-09-01	1.07500	0.00000
684	11	2035-09-01	2036-03-01	1.07500	0.00000
685	11	2036-03-01	2036-09-01	1.07500	0.00000
686	11	2036-09-01	2037-03-01	1.07500	0.00000
687	11	2037-03-01	2037-09-01	1.07500	0.00000
688	11	2037-09-01	2038-03-01	1.07500	0.00000
690	11	2038-09-01	2039-03-01	1.07500	0.00000
691	11	2039-03-01	2039-09-01	1.07500	0.00000
693	11	2040-03-01	2040-09-01	1.07500	0.00000
694	11	2040-09-01	2041-03-01	1.07500	0.00000
695	11	2041-03-01	2041-09-01	1.07500	0.00000
696	11	2041-09-01	2042-03-01	1.07500	0.00000
697	11	2042-03-01	2042-09-01	1.07500	0.00000
698	11	2042-09-01	2043-03-01	1.07500	0.00000
699	11	2043-03-01	2043-09-01	1.07500	0.00000
700	11	2043-09-01	2044-03-01	1.07500	0.00000
701	11	2044-03-01	2044-09-01	1.07500	0.00000
702	11	2044-09-01	2045-03-01	1.07500	0.00000
703	11	2045-03-01	2045-09-01	1.07500	0.00000
704	11	2045-09-01	2046-03-01	1.07500	0.00000
705	11	2046-03-01	2046-09-01	1.07500	0.00000
706	11	2046-09-01	2047-03-01	1.07500	0.00000
708	11	2047-09-01	2048-03-01	1.07500	0.00000
709	11	2048-03-01	2048-09-01	1.07500	0.00000
710	11	2048-09-01	2049-03-01	1.07500	0.00000
711	11	2049-03-01	2049-09-01	1.07500	0.00000
712	11	2049-09-01	2050-03-01	1.07500	0.00000
713	11	2050-03-01	2050-09-01	1.07500	0.00000
714	11	2050-09-01	2051-03-01	1.07500	0.00000
715	11	2051-03-01	2051-09-01	1.07500	0.00000
716	11	2051-09-01	2052-03-01	1.07500	0.00000
717	11	2052-03-01	2052-09-01	1.07500	0.00000
718	11	2052-09-01	2053-03-01	1.07500	0.00000
719	11	2053-03-01	2053-09-01	1.07500	0.00000
720	11	2053-09-01	2054-03-01	1.07500	0.00000
721	11	2054-03-01	2054-09-01	1.07500	0.00000
723	11	2055-03-01	2055-09-01	1.07500	0.00000
724	11	2055-09-01	2056-03-01	1.07500	0.00000
725	11	2056-03-01	2056-09-01	1.07500	0.00000
726	11	2056-09-01	2057-03-01	1.07500	0.00000
727	11	2057-03-01	2057-09-01	1.07500	0.00000
728	11	2057-09-01	2058-03-01	1.07500	0.00000
729	11	2058-03-01	2058-09-01	1.07500	0.00000
730	11	2058-09-01	2059-03-01	1.07500	0.00000
731	11	2059-03-01	2059-09-01	1.07500	0.00000
732	11	2059-09-01	2060-03-01	1.07500	0.00000
733	11	2060-03-01	2060-09-01	1.07500	0.00000
734	11	2060-09-01	2061-03-01	1.07500	0.00000
735	11	2061-03-01	2061-09-01	1.07500	0.00000
736	11	2061-09-01	2062-03-01	1.07500	0.00000
738	11	2062-09-01	2063-03-01	1.07500	0.00000
739	11	2063-03-01	2063-09-01	1.07500	0.00000
740	11	2063-09-01	2064-03-01	1.07500	0.00000
741	11	2064-03-01	2064-09-01	1.07500	0.00000
742	11	2064-09-01	2065-03-01	1.07500	0.00000
743	11	2065-03-01	2065-09-01	1.07500	0.00000
744	11	2065-09-01	2066-03-01	1.07500	0.00000
745	11	2066-03-01	2066-09-01	1.07500	0.00000
746	11	2066-09-01	2067-03-01	1.07500	0.00000
747	11	2067-03-01	2067-09-01	1.07500	0.00000
748	11	2067-09-01	2068-03-01	1.07500	0.00000
749	11	2068-03-01	2068-09-01	1.07500	0.00000
750	11	2068-09-01	2069-03-01	1.07500	0.00000
751	11	2069-03-01	2069-09-01	1.07500	0.00000
753	11	2070-03-01	2070-09-01	1.07500	0.00000
754	11	2070-09-01	2071-03-01	1.07500	0.00000
755	11	2071-03-01	2071-09-01	1.07500	0.00000
756	11	2071-09-01	2072-03-01	1.07500	100.00000
757	12	2015-01-22	2015-03-01	0.34116	0.00000
758	12	2015-03-01	2015-09-01	1.62500	0.00000
759	12	2015-09-01	2016-03-01	1.62500	0.00000
760	12	2016-03-01	2016-09-01	1.62500	0.00000
761	12	2016-09-01	2017-03-01	1.62500	0.00000
762	12	2017-03-01	2017-09-01	1.62500	0.00000
763	12	2017-09-01	2018-03-01	1.62500	0.00000
764	12	2018-03-01	2018-09-01	1.62500	0.00000
765	12	2018-09-01	2019-03-01	1.62500	0.00000
766	12	2019-03-01	2019-09-01	1.62500	0.00000
768	12	2020-03-01	2020-09-01	1.62500	0.00000
769	12	2020-09-01	2021-03-01	1.62500	0.00000
770	12	2021-03-01	2021-09-01	1.62500	0.00000
771	12	2021-09-01	2022-03-01	1.62500	0.00000
772	12	2022-03-01	2022-09-01	1.62500	0.00000
773	12	2022-09-01	2023-03-01	1.62500	0.00000
774	12	2023-03-01	2023-09-01	1.62500	0.00000
775	12	2023-09-01	2024-03-01	1.62500	0.00000
776	12	2024-03-01	2024-09-01	1.62500	0.00000
777	12	2024-09-01	2025-03-01	1.62500	0.00000
778	12	2025-03-01	2025-09-01	1.62500	0.00000
779	12	2025-09-01	2026-03-01	1.62500	0.00000
780	12	2026-03-01	2026-09-01	1.62500	0.00000
781	12	2026-09-01	2027-03-01	1.62500	0.00000
783	12	2027-09-01	2028-03-01	1.62500	0.00000
784	12	2028-03-01	2028-09-01	1.62500	0.00000
785	12	2028-09-01	2029-03-01	1.62500	0.00000
786	12	2029-03-01	2029-09-01	1.62500	0.00000
787	12	2029-09-01	2030-03-01	1.62500	0.00000
788	12	2030-03-01	2030-09-01	1.62500	0.00000
789	12	2030-09-01	2031-03-01	1.62500	0.00000
790	12	2031-03-01	2031-09-01	1.62500	0.00000
791	12	2031-09-01	2032-03-01	1.62500	0.00000
792	12	2032-03-01	2032-09-01	1.62500	0.00000
793	12	2032-09-01	2033-03-01	1.62500	0.00000
794	12	2033-03-01	2033-09-01	1.62500	0.00000
795	12	2033-09-01	2034-03-01	1.62500	0.00000
796	12	2034-03-01	2034-09-01	1.62500	0.00000
798	12	2035-03-01	2035-09-01	1.62500	0.00000
799	12	2035-09-01	2036-03-01	1.62500	0.00000
800	12	2036-03-01	2036-09-01	1.62500	0.00000
801	12	2036-09-01	2037-03-01	1.62500	0.00000
802	12	2037-03-01	2037-09-01	1.62500	0.00000
803	12	2037-09-01	2038-03-01	1.62500	0.00000
804	12	2038-03-01	2038-09-01	1.62500	0.00000
805	12	2038-09-01	2039-03-01	1.62500	0.00000
806	12	2039-03-01	2039-09-01	1.62500	0.00000
807	12	2039-09-01	2040-03-01	1.62500	0.00000
808	12	2040-03-01	2040-09-01	1.62500	0.00000
809	12	2040-09-01	2041-03-01	1.62500	0.00000
810	12	2041-03-01	2041-09-01	1.62500	0.00000
811	12	2041-09-01	2042-03-01	1.62500	0.00000
813	12	2042-09-01	2043-03-01	1.62500	0.00000
814	12	2043-03-01	2043-09-01	1.62500	0.00000
815	12	2043-09-01	2044-03-01	1.62500	0.00000
816	12	2044-03-01	2044-09-01	1.62500	0.00000
817	12	2044-09-01	2045-03-01	1.62500	0.00000
818	12	2045-03-01	2045-09-01	1.62500	0.00000
819	12	2045-09-01	2046-03-01	1.62500	0.00000
820	12	2046-03-01	2046-09-01	1.62500	100.00000
821	13	2025-01-15	2025-04-30	1.18269	0.00000
822	13	2025-04-30	2025-10-30	2.05000	0.00000
823	13	2025-10-30	2026-04-30	2.05000	0.00000
824	13	2026-04-30	2026-10-30	2.05000	0.00000
826	13	2027-04-30	2027-10-30	2.05000	0.00000
827	13	2027-10-30	2028-04-30	2.05000	0.00000
828	13	2028-04-30	2028-10-30	2.05000	0.00000
829	13	2028-10-30	2029-04-30	2.05000	0.00000
830	13	2029-04-30	2029-10-30	2.05000	0.00000
831	13	2029-10-30	2030-04-30	2.05000	0.00000
832	13	2030-04-30	2030-10-30	2.05000	0.00000
833	13	2030-10-30	2031-04-30	2.05000	0.00000
835	13	2031-10-30	2032-04-30	2.05000	0.00000
836	13	2032-04-30	2032-10-30	2.05000	0.00000
837	13	2032-10-30	2033-04-30	2.05000	0.00000
838	13	2033-04-30	2033-10-30	2.05000	0.00000
839	13	2033-10-30	2034-04-30	2.05000	0.00000
840	13	2034-04-30	2034-10-30	2.05000	0.00000
841	13	2034-10-30	2035-04-30	2.05000	0.00000
842	13	2035-04-30	2035-10-30	2.05000	0.00000
843	13	2035-10-30	2036-04-30	2.05000	0.00000
844	13	2036-04-30	2036-10-30	2.05000	0.00000
845	13	2036-10-30	2037-04-30	2.05000	0.00000
846	13	2037-04-30	2037-10-30	2.05000	0.00000
847	13	2037-10-30	2038-04-30	2.05000	0.00000
848	13	2038-04-30	2038-10-30	2.05000	0.00000
850	13	2039-04-30	2039-10-30	2.05000	0.00000
851	13	2039-10-30	2040-04-30	2.05000	0.00000
852	13	2040-04-30	2040-10-30	2.05000	0.00000
853	13	2040-10-30	2041-04-30	2.05000	0.00000
854	13	2041-04-30	2041-10-30	2.05000	0.00000
855	13	2041-10-30	2042-04-30	2.05000	0.00000
856	13	2042-04-30	2042-10-30	2.05000	0.00000
857	13	2042-10-30	2043-04-30	2.05000	0.00000
858	13	2043-04-30	2043-10-30	2.05000	0.00000
859	13	2043-10-30	2044-04-30	2.05000	0.00000
860	13	2044-04-30	2044-10-30	2.05000	0.00000
861	13	2044-10-30	2045-04-30	2.05000	0.00000
862	13	2045-04-30	2045-10-30	2.05000	0.00000
863	13	2045-10-30	2046-04-30	2.05000	100.00000
864	14	2020-10-30	2021-04-30	0.75000	0.00000
865	14	2021-04-30	2021-10-30	0.75000	0.00000
867	14	2022-04-30	2022-10-30	0.75000	0.00000
868	14	2022-10-30	2023-04-30	0.75000	0.00000
869	14	2023-04-30	2023-10-30	0.75000	0.00000
870	14	2023-10-30	2024-04-30	0.75000	0.00000
871	14	2024-04-30	2024-10-30	0.75000	0.00000
872	14	2024-10-30	2025-04-30	0.75000	0.00000
873	14	2025-04-30	2025-10-30	0.75000	0.00000
874	14	2025-10-30	2026-04-30	0.75000	0.00000
875	14	2026-04-30	2026-10-30	0.75000	0.00000
876	14	2026-10-30	2027-04-30	0.75000	0.00000
877	14	2027-04-30	2027-10-30	0.75000	0.00000
878	14	2027-10-30	2028-04-30	0.75000	0.00000
880	14	2028-10-30	2029-04-30	0.75000	0.00000
881	14	2029-04-30	2029-10-30	0.75000	0.00000
882	14	2029-10-30	2030-04-30	0.75000	0.00000
883	14	2030-04-30	2030-10-30	0.75000	0.00000
884	14	2030-10-30	2031-04-30	0.75000	0.00000
885	14	2031-04-30	2031-10-30	0.75000	0.00000
886	14	2031-10-30	2032-04-30	0.75000	0.00000
887	14	2032-04-30	2032-10-30	0.75000	0.00000
888	14	2032-10-30	2033-04-30	0.75000	0.00000
889	14	2033-04-30	2033-10-30	0.75000	0.00000
890	14	2033-10-30	2034-04-30	0.75000	0.00000
891	14	2034-04-30	2034-10-30	0.75000	0.00000
893	14	2035-04-30	2035-10-30	0.75000	0.00000
894	14	2035-10-30	2036-04-30	0.75000	0.00000
895	14	2036-04-30	2036-10-30	0.75000	0.00000
896	14	2036-10-30	2037-04-30	0.75000	0.00000
897	14	2037-04-30	2037-10-30	0.75000	0.00000
898	14	2037-10-30	2038-04-30	0.75000	0.00000
899	14	2038-04-30	2038-10-30	0.75000	0.00000
900	14	2038-10-30	2039-04-30	0.75000	0.00000
901	14	2039-04-30	2039-10-30	0.75000	0.00000
902	14	2039-10-30	2040-04-30	0.75000	0.00000
903	14	2040-04-30	2040-10-30	0.75000	0.00000
904	14	2040-10-30	2041-04-30	0.75000	0.00000
906	14	2041-10-30	2042-04-30	0.75000	0.00000
907	14	2042-04-30	2042-10-30	0.75000	0.00000
908	14	2042-10-30	2043-04-30	0.75000	0.00000
909	14	2043-04-30	2043-10-30	0.75000	0.00000
910	14	2043-10-30	2044-04-30	0.75000	0.00000
911	14	2044-04-30	2044-10-30	0.75000	0.00000
912	14	2044-10-30	2045-04-30	0.75000	100.00000
913	15	2013-03-01	2013-09-01	2.37500	0.00000
914	15	2013-09-01	2014-03-01	2.37500	0.00000
915	15	2014-03-01	2014-09-01	2.37500	0.00000
917	15	2015-03-01	2015-09-01	2.37500	0.00000
918	15	2015-09-01	2016-03-01	2.37500	0.00000
919	15	2016-03-01	2016-09-01	2.37500	0.00000
920	15	2016-09-01	2017-03-01	2.37500	0.00000
921	15	2017-03-01	2017-09-01	2.37500	0.00000
922	15	2017-09-01	2018-03-01	2.37500	0.00000
923	15	2018-03-01	2018-09-01	2.37500	0.00000
924	15	2018-09-01	2019-03-01	2.37500	0.00000
925	15	2019-03-01	2019-09-01	2.37500	0.00000
926	15	2019-09-01	2020-03-01	2.37500	0.00000
927	15	2020-03-01	2020-09-01	2.37500	0.00000
928	15	2020-09-01	2021-03-01	2.37500	0.00000
929	15	2021-03-01	2021-09-01	2.37500	0.00000
930	15	2021-09-01	2022-03-01	2.37500	0.00000
932	15	2022-09-01	2023-03-01	2.37500	0.00000
933	15	2023-03-01	2023-09-01	2.37500	0.00000
934	15	2023-09-01	2024-03-01	2.37500	0.00000
935	15	2024-03-01	2024-09-01	2.37500	0.00000
936	15	2024-09-01	2025-03-01	2.37500	0.00000
937	15	2025-03-01	2025-09-01	2.37500	0.00000
938	15	2025-09-01	2026-03-01	2.37500	0.00000
939	15	2026-03-01	2026-09-01	2.37500	0.00000
940	15	2026-09-01	2027-03-01	2.37500	0.00000
941	15	2027-03-01	2027-09-01	2.37500	0.00000
942	15	2027-09-01	2028-03-01	2.37500	0.00000
943	15	2028-03-01	2028-09-01	2.37500	0.00000
944	15	2028-09-01	2029-03-01	2.37500	0.00000
945	15	2029-03-01	2029-09-01	2.37500	0.00000
947	15	2030-03-01	2030-09-01	2.37500	0.00000
948	15	2030-09-01	2031-03-01	2.37500	0.00000
949	15	2031-03-01	2031-09-01	2.37500	0.00000
950	15	2031-09-01	2032-03-01	2.37500	0.00000
951	15	2032-03-01	2032-09-01	2.37500	0.00000
952	15	2032-09-01	2033-03-01	2.37500	0.00000
953	15	2033-03-01	2033-09-01	2.37500	0.00000
954	15	2033-09-01	2034-03-01	2.37500	0.00000
955	15	2034-03-01	2034-09-01	2.37500	0.00000
956	15	2034-09-01	2035-03-01	2.37500	0.00000
957	15	2035-03-01	2035-09-01	2.37500	0.00000
958	15	2035-09-01	2036-03-01	2.37500	0.00000
959	15	2036-03-01	2036-09-01	2.37500	0.00000
960	15	2036-09-01	2037-03-01	2.37500	0.00000
962	15	2037-09-01	2038-03-01	2.37500	0.00000
963	15	2038-03-01	2038-09-01	2.37500	0.00000
964	15	2038-09-01	2039-03-01	2.37500	0.00000
965	15	2039-03-01	2039-09-01	2.37500	0.00000
966	15	2039-09-01	2040-03-01	2.37500	0.00000
968	15	2040-09-01	2041-03-01	2.37500	0.00000
969	15	2041-03-01	2041-09-01	2.37500	0.00000
970	15	2041-09-01	2042-03-01	2.37500	0.00000
971	15	2042-03-01	2042-09-01	2.37500	0.00000
972	15	2042-09-01	2043-03-01	2.37500	0.00000
974	15	2043-09-01	2044-03-01	2.37500	0.00000
975	15	2044-03-01	2044-09-01	2.37500	100.00000
976	16	2022-09-01	2023-03-01	2.22500	0.00000
977	16	2023-03-01	2023-09-01	2.22500	0.00000
978	16	2023-09-01	2024-03-01	2.22500	0.00000
979	16	2024-03-01	2024-09-01	2.22500	0.00000
980	16	2024-09-01	2025-03-01	2.22500	0.00000
981	16	2025-03-01	2025-09-01	2.22500	0.00000
982	16	2025-09-01	2026-03-01	2.22500	0.00000
983	16	2026-03-01	2026-09-01	2.22500	0.00000
984	16	2026-09-01	2027-03-01	2.22500	0.00000
985	16	2027-03-01	2027-09-01	2.22500	0.00000
986	16	2027-09-01	2028-03-01	2.22500	0.00000
987	16	2028-03-01	2028-09-01	2.22500	0.00000
989	16	2029-03-01	2029-09-01	2.22500	0.00000
990	16	2029-09-01	2030-03-01	2.22500	0.00000
991	16	2030-03-01	2030-09-01	2.22500	0.00000
992	16	2030-09-01	2031-03-01	2.22500	0.00000
993	16	2031-03-01	2031-09-01	2.22500	0.00000
994	16	2031-09-01	2032-03-01	2.22500	0.00000
995	16	2032-03-01	2032-09-01	2.22500	0.00000
996	16	2032-09-01	2033-03-01	2.22500	0.00000
997	16	2033-03-01	2033-09-01	2.22500	0.00000
998	16	2033-09-01	2034-03-01	2.22500	0.00000
999	16	2034-03-01	2034-09-01	2.22500	0.00000
1000	16	2034-09-01	2035-03-01	2.22500	0.00000
1001	16	2035-03-01	2035-09-01	2.22500	0.00000
1002	16	2035-09-01	2036-03-01	2.22500	0.00000
1004	16	2036-09-01	2037-03-01	2.22500	0.00000
1005	16	2037-03-01	2037-09-01	2.22500	0.00000
1006	16	2037-09-01	2038-03-01	2.22500	0.00000
1007	16	2038-03-01	2038-09-01	2.22500	0.00000
1008	16	2038-09-01	2039-03-01	2.22500	0.00000
1009	16	2039-03-01	2039-09-01	2.22500	0.00000
1010	16	2039-09-01	2040-03-01	2.22500	0.00000
1011	16	2040-03-01	2040-09-01	2.22500	0.00000
1012	16	2040-09-01	2041-03-01	2.22500	0.00000
1013	16	2041-03-01	2041-09-01	2.22500	0.00000
1014	16	2041-09-01	2042-03-01	2.22500	0.00000
1015	16	2042-03-01	2042-09-01	2.22500	0.00000
1016	16	2042-09-01	2043-03-01	2.22500	0.00000
1017	16	2043-03-01	2043-09-01	2.22500	100.00000
1019	17	2021-03-01	2021-09-01	0.90000	0.00000
1020	17	2021-09-01	2022-03-01	0.90000	0.00000
1021	17	2022-03-01	2022-09-01	0.90000	0.00000
1022	17	2022-09-01	2023-03-01	0.90000	0.00000
1023	17	2023-03-01	2023-09-01	0.90000	0.00000
1024	17	2023-09-01	2024-03-01	0.90000	0.00000
1025	17	2024-03-01	2024-09-01	0.90000	0.00000
1026	17	2024-09-01	2025-03-01	0.90000	0.00000
1027	17	2025-03-01	2025-09-01	0.90000	0.00000
1028	17	2025-09-01	2026-03-01	0.90000	0.00000
1029	17	2026-03-01	2026-09-01	0.90000	0.00000
1030	17	2026-09-01	2027-03-01	0.90000	0.00000
1031	17	2027-03-01	2027-09-01	0.90000	0.00000
1032	17	2027-09-01	2028-03-01	0.90000	0.00000
1033	17	2028-03-01	2028-09-01	0.90000	0.00000
1034	17	2028-09-01	2029-03-01	0.90000	0.00000
1036	17	2029-09-01	2030-03-01	0.90000	0.00000
1037	17	2030-03-01	2030-09-01	0.90000	0.00000
1038	17	2030-09-01	2031-03-01	0.90000	0.00000
1039	17	2031-03-01	2031-09-01	0.90000	0.00000
1040	17	2031-09-01	2032-03-01	0.90000	0.00000
1041	17	2032-03-01	2032-09-01	0.90000	0.00000
1042	17	2032-09-01	2033-03-01	0.90000	0.00000
1043	17	2033-03-01	2033-09-01	0.90000	0.00000
1044	17	2033-09-01	2034-03-01	0.90000	0.00000
1045	17	2034-03-01	2034-09-01	0.90000	0.00000
1046	17	2034-09-01	2035-03-01	0.90000	0.00000
1047	17	2035-03-01	2035-09-01	0.90000	0.00000
1049	17	2036-03-01	2036-09-01	0.90000	0.00000
1050	17	2036-09-01	2037-03-01	0.90000	0.00000
1051	17	2037-03-01	2037-09-01	0.90000	0.00000
1052	17	2037-09-01	2038-03-01	0.90000	0.00000
1053	17	2038-03-01	2038-09-01	0.90000	0.00000
1054	17	2038-09-01	2039-03-01	0.90000	0.00000
1055	17	2039-03-01	2039-09-01	0.90000	0.00000
1056	17	2039-09-01	2040-03-01	0.90000	0.00000
1057	17	2040-03-01	2040-09-01	0.90000	0.00000
1058	17	2040-09-01	2041-03-01	0.90000	100.00000
1060	18	2025-04-01	2025-10-01	1.92500	0.00000
1061	18	2025-10-01	2026-04-01	1.92500	0.00000
1062	18	2026-04-01	2026-10-01	1.92500	0.00000
1063	18	2026-10-01	2027-04-01	1.92500	0.00000
1064	18	2027-04-01	2027-10-01	1.92500	0.00000
1065	18	2027-10-01	2028-04-01	1.92500	0.00000
1066	18	2028-04-01	2028-10-01	1.92500	0.00000
1067	18	2028-10-01	2029-04-01	1.92500	0.00000
1068	18	2029-04-01	2029-10-01	1.92500	0.00000
1069	18	2029-10-01	2030-04-01	1.92500	0.00000
1070	18	2030-04-01	2030-10-01	1.92500	0.00000
1071	18	2030-10-01	2031-04-01	1.92500	0.00000
1072	18	2031-04-01	2031-10-01	1.92500	0.00000
1073	18	2031-10-01	2032-04-01	1.92500	0.00000
1075	18	2032-10-01	2033-04-01	1.92500	0.00000
1076	18	2033-04-01	2033-10-01	1.92500	0.00000
1077	18	2033-10-01	2034-04-01	1.92500	0.00000
1078	18	2034-04-01	2034-10-01	1.92500	0.00000
1079	18	2034-10-01	2035-04-01	1.92500	0.00000
1080	18	2035-04-01	2035-10-01	1.92500	0.00000
1081	18	2035-10-01	2036-04-01	1.92500	0.00000
1082	18	2036-04-01	2036-10-01	1.92500	0.00000
1083	18	2036-10-01	2037-04-01	1.92500	0.00000
1084	18	2037-04-01	2037-10-01	1.92500	0.00000
1085	18	2037-10-01	2038-04-01	1.92500	0.00000
1086	18	2038-04-01	2038-10-01	1.92500	0.00000
1087	18	2038-10-01	2039-04-01	1.92500	0.00000
1088	18	2039-04-01	2039-10-01	1.92500	0.00000
1090	18	2040-04-01	2040-10-01	1.92500	100.00000
1091	19	2009-09-01	2010-03-01	2.50000	0.00000
1092	19	2010-03-01	2010-09-01	2.50000	0.00000
1093	19	2010-09-01	2011-03-01	2.50000	0.00000
1094	19	2011-03-01	2011-09-01	2.50000	0.00000
1095	19	2011-09-01	2012-03-01	2.50000	0.00000
1096	19	2012-03-01	2012-09-01	2.50000	0.00000
1097	19	2012-09-01	2013-03-01	2.50000	0.00000
1098	19	2013-03-01	2013-09-01	2.50000	0.00000
1099	19	2013-09-01	2014-03-01	2.50000	0.00000
1100	19	2014-03-01	2014-09-01	2.50000	0.00000
1101	19	2014-09-01	2015-03-01	2.50000	0.00000
1102	19	2015-03-01	2015-09-01	2.50000	0.00000
1103	19	2015-09-01	2016-03-01	2.50000	0.00000
1105	19	2016-09-01	2017-03-01	2.50000	0.00000
1106	19	2017-03-01	2017-09-01	2.50000	0.00000
1107	19	2017-09-01	2018-03-01	2.50000	0.00000
1109	19	2018-09-01	2019-03-01	2.50000	0.00000
1110	19	2019-03-01	2019-09-01	2.50000	0.00000
1111	19	2019-09-01	2020-03-01	2.50000	0.00000
1112	19	2020-03-01	2020-09-01	2.50000	0.00000
1113	19	2020-09-01	2021-03-01	2.50000	0.00000
1114	19	2021-03-01	2021-09-01	2.50000	0.00000
1115	19	2021-09-01	2022-03-01	2.50000	0.00000
1116	19	2022-03-01	2022-09-01	2.50000	0.00000
1117	19	2022-09-01	2023-03-01	2.50000	0.00000
1118	19	2023-03-01	2023-09-01	2.50000	0.00000
1120	19	2024-03-01	2024-09-01	2.50000	0.00000
1121	19	2024-09-01	2025-03-01	2.50000	0.00000
1122	19	2025-03-01	2025-09-01	2.50000	0.00000
1123	19	2025-09-01	2026-03-01	2.50000	0.00000
1124	19	2026-03-01	2026-09-01	2.50000	0.00000
1125	19	2026-09-01	2027-03-01	2.50000	0.00000
1126	19	2027-03-01	2027-09-01	2.50000	0.00000
1127	19	2027-09-01	2028-03-01	2.50000	0.00000
1128	19	2028-03-01	2028-09-01	2.50000	0.00000
1129	19	2028-09-01	2029-03-01	2.50000	0.00000
1130	19	2029-03-01	2029-09-01	2.50000	0.00000
1131	19	2029-09-01	2030-03-01	2.50000	0.00000
1132	19	2030-03-01	2030-09-01	2.50000	0.00000
1133	19	2030-09-01	2031-03-01	2.50000	0.00000
1135	19	2031-09-01	2032-03-01	2.50000	0.00000
1136	19	2032-03-01	2032-09-01	2.50000	0.00000
1137	19	2032-09-01	2033-03-01	2.50000	0.00000
1138	19	2033-03-01	2033-09-01	2.50000	0.00000
1139	19	2033-09-01	2034-03-01	2.50000	0.00000
1140	19	2034-03-01	2034-09-01	2.50000	0.00000
1141	19	2034-09-01	2035-03-01	2.50000	0.00000
1142	19	2035-03-01	2035-09-01	2.50000	0.00000
1143	19	2035-09-01	2036-03-01	2.50000	0.00000
1144	19	2036-03-01	2036-09-01	2.50000	0.00000
1145	19	2036-09-01	2037-03-01	2.50000	0.00000
1146	19	2037-03-01	2037-09-01	2.50000	0.00000
1147	19	2037-09-01	2038-03-01	2.50000	0.00000
1148	19	2038-03-01	2038-09-01	2.50000	0.00000
1150	19	2039-03-01	2039-09-01	2.50000	0.00000
1151	19	2039-09-01	2040-03-01	2.50000	0.00000
1152	19	2040-03-01	2040-09-01	2.50000	100.00000
1153	20	2019-06-19	2019-09-01	0.62337	0.00000
1154	20	2019-09-01	2020-03-01	1.55000	0.00000
1155	20	2020-03-01	2020-09-01	1.55000	0.00000
1156	20	2020-09-01	2021-03-01	1.55000	0.00000
1157	20	2021-03-01	2021-09-01	1.55000	0.00000
1158	20	2021-09-01	2022-03-01	1.55000	0.00000
1159	20	2022-03-01	2022-09-01	1.55000	0.00000
1160	20	2022-09-01	2023-03-01	1.55000	0.00000
1161	20	2023-03-01	2023-09-01	1.55000	0.00000
1162	20	2023-09-01	2024-03-01	1.55000	0.00000
1163	20	2024-03-01	2024-09-01	1.55000	0.00000
1165	20	2025-03-01	2025-09-01	1.55000	0.00000
1166	20	2025-09-01	2026-03-01	1.55000	0.00000
1167	20	2026-03-01	2026-09-01	1.55000	0.00000
1168	20	2026-09-01	2027-03-01	1.55000	0.00000
1169	20	2027-03-01	2027-09-01	1.55000	0.00000
1170	20	2027-09-01	2028-03-01	1.55000	0.00000
1171	20	2028-03-01	2028-09-01	1.55000	0.00000
1172	20	2028-09-01	2029-03-01	1.55000	0.00000
1173	20	2029-03-01	2029-09-01	1.55000	0.00000
1174	20	2029-09-01	2030-03-01	1.55000	0.00000
1175	20	2030-03-01	2030-09-01	1.55000	0.00000
1176	20	2030-09-01	2031-03-01	1.55000	0.00000
1177	20	2031-03-01	2031-09-01	1.55000	0.00000
1178	20	2031-09-01	2032-03-01	1.55000	0.00000
1180	20	2032-09-01	2033-03-01	1.55000	0.00000
1181	20	2033-03-01	2033-09-01	1.55000	0.00000
1182	20	2033-09-01	2034-03-01	1.55000	0.00000
1183	20	2034-03-01	2034-09-01	1.55000	0.00000
1184	20	2034-09-01	2035-03-01	1.55000	0.00000
1185	20	2035-03-01	2035-09-01	1.55000	0.00000
1186	20	2035-09-01	2036-03-01	1.55000	0.00000
1187	20	2036-03-01	2036-09-01	1.55000	0.00000
1188	20	2036-09-01	2037-03-01	1.55000	0.00000
1189	20	2037-03-01	2037-09-01	1.55000	0.00000
1190	20	2037-09-01	2038-03-01	1.55000	0.00000
1191	20	2038-03-01	2038-09-01	1.55000	0.00000
1192	20	2038-09-01	2039-03-01	1.55000	0.00000
1193	20	2039-03-01	2039-09-01	1.55000	0.00000
1195	21	2023-10-01	2024-04-01	2.07500	0.00000
1196	21	2024-04-01	2024-10-01	2.07500	0.00000
1197	21	2024-10-01	2025-04-01	2.07500	0.00000
1198	21	2025-04-01	2025-10-01	2.07500	0.00000
1199	21	2025-10-01	2026-04-01	2.07500	0.00000
1200	21	2026-04-01	2026-10-01	2.07500	0.00000
1201	21	2026-10-01	2027-04-01	2.07500	0.00000
1202	21	2027-04-01	2027-10-01	2.07500	0.00000
1203	21	2027-10-01	2028-04-01	2.07500	0.00000
1204	21	2028-04-01	2028-10-01	2.07500	0.00000
1205	21	2028-10-01	2029-04-01	2.07500	0.00000
1206	21	2029-04-01	2029-10-01	2.07500	0.00000
1207	21	2029-10-01	2030-04-01	2.07500	0.00000
1208	21	2030-04-01	2030-10-01	2.07500	0.00000
1210	21	2031-04-01	2031-10-01	2.07500	0.00000
1211	21	2031-10-01	2032-04-01	2.07500	0.00000
1212	21	2032-04-01	2032-10-01	2.07500	0.00000
1213	21	2032-10-01	2033-04-01	2.07500	0.00000
1214	21	2033-04-01	2033-10-01	2.07500	0.00000
1215	21	2033-10-01	2034-04-01	2.07500	0.00000
1216	21	2034-04-01	2034-10-01	2.07500	0.00000
1217	21	2034-10-01	2035-04-01	2.07500	0.00000
1218	21	2035-04-01	2035-10-01	2.07500	0.00000
1219	21	2035-10-01	2036-04-01	2.07500	0.00000
1220	21	2036-04-01	2036-10-01	2.07500	0.00000
1221	21	2036-10-01	2037-04-01	2.07500	0.00000
1222	21	2037-04-01	2037-10-01	2.07500	0.00000
1223	21	2037-10-01	2038-04-01	2.07500	0.00000
1225	21	2038-10-01	2039-04-01	2.07500	0.00000
1226	21	2039-04-01	2039-10-01	2.07500	100.00000
1227	22	2021-04-27	2021-10-27	0.37500	0.00000
1228	22	2021-10-27	2022-04-27	0.37500	0.00000
1229	22	2022-04-27	2022-10-27	0.37500	0.00000
1230	22	2022-10-27	2023-04-27	0.37500	0.00000
1231	22	2023-04-27	2023-10-27	0.37500	0.00000
1232	22	2023-10-27	2024-04-27	0.37500	0.00000
1233	22	2024-04-27	2024-10-27	0.37500	0.00000
1234	22	2024-10-27	2025-04-27	0.37500	0.00000
1235	22	2025-04-27	2025-10-27	0.60000	0.00000
1236	22	2025-10-27	2026-04-27	0.60000	0.00000
1237	22	2026-04-27	2026-10-27	0.60000	0.00000
1238	22	2026-10-27	2027-04-27	0.60000	0.00000
1239	22	2027-04-27	2027-10-27	0.60000	0.00000
1240	22	2027-10-27	2028-04-27	0.60000	0.00000
1242	22	2028-10-27	2029-04-27	0.60000	0.00000
1243	22	2029-04-27	2029-10-27	0.82500	0.00000
1244	22	2029-10-27	2030-04-27	0.82500	0.00000
1245	22	2030-04-27	2030-10-27	0.82500	0.00000
1247	22	2031-04-27	2031-10-27	0.82500	0.00000
1248	22	2031-10-27	2032-04-27	0.82500	0.00000
1249	22	2032-04-27	2032-10-27	0.82500	0.00000
1250	22	2032-10-27	2033-04-27	0.82500	0.00000
1251	22	2033-04-27	2033-10-27	1.00000	0.00000
1252	22	2033-10-27	2034-04-27	1.00000	0.00000
1253	22	2034-04-27	2034-10-27	1.00000	0.00000
1254	22	2034-10-27	2035-04-27	1.00000	0.00000
1255	22	2035-04-27	2035-10-27	1.00000	0.00000
1256	22	2035-10-27	2036-04-27	1.00000	0.00000
1257	22	2036-04-27	2036-10-27	1.00000	0.00000
1260	23	2008-02-01	2008-08-01	2.50000	0.00000
1261	23	2008-08-01	2009-02-01	2.50000	0.00000
1262	23	2009-02-01	2009-08-01	2.50000	0.00000
1263	23	2009-08-01	2010-02-01	2.50000	0.00000
1264	23	2010-02-01	2010-08-01	2.50000	0.00000
1265	23	2010-08-01	2011-02-01	2.50000	0.00000
1266	23	2011-02-01	2011-08-01	2.50000	0.00000
1267	23	2011-08-01	2012-02-01	2.50000	0.00000
1268	23	2012-02-01	2012-08-01	2.50000	0.00000
1269	23	2012-08-01	2013-02-01	2.50000	0.00000
1270	23	2013-02-01	2013-08-01	2.50000	0.00000
1271	23	2013-08-01	2014-02-01	2.50000	0.00000
1273	23	2014-08-01	2015-02-01	2.50000	0.00000
1274	23	2015-02-01	2015-08-01	2.50000	0.00000
1275	23	2015-08-01	2016-02-01	2.50000	0.00000
1276	23	2016-02-01	2016-08-01	2.50000	0.00000
1277	23	2016-08-01	2017-02-01	2.50000	0.00000
1278	23	2017-02-01	2017-08-01	2.50000	0.00000
1279	23	2017-08-01	2018-02-01	2.50000	0.00000
1280	23	2018-02-01	2018-08-01	2.50000	0.00000
1281	23	2018-08-01	2019-02-01	2.50000	0.00000
1282	23	2019-02-01	2019-08-01	2.50000	0.00000
1283	23	2019-08-01	2020-02-01	2.50000	0.00000
1284	23	2020-02-01	2020-08-01	2.50000	0.00000
1285	23	2020-08-01	2021-02-01	2.50000	0.00000
1286	23	2021-02-01	2021-08-01	2.50000	0.00000
1288	23	2022-02-01	2022-08-01	2.50000	0.00000
1289	23	2022-08-01	2023-02-01	2.50000	0.00000
1290	23	2023-02-01	2023-08-01	2.50000	0.00000
1291	23	2023-08-01	2024-02-01	2.50000	0.00000
1292	23	2024-02-01	2024-08-01	2.50000	0.00000
1293	23	2024-08-01	2025-02-01	2.50000	0.00000
1294	23	2025-02-01	2025-08-01	2.50000	0.00000
1295	23	2025-08-01	2026-02-01	2.50000	0.00000
1296	23	2026-02-01	2026-08-01	2.50000	0.00000
1297	23	2026-08-01	2027-02-01	2.50000	0.00000
1298	23	2027-02-01	2027-08-01	2.50000	0.00000
1299	23	2027-08-01	2028-02-01	2.50000	0.00000
1300	23	2028-02-01	2028-08-01	2.50000	0.00000
1301	23	2028-08-01	2029-02-01	2.50000	0.00000
1303	23	2029-08-01	2030-02-01	2.50000	0.00000
1304	23	2030-02-01	2030-08-01	2.50000	0.00000
1305	23	2030-08-01	2031-02-01	2.50000	0.00000
1306	23	2031-02-01	2031-08-01	2.50000	0.00000
1307	23	2031-08-01	2032-02-01	2.50000	0.00000
1308	23	2032-02-01	2032-08-01	2.50000	0.00000
1309	23	2032-08-01	2033-02-01	2.50000	0.00000
1310	23	2033-02-01	2033-08-01	2.50000	0.00000
1311	23	2033-08-01	2034-02-01	2.50000	0.00000
1312	23	2034-02-01	2034-08-01	2.50000	0.00000
1313	23	2034-08-01	2035-02-01	2.50000	0.00000
1314	23	2035-02-01	2035-08-01	2.50000	0.00000
1315	23	2035-08-01	2036-02-01	2.50000	0.00000
1316	23	2036-02-01	2036-08-01	2.50000	0.00000
1318	23	2037-02-01	2037-08-01	2.50000	0.00000
1319	23	2037-08-01	2038-02-01	2.50000	0.00000
1320	23	2038-02-01	2038-08-01	2.50000	0.00000
1321	23	2038-08-01	2039-02-01	2.50000	0.00000
1322	23	2039-02-01	2039-08-01	2.50000	100.00000
1323	24	2017-09-01	2018-03-01	1.47500	0.00000
1324	24	2018-03-01	2018-09-01	1.47500	0.00000
1325	24	2018-09-01	2019-03-01	1.47500	0.00000
1326	24	2019-03-01	2019-09-01	1.47500	0.00000
1327	24	2019-09-01	2020-03-01	1.47500	0.00000
1328	24	2020-03-01	2020-09-01	1.47500	0.00000
1329	24	2020-09-01	2021-03-01	1.47500	0.00000
1330	24	2021-03-01	2021-09-01	1.47500	0.00000
1331	24	2021-09-01	2022-03-01	1.47500	0.00000
1333	24	2022-09-01	2023-03-01	1.47500	0.00000
1334	24	2023-03-01	2023-09-01	1.47500	0.00000
1335	24	2023-09-01	2024-03-01	1.47500	0.00000
1336	24	2024-03-01	2024-09-01	1.47500	0.00000
1337	24	2024-09-01	2025-03-01	1.47500	0.00000
1338	24	2025-03-01	2025-09-01	1.47500	0.00000
1339	24	2025-09-01	2026-03-01	1.47500	0.00000
1340	24	2026-03-01	2026-09-01	1.47500	0.00000
1341	24	2026-09-01	2027-03-01	1.47500	0.00000
1342	24	2027-03-01	2027-09-01	1.47500	0.00000
1343	24	2027-09-01	2028-03-01	1.47500	0.00000
1344	24	2028-03-01	2028-09-01	1.47500	0.00000
1345	24	2028-09-01	2029-03-01	1.47500	0.00000
1346	24	2029-03-01	2029-09-01	1.47500	0.00000
1348	24	2030-03-01	2030-09-01	1.47500	0.00000
1349	24	2030-09-01	2031-03-01	1.47500	0.00000
1350	24	2031-03-01	2031-09-01	1.47500	0.00000
1351	24	2031-09-01	2032-03-01	1.47500	0.00000
1352	24	2032-03-01	2032-09-01	1.47500	0.00000
1353	24	2032-09-01	2033-03-01	1.47500	0.00000
1354	24	2033-03-01	2033-09-01	1.47500	0.00000
1355	24	2033-09-01	2034-03-01	1.47500	0.00000
1356	24	2034-03-01	2034-09-01	1.47500	0.00000
1357	24	2034-09-01	2035-03-01	1.47500	0.00000
1358	24	2035-03-01	2035-09-01	1.47500	0.00000
1359	24	2035-09-01	2036-03-01	1.47500	0.00000
1360	24	2036-03-01	2036-09-01	1.47500	0.00000
1361	24	2036-09-01	2037-03-01	1.47500	0.00000
1363	24	2037-09-01	2038-03-01	1.47500	0.00000
1364	24	2038-03-01	2038-09-01	1.47500	100.00000
1365	25	2022-03-01	2022-09-01	1.62500	0.00000
1366	25	2022-09-01	2023-03-01	1.62500	0.00000
1367	25	2023-03-01	2023-09-01	1.62500	0.00000
1368	25	2023-09-01	2024-03-01	1.62500	0.00000
1369	25	2024-03-01	2024-09-01	1.62500	0.00000
1370	25	2024-09-01	2025-03-01	1.62500	0.00000
1371	25	2025-03-01	2025-09-01	1.62500	0.00000
1372	25	2025-09-01	2026-03-01	1.62500	0.00000
1373	25	2026-03-01	2026-09-01	1.62500	0.00000
1374	25	2026-09-01	2027-03-01	1.62500	0.00000
1375	25	2027-03-01	2027-09-01	1.62500	0.00000
1376	25	2027-09-01	2028-03-01	1.62500	0.00000
1378	25	2028-09-01	2029-03-01	1.62500	0.00000
1379	25	2029-03-01	2029-09-01	1.62500	0.00000
1380	25	2029-09-01	2030-03-01	1.62500	0.00000
1381	25	2030-03-01	2030-09-01	1.62500	0.00000
1382	25	2030-09-01	2031-03-01	1.62500	0.00000
1383	25	2031-03-01	2031-09-01	1.62500	0.00000
1385	25	2032-03-01	2032-09-01	1.62500	0.00000
1386	25	2032-09-01	2033-03-01	1.62500	0.00000
1387	25	2033-03-01	2033-09-01	1.62500	0.00000
1388	25	2033-09-01	2034-03-01	1.62500	0.00000
1389	25	2034-03-01	2034-09-01	1.62500	0.00000
1390	25	2034-09-01	2035-03-01	1.62500	0.00000
1391	25	2035-03-01	2035-09-01	1.62500	0.00000
1392	25	2035-09-01	2036-03-01	1.62500	0.00000
1394	25	2036-09-01	2037-03-01	1.62500	0.00000
1395	25	2037-03-01	2037-09-01	1.62500	0.00000
1396	25	2037-09-01	2038-03-01	1.62500	100.00000
1397	26	2024-04-30	2024-10-30	2.02500	0.00000
1398	26	2024-10-30	2025-04-30	2.02500	0.00000
1399	26	2025-04-30	2025-10-30	2.02500	0.00000
1400	26	2025-10-30	2026-04-30	2.02500	0.00000
1401	26	2026-04-30	2026-10-30	2.02500	0.00000
1402	26	2026-10-30	2027-04-30	2.02500	0.00000
1403	26	2027-04-30	2027-10-30	2.02500	0.00000
1404	26	2027-10-30	2028-04-30	2.02500	0.00000
1405	26	2028-04-30	2028-10-30	2.02500	0.00000
1406	26	2028-10-30	2029-04-30	2.02500	0.00000
1407	26	2029-04-30	2029-10-30	2.02500	0.00000
1409	26	2030-04-30	2030-10-30	2.02500	0.00000
1410	26	2030-10-30	2031-04-30	2.02500	0.00000
1411	26	2031-04-30	2031-10-30	2.02500	0.00000
1412	26	2031-10-30	2032-04-30	2.02500	0.00000
1413	26	2032-04-30	2032-10-30	2.02500	0.00000
1414	26	2032-10-30	2033-04-30	2.02500	0.00000
1415	26	2033-04-30	2033-10-30	2.02500	0.00000
1416	26	2033-10-30	2034-04-30	2.02500	0.00000
1417	26	2034-04-30	2034-10-30	2.02500	0.00000
1418	26	2034-10-30	2035-04-30	2.02500	0.00000
1419	26	2035-04-30	2035-10-30	2.02500	0.00000
1420	26	2035-10-30	2036-04-30	2.02500	0.00000
1421	26	2036-04-30	2036-10-30	2.02500	0.00000
1422	26	2036-10-30	2037-04-30	2.02500	0.00000
1424	27	2021-01-12	2021-03-01	0.12597	0.00000
1425	27	2021-03-01	2021-09-01	0.47500	0.00000
1426	27	2021-09-01	2022-03-01	0.47500	0.00000
1427	27	2022-03-01	2022-09-01	0.47500	0.00000
1428	27	2022-09-01	2023-03-01	0.47500	0.00000
1429	27	2023-03-01	2023-09-01	0.47500	0.00000
1430	27	2023-09-01	2024-03-01	0.47500	0.00000
1431	27	2024-03-01	2024-09-01	0.47500	0.00000
1432	27	2024-09-01	2025-03-01	0.47500	0.00000
1433	27	2025-03-01	2025-09-01	0.47500	0.00000
1434	27	2025-09-01	2026-03-01	0.47500	0.00000
1435	27	2026-03-01	2026-09-01	0.47500	0.00000
1436	27	2026-09-01	2027-03-01	0.47500	0.00000
1437	27	2027-03-01	2027-09-01	0.47500	0.00000
1438	27	2027-09-01	2028-03-01	0.47500	0.00000
1439	27	2028-03-01	2028-09-01	0.47500	0.00000
1441	27	2029-03-01	2029-09-01	0.47500	0.00000
1442	27	2029-09-01	2030-03-01	0.47500	0.00000
1443	27	2030-03-01	2030-09-01	0.47500	0.00000
1444	27	2030-09-01	2031-03-01	0.47500	0.00000
1445	27	2031-03-01	2031-09-01	0.47500	0.00000
1446	27	2031-09-01	2032-03-01	0.47500	0.00000
1447	27	2032-03-01	2032-09-01	0.47500	0.00000
1448	27	2032-09-01	2033-03-01	0.47500	0.00000
1449	27	2033-03-01	2033-09-01	0.47500	0.00000
1450	27	2033-09-01	2034-03-01	0.47500	0.00000
1451	27	2034-03-01	2034-09-01	0.47500	0.00000
1452	27	2034-09-01	2035-03-01	0.47500	0.00000
1454	27	2035-09-01	2036-03-01	0.47500	0.00000
1455	27	2036-03-01	2036-09-01	0.47500	0.00000
1456	27	2036-09-01	2037-03-01	0.47500	100.00000
1457	28	2005-08-01	2006-02-01	2.00000	0.00000
1458	28	2006-02-01	2006-08-01	2.00000	0.00000
1459	28	2006-08-01	2007-02-01	2.00000	0.00000
1460	28	2007-02-01	2007-08-01	2.00000	0.00000
1461	28	2007-08-01	2008-02-01	2.00000	0.00000
1462	28	2008-02-01	2008-08-01	2.00000	0.00000
1463	28	2008-08-01	2009-02-01	2.00000	0.00000
1464	28	2009-02-01	2009-08-01	2.00000	0.00000
1465	28	2009-08-01	2010-02-01	2.00000	0.00000
1467	28	2010-08-01	2011-02-01	2.00000	0.00000
1468	28	2011-02-01	2011-08-01	2.00000	0.00000
1469	28	2011-08-01	2012-02-01	2.00000	0.00000
1470	28	2012-02-01	2012-08-01	2.00000	0.00000
1471	28	2012-08-01	2013-02-01	2.00000	0.00000
1472	28	2013-02-01	2013-08-01	2.00000	0.00000
1473	28	2013-08-01	2014-02-01	2.00000	0.00000
1474	28	2014-02-01	2014-08-01	2.00000	0.00000
1475	28	2014-08-01	2015-02-01	2.00000	0.00000
1476	28	2015-02-01	2015-08-01	2.00000	0.00000
1477	28	2015-08-01	2016-02-01	2.00000	0.00000
1478	28	2016-02-01	2016-08-01	2.00000	0.00000
1480	28	2017-02-01	2017-08-01	2.00000	0.00000
1481	28	2017-08-01	2018-02-01	2.00000	0.00000
1482	28	2018-02-01	2018-08-01	2.00000	0.00000
1483	28	2018-08-01	2019-02-01	2.00000	0.00000
1484	28	2019-02-01	2019-08-01	2.00000	0.00000
1485	28	2019-08-01	2020-02-01	2.00000	0.00000
1486	28	2020-02-01	2020-08-01	2.00000	0.00000
1487	28	2020-08-01	2021-02-01	2.00000	0.00000
1488	28	2021-02-01	2021-08-01	2.00000	0.00000
1489	28	2021-08-01	2022-02-01	2.00000	0.00000
1490	28	2022-02-01	2022-08-01	2.00000	0.00000
1491	28	2022-08-01	2023-02-01	2.00000	0.00000
1493	28	2023-08-01	2024-02-01	2.00000	0.00000
1494	28	2024-02-01	2024-08-01	2.00000	0.00000
1495	28	2024-08-01	2025-02-01	2.00000	0.00000
1496	28	2025-02-01	2025-08-01	2.00000	0.00000
1497	28	2025-08-01	2026-02-01	2.00000	0.00000
1498	28	2026-02-01	2026-08-01	2.00000	0.00000
1499	28	2026-08-01	2027-02-01	2.00000	0.00000
1500	28	2027-02-01	2027-08-01	2.00000	0.00000
1501	28	2027-08-01	2028-02-01	2.00000	0.00000
1502	28	2028-02-01	2028-08-01	2.00000	0.00000
1503	28	2028-08-01	2029-02-01	2.00000	0.00000
1504	28	2029-02-01	2029-08-01	2.00000	0.00000
1506	28	2030-02-01	2030-08-01	2.00000	0.00000
1507	28	2030-08-01	2031-02-01	2.00000	0.00000
1508	28	2031-02-01	2031-08-01	2.00000	0.00000
1509	28	2031-08-01	2032-02-01	2.00000	0.00000
1510	28	2032-02-01	2032-08-01	2.00000	0.00000
1511	28	2032-08-01	2033-02-01	2.00000	0.00000
1512	28	2033-02-01	2033-08-01	2.00000	0.00000
1513	28	2033-08-01	2034-02-01	2.00000	0.00000
1514	28	2034-02-01	2034-08-01	2.00000	0.00000
1515	28	2034-08-01	2035-02-01	2.00000	0.00000
1516	28	2035-02-01	2035-08-01	2.00000	0.00000
1517	28	2035-08-01	2036-02-01	2.00000	0.00000
1519	28	2036-08-01	2037-02-01	2.00000	100.00000
1520	29	2016-03-01	2016-09-01	1.12500	0.00000
1521	29	2016-09-01	2017-03-01	1.12500	0.00000
1522	29	2017-03-01	2017-09-01	1.12500	0.00000
1523	29	2017-09-01	2018-03-01	1.12500	0.00000
1524	29	2018-03-01	2018-09-01	1.12500	0.00000
1525	29	2018-09-01	2019-03-01	1.12500	0.00000
1526	29	2019-03-01	2019-09-01	1.12500	0.00000
1527	29	2019-09-01	2020-03-01	1.12500	0.00000
1528	29	2020-03-01	2020-09-01	1.12500	0.00000
1530	29	2021-03-01	2021-09-01	1.12500	0.00000
1531	29	2021-09-01	2022-03-01	1.12500	0.00000
1533	29	2022-09-01	2023-03-01	1.12500	0.00000
1534	29	2023-03-01	2023-09-01	1.12500	0.00000
1535	29	2023-09-01	2024-03-01	1.12500	0.00000
1536	29	2024-03-01	2024-09-01	1.12500	0.00000
1538	29	2025-03-01	2025-09-01	1.12500	0.00000
1539	29	2025-09-01	2026-03-01	1.12500	0.00000
1540	29	2026-03-01	2026-09-01	1.12500	0.00000
1541	29	2026-09-01	2027-03-01	1.12500	0.00000
1542	29	2027-03-01	2027-09-01	1.12500	0.00000
1543	29	2027-09-01	2028-03-01	1.12500	0.00000
1544	29	2028-03-01	2028-09-01	1.12500	0.00000
1545	29	2028-09-01	2029-03-01	1.12500	0.00000
1546	29	2029-03-01	2029-09-01	1.12500	0.00000
1547	29	2029-09-01	2030-03-01	1.12500	0.00000
1548	29	2030-03-01	2030-09-01	1.12500	0.00000
1549	29	2030-09-01	2031-03-01	1.12500	0.00000
1550	29	2031-03-01	2031-09-01	1.12500	0.00000
1551	29	2031-09-01	2032-03-01	1.12500	0.00000
1553	29	2032-09-01	2033-03-01	1.12500	0.00000
1554	29	2033-03-01	2033-09-01	1.12500	0.00000
1555	29	2033-09-01	2034-03-01	1.12500	0.00000
1556	29	2034-03-01	2034-09-01	1.12500	0.00000
1557	29	2034-09-01	2035-03-01	1.12500	0.00000
1558	29	2035-03-01	2035-09-01	1.12500	0.00000
1559	29	2035-09-01	2036-03-01	1.12500	0.00000
1560	29	2036-03-01	2036-09-01	1.12500	100.00000
1561	30	2020-02-18	2020-09-01	0.77280	0.00000
1562	30	2020-09-01	2021-03-01	0.72500	0.00000
1563	30	2021-03-01	2021-09-01	0.72500	0.00000
1564	30	2021-09-01	2022-03-01	0.72500	0.00000
1565	30	2022-03-01	2022-09-01	0.72500	0.00000
1566	30	2022-09-01	2023-03-01	0.72500	0.00000
1567	30	2023-03-01	2023-09-01	0.72500	0.00000
1568	30	2023-09-01	2024-03-01	0.72500	0.00000
1570	30	2024-09-01	2025-03-01	0.72500	0.00000
1571	30	2025-03-01	2025-09-01	0.72500	0.00000
1572	30	2025-09-01	2026-03-01	0.72500	0.00000
1573	30	2026-03-01	2026-09-01	0.72500	0.00000
1574	30	2026-09-01	2027-03-01	0.72500	0.00000
1575	30	2027-03-01	2027-09-01	0.72500	0.00000
1576	30	2027-09-01	2028-03-01	0.72500	0.00000
1577	30	2028-03-01	2028-09-01	0.72500	0.00000
1578	30	2028-09-01	2029-03-01	0.72500	0.00000
1579	30	2029-03-01	2029-09-01	0.72500	0.00000
1580	30	2029-09-01	2030-03-01	0.72500	0.00000
1581	30	2030-03-01	2030-09-01	0.72500	0.00000
1583	30	2031-03-01	2031-09-01	0.72500	0.00000
1584	30	2031-09-01	2032-03-01	0.72500	0.00000
1585	30	2032-03-01	2032-09-01	0.72500	0.00000
1586	30	2032-09-01	2033-03-01	0.72500	0.00000
1587	30	2033-03-01	2033-09-01	0.72500	0.00000
1588	30	2033-09-01	2034-03-01	0.72500	0.00000
1589	30	2034-03-01	2034-09-01	0.72500	0.00000
1590	30	2034-09-01	2035-03-01	0.72500	0.00000
1591	30	2035-03-01	2035-09-01	0.72500	0.00000
1592	30	2035-09-01	2036-03-01	0.72500	100.00000
1594	31	2026-02-01	2026-08-01	1.72500	0.00000
1595	31	2026-08-01	2027-02-01	1.72500	0.00000
1596	31	2027-02-01	2027-08-01	1.72500	0.00000
1597	31	2027-08-01	2028-02-01	1.72500	0.00000
1598	31	2028-02-01	2028-08-01	1.72500	0.00000
1599	31	2028-08-01	2029-02-01	1.72500	0.00000
1600	31	2029-02-01	2029-08-01	1.72500	0.00000
1601	31	2029-08-01	2030-02-01	1.72500	0.00000
1602	31	2030-02-01	2030-08-01	1.72500	0.00000
1603	31	2030-08-01	2031-02-01	1.72500	0.00000
1604	31	2031-02-01	2031-08-01	1.72500	0.00000
1605	31	2031-08-01	2032-02-01	1.72500	0.00000
1606	31	2032-02-01	2032-08-01	1.72500	0.00000
1607	31	2032-08-01	2033-02-01	1.72500	0.00000
1609	31	2033-08-01	2034-02-01	1.72500	0.00000
1610	31	2034-02-01	2034-08-01	1.72500	0.00000
1611	31	2034-08-01	2035-02-01	1.72500	0.00000
1612	31	2035-02-01	2035-08-01	1.72500	0.00000
1614	32	2025-05-02	2025-10-01	1.49508	0.00000
1615	32	2025-10-01	2026-04-01	1.80000	0.00000
1616	32	2026-04-01	2026-10-01	1.80000	0.00000
1617	32	2026-10-01	2027-04-01	1.80000	0.00000
1618	32	2027-04-01	2027-10-01	1.80000	0.00000
1619	32	2027-10-01	2028-04-01	1.80000	0.00000
1620	32	2028-04-01	2028-10-01	1.80000	0.00000
1621	32	2028-10-01	2029-04-01	1.80000	0.00000
1622	32	2029-04-01	2029-10-01	1.80000	0.00000
1623	32	2029-10-01	2030-04-01	1.80000	0.00000
1625	32	2030-10-01	2031-04-01	1.80000	0.00000
1626	32	2031-04-01	2031-10-01	1.80000	0.00000
1627	32	2031-10-01	2032-04-01	1.80000	0.00000
1628	32	2032-04-01	2032-10-01	1.80000	0.00000
1629	32	2032-10-01	2033-04-01	1.80000	0.00000
1630	32	2033-04-01	2033-10-01	1.80000	0.00000
1631	32	2033-10-01	2034-04-01	1.80000	0.00000
1632	32	2034-04-01	2034-10-01	1.80000	0.00000
1633	32	2034-10-01	2035-04-01	1.80000	0.00000
1634	32	2035-04-01	2035-10-01	1.80000	100.00000
1635	33	2025-01-15	2025-02-01	0.16861	0.00000
1636	33	2025-02-01	2025-08-01	1.82500	0.00000
1637	33	2025-08-01	2026-02-01	1.82500	0.00000
1638	33	2026-02-01	2026-08-01	1.82500	0.00000
1640	33	2027-02-01	2027-08-01	1.82500	0.00000
1641	33	2027-08-01	2028-02-01	1.82500	0.00000
1642	33	2028-02-01	2028-08-01	1.82500	0.00000
1643	33	2028-08-01	2029-02-01	1.82500	0.00000
1644	33	2029-02-01	2029-08-01	1.82500	0.00000
1645	33	2029-08-01	2030-02-01	1.82500	0.00000
1646	33	2030-02-01	2030-08-01	1.82500	0.00000
1647	33	2030-08-01	2031-02-01	1.82500	0.00000
1648	33	2031-02-01	2031-08-01	1.82500	0.00000
1649	33	2031-08-01	2032-02-01	1.82500	0.00000
1650	33	2032-02-01	2032-08-01	1.82500	0.00000
1651	33	2032-08-01	2033-02-01	1.82500	0.00000
1652	33	2033-02-01	2033-08-01	1.82500	0.00000
1653	33	2033-08-01	2034-02-01	1.82500	0.00000
1655	33	2034-08-01	2035-02-01	1.82500	0.00000
1656	33	2035-02-01	2035-08-01	1.82500	100.00000
1657	34	2024-08-01	2025-02-01	1.92500	0.00000
1658	34	2025-02-01	2025-08-01	1.92500	0.00000
1659	34	2025-08-01	2026-02-01	1.92500	0.00000
1660	34	2026-02-01	2026-08-01	1.92500	0.00000
1661	34	2026-08-01	2027-02-01	1.92500	0.00000
1662	34	2027-02-01	2027-08-01	1.92500	0.00000
1663	34	2027-08-01	2028-02-01	1.92500	0.00000
1664	34	2028-02-01	2028-08-01	1.92500	0.00000
1665	34	2028-08-01	2029-02-01	1.92500	0.00000
1666	34	2029-02-01	2029-08-01	1.92500	0.00000
1667	34	2029-08-01	2030-02-01	1.92500	0.00000
1668	34	2030-02-01	2030-08-01	1.92500	0.00000
1670	34	2031-02-01	2031-08-01	1.92500	0.00000
1671	34	2031-08-01	2032-02-01	1.92500	0.00000
1672	34	2032-02-01	2032-08-01	1.92500	0.00000
1674	34	2033-02-01	2033-08-01	1.92500	0.00000
1675	34	2033-08-01	2034-02-01	1.92500	0.00000
1676	34	2034-02-01	2034-08-01	1.92500	0.00000
1677	34	2034-08-01	2035-02-01	1.92500	100.00000
1678	35	2022-09-13	2022-10-30	0.51366	0.00000
1679	35	2022-10-30	2023-04-30	2.00000	0.00000
1680	35	2023-04-30	2023-10-30	2.00000	0.00000
1681	35	2023-10-30	2024-04-30	2.00000	0.00000
1683	35	2024-10-30	2025-04-30	2.00000	0.00000
1684	35	2025-04-30	2025-10-30	2.00000	0.00000
1685	35	2025-10-30	2026-04-30	2.00000	0.00000
1686	35	2026-04-30	2026-10-30	2.00000	0.00000
1687	35	2026-10-30	2027-04-30	2.00000	0.00000
1688	35	2027-04-30	2027-10-30	2.00000	0.00000
1689	35	2027-10-30	2028-04-30	2.00000	0.00000
1690	35	2028-04-30	2028-10-30	2.00000	0.00000
1691	35	2028-10-30	2029-04-30	2.00000	0.00000
1692	35	2029-04-30	2029-10-30	2.00000	0.00000
1693	35	2029-10-30	2030-04-30	2.00000	0.00000
1694	35	2030-04-30	2030-10-30	2.00000	0.00000
1696	35	2031-04-30	2031-10-30	2.00000	0.00000
1697	35	2031-10-30	2032-04-30	2.00000	0.00000
1698	35	2032-04-30	2032-10-30	2.00000	0.00000
1699	35	2032-10-30	2033-04-30	2.00000	0.00000
1700	35	2033-04-30	2033-10-30	2.00000	0.00000
1701	35	2033-10-30	2034-04-30	2.00000	0.00000
1702	35	2034-04-30	2034-10-30	2.00000	0.00000
1703	35	2034-10-30	2035-04-30	2.00000	100.00000
1704	36	2019-01-22	2019-03-01	0.35166	0.00000
1705	36	2019-03-01	2019-09-01	1.67500	0.00000
1707	36	2020-03-01	2020-09-01	1.67500	0.00000
1708	36	2020-09-01	2021-03-01	1.67500	0.00000
1709	36	2021-03-01	2021-09-01	1.67500	0.00000
1710	36	2021-09-01	2022-03-01	1.67500	0.00000
1711	36	2022-03-01	2022-09-01	1.67500	0.00000
1712	36	2022-09-01	2023-03-01	1.67500	0.00000
1713	36	2023-03-01	2023-09-01	1.67500	0.00000
1714	36	2023-09-01	2024-03-01	1.67500	0.00000
1715	36	2024-03-01	2024-09-01	1.67500	0.00000
1716	36	2024-09-01	2025-03-01	1.67500	0.00000
1717	36	2025-03-01	2025-09-01	1.67500	0.00000
1718	36	2025-09-01	2026-03-01	1.67500	0.00000
1719	36	2026-03-01	2026-09-01	1.67500	0.00000
1720	36	2026-09-01	2027-03-01	1.67500	0.00000
1722	36	2027-09-01	2028-03-01	1.67500	0.00000
1723	36	2028-03-01	2028-09-01	1.67500	0.00000
1724	36	2028-09-01	2029-03-01	1.67500	0.00000
1725	36	2029-03-01	2029-09-01	1.67500	0.00000
1726	36	2029-09-01	2030-03-01	1.67500	0.00000
1727	36	2030-03-01	2030-09-01	1.67500	0.00000
1728	36	2030-09-01	2031-03-01	1.67500	0.00000
1729	36	2031-03-01	2031-09-01	1.67500	0.00000
1730	36	2031-09-01	2032-03-01	1.67500	0.00000
1731	36	2032-03-01	2032-09-01	1.67500	0.00000
1732	36	2032-09-01	2033-03-01	1.67500	0.00000
1733	36	2033-03-01	2033-09-01	1.67500	0.00000
1734	36	2033-09-01	2034-03-01	1.67500	0.00000
1735	36	2034-03-01	2034-09-01	1.67500	0.00000
1737	37	2021-11-16	2022-05-16	0.37500	0.00000
1738	37	2022-05-16	2022-11-16	0.37500	0.00000
1739	37	2022-11-16	2023-05-16	0.37500	0.00000
1740	37	2023-05-16	2023-11-16	0.37500	0.00000
1741	37	2023-11-16	2024-05-16	0.37500	0.00000
1742	37	2024-05-16	2024-11-16	0.37500	0.00000
1743	37	2024-11-16	2025-05-16	0.37500	0.00000
1744	37	2025-05-16	2025-11-16	0.37500	0.00000
1745	37	2025-11-16	2026-05-16	0.67500	0.00000
1746	37	2026-05-16	2026-11-16	0.67500	0.00000
1747	37	2026-11-16	2027-05-16	0.67500	0.00000
1748	37	2027-05-16	2027-11-16	0.67500	0.00000
1749	37	2027-11-16	2028-05-16	0.67500	0.00000
1750	37	2028-05-16	2028-11-16	0.67500	0.00000
1751	37	2028-11-16	2029-05-16	0.67500	0.00000
1752	37	2029-05-16	2029-11-16	0.67500	0.00000
1754	37	2030-05-16	2030-11-16	0.85000	0.00000
1755	37	2030-11-16	2031-05-16	0.85000	0.00000
1756	37	2031-05-16	2031-11-16	0.85000	0.00000
1757	37	2031-11-16	2032-05-16	0.85000	0.00000
1758	37	2032-05-16	2032-11-16	0.85000	0.00000
1759	37	2032-11-16	2033-05-16	0.85000	0.00000
1761	38	2024-03-01	2024-07-01	1.29038	0.00000
1762	38	2024-07-01	2025-01-01	1.92500	0.00000
1763	38	2025-01-01	2025-07-01	1.92500	0.00000
1764	38	2025-07-01	2026-01-01	1.92500	0.00000
1766	38	2026-07-01	2027-01-01	1.92500	0.00000
1767	38	2027-01-01	2027-07-01	1.92500	0.00000
1768	38	2027-07-01	2028-01-01	1.92500	0.00000
1769	38	2028-01-01	2028-07-01	1.92500	0.00000
1770	38	2028-07-01	2029-01-01	1.92500	0.00000
1771	38	2029-01-01	2029-07-01	1.92500	0.00000
1772	38	2029-07-01	2030-01-01	1.92500	0.00000
1773	38	2030-01-01	2030-07-01	1.92500	0.00000
1774	38	2030-07-01	2031-01-01	1.92500	0.00000
1775	38	2031-01-01	2031-07-01	1.92500	0.00000
1776	38	2031-07-01	2032-01-01	1.92500	0.00000
1777	38	2032-01-01	2032-07-01	1.92500	0.00000
1778	38	2032-07-01	2033-01-01	1.92500	0.00000
1779	38	2033-01-01	2033-07-01	1.92500	0.00000
1781	38	2034-01-01	2034-07-01	1.92500	100.00000
1782	39	2003-08-01	2004-02-01	2.50000	0.00000
1783	39	2004-02-01	2004-08-01	2.50000	0.00000
1784	39	2004-08-01	2005-02-01	2.50000	0.00000
1785	39	2005-02-01	2005-08-01	2.50000	0.00000
1786	39	2005-08-01	2006-02-01	2.50000	0.00000
1787	39	2006-02-01	2006-08-01	2.50000	0.00000
1788	39	2006-08-01	2007-02-01	2.50000	0.00000
1789	39	2007-02-01	2007-08-01	2.50000	0.00000
1790	39	2007-08-01	2008-02-01	2.50000	0.00000
1791	39	2008-02-01	2008-08-01	2.50000	0.00000
1792	39	2008-08-01	2009-02-01	2.50000	0.00000
1793	39	2009-02-01	2009-08-01	2.50000	0.00000
1794	39	2009-08-01	2010-02-01	2.50000	0.00000
1796	39	2010-08-01	2011-02-01	2.50000	0.00000
1797	39	2011-02-01	2011-08-01	2.50000	0.00000
1798	39	2011-08-01	2012-02-01	2.50000	0.00000
1799	39	2012-02-01	2012-08-01	2.50000	0.00000
1800	39	2012-08-01	2013-02-01	2.50000	0.00000
1801	39	2013-02-01	2013-08-01	2.50000	0.00000
1802	39	2013-08-01	2014-02-01	2.50000	0.00000
1803	39	2014-02-01	2014-08-01	2.50000	0.00000
1804	39	2014-08-01	2015-02-01	2.50000	0.00000
1805	39	2015-02-01	2015-08-01	2.50000	0.00000
1806	39	2015-08-01	2016-02-01	2.50000	0.00000
1807	39	2016-02-01	2016-08-01	2.50000	0.00000
1808	39	2016-08-01	2017-02-01	2.50000	0.00000
1809	39	2017-02-01	2017-08-01	2.50000	0.00000
1811	39	2018-02-01	2018-08-01	2.50000	0.00000
1812	39	2018-08-01	2019-02-01	2.50000	0.00000
1813	39	2019-02-01	2019-08-01	2.50000	0.00000
1814	39	2019-08-01	2020-02-01	2.50000	0.00000
1815	39	2020-02-01	2020-08-01	2.50000	0.00000
1952	44	2025-03-14	2026-03-13	0.00000	100.00000
1613	31	2035-08-01	2036-02-01	1.72500	0.00000
1760	37	2033-05-16	2033-11-16	0.85000	0.00000
1816	39	2020-08-01	2021-02-01	2.50000	0.00000
1817	39	2021-02-01	2021-08-01	2.50000	0.00000
1818	39	2021-08-01	2022-02-01	2.50000	0.00000
1819	39	2022-02-01	2022-08-01	2.50000	0.00000
1820	39	2022-08-01	2023-02-01	2.50000	0.00000
1821	39	2023-02-01	2023-08-01	2.50000	0.00000
1823	39	2024-02-01	2024-08-01	2.50000	0.00000
1824	39	2024-08-01	2025-02-01	2.50000	0.00000
1825	39	2025-02-01	2025-08-01	2.50000	0.00000
1826	39	2025-08-01	2026-02-01	2.50000	0.00000
1827	39	2026-02-01	2026-08-01	2.50000	0.00000
1828	39	2026-08-01	2027-02-01	2.50000	0.00000
1829	39	2027-02-01	2027-08-01	2.50000	0.00000
1830	39	2027-08-01	2028-02-01	2.50000	0.00000
1831	39	2028-02-01	2028-08-01	2.50000	0.00000
1832	39	2028-08-01	2029-02-01	2.50000	0.00000
1833	39	2029-02-01	2029-08-01	2.50000	0.00000
1834	39	2029-08-01	2030-02-01	2.50000	0.00000
1835	39	2030-02-01	2030-08-01	2.50000	0.00000
1836	39	2030-08-01	2031-02-01	2.50000	0.00000
1838	39	2031-08-01	2032-02-01	2.50000	0.00000
1839	39	2032-02-01	2032-08-01	2.50000	0.00000
1840	39	2032-08-01	2033-02-01	2.50000	0.00000
1841	39	2033-02-01	2033-08-01	2.50000	0.00000
1842	39	2033-08-01	2034-02-01	2.50000	0.00000
1843	39	2034-02-01	2034-08-01	2.50000	100.00000
1844	40	2025-02-25	2025-05-25	0.71250	0.00000
1845	40	2025-05-25	2025-08-25	0.71250	0.00000
1846	40	2025-08-25	2025-11-25	0.71250	0.00000
1847	40	2025-11-25	2026-02-25	0.71250	0.00000
1848	40	2026-02-25	2026-05-25	0.71250	0.00000
1849	40	2026-05-25	2026-08-25	0.71250	0.00000
1850	40	2026-08-25	2026-11-25	0.71250	0.00000
1851	40	2026-11-25	2027-02-25	0.71250	0.00000
1852	40	2027-02-25	2027-05-25	0.71250	0.00000
1853	40	2027-05-25	2027-08-25	0.71250	0.00000
1855	40	2027-11-25	2028-02-25	0.71250	0.00000
1856	40	2028-02-25	2028-05-25	0.71250	0.00000
1857	40	2028-05-25	2028-08-25	0.71250	0.00000
1858	40	2028-08-25	2028-11-25	0.71250	0.00000
1859	40	2028-11-25	2029-02-25	0.71250	0.00000
1860	40	2029-02-25	2029-05-25	0.92500	0.00000
1861	40	2029-05-25	2029-08-25	0.92500	0.00000
1862	40	2029-08-25	2029-11-25	0.92500	0.00000
1863	40	2029-11-25	2030-02-25	0.92500	0.00000
1864	40	2030-02-25	2030-05-25	0.92500	0.00000
1865	40	2030-05-25	2030-08-25	0.92500	0.00000
1866	40	2030-08-25	2030-11-25	0.92500	0.00000
1868	40	2031-02-25	2031-05-25	0.92500	0.00000
1869	40	2031-05-25	2031-08-25	0.92500	0.00000
1870	40	2031-08-25	2031-11-25	0.92500	0.00000
1871	40	2031-11-25	2032-02-25	0.92500	0.00000
1872	40	2032-02-25	2032-05-25	0.92500	0.00000
1873	40	2032-05-25	2032-08-25	0.92500	0.00000
1874	40	2032-08-25	2032-11-25	0.92500	0.00000
1875	40	2032-11-25	2033-02-25	0.92500	0.00000
1876	41	2023-09-01	2024-03-01	2.10000	0.00000
1877	41	2024-03-01	2024-09-01	2.10000	0.00000
1878	41	2024-09-01	2025-03-01	2.10000	0.00000
1879	41	2025-03-01	2025-09-01	2.10000	0.00000
1881	41	2026-03-01	2026-09-01	2.10000	0.00000
1882	41	2026-09-01	2027-03-01	2.10000	0.00000
1883	41	2027-03-01	2027-09-01	2.10000	0.00000
1884	41	2027-09-01	2028-03-01	2.10000	0.00000
1885	41	2028-03-01	2028-09-01	2.10000	0.00000
1886	41	2028-09-01	2029-03-01	2.10000	0.00000
1887	41	2029-03-01	2029-09-01	2.10000	0.00000
1888	41	2029-09-01	2030-03-01	2.10000	0.00000
1889	41	2030-03-01	2030-09-01	2.10000	0.00000
1890	41	2030-09-01	2031-03-01	2.10000	0.00000
1891	41	2031-03-01	2031-09-01	2.10000	0.00000
1892	41	2031-09-01	2032-03-01	2.10000	0.00000
1893	41	2032-03-01	2032-09-01	2.10000	0.00000
1894	41	2032-09-01	2033-03-01	2.10000	0.00000
1896	41	2033-09-01	2034-03-01	2.10000	100.00000
1897	42	2023-05-02	2023-11-01	2.16318	0.00000
1898	42	2023-11-01	2024-05-01	2.17500	0.00000
1899	42	2024-05-01	2024-11-01	2.17500	0.00000
1900	42	2024-11-01	2025-05-01	2.17500	0.00000
1901	42	2025-05-01	2025-11-01	2.17500	0.00000
1902	42	2025-11-01	2026-05-01	2.17500	0.00000
1903	42	2026-05-01	2026-11-01	2.17500	0.00000
1904	42	2026-11-01	2027-05-01	2.17500	0.00000
1905	42	2027-05-01	2027-11-01	2.17500	0.00000
1906	42	2027-11-01	2028-05-01	2.17500	0.00000
1907	42	2028-05-01	2028-11-01	2.17500	0.00000
1908	42	2028-11-01	2029-05-01	2.17500	0.00000
1909	42	2029-05-01	2029-11-01	2.17500	0.00000
1911	42	2030-05-01	2030-11-01	2.17500	0.00000
1912	42	2030-11-01	2031-05-01	2.17500	0.00000
1913	42	2031-05-01	2031-11-01	2.17500	0.00000
1914	42	2031-11-01	2032-05-01	2.17500	0.00000
1915	42	2032-05-01	2032-11-01	2.17500	0.00000
1916	42	2032-11-01	2033-05-01	2.17500	0.00000
1917	42	2033-05-01	2033-11-01	2.17500	100.00000
1918	43	2017-01-25	2017-03-01	0.23688	0.00000
1919	43	2017-03-01	2017-09-01	1.22500	0.00000
1920	43	2017-09-01	2018-03-01	1.22500	0.00000
1921	43	2018-03-01	2018-09-01	1.22500	0.00000
1922	43	2018-09-01	2019-03-01	1.22500	0.00000
1923	43	2019-03-01	2019-09-01	1.22500	0.00000
1924	43	2019-09-01	2020-03-01	1.22500	0.00000
1926	43	2020-09-01	2021-03-01	1.22500	0.00000
1927	43	2021-03-01	2021-09-01	1.22500	0.00000
1928	43	2021-09-01	2022-03-01	1.22500	0.00000
1929	43	2022-03-01	2022-09-01	1.22500	0.00000
1930	43	2022-09-01	2023-03-01	1.22500	0.00000
1931	43	2023-03-01	2023-09-01	1.22500	0.00000
1932	43	2023-09-01	2024-03-01	1.22500	0.00000
1933	43	2024-03-01	2024-09-01	1.22500	0.00000
1934	43	2024-09-01	2025-03-01	1.22500	0.00000
1935	43	2025-03-01	2025-09-01	1.22500	0.00000
1936	43	2025-09-01	2026-03-01	1.22500	0.00000
1937	43	2026-03-01	2026-09-01	1.22500	0.00000
1938	43	2026-09-01	2027-03-01	1.22500	0.00000
1939	43	2027-03-01	2027-09-01	1.22500	0.00000
1941	43	2028-03-01	2028-09-01	1.22500	0.00000
1942	43	2028-09-01	2029-03-01	1.22500	0.00000
1943	43	2029-03-01	2029-09-01	1.22500	0.00000
1944	43	2029-09-01	2030-03-01	1.22500	0.00000
1945	43	2030-03-01	2030-09-01	1.22500	0.00000
1946	43	2030-09-01	2031-03-01	1.22500	0.00000
1947	43	2031-03-01	2031-09-01	1.22500	0.00000
1948	43	2031-09-01	2032-03-01	1.22500	0.00000
1949	43	2032-03-01	2032-09-01	1.22500	0.00000
1950	43	2032-09-01	2033-03-01	1.22500	0.00000
1951	43	2033-03-01	2033-09-01	1.22500	100.00000
1953	45	2026-01-14	2027-01-14	0.00000	100.00000
1954	46	2025-09-30	2026-03-31	0.00000	100.00000
1955	47	2026-02-13	2027-02-12	0.00000	100.00000
1956	48	2025-12-12	2025-12-14	0.00000	0.00000
1957	48	2025-12-14	2026-12-14	0.00000	100.00000
1958	49	2025-11-14	2026-11-13	0.00000	100.00000
1959	50	2026-01-30	2026-07-31	0.00000	100.00000
1960	51	2025-10-14	2026-10-14	0.00000	100.00000
1961	52	2025-09-12	2025-09-14	0.00000	0.00000
1962	52	2025-09-14	2026-09-14	0.00000	100.00000
1963	53	2025-11-28	2026-05-29	0.00000	100.00000
1964	54	2025-08-14	2026-08-14	0.00000	100.00000
1965	55	2025-05-14	2026-05-14	0.00000	100.00000
1966	56	2025-06-13	2026-06-12	0.00000	100.00000
1967	57	2025-07-14	2026-07-14	0.00000	100.00000
1968	58	2025-04-14	2026-04-14	0.00000	100.00000
1	1	2025-09-09	2025-10-01	0.27951	0.00000
4	1	2026-10-01	2027-04-01	2.32500	0.00000
19	1	2034-04-01	2034-10-01	2.32500	0.00000
34	1	2041-10-01	2042-04-01	2.32500	0.00000
49	1	2049-04-01	2049-10-01	2.32500	0.00000
64	2	2025-04-01	2025-10-01	2.15000	0.00000
79	2	2032-10-01	2033-04-01	2.15000	0.00000
94	2	2040-04-01	2040-10-01	2.15000	0.00000
109	2	2047-10-01	2048-04-01	2.15000	0.00000
124	3	2023-04-01	2023-10-01	2.25000	0.00000
137	3	2029-10-01	2030-04-01	2.25000	0.00000
140	3	2031-04-01	2031-10-01	2.25000	0.00000
155	3	2038-10-01	2039-04-01	2.25000	0.00000
170	3	2046-04-01	2046-10-01	2.25000	0.00000
185	4	2016-09-01	2017-03-01	1.40000	0.00000
200	4	2024-03-01	2024-09-01	1.40000	0.00000
215	4	2031-09-01	2032-03-01	1.40000	0.00000
230	4	2039-03-01	2039-09-01	1.40000	0.00000
245	4	2046-09-01	2047-03-01	1.40000	0.00000
260	4	2054-03-01	2054-09-01	1.40000	0.00000
273	4	2060-09-01	2061-03-01	1.40000	0.00000
278	4	2063-03-01	2063-09-01	1.40000	0.00000
293	5	2025-03-01	2025-09-01	1.07500	0.00000
308	5	2032-09-01	2033-03-01	1.07500	0.00000
323	5	2040-03-01	2040-09-01	1.07500	0.00000
338	5	2047-09-01	2048-03-01	1.07500	0.00000
355	6	2024-03-01	2024-09-01	0.85000	0.00000
368	6	2030-09-01	2031-03-01	0.85000	0.00000
381	6	2037-03-01	2037-09-01	0.85000	0.00000
394	6	2043-09-01	2044-03-01	0.85000	0.00000
407	6	2050-03-01	2050-09-01	0.85000	0.00000
417	7	2025-03-01	2025-09-01	1.22500	0.00000
420	7	2026-09-01	2027-03-01	1.22500	0.00000
435	7	2034-03-01	2034-09-01	1.22500	0.00000
450	7	2041-09-01	2042-03-01	1.22500	0.00000
465	7	2049-03-01	2049-09-01	1.22500	0.00000
480	8	2024-09-01	2025-03-01	1.92500	0.00000
495	8	2032-03-01	2032-09-01	1.92500	0.00000
510	8	2039-09-01	2040-03-01	1.92500	0.00000
525	8	2047-03-01	2047-09-01	1.92500	0.00000
540	9	2022-03-01	2022-09-01	1.72500	0.00000
553	9	2028-09-01	2029-03-01	1.72500	0.00000
556	9	2030-03-01	2030-09-01	1.72500	0.00000
571	9	2037-09-01	2038-03-01	1.72500	0.00000
586	9	2045-03-01	2045-09-01	1.72500	0.00000
601	10	2020-03-01	2020-09-01	1.35000	0.00000
616	10	2027-09-01	2028-03-01	1.35000	0.00000
631	10	2035-03-01	2035-09-01	1.35000	0.00000
646	10	2042-09-01	2043-03-01	1.35000	0.00000
661	11	2024-03-01	2024-09-01	1.07500	0.00000
676	11	2031-09-01	2032-03-01	1.07500	0.00000
689	11	2038-03-01	2038-09-01	1.07500	0.00000
692	11	2039-09-01	2040-03-01	1.07500	0.00000
707	11	2047-03-01	2047-09-01	1.07500	0.00000
722	11	2054-09-01	2055-03-01	1.07500	0.00000
737	11	2062-03-01	2062-09-01	1.07500	0.00000
752	11	2069-09-01	2070-03-01	1.07500	0.00000
767	12	2019-09-01	2020-03-01	1.62500	0.00000
782	12	2027-03-01	2027-09-01	1.62500	0.00000
797	12	2034-09-01	2035-03-01	1.62500	0.00000
812	12	2042-03-01	2042-09-01	1.62500	0.00000
825	13	2026-10-30	2027-04-30	2.05000	0.00000
834	13	2031-04-30	2031-10-30	2.05000	0.00000
849	13	2038-10-30	2039-04-30	2.05000	0.00000
866	14	2021-10-30	2022-04-30	0.75000	0.00000
879	14	2028-04-30	2028-10-30	0.75000	0.00000
892	14	2034-10-30	2035-04-30	0.75000	0.00000
905	14	2041-04-30	2041-10-30	0.75000	0.00000
916	15	2014-09-01	2015-03-01	2.37500	0.00000
931	15	2022-03-01	2022-09-01	2.37500	0.00000
946	15	2029-09-01	2030-03-01	2.37500	0.00000
961	15	2037-03-01	2037-09-01	2.37500	0.00000
967	15	2040-03-01	2040-09-01	2.37500	0.00000
973	15	2043-03-01	2043-09-01	2.37500	0.00000
988	16	2028-09-01	2029-03-01	2.22500	0.00000
1003	16	2036-03-01	2036-09-01	2.22500	0.00000
1018	17	2020-09-11	2021-03-01	0.85028	0.00000
1035	17	2029-03-01	2029-09-01	0.90000	0.00000
1048	17	2035-09-01	2036-03-01	0.90000	0.00000
1059	18	2025-02-18	2025-04-01	0.44423	0.00000
1074	18	2032-04-01	2032-10-01	1.92500	0.00000
1089	18	2039-10-01	2040-04-01	1.92500	0.00000
1104	19	2016-03-01	2016-09-01	2.50000	0.00000
1108	19	2018-03-01	2018-09-01	2.50000	0.00000
1119	19	2023-09-01	2024-03-01	2.50000	0.00000
1134	19	2031-03-01	2031-09-01	2.50000	0.00000
1149	19	2038-09-01	2039-03-01	2.50000	0.00000
1164	20	2024-09-01	2025-03-01	1.55000	0.00000
1179	20	2032-03-01	2032-09-01	1.55000	0.00000
1194	20	2039-09-01	2040-03-01	1.55000	100.00000
1209	21	2030-10-01	2031-04-01	2.07500	0.00000
1224	21	2038-04-01	2038-10-01	2.07500	0.00000
1241	22	2028-04-27	2028-10-27	0.60000	0.00000
1246	22	2030-10-27	2031-04-27	0.82500	0.00000
1258	22	2036-10-27	2037-04-27	1.00000	0.00000
1259	23	2007-08-01	2008-02-01	2.50000	0.00000
1272	23	2014-02-01	2014-08-01	2.50000	0.00000
1287	23	2021-08-01	2022-02-01	2.50000	0.00000
1302	23	2029-02-01	2029-08-01	2.50000	0.00000
1317	23	2036-08-01	2037-02-01	2.50000	0.00000
1332	24	2022-03-01	2022-09-01	1.47500	0.00000
1347	24	2029-09-01	2030-03-01	1.47500	0.00000
1362	24	2037-03-01	2037-09-01	1.47500	0.00000
1377	25	2028-03-01	2028-09-01	1.62500	0.00000
1384	25	2031-09-01	2032-03-01	1.62500	0.00000
1393	25	2036-03-01	2036-09-01	1.62500	0.00000
1408	26	2029-10-30	2030-04-30	2.02500	0.00000
1423	26	2037-04-30	2037-10-30	2.02500	100.00000
1440	27	2028-09-01	2029-03-01	0.47500	0.00000
1453	27	2035-03-01	2035-09-01	0.47500	0.00000
1466	28	2010-02-01	2010-08-01	2.00000	0.00000
1479	28	2016-08-01	2017-02-01	2.00000	0.00000
1492	28	2023-02-01	2023-08-01	2.00000	0.00000
1505	28	2029-08-01	2030-02-01	2.00000	0.00000
1518	28	2036-02-01	2036-08-01	2.00000	0.00000
1529	29	2020-09-01	2021-03-01	1.12500	0.00000
1532	29	2022-03-01	2022-09-01	1.12500	0.00000
1537	29	2024-09-01	2025-03-01	1.12500	0.00000
1552	29	2032-03-01	2032-09-01	1.12500	0.00000
1569	30	2024-03-01	2024-09-01	0.72500	0.00000
1582	30	2030-09-01	2031-03-01	0.72500	0.00000
1593	31	2025-11-03	2026-02-01	0.84375	0.00000
1608	31	2033-02-01	2033-08-01	1.72500	0.00000
1624	32	2030-04-01	2030-10-01	1.80000	0.00000
1639	33	2026-08-01	2027-02-01	1.82500	0.00000
1654	33	2034-02-01	2034-08-01	1.82500	0.00000
1669	34	2030-08-01	2031-02-01	1.92500	0.00000
1673	34	2032-08-01	2033-02-01	1.92500	0.00000
1682	35	2024-04-30	2024-10-30	2.00000	0.00000
1695	35	2030-10-30	2031-04-30	2.00000	0.00000
1706	36	2019-09-01	2020-03-01	1.67500	0.00000
1721	36	2027-03-01	2027-09-01	1.67500	0.00000
1736	36	2034-09-01	2035-03-01	1.67500	100.00000
1753	37	2029-11-16	2030-05-16	0.85000	0.00000
1765	38	2026-01-01	2026-07-01	1.92500	0.00000
1780	38	2033-07-01	2034-01-01	1.92500	0.00000
1795	39	2010-02-01	2010-08-01	2.50000	0.00000
1810	39	2017-08-01	2018-02-01	2.50000	0.00000
1822	39	2023-08-01	2024-02-01	2.50000	0.00000
1837	39	2031-02-01	2031-08-01	2.50000	0.00000
1854	40	2027-08-25	2027-11-25	0.71250	0.00000
1867	40	2030-11-25	2031-02-25	0.92500	0.00000
1880	41	2025-09-01	2026-03-01	2.10000	0.00000
1895	41	2033-03-01	2033-09-01	2.10000	0.00000
1910	42	2029-11-01	2030-05-01	2.17500	0.00000
1925	43	2020-03-01	2020-09-01	1.22500	0.00000
1940	43	2027-09-01	2028-03-01	1.22500	0.00000
1971	59	1997-11-01	1998-05-01	3.62500	0.00000
1972	59	1998-05-01	1998-11-01	3.62500	0.00000
1973	59	1998-11-01	1999-05-01	3.62500	0.00000
1974	59	1999-05-01	1999-11-01	3.62500	0.00000
1976	59	2000-05-01	2000-11-01	3.62500	0.00000
1977	59	2000-11-01	2001-05-01	3.62500	0.00000
1978	59	2001-05-01	2001-11-01	3.62500	0.00000
1979	59	2001-11-01	2002-05-01	3.62500	0.00000
1980	59	2002-05-01	2002-11-01	3.62500	0.00000
1981	59	2002-11-01	2003-05-01	3.62500	0.00000
1982	59	2003-05-01	2003-11-01	3.62500	0.00000
1983	59	2003-11-01	2004-05-01	3.62500	0.00000
1984	59	2004-05-01	2004-11-01	3.62500	0.00000
1985	59	2004-11-01	2005-05-01	3.62500	0.00000
1986	59	2005-05-01	2005-11-01	3.62500	0.00000
1987	59	2005-11-01	2006-05-01	3.62500	0.00000
1988	59	2006-05-01	2006-11-01	3.62500	0.00000
1989	59	2006-11-01	2007-05-01	3.62500	0.00000
1991	59	2007-11-01	2008-05-01	3.62500	0.00000
1992	59	2008-05-01	2008-11-01	3.62500	0.00000
1993	59	2008-11-01	2009-05-01	3.62500	0.00000
1994	59	2009-05-01	2009-11-01	3.62500	0.00000
1995	59	2009-11-01	2010-05-01	3.62500	0.00000
1996	59	2010-05-01	2010-11-01	3.62500	0.00000
1997	59	2010-11-01	2011-05-01	3.62500	0.00000
1998	59	2011-05-01	2011-11-01	3.62500	0.00000
1999	59	2011-11-01	2012-05-01	3.62500	0.00000
2000	59	2012-05-01	2012-11-01	3.62500	0.00000
2001	59	2012-11-01	2013-05-01	3.62500	0.00000
2002	59	2013-05-01	2013-11-01	3.62500	0.00000
2003	59	2013-11-01	2014-05-01	3.62500	0.00000
2004	59	2014-05-01	2014-11-01	3.62500	0.00000
2006	59	2015-05-01	2015-11-01	3.62500	0.00000
2007	59	2015-11-01	2016-05-01	3.62500	0.00000
2008	59	2016-05-01	2016-11-01	3.62500	0.00000
2009	59	2016-11-01	2017-05-01	3.62500	0.00000
2010	59	2017-05-01	2017-11-01	3.62500	0.00000
2011	59	2017-11-01	2018-05-01	3.62500	0.00000
2012	59	2018-05-01	2018-11-01	3.62500	0.00000
2013	59	2018-11-01	2019-05-01	3.62500	0.00000
2014	59	2019-05-01	2019-11-01	3.62500	0.00000
2015	59	2019-11-01	2020-05-01	3.62500	0.00000
2016	59	2020-05-01	2020-11-01	3.62500	0.00000
2017	59	2020-11-01	2021-05-01	3.62500	0.00000
2018	59	2021-05-01	2021-11-01	3.62500	0.00000
2019	59	2021-11-01	2022-05-01	3.62500	0.00000
2021	59	2022-11-01	2023-05-01	3.62500	0.00000
2022	59	2023-05-01	2023-11-01	3.62500	0.00000
2023	59	2023-11-01	2024-05-01	3.62500	0.00000
2024	59	2024-05-01	2024-11-01	3.62500	0.00000
2025	59	2024-11-01	2025-05-01	3.62500	0.00000
2026	59	2025-05-01	2025-11-01	3.62500	0.00000
2027	59	2025-11-01	2026-05-01	3.62500	0.00000
2028	59	2026-05-01	2026-11-01	3.62500	100.00000
2029	60	2026-01-15	2026-03-15	0.39116	0.00000
2030	60	2026-03-15	2026-09-15	1.20000	0.00000
2031	60	2026-09-15	2027-03-15	1.20000	0.00000
2032	60	2027-03-15	2027-09-15	1.20000	0.00000
2033	60	2027-09-15	2028-03-15	1.20000	0.00000
2034	60	2028-03-15	2028-09-15	1.20000	0.00000
2036	61	2024-12-16	2025-12-16	4.35000	0.00000
2037	61	2025-12-16	2026-06-16	2.17500	0.00000
2038	61	2026-06-16	2026-12-16	2.17500	0.00000
2039	61	2026-12-16	2027-06-16	2.17500	0.00000
2040	61	2027-06-16	2027-12-16	2.17500	0.00000
2041	61	2027-12-16	2028-06-16	2.17500	0.00000
2042	61	2028-06-16	2028-12-16	2.17500	0.00000
2043	61	2028-12-16	2029-06-16	2.17500	0.00000
2044	61	2029-06-16	2029-12-16	2.17500	0.00000
2045	61	2029-12-16	2030-06-16	2.17500	0.00000
2046	61	2030-06-16	2030-12-16	2.17500	100.00000
2047	62	1998-02-17	1998-08-17	0.00000	0.00000
2048	62	1998-08-17	1999-02-17	0.00000	0.00000
2049	62	1999-02-17	1999-08-17	0.00000	0.00000
2050	62	1999-08-17	2000-02-17	0.00000	0.00000
2051	62	2000-02-17	2000-08-17	0.00000	0.00000
2053	62	2001-02-17	2001-08-17	0.00000	0.00000
2054	62	2001-08-17	2002-02-17	0.00000	0.00000
2055	62	2002-02-17	2002-08-17	0.00000	0.00000
2056	62	2002-08-17	2003-02-17	0.00000	0.00000
2057	62	2003-02-17	2003-08-17	0.00000	0.00000
2058	62	2003-08-17	2004-02-17	0.00000	0.00000
2059	62	2004-02-17	2004-08-17	0.00000	0.00000
2060	62	2004-08-17	2005-02-17	0.00000	0.00000
2061	62	2005-02-17	2005-08-17	0.00000	0.00000
2062	62	2005-08-17	2006-02-17	0.00000	0.00000
2063	62	2006-02-17	2006-08-17	0.00000	0.00000
2064	62	2006-08-17	2007-02-17	0.00000	0.00000
2066	62	2007-08-17	2008-02-17	0.00000	0.00000
2067	62	2008-02-17	2008-08-17	0.00000	0.00000
2068	62	2008-08-17	2009-02-17	0.00000	0.00000
2069	62	2009-02-17	2009-08-17	0.00000	0.00000
2070	62	2009-08-17	2010-02-17	0.00000	0.00000
2071	62	2010-02-17	2010-08-17	0.00000	0.00000
2072	62	2010-08-17	2011-02-17	0.00000	0.00000
2073	62	2011-02-17	2011-08-17	0.00000	0.00000
2074	62	2011-08-17	2012-02-17	0.00000	0.00000
2075	62	2012-02-17	2012-08-17	0.00000	0.00000
2076	62	2012-08-17	2013-02-17	0.00000	0.00000
2077	62	2013-02-17	2013-08-17	0.00000	0.00000
2079	62	2014-02-17	2014-08-17	0.00000	0.00000
2080	62	2014-08-17	2015-02-17	0.00000	0.00000
2081	62	2015-02-17	2015-08-17	0.00000	0.00000
2082	62	2015-08-17	2016-02-17	0.00000	0.00000
2083	62	2016-02-17	2016-08-17	0.00000	0.00000
2084	62	2016-08-17	2017-02-17	0.00000	0.00000
2085	62	2017-02-17	2017-08-17	0.00000	0.00000
2086	62	2017-08-17	2018-02-17	0.00000	0.00000
2087	62	2018-02-17	2018-08-17	0.00000	0.00000
2088	62	2018-08-17	2019-02-17	0.00000	0.00000
2089	62	2019-02-17	2019-08-17	0.00000	0.00000
2090	62	2019-08-17	2020-02-17	0.00000	0.00000
2092	62	2020-08-17	2021-02-17	0.00000	0.00000
2093	62	2021-02-17	2021-08-17	0.00000	0.00000
2094	62	2021-08-17	2022-02-17	0.00000	0.00000
2095	62	2022-02-17	2022-08-17	0.00000	0.00000
2096	62	2022-08-17	2023-02-17	0.00000	0.00000
2097	62	2023-02-17	2023-08-17	0.00000	0.00000
2098	62	2023-08-17	2024-02-17	0.00000	0.00000
1970	59	1997-05-01	1997-11-01	3.62500	0.00000
2100	62	2024-08-17	2025-02-17	0.00000	0.00000
2133	63	2015-02-01	2015-08-01	2.87500	0.00000
2134	63	2015-08-01	2016-02-01	2.87500	0.00000
2135	63	2016-02-01	2016-08-01	2.87500	0.00000
2136	63	2016-08-01	2017-02-01	2.87500	0.00000
2137	63	2017-02-01	2017-08-01	2.87500	0.00000
2138	63	2017-08-01	2018-02-01	2.87500	0.00000
2139	63	2018-02-01	2018-08-01	2.87500	0.00000
2140	63	2018-08-01	2019-02-01	2.87500	0.00000
2141	63	2019-02-01	2019-08-01	2.87500	0.00000
2142	63	2019-08-01	2020-02-01	2.87500	0.00000
2143	63	2020-02-01	2020-08-01	2.87500	0.00000
2144	63	2020-08-01	2021-02-01	2.87500	0.00000
2145	63	2021-02-01	2021-08-01	2.87500	0.00000
2146	63	2021-08-01	2022-02-01	2.87500	0.00000
2147	63	2022-02-01	2022-08-01	2.87500	0.00000
2148	63	2022-08-01	2023-02-01	2.87500	0.00000
2149	63	2023-02-01	2023-08-01	2.87500	0.00000
2150	63	2023-08-01	2024-02-01	2.87500	0.00000
2151	63	2024-02-01	2024-08-01	2.87500	0.00000
2152	63	2024-08-01	2025-02-01	2.87500	0.00000
2153	63	2025-02-01	2025-08-01	2.87500	0.00000
2154	63	2025-08-01	2026-02-01	2.87500	0.00000
2155	63	2026-02-01	2026-08-01	2.87500	0.00000
2156	63	2026-08-01	2027-02-01	2.87500	0.00000
2157	63	2027-02-01	2027-08-01	2.87500	0.00000
2158	63	2027-08-01	2028-02-01	2.87500	0.00000
2159	63	2028-02-01	2028-08-01	2.87500	0.00000
2160	63	2028-08-01	2029-02-01	2.87500	0.00000
2161	63	2029-02-01	2029-08-01	2.87500	0.00000
2162	63	2029-08-01	2030-02-01	2.87500	0.00000
2163	63	2030-02-01	2030-08-01	2.87500	0.00000
2164	63	2030-08-01	2031-02-01	2.87500	0.00000
2165	63	2031-02-01	2031-08-01	2.87500	0.00000
2166	63	2031-08-01	2032-02-01	2.87500	0.00000
2167	63	2032-02-01	2032-08-01	2.87500	0.00000
2168	63	2032-08-01	2033-02-01	2.87500	100.00000
2169	64	2024-06-17	2024-07-15	0.26538	0.00000
2170	64	2024-07-15	2025-01-15	1.72500	0.00000
2171	64	2025-01-15	2025-07-15	1.72500	0.00000
2172	64	2025-07-15	2026-01-15	1.72500	0.00000
2173	64	2026-01-15	2026-07-15	1.72500	0.00000
2174	64	2026-07-15	2027-01-15	1.72500	0.00000
2175	64	2027-01-15	2027-07-15	1.72500	100.00000
2176	65	2020-07-16	2020-09-15	0.15747	0.00000
2177	65	2020-09-15	2021-03-15	0.47500	0.00000
2178	65	2021-03-15	2021-09-15	0.47500	0.00000
2179	65	2021-09-15	2022-03-15	0.47500	0.00000
2180	65	2022-03-15	2022-09-15	0.47500	0.00000
2181	65	2022-09-15	2023-03-15	0.47500	0.00000
2182	65	2023-03-15	2023-09-15	0.47500	0.00000
2183	65	2023-09-15	2024-03-15	0.47500	0.00000
2184	65	2024-03-15	2024-09-15	0.47500	0.00000
2185	65	2024-09-15	2025-03-15	0.47500	0.00000
2186	65	2025-03-15	2025-09-15	0.47500	0.00000
2187	65	2025-09-15	2026-03-15	0.47500	0.00000
2188	65	2026-03-15	2026-09-15	0.47500	0.00000
2189	65	2026-09-15	2027-03-15	0.47500	0.00000
2190	65	2027-03-15	2027-09-15	0.47500	100.00000
2231	71	2026-01-01	2026-07-01	1.50000	0.00000
2232	71	2026-07-01	2027-01-01	1.50000	0.00000
2233	71	2027-01-01	2027-07-01	1.50000	0.00000
2234	71	2027-07-01	2028-01-01	1.50000	0.00000
1969	59	1996-11-01	1997-05-01	3.62500	0.00000
1975	59	1999-11-01	2000-05-01	3.62500	0.00000
1990	59	2007-05-01	2007-11-01	3.62500	0.00000
2005	59	2014-11-01	2015-05-01	3.62500	0.00000
2020	59	2022-05-01	2022-11-01	3.62500	0.00000
2035	60	2028-09-15	2029-03-15	1.20000	0.00000
2052	62	2000-08-17	2001-02-17	0.00000	0.00000
2065	62	2007-02-17	2007-08-17	0.00000	0.00000
2078	62	2013-08-17	2014-02-17	0.00000	0.00000
2091	62	2020-02-17	2020-08-17	0.00000	0.00000
2099	62	2024-02-17	2024-08-17	0.00000	0.00000
2101	62	2025-02-17	2025-08-17	0.00000	0.00000
2102	62	2025-08-17	2026-02-17	0.00000	0.00000
2103	62	2026-02-17	2026-08-17	0.00000	0.00000
2104	62	2026-08-17	2027-02-17	0.00000	0.00000
2105	62	2027-02-17	2027-08-17	0.00000	0.00000
2106	62	2027-08-17	2028-02-17	0.00000	100.00000
2107	63	2002-01-01	2002-08-01	3.35938	0.00000
2108	63	2002-08-01	2003-02-01	2.87500	0.00000
2109	63	2003-02-01	2003-08-01	2.87500	0.00000
2110	63	2003-08-01	2004-02-01	2.87500	0.00000
2111	63	2004-02-01	2004-08-01	2.87500	0.00000
2112	63	2004-08-01	2005-02-01	2.87500	0.00000
2113	63	2005-02-01	2005-08-01	2.87500	0.00000
2114	63	2005-08-01	2006-02-01	2.87500	0.00000
2115	63	2006-02-01	2006-08-01	2.87500	0.00000
2116	63	2006-08-01	2007-02-01	2.87500	0.00000
2117	63	2007-02-01	2007-08-01	2.87500	0.00000
2118	63	2007-08-01	2008-02-01	2.87500	0.00000
2119	63	2008-02-01	2008-08-01	2.87500	0.00000
2120	63	2008-08-01	2009-02-01	2.87500	0.00000
2121	63	2009-02-01	2009-08-01	2.87500	0.00000
2122	63	2009-08-01	2010-02-01	2.87500	0.00000
2123	63	2010-02-01	2010-08-01	2.87500	0.00000
2124	63	2010-08-01	2011-02-01	2.87500	0.00000
2125	63	2011-02-01	2011-08-01	2.87500	0.00000
2126	63	2011-08-01	2012-02-01	2.87500	0.00000
2127	63	2012-02-01	2012-08-01	2.87500	0.00000
2128	63	2012-08-01	2013-02-01	2.87500	0.00000
2129	63	2013-02-01	2013-08-01	2.87500	0.00000
2130	63	2013-08-01	2014-02-01	2.87500	0.00000
2131	63	2014-02-01	2014-08-01	2.87500	0.00000
2132	63	2014-08-01	2015-02-01	2.87500	0.00000
2235	71	2028-01-01	2028-07-01	1.50000	0.00000
2236	71	2028-07-01	2029-01-01	1.50000	0.00000
2237	71	2029-01-01	2029-07-01	1.50000	0.00000
2238	71	2029-07-01	2030-01-01	1.50000	0.00000
2239	71	2030-01-01	2030-07-01	1.50000	0.00000
2240	71	2030-07-01	2031-01-01	1.50000	100.00000
2241	80	2024-02-19	2025-02-19	7.30000	0.00000
2242	80	2025-02-19	2026-02-19	7.30000	0.00000
2243	80	2026-02-19	2027-02-19	7.30000	0.00000
2244	80	2027-02-19	2028-02-19	7.30000	0.00000
2245	80	2028-02-19	2029-02-19	7.30000	0.00000
2246	80	2029-02-19	2030-02-19	7.30000	0.00000
2247	80	2030-02-19	2031-02-19	7.30000	0.00000
2248	80	2031-02-19	2032-02-19	7.30000	0.00000
2249	80	2032-02-19	2033-02-19	7.30000	0.00000
2250	80	2033-02-19	2034-02-19	7.30000	0.00000
2251	80	2034-02-19	2035-02-19	7.30000	0.00000
2252	80	2035-02-19	2036-02-19	7.30000	0.00000
2253	80	2036-02-19	2037-02-19	7.30000	100.00000
\.


--
-- Data for Name: counterparty; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.counterparty (id_counterparty, ctp_type, country, code, description, lei_code) FROM stdin;
1	1	332	RTL0001	Client Retail Test	
\.


--
-- Data for Name: counterparty_type; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.counterparty_type (id_counterparty_type, code, description) FROM stdin;
3	CORPORATE	Corporate
4	BANK	Bank
1	RCLIENT	Retail Client
2	ICLIENT	Institutional Client
5	CHOUSE	Clearing House
6	CUSTODIAN	Custodian
\.


--
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) FROM stdin;
234	Afghanistan	Afghanistan	AF	AFG	4				2	3
235	Albania	Albania	AL	ALB	8				2	3
236	Algeria	Algeria	DZ	DZA	12				2	3
237	American Samoa	American Samoa	AS	ASM	16				2	3
238	Andorra	Andorra	AD	AND	20				2	3
239	Angola	Angola	AO	AGO	24				2	3
240	Antigua and Barbuda	Antigua and Barbuda	AG	ATG	28				2	3
241	Azerbaijan	Azerbaijan	AZ	AZE	31				2	3
242	Argentina	Argentina	AR	ARG	32				2	3
243	Australia	Australia	AU	AUS	36				2	3
244	Austria	Austria	AT	AUT	40				2	3
245	Bahamas	Bahamas	BS	BHS	44				2	3
246	Bahrain	Bahrain	BH	BHR	48				2	3
247	Bangladesh	Bangladesh	BD	BGD	50				2	3
248	Armenia	Armenia	AM	ARM	51				2	3
249	Barbados	Barbados	BB	BRB	52				2	3
250	Belgium-Luxembourg	Belgium-Luxembourg	BE	BEL	58				2	3
251	Bermuda	Bermuda	BM	BMU	60				2	3
252	Bhutan	Bhutan	BT	BTN	64				2	3
253	Bolivia (Plurinational State of)	Plurinational State of Bolivia	BO	BOL	68				2	3
254	Bosnia Herzegovina	Bosnia Herzegovina	BA	BIH	70				2	3
255	Botswana	Botswana	BW	BWA	72				2	3
256	Brazil	Brazil	BR	BRA	76				2	3
257	Belize	Belize	BZ	BLZ	84				2	3
258	Br. Indian Ocean Terr.	British Indian Ocean Territories	IO	IOT	86				2	3
259	Solomon Isds	Solomon Islands	SB	SLB	90				2	3
260	Br. Virgin Isds	British Virgin Islands	VG	VGB	92				2	3
261	Brunei Darussalam	Brunei Darussalam	BN	BRN	96				2	3
262	Bulgaria	Bulgaria	BG	BGR	100				2	3
263	Myanmar	Myanmar	MM	MMR	104				2	3
264	Burundi	Burundi	BI	BDI	108				2	3
265	Belarus	Belarus	BY	BLR	112				2	3
266	Cambodia	Cambodia	KH	KHM	116				2	3
267	Cameroon	Cameroon	CM	CMR	120				2	3
268	Canada	Canada	CA	CAN	124				2	3
269	Cabo Verde	Cabo Verde	CV	CPV	132				2	3
270	Cayman Isds	Cayman Islands	KY	CYM	136				2	3
271	Central African Rep.	Central African Republic	CF	CAF	140				2	3
272	Sri Lanka	Sri Lanka	LK	LKA	144				2	3
273	Chad	Chad	TD	TCD	148				2	3
274	Chile	Chile	CL	CHL	152				2	3
275	China	China	CN	CHN	156				2	3
276	Christmas Isds	Christmas Islands	CX	CXR	162				2	3
277	Cocos Isds	Cocos Islands	CC	CCK	166				2	3
278	Colombia	Colombia	CO	COL	170				2	3
279	Comoros	Comoros	KM	COM	174				2	3
280	Mayotte	Mayotte	YT	MYT	175				2	3
281	Congo	Congo	CG	COG	178				2	3
282	Dem. Rep. of the Congo	Democratic Republic of the Congo	CD	COD	180				2	3
283	Cook Isds	Cook Islands	CK	COK	184				2	3
284	Costa Rica	Costa Rica	CR	CRI	188				2	3
285	Croatia	Croatia	HR	HRV	191				2	3
286	Cuba	Cuba	CU	CUB	192				2	3
287	Cyprus	Cyprus	CY	CYP	196				2	3
289	Czechia	Czechia	CZ	CZE	203				2	3
290	Benin	Benin	BJ	BEN	204				2	3
291	Denmark	Denmark	DK	DNK	208				2	3
292	Dominica	Dominica	DM	DMA	212				2	3
293	Dominican Rep.	Dominican Republic	DO	DOM	214				2	3
294	Ecuador	Ecuador	EC	ECU	218				2	3
295	El Salvador	El Salvador	SV	SLV	222				2	3
296	Equatorial Guinea	Equatorial Guinea	GQ	GNQ	226				2	3
297	Ethiopia	Ethiopia	ET	ETH	231				2	3
298	Estonia	Estonia	EE	EST	233				2	3
299	Falkland Isds (Malvinas)	Falkland Islands (Malvinas)	FK	FLK	238				2	3
300	Fiji	Fiji	FJ	FJI	242				2	3
301	Finland	Finland	FI	FIN	246				2	3
302	France	France and Monaco	FR	FRA	251				2	3
303	French Polynesia	French Polynesia	PF	PYF	258				2	3
304	Fr. South Antarctic Terr.	French South Antarctic Territories	FQ	ATF	260				2	3
305	Djibouti	Djibouti	DJ	DJI	262				2	3
306	Gabon	Gabon	GA	GAB	266				2	3
307	Georgia	Georgia	GE	GEO	268				2	3
308	Gambia	Gambia	GM	GMB	270				2	3
309	State of Palestine	State of Palestine	PS	PSE	275				2	3
311	Fmr Dem. Rep. of Germany	Former Democratic Republic of Germany	DD	DDR	278				2	3
310	Fmr Fed. Rep. of Germany	Former Federal Republic of Germany	DE	DEU	280				2	3
312	Ghana	Ghana	GH	GHA	288				2	3
313	Gibraltar	Gibraltar	GI	GIB	292				2	3
314	Kiribati	Kiribati	KI	KIR	296				2	3
315	Greece	Greece	GR	GRC	300				2	3
316	Greenland	Greenland	GL	GRL	304				2	3
317	Grenada	Grenada	GD	GRD	308				2	3
318	Guam	Guam	GU	GUM	316				2	3
319	Guatemala	Guatemala	GT	GTM	320				2	3
320	Guinea	Guinea	GN	GIN	324				2	3
321	Guyana	Guyana	GY	GUY	328				2	3
322	Haiti	Haiti	HT	HTI	332				2	3
323	Honduras	Honduras	HN	HND	340				2	3
324	China and Hong Kong SAR	China and Hong Kong Special Administrative Region	HK	HKG	344				2	3
325	Hungary	Hungary	HU	HUN	348				2	3
326	Iceland	Iceland	IS	ISL	352				2	3
327	Indonesia	Indonesia	ID	IDN	360				2	3
328	Iran	Iran	IR	IRN	364				2	3
329	Iraq	Iraq	IQ	IRQ	368				2	3
330	Ireland	Ireland	IE	IRL	372				2	3
331	Israel	Israel	IL	ISR	376				2	3
332	Italy	Italy	IT	ITA	381				2	3
333	C te d'Ivoire	C te d'Ivoire	CI	CIV	384				2	3
334	Jamaica	Jamaica	JM	JAM	388				2	3
335	Japan	Japan	JP	JPN	392				2	3
336	Kazakhstan	Kazakhstan	KZ	KAZ	398				2	3
337	Jordan	Jordan	JO	JOR	400				2	3
338	Kenya	Kenya	KE	KEN	404				2	3
339	Dem. People's Rep. of Korea	Democratic People's Republic of Korea	KP	PRK	408				2	3
340	Rep. of Korea	Republic of Korea	KR	KOR	410				2	3
341	Kuwait	Kuwait	KW	KWT	414				2	3
342	Kyrgyzstan	Kyrgyzstan	KG	KGZ	417				2	3
343	Lao People's Dem. Rep.	Lao People's Dem. Rep.	LA	LAO	418				2	3
344	Lebanon	Lebanon	LB	LBN	422				2	3
345	Lesotho	Lesotho	LS	LSO	426				2	3
346	Latvia	Latvia	LV	LVA	428				2	3
347	Liberia	Liberia	LR	LBR	430				2	3
348	Libya	Libya	LY	LBY	434				2	3
349	Lithuania	Lithuania	LT	LTU	440				2	3
350	Luxembourg	Luxembourg	LU	LUX	442				2	3
351	China Macao SAR	China Macao Special Administrative Region	MO	MAC	446				2	3
352	Madagascar	Madagascar	MG	MDG	450				2	3
353	Malawi	Malawi	MW	MWI	454				2	3
354	Malaysia	Malaysia	MY	MYS	458				2	3
355	Maldives	Maldives	MV	MDV	462				2	3
356	Mali	Mali	ML	MLI	466				2	3
357	Malta	Malta	MT	MLT	470				2	3
358	Mauritania	Mauritania	MR	MRT	478				2	3
359	Mauritius	Mauritius	MU	MUS	480				2	3
360	Mexico	Mexico	MX	MEX	484				2	3
361	Mongolia	Mongolia	MN	MNG	496				2	3
362	Rep. of Moldova	Republic of Moldova	MD	MDA	498				2	3
363	Montenegro	Montenegro	ME	MNE	499				2	3
364	Montserrat	Montserrat	MS	MSR	500				2	3
365	Morocco	Morocco	MA	MAR	504				2	3
366	Mozambique	Mozambique	MZ	MOZ	508				2	3
367	Oman	Oman	OM	OMN	512				2	3
368	Nauru	Nauru	NR	NRU	520				2	3
369	Nepal	Nepal	NP	NPL	524				2	3
370	Netherlands	Netherlands	NL	NLD	528				2	3
371	Neth. Antilles	Netherlands Antilles	AN	ANT	530				2	3
372	Cura ao	Cura ao	CW	CUW	531				2	3
373	Aruba	Aruba	AW	ABW	533				2	3
374	Saint Maarten	Saint Maarten (Dutch part)	SX	SXM	534				2	3
375	Bonaire	Bonaire Saint Eustatius and Saba	BQ	BES	535				2	3
376	New Caledonia	New Caledonia	NC	NCL	540				2	3
377	Vanuatu	Vanuatu	VU	VUT	548				2	3
378	New Zealand	New Zealand	NZ	NZL	554				2	3
379	Nicaragua	Nicaragua	NI	NIC	558				2	3
380	Niger	Niger	NE	NER	562				2	3
381	Nigeria	Nigeria	NG	NGA	566				2	3
382	Niue	Niue	NU	NIU	570				2	3
383	Norfolk Isds	Norfolk Islands	NF	NFK	574				2	3
384	Norway	Norway Svalbard and Jan Mayen	NO	NOR	579				2	3
385	N. Mariana Isds	Northern Mariana Islands	MP	MNP	580				2	3
386	FS Micronesia	Federated State of Micronesia	FM	FSM	583				2	3
387	Marshall Isds	Marshall Islands	MH	MHL	584				2	3
388	Palau	Palau	PW	PLW	585				2	3
389	Pakistan	Pakistan	PK	PAK	586				2	3
390	Panama	Panama	PA	PAN	591				2	3
391	Papua New Guinea	Papua New Guinea	PG	PNG	598				2	3
392	Paraguay	Paraguay	PY	PRY	600				2	3
393	Peru	Peru	PE	PER	604				2	3
394	Philippines	Philippines	PH	PHL	608				2	3
395	Pitcairn	Pitcairn	PN	PCN	612				2	3
396	Poland	Poland	PL	POL	616				2	3
397	Portugal	Portugal	PT	PRT	620				2	3
398	Guinea-Bissau	Guinea-Bissau	GW	GNB	624				2	3
399	Timor-Leste	Timor-Leste	TL	TLS	626				2	3
400	Qatar	Qatar	QA	QAT	634				2	3
401	Romania	Romania	RO	ROU	642				2	3
402	Russian Federation	Russian Federation	RU	RUS	643				2	3
403	Rwanda	Rwanda	RW	RWA	646				2	3
404	Saint Barth lemy	Saint Barth lemy	BL	BLM	652				2	3
405	Saint Helena	Saint Helena	SH	SHN	654				2	3
406	Saint Kitts and Nevis	Saint Kitts and Nevis	KN	KNA	659				2	3
407	Anguilla	Anguilla	AI	AIA	660				2	3
408	Saint Lucia	Saint Lucia	LC	LCA	662				2	3
409	Saint Pierre and Miquelon	Saint Pierre and Miquelon	PM	SPM	666				2	3
410	Saint Vincent and the Grenadines	Saint Vincent and the Grenadines	VC	VCT	670				2	3
411	San Marino	San Marino	SM	SMR	674				2	3
412	Sao Tome and Principe	Sao Tome and Principe	ST	STP	678				2	3
413	Saudi Arabia	Saudi Arabia	SA	SAU	682				2	3
414	Senegal	Senegal	SN	SEN	686				2	3
415	Serbia	Serbia	RS	SRB	688				2	3
416	Seychelles	Seychelles	SC	SYC	690				2	3
417	Sierra Leone	Sierra Leone	SL	SLE	694				2	3
418	India	India	IN	IND	699				2	3
419	Singapore	Singapore	SG	SGP	702				2	3
420	Slovakia	Slovakia	SK	SVK	703				2	3
421	Viet Nam	Viet Nam	VN	VNM	704				2	3
422	Slovenia	Slovenia	SI	SVN	705				2	3
423	Somalia	Somalia	SO	SOM	706				2	3
424	So. African Customs Union	Southern African Customs Union	ZA	ZAF	711				2	3
425	Zimbabwe	Zimbabwe	ZW	ZWE	716				2	3
426	Spain	Spain	ES	ESP	724				2	3
427	South Sudan	South Sudan	SS	SSD	728				2	3
428	Fmr Sudan	Former Sudan	SD	SDN	736				2	3
429	Suriname	Suriname	SR	SUR	740				2	3
430	Swaziland	Swaziland	SZ	SWZ	748				2	3
431	Sweden	Sweden	SE	SWE	752				2	3
432	Switzerland	Switzerland Liechtenstein	CH	CHE	757				2	3
433	Syria	Syria	SY	SYR	760				2	3
434	Tajikistan	Tajikistan	TJ	TJK	762				2	3
435	Thailand	Thailand	TH	THA	764				2	3
436	Togo	Togo	TG	TGO	768				2	3
437	Tokelau	Tokelau	TK	TKL	772				2	3
438	Tonga	Tonga	TO	TON	776				2	3
439	Trinidad and Tobago	Trinidad and Tobago	TT	TTO	780				2	3
440	United Arab Emirates	United Arab Emirates	AE	ARE	784				2	3
441	Tunisia	Tunisia	TN	TUN	788				2	3
442	Turkey	Turkey	TR	TUR	792				2	3
443	Turkmenistan	Turkmenistan	TM	TKM	795				2	3
444	Turks and Caicos Isds	Turks and Caicos Islands	TC	TCA	796				2	3
445	Tuvalu	Tuvalu	TV	TUV	798				2	3
446	Uganda	Uganda	UG	UGA	800				2	3
447	Ukraine	Ukraine	UA	UKR	804				2	3
448	TFYR of Macedonia	The Former Yugoslav Republic of Macedonia	MK	MKD	807				2	3
449	Fmr USSR	Former USSR	SU	SUN	810				2	3
450	Egypt	Egypt	EG	EGY	818				2	3
451	United Kingdom	United Kingdom	GB	GBR	826				2	3
452	United Rep. of Tanzania	United Republic of Tanzania	TZ	TZA	834				2	3
453	USA	USA Puerto Rico and US Virgin Islands	US	USA	842				2	3
454	Burkina Faso	Burkina Faso	BF	BFA	854				2	3
455	Uruguay	Uruguay	UY	URY	858				2	3
456	Uzbekistan	Uzbekistan	UZ	UZB	860				2	3
457	Venezuela	Venezuela	VE	VEN	862				2	3
458	Wallis and Futuna Isds	Wallis and Futuna Islands	WF	WLF	876				2	3
459	Samoa	Samoa	WS	WSM	882				2	3
460	Yemen	Yemen	YE	YEM	887				2	3
288	Serbia and Montenegro	Serbia and Montenegro	CS	SCG	891				2	3
\.


--
-- Data for Name: currency; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.currency (id_currency, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, calendar, business_days, daycount) FROM stdin;
88	LBP	422	Lebanese Pound	2	0	1	3	2	8
31	BIF	108	Burundi Franc	0	0	1	3	2	8
32	CVE	132	Cabo Verde Escudo	2	0	1	3	2	8
33	KHR	116	Riel	2	0	1	3	2	8
35	CAD	124	Canadian Dollar	2	0	1	3	2	8
36	KYD	136	Cayman Islands Dollar	2	0	1	3	2	8
89	LSL	426	Loti	2	0	1	3	2	8
37	CLP	152	Chilean Peso	0	0	1	3	2	8
38	CLF	990	Unidad de Fomento	4	0	1	3	2	8
39	CNY	156	Yuan Renminbi	2	0	1	3	2	8
91	LRD	430	Liberian Dollar	2	0	1	3	2	8
92	LYD	434	Libyan Dinar	3	0	1	3	2	8
40	COP	170	Colombian Peso	2	0	1	3	2	8
41	COU	970	Unidad de Valor Real	2	0	1	3	2	8
42	KMF	174	Comorian Franc	0	0	1	3	2	8
43	CDF	976	Congolese Franc	2	0	1	3	2	8
45	CRC	188	Costa Rican Colon	2	0	1	3	2	8
110	NPR	524	Nepalese Rupee	2	0	1	3	2	8
7	XCD	951	East Caribbean Dollar	2	0	1	3	2	8
46	CUP	192	Cuban Peso	2	0	1	3	2	8
94	MOP	446	Pataca	2	0	1	3	2	8
48	CZK	203	Czech Koruna	2	0	1	3	2	8
50	DJF	262	Djibouti Franc	0	0	1	3	2	8
95	MGA	969	Malagasy Ariary	2	0	1	3	2	8
51	DOP	214	Dominican Peso	2	0	1	3	2	8
96	MWK	454	Malawi Kwacha	2	0	1	3	2	8
52	EGP	818	Egyptian Pound	2	0	1	3	2	8
53	SVC	222	El Salvador Colon	2	0	1	3	2	8
97	MYR	458	Malaysian Ringgit	2	0	1	3	2	8
98	MVR	462	Rufiyaa	2	0	1	3	2	8
54	ERN	232	Nakfa	2	0	1	3	2	8
123	RON	946	Romanian Leu	2	0	1	3	2	8
55	SZL	748	Lilangeni	2	0	1	3	2	8
56	ETB	230	Ethiopian Birr	2	0	1	3	2	8
106	MAD	504	Moroccan Dirham	2	0	1	3	2	8
57	FKP	238	Falkland Islands Pound	2	0	1	3	2	8
117	PGK	598	Kina	2	0	1	3	2	8
58	FJD	242	Fiji Dollar	2	0	1	3	2	8
111	NIO	558	Cordoba Oro	2	0	1	3	2	8
99	MRU	929	Ouguiya	2	0	1	3	2	8
100	MUR	480	Mauritius Rupee	2	0	1	3	2	8
47	XCG	532	Caribbean Guilder	2	0	1	3	2	8
34	XAF	950	CFA Franc BEAC	0	0	1	3	2	8
60	GMD	270	Dalasi	2	0	1	3	2	8
61	GEL	981	Lari	2	0	1	3	2	8
101	XUA	965	ADB Unit of Account	0	0	1	3	2	8
62	GHS	936	Ghana Cedi	2	0	1	3	2	8
63	GIP	292	Gibraltar Pound	2	0	1	3	2	8
102	MXN	484	Mexican Peso	2	0	1	3	2	8
49	DKK	208	Danish Krone	2	0	1	3	2	8
112	NGN	566	Naira	2	0	1	3	2	8
103	MXV	979	Mexican Unidad de Inversion (UDI)	2	0	1	3	2	8
118	PYG	600	Guarani	0	0	1	3	2	8
64	GTQ	320	Quetzal	2	0	1	3	2	8
66	GNF	324	Guinean Franc	0	0	1	3	2	8
20	XOF	952	CFA Franc BCEAO	0	0	1	3	2	8
67	GYD	328	Guyana Dollar	2	0	1	3	2	8
68	HTG	332	Gourde	2	0	1	3	2	8
65	GBP	826	Pound Sterling	2	0	1	3	2	8
113	MKD	807	Denar	2	0	1	3	2	8
131	SCR	690	Seychelles Rupee	2	0	1	3	2	8
132	SLE	925	Leone	2	0	1	3	2	8
133	SGD	702	Singapore Dollar	2	0	1	3	2	8
134	XSU	994	Sucre	0	0	1	3	2	8
90	ZAR	710	Rand	2	0	1	3	2	8
135	SBD	90	Solomon Islands Dollar	2	0	1	3	2	8
136	SOS	706	Somali Shilling	2	0	1	3	2	8
137	SSP	728	South Sudanese Pound	2	0	1	3	2	8
138	LKR	144	Sri Lanka Rupee	2	0	1	3	2	8
139	SDG	938	Sudanese Pound	2	0	1	3	2	8
140	SRD	968	Surinam Dollar	2	0	1	3	2	8
141	SEK	752	Swedish Krona	2	0	1	3	2	8
142	CHE	947	WIR Euro	2	0	1	3	2	8
143	CHW	948	WIR Franc	2	0	1	3	2	8
144	SYP	760	Syrian Pound	2	0	1	3	2	8
145	TWD	901	New Taiwan Dollar	2	0	1	3	2	8
146	TJS	972	Somoni	2	0	1	3	2	8
147	TZS	834	Tanzanian Shilling	2	0	1	3	2	8
148	THB	764	Baht	2	0	1	3	2	8
44	NZD	554	New Zealand Dollar	2	0	1	3	2	8
150	TTD	780	Trinidad and Tobago Dollar	2	0	1	3	2	8
151	TND	788	Tunisian Dinar	3	0	1	3	2	8
152	TRY	949	Turkish Lira	2	0	1	3	2	8
153	TMT	934	Turkmenistan New Manat	2	0	1	3	2	8
154	UGX	800	Uganda Shilling	0	0	1	3	2	8
155	UAH	980	Hryvnia	2	0	1	3	2	8
156	AED	784	UAE Dirham	2	0	1	3	2	8
157	USN	997	US Dollar (Next day)	2	0	1	3	2	8
158	UYU	858	Peso Uruguayo	2	0	1	3	2	8
159	UYI	940	Uruguay Peso en Unidades Indexadas (UI)	0	0	1	3	2	8
160	UYW	927	Unidad Previsional	4	0	1	3	2	8
161	UZS	860	Uzbekistan Sum	2	0	1	3	2	8
162	VUV	548	Vatu	0	0	1	3	2	8
165	VND	704	Dong	0	0	1	3	2	8
166	YER	886	Yemeni Rial	2	0	1	3	2	8
167	ZMW	967	Zambian Kwacha	2	0	1	3	2	8
168	ZWG	924	Zimbabwe Gold	2	0	1	3	2	8
169	XAU	959	Gold	0	0	1	3	2	8
170	XPD	964	Palladium	0	0	1	3	2	8
171	XPT	962	Platinum	0	0	1	3	2	8
172	XAG	961	Silver	0	0	1	3	2	8
149	TOP	776	PaÔÇÖanga	2	0	1	3	2	8
163	VES	928	Bol├¡var Soberano	2	0	1	3	2	8
164	VED	926	Bol├¡var Soberano	2	0	1	3	2	8
3	ALL	8	Lek	2	0	1	3	2	8
4	DZD	12	Algerian Dinar	2	0	1	3	2	8
6	AOA	973	Kwanza	2	0	1	3	2	8
83	KPW	408	North Korean Won	2	0	1	3	2	8
8	XAD	396	Arab Accounting Dinar	2	0	1	3	2	8
9	ARS	32	Argentine Peso	2	0	1	3	2	8
10	AMD	51	Armenian Dram	2	0	1	3	2	8
11	AWG	533	Aruban Florin	2	0	1	3	2	8
84	KRW	410	Won	0	0	1	3	2	8
13	AZN	944	Azerbaijan Manat	2	0	1	3	2	8
14	BSD	44	Bahamian Dollar	2	0	1	3	2	8
15	BHD	48	Bahraini Dinar	3	0	1	3	2	8
16	BDT	50	Taka	2	0	1	3	2	8
17	BBD	52	Barbados Dollar	2	0	1	3	2	8
18	BYN	933	Belarusian Ruble	2	0	1	3	2	8
85	KWD	414	Kuwaiti Dinar	3	0	1	3	2	8
19	BZD	84	Belize Dollar	2	0	1	3	2	8
21	BMD	60	Bermudian Dollar	2	0	1	3	2	8
23	BTN	64	Ngultrum	2	0	1	3	2	8
24	BOB	68	Boliviano	2	0	1	3	2	8
25	BOV	984	Mvdol	2	0	1	3	2	8
86	KGS	417	Som	2	0	1	3	2	8
26	BAM	977	Convertible Mark	2	0	1	3	2	8
27	BWP	72	Pula	2	0	1	3	2	8
29	BRL	986	Brazilian Real	2	0	1	3	2	8
87	LAK	418	Lao Kip	2	0	1	3	2	8
30	BND	96	Brunei Dollar	2	0	1	3	2	8
116	PAB	590	Balboa	2	0	1	3	2	8
1	AFN	971	´╗┐Afghani	2	0	1	3	2	8
5	USD	840	US Dollar	2	0	1	4	2	8
104	MDL	498	Moldovan Leu	2	0	1	3	2	8
69	HNL	340	Lempira	2	0	1	3	2	8
70	HKD	344	Hong Kong Dollar	2	0	1	3	2	8
71	HUF	348	Forint	2	0	1	3	2	8
72	ISK	352	Iceland Krona	0	0	1	3	2	8
22	INR	356	Indian Rupee	2	0	1	3	2	8
73	IDR	360	Rupiah	2	0	1	3	2	8
74	XDR	960	SDR (Special Drawing Right)	0	0	1	3	2	8
75	IRR	364	Iranian Rial	2	0	1	3	2	8
76	IQD	368	Iraqi Dinar	3	0	1	3	2	8
122	QAR	634	Qatari Rial	2	0	1	3	2	8
105	MNT	496	Tugrik	2	0	1	3	2	8
77	ILS	376	New Israeli Sheqel	2	0	1	3	2	8
93	CHF	756	Swiss Franc	2	0	1	3	2	8
78	JMD	388	Jamaican Dollar	2	0	1	3	2	8
79	JPY	392	Yen	0	0	1	3	2	8
59	XPF	953	CFP Franc	0	0	1	3	2	8
80	JOD	400	Jordanian Dinar	3	0	1	3	2	8
81	KZT	398	Tenge	2	0	1	3	2	8
82	KES	404	Kenyan Shilling	2	0	1	3	2	8
119	PEN	604	Sol	2	0	1	3	2	8
28	NOK	578	Norwegian Krone	2	0	1	3	2	8
107	MZN	943	Mozambique Metical	2	0	1	3	2	8
108	MMK	104	Kyat	2	0	1	3	2	8
109	NAD	516	Namibia Dollar	2	0	1	3	2	8
2	EUR	978	Euro	2	0	1	3	2	8
114	OMR	512	Rial Omani	3	0	1	3	2	8
115	PKR	586	Pakistan Rupee	2	0	1	3	2	8
120	PHP	608	Philippine Peso	2	0	1	3	2	8
12	AUD	36	Australian Dollar	2	0	1	3	2	8
121	PLN	985	Zloty	2	0	1	3	2	8
124	RUB	643	Russian Ruble	2	0	1	3	2	8
125	RWF	646	Rwanda Franc	0	0	1	3	2	8
126	SHP	654	Saint Helena Pound	2	0	1	3	2	8
127	WST	882	Tala	2	0	1	3	2	8
128	STN	930	Dobra	2	0	1	3	2	8
129	SAR	682	Saudi Riyal	2	0	1	3	2	8
130	RSD	941	Serbian Dinar	2	0	1	3	2	8
\.


--
-- Data for Name: currency_pair; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.currency_pair (id_currency_pair, code, bcy, ccy, bid, ask) FROM stdin;
1	EURUSD	2	5	1.15069	1.15069
2	EURCHF	2	93	0.90624	0.90624
3	EURJPY	2	79	183.16800	183.16800
4	EURCAD	2	35	1.57454	1.57454
5	EURAUD	2	12	1.62697	1.62697
6	EURGBP	2	65	0.86356	0.86356
\.


--
-- Data for Name: currpair_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.currpair_master_data (id_master_data, bcy, ccy, bcy_irc, ccy_irc) FROM stdin;
\.


--
-- Data for Name: daycount; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.daycount (id_daycount, code, description) FROM stdin;
6	NASD_30_360	30/360 NASD
7	EUR_30_360	30/360 EUR
8	ACT_360	Actual/360
9	ACT_365	Actual/365
10	ACT_ACT	Actual/Actual
\.


--
-- Data for Name: deliverable_bonds; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.deliverable_bonds (id_deliverable_bonds, master_data, expiration_date, isin, coupon_rate, bond_maturity, bond_cf) FROM stdin;
8	67	2026-06-08	IT0005358806	3.35000	2035-03-01	0.82719
9	67	2026-06-08	IT0005402117	1.45000	2036-03-01	0.67328
10	67	2026-06-08	IT0005433195	0.95000	2037-03-01	0.60984
11	67	2026-06-08	IT0005607970	3.85000	2035-02-01	0.86189
12	67	2026-06-08	IT0005631590	3.65000	2035-08-01	0.84187
13	67	2026-06-08	IT0005648149	3.60000	2035-10-01	0.83617
14	67	2026-06-08	IT0005676504	3.45000	2036-02-01	0.82088
\.


--
-- Data for Name: ec_exchange_rate; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.ec_exchange_rate (id_ec_exchange_rate, country, currency, isoa3code, isoa2code, rate) FROM stdin;
\.


--
-- Data for Name: finacial_txn; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.finacial_txn (id_finacial_txn, counterparty, position_md, master_data, txn_status, txn_size, description, trade_date, settlement, quantity, price) FROM stdin;
\.


--
-- Data for Name: forex_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.forex_master_data (id_master_data, bcy, ccy, bcy_irc, ccy_irc) FROM stdin;
69	2	5	EURIBOR	SOFR
74	2	93	EURIBOR	SOFR
77	2	35	EURIBOR	SOFR
78	2	12	EURIBOR	SOFR
79	2	79	EURIBOR	SOFR
\.


--
-- Data for Name: form; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.form (id_form, code, description) FROM stdin;
4	BEARER	Bearer
5	REGISTERED	Registered
6	BOOK-ENTRY-BOND	Book-entry Bond
\.


--
-- Data for Name: frequency; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.frequency (id_frequency, code, description, year_fraction) FROM stdin;
7	ANNUAL	Annual	1
8	SEMI-ANNUAL	Semi Annual	2
9	E4M	Every 4 months	3
10	QUARTERLY	Quarterly	4
11	BI-MONTHLY	Every two months	6
12	MONTHLY	Monthly	12
100	CUSTOM	Custom	0
\.


--
-- Data for Name: future_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.future_master_data (id_master_data, isin, settlement_type, description, exchange_contract_code) FROM stdin;
67	IT0024832682	2	Btp Mini-futures 10y Giugno 2026	MBTP
70	IT0001	2	Btp Mini-futures 10y Settembre 2026	MBTP
81	ZBM6	1	U.S. Treasury Bond 10Y Future	CME
83	6SM6	1	Swiss Franc Futures	CME
82	6EM6	1	Euro Future	CME
84	ESRM6	1	ESTR Futures JUN 2026	CME
\.


--
-- Data for Name: fx_future_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.fx_future_master_data (id_master_data, underlying, contract_value, tick_size, initial_margin, maintenance_margin) FROM stdin;
82	69	125000.00000	0.01000	300.00000	200.00000
83	74	125000.00000	0.01000	300.00000	200.00000
\.


--
-- Data for Name: holiday; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) FROM stdin;
19	3	1	8	New Year Day
20	3	3	4	Good Friday
21	3	6	4	Easter Monday
22	3	1	5	Labor Day
23	3	25	12	Christmas Day
24	3	26	12	Christmas Holiday
25	4	1	1	New Year Day
26	4	19	1	Martin Luther King Jr. Day
27	4	16	2	Presidents Day
28	4	3	4	Good Friday
29	4	25	5	Memorial Day
30	4	19	6	Juneteenth National Independence Day
31	4	3	7	Independence Day
32	4	7	9	Labor Day
33	4	12	10	Columbus Day
34	4	11	11	Veterans Day
35	4	26	11	Thanksgiving
36	4	25	12	Christmas Day
\.


--
-- Data for Name: instrument_quote; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.instrument_quote (id_instrument_quote, master_data, code, bid, ask, provider) FROM stdin;
69	69	EURUSD	0.00000	0.00000	InvestingComProvider
70	74	EURCHF	0.00000	0.00000	InvestingComProvider
72	82	58@6EM6	97.86750	97.86750	CmeGroupProvider
74	84	10247@ESRM6	97.86750	97.86750	CmeGroupProvider
42	30	IT0005402117-MOTX	81.53000	81.53000	EuroNextProvider
43	31	IT0005676504-MOTX	98.05000	98.05000	EuroNextProvider
44	32	IT0005648149-MOTX	99.53000	99.53000	EuroNextProvider
45	33	IT0005631590-MOTX	100.10000	100.10000	EuroNextProvider
46	34	IT0005607970-MOTX	102.05000	102.05000	EuroNextProvider
47	35	IT0005508590-MOTX	103.29000	103.29000	EuroNextProvider
48	36	IT0005358806-MOTX	98.45000	98.45000	EuroNextProvider
49	37	IT0005466351-MOTX	86.49000	86.49000	EuroNextProvider
50	38	IT0005584856-MOTX	102.45000	102.45000	EuroNextProvider
51	39	IT0003535157-MOTX	110.77000	110.77000	EuroNextProvider
52	40	IT0005634800-MOTX	99.14000	99.14000	EuroNextProvider
53	41	IT0005560948-MOTX	105.05000	105.05000	EuroNextProvider
3	50	IT0005692485-MOTX	99.19500	99.19500	EuroNextProvider
4	51	IT0005674335-MOTX	98.71600	98.71600	EuroNextProvider
5	52	IT0005669269-MOTX	98.90000	98.90000	EuroNextProvider
6	53	IT0005680639-MOTX	99.58700	99.58700	EuroNextProvider
7	54	IT0005666851-MOTX	99.10600	99.10600	EuroNextProvider
8	55	IT0005650574-MOTX	99.68700	99.68700	EuroNextProvider
9	56	IT0005655037-MOTX	99.50000	99.50000	EuroNextProvider
10	57	IT0005660029-MOTX	99.31200	99.31200	EuroNextProvider
11	58	IT0005645509-MOTX	99.85700	99.85700	EuroNextProvider
12	1	IT0005668238-MOTX	102.38000	102.38000	EuroNextProvider
13	2	IT0005611741-MOTX	97.30000	97.30000	EuroNextProvider
14	44	IT0005640666-MOTX	99.99200	99.99200	EuroNextProvider
15	3	IT0005534141-MOTX	100.56000	100.56000	EuroNextProvider
16	4	IT0005217390-MOTX	70.45000	70.45000	EuroNextProvider
17	5	IT0005480980-MOTX	64.93000	64.93000	EuroNextProvider
18	6	IT0005425233-MOTX	59.10000	59.10000	EuroNextProvider
19	7	IT0005398406-MOTX	71.32000	71.32000	EuroNextProvider
54	42	IT0005544082-MOTX	106.15000	106.15000	EuroNextProvider
55	43	IT0005240350-MOTX	93.96000	93.96000	EuroNextProvider
56	45	IT0005689887-MOTX	98.07600	98.07600	EuroNextProvider
57	46	IT0005670895-MOTX	99.92600	99.92600	EuroNextProvider
58	47	IT0005695256-MOTX	97.86700	97.86700	EuroNextProvider
59	60	IT0005689960-MOTX	98.99000	98.99000	EuroNextProvider
60	62	IT0001200390-MOTX	94.79000	94.79000	EuroNextProvider
61	63	IT0003256820-MOTX	114.88000	114.88000	EuroNextProvider
62	64	IT0005599904-MOTX	101.22000	101.22000	EuroNextProvider
63	59	IT0001086567-MOTX	103.07300	103.07300	EuroNextProvider
64	65	IT0005416570-MOTX	97.78000	97.78000	EuroNextProvider
1	48	IT0005684888-MOTX	98.28800	98.28800	EuroNextProvider
2	49	IT0005678492-MOTX	98.50600	98.50600	EuroNextProvider
20	8	IT0005363111-MOTX	93.01000	93.01000	EuroNextProvider
21	9	IT0005273013-MOTX	87.90000	87.90000	EuroNextProvider
22	10	IT0005162828-MOTX	78.27000	78.27000	EuroNextProvider
23	11	IT0005441883-MOTX	58.61000	58.61000	EuroNextProvider
24	12	IT0005083057-MOTX	86.15000	86.15000	EuroNextProvider
25	13	IT0005631608-MOTX	97.77000	97.77000	EuroNextProvider
26	14	IT0005438004-MOTX	64.41000	64.41000	EuroNextProvider
27	15	IT0004923998-MOTX	107.13000	107.13000	EuroNextProvider
28	16	IT0005530032-MOTX	103.26000	103.26000	EuroNextProvider
29	17	IT0005421703-MOTX	74.67000	74.67000	EuroNextProvider
30	18	IT0005635583-MOTX	97.70000	97.70000	EuroNextProvider
31	19	IT0004532559-MOTX	110.80000	110.80000	EuroNextProvider
32	20	IT0005377152-MOTX	90.48000	90.48000	EuroNextProvider
33	21	IT0005582421-MOTX	101.74000	101.74000	EuroNextProvider
34	22	IT0005442097-MOTX	78.80000	78.80000	EuroNextProvider
35	23	IT0004286966-MOTX	110.93000	110.93000	EuroNextProvider
36	24	IT0005321325-MOTX	90.79000	90.79000	EuroNextProvider
37	25	IT0005496770-MOTX	94.20000	94.20000	EuroNextProvider
38	26	IT0005596470-MOTX	102.12000	102.12000	EuroNextProvider
39	27	IT0005433195-MOTX	74.82000	74.82000	EuroNextProvider
40	28	IT0003934657-MOTX	102.35000	102.35000	EuroNextProvider
41	29	IT0005177909-MOTX	87.34000	87.34000	EuroNextProvider
66	67	MBTP-DMIL?fOrO=F&md=01-06-2026	117.98000	117.98000	EuroNextProvider
71	81	307@ZBM6	0.00000	0.00000	CmeGroupProvider
73	83	86@6SM6	0.00000	0.00000	CmeGroupProvider
\.


--
-- Data for Name: instrument_quote_hist; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.instrument_quote_hist (id_instrument_quote_hist, instrument_quote, master_data, code, bid, ask, update_date) FROM stdin;
1	18	6	IT0005425233	60.08000	60.08000	2026-03-12
2	19	7	IT0005398406	72.59000	72.59000	2026-03-12
3	20	8	IT0005363111	94.44000	94.44000	2026-03-12
4	21	9	IT0005273013	89.28000	89.28000	2026-03-12
5	22	10	IT0005162828	79.38000	79.38000	2026-03-12
6	23	11	IT0005441883	59.26000	59.26000	2026-03-12
7	24	12	IT0005083057	87.45000	87.45000	2026-03-12
8	25	13	IT0005631608	99.18000	99.18000	2026-03-12
9	26	14	IT0005438004	65.45000	65.45000	2026-03-12
10	27	15	IT0004923998	108.63000	108.63000	2026-03-12
11	28	16	IT0005530032	104.55000	104.55000	2026-03-12
12	29	17	IT0005421703	75.67000	75.67000	2026-03-12
13	30	18	IT0005635583	99.05000	99.05000	2026-03-12
14	31	19	IT0004532559	112.08000	112.08000	2026-03-12
15	32	20	IT0005377152	91.61000	91.61000	2026-03-12
16	33	21	IT0005582421	102.91000	102.91000	2026-03-12
17	34	22	IT0005442097	79.64000	79.64000	2026-03-12
18	57	46	IT0005670895	99.88300	99.88300	2026-03-12
19	58	47	IT0005695256	97.87400	97.87400	2026-03-12
20	59	60	IT0005689960	99.24000	99.24000	2026-03-12
21	60	62	IT0001200390	94.28000	94.28000	2026-03-12
22	61	63	IT0003256820	115.57000	115.57000	2026-03-12
23	62	64	IT0005599904	101.34000	101.34000	2026-03-12
24	63	59	IT0001086567	103.18000	103.18000	2026-03-12
25	64	65	IT0005416570	97.81000	97.81000	2026-03-12
27	66	67	IT0024832682	118.91000	118.91000	2026-03-12
28	1	48	IT0005684888	98.35000	98.35000	2026-03-12
29	2	49	IT0005678492	98.49200	98.49200	2026-03-12
30	3	50	IT0005692485	99.14400	99.14400	2026-03-12
31	4	51	IT0005674335	98.65000	98.65000	2026-03-12
32	5	52	IT0005669269	98.88700	98.88700	2026-03-12
33	6	53	IT0005680639	99.54800	99.54800	2026-03-12
34	7	54	IT0005666851	99.05400	99.05400	2026-03-12
35	8	55	IT0005650574	99.63500	99.63500	2026-03-12
36	9	56	IT0005655037	99.46000	99.46000	2026-03-12
37	10	57	IT0005660029	99.26300	99.26300	2026-03-12
38	11	58	IT0005645509	99.81000	99.81000	2026-03-12
39	12	1	IT0005668238	104.30000	104.30000	2026-03-12
40	13	2	IT0005611741	99.01000	99.01000	2026-03-12
41	14	44	IT0005640666	99.99000	99.99000	2026-03-12
42	15	3	IT0005534141	102.40000	102.40000	2026-03-12
43	16	4	IT0005217390	71.15000	71.15000	2026-03-12
44	17	5	IT0005480980	66.14000	66.14000	2026-03-12
45	35	23	IT0004286966	112.14000	112.14000	2026-03-12
46	36	24	IT0005321325	91.85000	91.85000	2026-03-12
47	37	25	IT0005496770	95.10000	95.10000	2026-03-12
48	38	26	IT0005596470	103.19000	103.19000	2026-03-12
49	39	27	IT0005433195	75.48000	75.48000	2026-03-12
50	40	28	IT0003934657	103.38000	103.38000	2026-03-12
51	41	29	IT0005177909	88.16000	88.16000	2026-03-12
52	42	30	IT0005402117	82.24000	82.24000	2026-03-12
53	43	31	IT0005676504	98.86000	98.86000	2026-03-12
54	44	32	IT0005648149	100.23000	100.23000	2026-03-12
55	45	33	IT0005631590	100.96000	100.96000	2026-03-12
56	46	34	IT0005607970	102.73000	102.73000	2026-03-12
57	47	35	IT0005508590	104.03000	104.03000	2026-03-12
58	48	36	IT0005358806	99.20000	99.20000	2026-03-12
59	49	37	IT0005466351	86.96000	86.96000	2026-03-12
60	50	38	IT0005584856	103.07000	103.07000	2026-03-12
61	51	39	IT0003535157	111.49000	111.49000	2026-03-12
62	52	40	IT0005634800	99.72000	99.72000	2026-03-12
63	53	41	IT0005560948	105.68000	105.68000	2026-03-12
64	54	42	IT0005544082	106.82000	106.82000	2026-03-12
65	55	43	IT0005240350	94.54000	94.54000	2026-03-12
66	56	45	IT0005689887	98.06200	98.06200	2026-03-12
145	30	18	IT0005635583	97.86000	97.86000	2026-03-13
146	31	19	IT0004532559	110.87000	110.87000	2026-03-13
147	32	20	IT0005377152	90.56000	90.56000	2026-03-13
148	33	21	IT0005582421	101.75000	101.75000	2026-03-13
149	34	22	IT0005442097	78.79000	78.79000	2026-03-13
177	35	23	IT0004286966	111.02000	111.02000	2026-03-13
178	36	24	IT0005321325	90.85000	90.85000	2026-03-13
179	37	25	IT0005496770	94.25000	94.25000	2026-03-13
180	38	26	IT0005596470	102.19000	102.19000	2026-03-13
181	39	27	IT0005433195	74.82000	74.82000	2026-03-13
182	40	28	IT0003934657	102.42000	102.42000	2026-03-13
183	41	29	IT0005177909	87.40000	87.40000	2026-03-13
160	1	48	IT0005684888	98.28200	98.28200	2026-03-13
161	2	49	IT0005678492	98.50200	98.50200	2026-03-13
162	3	50	IT0005692485	99.18200	99.18200	2026-03-13
163	4	51	IT0005674335	98.69500	98.69500	2026-03-13
164	5	52	IT0005669269	98.90700	98.90700	2026-03-13
165	6	53	IT0005680639	99.58100	99.58100	2026-03-13
166	7	54	IT0005666851	99.10400	99.10400	2026-03-13
167	8	55	IT0005650574	99.68000	99.68000	2026-03-13
168	9	56	IT0005655037	99.50200	99.50200	2026-03-13
169	10	57	IT0005660029	99.30000	99.30000	2026-03-13
170	11	58	IT0005645509	99.84300	99.84300	2026-03-13
171	12	1	IT0005668238	102.35000	102.35000	2026-03-13
172	13	2	IT0005611741	97.19000	97.19000	2026-03-13
173	14	44	IT0005640666	99.99200	99.99200	2026-03-13
174	15	3	IT0005534141	100.55000	100.55000	2026-03-13
175	16	4	IT0005217390	70.20000	70.20000	2026-03-13
176	17	5	IT0005480980	64.85000	64.85000	2026-03-13
133	18	6	IT0005425233	59.10000	59.10000	2026-03-13
134	19	7	IT0005398406	71.31000	71.31000	2026-03-13
135	20	8	IT0005363111	93.00000	93.00000	2026-03-13
136	21	9	IT0005273013	87.88000	87.88000	2026-03-13
137	22	10	IT0005162828	78.19000	78.19000	2026-03-13
138	23	11	IT0005441883	58.44000	58.44000	2026-03-13
139	24	12	IT0005083057	86.16000	86.16000	2026-03-13
140	25	13	IT0005631608	97.81000	97.81000	2026-03-13
141	26	14	IT0005438004	64.36000	64.36000	2026-03-13
142	27	15	IT0004923998	107.01000	107.01000	2026-03-13
143	28	16	IT0005530032	103.23000	103.23000	2026-03-13
144	29	17	IT0005421703	74.74000	74.74000	2026-03-13
184	42	30	IT0005402117	81.57000	81.57000	2026-03-13
185	43	31	IT0005676504	98.07000	98.07000	2026-03-13
186	44	32	IT0005648149	99.59000	99.59000	2026-03-13
187	45	33	IT0005631590	100.15000	100.15000	2026-03-13
188	46	34	IT0005607970	102.05000	102.05000	2026-03-13
189	47	35	IT0005508590	103.35000	103.35000	2026-03-13
190	48	36	IT0005358806	98.49000	98.49000	2026-03-13
191	49	37	IT0005466351	86.40000	86.40000	2026-03-13
192	50	38	IT0005584856	102.45000	102.45000	2026-03-13
193	51	39	IT0003535157	110.79000	110.79000	2026-03-13
194	52	40	IT0005634800	98.96000	98.96000	2026-03-13
195	53	41	IT0005560948	105.08000	105.08000	2026-03-13
196	54	42	IT0005544082	106.09000	106.09000	2026-03-13
197	55	43	IT0005240350	93.97000	93.97000	2026-03-13
198	56	45	IT0005689887	98.07000	98.07000	2026-03-13
150	57	46	IT0005670895	99.92000	99.92000	2026-03-13
151	58	47	IT0005695256	97.87200	97.87200	2026-03-13
152	59	60	IT0005689960	99.03000	99.03000	2026-03-13
153	60	62	IT0001200390	94.36000	94.36000	2026-03-13
154	61	63	IT0003256820	114.86000	114.86000	2026-03-13
155	62	64	IT0005599904	101.24000	101.24000	2026-03-13
156	63	59	IT0001086567	103.05500	103.05500	2026-03-13
157	64	65	IT0005416570	97.77000	97.77000	2026-03-13
159	66	67	IT0024832682	117.88000	117.88000	2026-03-13
265	42	30	IT0005402117	81.53000	81.53000	2026-03-16
266	43	31	IT0005676504	98.05000	98.05000	2026-03-16
267	44	32	IT0005648149	99.53000	99.53000	2026-03-16
268	45	33	IT0005631590	100.10000	100.10000	2026-03-16
269	46	34	IT0005607970	102.05000	102.05000	2026-03-16
270	47	35	IT0005508590	103.29000	103.29000	2026-03-16
271	48	36	IT0005358806	98.45000	98.45000	2026-03-16
272	49	37	IT0005466351	86.49000	86.49000	2026-03-16
273	50	38	IT0005584856	102.45000	102.45000	2026-03-16
274	51	39	IT0003535157	110.77000	110.77000	2026-03-16
275	52	40	IT0005634800	99.14000	99.14000	2026-03-16
276	53	41	IT0005560948	105.05000	105.05000	2026-03-16
277	3	50	IT0005692485	99.19500	99.19500	2026-03-16
278	4	51	IT0005674335	98.71600	98.71600	2026-03-16
279	5	52	IT0005669269	98.90000	98.90000	2026-03-16
280	6	53	IT0005680639	99.58700	99.58700	2026-03-16
281	7	54	IT0005666851	99.10600	99.10600	2026-03-16
282	8	55	IT0005650574	99.68700	99.68700	2026-03-16
283	9	56	IT0005655037	99.50000	99.50000	2026-03-16
284	10	57	IT0005660029	99.31200	99.31200	2026-03-16
285	11	58	IT0005645509	99.85700	99.85700	2026-03-16
286	12	1	IT0005668238	102.38000	102.38000	2026-03-16
287	13	2	IT0005611741	97.30000	97.30000	2026-03-16
288	14	44	IT0005640666	99.99200	99.99200	2026-03-16
289	15	3	IT0005534141	100.56000	100.56000	2026-03-16
290	16	4	IT0005217390	70.45000	70.45000	2026-03-16
291	17	5	IT0005480980	64.93000	64.93000	2026-03-16
292	18	6	IT0005425233	59.10000	59.10000	2026-03-16
293	19	7	IT0005398406	71.32000	71.32000	2026-03-16
294	54	42	IT0005544082	106.15000	106.15000	2026-03-16
295	55	43	IT0005240350	93.96000	93.96000	2026-03-16
296	56	45	IT0005689887	98.07600	98.07600	2026-03-16
297	57	46	IT0005670895	99.92600	99.92600	2026-03-16
298	58	47	IT0005695256	97.86700	97.86700	2026-03-16
299	59	60	IT0005689960	98.99000	98.99000	2026-03-16
300	60	62	IT0001200390	94.79000	94.79000	2026-03-16
301	61	63	IT0003256820	114.88000	114.88000	2026-03-16
302	62	64	IT0005599904	101.22000	101.22000	2026-03-16
303	63	59	IT0001086567	103.07300	103.07300	2026-03-16
304	64	65	IT0005416570	97.78000	97.78000	2026-03-16
306	66	67	IT0024832682	117.98000	117.98000	2026-03-16
307	1	48	IT0005684888	98.28800	98.28800	2026-03-16
308	2	49	IT0005678492	98.50600	98.50600	2026-03-16
309	20	8	IT0005363111	93.01000	93.01000	2026-03-16
310	21	9	IT0005273013	87.90000	87.90000	2026-03-16
311	22	10	IT0005162828	78.27000	78.27000	2026-03-16
312	23	11	IT0005441883	58.61000	58.61000	2026-03-16
313	24	12	IT0005083057	86.15000	86.15000	2026-03-16
314	25	13	IT0005631608	97.77000	97.77000	2026-03-16
315	26	14	IT0005438004	64.41000	64.41000	2026-03-16
316	27	15	IT0004923998	107.13000	107.13000	2026-03-16
317	28	16	IT0005530032	103.26000	103.26000	2026-03-16
318	29	17	IT0005421703	74.67000	74.67000	2026-03-16
319	30	18	IT0005635583	97.70000	97.70000	2026-03-16
320	31	19	IT0004532559	110.80000	110.80000	2026-03-16
321	32	20	IT0005377152	90.48000	90.48000	2026-03-16
322	33	21	IT0005582421	101.74000	101.74000	2026-03-16
323	34	22	IT0005442097	78.80000	78.80000	2026-03-16
324	35	23	IT0004286966	110.93000	110.93000	2026-03-16
325	36	24	IT0005321325	90.79000	90.79000	2026-03-16
326	37	25	IT0005496770	94.20000	94.20000	2026-03-16
327	38	26	IT0005596470	102.12000	102.12000	2026-03-16
328	39	27	IT0005433195	74.82000	74.82000	2026-03-16
329	40	28	IT0003934657	102.35000	102.35000	2026-03-16
330	41	29	IT0005177909	87.34000	87.34000	2026-03-16
\.


--
-- Data for Name: issuer; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.issuer (id_issuer, short_issuer_name, long_issuer_name, country) FROM stdin;
2	REP ITA	REPUBBLICA ITALIANA	332
3	MEDIOBANCA	MEDIOBANCA BANCA DI CREDITO FINANZIARIO SOCIETA  PER AZIONI	332
4	MARKET	MARKET	332
\.


--
-- Data for Name: loan_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.loan_master_data (id_master_data, description, processing_fees, management_fees, incidental_expenses, default_interest, late_payment_fee, underwriting_fee, insurance_premium, tax_charges) FROM stdin;
\.


--
-- Data for Name: market_segment; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.market_segment (id_market_segment, code, description) FROM stdin;
2	CB	Currency bonds
\.


--
-- Data for Name: master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.master_data (id_master_data, code, currency, calendar, issue_date, maturity_date, type_of_interest, form, daycount, frequency, roll_convention, accrual_schedule_type, interest_rate, issue_price, redempion_price, business_days, asset_class, amortization_schedule) FROM stdin;
70	IT0001	2	3	2026-03-08	2026-09-08	7	4	10	100	10	100	0.0000000000	100.00000	100.00000	2	15	4
77	EURCAD	2	3	2026-04-11	2026-04-11	7	4	8	100	10	100	0.0000000000	0.00000	0.00000	2	16	4
78	EURAUD	2	3	2026-04-11	2026-04-11	7	4	8	100	10	100	0.0000000000	0.00000	0.00000	2	16	4
79	EURJPY	2	3	2026-04-11	2026-04-11	7	4	8	100	10	100	0.0000000000	0.00000	0.00000	2	16	4
80	IT0005583643	2	3	2024-02-19	2037-02-19	7	4	10	7	6	100	0.0730000000	100.00000	100.00000	2	11	4
81	ZBM6	5	3	2025-09-22	2026-05-22	7	4	10	100	10	100	0.0000000000	100.00000	100.00000	2	15	4
82	6EM6	5	3	2021-06-15	2026-06-15	7	4	8	100	10	100	0.0000000000	100.00000	100.00000	2	18	4
83	6SM6	5	3	2021-06-15	2026-06-15	7	4	8	100	10	100	0.0000000000	100.00000	100.00000	2	18	4
84	ESRM6	5	3	2023-03-17	2026-06-15	7	4	8	100	10	100	0.0000000000	100.00000	100.00000	2	19	4
48	IT0005684888	2	3	2025-12-12	2026-12-14	7	4	10	7	10	100	0.0000000000	97.82500	100.00000	2	9	6
49	IT0005678492	2	3	2025-11-14	2026-11-13	7	4	10	7	10	100	0.0000000000	97.95700	100.00000	2	9	6
71	IT0002	2	3	2026-01-01	2031-01-01	7	4	10	8	10	100	0.0300000000	100.00000	100.00000	2	11	4
50	IT0005692485	2	3	2026-01-30	2026-07-31	7	4	10	7	10	100	0.0000000000	98.97900	100.00000	2	9	6
51	IT0005674335	2	3	2025-10-14	2026-10-14	7	4	10	7	10	100	0.0000000000	97.96400	100.00000	2	9	6
52	IT0005669269	2	3	2025-09-12	2026-09-14	7	4	10	7	10	100	0.0000000000	97.97200	100.00000	2	9	6
53	IT0005680639	2	3	2025-11-28	2026-05-29	7	4	10	7	10	100	0.0000000000	98.98100	100.00000	2	9	6
54	IT0005666851	2	3	2025-08-14	2026-08-14	7	4	10	7	10	100	0.0000000000	98.00100	100.00000	2	9	6
55	IT0005650574	2	3	2025-05-14	2026-05-14	7	4	10	7	10	100	0.0000000000	98.05200	100.00000	2	9	6
56	IT0005655037	2	3	2025-06-13	2026-06-12	7	4	10	7	10	100	0.0000000000	98.03400	100.00000	2	9	6
57	IT0005660029	2	3	2025-07-14	2026-07-14	7	4	10	7	10	100	0.0000000000	98.05100	100.00000	2	9	6
58	IT0005645509	2	3	2025-04-14	2026-04-14	7	4	10	7	10	100	0.0000000000	97.89400	100.00000	2	9	6
1	IT0005668238	2	3	2025-09-09	2055-10-01	7	4	10	8	10	100	4.6500000000	99.56000	100.00000	2	11	6
2	IT0005611741	2	3	2024-09-17	2054-10-01	7	4	10	8	10	100	4.3000000000	99.78900	100.00000	2	11	6
44	IT0005640666	2	3	2025-03-14	2026-03-13	7	4	10	7	10	100	0.0000000000	97.69200	100.00000	2	9	6
3	IT0005534141	2	3	2023-02-23	2053-10-01	7	4	10	8	10	100	4.5000000000	99.55900	100.00000	2	11	6
4	IT0005217390	2	3	2016-09-01	2067-03-01	7	4	10	8	10	100	2.8000000000	99.19000	100.00000	2	11	6
5	IT0005480980	2	3	2022-01-12	2052-09-01	7	4	10	8	10	100	2.1500000000	99.98700	100.00000	2	11	6
6	IT0005425233	2	3	2020-09-01	2051-09-01	7	4	10	8	10	100	1.7000000000	98.68600	100.00000	2	11	6
7	IT0005398406	2	3	2022-01-22	2050-09-01	7	4	10	8	10	100	2.4500000000	99.28000	100.00000	2	11	6
8	IT0005363111	2	3	2019-02-13	2049-09-01	7	4	10	8	10	100	3.8500000000	99.59400	100.00000	2	11	6
9	IT0005273013	2	3	2017-03-01	2048-03-01	7	4	10	8	10	100	3.4500000000	98.95600	100.00000	2	11	6
10	IT0005162828	2	3	2016-02-09	2047-03-01	7	4	10	8	10	100	2.7000000000	99.18000	100.00000	2	11	6
11	IT0005441883	2	3	2021-03-01	2072-03-01	7	4	10	8	10	100	2.1500000000	99.46700	100.00000	2	11	6
12	IT0005083057	2	3	2015-01-22	2046-09-01	7	4	10	8	10	100	3.2500000000	99.71000	100.00000	2	11	6
13	IT0005631608	2	3	2025-01-15	2046-04-30	7	4	10	8	10	100	4.1000000000	99.46500	100.00000	2	11	6
14	IT0005438004	2	3	2020-10-30	2045-04-30	7	4	10	8	10	100	1.5000000000	99.16800	100.00000	2	11	6
15	IT0004923998	2	3	2013-03-01	2044-09-01	7	4	10	8	10	100	4.7500000000	97.22100	100.00000	2	11	6
16	IT0005530032	2	3	2022-09-01	2043-09-01	7	4	10	8	10	100	4.4500000000	99.60600	100.00000	2	11	6
17	IT0005421703	2	3	2020-09-11	2041-03-01	7	4	10	8	10	100	1.8000000000	99.76500	100.00000	2	11	6
18	IT0005635583	2	3	2025-02-18	2040-10-01	7	4	10	8	10	100	3.8500000000	99.37500	100.00000	2	11	6
19	IT0004532559	2	3	2009-09-01	2040-09-01	7	4	10	8	10	100	5.0000000000	98.18600	100.00000	2	11	6
20	IT0005377152	2	3	2019-06-19	2040-03-01	7	4	10	8	10	100	3.1000000000	99.62300	100.00000	2	11	6
21	IT0005582421	2	3	2023-10-01	2039-10-01	7	4	10	8	10	100	4.1500000000	99.68000	100.00000	2	11	6
22	IT0005442097	2	3	2021-04-27	2037-04-27	7	4	10	8	10	100	0.0000000000	100.00000	100.00000	2	11	6
23	IT0004286966	2	3	2007-08-01	2039-08-01	7	4	10	8	10	100	5.0000000000	99.98000	100.00000	2	11	6
24	IT0005321325	2	3	2017-09-01	2038-09-01	7	4	10	8	10	100	2.9500000000	99.76600	100.00000	2	11	6
25	IT0005496770	2	3	2022-03-01	2038-03-01	7	4	10	8	10	100	3.2500000000	99.65100	100.00000	2	11	6
26	IT0005596470	2	3	2024-04-30	2037-10-30	7	4	10	8	10	100	4.0500000000	99.86500	100.00000	2	11	6
27	IT0005433195	2	3	2021-01-12	2037-03-01	7	4	10	8	10	100	0.9500000000	99.40900	100.00000	2	11	6
28	IT0003934657	2	3	2005-08-01	2037-02-01	7	4	10	8	10	100	4.0000000000	101.28900	100.00000	2	11	6
29	IT0005177909	2	3	2016-03-01	2036-09-01	7	4	10	8	10	100	2.2500000000	99.36800	100.00000	2	11	6
30	IT0005402117	2	3	2020-02-18	2036-03-01	7	4	10	8	10	100	1.4500000000	100.00000	100.00000	2	11	6
31	IT0005676504	2	3	2025-11-03	2036-02-01	7	4	10	8	10	100	0.0000000000	100.19000	100.00000	2	11	6
32	IT0005648149	2	3	2025-05-02	2035-10-01	7	4	10	8	10	100	3.6000000000	100.09000	100.00000	2	11	6
33	IT0005631590	2	3	2025-01-15	2035-08-01	7	4	10	8	10	100	3.6500000000	99.57700	100.00000	2	11	6
34	IT0005607970	2	3	2024-08-01	2035-02-01	7	4	10	8	10	100	3.8500000000	101.04000	100.00000	2	11	6
35	IT0005508590	2	3	2022-09-13	2035-04-30	7	4	10	8	10	100	4.0000000000	99.73400	100.00000	2	11	6
36	IT0005358806	2	3	2019-01-22	2035-03-01	7	4	10	8	10	100	3.3500000000	99.60900	100.00000	2	11	6
37	IT0005466351	2	3	2021-11-16	2033-11-16	7	4	10	8	10	100	0.0000000000	100.00000	100.00000	2	11	6
38	IT0005584856	2	3	2024-03-01	2034-07-01	7	4	10	8	10	100	3.8500000000	99.80000	100.00000	2	11	6
39	IT0003535157	2	3	2003-08-01	2034-08-01	7	4	10	8	10	100	5.0000000000	98.10600	100.00000	2	11	6
40	IT0005634800	2	3	2025-02-25	2033-02-25	7	4	10	8	10	100	0.0000000000	100.00000	100.00000	2	11	6
41	IT0005560948	2	3	2023-09-01	2034-03-01	7	4	10	8	10	100	4.2000000000	100.00000	100.00000	2	11	6
42	IT0005544082	2	3	2023-05-02	2033-11-01	7	4	10	8	10	100	4.3500000000	99.85000	100.00000	2	11	6
43	IT0005240350	2	3	2017-01-25	2033-09-01	7	4	10	8	10	100	2.4500000000	99.13000	100.00000	2	11	6
45	IT0005689887	2	3	2026-01-14	2027-01-14	7	4	10	7	10	100	0.0000000000	97.90400	100.00000	2	9	6
46	IT0005670895	2	3	2025-09-30	2026-03-31	7	4	10	7	10	100	0.0000000000	98.97700	100.00000	2	9	6
47	IT0005695256	2	3	2026-02-13	2027-02-12	7	4	10	7	10	100	0.0000000000	97.95200	100.00000	2	9	6
60	IT0005689960	2	3	2026-01-15	2029-03-15	7	4	10	8	10	100	0.0000000000	99.82000	100.00000	2	11	6
61	IT0005620346	5	3	2024-12-16	2030-12-16	7	4	10	8	10	100	4.3500000000	100.00000	100.00000	2	11	6
62	IT0001200390	2	3	1998-02-17	2028-02-17	7	4	10	8	10	100	0.0000000000	18.65000	100.00000	2	11	6
63	IT0003256820	2	3	2002-01-01	2033-02-01	7	4	10	8	10	100	5.7500000000	101.15000	100.00000	2	11	6
64	IT0005599904	2	3	2024-06-17	2027-07-15	7	4	10	8	10	100	3.4500000000	100.04000	100.00000	2	11	6
59	IT0001086567	2	3	1996-11-01	2026-11-01	7	4	10	8	10	100	7.2500000000	99.45000	100.00000	2	11	6
65	IT0005416570	2	3	2020-07-16	2027-09-15	7	4	10	8	10	100	0.9500000000	100.03000	100.00000	2	11	6
67	IT0024832682	2	3	2026-03-06	2026-06-10	7	4	10	100	10	100	0.0000000000	100.00000	100.00000	2	15	6
69	EURUSD	2	3	2026-03-21	2026-03-21	7	4	10	100	10	100	0.0000000000	0.00000	0.00000	2	16	6
74	EURCHF	2	3	2026-04-11	2026-04-11	7	4	8	100	10	100	0.0000000000	0.00000	0.00000	2	16	4
\.


--
-- Data for Name: mm_future_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.mm_future_master_data (id_master_data, underlying, contract_value, tick_size, initial_margin, maintenance_margin) FROM stdin;
84	69	250000.00000	0.02000	300.00000	200.00000
\.


--
-- Data for Name: portfolio_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.portfolio_master_data (id_portfolio, currency, code, description) FROM stdin;
2	2	P0001	Portfolio Test
3	5	P0002	Port test 2
\.


--
-- Data for Name: position_detail; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.position_detail (id_position_detail, position_md, master_data, realized_pnl, unrealized_pnl, avg_price, market_value, net_quantity) FROM stdin;
\.


--
-- Data for Name: position_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.position_master_data (id_position, currency, code, description, portfolio) FROM stdin;
2	2	P0001	Position Test	2
\.


--
-- Data for Name: roll_convention; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.roll_convention (id_roll_convention, code, description) FROM stdin;
6	PREVIOUS	Previus
7	PREVIOUS-MODIFIED	Previus Following
8	FORWARD	Following
9	FORWARD-MODIFIED	Modified Following
10	RC_NONE	None
\.


--
-- Data for Name: security_master_data; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.security_master_data (id_master_data, isin, cfi_code, fisn, lei, issuer, issue_description, nominal_value, first_coupon_rate, first_coupon_payment_date) FROM stdin;
8	IT0005363111	DBFTFB	ITALIA/3.85 BTP 20490901	815600DE60799F5A9309	2	ITALIA/3.85 BTP 20490901 - BUONI DEL TESORO POLIENNALI	13342.0000000000	1.9250000000	2019-03-01
9	IT0005273013	DBFTFB	ITALIA/3.45 BTP 20480301	815600DE60799F5A9309	2	ITALIA/3.45 BTP 20480301 - BUONI DEL TESORO POLIENNALI	500000000.0000000000	1.7250000000	2017-09-01
10	IT0005162828	DBFTFB	ITALIA/2.7 BTP 20470301	815600DE60799F5A9309	2	ITALIA/2.7 BTP 20470301 - BUONI DEL TESORO POLIENNALI	9000000000.0000000000	0.1557700000	2016-03-01
11	IT0005441883	DBFTFB	ITALIA/2.15 BTP 20720301	815600DE60799F5A9309	2	ITALIA/2.15 BTP 20720301 - BUONI DEL TESORO POLIENNALI	5000.0000000000	1.0750000000	2021-09-01
13	IT0005631608	DBFTFB	ITALIA/4.1 BTP 20460430	815600DE60799F5A9309	2	ITALIA/4.1 BTP 20460430 - BUONI DEL TESORO POLIENNALI	5000.0000000000	1.1826900000	2025-04-30
14	IT0005438004	DBFTFB	ITALIA/1.5 BTP 20450430	815600DE60799F5A9309	2	ITALIA/1.5 BTP 20450430 - BUONI DEL TESORO POLIENNALI	8500.0000000000	0.7500000000	2021-04-30
15	IT0004923998	DBFTFB	ITALIA/4.75 BTP 20440901	815600DE60799F5A9309	2	ITALIA/4.75 BTP 20440901 - BUONI DEL TESORO POLIENNALI	6000000000.0000000000	2.3750000000	2013-09-01
16	IT0005530032	DBFTFB	ITALIA/4.45 BTP 20430901	815600DE60799F5A9309	2	ITALIA/4.45 BTP 20430901 - BUONI DEL TESORO POLIENNALI	7000.0000000000	2.2250000000	2023-03-01
17	IT0005421703	DBFTFB	ITALIA/1.8 BTP 20410301	815600DE60799F5A9309	2	ITALIA/1.8 BTP 20410301 - BUONI DEL TESORO POLIENNALI	10000.0000000000	0.8303900000	2021-03-01
18	IT0005635583	DBFTFB	ITALIA/3.85 BTP 20401001	815600DE60799F5A9309	2	ITALIA/3.85 BTP 20401001 - BUONI DEL TESORO POLIENNALI	13000.0000000000	0.4442300000	2025-04-01
19	IT0004532559	DBFTFB	ITALIA/5 BTP 20400901	815600DE60799F5A9309	2	ITALIA/5 BTP 20400901 - BUONI DEL TESORO POLIENNALI	0.0000000000	2.5000000000	2010-03-01
20	IT0005377152	DBFTFB	ITALIA/3.1 BTP 20400301	815600DE60799F5A9309	2	ITALIA/3.1 BTP 20400301 - BUONI DEL TESORO POLIENNALI	69699.0000000000	1.5500000000	2019-09-01
21	IT0005582421	DBFTFB	ITALIA/4.15 BTP 20391001	815600DE60799F5A9309	2	ITALIA/4.15 BTP 20391001 - BUONI DEL TESORO POLIENNALI	10000.0000000000	2.0750000000	2024-04-01
22	IT0005442097	DBVTFB	ITALIA/TV BTP FUTURA 20370427	815600DE60799F5A9309	2	ITALIA/TV BTP FUTURA 20370427 EX - BTP FUTURA	0.0000000000	0.3750000000	2021-10-27
23	IT0004286966	DBFTFB	ITALIA/5 BTP 20390801	815600DE60799F5A9309	2	ITALIA/5 BTP 20390801 - BUONI DEL TESORO POLIENNALI	0.0000000000	2.5000000000	2008-02-01
24	IT0005321325	DBFTFB	ITALIA/2.95 BTP 20380901	815600DE60799F5A9309	2	ITALIA/2.95 BTP 20380901 - BUONI DEL TESORO POLIENNALI	0.0000000000	1.4750000000	2018-03-01
25	IT0005496770	DBFTFB	ITALIA/3.25 BTP 20380301	815600DE60799F5A9309	2	ITALIA/3.25 BTP 20380301 - BUONI DEL TESORO POLIENNALI	5000.0000000000	1.6250000000	2022-09-01
26	IT0005596470	DBFTFB	ITALIA/4.05 BTP 20371030	815600DE60799F5A9309	2	ITALIA/4.05 BTP 20371030 - BUONI DEL TESORO POLIENNALI	9000.0000000000	2.0250000000	2024-10-30
27	IT0005433195	DBFTFB	ITALIA/0.95 BTP 20370301	815600DE60799F5A9309	2	ITALIA/0.95 BTP 20370301 - BUONI DEL TESORO POLIENNALI	10000000000.0000000000	0.1259700000	2021-03-01
28	IT0003934657	DBFTFB	ITALIA/4 BTP 20370201	815600DE60799F5A9309	2	ITALIA/4 BTP 20370201 - BUONI DEL TESORO POLIENNALI	0.0000000000	2.0000000000	2006-02-01
29	IT0005177909	DBFTFB	ITALIA/2.25 BTP 20360901	815600DE60799F5A9309	2	ITALIA/2.25 BTP 20360901 - BUONI DEL TESORO POLIENNALI	500000000.0000000000	1.1250000000	2016-09-01
30	IT0005402117	DBFTFB	ITALIA/1.45 BTP 20360301	815600DE60799F5A9309	2	ITALIA/1.45 BTP 20360301 - BUONI DEL TESORO POLIENNALI	14400.0000000000	0.7728000000	2020-09-01
31	IT0005676504	DBFTFB	ITALIA/3.45 BTP 20360201	815600DE60799F5A9309	2	ITALIA/3.45 BTP 20360201 - BUONI DEL TESORO POLIENNALI	4500.0000000000	0.8437500000	2026-02-01
32	IT0005648149	DBFTFB	ITALIA/3.6 BTP 20351001	815600DE60799F5A9309	2	ITALIA/3.6 BTP 20351001 - BUONI DEL TESORO POLIENNALI	4000.0000000000	1.4950800000	2025-10-01
33	IT0005631590	DBFTFB	ITALIA/3.65 BTP 20350801	815600DE60799F5A9309	2	ITALIA/3.65 BTP 20350801 - BUONI DEL TESORO POLIENNALI	13000.0000000000	0.1686100000	2025-02-01
34	IT0005607970	DBFTFB	ITALIA/3.85 BTP 20350201	815600DE60799F5A9309	2	ITALIA/3.85 BTP 20350201 - BUONI DEL TESORO POLIENNALI	4500.0000000000	1.9250000000	2024-08-01
35	IT0005508590	DBFTFB	ITALIA/4 BTP 20350430	815600DE60799F5A9309	2	ITALIA/4 BTP 20350430 - BUONI DEL TESORO POLIENNALI	8000.0000000000	2.0000000000	2022-10-30
36	IT0005358806	DBFTFB	ITALIA/3.35 BTP 20350301	815600DE60799F5A9309	2	ITALIA/3.35 BTP 20350301 - BUONI DEL TESORO POLIENNALI	14800.0000000000	1.6750000000	2019-03-01
37	IT0005466351	DBVTFB	ITALIA/TV BTP FUTURA 20331116	815600DE60799F5A9309	2	ITALIA/TV BTP FUTURA 20331116 EX - BTP FUTURA	3268.2400000000	0.3750000000	2022-05-16
38	IT0005584856	DBFTFB	ITALIA/3.85 BTP 20340701	815600DE60799F5A9309	2	ITALIA/3.85 BTP 20340701 - BUONI DEL TESORO POLIENNALI	4500.0000000000	1.2903900000	2024-07-01
39	IT0003535157	DBFTFB	ITALIA/5 BTP 20340801	815600DE60799F5A9309	2	ITALIA/5 BTP 20340801 - BUONI DEL TESORO POLIENNALI	0.0000000000	2.5000000000	2004-02-01
40	IT0005634800	DBVTFB	ITALIA/TV BTP PIU 20330225	815600DE60799F5A9309	2	ITALIA/TV BTP PIU 20330225 - BTP PIU	0.0000000000	0.7125000000	2025-05-25
41	IT0005560948	DBFTFB	ITALIA/4.2 BTP 20340301	815600DE60799F5A9309	2	ITALIA/4.2 BTP 20340301 - BUONI DEL TESORO POLIENNALI	4875.0000000000	2.1000000000	2024-03-01
42	IT0005544082	DBFTFB	ITALIA/4.35 BTP 20331101	815600DE60799F5A9309	2	ITALIA/4.35 BTP 20331101 - BUONI DEL TESORO POLIENNALI	5000.0000000000	2.1631800000	2023-11-01
43	IT0005240350	DBFTFB	ITALIA/2.45 BTP 20330901	815600DE60799F5A9309	2	ITALIA/2.45 BTP 20330901 - BUONI DEL TESORO POLIENNALI	6000000000.0000000000	1.2250000000	2017-03-01
45	IT0005689887	DYZTXB	ITALIA/ZC BOT 20270114	815600DE60799F5A9309	2	ITALIA/ZC BOT 20270114 - BUONI DEL TESORO ORDINARI	8800.0000000000	0.0000000000	2026-01-14
46	IT0005670895	DYZTXB	ITALIA/ZC BOT 20260331	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260331 - BUONI DEL TESORO ORDINARI	6500.0000000000	0.0000000000	2025-09-30
2	IT0005611741	DBFTFB	ITALIA/4.3 BTP 20541001	815600DE60799F5A9309	2	ITALIA/4.3 BTP 20541001 - BUONI DEL TESORO POLIENNALI	8000.0000000000	0.1644800000	2024-10-01
44	IT0005640666	DYZTXB	ITALIA/ZC BOT 20260313	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260313 - BUONI DEL TESORO ORDINARI	9000.0000000000	0.0000000000	2025-03-14
3	IT0005534141	DBFTFB	ITALIA/4.5 BTP 20531001	815600DE60799F5A9309	2	ITALIA/4.5 BTP 20531001 - BUONI DEL TESORO POLIENNALI	5000.0000000000	0.4574200000	2023-04-01
4	IT0005217390	DBFTFB	ITALIA/2.8 BTP 20670301	815600DE60799F5A9309	2	ITALIA/2.8 BTP 20670301 - BUONI DEL TESORO POLIENNALI	5000000000.0000000000	1.4000000000	2017-03-01
5	IT0005480980	DBFTFB	ITALIA/2.15 BTP 20520901	815600DE60799F5A9309	2	ITALIA/2.15 BTP 20520901 - BUONI DEL TESORO POLIENNALI	7000.0000000000	0.2850800000	2022-03-01
6	IT0005425233	DBFTFB	ITALIA/1.7 BTP 20510901	815600DE60799F5A9309	2	ITALIA/1.7 BTP 20510901 - BUONI DEL TESORO POLIENNALI	8000.0000000000	0.8500000000	2021-03-01
7	IT0005398406	DBFTFB	ITALIA/2.45 BTP 20500901	815600DE60799F5A9309	2	ITALIA/2.45 BTP 20500901 - BUONI DEL TESORO POLIENNALI	15835.0000000000	1.2250000000	2020-03-01
55	IT0005650574	DYZTXB	ITALIA/ZC BOT 20260514	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260514 - BUONI DEL TESORO ORDINARI	8500.0000000000	0.0000000000	2025-05-14
56	IT0005655037	DYZTXB	ITALIA/ZC BOT 20260612	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260612 - BUONI DEL TESORO ORDINARI	9350.0000000000	0.0000000000	2025-06-13
57	IT0005660029	DYZTXB	ITALIA/ZC BOT 20260714	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260714 - BUONI DEL TESORO ORDINARI	7500.0000000000	0.0000000000	2025-07-14
58	IT0005645509	DYZTXB	ITALIA/ZC BOT 20260414	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260414 - BUONI DEL TESORO ORDINARI	7700.0000000000	0.0000000000	2025-04-14
1	IT0005668238	DBFTFB	ITALIA/4.65 BTP 20551001	815600DE60799F5A9309	2	ITALIA/4.65 BTP 20551001 - BUONI DEL TESORO POLIENNALI	5000.0000000000	0.2795100000	2025-10-01
12	IT0005083057	DBFTFB	ITALIA/3.25 BTP 20460901	815600DE60799F5A9309	2	ITALIA/3.25 BTP 20460901 - BUONI DEL TESORO POLIENNALI	13240700000.0000000000	1.6250000000	2015-03-01
47	IT0005695256	DYZTXB	ITALIA/ZC BOT 20270212	815600DE60799F5A9309	2	ITALIA/ZC BOT 20270212 - BUONI DEL TESORO ORDINARI	9350.0000000000	0.0000000000	2026-02-13
48	IT0005684888	DYZTXB	ITALIA/ZC BOT 20261214	815600DE60799F5A9309	2	ITALIA/ZC BOT 20261214 - BUONI DEL TESORO ORDINARI	9900.0000000000	0.0000000000	2025-12-12
49	IT0005678492	DYZTXB	ITALIA/ZC BOT 20261113	815600DE60799F5A9309	2	ITALIA/ZC BOT 20261113 - BUONI DEL TESORO ORDINARI	8500.0000000000	0.0000000000	2025-11-14
50	IT0005692485	DYZTXB	ITALIA/ZC BOT 20260731	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260731 - BUONI DEL TESORO ORDINARI	8250.0000000000	0.0000000000	2026-01-30
51	IT0005674335	DYZTXB	ITALIA/ZC BOT 20261014	815600DE60799F5A9309	2	ITALIA/ZC BOT 20261014 - BUONI DEL TESORO ORDINARI	9900.0000000000	0.0000000000	2025-10-14
52	IT0005669269	DYZTXB	ITALIA/ZC BOT 20260914	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260914 - BUONI DEL TESORO ORDINARI	9000.0000000000	0.0000000000	2025-09-12
53	IT0005680639	DYZTXB	ITALIA/ZC BOT 20260529	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260529 - BUONI DEL TESORO ORDINARI	7520.0000000000	0.0000000000	2025-11-28
54	IT0005666851	DYZTXB	ITALIA/ZC BOT 20260814	815600DE60799F5A9309	2	ITALIA/ZC BOT 20260814 - BUONI DEL TESORO ORDINARI	8000.0000000000	0.0000000000	2025-08-14
59	IT0001086567	DBFTFB	ITALIA/7.25 BTP 20261101	815600DE60799F5A9309	2	ITALIA/7.25 BTP 20261101 - BUONI DEL TESORO POLIENNALI	0.0000000000	3.6250000000	1997-05-01
60	IT0005689960	DBFTFB	ITALIA/2.4 BTP 20290315	815600DE60799F5A9309	2	ITALIA/2.4 BTP 20290315 - BUONI DEL TESORO POLIENNALI	4000.0000000000	0.3911600000	2026-03-15
61	IT0005620346	DTFUFB	MEDIOBANCA/4.35 OB 20301216 SR657	PSNL19R2RXX5U3QWHI44	2	MEDIOBANCA/4.35 OB 20301216 SR657 - OBBLIGAZIONI ORDINARIE	16.0000000000	4.3500000000	2025-12-16
62	IT0001200390	DBZUFB	B INTESA/ZC OB 20280217	2W8N8UU78PMDQKZENC08	2	B INTESA/ZC OB 20280217 - OBBLIGAZIONI ORDINARIE	0.0000000000	0.0000000000	1998-02-17
63	IT0003256820	DBFTFB	ITALIA/5.75 BTP 20330201	815600DE60799F5A9309	2	ITALIA/5.75 BTP 20330201 - BUONI DEL TESORO POLIENNALI	0.0000000000	2.8750000000	2002-08-01
64	IT0005599904	DBFTFB	ITALIA/3.45 BTP 20270715	815600DE60799F5A9309	2	ITALIA/3.45 BTP 20270715 - BUONI DEL TESORO POLIENNALI	4200.0000000000	0.2653900000	2024-07-15
65	IT0005416570	DBFTFB	ITALIA/0.95 BTP 20270915	815600DE60799F5A9309	2	ITALIA/0.95 BTP 20270915 - BUONI DEL TESORO POLIENNALI	21832.6970000000	0.1574700000	2020-09-15
71	IT0002				4	TEST BOND	100.0000000000	0.0150000000	2026-07-01
80	IT0005583643				4	Obbligazioni Senior con Tasso da Fisso a Variabile e scadenza il 19 febbraio 2037	100.0000000000	0.0730000000	2025-02-19
\.


--
-- Data for Name: settlement_type; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.settlement_type (id_settlement_type, code, description) FROM stdin;
1	PHYSICAL	Physical Settlement
2	CASH	Cash Settlement
\.


--
-- Data for Name: super_class; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.super_class (id_super_class, code, description) FROM stdin;
4	EQU	Equities
5	FIN	Fixed Income
6	CCE	 Cash and Cash Equivalents (Money Market)
7	DER	 Derivatives
8	FX	 Forex
\.


--
-- Data for Name: txn_status; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.txn_status (id_txn_status, code, description) FROM stdin;
1	PENDING	Pending
2	VALIDATING	Validating
3	EXECUTED	Executed
4	SETTLED	Settled
5	REJECTED	Rejected
6	CANCELLED	Cancelled
\.


--
-- Data for Name: type_of_interest; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.type_of_interest (id_type_of_interest, code, description) FROM stdin;
7	FIXED	Fixed-Rate
8	FLOATING	Floating-Rate
9	ZERO-COUPON	Zero-Coupon
10	INFLATION	Inflation-Linked
11	CONVERTIBLE	Convertible
12	CALLABLE	Callable
100	NONE	None
\.


--
-- Data for Name: yield_curve; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.yield_curve (id_yield_curve, code, description, currency, calendar, compounding) FROM stdin;
2	ECBYC	European Central Bank Yield Curve	2	3	2
3	ITAYC	Investing.com - Italy - Government Bonds	2	3	3
4	USAYC	Investing.com - United States - Government Bonds	5	4	3
5	EURIBOR	Euribor Rates	2	3	2
6	EURIRS	Eurirs Rates	2	3	3
7	SOFR	SOFR Averages Rates	5	4	2
8	ESTER	ESTER Averages Rates	2	3	2
\.


--
-- Data for Name: yield_curve_item; Type: TABLE DATA; Schema: public; Owner: easypricer
--

COPY public.yield_curve_item (id_yield_curve_item, yield_curve, ric, offset_type, offset_value, bid, ask) FROM stdin;
307	5	EURIBOR 1W	0	7	0.01918	0.01918
308	5	EURIBOR 1M	1	1	0.01909	0.01909
310	5	EURIBOR 6M	1	6	0.02458	0.02458
311	5	EURIBOR 1Y	2	1	0.02799	0.02799
312	6	EUR 01A Irs	2	1	0.02730	0.02730
316	6	EUR 05A Irs	2	5	0.02860	0.02860
318	6	EUR 07A Irs	2	7	0.02940	0.02940
319	6	EUR 08A Irs	2	8	0.02980	0.02980
320	6	EUR 09A Irs	2	9	0.03010	0.03010
323	6	EUR 12A Irs	2	12	0.03130	0.03130
324	6	EUR 15A Irs	2	15	0.03200	0.03200
325	6	EUR 20A Irs	2	20	0.03220	0.03220
326	6	EUR 25A Irs	2	25	0.03180	0.03180
328	6	EUR 40A Irs	2	40	0.02970	0.02970
329	6	EUR 50A Irs	2	50	0.02810	0.02810
250	2	SR_12Y	2	12	0.03338	0.03338
251	2	SR_13Y	2	13	0.03423	0.03423
252	2	SR_14Y	2	14	0.03498	0.03498
248	2	SR_10Y	2	10	0.03140	0.03140
253	2	SR_15Y	2	15	0.03565	0.03565
254	2	SR_16Y	2	16	0.03624	0.03624
255	2	SR_17Y	2	17	0.03675	0.03675
256	2	SR_18Y	2	18	0.03719	0.03719
236	2	SR_3M	1	3	0.02016	0.02016
237	2	SR_6M	1	6	0.02012	0.02012
238	2	SR_9M	1	9	0.02014	0.02014
257	2	SR_19Y	2	19	0.03757	0.03757
239	2	SR_1Y	2	1	0.02022	0.02022
240	2	SR_2Y	2	2	0.02091	0.02091
241	2	SR_3Y	2	3	0.02204	0.02204
242	2	SR_4Y	2	4	0.02339	0.02339
243	2	SR_5Y	2	5	0.02484	0.02484
269	3	Italy 1M	1	1	0.02014	0.02014
444	4	U.S. 1M	1	1	0.03686	0.03686
445	4	U.S. 2M	1	2	0.03694	0.03694
446	4	U.S. 3M	1	3	0.03703	0.03703
447	4	U.S. 4M	1	4	0.03717	0.03717
448	4	U.S. 6M	1	6	0.03719	0.03719
449	4	U.S. 1Y	2	1	0.03713	0.03713
450	4	U.S. 2Y	2	2	0.03846	0.03846
271	3	Italy 6M	1	6	0.02439	0.02439
452	4	U.S. 5Y	2	5	0.03987	0.03987
453	4	U.S. 7Y	2	7	0.04168	0.04168
273	3	Italy 1Y	2	1	0.02646	0.02646
455	4	U.S. 20Y	2	20	0.04920	0.04920
275	3	Italy 3Y	2	3	0.02985	0.02985
284	3	Italy 20Y	2	20	0.04411	0.04411
286	3	Italy 30Y	2	30	0.04615	0.04615
456	4	U.S. 30Y	2	30	0.04911	0.04911
451	4	U.S. 3Y	2	3	0.03874	0.03874
270	3	Italy 3M	1	3	0.02108	0.02108
272	3	Italy 9M	1	9	0.02580	0.02580
274	3	Italy 2Y	2	2	0.02872	0.02872
276	3	Italy 4Y	2	4	0.03166	0.03166
277	3	Italy 5Y	2	5	0.03246	0.03246
278	3	Italy 6Y	2	6	0.03438	0.03438
279	3	Italy 7Y	2	7	0.03528	0.03528
280	3	Italy 8Y	2	8	0.03686	0.03686
281	3	Italy 9Y	2	9	0.03827	0.03827
282	3	Italy 10Y	2	10	0.03857	0.03857
283	3	Italy 15Y	2	15	0.04275	0.04275
285	3	Italy 25Y	2	25	0.04491	0.04491
287	3	Italy 50Y	2	50	0.04262	0.04262
454	4	U.S. 10Y	2	10	0.04345	0.04345
309	5	EURIBOR 3M	1	3	0.02103	0.02103
313	6	EUR 02A Irs	2	2	0.02830	0.02830
314	6	EUR 03A Irs	2	3	0.02830	0.02830
315	6	EUR 04A Irs	2	4	0.02840	0.02840
317	6	EUR 06A Irs	2	6	0.02900	0.02900
321	6	EUR 10A Irs	2	10	0.03060	0.03060
322	6	EUR 11A Irs	2	11	0.03090	0.03090
327	6	EUR 30A Irs	2	30	0.03120	0.03120
244	2	SR_6Y	2	6	0.02629	0.02629
245	2	SR_7Y	2	7	0.02769	0.02769
246	2	SR_8Y	2	8	0.02902	0.02902
247	2	SR_9Y	2	9	0.03026	0.03026
249	2	SR_11Y	2	11	0.03244	0.03244
258	2	SR_20Y	2	20	0.03790	0.03790
259	2	SR_21Y	2	21	0.03816	0.03816
260	2	SR_22Y	2	22	0.03838	0.03838
261	2	SR_23Y	2	23	0.03856	0.03856
262	2	SR_24Y	2	24	0.03869	0.03869
263	2	SR_25Y	2	25	0.03878	0.03878
264	2	SR_26Y	2	26	0.03884	0.03884
265	2	SR_27Y	2	27	0.03887	0.03887
266	2	SR_28Y	2	28	0.03887	0.03887
267	2	SR_29Y	2	29	0.03885	0.03885
268	2	SR_30Y	2	30	0.03880	0.03880
457	7	SOFR 1M	1	1	0.03661	0.03661
458	7	SOFR 3M	1	3	0.03679	0.03679
459	7	SOFR 6M	1	6	0.03695	0.03695
460	7	SOFR 1Y	2	1	0.03714	0.03714
\.


--
-- Name: accrual_schedule_type_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.accrual_schedule_type_s', 2, true);


--
-- Name: amortization_schedule_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.amortization_schedule_s', 6, true);


--
-- Name: asset_class_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.asset_class_s', 19, true);


--
-- Name: calendar_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.calendar_s', 4, true);


--
-- Name: cash_flow_item_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.cash_flow_item_s', 2253, true);


--
-- Name: counterparty_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.counterparty_s', 2, true);


--
-- Name: counterparty_type_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.counterparty_type_s', 6, true);


--
-- Name: country_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.country_s', 460, true);


--
-- Name: currency_pair_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.currency_pair_s', 6, true);


--
-- Name: currency_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.currency_s', 172, true);


--
-- Name: daycount_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.daycount_s', 10, true);


--
-- Name: deliverable_bonds_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.deliverable_bonds_s', 14, true);


--
-- Name: ec_exchange_rate_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.ec_exchange_rate_s', 1, false);


--
-- Name: finacial_txn_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.finacial_txn_s', 1, false);


--
-- Name: form_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.form_s', 6, true);


--
-- Name: frequency_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.frequency_s', 12, true);


--
-- Name: holiday_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.holiday_s', 36, true);


--
-- Name: instrument_quote_hist_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.instrument_quote_hist_s', 330, true);


--
-- Name: instrument_quote_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.instrument_quote_s', 74, true);


--
-- Name: issuer_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.issuer_s', 4, true);


--
-- Name: market_segment_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.market_segment_s', 2, true);


--
-- Name: master_data_code_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.master_data_code_s', 1, false);


--
-- Name: master_data_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.master_data_s', 84, true);


--
-- Name: portfolio_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.portfolio_master_data_s', 3, true);


--
-- Name: position_detail_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.position_detail_s', 1, false);


--
-- Name: position_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.position_master_data_s', 2, true);


--
-- Name: roll_convention_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.roll_convention_s', 10, true);


--
-- Name: settlement_type_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.settlement_type_s', 2, true);


--
-- Name: super_class_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.super_class_s', 8, true);


--
-- Name: txn_status_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.txn_status_s', 6, true);


--
-- Name: type_of_interest_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.type_of_interest_s', 12, true);


--
-- Name: yield_curve_item_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.yield_curve_item_s', 460, true);


--
-- Name: yield_curve_s; Type: SEQUENCE SET; Schema: public; Owner: easypricer
--

SELECT pg_catalog.setval('public.yield_curve_s', 8, true);


--
-- Name: accrual_schedule_type accrual_schedule_type_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.accrual_schedule_type
    ADD CONSTRAINT accrual_schedule_type_pkey PRIMARY KEY (id_accrual_schedule_type);


--
-- Name: amortization_schedule amortization_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.amortization_schedule
    ADD CONSTRAINT amortization_schedule_pkey PRIMARY KEY (id_amortization_schedule);


--
-- Name: asset_class asset_class_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT asset_class_pkey PRIMARY KEY (id_asset_class);


--
-- Name: bond_future_master_data bond_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.bond_future_master_data
    ADD CONSTRAINT bond_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: calendar calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (id_calendar);


--
-- Name: cash_flow_item cash_flow_item_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT cash_flow_item_pkey PRIMARY KEY (id_cash_flow_item);


--
-- Name: counterparty counterparty_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT counterparty_pkey PRIMARY KEY (id_counterparty);


--
-- Name: counterparty_type counterparty_type_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.counterparty_type
    ADD CONSTRAINT counterparty_type_pkey PRIMARY KEY (id_counterparty_type);


--
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id_country);


--
-- Name: currency_pair currency_pair_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currency_pair
    ADD CONSTRAINT currency_pair_pkey PRIMARY KEY (id_currency_pair);


--
-- Name: currency currency_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT currency_pkey PRIMARY KEY (id_currency);


--
-- Name: currpair_master_data currpair_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT currpair_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: daycount daycount_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.daycount
    ADD CONSTRAINT daycount_pkey PRIMARY KEY (id_daycount);


--
-- Name: deliverable_bonds deliverable_bonds_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT deliverable_bonds_pkey PRIMARY KEY (id_deliverable_bonds);


--
-- Name: ec_exchange_rate ec_exchange_rate_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.ec_exchange_rate
    ADD CONSTRAINT ec_exchange_rate_pkey PRIMARY KEY (id_ec_exchange_rate);


--
-- Name: finacial_txn finacial_txn_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.finacial_txn
    ADD CONSTRAINT finacial_txn_pkey PRIMARY KEY (id_finacial_txn);


--
-- Name: forex_master_data forex_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT forex_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: form form_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.form
    ADD CONSTRAINT form_pkey PRIMARY KEY (id_form);


--
-- Name: frequency frequency_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.frequency
    ADD CONSTRAINT frequency_pkey PRIMARY KEY (id_frequency);


--
-- Name: future_master_data future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.future_master_data
    ADD CONSTRAINT future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: fx_future_master_data fx_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fx_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: holiday holiday_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT holiday_pkey PRIMARY KEY (id_holiday);


--
-- Name: instrument_quote_hist instrument_quote_hist_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT instrument_quote_hist_pkey PRIMARY KEY (id_instrument_quote_hist);


--
-- Name: instrument_quote instrument_quote_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT instrument_quote_pkey PRIMARY KEY (id_instrument_quote);


--
-- Name: issuer issuer_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT issuer_pkey PRIMARY KEY (id_issuer);


--
-- Name: loan_master_data loan_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.loan_master_data
    ADD CONSTRAINT loan_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: market_segment market_segment_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.market_segment
    ADD CONSTRAINT market_segment_pkey PRIMARY KEY (id_market_segment);


--
-- Name: master_data master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: mm_future_master_data mm_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT mm_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: portfolio_master_data portfolio_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT portfolio_master_data_pkey PRIMARY KEY (id_portfolio);


--
-- Name: position_detail position_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT position_detail_pkey PRIMARY KEY (id_position_detail);


--
-- Name: position_master_data position_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT position_master_data_pkey PRIMARY KEY (id_position);


--
-- Name: roll_convention roll_convention_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.roll_convention
    ADD CONSTRAINT roll_convention_pkey PRIMARY KEY (id_roll_convention);


--
-- Name: security_master_data security_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT security_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: settlement_type settlement_type_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.settlement_type
    ADD CONSTRAINT settlement_type_pkey PRIMARY KEY (id_settlement_type);


--
-- Name: super_class super_class_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.super_class
    ADD CONSTRAINT super_class_pkey PRIMARY KEY (id_super_class);


--
-- Name: txn_status txn_status_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.txn_status
    ADD CONSTRAINT txn_status_pkey PRIMARY KEY (id_txn_status);


--
-- Name: type_of_interest type_of_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.type_of_interest
    ADD CONSTRAINT type_of_interest_pkey PRIMARY KEY (id_type_of_interest);


--
-- Name: yield_curve_item yield_curve_item_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT yield_curve_item_pkey PRIMARY KEY (id_yield_curve_item);


--
-- Name: yield_curve yield_curve_pkey; Type: CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT yield_curve_pkey PRIMARY KEY (id_yield_curve);


--
-- Name: alfa_2_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX alfa_2_code ON public.country USING btree (alfa_2_code);


--
-- Name: alfa_3_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX alfa_3_code ON public.country USING btree (alfa_3_code);


--
-- Name: idx_accrual_schedule_type_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_accrual_schedule_type_code ON public.accrual_schedule_type USING btree (code);


--
-- Name: idx_amortization_schedule_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_amortization_schedule_code ON public.amortization_schedule USING btree (code);


--
-- Name: idx_asset_class_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_asset_class_code ON public.asset_class USING btree (code);


--
-- Name: idx_calendar_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_calendar_code ON public.calendar USING btree (code);


--
-- Name: idx_counterparty_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_counterparty_code ON public.counterparty USING btree (code);


--
-- Name: idx_counterparty_type_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_counterparty_type_code ON public.counterparty_type USING btree (code);


--
-- Name: idx_country_numeric_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_country_numeric_code ON public.country USING btree (country_numeric_code);


--
-- Name: idx_currency_numeric_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_currency_numeric_code ON public.currency USING btree (currency_numeric_code);


--
-- Name: idx_currency_pair_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_currency_pair_code ON public.currency_pair USING btree (code);


--
-- Name: idx_currpair_bcy_ccy; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_currpair_bcy_ccy ON public.currpair_master_data USING btree (bcy, ccy);


--
-- Name: idx_daycount_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_daycount_code ON public.daycount USING btree (code);


--
-- Name: idx_deliverable_bonds_isin; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_deliverable_bonds_isin ON public.deliverable_bonds USING btree (master_data, isin);


--
-- Name: idx_forex_bcy_ccy; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_forex_bcy_ccy ON public.forex_master_data USING btree (bcy, ccy);


--
-- Name: idx_form_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_form_code ON public.form USING btree (code);


--
-- Name: idx_frequency_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_frequency_code ON public.frequency USING btree (code);


--
-- Name: idx_future_master_data_isin; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_future_master_data_isin ON public.future_master_data USING btree (isin);


--
-- Name: idx_instrument_quote_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_instrument_quote_code ON public.instrument_quote USING btree (code);


--
-- Name: idx_instrument_quote_hist_iqud; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_instrument_quote_hist_iqud ON public.instrument_quote_hist USING btree (instrument_quote, update_date);


--
-- Name: idx_iso_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_iso_code ON public.currency USING btree (iso_code);


--
-- Name: idx_market_segment_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_market_segment_code ON public.market_segment USING btree (code);


--
-- Name: idx_master_data_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_master_data_code ON public.master_data USING btree (code);


--
-- Name: idx_md_ed; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_md_ed ON public.cash_flow_item USING btree (master_data, end_date);


--
-- Name: idx_portfolio_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_portfolio_code ON public.portfolio_master_data USING btree (code);


--
-- Name: idx_position_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_position_code ON public.position_master_data USING btree (code);


--
-- Name: idx_roll_convention_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_roll_convention_code ON public.roll_convention USING btree (code);


--
-- Name: idx_security_master_data_isin; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_security_master_data_isin ON public.security_master_data USING btree (isin);


--
-- Name: idx_settlement_type_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_settlement_type_code ON public.settlement_type USING btree (code);


--
-- Name: idx_super_class_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_super_class_code ON public.super_class USING btree (code);


--
-- Name: idx_txn_status_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_txn_status_code ON public.txn_status USING btree (code);


--
-- Name: idx_type_of_interest_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_type_of_interest_code ON public.type_of_interest USING btree (code);


--
-- Name: idx_yield_curve_code; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_yield_curve_code ON public.yield_curve USING btree (code);


--
-- Name: idx_yield_curve_item_ric; Type: INDEX; Schema: public; Owner: easypricer
--

CREATE UNIQUE INDEX idx_yield_curve_item_ric ON public.yield_curve_item USING btree (ric);


--
-- Name: instrument_quote_hist aggiorna_id_instrument_quote_hist; Type: TRIGGER; Schema: public; Owner: easypricer
--

CREATE TRIGGER aggiorna_id_instrument_quote_hist BEFORE INSERT ON public.instrument_quote_hist FOR EACH ROW EXECUTE FUNCTION public.aggiorna_id_instrument_quote_hist();


--
-- Name: master_data fk_accrual_schedule_type; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_accrual_schedule_type FOREIGN KEY (accrual_schedule_type) REFERENCES public.accrual_schedule_type(id_accrual_schedule_type);


--
-- Name: master_data fk_amortization_schedule; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_amortization_schedule FOREIGN KEY (amortization_schedule) REFERENCES public.amortization_schedule(id_amortization_schedule);


--
-- Name: master_data fk_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- Name: currency_pair fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currency_pair
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- Name: currpair_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- Name: forex_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- Name: country fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: currency fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: holiday fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: master_data fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: yield_curve fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: currency_pair fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currency_pair
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- Name: currpair_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- Name: forex_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- Name: finacial_txn fk_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.finacial_txn
    ADD CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty);


--
-- Name: counterparty fk_country; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- Name: issuer fk_country; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- Name: counterparty fk_ctp_type; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_ctp_type FOREIGN KEY (ctp_type) REFERENCES public.counterparty_type(id_counterparty_type);


--
-- Name: country fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: portfolio_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: position_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: yield_curve fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: currency fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: master_data fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: master_data fk_form; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_form FOREIGN KEY (form) REFERENCES public.form(id_form);


--
-- Name: master_data fk_frequency; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_frequency FOREIGN KEY (frequency) REFERENCES public.frequency(id_frequency);


--
-- Name: instrument_quote_hist fk_instrument_quote; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_instrument_quote FOREIGN KEY (instrument_quote) REFERENCES public.instrument_quote(id_instrument_quote);


--
-- Name: security_master_data fk_issuer; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT fk_issuer FOREIGN KEY (issuer) REFERENCES public.issuer(id_issuer);


--
-- Name: cash_flow_item fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: deliverable_bonds fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.bond_future_master_data(id_master_data);


--
-- Name: finacial_txn fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.finacial_txn
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: instrument_quote fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: instrument_quote_hist fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: position_detail fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: position_master_data fk_portfolio; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_portfolio FOREIGN KEY (portfolio) REFERENCES public.portfolio_master_data(id_portfolio);


--
-- Name: finacial_txn fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.finacial_txn
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- Name: position_detail fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- Name: master_data fk_roll_convention; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_roll_convention FOREIGN KEY (roll_convention) REFERENCES public.roll_convention(id_roll_convention);


--
-- Name: future_master_data fk_settlement_type; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.future_master_data
    ADD CONSTRAINT fk_settlement_type FOREIGN KEY (settlement_type) REFERENCES public.settlement_type(id_settlement_type);


--
-- Name: asset_class fk_super_class; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT fk_super_class FOREIGN KEY (super_class) REFERENCES public.super_class(id_super_class);


--
-- Name: finacial_txn fk_txn_status; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.finacial_txn
    ADD CONSTRAINT fk_txn_status FOREIGN KEY (txn_status) REFERENCES public.txn_status(id_txn_status);


--
-- Name: master_data fk_type_of_interest; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_type_of_interest FOREIGN KEY (type_of_interest) REFERENCES public.type_of_interest(id_type_of_interest);


--
-- Name: fx_future_master_data fk_underlying; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- Name: mm_future_master_data fk_underlying; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- Name: yield_curve_item fk_yield_curve; Type: FK CONSTRAINT; Schema: public; Owner: easypricer
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_yield_curve FOREIGN KEY (yield_curve) REFERENCES public.yield_curve(id_yield_curve);


--
-- PostgreSQL database dump complete
--


