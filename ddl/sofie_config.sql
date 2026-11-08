-- ============================================================================
-- Dati di sistema: 15 tabelle
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
-- Data for Name: calendar; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.calendar (id_calendar, code, description) VALUES (1, 'EUR', 'Euro Area Calendar');
INSERT INTO public.calendar (id_calendar, code, description) VALUES (2, 'USD', 'Usd Area Calendar');


--
-- Data for Name: currency; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days, decimal_places) VALUES (1, 1, 2, 'EUR', 978, 'EUR', 2, 0, 1, 2, 2);
INSERT INTO public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days, decimal_places) VALUES (2, 2, 2, 'USD', 840, 'USD', 2, 0, 1, 2, 2);
INSERT INTO public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days, decimal_places) VALUES (3, 1, 2, 'GBP', 826, 'GBP', 2, 0, 1, 2, 2);
INSERT INTO public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days, decimal_places) VALUES (4, 1, 2, 'CHF', 576, 'CHF', 2, 0, 1, 2, 2);
INSERT INTO public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days, decimal_places) VALUES (5, 2, 2, 'CAD', 124, 'CAD', 2, 0, 1, 2, 2);
INSERT INTO public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days, decimal_places) VALUES (6, 2, 2, 'AUD', 36, 'AUD', 2, 0, 1, 2, 2);
INSERT INTO public.currency (id_currency, calendar, daycount, iso_code, currency_numeric_code, description, minor_unit, system_curr, physical_curr, business_days, decimal_places) VALUES (7, 2, 2, 'JPY', 392, 'JPY', 2, 0, 1, 2, 0);


--
-- Data for Name: gl_accounts; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (1, NULL, '1', 'Assets', false, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (2, NULL, '2', 'Liabilities', false, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (3, NULL, '3', 'Equity', false, 1, 3, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (4, NULL, '4', 'Income', false, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (5, NULL, '5', 'Expenses', false, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (6, NULL, '6', 'Off-Balance Commitments', false, 3, 6, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (7, 1, '10', 'Cash and Cash Equivalents', false, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (8, 1, '11', 'Short-Term Deposits', false, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (9, 1, '12', 'Margin Accounts and Broker Receivables', false, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (10, 1, '13', 'Financial Assets at FVTPL', false, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (11, 1, '14', 'Financial Derivatives - Assets', false, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (12, 1, '19', 'Accruals and Receivables', false, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (13, 2, '21', 'Short-Term Borrowings', false, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (14, 2, '24', 'Financial Derivatives & Settlement Liabilities', false, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (15, 3, '30', 'Capital and Reserves', false, 1, 3, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (16, 4, '40', 'Gains on Financial Derivatives', false, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (17, 4, '41', 'Foreign Exchange Gains', false, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (18, 4, '42', 'Interest and Dividend Income', false, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (19, 5, '50', 'Losses on Financial Derivatives', false, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (20, 5, '51', 'Foreign Exchange Losses', false, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (21, 5, '52', 'Interest Expenses and Trading Losses', false, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (22, 5, '58', 'Trading Fees and Execution Costs', false, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (23, 6, '60', 'Financial Commitments', false, 3, 6, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (38, 11, '140020', 'Options Premium Purchased', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (42, 12, '190020', 'Dividends Receivable', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (45, 13, '210020', 'Short-Term Repo Loans', true, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (47, 14, '240020', 'Options Premium Written', true, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (50, 14, '240050', 'Due to Brokers / Settlement Liabilities', true, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (52, 15, '300010', 'Share Capital', true, 1, 3, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (53, 15, '300050', 'Retained Earnings', true, 1, 3, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (54, 15, '300080', 'FX Translation Reserve', true, 1, 3, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (57, 17, '410010', 'Realized Foreign Exchange Gains', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (58, 17, '410020', 'Unrealized Foreign Exchange Gains', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (59, 18, '420010', 'Interest Income - Bank & Short-Term Deposits', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (60, 18, '420020', 'Interest Income - Sovereign Debt (Coupons)', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (61, 18, '420050', 'Realized Gain on Debt Securities', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (62, 18, '420060', 'Realized Gain on Equity Securities', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (63, 19, '500010', 'Realized Loss on Financial Derivatives', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (64, 19, '500020', 'Unrealized Loss on Financial Derivatives', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (65, 20, '510010', 'Realized Foreign Exchange Losses', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (66, 20, '510020', 'Unrealized Foreign Exchange Losses', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (67, 21, '520010', 'Interest Expense on Borrowings / Repo', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (68, 21, '520050', 'Realized Loss on Debt Securities', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (69, 21, '520060', 'Realized Loss on Equity Securities', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (70, 22, '580010', 'Brokerage and Execution Fees', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (71, 22, '580020', 'Clearing and Exchange Fees', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (72, 22, '580030', 'Custody and Safe-Keeping Fees', true, 2, 5, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (73, 23, '600010', 'Financial Commitments - Long Futures', true, 3, 6, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (74, 23, '600015', 'Financial Commitments - Short Futures', true, 3, 6, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (77, 14, '240060', 'FX Settlement Payable', true, 1, 2, 2, '2026-06-18 18:52:21.578589', '2026-06-18 18:52:21.578589');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (78, 12, '190030', 'FX Settlement Receivable', true, 1, 1, 1, '2026-06-18 18:52:21.578589', '2026-06-18 18:52:21.578589');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (27, 8, '110010', 'Short-Term Time Deposits', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (29, 9, '120050', 'Initial Margin Deposit', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (31, 9, '120060', 'Variation Margin Account', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (33, 10, '130010', 'Debt Securities - Sovereign Bonds', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (35, 10, '130020', 'Equity Securities - Domestic Shares', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (43, 13, '210010', 'Bank Overdrafts', true, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (55, 16, '400010', 'Realized Gain on Financial Derivatives', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (56, 16, '400020', 'Unrealized Gain on Financial Derivatives', true, 2, 4, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (79, 9, '120090', 'Currency Position', true, 1, 1, 1, '2026-06-25 12:51:26.676358', '2026-06-25 12:51:26.676358');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (24, 7, '100010', 'Cash and Cash Equivalents ', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (41, 12, '190010', 'Accrued Interest', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (89, 23, '600040', 'Financial Commitments - Bonds', true, 3, 6, 1, '2026-07-03 22:11:03.19662', '2026-07-03 22:11:03.19662');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (91, 23, '600090', 'Off-Balance Sheet Clearing Account', true, 3, 6, 2, '2026-07-03 22:11:03.19662', '2026-07-03 22:11:03.19662');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (99, 23, '600050', 'Financial Commitments - Fx-Spot', true, 3, 6, 1, '2026-07-03 22:49:14.682752', '2026-07-03 22:49:14.682752');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (26, 7, '100020', 'Petty Cash', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (37, 10, '130050', 'FX Spot Position', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (39, 11, '140030', 'FX Future Position', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (40, 11, '140040', 'Financial Futures Position', true, 1, 1, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (51, 14, '240090', 'Currency Position Control', true, 1, 2, 2, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (75, 23, '600020', 'Financial Commitments - Forward Currency', true, 3, 6, 1, '2026-06-05 09:03:34.250266', '2026-06-05 09:03:34.250266');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (103, 21, '520020', 'Interest Expense - Sovereign Debt (Coupons)', true, 2, 5, 1, '2026-09-15 23:23:00.28044', '2026-09-15 23:23:00.28044');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (104, 10, '130030', 'Fair Value Adjustment - Debt Securities', true, 1, 1, 1, '2026-11-02 23:13:22.734642', '2026-11-02 23:13:22.734642');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (105, 18, '420070', 'Unrealized Gain on Debt Securities', true, 2, 4, 2, '2026-11-02 23:13:22.734642', '2026-11-02 23:13:22.734642');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (106, 21, '520070', 'Unrealized Loss on Debt Securities', true, 2, 5, 1, '2026-11-02 23:13:22.734642', '2026-11-02 23:13:22.734642');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (107, 1, '15', 'Financial Assets at Amortised Cost', false, 1, 1, 1, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (108, 2, '22', 'Loans and Borrowings', false, 1, 2, 2, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (109, 4, '43', 'Fee and Commission Income', false, 2, 4, 2, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (110, 18, '420030', 'Interest Income - Loans', true, 2, 4, 2, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (111, 22, '580040', 'Loan Fees Paid', true, 2, 5, 1, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (112, 23, '600060', 'Financial Commitments - Loans', true, 3, 6, 1, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (113, 107, '150010', 'Loans and Advances to Customers', true, 1, 1, 1, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (114, 108, '220010', 'Loans Received', true, 1, 2, 2, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (115, 109, '430010', 'Loan Fees Received', true, 2, 4, 2, '2026-11-07 18:26:34.640532', '2026-11-07 18:26:34.640532');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (116, 107, '150020', 'Accrued Interest Receivable - Loans', true, 1, 1, 1, '2026-11-07 18:57:28.65387', '2026-11-07 18:57:28.65387');
INSERT INTO public.gl_accounts (account_id, parent, code, description, is_postable, statement_type, nature, balance, created_at, updated_at) VALUES (117, 108, '220020', 'Accrued Interest Payable - Loans', true, 1, 2, 2, '2026-11-07 18:57:28.65387', '2026-11-07 18:57:28.65387');


--
-- Data for Name: account_mapping; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (1, 'CURRENCY_POSITION', 1, 79);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (2, 'CURRENCY_POSITION', 2, 79);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (3, 'CURRENCY_POSITION', 4, 79);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (4, 'FX_SPOT_ASSET', 1, 37);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (5, 'FX_SPOT_ASSET', 2, 37);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (6, 'FX_SPOT_ASSET', 4, 37);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (7, 'POSITION_CONTROL', 1, 51);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (8, 'POSITION_CONTROL', 2, 51);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (9, 'POSITION_CONTROL', 4, 51);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (10, 'FXSPOT_COMMITMENT', 1, 99);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (11, 'FXSPOT_COMMITMENT', 2, 99);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (12, 'FXSPOT_COMMITMENT', 4, 99);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (13, 'OBS_CLEARING', 1, 91);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (14, 'OBS_CLEARING', 2, 91);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (15, 'OBS_CLEARING', 4, 91);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (16, 'BOND_ASSET', 1, 33);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (17, 'ACCRUED_INTEREST', 1, 41);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (18, 'SETTLEMENT_LIAB', 1, 50);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (19, 'INTEREST_INCOME', 1, 60);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (20, 'CASH_ACCOUNT', 1, 24);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (21, 'BOND_ASSET', 2, 33);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (22, 'ACCRUED_INTEREST', 2, 41);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (23, 'SETTLEMENT_LIAB', 2, 50);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (24, 'INTEREST_INCOME', 2, 60);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (25, 'CASH_ACCOUNT', 2, 24);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (26, 'BOND_ASSET', 4, 33);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (27, 'ACCRUED_INTEREST', 4, 41);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (28, 'SETTLEMENT_LIAB', 4, 50);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (29, 'INTEREST_INCOME', 4, 60);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (30, 'CASH_ACCOUNT', 4, 24);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (31, 'BOND_COMMITMENT', 1, 89);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (32, 'BOND_COMMITMENT', 2, 89);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (33, 'BOND_COMMITMENT', 4, 89);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (34, 'INITIAL_MARGIN', 1, 29);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (35, 'INITIAL_MARGIN', 2, 29);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (36, 'INITIAL_MARGIN', 4, 29);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (37, 'VARIATION_MARGIN', 1, 31);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (38, 'VARIATION_MARGIN', 2, 31);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (39, 'VARIATION_MARGIN', 4, 31);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (40, 'FUT_REALIZED_LOSS', 1, 63);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (41, 'FUT_REALIZED_LOSS', 2, 63);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (42, 'FUT_REALIZED_LOSS', 4, 63);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (43, 'FUT_REALIZED_GAIN', 1, 55);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (44, 'FUT_REALIZED_GAIN', 2, 55);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (45, 'FUT_REALIZED_GAIN', 4, 55);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (46, 'INTEREST_EXPENSE', 1, 103);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (47, 'INTEREST_EXPENSE', 2, 103);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (48, 'INTEREST_EXPENSE', 4, 103);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (49, 'BOND_FV_ADJUSTMENT', 1, 104);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (50, 'BOND_FV_ADJUSTMENT', 2, 104);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (51, 'BOND_FV_ADJUSTMENT', 4, 104);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (52, 'BOND_UNREALIZED_GAIN', 1, 105);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (53, 'BOND_UNREALIZED_GAIN', 2, 105);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (54, 'BOND_UNREALIZED_GAIN', 4, 105);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (55, 'BOND_UNREALIZED_LOSS', 1, 106);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (56, 'BOND_UNREALIZED_LOSS', 2, 106);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (57, 'BOND_UNREALIZED_LOSS', 4, 106);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (58, 'FX_REALIZED_GAIN', 4, 57);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (59, 'FX_REALIZED_GAIN', 1, 57);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (60, 'FX_REALIZED_GAIN', 2, 57);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (61, 'FX_REALIZED_LOSS', 4, 65);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (62, 'FX_REALIZED_LOSS', 1, 65);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (63, 'FX_REALIZED_LOSS', 2, 65);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (64, 'FX_UNREALIZED_GAIN', 4, 58);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (65, 'FX_UNREALIZED_GAIN', 1, 58);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (66, 'FX_UNREALIZED_GAIN', 2, 58);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (67, 'FX_UNREALIZED_LOSS', 4, 66);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (68, 'FX_UNREALIZED_LOSS', 1, 66);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (69, 'FX_UNREALIZED_LOSS', 2, 66);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (70, 'LOAN_ASSET', 4, 113);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (71, 'LOAN_ASSET', 1, 113);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (72, 'LOAN_ASSET', 2, 113);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (73, 'LOAN_COMMITMENT', 4, 112);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (74, 'LOAN_COMMITMENT', 1, 112);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (75, 'LOAN_COMMITMENT', 2, 112);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (76, 'LOAN_FEE_EXPENSE', 4, 111);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (77, 'LOAN_FEE_EXPENSE', 1, 111);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (78, 'LOAN_FEE_EXPENSE', 2, 111);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (79, 'LOAN_FEE_INCOME', 4, 115);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (80, 'LOAN_FEE_INCOME', 1, 115);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (81, 'LOAN_FEE_INCOME', 2, 115);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (82, 'LOAN_INTEREST_EXPENSE', 4, 67);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (83, 'LOAN_INTEREST_EXPENSE', 1, 67);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (84, 'LOAN_INTEREST_EXPENSE', 2, 67);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (85, 'LOAN_INTEREST_INCOME', 4, 110);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (86, 'LOAN_INTEREST_INCOME', 1, 110);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (87, 'LOAN_INTEREST_INCOME', 2, 110);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (88, 'LOAN_LIABILITY', 4, 114);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (89, 'LOAN_LIABILITY', 1, 114);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (90, 'LOAN_LIABILITY', 2, 114);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (91, 'LOAN_ACCRUED_INTEREST_PAYABLE', 4, 117);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (92, 'LOAN_ACCRUED_INTEREST_PAYABLE', 1, 117);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (93, 'LOAN_ACCRUED_INTEREST_PAYABLE', 2, 117);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (94, 'LOAN_ACCRUED_INTEREST_RECEIVABLE', 4, 116);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (95, 'LOAN_ACCRUED_INTEREST_RECEIVABLE', 1, 116);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (96, 'LOAN_ACCRUED_INTEREST_RECEIVABLE', 2, 116);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (97, 'BOND_REALIZED_GAIN', 4, 61);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (98, 'BOND_REALIZED_GAIN', 1, 61);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (99, 'BOND_REALIZED_GAIN', 2, 61);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (100, 'BOND_REALIZED_LOSS', 4, 68);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (101, 'BOND_REALIZED_LOSS', 1, 68);
INSERT INTO public.account_mapping (account_mapping_id, mapping_key, currency, gl_account) VALUES (102, 'BOND_REALIZED_LOSS', 2, 68);


--
-- Data for Name: asset_class; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (1, 2, 'FRN', 'Floating Rate Notes');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (2, 2, 'XRN', 'Fixed Rate Notes');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (3, 2, 'FRB', 'Floating Rate Bonds');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (4, 2, 'XRB', 'Fixed Rate Bonds');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (5, 2, 'BLL', 'Bills');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (6, 3, 'FRM', 'Floating Rate Mortages');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (7, 3, 'XRM', 'Fixed Rate Mortages');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (8, 4, 'BFU', 'Bond Futures');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (9, 5, 'FSP', 'Spot Forex');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (10, 5, 'FFW', 'Forex Forward');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (11, 5, 'FFU', 'Forex Future');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (12, 3, 'MFU', 'MM Future');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (13, 4, 'CFU', 'Commodity Future');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (14, 4, 'FFO', 'Forex Future Option');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (15, 4, 'FXO', 'Forex Option');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (16, 4, 'BDO', 'Bond Option');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (17, 4, 'BFO', 'Bond Future Option');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (18, 4, 'FXA', 'Forex Accumulator');
INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES (19, 4, 'CMA', 'Commodity Accumulator');


--
-- Data for Name: asset_class_script_mapping; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (1, 1, NULL, 'XRB');
INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (2, 2, NULL, 'XRB');
INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (3, 3, NULL, 'XRB');
INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (4, 4, NULL, 'XRB');
INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (5, 8, NULL, 'FUT');
INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (6, 11, NULL, 'FUT');
INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (7, 6, NULL, 'LOAN');
INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES (8, 7, NULL, 'LOAN');


--
-- Data for Name: country; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (2, 'USA', 'United States of America (the)', 'US', 'USA', 840, 'UN Member State', '', '', 2, 2);
INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (3, 'GBR', 'United Kingdom of Great Britain and Northern Ireland (the)', 'GB', 'GBR', 826, 'UN Member State', '', '', 3, 1);
INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (4, 'CHE', 'Swiss Confederation (the)', 'CH', 'CHE', 756, 'UN Member State', '', '', 4, 1);
INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (5, 'CAN', 'Canada', 'CA', 'CAN', 124, 'UN Member State', '', '', 5, 2);
INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (6, 'AUS', 'Australia', 'AU', 'AUS', 36, 'UN Member State', '', '', 6, 2);
INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (7, 'JP', 'Japan', 'JP', 'JPN', 392, 'UN Member State', '', '', 7, 2);
INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (1, 'ITA', 'Italy', 'IT', 'ITA', 381, 'UN Member State', '', '', 1, 1);
INSERT INTO public.country (id_country, country_name, official_state_name, alfa_2_code, alfa_3_code, country_numeric_code, sovereign, subdivision_code_links, internet_cc_tld, currency, calendar) VALUES (8, 'DEU', 'Germany', 'DE', 'DEU', 276, 'UN Member State', '', '', 1, 1);


--
-- Data for Name: counterparty; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.counterparty (id_counterparty, ctp_type, lei_code, country, code, description) VALUES (1, 1, 'Default', 2, 'DEFAULT', 'Default Counterparty');
INSERT INTO public.counterparty (id_counterparty, ctp_type, lei_code, country, code, description) VALUES (2, 2, 'LCZ7XYGSLJUHFXXNXD88', 2, 'CME', 'CME Group');
INSERT INTO public.counterparty (id_counterparty, ctp_type, lei_code, country, code, description) VALUES (3, 2, '549300BEI5FJA6U6QK51', 2, 'OANDA', 'Oanda Smarter Trading');
INSERT INTO public.counterparty (id_counterparty, ctp_type, lei_code, country, code, description) VALUES (4, 2, '549300SZJ9VS8SGXAN81', 4, 'UBS', 'UBS Group AG');
INSERT INTO public.counterparty (id_counterparty, ctp_type, lei_code, country, code, description) VALUES (5, 2, '529900J0JGLSFDWNFC20', 8, 'EEX', 'European Energy Exchange AG');


--
-- Data for Name: counterparty_role_mapping; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.counterparty_role_mapping (counterparty_role_mapping_id, counterparty, ctp_role) VALUES (1, 2, 3);
INSERT INTO public.counterparty_role_mapping (counterparty_role_mapping_id, counterparty, ctp_role) VALUES (2, 3, 3);
INSERT INTO public.counterparty_role_mapping (counterparty_role_mapping_id, counterparty, ctp_role) VALUES (3, 4, 3);
INSERT INTO public.counterparty_role_mapping (counterparty_role_mapping_id, counterparty, ctp_role) VALUES (4, 5, 4);


--
-- Data for Name: descriptors; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.descriptors (descriptor_id, service_type, jar_path, active_profile, port) VALUES (1, 1, 'c:\tools\projects\DEVELOP\softcaster\easy_pricer_srv\target\easy_pricer_srv-1.0.jar', 'dev', '8080');
INSERT INTO public.descriptors (descriptor_id, service_type, jar_path, active_profile, port) VALUES (2, 2, 'c:\tools\projects\DEVELOP\softcaster\easy_pricer_proc\target\easy_pricer_proc-1.0.jar', 'dev', '8081');
INSERT INTO public.descriptors (descriptor_id, service_type, jar_path, active_profile, port) VALUES (3, 3, 'c:\tools\projects\DEVELOP\softcaster\easy_pricer_mtm\target\easy_pricer_mtm-1.0.jar', 'dev', '8082');
INSERT INTO public.descriptors (descriptor_id, service_type, jar_path, active_profile, port) VALUES (4, 4, 'c:\tools\projects\DEVELOP\softcaster\easy_pricer_acct\target\easy_pricer_acct-1.0.jar', 'dev', '8083');
INSERT INTO public.descriptors (descriptor_id, service_type, jar_path, active_profile, port) VALUES (5, 5, 'c:\tools\projects\DEVELOP\softcaster\easy_pricer_lc\target\easy_pricer_lc-1.0.jar', 'dev', '8084');


--
-- Data for Name: gl_account_slots; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (1, 24, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (2, 24, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (3, 24, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (4, 24, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (5, 24, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (6, 24, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (7, 24, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (8, 26, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (9, 26, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (10, 26, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (11, 26, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (12, 26, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (13, 26, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (14, 26, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (15, 27, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (16, 27, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (17, 27, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (18, 27, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (19, 27, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (20, 27, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (21, 27, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (22, 29, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (23, 29, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (24, 29, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (25, 29, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (26, 29, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (27, 29, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (28, 29, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (29, 31, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (30, 31, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (31, 31, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (32, 31, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (33, 31, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (34, 31, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (35, 31, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (36, 79, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (37, 79, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (38, 79, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (39, 79, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (40, 79, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (41, 79, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (42, 79, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (43, 33, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (44, 33, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (45, 33, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (46, 33, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (47, 33, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (48, 33, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (49, 33, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (50, 35, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (51, 35, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (52, 35, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (53, 35, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (54, 35, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (55, 35, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (56, 35, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (57, 37, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (58, 37, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (59, 37, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (60, 37, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (61, 37, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (62, 37, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (63, 37, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (64, 38, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (65, 38, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (66, 38, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (67, 38, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (68, 38, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (69, 38, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (70, 38, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (71, 39, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (72, 39, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (73, 39, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (74, 39, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (75, 39, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (76, 39, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (77, 39, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (78, 40, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (79, 40, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (80, 40, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (81, 40, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (82, 40, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (83, 40, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (84, 40, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (85, 41, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (86, 41, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (87, 41, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (88, 41, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (89, 41, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (90, 41, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (91, 41, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (92, 42, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (93, 42, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (94, 42, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (95, 42, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (96, 42, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (97, 42, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (98, 42, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (99, 78, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (100, 78, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (101, 78, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (102, 78, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (103, 78, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (104, 78, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (105, 78, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (106, 43, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (107, 43, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (108, 43, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (109, 43, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (110, 43, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (111, 43, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (112, 43, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (113, 45, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (114, 45, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (115, 45, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (116, 45, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (117, 45, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (118, 45, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (119, 45, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (120, 47, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (121, 47, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (122, 47, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (123, 47, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (124, 47, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (125, 47, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (126, 47, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (127, 50, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (128, 50, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (129, 50, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (130, 50, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (131, 50, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (132, 50, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (133, 50, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (134, 77, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (135, 77, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (136, 77, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (137, 77, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (138, 77, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (139, 77, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (140, 77, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (141, 51, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (142, 51, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (143, 51, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (144, 51, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (145, 51, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (146, 51, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (147, 51, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (148, 52, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (149, 52, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (150, 52, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (151, 52, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (152, 52, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (153, 52, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (154, 52, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (155, 53, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (156, 53, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (157, 53, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (158, 53, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (159, 53, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (160, 53, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (161, 53, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (162, 54, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (163, 54, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (164, 54, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (165, 54, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (166, 54, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (167, 54, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (168, 54, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (169, 55, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (170, 55, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (171, 55, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (172, 55, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (173, 55, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (174, 55, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (175, 55, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (176, 56, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (177, 56, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (178, 56, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (179, 56, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (180, 56, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (181, 56, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (182, 56, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (183, 57, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (184, 57, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (185, 57, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (186, 57, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (187, 57, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (188, 57, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (189, 57, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (190, 58, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (191, 58, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (192, 58, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (193, 58, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (194, 58, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (195, 58, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (196, 58, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (197, 59, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (198, 59, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (199, 59, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (200, 59, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (201, 59, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (202, 59, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (203, 59, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (204, 60, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (205, 60, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (206, 60, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (207, 60, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (208, 60, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (209, 60, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (210, 60, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (211, 61, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (212, 61, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (213, 61, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (214, 61, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (215, 61, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (216, 61, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (217, 61, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (218, 62, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (219, 62, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (220, 62, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (221, 62, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (222, 62, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (223, 62, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (224, 62, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (225, 63, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (226, 63, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (227, 63, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (228, 63, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (229, 63, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (230, 63, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (231, 63, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (232, 64, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (233, 64, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (234, 64, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (235, 64, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (236, 64, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (237, 64, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (238, 64, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (239, 65, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (240, 65, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (241, 65, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (242, 65, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (243, 65, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (244, 65, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (245, 65, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (246, 66, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (247, 66, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (248, 66, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (249, 66, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (250, 66, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (251, 66, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (252, 66, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (253, 67, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (254, 67, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (255, 67, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (256, 67, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (257, 67, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (258, 67, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (259, 67, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (260, 68, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (261, 68, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (262, 68, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (263, 68, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (264, 68, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (265, 68, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (266, 68, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (267, 69, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (268, 69, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (269, 69, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (270, 69, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (271, 69, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (272, 69, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (273, 69, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (274, 70, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (275, 70, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (276, 70, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (277, 70, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (278, 70, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (279, 70, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (280, 70, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (281, 71, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (282, 71, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (283, 71, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (284, 71, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (285, 71, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (286, 71, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (287, 71, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (288, 72, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (289, 72, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (290, 72, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (291, 72, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (292, 72, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (293, 72, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (294, 72, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (295, 73, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (296, 73, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (297, 73, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (298, 73, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (299, 73, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (300, 73, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (301, 73, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (302, 74, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (303, 74, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (304, 74, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (305, 74, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (306, 74, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (307, 74, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (308, 74, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (309, 75, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (310, 75, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (311, 75, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (312, 75, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (313, 75, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (314, 75, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (315, 75, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (323, 89, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (324, 89, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (325, 89, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (326, 89, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (327, 89, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (328, 89, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (329, 89, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (330, 99, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (331, 99, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (332, 99, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (333, 99, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (334, 99, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (335, 99, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (336, 99, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (337, 91, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (338, 91, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (339, 91, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (340, 91, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (341, 91, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (342, 91, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (343, 91, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (344, 103, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (345, 103, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (346, 103, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (347, 103, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (348, 103, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (349, 103, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (350, 103, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (351, 104, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (352, 104, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (353, 104, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (354, 104, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (355, 104, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (356, 104, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (357, 104, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (358, 105, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (359, 105, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (360, 105, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (361, 105, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (362, 105, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (363, 105, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (364, 105, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (365, 106, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (366, 106, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (367, 106, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (368, 106, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (369, 106, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (370, 106, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (371, 106, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (372, 113, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (373, 113, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (374, 113, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (375, 113, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (376, 113, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (377, 113, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (378, 113, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (379, 114, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (380, 114, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (381, 114, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (382, 114, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (383, 114, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (384, 114, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (385, 114, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (386, 110, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (387, 110, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (388, 110, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (389, 110, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (390, 110, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (391, 110, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (392, 110, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (393, 115, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (394, 115, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (395, 115, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (396, 115, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (397, 115, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (398, 115, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (399, 115, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (400, 111, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (401, 111, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (402, 111, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (403, 111, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (404, 111, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (405, 111, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (406, 111, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (407, 112, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (408, 112, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (409, 112, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (410, 112, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (411, 112, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (412, 112, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (413, 112, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (414, 116, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (415, 116, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (416, 116, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (417, 116, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (418, 116, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (419, 116, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (420, 116, 7);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (421, 117, 1);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (422, 117, 2);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (423, 117, 3);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (424, 117, 4);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (425, 117, 5);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (426, 117, 6);
INSERT INTO public.gl_account_slots (account_slot_id, account, currency) VALUES (427, 117, 7);


--
-- Data for Name: holiday; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (2, 1, 3, 4, 'Good Friday');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (3, 1, 6, 4, 'Easter Monday');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (4, 1, 1, 5, 'Labor Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (5, 1, 25, 12, 'Christmas Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (6, 1, 26, 12, 'Christmas Holiday');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (7, 2, 1, 1, 'New Year Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (8, 2, 19, 1, 'Martin Luther King Jr. Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (9, 2, 16, 2, 'Presidents Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (10, 2, 3, 4, 'Good Friday');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (11, 2, 25, 5, 'Memorial Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (12, 2, 19, 6, 'Juneteenth National Independence Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (13, 2, 3, 7, 'Independence Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (14, 2, 7, 9, 'Labor Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (15, 2, 12, 10, 'Columbus Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (16, 2, 11, 11, 'Veterans Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (17, 2, 26, 11, 'Thanksgiving');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (18, 2, 25, 12, 'Christmas Day');
INSERT INTO public.holiday (id_holiday, calendar, holiday_day, holiday_month, description) VALUES (1, 1, 1, 1, 'New Year Day');


--
-- Data for Name: portfolio_master_data; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.portfolio_master_data (id_portfolio, currency, code, description) VALUES (1, 2, 'PTUSD01', 'Default Portfolio (USD)');
INSERT INTO public.portfolio_master_data (id_portfolio, currency, code, description) VALUES (2, 2, 'USDT', 'United States Department of the Treasury');
INSERT INTO public.portfolio_master_data (id_portfolio, currency, code, description) VALUES (3, 4, 'PTCHFO1', 'Default Portfolio (CHF)');
INSERT INTO public.portfolio_master_data (id_portfolio, currency, code, description) VALUES (4, 1, 'PTEUR01', 'Default Portfolio (EUR)');


--
-- Data for Name: position_master_data; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.position_master_data (id_position, portfolio, currency, code, description, fx_rolling, bond_valuation) VALUES (2, 3, 4, 'POSCHF01', 'Default Position (CHF)', false, 1);
INSERT INTO public.position_master_data (id_position, portfolio, currency, code, description, fx_rolling, bond_valuation) VALUES (1, 1, 2, 'POSUSD01', 'Default Position (USD)', false, 1);
INSERT INTO public.position_master_data (id_position, portfolio, currency, code, description, fx_rolling, bond_valuation) VALUES (3, 4, 1, 'POSEUR01', 'Default Position (EUR)', false, 1);


--
-- Data for Name: system_business_calendar; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.system_business_calendar (sbc_id, description, calendar, status, official_date, next_business_date, version, updated_at, currency, last_official_date) VALUES (1, 'SO.FI.E Buiness Calendar', 1, 1, '2026-11-11', NULL, 15, '2026-11-08 15:53:52.964759', 1, '2026-11-10');


--
-- Name: account_mapping_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.account_mapping_s', 102, true);


--
-- Name: asset_class_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.asset_class_s', 19, true);


--
-- Name: asset_class_script_mapping_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.asset_class_script_mapping_s', 8, true);


--
-- Name: calendar_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.calendar_s', 2, true);


--
-- Name: counterparty_role_mapping_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.counterparty_role_mapping_s', 4, true);


--
-- Name: counterparty_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.counterparty_s', 5, true);


--
-- Name: country_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.country_s', 8, true);


--
-- Name: currency_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.currency_s', 7, true);


--
-- Name: descriptors_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.descriptors_s', 1, false);


--
-- Name: gl_account_slots_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.gl_account_slots_s', 427, true);


--
-- Name: gl_accounts_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.gl_accounts_s', 117, true);


--
-- Name: holiday_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.holiday_s', 21, true);


--
-- Name: portfolio_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.portfolio_master_data_s', 4, true);


--
-- Name: position_master_data_s; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.position_master_data_s', 3, true);


--
-- PostgreSQL database dump complete
--


