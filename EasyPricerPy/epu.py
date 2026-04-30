# Generatore Codice accesso DB

import psycopg2
import configparser
from contextlib import contextmanager 
import sys
import datetime
import json
import requests
from utilities.database_util import *
from generated.tables import *

@contextmanager
def open_db_connection():
    config = configparser.ConfigParser()
    path = '/'.join((os.path.abspath(__file__).replace('\\', '/')).split('/')[:-1])
    config.read(os.path.join(path, 'config.ini'))
    dbname = config['DBCONFIG']['dbname']
    user = config['DBCONFIG']['user']
    password = config['DBCONFIG']['password']
    host = config['DBCONFIG']['host']
    
    connStr =  'dbname=' + dbname + ' user='+ user + ' password=' + password + ' host=' + host 
    postgres_connection = PostgresConnection(connStr)   
    conn = postgres_connection.get_connection()

    try:
        yield conn
    except psycopg2.DatabaseError as err:
        error, = err.args
        sys.stderr.write(error.message)
        conn.execute("ROLLBACK")
        raise err
    finally:
        conn.close()

def main():
    
    """
    """
    with  open_db_connection() as conn:
        schema = InformationSchema(conn)
        '''
        tables = schema.getTablesData()
        tablesSorted = []
        for table in tables:
            tablesSorted.append(table[0])
        tablesSorted.sort();
        print(tablesSorted)
        '''
        beanGenerator = BeanGeneratorTS(schema)

        beanGenerator.writeService('daycount')    
        beanGenerator.writeService('frequency')    
        beanGenerator.writeService('form')    
        beanGenerator.writeService('roll_convention')    
        beanGenerator.writeService('type_of_interest')    
        beanGenerator.writeService('accrual_schedule_type')    
        beanGenerator.writeService('market_segment')    
        beanGenerator.writeService('amortization_schedule')    
        beanGenerator.writeService('calendar')    
        beanGenerator.writeService('holiday')    
        beanGenerator.writeService('currency')    
        beanGenerator.writeService('country')    
        beanGenerator.writeService('issuer')    
        beanGenerator.writeService('super_class')    
        beanGenerator.writeService('asset_class')    
        beanGenerator.writeService('master_data')    
        beanGenerator.writeService('loan_master_data')    
        beanGenerator.writeService('security_master_data')    
        beanGenerator.writeService('cash_flow_item')    
        beanGenerator.writeService('cash_flow_reset')    
        beanGenerator.writeService('settlement_type')    
        beanGenerator.writeService('future_master_data')    
        beanGenerator.writeService('bond_future_master_data')    
        beanGenerator.writeService('deliverable_bonds')    
        beanGenerator.writeService('fx_future_master_data')    
        beanGenerator.writeService('mm_future_master_data')    
        beanGenerator.writeService('instrument_quote')    
        beanGenerator.writeService('instrument_quote_hist')    
        beanGenerator.writeService('forex_master_data')    
        beanGenerator.writeService('counterparty_type')    
        beanGenerator.writeService('counterparty')    
        beanGenerator.writeService('portfolio_master_data')    
        beanGenerator.writeService('position_master_data')    
        beanGenerator.writeService('position_detail')    
        beanGenerator.writeService('txn_status')    
        beanGenerator.writeService('finacial_txn')    
        beanGenerator.writeService('yield_curve')    
        beanGenerator.writeService('yield_curve_item')    

if __name__ == "__main__":
    main()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              