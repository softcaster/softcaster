delete from journal_entry_lines;
delete from journal_entries;
delete from accounting_events;
delete from position_detail;
delete from financial_txn;
delete from instrument_valuation;

ALTER SEQUENCE journal_entry_lines_s RESTART WITH 1;
ALTER SEQUENCE journal_entries_s RESTART WITH 1;
ALTER SEQUENCE accounting_events_s RESTART WITH 1;
ALTER SEQUENCE position_detail_s RESTART WITH 1;
ALTER SEQUENCE financial_txn_s RESTART WITH 1;
ALTER SEQUENCE instrument_valuation_s RESTART WITH 1;
