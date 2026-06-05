--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2026-06-05 13:28:44

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
-- TOC entry 295 (class 1255 OID 27442)
-- Name: fn_manage_ref_id(); Type: FUNCTION; Schema: public; Owner: sofie
--

CREATE FUNCTION public.fn_manage_ref_id() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    current_status_code VARCHAR;
BEGIN
    -- Recuperiamo il codice dello stato per il controllo
    SELECT code INTO current_status_code FROM txn_status WHERE id_txn_status = NEW.txn_status;

    IF (TG_OP = 'INSERT') THEN
        -- Se è PENDING e l'utente esiste un refId, usiamo l'ID appena generato
        IF current_status_code = 'PENDING' AND NEW.ref_id IS NULL THEN
            NEW.ref_id := NEW.id_financial_txn;
        END IF;

    ELSIF (TG_OP = 'UPDATE') THEN
        -- Se lo stato è PENDING, proteggiamo il campo (l'utente non può forzarlo)
        IF current_status_code = 'PENDING' AND NEW.ref_id IS NULL THEN
            NEW.ref_id := NEW.id_financial_txn;
        END IF;
        -- Se lo stato NON è PENDING, il trigger non entra nell'IF e 
        -- accetta il valore che l'utente ha messo nel campo refId.
    END IF;

    RETURN NEW;
END;
$$;


ALTER FUNCTION public.fn_manage_ref_id() OWNER TO sofie;

--
-- TOC entry 296 (class 1255 OID 28048)
-- Name: purge_expired_master_data(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.purge_expired_master_data() RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Cancella prima i record figli
    DELETE FROM cash_flow_item
    WHERE master_data IN (
        SELECT id_master_data 
        FROM master_data 
        WHERE maturity_date <= CURRENT_DATE
    );

    -- Cancella poi i record padri
    DELETE FROM master_data
    WHERE maturity_date <= CURRENT_DATE;
END;
$$;


ALTER FUNCTION public.purge_expired_master_data() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 27443)
-- Name: account_natures; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.account_natures (
    nature_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.account_natures OWNER TO sofie;

--
-- TOC entry 218 (class 1259 OID 27447)
-- Name: account_natures_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.account_natures_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.account_natures_s OWNER TO sofie;

--
-- TOC entry 219 (class 1259 OID 27448)
-- Name: accrual_schedule_type; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.accrual_schedule_type (
    id_accrual_schedule_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.accrual_schedule_type OWNER TO sofie;

--
-- TOC entry 220 (class 1259 OID 27451)
-- Name: accrual_schedule_type_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.accrual_schedule_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.accrual_schedule_type_s OWNER TO sofie;

--
-- TOC entry 221 (class 1259 OID 27452)
-- Name: amortization_schedule; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.amortization_schedule (
    id_amortization_schedule integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.amortization_schedule OWNER TO sofie;

--
-- TOC entry 222 (class 1259 OID 27455)
-- Name: amortization_schedule_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.amortization_schedule_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.amortization_schedule_s OWNER TO sofie;

--
-- TOC entry 223 (class 1259 OID 27456)
-- Name: asset_class; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.asset_class (
    id_asset_class integer NOT NULL,
    super_class integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.asset_class OWNER TO sofie;

--
-- TOC entry 224 (class 1259 OID 27460)
-- Name: asset_class_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.asset_class_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.asset_class_s OWNER TO sofie;

--
-- TOC entry 225 (class 1259 OID 27461)
-- Name: bond_future_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.bond_future_master_data (
    id_master_data integer NOT NULL,
    contract_value numeric(15,5) NOT NULL,
    tick_size numeric(15,5) NOT NULL,
    initial_margin numeric(15,5) NOT NULL
);


ALTER TABLE public.bond_future_master_data OWNER TO sofie;

--
-- TOC entry 226 (class 1259 OID 27464)
-- Name: calendar; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.calendar (
    id_calendar integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.calendar OWNER TO sofie;

--
-- TOC entry 227 (class 1259 OID 27467)
-- Name: calendar_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.calendar_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.calendar_s OWNER TO sofie;

--
-- TOC entry 228 (class 1259 OID 27468)
-- Name: cash_flow_item; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.cash_flow_item (
    id_cash_flow_item integer NOT NULL,
    master_data integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    interest numeric(15,5) NOT NULL,
    amount numeric(15,5) NOT NULL,
    known smallint DEFAULT 1 NOT NULL
);


ALTER TABLE public.cash_flow_item OWNER TO sofie;

--
-- TOC entry 229 (class 1259 OID 27472)
-- Name: cash_flow_item_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.cash_flow_item_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cash_flow_item_s OWNER TO sofie;

--
-- TOC entry 230 (class 1259 OID 27473)
-- Name: cash_flow_reset; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.cash_flow_reset (
    id_cash_flow_reset integer NOT NULL,
    master_data integer NOT NULL,
    start_date_reset date NOT NULL,
    interest_reset numeric(15,5) NOT NULL
);


ALTER TABLE public.cash_flow_reset OWNER TO sofie;

--
-- TOC entry 231 (class 1259 OID 27476)
-- Name: cash_flow_reset_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.cash_flow_reset_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cash_flow_reset_s OWNER TO sofie;

--
-- TOC entry 232 (class 1259 OID 27477)
-- Name: compounding; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.compounding (
    id_compounding integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.compounding OWNER TO sofie;

--
-- TOC entry 233 (class 1259 OID 27480)
-- Name: compounding_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.compounding_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.compounding_s OWNER TO sofie;

--
-- TOC entry 234 (class 1259 OID 27481)
-- Name: counterparty; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.counterparty (
    id_counterparty integer NOT NULL,
    ctp_type integer NOT NULL,
    lei_code character varying(50),
    country integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.counterparty OWNER TO sofie;

--
-- TOC entry 235 (class 1259 OID 27484)
-- Name: counterparty_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.counterparty_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.counterparty_s OWNER TO sofie;

--
-- TOC entry 236 (class 1259 OID 27485)
-- Name: counterparty_type; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.counterparty_type (
    id_counterparty_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.counterparty_type OWNER TO sofie;

--
-- TOC entry 237 (class 1259 OID 27488)
-- Name: counterparty_type_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.counterparty_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.counterparty_type_s OWNER TO sofie;

--
-- TOC entry 238 (class 1259 OID 27489)
-- Name: country; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.country (
    id_country integer NOT NULL,
    country_name character varying(100) NOT NULL,
    official_state_name character varying(255) NOT NULL,
    alfa_2_code character varying(2) NOT NULL,
    alfa_3_code character varying(3) NOT NULL,
    country_numeric_code smallint NOT NULL,
    sovereign character varying(25) DEFAULT 'UN Member State'::character varying,
    subdivision_code_links character varying(25) DEFAULT ''::character varying,
    internet_cc_tld character varying(10) DEFAULT ''::character varying,
    currency integer NOT NULL,
    calendar integer NOT NULL
);


ALTER TABLE public.country OWNER TO sofie;

--
-- TOC entry 239 (class 1259 OID 27495)
-- Name: country_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.country_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.country_s OWNER TO sofie;

--
-- TOC entry 240 (class 1259 OID 27496)
-- Name: currency; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.currency (
    id_currency integer NOT NULL,
    calendar integer NOT NULL,
    daycount integer NOT NULL,
    iso_code character varying(3) NOT NULL,
    currency_numeric_code smallint NOT NULL,
    description character varying(50) NOT NULL,
    minor_unit smallint DEFAULT 2 NOT NULL,
    system_curr smallint DEFAULT 0 NOT NULL,
    physical_curr smallint DEFAULT 1 NOT NULL,
    business_days integer DEFAULT 2 NOT NULL
);


ALTER TABLE public.currency OWNER TO sofie;

--
-- TOC entry 241 (class 1259 OID 27503)
-- Name: currency_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.currency_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.currency_s OWNER TO sofie;

--
-- TOC entry 242 (class 1259 OID 27504)
-- Name: currpair_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.currpair_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL,
    ccy integer NOT NULL,
    bcy_irc character varying(25) DEFAULT ''::character varying NOT NULL,
    ccy_irc character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.currpair_master_data OWNER TO sofie;

--
-- TOC entry 243 (class 1259 OID 27509)
-- Name: daycount; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.daycount (
    id_daycount integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.daycount OWNER TO sofie;

--
-- TOC entry 244 (class 1259 OID 27512)
-- Name: daycount_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.daycount_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.daycount_s OWNER TO sofie;

--
-- TOC entry 245 (class 1259 OID 27513)
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
-- TOC entry 246 (class 1259 OID 27516)
-- Name: deliverable_bonds_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.deliverable_bonds_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.deliverable_bonds_s OWNER TO sofie;

--
-- TOC entry 247 (class 1259 OID 27517)
-- Name: financial_statement_types; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.financial_statement_types (
    statement_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.financial_statement_types OWNER TO sofie;

--
-- TOC entry 248 (class 1259 OID 27521)
-- Name: financial_statement_types_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.financial_statement_types_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.financial_statement_types_s OWNER TO sofie;

--
-- TOC entry 249 (class 1259 OID 27522)
-- Name: financial_txn; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.financial_txn (
    id_financial_txn integer NOT NULL,
    counterparty integer NOT NULL,
    position_md integer NOT NULL,
    master_data integer NOT NULL,
    txn_status integer NOT NULL,
    txn_side smallint NOT NULL,
    description character varying(255) NOT NULL,
    trade_date date DEFAULT now() NOT NULL,
    value_date date NOT NULL,
    settlement date NOT NULL,
    quantity numeric(15,5) NOT NULL,
    price numeric(15,5) NOT NULL,
    ref_id integer NOT NULL,
    version integer DEFAULT 0
);


ALTER TABLE public.financial_txn OWNER TO sofie;

--
-- TOC entry 250 (class 1259 OID 27527)
-- Name: financial_txn_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.financial_txn_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.financial_txn_s OWNER TO sofie;

--
-- TOC entry 251 (class 1259 OID 27528)
-- Name: forex_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.forex_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL,
    ccy integer NOT NULL,
    bcy_irc character varying(25) DEFAULT ''::character varying NOT NULL,
    ccy_irc character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.forex_master_data OWNER TO sofie;

--
-- TOC entry 252 (class 1259 OID 27533)
-- Name: form; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.form (
    id_form integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.form OWNER TO sofie;

--
-- TOC entry 253 (class 1259 OID 27536)
-- Name: form_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.form_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.form_s OWNER TO sofie;

--
-- TOC entry 254 (class 1259 OID 27537)
-- Name: frequency; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.frequency (
    id_frequency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL,
    year_fraction smallint DEFAULT 2 NOT NULL
);


ALTER TABLE public.frequency OWNER TO sofie;

--
-- TOC entry 255 (class 1259 OID 27541)
-- Name: frequency_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.frequency_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.frequency_s OWNER TO sofie;

--
-- TOC entry 256 (class 1259 OID 27542)
-- Name: future_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.future_master_data (
    id_master_data integer NOT NULL,
    isin character varying(25) NOT NULL,
    exchange_contract_code character varying(25) DEFAULT ''::character varying NOT NULL,
    settlement_type integer NOT NULL
);


ALTER TABLE public.future_master_data OWNER TO sofie;

--
-- TOC entry 257 (class 1259 OID 27546)
-- Name: fx_future_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.fx_future_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL,
    contract_value numeric(15,5) NOT NULL,
    tick_size numeric(15,5) NOT NULL,
    initial_margin numeric(15,5) NOT NULL,
    maintenance_margin numeric(15,5) NOT NULL
);


ALTER TABLE public.fx_future_master_data OWNER TO sofie;

--
-- TOC entry 258 (class 1259 OID 27549)
-- Name: gl_accounts; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.gl_accounts (
    account_id integer NOT NULL,
    parent integer,
    code character varying(50) NOT NULL,
    description character varying(150) DEFAULT ''::character varying NOT NULL,
    is_postable boolean DEFAULT false NOT NULL,
    currency integer,
    statement_type integer NOT NULL,
    nature integer NOT NULL,
    balance integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.gl_accounts OWNER TO sofie;

--
-- TOC entry 259 (class 1259 OID 27556)
-- Name: gl_accounts_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.gl_accounts_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.gl_accounts_s OWNER TO sofie;

--
-- TOC entry 260 (class 1259 OID 27557)
-- Name: holiday; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.holiday (
    id_holiday integer NOT NULL,
    calendar integer NOT NULL,
    holiday_day smallint NOT NULL,
    holiday_month smallint NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.holiday OWNER TO sofie;

--
-- TOC entry 261 (class 1259 OID 27561)
-- Name: holiday_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.holiday_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.holiday_s OWNER TO sofie;

--
-- TOC entry 262 (class 1259 OID 27562)
-- Name: instrument_quote; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.instrument_quote (
    id_instrument_quote integer NOT NULL,
    master_data integer NOT NULL,
    provider character varying(50) NOT NULL,
    code character varying(255) NOT NULL,
    bid numeric(15,5) NOT NULL,
    ask numeric(15,5) NOT NULL
);


ALTER TABLE public.instrument_quote OWNER TO sofie;

--
-- TOC entry 263 (class 1259 OID 27565)
-- Name: instrument_quote_hist; Type: TABLE; Schema: public; Owner: sofie
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


ALTER TABLE public.instrument_quote_hist OWNER TO sofie;

--
-- TOC entry 264 (class 1259 OID 27568)
-- Name: instrument_quote_hist_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.instrument_quote_hist_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.instrument_quote_hist_s OWNER TO sofie;

--
-- TOC entry 265 (class 1259 OID 27569)
-- Name: instrument_quote_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.instrument_quote_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.instrument_quote_s OWNER TO sofie;

--
-- TOC entry 266 (class 1259 OID 27570)
-- Name: issuer; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.issuer (
    id_issuer integer NOT NULL,
    short_issuer_name character varying(25) DEFAULT ''::character varying NOT NULL,
    long_issuer_name character varying(255) NOT NULL,
    country integer NOT NULL
);


ALTER TABLE public.issuer OWNER TO sofie;

--
-- TOC entry 267 (class 1259 OID 27574)
-- Name: issuer_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.issuer_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.issuer_s OWNER TO sofie;

--
-- TOC entry 268 (class 1259 OID 27575)
-- Name: loan_master_data; Type: TABLE; Schema: public; Owner: sofie
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


ALTER TABLE public.loan_master_data OWNER TO sofie;

--
-- TOC entry 269 (class 1259 OID 27587)
-- Name: master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.master_data (
    id_master_data integer NOT NULL,
    code character varying(25) NOT NULL,
    currency integer NOT NULL,
    issue_date date NOT NULL,
    maturity_date date NOT NULL,
    type_of_interest integer NOT NULL,
    form integer NOT NULL,
    daycount integer NOT NULL,
    accrual_daycount integer NOT NULL,
    frequency integer NOT NULL,
    roll_convention integer DEFAULT 0 NOT NULL,
    accrual_schedule_type integer DEFAULT 0 NOT NULL,
    interest_rate numeric(23,10) NOT NULL,
    issue_price numeric(15,5) NOT NULL,
    redempion_price numeric(15,5) NOT NULL,
    business_days integer DEFAULT 2 NOT NULL,
    asset_class integer NOT NULL,
    amortization_schedule integer NOT NULL,
    multiplier numeric(15,5) DEFAULT 1 NOT NULL,
    description character varying(255) DEFAULT ''::character varying
);


ALTER TABLE public.master_data OWNER TO sofie;

--
-- TOC entry 270 (class 1259 OID 27595)
-- Name: master_data_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.master_data_s OWNER TO sofie;

--
-- TOC entry 271 (class 1259 OID 27596)
-- Name: mm_future_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.mm_future_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL,
    contract_value numeric(15,5) NOT NULL,
    tick_size numeric(15,5) NOT NULL,
    initial_margin numeric(15,5) NOT NULL,
    maintenance_margin numeric(15,5) NOT NULL
);


ALTER TABLE public.mm_future_master_data OWNER TO sofie;

--
-- TOC entry 272 (class 1259 OID 27599)
-- Name: normal_balances; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.normal_balances (
    balance_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.normal_balances OWNER TO sofie;

--
-- TOC entry 273 (class 1259 OID 27603)
-- Name: normal_balances_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.normal_balances_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.normal_balances_s OWNER TO sofie;

--
-- TOC entry 274 (class 1259 OID 27604)
-- Name: portfolio_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.portfolio_master_data (
    id_portfolio integer NOT NULL,
    currency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.portfolio_master_data OWNER TO sofie;

--
-- TOC entry 275 (class 1259 OID 27607)
-- Name: portfolio_master_data_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.portfolio_master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.portfolio_master_data_s OWNER TO sofie;

--
-- TOC entry 276 (class 1259 OID 27608)
-- Name: position_detail; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.position_detail (
    id_position_detail integer NOT NULL,
    position_md integer NOT NULL,
    master_data integer NOT NULL,
    counterparty integer NOT NULL,
    realized_pnl numeric(15,5) NOT NULL,
    unrealized_pnl numeric(15,5) NOT NULL,
    buy_qty numeric(15,5) NOT NULL,
    notional_value_buy numeric(15,5) NOT NULL,
    buy_fees numeric(15,5) NOT NULL,
    buy_taxes numeric(15,5) NOT NULL,
    sell_qty numeric(15,5) NOT NULL,
    notional_value_sell numeric(15,5) NOT NULL,
    sell_fees numeric(15,5) NOT NULL,
    sell_taxes numeric(15,5) NOT NULL,
    multiplier numeric(15,5) DEFAULT 1 NOT NULL,
    market_price numeric(15,5) NOT NULL
);


ALTER TABLE public.position_detail OWNER TO sofie;

--
-- TOC entry 277 (class 1259 OID 27612)
-- Name: position_detail_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.position_detail_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.position_detail_s OWNER TO sofie;

--
-- TOC entry 278 (class 1259 OID 27613)
-- Name: position_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.position_master_data (
    id_position integer NOT NULL,
    portfolio integer NOT NULL,
    currency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.position_master_data OWNER TO sofie;

--
-- TOC entry 279 (class 1259 OID 27616)
-- Name: position_master_data_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.position_master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.position_master_data_s OWNER TO sofie;

--
-- TOC entry 280 (class 1259 OID 27617)
-- Name: roll_convention; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.roll_convention (
    id_roll_convention integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.roll_convention OWNER TO sofie;

--
-- TOC entry 281 (class 1259 OID 27620)
-- Name: roll_convention_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.roll_convention_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roll_convention_s OWNER TO sofie;

--
-- TOC entry 282 (class 1259 OID 27621)
-- Name: security_master_data; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.security_master_data (
    id_master_data integer NOT NULL,
    isin character varying(25) NOT NULL,
    cfi_code character varying(25) NOT NULL,
    fisn character varying(255) NOT NULL,
    lei character varying(255) NOT NULL,
    issuer integer NOT NULL,
    nominal_value numeric(23,10) NOT NULL,
    first_coupon_rate numeric(23,10) NOT NULL,
    first_coupon_payment_date date NOT NULL
);


ALTER TABLE public.security_master_data OWNER TO sofie;

--
-- TOC entry 283 (class 1259 OID 27626)
-- Name: settlement_type; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.settlement_type (
    id_settlement_type integer NOT NULL,
    code character varying(25),
    description character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.settlement_type OWNER TO sofie;

--
-- TOC entry 284 (class 1259 OID 27630)
-- Name: settlement_type_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.settlement_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.settlement_type_s OWNER TO sofie;

--
-- TOC entry 285 (class 1259 OID 27631)
-- Name: super_class; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.super_class (
    id_super_class integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.super_class OWNER TO sofie;

--
-- TOC entry 286 (class 1259 OID 27635)
-- Name: super_class_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.super_class_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.super_class_s OWNER TO sofie;

--
-- TOC entry 287 (class 1259 OID 27636)
-- Name: txn_status; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.txn_status (
    id_txn_status integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.txn_status OWNER TO sofie;

--
-- TOC entry 288 (class 1259 OID 27639)
-- Name: txn_status_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.txn_status_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.txn_status_s OWNER TO sofie;

--
-- TOC entry 289 (class 1259 OID 27640)
-- Name: type_of_interest; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.type_of_interest (
    id_type_of_interest integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.type_of_interest OWNER TO sofie;

--
-- TOC entry 290 (class 1259 OID 27643)
-- Name: type_of_interest_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.type_of_interest_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.type_of_interest_s OWNER TO sofie;

--
-- TOC entry 291 (class 1259 OID 27644)
-- Name: yield_curve; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.yield_curve (
    id_yield_curve integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL,
    currency integer NOT NULL,
    calendar integer NOT NULL,
    compounding smallint DEFAULT 1 NOT NULL,
    provider character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.yield_curve OWNER TO sofie;

--
-- TOC entry 292 (class 1259 OID 27650)
-- Name: yield_curve_item; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.yield_curve_item (
    id_yield_curve_item integer NOT NULL,
    yield_curve integer NOT NULL,
    ric character varying(25) NOT NULL,
    offset_type smallint NOT NULL,
    offset_value smallint NOT NULL,
    bid numeric(15,5) NOT NULL,
    ask numeric(15,5) NOT NULL,
    compounding smallint DEFAULT 1 NOT NULL,
    daycount smallint NOT NULL
);


ALTER TABLE public.yield_curve_item OWNER TO sofie;

--
-- TOC entry 293 (class 1259 OID 27654)
-- Name: yield_curve_item_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.yield_curve_item_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.yield_curve_item_s OWNER TO sofie;

--
-- TOC entry 294 (class 1259 OID 27655)
-- Name: yield_curve_s; Type: SEQUENCE; Schema: public; Owner: sofie
--

CREATE SEQUENCE public.yield_curve_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.yield_curve_s OWNER TO sofie;

--
-- TOC entry 5214 (class 0 OID 27443)
-- Dependencies: 217
-- Data for Name: account_natures; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.account_natures (nature_id, code, description) FROM stdin;
1	ASSET	Asset
2	LIABILITY	Liability
3	EQUITY	Equity
4	INCOME	Income
5	EXPENSE	Expense
6	MEMORANDUM	Memorandum
\.


--
-- TOC entry 5216 (class 0 OID 27448)
-- Dependencies: 219
-- Data for Name: accrual_schedule_type; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.accrual_schedule_type (id_accrual_schedule_type, code, description) FROM stdin;
100	NONE	None
\.


--
-- TOC entry 5218 (class 0 OID 27452)
-- Dependencies: 221
-- Data for Name: amortization_schedule; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.amortization_schedule (id_amortization_schedule, code, description) FROM stdin;
1	SAS	Standard Amortization Schedule
2	SLP	Straight-line Principal
3	IOL	Interest Only Loan
100	NONE	None
\.


--
-- TOC entry 5220 (class 0 OID 27456)
-- Dependencies: 223
-- Data for Name: asset_class; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.asset_class (id_asset_class, super_class, code, description) FROM stdin;
1	2	FRN	Floating Rate Notes
2	2	XRN	Fixed Rate Notes
3	2	FRB	Floating Rate Bonds
4	2	XRB	Fixed Rate Bonds
5	2	BLL	Bills
6	3	FRM	Floating Rate Mortages
7	3	XRM	Fixed Rate Mortages
8	4	BFU	Bond Futures
9	5	FSP	Spot Forex
10	5	FFW	Forex Forward
11	5	FFU	Forex Future
12	3	MFU	MM Future
\.


--
-- TOC entry 5222 (class 0 OID 27461)
-- Dependencies: 225
-- Data for Name: bond_future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.bond_future_master_data (id_master_data, contract_value, tick_size, initial_margin) FROM stdin;
\.


--
-- TOC entry 5223 (class 0 OID 27464)
-- Dependencies: 226
-- Data for Name: calendar; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.calendar (id_calendar, code, description) FROM stdin;
1	EUR	Euro Area Calendar
2	USD	Usd Area Calendar
\.


--
-- TOC entry 5225 (class 0 OID 27468)
-- Dependencies: 228
-- Data for Name: cash_flow_item; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.cash_flow_item (id_cash_flow_item, master_data, start_date, end_date, interest, amount, known) FROM stdin;
2	2	2026-01-14	2027-01-14	0.00000	100.00000	1
4	4	2026-02-13	2027-02-12	0.00000	100.00000	1
5	5	2025-12-12	2026-12-14	0.00000	100.00000	1
6	6	2025-11-14	2026-11-13	0.00000	100.00000	1
7	7	2026-01-30	2026-07-31	0.00000	100.00000	1
8	8	2025-10-14	2026-10-14	0.00000	100.00000	1
9	9	2025-09-12	2026-09-14	0.00000	100.00000	1
11	11	2025-08-14	2026-08-14	0.00000	100.00000	1
13	13	2025-06-13	2026-06-12	0.00000	100.00000	1
14	14	2025-07-14	2026-07-14	0.00000	100.00000	1
16	16	2025-09-09	2025-10-01	0.27951	0.00000	1
17	16	2025-10-01	2026-04-01	2.32500	0.00000	1
18	16	2026-04-01	2026-10-01	2.32500	0.00000	1
19	16	2026-10-01	2027-04-01	2.32500	0.00000	1
20	16	2027-04-01	2027-10-01	2.32500	0.00000	1
21	16	2027-10-01	2028-04-01	2.32500	0.00000	1
22	16	2028-04-01	2028-10-01	2.32500	0.00000	1
23	16	2028-10-01	2029-04-01	2.32500	0.00000	1
24	16	2029-04-01	2029-10-01	2.32500	0.00000	1
25	16	2029-10-01	2030-04-01	2.32500	0.00000	1
26	16	2030-04-01	2030-10-01	2.32500	0.00000	1
27	16	2030-10-01	2031-04-01	2.32500	0.00000	1
28	16	2031-04-01	2031-10-01	2.32500	0.00000	1
29	16	2031-10-01	2032-04-01	2.32500	0.00000	1
30	16	2032-04-01	2032-10-01	2.32500	0.00000	1
31	16	2032-10-01	2033-04-01	2.32500	0.00000	1
32	16	2033-04-01	2033-10-01	2.32500	0.00000	1
33	16	2033-10-01	2034-04-01	2.32500	0.00000	1
34	16	2034-04-01	2034-10-01	2.32500	0.00000	1
35	16	2034-10-01	2035-04-01	2.32500	0.00000	1
36	16	2035-04-01	2035-10-01	2.32500	0.00000	1
37	16	2035-10-01	2036-04-01	2.32500	0.00000	1
38	16	2036-04-01	2036-10-01	2.32500	0.00000	1
39	16	2036-10-01	2037-04-01	2.32500	0.00000	1
40	16	2037-04-01	2037-10-01	2.32500	0.00000	1
41	16	2037-10-01	2038-04-01	2.32500	0.00000	1
42	16	2038-04-01	2038-10-01	2.32500	0.00000	1
43	16	2038-10-01	2039-04-01	2.32500	0.00000	1
44	16	2039-04-01	2039-10-01	2.32500	0.00000	1
45	16	2039-10-01	2040-04-01	2.32500	0.00000	1
46	16	2040-04-01	2040-10-01	2.32500	0.00000	1
47	16	2040-10-01	2041-04-01	2.32500	0.00000	1
48	16	2041-04-01	2041-10-01	2.32500	0.00000	1
49	16	2041-10-01	2042-04-01	2.32500	0.00000	1
50	16	2042-04-01	2042-10-01	2.32500	0.00000	1
51	16	2042-10-01	2043-04-01	2.32500	0.00000	1
52	16	2043-04-01	2043-10-01	2.32500	0.00000	1
53	16	2043-10-01	2044-04-01	2.32500	0.00000	1
54	16	2044-04-01	2044-10-01	2.32500	0.00000	1
55	16	2044-10-01	2045-04-01	2.32500	0.00000	1
56	16	2045-04-01	2045-10-01	2.32500	0.00000	1
57	16	2045-10-01	2046-04-01	2.32500	0.00000	1
58	16	2046-04-01	2046-10-01	2.32500	0.00000	1
59	16	2046-10-01	2047-04-01	2.32500	0.00000	1
60	16	2047-04-01	2047-10-01	2.32500	0.00000	1
61	16	2047-10-01	2048-04-01	2.32500	0.00000	1
62	16	2048-04-01	2048-10-01	2.32500	0.00000	1
63	16	2048-10-01	2049-04-01	2.32500	0.00000	1
64	16	2049-04-01	2049-10-01	2.32500	0.00000	1
65	16	2049-10-01	2050-04-01	2.32500	0.00000	1
66	16	2050-04-01	2050-10-01	2.32500	0.00000	1
67	16	2050-10-01	2051-04-01	2.32500	0.00000	1
68	16	2051-04-01	2051-10-01	2.32500	0.00000	1
69	16	2051-10-01	2052-04-01	2.32500	0.00000	1
70	16	2052-04-01	2052-10-01	2.32500	0.00000	1
71	16	2052-10-01	2053-04-01	2.32500	0.00000	1
72	16	2053-04-01	2053-10-01	2.32500	0.00000	1
73	16	2053-10-01	2054-04-01	2.32500	0.00000	1
74	16	2054-04-01	2054-10-01	2.32500	0.00000	1
75	16	2054-10-01	2055-04-01	2.32500	0.00000	1
76	16	2055-04-01	2055-10-01	2.32500	100.00000	1
77	17	2024-09-17	2024-10-01	0.16448	0.00000	1
78	17	2024-10-01	2025-04-01	2.15000	0.00000	1
79	17	2025-04-01	2025-10-01	2.15000	0.00000	1
80	17	2025-10-01	2026-04-01	2.15000	0.00000	1
81	17	2026-04-01	2026-10-01	2.15000	0.00000	1
82	17	2026-10-01	2027-04-01	2.15000	0.00000	1
83	17	2027-04-01	2027-10-01	2.15000	0.00000	1
84	17	2027-10-01	2028-04-01	2.15000	0.00000	1
85	17	2028-04-01	2028-10-01	2.15000	0.00000	1
86	17	2028-10-01	2029-04-01	2.15000	0.00000	1
87	17	2029-04-01	2029-10-01	2.15000	0.00000	1
88	17	2029-10-01	2030-04-01	2.15000	0.00000	1
89	17	2030-04-01	2030-10-01	2.15000	0.00000	1
90	17	2030-10-01	2031-04-01	2.15000	0.00000	1
91	17	2031-04-01	2031-10-01	2.15000	0.00000	1
92	17	2031-10-01	2032-04-01	2.15000	0.00000	1
93	17	2032-04-01	2032-10-01	2.15000	0.00000	1
94	17	2032-10-01	2033-04-01	2.15000	0.00000	1
95	17	2033-04-01	2033-10-01	2.15000	0.00000	1
96	17	2033-10-01	2034-04-01	2.15000	0.00000	1
97	17	2034-04-01	2034-10-01	2.15000	0.00000	1
98	17	2034-10-01	2035-04-01	2.15000	0.00000	1
99	17	2035-04-01	2035-10-01	2.15000	0.00000	1
100	17	2035-10-01	2036-04-01	2.15000	0.00000	1
101	17	2036-04-01	2036-10-01	2.15000	0.00000	1
102	17	2036-10-01	2037-04-01	2.15000	0.00000	1
103	17	2037-04-01	2037-10-01	2.15000	0.00000	1
104	17	2037-10-01	2038-04-01	2.15000	0.00000	1
105	17	2038-04-01	2038-10-01	2.15000	0.00000	1
106	17	2038-10-01	2039-04-01	2.15000	0.00000	1
107	17	2039-04-01	2039-10-01	2.15000	0.00000	1
108	17	2039-10-01	2040-04-01	2.15000	0.00000	1
109	17	2040-04-01	2040-10-01	2.15000	0.00000	1
110	17	2040-10-01	2041-04-01	2.15000	0.00000	1
111	17	2041-04-01	2041-10-01	2.15000	0.00000	1
112	17	2041-10-01	2042-04-01	2.15000	0.00000	1
113	17	2042-04-01	2042-10-01	2.15000	0.00000	1
114	17	2042-10-01	2043-04-01	2.15000	0.00000	1
115	17	2043-04-01	2043-10-01	2.15000	0.00000	1
116	17	2043-10-01	2044-04-01	2.15000	0.00000	1
117	17	2044-04-01	2044-10-01	2.15000	0.00000	1
118	17	2044-10-01	2045-04-01	2.15000	0.00000	1
119	17	2045-04-01	2045-10-01	2.15000	0.00000	1
120	17	2045-10-01	2046-04-01	2.15000	0.00000	1
121	17	2046-04-01	2046-10-01	2.15000	0.00000	1
122	17	2046-10-01	2047-04-01	2.15000	0.00000	1
123	17	2047-04-01	2047-10-01	2.15000	0.00000	1
124	17	2047-10-01	2048-04-01	2.15000	0.00000	1
125	17	2048-04-01	2048-10-01	2.15000	0.00000	1
126	17	2048-10-01	2049-04-01	2.15000	0.00000	1
127	17	2049-04-01	2049-10-01	2.15000	0.00000	1
128	17	2049-10-01	2050-04-01	2.15000	0.00000	1
129	17	2050-04-01	2050-10-01	2.15000	0.00000	1
130	17	2050-10-01	2051-04-01	2.15000	0.00000	1
131	17	2051-04-01	2051-10-01	2.15000	0.00000	1
132	17	2051-10-01	2052-04-01	2.15000	0.00000	1
133	17	2052-04-01	2052-10-01	2.15000	0.00000	1
134	17	2052-10-01	2053-04-01	2.15000	0.00000	1
135	17	2053-04-01	2053-10-01	2.15000	0.00000	1
136	17	2053-10-01	2054-04-01	2.15000	0.00000	1
137	17	2054-04-01	2054-10-01	2.15000	100.00000	1
138	18	2023-02-23	2023-04-01	0.45742	0.00000	1
139	18	2023-04-01	2023-10-01	2.25000	0.00000	1
140	18	2023-10-01	2024-04-01	2.25000	0.00000	1
141	18	2024-04-01	2024-10-01	2.25000	0.00000	1
142	18	2024-10-01	2025-04-01	2.25000	0.00000	1
143	18	2025-04-01	2025-10-01	2.25000	0.00000	1
144	18	2025-10-01	2026-04-01	2.25000	0.00000	1
145	18	2026-04-01	2026-10-01	2.25000	0.00000	1
146	18	2026-10-01	2027-04-01	2.25000	0.00000	1
147	18	2027-04-01	2027-10-01	2.25000	0.00000	1
148	18	2027-10-01	2028-04-01	2.25000	0.00000	1
149	18	2028-04-01	2028-10-01	2.25000	0.00000	1
150	18	2028-10-01	2029-04-01	2.25000	0.00000	1
151	18	2029-04-01	2029-10-01	2.25000	0.00000	1
152	18	2029-10-01	2030-04-01	2.25000	0.00000	1
153	18	2030-04-01	2030-10-01	2.25000	0.00000	1
154	18	2030-10-01	2031-04-01	2.25000	0.00000	1
155	18	2031-04-01	2031-10-01	2.25000	0.00000	1
156	18	2031-10-01	2032-04-01	2.25000	0.00000	1
157	18	2032-04-01	2032-10-01	2.25000	0.00000	1
158	18	2032-10-01	2033-04-01	2.25000	0.00000	1
159	18	2033-04-01	2033-10-01	2.25000	0.00000	1
160	18	2033-10-01	2034-04-01	2.25000	0.00000	1
161	18	2034-04-01	2034-10-01	2.25000	0.00000	1
162	18	2034-10-01	2035-04-01	2.25000	0.00000	1
163	18	2035-04-01	2035-10-01	2.25000	0.00000	1
164	18	2035-10-01	2036-04-01	2.25000	0.00000	1
165	18	2036-04-01	2036-10-01	2.25000	0.00000	1
166	18	2036-10-01	2037-04-01	2.25000	0.00000	1
167	18	2037-04-01	2037-10-01	2.25000	0.00000	1
168	18	2037-10-01	2038-04-01	2.25000	0.00000	1
169	18	2038-04-01	2038-10-01	2.25000	0.00000	1
170	18	2038-10-01	2039-04-01	2.25000	0.00000	1
171	18	2039-04-01	2039-10-01	2.25000	0.00000	1
172	18	2039-10-01	2040-04-01	2.25000	0.00000	1
173	18	2040-04-01	2040-10-01	2.25000	0.00000	1
174	18	2040-10-01	2041-04-01	2.25000	0.00000	1
175	18	2041-04-01	2041-10-01	2.25000	0.00000	1
176	18	2041-10-01	2042-04-01	2.25000	0.00000	1
177	18	2042-04-01	2042-10-01	2.25000	0.00000	1
178	18	2042-10-01	2043-04-01	2.25000	0.00000	1
179	18	2043-04-01	2043-10-01	2.25000	0.00000	1
180	18	2043-10-01	2044-04-01	2.25000	0.00000	1
181	18	2044-04-01	2044-10-01	2.25000	0.00000	1
182	18	2044-10-01	2045-04-01	2.25000	0.00000	1
183	18	2045-04-01	2045-10-01	2.25000	0.00000	1
184	18	2045-10-01	2046-04-01	2.25000	0.00000	1
185	18	2046-04-01	2046-10-01	2.25000	0.00000	1
186	18	2046-10-01	2047-04-01	2.25000	0.00000	1
187	18	2047-04-01	2047-10-01	2.25000	0.00000	1
188	18	2047-10-01	2048-04-01	2.25000	0.00000	1
189	18	2048-04-01	2048-10-01	2.25000	0.00000	1
190	18	2048-10-01	2049-04-01	2.25000	0.00000	1
191	18	2049-04-01	2049-10-01	2.25000	0.00000	1
192	18	2049-10-01	2050-04-01	2.25000	0.00000	1
193	18	2050-04-01	2050-10-01	2.25000	0.00000	1
194	18	2050-10-01	2051-04-01	2.25000	0.00000	1
195	18	2051-04-01	2051-10-01	2.25000	0.00000	1
196	18	2051-10-01	2052-04-01	2.25000	0.00000	1
197	18	2052-04-01	2052-10-01	2.25000	0.00000	1
198	18	2052-10-01	2053-04-01	2.25000	0.00000	1
199	18	2053-04-01	2053-10-01	2.25000	100.00000	1
200	19	2016-09-01	2017-03-01	1.40000	0.00000	1
201	19	2017-03-01	2017-09-01	1.40000	0.00000	1
202	19	2017-09-01	2018-03-01	1.40000	0.00000	1
203	19	2018-03-01	2018-09-01	1.40000	0.00000	1
204	19	2018-09-01	2019-03-01	1.40000	0.00000	1
205	19	2019-03-01	2019-09-01	1.40000	0.00000	1
206	19	2019-09-01	2020-03-01	1.40000	0.00000	1
207	19	2020-03-01	2020-09-01	1.40000	0.00000	1
208	19	2020-09-01	2021-03-01	1.40000	0.00000	1
209	19	2021-03-01	2021-09-01	1.40000	0.00000	1
210	19	2021-09-01	2022-03-01	1.40000	0.00000	1
211	19	2022-03-01	2022-09-01	1.40000	0.00000	1
212	19	2022-09-01	2023-03-01	1.40000	0.00000	1
213	19	2023-03-01	2023-09-01	1.40000	0.00000	1
214	19	2023-09-01	2024-03-01	1.40000	0.00000	1
215	19	2024-03-01	2024-09-01	1.40000	0.00000	1
216	19	2024-09-01	2025-03-01	1.40000	0.00000	1
217	19	2025-03-01	2025-09-01	1.40000	0.00000	1
218	19	2025-09-01	2026-03-01	1.40000	0.00000	1
219	19	2026-03-01	2026-09-01	1.40000	0.00000	1
220	19	2026-09-01	2027-03-01	1.40000	0.00000	1
221	19	2027-03-01	2027-09-01	1.40000	0.00000	1
222	19	2027-09-01	2028-03-01	1.40000	0.00000	1
223	19	2028-03-01	2028-09-01	1.40000	0.00000	1
224	19	2028-09-01	2029-03-01	1.40000	0.00000	1
225	19	2029-03-01	2029-09-01	1.40000	0.00000	1
226	19	2029-09-01	2030-03-01	1.40000	0.00000	1
227	19	2030-03-01	2030-09-01	1.40000	0.00000	1
228	19	2030-09-01	2031-03-01	1.40000	0.00000	1
229	19	2031-03-01	2031-09-01	1.40000	0.00000	1
230	19	2031-09-01	2032-03-01	1.40000	0.00000	1
231	19	2032-03-01	2032-09-01	1.40000	0.00000	1
232	19	2032-09-01	2033-03-01	1.40000	0.00000	1
233	19	2033-03-01	2033-09-01	1.40000	0.00000	1
234	19	2033-09-01	2034-03-01	1.40000	0.00000	1
235	19	2034-03-01	2034-09-01	1.40000	0.00000	1
236	19	2034-09-01	2035-03-01	1.40000	0.00000	1
237	19	2035-03-01	2035-09-01	1.40000	0.00000	1
238	19	2035-09-01	2036-03-01	1.40000	0.00000	1
239	19	2036-03-01	2036-09-01	1.40000	0.00000	1
240	19	2036-09-01	2037-03-01	1.40000	0.00000	1
241	19	2037-03-01	2037-09-01	1.40000	0.00000	1
242	19	2037-09-01	2038-03-01	1.40000	0.00000	1
243	19	2038-03-01	2038-09-01	1.40000	0.00000	1
244	19	2038-09-01	2039-03-01	1.40000	0.00000	1
245	19	2039-03-01	2039-09-01	1.40000	0.00000	1
246	19	2039-09-01	2040-03-01	1.40000	0.00000	1
247	19	2040-03-01	2040-09-01	1.40000	0.00000	1
248	19	2040-09-01	2041-03-01	1.40000	0.00000	1
249	19	2041-03-01	2041-09-01	1.40000	0.00000	1
250	19	2041-09-01	2042-03-01	1.40000	0.00000	1
251	19	2042-03-01	2042-09-01	1.40000	0.00000	1
252	19	2042-09-01	2043-03-01	1.40000	0.00000	1
253	19	2043-03-01	2043-09-01	1.40000	0.00000	1
254	19	2043-09-01	2044-03-01	1.40000	0.00000	1
255	19	2044-03-01	2044-09-01	1.40000	0.00000	1
256	19	2044-09-01	2045-03-01	1.40000	0.00000	1
257	19	2045-03-01	2045-09-01	1.40000	0.00000	1
258	19	2045-09-01	2046-03-01	1.40000	0.00000	1
259	19	2046-03-01	2046-09-01	1.40000	0.00000	1
260	19	2046-09-01	2047-03-01	1.40000	0.00000	1
261	19	2047-03-01	2047-09-01	1.40000	0.00000	1
262	19	2047-09-01	2048-03-01	1.40000	0.00000	1
263	19	2048-03-01	2048-09-01	1.40000	0.00000	1
264	19	2048-09-01	2049-03-01	1.40000	0.00000	1
265	19	2049-03-01	2049-09-01	1.40000	0.00000	1
266	19	2049-09-01	2050-03-01	1.40000	0.00000	1
267	19	2050-03-01	2050-09-01	1.40000	0.00000	1
268	19	2050-09-01	2051-03-01	1.40000	0.00000	1
269	19	2051-03-01	2051-09-01	1.40000	0.00000	1
270	19	2051-09-01	2052-03-01	1.40000	0.00000	1
271	19	2052-03-01	2052-09-01	1.40000	0.00000	1
272	19	2052-09-01	2053-03-01	1.40000	0.00000	1
273	19	2053-03-01	2053-09-01	1.40000	0.00000	1
274	19	2053-09-01	2054-03-01	1.40000	0.00000	1
275	19	2054-03-01	2054-09-01	1.40000	0.00000	1
276	19	2054-09-01	2055-03-01	1.40000	0.00000	1
277	19	2055-03-01	2055-09-01	1.40000	0.00000	1
278	19	2055-09-01	2056-03-01	1.40000	0.00000	1
279	19	2056-03-01	2056-09-01	1.40000	0.00000	1
280	19	2056-09-01	2057-03-01	1.40000	0.00000	1
281	19	2057-03-01	2057-09-01	1.40000	0.00000	1
282	19	2057-09-01	2058-03-01	1.40000	0.00000	1
283	19	2058-03-01	2058-09-01	1.40000	0.00000	1
284	19	2058-09-01	2059-03-01	1.40000	0.00000	1
285	19	2059-03-01	2059-09-01	1.40000	0.00000	1
286	19	2059-09-01	2060-03-01	1.40000	0.00000	1
287	19	2060-03-01	2060-09-01	1.40000	0.00000	1
288	19	2060-09-01	2061-03-01	1.40000	0.00000	1
289	19	2061-03-01	2061-09-01	1.40000	0.00000	1
290	19	2061-09-01	2062-03-01	1.40000	0.00000	1
291	19	2062-03-01	2062-09-01	1.40000	0.00000	1
292	19	2062-09-01	2063-03-01	1.40000	0.00000	1
293	19	2063-03-01	2063-09-01	1.40000	0.00000	1
294	19	2063-09-01	2064-03-01	1.40000	0.00000	1
295	19	2064-03-01	2064-09-01	1.40000	0.00000	1
296	19	2064-09-01	2065-03-01	1.40000	0.00000	1
297	19	2065-03-01	2065-09-01	1.40000	0.00000	1
298	19	2065-09-01	2066-03-01	1.40000	0.00000	1
299	19	2066-03-01	2066-09-01	1.40000	0.00000	1
300	19	2066-09-01	2067-03-01	1.40000	100.00000	1
301	20	2022-01-12	2022-03-01	0.28508	0.00000	1
302	20	2022-03-01	2022-09-01	1.07500	0.00000	1
303	20	2022-09-01	2023-03-01	1.07500	0.00000	1
304	20	2023-03-01	2023-09-01	1.07500	0.00000	1
305	20	2023-09-01	2024-03-01	1.07500	0.00000	1
306	20	2024-03-01	2024-09-01	1.07500	0.00000	1
307	20	2024-09-01	2025-03-01	1.07500	0.00000	1
308	20	2025-03-01	2025-09-01	1.07500	0.00000	1
309	20	2025-09-01	2026-03-01	1.07500	0.00000	1
310	20	2026-03-01	2026-09-01	1.07500	0.00000	1
311	20	2026-09-01	2027-03-01	1.07500	0.00000	1
312	20	2027-03-01	2027-09-01	1.07500	0.00000	1
313	20	2027-09-01	2028-03-01	1.07500	0.00000	1
314	20	2028-03-01	2028-09-01	1.07500	0.00000	1
315	20	2028-09-01	2029-03-01	1.07500	0.00000	1
316	20	2029-03-01	2029-09-01	1.07500	0.00000	1
317	20	2029-09-01	2030-03-01	1.07500	0.00000	1
318	20	2030-03-01	2030-09-01	1.07500	0.00000	1
319	20	2030-09-01	2031-03-01	1.07500	0.00000	1
320	20	2031-03-01	2031-09-01	1.07500	0.00000	1
321	20	2031-09-01	2032-03-01	1.07500	0.00000	1
322	20	2032-03-01	2032-09-01	1.07500	0.00000	1
323	20	2032-09-01	2033-03-01	1.07500	0.00000	1
324	20	2033-03-01	2033-09-01	1.07500	0.00000	1
325	20	2033-09-01	2034-03-01	1.07500	0.00000	1
326	20	2034-03-01	2034-09-01	1.07500	0.00000	1
327	20	2034-09-01	2035-03-01	1.07500	0.00000	1
328	20	2035-03-01	2035-09-01	1.07500	0.00000	1
329	20	2035-09-01	2036-03-01	1.07500	0.00000	1
330	20	2036-03-01	2036-09-01	1.07500	0.00000	1
331	20	2036-09-01	2037-03-01	1.07500	0.00000	1
332	20	2037-03-01	2037-09-01	1.07500	0.00000	1
333	20	2037-09-01	2038-03-01	1.07500	0.00000	1
334	20	2038-03-01	2038-09-01	1.07500	0.00000	1
335	20	2038-09-01	2039-03-01	1.07500	0.00000	1
336	20	2039-03-01	2039-09-01	1.07500	0.00000	1
337	20	2039-09-01	2040-03-01	1.07500	0.00000	1
338	20	2040-03-01	2040-09-01	1.07500	0.00000	1
339	20	2040-09-01	2041-03-01	1.07500	0.00000	1
340	20	2041-03-01	2041-09-01	1.07500	0.00000	1
341	20	2041-09-01	2042-03-01	1.07500	0.00000	1
342	20	2042-03-01	2042-09-01	1.07500	0.00000	1
343	20	2042-09-01	2043-03-01	1.07500	0.00000	1
344	20	2043-03-01	2043-09-01	1.07500	0.00000	1
345	20	2043-09-01	2044-03-01	1.07500	0.00000	1
346	20	2044-03-01	2044-09-01	1.07500	0.00000	1
347	20	2044-09-01	2045-03-01	1.07500	0.00000	1
348	20	2045-03-01	2045-09-01	1.07500	0.00000	1
349	20	2045-09-01	2046-03-01	1.07500	0.00000	1
350	20	2046-03-01	2046-09-01	1.07500	0.00000	1
351	20	2046-09-01	2047-03-01	1.07500	0.00000	1
352	20	2047-03-01	2047-09-01	1.07500	0.00000	1
353	20	2047-09-01	2048-03-01	1.07500	0.00000	1
354	20	2048-03-01	2048-09-01	1.07500	0.00000	1
355	20	2048-09-01	2049-03-01	1.07500	0.00000	1
356	20	2049-03-01	2049-09-01	1.07500	0.00000	1
357	20	2049-09-01	2050-03-01	1.07500	0.00000	1
358	20	2050-03-01	2050-09-01	1.07500	0.00000	1
359	20	2050-09-01	2051-03-01	1.07500	0.00000	1
360	20	2051-03-01	2051-09-01	1.07500	0.00000	1
361	20	2051-09-01	2052-03-01	1.07500	0.00000	1
362	20	2052-03-01	2052-09-01	1.07500	100.00000	1
363	21	2020-09-01	2021-03-01	0.85000	0.00000	1
364	21	2021-03-01	2021-09-01	0.85000	0.00000	1
365	21	2021-09-01	2022-03-01	0.85000	0.00000	1
366	21	2022-03-01	2022-09-01	0.85000	0.00000	1
367	21	2022-09-01	2023-03-01	0.85000	0.00000	1
368	21	2023-03-01	2023-09-01	0.85000	0.00000	1
369	21	2023-09-01	2024-03-01	0.85000	0.00000	1
370	21	2024-03-01	2024-09-01	0.85000	0.00000	1
371	21	2024-09-01	2025-03-01	0.85000	0.00000	1
372	21	2025-03-01	2025-09-01	0.85000	0.00000	1
373	21	2025-09-01	2026-03-01	0.85000	0.00000	1
374	21	2026-03-01	2026-09-01	0.85000	0.00000	1
375	21	2026-09-01	2027-03-01	0.85000	0.00000	1
376	21	2027-03-01	2027-09-01	0.85000	0.00000	1
377	21	2027-09-01	2028-03-01	0.85000	0.00000	1
378	21	2028-03-01	2028-09-01	0.85000	0.00000	1
379	21	2028-09-01	2029-03-01	0.85000	0.00000	1
380	21	2029-03-01	2029-09-01	0.85000	0.00000	1
381	21	2029-09-01	2030-03-01	0.85000	0.00000	1
382	21	2030-03-01	2030-09-01	0.85000	0.00000	1
383	21	2030-09-01	2031-03-01	0.85000	0.00000	1
384	21	2031-03-01	2031-09-01	0.85000	0.00000	1
385	21	2031-09-01	2032-03-01	0.85000	0.00000	1
386	21	2032-03-01	2032-09-01	0.85000	0.00000	1
387	21	2032-09-01	2033-03-01	0.85000	0.00000	1
388	21	2033-03-01	2033-09-01	0.85000	0.00000	1
389	21	2033-09-01	2034-03-01	0.85000	0.00000	1
390	21	2034-03-01	2034-09-01	0.85000	0.00000	1
391	21	2034-09-01	2035-03-01	0.85000	0.00000	1
392	21	2035-03-01	2035-09-01	0.85000	0.00000	1
393	21	2035-09-01	2036-03-01	0.85000	0.00000	1
394	21	2036-03-01	2036-09-01	0.85000	0.00000	1
395	21	2036-09-01	2037-03-01	0.85000	0.00000	1
396	21	2037-03-01	2037-09-01	0.85000	0.00000	1
397	21	2037-09-01	2038-03-01	0.85000	0.00000	1
398	21	2038-03-01	2038-09-01	0.85000	0.00000	1
399	21	2038-09-01	2039-03-01	0.85000	0.00000	1
400	21	2039-03-01	2039-09-01	0.85000	0.00000	1
401	21	2039-09-01	2040-03-01	0.85000	0.00000	1
402	21	2040-03-01	2040-09-01	0.85000	0.00000	1
403	21	2040-09-01	2041-03-01	0.85000	0.00000	1
404	21	2041-03-01	2041-09-01	0.85000	0.00000	1
405	21	2041-09-01	2042-03-01	0.85000	0.00000	1
406	21	2042-03-01	2042-09-01	0.85000	0.00000	1
407	21	2042-09-01	2043-03-01	0.85000	0.00000	1
408	21	2043-03-01	2043-09-01	0.85000	0.00000	1
409	21	2043-09-01	2044-03-01	0.85000	0.00000	1
410	21	2044-03-01	2044-09-01	0.85000	0.00000	1
411	21	2044-09-01	2045-03-01	0.85000	0.00000	1
412	21	2045-03-01	2045-09-01	0.85000	0.00000	1
413	21	2045-09-01	2046-03-01	0.85000	0.00000	1
414	21	2046-03-01	2046-09-01	0.85000	0.00000	1
415	21	2046-09-01	2047-03-01	0.85000	0.00000	1
416	21	2047-03-01	2047-09-01	0.85000	0.00000	1
417	21	2047-09-01	2048-03-01	0.85000	0.00000	1
418	21	2048-03-01	2048-09-01	0.85000	0.00000	1
419	21	2048-09-01	2049-03-01	0.85000	0.00000	1
420	21	2049-03-01	2049-09-01	0.85000	0.00000	1
421	21	2049-09-01	2050-03-01	0.85000	0.00000	1
422	21	2050-03-01	2050-09-01	0.85000	0.00000	1
423	21	2050-09-01	2051-03-01	0.85000	0.00000	1
424	21	2051-03-01	2051-09-01	0.85000	100.00000	1
425	22	2022-01-22	2022-03-01	0.25718	0.00000	1
426	22	2022-03-01	2022-09-01	1.22500	0.00000	1
427	22	2022-09-01	2023-03-01	1.22500	0.00000	1
428	22	2023-03-01	2023-09-01	1.22500	0.00000	1
429	22	2023-09-01	2024-03-01	1.22500	0.00000	1
430	22	2024-03-01	2024-09-01	1.22500	0.00000	1
431	22	2024-09-01	2025-03-01	1.22500	0.00000	1
432	22	2025-03-01	2025-09-01	1.22500	0.00000	1
433	22	2025-09-01	2026-03-01	1.22500	0.00000	1
434	22	2026-03-01	2026-09-01	1.22500	0.00000	1
435	22	2026-09-01	2027-03-01	1.22500	0.00000	1
436	22	2027-03-01	2027-09-01	1.22500	0.00000	1
437	22	2027-09-01	2028-03-01	1.22500	0.00000	1
438	22	2028-03-01	2028-09-01	1.22500	0.00000	1
439	22	2028-09-01	2029-03-01	1.22500	0.00000	1
440	22	2029-03-01	2029-09-01	1.22500	0.00000	1
441	22	2029-09-01	2030-03-01	1.22500	0.00000	1
442	22	2030-03-01	2030-09-01	1.22500	0.00000	1
443	22	2030-09-01	2031-03-01	1.22500	0.00000	1
444	22	2031-03-01	2031-09-01	1.22500	0.00000	1
445	22	2031-09-01	2032-03-01	1.22500	0.00000	1
446	22	2032-03-01	2032-09-01	1.22500	0.00000	1
447	22	2032-09-01	2033-03-01	1.22500	0.00000	1
448	22	2033-03-01	2033-09-01	1.22500	0.00000	1
449	22	2033-09-01	2034-03-01	1.22500	0.00000	1
450	22	2034-03-01	2034-09-01	1.22500	0.00000	1
451	22	2034-09-01	2035-03-01	1.22500	0.00000	1
452	22	2035-03-01	2035-09-01	1.22500	0.00000	1
453	22	2035-09-01	2036-03-01	1.22500	0.00000	1
454	22	2036-03-01	2036-09-01	1.22500	0.00000	1
455	22	2036-09-01	2037-03-01	1.22500	0.00000	1
456	22	2037-03-01	2037-09-01	1.22500	0.00000	1
457	22	2037-09-01	2038-03-01	1.22500	0.00000	1
458	22	2038-03-01	2038-09-01	1.22500	0.00000	1
459	22	2038-09-01	2039-03-01	1.22500	0.00000	1
460	22	2039-03-01	2039-09-01	1.22500	0.00000	1
461	22	2039-09-01	2040-03-01	1.22500	0.00000	1
462	22	2040-03-01	2040-09-01	1.22500	0.00000	1
463	22	2040-09-01	2041-03-01	1.22500	0.00000	1
464	22	2041-03-01	2041-09-01	1.22500	0.00000	1
465	22	2041-09-01	2042-03-01	1.22500	0.00000	1
466	22	2042-03-01	2042-09-01	1.22500	0.00000	1
467	22	2042-09-01	2043-03-01	1.22500	0.00000	1
468	22	2043-03-01	2043-09-01	1.22500	0.00000	1
469	22	2043-09-01	2044-03-01	1.22500	0.00000	1
470	22	2044-03-01	2044-09-01	1.22500	0.00000	1
471	22	2044-09-01	2045-03-01	1.22500	0.00000	1
472	22	2045-03-01	2045-09-01	1.22500	0.00000	1
473	22	2045-09-01	2046-03-01	1.22500	0.00000	1
474	22	2046-03-01	2046-09-01	1.22500	0.00000	1
475	22	2046-09-01	2047-03-01	1.22500	0.00000	1
476	22	2047-03-01	2047-09-01	1.22500	0.00000	1
477	22	2047-09-01	2048-03-01	1.22500	0.00000	1
478	22	2048-03-01	2048-09-01	1.22500	0.00000	1
479	22	2048-09-01	2049-03-01	1.22500	0.00000	1
480	22	2049-03-01	2049-09-01	1.22500	0.00000	1
481	22	2049-09-01	2050-03-01	1.22500	0.00000	1
482	22	2050-03-01	2050-09-01	1.22500	100.00000	1
483	23	2019-02-13	2019-03-01	0.17017	0.00000	1
484	23	2019-03-01	2019-09-01	1.92500	0.00000	1
485	23	2019-09-01	2020-03-01	1.92500	0.00000	1
486	23	2020-03-01	2020-09-01	1.92500	0.00000	1
487	23	2020-09-01	2021-03-01	1.92500	0.00000	1
488	23	2021-03-01	2021-09-01	1.92500	0.00000	1
489	23	2021-09-01	2022-03-01	1.92500	0.00000	1
490	23	2022-03-01	2022-09-01	1.92500	0.00000	1
491	23	2022-09-01	2023-03-01	1.92500	0.00000	1
492	23	2023-03-01	2023-09-01	1.92500	0.00000	1
493	23	2023-09-01	2024-03-01	1.92500	0.00000	1
494	23	2024-03-01	2024-09-01	1.92500	0.00000	1
495	23	2024-09-01	2025-03-01	1.92500	0.00000	1
496	23	2025-03-01	2025-09-01	1.92500	0.00000	1
497	23	2025-09-01	2026-03-01	1.92500	0.00000	1
498	23	2026-03-01	2026-09-01	1.92500	0.00000	1
499	23	2026-09-01	2027-03-01	1.92500	0.00000	1
500	23	2027-03-01	2027-09-01	1.92500	0.00000	1
501	23	2027-09-01	2028-03-01	1.92500	0.00000	1
502	23	2028-03-01	2028-09-01	1.92500	0.00000	1
503	23	2028-09-01	2029-03-01	1.92500	0.00000	1
504	23	2029-03-01	2029-09-01	1.92500	0.00000	1
505	23	2029-09-01	2030-03-01	1.92500	0.00000	1
506	23	2030-03-01	2030-09-01	1.92500	0.00000	1
507	23	2030-09-01	2031-03-01	1.92500	0.00000	1
508	23	2031-03-01	2031-09-01	1.92500	0.00000	1
509	23	2031-09-01	2032-03-01	1.92500	0.00000	1
510	23	2032-03-01	2032-09-01	1.92500	0.00000	1
511	23	2032-09-01	2033-03-01	1.92500	0.00000	1
512	23	2033-03-01	2033-09-01	1.92500	0.00000	1
513	23	2033-09-01	2034-03-01	1.92500	0.00000	1
514	23	2034-03-01	2034-09-01	1.92500	0.00000	1
515	23	2034-09-01	2035-03-01	1.92500	0.00000	1
516	23	2035-03-01	2035-09-01	1.92500	0.00000	1
517	23	2035-09-01	2036-03-01	1.92500	0.00000	1
518	23	2036-03-01	2036-09-01	1.92500	0.00000	1
519	23	2036-09-01	2037-03-01	1.92500	0.00000	1
520	23	2037-03-01	2037-09-01	1.92500	0.00000	1
521	23	2037-09-01	2038-03-01	1.92500	0.00000	1
522	23	2038-03-01	2038-09-01	1.92500	0.00000	1
523	23	2038-09-01	2039-03-01	1.92500	0.00000	1
524	23	2039-03-01	2039-09-01	1.92500	0.00000	1
525	23	2039-09-01	2040-03-01	1.92500	0.00000	1
526	23	2040-03-01	2040-09-01	1.92500	0.00000	1
527	23	2040-09-01	2041-03-01	1.92500	0.00000	1
528	23	2041-03-01	2041-09-01	1.92500	0.00000	1
529	23	2041-09-01	2042-03-01	1.92500	0.00000	1
530	23	2042-03-01	2042-09-01	1.92500	0.00000	1
531	23	2042-09-01	2043-03-01	1.92500	0.00000	1
532	23	2043-03-01	2043-09-01	1.92500	0.00000	1
533	23	2043-09-01	2044-03-01	1.92500	0.00000	1
534	23	2044-03-01	2044-09-01	1.92500	0.00000	1
535	23	2044-09-01	2045-03-01	1.92500	0.00000	1
536	23	2045-03-01	2045-09-01	1.92500	0.00000	1
537	23	2045-09-01	2046-03-01	1.92500	0.00000	1
538	23	2046-03-01	2046-09-01	1.92500	0.00000	1
539	23	2046-09-01	2047-03-01	1.92500	0.00000	1
540	23	2047-03-01	2047-09-01	1.92500	0.00000	1
541	23	2047-09-01	2048-03-01	1.92500	0.00000	1
542	23	2048-03-01	2048-09-01	1.92500	0.00000	1
543	23	2048-09-01	2049-03-01	1.92500	0.00000	1
544	23	2049-03-01	2049-09-01	1.92500	100.00000	1
545	24	2017-03-01	2017-09-01	1.72500	0.00000	1
546	24	2017-09-01	2018-03-01	1.72500	0.00000	1
547	24	2018-03-01	2018-09-01	1.72500	0.00000	1
548	24	2018-09-01	2019-03-01	1.72500	0.00000	1
549	24	2019-03-01	2019-09-01	1.72500	0.00000	1
550	24	2019-09-01	2020-03-01	1.72500	0.00000	1
551	24	2020-03-01	2020-09-01	1.72500	0.00000	1
552	24	2020-09-01	2021-03-01	1.72500	0.00000	1
553	24	2021-03-01	2021-09-01	1.72500	0.00000	1
554	24	2021-09-01	2022-03-01	1.72500	0.00000	1
555	24	2022-03-01	2022-09-01	1.72500	0.00000	1
556	24	2022-09-01	2023-03-01	1.72500	0.00000	1
557	24	2023-03-01	2023-09-01	1.72500	0.00000	1
558	24	2023-09-01	2024-03-01	1.72500	0.00000	1
559	24	2024-03-01	2024-09-01	1.72500	0.00000	1
560	24	2024-09-01	2025-03-01	1.72500	0.00000	1
561	24	2025-03-01	2025-09-01	1.72500	0.00000	1
562	24	2025-09-01	2026-03-01	1.72500	0.00000	1
563	24	2026-03-01	2026-09-01	1.72500	0.00000	1
564	24	2026-09-01	2027-03-01	1.72500	0.00000	1
565	24	2027-03-01	2027-09-01	1.72500	0.00000	1
566	24	2027-09-01	2028-03-01	1.72500	0.00000	1
567	24	2028-03-01	2028-09-01	1.72500	0.00000	1
568	24	2028-09-01	2029-03-01	1.72500	0.00000	1
569	24	2029-03-01	2029-09-01	1.72500	0.00000	1
570	24	2029-09-01	2030-03-01	1.72500	0.00000	1
571	24	2030-03-01	2030-09-01	1.72500	0.00000	1
572	24	2030-09-01	2031-03-01	1.72500	0.00000	1
573	24	2031-03-01	2031-09-01	1.72500	0.00000	1
574	24	2031-09-01	2032-03-01	1.72500	0.00000	1
575	24	2032-03-01	2032-09-01	1.72500	0.00000	1
576	24	2032-09-01	2033-03-01	1.72500	0.00000	1
577	24	2033-03-01	2033-09-01	1.72500	0.00000	1
578	24	2033-09-01	2034-03-01	1.72500	0.00000	1
579	24	2034-03-01	2034-09-01	1.72500	0.00000	1
580	24	2034-09-01	2035-03-01	1.72500	0.00000	1
581	24	2035-03-01	2035-09-01	1.72500	0.00000	1
582	24	2035-09-01	2036-03-01	1.72500	0.00000	1
583	24	2036-03-01	2036-09-01	1.72500	0.00000	1
584	24	2036-09-01	2037-03-01	1.72500	0.00000	1
585	24	2037-03-01	2037-09-01	1.72500	0.00000	1
586	24	2037-09-01	2038-03-01	1.72500	0.00000	1
587	24	2038-03-01	2038-09-01	1.72500	0.00000	1
588	24	2038-09-01	2039-03-01	1.72500	0.00000	1
589	24	2039-03-01	2039-09-01	1.72500	0.00000	1
590	24	2039-09-01	2040-03-01	1.72500	0.00000	1
591	24	2040-03-01	2040-09-01	1.72500	0.00000	1
592	24	2040-09-01	2041-03-01	1.72500	0.00000	1
593	24	2041-03-01	2041-09-01	1.72500	0.00000	1
594	24	2041-09-01	2042-03-01	1.72500	0.00000	1
595	24	2042-03-01	2042-09-01	1.72500	0.00000	1
596	24	2042-09-01	2043-03-01	1.72500	0.00000	1
597	24	2043-03-01	2043-09-01	1.72500	0.00000	1
598	24	2043-09-01	2044-03-01	1.72500	0.00000	1
599	24	2044-03-01	2044-09-01	1.72500	0.00000	1
600	24	2044-09-01	2045-03-01	1.72500	0.00000	1
601	24	2045-03-01	2045-09-01	1.72500	0.00000	1
602	24	2045-09-01	2046-03-01	1.72500	0.00000	1
603	24	2046-03-01	2046-09-01	1.72500	0.00000	1
604	24	2046-09-01	2047-03-01	1.72500	0.00000	1
605	24	2047-03-01	2047-09-01	1.72500	0.00000	1
606	24	2047-09-01	2048-03-01	1.72500	100.00000	1
607	25	2016-02-09	2016-03-01	0.15577	0.00000	1
608	25	2016-03-01	2016-09-01	1.35000	0.00000	1
609	25	2016-09-01	2017-03-01	1.35000	0.00000	1
610	25	2017-03-01	2017-09-01	1.35000	0.00000	1
611	25	2017-09-01	2018-03-01	1.35000	0.00000	1
612	25	2018-03-01	2018-09-01	1.35000	0.00000	1
613	25	2018-09-01	2019-03-01	1.35000	0.00000	1
614	25	2019-03-01	2019-09-01	1.35000	0.00000	1
615	25	2019-09-01	2020-03-01	1.35000	0.00000	1
616	25	2020-03-01	2020-09-01	1.35000	0.00000	1
617	25	2020-09-01	2021-03-01	1.35000	0.00000	1
618	25	2021-03-01	2021-09-01	1.35000	0.00000	1
619	25	2021-09-01	2022-03-01	1.35000	0.00000	1
620	25	2022-03-01	2022-09-01	1.35000	0.00000	1
621	25	2022-09-01	2023-03-01	1.35000	0.00000	1
622	25	2023-03-01	2023-09-01	1.35000	0.00000	1
623	25	2023-09-01	2024-03-01	1.35000	0.00000	1
624	25	2024-03-01	2024-09-01	1.35000	0.00000	1
625	25	2024-09-01	2025-03-01	1.35000	0.00000	1
626	25	2025-03-01	2025-09-01	1.35000	0.00000	1
627	25	2025-09-01	2026-03-01	1.35000	0.00000	1
628	25	2026-03-01	2026-09-01	1.35000	0.00000	1
629	25	2026-09-01	2027-03-01	1.35000	0.00000	1
630	25	2027-03-01	2027-09-01	1.35000	0.00000	1
631	25	2027-09-01	2028-03-01	1.35000	0.00000	1
632	25	2028-03-01	2028-09-01	1.35000	0.00000	1
633	25	2028-09-01	2029-03-01	1.35000	0.00000	1
634	25	2029-03-01	2029-09-01	1.35000	0.00000	1
635	25	2029-09-01	2030-03-01	1.35000	0.00000	1
636	25	2030-03-01	2030-09-01	1.35000	0.00000	1
637	25	2030-09-01	2031-03-01	1.35000	0.00000	1
638	25	2031-03-01	2031-09-01	1.35000	0.00000	1
639	25	2031-09-01	2032-03-01	1.35000	0.00000	1
640	25	2032-03-01	2032-09-01	1.35000	0.00000	1
641	25	2032-09-01	2033-03-01	1.35000	0.00000	1
642	25	2033-03-01	2033-09-01	1.35000	0.00000	1
643	25	2033-09-01	2034-03-01	1.35000	0.00000	1
644	25	2034-03-01	2034-09-01	1.35000	0.00000	1
645	25	2034-09-01	2035-03-01	1.35000	0.00000	1
646	25	2035-03-01	2035-09-01	1.35000	0.00000	1
647	25	2035-09-01	2036-03-01	1.35000	0.00000	1
648	25	2036-03-01	2036-09-01	1.35000	0.00000	1
649	25	2036-09-01	2037-03-01	1.35000	0.00000	1
650	25	2037-03-01	2037-09-01	1.35000	0.00000	1
651	25	2037-09-01	2038-03-01	1.35000	0.00000	1
652	25	2038-03-01	2038-09-01	1.35000	0.00000	1
653	25	2038-09-01	2039-03-01	1.35000	0.00000	1
654	25	2039-03-01	2039-09-01	1.35000	0.00000	1
655	25	2039-09-01	2040-03-01	1.35000	0.00000	1
656	25	2040-03-01	2040-09-01	1.35000	0.00000	1
657	25	2040-09-01	2041-03-01	1.35000	0.00000	1
658	25	2041-03-01	2041-09-01	1.35000	0.00000	1
659	25	2041-09-01	2042-03-01	1.35000	0.00000	1
660	25	2042-03-01	2042-09-01	1.35000	0.00000	1
661	25	2042-09-01	2043-03-01	1.35000	0.00000	1
662	25	2043-03-01	2043-09-01	1.35000	0.00000	1
663	25	2043-09-01	2044-03-01	1.35000	0.00000	1
664	25	2044-03-01	2044-09-01	1.35000	0.00000	1
665	25	2044-09-01	2045-03-01	1.35000	0.00000	1
666	25	2045-03-01	2045-09-01	1.35000	0.00000	1
667	25	2045-09-01	2046-03-01	1.35000	0.00000	1
668	25	2046-03-01	2046-09-01	1.35000	0.00000	1
669	25	2046-09-01	2047-03-01	1.35000	100.00000	1
670	26	2021-03-01	2021-09-01	1.07500	0.00000	1
671	26	2021-09-01	2022-03-01	1.07500	0.00000	1
672	26	2022-03-01	2022-09-01	1.07500	0.00000	1
673	26	2022-09-01	2023-03-01	1.07500	0.00000	1
674	26	2023-03-01	2023-09-01	1.07500	0.00000	1
675	26	2023-09-01	2024-03-01	1.07500	0.00000	1
676	26	2024-03-01	2024-09-01	1.07500	0.00000	1
677	26	2024-09-01	2025-03-01	1.07500	0.00000	1
678	26	2025-03-01	2025-09-01	1.07500	0.00000	1
679	26	2025-09-01	2026-03-01	1.07500	0.00000	1
680	26	2026-03-01	2026-09-01	1.07500	0.00000	1
681	26	2026-09-01	2027-03-01	1.07500	0.00000	1
682	26	2027-03-01	2027-09-01	1.07500	0.00000	1
683	26	2027-09-01	2028-03-01	1.07500	0.00000	1
684	26	2028-03-01	2028-09-01	1.07500	0.00000	1
685	26	2028-09-01	2029-03-01	1.07500	0.00000	1
686	26	2029-03-01	2029-09-01	1.07500	0.00000	1
687	26	2029-09-01	2030-03-01	1.07500	0.00000	1
688	26	2030-03-01	2030-09-01	1.07500	0.00000	1
689	26	2030-09-01	2031-03-01	1.07500	0.00000	1
690	26	2031-03-01	2031-09-01	1.07500	0.00000	1
691	26	2031-09-01	2032-03-01	1.07500	0.00000	1
692	26	2032-03-01	2032-09-01	1.07500	0.00000	1
693	26	2032-09-01	2033-03-01	1.07500	0.00000	1
694	26	2033-03-01	2033-09-01	1.07500	0.00000	1
695	26	2033-09-01	2034-03-01	1.07500	0.00000	1
696	26	2034-03-01	2034-09-01	1.07500	0.00000	1
697	26	2034-09-01	2035-03-01	1.07500	0.00000	1
698	26	2035-03-01	2035-09-01	1.07500	0.00000	1
699	26	2035-09-01	2036-03-01	1.07500	0.00000	1
700	26	2036-03-01	2036-09-01	1.07500	0.00000	1
701	26	2036-09-01	2037-03-01	1.07500	0.00000	1
702	26	2037-03-01	2037-09-01	1.07500	0.00000	1
703	26	2037-09-01	2038-03-01	1.07500	0.00000	1
704	26	2038-03-01	2038-09-01	1.07500	0.00000	1
705	26	2038-09-01	2039-03-01	1.07500	0.00000	1
706	26	2039-03-01	2039-09-01	1.07500	0.00000	1
707	26	2039-09-01	2040-03-01	1.07500	0.00000	1
708	26	2040-03-01	2040-09-01	1.07500	0.00000	1
709	26	2040-09-01	2041-03-01	1.07500	0.00000	1
710	26	2041-03-01	2041-09-01	1.07500	0.00000	1
711	26	2041-09-01	2042-03-01	1.07500	0.00000	1
712	26	2042-03-01	2042-09-01	1.07500	0.00000	1
713	26	2042-09-01	2043-03-01	1.07500	0.00000	1
714	26	2043-03-01	2043-09-01	1.07500	0.00000	1
715	26	2043-09-01	2044-03-01	1.07500	0.00000	1
716	26	2044-03-01	2044-09-01	1.07500	0.00000	1
717	26	2044-09-01	2045-03-01	1.07500	0.00000	1
718	26	2045-03-01	2045-09-01	1.07500	0.00000	1
719	26	2045-09-01	2046-03-01	1.07500	0.00000	1
720	26	2046-03-01	2046-09-01	1.07500	0.00000	1
721	26	2046-09-01	2047-03-01	1.07500	0.00000	1
722	26	2047-03-01	2047-09-01	1.07500	0.00000	1
723	26	2047-09-01	2048-03-01	1.07500	0.00000	1
724	26	2048-03-01	2048-09-01	1.07500	0.00000	1
725	26	2048-09-01	2049-03-01	1.07500	0.00000	1
726	26	2049-03-01	2049-09-01	1.07500	0.00000	1
727	26	2049-09-01	2050-03-01	1.07500	0.00000	1
728	26	2050-03-01	2050-09-01	1.07500	0.00000	1
729	26	2050-09-01	2051-03-01	1.07500	0.00000	1
730	26	2051-03-01	2051-09-01	1.07500	0.00000	1
731	26	2051-09-01	2052-03-01	1.07500	0.00000	1
732	26	2052-03-01	2052-09-01	1.07500	0.00000	1
733	26	2052-09-01	2053-03-01	1.07500	0.00000	1
734	26	2053-03-01	2053-09-01	1.07500	0.00000	1
735	26	2053-09-01	2054-03-01	1.07500	0.00000	1
736	26	2054-03-01	2054-09-01	1.07500	0.00000	1
737	26	2054-09-01	2055-03-01	1.07500	0.00000	1
738	26	2055-03-01	2055-09-01	1.07500	0.00000	1
739	26	2055-09-01	2056-03-01	1.07500	0.00000	1
740	26	2056-03-01	2056-09-01	1.07500	0.00000	1
741	26	2056-09-01	2057-03-01	1.07500	0.00000	1
742	26	2057-03-01	2057-09-01	1.07500	0.00000	1
743	26	2057-09-01	2058-03-01	1.07500	0.00000	1
744	26	2058-03-01	2058-09-01	1.07500	0.00000	1
745	26	2058-09-01	2059-03-01	1.07500	0.00000	1
746	26	2059-03-01	2059-09-01	1.07500	0.00000	1
747	26	2059-09-01	2060-03-01	1.07500	0.00000	1
748	26	2060-03-01	2060-09-01	1.07500	0.00000	1
749	26	2060-09-01	2061-03-01	1.07500	0.00000	1
750	26	2061-03-01	2061-09-01	1.07500	0.00000	1
751	26	2061-09-01	2062-03-01	1.07500	0.00000	1
752	26	2062-03-01	2062-09-01	1.07500	0.00000	1
753	26	2062-09-01	2063-03-01	1.07500	0.00000	1
754	26	2063-03-01	2063-09-01	1.07500	0.00000	1
755	26	2063-09-01	2064-03-01	1.07500	0.00000	1
756	26	2064-03-01	2064-09-01	1.07500	0.00000	1
757	26	2064-09-01	2065-03-01	1.07500	0.00000	1
758	26	2065-03-01	2065-09-01	1.07500	0.00000	1
759	26	2065-09-01	2066-03-01	1.07500	0.00000	1
760	26	2066-03-01	2066-09-01	1.07500	0.00000	1
761	26	2066-09-01	2067-03-01	1.07500	0.00000	1
762	26	2067-03-01	2067-09-01	1.07500	0.00000	1
763	26	2067-09-01	2068-03-01	1.07500	0.00000	1
764	26	2068-03-01	2068-09-01	1.07500	0.00000	1
765	26	2068-09-01	2069-03-01	1.07500	0.00000	1
766	26	2069-03-01	2069-09-01	1.07500	0.00000	1
767	26	2069-09-01	2070-03-01	1.07500	0.00000	1
768	26	2070-03-01	2070-09-01	1.07500	0.00000	1
769	26	2070-09-01	2071-03-01	1.07500	0.00000	1
770	26	2071-03-01	2071-09-01	1.07500	0.00000	1
771	26	2071-09-01	2072-03-01	1.07500	100.00000	1
772	27	2015-01-22	2015-03-01	0.34116	0.00000	1
773	27	2015-03-01	2015-09-01	1.62500	0.00000	1
774	27	2015-09-01	2016-03-01	1.62500	0.00000	1
775	27	2016-03-01	2016-09-01	1.62500	0.00000	1
776	27	2016-09-01	2017-03-01	1.62500	0.00000	1
777	27	2017-03-01	2017-09-01	1.62500	0.00000	1
778	27	2017-09-01	2018-03-01	1.62500	0.00000	1
779	27	2018-03-01	2018-09-01	1.62500	0.00000	1
780	27	2018-09-01	2019-03-01	1.62500	0.00000	1
781	27	2019-03-01	2019-09-01	1.62500	0.00000	1
782	27	2019-09-01	2020-03-01	1.62500	0.00000	1
783	27	2020-03-01	2020-09-01	1.62500	0.00000	1
784	27	2020-09-01	2021-03-01	1.62500	0.00000	1
785	27	2021-03-01	2021-09-01	1.62500	0.00000	1
786	27	2021-09-01	2022-03-01	1.62500	0.00000	1
787	27	2022-03-01	2022-09-01	1.62500	0.00000	1
788	27	2022-09-01	2023-03-01	1.62500	0.00000	1
789	27	2023-03-01	2023-09-01	1.62500	0.00000	1
790	27	2023-09-01	2024-03-01	1.62500	0.00000	1
791	27	2024-03-01	2024-09-01	1.62500	0.00000	1
792	27	2024-09-01	2025-03-01	1.62500	0.00000	1
793	27	2025-03-01	2025-09-01	1.62500	0.00000	1
794	27	2025-09-01	2026-03-01	1.62500	0.00000	1
795	27	2026-03-01	2026-09-01	1.62500	0.00000	1
796	27	2026-09-01	2027-03-01	1.62500	0.00000	1
797	27	2027-03-01	2027-09-01	1.62500	0.00000	1
798	27	2027-09-01	2028-03-01	1.62500	0.00000	1
799	27	2028-03-01	2028-09-01	1.62500	0.00000	1
800	27	2028-09-01	2029-03-01	1.62500	0.00000	1
801	27	2029-03-01	2029-09-01	1.62500	0.00000	1
802	27	2029-09-01	2030-03-01	1.62500	0.00000	1
803	27	2030-03-01	2030-09-01	1.62500	0.00000	1
804	27	2030-09-01	2031-03-01	1.62500	0.00000	1
805	27	2031-03-01	2031-09-01	1.62500	0.00000	1
806	27	2031-09-01	2032-03-01	1.62500	0.00000	1
807	27	2032-03-01	2032-09-01	1.62500	0.00000	1
808	27	2032-09-01	2033-03-01	1.62500	0.00000	1
809	27	2033-03-01	2033-09-01	1.62500	0.00000	1
810	27	2033-09-01	2034-03-01	1.62500	0.00000	1
811	27	2034-03-01	2034-09-01	1.62500	0.00000	1
812	27	2034-09-01	2035-03-01	1.62500	0.00000	1
813	27	2035-03-01	2035-09-01	1.62500	0.00000	1
814	27	2035-09-01	2036-03-01	1.62500	0.00000	1
815	27	2036-03-01	2036-09-01	1.62500	0.00000	1
816	27	2036-09-01	2037-03-01	1.62500	0.00000	1
817	27	2037-03-01	2037-09-01	1.62500	0.00000	1
818	27	2037-09-01	2038-03-01	1.62500	0.00000	1
819	27	2038-03-01	2038-09-01	1.62500	0.00000	1
820	27	2038-09-01	2039-03-01	1.62500	0.00000	1
821	27	2039-03-01	2039-09-01	1.62500	0.00000	1
822	27	2039-09-01	2040-03-01	1.62500	0.00000	1
823	27	2040-03-01	2040-09-01	1.62500	0.00000	1
824	27	2040-09-01	2041-03-01	1.62500	0.00000	1
825	27	2041-03-01	2041-09-01	1.62500	0.00000	1
826	27	2041-09-01	2042-03-01	1.62500	0.00000	1
827	27	2042-03-01	2042-09-01	1.62500	0.00000	1
828	27	2042-09-01	2043-03-01	1.62500	0.00000	1
829	27	2043-03-01	2043-09-01	1.62500	0.00000	1
830	27	2043-09-01	2044-03-01	1.62500	0.00000	1
831	27	2044-03-01	2044-09-01	1.62500	0.00000	1
832	27	2044-09-01	2045-03-01	1.62500	0.00000	1
833	27	2045-03-01	2045-09-01	1.62500	0.00000	1
834	27	2045-09-01	2046-03-01	1.62500	0.00000	1
835	27	2046-03-01	2046-09-01	1.62500	100.00000	1
836	28	2025-01-15	2025-04-30	1.18269	0.00000	1
837	28	2025-04-30	2025-10-30	2.05000	0.00000	1
838	28	2025-10-30	2026-04-30	2.05000	0.00000	1
839	28	2026-04-30	2026-10-30	2.05000	0.00000	1
840	28	2026-10-30	2027-04-30	2.05000	0.00000	1
841	28	2027-04-30	2027-10-30	2.05000	0.00000	1
842	28	2027-10-30	2028-04-30	2.05000	0.00000	1
843	28	2028-04-30	2028-10-30	2.05000	0.00000	1
844	28	2028-10-30	2029-04-30	2.05000	0.00000	1
845	28	2029-04-30	2029-10-30	2.05000	0.00000	1
846	28	2029-10-30	2030-04-30	2.05000	0.00000	1
847	28	2030-04-30	2030-10-30	2.05000	0.00000	1
848	28	2030-10-30	2031-04-30	2.05000	0.00000	1
849	28	2031-04-30	2031-10-30	2.05000	0.00000	1
850	28	2031-10-30	2032-04-30	2.05000	0.00000	1
851	28	2032-04-30	2032-10-30	2.05000	0.00000	1
852	28	2032-10-30	2033-04-30	2.05000	0.00000	1
853	28	2033-04-30	2033-10-30	2.05000	0.00000	1
854	28	2033-10-30	2034-04-30	2.05000	0.00000	1
855	28	2034-04-30	2034-10-30	2.05000	0.00000	1
856	28	2034-10-30	2035-04-30	2.05000	0.00000	1
857	28	2035-04-30	2035-10-30	2.05000	0.00000	1
858	28	2035-10-30	2036-04-30	2.05000	0.00000	1
859	28	2036-04-30	2036-10-30	2.05000	0.00000	1
860	28	2036-10-30	2037-04-30	2.05000	0.00000	1
861	28	2037-04-30	2037-10-30	2.05000	0.00000	1
862	28	2037-10-30	2038-04-30	2.05000	0.00000	1
863	28	2038-04-30	2038-10-30	2.05000	0.00000	1
864	28	2038-10-30	2039-04-30	2.05000	0.00000	1
865	28	2039-04-30	2039-10-30	2.05000	0.00000	1
866	28	2039-10-30	2040-04-30	2.05000	0.00000	1
867	28	2040-04-30	2040-10-30	2.05000	0.00000	1
868	28	2040-10-30	2041-04-30	2.05000	0.00000	1
869	28	2041-04-30	2041-10-30	2.05000	0.00000	1
870	28	2041-10-30	2042-04-30	2.05000	0.00000	1
871	28	2042-04-30	2042-10-30	2.05000	0.00000	1
872	28	2042-10-30	2043-04-30	2.05000	0.00000	1
873	28	2043-04-30	2043-10-30	2.05000	0.00000	1
874	28	2043-10-30	2044-04-30	2.05000	0.00000	1
875	28	2044-04-30	2044-10-30	2.05000	0.00000	1
876	28	2044-10-30	2045-04-30	2.05000	0.00000	1
877	28	2045-04-30	2045-10-30	2.05000	0.00000	1
878	28	2045-10-30	2046-04-30	2.05000	100.00000	1
879	29	2020-10-30	2021-04-30	0.75000	0.00000	1
880	29	2021-04-30	2021-10-30	0.75000	0.00000	1
881	29	2021-10-30	2022-04-30	0.75000	0.00000	1
882	29	2022-04-30	2022-10-30	0.75000	0.00000	1
883	29	2022-10-30	2023-04-30	0.75000	0.00000	1
884	29	2023-04-30	2023-10-30	0.75000	0.00000	1
885	29	2023-10-30	2024-04-30	0.75000	0.00000	1
886	29	2024-04-30	2024-10-30	0.75000	0.00000	1
887	29	2024-10-30	2025-04-30	0.75000	0.00000	1
888	29	2025-04-30	2025-10-30	0.75000	0.00000	1
889	29	2025-10-30	2026-04-30	0.75000	0.00000	1
890	29	2026-04-30	2026-10-30	0.75000	0.00000	1
891	29	2026-10-30	2027-04-30	0.75000	0.00000	1
892	29	2027-04-30	2027-10-30	0.75000	0.00000	1
893	29	2027-10-30	2028-04-30	0.75000	0.00000	1
894	29	2028-04-30	2028-10-30	0.75000	0.00000	1
895	29	2028-10-30	2029-04-30	0.75000	0.00000	1
896	29	2029-04-30	2029-10-30	0.75000	0.00000	1
897	29	2029-10-30	2030-04-30	0.75000	0.00000	1
898	29	2030-04-30	2030-10-30	0.75000	0.00000	1
899	29	2030-10-30	2031-04-30	0.75000	0.00000	1
900	29	2031-04-30	2031-10-30	0.75000	0.00000	1
901	29	2031-10-30	2032-04-30	0.75000	0.00000	1
902	29	2032-04-30	2032-10-30	0.75000	0.00000	1
903	29	2032-10-30	2033-04-30	0.75000	0.00000	1
904	29	2033-04-30	2033-10-30	0.75000	0.00000	1
905	29	2033-10-30	2034-04-30	0.75000	0.00000	1
906	29	2034-04-30	2034-10-30	0.75000	0.00000	1
907	29	2034-10-30	2035-04-30	0.75000	0.00000	1
908	29	2035-04-30	2035-10-30	0.75000	0.00000	1
909	29	2035-10-30	2036-04-30	0.75000	0.00000	1
910	29	2036-04-30	2036-10-30	0.75000	0.00000	1
911	29	2036-10-30	2037-04-30	0.75000	0.00000	1
912	29	2037-04-30	2037-10-30	0.75000	0.00000	1
913	29	2037-10-30	2038-04-30	0.75000	0.00000	1
914	29	2038-04-30	2038-10-30	0.75000	0.00000	1
915	29	2038-10-30	2039-04-30	0.75000	0.00000	1
916	29	2039-04-30	2039-10-30	0.75000	0.00000	1
917	29	2039-10-30	2040-04-30	0.75000	0.00000	1
918	29	2040-04-30	2040-10-30	0.75000	0.00000	1
919	29	2040-10-30	2041-04-30	0.75000	0.00000	1
920	29	2041-04-30	2041-10-30	0.75000	0.00000	1
921	29	2041-10-30	2042-04-30	0.75000	0.00000	1
922	29	2042-04-30	2042-10-30	0.75000	0.00000	1
923	29	2042-10-30	2043-04-30	0.75000	0.00000	1
924	29	2043-04-30	2043-10-30	0.75000	0.00000	1
925	29	2043-10-30	2044-04-30	0.75000	0.00000	1
926	29	2044-04-30	2044-10-30	0.75000	0.00000	1
927	29	2044-10-30	2045-04-30	0.75000	100.00000	1
928	30	2013-03-01	2013-09-01	2.37500	0.00000	1
929	30	2013-09-01	2014-03-01	2.37500	0.00000	1
930	30	2014-03-01	2014-09-01	2.37500	0.00000	1
931	30	2014-09-01	2015-03-01	2.37500	0.00000	1
932	30	2015-03-01	2015-09-01	2.37500	0.00000	1
933	30	2015-09-01	2016-03-01	2.37500	0.00000	1
934	30	2016-03-01	2016-09-01	2.37500	0.00000	1
935	30	2016-09-01	2017-03-01	2.37500	0.00000	1
936	30	2017-03-01	2017-09-01	2.37500	0.00000	1
937	30	2017-09-01	2018-03-01	2.37500	0.00000	1
938	30	2018-03-01	2018-09-01	2.37500	0.00000	1
939	30	2018-09-01	2019-03-01	2.37500	0.00000	1
940	30	2019-03-01	2019-09-01	2.37500	0.00000	1
941	30	2019-09-01	2020-03-01	2.37500	0.00000	1
942	30	2020-03-01	2020-09-01	2.37500	0.00000	1
943	30	2020-09-01	2021-03-01	2.37500	0.00000	1
944	30	2021-03-01	2021-09-01	2.37500	0.00000	1
945	30	2021-09-01	2022-03-01	2.37500	0.00000	1
946	30	2022-03-01	2022-09-01	2.37500	0.00000	1
947	30	2022-09-01	2023-03-01	2.37500	0.00000	1
948	30	2023-03-01	2023-09-01	2.37500	0.00000	1
949	30	2023-09-01	2024-03-01	2.37500	0.00000	1
950	30	2024-03-01	2024-09-01	2.37500	0.00000	1
951	30	2024-09-01	2025-03-01	2.37500	0.00000	1
952	30	2025-03-01	2025-09-01	2.37500	0.00000	1
953	30	2025-09-01	2026-03-01	2.37500	0.00000	1
954	30	2026-03-01	2026-09-01	2.37500	0.00000	1
955	30	2026-09-01	2027-03-01	2.37500	0.00000	1
956	30	2027-03-01	2027-09-01	2.37500	0.00000	1
957	30	2027-09-01	2028-03-01	2.37500	0.00000	1
958	30	2028-03-01	2028-09-01	2.37500	0.00000	1
959	30	2028-09-01	2029-03-01	2.37500	0.00000	1
960	30	2029-03-01	2029-09-01	2.37500	0.00000	1
961	30	2029-09-01	2030-03-01	2.37500	0.00000	1
962	30	2030-03-01	2030-09-01	2.37500	0.00000	1
963	30	2030-09-01	2031-03-01	2.37500	0.00000	1
964	30	2031-03-01	2031-09-01	2.37500	0.00000	1
965	30	2031-09-01	2032-03-01	2.37500	0.00000	1
966	30	2032-03-01	2032-09-01	2.37500	0.00000	1
967	30	2032-09-01	2033-03-01	2.37500	0.00000	1
968	30	2033-03-01	2033-09-01	2.37500	0.00000	1
969	30	2033-09-01	2034-03-01	2.37500	0.00000	1
970	30	2034-03-01	2034-09-01	2.37500	0.00000	1
971	30	2034-09-01	2035-03-01	2.37500	0.00000	1
972	30	2035-03-01	2035-09-01	2.37500	0.00000	1
973	30	2035-09-01	2036-03-01	2.37500	0.00000	1
974	30	2036-03-01	2036-09-01	2.37500	0.00000	1
975	30	2036-09-01	2037-03-01	2.37500	0.00000	1
976	30	2037-03-01	2037-09-01	2.37500	0.00000	1
977	30	2037-09-01	2038-03-01	2.37500	0.00000	1
978	30	2038-03-01	2038-09-01	2.37500	0.00000	1
979	30	2038-09-01	2039-03-01	2.37500	0.00000	1
980	30	2039-03-01	2039-09-01	2.37500	0.00000	1
981	30	2039-09-01	2040-03-01	2.37500	0.00000	1
982	30	2040-03-01	2040-09-01	2.37500	0.00000	1
983	30	2040-09-01	2041-03-01	2.37500	0.00000	1
984	30	2041-03-01	2041-09-01	2.37500	0.00000	1
985	30	2041-09-01	2042-03-01	2.37500	0.00000	1
986	30	2042-03-01	2042-09-01	2.37500	0.00000	1
987	30	2042-09-01	2043-03-01	2.37500	0.00000	1
988	30	2043-03-01	2043-09-01	2.37500	0.00000	1
989	30	2043-09-01	2044-03-01	2.37500	0.00000	1
990	30	2044-03-01	2044-09-01	2.37500	100.00000	1
991	31	2022-09-01	2023-03-01	2.22500	0.00000	1
992	31	2023-03-01	2023-09-01	2.22500	0.00000	1
993	31	2023-09-01	2024-03-01	2.22500	0.00000	1
994	31	2024-03-01	2024-09-01	2.22500	0.00000	1
995	31	2024-09-01	2025-03-01	2.22500	0.00000	1
996	31	2025-03-01	2025-09-01	2.22500	0.00000	1
997	31	2025-09-01	2026-03-01	2.22500	0.00000	1
998	31	2026-03-01	2026-09-01	2.22500	0.00000	1
999	31	2026-09-01	2027-03-01	2.22500	0.00000	1
1000	31	2027-03-01	2027-09-01	2.22500	0.00000	1
1001	31	2027-09-01	2028-03-01	2.22500	0.00000	1
1002	31	2028-03-01	2028-09-01	2.22500	0.00000	1
1003	31	2028-09-01	2029-03-01	2.22500	0.00000	1
1004	31	2029-03-01	2029-09-01	2.22500	0.00000	1
1005	31	2029-09-01	2030-03-01	2.22500	0.00000	1
1006	31	2030-03-01	2030-09-01	2.22500	0.00000	1
1007	31	2030-09-01	2031-03-01	2.22500	0.00000	1
1008	31	2031-03-01	2031-09-01	2.22500	0.00000	1
1009	31	2031-09-01	2032-03-01	2.22500	0.00000	1
1010	31	2032-03-01	2032-09-01	2.22500	0.00000	1
1011	31	2032-09-01	2033-03-01	2.22500	0.00000	1
1012	31	2033-03-01	2033-09-01	2.22500	0.00000	1
1013	31	2033-09-01	2034-03-01	2.22500	0.00000	1
1014	31	2034-03-01	2034-09-01	2.22500	0.00000	1
1015	31	2034-09-01	2035-03-01	2.22500	0.00000	1
1016	31	2035-03-01	2035-09-01	2.22500	0.00000	1
1017	31	2035-09-01	2036-03-01	2.22500	0.00000	1
1018	31	2036-03-01	2036-09-01	2.22500	0.00000	1
1019	31	2036-09-01	2037-03-01	2.22500	0.00000	1
1020	31	2037-03-01	2037-09-01	2.22500	0.00000	1
1021	31	2037-09-01	2038-03-01	2.22500	0.00000	1
1022	31	2038-03-01	2038-09-01	2.22500	0.00000	1
1023	31	2038-09-01	2039-03-01	2.22500	0.00000	1
1024	31	2039-03-01	2039-09-01	2.22500	0.00000	1
1025	31	2039-09-01	2040-03-01	2.22500	0.00000	1
1026	31	2040-03-01	2040-09-01	2.22500	0.00000	1
1027	31	2040-09-01	2041-03-01	2.22500	0.00000	1
1028	31	2041-03-01	2041-09-01	2.22500	0.00000	1
1029	31	2041-09-01	2042-03-01	2.22500	0.00000	1
1030	31	2042-03-01	2042-09-01	2.22500	0.00000	1
1031	31	2042-09-01	2043-03-01	2.22500	0.00000	1
1032	31	2043-03-01	2043-09-01	2.22500	100.00000	1
1033	32	2020-09-11	2021-03-01	0.85028	0.00000	1
1034	32	2021-03-01	2021-09-01	0.90000	0.00000	1
1035	32	2021-09-01	2022-03-01	0.90000	0.00000	1
1036	32	2022-03-01	2022-09-01	0.90000	0.00000	1
1037	32	2022-09-01	2023-03-01	0.90000	0.00000	1
1038	32	2023-03-01	2023-09-01	0.90000	0.00000	1
1039	32	2023-09-01	2024-03-01	0.90000	0.00000	1
1040	32	2024-03-01	2024-09-01	0.90000	0.00000	1
1041	32	2024-09-01	2025-03-01	0.90000	0.00000	1
1042	32	2025-03-01	2025-09-01	0.90000	0.00000	1
1043	32	2025-09-01	2026-03-01	0.90000	0.00000	1
1044	32	2026-03-01	2026-09-01	0.90000	0.00000	1
1045	32	2026-09-01	2027-03-01	0.90000	0.00000	1
1046	32	2027-03-01	2027-09-01	0.90000	0.00000	1
1047	32	2027-09-01	2028-03-01	0.90000	0.00000	1
1048	32	2028-03-01	2028-09-01	0.90000	0.00000	1
1049	32	2028-09-01	2029-03-01	0.90000	0.00000	1
1050	32	2029-03-01	2029-09-01	0.90000	0.00000	1
1051	32	2029-09-01	2030-03-01	0.90000	0.00000	1
1052	32	2030-03-01	2030-09-01	0.90000	0.00000	1
1053	32	2030-09-01	2031-03-01	0.90000	0.00000	1
1054	32	2031-03-01	2031-09-01	0.90000	0.00000	1
1055	32	2031-09-01	2032-03-01	0.90000	0.00000	1
1056	32	2032-03-01	2032-09-01	0.90000	0.00000	1
1057	32	2032-09-01	2033-03-01	0.90000	0.00000	1
1058	32	2033-03-01	2033-09-01	0.90000	0.00000	1
1059	32	2033-09-01	2034-03-01	0.90000	0.00000	1
1060	32	2034-03-01	2034-09-01	0.90000	0.00000	1
1061	32	2034-09-01	2035-03-01	0.90000	0.00000	1
1062	32	2035-03-01	2035-09-01	0.90000	0.00000	1
1063	32	2035-09-01	2036-03-01	0.90000	0.00000	1
1064	32	2036-03-01	2036-09-01	0.90000	0.00000	1
1065	32	2036-09-01	2037-03-01	0.90000	0.00000	1
1066	32	2037-03-01	2037-09-01	0.90000	0.00000	1
1067	32	2037-09-01	2038-03-01	0.90000	0.00000	1
1068	32	2038-03-01	2038-09-01	0.90000	0.00000	1
1069	32	2038-09-01	2039-03-01	0.90000	0.00000	1
1070	32	2039-03-01	2039-09-01	0.90000	0.00000	1
1071	32	2039-09-01	2040-03-01	0.90000	0.00000	1
1072	32	2040-03-01	2040-09-01	0.90000	0.00000	1
1073	32	2040-09-01	2041-03-01	0.90000	100.00000	1
1074	33	2025-02-18	2025-04-01	0.44423	0.00000	1
1075	33	2025-04-01	2025-10-01	1.92500	0.00000	1
1076	33	2025-10-01	2026-04-01	1.92500	0.00000	1
1077	33	2026-04-01	2026-10-01	1.92500	0.00000	1
1078	33	2026-10-01	2027-04-01	1.92500	0.00000	1
1079	33	2027-04-01	2027-10-01	1.92500	0.00000	1
1080	33	2027-10-01	2028-04-01	1.92500	0.00000	1
1081	33	2028-04-01	2028-10-01	1.92500	0.00000	1
1082	33	2028-10-01	2029-04-01	1.92500	0.00000	1
1083	33	2029-04-01	2029-10-01	1.92500	0.00000	1
1084	33	2029-10-01	2030-04-01	1.92500	0.00000	1
1085	33	2030-04-01	2030-10-01	1.92500	0.00000	1
1086	33	2030-10-01	2031-04-01	1.92500	0.00000	1
1087	33	2031-04-01	2031-10-01	1.92500	0.00000	1
1088	33	2031-10-01	2032-04-01	1.92500	0.00000	1
1089	33	2032-04-01	2032-10-01	1.92500	0.00000	1
1090	33	2032-10-01	2033-04-01	1.92500	0.00000	1
1091	33	2033-04-01	2033-10-01	1.92500	0.00000	1
1092	33	2033-10-01	2034-04-01	1.92500	0.00000	1
1093	33	2034-04-01	2034-10-01	1.92500	0.00000	1
1094	33	2034-10-01	2035-04-01	1.92500	0.00000	1
1095	33	2035-04-01	2035-10-01	1.92500	0.00000	1
1096	33	2035-10-01	2036-04-01	1.92500	0.00000	1
1097	33	2036-04-01	2036-10-01	1.92500	0.00000	1
1098	33	2036-10-01	2037-04-01	1.92500	0.00000	1
1099	33	2037-04-01	2037-10-01	1.92500	0.00000	1
1100	33	2037-10-01	2038-04-01	1.92500	0.00000	1
1101	33	2038-04-01	2038-10-01	1.92500	0.00000	1
1102	33	2038-10-01	2039-04-01	1.92500	0.00000	1
1103	33	2039-04-01	2039-10-01	1.92500	0.00000	1
1104	33	2039-10-01	2040-04-01	1.92500	0.00000	1
1105	33	2040-04-01	2040-10-01	1.92500	100.00000	1
1106	34	2009-09-01	2010-03-01	2.50000	0.00000	1
1107	34	2010-03-01	2010-09-01	2.50000	0.00000	1
1108	34	2010-09-01	2011-03-01	2.50000	0.00000	1
1109	34	2011-03-01	2011-09-01	2.50000	0.00000	1
1110	34	2011-09-01	2012-03-01	2.50000	0.00000	1
1111	34	2012-03-01	2012-09-01	2.50000	0.00000	1
1112	34	2012-09-01	2013-03-01	2.50000	0.00000	1
1113	34	2013-03-01	2013-09-01	2.50000	0.00000	1
1114	34	2013-09-01	2014-03-01	2.50000	0.00000	1
1115	34	2014-03-01	2014-09-01	2.50000	0.00000	1
1116	34	2014-09-01	2015-03-01	2.50000	0.00000	1
1117	34	2015-03-01	2015-09-01	2.50000	0.00000	1
1118	34	2015-09-01	2016-03-01	2.50000	0.00000	1
1119	34	2016-03-01	2016-09-01	2.50000	0.00000	1
1120	34	2016-09-01	2017-03-01	2.50000	0.00000	1
1121	34	2017-03-01	2017-09-01	2.50000	0.00000	1
1122	34	2017-09-01	2018-03-01	2.50000	0.00000	1
1123	34	2018-03-01	2018-09-01	2.50000	0.00000	1
1124	34	2018-09-01	2019-03-01	2.50000	0.00000	1
1125	34	2019-03-01	2019-09-01	2.50000	0.00000	1
1126	34	2019-09-01	2020-03-01	2.50000	0.00000	1
1127	34	2020-03-01	2020-09-01	2.50000	0.00000	1
1128	34	2020-09-01	2021-03-01	2.50000	0.00000	1
1129	34	2021-03-01	2021-09-01	2.50000	0.00000	1
1130	34	2021-09-01	2022-03-01	2.50000	0.00000	1
1131	34	2022-03-01	2022-09-01	2.50000	0.00000	1
1132	34	2022-09-01	2023-03-01	2.50000	0.00000	1
1133	34	2023-03-01	2023-09-01	2.50000	0.00000	1
1134	34	2023-09-01	2024-03-01	2.50000	0.00000	1
1135	34	2024-03-01	2024-09-01	2.50000	0.00000	1
1136	34	2024-09-01	2025-03-01	2.50000	0.00000	1
1137	34	2025-03-01	2025-09-01	2.50000	0.00000	1
1138	34	2025-09-01	2026-03-01	2.50000	0.00000	1
1139	34	2026-03-01	2026-09-01	2.50000	0.00000	1
1140	34	2026-09-01	2027-03-01	2.50000	0.00000	1
1141	34	2027-03-01	2027-09-01	2.50000	0.00000	1
1142	34	2027-09-01	2028-03-01	2.50000	0.00000	1
1143	34	2028-03-01	2028-09-01	2.50000	0.00000	1
1144	34	2028-09-01	2029-03-01	2.50000	0.00000	1
1145	34	2029-03-01	2029-09-01	2.50000	0.00000	1
1146	34	2029-09-01	2030-03-01	2.50000	0.00000	1
1147	34	2030-03-01	2030-09-01	2.50000	0.00000	1
1148	34	2030-09-01	2031-03-01	2.50000	0.00000	1
1149	34	2031-03-01	2031-09-01	2.50000	0.00000	1
1150	34	2031-09-01	2032-03-01	2.50000	0.00000	1
1151	34	2032-03-01	2032-09-01	2.50000	0.00000	1
1152	34	2032-09-01	2033-03-01	2.50000	0.00000	1
1153	34	2033-03-01	2033-09-01	2.50000	0.00000	1
1154	34	2033-09-01	2034-03-01	2.50000	0.00000	1
1155	34	2034-03-01	2034-09-01	2.50000	0.00000	1
1156	34	2034-09-01	2035-03-01	2.50000	0.00000	1
1157	34	2035-03-01	2035-09-01	2.50000	0.00000	1
1158	34	2035-09-01	2036-03-01	2.50000	0.00000	1
1159	34	2036-03-01	2036-09-01	2.50000	0.00000	1
1160	34	2036-09-01	2037-03-01	2.50000	0.00000	1
1161	34	2037-03-01	2037-09-01	2.50000	0.00000	1
1162	34	2037-09-01	2038-03-01	2.50000	0.00000	1
1163	34	2038-03-01	2038-09-01	2.50000	0.00000	1
1164	34	2038-09-01	2039-03-01	2.50000	0.00000	1
1165	34	2039-03-01	2039-09-01	2.50000	0.00000	1
1166	34	2039-09-01	2040-03-01	2.50000	0.00000	1
1167	34	2040-03-01	2040-09-01	2.50000	100.00000	1
1168	35	2019-06-19	2019-09-01	0.62337	0.00000	1
1169	35	2019-09-01	2020-03-01	1.55000	0.00000	1
1170	35	2020-03-01	2020-09-01	1.55000	0.00000	1
1171	35	2020-09-01	2021-03-01	1.55000	0.00000	1
1172	35	2021-03-01	2021-09-01	1.55000	0.00000	1
1173	35	2021-09-01	2022-03-01	1.55000	0.00000	1
1174	35	2022-03-01	2022-09-01	1.55000	0.00000	1
1175	35	2022-09-01	2023-03-01	1.55000	0.00000	1
1176	35	2023-03-01	2023-09-01	1.55000	0.00000	1
1177	35	2023-09-01	2024-03-01	1.55000	0.00000	1
1178	35	2024-03-01	2024-09-01	1.55000	0.00000	1
1179	35	2024-09-01	2025-03-01	1.55000	0.00000	1
1180	35	2025-03-01	2025-09-01	1.55000	0.00000	1
1181	35	2025-09-01	2026-03-01	1.55000	0.00000	1
1182	35	2026-03-01	2026-09-01	1.55000	0.00000	1
1183	35	2026-09-01	2027-03-01	1.55000	0.00000	1
1184	35	2027-03-01	2027-09-01	1.55000	0.00000	1
1185	35	2027-09-01	2028-03-01	1.55000	0.00000	1
1186	35	2028-03-01	2028-09-01	1.55000	0.00000	1
1187	35	2028-09-01	2029-03-01	1.55000	0.00000	1
1188	35	2029-03-01	2029-09-01	1.55000	0.00000	1
1189	35	2029-09-01	2030-03-01	1.55000	0.00000	1
1190	35	2030-03-01	2030-09-01	1.55000	0.00000	1
1191	35	2030-09-01	2031-03-01	1.55000	0.00000	1
1192	35	2031-03-01	2031-09-01	1.55000	0.00000	1
1193	35	2031-09-01	2032-03-01	1.55000	0.00000	1
1194	35	2032-03-01	2032-09-01	1.55000	0.00000	1
1195	35	2032-09-01	2033-03-01	1.55000	0.00000	1
1196	35	2033-03-01	2033-09-01	1.55000	0.00000	1
1197	35	2033-09-01	2034-03-01	1.55000	0.00000	1
1198	35	2034-03-01	2034-09-01	1.55000	0.00000	1
1199	35	2034-09-01	2035-03-01	1.55000	0.00000	1
1200	35	2035-03-01	2035-09-01	1.55000	0.00000	1
1201	35	2035-09-01	2036-03-01	1.55000	0.00000	1
1202	35	2036-03-01	2036-09-01	1.55000	0.00000	1
1203	35	2036-09-01	2037-03-01	1.55000	0.00000	1
1204	35	2037-03-01	2037-09-01	1.55000	0.00000	1
1205	35	2037-09-01	2038-03-01	1.55000	0.00000	1
1206	35	2038-03-01	2038-09-01	1.55000	0.00000	1
1207	35	2038-09-01	2039-03-01	1.55000	0.00000	1
1208	35	2039-03-01	2039-09-01	1.55000	0.00000	1
1209	35	2039-09-01	2040-03-01	1.55000	100.00000	1
1210	36	2023-10-01	2024-04-01	2.07500	0.00000	1
1211	36	2024-04-01	2024-10-01	2.07500	0.00000	1
1212	36	2024-10-01	2025-04-01	2.07500	0.00000	1
1213	36	2025-04-01	2025-10-01	2.07500	0.00000	1
1214	36	2025-10-01	2026-04-01	2.07500	0.00000	1
1215	36	2026-04-01	2026-10-01	2.07500	0.00000	1
1216	36	2026-10-01	2027-04-01	2.07500	0.00000	1
1217	36	2027-04-01	2027-10-01	2.07500	0.00000	1
1218	36	2027-10-01	2028-04-01	2.07500	0.00000	1
1219	36	2028-04-01	2028-10-01	2.07500	0.00000	1
1220	36	2028-10-01	2029-04-01	2.07500	0.00000	1
1221	36	2029-04-01	2029-10-01	2.07500	0.00000	1
1222	36	2029-10-01	2030-04-01	2.07500	0.00000	1
1223	36	2030-04-01	2030-10-01	2.07500	0.00000	1
1224	36	2030-10-01	2031-04-01	2.07500	0.00000	1
1225	36	2031-04-01	2031-10-01	2.07500	0.00000	1
1226	36	2031-10-01	2032-04-01	2.07500	0.00000	1
1227	36	2032-04-01	2032-10-01	2.07500	0.00000	1
1228	36	2032-10-01	2033-04-01	2.07500	0.00000	1
1229	36	2033-04-01	2033-10-01	2.07500	0.00000	1
1230	36	2033-10-01	2034-04-01	2.07500	0.00000	1
1231	36	2034-04-01	2034-10-01	2.07500	0.00000	1
1232	36	2034-10-01	2035-04-01	2.07500	0.00000	1
1233	36	2035-04-01	2035-10-01	2.07500	0.00000	1
1234	36	2035-10-01	2036-04-01	2.07500	0.00000	1
1235	36	2036-04-01	2036-10-01	2.07500	0.00000	1
1236	36	2036-10-01	2037-04-01	2.07500	0.00000	1
1237	36	2037-04-01	2037-10-01	2.07500	0.00000	1
1238	36	2037-10-01	2038-04-01	2.07500	0.00000	1
1239	36	2038-04-01	2038-10-01	2.07500	0.00000	1
1240	36	2038-10-01	2039-04-01	2.07500	0.00000	1
1241	36	2039-04-01	2039-10-01	2.07500	100.00000	1
1242	37	2021-04-27	2021-10-27	0.37500	0.00000	1
1243	37	2021-10-27	2022-04-27	0.37500	0.00000	1
1244	37	2022-04-27	2022-10-27	0.37500	0.00000	1
1245	37	2022-10-27	2023-04-27	0.37500	0.00000	1
1246	37	2023-04-27	2023-10-27	0.37500	0.00000	1
1247	37	2023-10-27	2024-04-27	0.37500	0.00000	1
1248	37	2024-04-27	2024-10-27	0.37500	0.00000	1
1249	37	2024-10-27	2025-04-27	0.37500	0.00000	1
1250	37	2025-04-27	2025-10-27	0.60000	0.00000	1
1251	37	2025-10-27	2026-04-27	0.60000	0.00000	1
1252	37	2026-04-27	2026-10-27	0.60000	0.00000	1
1253	37	2026-10-27	2027-04-27	0.60000	0.00000	1
1254	37	2027-04-27	2027-10-27	0.60000	0.00000	1
1255	37	2027-10-27	2028-04-27	0.60000	0.00000	1
1256	37	2028-04-27	2028-10-27	0.60000	0.00000	1
1257	37	2028-10-27	2029-04-27	0.60000	0.00000	1
1258	37	2029-04-27	2029-10-27	0.82500	0.00000	1
1259	37	2029-10-27	2030-04-27	0.82500	0.00000	1
1260	37	2030-04-27	2030-10-27	0.82500	0.00000	1
1261	37	2030-10-27	2031-04-27	0.82500	0.00000	1
1262	37	2031-04-27	2031-10-27	0.82500	0.00000	1
1263	37	2031-10-27	2032-04-27	0.82500	0.00000	1
1264	37	2032-04-27	2032-10-27	0.82500	0.00000	1
1265	37	2032-10-27	2033-04-27	0.82500	0.00000	1
1266	37	2033-04-27	2033-10-27	1.00000	0.00000	1
1267	37	2033-10-27	2034-04-27	1.00000	0.00000	1
1268	37	2034-04-27	2034-10-27	1.00000	0.00000	1
1269	37	2034-10-27	2035-04-27	1.00000	0.00000	1
1270	37	2035-04-27	2035-10-27	1.00000	0.00000	1
1271	37	2035-10-27	2036-04-27	1.00000	0.00000	1
1272	37	2036-04-27	2036-10-27	1.00000	0.00000	1
1273	37	2036-10-27	2037-04-27	1.00000	0.00000	1
1274	38	2007-08-01	2008-02-01	2.50000	0.00000	1
1275	38	2008-02-01	2008-08-01	2.50000	0.00000	1
1276	38	2008-08-01	2009-02-01	2.50000	0.00000	1
1277	38	2009-02-01	2009-08-01	2.50000	0.00000	1
1278	38	2009-08-01	2010-02-01	2.50000	0.00000	1
1279	38	2010-02-01	2010-08-01	2.50000	0.00000	1
1280	38	2010-08-01	2011-02-01	2.50000	0.00000	1
1281	38	2011-02-01	2011-08-01	2.50000	0.00000	1
1282	38	2011-08-01	2012-02-01	2.50000	0.00000	1
1283	38	2012-02-01	2012-08-01	2.50000	0.00000	1
1284	38	2012-08-01	2013-02-01	2.50000	0.00000	1
1285	38	2013-02-01	2013-08-01	2.50000	0.00000	1
1286	38	2013-08-01	2014-02-01	2.50000	0.00000	1
1287	38	2014-02-01	2014-08-01	2.50000	0.00000	1
1288	38	2014-08-01	2015-02-01	2.50000	0.00000	1
1289	38	2015-02-01	2015-08-01	2.50000	0.00000	1
1290	38	2015-08-01	2016-02-01	2.50000	0.00000	1
1291	38	2016-02-01	2016-08-01	2.50000	0.00000	1
1292	38	2016-08-01	2017-02-01	2.50000	0.00000	1
1293	38	2017-02-01	2017-08-01	2.50000	0.00000	1
1294	38	2017-08-01	2018-02-01	2.50000	0.00000	1
1295	38	2018-02-01	2018-08-01	2.50000	0.00000	1
1296	38	2018-08-01	2019-02-01	2.50000	0.00000	1
1297	38	2019-02-01	2019-08-01	2.50000	0.00000	1
1298	38	2019-08-01	2020-02-01	2.50000	0.00000	1
1299	38	2020-02-01	2020-08-01	2.50000	0.00000	1
1300	38	2020-08-01	2021-02-01	2.50000	0.00000	1
1301	38	2021-02-01	2021-08-01	2.50000	0.00000	1
1302	38	2021-08-01	2022-02-01	2.50000	0.00000	1
1303	38	2022-02-01	2022-08-01	2.50000	0.00000	1
1304	38	2022-08-01	2023-02-01	2.50000	0.00000	1
1305	38	2023-02-01	2023-08-01	2.50000	0.00000	1
1306	38	2023-08-01	2024-02-01	2.50000	0.00000	1
1307	38	2024-02-01	2024-08-01	2.50000	0.00000	1
1308	38	2024-08-01	2025-02-01	2.50000	0.00000	1
1309	38	2025-02-01	2025-08-01	2.50000	0.00000	1
1310	38	2025-08-01	2026-02-01	2.50000	0.00000	1
1311	38	2026-02-01	2026-08-01	2.50000	0.00000	1
1312	38	2026-08-01	2027-02-01	2.50000	0.00000	1
1313	38	2027-02-01	2027-08-01	2.50000	0.00000	1
1314	38	2027-08-01	2028-02-01	2.50000	0.00000	1
1315	38	2028-02-01	2028-08-01	2.50000	0.00000	1
1316	38	2028-08-01	2029-02-01	2.50000	0.00000	1
1317	38	2029-02-01	2029-08-01	2.50000	0.00000	1
1318	38	2029-08-01	2030-02-01	2.50000	0.00000	1
1319	38	2030-02-01	2030-08-01	2.50000	0.00000	1
1320	38	2030-08-01	2031-02-01	2.50000	0.00000	1
1321	38	2031-02-01	2031-08-01	2.50000	0.00000	1
1322	38	2031-08-01	2032-02-01	2.50000	0.00000	1
1323	38	2032-02-01	2032-08-01	2.50000	0.00000	1
1324	38	2032-08-01	2033-02-01	2.50000	0.00000	1
1325	38	2033-02-01	2033-08-01	2.50000	0.00000	1
1326	38	2033-08-01	2034-02-01	2.50000	0.00000	1
1327	38	2034-02-01	2034-08-01	2.50000	0.00000	1
1328	38	2034-08-01	2035-02-01	2.50000	0.00000	1
1329	38	2035-02-01	2035-08-01	2.50000	0.00000	1
1330	38	2035-08-01	2036-02-01	2.50000	0.00000	1
1331	38	2036-02-01	2036-08-01	2.50000	0.00000	1
1332	38	2036-08-01	2037-02-01	2.50000	0.00000	1
1333	38	2037-02-01	2037-08-01	2.50000	0.00000	1
1334	38	2037-08-01	2038-02-01	2.50000	0.00000	1
1335	38	2038-02-01	2038-08-01	2.50000	0.00000	1
1336	38	2038-08-01	2039-02-01	2.50000	0.00000	1
1337	38	2039-02-01	2039-08-01	2.50000	100.00000	1
1338	39	2017-09-01	2018-03-01	1.47500	0.00000	1
1339	39	2018-03-01	2018-09-01	1.47500	0.00000	1
1340	39	2018-09-01	2019-03-01	1.47500	0.00000	1
1341	39	2019-03-01	2019-09-01	1.47500	0.00000	1
1342	39	2019-09-01	2020-03-01	1.47500	0.00000	1
1343	39	2020-03-01	2020-09-01	1.47500	0.00000	1
1344	39	2020-09-01	2021-03-01	1.47500	0.00000	1
1345	39	2021-03-01	2021-09-01	1.47500	0.00000	1
1346	39	2021-09-01	2022-03-01	1.47500	0.00000	1
1347	39	2022-03-01	2022-09-01	1.47500	0.00000	1
1348	39	2022-09-01	2023-03-01	1.47500	0.00000	1
1349	39	2023-03-01	2023-09-01	1.47500	0.00000	1
1350	39	2023-09-01	2024-03-01	1.47500	0.00000	1
1351	39	2024-03-01	2024-09-01	1.47500	0.00000	1
1352	39	2024-09-01	2025-03-01	1.47500	0.00000	1
1353	39	2025-03-01	2025-09-01	1.47500	0.00000	1
1354	39	2025-09-01	2026-03-01	1.47500	0.00000	1
1355	39	2026-03-01	2026-09-01	1.47500	0.00000	1
1356	39	2026-09-01	2027-03-01	1.47500	0.00000	1
1357	39	2027-03-01	2027-09-01	1.47500	0.00000	1
1358	39	2027-09-01	2028-03-01	1.47500	0.00000	1
1359	39	2028-03-01	2028-09-01	1.47500	0.00000	1
1360	39	2028-09-01	2029-03-01	1.47500	0.00000	1
1361	39	2029-03-01	2029-09-01	1.47500	0.00000	1
1362	39	2029-09-01	2030-03-01	1.47500	0.00000	1
1363	39	2030-03-01	2030-09-01	1.47500	0.00000	1
1364	39	2030-09-01	2031-03-01	1.47500	0.00000	1
1365	39	2031-03-01	2031-09-01	1.47500	0.00000	1
1366	39	2031-09-01	2032-03-01	1.47500	0.00000	1
1367	39	2032-03-01	2032-09-01	1.47500	0.00000	1
1368	39	2032-09-01	2033-03-01	1.47500	0.00000	1
1369	39	2033-03-01	2033-09-01	1.47500	0.00000	1
1370	39	2033-09-01	2034-03-01	1.47500	0.00000	1
1371	39	2034-03-01	2034-09-01	1.47500	0.00000	1
1372	39	2034-09-01	2035-03-01	1.47500	0.00000	1
1373	39	2035-03-01	2035-09-01	1.47500	0.00000	1
1374	39	2035-09-01	2036-03-01	1.47500	0.00000	1
1375	39	2036-03-01	2036-09-01	1.47500	0.00000	1
1376	39	2036-09-01	2037-03-01	1.47500	0.00000	1
1377	39	2037-03-01	2037-09-01	1.47500	0.00000	1
1378	39	2037-09-01	2038-03-01	1.47500	0.00000	1
1379	39	2038-03-01	2038-09-01	1.47500	100.00000	1
1380	40	2022-03-01	2022-09-01	1.62500	0.00000	1
1381	40	2022-09-01	2023-03-01	1.62500	0.00000	1
1382	40	2023-03-01	2023-09-01	1.62500	0.00000	1
1383	40	2023-09-01	2024-03-01	1.62500	0.00000	1
1384	40	2024-03-01	2024-09-01	1.62500	0.00000	1
1385	40	2024-09-01	2025-03-01	1.62500	0.00000	1
1386	40	2025-03-01	2025-09-01	1.62500	0.00000	1
1387	40	2025-09-01	2026-03-01	1.62500	0.00000	1
1388	40	2026-03-01	2026-09-01	1.62500	0.00000	1
1389	40	2026-09-01	2027-03-01	1.62500	0.00000	1
1390	40	2027-03-01	2027-09-01	1.62500	0.00000	1
1391	40	2027-09-01	2028-03-01	1.62500	0.00000	1
1392	40	2028-03-01	2028-09-01	1.62500	0.00000	1
1393	40	2028-09-01	2029-03-01	1.62500	0.00000	1
1394	40	2029-03-01	2029-09-01	1.62500	0.00000	1
1395	40	2029-09-01	2030-03-01	1.62500	0.00000	1
1396	40	2030-03-01	2030-09-01	1.62500	0.00000	1
1397	40	2030-09-01	2031-03-01	1.62500	0.00000	1
1398	40	2031-03-01	2031-09-01	1.62500	0.00000	1
1399	40	2031-09-01	2032-03-01	1.62500	0.00000	1
1400	40	2032-03-01	2032-09-01	1.62500	0.00000	1
1401	40	2032-09-01	2033-03-01	1.62500	0.00000	1
1402	40	2033-03-01	2033-09-01	1.62500	0.00000	1
1403	40	2033-09-01	2034-03-01	1.62500	0.00000	1
1404	40	2034-03-01	2034-09-01	1.62500	0.00000	1
1405	40	2034-09-01	2035-03-01	1.62500	0.00000	1
1406	40	2035-03-01	2035-09-01	1.62500	0.00000	1
1407	40	2035-09-01	2036-03-01	1.62500	0.00000	1
1408	40	2036-03-01	2036-09-01	1.62500	0.00000	1
1409	40	2036-09-01	2037-03-01	1.62500	0.00000	1
1410	40	2037-03-01	2037-09-01	1.62500	0.00000	1
1411	40	2037-09-01	2038-03-01	1.62500	100.00000	1
1412	41	2024-04-30	2024-10-30	2.02500	0.00000	1
1413	41	2024-10-30	2025-04-30	2.02500	0.00000	1
1414	41	2025-04-30	2025-10-30	2.02500	0.00000	1
1415	41	2025-10-30	2026-04-30	2.02500	0.00000	1
1416	41	2026-04-30	2026-10-30	2.02500	0.00000	1
1417	41	2026-10-30	2027-04-30	2.02500	0.00000	1
1418	41	2027-04-30	2027-10-30	2.02500	0.00000	1
1419	41	2027-10-30	2028-04-30	2.02500	0.00000	1
1420	41	2028-04-30	2028-10-30	2.02500	0.00000	1
1421	41	2028-10-30	2029-04-30	2.02500	0.00000	1
1422	41	2029-04-30	2029-10-30	2.02500	0.00000	1
1423	41	2029-10-30	2030-04-30	2.02500	0.00000	1
1424	41	2030-04-30	2030-10-30	2.02500	0.00000	1
1425	41	2030-10-30	2031-04-30	2.02500	0.00000	1
1426	41	2031-04-30	2031-10-30	2.02500	0.00000	1
1427	41	2031-10-30	2032-04-30	2.02500	0.00000	1
1428	41	2032-04-30	2032-10-30	2.02500	0.00000	1
1429	41	2032-10-30	2033-04-30	2.02500	0.00000	1
1430	41	2033-04-30	2033-10-30	2.02500	0.00000	1
1431	41	2033-10-30	2034-04-30	2.02500	0.00000	1
1432	41	2034-04-30	2034-10-30	2.02500	0.00000	1
1433	41	2034-10-30	2035-04-30	2.02500	0.00000	1
1434	41	2035-04-30	2035-10-30	2.02500	0.00000	1
1435	41	2035-10-30	2036-04-30	2.02500	0.00000	1
1436	41	2036-04-30	2036-10-30	2.02500	0.00000	1
1437	41	2036-10-30	2037-04-30	2.02500	0.00000	1
1438	41	2037-04-30	2037-10-30	2.02500	100.00000	1
1439	42	2021-01-12	2021-03-01	0.12597	0.00000	1
1440	42	2021-03-01	2021-09-01	0.47500	0.00000	1
1441	42	2021-09-01	2022-03-01	0.47500	0.00000	1
1442	42	2022-03-01	2022-09-01	0.47500	0.00000	1
1443	42	2022-09-01	2023-03-01	0.47500	0.00000	1
1444	42	2023-03-01	2023-09-01	0.47500	0.00000	1
1445	42	2023-09-01	2024-03-01	0.47500	0.00000	1
1446	42	2024-03-01	2024-09-01	0.47500	0.00000	1
1447	42	2024-09-01	2025-03-01	0.47500	0.00000	1
1448	42	2025-03-01	2025-09-01	0.47500	0.00000	1
1449	42	2025-09-01	2026-03-01	0.47500	0.00000	1
1450	42	2026-03-01	2026-09-01	0.47500	0.00000	1
1451	42	2026-09-01	2027-03-01	0.47500	0.00000	1
1452	42	2027-03-01	2027-09-01	0.47500	0.00000	1
1453	42	2027-09-01	2028-03-01	0.47500	0.00000	1
1454	42	2028-03-01	2028-09-01	0.47500	0.00000	1
1455	42	2028-09-01	2029-03-01	0.47500	0.00000	1
1456	42	2029-03-01	2029-09-01	0.47500	0.00000	1
1457	42	2029-09-01	2030-03-01	0.47500	0.00000	1
1458	42	2030-03-01	2030-09-01	0.47500	0.00000	1
1459	42	2030-09-01	2031-03-01	0.47500	0.00000	1
1460	42	2031-03-01	2031-09-01	0.47500	0.00000	1
1461	42	2031-09-01	2032-03-01	0.47500	0.00000	1
1462	42	2032-03-01	2032-09-01	0.47500	0.00000	1
1463	42	2032-09-01	2033-03-01	0.47500	0.00000	1
1464	42	2033-03-01	2033-09-01	0.47500	0.00000	1
1465	42	2033-09-01	2034-03-01	0.47500	0.00000	1
1466	42	2034-03-01	2034-09-01	0.47500	0.00000	1
1467	42	2034-09-01	2035-03-01	0.47500	0.00000	1
1468	42	2035-03-01	2035-09-01	0.47500	0.00000	1
1469	42	2035-09-01	2036-03-01	0.47500	0.00000	1
1470	42	2036-03-01	2036-09-01	0.47500	0.00000	1
1471	42	2036-09-01	2037-03-01	0.47500	100.00000	1
1472	43	2005-08-01	2006-02-01	2.00000	0.00000	1
1473	43	2006-02-01	2006-08-01	2.00000	0.00000	1
1474	43	2006-08-01	2007-02-01	2.00000	0.00000	1
1475	43	2007-02-01	2007-08-01	2.00000	0.00000	1
1476	43	2007-08-01	2008-02-01	2.00000	0.00000	1
1477	43	2008-02-01	2008-08-01	2.00000	0.00000	1
1478	43	2008-08-01	2009-02-01	2.00000	0.00000	1
1479	43	2009-02-01	2009-08-01	2.00000	0.00000	1
1480	43	2009-08-01	2010-02-01	2.00000	0.00000	1
1481	43	2010-02-01	2010-08-01	2.00000	0.00000	1
1482	43	2010-08-01	2011-02-01	2.00000	0.00000	1
1483	43	2011-02-01	2011-08-01	2.00000	0.00000	1
1484	43	2011-08-01	2012-02-01	2.00000	0.00000	1
1485	43	2012-02-01	2012-08-01	2.00000	0.00000	1
1486	43	2012-08-01	2013-02-01	2.00000	0.00000	1
1487	43	2013-02-01	2013-08-01	2.00000	0.00000	1
1488	43	2013-08-01	2014-02-01	2.00000	0.00000	1
1489	43	2014-02-01	2014-08-01	2.00000	0.00000	1
1490	43	2014-08-01	2015-02-01	2.00000	0.00000	1
1491	43	2015-02-01	2015-08-01	2.00000	0.00000	1
1492	43	2015-08-01	2016-02-01	2.00000	0.00000	1
1493	43	2016-02-01	2016-08-01	2.00000	0.00000	1
1494	43	2016-08-01	2017-02-01	2.00000	0.00000	1
1495	43	2017-02-01	2017-08-01	2.00000	0.00000	1
1496	43	2017-08-01	2018-02-01	2.00000	0.00000	1
1497	43	2018-02-01	2018-08-01	2.00000	0.00000	1
1498	43	2018-08-01	2019-02-01	2.00000	0.00000	1
1499	43	2019-02-01	2019-08-01	2.00000	0.00000	1
1500	43	2019-08-01	2020-02-01	2.00000	0.00000	1
1501	43	2020-02-01	2020-08-01	2.00000	0.00000	1
1502	43	2020-08-01	2021-02-01	2.00000	0.00000	1
1503	43	2021-02-01	2021-08-01	2.00000	0.00000	1
1504	43	2021-08-01	2022-02-01	2.00000	0.00000	1
1505	43	2022-02-01	2022-08-01	2.00000	0.00000	1
1506	43	2022-08-01	2023-02-01	2.00000	0.00000	1
1507	43	2023-02-01	2023-08-01	2.00000	0.00000	1
1508	43	2023-08-01	2024-02-01	2.00000	0.00000	1
1509	43	2024-02-01	2024-08-01	2.00000	0.00000	1
1510	43	2024-08-01	2025-02-01	2.00000	0.00000	1
1511	43	2025-02-01	2025-08-01	2.00000	0.00000	1
1512	43	2025-08-01	2026-02-01	2.00000	0.00000	1
1513	43	2026-02-01	2026-08-01	2.00000	0.00000	1
1514	43	2026-08-01	2027-02-01	2.00000	0.00000	1
1515	43	2027-02-01	2027-08-01	2.00000	0.00000	1
1516	43	2027-08-01	2028-02-01	2.00000	0.00000	1
1517	43	2028-02-01	2028-08-01	2.00000	0.00000	1
1518	43	2028-08-01	2029-02-01	2.00000	0.00000	1
1519	43	2029-02-01	2029-08-01	2.00000	0.00000	1
1520	43	2029-08-01	2030-02-01	2.00000	0.00000	1
1521	43	2030-02-01	2030-08-01	2.00000	0.00000	1
1522	43	2030-08-01	2031-02-01	2.00000	0.00000	1
1523	43	2031-02-01	2031-08-01	2.00000	0.00000	1
1524	43	2031-08-01	2032-02-01	2.00000	0.00000	1
1525	43	2032-02-01	2032-08-01	2.00000	0.00000	1
1526	43	2032-08-01	2033-02-01	2.00000	0.00000	1
1527	43	2033-02-01	2033-08-01	2.00000	0.00000	1
1528	43	2033-08-01	2034-02-01	2.00000	0.00000	1
1529	43	2034-02-01	2034-08-01	2.00000	0.00000	1
1530	43	2034-08-01	2035-02-01	2.00000	0.00000	1
1531	43	2035-02-01	2035-08-01	2.00000	0.00000	1
1532	43	2035-08-01	2036-02-01	2.00000	0.00000	1
1533	43	2036-02-01	2036-08-01	2.00000	0.00000	1
1534	43	2036-08-01	2037-02-01	2.00000	100.00000	1
1535	44	2016-03-01	2016-09-01	1.12500	0.00000	1
1536	44	2016-09-01	2017-03-01	1.12500	0.00000	1
1537	44	2017-03-01	2017-09-01	1.12500	0.00000	1
1538	44	2017-09-01	2018-03-01	1.12500	0.00000	1
1539	44	2018-03-01	2018-09-01	1.12500	0.00000	1
1540	44	2018-09-01	2019-03-01	1.12500	0.00000	1
1541	44	2019-03-01	2019-09-01	1.12500	0.00000	1
1542	44	2019-09-01	2020-03-01	1.12500	0.00000	1
1543	44	2020-03-01	2020-09-01	1.12500	0.00000	1
1544	44	2020-09-01	2021-03-01	1.12500	0.00000	1
1545	44	2021-03-01	2021-09-01	1.12500	0.00000	1
1546	44	2021-09-01	2022-03-01	1.12500	0.00000	1
1547	44	2022-03-01	2022-09-01	1.12500	0.00000	1
1548	44	2022-09-01	2023-03-01	1.12500	0.00000	1
1549	44	2023-03-01	2023-09-01	1.12500	0.00000	1
1550	44	2023-09-01	2024-03-01	1.12500	0.00000	1
1551	44	2024-03-01	2024-09-01	1.12500	0.00000	1
1552	44	2024-09-01	2025-03-01	1.12500	0.00000	1
1553	44	2025-03-01	2025-09-01	1.12500	0.00000	1
1554	44	2025-09-01	2026-03-01	1.12500	0.00000	1
1555	44	2026-03-01	2026-09-01	1.12500	0.00000	1
1556	44	2026-09-01	2027-03-01	1.12500	0.00000	1
1557	44	2027-03-01	2027-09-01	1.12500	0.00000	1
1558	44	2027-09-01	2028-03-01	1.12500	0.00000	1
1559	44	2028-03-01	2028-09-01	1.12500	0.00000	1
1560	44	2028-09-01	2029-03-01	1.12500	0.00000	1
1561	44	2029-03-01	2029-09-01	1.12500	0.00000	1
1562	44	2029-09-01	2030-03-01	1.12500	0.00000	1
1563	44	2030-03-01	2030-09-01	1.12500	0.00000	1
1564	44	2030-09-01	2031-03-01	1.12500	0.00000	1
1565	44	2031-03-01	2031-09-01	1.12500	0.00000	1
1566	44	2031-09-01	2032-03-01	1.12500	0.00000	1
1567	44	2032-03-01	2032-09-01	1.12500	0.00000	1
1568	44	2032-09-01	2033-03-01	1.12500	0.00000	1
1569	44	2033-03-01	2033-09-01	1.12500	0.00000	1
1570	44	2033-09-01	2034-03-01	1.12500	0.00000	1
1571	44	2034-03-01	2034-09-01	1.12500	0.00000	1
1572	44	2034-09-01	2035-03-01	1.12500	0.00000	1
1573	44	2035-03-01	2035-09-01	1.12500	0.00000	1
1574	44	2035-09-01	2036-03-01	1.12500	0.00000	1
1575	44	2036-03-01	2036-09-01	1.12500	100.00000	1
1576	45	2020-02-18	2020-03-01	0.04780	0.00000	1
1577	45	2020-03-01	2020-09-01	0.72500	0.00000	1
1578	45	2020-09-01	2021-03-01	0.72500	0.00000	1
1579	45	2021-03-01	2021-09-01	0.72500	0.00000	1
1580	45	2021-09-01	2022-03-01	0.72500	0.00000	1
1581	45	2022-03-01	2022-09-01	0.72500	0.00000	1
1582	45	2022-09-01	2023-03-01	0.72500	0.00000	1
1583	45	2023-03-01	2023-09-01	0.72500	0.00000	1
1584	45	2023-09-01	2024-03-01	0.72500	0.00000	1
1585	45	2024-03-01	2024-09-01	0.72500	0.00000	1
1586	45	2024-09-01	2025-03-01	0.72500	0.00000	1
1587	45	2025-03-01	2025-09-01	0.72500	0.00000	1
1588	45	2025-09-01	2026-03-01	0.72500	0.00000	1
1589	45	2026-03-01	2026-09-01	0.72500	0.00000	1
1590	45	2026-09-01	2027-03-01	0.72500	0.00000	1
1591	45	2027-03-01	2027-09-01	0.72500	0.00000	1
1592	45	2027-09-01	2028-03-01	0.72500	0.00000	1
1593	45	2028-03-01	2028-09-01	0.72500	0.00000	1
1594	45	2028-09-01	2029-03-01	0.72500	0.00000	1
1595	45	2029-03-01	2029-09-01	0.72500	0.00000	1
1596	45	2029-09-01	2030-03-01	0.72500	0.00000	1
1597	45	2030-03-01	2030-09-01	0.72500	0.00000	1
1598	45	2030-09-01	2031-03-01	0.72500	0.00000	1
1599	45	2031-03-01	2031-09-01	0.72500	0.00000	1
1600	45	2031-09-01	2032-03-01	0.72500	0.00000	1
1601	45	2032-03-01	2032-09-01	0.72500	0.00000	1
1602	45	2032-09-01	2033-03-01	0.72500	0.00000	1
1603	45	2033-03-01	2033-09-01	0.72500	0.00000	1
1604	45	2033-09-01	2034-03-01	0.72500	0.00000	1
1605	45	2034-03-01	2034-09-01	0.72500	0.00000	1
1606	45	2034-09-01	2035-03-01	0.72500	0.00000	1
1607	45	2035-03-01	2035-09-01	0.72500	0.00000	1
1608	45	2035-09-01	2036-03-01	0.72500	100.00000	1
1609	46	2025-11-03	2026-02-01	0.84375	0.00000	1
1610	46	2026-02-01	2026-08-01	1.72500	0.00000	1
1611	46	2026-08-01	2027-02-01	1.72500	0.00000	1
1612	46	2027-02-01	2027-08-01	1.72500	0.00000	1
1613	46	2027-08-01	2028-02-01	1.72500	0.00000	1
1614	46	2028-02-01	2028-08-01	1.72500	0.00000	1
1615	46	2028-08-01	2029-02-01	1.72500	0.00000	1
1616	46	2029-02-01	2029-08-01	1.72500	0.00000	1
1617	46	2029-08-01	2030-02-01	1.72500	0.00000	1
1618	46	2030-02-01	2030-08-01	1.72500	0.00000	1
1619	46	2030-08-01	2031-02-01	1.72500	0.00000	1
1620	46	2031-02-01	2031-08-01	1.72500	0.00000	1
1621	46	2031-08-01	2032-02-01	1.72500	0.00000	1
1622	46	2032-02-01	2032-08-01	1.72500	0.00000	1
1623	46	2032-08-01	2033-02-01	1.72500	0.00000	1
1624	46	2033-02-01	2033-08-01	1.72500	0.00000	1
1625	46	2033-08-01	2034-02-01	1.72500	0.00000	1
1626	46	2034-02-01	2034-08-01	1.72500	0.00000	1
1627	46	2034-08-01	2035-02-01	1.72500	0.00000	1
1628	46	2035-02-01	2035-08-01	1.72500	0.00000	1
1629	46	2035-08-01	2036-02-01	1.72500	0.00000	1
1630	47	2025-05-02	2025-10-01	1.49508	0.00000	1
1631	47	2025-10-01	2026-04-01	1.80000	0.00000	1
1632	47	2026-04-01	2026-10-01	1.80000	0.00000	1
1633	47	2026-10-01	2027-04-01	1.80000	0.00000	1
1634	47	2027-04-01	2027-10-01	1.80000	0.00000	1
1635	47	2027-10-01	2028-04-01	1.80000	0.00000	1
1636	47	2028-04-01	2028-10-01	1.80000	0.00000	1
1637	47	2028-10-01	2029-04-01	1.80000	0.00000	1
1638	47	2029-04-01	2029-10-01	1.80000	0.00000	1
1639	47	2029-10-01	2030-04-01	1.80000	0.00000	1
1640	47	2030-04-01	2030-10-01	1.80000	0.00000	1
1641	47	2030-10-01	2031-04-01	1.80000	0.00000	1
1642	47	2031-04-01	2031-10-01	1.80000	0.00000	1
1643	47	2031-10-01	2032-04-01	1.80000	0.00000	1
1644	47	2032-04-01	2032-10-01	1.80000	0.00000	1
1645	47	2032-10-01	2033-04-01	1.80000	0.00000	1
1646	47	2033-04-01	2033-10-01	1.80000	0.00000	1
1647	47	2033-10-01	2034-04-01	1.80000	0.00000	1
1648	47	2034-04-01	2034-10-01	1.80000	0.00000	1
1649	47	2034-10-01	2035-04-01	1.80000	0.00000	1
1650	47	2035-04-01	2035-10-01	1.80000	100.00000	1
1651	48	2025-01-15	2025-02-01	0.16861	0.00000	1
1652	48	2025-02-01	2025-08-01	1.82500	0.00000	1
1653	48	2025-08-01	2026-02-01	1.82500	0.00000	1
1654	48	2026-02-01	2026-08-01	1.82500	0.00000	1
1655	48	2026-08-01	2027-02-01	1.82500	0.00000	1
1656	48	2027-02-01	2027-08-01	1.82500	0.00000	1
1657	48	2027-08-01	2028-02-01	1.82500	0.00000	1
1658	48	2028-02-01	2028-08-01	1.82500	0.00000	1
1659	48	2028-08-01	2029-02-01	1.82500	0.00000	1
1660	48	2029-02-01	2029-08-01	1.82500	0.00000	1
1661	48	2029-08-01	2030-02-01	1.82500	0.00000	1
1662	48	2030-02-01	2030-08-01	1.82500	0.00000	1
1663	48	2030-08-01	2031-02-01	1.82500	0.00000	1
1664	48	2031-02-01	2031-08-01	1.82500	0.00000	1
1665	48	2031-08-01	2032-02-01	1.82500	0.00000	1
1666	48	2032-02-01	2032-08-01	1.82500	0.00000	1
1667	48	2032-08-01	2033-02-01	1.82500	0.00000	1
1668	48	2033-02-01	2033-08-01	1.82500	0.00000	1
1669	48	2033-08-01	2034-02-01	1.82500	0.00000	1
1670	48	2034-02-01	2034-08-01	1.82500	0.00000	1
1671	48	2034-08-01	2035-02-01	1.82500	0.00000	1
1672	48	2035-02-01	2035-08-01	1.82500	100.00000	1
1673	49	2024-08-01	2025-02-01	1.92500	0.00000	1
1674	49	2025-02-01	2025-08-01	1.92500	0.00000	1
1675	49	2025-08-01	2026-02-01	1.92500	0.00000	1
1676	49	2026-02-01	2026-08-01	1.92500	0.00000	1
1677	49	2026-08-01	2027-02-01	1.92500	0.00000	1
1678	49	2027-02-01	2027-08-01	1.92500	0.00000	1
1679	49	2027-08-01	2028-02-01	1.92500	0.00000	1
1680	49	2028-02-01	2028-08-01	1.92500	0.00000	1
1681	49	2028-08-01	2029-02-01	1.92500	0.00000	1
1682	49	2029-02-01	2029-08-01	1.92500	0.00000	1
1683	49	2029-08-01	2030-02-01	1.92500	0.00000	1
1684	49	2030-02-01	2030-08-01	1.92500	0.00000	1
1685	49	2030-08-01	2031-02-01	1.92500	0.00000	1
1686	49	2031-02-01	2031-08-01	1.92500	0.00000	1
1687	49	2031-08-01	2032-02-01	1.92500	0.00000	1
1688	49	2032-02-01	2032-08-01	1.92500	0.00000	1
1689	49	2032-08-01	2033-02-01	1.92500	0.00000	1
1690	49	2033-02-01	2033-08-01	1.92500	0.00000	1
1691	49	2033-08-01	2034-02-01	1.92500	0.00000	1
1692	49	2034-02-01	2034-08-01	1.92500	0.00000	1
1693	49	2034-08-01	2035-02-01	1.92500	100.00000	1
1694	50	2022-09-13	2022-10-30	0.51366	0.00000	1
1695	50	2022-10-30	2023-04-30	2.00000	0.00000	1
1696	50	2023-04-30	2023-10-30	2.00000	0.00000	1
1697	50	2023-10-30	2024-04-30	2.00000	0.00000	1
1698	50	2024-04-30	2024-10-30	2.00000	0.00000	1
1699	50	2024-10-30	2025-04-30	2.00000	0.00000	1
1700	50	2025-04-30	2025-10-30	2.00000	0.00000	1
1701	50	2025-10-30	2026-04-30	2.00000	0.00000	1
1702	50	2026-04-30	2026-10-30	2.00000	0.00000	1
1703	50	2026-10-30	2027-04-30	2.00000	0.00000	1
1704	50	2027-04-30	2027-10-30	2.00000	0.00000	1
1705	50	2027-10-30	2028-04-30	2.00000	0.00000	1
1706	50	2028-04-30	2028-10-30	2.00000	0.00000	1
1707	50	2028-10-30	2029-04-30	2.00000	0.00000	1
1708	50	2029-04-30	2029-10-30	2.00000	0.00000	1
1709	50	2029-10-30	2030-04-30	2.00000	0.00000	1
1710	50	2030-04-30	2030-10-30	2.00000	0.00000	1
1711	50	2030-10-30	2031-04-30	2.00000	0.00000	1
1712	50	2031-04-30	2031-10-30	2.00000	0.00000	1
1713	50	2031-10-30	2032-04-30	2.00000	0.00000	1
1714	50	2032-04-30	2032-10-30	2.00000	0.00000	1
1715	50	2032-10-30	2033-04-30	2.00000	0.00000	1
1716	50	2033-04-30	2033-10-30	2.00000	0.00000	1
1717	50	2033-10-30	2034-04-30	2.00000	0.00000	1
1718	50	2034-04-30	2034-10-30	2.00000	0.00000	1
1719	50	2034-10-30	2035-04-30	2.00000	100.00000	1
1720	51	2019-01-22	2019-03-01	0.35166	0.00000	1
1721	51	2019-03-01	2019-09-01	1.67500	0.00000	1
1722	51	2019-09-01	2020-03-01	1.67500	0.00000	1
1723	51	2020-03-01	2020-09-01	1.67500	0.00000	1
1724	51	2020-09-01	2021-03-01	1.67500	0.00000	1
1725	51	2021-03-01	2021-09-01	1.67500	0.00000	1
1726	51	2021-09-01	2022-03-01	1.67500	0.00000	1
1727	51	2022-03-01	2022-09-01	1.67500	0.00000	1
1728	51	2022-09-01	2023-03-01	1.67500	0.00000	1
1729	51	2023-03-01	2023-09-01	1.67500	0.00000	1
1730	51	2023-09-01	2024-03-01	1.67500	0.00000	1
1731	51	2024-03-01	2024-09-01	1.67500	0.00000	1
1732	51	2024-09-01	2025-03-01	1.67500	0.00000	1
1733	51	2025-03-01	2025-09-01	1.67500	0.00000	1
1734	51	2025-09-01	2026-03-01	1.67500	0.00000	1
1735	51	2026-03-01	2026-09-01	1.67500	0.00000	1
1736	51	2026-09-01	2027-03-01	1.67500	0.00000	1
1737	51	2027-03-01	2027-09-01	1.67500	0.00000	1
1738	51	2027-09-01	2028-03-01	1.67500	0.00000	1
1739	51	2028-03-01	2028-09-01	1.67500	0.00000	1
1740	51	2028-09-01	2029-03-01	1.67500	0.00000	1
1741	51	2029-03-01	2029-09-01	1.67500	0.00000	1
1742	51	2029-09-01	2030-03-01	1.67500	0.00000	1
1743	51	2030-03-01	2030-09-01	1.67500	0.00000	1
1744	51	2030-09-01	2031-03-01	1.67500	0.00000	1
1745	51	2031-03-01	2031-09-01	1.67500	0.00000	1
1746	51	2031-09-01	2032-03-01	1.67500	0.00000	1
1747	51	2032-03-01	2032-09-01	1.67500	0.00000	1
1748	51	2032-09-01	2033-03-01	1.67500	0.00000	1
1749	51	2033-03-01	2033-09-01	1.67500	0.00000	1
1750	51	2033-09-01	2034-03-01	1.67500	0.00000	1
1751	51	2034-03-01	2034-09-01	1.67500	0.00000	1
1752	51	2034-09-01	2035-03-01	1.67500	100.00000	1
1753	52	2021-11-16	2022-05-16	0.37500	0.00000	1
1754	52	2022-05-16	2022-11-16	0.37500	0.00000	1
1755	52	2022-11-16	2023-05-16	0.37500	0.00000	1
1756	52	2023-05-16	2023-11-16	0.37500	0.00000	1
1757	52	2023-11-16	2024-05-16	0.37500	0.00000	1
1758	52	2024-05-16	2024-11-16	0.37500	0.00000	1
1759	52	2024-11-16	2025-05-16	0.37500	0.00000	1
1760	52	2025-05-16	2025-11-16	0.37500	0.00000	1
1761	52	2025-11-16	2026-05-16	0.67500	0.00000	1
1762	52	2026-05-16	2026-11-16	0.67500	0.00000	1
1763	52	2026-11-16	2027-05-16	0.67500	0.00000	1
1764	52	2027-05-16	2027-11-16	0.67500	0.00000	1
1765	52	2027-11-16	2028-05-16	0.67500	0.00000	1
1766	52	2028-05-16	2028-11-16	0.67500	0.00000	1
1767	52	2028-11-16	2029-05-16	0.67500	0.00000	1
1768	52	2029-05-16	2029-11-16	0.67500	0.00000	1
1769	52	2029-11-16	2030-05-16	0.85000	0.00000	1
1770	52	2030-05-16	2030-11-16	0.85000	0.00000	1
1771	52	2030-11-16	2031-05-16	0.85000	0.00000	1
1772	52	2031-05-16	2031-11-16	0.85000	0.00000	1
1773	52	2031-11-16	2032-05-16	0.85000	0.00000	1
1774	52	2032-05-16	2032-11-16	0.85000	0.00000	1
1775	52	2032-11-16	2033-05-16	0.85000	0.00000	1
1776	52	2033-05-16	2033-11-16	0.85000	0.00000	1
1777	53	2024-03-01	2024-07-01	1.29038	0.00000	1
1778	53	2024-07-01	2025-01-01	1.92500	0.00000	1
1779	53	2025-01-01	2025-07-01	1.92500	0.00000	1
1780	53	2025-07-01	2026-01-01	1.92500	0.00000	1
1781	53	2026-01-01	2026-07-01	1.92500	0.00000	1
1782	53	2026-07-01	2027-01-01	1.92500	0.00000	1
1783	53	2027-01-01	2027-07-01	1.92500	0.00000	1
1784	53	2027-07-01	2028-01-01	1.92500	0.00000	1
1785	53	2028-01-01	2028-07-01	1.92500	0.00000	1
1786	53	2028-07-01	2029-01-01	1.92500	0.00000	1
1787	53	2029-01-01	2029-07-01	1.92500	0.00000	1
1788	53	2029-07-01	2030-01-01	1.92500	0.00000	1
1789	53	2030-01-01	2030-07-01	1.92500	0.00000	1
1790	53	2030-07-01	2031-01-01	1.92500	0.00000	1
1791	53	2031-01-01	2031-07-01	1.92500	0.00000	1
1792	53	2031-07-01	2032-01-01	1.92500	0.00000	1
1793	53	2032-01-01	2032-07-01	1.92500	0.00000	1
1794	53	2032-07-01	2033-01-01	1.92500	0.00000	1
1795	53	2033-01-01	2033-07-01	1.92500	0.00000	1
1796	53	2033-07-01	2034-01-01	1.92500	0.00000	1
1797	53	2034-01-01	2034-07-01	1.92500	100.00000	1
1798	54	2003-08-01	2004-02-01	2.50000	0.00000	1
1799	54	2004-02-01	2004-08-01	2.50000	0.00000	1
1800	54	2004-08-01	2005-02-01	2.50000	0.00000	1
1801	54	2005-02-01	2005-08-01	2.50000	0.00000	1
1802	54	2005-08-01	2006-02-01	2.50000	0.00000	1
1803	54	2006-02-01	2006-08-01	2.50000	0.00000	1
1804	54	2006-08-01	2007-02-01	2.50000	0.00000	1
1805	54	2007-02-01	2007-08-01	2.50000	0.00000	1
1806	54	2007-08-01	2008-02-01	2.50000	0.00000	1
1807	54	2008-02-01	2008-08-01	2.50000	0.00000	1
1808	54	2008-08-01	2009-02-01	2.50000	0.00000	1
1809	54	2009-02-01	2009-08-01	2.50000	0.00000	1
1810	54	2009-08-01	2010-02-01	2.50000	0.00000	1
1811	54	2010-02-01	2010-08-01	2.50000	0.00000	1
1812	54	2010-08-01	2011-02-01	2.50000	0.00000	1
1813	54	2011-02-01	2011-08-01	2.50000	0.00000	1
1814	54	2011-08-01	2012-02-01	2.50000	0.00000	1
1815	54	2012-02-01	2012-08-01	2.50000	0.00000	1
1816	54	2012-08-01	2013-02-01	2.50000	0.00000	1
1817	54	2013-02-01	2013-08-01	2.50000	0.00000	1
1818	54	2013-08-01	2014-02-01	2.50000	0.00000	1
1819	54	2014-02-01	2014-08-01	2.50000	0.00000	1
1820	54	2014-08-01	2015-02-01	2.50000	0.00000	1
1821	54	2015-02-01	2015-08-01	2.50000	0.00000	1
1822	54	2015-08-01	2016-02-01	2.50000	0.00000	1
1823	54	2016-02-01	2016-08-01	2.50000	0.00000	1
1824	54	2016-08-01	2017-02-01	2.50000	0.00000	1
1825	54	2017-02-01	2017-08-01	2.50000	0.00000	1
1826	54	2017-08-01	2018-02-01	2.50000	0.00000	1
1827	54	2018-02-01	2018-08-01	2.50000	0.00000	1
1828	54	2018-08-01	2019-02-01	2.50000	0.00000	1
1829	54	2019-02-01	2019-08-01	2.50000	0.00000	1
1830	54	2019-08-01	2020-02-01	2.50000	0.00000	1
1831	54	2020-02-01	2020-08-01	2.50000	0.00000	1
1832	54	2020-08-01	2021-02-01	2.50000	0.00000	1
1833	54	2021-02-01	2021-08-01	2.50000	0.00000	1
1834	54	2021-08-01	2022-02-01	2.50000	0.00000	1
1835	54	2022-02-01	2022-08-01	2.50000	0.00000	1
1836	54	2022-08-01	2023-02-01	2.50000	0.00000	1
1837	54	2023-02-01	2023-08-01	2.50000	0.00000	1
1838	54	2023-08-01	2024-02-01	2.50000	0.00000	1
1839	54	2024-02-01	2024-08-01	2.50000	0.00000	1
1840	54	2024-08-01	2025-02-01	2.50000	0.00000	1
1841	54	2025-02-01	2025-08-01	2.50000	0.00000	1
1842	54	2025-08-01	2026-02-01	2.50000	0.00000	1
1843	54	2026-02-01	2026-08-01	2.50000	0.00000	1
1844	54	2026-08-01	2027-02-01	2.50000	0.00000	1
1845	54	2027-02-01	2027-08-01	2.50000	0.00000	1
1846	54	2027-08-01	2028-02-01	2.50000	0.00000	1
1847	54	2028-02-01	2028-08-01	2.50000	0.00000	1
1848	54	2028-08-01	2029-02-01	2.50000	0.00000	1
1849	54	2029-02-01	2029-08-01	2.50000	0.00000	1
1850	54	2029-08-01	2030-02-01	2.50000	0.00000	1
1851	54	2030-02-01	2030-08-01	2.50000	0.00000	1
1852	54	2030-08-01	2031-02-01	2.50000	0.00000	1
1853	54	2031-02-01	2031-08-01	2.50000	0.00000	1
1854	54	2031-08-01	2032-02-01	2.50000	0.00000	1
1855	54	2032-02-01	2032-08-01	2.50000	0.00000	1
1856	54	2032-08-01	2033-02-01	2.50000	0.00000	1
1857	54	2033-02-01	2033-08-01	2.50000	0.00000	1
1858	54	2033-08-01	2034-02-01	2.50000	0.00000	1
1859	54	2034-02-01	2034-08-01	2.50000	100.00000	1
1860	55	2025-02-25	2025-05-25	0.71250	0.00000	1
1861	55	2025-05-25	2025-08-25	0.71250	0.00000	1
1862	55	2025-08-25	2025-11-25	0.71250	0.00000	1
1863	55	2025-11-25	2026-02-25	0.71250	0.00000	1
1864	55	2026-02-25	2026-05-25	0.71250	0.00000	1
1865	55	2026-05-25	2026-08-25	0.71250	0.00000	1
1866	55	2026-08-25	2026-11-25	0.71250	0.00000	1
1867	55	2026-11-25	2027-02-25	0.71250	0.00000	1
1868	55	2027-02-25	2027-05-25	0.71250	0.00000	1
1869	55	2027-05-25	2027-08-25	0.71250	0.00000	1
1870	55	2027-08-25	2027-11-25	0.71250	0.00000	1
1871	55	2027-11-25	2028-02-25	0.71250	0.00000	1
1872	55	2028-02-25	2028-05-25	0.71250	0.00000	1
1873	55	2028-05-25	2028-08-25	0.71250	0.00000	1
1874	55	2028-08-25	2028-11-25	0.71250	0.00000	1
1875	55	2028-11-25	2029-02-25	0.71250	0.00000	1
1876	55	2029-02-25	2029-05-25	0.92500	0.00000	1
1877	55	2029-05-25	2029-08-25	0.92500	0.00000	1
1878	55	2029-08-25	2029-11-25	0.92500	0.00000	1
1879	55	2029-11-25	2030-02-25	0.92500	0.00000	1
1880	55	2030-02-25	2030-05-25	0.92500	0.00000	1
1881	55	2030-05-25	2030-08-25	0.92500	0.00000	1
1882	55	2030-08-25	2030-11-25	0.92500	0.00000	1
1883	55	2030-11-25	2031-02-25	0.92500	0.00000	1
1884	55	2031-02-25	2031-05-25	0.92500	0.00000	1
1885	55	2031-05-25	2031-08-25	0.92500	0.00000	1
1886	55	2031-08-25	2031-11-25	0.92500	0.00000	1
1887	55	2031-11-25	2032-02-25	0.92500	0.00000	1
1888	55	2032-02-25	2032-05-25	0.92500	0.00000	1
1889	55	2032-05-25	2032-08-25	0.92500	0.00000	1
1890	55	2032-08-25	2032-11-25	0.92500	0.00000	1
1891	55	2032-11-25	2033-02-25	0.92500	0.00000	1
1892	56	2023-09-01	2024-03-01	2.10000	0.00000	1
1893	56	2024-03-01	2024-09-01	2.10000	0.00000	1
1894	56	2024-09-01	2025-03-01	2.10000	0.00000	1
1895	56	2025-03-01	2025-09-01	2.10000	0.00000	1
1896	56	2025-09-01	2026-03-01	2.10000	0.00000	1
1897	56	2026-03-01	2026-09-01	2.10000	0.00000	1
1898	56	2026-09-01	2027-03-01	2.10000	0.00000	1
1899	56	2027-03-01	2027-09-01	2.10000	0.00000	1
1900	56	2027-09-01	2028-03-01	2.10000	0.00000	1
1901	56	2028-03-01	2028-09-01	2.10000	0.00000	1
1902	56	2028-09-01	2029-03-01	2.10000	0.00000	1
1903	56	2029-03-01	2029-09-01	2.10000	0.00000	1
1904	56	2029-09-01	2030-03-01	2.10000	0.00000	1
1905	56	2030-03-01	2030-09-01	2.10000	0.00000	1
1906	56	2030-09-01	2031-03-01	2.10000	0.00000	1
1907	56	2031-03-01	2031-09-01	2.10000	0.00000	1
1908	56	2031-09-01	2032-03-01	2.10000	0.00000	1
1909	56	2032-03-01	2032-09-01	2.10000	0.00000	1
1910	56	2032-09-01	2033-03-01	2.10000	0.00000	1
1911	56	2033-03-01	2033-09-01	2.10000	0.00000	1
1912	56	2033-09-01	2034-03-01	2.10000	100.00000	1
1913	57	2023-05-02	2023-11-01	2.16318	0.00000	1
1914	57	2023-11-01	2024-05-01	2.17500	0.00000	1
1915	57	2024-05-01	2024-11-01	2.17500	0.00000	1
1916	57	2024-11-01	2025-05-01	2.17500	0.00000	1
1917	57	2025-05-01	2025-11-01	2.17500	0.00000	1
1918	57	2025-11-01	2026-05-01	2.17500	0.00000	1
1919	57	2026-05-01	2026-11-01	2.17500	0.00000	1
1920	57	2026-11-01	2027-05-01	2.17500	0.00000	1
1921	57	2027-05-01	2027-11-01	2.17500	0.00000	1
1922	57	2027-11-01	2028-05-01	2.17500	0.00000	1
1923	57	2028-05-01	2028-11-01	2.17500	0.00000	1
1924	57	2028-11-01	2029-05-01	2.17500	0.00000	1
1925	57	2029-05-01	2029-11-01	2.17500	0.00000	1
1926	57	2029-11-01	2030-05-01	2.17500	0.00000	1
1927	57	2030-05-01	2030-11-01	2.17500	0.00000	1
1928	57	2030-11-01	2031-05-01	2.17500	0.00000	1
1929	57	2031-05-01	2031-11-01	2.17500	0.00000	1
1930	57	2031-11-01	2032-05-01	2.17500	0.00000	1
1931	57	2032-05-01	2032-11-01	2.17500	0.00000	1
1932	57	2032-11-01	2033-05-01	2.17500	0.00000	1
1933	57	2033-05-01	2033-11-01	2.17500	100.00000	1
1934	58	2017-01-25	2017-03-01	0.23688	0.00000	1
1935	58	2017-03-01	2017-09-01	1.22500	0.00000	1
1936	58	2017-09-01	2018-03-01	1.22500	0.00000	1
1937	58	2018-03-01	2018-09-01	1.22500	0.00000	1
1938	58	2018-09-01	2019-03-01	1.22500	0.00000	1
1939	58	2019-03-01	2019-09-01	1.22500	0.00000	1
1940	58	2019-09-01	2020-03-01	1.22500	0.00000	1
1941	58	2020-03-01	2020-09-01	1.22500	0.00000	1
1942	58	2020-09-01	2021-03-01	1.22500	0.00000	1
1943	58	2021-03-01	2021-09-01	1.22500	0.00000	1
1944	58	2021-09-01	2022-03-01	1.22500	0.00000	1
1945	58	2022-03-01	2022-09-01	1.22500	0.00000	1
1946	58	2022-09-01	2023-03-01	1.22500	0.00000	1
1947	58	2023-03-01	2023-09-01	1.22500	0.00000	1
1948	58	2023-09-01	2024-03-01	1.22500	0.00000	1
1949	58	2024-03-01	2024-09-01	1.22500	0.00000	1
1950	58	2024-09-01	2025-03-01	1.22500	0.00000	1
1951	58	2025-03-01	2025-09-01	1.22500	0.00000	1
1952	58	2025-09-01	2026-03-01	1.22500	0.00000	1
1953	58	2026-03-01	2026-09-01	1.22500	0.00000	1
1954	58	2026-09-01	2027-03-01	1.22500	0.00000	1
1955	58	2027-03-01	2027-09-01	1.22500	0.00000	1
1956	58	2027-09-01	2028-03-01	1.22500	0.00000	1
1957	58	2028-03-01	2028-09-01	1.22500	0.00000	1
1958	58	2028-09-01	2029-03-01	1.22500	0.00000	1
1959	58	2029-03-01	2029-09-01	1.22500	0.00000	1
1960	58	2029-09-01	2030-03-01	1.22500	0.00000	1
1961	58	2030-03-01	2030-09-01	1.22500	0.00000	1
1962	58	2030-09-01	2031-03-01	1.22500	0.00000	1
1963	58	2031-03-01	2031-09-01	1.22500	0.00000	1
1964	58	2031-09-01	2032-03-01	1.22500	0.00000	1
1965	58	2032-03-01	2032-09-01	1.22500	0.00000	1
1966	58	2032-09-01	2033-03-01	1.22500	0.00000	1
1967	58	2033-03-01	2033-09-01	1.22500	100.00000	1
\.


--
-- TOC entry 5227 (class 0 OID 27473)
-- Dependencies: 230
-- Data for Name: cash_flow_reset; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.cash_flow_reset (id_cash_flow_reset, master_data, start_date_reset, interest_reset) FROM stdin;
\.


--
-- TOC entry 5229 (class 0 OID 27477)
-- Dependencies: 232
-- Data for Name: compounding; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.compounding (id_compounding, code, description) FROM stdin;
1	SIMPLE	Linear
2	COMPOUNDED	Compounded
3	CONTINUOUS	Continuous
4	SIMPLE_THEN_COMPOUNDED	Linear then Compounded
\.


--
-- TOC entry 5231 (class 0 OID 27481)
-- Dependencies: 234
-- Data for Name: counterparty; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.counterparty (id_counterparty, ctp_type, lei_code, country, code, description) FROM stdin;
\.


--
-- TOC entry 5233 (class 0 OID 27485)
-- Dependencies: 236
-- Data for Name: counterparty_type; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.counterparty_type (id_counterparty_type, code, description) FROM stdin;
1	RCLIENT	Retail Clients
2	ICLIENT	Institutional Clients
3	CORPORATE	Corporate
4	BANK	Bank
5	CHOUSE	Clearing Houses
6	CUSTODIAN	Custodians
\.


--
-- TOC entry 5235 (class 0 OID 27489)
-- Dependencies: 238
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) FROM stdin;
2	USA	United States of America (the)	US	USA	840	UN Member State			2	2
3	GBR	United Kingdom of Great Britain and Northern Ireland (the)	GB	GBR	826	UN Member State			3	1
4	CHE	Swiss Confederation (the)	CH	CHE	756	UN Member State			4	1
5	CAN	Canada	CA	CAN	124	UN Member State			5	2
6	AUS	Australia	AU	AUS	36	UN Member State			6	2
7	JP	Japan	JP	JPN	392	UN Member State			7	2
1	ITA	Italy	IT	ITA	381	UN Member State			1	1
\.


--
-- TOC entry 5237 (class 0 OID 27496)
-- Dependencies: 240
-- Data for Name: currency; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days) FROM stdin;
1	1	2	EUR	978	EUR	2	0	1	2
2	2	2	USD	840	USD	2	0	1	2
3	1	2	GBP	826	GBP	2	0	1	2
4	1	2	CHF	576	CHF	2	0	1	2
5	2	2	CAD	124	CAD	2	0	1	2
6	2	2	AUD	36	AUD	2	0	1	2
7	2	2	JPY	392	JPY	2	0	1	2
\.


--
-- TOC entry 5239 (class 0 OID 27504)
-- Dependencies: 242
-- Data for Name: currpair_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.currpair_master_data (id_master_data, bcy, ccy, bcy_irc, ccy_irc) FROM stdin;
\.


--
-- TOC entry 5240 (class 0 OID 27509)
-- Dependencies: 243
-- Data for Name: daycount; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.daycount (id_daycount, code, description) FROM stdin;
1	NASD_30_360	30/360 NASD
2	ACT_360	Actual/360
3	ACT_365	Actual/365
4	ACT_ACT_ISDA	Actual/Actual ISDA
5	ACT_ACT_ICMA	Actual/Actual ICMA
6	EUR_30_360	30/360 EUR
\.


--
-- TOC entry 5242 (class 0 OID 27513)
-- Dependencies: 245
-- Data for Name: deliverable_bonds; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.deliverable_bonds (id_deliverable_bonds, master_data, expiration_date, isin, coupon_rate, bond_maturity, bond_cf) FROM stdin;
\.


--
-- TOC entry 5244 (class 0 OID 27517)
-- Dependencies: 247
-- Data for Name: financial_statement_types; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.financial_statement_types (statement_type_id, code, description) FROM stdin;
1	BALANCE_SHEET	Balance Sheet
2	INCOME_STATEMENT	Income Statement
3	OFF_BALANCE_SHEET	Off Balance Sheet
\.


--
-- TOC entry 5246 (class 0 OID 27522)
-- Dependencies: 249
-- Data for Name: financial_txn; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.financial_txn (id_financial_txn, counterparty, position_md, master_data, txn_status, txn_side, description, trade_date, value_date, settlement, quantity, price, ref_id, version) FROM stdin;
\.


--
-- TOC entry 5248 (class 0 OID 27528)
-- Dependencies: 251
-- Data for Name: forex_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.forex_master_data (id_master_data, bcy, ccy, bcy_irc, ccy_irc) FROM stdin;
60	1	2	TERMESTR	TERMSOFR
\.


--
-- TOC entry 5249 (class 0 OID 27533)
-- Dependencies: 252
-- Data for Name: form; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.form (id_form, code, description) FROM stdin;
1	BEARER	Bearer
2	REGISTERED	Registered
3	BOOK-ENTRY-BOND	Book-entry Bond
\.


--
-- TOC entry 5251 (class 0 OID 27537)
-- Dependencies: 254
-- Data for Name: frequency; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.frequency (id_frequency, code, description, year_fraction) FROM stdin;
1	ANNUAL	Annual	1
2	SEMI-ANNUAL	Semi Annual	2
3	E4M	Every 4 months	3
4	QUARTERLY	Quarterly	4
5	BI-MONTHLY	Every two months	6
6	MONTHLY	Monthly	12
100	NONE	None	0
\.


--
-- TOC entry 5253 (class 0 OID 27542)
-- Dependencies: 256
-- Data for Name: future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.future_master_data (id_master_data, isin, exchange_contract_code, settlement_type) FROM stdin;
61	M6EU6	M6EU6	2
\.


--
-- TOC entry 5254 (class 0 OID 27546)
-- Dependencies: 257
-- Data for Name: fx_future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.fx_future_master_data (id_master_data, underlying, contract_value, tick_size, initial_margin, maintenance_margin) FROM stdin;
61	60	12500.00000	0.00010	200.00000	200.00000
\.


--
-- TOC entry 5255 (class 0 OID 27549)
-- Dependencies: 258
-- Data for Name: gl_accounts; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance, created_at, updated_at) FROM stdin;
1	\N	1	Assets	f	\N	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
2	\N	2	Liabilities	f	\N	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
3	\N	3	Equity	f	\N	1	3	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
4	\N	4	Income	f	\N	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
5	\N	5	Expenses	f	\N	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
6	\N	6	Off-Balance Commitments	f	\N	3	6	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
7	1	10	Cash and Cash Equivalents	f	\N	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
8	1	11	Short-Term Deposits	f	\N	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
9	1	12	Margin Accounts and Broker Receivables	f	\N	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
10	1	13	Financial Assets at FVTPL	f	\N	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
11	1	14	Financial Derivatives - Assets	f	\N	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
12	1	19	Accruals and Receivables	f	\N	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
13	2	21	Short-Term Borrowings	f	\N	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
14	2	24	Financial Derivatives & Settlement Liabilities	f	\N	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
15	3	30	Capital and Reserves	f	\N	1	3	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
16	4	40	Gains on Financial Derivatives	f	\N	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
17	4	41	Foreign Exchange Gains	f	\N	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
18	4	42	Interest and Dividend Income	f	\N	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
19	5	50	Losses on Financial Derivatives	f	\N	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
20	5	51	Foreign Exchange Losses	f	\N	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
21	5	52	Interest Expenses and Trading Losses	f	\N	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
22	5	58	Trading Fees and Execution Costs	f	\N	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
23	6	60	Financial Commitments	f	\N	3	6	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
24	7	100010	Cash and Cash Equivalents - Base Currency (EUR)	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
25	7	100015	Cash and Cash Equivalents - Foreign Currency (USD)	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
26	7	100020	Petty Cash	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
27	8	110010	Short-Term Time Deposits - EUR	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
28	8	110015	Short-Term Time Deposits - USD	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
29	9	120050	Initial Margin Deposit - EUR	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
30	9	120055	Initial Margin Deposit - USD	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
31	9	120060	Variation Margin Account - EUR	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
32	9	120065	Variation Margin Account - USD	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
33	10	130010	Debt Securities - Sovereign Bonds (EUR)	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
34	10	130015	Debt Securities - U.S. Treasuries (USD)	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
35	10	130020	Equity Securities - Domestic Shares (EUR)	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
36	2	130025	Equity Securities - International Shares (USD)	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
37	11	140010	FX Forward Contracts - Asset	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
38	11	140020	Options Premium Purchased	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
39	11	140030	FX Future Contracts - Asset	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
40	11	140040	Financial Futures - Asset	t	2	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
41	12	190010	Accrued Interest Receivable - Debt Securities	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
42	12	190020	Dividends Receivable	t	1	1	1	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
43	13	210010	Bank Overdrafts - EUR	t	1	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
44	13	210015	Bank Overdrafts - USD	t	2	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
45	13	210020	Short-Term Repo Loans	t	1	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
46	14	240010	FX Forward Contracts - Liability	t	1	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
47	14	240020	Options Premium Written	t	1	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
48	14	240030	FX Future Contracts - Liability	t	2	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
49	14	240040	Financial Futures - Liability	t	2	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
50	14	240050	Due to Brokers / Settlement Liabilities	t	1	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
51	14	240090	Currency Clearing Account	t	1	1	2	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
52	15	300010	Share Capital	t	1	1	3	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
53	15	300050	Retained Earnings	t	1	1	3	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
54	15	300080	FX Translation Reserve	t	1	1	3	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
55	16	400010	Realized Gain on Financial Derivatives	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
56	16	400020	Unrealized Gain on Financial Derivatives	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
57	17	410010	Realized Foreign Exchange Gains	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
58	17	410020	Unrealized Foreign Exchange Gains	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
59	18	420010	Interest Income - Bank & Short-Term Deposits	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
60	18	420020	Interest Income - Sovereign Debt (Coupons)	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
61	18	420050	Realized Gain on Debt Securities	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
62	18	420060	Realized Gain on Equity Securities	t	1	2	4	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
63	19	500010	Realized Loss on Financial Derivatives	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
64	19	500020	Unrealized Loss on Financial Derivatives	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
65	20	510010	Realized Foreign Exchange Losses	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
66	20	510020	Unrealized Foreign Exchange Losses	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
67	21	520010	Interest Expense on Borrowings / Repo	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
68	21	520050	Realized Loss on Debt Securities	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
69	21	520060	Realized Loss on Equity Securities	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
70	22	580010	Brokerage and Execution Fees	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
71	22	580020	Clearing and Exchange Fees	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
72	22	580030	Custody and Safe-Keeping Fees	t	1	2	5	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
73	23	600010	Financial Commitments - Long Futures	t	2	3	6	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
74	23	600015	Financial Commitments - Short Futures	t	2	3	6	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
75	23	600020	Financial Commitments - Forward Currency Purchase	t	2	3	6	1	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
76	23	600030	Counterpart for Financial Commitments	t	2	3	6	2	2026-06-05 09:03:34.250266	2026-06-05 09:03:34.250266
\.


--
-- TOC entry 5257 (class 0 OID 27557)
-- Dependencies: 260
-- Data for Name: holiday; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) FROM stdin;
1	1	1	8	New Year Day
2	1	3	4	Good Friday
3	1	6	4	Easter Monday
4	1	1	5	Labor Day
5	1	25	12	Christmas Day
6	1	26	12	Christmas Holiday
7	2	1	1	New Year Day
8	2	19	1	Martin Luther King Jr. Day
9	2	16	2	Presidents Day
10	2	3	4	Good Friday
11	2	25	5	Memorial Day
12	2	19	6	Juneteenth National Independence Day
13	2	3	7	Independence Day
14	2	7	9	Labor Day
15	2	12	10	Columbus Day
16	2	11	11	Veterans Day
17	2	26	11	Thanksgiving
18	2	25	12	Christmas Day
\.


--
-- TOC entry 5259 (class 0 OID 27562)
-- Dependencies: 262
-- Data for Name: instrument_quote; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.instrument_quote (id_instrument_quote, master_data, provider, code, bid, ask) FROM stdin;
2	61	CmeGroupProvider	2674@M6EU6	1.16820	1.16820
1	60	InvestingComProvider	EURUSD	1.16350	1.16370
3	26	EuroNextProvider	IT0005441883	58.42000	58.45000
\.


--
-- TOC entry 5260 (class 0 OID 27565)
-- Dependencies: 263
-- Data for Name: instrument_quote_hist; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.instrument_quote_hist (id_instrument_quote_hist, instrument_quote, master_data, code, bid, ask, update_date) FROM stdin;
\.


--
-- TOC entry 5263 (class 0 OID 27570)
-- Dependencies: 266
-- Data for Name: issuer; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.issuer (id_issuer, short_issuer_name, long_issuer_name, country) FROM stdin;
1	REP ITA	Repubblica Italiana	1
\.


--
-- TOC entry 5265 (class 0 OID 27575)
-- Dependencies: 268
-- Data for Name: loan_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.loan_master_data (id_master_data, description, processing_fees, management_fees, incidental_expenses, default_interest, late_payment_fee, underwriting_fee, insurance_premium, tax_charges) FROM stdin;
\.


--
-- TOC entry 5266 (class 0 OID 27587)
-- Dependencies: 269
-- Data for Name: master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.master_data (id_master_data, code, currency, issue_date, maturity_date, type_of_interest, form, daycount, accrual_daycount, frequency, roll_convention, accrual_schedule_type, interest_rate, issue_price, redempion_price, business_days, asset_class, amortization_schedule, multiplier, description) FROM stdin;
16	IT0005668238	1	2025-09-09	2055-10-01	1	1	5	3	2	100	100	4.6500000000	99.56000	100.00000	2	4	3	0.01000	ITALIA/4.65 BTP 20551001 - BUONI DEL TESORO POLIENNALI
17	IT0005611741	1	2024-09-17	2054-10-01	1	1	5	3	2	100	100	4.3000000000	99.78900	100.00000	2	4	3	0.01000	ITALIA/4.3 BTP 20541001 - BUONI DEL TESORO POLIENNALI
18	IT0005534141	1	2023-02-23	2053-10-01	1	1	5	3	2	100	100	4.5000000000	99.55900	100.00000	2	4	3	0.01000	ITALIA/4.5 BTP 20531001 - BUONI DEL TESORO POLIENNALI
19	IT0005217390	1	2016-09-01	2067-03-01	1	1	5	3	2	100	100	2.8000000000	99.19000	100.00000	2	4	3	0.01000	ITALIA/2.8 BTP 20670301 - BUONI DEL TESORO POLIENNALI
20	IT0005480980	1	2022-01-12	2052-09-01	1	1	5	3	2	100	100	2.1500000000	99.98700	100.00000	2	4	3	0.01000	ITALIA/2.15 BTP 20520901 - BUONI DEL TESORO POLIENNALI
21	IT0005425233	1	2020-09-01	2051-09-01	1	1	5	3	2	100	100	1.7000000000	98.68600	100.00000	2	4	3	0.01000	ITALIA/1.7 BTP 20510901 - BUONI DEL TESORO POLIENNALI
22	IT0005398406	1	2022-01-22	2050-09-01	1	1	5	3	2	100	100	2.4500000000	99.28000	100.00000	2	4	3	0.01000	ITALIA/2.45 BTP 20500901 - BUONI DEL TESORO POLIENNALI
23	IT0005363111	1	2019-02-13	2049-09-01	1	1	5	3	2	100	100	3.8500000000	99.59400	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20490901 - BUONI DEL TESORO POLIENNALI
24	IT0005273013	1	2017-03-01	2048-03-01	1	1	5	3	2	100	100	3.4500000000	98.95600	100.00000	2	4	3	0.01000	ITALIA/3.45 BTP 20480301 - BUONI DEL TESORO POLIENNALI
25	IT0005162828	1	2016-02-09	2047-03-01	1	1	5	3	2	100	100	2.7000000000	99.18000	100.00000	2	4	3	0.01000	ITALIA/2.7 BTP 20470301 - BUONI DEL TESORO POLIENNALI
26	IT0005441883	1	2021-03-01	2072-03-01	1	1	5	3	2	100	100	2.1500000000	99.46700	100.00000	2	4	3	0.01000	ITALIA/2.15 BTP 20720301 - BUONI DEL TESORO POLIENNALI
27	IT0005083057	1	2015-01-22	2046-09-01	1	1	5	3	2	100	100	3.2500000000	99.71000	100.00000	2	4	3	0.01000	ITALIA/3.25 BTP 20460901 - BUONI DEL TESORO POLIENNALI
28	IT0005631608	1	2025-01-15	2046-04-30	1	1	5	3	2	100	100	4.1000000000	99.46500	100.00000	2	4	3	0.01000	ITALIA/4.1 BTP 20460430 - BUONI DEL TESORO POLIENNALI
29	IT0005438004	1	2020-10-30	2045-04-30	1	1	5	3	2	100	100	1.5000000000	99.16800	100.00000	2	4	3	0.01000	ITALIA/1.5 BTP 20450430 - BUONI DEL TESORO POLIENNALI
30	IT0004923998	1	2013-03-01	2044-09-01	1	1	5	3	2	100	100	4.7500000000	97.22100	100.00000	2	4	3	0.01000	ITALIA/4.75 BTP 20440901 - BUONI DEL TESORO POLIENNALI
31	IT0005530032	1	2022-09-01	2043-09-01	1	1	5	3	2	100	100	4.4500000000	99.60600	100.00000	2	4	3	0.01000	ITALIA/4.45 BTP 20430901 - BUONI DEL TESORO POLIENNALI
32	IT0005421703	1	2020-09-11	2041-03-01	1	1	5	3	2	100	100	1.8000000000	99.76500	100.00000	2	4	3	0.01000	ITALIA/1.8 BTP 20410301 - BUONI DEL TESORO POLIENNALI
33	IT0005635583	1	2025-02-18	2040-10-01	1	1	5	3	2	100	100	3.8500000000	99.37500	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20401001 - BUONI DEL TESORO POLIENNALI
34	IT0004532559	1	2009-09-01	2040-09-01	1	1	5	3	2	100	100	5.0000000000	98.18600	100.00000	2	4	3	0.01000	ITALIA/5 BTP 20400901 - BUONI DEL TESORO POLIENNALI
35	IT0005377152	1	2019-06-19	2040-03-01	1	1	5	3	2	100	100	3.1000000000	99.62300	100.00000	2	4	3	0.01000	ITALIA/3.1 BTP 20400301 - BUONI DEL TESORO POLIENNALI
36	IT0005582421	1	2023-10-01	2039-10-01	1	1	5	3	2	100	100	4.1500000000	99.68000	100.00000	2	4	3	0.01000	ITALIA/4.15 BTP 20391001 - BUONI DEL TESORO POLIENNALI
37	IT0005442097	1	2021-04-27	2037-04-27	1	1	5	3	2	100	100	0.0000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/TV BTP FUTURA 20370427 EX - BTP FUTURA
38	IT0004286966	1	2007-08-01	2039-08-01	1	1	5	3	2	100	100	5.0000000000	99.98000	100.00000	2	4	3	0.01000	ITALIA/5 BTP 20390801 - BUONI DEL TESORO POLIENNALI
39	IT0005321325	1	2017-09-01	2038-09-01	1	1	5	3	2	100	100	2.9500000000	99.76600	100.00000	2	4	3	0.01000	ITALIA/2.95 BTP 20380901 - BUONI DEL TESORO POLIENNALI
40	IT0005496770	1	2022-03-01	2038-03-01	1	1	5	3	2	100	100	3.2500000000	99.65100	100.00000	2	4	3	0.01000	ITALIA/3.25 BTP 20380301 - BUONI DEL TESORO POLIENNALI
41	IT0005596470	1	2024-04-30	2037-10-30	1	1	5	3	2	100	100	4.0500000000	99.86500	100.00000	2	4	3	0.01000	ITALIA/4.05 BTP 20371030 - BUONI DEL TESORO POLIENNALI
42	IT0005433195	1	2021-01-12	2037-03-01	1	1	5	3	2	100	100	0.9500000000	99.40900	100.00000	2	4	3	0.01000	ITALIA/0.95 BTP 20370301 - BUONI DEL TESORO POLIENNALI
43	IT0003934657	1	2005-08-01	2037-02-01	1	1	5	3	2	100	100	4.0000000000	101.28900	100.00000	2	4	3	0.01000	ITALIA/4 BTP 20370201 - BUONI DEL TESORO POLIENNALI
44	IT0005177909	1	2016-03-01	2036-09-01	1	1	5	3	2	100	100	2.2500000000	99.36800	100.00000	2	4	3	0.01000	ITALIA/2.25 BTP 20360901 - BUONI DEL TESORO POLIENNALI
45	IT0005402117	1	2020-02-18	2036-03-01	1	1	5	3	2	100	100	1.4500000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/1.45 BTP 20360301 - BUONI DEL TESORO POLIENNALI
46	IT0005676504	1	2025-11-03	2036-02-01	1	1	5	3	2	100	100	0.0000000000	100.19000	100.00000	2	4	3	0.01000	ITALIA/3.45 BTP 20360201 - BUONI DEL TESORO POLIENNALI
47	IT0005648149	1	2025-05-02	2035-10-01	1	1	5	3	2	100	100	3.6000000000	100.09000	100.00000	2	4	3	0.01000	ITALIA/3.6 BTP 20351001 - BUONI DEL TESORO POLIENNALI
48	IT0005631590	1	2025-01-15	2035-08-01	1	1	5	3	2	100	100	3.6500000000	99.57700	100.00000	2	4	3	0.01000	ITALIA/3.65 BTP 20350801 - BUONI DEL TESORO POLIENNALI
49	IT0005607970	1	2024-08-01	2035-02-01	1	1	5	3	2	100	100	3.8500000000	101.04000	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20350201 - BUONI DEL TESORO POLIENNALI
50	IT0005508590	1	2022-09-13	2035-04-30	1	1	5	3	2	100	100	4.0000000000	99.73400	100.00000	2	4	3	0.01000	ITALIA/4 BTP 20350430 - BUONI DEL TESORO POLIENNALI
51	IT0005358806	1	2019-01-22	2035-03-01	1	1	5	3	2	100	100	3.3500000000	99.60900	100.00000	2	4	3	0.01000	ITALIA/3.35 BTP 20350301 - BUONI DEL TESORO POLIENNALI
52	IT0005466351	1	2021-11-16	2033-11-16	1	1	5	3	2	100	100	0.0000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/TV BTP FUTURA 20331116 EX - BTP FUTURA
53	IT0005584856	1	2024-03-01	2034-07-01	1	1	5	3	2	100	100	3.8500000000	99.80000	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20340701 - BUONI DEL TESORO POLIENNALI
54	IT0003535157	1	2003-08-01	2034-08-01	1	1	5	3	2	100	100	5.0000000000	98.10600	100.00000	2	4	3	0.01000	ITALIA/5 BTP 20340801 - BUONI DEL TESORO POLIENNALI
55	IT0005634800	1	2025-02-25	2033-02-25	1	1	5	3	2	100	100	0.0000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/TV BTP PIU 20330225 - BTP PIU
56	IT0005560948	1	2023-09-01	2034-03-01	1	1	5	3	2	100	100	4.2000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/4.2 BTP 20340301 - BUONI DEL TESORO POLIENNALI
57	IT0005544082	1	2023-05-02	2033-11-01	1	1	5	3	2	100	100	4.3500000000	99.85000	100.00000	2	4	3	0.01000	ITALIA/4.35 BTP 20331101 - BUONI DEL TESORO POLIENNALI
58	IT0005240350	1	2017-01-25	2033-09-01	1	1	5	3	2	100	100	2.4500000000	99.13000	100.00000	2	4	3	0.01000	ITALIA/2.45 BTP 20330901 - BUONI DEL TESORO POLIENNALI
2	IT0005689887	1	2026-01-14	2027-01-14	1	1	5	3	1	100	100	0.0000000000	97.90400	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20270114 - BUONI DEL TESORO ORDINARI
4	IT0005695256	1	2026-02-13	2027-02-12	1	1	5	3	1	100	100	0.0000000000	97.95200	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20270212 - BUONI DEL TESORO ORDINARI
5	IT0005684888	1	2025-12-12	2026-12-14	1	1	5	3	1	100	100	0.0000000000	97.82500	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20261214 - BUONI DEL TESORO ORDINARI
6	IT0005678492	1	2025-11-14	2026-11-13	1	1	5	3	1	100	100	0.0000000000	97.95700	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20261113 - BUONI DEL TESORO ORDINARI
7	IT0005692485	1	2026-01-30	2026-07-31	1	1	5	3	1	100	100	0.0000000000	98.97900	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20260731 - BUONI DEL TESORO ORDINARI
8	IT0005674335	1	2025-10-14	2026-10-14	1	1	5	3	1	100	100	0.0000000000	97.96400	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20261014 - BUONI DEL TESORO ORDINARI
9	IT0005669269	1	2025-09-12	2026-09-14	1	1	5	3	1	100	100	0.0000000000	97.97200	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20260914 - BUONI DEL TESORO ORDINARI
11	IT0005666851	1	2025-08-14	2026-08-14	1	1	5	3	1	100	100	0.0000000000	98.00100	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20260814 - BUONI DEL TESORO ORDINARI
13	IT0005655037	1	2025-06-13	2026-06-12	1	1	5	3	1	100	100	0.0000000000	98.03400	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20260612 - BUONI DEL TESORO ORDINARI
14	IT0005660029	1	2025-07-14	2026-07-14	1	1	5	3	1	100	100	0.0000000000	98.05100	100.00000	2	5	3	0.01000	ITALIA/ZC BOT 20260714 - BUONI DEL TESORO ORDINARI
60	EURUSD	1	2026-06-05	2076-06-05	1	1	2	2	100	100	100	0.0000000000	0.00000	0.00000	2	9	1	1.00000	\N
61	M6EU6	2	2026-06-15	2026-09-14	1	1	3	3	100	100	100	0.0000000000	100.00000	100.00000	2	11	1	1.00000	Micro EUR/USD Futures - September
\.


--
-- TOC entry 5268 (class 0 OID 27596)
-- Dependencies: 271
-- Data for Name: mm_future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.mm_future_master_data (id_master_data, underlying, contract_value, tick_size, initial_margin, maintenance_margin) FROM stdin;
\.


--
-- TOC entry 5269 (class 0 OID 27599)
-- Dependencies: 272
-- Data for Name: normal_balances; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.normal_balances (balance_id, code, description) FROM stdin;
1	DEBIT	Dr
2	CREDIT	Cr
\.


--
-- TOC entry 5271 (class 0 OID 27604)
-- Dependencies: 274
-- Data for Name: portfolio_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.portfolio_master_data (id_portfolio, currency, code, description) FROM stdin;
\.


--
-- TOC entry 5273 (class 0 OID 27608)
-- Dependencies: 276
-- Data for Name: position_detail; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.position_detail (id_position_detail, position_md, master_data, counterparty, realized_pnl, unrealized_pnl, buy_qty, notional_value_buy, buy_fees, buy_taxes, sell_qty, notional_value_sell, sell_fees, sell_taxes, multiplier, market_price) FROM stdin;
\.


--
-- TOC entry 5275 (class 0 OID 27613)
-- Dependencies: 278
-- Data for Name: position_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.position_master_data (id_position, portfolio, currency, code, description) FROM stdin;
\.


--
-- TOC entry 5277 (class 0 OID 27617)
-- Dependencies: 280
-- Data for Name: roll_convention; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.roll_convention (id_roll_convention, code, description) FROM stdin;
1	PREVIOUS	Previus
2	PREVIOUS-MODIFIED	Previus Following
3	FORWARD	Following
4	FORWARD-MODIFIED	Modified Following
100	UNADJUSTED	Unadjusted
\.


--
-- TOC entry 5279 (class 0 OID 27621)
-- Dependencies: 282
-- Data for Name: security_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.security_master_data (id_master_data, isin, cfi_code, fisn, lei, issuer, nominal_value, first_coupon_rate, first_coupon_payment_date) FROM stdin;
3	IT0005670895	DYZTXB	ITALIA/ZC BOT 20260331	815600DE60799F5A9309	1	6500.0000000000	0.0000000000	2025-09-30
1	IT0005640666	DYZTXB	ITALIA/ZC BOT 20260313	815600DE60799F5A9309	1	9000.0000000000	0.0000000000	2025-03-14
2	IT0005689887	DYZTXB	ITALIA/ZC BOT 20270114	815600DE60799F5A9309	1	8800.0000000000	0.0000000000	2026-01-14
4	IT0005695256	DYZTXB	ITALIA/ZC BOT 20270212	815600DE60799F5A9309	1	9350.0000000000	0.0000000000	2026-02-13
5	IT0005684888	DYZTXB	ITALIA/ZC BOT 20261214	815600DE60799F5A9309	1	9900.0000000000	0.0000000000	2025-12-12
6	IT0005678492	DYZTXB	ITALIA/ZC BOT 20261113	815600DE60799F5A9309	1	8500.0000000000	0.0000000000	2025-11-14
7	IT0005692485	DYZTXB	ITALIA/ZC BOT 20260731	815600DE60799F5A9309	1	8250.0000000000	0.0000000000	2026-01-30
8	IT0005674335	DYZTXB	ITALIA/ZC BOT 20261014	815600DE60799F5A9309	1	9900.0000000000	0.0000000000	2025-10-14
9	IT0005669269	DYZTXB	ITALIA/ZC BOT 20260914	815600DE60799F5A9309	1	9000.0000000000	0.0000000000	2025-09-12
10	IT0005680639	DYZTXB	ITALIA/ZC BOT 20260529	815600DE60799F5A9309	1	7520.0000000000	0.0000000000	2025-11-28
11	IT0005666851	DYZTXB	ITALIA/ZC BOT 20260814	815600DE60799F5A9309	1	8000.0000000000	0.0000000000	2025-08-14
12	IT0005650574	DYZTXB	ITALIA/ZC BOT 20260514	815600DE60799F5A9309	1	8500.0000000000	0.0000000000	2025-05-14
13	IT0005655037	DYZTXB	ITALIA/ZC BOT 20260612	815600DE60799F5A9309	1	9350.0000000000	0.0000000000	2025-06-13
14	IT0005660029	DYZTXB	ITALIA/ZC BOT 20260714	815600DE60799F5A9309	1	7500.0000000000	0.0000000000	2025-07-14
15	IT0005645509	DYZTXB	ITALIA/ZC BOT 20260414	815600DE60799F5A9309	1	7700.0000000000	0.0000000000	2025-04-14
16	IT0005668238	DBFTFB	ITALIA/4.65 BTP 20551001	815600DE60799F5A9309	1	5000.0000000000	0.2795100000	2025-10-01
17	IT0005611741	DBFTFB	ITALIA/4.3 BTP 20541001	815600DE60799F5A9309	1	8000.0000000000	0.1644800000	2024-10-01
18	IT0005534141	DBFTFB	ITALIA/4.5 BTP 20531001	815600DE60799F5A9309	1	5000.0000000000	0.4574200000	2023-04-01
19	IT0005217390	DBFTFB	ITALIA/2.8 BTP 20670301	815600DE60799F5A9309	1	5000000000.0000000000	1.4000000000	2017-03-01
20	IT0005480980	DBFTFB	ITALIA/2.15 BTP 20520901	815600DE60799F5A9309	1	7000.0000000000	0.2850800000	2022-03-01
21	IT0005425233	DBFTFB	ITALIA/1.7 BTP 20510901	815600DE60799F5A9309	1	8000.0000000000	0.8500000000	2021-03-01
22	IT0005398406	DBFTFB	ITALIA/2.45 BTP 20500901	815600DE60799F5A9309	1	15835.0000000000	1.2250000000	2020-03-01
23	IT0005363111	DBFTFB	ITALIA/3.85 BTP 20490901	815600DE60799F5A9309	1	13342.0000000000	1.9250000000	2019-03-01
24	IT0005273013	DBFTFB	ITALIA/3.45 BTP 20480301	815600DE60799F5A9309	1	500000000.0000000000	1.7250000000	2017-09-01
25	IT0005162828	DBFTFB	ITALIA/2.7 BTP 20470301	815600DE60799F5A9309	1	9000000000.0000000000	0.1557700000	2016-03-01
26	IT0005441883	DBFTFB	ITALIA/2.15 BTP 20720301	815600DE60799F5A9309	1	5000.0000000000	1.0750000000	2021-09-01
27	IT0005083057	DBFTFB	ITALIA/3.25 BTP 20460901	815600DE60799F5A9309	1	13240700000.0000000000	1.6250000000	2015-03-01
28	IT0005631608	DBFTFB	ITALIA/4.1 BTP 20460430	815600DE60799F5A9309	1	5000.0000000000	1.1826900000	2025-04-30
29	IT0005438004	DBFTFB	ITALIA/1.5 BTP 20450430	815600DE60799F5A9309	1	8500.0000000000	0.7500000000	2021-04-30
30	IT0004923998	DBFTFB	ITALIA/4.75 BTP 20440901	815600DE60799F5A9309	1	6000000000.0000000000	2.3750000000	2013-09-01
31	IT0005530032	DBFTFB	ITALIA/4.45 BTP 20430901	815600DE60799F5A9309	1	7000.0000000000	2.2250000000	2023-03-01
32	IT0005421703	DBFTFB	ITALIA/1.8 BTP 20410301	815600DE60799F5A9309	1	10000.0000000000	0.8303900000	2021-03-01
33	IT0005635583	DBFTFB	ITALIA/3.85 BTP 20401001	815600DE60799F5A9309	1	13000.0000000000	0.4442300000	2025-04-01
34	IT0004532559	DBFTFB	ITALIA/5 BTP 20400901	815600DE60799F5A9309	1	0.0000000000	2.5000000000	2010-03-01
35	IT0005377152	DBFTFB	ITALIA/3.1 BTP 20400301	815600DE60799F5A9309	1	69699.0000000000	1.5500000000	2019-09-01
36	IT0005582421	DBFTFB	ITALIA/4.15 BTP 20391001	815600DE60799F5A9309	1	10000.0000000000	2.0750000000	2024-04-01
37	IT0005442097	DBVTFB	ITALIA/TV BTP FUTURA 20370427	815600DE60799F5A9309	1	0.0000000000	0.3750000000	2021-10-27
38	IT0004286966	DBFTFB	ITALIA/5 BTP 20390801	815600DE60799F5A9309	1	0.0000000000	2.5000000000	2008-02-01
39	IT0005321325	DBFTFB	ITALIA/2.95 BTP 20380901	815600DE60799F5A9309	1	0.0000000000	1.4750000000	2018-03-01
40	IT0005496770	DBFTFB	ITALIA/3.25 BTP 20380301	815600DE60799F5A9309	1	5000.0000000000	1.6250000000	2022-09-01
41	IT0005596470	DBFTFB	ITALIA/4.05 BTP 20371030	815600DE60799F5A9309	1	9000.0000000000	2.0250000000	2024-10-30
42	IT0005433195	DBFTFB	ITALIA/0.95 BTP 20370301	815600DE60799F5A9309	1	10000000000.0000000000	0.1259700000	2021-03-01
43	IT0003934657	DBFTFB	ITALIA/4 BTP 20370201	815600DE60799F5A9309	1	0.0000000000	2.0000000000	2006-02-01
44	IT0005177909	DBFTFB	ITALIA/2.25 BTP 20360901	815600DE60799F5A9309	1	500000000.0000000000	1.1250000000	2016-09-01
45	IT0005402117	DBFTFB	ITALIA/1.45 BTP 20360301	815600DE60799F5A9309	1	14400.0000000000	0.7728000000	2020-09-01
46	IT0005676504	DBFTFB	ITALIA/3.45 BTP 20360201	815600DE60799F5A9309	1	4500.0000000000	0.8437500000	2026-02-01
47	IT0005648149	DBFTFB	ITALIA/3.6 BTP 20351001	815600DE60799F5A9309	1	4000.0000000000	1.4950800000	2025-10-01
48	IT0005631590	DBFTFB	ITALIA/3.65 BTP 20350801	815600DE60799F5A9309	1	13000.0000000000	0.1686100000	2025-02-01
49	IT0005607970	DBFTFB	ITALIA/3.85 BTP 20350201	815600DE60799F5A9309	1	4500.0000000000	1.9250000000	2024-08-01
50	IT0005508590	DBFTFB	ITALIA/4 BTP 20350430	815600DE60799F5A9309	1	8000.0000000000	2.0000000000	2022-10-30
51	IT0005358806	DBFTFB	ITALIA/3.35 BTP 20350301	815600DE60799F5A9309	1	14800.0000000000	1.6750000000	2019-03-01
52	IT0005466351	DBVTFB	ITALIA/TV BTP FUTURA 20331116	815600DE60799F5A9309	1	3268.2400000000	0.3750000000	2022-05-16
53	IT0005584856	DBFTFB	ITALIA/3.85 BTP 20340701	815600DE60799F5A9309	1	4500.0000000000	1.2903900000	2024-07-01
54	IT0003535157	DBFTFB	ITALIA/5 BTP 20340801	815600DE60799F5A9309	1	0.0000000000	2.5000000000	2004-02-01
55	IT0005634800	DBVTFB	ITALIA/TV BTP PIU 20330225	815600DE60799F5A9309	1	0.0000000000	0.7125000000	2025-05-25
56	IT0005560948	DBFTFB	ITALIA/4.2 BTP 20340301	815600DE60799F5A9309	1	4875.0000000000	2.1000000000	2024-03-01
57	IT0005544082	DBFTFB	ITALIA/4.35 BTP 20331101	815600DE60799F5A9309	1	5000.0000000000	2.1631800000	2023-11-01
58	IT0005240350	DBFTFB	ITALIA/2.45 BTP 20330901	815600DE60799F5A9309	1	6000000000.0000000000	1.2250000000	2017-03-01
\.


--
-- TOC entry 5280 (class 0 OID 27626)
-- Dependencies: 283
-- Data for Name: settlement_type; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.settlement_type (id_settlement_type, code, description) FROM stdin;
1	PHYSICAL	Physical Settlement
2	CASH	Cash Settlement
\.


--
-- TOC entry 5282 (class 0 OID 27631)
-- Dependencies: 285
-- Data for Name: super_class; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.super_class (id_super_class, code, description) FROM stdin;
1	EQU	Equities
2	FIN	Fixed Income
3	CCE	 Cash and Cash Equivalents (Money Market)
4	DER	 Derivatives
5	FX	 Forex
\.


--
-- TOC entry 5284 (class 0 OID 27636)
-- Dependencies: 287
-- Data for Name: txn_status; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.txn_status (id_txn_status, code, description) FROM stdin;
1	PENDING	Pending
2	VALIDATING	Validating
3	EXECUTED	Executed
4	REJECTED	Rejected
5	TO_AMEND	To Amend
6	AMENDED	Amended
7	TO_CANCEL	To Cancel
8	CANCELLED	Cancelled
9	RESTARTING	Restartin
\.


--
-- TOC entry 5286 (class 0 OID 27640)
-- Dependencies: 289
-- Data for Name: type_of_interest; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.type_of_interest (id_type_of_interest, code, description) FROM stdin;
1	FIXED	Fixed-Rate
2	FLOATING	Floating-Rate
3	ZERO-COUPON	Zero-Coupon
4	INFLATION	Inflation-Linked
5	CONVERTIBLE	Convertible
6	CALLABLE	Callable
100	NONE	None
\.


--
-- TOC entry 5288 (class 0 OID 27644)
-- Dependencies: 291
-- Data for Name: yield_curve; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.yield_curve (id_yield_curve, code, description, currency, calendar, compounding, provider) FROM stdin;
1	TERMESTR	Estr Averages Rates	1	1	1	CmeGroupProvider
2	TERMSOFR	Sofr Averages Rates	2	2	1	CmeGroupProvider
3	ITYIELD	Italy - Government Bonds	1	1	2	InvestingComProvider
4	USYIELD	United States - Government Bonds	2	2	2	InvestingComProvider
5	EURIBOR	Euribor Rates	1	1	1	
6	FMIRS	Eurirs Rates	1	1	2	Sole24hProvider
7	ECBYC	European Central Bank Yield Curve	1	1	3	
\.


--
-- TOC entry 5289 (class 0 OID 27650)
-- Dependencies: 292
-- Data for Name: yield_curve_item; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.yield_curve_item (id_yield_curve_item, yield_curve, ric, offset_type, offset_value, bid, ask, compounding, daycount) FROM stdin;
56	4	U.S. 4M	2	4	0.03730	0.03730	1	3
2	6	EUR 01A Irs	3	1	0.02810	0.02810	2	3
3	6	EUR 02A Irs	3	2	0.02850	0.02850	2	3
4	6	EUR 03A Irs	3	3	0.02850	0.02850	2	3
5	6	EUR 04A Irs	3	4	0.02860	0.02860	2	3
6	6	EUR 05A Irs	3	5	0.02890	0.02890	2	3
7	6	EUR 06A Irs	3	6	0.02920	0.02920	2	3
8	6	EUR 07A Irs	3	7	0.02960	0.02960	2	3
9	6	EUR 08A Irs	3	8	0.03000	0.03000	2	3
10	6	EUR 09A Irs	3	9	0.03040	0.03040	2	3
11	6	EUR 10A Irs	3	10	0.03080	0.03080	2	3
12	6	EUR 11A Irs	3	11	0.03120	0.03120	2	3
13	6	EUR 12A Irs	3	12	0.03150	0.03150	2	3
14	6	EUR 15A Irs	3	15	0.03240	0.03240	2	3
15	6	EUR 20A Irs	3	20	0.03290	0.03290	2	3
16	6	EUR 25A Irs	3	25	0.03260	0.03260	2	3
17	6	EUR 30A Irs	3	30	0.03210	0.03210	2	3
18	6	EUR 40A Irs	3	40	0.03090	0.03090	2	3
19	6	EUR 50A Irs	3	50	0.02960	0.02960	2	3
21	3	Italy 1M	2	1	0.02173	0.02173	1	3
22	3	Italy 3M	2	3	0.02283	0.02283	1	3
23	3	Italy 6M	2	6	0.02428	0.02428	1	3
24	3	Italy 9M	2	9	0.02545	0.02545	1	3
25	3	Italy 1Y	3	1	0.02593	0.02593	2	3
26	3	Italy 2Y	3	2	0.02833	0.02833	2	3
27	3	Italy 3Y	3	3	0.02933	0.02933	2	3
28	3	Italy 4Y	3	4	0.03081	0.03081	2	3
29	3	Italy 5Y	3	5	0.03163	0.03163	2	3
30	3	Italy 6Y	3	6	0.03349	0.03349	2	3
31	3	Italy 7Y	3	7	0.03443	0.03443	2	3
32	3	Italy 8Y	3	8	0.03596	0.03596	2	3
33	3	Italy 9Y	3	9	0.03746	0.03746	2	3
34	3	Italy 10Y	3	10	0.03788	0.03788	2	3
35	3	Italy 15Y	3	15	0.04238	0.04238	2	3
36	3	Italy 20Y	3	20	0.04412	0.04412	2	3
37	3	Italy 25Y	3	25	0.04513	0.04513	2	3
38	3	Italy 30Y	3	30	0.04630	0.04630	2	3
39	3	Italy 50Y	3	50	0.04256	0.04256	2	3
41	1	Ovn	1	1	0.01933	0.01933	1	2
42	1	1M	2	1	0.02112	0.02112	1	2
43	1	3M	2	3	0.02199	0.02199	1	2
44	1	6M	2	6	0.02330	0.02330	1	2
45	1	1Y	3	1	0.02497	0.02497	1	2
47	2	Ovn	1	1	0.03610	0.03610	1	2
48	2	1M	2	1	0.03612	0.03612	1	2
49	2	3M	2	3	0.03649	0.03649	1	2
50	2	6M	2	6	0.03708	0.03708	1	2
51	2	1Y	3	1	0.03845	0.03845	1	2
53	4	U.S. 1M	2	1	0.03681	0.03681	1	3
54	4	U.S. 2M	2	2	0.03680	0.03680	1	3
55	4	U.S. 3M	2	3	0.03710	0.03710	1	3
57	4	U.S. 6M	2	6	0.03764	0.03764	1	3
58	4	U.S. 1Y	3	1	0.03791	0.03791	2	3
59	4	U.S. 2Y	3	2	0.04037	0.04037	2	3
60	4	U.S. 3Y	3	3	0.04088	0.04088	2	3
61	4	U.S. 5Y	3	5	0.04176	0.04176	2	3
62	4	U.S. 7Y	3	7	0.04317	0.04317	2	3
63	4	U.S. 10Y	3	10	0.04470	0.04470	2	3
64	4	U.S. 20Y	3	20	0.04979	0.04979	2	3
65	4	U.S. 30Y	3	30	0.04976	0.04976	2	3
\.


--
-- TOC entry 5297 (class 0 OID 0)
-- Dependencies: 218
-- Name: account_natures_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.account_natures_s', 6, true);


--
-- TOC entry 5298 (class 0 OID 0)
-- Dependencies: 220
-- Name: accrual_schedule_type_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.accrual_schedule_type_s', 1, false);


--
-- TOC entry 5299 (class 0 OID 0)
-- Dependencies: 222
-- Name: amortization_schedule_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.amortization_schedule_s', 3, true);


--
-- TOC entry 5300 (class 0 OID 0)
-- Dependencies: 224
-- Name: asset_class_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.asset_class_s', 12, true);


--
-- TOC entry 5301 (class 0 OID 0)
-- Dependencies: 227
-- Name: calendar_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.calendar_s', 2, true);


--
-- TOC entry 5302 (class 0 OID 0)
-- Dependencies: 229
-- Name: cash_flow_item_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.cash_flow_item_s', 1967, true);


--
-- TOC entry 5303 (class 0 OID 0)
-- Dependencies: 231
-- Name: cash_flow_reset_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.cash_flow_reset_s', 1, false);


--
-- TOC entry 5304 (class 0 OID 0)
-- Dependencies: 233
-- Name: compounding_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.compounding_s', 4, true);


--
-- TOC entry 5305 (class 0 OID 0)
-- Dependencies: 235
-- Name: counterparty_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.counterparty_s', 1, false);


--
-- TOC entry 5306 (class 0 OID 0)
-- Dependencies: 237
-- Name: counterparty_type_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.counterparty_type_s', 6, true);


--
-- TOC entry 5307 (class 0 OID 0)
-- Dependencies: 239
-- Name: country_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.country_s', 7, true);


--
-- TOC entry 5308 (class 0 OID 0)
-- Dependencies: 241
-- Name: currency_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.currency_s', 7, true);


--
-- TOC entry 5309 (class 0 OID 0)
-- Dependencies: 244
-- Name: daycount_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.daycount_s', 6, true);


--
-- TOC entry 5310 (class 0 OID 0)
-- Dependencies: 246
-- Name: deliverable_bonds_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.deliverable_bonds_s', 1, false);


--
-- TOC entry 5311 (class 0 OID 0)
-- Dependencies: 248
-- Name: financial_statement_types_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.financial_statement_types_s', 3, true);


--
-- TOC entry 5312 (class 0 OID 0)
-- Dependencies: 250
-- Name: financial_txn_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.financial_txn_s', 1, false);


--
-- TOC entry 5313 (class 0 OID 0)
-- Dependencies: 253
-- Name: form_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.form_s', 3, true);


--
-- TOC entry 5314 (class 0 OID 0)
-- Dependencies: 255
-- Name: frequency_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.frequency_s', 6, true);


--
-- TOC entry 5315 (class 0 OID 0)
-- Dependencies: 259
-- Name: gl_accounts_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.gl_accounts_s', 76, true);


--
-- TOC entry 5316 (class 0 OID 0)
-- Dependencies: 261
-- Name: holiday_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.holiday_s', 21, true);


--
-- TOC entry 5317 (class 0 OID 0)
-- Dependencies: 264
-- Name: instrument_quote_hist_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.instrument_quote_hist_s', 1, false);


--
-- TOC entry 5318 (class 0 OID 0)
-- Dependencies: 265
-- Name: instrument_quote_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.instrument_quote_s', 3, true);


--
-- TOC entry 5319 (class 0 OID 0)
-- Dependencies: 267
-- Name: issuer_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.issuer_s', 1, true);


--
-- TOC entry 5320 (class 0 OID 0)
-- Dependencies: 270
-- Name: master_data_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.master_data_s', 61, true);


--
-- TOC entry 5321 (class 0 OID 0)
-- Dependencies: 273
-- Name: normal_balances_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.normal_balances_s', 2, true);


--
-- TOC entry 5322 (class 0 OID 0)
-- Dependencies: 275
-- Name: portfolio_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.portfolio_master_data_s', 1, false);


--
-- TOC entry 5323 (class 0 OID 0)
-- Dependencies: 277
-- Name: position_detail_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.position_detail_s', 1, false);


--
-- TOC entry 5324 (class 0 OID 0)
-- Dependencies: 279
-- Name: position_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.position_master_data_s', 1, false);


--
-- TOC entry 5325 (class 0 OID 0)
-- Dependencies: 281
-- Name: roll_convention_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.roll_convention_s', 4, true);


--
-- TOC entry 5326 (class 0 OID 0)
-- Dependencies: 284
-- Name: settlement_type_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.settlement_type_s', 2, true);


--
-- TOC entry 5327 (class 0 OID 0)
-- Dependencies: 286
-- Name: super_class_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.super_class_s', 5, true);


--
-- TOC entry 5328 (class 0 OID 0)
-- Dependencies: 288
-- Name: txn_status_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.txn_status_s', 9, true);


--
-- TOC entry 5329 (class 0 OID 0)
-- Dependencies: 290
-- Name: type_of_interest_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.type_of_interest_s', 6, true);


--
-- TOC entry 5330 (class 0 OID 0)
-- Dependencies: 293
-- Name: yield_curve_item_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.yield_curve_item_s', 65, true);


--
-- TOC entry 5331 (class 0 OID 0)
-- Dependencies: 294
-- Name: yield_curve_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.yield_curve_s', 7, true);


--
-- TOC entry 4893 (class 2606 OID 27657)
-- Name: account_natures account_natures_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.account_natures
    ADD CONSTRAINT account_natures_pkey PRIMARY KEY (nature_id);


--
-- TOC entry 4895 (class 2606 OID 27659)
-- Name: accrual_schedule_type accrual_schedule_type_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.accrual_schedule_type
    ADD CONSTRAINT accrual_schedule_type_pkey PRIMARY KEY (id_accrual_schedule_type);


--
-- TOC entry 4898 (class 2606 OID 27661)
-- Name: amortization_schedule amortization_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.amortization_schedule
    ADD CONSTRAINT amortization_schedule_pkey PRIMARY KEY (id_amortization_schedule);


--
-- TOC entry 4901 (class 2606 OID 27663)
-- Name: asset_class asset_class_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT asset_class_pkey PRIMARY KEY (id_asset_class);


--
-- TOC entry 4904 (class 2606 OID 27665)
-- Name: bond_future_master_data bond_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.bond_future_master_data
    ADD CONSTRAINT bond_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4906 (class 2606 OID 27667)
-- Name: calendar calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (id_calendar);


--
-- TOC entry 4909 (class 2606 OID 27669)
-- Name: cash_flow_item cash_flow_item_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT cash_flow_item_pkey PRIMARY KEY (id_cash_flow_item);


--
-- TOC entry 4912 (class 2606 OID 27671)
-- Name: cash_flow_reset cash_flow_reset_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_reset
    ADD CONSTRAINT cash_flow_reset_pkey PRIMARY KEY (id_cash_flow_reset);


--
-- TOC entry 4915 (class 2606 OID 27673)
-- Name: compounding compounding_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.compounding
    ADD CONSTRAINT compounding_pkey PRIMARY KEY (id_compounding);


--
-- TOC entry 4918 (class 2606 OID 27675)
-- Name: counterparty counterparty_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT counterparty_pkey PRIMARY KEY (id_counterparty);


--
-- TOC entry 4921 (class 2606 OID 27677)
-- Name: counterparty_type counterparty_type_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty_type
    ADD CONSTRAINT counterparty_type_pkey PRIMARY KEY (id_counterparty_type);


--
-- TOC entry 4926 (class 2606 OID 27679)
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id_country);


--
-- TOC entry 4929 (class 2606 OID 27681)
-- Name: currency currency_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT currency_pkey PRIMARY KEY (id_currency);


--
-- TOC entry 4933 (class 2606 OID 27683)
-- Name: currpair_master_data currpair_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT currpair_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4936 (class 2606 OID 27685)
-- Name: daycount daycount_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.daycount
    ADD CONSTRAINT daycount_pkey PRIMARY KEY (id_daycount);


--
-- TOC entry 4939 (class 2606 OID 27687)
-- Name: deliverable_bonds deliverable_bonds_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT deliverable_bonds_pkey PRIMARY KEY (id_deliverable_bonds);


--
-- TOC entry 4942 (class 2606 OID 27689)
-- Name: financial_statement_types financial_statement_types_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_statement_types
    ADD CONSTRAINT financial_statement_types_pkey PRIMARY KEY (statement_type_id);


--
-- TOC entry 4944 (class 2606 OID 27691)
-- Name: financial_txn financial_txn_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT financial_txn_pkey PRIMARY KEY (id_financial_txn);


--
-- TOC entry 4946 (class 2606 OID 27693)
-- Name: forex_master_data forex_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT forex_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4949 (class 2606 OID 27695)
-- Name: form form_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.form
    ADD CONSTRAINT form_pkey PRIMARY KEY (id_form);


--
-- TOC entry 4952 (class 2606 OID 27697)
-- Name: frequency frequency_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.frequency
    ADD CONSTRAINT frequency_pkey PRIMARY KEY (id_frequency);


--
-- TOC entry 4955 (class 2606 OID 27699)
-- Name: future_master_data future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.future_master_data
    ADD CONSTRAINT future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4958 (class 2606 OID 27701)
-- Name: fx_future_master_data fx_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fx_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4960 (class 2606 OID 27703)
-- Name: gl_accounts gl_accounts_code_key; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT gl_accounts_code_key UNIQUE (code);


--
-- TOC entry 4962 (class 2606 OID 27705)
-- Name: gl_accounts gl_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT gl_accounts_pkey PRIMARY KEY (account_id);


--
-- TOC entry 4965 (class 2606 OID 27707)
-- Name: holiday holiday_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT holiday_pkey PRIMARY KEY (id_holiday);


--
-- TOC entry 4971 (class 2606 OID 27709)
-- Name: instrument_quote_hist instrument_quote_hist_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT instrument_quote_hist_pkey PRIMARY KEY (id_instrument_quote_hist);


--
-- TOC entry 4968 (class 2606 OID 27711)
-- Name: instrument_quote instrument_quote_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT instrument_quote_pkey PRIMARY KEY (id_instrument_quote);


--
-- TOC entry 4973 (class 2606 OID 27713)
-- Name: issuer issuer_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT issuer_pkey PRIMARY KEY (id_issuer);


--
-- TOC entry 4975 (class 2606 OID 27715)
-- Name: loan_master_data loan_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.loan_master_data
    ADD CONSTRAINT loan_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4978 (class 2606 OID 27717)
-- Name: master_data master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4980 (class 2606 OID 27719)
-- Name: mm_future_master_data mm_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT mm_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4982 (class 2606 OID 27721)
-- Name: normal_balances normal_balances_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.normal_balances
    ADD CONSTRAINT normal_balances_pkey PRIMARY KEY (balance_id);


--
-- TOC entry 4985 (class 2606 OID 27723)
-- Name: portfolio_master_data portfolio_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT portfolio_master_data_pkey PRIMARY KEY (id_portfolio);


--
-- TOC entry 4988 (class 2606 OID 27725)
-- Name: position_detail position_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT position_detail_pkey PRIMARY KEY (id_position_detail);


--
-- TOC entry 4991 (class 2606 OID 27727)
-- Name: position_master_data position_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT position_master_data_pkey PRIMARY KEY (id_position);


--
-- TOC entry 4994 (class 2606 OID 27729)
-- Name: roll_convention roll_convention_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.roll_convention
    ADD CONSTRAINT roll_convention_pkey PRIMARY KEY (id_roll_convention);


--
-- TOC entry 4997 (class 2606 OID 27731)
-- Name: security_master_data security_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT security_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 5000 (class 2606 OID 27733)
-- Name: settlement_type settlement_type_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.settlement_type
    ADD CONSTRAINT settlement_type_pkey PRIMARY KEY (id_settlement_type);


--
-- TOC entry 5003 (class 2606 OID 27735)
-- Name: super_class super_class_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.super_class
    ADD CONSTRAINT super_class_pkey PRIMARY KEY (id_super_class);


--
-- TOC entry 5006 (class 2606 OID 27737)
-- Name: txn_status txn_status_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.txn_status
    ADD CONSTRAINT txn_status_pkey PRIMARY KEY (id_txn_status);


--
-- TOC entry 5009 (class 2606 OID 27739)
-- Name: type_of_interest type_of_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.type_of_interest
    ADD CONSTRAINT type_of_interest_pkey PRIMARY KEY (id_type_of_interest);


--
-- TOC entry 5015 (class 2606 OID 27741)
-- Name: yield_curve_item yield_curve_item_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT yield_curve_item_pkey PRIMARY KEY (id_yield_curve_item);


--
-- TOC entry 5012 (class 2606 OID 27743)
-- Name: yield_curve yield_curve_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT yield_curve_pkey PRIMARY KEY (id_yield_curve);


--
-- TOC entry 4923 (class 1259 OID 27744)
-- Name: alfa_2_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX alfa_2_code ON public.country USING btree (alfa_2_code);


--
-- TOC entry 4924 (class 1259 OID 27745)
-- Name: alfa_3_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX alfa_3_code ON public.country USING btree (alfa_3_code);


--
-- TOC entry 4963 (class 1259 OID 27746)
-- Name: idx_account_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_account_code ON public.gl_accounts USING btree (code);


--
-- TOC entry 4896 (class 1259 OID 27747)
-- Name: idx_accrual_schedule_type_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_accrual_schedule_type_code ON public.accrual_schedule_type USING btree (code);


--
-- TOC entry 4899 (class 1259 OID 27748)
-- Name: idx_amortization_schedule_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_amortization_schedule_code ON public.amortization_schedule USING btree (code);


--
-- TOC entry 4902 (class 1259 OID 27749)
-- Name: idx_asset_class_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_asset_class_code ON public.asset_class USING btree (code);


--
-- TOC entry 4907 (class 1259 OID 27750)
-- Name: idx_calendar_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_calendar_code ON public.calendar USING btree (code);


--
-- TOC entry 4916 (class 1259 OID 27751)
-- Name: idx_compounding_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_compounding_code ON public.compounding USING btree (code);


--
-- TOC entry 4919 (class 1259 OID 27752)
-- Name: idx_counterparty_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_counterparty_code ON public.counterparty USING btree (code);


--
-- TOC entry 4922 (class 1259 OID 27753)
-- Name: idx_counterparty_type_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_counterparty_type_code ON public.counterparty_type USING btree (code);


--
-- TOC entry 4927 (class 1259 OID 27754)
-- Name: idx_country_numeric_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_country_numeric_code ON public.country USING btree (country_numeric_code);


--
-- TOC entry 4930 (class 1259 OID 27755)
-- Name: idx_currency_numeric_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_currency_numeric_code ON public.currency USING btree (currency_numeric_code);


--
-- TOC entry 4934 (class 1259 OID 27756)
-- Name: idx_currpair_bcy_ccy; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_currpair_bcy_ccy ON public.currpair_master_data USING btree (bcy, ccy);


--
-- TOC entry 4937 (class 1259 OID 27757)
-- Name: idx_daycount_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_daycount_code ON public.daycount USING btree (code);


--
-- TOC entry 4940 (class 1259 OID 27758)
-- Name: idx_deliverable_bonds_isin; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_deliverable_bonds_isin ON public.deliverable_bonds USING btree (master_data, isin);


--
-- TOC entry 4947 (class 1259 OID 27759)
-- Name: idx_forex_bcy_ccy; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_forex_bcy_ccy ON public.forex_master_data USING btree (bcy, ccy);


--
-- TOC entry 4950 (class 1259 OID 27760)
-- Name: idx_form_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_form_code ON public.form USING btree (code);


--
-- TOC entry 4953 (class 1259 OID 27761)
-- Name: idx_frequency_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_frequency_code ON public.frequency USING btree (code);


--
-- TOC entry 4956 (class 1259 OID 27762)
-- Name: idx_future_master_data_isin; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_future_master_data_isin ON public.future_master_data USING btree (isin);


--
-- TOC entry 4966 (class 1259 OID 27763)
-- Name: idx_instrument_quote_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_instrument_quote_code ON public.instrument_quote USING btree (code);


--
-- TOC entry 4969 (class 1259 OID 27764)
-- Name: idx_instrument_quote_hist_iqud; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_instrument_quote_hist_iqud ON public.instrument_quote_hist USING btree (instrument_quote, update_date);


--
-- TOC entry 4931 (class 1259 OID 27765)
-- Name: idx_iso_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_iso_code ON public.currency USING btree (iso_code);


--
-- TOC entry 4976 (class 1259 OID 27766)
-- Name: idx_master_data_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_master_data_code ON public.master_data USING btree (code);


--
-- TOC entry 4910 (class 1259 OID 27767)
-- Name: idx_md_ed; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_md_ed ON public.cash_flow_item USING btree (master_data, end_date);


--
-- TOC entry 4913 (class 1259 OID 27768)
-- Name: idx_md_sdr; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_md_sdr ON public.cash_flow_reset USING btree (master_data, start_date_reset);


--
-- TOC entry 4983 (class 1259 OID 27769)
-- Name: idx_portfolio_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_portfolio_code ON public.portfolio_master_data USING btree (code);


--
-- TOC entry 4989 (class 1259 OID 27770)
-- Name: idx_position_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_position_code ON public.position_master_data USING btree (code);


--
-- TOC entry 4986 (class 1259 OID 27771)
-- Name: idx_position_detail_pmc; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_position_detail_pmc ON public.position_detail USING btree (position_md, master_data, counterparty);


--
-- TOC entry 4992 (class 1259 OID 27772)
-- Name: idx_roll_convention_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_roll_convention_code ON public.roll_convention USING btree (code);


--
-- TOC entry 4995 (class 1259 OID 27773)
-- Name: idx_security_master_data_isin; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_security_master_data_isin ON public.security_master_data USING btree (isin);


--
-- TOC entry 4998 (class 1259 OID 27774)
-- Name: idx_settlement_type_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_settlement_type_code ON public.settlement_type USING btree (code);


--
-- TOC entry 5001 (class 1259 OID 27775)
-- Name: idx_super_class_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_super_class_code ON public.super_class USING btree (code);


--
-- TOC entry 5004 (class 1259 OID 27776)
-- Name: idx_txn_status_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_txn_status_code ON public.txn_status USING btree (code);


--
-- TOC entry 5007 (class 1259 OID 27777)
-- Name: idx_type_of_interest_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_type_of_interest_code ON public.type_of_interest USING btree (code);


--
-- TOC entry 5010 (class 1259 OID 27778)
-- Name: idx_yield_curve_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_yield_curve_code ON public.yield_curve USING btree (code);


--
-- TOC entry 5013 (class 1259 OID 27779)
-- Name: idx_yield_curve_item_ric; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_yield_curve_item_ric ON public.yield_curve_item USING btree (ric, yield_curve);


--
-- TOC entry 5068 (class 2620 OID 27780)
-- Name: financial_txn trg_financial_txn_ref_id; Type: TRIGGER; Schema: public; Owner: sofie
--

CREATE TRIGGER trg_financial_txn_ref_id BEFORE INSERT OR UPDATE ON public.financial_txn FOR EACH ROW EXECUTE FUNCTION public.fn_manage_ref_id();


--
-- TOC entry 5046 (class 2606 OID 27781)
-- Name: master_data fk_accrual_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_accrual_daycount FOREIGN KEY (accrual_daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5047 (class 2606 OID 27786)
-- Name: master_data fk_accrual_schedule_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_accrual_schedule_type FOREIGN KEY (accrual_schedule_type) REFERENCES public.accrual_schedule_type(id_accrual_schedule_type);


--
-- TOC entry 5048 (class 2606 OID 27791)
-- Name: master_data fk_amortization_schedule; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_amortization_schedule FOREIGN KEY (amortization_schedule) REFERENCES public.amortization_schedule(id_amortization_schedule);


--
-- TOC entry 5049 (class 2606 OID 27796)
-- Name: master_data fk_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- TOC entry 5036 (class 2606 OID 27801)
-- Name: gl_accounts fk_balance; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_balance FOREIGN KEY (balance) REFERENCES public.normal_balances(balance_id);


--
-- TOC entry 5032 (class 2606 OID 27806)
-- Name: forex_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5025 (class 2606 OID 27811)
-- Name: currpair_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5041 (class 2606 OID 27816)
-- Name: holiday fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5023 (class 2606 OID 27821)
-- Name: currency fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5021 (class 2606 OID 27826)
-- Name: country fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5064 (class 2606 OID 27836)
-- Name: yield_curve fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5033 (class 2606 OID 27841)
-- Name: forex_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5026 (class 2606 OID 27846)
-- Name: currpair_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5058 (class 2606 OID 27851)
-- Name: position_detail fk_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty);


--
-- TOC entry 5028 (class 2606 OID 27856)
-- Name: financial_txn fk_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty);


--
-- TOC entry 5045 (class 2606 OID 27861)
-- Name: issuer fk_country; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- TOC entry 5019 (class 2606 OID 27866)
-- Name: counterparty fk_country; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- TOC entry 5020 (class 2606 OID 27871)
-- Name: counterparty fk_ctp_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_ctp_type FOREIGN KEY (ctp_type) REFERENCES public.counterparty_type(id_counterparty_type);


--
-- TOC entry 5022 (class 2606 OID 27876)
-- Name: country fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5050 (class 2606 OID 27881)
-- Name: master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5065 (class 2606 OID 27886)
-- Name: yield_curve fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5057 (class 2606 OID 27891)
-- Name: portfolio_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5061 (class 2606 OID 27896)
-- Name: position_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5037 (class 2606 OID 27901)
-- Name: gl_accounts fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5024 (class 2606 OID 27906)
-- Name: currency fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5051 (class 2606 OID 27911)
-- Name: master_data fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5066 (class 2606 OID 27916)
-- Name: yield_curve_item fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5052 (class 2606 OID 27921)
-- Name: master_data fk_form; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_form FOREIGN KEY (form) REFERENCES public.form(id_form);


--
-- TOC entry 5053 (class 2606 OID 27926)
-- Name: master_data fk_frequency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_frequency FOREIGN KEY (frequency) REFERENCES public.frequency(id_frequency);


--
-- TOC entry 5043 (class 2606 OID 27931)
-- Name: instrument_quote_hist fk_instrument_quote; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_instrument_quote FOREIGN KEY (instrument_quote) REFERENCES public.instrument_quote(id_instrument_quote);


--
-- TOC entry 5063 (class 2606 OID 27936)
-- Name: security_master_data fk_issuer; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT fk_issuer FOREIGN KEY (issuer) REFERENCES public.issuer(id_issuer);


--
-- TOC entry 5017 (class 2606 OID 27941)
-- Name: cash_flow_item fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5018 (class 2606 OID 27946)
-- Name: cash_flow_reset fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_reset
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5027 (class 2606 OID 27951)
-- Name: deliverable_bonds fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.bond_future_master_data(id_master_data);


--
-- TOC entry 5042 (class 2606 OID 27956)
-- Name: instrument_quote fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5044 (class 2606 OID 27961)
-- Name: instrument_quote_hist fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5059 (class 2606 OID 27966)
-- Name: position_detail fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5029 (class 2606 OID 27971)
-- Name: financial_txn fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5038 (class 2606 OID 27976)
-- Name: gl_accounts fk_nature; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_nature FOREIGN KEY (nature) REFERENCES public.account_natures(nature_id);


--
-- TOC entry 5039 (class 2606 OID 27981)
-- Name: gl_accounts fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_parent FOREIGN KEY (parent) REFERENCES public.gl_accounts(account_id);


--
-- TOC entry 5062 (class 2606 OID 27986)
-- Name: position_master_data fk_portfolio; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_portfolio FOREIGN KEY (portfolio) REFERENCES public.portfolio_master_data(id_portfolio);


--
-- TOC entry 5060 (class 2606 OID 27991)
-- Name: position_detail fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- TOC entry 5030 (class 2606 OID 27996)
-- Name: financial_txn fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- TOC entry 5054 (class 2606 OID 28001)
-- Name: master_data fk_roll_convention; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_roll_convention FOREIGN KEY (roll_convention) REFERENCES public.roll_convention(id_roll_convention);


--
-- TOC entry 5034 (class 2606 OID 28006)
-- Name: future_master_data fk_settlement_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.future_master_data
    ADD CONSTRAINT fk_settlement_type FOREIGN KEY (settlement_type) REFERENCES public.settlement_type(id_settlement_type);


--
-- TOC entry 5040 (class 2606 OID 28011)
-- Name: gl_accounts fk_statement_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_statement_type FOREIGN KEY (statement_type) REFERENCES public.financial_statement_types(statement_type_id);


--
-- TOC entry 5016 (class 2606 OID 28016)
-- Name: asset_class fk_super_class; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT fk_super_class FOREIGN KEY (super_class) REFERENCES public.super_class(id_super_class);


--
-- TOC entry 5031 (class 2606 OID 28021)
-- Name: financial_txn fk_txn_status; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_txn_status FOREIGN KEY (txn_status) REFERENCES public.txn_status(id_txn_status);


--
-- TOC entry 5055 (class 2606 OID 28026)
-- Name: master_data fk_type_of_interest; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_type_of_interest FOREIGN KEY (type_of_interest) REFERENCES public.type_of_interest(id_type_of_interest);


--
-- TOC entry 5035 (class 2606 OID 28031)
-- Name: fx_future_master_data fk_underlying; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- TOC entry 5056 (class 2606 OID 28036)
-- Name: mm_future_master_data fk_underlying; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- TOC entry 5067 (class 2606 OID 28041)
-- Name: yield_curve_item fk_yield_curve; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_yield_curve FOREIGN KEY (yield_curve) REFERENCES public.yield_curve(id_yield_curve);


-- Completed on 2026-06-05 13:28:44

--
-- PostgreSQL database dump complete
--

