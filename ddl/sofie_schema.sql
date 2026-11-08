-- ============================================================================
-- Schema del database
-- Generato da ddl/export_schema.ps1: non modificare a mano.
-- Ordine: sofie_schema.sql, sofie_lookup.sql, sofie_config.sql
-- ============================================================================
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
-- Name: fn_manage_ref_id(); Type: FUNCTION; Schema: public; Owner: -
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


--
-- Name: purge_expired_master_data(); Type: FUNCTION; Schema: public; Owner: -
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


--
-- Name: update_position_txn_links_phase(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.update_position_txn_links_phase() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Aggiorna la fase nei link associati alla transazione finanziaria appena modificata
    UPDATE position_txn_links
    SET txn_acct_phase = NEW.txn_acct_phase
    WHERE financial_txn = NEW.id_financial_txn;

    RETURN NEW;
END;
$$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: ac_dc_bcy_link; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ac_dc_bcy_link (
    ac_dc_bcy_link_id integer NOT NULL,
    asset_class integer NOT NULL,
    currency integer NOT NULL,
    curve_role integer NOT NULL,
    yield_curve integer,
    spread_curve integer,
    CONSTRAINT ck_ac_dc_bcy_one_curve CHECK ((num_nonnulls(yield_curve, spread_curve) = 1))
);


--
-- Name: ac_dc_bcy_link_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ac_dc_bcy_link_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: account_mapping; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.account_mapping (
    account_mapping_id integer NOT NULL,
    mapping_key character varying(50) NOT NULL,
    currency integer NOT NULL,
    gl_account integer NOT NULL
);


--
-- Name: account_mapping_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.account_mapping_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: account_natures; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.account_natures (
    nature_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


--
-- Name: accounting_event_accruals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accounting_event_accruals (
    event_id integer NOT NULL,
    accounting_nominal numeric(28,10) NOT NULL,
    accrual_amount numeric(28,10) NOT NULL,
    coupon_rate numeric(28,10) NOT NULL,
    daycount integer NOT NULL,
    days integer NOT NULL
);


--
-- Name: accounting_event_mtm; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accounting_event_mtm (
    event_id integer NOT NULL,
    valuation_date date NOT NULL,
    market_price numeric(28,10) NOT NULL,
    accounting_nominal numeric(28,10) CONSTRAINT accounting_event_mtm_net_quantity_not_null NOT NULL,
    average_price numeric(28,10) NOT NULL,
    realized_pnl numeric(28,10) NOT NULL,
    unrealized_pnl numeric(28,10) NOT NULL
);


--
-- Name: accounting_event_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accounting_event_status (
    event_status_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


--
-- Name: accounting_event_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accounting_event_types (
    event_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


--
-- Name: accounting_events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accounting_events (
    event_id integer NOT NULL,
    event_type integer NOT NULL,
    event_status integer NOT NULL,
    source_type integer NOT NULL,
    source_id integer NOT NULL,
    event_key character varying(100) NOT NULL,
    generated_by integer NOT NULL,
    generated_ref character varying(100),
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    processed_at timestamp without time zone,
    position_detail integer DEFAULT 0 NOT NULL
);


--
-- Name: accounting_events_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.accounting_events_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: accrual_schedule_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accrual_schedule_type (
    id_accrual_schedule_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


--
-- Name: accrual_schedule_type_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.accrual_schedule_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: accumulator_fixing; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accumulator_fixing (
    id_accumulator_fixing integer NOT NULL,
    master_data integer NOT NULL,
    fixing_date date NOT NULL,
    settlement_date date NOT NULL,
    fixing_price numeric(28,10),
    accumulated_quantity numeric(28,10),
    CONSTRAINT ck_accumulator_fixing_dates CHECK ((settlement_date >= fixing_date))
);


--
-- Name: accumulator_fixing_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.accumulator_fixing_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: accumulator_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accumulator_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL,
    accumulator_type integer NOT NULL,
    settlement_type integer NOT NULL,
    settlement_frequency integer NOT NULL,
    strike numeric(28,10) NOT NULL,
    ko_barrier numeric(28,10) NOT NULL,
    ko_observation integer DEFAULT 1 NOT NULL,
    ki_barrier numeric(28,10),
    ki_effect integer,
    leverage numeric(28,10) DEFAULT 1 NOT NULL,
    daily_quantity numeric(28,10) NOT NULL,
    first_fixing_date date NOT NULL,
    last_fixing_date date NOT NULL,
    guaranteed_periods integer DEFAULT 0 CONSTRAINT accumulator_master_data_guaranteed_fixings_not_null NOT NULL,
    ko_accrual boolean DEFAULT false NOT NULL,
    fixing_time time without time zone,
    fixing_time_zone character varying(64),
    ko_date date,
    ki_date date,
    guarantee_type integer DEFAULT 1 NOT NULL,
    CONSTRAINT ck_accumulator_master_data_barrier CHECK ((((accumulator_type = 1) AND (ko_barrier > strike) AND ((ki_barrier IS NULL) OR (ki_barrier < strike))) OR ((accumulator_type = 2) AND (ko_barrier < strike) AND ((ki_barrier IS NULL) OR (ki_barrier > strike))))),
    CONSTRAINT ck_accumulator_master_data_dates CHECK ((last_fixing_date >= first_fixing_date)),
    CONSTRAINT ck_accumulator_master_data_guarantee CHECK (((guarantee_type = 1) = (guaranteed_periods = 0))),
    CONSTRAINT ck_accumulator_master_data_knock_in CHECK ((((ki_barrier IS NULL) = (ki_effect IS NULL)) AND ((ki_date IS NULL) OR (ki_barrier IS NOT NULL)))),
    CONSTRAINT ck_accumulator_master_data_quantities CHECK (((strike > (0)::numeric) AND (ko_barrier > (0)::numeric) AND (leverage >= (1)::numeric) AND (daily_quantity > (0)::numeric) AND (guaranteed_periods >= 0)))
);


--
-- Name: accumulator_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.accumulator_type (
    accumulator_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: amortization_schedule; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.amortization_schedule (
    id_amortization_schedule integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: amortization_schedule_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.amortization_schedule_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: asset_class; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.asset_class (
    id_asset_class integer NOT NULL,
    super_class integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


--
-- Name: asset_class_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.asset_class_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: asset_class_script_mapping; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.asset_class_script_mapping (
    asset_class_script_mapping_id integer CONSTRAINT asset_class_script_mapping_asset_class_script_mapping__not_null NOT NULL,
    asset_class integer NOT NULL,
    event_type integer,
    script_code character varying(16) NOT NULL
);


--
-- Name: asset_class_script_mapping_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.asset_class_script_mapping_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: barrier_observation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.barrier_observation (
    barrier_observation_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: bond_future_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bond_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL
);


--
-- Name: bond_future_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bond_future_product (
    id_future_product integer NOT NULL
);


--
-- Name: bond_valuation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bond_valuation (
    bond_valuation_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: broker_instrument_rules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.broker_instrument_rules (
    broker_rule_id integer NOT NULL,
    broker integer NOT NULL,
    master_data integer NOT NULL,
    txn_side smallint NOT NULL,
    initial_margin numeric(28,10) DEFAULT 0.0 NOT NULL,
    maintenance_margin numeric(28,10) DEFAULT 0.0 NOT NULL,
    broker_fee numeric(28,10) DEFAULT 0.0 NOT NULL,
    exchange_fee numeric(28,10) DEFAULT 0.0 NOT NULL,
    currency integer NOT NULL
);


--
-- Name: broker_instrument_rules_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.broker_instrument_rules_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: calendar; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.calendar (
    id_calendar integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


--
-- Name: calendar_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.calendar_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: calibration_profile; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.calibration_profile (
    calibration_profile_id integer NOT NULL,
    code character varying(25),
    description character varying(200),
    currency integer NOT NULL,
    last_update date NOT NULL
);


--
-- Name: calibration_profile_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.calibration_profile_item (
    calibration_profile_item_id integer NOT NULL,
    calibration_profile integer NOT NULL,
    master_data integer NOT NULL,
    bid numeric(28,10) DEFAULT 0 NOT NULL,
    ask numeric(28,10) DEFAULT 0 NOT NULL,
    instrument_quote integer NOT NULL,
    code character varying(25) NOT NULL
);


--
-- Name: calibration_profile_item_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.calibration_profile_item_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: calibration_profile_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.calibration_profile_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cash_flow_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cash_flow_item (
    id_cash_flow_item integer NOT NULL,
    master_data integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    interest numeric(28,10) NOT NULL,
    amount numeric(28,10) NOT NULL,
    cash_flow_status integer DEFAULT 2 NOT NULL
);


--
-- Name: cash_flow_item_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cash_flow_item_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cash_flow_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cash_flow_master_data (
    id_master_data integer NOT NULL,
    type_of_interest integer NOT NULL,
    form integer NOT NULL,
    daycount integer NOT NULL,
    accrual_daycount integer NOT NULL,
    frequency integer NOT NULL,
    roll_convention integer DEFAULT 0 NOT NULL,
    accrual_schedule_type integer DEFAULT 0 NOT NULL,
    interest_rate numeric(28,10) NOT NULL,
    issue_price numeric(28,10) NOT NULL,
    redempion_price numeric(28,10) NOT NULL,
    amortization_schedule integer NOT NULL,
    compounding integer DEFAULT 2 NOT NULL
);


--
-- Name: cash_flow_reset; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cash_flow_reset (
    id_cash_flow_reset integer NOT NULL,
    master_data integer NOT NULL,
    start_date_reset date NOT NULL,
    interest_reset numeric(28,10) NOT NULL
);


--
-- Name: cash_flow_reset_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.cash_flow_reset_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: cash_flow_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cash_flow_status (
    cash_flow_status_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) NOT NULL
);


--
-- Name: cmd_future_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cmd_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL
);


--
-- Name: cmd_future_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cmd_future_product (
    id_future_product integer NOT NULL,
    commodity_type integer NOT NULL
);


--
-- Name: commodity_delivery_profile; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.commodity_delivery_profile (
    id_master_data integer NOT NULL,
    delivery_period_type integer NOT NULL,
    delivery_start date NOT NULL,
    delivery_end date NOT NULL,
    total_delivery_hours integer NOT NULL,
    market character varying(16) NOT NULL,
    notional_mw numeric(28,10) NOT NULL,
    notional_mwh numeric(28,10) NOT NULL,
    parent_profile integer,
    market_quote_definition_id integer,
    CONSTRAINT chk_delivery_profile_dates CHECK ((delivery_end >= delivery_start)),
    CONSTRAINT chk_delivery_profile_notional_positive CHECK (((notional_mw > (0)::numeric) AND (notional_mwh > (0)::numeric)))
);


--
-- Name: commodity_delivery_profile_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.commodity_delivery_profile_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: commodity_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.commodity_type (
    commodity_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


--
-- Name: compounding; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.compounding (
    id_compounding integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: compounding_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.compounding_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: counterparty; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.counterparty (
    id_counterparty integer NOT NULL,
    ctp_type integer NOT NULL,
    lei_code character varying(50),
    country integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: counterparty_role_mapping; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.counterparty_role_mapping (
    counterparty_role_mapping_id integer NOT NULL,
    counterparty integer NOT NULL,
    ctp_role integer NOT NULL
);


--
-- Name: counterparty_role_mapping_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.counterparty_role_mapping_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: counterparty_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.counterparty_roles (
    role_id integer NOT NULL,
    code character varying(50) NOT NULL,
    description character varying(150) NOT NULL
);


--
-- Name: counterparty_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.counterparty_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: counterparty_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.counterparty_type (
    id_counterparty_type integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: counterparty_type_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.counterparty_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: country; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: country_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.country_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: coupon_pm; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.coupon_pm (
    coupon_pm_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) NOT NULL
);


--
-- Name: currency; Type: TABLE; Schema: public; Owner: -
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
    business_days integer DEFAULT 2 NOT NULL,
    decimal_places smallint DEFAULT 2 NOT NULL
);


--
-- Name: currency_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.currency_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: currpair_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.currpair_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL,
    ccy integer NOT NULL,
    bcy_irc character varying(25) DEFAULT ''::character varying NOT NULL,
    ccy_irc character varying(25) DEFAULT ''::character varying NOT NULL
);


--
-- Name: curve_role; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.curve_role (
    curve_role_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) NOT NULL
);


--
-- Name: daycount; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.daycount (
    id_daycount integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


--
-- Name: daycount_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.daycount_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: deliverable_bonds; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.deliverable_bonds (
    id_deliverable_bonds integer NOT NULL,
    expiration_date date NOT NULL,
    isin character varying(25) NOT NULL,
    coupon_rate numeric(28,10) NOT NULL,
    bond_maturity date NOT NULL,
    bond_cf numeric(28,10) NOT NULL,
    master_data integer NOT NULL
);


--
-- Name: deliverable_bonds_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.deliverable_bonds_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: delivery_period_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.delivery_period_type (
    delivery_period_type_id integer NOT NULL,
    code character varying(16) NOT NULL,
    description character varying(64)
);


--
-- Name: descriptors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.descriptors (
    descriptor_id integer NOT NULL,
    service_type integer NOT NULL,
    jar_path character varying(255) NOT NULL,
    active_profile character varying(25) NOT NULL,
    port character varying(5) NOT NULL
);


--
-- Name: descriptors_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.descriptors_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: eod_run; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.eod_run (
    eod_run_id integer NOT NULL,
    business_date date NOT NULL,
    next_business_date date NOT NULL,
    status integer NOT NULL,
    started_at timestamp without time zone NOT NULL,
    ended_at timestamp without time zone,
    message character varying(2000)
);


--
-- Name: eod_run_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.eod_run_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: eod_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.eod_status (
    eod_status_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: eod_step; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.eod_step (
    eod_step_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: eod_step_run; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.eod_step_run (
    eod_step_run_id integer NOT NULL,
    eod_run integer NOT NULL,
    step integer NOT NULL,
    status integer NOT NULL,
    processed integer DEFAULT 0 NOT NULL,
    created integer DEFAULT 0 NOT NULL,
    skipped integer DEFAULT 0 NOT NULL,
    errors integer DEFAULT 0 NOT NULL,
    message character varying(2000),
    started_at timestamp without time zone NOT NULL,
    ended_at timestamp without time zone
);


--
-- Name: eod_step_run_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.eod_step_run_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: event_source_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.event_source_types (
    source_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


--
-- Name: exercise_style; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.exercise_style (
    exercise_style_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: fee_basis; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fee_basis (
    fee_basis_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: fee_timing; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fee_timing (
    fee_timing_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: financial_statement_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.financial_statement_types (
    statement_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


--
-- Name: financial_txn; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.financial_txn (
    id_financial_txn integer NOT NULL,
    counterparty integer NOT NULL,
    position_md integer NOT NULL,
    master_data integer NOT NULL,
    txn_status integer NOT NULL,
    txn_side integer NOT NULL,
    description character varying(255) NOT NULL,
    trade_date date DEFAULT now() NOT NULL,
    value_date date NOT NULL,
    settlement date NOT NULL,
    quantity numeric(28,10) NOT NULL,
    price numeric(28,10) NOT NULL,
    ref_id integer NOT NULL,
    version integer DEFAULT 0,
    txn_status_pre_elab integer DEFAULT 1 NOT NULL,
    fx_rate numeric(28,10) DEFAULT 1 NOT NULL,
    txn_acct_phase integer DEFAULT 1 NOT NULL,
    txn_origin integer DEFAULT 1 NOT NULL,
    origin_key character varying(100)
);


--
-- Name: financial_txn_components; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.financial_txn_components (
    txn_component_id integer NOT NULL,
    financial_txn integer NOT NULL,
    component_type integer NOT NULL,
    currency integer NOT NULL,
    amount numeric(28,10) NOT NULL,
    description character varying(255) DEFAULT ''::character varying NOT NULL
);


--
-- Name: financial_txn_components_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.financial_txn_components_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: financial_txn_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.financial_txn_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: fixing_day_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fixing_day_type (
    fixing_day_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) NOT NULL
);


--
-- Name: flt_loan_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flt_loan_master_data (
    id_master_data integer NOT NULL,
    spread numeric(28,10) NOT NULL,
    projection_index integer,
    coupon_pm integer NOT NULL,
    fixing_lag_days integer DEFAULT 2 NOT NULL,
    fixing_day_type integer DEFAULT 2 NOT NULL,
    floor numeric(28,10),
    cap numeric(28,10),
    index_months integer DEFAULT 6 NOT NULL,
    projection_curve integer,
    CONSTRAINT ck_flt_loan_floor_cap CHECK (((floor IS NULL) OR (cap IS NULL) OR (floor <= cap)))
);


--
-- Name: flt_security_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flt_security_master_data (
    id_master_data integer NOT NULL,
    spread numeric(28,10) NOT NULL,
    projection_index integer,
    coupon_pm integer NOT NULL,
    fixing_lag_days integer DEFAULT 2 NOT NULL,
    fixing_day_type integer DEFAULT 2 NOT NULL,
    floor numeric(28,10),
    index_months integer DEFAULT 6 NOT NULL,
    projection_curve integer,
    cap numeric(28,10)
);


--
-- Name: forex_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.forex_master_data (
    id_master_data integer NOT NULL,
    bcy integer NOT NULL,
    ccy integer NOT NULL,
    bcy_irc character varying(25) DEFAULT ''::character varying NOT NULL,
    ccy_irc character varying(25) DEFAULT ''::character varying NOT NULL
);


--
-- Name: form; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.form (
    id_form integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


--
-- Name: form_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.form_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: frequency; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.frequency (
    id_frequency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL,
    year_fraction smallint DEFAULT 2 NOT NULL
);


--
-- Name: frequency_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.frequency_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: future_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.future_master_data (
    id_master_data integer NOT NULL,
    isin character varying(25),
    last_trading_date date NOT NULL,
    contract_month date NOT NULL,
    delivery_date date
);


--
-- Name: future_option_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.future_option_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL
);


--
-- Name: future_option_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.future_option_product (
    id_option_product integer NOT NULL,
    future_product integer NOT NULL
);


--
-- Name: future_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.future_product (
    id_future_product integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) DEFAULT ''::character varying NOT NULL,
    exchange_contract_code character varying(25) DEFAULT ''::character varying NOT NULL,
    settlement_type integer NOT NULL,
    contract_value numeric(28,10) NOT NULL,
    tick_size numeric(28,10) NOT NULL,
    initial_margin numeric(28,10) NOT NULL,
    maintenance_margin numeric(28,10),
    currency integer NOT NULL,
    asset_class integer NOT NULL,
    business_days integer DEFAULT 2 NOT NULL,
    multiplier numeric(28,10) DEFAULT 1 NOT NULL
);


--
-- Name: future_product_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.future_product_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: fx_future_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fx_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL
);


--
-- Name: fx_future_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fx_future_product (
    id_future_product integer NOT NULL,
    underlying integer NOT NULL
);


--
-- Name: gl_account_slots; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gl_account_slots (
    account_slot_id integer NOT NULL,
    account integer NOT NULL,
    currency integer NOT NULL
);


--
-- Name: gl_account_slots_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.gl_account_slots_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: gl_accounts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.gl_accounts (
    account_id integer NOT NULL,
    parent integer,
    code character varying(50) NOT NULL,
    description character varying(150) DEFAULT ''::character varying NOT NULL,
    is_postable boolean DEFAULT false NOT NULL,
    statement_type integer NOT NULL,
    nature integer NOT NULL,
    balance integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: gl_accounts_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.gl_accounts_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: granular_curve_definition; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granular_curve_definition (
    granular_curve_id integer NOT NULL,
    code character varying(32) NOT NULL,
    description character varying(255) NOT NULL,
    market_quote_definition_id integer NOT NULL,
    shape_profile_id integer NOT NULL
);


--
-- Name: granular_curve_definition_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granular_curve_definition_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: granular_curve_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.granular_curve_point (
    granular_curve_point_id bigint NOT NULL,
    granular_curve_id integer NOT NULL,
    delivery_date date NOT NULL,
    forward_price numeric(28,10) NOT NULL
);


--
-- Name: granular_curve_point_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.granular_curve_point_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: guarantee_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.guarantee_type (
    guarantee_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: holiday; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.holiday (
    id_holiday integer NOT NULL,
    calendar integer NOT NULL,
    holiday_day smallint NOT NULL,
    holiday_month smallint NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


--
-- Name: holiday_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.holiday_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: instrument_quote; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.instrument_quote (
    id_instrument_quote integer NOT NULL,
    master_data integer NOT NULL,
    provider character varying(50) NOT NULL,
    code character varying(255) NOT NULL,
    bid numeric(28,10) NOT NULL,
    ask numeric(28,10) NOT NULL
);


--
-- Name: instrument_quote_hist; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.instrument_quote_hist (
    id_instrument_quote_hist integer NOT NULL,
    instrument_quote integer NOT NULL,
    master_data integer NOT NULL,
    code character varying(25) NOT NULL,
    bid numeric(28,10) NOT NULL,
    ask numeric(28,10) NOT NULL,
    update_date date NOT NULL
);


--
-- Name: instrument_quote_hist_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.instrument_quote_hist_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: instrument_quote_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.instrument_quote_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: instrument_valuation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.instrument_valuation (
    instrument_valuation_id integer NOT NULL,
    master_data integer NOT NULL,
    market_price numeric(28,10) NOT NULL,
    accrued_interest numeric(28,10) DEFAULT 0 NOT NULL,
    ytm numeric(28,10) DEFAULT 0 NOT NULL,
    duration numeric(28,10) DEFAULT 0 NOT NULL,
    mod_duration numeric(28,10) DEFAULT 0 NOT NULL,
    theoretical_price numeric(28,10) DEFAULT 0 NOT NULL,
    valuation_date date DEFAULT now() NOT NULL,
    dv01 numeric(28,10) DEFAULT 0 NOT NULL
);


--
-- Name: issuer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.issuer (
    id_issuer integer NOT NULL,
    short_issuer_name character varying(25) DEFAULT ''::character varying NOT NULL,
    long_issuer_name character varying(255) NOT NULL,
    country integer NOT NULL
);


--
-- Name: issuer_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.issuer_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: journal_entries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.journal_entries (
    journal_entry_id integer NOT NULL,
    accounting_event integer NOT NULL,
    entry_type integer NOT NULL,
    business_date date NOT NULL,
    reference character varying(100),
    description character varying(500),
    reversal_of integer,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    entry_status integer DEFAULT 1 NOT NULL
);


--
-- Name: journal_entries_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.journal_entries_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: journal_entry_lines; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.journal_entry_lines (
    journal_entry_line_id integer NOT NULL,
    journal_entry integer NOT NULL,
    line_no integer NOT NULL,
    debit_amount numeric(28,10),
    credit_amount numeric(28,10),
    description character varying(250),
    account_slot integer NOT NULL,
    currency integer NOT NULL
);


--
-- Name: journal_entry_lines_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.journal_entry_lines_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: journal_entry_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.journal_entry_status (
    entry_status_id integer NOT NULL,
    code character varying(30) NOT NULL,
    description character varying(100)
);


--
-- Name: journal_entry_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.journal_entry_types (
    entry_type_id integer NOT NULL,
    code character varying(30) NOT NULL,
    description character varying(100)
);


--
-- Name: knock_in_effect; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.knock_in_effect (
    knock_in_effect_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: ledger_balances; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ledger_balances (
    ledger_balance_id integer NOT NULL,
    gl_account integer NOT NULL,
    currency integer NOT NULL,
    opening_balance numeric(28,10) DEFAULT 0,
    debit_turnover numeric(28,10) DEFAULT 0,
    credit_turnover numeric(28,10) DEFAULT 0,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    closing_balance numeric(28,10) GENERATED ALWAYS AS (((opening_balance + debit_turnover) - credit_turnover)) STORED
);


--
-- Name: ledger_balances_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ledger_balances_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: load_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.load_type (
    load_type_id integer NOT NULL,
    code character varying(16) NOT NULL,
    description character varying(64)
);


--
-- Name: loan_fee; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan_fee (
    id_loan_fee integer NOT NULL,
    master_data integer NOT NULL,
    fee_type integer NOT NULL,
    fee_basis integer NOT NULL,
    fee_timing integer NOT NULL,
    value numeric(28,10) NOT NULL,
    CONSTRAINT ck_loan_fee_value CHECK ((value >= (0)::numeric))
);


--
-- Name: loan_fee_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.loan_fee_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: loan_fee_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan_fee_type (
    loan_fee_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: loan_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loan_master_data (
    id_master_data integer NOT NULL,
    default_interest numeric(28,10) DEFAULT 0 NOT NULL,
    late_payment_fee numeric(28,10) DEFAULT 0 NOT NULL,
    principal_amount numeric(28,10) NOT NULL,
    first_payment_date date,
    pre_amortization_periods integer DEFAULT 0 NOT NULL,
    CONSTRAINT ck_loan_master_data_values CHECK (((principal_amount > (0)::numeric) AND (pre_amortization_periods >= 0) AND (default_interest >= (0)::numeric) AND (late_payment_fee >= (0)::numeric)))
);


--
-- Name: market_data_source; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.market_data_source (
    data_source_id integer NOT NULL,
    code character varying(32) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: market_quote; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.market_quote (
    market_quote_id integer NOT NULL,
    market_quote_definition_id integer NOT NULL,
    business_date date NOT NULL,
    price numeric(28,10) NOT NULL
);


--
-- Name: market_quote_definition; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.market_quote_definition (
    market_quote_definition_id integer NOT NULL,
    code character varying(32) NOT NULL,
    description character varying(255) NOT NULL,
    data_source integer NOT NULL,
    id_country integer NOT NULL
);


--
-- Name: market_quote_definition_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.market_quote_definition_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: market_quote_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.market_quote_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.master_data (
    id_master_data integer NOT NULL,
    code character varying(25) NOT NULL,
    currency integer NOT NULL,
    issue_date date NOT NULL,
    maturity_date date NOT NULL,
    business_days integer DEFAULT 2 NOT NULL,
    asset_class integer NOT NULL,
    multiplier numeric(28,10) DEFAULT 1 NOT NULL,
    description character varying(255) DEFAULT ''::character varying
);


--
-- Name: master_data_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: mm_future_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.mm_future_master_data (
    id_master_data integer NOT NULL,
    future_product integer NOT NULL
);


--
-- Name: mm_future_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.mm_future_product (
    id_future_product integer NOT NULL,
    underlying integer NOT NULL
);


--
-- Name: node_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.node_type (
    node_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) NOT NULL
);


--
-- Name: normal_balances; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.normal_balances (
    balance_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) DEFAULT ''::character varying NOT NULL
);


--
-- Name: option_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.option_master_data (
    id_master_data integer NOT NULL,
    option_product integer NOT NULL,
    option_type integer NOT NULL,
    strike numeric(28,10) NOT NULL,
    isin character varying(25)
);


--
-- Name: option_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.option_product (
    id_option_product integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) DEFAULT ''::character varying NOT NULL,
    exchange_contract_code character varying(25) DEFAULT ''::character varying NOT NULL,
    exercise_style integer NOT NULL,
    premium_style integer NOT NULL,
    settlement_type integer NOT NULL,
    contract_value numeric(28,10) NOT NULL,
    tick_size numeric(28,10) NOT NULL,
    initial_margin numeric(28,10),
    maintenance_margin numeric(28,10),
    expiry_time time without time zone,
    expiry_time_zone character varying(64),
    currency integer NOT NULL,
    asset_class integer NOT NULL,
    business_days integer DEFAULT 2 NOT NULL,
    multiplier numeric(28,10) DEFAULT 1 NOT NULL
);


--
-- Name: option_product_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.option_product_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: option_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.option_type (
    option_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: portfolio_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.portfolio_master_data (
    id_portfolio integer NOT NULL,
    currency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: portfolio_master_data_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.portfolio_master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: position_detail; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.position_detail (
    id_position_detail integer NOT NULL,
    position_md integer NOT NULL,
    master_data integer NOT NULL,
    counterparty integer NOT NULL,
    realized_pnl numeric(28,10) NOT NULL,
    unrealized_pnl numeric(28,10) NOT NULL,
    buy_qty numeric(28,10) NOT NULL,
    notional_value_buy numeric(28,10) NOT NULL,
    buy_fees numeric(28,10) NOT NULL,
    buy_taxes numeric(28,10) NOT NULL,
    sell_qty numeric(28,10) NOT NULL,
    notional_value_sell numeric(28,10) NOT NULL,
    sell_fees numeric(28,10) NOT NULL,
    sell_taxes numeric(28,10) NOT NULL,
    market_price numeric(28,10) NOT NULL,
    ytm numeric(28,10) DEFAULT 0 NOT NULL,
    duration numeric(28,10) DEFAULT 0 NOT NULL,
    time_to_maturity numeric(28,10) DEFAULT 0 NOT NULL,
    mod_duration numeric(28,10) DEFAULT 0 NOT NULL,
    theoretical_price numeric(28,10) DEFAULT 0 NOT NULL,
    buy_accrual numeric(28,10) DEFAULT 0 NOT NULL,
    sell_accrual numeric(28,10) DEFAULT 0 NOT NULL,
    official_date date DEFAULT now() NOT NULL,
    last_mtm_executed timestamp without time zone DEFAULT (now() - '01:00:00'::interval)
);


--
-- Name: position_detail_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.position_detail_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: position_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.position_master_data (
    id_position integer NOT NULL,
    portfolio integer NOT NULL,
    currency integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL,
    fx_rolling boolean DEFAULT false NOT NULL,
    bond_valuation integer DEFAULT 1 NOT NULL
);


--
-- Name: position_master_data_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.position_master_data_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: position_txn_links; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.position_txn_links (
    pos_txn_link_id integer NOT NULL,
    position_detail integer NOT NULL,
    financial_txn integer NOT NULL,
    txn_acct_phase integer NOT NULL,
    settlement date NOT NULL,
    quantity numeric(28,10) NOT NULL,
    price numeric(28,10) NOT NULL,
    fx_rate numeric(28,10) NOT NULL
);


--
-- Name: position_txn_links_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.position_txn_links_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: power_future_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.power_future_master_data (
    id_master_data integer NOT NULL
);


--
-- Name: power_future_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.power_future_product (
    id_future_product integer NOT NULL,
    load_type integer NOT NULL
);


--
-- Name: power_spot_price; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.power_spot_price (
    spot_price_id integer NOT NULL,
    spot_price_definition_id integer NOT NULL,
    delivery_date date NOT NULL,
    price numeric(28,10) NOT NULL
);


--
-- Name: power_spot_price_definition; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.power_spot_price_definition (
    spot_price_definition_id integer NOT NULL,
    code character varying(32) NOT NULL,
    description character varying(255) NOT NULL,
    id_country integer NOT NULL,
    load_type integer NOT NULL,
    data_source integer NOT NULL
);


--
-- Name: power_spot_price_definition_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.power_spot_price_definition_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: power_spot_price_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.power_spot_price_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: premium_style; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.premium_style (
    premium_style_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: projection_method; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.projection_method (
    projection_method_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(50) NOT NULL
);


--
-- Name: ref_rate_index; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ref_rate_index (
    ref_rate_index_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(100) NOT NULL,
    currency integer NOT NULL,
    daycount integer NOT NULL,
    tenor integer NOT NULL,
    market_quote_definition_id integer NOT NULL
);


--
-- Name: ref_rate_index_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.ref_rate_index_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: roll_convention; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.roll_convention (
    id_roll_convention integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


--
-- Name: roll_convention_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.roll_convention_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sbc_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sbc_status (
    sbc_status_id integer NOT NULL,
    code character varying(50) NOT NULL,
    description character varying(150) NOT NULL
);


--
-- Name: security_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.security_master_data (
    id_master_data integer NOT NULL,
    isin character varying(25) NOT NULL,
    cfi_code character varying(25) NOT NULL,
    fisn character varying(255) NOT NULL,
    lei character varying(255) NOT NULL,
    issuer integer NOT NULL,
    nominal_value numeric(28,10) NOT NULL,
    first_coupon_rate numeric(28,10) NOT NULL,
    first_coupon_payment_date date NOT NULL
);


--
-- Name: service_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.service_types (
    service_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: settlement_type; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.settlement_type (
    id_settlement_type integer NOT NULL,
    code character varying(25),
    description character varying(25) DEFAULT ''::character varying NOT NULL
);


--
-- Name: settlement_type_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.settlement_type_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: shape_calculation_method; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shape_calculation_method (
    calculation_method_id integer NOT NULL,
    code character varying(32) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: shape_granularity; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shape_granularity (
    granularity_id integer NOT NULL,
    code character varying(32) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: shape_profile_definition; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shape_profile_definition (
    shape_profile_id integer NOT NULL,
    profile_code character varying(32) NOT NULL,
    description character varying(255) NOT NULL,
    id_country integer NOT NULL,
    load_type integer NOT NULL,
    calculation_method integer NOT NULL,
    granularity integer NOT NULL,
    data_source integer NOT NULL,
    historical_years integer,
    valid_from date NOT NULL,
    valid_to date,
    CONSTRAINT chk_shape_profile_dates CHECK (((valid_to IS NULL) OR (valid_to >= valid_from))),
    CONSTRAINT chk_shape_profile_years CHECK (((historical_years IS NULL) OR (historical_years > 0)))
);


--
-- Name: shape_profile_definition_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.shape_profile_definition_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: shape_profile_factor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shape_profile_factor (
    shape_profile_factor_id bigint NOT NULL,
    shape_profile_id integer NOT NULL,
    month_of_year smallint,
    day_of_week smallint,
    hour_of_day smallint,
    is_holiday boolean,
    weight_factor numeric(28,10) NOT NULL,
    CONSTRAINT chk_shape_factor_dow CHECK (((day_of_week IS NULL) OR ((day_of_week >= 1) AND (day_of_week <= 7)))),
    CONSTRAINT chk_shape_factor_hour CHECK (((hour_of_day IS NULL) OR ((hour_of_day >= 0) AND (hour_of_day <= 23)))),
    CONSTRAINT chk_shape_factor_month CHECK (((month_of_year IS NULL) OR ((month_of_year >= 1) AND (month_of_year <= 12))))
);


--
-- Name: shape_profile_factor_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.shape_profile_factor_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: shape_profile_spot_series; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shape_profile_spot_series (
    shape_profile_spot_series_id bigint NOT NULL,
    shape_profile_id integer NOT NULL,
    spot_price_definition_id integer NOT NULL,
    observation_from date,
    observation_to date
);


--
-- Name: shape_profile_spot_series_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.shape_profile_spot_series_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: spot_option_master_data; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spot_option_master_data (
    id_master_data integer NOT NULL,
    underlying integer NOT NULL,
    delivery_date date
);


--
-- Name: spot_option_product; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spot_option_product (
    id_option_product integer NOT NULL,
    underlying integer
);


--
-- Name: spread_curve; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spread_curve (
    id_spread_curve integer NOT NULL,
    code character varying(50) NOT NULL,
    description character varying(200),
    base_curve integer NOT NULL,
    max_age_days integer DEFAULT 5 NOT NULL,
    min_bonds integer DEFAULT 3 NOT NULL,
    min_valid_buckets integer DEFAULT 3 NOT NULL,
    calibration_profile integer DEFAULT 1 NOT NULL
);


--
-- Name: spread_curve_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.spread_curve_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: super_class; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.super_class (
    id_super_class integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(225) DEFAULT ''::character varying NOT NULL
);


--
-- Name: super_class_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.super_class_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: system_business_calendar; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.system_business_calendar (
    sbc_id integer NOT NULL,
    description character varying(50) NOT NULL,
    calendar integer,
    status integer NOT NULL,
    official_date date NOT NULL,
    next_business_date date,
    version integer DEFAULT 0 NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    currency integer DEFAULT 1 NOT NULL,
    last_official_date date NOT NULL
);


--
-- Name: tenor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tenor (
    tenor_id integer NOT NULL,
    code character varying(20) NOT NULL,
    description character varying(50) NOT NULL
);


--
-- Name: txn_accounting_phase; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.txn_accounting_phase (
    acct_phase_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: txn_component_types; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.txn_component_types (
    component_type_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(150) NOT NULL
);


--
-- Name: txn_origin; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.txn_origin (
    txn_origin_id integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(64) NOT NULL
);


--
-- Name: txn_side; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.txn_side (
    id_txn_side integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: txn_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.txn_status (
    id_txn_status integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) NOT NULL
);


--
-- Name: txn_status_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.txn_status_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: type_of_interest; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.type_of_interest (
    id_type_of_interest integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(25) NOT NULL
);


--
-- Name: type_of_interest_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.type_of_interest_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: volatility_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.volatility_point (
    id_volatility_point integer NOT NULL,
    volatility_surface integer NOT NULL,
    reference_date date NOT NULL,
    expiry_date date NOT NULL,
    strike numeric(28,10),
    volatility numeric(28,10) NOT NULL,
    source character varying(64) DEFAULT ''::character varying NOT NULL,
    CONSTRAINT ck_volatility_point_values CHECK (((volatility > (0)::numeric) AND (volatility < (5)::numeric) AND ((strike IS NULL) OR (strike > (0)::numeric)) AND (expiry_date > reference_date)))
);


--
-- Name: volatility_point_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.volatility_point_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: volatility_surface; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.volatility_surface (
    id_volatility_surface integer NOT NULL,
    code character varying(25) NOT NULL,
    description character varying(255) DEFAULT ''::character varying NOT NULL,
    underlying integer NOT NULL
);


--
-- Name: volatility_surface_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.volatility_surface_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: yield_curve; Type: TABLE; Schema: public; Owner: -
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


--
-- Name: yield_curve_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.yield_curve_item (
    id_yield_curve_item integer NOT NULL,
    yield_curve integer NOT NULL,
    ric character varying(25) NOT NULL,
    offset_type smallint NOT NULL,
    offset_value smallint NOT NULL,
    bid numeric(28,10) NOT NULL,
    ask numeric(28,10) NOT NULL,
    compounding integer DEFAULT 1 NOT NULL,
    daycount integer NOT NULL,
    node_type integer DEFAULT 1 NOT NULL
);


--
-- Name: yield_curve_item_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.yield_curve_item_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: yield_curve_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.yield_curve_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: yield_curve_spread; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.yield_curve_spread (
    id_yield_curve_spread integer NOT NULL,
    spread_curve integer NOT NULL,
    as_of_date date NOT NULL,
    base_curve_code character varying(50) NOT NULL,
    offset_type smallint NOT NULL,
    offset_value smallint NOT NULL,
    z_spread numeric(28,10) NOT NULL,
    n_bonds integer,
    dispersion numeric(28,10),
    calibrated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_ycs_offset_value CHECK ((offset_value > 0))
);


--
-- Name: yield_curve_spread_s; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.yield_curve_spread_s
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: account_mapping account_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account_mapping
    ADD CONSTRAINT account_mapping_pkey PRIMARY KEY (account_mapping_id);


--
-- Name: account_natures account_natures_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account_natures
    ADD CONSTRAINT account_natures_pkey PRIMARY KEY (nature_id);


--
-- Name: accounting_event_accruals accounting_event_accruals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_event_accruals
    ADD CONSTRAINT accounting_event_accruals_pkey PRIMARY KEY (event_id);


--
-- Name: accounting_event_status accounting_event_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_event_status
    ADD CONSTRAINT accounting_event_status_pkey PRIMARY KEY (event_status_id);


--
-- Name: accounting_event_types accounting_event_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_event_types
    ADD CONSTRAINT accounting_event_types_pkey PRIMARY KEY (event_type_id);


--
-- Name: accounting_events accounting_events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_events
    ADD CONSTRAINT accounting_events_pkey PRIMARY KEY (event_id);


--
-- Name: accrual_schedule_type accrual_schedule_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accrual_schedule_type
    ADD CONSTRAINT accrual_schedule_type_pkey PRIMARY KEY (id_accrual_schedule_type);


--
-- Name: amortization_schedule amortization_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.amortization_schedule
    ADD CONSTRAINT amortization_schedule_pkey PRIMARY KEY (id_amortization_schedule);


--
-- Name: asset_class asset_class_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT asset_class_pkey PRIMARY KEY (id_asset_class);


--
-- Name: bond_future_master_data bond_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bond_future_master_data
    ADD CONSTRAINT bond_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: broker_instrument_rules broker_instrument_rules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broker_instrument_rules
    ADD CONSTRAINT broker_instrument_rules_pkey PRIMARY KEY (broker_rule_id);


--
-- Name: calendar calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (id_calendar);


--
-- Name: cash_flow_item cash_flow_item_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT cash_flow_item_pkey PRIMARY KEY (id_cash_flow_item);


--
-- Name: cash_flow_reset cash_flow_reset_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_reset
    ADD CONSTRAINT cash_flow_reset_pkey PRIMARY KEY (id_cash_flow_reset);


--
-- Name: cash_flow_status cash_flow_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_status
    ADD CONSTRAINT cash_flow_status_pkey PRIMARY KEY (cash_flow_status_id);


--
-- Name: cmd_future_master_data cmd_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cmd_future_master_data
    ADD CONSTRAINT cmd_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: commodity_type commodity_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commodity_type
    ADD CONSTRAINT commodity_type_pkey PRIMARY KEY (commodity_type_id);


--
-- Name: compounding compounding_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.compounding
    ADD CONSTRAINT compounding_pkey PRIMARY KEY (id_compounding);


--
-- Name: counterparty counterparty_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT counterparty_pkey PRIMARY KEY (id_counterparty);


--
-- Name: counterparty_role_mapping counterparty_role_mapping_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty_role_mapping
    ADD CONSTRAINT counterparty_role_mapping_pkey PRIMARY KEY (counterparty_role_mapping_id);


--
-- Name: counterparty_roles counterparty_roles_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty_roles
    ADD CONSTRAINT counterparty_roles_code_key UNIQUE (code);


--
-- Name: counterparty_roles counterparty_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty_roles
    ADD CONSTRAINT counterparty_roles_pkey PRIMARY KEY (role_id);


--
-- Name: counterparty_type counterparty_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty_type
    ADD CONSTRAINT counterparty_type_pkey PRIMARY KEY (id_counterparty_type);


--
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id_country);


--
-- Name: coupon_pm coupon_pm_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.coupon_pm
    ADD CONSTRAINT coupon_pm_pkey PRIMARY KEY (coupon_pm_id);


--
-- Name: currency currency_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT currency_pkey PRIMARY KEY (id_currency);


--
-- Name: currpair_master_data currpair_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT currpair_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: curve_role curve_role_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.curve_role
    ADD CONSTRAINT curve_role_pkey PRIMARY KEY (curve_role_id);


--
-- Name: daycount daycount_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.daycount
    ADD CONSTRAINT daycount_pkey PRIMARY KEY (id_daycount);


--
-- Name: deliverable_bonds deliverable_bonds_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT deliverable_bonds_pkey PRIMARY KEY (id_deliverable_bonds);


--
-- Name: delivery_period_type delivery_period_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.delivery_period_type
    ADD CONSTRAINT delivery_period_type_pkey PRIMARY KEY (delivery_period_type_id);


--
-- Name: descriptors descriptors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.descriptors
    ADD CONSTRAINT descriptors_pkey PRIMARY KEY (descriptor_id);


--
-- Name: event_source_types event_source_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.event_source_types
    ADD CONSTRAINT event_source_types_pkey PRIMARY KEY (source_type_id);


--
-- Name: financial_statement_types financial_statement_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_statement_types
    ADD CONSTRAINT financial_statement_types_pkey PRIMARY KEY (statement_type_id);


--
-- Name: financial_txn_components financial_txn_components_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn_components
    ADD CONSTRAINT financial_txn_components_pkey PRIMARY KEY (txn_component_id);


--
-- Name: financial_txn financial_txn_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT financial_txn_pkey PRIMARY KEY (id_financial_txn);


--
-- Name: fixing_day_type fixing_day_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fixing_day_type
    ADD CONSTRAINT fixing_day_type_pkey PRIMARY KEY (fixing_day_type_id);


--
-- Name: flt_security_master_data flt_security_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_security_master_data
    ADD CONSTRAINT flt_security_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: forex_master_data forex_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT forex_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: form form_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.form
    ADD CONSTRAINT form_pkey PRIMARY KEY (id_form);


--
-- Name: frequency frequency_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.frequency
    ADD CONSTRAINT frequency_pkey PRIMARY KEY (id_frequency);


--
-- Name: future_master_data future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_master_data
    ADD CONSTRAINT future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: fx_future_master_data fx_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fx_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: gl_account_slots gl_account_slots_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_account_slots
    ADD CONSTRAINT gl_account_slots_pkey PRIMARY KEY (account_slot_id);


--
-- Name: gl_accounts gl_accounts_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT gl_accounts_code_key UNIQUE (code);


--
-- Name: gl_accounts gl_accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT gl_accounts_pkey PRIMARY KEY (account_id);


--
-- Name: holiday holiday_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT holiday_pkey PRIMARY KEY (id_holiday);


--
-- Name: broker_instrument_rules idx_broker_inst_side; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broker_instrument_rules
    ADD CONSTRAINT idx_broker_inst_side UNIQUE (broker, master_data, txn_side);


--
-- Name: instrument_quote_hist instrument_quote_hist_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT instrument_quote_hist_pkey PRIMARY KEY (id_instrument_quote_hist);


--
-- Name: instrument_quote instrument_quote_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT instrument_quote_pkey PRIMARY KEY (id_instrument_quote);


--
-- Name: instrument_valuation instrument_valuation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.instrument_valuation
    ADD CONSTRAINT instrument_valuation_pkey PRIMARY KEY (instrument_valuation_id);


--
-- Name: issuer issuer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT issuer_pkey PRIMARY KEY (id_issuer);


--
-- Name: journal_entries journal_entries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entries
    ADD CONSTRAINT journal_entries_pkey PRIMARY KEY (journal_entry_id);


--
-- Name: journal_entry_lines journal_entry_lines_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entry_lines
    ADD CONSTRAINT journal_entry_lines_pkey PRIMARY KEY (journal_entry_line_id);


--
-- Name: journal_entry_status journal_entry_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entry_status
    ADD CONSTRAINT journal_entry_status_pkey PRIMARY KEY (entry_status_id);


--
-- Name: journal_entry_types journal_entry_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entry_types
    ADD CONSTRAINT journal_entry_types_pkey PRIMARY KEY (entry_type_id);


--
-- Name: ledger_balances ledger_balances_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ledger_balances
    ADD CONSTRAINT ledger_balances_pkey PRIMARY KEY (ledger_balance_id);


--
-- Name: load_type load_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.load_type
    ADD CONSTRAINT load_type_pkey PRIMARY KEY (load_type_id);


--
-- Name: loan_master_data loan_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_master_data
    ADD CONSTRAINT loan_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: master_data master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: mm_future_master_data mm_future_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT mm_future_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: node_type node_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.node_type
    ADD CONSTRAINT node_type_pkey PRIMARY KEY (node_type_id);


--
-- Name: normal_balances normal_balances_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.normal_balances
    ADD CONSTRAINT normal_balances_pkey PRIMARY KEY (balance_id);


--
-- Name: ac_dc_bcy_link pk_ac_dc_bcy_link; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ac_dc_bcy_link
    ADD CONSTRAINT pk_ac_dc_bcy_link PRIMARY KEY (ac_dc_bcy_link_id);


--
-- Name: accounting_event_mtm pk_accounting_event_mtm; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_event_mtm
    ADD CONSTRAINT pk_accounting_event_mtm PRIMARY KEY (event_id);


--
-- Name: accumulator_fixing pk_accumulator_fixing; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_fixing
    ADD CONSTRAINT pk_accumulator_fixing PRIMARY KEY (id_accumulator_fixing);


--
-- Name: accumulator_master_data pk_accumulator_master_data; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT pk_accumulator_master_data PRIMARY KEY (id_master_data);


--
-- Name: accumulator_type pk_accumulator_type; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_type
    ADD CONSTRAINT pk_accumulator_type PRIMARY KEY (accumulator_type_id);


--
-- Name: asset_class_script_mapping pk_asset_class_script_mapping; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asset_class_script_mapping
    ADD CONSTRAINT pk_asset_class_script_mapping PRIMARY KEY (asset_class_script_mapping_id);


--
-- Name: barrier_observation pk_barrier_observation; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barrier_observation
    ADD CONSTRAINT pk_barrier_observation PRIMARY KEY (barrier_observation_id);


--
-- Name: bond_future_product pk_bond_future_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bond_future_product
    ADD CONSTRAINT pk_bond_future_product PRIMARY KEY (id_future_product);


--
-- Name: bond_valuation pk_bond_valuation; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bond_valuation
    ADD CONSTRAINT pk_bond_valuation PRIMARY KEY (bond_valuation_id);


--
-- Name: calibration_profile pk_calibration_profile; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile
    ADD CONSTRAINT pk_calibration_profile PRIMARY KEY (calibration_profile_id);


--
-- Name: calibration_profile_item pk_calibration_profile_item; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile_item
    ADD CONSTRAINT pk_calibration_profile_item PRIMARY KEY (calibration_profile_item_id);


--
-- Name: cash_flow_master_data pk_cash_flow_master_data; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT pk_cash_flow_master_data PRIMARY KEY (id_master_data);


--
-- Name: cmd_future_product pk_cmd_future_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cmd_future_product
    ADD CONSTRAINT pk_cmd_future_product PRIMARY KEY (id_future_product);


--
-- Name: commodity_delivery_profile pk_commodity_delivery_profile; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commodity_delivery_profile
    ADD CONSTRAINT pk_commodity_delivery_profile PRIMARY KEY (id_master_data);


--
-- Name: eod_run pk_eod_run; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_run
    ADD CONSTRAINT pk_eod_run PRIMARY KEY (eod_run_id);


--
-- Name: eod_status pk_eod_status; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_status
    ADD CONSTRAINT pk_eod_status PRIMARY KEY (eod_status_id);


--
-- Name: eod_step pk_eod_step; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_step
    ADD CONSTRAINT pk_eod_step PRIMARY KEY (eod_step_id);


--
-- Name: eod_step_run pk_eod_step_run; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_step_run
    ADD CONSTRAINT pk_eod_step_run PRIMARY KEY (eod_step_run_id);


--
-- Name: exercise_style pk_exercise_style; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exercise_style
    ADD CONSTRAINT pk_exercise_style PRIMARY KEY (exercise_style_id);


--
-- Name: fee_basis pk_fee_basis; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fee_basis
    ADD CONSTRAINT pk_fee_basis PRIMARY KEY (fee_basis_id);


--
-- Name: fee_timing pk_fee_timing; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fee_timing
    ADD CONSTRAINT pk_fee_timing PRIMARY KEY (fee_timing_id);


--
-- Name: flt_loan_master_data pk_flt_loan_master_data; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_loan_master_data
    ADD CONSTRAINT pk_flt_loan_master_data PRIMARY KEY (id_master_data);


--
-- Name: future_option_master_data pk_future_option_master_data; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_option_master_data
    ADD CONSTRAINT pk_future_option_master_data PRIMARY KEY (id_master_data);


--
-- Name: future_option_product pk_future_option_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_option_product
    ADD CONSTRAINT pk_future_option_product PRIMARY KEY (id_option_product);


--
-- Name: future_product pk_future_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_product
    ADD CONSTRAINT pk_future_product PRIMARY KEY (id_future_product);


--
-- Name: fx_future_product pk_fx_future_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fx_future_product
    ADD CONSTRAINT pk_fx_future_product PRIMARY KEY (id_future_product);


--
-- Name: granular_curve_definition pk_granular_curve_definition; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granular_curve_definition
    ADD CONSTRAINT pk_granular_curve_definition PRIMARY KEY (granular_curve_id);


--
-- Name: granular_curve_point pk_granular_curve_point; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granular_curve_point
    ADD CONSTRAINT pk_granular_curve_point PRIMARY KEY (granular_curve_point_id);


--
-- Name: guarantee_type pk_guarantee_type; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.guarantee_type
    ADD CONSTRAINT pk_guarantee_type PRIMARY KEY (guarantee_type_id);


--
-- Name: knock_in_effect pk_knock_in_effect; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.knock_in_effect
    ADD CONSTRAINT pk_knock_in_effect PRIMARY KEY (knock_in_effect_id);


--
-- Name: loan_fee pk_loan_fee; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee
    ADD CONSTRAINT pk_loan_fee PRIMARY KEY (id_loan_fee);


--
-- Name: loan_fee_type pk_loan_fee_type; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee_type
    ADD CONSTRAINT pk_loan_fee_type PRIMARY KEY (loan_fee_type_id);


--
-- Name: market_data_source pk_market_data_source; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_data_source
    ADD CONSTRAINT pk_market_data_source PRIMARY KEY (data_source_id);


--
-- Name: market_quote pk_market_quote; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_quote
    ADD CONSTRAINT pk_market_quote PRIMARY KEY (market_quote_id);


--
-- Name: market_quote_definition pk_market_quote_definition; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_quote_definition
    ADD CONSTRAINT pk_market_quote_definition PRIMARY KEY (market_quote_definition_id);


--
-- Name: mm_future_product pk_mm_future_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mm_future_product
    ADD CONSTRAINT pk_mm_future_product PRIMARY KEY (id_future_product);


--
-- Name: option_master_data pk_option_master_data; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_master_data
    ADD CONSTRAINT pk_option_master_data PRIMARY KEY (id_master_data);


--
-- Name: option_product pk_option_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_product
    ADD CONSTRAINT pk_option_product PRIMARY KEY (id_option_product);


--
-- Name: option_type pk_option_type; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_type
    ADD CONSTRAINT pk_option_type PRIMARY KEY (option_type_id);


--
-- Name: power_future_master_data pk_power_future_master_data; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_future_master_data
    ADD CONSTRAINT pk_power_future_master_data PRIMARY KEY (id_master_data);


--
-- Name: power_future_product pk_power_future_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_future_product
    ADD CONSTRAINT pk_power_future_product PRIMARY KEY (id_future_product);


--
-- Name: power_spot_price pk_power_spot_price; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price
    ADD CONSTRAINT pk_power_spot_price PRIMARY KEY (spot_price_id);


--
-- Name: power_spot_price_definition pk_power_spot_price_definition; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price_definition
    ADD CONSTRAINT pk_power_spot_price_definition PRIMARY KEY (spot_price_definition_id);


--
-- Name: premium_style pk_premium_style; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.premium_style
    ADD CONSTRAINT pk_premium_style PRIMARY KEY (premium_style_id);


--
-- Name: ref_rate_index pk_ref_rate_index; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ref_rate_index
    ADD CONSTRAINT pk_ref_rate_index PRIMARY KEY (ref_rate_index_id);


--
-- Name: shape_calculation_method pk_shape_calculation_method; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_calculation_method
    ADD CONSTRAINT pk_shape_calculation_method PRIMARY KEY (calculation_method_id);


--
-- Name: shape_granularity pk_shape_granularity; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_granularity
    ADD CONSTRAINT pk_shape_granularity PRIMARY KEY (granularity_id);


--
-- Name: shape_profile_definition pk_shape_profile_definition; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_definition
    ADD CONSTRAINT pk_shape_profile_definition PRIMARY KEY (shape_profile_id);


--
-- Name: shape_profile_factor pk_shape_profile_factor; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_factor
    ADD CONSTRAINT pk_shape_profile_factor PRIMARY KEY (shape_profile_factor_id);


--
-- Name: shape_profile_spot_series pk_shape_profile_spot_series; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_spot_series
    ADD CONSTRAINT pk_shape_profile_spot_series PRIMARY KEY (shape_profile_spot_series_id);


--
-- Name: spot_option_master_data pk_spot_option_master_data; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spot_option_master_data
    ADD CONSTRAINT pk_spot_option_master_data PRIMARY KEY (id_master_data);


--
-- Name: spot_option_product pk_spot_option_product; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spot_option_product
    ADD CONSTRAINT pk_spot_option_product PRIMARY KEY (id_option_product);


--
-- Name: spread_curve pk_spread_curve; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spread_curve
    ADD CONSTRAINT pk_spread_curve PRIMARY KEY (id_spread_curve);


--
-- Name: tenor pk_tenor; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenor
    ADD CONSTRAINT pk_tenor PRIMARY KEY (tenor_id);


--
-- Name: txn_origin pk_txn_origin; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.txn_origin
    ADD CONSTRAINT pk_txn_origin PRIMARY KEY (txn_origin_id);


--
-- Name: volatility_point pk_volatility_point; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volatility_point
    ADD CONSTRAINT pk_volatility_point PRIMARY KEY (id_volatility_point);


--
-- Name: volatility_surface pk_volatility_surface; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volatility_surface
    ADD CONSTRAINT pk_volatility_surface PRIMARY KEY (id_volatility_surface);


--
-- Name: yield_curve_spread pk_yield_curve_spread; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve_spread
    ADD CONSTRAINT pk_yield_curve_spread PRIMARY KEY (id_yield_curve_spread);


--
-- Name: portfolio_master_data portfolio_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT portfolio_master_data_pkey PRIMARY KEY (id_portfolio);


--
-- Name: position_detail position_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT position_detail_pkey PRIMARY KEY (id_position_detail);


--
-- Name: position_master_data position_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT position_master_data_pkey PRIMARY KEY (id_position);


--
-- Name: position_txn_links position_txn_links_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_txn_links
    ADD CONSTRAINT position_txn_links_pkey PRIMARY KEY (pos_txn_link_id);


--
-- Name: projection_method projection_method_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.projection_method
    ADD CONSTRAINT projection_method_pkey PRIMARY KEY (projection_method_id);


--
-- Name: roll_convention roll_convention_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roll_convention
    ADD CONSTRAINT roll_convention_pkey PRIMARY KEY (id_roll_convention);


--
-- Name: sbc_status sbc_status_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sbc_status
    ADD CONSTRAINT sbc_status_code_key UNIQUE (code);


--
-- Name: sbc_status sbc_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sbc_status
    ADD CONSTRAINT sbc_status_pkey PRIMARY KEY (sbc_status_id);


--
-- Name: security_master_data security_master_data_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT security_master_data_pkey PRIMARY KEY (id_master_data);


--
-- Name: service_types service_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.service_types
    ADD CONSTRAINT service_types_pkey PRIMARY KEY (service_type_id);


--
-- Name: settlement_type settlement_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.settlement_type
    ADD CONSTRAINT settlement_type_pkey PRIMARY KEY (id_settlement_type);


--
-- Name: super_class super_class_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.super_class
    ADD CONSTRAINT super_class_pkey PRIMARY KEY (id_super_class);


--
-- Name: system_business_calendar system_business_calendar_calendar_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_business_calendar
    ADD CONSTRAINT system_business_calendar_calendar_key UNIQUE (calendar);


--
-- Name: system_business_calendar system_business_calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_business_calendar
    ADD CONSTRAINT system_business_calendar_pkey PRIMARY KEY (sbc_id);


--
-- Name: txn_accounting_phase txn_accounting_phase_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.txn_accounting_phase
    ADD CONSTRAINT txn_accounting_phase_pkey PRIMARY KEY (acct_phase_id);


--
-- Name: txn_component_types txn_component_types_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.txn_component_types
    ADD CONSTRAINT txn_component_types_code_key UNIQUE (code);


--
-- Name: txn_component_types txn_component_types_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.txn_component_types
    ADD CONSTRAINT txn_component_types_pkey PRIMARY KEY (component_type_id);


--
-- Name: txn_side txn_side_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.txn_side
    ADD CONSTRAINT txn_side_pkey PRIMARY KEY (id_txn_side);


--
-- Name: txn_status txn_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.txn_status
    ADD CONSTRAINT txn_status_pkey PRIMARY KEY (id_txn_status);


--
-- Name: type_of_interest type_of_interest_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.type_of_interest
    ADD CONSTRAINT type_of_interest_pkey PRIMARY KEY (id_type_of_interest);


--
-- Name: gl_account_slots uk_account_currency; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_account_slots
    ADD CONSTRAINT uk_account_currency UNIQUE (account, currency);


--
-- Name: account_mapping uk_account_mapping; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account_mapping
    ADD CONSTRAINT uk_account_mapping UNIQUE (mapping_key, currency);


--
-- Name: ref_rate_index uk_ref_rate_index_code_tenor; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ref_rate_index
    ADD CONSTRAINT uk_ref_rate_index_code_tenor UNIQUE (code, tenor);


--
-- Name: tenor uk_tenor_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenor
    ADD CONSTRAINT uk_tenor_code UNIQUE (code);


--
-- Name: ac_dc_bcy_link uq_ac_dc_bcy_natural_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ac_dc_bcy_link
    ADD CONSTRAINT uq_ac_dc_bcy_natural_key UNIQUE (asset_class, curve_role, currency);


--
-- Name: accumulator_fixing uq_accumulator_fixing; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_fixing
    ADD CONSTRAINT uq_accumulator_fixing UNIQUE (master_data, fixing_date);


--
-- Name: accumulator_type uq_accumulator_type_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_type
    ADD CONSTRAINT uq_accumulator_type_code UNIQUE (code);


--
-- Name: asset_class_script_mapping uq_asset_class_script_mapping; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asset_class_script_mapping
    ADD CONSTRAINT uq_asset_class_script_mapping UNIQUE NULLS NOT DISTINCT (asset_class, event_type);


--
-- Name: barrier_observation uq_barrier_observation_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.barrier_observation
    ADD CONSTRAINT uq_barrier_observation_code UNIQUE (code);


--
-- Name: bond_valuation uq_bond_valuation_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bond_valuation
    ADD CONSTRAINT uq_bond_valuation_code UNIQUE (code);


--
-- Name: calibration_profile uq_calibration_profile_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile
    ADD CONSTRAINT uq_calibration_profile_code UNIQUE (code);


--
-- Name: commodity_delivery_profile uq_delivery_profile_natural_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commodity_delivery_profile
    ADD CONSTRAINT uq_delivery_profile_natural_key UNIQUE (market, delivery_period_type, delivery_start, delivery_end);


--
-- Name: eod_status uq_eod_status_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_status
    ADD CONSTRAINT uq_eod_status_code UNIQUE (code);


--
-- Name: eod_step uq_eod_step_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_step
    ADD CONSTRAINT uq_eod_step_code UNIQUE (code);


--
-- Name: eod_step_run uq_eod_step_run; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_step_run
    ADD CONSTRAINT uq_eod_step_run UNIQUE (eod_run, step);


--
-- Name: fee_basis uq_fee_basis_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fee_basis
    ADD CONSTRAINT uq_fee_basis_code UNIQUE (code);


--
-- Name: fee_timing uq_fee_timing_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fee_timing
    ADD CONSTRAINT uq_fee_timing_code UNIQUE (code);


--
-- Name: financial_txn uq_financial_txn_origin_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT uq_financial_txn_origin_key UNIQUE (origin_key);


--
-- Name: future_product uq_future_product_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_product
    ADD CONSTRAINT uq_future_product_code UNIQUE (code);


--
-- Name: granular_curve_definition uq_gcd_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granular_curve_definition
    ADD CONSTRAINT uq_gcd_code UNIQUE (code);


--
-- Name: granular_curve_definition uq_granular_curve; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granular_curve_definition
    ADD CONSTRAINT uq_granular_curve UNIQUE (market_quote_definition_id, shape_profile_id);


--
-- Name: granular_curve_point uq_granular_curve_point; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granular_curve_point
    ADD CONSTRAINT uq_granular_curve_point UNIQUE (granular_curve_id, delivery_date);


--
-- Name: guarantee_type uq_guarantee_type_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.guarantee_type
    ADD CONSTRAINT uq_guarantee_type_code UNIQUE (code);


--
-- Name: calibration_profile_item uq_item_cp_md; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile_item
    ADD CONSTRAINT uq_item_cp_md UNIQUE (calibration_profile, master_data);


--
-- Name: knock_in_effect uq_knock_in_effect_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.knock_in_effect
    ADD CONSTRAINT uq_knock_in_effect_code UNIQUE (code);


--
-- Name: loan_fee uq_loan_fee; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee
    ADD CONSTRAINT uq_loan_fee UNIQUE (master_data, fee_type);


--
-- Name: loan_fee_type uq_loan_fee_type_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee_type
    ADD CONSTRAINT uq_loan_fee_type_code UNIQUE (code);


--
-- Name: market_data_source uq_market_data_source_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_data_source
    ADD CONSTRAINT uq_market_data_source_code UNIQUE (code);


--
-- Name: market_quote uq_market_quote; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_quote
    ADD CONSTRAINT uq_market_quote UNIQUE (market_quote_definition_id, business_date);


--
-- Name: market_quote_definition uq_mqd_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_quote_definition
    ADD CONSTRAINT uq_mqd_code UNIQUE (code);


--
-- Name: option_product uq_option_product_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_product
    ADD CONSTRAINT uq_option_product_code UNIQUE (code);


--
-- Name: power_spot_price uq_power_spot_price; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price
    ADD CONSTRAINT uq_power_spot_price UNIQUE (spot_price_definition_id, delivery_date);


--
-- Name: power_spot_price_definition uq_power_spot_price_definition; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price_definition
    ADD CONSTRAINT uq_power_spot_price_definition UNIQUE (id_country, load_type, data_source);


--
-- Name: power_spot_price_definition uq_pspd_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price_definition
    ADD CONSTRAINT uq_pspd_code UNIQUE (code);


--
-- Name: ref_rate_index uq_ref_rate_index_quote_definition; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ref_rate_index
    ADD CONSTRAINT uq_ref_rate_index_quote_definition UNIQUE (market_quote_definition_id);


--
-- Name: shape_calculation_method uq_shape_calculation_method_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_calculation_method
    ADD CONSTRAINT uq_shape_calculation_method_code UNIQUE (code);


--
-- Name: shape_granularity uq_shape_granularity_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_granularity
    ADD CONSTRAINT uq_shape_granularity_code UNIQUE (code);


--
-- Name: shape_profile_definition uq_shape_profile_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_definition
    ADD CONSTRAINT uq_shape_profile_code UNIQUE (profile_code);


--
-- Name: shape_profile_spot_series uq_shape_profile_spot_series; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_spot_series
    ADD CONSTRAINT uq_shape_profile_spot_series UNIQUE (shape_profile_id, spot_price_definition_id);


--
-- Name: spread_curve uq_spread_curve_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spread_curve
    ADD CONSTRAINT uq_spread_curve_code UNIQUE (code);


--
-- Name: txn_origin uq_txn_origin_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.txn_origin
    ADD CONSTRAINT uq_txn_origin_code UNIQUE (code);


--
-- Name: volatility_point uq_volatility_point; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volatility_point
    ADD CONSTRAINT uq_volatility_point UNIQUE NULLS NOT DISTINCT (volatility_surface, reference_date, expiry_date, strike);


--
-- Name: volatility_surface uq_volatility_surface_code; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volatility_surface
    ADD CONSTRAINT uq_volatility_surface_code UNIQUE (code);


--
-- Name: volatility_surface uq_volatility_surface_underlying; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volatility_surface
    ADD CONSTRAINT uq_volatility_surface_underlying UNIQUE (underlying);


--
-- Name: yield_curve_spread uq_ycs_bucket; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve_spread
    ADD CONSTRAINT uq_ycs_bucket UNIQUE (spread_curve, as_of_date, offset_type, offset_value);


--
-- Name: yield_curve_item yield_curve_item_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT yield_curve_item_pkey PRIMARY KEY (id_yield_curve_item);


--
-- Name: yield_curve yield_curve_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT yield_curve_pkey PRIMARY KEY (id_yield_curve);


--
-- Name: alfa_2_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX alfa_2_code ON public.country USING btree (alfa_2_code);


--
-- Name: alfa_3_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX alfa_3_code ON public.country USING btree (alfa_3_code);


--
-- Name: cash_flow_status_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX cash_flow_status_code ON public.cash_flow_status USING btree (code);


--
-- Name: commodity_type_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX commodity_type_code ON public.commodity_type USING btree (code);


--
-- Name: coupon_fdt_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX coupon_fdt_code ON public.fixing_day_type USING btree (code);


--
-- Name: coupon_pm_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX coupon_pm_code ON public.coupon_pm USING btree (code);


--
-- Name: exercise_style_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX exercise_style_code ON public.exercise_style USING btree (code);


--
-- Name: idx_account_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_account_code ON public.gl_accounts USING btree (code);


--
-- Name: idx_accrual_schedule_type_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_accrual_schedule_type_code ON public.accrual_schedule_type USING btree (code);


--
-- Name: idx_acct_phase_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_acct_phase_code ON public.txn_accounting_phase USING btree (code);


--
-- Name: idx_accumulator_fixing_settlement; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_accumulator_fixing_settlement ON public.accumulator_fixing USING btree (settlement_date);


--
-- Name: idx_accumulator_master_data_underlying; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_accumulator_master_data_underlying ON public.accumulator_master_data USING btree (underlying);


--
-- Name: idx_amortization_schedule_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_amortization_schedule_code ON public.amortization_schedule USING btree (code);


--
-- Name: idx_asset_class_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_asset_class_code ON public.asset_class USING btree (code);


--
-- Name: idx_calendar_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_calendar_code ON public.calendar USING btree (code);


--
-- Name: idx_compounding_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_compounding_code ON public.compounding USING btree (code);


--
-- Name: idx_counterparty_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_counterparty_code ON public.counterparty USING btree (code);


--
-- Name: idx_counterparty_type_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_counterparty_type_code ON public.counterparty_type USING btree (code);


--
-- Name: idx_country_numeric_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_country_numeric_code ON public.country USING btree (country_numeric_code);


--
-- Name: idx_ctp_role; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_ctp_role ON public.counterparty_role_mapping USING btree (counterparty, ctp_role);


--
-- Name: idx_currency_numeric_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_currency_numeric_code ON public.currency USING btree (currency_numeric_code);


--
-- Name: idx_currpair_bcy_ccy; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_currpair_bcy_ccy ON public.currpair_master_data USING btree (bcy, ccy);


--
-- Name: idx_curve_role_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_curve_role_code ON public.curve_role USING btree (code);


--
-- Name: idx_daycount_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_daycount_code ON public.daycount USING btree (code);


--
-- Name: idx_delivery_profile_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_delivery_profile_parent ON public.commodity_delivery_profile USING btree (parent_profile);


--
-- Name: idx_delivery_profile_range; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_delivery_profile_range ON public.commodity_delivery_profile USING btree (delivery_start, delivery_end);


--
-- Name: idx_event_key; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_event_key ON public.accounting_events USING btree (event_key);


--
-- Name: idx_event_status_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_event_status_code ON public.accounting_event_status USING btree (code);


--
-- Name: idx_event_status_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_event_status_created ON public.accounting_events USING btree (event_status, created_at);


--
-- Name: idx_event_status_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_event_status_type ON public.accounting_events USING btree (event_status, event_type);


--
-- Name: idx_event_types_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_event_types_code ON public.accounting_event_types USING btree (code);


--
-- Name: idx_financial_txn_components_txn; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_financial_txn_components_txn ON public.financial_txn_components USING btree (financial_txn);


--
-- Name: idx_forex_bcy_ccy; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_forex_bcy_ccy ON public.forex_master_data USING btree (bcy, ccy);


--
-- Name: idx_form_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_form_code ON public.form USING btree (code);


--
-- Name: idx_frequency_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_frequency_code ON public.frequency USING btree (code);


--
-- Name: idx_future_master_data_isin; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_future_master_data_isin ON public.future_master_data USING btree (isin);


--
-- Name: idx_future_option_master_data_underlying; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_future_option_master_data_underlying ON public.future_option_master_data USING btree (underlying);


--
-- Name: idx_instrument_quote_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_instrument_quote_code ON public.instrument_quote USING btree (code);


--
-- Name: idx_instrument_quote_hist_iqud; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_instrument_quote_hist_iqud ON public.instrument_quote_hist USING btree (instrument_quote, update_date);


--
-- Name: idx_iso_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_iso_code ON public.currency USING btree (iso_code);


--
-- Name: idx_master_data_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_master_data_code ON public.master_data USING btree (code);


--
-- Name: idx_md_ed; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_md_ed ON public.cash_flow_item USING btree (master_data, end_date);


--
-- Name: idx_md_sdr; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_md_sdr ON public.cash_flow_reset USING btree (master_data, start_date_reset);


--
-- Name: idx_node_type_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_node_type_code ON public.node_type USING btree (code);


--
-- Name: idx_option_master_data_isin; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_option_master_data_isin ON public.option_master_data USING btree (isin);


--
-- Name: idx_option_master_data_product; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_option_master_data_product ON public.option_master_data USING btree (option_product);


--
-- Name: idx_pm_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_pm_code ON public.projection_method USING btree (code);


--
-- Name: idx_portfolio_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_portfolio_code ON public.portfolio_master_data USING btree (code);


--
-- Name: idx_position_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_position_code ON public.position_master_data USING btree (code);


--
-- Name: idx_position_detail_pmc; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_position_detail_pmc ON public.position_detail USING btree (position_md, master_data, counterparty);


--
-- Name: idx_position_txn_links_detail; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_position_txn_links_detail ON public.position_txn_links USING btree (position_detail, pos_txn_link_id);


--
-- Name: idx_roll_convention_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_roll_convention_code ON public.roll_convention USING btree (code);


--
-- Name: idx_security_master_data_isin; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_security_master_data_isin ON public.security_master_data USING btree (isin);


--
-- Name: idx_settlement_type_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_settlement_type_code ON public.settlement_type USING btree (code);


--
-- Name: idx_source; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_source ON public.accounting_events USING btree (source_type, source_id);


--
-- Name: idx_source_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_source_code ON public.event_source_types USING btree (code);


--
-- Name: idx_spot_option_master_data_underlying; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_spot_option_master_data_underlying ON public.spot_option_master_data USING btree (underlying);


--
-- Name: idx_super_class_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_super_class_code ON public.super_class USING btree (code);


--
-- Name: idx_txn_side_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_txn_side_code ON public.txn_side USING btree (code);


--
-- Name: idx_txn_status_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_txn_status_code ON public.txn_status USING btree (code);


--
-- Name: idx_type_of_interest_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_type_of_interest_code ON public.type_of_interest USING btree (code);


--
-- Name: idx_volatility_point_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_volatility_point_date ON public.volatility_point USING btree (volatility_surface, reference_date);


--
-- Name: idx_yield_curve_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_yield_curve_code ON public.yield_curve USING btree (code);


--
-- Name: idx_yield_curve_item_ric; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_yield_curve_item_ric ON public.yield_curve_item USING btree (ric, yield_curve);


--
-- Name: ix_ycs_curve_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX ix_ycs_curve_date ON public.yield_curve_spread USING btree (spread_curve, as_of_date);


--
-- Name: load_dpt_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX load_dpt_code ON public.delivery_period_type USING btree (code);


--
-- Name: load_type_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX load_type_code ON public.load_type USING btree (code);


--
-- Name: option_type_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX option_type_code ON public.option_type USING btree (code);


--
-- Name: premium_style_code; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX premium_style_code ON public.premium_style USING btree (code);


--
-- Name: uq_eod_run_completed; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_eod_run_completed ON public.eod_run USING btree (business_date) WHERE (status = ANY (ARRAY[2, 3]));


--
-- Name: uq_eod_run_running; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_eod_run_running ON public.eod_run USING btree (status) WHERE (status = 1);


--
-- Name: ux_deliverable_bonds_isin; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX ux_deliverable_bonds_isin ON public.deliverable_bonds USING btree (master_data, isin);


--
-- Name: financial_txn trg_financial_txn_ref_id; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_financial_txn_ref_id BEFORE INSERT OR UPDATE ON public.financial_txn FOR EACH ROW EXECUTE FUNCTION public.fn_manage_ref_id();


--
-- Name: financial_txn trg_sync_financial_txn_phase; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_sync_financial_txn_phase AFTER UPDATE OF txn_acct_phase ON public.financial_txn FOR EACH ROW WHEN ((old.txn_acct_phase IS DISTINCT FROM new.txn_acct_phase)) EXECUTE FUNCTION public.update_position_txn_links_phase();


--
-- Name: cash_flow_item cash_flow_status; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT cash_flow_status FOREIGN KEY (cash_flow_status) REFERENCES public.cash_flow_status(cash_flow_status_id);


--
-- Name: ac_dc_bcy_link fk_ac_dc_bcy_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ac_dc_bcy_link
    ADD CONSTRAINT fk_ac_dc_bcy_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- Name: ac_dc_bcy_link fk_ac_dc_bcy_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ac_dc_bcy_link
    ADD CONSTRAINT fk_ac_dc_bcy_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: ac_dc_bcy_link fk_ac_dc_bcy_curve_role; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ac_dc_bcy_link
    ADD CONSTRAINT fk_ac_dc_bcy_curve_role FOREIGN KEY (curve_role) REFERENCES public.curve_role(curve_role_id);


--
-- Name: ac_dc_bcy_link fk_ac_dc_bcy_spread_curve; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ac_dc_bcy_link
    ADD CONSTRAINT fk_ac_dc_bcy_spread_curve FOREIGN KEY (spread_curve) REFERENCES public.spread_curve(id_spread_curve);


--
-- Name: ac_dc_bcy_link fk_ac_dc_bcy_yield_curve; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ac_dc_bcy_link
    ADD CONSTRAINT fk_ac_dc_bcy_yield_curve FOREIGN KEY (yield_curve) REFERENCES public.yield_curve(id_yield_curve);


--
-- Name: accounting_event_mtm fk_accounting_event_mtm_event; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_event_mtm
    ADD CONSTRAINT fk_accounting_event_mtm_event FOREIGN KEY (event_id) REFERENCES public.accounting_events(event_id) ON DELETE CASCADE;


--
-- Name: accumulator_fixing fk_accumulator_fixing_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_fixing
    ADD CONSTRAINT fk_accumulator_fixing_master_data FOREIGN KEY (master_data) REFERENCES public.accumulator_master_data(id_master_data) ON DELETE CASCADE;


--
-- Name: accumulator_master_data fk_accumulator_master_data_guarantee_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_guarantee_type FOREIGN KEY (guarantee_type) REFERENCES public.guarantee_type(guarantee_type_id);


--
-- Name: accumulator_master_data fk_accumulator_master_data_ki_effect; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_ki_effect FOREIGN KEY (ki_effect) REFERENCES public.knock_in_effect(knock_in_effect_id);


--
-- Name: accumulator_master_data fk_accumulator_master_data_ko_observation; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_ko_observation FOREIGN KEY (ko_observation) REFERENCES public.barrier_observation(barrier_observation_id);


--
-- Name: accumulator_master_data fk_accumulator_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: accumulator_master_data fk_accumulator_master_data_settlement_frequency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_settlement_frequency FOREIGN KEY (settlement_frequency) REFERENCES public.frequency(id_frequency);


--
-- Name: accumulator_master_data fk_accumulator_master_data_settlement_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_settlement_type FOREIGN KEY (settlement_type) REFERENCES public.settlement_type(id_settlement_type);


--
-- Name: accumulator_master_data fk_accumulator_master_data_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_type FOREIGN KEY (accumulator_type) REFERENCES public.accumulator_type(accumulator_type_id);


--
-- Name: accumulator_master_data fk_accumulator_master_data_underlying; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accumulator_master_data
    ADD CONSTRAINT fk_accumulator_master_data_underlying FOREIGN KEY (underlying) REFERENCES public.master_data(id_master_data);


--
-- Name: accounting_event_accruals fk_aea_daycount; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_event_accruals
    ADD CONSTRAINT fk_aea_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: account_mapping fk_am_account; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account_mapping
    ADD CONSTRAINT fk_am_account FOREIGN KEY (gl_account) REFERENCES public.gl_accounts(account_id);


--
-- Name: account_mapping fk_am_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.account_mapping
    ADD CONSTRAINT fk_am_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: master_data fk_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- Name: asset_class_script_mapping fk_asset_class_script_mapping_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asset_class_script_mapping
    ADD CONSTRAINT fk_asset_class_script_mapping_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- Name: asset_class_script_mapping fk_asset_class_script_mapping_event_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asset_class_script_mapping
    ADD CONSTRAINT fk_asset_class_script_mapping_event_type FOREIGN KEY (event_type) REFERENCES public.accounting_event_types(event_type_id);


--
-- Name: gl_accounts fk_balance; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_balance FOREIGN KEY (balance) REFERENCES public.normal_balances(balance_id);


--
-- Name: currpair_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- Name: forex_master_data fk_bcy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_bcy FOREIGN KEY (bcy) REFERENCES public.currency(id_currency);


--
-- Name: bond_future_master_data fk_bond_future_master_data_product; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bond_future_master_data
    ADD CONSTRAINT fk_bond_future_master_data_product FOREIGN KEY (future_product) REFERENCES public.bond_future_product(id_future_product);


--
-- Name: bond_future_product fk_bond_future_product_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bond_future_product
    ADD CONSTRAINT fk_bond_future_product_parent FOREIGN KEY (id_future_product) REFERENCES public.future_product(id_future_product);


--
-- Name: country fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: currency fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: holiday fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.holiday
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: system_business_calendar fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_business_calendar
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: yield_curve fk_calendar; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_calendar FOREIGN KEY (calendar) REFERENCES public.calendar(id_calendar);


--
-- Name: cash_flow_item fk_cash_flow_item_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_item
    ADD CONSTRAINT fk_cash_flow_item_master_data FOREIGN KEY (master_data) REFERENCES public.cash_flow_master_data(id_master_data);


--
-- Name: cash_flow_master_data fk_cash_flow_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cash_flow_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: cash_flow_reset fk_cash_flow_reset_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_reset
    ADD CONSTRAINT fk_cash_flow_reset_master_data FOREIGN KEY (master_data) REFERENCES public.cash_flow_master_data(id_master_data);


--
-- Name: currpair_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.currpair_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- Name: forex_master_data fk_ccy; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.forex_master_data
    ADD CONSTRAINT fk_ccy FOREIGN KEY (ccy) REFERENCES public.currency(id_currency);


--
-- Name: cash_flow_master_data fk_cfmd_accrual_daycount; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_accrual_daycount FOREIGN KEY (accrual_daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: cash_flow_master_data fk_cfmd_accrual_schedule_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_accrual_schedule_type FOREIGN KEY (accrual_schedule_type) REFERENCES public.accrual_schedule_type(id_accrual_schedule_type);


--
-- Name: cash_flow_master_data fk_cfmd_amortization_schedule; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_amortization_schedule FOREIGN KEY (amortization_schedule) REFERENCES public.amortization_schedule(id_amortization_schedule);


--
-- Name: cash_flow_master_data fk_cfmd_compounding; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_compounding FOREIGN KEY (compounding) REFERENCES public.compounding(id_compounding);


--
-- Name: cash_flow_master_data fk_cfmd_daycount; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: cash_flow_master_data fk_cfmd_form; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_form FOREIGN KEY (form) REFERENCES public.form(id_form);


--
-- Name: cash_flow_master_data fk_cfmd_frequency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_frequency FOREIGN KEY (frequency) REFERENCES public.frequency(id_frequency);


--
-- Name: cash_flow_master_data fk_cfmd_roll_convention; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_roll_convention FOREIGN KEY (roll_convention) REFERENCES public.roll_convention(id_roll_convention);


--
-- Name: cash_flow_master_data fk_cfmd_type_of_interest; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cash_flow_master_data
    ADD CONSTRAINT fk_cfmd_type_of_interest FOREIGN KEY (type_of_interest) REFERENCES public.type_of_interest(id_type_of_interest);


--
-- Name: cmd_future_master_data fk_cmd_future_master_data_product; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cmd_future_master_data
    ADD CONSTRAINT fk_cmd_future_master_data_product FOREIGN KEY (future_product) REFERENCES public.cmd_future_product(id_future_product);


--
-- Name: cmd_future_product fk_cmd_future_product_commodity_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cmd_future_product
    ADD CONSTRAINT fk_cmd_future_product_commodity_type FOREIGN KEY (commodity_type) REFERENCES public.commodity_type(commodity_type_id);


--
-- Name: cmd_future_product fk_cmd_future_product_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cmd_future_product
    ADD CONSTRAINT fk_cmd_future_product_parent FOREIGN KEY (id_future_product) REFERENCES public.future_product(id_future_product);


--
-- Name: financial_txn_components fk_component_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn_components
    ADD CONSTRAINT fk_component_type FOREIGN KEY (component_type) REFERENCES public.txn_component_types(component_type_id);


--
-- Name: financial_txn fk_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty);


--
-- Name: position_detail fk_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty);


--
-- Name: counterparty fk_country; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- Name: issuer fk_country; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.issuer
    ADD CONSTRAINT fk_country FOREIGN KEY (country) REFERENCES public.country(id_country);


--
-- Name: flt_security_master_data fk_coupon_pm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_security_master_data
    ADD CONSTRAINT fk_coupon_pm FOREIGN KEY (coupon_pm) REFERENCES public.coupon_pm(coupon_pm_id);


--
-- Name: calibration_profile_item fk_cpi_calibration_profile; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile_item
    ADD CONSTRAINT fk_cpi_calibration_profile FOREIGN KEY (calibration_profile) REFERENCES public.calibration_profile(calibration_profile_id);


--
-- Name: calibration_profile fk_cpi_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile
    ADD CONSTRAINT fk_cpi_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: calibration_profile_item fk_cpi_instrument_quote; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile_item
    ADD CONSTRAINT fk_cpi_instrument_quote FOREIGN KEY (instrument_quote) REFERENCES public.instrument_quote(id_instrument_quote);


--
-- Name: calibration_profile_item fk_cpi_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.calibration_profile_item
    ADD CONSTRAINT fk_cpi_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: counterparty fk_ctp_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty
    ADD CONSTRAINT fk_ctp_type FOREIGN KEY (ctp_type) REFERENCES public.counterparty_type(id_counterparty_type);


--
-- Name: country fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: financial_txn_components fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn_components
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: portfolio_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.portfolio_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: position_master_data fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: system_business_calendar fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_business_calendar
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: yield_curve fk_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve
    ADD CONSTRAINT fk_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: currency fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.currency
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: yield_curve_item fk_daycount; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: deliverable_bonds fk_deliverable_bonds_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.deliverable_bonds
    ADD CONSTRAINT fk_deliverable_bonds_master_data FOREIGN KEY (master_data) REFERENCES public.bond_future_master_data(id_master_data);


--
-- Name: commodity_delivery_profile fk_delivery_profile_mdp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commodity_delivery_profile
    ADD CONSTRAINT fk_delivery_profile_mdp FOREIGN KEY (market_quote_definition_id) REFERENCES public.market_quote_definition(market_quote_definition_id);


--
-- Name: commodity_delivery_profile fk_delivery_profile_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commodity_delivery_profile
    ADD CONSTRAINT fk_delivery_profile_parent FOREIGN KEY (parent_profile) REFERENCES public.commodity_delivery_profile(id_master_data);


--
-- Name: commodity_delivery_profile fk_delivery_profile_period_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.commodity_delivery_profile
    ADD CONSTRAINT fk_delivery_profile_period_type FOREIGN KEY (delivery_period_type) REFERENCES public.delivery_period_type(delivery_period_type_id);


--
-- Name: eod_run fk_eod_run_status; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_run
    ADD CONSTRAINT fk_eod_run_status FOREIGN KEY (status) REFERENCES public.eod_status(eod_status_id);


--
-- Name: eod_step_run fk_eod_step_run_run; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_step_run
    ADD CONSTRAINT fk_eod_step_run_run FOREIGN KEY (eod_run) REFERENCES public.eod_run(eod_run_id);


--
-- Name: eod_step_run fk_eod_step_run_status; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_step_run
    ADD CONSTRAINT fk_eod_step_run_status FOREIGN KEY (status) REFERENCES public.eod_status(eod_status_id);


--
-- Name: eod_step_run fk_eod_step_run_step; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.eod_step_run
    ADD CONSTRAINT fk_eod_step_run_step FOREIGN KEY (step) REFERENCES public.eod_step(eod_step_id);


--
-- Name: accounting_events fk_event_status; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_events
    ADD CONSTRAINT fk_event_status FOREIGN KEY (event_status) REFERENCES public.accounting_event_status(event_status_id);


--
-- Name: accounting_events fk_event_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_events
    ADD CONSTRAINT fk_event_type FOREIGN KEY (event_type) REFERENCES public.accounting_event_types(event_type_id);


--
-- Name: financial_txn_components fk_financial_txn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn_components
    ADD CONSTRAINT fk_financial_txn FOREIGN KEY (financial_txn) REFERENCES public.financial_txn(id_financial_txn) ON DELETE CASCADE;


--
-- Name: flt_loan_master_data fk_flt_loan_coupon_pm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_loan_master_data
    ADD CONSTRAINT fk_flt_loan_coupon_pm FOREIGN KEY (coupon_pm) REFERENCES public.coupon_pm(coupon_pm_id);


--
-- Name: flt_loan_master_data fk_flt_loan_fixing_day_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_loan_master_data
    ADD CONSTRAINT fk_flt_loan_fixing_day_type FOREIGN KEY (fixing_day_type) REFERENCES public.fixing_day_type(fixing_day_type_id);


--
-- Name: flt_loan_master_data fk_flt_loan_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_loan_master_data
    ADD CONSTRAINT fk_flt_loan_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.loan_master_data(id_master_data);


--
-- Name: flt_loan_master_data fk_flt_loan_projection_index; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_loan_master_data
    ADD CONSTRAINT fk_flt_loan_projection_index FOREIGN KEY (projection_index) REFERENCES public.ref_rate_index(ref_rate_index_id);


--
-- Name: future_option_master_data fk_future_option_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_option_master_data
    ADD CONSTRAINT fk_future_option_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.option_master_data(id_master_data);


--
-- Name: future_option_master_data fk_future_option_master_data_underlying; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_option_master_data
    ADD CONSTRAINT fk_future_option_master_data_underlying FOREIGN KEY (underlying) REFERENCES public.future_master_data(id_master_data);


--
-- Name: future_option_product fk_future_option_product_future_product; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_option_product
    ADD CONSTRAINT fk_future_option_product_future_product FOREIGN KEY (future_product) REFERENCES public.future_product(id_future_product);


--
-- Name: future_option_product fk_future_option_product_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_option_product
    ADD CONSTRAINT fk_future_option_product_parent FOREIGN KEY (id_option_product) REFERENCES public.option_product(id_option_product);


--
-- Name: future_product fk_future_product_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_product
    ADD CONSTRAINT fk_future_product_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- Name: future_product fk_future_product_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_product
    ADD CONSTRAINT fk_future_product_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: future_product fk_future_product_settlement_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.future_product
    ADD CONSTRAINT fk_future_product_settlement_type FOREIGN KEY (settlement_type) REFERENCES public.settlement_type(id_settlement_type);


--
-- Name: fx_future_master_data fk_fx_future_master_data_product; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fx_future_master_data
    ADD CONSTRAINT fk_fx_future_master_data_product FOREIGN KEY (future_product) REFERENCES public.fx_future_product(id_future_product);


--
-- Name: fx_future_product fk_fx_future_product_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fx_future_product
    ADD CONSTRAINT fk_fx_future_product_parent FOREIGN KEY (id_future_product) REFERENCES public.future_product(id_future_product);


--
-- Name: fx_future_product fk_fx_future_product_underlying; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fx_future_product
    ADD CONSTRAINT fk_fx_future_product_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- Name: granular_curve_definition fk_gcd_quote_definition; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granular_curve_definition
    ADD CONSTRAINT fk_gcd_quote_definition FOREIGN KEY (market_quote_definition_id) REFERENCES public.market_quote_definition(market_quote_definition_id);


--
-- Name: granular_curve_definition fk_gcd_shape_profile; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.granular_curve_definition
    ADD CONSTRAINT fk_gcd_shape_profile FOREIGN KEY (shape_profile_id) REFERENCES public.shape_profile_definition(shape_profile_id);


--
-- Name: instrument_quote_hist fk_instrument_quote; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_instrument_quote FOREIGN KEY (instrument_quote) REFERENCES public.instrument_quote(id_instrument_quote);


--
-- Name: security_master_data fk_issuer; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT fk_issuer FOREIGN KEY (issuer) REFERENCES public.issuer(id_issuer);


--
-- Name: journal_entries fk_je_event; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entries
    ADD CONSTRAINT fk_je_event FOREIGN KEY (accounting_event) REFERENCES public.accounting_events(event_id);


--
-- Name: journal_entries fk_je_reversal; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entries
    ADD CONSTRAINT fk_je_reversal FOREIGN KEY (reversal_of) REFERENCES public.journal_entries(journal_entry_id);


--
-- Name: journal_entries fk_je_status; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entries
    ADD CONSTRAINT fk_je_status FOREIGN KEY (entry_status) REFERENCES public.journal_entry_status(entry_status_id);


--
-- Name: journal_entries fk_je_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entries
    ADD CONSTRAINT fk_je_type FOREIGN KEY (entry_type) REFERENCES public.journal_entry_types(entry_type_id);


--
-- Name: journal_entry_lines fk_jel_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entry_lines
    ADD CONSTRAINT fk_jel_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: journal_entry_lines fk_jel_entry; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entry_lines
    ADD CONSTRAINT fk_jel_entry FOREIGN KEY (journal_entry) REFERENCES public.journal_entries(journal_entry_id);


--
-- Name: journal_entry_lines fk_jel_slot; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.journal_entry_lines
    ADD CONSTRAINT fk_jel_slot FOREIGN KEY (account_slot) REFERENCES public.gl_account_slots(account_slot_id);


--
-- Name: ledger_balances fk_ledger_account; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ledger_balances
    ADD CONSTRAINT fk_ledger_account FOREIGN KEY (gl_account) REFERENCES public.gl_accounts(account_id);


--
-- Name: ledger_balances fk_ledger_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ledger_balances
    ADD CONSTRAINT fk_ledger_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: position_txn_links fk_link_acct_phase; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_txn_links
    ADD CONSTRAINT fk_link_acct_phase FOREIGN KEY (txn_acct_phase) REFERENCES public.txn_accounting_phase(acct_phase_id);


--
-- Name: position_txn_links fk_link_pos; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_txn_links
    ADD CONSTRAINT fk_link_pos FOREIGN KEY (position_detail) REFERENCES public.position_detail(id_position_detail);


--
-- Name: position_txn_links fk_link_txn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_txn_links
    ADD CONSTRAINT fk_link_txn FOREIGN KEY (financial_txn) REFERENCES public.financial_txn(id_financial_txn);


--
-- Name: loan_fee fk_loan_fee_basis; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee
    ADD CONSTRAINT fk_loan_fee_basis FOREIGN KEY (fee_basis) REFERENCES public.fee_basis(fee_basis_id);


--
-- Name: loan_fee fk_loan_fee_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee
    ADD CONSTRAINT fk_loan_fee_master_data FOREIGN KEY (master_data) REFERENCES public.loan_master_data(id_master_data) ON DELETE CASCADE;


--
-- Name: loan_fee fk_loan_fee_timing; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee
    ADD CONSTRAINT fk_loan_fee_timing FOREIGN KEY (fee_timing) REFERENCES public.fee_timing(fee_timing_id);


--
-- Name: loan_fee fk_loan_fee_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_fee
    ADD CONSTRAINT fk_loan_fee_type FOREIGN KEY (fee_type) REFERENCES public.loan_fee_type(loan_fee_type_id);


--
-- Name: loan_master_data fk_loan_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loan_master_data
    ADD CONSTRAINT fk_loan_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.cash_flow_master_data(id_master_data);


--
-- Name: counterparty_role_mapping fk_mapping_counterparty; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty_role_mapping
    ADD CONSTRAINT fk_mapping_counterparty FOREIGN KEY (counterparty) REFERENCES public.counterparty(id_counterparty) ON DELETE CASCADE;


--
-- Name: counterparty_role_mapping fk_mapping_role; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.counterparty_role_mapping
    ADD CONSTRAINT fk_mapping_role FOREIGN KEY (ctp_role) REFERENCES public.counterparty_roles(role_id);


--
-- Name: market_quote fk_market_quote_definition; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_quote
    ADD CONSTRAINT fk_market_quote_definition FOREIGN KEY (market_quote_definition_id) REFERENCES public.market_quote_definition(market_quote_definition_id);


--
-- Name: financial_txn fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: instrument_quote fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.instrument_quote
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: instrument_quote_hist fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.instrument_quote_hist
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: instrument_valuation fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.instrument_valuation
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: position_detail fk_master_data; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_master_data FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: mm_future_master_data fk_mm_future_master_data_product; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mm_future_master_data
    ADD CONSTRAINT fk_mm_future_master_data_product FOREIGN KEY (future_product) REFERENCES public.mm_future_product(id_future_product);


--
-- Name: mm_future_product fk_mm_future_product_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mm_future_product
    ADD CONSTRAINT fk_mm_future_product_parent FOREIGN KEY (id_future_product) REFERENCES public.future_product(id_future_product);


--
-- Name: mm_future_product fk_mm_future_product_underlying; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.mm_future_product
    ADD CONSTRAINT fk_mm_future_product_underlying FOREIGN KEY (underlying) REFERENCES public.forex_master_data(id_master_data);


--
-- Name: market_quote_definition fk_mqd_country; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_quote_definition
    ADD CONSTRAINT fk_mqd_country FOREIGN KEY (id_country) REFERENCES public.country(id_country);


--
-- Name: market_quote_definition fk_mqd_data_source; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.market_quote_definition
    ADD CONSTRAINT fk_mqd_data_source FOREIGN KEY (data_source) REFERENCES public.market_data_source(data_source_id);


--
-- Name: gl_accounts fk_nature; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_nature FOREIGN KEY (nature) REFERENCES public.account_natures(nature_id);


--
-- Name: yield_curve_item fk_node_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_node_type FOREIGN KEY (node_type) REFERENCES public.node_type(node_type_id);


--
-- Name: option_master_data fk_option_master_data_option_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_master_data
    ADD CONSTRAINT fk_option_master_data_option_type FOREIGN KEY (option_type) REFERENCES public.option_type(option_type_id);


--
-- Name: option_master_data fk_option_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_master_data
    ADD CONSTRAINT fk_option_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: option_master_data fk_option_master_data_product; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_master_data
    ADD CONSTRAINT fk_option_master_data_product FOREIGN KEY (option_product) REFERENCES public.option_product(id_option_product);


--
-- Name: option_product fk_option_product_asset_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_product
    ADD CONSTRAINT fk_option_product_asset_class FOREIGN KEY (asset_class) REFERENCES public.asset_class(id_asset_class);


--
-- Name: option_product fk_option_product_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_product
    ADD CONSTRAINT fk_option_product_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: option_product fk_option_product_exercise_style; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_product
    ADD CONSTRAINT fk_option_product_exercise_style FOREIGN KEY (exercise_style) REFERENCES public.exercise_style(exercise_style_id);


--
-- Name: option_product fk_option_product_premium_style; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_product
    ADD CONSTRAINT fk_option_product_premium_style FOREIGN KEY (premium_style) REFERENCES public.premium_style(premium_style_id);


--
-- Name: option_product fk_option_product_settlement_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.option_product
    ADD CONSTRAINT fk_option_product_settlement_type FOREIGN KEY (settlement_type) REFERENCES public.settlement_type(id_settlement_type);


--
-- Name: gl_accounts fk_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_parent FOREIGN KEY (parent) REFERENCES public.gl_accounts(account_id);


--
-- Name: position_master_data fk_portfolio; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_portfolio FOREIGN KEY (portfolio) REFERENCES public.portfolio_master_data(id_portfolio);


--
-- Name: position_master_data fk_position_bond_valuation; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_master_data
    ADD CONSTRAINT fk_position_bond_valuation FOREIGN KEY (bond_valuation) REFERENCES public.bond_valuation(bond_valuation_id);


--
-- Name: financial_txn fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- Name: position_detail fk_position_md; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.position_detail
    ADD CONSTRAINT fk_position_md FOREIGN KEY (position_md) REFERENCES public.position_master_data(id_position);


--
-- Name: power_future_master_data fk_power_future_master_data_parent_table; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_future_master_data
    ADD CONSTRAINT fk_power_future_master_data_parent_table FOREIGN KEY (id_master_data) REFERENCES public.cmd_future_master_data(id_master_data);


--
-- Name: power_future_product fk_power_future_product_load_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_future_product
    ADD CONSTRAINT fk_power_future_product_load_type FOREIGN KEY (load_type) REFERENCES public.load_type(load_type_id);


--
-- Name: power_future_product fk_power_future_product_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_future_product
    ADD CONSTRAINT fk_power_future_product_parent FOREIGN KEY (id_future_product) REFERENCES public.cmd_future_product(id_future_product);


--
-- Name: power_spot_price fk_power_spot_definition; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price
    ADD CONSTRAINT fk_power_spot_definition FOREIGN KEY (spot_price_definition_id) REFERENCES public.power_spot_price_definition(spot_price_definition_id);


--
-- Name: flt_security_master_data fk_projection_index; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flt_security_master_data
    ADD CONSTRAINT fk_projection_index FOREIGN KEY (projection_index) REFERENCES public.ref_rate_index(ref_rate_index_id);


--
-- Name: power_spot_price_definition fk_pspd_country; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price_definition
    ADD CONSTRAINT fk_pspd_country FOREIGN KEY (id_country) REFERENCES public.country(id_country);


--
-- Name: power_spot_price_definition fk_pspd_data_source; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price_definition
    ADD CONSTRAINT fk_pspd_data_source FOREIGN KEY (data_source) REFERENCES public.market_data_source(data_source_id);


--
-- Name: power_spot_price_definition fk_pspd_load_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.power_spot_price_definition
    ADD CONSTRAINT fk_pspd_load_type FOREIGN KEY (load_type) REFERENCES public.load_type(load_type_id);


--
-- Name: ref_rate_index fk_ref_rate_index_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ref_rate_index
    ADD CONSTRAINT fk_ref_rate_index_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: ref_rate_index fk_ref_rate_index_daycount; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ref_rate_index
    ADD CONSTRAINT fk_ref_rate_index_daycount FOREIGN KEY (daycount) REFERENCES public.daycount(id_daycount);


--
-- Name: ref_rate_index fk_ref_rate_index_quote_definition; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ref_rate_index
    ADD CONSTRAINT fk_ref_rate_index_quote_definition FOREIGN KEY (market_quote_definition_id) REFERENCES public.market_quote_definition(market_quote_definition_id);


--
-- Name: ref_rate_index fk_ref_rate_index_tenor; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ref_rate_index
    ADD CONSTRAINT fk_ref_rate_index_tenor FOREIGN KEY (tenor) REFERENCES public.tenor(tenor_id);


--
-- Name: broker_instrument_rules fk_rule_broker; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broker_instrument_rules
    ADD CONSTRAINT fk_rule_broker FOREIGN KEY (broker) REFERENCES public.counterparty(id_counterparty);


--
-- Name: broker_instrument_rules fk_rule_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broker_instrument_rules
    ADD CONSTRAINT fk_rule_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: broker_instrument_rules fk_rule_instrument; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.broker_instrument_rules
    ADD CONSTRAINT fk_rule_instrument FOREIGN KEY (master_data) REFERENCES public.master_data(id_master_data);


--
-- Name: spread_curve fk_sc_base_curve; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spread_curve
    ADD CONSTRAINT fk_sc_base_curve FOREIGN KEY (base_curve) REFERENCES public.yield_curve(id_yield_curve);


--
-- Name: spread_curve fk_sc_profile; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spread_curve
    ADD CONSTRAINT fk_sc_profile FOREIGN KEY (calibration_profile) REFERENCES public.calibration_profile(calibration_profile_id);


--
-- Name: security_master_data fk_security_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.security_master_data
    ADD CONSTRAINT fk_security_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.cash_flow_master_data(id_master_data);


--
-- Name: descriptors fk_service_types; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.descriptors
    ADD CONSTRAINT fk_service_types FOREIGN KEY (service_type) REFERENCES public.service_types(service_type_id);


--
-- Name: shape_profile_definition fk_shape_profile_calc_method; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_definition
    ADD CONSTRAINT fk_shape_profile_calc_method FOREIGN KEY (calculation_method) REFERENCES public.shape_calculation_method(calculation_method_id);


--
-- Name: shape_profile_definition fk_shape_profile_country; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_definition
    ADD CONSTRAINT fk_shape_profile_country FOREIGN KEY (id_country) REFERENCES public.country(id_country);


--
-- Name: shape_profile_definition fk_shape_profile_data_source; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_definition
    ADD CONSTRAINT fk_shape_profile_data_source FOREIGN KEY (data_source) REFERENCES public.market_data_source(data_source_id);


--
-- Name: shape_profile_factor fk_shape_profile_factor_profile; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_factor
    ADD CONSTRAINT fk_shape_profile_factor_profile FOREIGN KEY (shape_profile_id) REFERENCES public.shape_profile_definition(shape_profile_id);


--
-- Name: shape_profile_definition fk_shape_profile_granularity; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_definition
    ADD CONSTRAINT fk_shape_profile_granularity FOREIGN KEY (granularity) REFERENCES public.shape_granularity(granularity_id);


--
-- Name: shape_profile_definition fk_shape_profile_load_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_definition
    ADD CONSTRAINT fk_shape_profile_load_type FOREIGN KEY (load_type) REFERENCES public.load_type(load_type_id);


--
-- Name: gl_account_slots fk_slot_account; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_account_slots
    ADD CONSTRAINT fk_slot_account FOREIGN KEY (account) REFERENCES public.gl_accounts(account_id);


--
-- Name: gl_account_slots fk_slot_currency; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_account_slots
    ADD CONSTRAINT fk_slot_currency FOREIGN KEY (currency) REFERENCES public.currency(id_currency);


--
-- Name: accounting_events fk_source_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.accounting_events
    ADD CONSTRAINT fk_source_type FOREIGN KEY (source_type) REFERENCES public.event_source_types(source_type_id);


--
-- Name: spot_option_master_data fk_spot_option_master_data_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spot_option_master_data
    ADD CONSTRAINT fk_spot_option_master_data_parent FOREIGN KEY (id_master_data) REFERENCES public.option_master_data(id_master_data);


--
-- Name: spot_option_master_data fk_spot_option_master_data_underlying; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spot_option_master_data
    ADD CONSTRAINT fk_spot_option_master_data_underlying FOREIGN KEY (underlying) REFERENCES public.master_data(id_master_data);


--
-- Name: spot_option_product fk_spot_option_product_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spot_option_product
    ADD CONSTRAINT fk_spot_option_product_parent FOREIGN KEY (id_option_product) REFERENCES public.option_product(id_option_product);


--
-- Name: spot_option_product fk_spot_option_product_underlying; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spot_option_product
    ADD CONSTRAINT fk_spot_option_product_underlying FOREIGN KEY (underlying) REFERENCES public.master_data(id_master_data);


--
-- Name: shape_profile_spot_series fk_spss_shape_profile; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_spot_series
    ADD CONSTRAINT fk_spss_shape_profile FOREIGN KEY (shape_profile_id) REFERENCES public.shape_profile_definition(shape_profile_id);


--
-- Name: shape_profile_spot_series fk_spss_spot_definition; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shape_profile_spot_series
    ADD CONSTRAINT fk_spss_spot_definition FOREIGN KEY (spot_price_definition_id) REFERENCES public.power_spot_price_definition(spot_price_definition_id);


--
-- Name: gl_accounts fk_statement_type; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.gl_accounts
    ADD CONSTRAINT fk_statement_type FOREIGN KEY (statement_type) REFERENCES public.financial_statement_types(statement_type_id);


--
-- Name: system_business_calendar fk_status; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_business_calendar
    ADD CONSTRAINT fk_status FOREIGN KEY (status) REFERENCES public.sbc_status(sbc_status_id);


--
-- Name: asset_class fk_super_class; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.asset_class
    ADD CONSTRAINT fk_super_class FOREIGN KEY (super_class) REFERENCES public.super_class(id_super_class);


--
-- Name: financial_txn fk_txn_acct_phase; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_txn_acct_phase FOREIGN KEY (txn_acct_phase) REFERENCES public.txn_accounting_phase(acct_phase_id);


--
-- Name: financial_txn fk_txn_origin; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_txn_origin FOREIGN KEY (txn_origin) REFERENCES public.txn_origin(txn_origin_id);


--
-- Name: financial_txn fk_txn_status; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.financial_txn
    ADD CONSTRAINT fk_txn_status FOREIGN KEY (txn_status) REFERENCES public.txn_status(id_txn_status);


--
-- Name: volatility_point fk_volatility_point_surface; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volatility_point
    ADD CONSTRAINT fk_volatility_point_surface FOREIGN KEY (volatility_surface) REFERENCES public.volatility_surface(id_volatility_surface) ON DELETE CASCADE;


--
-- Name: volatility_surface fk_volatility_surface_underlying; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.volatility_surface
    ADD CONSTRAINT fk_volatility_surface_underlying FOREIGN KEY (underlying) REFERENCES public.master_data(id_master_data);


--
-- Name: yield_curve_spread fk_ycs_spread_curve; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve_spread
    ADD CONSTRAINT fk_ycs_spread_curve FOREIGN KEY (spread_curve) REFERENCES public.spread_curve(id_spread_curve);


--
-- Name: yield_curve_item fk_yield_curve; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.yield_curve_item
    ADD CONSTRAINT fk_yield_curve FOREIGN KEY (yield_curve) REFERENCES public.yield_curve(id_yield_curve);


--
-- PostgreSQL database dump complete
--


