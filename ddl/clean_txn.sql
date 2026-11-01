delete from journal_entry_lines;
delete from journal_entries;
delete from accounting_event_accruals;
delete from accounting_events;
delete from position_txn_links;
delete from position_detail;
delete from financial_txn_components;
delete from financial_txn;
delete from instrument_valuation;
--delete from instrument_quote_hist;
--delete from instrument_quote;

-- ----------------------------------------------------------------------------
-- Anagrafiche OTC: ogni contratto e' un'anagrafica creata dal deal capture,
-- senza deal non ha senso tenerla.
--   mutui (XRM, FRM): master_data -> cash_flow_master_data -> loan_master_data
--                     (-> flt_loan_master_data), spese, piano delle rate
--   accumulator (FXA, CMA): master_data -> accumulator_master_data, fixing
-- ----------------------------------------------------------------------------
begin;
-- otc_md vive fino al commit: la transazione racchiude tutta la pulizia OTC
create temp table otc_md on commit drop as
select md.id_master_data
from master_data md
join asset_class ac on ac.id_asset_class = md.asset_class
where ac.code in ('XRM', 'FRM', 'FXA', 'CMA');

-- quotazioni e regole broker (di norma assenti per gli OTC)
delete from instrument_quote_hist where master_data in (select id_master_data from otc_md);
delete from instrument_quote where master_data in (select id_master_data from otc_md);
delete from broker_instrument_rules where master_data in (select id_master_data from otc_md);

-- mutui: spese, piano, foglie
delete from loan_fee where master_data in (select id_master_data from otc_md);
delete from cash_flow_reset where master_data in (select id_master_data from otc_md);
delete from cash_flow_item where master_data in (select id_master_data from otc_md);
delete from flt_loan_master_data where id_master_data in (select id_master_data from otc_md);
delete from loan_master_data where id_master_data in (select id_master_data from otc_md);
delete from cash_flow_master_data where id_master_data in (select id_master_data from otc_md);

-- accumulator: calendario dei fixing, foglia
delete from accumulator_fixing where master_data in (select id_master_data from otc_md);
delete from accumulator_master_data where id_master_data in (select id_master_data from otc_md);

delete from master_data where id_master_data in (select id_master_data from otc_md);
commit;

ALTER SEQUENCE journal_entry_lines_s RESTART WITH 1;
ALTER SEQUENCE journal_entries_s RESTART WITH 1;
ALTER SEQUENCE accounting_events_s RESTART WITH 1;
ALTER SEQUENCE position_detail_s RESTART WITH 1;
ALTER SEQUENCE position_txn_links_s RESTART WITH 1;
ALTER SEQUENCE financial_txn_components_s RESTART WITH 1;
ALTER SEQUENCE financial_txn_s RESTART WITH 1;
-- sequence usate solo dagli OTC; master_data_s e cash_flow_item_s sono condivise con le altre anagrafiche
ALTER SEQUENCE loan_fee_s RESTART WITH 1;
ALTER SEQUENCE accumulator_fixing_s RESTART WITH 1;
--ALTER SEQUENCE instrument_quote_s RESTART WITH 1;
--ALTER SEQUENCE instrument_quote_s RESTART WITH 1;
