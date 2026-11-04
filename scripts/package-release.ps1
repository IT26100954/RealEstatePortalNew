$ErrorActionPreference = 'Stop'
Push-Location (Join-Path $PSScriptRoot '../frontend')
try {
    npm.cmd ci
    if ($LASTEXITCODE -ne 0) { throw 'Frontend dependencies failed' }
    npm.cmd run build
    if ($LASTEXITCODE -ne 0) { throw 'Frontend build failed' }
} finally { Pop-Location }
Push-Location (Join-Path $PSScriptRoot '../backend')
try {
    & ./mvnw.cmd -Pwith-frontend clean package
    if ($LASTEXITCODE -ne 0) { throw 'Backend packaging failed' }
} finally { Pop-Location }
