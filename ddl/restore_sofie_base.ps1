# ============================================================================
# Ripristina il database dall'ultimo backup della base di test (sofie-base-*.backup).
# Il database di destinazione viene CANCELLATO e ricreato.
#
#   .\ddl\restore_sofie_base.ps1 -Database sofie_test     su una copia
#   .\ddl\restore_sofie_base.ps1 -Force                   su sofie
#   .\ddl\restore_sofie_base.ps1 -Backup ddl\sofie-base-20261106.backup -Force
#
# Su sofie i servizi devono essere spenti: lo script controlla le porte 8080-8084.
# Dopo il ripristino la data ufficiale e' quella del backup: va lasciata cosi',
# gli z-spread hanno una validita' limitata (spread_curve.max_age_days).
# ============================================================================
param(
    [string]$Database = "sofie",
    [string]$Backup = "",
    [switch]$Force
)

$ErrorActionPreference = "Stop"
$bin = "C:\tools\PostgreSQL\18\bin"
$env:PGPASSWORD = "sofie"

if ($Backup -eq "") {
    $latest = Get-ChildItem $PSScriptRoot -Filter "sofie-base-*.backup" | Sort-Object Name -Descending | Select-Object -First 1
    if ($null -eq $latest) {
        throw "No sofie-base-*.backup in $PSScriptRoot"
    }
    $Backup = $latest.FullName
}
if (-not (Test-Path $Backup)) {
    throw "Backup not found: $Backup"
}

if ($Database -eq "sofie") {
    if (-not $Force) {
        throw "This drops the sofie database: run it again with -Force"
    }
    $running = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue | Where-Object { $_.LocalPort -in 8080..8084 }
    if ($running) {
        throw "Services are running on ports $(($running.LocalPort | Sort-Object) -join ', '): stop them first"
    }
}

Write-Output "Restoring $Backup into $Database"
# --force chiude le connessioni aperte, per esempio pgAdmin
& "$bin\dropdb.exe" -h localhost -U sofie --if-exists --force $Database
if ($LASTEXITCODE -ne 0) { throw "dropdb failed" }
& "$bin\createdb.exe" -h localhost -U sofie $Database
if ($LASTEXITCODE -ne 0) { throw "createdb failed" }
& "$bin\pg_restore.exe" -h localhost -U sofie -d $Database --no-owner --exit-on-error $Backup
if ($LASTEXITCODE -ne 0) { throw "pg_restore failed" }

& "$bin\psql.exe" -h localhost -U sofie -d $Database -c "select official_date, status, (select count(*) from financial_txn) trades, (select count(*) from master_data) instruments from system_business_calendar"
Write-Output "Done"
