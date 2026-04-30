# File: db/postgres_connection.py
import os 
import sys
import re
import psycopg2
from psycopg2 import pool
from threading import Lock

class PostgresConnection:
    _instance = None                
    _lock = Lock()

    def __new__(cls, dsn):
        if not cls._instance:
            with cls._lock:
                if not cls._instance:
                    cls._instance = super().__new__(cls)
                    cls._instance._pool = psycopg2.pool.SimpleConnectionPool(1, 10, dsn)
        return cls._instance

    def get_connection(self):
        return self._pool.getconn()

    def release_connection(self, conn):
        self._pool.putconn(conn)    

'''
import psycopg2 as db
>>> conn = db.connect('dbname=billings user=steve password=xxxxx port=5432')
>>> curs = conn.cursor()
>>> curs.execute("""select table_name from information_schema.tables WHERE table_schema='public' AND table_type='BASETABLE'""")
>>> curs.fetchall()
'''
class InformationSchema:                                    

    def __init__(self,conn):
        self.conn = conn   
    
    def getTablesData(self):
        # Create a cursor object
        cur = self.conn.cursor()

        # Query to get table names 
        str = "SELECT table_name FROM information_schema.tables WHERE table_schema='public'"
        cur.execute(str)
        # Fetch all results
        tables = cur.fetchall()

        # Close the cursor 
        cur.close()

        return tables

    def getColumnsData(self, table_name):
        # Create a cursor object
        cur = self.conn.cursor()

        # Query to get column names and types
        str = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table_name + "'"
        cur.execute(str)
        # Fetch all results
        columns = cur.fetchall()

        # Close the cursor 
        cur.close()

        return columns
    
    def getConstraintsData(self, table_name):
        # Create a cursor object
        cur = self.conn.cursor()

        # Query to get constraints names 
        str = ("SELECT DISTINCT tc.table_name AS foreign_key_table,kcu.column_name AS foreign_key_column,ccu.table_name AS referenced_table,ccu.column_name AS referenced_column "
        "FROM information_schema.table_constraints AS tc JOIN information_schema.key_column_usage AS kcu ON tc.constraint_name = kcu.constraint_name "
        "JOIN information_schema.constraint_column_usage AS ccu ON kcu.constraint_name = ccu.constraint_name "
        "WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_name = '" + table_name + "'")
        cur.execute(str)
        # Fetch all results
        constraints = cur.fetchall()

        # Close the cursor 
        cur.close()

        return constraints

class InformationData:                                    

    def __init__(self,conn):
        self.conn = conn   
    
    def getData(self, table_name):
        # Create a cursor object
        cur = self.conn.cursor()

        # Query to get data        
        str = "SELECT * FROM " + table_name
        cur.execute(str)

        # Fetch all results
        data = cur.fetchall()

        # Close the cursor 
        cur.close()

        return data  
    
class BeanGenerator:

    def __init__(self,schema):
        self.schema = schema   

    def toCamelCase(self,text):
        return re.sub(r"[-_]([a-zA-Z])", lambda x: x[1].upper(), text)
            
    def capitalizeFirstLetter(self,text):
        return text[0].upper()+text[1:]

    def isConstraintName(self, element, constraintsData):
        for constraint in constraintsData:
            if(constraint[1] == element[0]):
                return True;
        return False;

    def getConstraintName(self, element, constraintsData):
        for constraint in constraintsData:
            if(constraint[1] == element[0]):
                return constraint[2];
        return '';

    def writeBean(self, table_name):
        fileName = "tables.py"
        path = os.path.dirname(os.path.abspath(sys.argv[0]))
        generatedPath = path + "\\generated\\" 
        filepath = os.path.join(generatedPath, fileName)
        if not os.path.exists(generatedPath):
            os.makedirs(generatedPath)
        
        fw = open(filepath, "a")                                                                                                
          
        table_name[0].capitalize()
        className = self.toCamelCase(table_name)
        className =self.capitalizeFirstLetter(className)
        fw.write("#----------------------------------------------------------------------\n")
        fw.write("# " + className + "\n")
        fw.write("#----------------------------------------------------------------------\n")
        fw.write("class " + className + ":\n")

        fw.write("    def __init__(self):\n")

        constraintsData = self.schema.getConstraintsData(table_name)
        schemaData = self.schema.getColumnsData(table_name)        
        for element in schemaData:
            variable = element[0]
            if(self.isConstraintName(element, constraintsData)):
                variable = self.getConstraintName(element, constraintsData)
            classVariabile = self.toCamelCase(variable)
            fw.write("        __" + classVariabile + " = None\n")
        fw.write("\n")

        for element in schemaData:
            method = element[0]
            if(self.isConstraintName(element, constraintsData)):
                method = self.getConstraintName(element, constraintsData)
            methodName = self.toCamelCase(method)
            fw.write("    def get" + self.capitalizeFirstLetter(methodName) + "(self):\n") 
            fw.write("        return self.__" + methodName + "\n") 
        fw.write("\n")

        for element in schemaData:
            method = element[0]
            if(self.isConstraintName(element, constraintsData)):
                method = self.getConstraintName(element, constraintsData)
            methodName = self.toCamelCase(method)
            fw.write("    def set" + self.capitalizeFirstLetter(methodName) + "(self,value):\n") 
            fw.write("        self.__" + methodName + "=value\n") 
        fw.write("\n")

        # fromJson
        parameters=""
        for element in schemaData:
            variable = element[0]
            if(self.isConstraintName(element, constraintsData)):
                variable = self.getConstraintName(element, constraintsData)
            parameters += self.toCamelCase(variable) + ","
        # Rimuovo ultimo parametro dalla stringa
        parameters = parameters[:-1]
        
        fw.write("    def fromJson(self," + parameters + "):" + "\n")
        
        for element in schemaData:
            variable = element[0]
            if(self.isConstraintName(element, constraintsData)):
                variable = self.getConstraintName(element, constraintsData)
                parameter = self.toCamelCase(variable)
                fw.write("        self.__" + parameter + " = " + self.capitalizeFirstLetter(parameter) + "()" + "\n")
                fw.write("        if(" + parameter + " != None):" + "\n")
                fw.write("            self.__" + parameter + ".fromJson(**" + parameter + ")" + "\n")
            else:
                parameter = self.toCamelCase(variable) 
                fw.write("        self.__" + parameter + " = " + parameter + "\n")
        fw.write("\n")

        # toJson
        parameters=""
        for element in schemaData:
            variable = element[0]
            if(self.isConstraintName(element, constraintsData)):
                variable = self.getConstraintName(element, constraintsData)
            parameters += self.toCamelCase(variable) + ","
        # Rimuovo ultimo parametro dalla stringa
        parameters = parameters[:-1]
        
        fw.write("    def toJson(self):" + "\n")
        string = "        return {"
        for element in schemaData:
            variable = element[0]
            if(self.isConstraintName(element, constraintsData)):
                variable = self.getConstraintName(element, constraintsData)
                parameter = self.toCamelCase(variable)
                string += "\"" +  parameter + "\"" + ":" + "self.__" + parameter + ".toJson(),"
            else:
                parameter = self.toCamelCase(variable) 
                string += "\"" +  parameter + "\"" + ":" + "self.__" + parameter + ","
        # Rimuovo ultimo parametro dalla stringa
        string = string[:-1]
        string += "}"
        fw.write(string)
        fw.write("\n")

        fw.write("\n")
        fw.close

class BeanGeneratorTS:

    def __init__(self,schema):
        self.schema = schema   

    def toCamelCase(self,text):
        return re.sub(r"[-_]([a-zA-Z])", lambda x: x[1].upper(), text)
            
    def capitalizeFirstLetter(self,text):
        return text[0].upper()+text[1:]

    def isConstraintName(self, element, constraintsData):
        for constraint in constraintsData:
            if(constraint[1] == element[0]):
                return True;
        return False;

    def getConstraintName(self, element, constraintsData):
        for constraint in constraintsData:
            if(constraint[1] == element[0]):
                return constraint[2];
        return ''
    '''
    Ok per le classi, interfaccie non implementano qualcosa tipo = xyz
    def getType(self,_type):
        match _type:
            case 'character varying' | 'varchar' | 'character' | 'char' | 'text': 
                return 'string = ' + "\"" + "\""
            case 'smallint' | 'integer' | 'numeric' | 'decimal' | 'bigint':
                return 'number = 0'
            case 'date' | 'timestamp' | 'time':
                return 'Date = new Date'
        return ""
    '''
    def getType(self,_type):
        match _type:
            case 'character varying' | 'varchar' | 'character' | 'char' | 'text': 
                return 'string'
            case 'smallint' | 'integer' | 'numeric' | 'decimal' | 'bigint':
                return 'number'
            case 'date' | 'timestamp' | 'time':
                return 'Date'
        return ""

    def writeBean(self, table_name):
        fileName = "tables.ts"
        path = os.path.dirname(os.path.abspath(sys.argv[0]))
        generatedPath = path + "\\generated\\" 
        filepath = os.path.join(generatedPath, fileName)
        if not os.path.exists(generatedPath):
            os.makedirs(generatedPath)
        
        fw = open(filepath, "a")                                                                                                
          
        table_name[0].capitalize()
        className = self.toCamelCase(table_name)
        className =self.capitalizeFirstLetter(className)
        fw.write("//----------------------------------------------------------------------\n")
        fw.write("// " + className + "\n")
        fw.write("//----------------------------------------------------------------------\n")

        fw.write("export interface " + className + " {\n")

        constraintsData = self.schema.getConstraintsData(table_name)
        schemaData = self.schema.getColumnsData(table_name)        
        for element in schemaData:
            if(self.isConstraintName(element, constraintsData)):
                variable = self.getConstraintName(element, constraintsData)
                # questo ok se sono una classe, interfaccie non hanno = ...
                #fw.write("    " + self.toCamelCase(element[0]) + ": " + variable.capitalize() + " = new " + variable.capitalize() + ";\n")
                fw.write("    " + self.toCamelCase(element[0]) + ": " + variable.capitalize() + ";\n")
            else: 
                # questo ok se sono una classe, interfaccie non hanno = ...
                #fw.write("    " + self.toCamelCase(element[0]) + ": " + self.getType(element[1]) + ";\n")
                fw.write("    " + self.toCamelCase(element[0]) + ": " + self.getType(element[1]) + ";\n")

        fw.write("}\n")
        fw.write("\n")
        fw.close

    def writeService(self, table_name):
        fileName = "services.ts"
        path = os.path.dirname(os.path.abspath(sys.argv[0]))
        generatedPath = path + "\\generated\\" 
        filepath = os.path.join(generatedPath, fileName)
        if not os.path.exists(generatedPath):
            os.makedirs(generatedPath)
        
        fw = open(filepath, "a")                                                                                                
          
        table_name[0].capitalize()
        className = self.toCamelCase(table_name)
        className =self.capitalizeFirstLetter(className)
        fw.write("export const fetch" + className + " = async (): Promise<" + className + "[]> => {\n") 
        fw.write("    try {\n")
        fw.write("        return await apiRequest<" + className + "[]>('/" + table_name + "/r01','GET');\n")
        fw.write("    } catch (error) {\n")
        fw.write("        console.error('Failed to fetch " + table_name + ":', error);\n")
        fw.write("        return [];\n")
        fw.write("    }\n")
        fw.write("};\n")
        fw.write("\n")

        fw.write("export const fetch" + className + "ById = async (id: number): Promise<" + className + " | null> => {\n") 
        fw.write("    try {\n")
        fw.write("        return await apiRequest<" + className + ">('/" + table_name + "/r02/'+id,'GET');\n")
        fw.write("    } catch (error) {\n")
        fw.write("        console.error('Failed to fetch " + table_name + ":', error);\n")
        fw.write("        return null;\n")
        fw.write("    }\n")
        fw.write("};\n")
        fw.write("\n")

        fw.write("export const save" + className + "= async (" + table_name + ":" + className + "): Promise<" + className + " | null> => {\n") 
        fw.write("    try {\n")
        fw.write("        return await apiRequest<" + className + ">('/" + table_name + "','POST'," + table_name + ");\n")
        fw.write("    } catch (error) {\n")
        fw.write("        console.error('Failed to save " + table_name + ":', error);\n")
        fw.write("        return null;\n")
        fw.write("    }\n")
        fw.write("};\n")
        fw.write("\n")

        fw.write("export const delete" + className + " = async (id: number): Promise<" + className + " | null> => {\n") 
        fw.write("    try {\n")
        fw.write("        return await apiRequest<" + className + ">('/" + table_name + "/d01/'+id,'DELETE');\n")
        fw.write("    } catch (error) {\n")
        fw.write("        console.error('Failed to delete " + table_name + ":', error);\n")
        fw.write("        return null;\n")
        fw.write("    }\n")
        fw.write("};\n")
        fw.write("\n")


        fw.write("\n")
        fw.close

class JdbcDaoBuilder:
    def __init__(self,schema,tableName):
        self.tableName = tableName
        self.schema = schema 
    
    def getType(self):
        schemaData = self.schema.getColumnsData(self.tableName )        
        for element in schemaData:
            classVariabile = element[1] + ":" + element[0]
            print(classVariabile)
        pass





