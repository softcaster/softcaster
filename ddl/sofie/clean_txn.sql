delete from position_detail;
delete from accounting_events;
delete from financial_txn;
ALTER SEQUENCE financial_txn_s RESTART WITH 1;
ALTER SEQUENCE accounting_events_s RESTART WITH 1;
ALTER SEQUENCE position_detail_s RESTART WITH 1;
