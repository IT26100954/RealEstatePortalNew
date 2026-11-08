[CmdletBinding()]
param([switch]$Build, [switch]$NoBrowser, [switch]$Stop, [switch]$WiFi)
$ErrorActionPreference = 'Stop'
$projectRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$backend = Join-Path $projectRoot 'backend'
$frontend = Join-Path $projectRoot 'frontend'
$localDirectory = Join-Path $projectRoot '.local'
$stateFile = Join-Path $localDirectory 'local-evaluation-process.json'
$outLog = Join-Path $localDirectory 'local-evaluation-out.log'
$errLog = Join-Path $localDirectory 'local-evaluation-error.log'
$jar = Join-Path $backend 'target/real-estate-portal-0.0.1-SNAPSHOT.jar'
$site = 'http://127.0.0.1:8081'
$marker = '-Dportal.local.evaluation=1'
$bindAddress = '127.0.0.1'
$wifiAddress = $null
if ($WiFi -and -not $Stop) {
    $wireless = @(Get-NetAdapter -Physical | Where-Object {
        $_.Status -eq 'Up' -and $_.PhysicalMediaType -eq 'Native 802.11'
    })
    if ($wireless.Count -ne 1) { throw 'Connect one Wi-Fi adapter before using START-WIFI.cmd.' }
    $wifiAddress = Get-NetIPAddress -InterfaceIndex $wireless[0].ifIndex -AddressFamily IPv4 |
        Where-Object { $_.AddressState -eq 'Preferred' -and $_.IPAddress -notlike '169.254.*' } |
        Select-Object -First 1
    if (-not $wifiAddress) { throw 'Wi-Fi does not have an IPv4 address yet.' }
    # Listen on this Wi-Fi address only, not every network adapter.
    $bindAddress = $wifiAddress.IPAddress
    $site = "http://${bindAddress}:8081"
}

function Get-ManagedProcess {
    if (-not (Test-Path -LiteralPath $stateFile)) { return $null }
    $saved = Get-Content -LiteralPath $stateFile -Raw | ConvertFrom-Json
    $running = Get-CimInstance Win32_Process -Filter "ProcessId = $([int]$saved.ProcessId)" -ErrorAction SilentlyContinue
    if ($null -ne $running -and $running.CommandLine -and
        $running.CommandLine.Contains($marker) -and
        $running.CommandLine.Contains($jar)) { return $running }
    return $null
}

function Test-LocalWebsite {
    try {
        $response = Invoke-WebRequest -UseBasicParsing -Uri "$site/api/auth/me" -TimeoutSec 3
        $body = $response.Content | ConvertFrom-Json
        return ($response.StatusCode -eq 200 -and $body.csrfToken -and
            $body.PSObject.Properties.Name -contains 'user')
    } catch { return $false }
}

if ($Stop) {
    $running = Get-ManagedProcess
    if ($null -ne $running) {
        Stop-Process -Id $running.ProcessId
        Write-Host 'Local evaluation website stopped. MySQL and other applications are unchanged.'
    } else { Write-Host 'No website started by this launcher is running.' }
    if (Test-Path -LiteralPath $stateFile) { Remove-Item -LiteralPath $stateFile }
    exit 0
}

$managed = Get-ManagedProcess
if ($null -ne $managed) {
    if ($Build) { throw 'Use STOP-LOCAL.cmd before rebuilding the running application.' }
    $savedMode = Get-Content -LiteralPath $stateFile -Raw | ConvertFrom-Json
    if ($savedMode.Url -ne $site) {
        throw 'A different local/Wi-Fi mode is running. Use STOP-LOCAL.cmd, then the desired START launcher.'
    }
    if (Test-LocalWebsite) {
        Write-Host "Already running: $site"
        if (-not $NoBrowser) { Start-Process $site }
        exit 0
    }
    throw 'The local process exists but is not ready. Check .local/local-evaluation-error.log, or use STOP-LOCAL.cmd and start again.'
}

if (-not (Test-Path -LiteralPath (Join-Path $backend '.env'))) {
    throw 'Missing private backend/.env. Configure your LOCAL MySQL account first. Do not copy hosted credentials.'
}
if (Get-NetTCPConnection -State Listen -LocalPort 8081 -ErrorAction SilentlyContinue) {
    throw 'Port 8081 is occupied. Stop the earlier IntelliJ/backend instance, then start this launcher. No unrelated process was stopped.'
}

$mysql = Get-Service -Name 'MySQL*' -ErrorAction SilentlyContinue | Where-Object { $_.Status -eq 'Running' }
if (-not $mysql) { Write-Host 'Check that your local MySQL Server is running before continuing.' }

# Find Java 21 without changing the computer-wide PATH or Java installation.
$homes = @()
if ($env:JAVA_HOME) { $homes += $env:JAVA_HOME }
$jdks = Join-Path $env:USERPROFILE '.jdks'
if (Test-Path -LiteralPath $jdks) {
    $homes += @(Get-ChildItem -LiteralPath $jdks -Directory | Select-Object -ExpandProperty FullName)
}
$javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
if ($javaCommand) { $homes += Split-Path (Split-Path $javaCommand.Source -Parent) -Parent }
$java = $null
foreach ($candidateHome in ($homes | Select-Object -Unique)) {
    $candidate = Join-Path $candidateHome 'bin/java.exe'
    $release = Join-Path $candidateHome 'release'
    if ((Test-Path -LiteralPath $candidate) -and (Test-Path -LiteralPath $release) -and
        (Select-String -LiteralPath $release -Pattern '^JAVA_VERSION="21[.\"]' -Quiet)) {
        $java = $candidate
        $env:JAVA_HOME = $candidateHome
        break
    }
}
if (-not $java) { throw 'JDK 21 was not found. Select/install JDK 21 and set JAVA_HOME for this terminal.' }

if ($WiFi) {
    # The helper requests Windows administrator approval only if its narrow rule needs updating.
    & (Join-Path $PSScriptRoot 'configure-wifi-access.ps1') -InterfaceIndex $wifiAddress.InterfaceIndex -JavaPath $java
}

$needBuild = $Build -or -not (Test-Path -LiteralPath $jar)
if (-not $needBuild) {
    $jarTime = (Get-Item -LiteralPath $jar).LastWriteTimeUtc
    $inputs = @((Join-Path $backend 'pom.xml'), (Join-Path $frontend 'package.json'),
        (Join-Path $frontend 'package-lock.json'), (Join-Path $frontend 'index.html'),
        (Join-Path $frontend 'vite.config.js'))
    $changed = @($inputs | Where-Object { (Get-Item -LiteralPath $_).LastWriteTimeUtc -gt $jarTime })
    foreach ($sourceDirectory in @((Join-Path $backend 'src'), (Join-Path $frontend 'src'))) {
        $changed += @(Get-ChildItem -LiteralPath $sourceDirectory -Recurse -File |
            Where-Object { $_.LastWriteTimeUtc -gt $jarTime })
    }
    $needBuild = $changed.Count -gt 0
}

if ($needBuild) {
    Write-Host 'Building the React frontend and Java application, including backend tests. The first build may need internet.'
    if (-not (Get-Command npm.cmd -ErrorAction SilentlyContinue)) { throw 'Node.js/npm is needed for the first build.' }
    Push-Location $frontend
    try {
        $lockFile = Join-Path $frontend 'node_modules/.package-lock.json'
        if (-not (Test-Path -LiteralPath $lockFile) -or
            (Get-Item -LiteralPath (Join-Path $frontend 'package-lock.json')).LastWriteTimeUtc -gt (Get-Item -LiteralPath $lockFile).LastWriteTimeUtc) {
            & npm.cmd ci
            if ($LASTEXITCODE -ne 0) { throw 'Frontend dependency installation failed.' }
        }
        & npm.cmd run build
        if ($LASTEXITCODE -ne 0) { throw 'Frontend build failed.' }
    } finally { Pop-Location }

    $mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    $maven = if ($mavenCommand) { $mavenCommand.Source } else { $null }
    $jetbrains = Join-Path $env:ProgramFiles 'JetBrains'
    if (-not $maven -and (Test-Path -LiteralPath $jetbrains)) {
        foreach ($ide in (Get-ChildItem -LiteralPath $jetbrains -Directory | Sort-Object Name -Descending)) {
            $bundled = Join-Path $ide.FullName 'plugins/maven/lib/maven3/bin/mvn.cmd'
            if (Test-Path -LiteralPath $bundled) { $maven = $bundled; break }
        }
    }
    if (-not $maven) { $maven = Join-Path $backend 'mvnw.cmd' }
    Push-Location $backend
    try {
        & $maven -B '-Pwith-frontend' package
        if ($LASTEXITCODE -ne 0) { throw 'Java packaging/tests failed. The application was not started.' }
    } finally { Pop-Location }
}

New-Item -ItemType Directory -Path $localDirectory -Force | Out-Null
# Start only the packaged app, with explicitly LOCAL settings and no bootstrap profile.
# Remove stale hosting overrides from this child process, never from Windows settings.
$names = @('DB_URL','DB_USER','DB_PASSWORD','SPRING_PROFILES_ACTIVE','SPRING_APPLICATION_JSON',
    'SPRING_CONFIG_IMPORT','SPRING_DATASOURCE_URL','SPRING_DATASOURCE_USERNAME','SPRING_DATASOURCE_PASSWORD',
    'BOOTSTRAP_ADMIN_USERNAME','BOOTSTRAP_ADMIN_PASSWORD','BOOTSTRAP_ADMIN_EMAIL','BOOTSTRAP_ADMIN_PHONE')
$savedEnvironment = @{}
foreach ($name in $names) {
    $savedEnvironment[$name] = [Environment]::GetEnvironmentVariable($name,'Process')
    [Environment]::SetEnvironmentVariable($name,$null,'Process')
}
try {
    $arguments = @($marker,'-jar',('"'+$jar+'"'),
        '--spring.profiles.active=local-evaluation',
        '--spring.config.import=file:./.env[.properties]',
        '--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/real_estate_portal_new',
        "--server.address=$bindAddress",'--server.port=8081',
        '--server.servlet.session.cookie.secure=false',
        '--spring.sql.init.mode=never','--spring.jpa.hibernate.ddl-auto=none')
    $process = Start-Process -FilePath $java -ArgumentList $arguments -WorkingDirectory $backend `
        -WindowStyle Hidden -PassThru -RedirectStandardOutput $outLog -RedirectStandardError $errLog
} finally {
    foreach ($name in $names) { [Environment]::SetEnvironmentVariable($name,$savedEnvironment[$name],'Process') }
}
@{ ProcessId=$process.Id; Url=$site; StartedUtc=[DateTime]::UtcNow.ToString('o') } |
    ConvertTo-Json | Set-Content -LiteralPath $stateFile -Encoding UTF8

Write-Host 'Waiting for the local website and database connection...'
$deadline = (Get-Date).AddSeconds(90)
while ((Get-Date) -lt $deadline) {
    $process.Refresh()
    if ($process.HasExited) { throw 'Backend exited. Check the private .local/local-evaluation logs; do not publish credentials or full logs.' }
    if (Test-LocalWebsite) {
        $page = Invoke-WebRequest -UseBasicParsing -Uri $site -TimeoutSec 5
        if ($page.Content -notmatch 'id="root"') { throw 'Backend is running but the frontend is missing. Stop and run START-LOCAL.cmd -Build.' }
        Write-Host "Ready: $site"
        if ($WiFi) {
            Write-Host "On a second device connected to the same Wi-Fi, open: $site"
            Write-Host 'This is local network access, not public internet hosting. Keep the laptop awake.'
            $site | Set-Content -LiteralPath (Join-Path $localDirectory 'wifi-website-address.txt') -Encoding UTF8
        }
        Write-Host 'All six modules use the existing LOCAL MySQL database. Use your LOCAL login.'
        Write-Host 'Use STOP-LOCAL.cmd to stop this website. No IntelliJ or Vite terminal is needed.'
        if (-not $NoBrowser) { Start-Process $site }
        exit 0
    }
    Start-Sleep -Milliseconds 500
}
throw 'Startup timed out. See .local/local-evaluation logs, check MySQL, then use STOP-LOCAL.cmd before retrying.'
