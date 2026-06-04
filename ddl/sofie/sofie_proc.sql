-- chiamata: SELECT purge_expired_master_data();
CREATE OR REPLACE FUNCTION purge_expired_master_data()
RETURNS void AS $$
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
$$ LANGUAGE plpgsql;

