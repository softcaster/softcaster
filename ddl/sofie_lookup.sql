-- ============================================================================
-- Tabelle di lookup: 53
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
-- Data for Name: account_natures; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.account_natures (nature_id, code, description) VALUES (1, 'ASSET', 'Asset');
INSERT INTO public.account_natures (nature_id, code, description) VALUES (2, 'LIABILITY', 'Liability');
INSERT INTO public.account_natures (nature_id, code, description) VALUES (3, 'EQUITY', 'Equity');
INSERT INTO public.account_natures (nature_id, code, description) VALUES (4, 'INCOME', 'Income');
INSERT INTO public.account_natures (nature_id, code, description) VALUES (5, 'EXPENSE', 'Expense');
INSERT INTO public.account_natures (nature_id, code, description) VALUES (6, 'MEMORANDUM', 'Memorandum');


--
-- Data for Name: accounting_event_status; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.accounting_event_status (event_status_id, code, description) VALUES (1, 'NEW', 'New');
INSERT INTO public.accounting_event_status (event_status_id, code, description) VALUES (2, 'PROCESSING', 'Processing');
INSERT INTO public.accounting_event_status (event_status_id, code, description) VALUES (3, 'PROCESSED', 'Processed');
INSERT INTO public.accounting_event_status (event_status_id, code, description) VALUES (4, 'FAILED', 'Failed');


--
-- Data for Name: accounting_event_types; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (1, 'TRADE_EXECUTED', 'Trade Executed');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (2, 'TRADE_AMENDED', 'Trade Amended');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (4, 'MTM', 'Mtm');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (5, 'COUPON', 'Coupon');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (6, 'ACCRUAL', 'Accrual');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (7, 'SETTLEMENT', 'Settlement');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (8, 'MATURITY', 'Maturity');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (3, 'TRADE_CANCELED', 'Trade Cancelled');
INSERT INTO public.accounting_event_types (event_type_id, code, description) VALUES (9, 'ROLLOVER', 'Rollover');


--
-- Data for Name: accrual_schedule_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.accrual_schedule_type (id_accrual_schedule_type, code, description) VALUES (100, 'AST_NONE', 'None');


--
-- Data for Name: accumulator_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.accumulator_type (accumulator_type_id, code, description) VALUES (1, 'ACCUMULATOR', 'Accumulator');
INSERT INTO public.accumulator_type (accumulator_type_id, code, description) VALUES (2, 'DECUMULATOR', 'Decumulator');


--
-- Data for Name: amortization_schedule; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.amortization_schedule (id_amortization_schedule, code, description) VALUES (100, 'NONE', 'None');
INSERT INTO public.amortization_schedule (id_amortization_schedule, code, description) VALUES (1, 'SAS', 'French (level installment)');
INSERT INTO public.amortization_schedule (id_amortization_schedule, code, description) VALUES (2, 'SLP', 'Italian (level principal)');
INSERT INTO public.amortization_schedule (id_amortization_schedule, code, description) VALUES (3, 'IOL', 'Bullet (principal at maturity)');


--
-- Data for Name: barrier_observation; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.barrier_observation (barrier_observation_id, code, description) VALUES (1, 'FIXING', 'At each fixing');
INSERT INTO public.barrier_observation (barrier_observation_id, code, description) VALUES (2, 'CONTINUOUS', 'Continuous');
INSERT INTO public.barrier_observation (barrier_observation_id, code, description) VALUES (3, 'SETTLEMENT', 'At settlement dates');


--
-- Data for Name: bond_valuation; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.bond_valuation (bond_valuation_id, code, description) VALUES (1, 'FAIR_VALUE', 'Fair value');
INSERT INTO public.bond_valuation (bond_valuation_id, code, description) VALUES (2, 'AMORTIZED_COST', 'Amortized cost');


--
-- Data for Name: cash_flow_status; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.cash_flow_status (cash_flow_status_id, code, description) VALUES (1, 'ESTIMATED', 'Estimated');
INSERT INTO public.cash_flow_status (cash_flow_status_id, code, description) VALUES (2, 'RECORDED', 'Recorded');
INSERT INTO public.cash_flow_status (cash_flow_status_id, code, description) VALUES (3, 'PAID', 'Paid');


--
-- Data for Name: commodity_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.commodity_type (commodity_type_id, code, description) VALUES (1, 'CRUDE_OIL', 'Crude Oil');
INSERT INTO public.commodity_type (commodity_type_id, code, description) VALUES (2, 'POWER', 'Power');
INSERT INTO public.commodity_type (commodity_type_id, code, description) VALUES (3, 'NATGAS', 'Natural Gas');
INSERT INTO public.commodity_type (commodity_type_id, code, description) VALUES (4, 'GOLD', 'Gold');
INSERT INTO public.commodity_type (commodity_type_id, code, description) VALUES (5, 'SILVER', 'Silver');
INSERT INTO public.commodity_type (commodity_type_id, code, description) VALUES (6, 'PLATINUM', 'Platinum');
INSERT INTO public.commodity_type (commodity_type_id, code, description) VALUES (7, 'PALLADIUM', 'Palladium');


--
-- Data for Name: compounding; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.compounding (id_compounding, code, description) VALUES (1, 'SIMPLE', 'Linear');
INSERT INTO public.compounding (id_compounding, code, description) VALUES (2, 'COMPOUNDED', 'Compounded');
INSERT INTO public.compounding (id_compounding, code, description) VALUES (4, 'CONTINUOUS', 'Continuous');
INSERT INTO public.compounding (id_compounding, code, description) VALUES (3, 'SIMPLE-THEN-COMPOUNDED', 'Linear then Compounded');


--
-- Data for Name: counterparty_roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.counterparty_roles (role_id, code, description) VALUES (1, 'CUSTOMER', 'Customer');
INSERT INTO public.counterparty_roles (role_id, code, description) VALUES (2, 'VENDOR', 'Vendor');
INSERT INTO public.counterparty_roles (role_id, code, description) VALUES (3, 'BROKER', 'Financial Broker');
INSERT INTO public.counterparty_roles (role_id, code, description) VALUES (4, 'CLEARING_HOUSE', 'Clearing House');
INSERT INTO public.counterparty_roles (role_id, code, description) VALUES (5, 'INTERNAL_ENTITY', 'Group companies / Affiliates');


--
-- Data for Name: counterparty_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.counterparty_type (id_counterparty_type, code, description) VALUES (1, 'NATURAL_PERSON', 'Individual Person');
INSERT INTO public.counterparty_type (id_counterparty_type, code, description) VALUES (2, 'LEGAL_ENTITY', 'Corporate Entity');
INSERT INTO public.counterparty_type (id_counterparty_type, code, description) VALUES (3, 'INVESTMENT_FUND', 'Investment Fund');
INSERT INTO public.counterparty_type (id_counterparty_type, code, description) VALUES (4, 'SOVEREIGN_PUBLIC', 'Sovereign Government');


--
-- Data for Name: coupon_pm; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.coupon_pm (coupon_pm_id, code, description) VALUES (1, 'LAST_COUPON', 'Last Coupon');
INSERT INTO public.coupon_pm (coupon_pm_id, code, description) VALUES (2, 'FORWARD_CURVE', 'Forward Curve');
INSERT INTO public.coupon_pm (coupon_pm_id, code, description) VALUES (3, 'HIST_SERIE', 'Historical Serie');
INSERT INTO public.coupon_pm (coupon_pm_id, code, description) VALUES (4, 'NONE', 'None');


--
-- Data for Name: curve_role; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.curve_role (curve_role_id, code, description) VALUES (1, 'DISCOUNT', 'Discount');
INSERT INTO public.curve_role (curve_role_id, code, description) VALUES (2, 'PROJECTION', 'Projection');
INSERT INTO public.curve_role (curve_role_id, code, description) VALUES (3, 'REPO', 'Repo');


--
-- Data for Name: daycount; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.daycount (id_daycount, code, description) VALUES (1, 'NASD_30_360', '30/360 US (NASD)');
INSERT INTO public.daycount (id_daycount, code, description) VALUES (2, 'ACT_360', 'ACT/360');
INSERT INTO public.daycount (id_daycount, code, description) VALUES (3, 'ACT_365', 'ACT/365 Fixed');
INSERT INTO public.daycount (id_daycount, code, description) VALUES (4, 'ACT_ACT_ISDA', 'ACT/ACT ISDA');
INSERT INTO public.daycount (id_daycount, code, description) VALUES (5, 'ACT_ACT_ICMA', 'ACT/ACT ICMA');
INSERT INTO public.daycount (id_daycount, code, description) VALUES (6, 'EUR_30_360', '30/360 European');


--
-- Data for Name: delivery_period_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.delivery_period_type (delivery_period_type_id, code, description) VALUES (1, 'YEAR', 'Year Future - annual delivery contract');
INSERT INTO public.delivery_period_type (delivery_period_type_id, code, description) VALUES (2, 'QUARTER', 'Quarter Future - quarterly delivery contract');
INSERT INTO public.delivery_period_type (delivery_period_type_id, code, description) VALUES (3, 'MONTH', 'Month Future - monthly delivery contract');
INSERT INTO public.delivery_period_type (delivery_period_type_id, code, description) VALUES (4, 'DAY', 'Daily delivery - post final cascading');


--
-- Data for Name: eod_status; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.eod_status (eod_status_id, code, description) VALUES (1, 'RUNNING', 'Running');
INSERT INTO public.eod_status (eod_status_id, code, description) VALUES (2, 'OK', 'Completed');
INSERT INTO public.eod_status (eod_status_id, code, description) VALUES (3, 'WARNING', 'Completed with warnings');
INSERT INTO public.eod_status (eod_status_id, code, description) VALUES (4, 'FAILED', 'Failed');


--
-- Data for Name: eod_step; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (1, 'OPEN_CLOSING', 'Set the next business date');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (2, 'COUPON', 'Coupon events');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (3, 'SETTLEMENT', 'Settlement events');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (4, 'WAIT_SETTLEMENT_POSTED', 'Wait for settlement accounting');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (5, 'FX_ROLLING', 'FX spot rolling');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (6, 'ACCRUAL', 'Accrual events');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (7, 'MTM', 'End of day mark to market');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (8, 'WAIT_EOD_POSTED', 'Wait for end of day accounting');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (9, 'CONTROLS', 'Closing controls');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (10, 'DATE_ROLL', 'Roll the official date');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (11, 'FIXING', 'Floating rate fixing');
INSERT INTO public.eod_step (eod_step_id, code, description) VALUES (12, 'MATURITY', 'Maturity and expiry');


--
-- Data for Name: event_source_types; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.event_source_types (source_type_id, code, description) VALUES (1, 'TRADE', 'Trade');
INSERT INTO public.event_source_types (source_type_id, code, description) VALUES (2, 'INSTRUMENT', 'Instrument');
INSERT INTO public.event_source_types (source_type_id, code, description) VALUES (3, 'POSITION_DETAIL', 'Position Detail');


--
-- Data for Name: exercise_style; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.exercise_style (exercise_style_id, code, description) VALUES (1, 'EUROPEAN', 'Can only be exercised at the expiration date');
INSERT INTO public.exercise_style (exercise_style_id, code, description) VALUES (2, 'AMERICAN', 'Can be exercised at any time up to the expiration date');
INSERT INTO public.exercise_style (exercise_style_id, code, description) VALUES (3, 'BERMUDAN', 'Can be exercised on specific dates before expiration');


--
-- Data for Name: fee_basis; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fee_basis (fee_basis_id, code, description) VALUES (1, 'AMOUNT', 'Amount');
INSERT INTO public.fee_basis (fee_basis_id, code, description) VALUES (2, 'PERCENT', '% (points)');


--
-- Data for Name: fee_timing; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fee_timing (fee_timing_id, code, description) VALUES (1, 'UPFRONT', 'Upfront');
INSERT INTO public.fee_timing (fee_timing_id, code, description) VALUES (2, 'PER_INSTALLMENT', 'Per installment');


--
-- Data for Name: financial_statement_types; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.financial_statement_types (statement_type_id, code, description) VALUES (1, 'BALANCE_SHEET', 'Balance Sheet');
INSERT INTO public.financial_statement_types (statement_type_id, code, description) VALUES (2, 'INCOME_STATEMENT', 'Income Statement');
INSERT INTO public.financial_statement_types (statement_type_id, code, description) VALUES (3, 'OFF_BALANCE_SHEET', 'Off Balance Sheet');


--
-- Data for Name: fixing_day_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fixing_day_type (fixing_day_type_id, code, description) VALUES (1, 'CALENDAR_DAYS', 'Calendar Days');
INSERT INTO public.fixing_day_type (fixing_day_type_id, code, description) VALUES (2, 'BUSINESS_DAYS', 'Business Days');


--
-- Data for Name: form; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.form (id_form, code, description) VALUES (1, 'BEARER', 'Bearer');
INSERT INTO public.form (id_form, code, description) VALUES (2, 'REGISTERED', 'Registered');
INSERT INTO public.form (id_form, code, description) VALUES (3, 'BOOK-ENTRY-BOND', 'Book-entry Bond');
INSERT INTO public.form (id_form, code, description) VALUES (100, 'NONE', 'None');


--
-- Data for Name: frequency; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (1, 'ANNUAL', 'Annual', 1);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (3, 'E4M', 'Every 4 months', 3);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (4, 'QUARTERLY', 'Quarterly', 4);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (6, 'MONTHLY', 'Monthly', 12);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (7, 'CUSTOM', 'Custom', 0);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (8, 'WEEKLY', 'Weekly', 52);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (9, 'DAILY', 'Daily', 365);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (2, 'SEMI-ANNUAL', 'Semi-annual', 2);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (5, 'BI-MONTHLY', 'Bi-monthly', 6);
INSERT INTO public.frequency (id_frequency, code, description, year_fraction) VALUES (100, 'NONE', 'At maturity only', 0);


--
-- Data for Name: guarantee_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.guarantee_type (guarantee_type_id, code, description) VALUES (1, 'NONE', 'No guarantee');
INSERT INTO public.guarantee_type (guarantee_type_id, code, description) VALUES (2, 'LEVERAGED', 'Leveraged');
INSERT INTO public.guarantee_type (guarantee_type_id, code, description) VALUES (3, 'UNLEVERAGED', 'Unleveraged');


--
-- Data for Name: journal_entry_status; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.journal_entry_status (entry_status_id, code, description) VALUES (1, 'UNCONSOLIDATED', 'Unconsolidated');
INSERT INTO public.journal_entry_status (entry_status_id, code, description) VALUES (2, 'CONSOLIDATED', 'Consolidated');
INSERT INTO public.journal_entry_status (entry_status_id, code, description) VALUES (3, 'ERROR', 'Error');


--
-- Data for Name: journal_entry_types; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.journal_entry_types (entry_type_id, code, description) VALUES (1, 'ACCOUNTING', 'Official accounting entry');
INSERT INTO public.journal_entry_types (entry_type_id, code, description) VALUES (2, 'MEMO', 'Memorandum entry');
INSERT INTO public.journal_entry_types (entry_type_id, code, description) VALUES (3, 'REVERSAL', 'Reversal entry');
INSERT INTO public.journal_entry_types (entry_type_id, code, description) VALUES (4, 'ADJUSTMENT', 'Adjustment entry');


--
-- Data for Name: knock_in_effect; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.knock_in_effect (knock_in_effect_id, code, description) VALUES (2, 'CONTRACT', 'Activates the contract');
INSERT INTO public.knock_in_effect (knock_in_effect_id, code, description) VALUES (1, 'LEVERAGE', 'Activates the leverage');


--
-- Data for Name: load_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.load_type (load_type_id, code, description) VALUES (1, 'BASE', 'Base load - 24 hours a day, 7 days a week');
INSERT INTO public.load_type (load_type_id, code, description) VALUES (2, 'PEAK', 'Peak load - peak hours as defined by market convention');


--
-- Data for Name: loan_fee_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.loan_fee_type (loan_fee_type_id, code, description) VALUES (1, 'PROCESSING', 'Processing fee');
INSERT INTO public.loan_fee_type (loan_fee_type_id, code, description) VALUES (2, 'UNDERWRITING', 'Underwriting / appraisal fee');
INSERT INTO public.loan_fee_type (loan_fee_type_id, code, description) VALUES (3, 'TAX', 'Tax charges');
INSERT INTO public.loan_fee_type (loan_fee_type_id, code, description) VALUES (4, 'INCIDENTAL', 'Incidental expenses');
INSERT INTO public.loan_fee_type (loan_fee_type_id, code, description) VALUES (5, 'MANAGEMENT', 'Management / collection fee');
INSERT INTO public.loan_fee_type (loan_fee_type_id, code, description) VALUES (6, 'INSURANCE', 'Insurance premium');


--
-- Data for Name: market_data_source; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.market_data_source (data_source_id, code, description) VALUES (1, 'EEX', 'Eex');
INSERT INTO public.market_data_source (data_source_id, code, description) VALUES (2, 'ENTSO_E', 'Entso E');
INSERT INTO public.market_data_source (data_source_id, code, description) VALUES (3, 'VENDOR', 'Vendor');
INSERT INTO public.market_data_source (data_source_id, code, description) VALUES (4, 'INTERNAL', 'Internal');
INSERT INTO public.market_data_source (data_source_id, code, description) VALUES (5, 'GME', 'Gestore Mercati Energetici');


--
-- Data for Name: node_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.node_type (node_type_id, code, description) VALUES (1, 'MONEY_MARKET', 'Money Market');
INSERT INTO public.node_type (node_type_id, code, description) VALUES (2, 'SWAP', 'Swap');
INSERT INTO public.node_type (node_type_id, code, description) VALUES (4, 'PAR_YIELD', 'Par Yield');
INSERT INTO public.node_type (node_type_id, code, description) VALUES (3, 'ZERO_RATES', 'Zero Rates');
INSERT INTO public.node_type (node_type_id, code, description) VALUES (5, 'OIS', 'Ois');


--
-- Data for Name: normal_balances; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.normal_balances (balance_id, code, description) VALUES (1, 'DEBIT', 'Dr');
INSERT INTO public.normal_balances (balance_id, code, description) VALUES (2, 'CREDIT', 'Cr');


--
-- Data for Name: option_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.option_type (option_type_id, code, description) VALUES (1, 'CALL', 'Call Option');
INSERT INTO public.option_type (option_type_id, code, description) VALUES (2, 'PUT', 'Put Option');


--
-- Data for Name: premium_style; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.premium_style (premium_style_id, code, description) VALUES (1, 'EQUITY_STYLE', 'Premium paid upfront');
INSERT INTO public.premium_style (premium_style_id, code, description) VALUES (2, 'FUTURES_STYLE', 'Premium margined daily like a future');


--
-- Data for Name: projection_method; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.projection_method (projection_method_id, code, description) VALUES (1, 'LAST_COUPON', 'Last Coupon');
INSERT INTO public.projection_method (projection_method_id, code, description) VALUES (2, 'FORWARD_CURVE', 'Forward Curve');
INSERT INTO public.projection_method (projection_method_id, code, description) VALUES (3, 'HIST_SERIE', 'Historical Serie');
INSERT INTO public.projection_method (projection_method_id, code, description) VALUES (4, 'NONE', 'None');


--
-- Data for Name: roll_convention; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.roll_convention (id_roll_convention, code, description) VALUES (1, 'PREVIOUS', 'Preceding');
INSERT INTO public.roll_convention (id_roll_convention, code, description) VALUES (2, 'PREVIOUS-MODIFIED', 'Modified preceding');
INSERT INTO public.roll_convention (id_roll_convention, code, description) VALUES (3, 'FORWARD', 'Following');
INSERT INTO public.roll_convention (id_roll_convention, code, description) VALUES (4, 'FORWARD-MODIFIED', 'Modified following');
INSERT INTO public.roll_convention (id_roll_convention, code, description) VALUES (100, 'UNADJUSTED', 'Unadjusted');


--
-- Data for Name: sbc_status; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.sbc_status (sbc_status_id, code, description) VALUES (1, 'OPEN', 'Open');
INSERT INTO public.sbc_status (sbc_status_id, code, description) VALUES (2, 'CLOSING', 'Closing');
INSERT INTO public.sbc_status (sbc_status_id, code, description) VALUES (3, 'CLOSED', 'Closed');
INSERT INTO public.sbc_status (sbc_status_id, code, description) VALUES (4, 'LOCKED', 'Loked');


--
-- Data for Name: service_types; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.service_types (service_type_id, code, description) VALUES (1, 'TSRV', 'Trade Capture Service');
INSERT INTO public.service_types (service_type_id, code, description) VALUES (2, 'PSRV', 'Trade Processing Service');
INSERT INTO public.service_types (service_type_id, code, description) VALUES (3, 'MSRV', 'Mtm Service');
INSERT INTO public.service_types (service_type_id, code, description) VALUES (4, 'ASRV', 'Accounting Service');
INSERT INTO public.service_types (service_type_id, code, description) VALUES (5, 'LSRV', 'Life Cycle Service');


--
-- Data for Name: settlement_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.settlement_type (id_settlement_type, code, description) VALUES (1, 'PHYSICAL', 'Physical');
INSERT INTO public.settlement_type (id_settlement_type, code, description) VALUES (2, 'CASH', 'Cash');


--
-- Data for Name: shape_calculation_method; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.shape_calculation_method (calculation_method_id, code, description) VALUES (1, 'AVERAGE', 'Average');
INSERT INTO public.shape_calculation_method (calculation_method_id, code, description) VALUES (2, 'MEDIAN', 'Median');
INSERT INTO public.shape_calculation_method (calculation_method_id, code, description) VALUES (3, 'WEIGHTED_AVERAGE', 'Weighted Average');


--
-- Data for Name: shape_granularity; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.shape_granularity (granularity_id, code, description) VALUES (1, 'WEEKLY', 'Weekly profile');
INSERT INTO public.shape_granularity (granularity_id, code, description) VALUES (2, 'MONTHLY', 'Monthly profile');
INSERT INTO public.shape_granularity (granularity_id, code, description) VALUES (3, 'MONTH_DOW', 'Monthly profile differentiated by day of week');
INSERT INTO public.shape_granularity (granularity_id, code, description) VALUES (4, 'MONTH_DOW_HOUR', 'Monthly profile differentiated by day of week and hour');


--
-- Data for Name: super_class; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.super_class (id_super_class, code, description) VALUES (1, 'EQU', 'Equities');
INSERT INTO public.super_class (id_super_class, code, description) VALUES (2, 'FIN', 'Fixed Income');
INSERT INTO public.super_class (id_super_class, code, description) VALUES (3, 'CCE', ' Cash and Cash Equivalents (Money Market)');
INSERT INTO public.super_class (id_super_class, code, description) VALUES (4, 'DER', ' Derivatives');
INSERT INTO public.super_class (id_super_class, code, description) VALUES (5, 'FX', ' Forex');


--
-- Data for Name: tenor; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tenor (tenor_id, code, description) VALUES (1, 'ON', 'Overnight');
INSERT INTO public.tenor (tenor_id, code, description) VALUES (2, 'TN', 'Tomorrow/Next');
INSERT INTO public.tenor (tenor_id, code, description) VALUES (3, '1W', '1 Week');
INSERT INTO public.tenor (tenor_id, code, description) VALUES (4, '1M', '1 Month');
INSERT INTO public.tenor (tenor_id, code, description) VALUES (5, '3M', '3 Months');
INSERT INTO public.tenor (tenor_id, code, description) VALUES (6, '6M', '6 Months');
INSERT INTO public.tenor (tenor_id, code, description) VALUES (7, '9M', '9 Months');
INSERT INTO public.tenor (tenor_id, code, description) VALUES (8, '12M', '12 Months');


--
-- Data for Name: txn_accounting_phase; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.txn_accounting_phase (acct_phase_id, code, description) VALUES (1, 'NONE', 'Not accounted');
INSERT INTO public.txn_accounting_phase (acct_phase_id, code, description) VALUES (2, 'MEMO_POSTED', 'Memo accounting posted');
INSERT INTO public.txn_accounting_phase (acct_phase_id, code, description) VALUES (3, 'OFFICIAL_POSTED', 'Official accounting posted');
INSERT INTO public.txn_accounting_phase (acct_phase_id, code, description) VALUES (4, 'REVERSED', 'Reversed');


--
-- Data for Name: txn_component_types; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (1, 'BROKER_FEE', 'Broker Transaction and Execution Fees');
INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (2, 'EXCHANGE_FEE', 'CME / Clearing House Regulatory Fees');
INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (3, 'INITIAL_MARGIN', 'Initial Margin Deposit Requirement');
INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (4, 'MAINTENANCE_MARGIN', 'Maintenance Margin Requirement');
INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (5, 'OPTION_PREMIUM', 'Option Premium Paid or Written');
INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (6, 'BOND_ACCRUAL', 'Bond Accrual');
INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (7, 'EFFECTIVE_INTEREST_RATE', 'Internal rate of return calculated at trade date for amortized cost accounting');
INSERT INTO public.txn_component_types (component_type_id, code, description) VALUES (8, 'LOAN_FEE', 'Loan fees charged at disbursement');


--
-- Data for Name: txn_origin; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.txn_origin (txn_origin_id, code, description) VALUES (1, 'USER', 'Entered by a user');
INSERT INTO public.txn_origin (txn_origin_id, code, description) VALUES (2, 'EOD', 'Generated by the end of day');


--
-- Data for Name: txn_side; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.txn_side (id_txn_side, code, description) VALUES (1, 'BUY', 'Buy');
INSERT INTO public.txn_side (id_txn_side, code, description) VALUES (2, 'SELL', 'Sell');


--
-- Data for Name: txn_status; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (1, 'PENDING', 'Pending');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (2, 'VALIDATING', 'Validating');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (3, 'EXECUTED', 'Executed');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (4, 'REJECTED', 'Rejected');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (5, 'TO_AMEND', 'To Amend');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (6, 'AMENDED', 'Amended');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (7, 'TO_CANCEL', 'To Cancel');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (8, 'CANCELLED', 'Cancelled');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (10, 'CLAIMED', 'Claimed');
INSERT INTO public.txn_status (id_txn_status, code, description) VALUES (9, 'RESTARTING', 'Restarting');


--
-- Data for Name: type_of_interest; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.type_of_interest (id_type_of_interest, code, description) VALUES (1, 'FIXED', 'Fixed-Rate');
INSERT INTO public.type_of_interest (id_type_of_interest, code, description) VALUES (2, 'FLOATING', 'Floating-Rate');
INSERT INTO public.type_of_interest (id_type_of_interest, code, description) VALUES (3, 'ZERO-COUPON', 'Zero-Coupon');
INSERT INTO public.type_of_interest (id_type_of_interest, code, description) VALUES (4, 'INFLATION', 'Inflation-Linked');
INSERT INTO public.type_of_interest (id_type_of_interest, code, description) VALUES (5, 'CONVERTIBLE', 'Convertible');
INSERT INTO public.type_of_interest (id_type_of_interest, code, description) VALUES (6, 'CALLABLE', 'Callable');
INSERT INTO public.type_of_interest (id_type_of_interest, code, description) VALUES (100, 'NONE', 'None');


--
-- Name: accrual_schedule_type_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.accrual_schedule_type_s', 1, false);


--
-- Name: amortization_schedule_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.amortization_schedule_s', 3, true);


--
-- Name: compounding_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.compounding_s', 4, true);


--
-- Name: counterparty_type_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.counterparty_type_s', 6, true);


--
-- Name: daycount_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.daycount_s', 6, true);


--
-- Name: form_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.form_s', 3, true);


--
-- Name: frequency_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.frequency_s', 6, true);


--
-- Name: roll_convention_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.roll_convention_s', 4, true);


--
-- Name: settlement_type_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.settlement_type_s', 2, true);


--
-- Name: super_class_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.super_class_s', 5, true);


--
-- Name: txn_status_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.txn_status_s', 9, true);


--
-- Name: type_of_interest_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.type_of_interest_s', 6, true);


--
-- PostgreSQL database dump complete
--


