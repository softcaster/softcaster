# ============================================================================
# Genera dal database i tre script di creazione, da eseguire in quest'ordine:
#
#   sofie_schema.sql   tabelle, sequenze, funzioni, trigger, indici, vincoli
#   sofie_lookup.sql   valori delle tabelle di lookup (enum Java)
#   sofie_config.sql   dati senza cui il sistema non gira
#
# Anagrafiche degli strumenti, curve e quotazioni restano solo nei backup.
#
#   .\ddl\export_schema.ps1                       da sofie
#   .\ddl\export_schema.ps1 -Database sofie_test
#
# Una modifica allo schema nasce come migrazione, provata su una copia: applicata
# a sofie si rigenerano i file e la migrazione si butta. Un database di partenza
# deve avere i servizi spenti e nessun dato di prova nelle tabelle di config.
# ============================================================================
param(
    [string]$Database = "sofie",
    [string]$OutDir = $PSScriptRoot
)

$ErrorActionPreference = "Stop"
$bin = "C:\tools\PostgreSQL\18\bin"
$env:PGPASSWORD = "sofie"

# Dati di sistema, non enum. calendar e' una tabella id/code/description ma contiene dati veri.
$configTables = @(
    "calendar", "holiday", "currency", "country", "asset_class", "asset_class_script_mapping",
    "gl_accounts", "gl_account_slots", "account_mapping", "counterparty", "counterparty_role_mapping",
    "portfolio_master_data", "position_master_data", "descriptors", "system_business_calendar"
)

function Invoke-Sql([string]$sql) {
    $rows = & "$bin\psql.exe" -h localhost -U sofie -d $Database -t -A -c $sql
    if ($LASTEXITCODE -ne 0) { throw "psql failed: $sql" }
    return @($rows | Where-Object { $_ -ne "" })
}

# Lookup: solo id, code, description e nessuna FK; piu' frequency, che ha anche year_fraction
$lookupTables = Invoke-Sql @"
select c.table_name
from information_schema.columns c
where c.table_schema = 'public'
  and not exists (select 1 from pg_constraint k where k.contype = 'f' and k.conrelid = format('public.%I', c.table_name)::regclass)
group by c.table_name
having (count(*) = 3 and bool_or(c.column_name = 'code') and bool_or(c.column_name = 'description'))
    or c.table_name = 'frequency'
order by 1
"@
$lookupTables = @($lookupTables | Where-Object { $configTables -notcontains $_ })

$sequences = Invoke-Sql "select sequencename from pg_sequences where schemaname = 'public'"

function Get-DumpArgs([string[]]$tables) {
    $dumpArgs = @()
    foreach ($table in $tables) {
        $dumpArgs += "--table=public.$table"
        # le sequenze non sono legate alle tabelle: serve il loro valore per i nuovi id
        if ($sequences -contains "${table}_s") { $dumpArgs += "--table=public.${table}_s" }
    }
    return $dumpArgs
}

function Write-Script([string]$fileName, [string]$title, [string[]]$dumpArgs) {
    $temp = [IO.Path]::GetTempFileName()
    try {
        & "$bin\pg_dump.exe" -h localhost -U sofie -d $Database -E UTF8 --no-owner --no-privileges -f $temp @dumpArgs
        if ($LASTEXITCODE -ne 0) { throw "pg_dump failed for $fileName" }
        # \restrict e \unrestrict hanno un token casuale: cambierebbero il file a ogni export
        $body = [IO.File]::ReadAllLines($temp, [Text.Encoding]::UTF8) | Where-Object { $_ -notmatch '^\\(un)?restrict ' }
        $header = @(
            "-- ============================================================================",
            "-- $title",
            "-- Generato da ddl/export_schema.ps1: non modificare a mano.",
            "-- Ordine: sofie_schema.sql, sofie_lookup.sql, sofie_config.sql",
            "-- ============================================================================"
        )
        [IO.File]::WriteAllLines((Join-Path $OutDir $fileName), [string[]]($header + $body), (New-Object Text.UTF8Encoding($false)))
    } finally {
        Remove-Item $temp -Force -ErrorAction SilentlyContinue
    }
    Write-Output "$fileName written"
}

Write-Script "sofie_schema.sql" "Schema del database" @("--schema-only")
Write-Script "sofie_lookup.sql" "Tabelle di lookup: $($lookupTables.Count)" (@("--data-only", "--column-inserts") + (Get-DumpArgs $lookupTables))
Write-Script "sofie_config.sql" "Dati di sistema: $($configTables.Count) tabelle" (@("--data-only", "--column-inserts") + (Get-DumpArgs $configTables))
