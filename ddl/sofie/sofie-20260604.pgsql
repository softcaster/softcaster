--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2026-06-04 15:28:57

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
-- TOC entry 295 (class 1255 OID 26148)
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

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 287 (class 1259 OID 26157)
-- Name: account_natures; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.account_natures (
    nature_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.account_natures OWNER TO sofie;

--
-- TOC entry 288 (class 1259 OID 26163)
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
-- TOC entry 227 (class 1259 OID 25643)
-- Name: accrual_schedule_type; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.accrual_schedule_type (
    id_accrual_schedule_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.accrual_schedule_type OWNER TO sofie;

--
-- TOC entry 228 (class 1259 OID 25649)
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
-- TOC entry 229 (class 1259 OID 25650)
-- Name: amortization_schedule; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.amortization_schedule (
    id_amortization_schedule integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.amortization_schedule OWNER TO sofie;

--
-- TOC entry 230 (class 1259 OID 25656)
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
-- TOC entry 243 (class 1259 OID 25740)
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
-- TOC entry 244 (class 1259 OID 25752)
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
-- TOC entry 261 (class 1259 OID 25948)
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
-- TOC entry 231 (class 1259 OID 25657)
-- Name: calendar; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.calendar (
    id_calendar integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.calendar OWNER TO sofie;

--
-- TOC entry 232 (class 1259 OID 25663)
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
-- TOC entry 250 (class 1259 OID 25865)
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
-- TOC entry 251 (class 1259 OID 25877)
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
-- TOC entry 252 (class 1259 OID 25878)
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
-- TOC entry 253 (class 1259 OID 25889)
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
-- TOC entry 293 (class 1259 OID 26217)
-- Name: compounding; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.compounding (
    id_compounding integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.compounding OWNER TO sofie;

--
-- TOC entry 294 (class 1259 OID 26223)
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
-- TOC entry 273 (class 1259 OID 26039)
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
-- TOC entry 274 (class 1259 OID 26055)
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
-- TOC entry 271 (class 1259 OID 26032)
-- Name: counterparty_type; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.counterparty_type (
    id_counterparty_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.counterparty_type OWNER TO sofie;

--
-- TOC entry 272 (class 1259 OID 26038)
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
-- TOC entry 237 (class 1259 OID 25698)
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
-- TOC entry 238 (class 1259 OID 25719)
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
-- TOC entry 235 (class 1259 OID 25676)
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
-- TOC entry 236 (class 1259 OID 25697)
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
-- TOC entry 270 (class 1259 OID 26014)
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
-- TOC entry 217 (class 1259 OID 25607)
-- Name: daycount; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.daycount (
    id_daycount integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.daycount OWNER TO sofie;

--
-- TOC entry 218 (class 1259 OID 25613)
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
-- TOC entry 262 (class 1259 OID 25953)
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
-- TOC entry 263 (class 1259 OID 25964)
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
-- TOC entry 285 (class 1259 OID 26150)
-- Name: financial_statement_types; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.financial_statement_types (
    statement_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.financial_statement_types OWNER TO sofie;

--
-- TOC entry 286 (class 1259 OID 26156)
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
-- TOC entry 283 (class 1259 OID 26115)
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
-- TOC entry 284 (class 1259 OID 26142)
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
-- TOC entry 249 (class 1259 OID 25847)
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
-- TOC entry 221 (class 1259 OID 25622)
-- Name: form; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.form (
    id_form integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.form OWNER TO sofie;

--
-- TOC entry 222 (class 1259 OID 25628)
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
-- TOC entry 219 (class 1259 OID 25614)
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
-- TOC entry 220 (class 1259 OID 25621)
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
-- TOC entry 260 (class 1259 OID 25936)
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
-- TOC entry 264 (class 1259 OID 25965)
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
-- TOC entry 291 (class 1259 OID 26171)
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
-- TOC entry 292 (class 1259 OID 26208)
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
-- TOC entry 233 (class 1259 OID 25664)
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
-- TOC entry 234 (class 1259 OID 25675)
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
-- TOC entry 266 (class 1259 OID 25985)
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
-- TOC entry 268 (class 1259 OID 25997)
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
-- TOC entry 269 (class 1259 OID 26013)
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
-- TOC entry 267 (class 1259 OID 25996)
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
-- TOC entry 239 (class 1259 OID 25720)
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
-- TOC entry 240 (class 1259 OID 25731)
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
-- TOC entry 247 (class 1259 OID 25820)
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
-- TOC entry 245 (class 1259 OID 25753)
-- Name: master_data; Type: TABLE; Schema: public; Owner: sofie
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
-- TOC entry 246 (class 1259 OID 25818)
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
-- TOC entry 265 (class 1259 OID 25975)
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
-- TOC entry 289 (class 1259 OID 26164)
-- Name: normal_balances; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.normal_balances (
    balance_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.normal_balances OWNER TO sofie;

--
-- TOC entry 290 (class 1259 OID 26170)
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
-- TOC entry 275 (class 1259 OID 26056)
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
-- TOC entry 276 (class 1259 OID 26067)
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
-- TOC entry 279 (class 1259 OID 26085)
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
-- TOC entry 280 (class 1259 OID 26107)
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
-- TOC entry 277 (class 1259 OID 26068)
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
-- TOC entry 278 (class 1259 OID 26084)
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
-- TOC entry 223 (class 1259 OID 25629)
-- Name: roll_convention; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.roll_convention (
    id_roll_convention integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.roll_convention OWNER TO sofie;

--
-- TOC entry 224 (class 1259 OID 25635)
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
-- TOC entry 248 (class 1259 OID 25834)
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
-- TOC entry 258 (class 1259 OID 25928)
-- Name: settlement_type; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.settlement_type (
    id_settlement_type integer NOT NULL,
    code character varying(25),
    description character varying(25) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.settlement_type OWNER TO sofie;

--
-- TOC entry 259 (class 1259 OID 25935)
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
-- TOC entry 241 (class 1259 OID 25732)
-- Name: super_class; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.super_class (
    id_super_class integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.super_class OWNER TO sofie;

--
-- TOC entry 242 (class 1259 OID 25739)
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
-- TOC entry 281 (class 1259 OID 26108)
-- Name: txn_status; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.txn_status (
    id_txn_status integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


ALTER TABLE public.txn_status OWNER TO sofie;

--
-- TOC entry 282 (class 1259 OID 26114)
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
-- TOC entry 225 (class 1259 OID 25636)
-- Name: type_of_interest; Type: TABLE; Schema: public; Owner: sofie
--

CREATE TABLE public.type_of_interest (
    id_type_of_interest integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


ALTER TABLE public.type_of_interest OWNER TO sofie;

--
-- TOC entry 226 (class 1259 OID 25642)
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
-- TOC entry 254 (class 1259 OID 25890)
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
-- TOC entry 256 (class 1259 OID 25910)
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
-- TOC entry 257 (class 1259 OID 25927)
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
-- TOC entry 255 (class 1259 OID 25909)
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
-- TOC entry 5284 (class 0 OID 26157)
-- Dependencies: 287
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
-- TOC entry 5224 (class 0 OID 25643)
-- Dependencies: 227
-- Data for Name: accrual_schedule_type; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.accrual_schedule_type (id_accrual_schedule_type, code, description) FROM stdin;
100	NONE	None
\.


--
-- TOC entry 5226 (class 0 OID 25650)
-- Dependencies: 229
-- Data for Name: amortization_schedule; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.amortization_schedule (id_amortization_schedule, code, description) FROM stdin;
1	SAS	Standard Amortization Schedule
2	SLP	Straight-line Principal
3	IOL	Interest Only Loan
100	NONE	None
\.


--
-- TOC entry 5240 (class 0 OID 25740)
-- Dependencies: 243
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
-- TOC entry 5258 (class 0 OID 25948)
-- Dependencies: 261
-- Data for Name: bond_future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.bond_future_master_data (id_master_data, contract_value, tick_size, initial_margin) FROM stdin;
\.


--
-- TOC entry 5228 (class 0 OID 25657)
-- Dependencies: 231
-- Data for Name: calendar; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.calendar (id_calendar, code, description) FROM stdin;
1	EUR	Euro Area Calendar
2	USD	Usd Area Calendar
\.


--
-- TOC entry 5247 (class 0 OID 25865)
-- Dependencies: 250
-- Data for Name: cash_flow_item; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.cash_flow_item (id_cash_flow_item, master_data, start_date, end_date, interest, amount, known) FROM stdin;
\.


--
-- TOC entry 5249 (class 0 OID 25878)
-- Dependencies: 252
-- Data for Name: cash_flow_reset; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.cash_flow_reset (id_cash_flow_reset, master_data, start_date_reset, interest_reset) FROM stdin;
\.


--
-- TOC entry 5290 (class 0 OID 26217)
-- Dependencies: 293
-- Data for Name: compounding; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.compounding (id_compounding, code, description) FROM stdin;
1	SIMPLE	Linear
2	COMPOUNDED	Compounded
3	CONTINUOUS	Continuous
4	SIMPLE_THEN_COMPOUNDED	Linear then Compounded
\.


--
-- TOC entry 5270 (class 0 OID 26039)
-- Dependencies: 273
-- Data for Name: counterparty; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.counterparty (id_counterparty, ctp_type, lei_code, country, code, description) FROM stdin;
\.


--
-- TOC entry 5268 (class 0 OID 26032)
-- Dependencies: 271
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
-- TOC entry 5234 (class 0 OID 25698)
-- Dependencies: 237
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
-- TOC entry 5232 (class 0 OID 25676)
-- Dependencies: 235
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
-- TOC entry 5267 (class 0 OID 26014)
-- Dependencies: 270
-- Data for Name: currpair_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.currpair_master_data (id_master_data, bcy, ccy, bcy_irc, ccy_irc) FROM stdin;
\.


--
-- TOC entry 5214 (class 0 OID 25607)
-- Dependencies: 217
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
-- TOC entry 5259 (class 0 OID 25953)
-- Dependencies: 262
-- Data for Name: deliverable_bonds; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.deliverable_bonds (id_deliverable_bonds, master_data, expiration_date, isin, coupon_rate, bond_maturity, bond_cf) FROM stdin;
\.


--
-- TOC entry 5282 (class 0 OID 26150)
-- Dependencies: 285
-- Data for Name: financial_statement_types; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.financial_statement_types (statement_type_id, code, description) FROM stdin;
1	BALANCE_SHEET	Balance Sheet
2	INCOME_STATEMENT	Income Statement
3	OFF_BALANCE_SHEET	Off Balance Sheet
\.


--
-- TOC entry 5280 (class 0 OID 26115)
-- Dependencies: 283
-- Data for Name: financial_txn; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.financial_txn (id_financial_txn, counterparty, position_md, master_data, txn_status, txn_side, description, trade_date, value_date, settlement, quantity, price, ref_id, version) FROM stdin;
\.


--
-- TOC entry 5246 (class 0 OID 25847)
-- Dependencies: 249
-- Data for Name: forex_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.forex_master_data (id_master_data, bcy, ccy, bcy_irc, ccy_irc) FROM stdin;
\.


--
-- TOC entry 5218 (class 0 OID 25622)
-- Dependencies: 221
-- Data for Name: form; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.form (id_form, code, description) FROM stdin;
1	BEARER	Bearer
2	REGISTERED	Registered
3	BOOK-ENTRY-BOND	Book-entry Bond
\.


--
-- TOC entry 5216 (class 0 OID 25614)
-- Dependencies: 219
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
-- TOC entry 5257 (class 0 OID 25936)
-- Dependencies: 260
-- Data for Name: future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.future_master_data (id_master_data, isin, exchange_contract_code, settlement_type) FROM stdin;
\.


--
-- TOC entry 5261 (class 0 OID 25965)
-- Dependencies: 264
-- Data for Name: fx_future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.fx_future_master_data (id_master_data, underlying, contract_value, tick_size, initial_margin, maintenance_margin) FROM stdin;
\.


--
-- TOC entry 5288 (class 0 OID 26171)
-- Dependencies: 291
-- Data for Name: gl_accounts; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.gl_accounts (account_id, parent, code, description, is_postable, currency, statement_type, nature, balance, created_at, updated_at) FROM stdin;
\.


--
-- TOC entry 5230 (class 0 OID 25664)
-- Dependencies: 233
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
-- TOC entry 5263 (class 0 OID 25985)
-- Dependencies: 266
-- Data for Name: instrument_quote; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.instrument_quote (id_instrument_quote, master_data, provider, code, bid, ask) FROM stdin;
\.


--
-- TOC entry 5265 (class 0 OID 25997)
-- Dependencies: 268
-- Data for Name: instrument_quote_hist; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.instrument_quote_hist (id_instrument_quote_hist, instrument_quote, master_data, code, bid, ask, update_date) FROM stdin;
\.


--
-- TOC entry 5236 (class 0 OID 25720)
-- Dependencies: 239
-- Data for Name: issuer; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.issuer (id_issuer, short_issuer_name, long_issuer_name, country) FROM stdin;
1	REP ITA	Repubblica Italiana	1
\.


--
-- TOC entry 5244 (class 0 OID 25820)
-- Dependencies: 247
-- Data for Name: loan_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.loan_master_data (id_master_data, description, processing_fees, management_fees, incidental_expenses, default_interest, late_payment_fee, underwriting_fee, insurance_premium, tax_charges) FROM stdin;
\.


--
-- TOC entry 5242 (class 0 OID 25753)
-- Dependencies: 245
-- Data for Name: master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.master_data (id_master_data, code, currency, calendar, issue_date, maturity_date, type_of_interest, form, daycount, accrual_daycount, frequency, roll_convention, accrual_schedule_type, interest_rate, issue_price, redempion_price, business_days, asset_class, amortization_schedule, multiplier, description) FROM stdin;
1	IT0005640666	1	1	2025-03-14	2026-03-13	1	1	5	3	1	100	100	0.0000000000	97.69200	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260313 - BUONI DEL TESORO ORDINARI
2	IT0005689887	1	1	2026-01-14	2027-01-14	1	1	5	3	1	100	100	0.0000000000	97.90400	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20270114 - BUONI DEL TESORO ORDINARI
3	IT0005670895	1	1	2025-09-30	2026-03-31	1	1	5	3	1	100	100	0.0000000000	98.97700	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260331 - BUONI DEL TESORO ORDINARI
4	IT0005695256	1	1	2026-02-13	2027-02-12	1	1	5	3	1	100	100	0.0000000000	97.95200	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20270212 - BUONI DEL TESORO ORDINARI
5	IT0005684888	1	1	2025-12-12	2026-12-14	1	1	5	3	1	100	100	0.0000000000	97.82500	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20261214 - BUONI DEL TESORO ORDINARI
6	IT0005678492	1	1	2025-11-14	2026-11-13	1	1	5	3	1	100	100	0.0000000000	97.95700	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20261113 - BUONI DEL TESORO ORDINARI
7	IT0005692485	1	1	2026-01-30	2026-07-31	1	1	5	3	1	100	100	0.0000000000	98.97900	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260731 - BUONI DEL TESORO ORDINARI
8	IT0005674335	1	1	2025-10-14	2026-10-14	1	1	5	3	1	100	100	0.0000000000	97.96400	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20261014 - BUONI DEL TESORO ORDINARI
9	IT0005669269	1	1	2025-09-12	2026-09-14	1	1	5	3	1	100	100	0.0000000000	97.97200	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260914 - BUONI DEL TESORO ORDINARI
10	IT0005680639	1	1	2025-11-28	2026-05-29	1	1	5	3	1	100	100	0.0000000000	98.98100	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260529 - BUONI DEL TESORO ORDINARI
11	IT0005666851	1	1	2025-08-14	2026-08-14	1	1	5	3	1	100	100	0.0000000000	98.00100	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260814 - BUONI DEL TESORO ORDINARI
12	IT0005650574	1	1	2025-05-14	2026-05-14	1	1	5	3	1	100	100	0.0000000000	98.05200	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260514 - BUONI DEL TESORO ORDINARI
13	IT0005655037	1	1	2025-06-13	2026-06-12	1	1	5	3	1	100	100	0.0000000000	98.03400	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260612 - BUONI DEL TESORO ORDINARI
14	IT0005660029	1	1	2025-07-14	2026-07-14	1	1	5	3	1	100	100	0.0000000000	98.05100	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260714 - BUONI DEL TESORO ORDINARI
15	IT0005645509	1	1	2025-04-14	2026-04-14	1	1	5	3	1	100	100	0.0000000000	97.89400	100.00000	2	2	3	0.01000	ITALIA/ZC BOT 20260414 - BUONI DEL TESORO ORDINARI
16	IT0005668238	1	1	2025-09-09	2055-10-01	1	1	5	3	2	100	100	4.6500000000	99.56000	100.00000	2	4	3	0.01000	ITALIA/4.65 BTP 20551001 - BUONI DEL TESORO POLIENNALI
17	IT0005611741	1	1	2024-09-17	2054-10-01	1	1	5	3	2	100	100	4.3000000000	99.78900	100.00000	2	4	3	0.01000	ITALIA/4.3 BTP 20541001 - BUONI DEL TESORO POLIENNALI
18	IT0005534141	1	1	2023-02-23	2053-10-01	1	1	5	3	2	100	100	4.5000000000	99.55900	100.00000	2	4	3	0.01000	ITALIA/4.5 BTP 20531001 - BUONI DEL TESORO POLIENNALI
19	IT0005217390	1	1	2016-09-01	2067-03-01	1	1	5	3	2	100	100	2.8000000000	99.19000	100.00000	2	4	3	0.01000	ITALIA/2.8 BTP 20670301 - BUONI DEL TESORO POLIENNALI
20	IT0005480980	1	1	2022-01-12	2052-09-01	1	1	5	3	2	100	100	2.1500000000	99.98700	100.00000	2	4	3	0.01000	ITALIA/2.15 BTP 20520901 - BUONI DEL TESORO POLIENNALI
21	IT0005425233	1	1	2020-09-01	2051-09-01	1	1	5	3	2	100	100	1.7000000000	98.68600	100.00000	2	4	3	0.01000	ITALIA/1.7 BTP 20510901 - BUONI DEL TESORO POLIENNALI
22	IT0005398406	1	1	2022-01-22	2050-09-01	1	1	5	3	2	100	100	2.4500000000	99.28000	100.00000	2	4	3	0.01000	ITALIA/2.45 BTP 20500901 - BUONI DEL TESORO POLIENNALI
23	IT0005363111	1	1	2019-02-13	2049-09-01	1	1	5	3	2	100	100	3.8500000000	99.59400	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20490901 - BUONI DEL TESORO POLIENNALI
24	IT0005273013	1	1	2017-03-01	2048-03-01	1	1	5	3	2	100	100	3.4500000000	98.95600	100.00000	2	4	3	0.01000	ITALIA/3.45 BTP 20480301 - BUONI DEL TESORO POLIENNALI
25	IT0005162828	1	1	2016-02-09	2047-03-01	1	1	5	3	2	100	100	2.7000000000	99.18000	100.00000	2	4	3	0.01000	ITALIA/2.7 BTP 20470301 - BUONI DEL TESORO POLIENNALI
26	IT0005441883	1	1	2021-03-01	2072-03-01	1	1	5	3	2	100	100	2.1500000000	99.46700	100.00000	2	4	3	0.01000	ITALIA/2.15 BTP 20720301 - BUONI DEL TESORO POLIENNALI
27	IT0005083057	1	1	2015-01-22	2046-09-01	1	1	5	3	2	100	100	3.2500000000	99.71000	100.00000	2	4	3	0.01000	ITALIA/3.25 BTP 20460901 - BUONI DEL TESORO POLIENNALI
28	IT0005631608	1	1	2025-01-15	2046-04-30	1	1	5	3	2	100	100	4.1000000000	99.46500	100.00000	2	4	3	0.01000	ITALIA/4.1 BTP 20460430 - BUONI DEL TESORO POLIENNALI
29	IT0005438004	1	1	2020-10-30	2045-04-30	1	1	5	3	2	100	100	1.5000000000	99.16800	100.00000	2	4	3	0.01000	ITALIA/1.5 BTP 20450430 - BUONI DEL TESORO POLIENNALI
30	IT0004923998	1	1	2013-03-01	2044-09-01	1	1	5	3	2	100	100	4.7500000000	97.22100	100.00000	2	4	3	0.01000	ITALIA/4.75 BTP 20440901 - BUONI DEL TESORO POLIENNALI
31	IT0005530032	1	1	2022-09-01	2043-09-01	1	1	5	3	2	100	100	4.4500000000	99.60600	100.00000	2	4	3	0.01000	ITALIA/4.45 BTP 20430901 - BUONI DEL TESORO POLIENNALI
32	IT0005421703	1	1	2020-09-11	2041-03-01	1	1	5	3	2	100	100	1.8000000000	99.76500	100.00000	2	4	3	0.01000	ITALIA/1.8 BTP 20410301 - BUONI DEL TESORO POLIENNALI
33	IT0005635583	1	1	2025-02-18	2040-10-01	1	1	5	3	2	100	100	3.8500000000	99.37500	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20401001 - BUONI DEL TESORO POLIENNALI
34	IT0004532559	1	1	2009-09-01	2040-09-01	1	1	5	3	2	100	100	5.0000000000	98.18600	100.00000	2	4	3	0.01000	ITALIA/5 BTP 20400901 - BUONI DEL TESORO POLIENNALI
35	IT0005377152	1	1	2019-06-19	2040-03-01	1	1	5	3	2	100	100	3.1000000000	99.62300	100.00000	2	4	3	0.01000	ITALIA/3.1 BTP 20400301 - BUONI DEL TESORO POLIENNALI
36	IT0005582421	1	1	2023-10-01	2039-10-01	1	1	5	3	2	100	100	4.1500000000	99.68000	100.00000	2	4	3	0.01000	ITALIA/4.15 BTP 20391001 - BUONI DEL TESORO POLIENNALI
37	IT0005442097	1	1	2021-04-27	2037-04-27	1	1	5	3	2	100	100	0.0000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/TV BTP FUTURA 20370427 EX - BTP FUTURA
38	IT0004286966	1	1	2007-08-01	2039-08-01	1	1	5	3	2	100	100	5.0000000000	99.98000	100.00000	2	4	3	0.01000	ITALIA/5 BTP 20390801 - BUONI DEL TESORO POLIENNALI
39	IT0005321325	1	1	2017-09-01	2038-09-01	1	1	5	3	2	100	100	2.9500000000	99.76600	100.00000	2	4	3	0.01000	ITALIA/2.95 BTP 20380901 - BUONI DEL TESORO POLIENNALI
40	IT0005496770	1	1	2022-03-01	2038-03-01	1	1	5	3	2	100	100	3.2500000000	99.65100	100.00000	2	4	3	0.01000	ITALIA/3.25 BTP 20380301 - BUONI DEL TESORO POLIENNALI
41	IT0005596470	1	1	2024-04-30	2037-10-30	1	1	5	3	2	100	100	4.0500000000	99.86500	100.00000	2	4	3	0.01000	ITALIA/4.05 BTP 20371030 - BUONI DEL TESORO POLIENNALI
42	IT0005433195	1	1	2021-01-12	2037-03-01	1	1	5	3	2	100	100	0.9500000000	99.40900	100.00000	2	4	3	0.01000	ITALIA/0.95 BTP 20370301 - BUONI DEL TESORO POLIENNALI
43	IT0003934657	1	1	2005-08-01	2037-02-01	1	1	5	3	2	100	100	4.0000000000	101.28900	100.00000	2	4	3	0.01000	ITALIA/4 BTP 20370201 - BUONI DEL TESORO POLIENNALI
44	IT0005177909	1	1	2016-03-01	2036-09-01	1	1	5	3	2	100	100	2.2500000000	99.36800	100.00000	2	4	3	0.01000	ITALIA/2.25 BTP 20360901 - BUONI DEL TESORO POLIENNALI
45	IT0005402117	1	1	2020-02-18	2036-03-01	1	1	5	3	2	100	100	1.4500000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/1.45 BTP 20360301 - BUONI DEL TESORO POLIENNALI
46	IT0005676504	1	1	2025-11-03	2036-02-01	1	1	5	3	2	100	100	0.0000000000	100.19000	100.00000	2	4	3	0.01000	ITALIA/3.45 BTP 20360201 - BUONI DEL TESORO POLIENNALI
47	IT0005648149	1	1	2025-05-02	2035-10-01	1	1	5	3	2	100	100	3.6000000000	100.09000	100.00000	2	4	3	0.01000	ITALIA/3.6 BTP 20351001 - BUONI DEL TESORO POLIENNALI
48	IT0005631590	1	1	2025-01-15	2035-08-01	1	1	5	3	2	100	100	3.6500000000	99.57700	100.00000	2	4	3	0.01000	ITALIA/3.65 BTP 20350801 - BUONI DEL TESORO POLIENNALI
49	IT0005607970	1	1	2024-08-01	2035-02-01	1	1	5	3	2	100	100	3.8500000000	101.04000	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20350201 - BUONI DEL TESORO POLIENNALI
50	IT0005508590	1	1	2022-09-13	2035-04-30	1	1	5	3	2	100	100	4.0000000000	99.73400	100.00000	2	4	3	0.01000	ITALIA/4 BTP 20350430 - BUONI DEL TESORO POLIENNALI
51	IT0005358806	1	1	2019-01-22	2035-03-01	1	1	5	3	2	100	100	3.3500000000	99.60900	100.00000	2	4	3	0.01000	ITALIA/3.35 BTP 20350301 - BUONI DEL TESORO POLIENNALI
52	IT0005466351	1	1	2021-11-16	2033-11-16	1	1	5	3	2	100	100	0.0000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/TV BTP FUTURA 20331116 EX - BTP FUTURA
53	IT0005584856	1	1	2024-03-01	2034-07-01	1	1	5	3	2	100	100	3.8500000000	99.80000	100.00000	2	4	3	0.01000	ITALIA/3.85 BTP 20340701 - BUONI DEL TESORO POLIENNALI
54	IT0003535157	1	1	2003-08-01	2034-08-01	1	1	5	3	2	100	100	5.0000000000	98.10600	100.00000	2	4	3	0.01000	ITALIA/5 BTP 20340801 - BUONI DEL TESORO POLIENNALI
55	IT0005634800	1	1	2025-02-25	2033-02-25	1	1	5	3	2	100	100	0.0000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/TV BTP PIU 20330225 - BTP PIU
56	IT0005560948	1	1	2023-09-01	2034-03-01	1	1	5	3	2	100	100	4.2000000000	100.00000	100.00000	2	4	3	0.01000	ITALIA/4.2 BTP 20340301 - BUONI DEL TESORO POLIENNALI
57	IT0005544082	1	1	2023-05-02	2033-11-01	1	1	5	3	2	100	100	4.3500000000	99.85000	100.00000	2	4	3	0.01000	ITALIA/4.35 BTP 20331101 - BUONI DEL TESORO POLIENNALI
58	IT0005240350	1	1	2017-01-25	2033-09-01	1	1	5	3	2	100	100	2.4500000000	99.13000	100.00000	2	4	3	0.01000	ITALIA/2.45 BTP 20330901 - BUONI DEL TESORO POLIENNALI
\.


--
-- TOC entry 5262 (class 0 OID 25975)
-- Dependencies: 265
-- Data for Name: mm_future_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.mm_future_master_data (id_master_data, underlying, contract_value, tick_size, initial_margin, maintenance_margin) FROM stdin;
\.


--
-- TOC entry 5286 (class 0 OID 26164)
-- Dependencies: 289
-- Data for Name: normal_balances; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.normal_balances (balance_id, code, description) FROM stdin;
1	DEBIT	Dr
2	CREDIT	Cr
\.


--
-- TOC entry 5272 (class 0 OID 26056)
-- Dependencies: 275
-- Data for Name: portfolio_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.portfolio_master_data (id_portfolio, currency, code, description) FROM stdin;
\.


--
-- TOC entry 5276 (class 0 OID 26085)
-- Dependencies: 279
-- Data for Name: position_detail; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.position_detail (id_position_detail, position_md, master_data, counterparty, realized_pnl, unrealized_pnl, buy_qty, notional_value_buy, buy_fees, buy_taxes, sell_qty, notional_value_sell, sell_fees, sell_taxes, multiplier, market_price) FROM stdin;
\.


--
-- TOC entry 5274 (class 0 OID 26068)
-- Dependencies: 277
-- Data for Name: position_master_data; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.position_master_data (id_position, portfolio, currency, code, description) FROM stdin;
\.


--
-- TOC entry 5220 (class 0 OID 25629)
-- Dependencies: 223
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
-- TOC entry 5245 (class 0 OID 25834)
-- Dependencies: 248
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
-- TOC entry 5255 (class 0 OID 25928)
-- Dependencies: 258
-- Data for Name: settlement_type; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.settlement_type (id_settlement_type, code, description) FROM stdin;
1	PHYSICAL	Physical Settlement
2	CASH	Cash Settlement
\.


--
-- TOC entry 5238 (class 0 OID 25732)
-- Dependencies: 241
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
-- TOC entry 5278 (class 0 OID 26108)
-- Dependencies: 281
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
-- TOC entry 5222 (class 0 OID 25636)
-- Dependencies: 225
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
-- TOC entry 5251 (class 0 OID 25890)
-- Dependencies: 254
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
-- TOC entry 5253 (class 0 OID 25910)
-- Dependencies: 256
-- Data for Name: yield_curve_item; Type: TABLE DATA; Schema: public; Owner: sofie
--

COPY public.yield_curve_item (id_yield_curve_item, yield_curve, ric, offset_type, offset_value, bid, ask, compounding, daycount) FROM stdin;
1	6		1	0	0.00000	0.00000	2	3
2	6	EUR 01A Irs	3	1	0.02750	0.02750	2	3
3	6	EUR 02A Irs	3	2	0.02810	0.02810	2	3
4	6	EUR 03A Irs	3	3	0.02790	0.02790	2	3
5	6	EUR 04A Irs	3	4	0.02810	0.02810	2	3
6	6	EUR 05A Irs	3	5	0.02830	0.02830	2	3
7	6	EUR 06A Irs	3	6	0.02870	0.02870	2	3
8	6	EUR 07A Irs	3	7	0.02900	0.02900	2	3
9	6	EUR 08A Irs	3	8	0.02940	0.02940	2	3
10	6	EUR 09A Irs	3	9	0.02990	0.02990	2	3
11	6	EUR 10A Irs	3	10	0.03030	0.03030	2	3
12	6	EUR 11A Irs	3	11	0.03060	0.03060	2	3
13	6	EUR 12A Irs	3	12	0.03100	0.03100	2	3
14	6	EUR 15A Irs	3	15	0.03190	0.03190	2	3
15	6	EUR 20A Irs	3	20	0.03240	0.03240	2	3
16	6	EUR 25A Irs	3	25	0.03210	0.03210	2	3
17	6	EUR 30A Irs	3	30	0.03170	0.03170	2	3
18	6	EUR 40A Irs	3	40	0.03050	0.03050	2	3
19	6	EUR 50A Irs	3	50	0.02920	0.02920	2	3
20	3		1	0	0.00000	0.00000	2	3
21	3	Italy 1M	2	1	0.02174	0.02174	1	3
22	3	Italy 3M	2	3	0.02280	0.02280	1	3
23	3	Italy 6M	2	6	0.02388	0.02388	1	3
24	3	Italy 9M	2	9	0.02576	0.02576	1	3
25	3	Italy 1Y	3	1	0.02626	0.02626	2	3
26	3	Italy 2Y	3	2	0.02830	0.02830	2	3
27	3	Italy 3Y	3	3	0.02931	0.02931	2	3
28	3	Italy 4Y	3	4	0.03064	0.03064	2	3
29	3	Italy 5Y	3	5	0.03150	0.03150	2	3
30	3	Italy 6Y	3	6	0.03332	0.03332	2	3
31	3	Italy 7Y	3	7	0.03422	0.03422	2	3
32	3	Italy 8Y	3	8	0.03572	0.03572	2	3
33	3	Italy 9Y	3	9	0.03718	0.03718	2	3
34	3	Italy 10Y	3	10	0.03753	0.03753	2	3
35	3	Italy 15Y	3	15	0.04203	0.04203	2	3
36	3	Italy 20Y	3	20	0.04383	0.04383	2	3
37	3	Italy 25Y	3	25	0.04480	0.04480	2	3
38	3	Italy 30Y	3	30	0.04595	0.04595	2	3
39	3	Italy 50Y	3	50	0.04235	0.04235	2	3
40	1		1	0	0.00000	0.00000	2	3
41	1	Ovn	1	1	0.01932	0.01932	1	2
42	1	1M	2	1	0.02081	0.02081	1	2
43	1	3M	2	3	0.02180	0.02180	1	2
44	1	6M	2	6	0.02311	0.02311	1	2
45	1	1Y	3	1	0.02470	0.02470	1	2
46	2		1	0	0.00000	0.00000	2	3
47	2	Ovn	1	1	0.03630	0.03630	1	2
48	2	1M	2	1	0.03613	0.03613	1	2
49	2	3M	2	3	0.03652	0.03652	1	2
50	2	6M	2	6	0.03711	0.03711	1	2
51	2	1Y	3	1	0.03849	0.03849	1	2
52	4		1	0	0.00000	0.00000	2	3
53	4	U.S. 1M	2	1	0.03685	0.03685	1	3
54	4	U.S. 2M	2	2	0.03696	0.03696	1	3
55	4	U.S. 3M	2	3	0.03709	0.03709	1	3
56	4	U.S. 4M	2	4	0.03730	0.03730	1	3
57	4	U.S. 6M	2	6	0.03770	0.03770	1	3
58	4	U.S. 1Y	3	1	0.03819	0.03819	2	3
59	4	U.S. 2Y	3	2	0.04082	0.04082	2	3
60	4	U.S. 3Y	3	3	0.04132	0.04132	2	3
61	4	U.S. 5Y	3	5	0.04214	0.04214	2	3
62	4	U.S. 7Y	3	7	0.04348	0.04348	2	3
63	4	U.S. 10Y	3	10	0.04492	0.04492	2	3
64	4	U.S. 20Y	3	20	0.04996	0.04996	2	3
65	4	U.S. 30Y	3	30	0.04992	0.04992	2	3
\.


--
-- TOC entry 5297 (class 0 OID 0)
-- Dependencies: 288
-- Name: account_natures_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.account_natures_s', 6, true);


--
-- TOC entry 5298 (class 0 OID 0)
-- Dependencies: 228
-- Name: accrual_schedule_type_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.accrual_schedule_type_s', 1, false);


--
-- TOC entry 5299 (class 0 OID 0)
-- Dependencies: 230
-- Name: amortization_schedule_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.amortization_schedule_s', 3, true);


--
-- TOC entry 5300 (class 0 OID 0)
-- Dependencies: 244
-- Name: asset_class_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.asset_class_s', 12, true);


--
-- TOC entry 5301 (class 0 OID 0)
-- Dependencies: 232
-- Name: calendar_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.calendar_s', 2, true);


--
-- TOC entry 5302 (class 0 OID 0)
-- Dependencies: 251
-- Name: cash_flow_item_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.cash_flow_item_s', 1, false);


--
-- TOC entry 5303 (class 0 OID 0)
-- Dependencies: 253
-- Name: cash_flow_reset_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.cash_flow_reset_s', 1, false);


--
-- TOC entry 5304 (class 0 OID 0)
-- Dependencies: 294
-- Name: compounding_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.compounding_s', 4, true);


--
-- TOC entry 5305 (class 0 OID 0)
-- Dependencies: 274
-- Name: counterparty_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.counterparty_s', 1, false);


--
-- TOC entry 5306 (class 0 OID 0)
-- Dependencies: 272
-- Name: counterparty_type_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.counterparty_type_s', 6, true);


--
-- TOC entry 5307 (class 0 OID 0)
-- Dependencies: 238
-- Name: country_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.country_s', 7, true);


--
-- TOC entry 5308 (class 0 OID 0)
-- Dependencies: 236
-- Name: currency_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.currency_s', 7, true);


--
-- TOC entry 5309 (class 0 OID 0)
-- Dependencies: 218
-- Name: daycount_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.daycount_s', 6, true);


--
-- TOC entry 5310 (class 0 OID 0)
-- Dependencies: 263
-- Name: deliverable_bonds_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.deliverable_bonds_s', 1, false);


--
-- TOC entry 5311 (class 0 OID 0)
-- Dependencies: 286
-- Name: financial_statement_types_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.financial_statement_types_s', 3, true);


--
-- TOC entry 5312 (class 0 OID 0)
-- Dependencies: 284
-- Name: financial_txn_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.financial_txn_s', 1, false);


--
-- TOC entry 5313 (class 0 OID 0)
-- Dependencies: 222
-- Name: form_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.form_s', 3, true);


--
-- TOC entry 5314 (class 0 OID 0)
-- Dependencies: 220
-- Name: frequency_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.frequency_s', 6, true);


--
-- TOC entry 5315 (class 0 OID 0)
-- Dependencies: 292
-- Name: gl_accounts_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.gl_accounts_s', 1, false);


--
-- TOC entry 5316 (class 0 OID 0)
-- Dependencies: 234
-- Name: holiday_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.holiday_s', 21, true);


--
-- TOC entry 5317 (class 0 OID 0)
-- Dependencies: 269
-- Name: instrument_quote_hist_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.instrument_quote_hist_s', 1, false);


--
-- TOC entry 5318 (class 0 OID 0)
-- Dependencies: 267
-- Name: instrument_quote_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.instrument_quote_s', 1, false);


--
-- TOC entry 5319 (class 0 OID 0)
-- Dependencies: 240
-- Name: issuer_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.issuer_s', 1, true);


--
-- TOC entry 5320 (class 0 OID 0)
-- Dependencies: 246
-- Name: master_data_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.master_data_s', 58, true);


--
-- TOC entry 5321 (class 0 OID 0)
-- Dependencies: 290
-- Name: normal_balances_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.normal_balances_s', 2, true);


--
-- TOC entry 5322 (class 0 OID 0)
-- Dependencies: 276
-- Name: portfolio_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.portfolio_master_data_s', 1, false);


--
-- TOC entry 5323 (class 0 OID 0)
-- Dependencies: 280
-- Name: position_detail_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.position_detail_s', 1, false);


--
-- TOC entry 5324 (class 0 OID 0)
-- Dependencies: 278
-- Name: position_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.position_master_data_s', 1, false);


--
-- TOC entry 5325 (class 0 OID 0)
-- Dependencies: 224
-- Name: roll_convention_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.roll_convention_s', 4, true);


--
-- TOC entry 5326 (class 0 OID 0)
-- Dependencies: 259
-- Name: settlement_type_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.settlement_type_s', 2, true);


--
-- TOC entry 5327 (class 0 OID 0)
-- Dependencies: 242
-- Name: super_class_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.super_class_s', 5, true);


--
-- TOC entry 5328 (class 0 OID 0)
-- Dependencies: 282
-- Name: txn_status_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.txn_status_s', 9, true);


--
-- TOC entry 5329 (class 0 OID 0)
-- Dependencies: 226
-- Name: type_of_interest_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.type_of_interest_s', 6, true);


--
-- TOC entry 5330 (class 0 OID 0)
-- Dependencies: 257
-- Name: yield_curve_item_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.yield_curve_item_s', 65, true);


--
-- TOC entry 5331 (class 0 OID 0)
-- Dependencies: 255
-- Name: yield_curve_s; Type: SEQUENCE SET; Schema: public; Owner: sofie
--

SELECT pg_catalog.setval('public.yield_curve_s', 7, true);


--
-- TOC entry 5004 (class 2606 OID 26162)
-- Name: account_natures account_natures_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.account_natures
    ADD CONSTRAINT account_natures_pkey PRIMARY KEY (nature_id);


--
-- TOC entry 4907 (class 2606 OID 25647)
-- Name: accrual_schedule_type accrual_schedule_type_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.accrual_schedule_type
    ADD CONSTRAINT accrual_schedule_type_pkey PRIMARY KEY (id_accrual_schedule_type);


--
-- TOC entry 4910 (class 2606 OID 25654)
-- Name: amortization_schedule amortization_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.amortization_schedule
    ADD CONSTRAINT amortization_schedule_pkey PRIMARY KEY (id_amortization_schedule);


--
-- TOC entry 4932 (class 2606 OID 25745)
-- Name: asset_class asset_class_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT asset_class_pkey PRIMARY KEY (id_asset_class);


--
-- TOC entry 4964 (class 2606 OID 25952)
-- Name: bond_future_master_data bond_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.bond_future_master_data
    ADD CONSTRAINT bond_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4913 (class 2606 OID 25661)
-- Name: calendar calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (id_calendar);


--
-- TOC entry 4946 (class 2606 OID 25870)
-- Name: cash_flow_item cash_flow_item_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT cash_flow_item_pkey PRIMARY KEY (id_cash_flow_item);


--
-- TOC entry 4949 (class 2606 OID 25882)
-- Name: cash_flow_reset cash_flow_reset_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_reset
    ADD CONSTRAINT cash_flow_reset_pkey PRIMARY KEY (id_cash_flow_reset);


--
-- TOC entry 5013 (class 2606 OID 26221)
-- Name: compounding compounding_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.compounding
    ADD CONSTRAINT compounding_pkey PRIMARY KEY (id_compounding);


--
-- TOC entry 4985 (class 2606 OID 26043)
-- Name: counterparty counterparty_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT counterparty_pkey PRIMARY KEY (id_counterparty);


--
-- TOC entry 4982 (class 2606 OID 26036)
-- Name: counterparty_type counterparty_type_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty_type
    ADD CONSTRAINT counterparty_type_pkey PRIMARY KEY (id_counterparty_type);


--
-- TOC entry 4924 (class 2606 OID 25705)
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id_country);


--
-- TOC entry 4918 (class 2606 OID 25684)
-- Name: currency currency_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT currency_pkey PRIMARY KEY (id_currency);


--
-- TOC entry 4979 (class 2606 OID 26020)
-- Name: currpair_master_data currpair_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT currpair_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4892 (class 2606 OID 25611)
-- Name: daycount daycount_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.daycount
    ADD CONSTRAINT daycount_pkey PRIMARY KEY (id_daycount);


--
-- TOC entry 4966 (class 2606 OID 25957)
-- Name: deliverable_bonds deliverable_bonds_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT deliverable_bonds_pkey PRIMARY KEY (id_deliverable_bonds);


--
-- TOC entry 5002 (class 2606 OID 26155)
-- Name: financial_statement_types financial_statement_types_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_statement_types
    ADD CONSTRAINT financial_statement_types_pkey PRIMARY KEY (statement_type_id);


--
-- TOC entry 5000 (class 2606 OID 26121)
-- Name: financial_txn financial_txn_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT financial_txn_pkey PRIMARY KEY (id_financial_txn);


--
-- TOC entry 4943 (class 2606 OID 25853)
-- Name: forex_master_data forex_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT forex_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4898 (class 2606 OID 25626)
-- Name: form form_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.form
    ADD CONSTRAINT form_pkey PRIMARY KEY (id_form);


--
-- TOC entry 4895 (class 2606 OID 25619)
-- Name: frequency frequency_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.frequency
    ADD CONSTRAINT frequency_pkey PRIMARY KEY (id_frequency);


--
-- TOC entry 4961 (class 2606 OID 25941)
-- Name: future_master_data future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.future_master_data
    ADD CONSTRAINT future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4969 (class 2606 OID 25969)
-- Name: fx_future_master_data fx_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fx_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 5008 (class 2606 OID 26181)
-- Name: gl_accounts gl_accounts_code_key; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT gl_accounts_code_key UNIQUE (code);


--
-- TOC entry 5010 (class 2606 OID 26179)
-- Name: gl_accounts gl_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT gl_accounts_pkey PRIMARY KEY (account_id);


--
-- TOC entry 4916 (class 2606 OID 25669)
-- Name: holiday holiday_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT holiday_pkey PRIMARY KEY (id_holiday);


--
-- TOC entry 4977 (class 2606 OID 26001)
-- Name: instrument_quote_hist instrument_quote_hist_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT instrument_quote_hist_pkey PRIMARY KEY (id_instrument_quote_hist);


--
-- TOC entry 4974 (class 2606 OID 25989)
-- Name: instrument_quote instrument_quote_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT instrument_quote_pkey PRIMARY KEY (id_instrument_quote);


--
-- TOC entry 4927 (class 2606 OID 25725)
-- Name: issuer issuer_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT issuer_pkey PRIMARY KEY (id_issuer);


--
-- TOC entry 4938 (class 2606 OID 25833)
-- Name: loan_master_data loan_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.loan_master_data
    ADD CONSTRAINT loan_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4936 (class 2606 OID 25761)
-- Name: master_data master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4971 (class 2606 OID 25979)
-- Name: mm_future_master_data mm_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT mm_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 5006 (class 2606 OID 26169)
-- Name: normal_balances normal_balances_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.normal_balances
    ADD CONSTRAINT normal_balances_pkey PRIMARY KEY (balance_id);


--
-- TOC entry 4989 (class 2606 OID 26060)
-- Name: portfolio_master_data portfolio_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT portfolio_master_data_pkey PRIMARY KEY (id_portfolio);


--
-- TOC entry 4995 (class 2606 OID 26090)
-- Name: position_detail position_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT position_detail_pkey PRIMARY KEY (id_position_detail);


--
-- TOC entry 4992 (class 2606 OID 26072)
-- Name: position_master_data position_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT position_master_data_pkey PRIMARY KEY (id_position);


--
-- TOC entry 4902 (class 2606 OID 25633)
-- Name: roll_convention roll_convention_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.roll_convention
    ADD CONSTRAINT roll_convention_pkey PRIMARY KEY (id_roll_convention);


--
-- TOC entry 4941 (class 2606 OID 25840)
-- Name: security_master_data security_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT security_master_data_pkey PRIMARY KEY (id_master_data);


--
-- TOC entry 4959 (class 2606 OID 25933)
-- Name: settlement_type settlement_type_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.settlement_type
    ADD CONSTRAINT settlement_type_pkey PRIMARY KEY (id_settlement_type);


--
-- TOC entry 4930 (class 2606 OID 25737)
-- Name: super_class super_class_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.super_class
    ADD CONSTRAINT super_class_pkey PRIMARY KEY (id_super_class);


--
-- TOC entry 4998 (class 2606 OID 26112)
-- Name: txn_status txn_status_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.txn_status
    ADD CONSTRAINT txn_status_pkey PRIMARY KEY (id_txn_status);


--
-- TOC entry 4905 (class 2606 OID 25640)
-- Name: type_of_interest type_of_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.type_of_interest
    ADD CONSTRAINT type_of_interest_pkey PRIMARY KEY (id_type_of_interest);


--
-- TOC entry 4956 (class 2606 OID 25915)
-- Name: yield_curve_item yield_curve_item_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT yield_curve_item_pkey PRIMARY KEY (id_yield_curve_item);


--
-- TOC entry 4953 (class 2606 OID 25897)
-- Name: yield_curve yield_curve_pkey; Type: CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT yield_curve_pkey PRIMARY KEY (id_yield_curve);


--
-- TOC entry 4921 (class 1259 OID 25717)
-- Name: alfa_2_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX alfa_2_code ON public.country USING btree (alfa_2_code);


--
-- TOC entry 4922 (class 1259 OID 25718)
-- Name: alfa_3_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX alfa_3_code ON public.country USING btree (alfa_3_code);


--
-- TOC entry 5011 (class 1259 OID 26207)
-- Name: idx_account_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_account_code ON public.gl_accounts USING btree (code);


--
-- TOC entry 4908 (class 1259 OID 25648)
-- Name: idx_accrual_schedule_type_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_accrual_schedule_type_code ON public.accrual_schedule_type USING btree (code);


--
-- TOC entry 4911 (class 1259 OID 25655)
-- Name: idx_amortization_schedule_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_amortization_schedule_code ON public.amortization_schedule USING btree (code);


--
-- TOC entry 4933 (class 1259 OID 25751)
-- Name: idx_asset_class_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_asset_class_code ON public.asset_class USING btree (code);


--
-- TOC entry 4914 (class 1259 OID 25662)
-- Name: idx_calendar_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_calendar_code ON public.calendar USING btree (code);


--
-- TOC entry 5014 (class 1259 OID 26222)
-- Name: idx_compounding_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_compounding_code ON public.compounding USING btree (code);


--
-- TOC entry 4986 (class 1259 OID 26054)
-- Name: idx_counterparty_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_counterparty_code ON public.counterparty USING btree (code);


--
-- TOC entry 4983 (class 1259 OID 26037)
-- Name: idx_counterparty_type_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_counterparty_type_code ON public.counterparty_type USING btree (code);


--
-- TOC entry 4925 (class 1259 OID 25716)
-- Name: idx_country_numeric_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_country_numeric_code ON public.country USING btree (country_numeric_code);


--
-- TOC entry 4919 (class 1259 OID 25696)
-- Name: idx_currency_numeric_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_currency_numeric_code ON public.currency USING btree (currency_numeric_code);


--
-- TOC entry 4980 (class 1259 OID 26031)
-- Name: idx_currpair_bcy_ccy; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_currpair_bcy_ccy ON public.currpair_master_data USING btree (bcy, ccy);


--
-- TOC entry 4893 (class 1259 OID 25612)
-- Name: idx_daycount_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_daycount_code ON public.daycount USING btree (code);


--
-- TOC entry 4967 (class 1259 OID 25963)
-- Name: idx_deliverable_bonds_isin; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_deliverable_bonds_isin ON public.deliverable_bonds USING btree (master_data, isin);


--
-- TOC entry 4944 (class 1259 OID 25864)
-- Name: idx_forex_bcy_ccy; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_forex_bcy_ccy ON public.forex_master_data USING btree (bcy, ccy);


--
-- TOC entry 4899 (class 1259 OID 25627)
-- Name: idx_form_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_form_code ON public.form USING btree (code);


--
-- TOC entry 4896 (class 1259 OID 25620)
-- Name: idx_frequency_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_frequency_code ON public.frequency USING btree (code);


--
-- TOC entry 4962 (class 1259 OID 25947)
-- Name: idx_future_master_data_isin; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_future_master_data_isin ON public.future_master_data USING btree (isin);


--
-- TOC entry 4972 (class 1259 OID 25995)
-- Name: idx_instrument_quote_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_instrument_quote_code ON public.instrument_quote USING btree (code);


--
-- TOC entry 4975 (class 1259 OID 26012)
-- Name: idx_instrument_quote_hist_iqud; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_instrument_quote_hist_iqud ON public.instrument_quote_hist USING btree (instrument_quote, update_date);


--
-- TOC entry 4920 (class 1259 OID 25695)
-- Name: idx_iso_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_iso_code ON public.currency USING btree (iso_code);


--
-- TOC entry 4934 (class 1259 OID 25817)
-- Name: idx_master_data_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_master_data_code ON public.master_data USING btree (code);


--
-- TOC entry 4947 (class 1259 OID 25876)
-- Name: idx_md_ed; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_md_ed ON public.cash_flow_item USING btree (master_data, end_date);


--
-- TOC entry 4950 (class 1259 OID 25888)
-- Name: idx_md_sdr; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_md_sdr ON public.cash_flow_reset USING btree (master_data, start_date_reset);


--
-- TOC entry 4987 (class 1259 OID 26066)
-- Name: idx_portfolio_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_portfolio_code ON public.portfolio_master_data USING btree (code);


--
-- TOC entry 4990 (class 1259 OID 26083)
-- Name: idx_position_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_position_code ON public.position_master_data USING btree (code);


--
-- TOC entry 4993 (class 1259 OID 26106)
-- Name: idx_position_detail_pmc; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_position_detail_pmc ON public.position_detail USING btree (position_md, master_data, counterparty);


--
-- TOC entry 4900 (class 1259 OID 25634)
-- Name: idx_roll_convention_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_roll_convention_code ON public.roll_convention USING btree (code);


--
-- TOC entry 4939 (class 1259 OID 25846)
-- Name: idx_security_master_data_isin; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_security_master_data_isin ON public.security_master_data USING btree (isin);


--
-- TOC entry 4957 (class 1259 OID 25934)
-- Name: idx_settlement_type_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_settlement_type_code ON public.settlement_type USING btree (code);


--
-- TOC entry 4928 (class 1259 OID 25738)
-- Name: idx_super_class_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_super_class_code ON public.super_class USING btree (code);


--
-- TOC entry 4996 (class 1259 OID 26113)
-- Name: idx_txn_status_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_txn_status_code ON public.txn_status USING btree (code);


--
-- TOC entry 4903 (class 1259 OID 25641)
-- Name: idx_type_of_interest_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_type_of_interest_code ON public.type_of_interest USING btree (code);


--
-- TOC entry 4951 (class 1259 OID 25908)
-- Name: idx_yield_curve_code; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_yield_curve_code ON public.yield_curve USING btree (code);


--
-- TOC entry 4954 (class 1259 OID 25926)
-- Name: idx_yield_curve_item_ric; Type: INDEX; Schema: public; Owner: sofie
--

CREATE UNIQUE INDEX idx_yield_curve_item_ric ON public.yield_curve_item USING btree (ric, yield_curve);


--
-- TOC entry 5068 (class 2620 OID 26149)
-- Name: financial_txn trg_financial_txn_ref_id; Type: TRIGGER; Schema: public; Owner: sofie
--

CREATE TRIGGER trg_financial_txn_ref_id BEFORE INSERT OR UPDATE ON public.financial_txn FOR EACH ROW EXECUTE FUNCTION public.fn_manage_ref_id();


--
-- TOC entry 5022 (class 2606 OID 25767)
-- Name: master_data fk_accrual_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_accrual_daycount FOREIGN KEY (accrual_daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5023 (class 2606 OID 25802)
-- Name: master_data fk_accrual_schedule_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_accrual_schedule_type FOREIGN KEY (accrual_schedule_type) REFERENCES public.accrual_schedule_type(id_accrual_schedule_type);


--
-- TOC entry 5024 (class 2606 OID 25812)
-- Name: master_data fk_amortization_schedule; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_amortization_schedule FOREIGN KEY (amortization_schedule) REFERENCES public.amortization_schedule(id_amortization_schedule);


--
-- TOC entry 5025 (class 2606 OID 25807)
-- Name: master_data fk_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- TOC entry 5063 (class 2606 OID 26197)
-- Name: gl_accounts fk_balance; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_balance FOREIGN KEY (balance) REFERENCES public.normal_balances(balance_id);


--
-- TOC entry 5034 (class 2606 OID 25854)
-- Name: forex_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5049 (class 2606 OID 26021)
-- Name: currpair_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5015 (class 2606 OID 25670)
-- Name: holiday fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5016 (class 2606 OID 25685)
-- Name: currency fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5018 (class 2606 OID 25711)
-- Name: country fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5026 (class 2606 OID 25782)
-- Name: master_data fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5038 (class 2606 OID 25898)
-- Name: yield_curve fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- TOC entry 5035 (class 2606 OID 25859)
-- Name: forex_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5050 (class 2606 OID 26026)
-- Name: currpair_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- TOC entry 5056 (class 2606 OID 26096)
-- Name: position_detail fk_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty);


--
-- TOC entry 5059 (class 2606 OID 26122)
-- Name: financial_txn fk_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty);


--
-- TOC entry 5020 (class 2606 OID 25726)
-- Name: issuer fk_country; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- TOC entry 5051 (class 2606 OID 26049)
-- Name: counterparty fk_country; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- TOC entry 5052 (class 2606 OID 26044)
-- Name: counterparty fk_ctp_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_ctp_type FOREIGN KEY (ctp_type) REFERENCES public.counterparty_type(id_counterparty_type);


--
-- TOC entry 5019 (class 2606 OID 25706)
-- Name: country fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5027 (class 2606 OID 25787)
-- Name: master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5039 (class 2606 OID 25903)
-- Name: yield_curve fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5053 (class 2606 OID 26061)
-- Name: portfolio_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5054 (class 2606 OID 26073)
-- Name: position_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5064 (class 2606 OID 26202)
-- Name: gl_accounts fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- TOC entry 5017 (class 2606 OID 25690)
-- Name: currency fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5028 (class 2606 OID 25762)
-- Name: master_data fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5040 (class 2606 OID 25921)
-- Name: yield_curve_item fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- TOC entry 5029 (class 2606 OID 25777)
-- Name: master_data fk_form; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_form FOREIGN KEY (form) REFERENCES public.form(id_form);


--
-- TOC entry 5030 (class 2606 OID 25772)
-- Name: master_data fk_frequency; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_frequency FOREIGN KEY (frequency) REFERENCES public.frequency(id_frequency);


--
-- TOC entry 5047 (class 2606 OID 26007)
-- Name: instrument_quote_hist fk_instrument_quote; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_instrument_quote FOREIGN KEY (instrument_quote) REFERENCES public.instrument_quote(id_instrument_quote);


--
-- TOC entry 5033 (class 2606 OID 25841)
-- Name: security_master_data fk_issuer; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT fk_issuer FOREIGN KEY (issuer) REFERENCES public.issuer(id_issuer);


--
-- TOC entry 5036 (class 2606 OID 25871)
-- Name: cash_flow_item fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5037 (class 2606 OID 25883)
-- Name: cash_flow_reset fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.cash_flow_reset
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5043 (class 2606 OID 25958)
-- Name: deliverable_bonds fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.bond_future_master_data(id_master_data);


--
-- TOC entry 5046 (class 2606 OID 25990)
-- Name: instrument_quote fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5048 (class 2606 OID 26002)
-- Name: instrument_quote_hist fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5057 (class 2606 OID 26091)
-- Name: position_detail fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5060 (class 2606 OID 26132)
-- Name: financial_txn fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- TOC entry 5065 (class 2606 OID 26192)
-- Name: gl_accounts fk_nature; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_nature FOREIGN KEY (nature) REFERENCES public.account_natures(nature_id);


--
-- TOC entry 5066 (class 2606 OID 26182)
-- Name: gl_accounts fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_parent FOREIGN KEY (parent) REFERENCES public.gl_accounts(account_id);


--
-- TOC entry 5055 (class 2606 OID 26078)
-- Name: position_master_data fk_portfolio; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_portfolio FOREIGN KEY (portfolio) REFERENCES public.portfolio_master_data(id_portfolio);


--
-- TOC entry 5058 (class 2606 OID 26101)
-- Name: position_detail fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- TOC entry 5061 (class 2606 OID 26127)
-- Name: financial_txn fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- TOC entry 5031 (class 2606 OID 25792)
-- Name: master_data fk_roll_convention; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_roll_convention FOREIGN KEY (roll_convention) REFERENCES public.roll_convention(id_roll_convention);


--
-- TOC entry 5042 (class 2606 OID 25942)
-- Name: future_master_data fk_settlement_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.future_master_data
    ADD CONSTRAINT fk_settlement_type FOREIGN KEY (settlement_type) REFERENCES public.settlement_type(id_settlement_type);


--
-- TOC entry 5067 (class 2606 OID 26187)
-- Name: gl_accounts fk_statement_type; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_statement_type FOREIGN KEY (statement_type) REFERENCES public.financial_statement_types(statement_type_id);


--
-- TOC entry 5021 (class 2606 OID 25746)
-- Name: asset_class fk_super_class; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT fk_super_class FOREIGN KEY (super_class) REFERENCES public.super_class(id_super_class);


--
-- TOC entry 5062 (class 2606 OID 26137)
-- Name: financial_txn fk_txn_status; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_txn_status FOREIGN KEY (txn_status) REFERENCES public.txn_status(id_txn_status);


--
-- TOC entry 5032 (class 2606 OID 25797)
-- Name: master_data fk_type_of_interest; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_type_of_interest FOREIGN KEY (type_of_interest) REFERENCES public.type_of_interest(id_type_of_interest);


--
-- TOC entry 5044 (class 2606 OID 25970)
-- Name: fx_future_master_data fk_underlying; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- TOC entry 5045 (class 2606 OID 25980)
-- Name: mm_future_master_data fk_underlying; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT fk_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- TOC entry 5041 (class 2606 OID 25916)
-- Name: yield_curve_item fk_yield_curve; Type: FK CONSTRAINT; Schema: public; Owner: sofie
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_yield_curve FOREIGN KEY (yield_curve) REFERENCES public.yield_curve(id_yield_curve);


-- Completed on 2026-06-04 15:28:58

--
-- PostgreSQL database dump complete
--

