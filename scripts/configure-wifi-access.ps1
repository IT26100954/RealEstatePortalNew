[CmdletBinding()]
param([int]$InterfaceIndex, [string]$JavaPath, [switch]$Remove)
$ErrorActionPreference = 'Stop'
trap {
    $isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole(
        [Security.Principal.WindowsBuiltInRole]::Administrator)
    if ($isAdmin) {
        $logDirectory = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../.local'))
        New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null
        ($_.Exception.Message + [Environment]::NewLine + $_.ScriptStackTrace) |
            Set-Content -LiteralPath (Join-Path $logDirectory 'wifi-firewall-error.log') -Encoding UTF8
    }
    Write-Error $_.Exception.Message -ErrorAction Continue
    exit 1
}
$ruleName = 'RealEstatePortal.LocalEvaluation.WiFi8081'
$description = 'Real Estate Portal evaluation: Wi-Fi IPv4 subnet only, Java TCP 8081. No MySQL access.'
$rule = Get-NetFirewallRule -Name $ruleName -ErrorAction SilentlyContinue
if ($rule -and $rule.Description -ne $description) { throw 'An unrelated firewall rule uses this name. No rule was changed.' }
if ($Remove -and -not $rule) { Write-Host 'The evaluation Wi-Fi firewall rule is already absent.'; return }

if (-not $Remove) {
    if ($JavaPath) { $JavaPath = [IO.Path]::GetFullPath($JavaPath) }
    if (-not $JavaPath -or -not (Test-Path -LiteralPath $JavaPath) -or
        [IO.Path]::GetFileName($JavaPath) -ne 'java.exe') { throw 'A valid Java executable is required.' }
    $adapter = Get-NetAdapter -Physical | Where-Object { $_.ifIndex -eq $InterfaceIndex } | Select-Object -First 1
    if ($adapter.Status -ne 'Up' -or $adapter.PhysicalMediaType -ne 'Native 802.11') {
        throw 'Select the connected physical Wi-Fi adapter.'
    }
    $address = Get-NetIPAddress -InterfaceIndex $InterfaceIndex -AddressFamily IPv4 |
        Where-Object { $_.AddressState -eq 'Preferred' -and $_.IPAddress -notlike '169.254.*' } | Select-Object -First 1
    if (-not $address) { throw 'Wi-Fi IPv4 address not available.' }
    $bytes = [Net.IPAddress]::Parse($address.IPAddress).GetAddressBytes()
    $network = @()
    $masks = @()
    for ($i=0; $i -lt 4; $i++) {
        $bits = [Math]::Min(8,[Math]::Max(0,$address.PrefixLength - $i*8))
        $mask = if ($bits -eq 0) { 0 } else { (255 -shl (8-$bits)) -band 255 }
        $network += ([int]$bytes[$i] -band $mask)
        $masks += $mask
    }
    $subnet = ($network -join '.') + '/' + $address.PrefixLength
    $windowsSubnet = ($network -join '.') + '/' + ($masks -join '.')
    if ($rule -and $rule.Enabled -eq 'True' -and $rule.Action -eq 'Allow') {
        $port = $rule | Get-NetFirewallPortFilter
        $scope = $rule | Get-NetFirewallAddressFilter
        $program = $rule | Get-NetFirewallApplicationFilter
        $interface = $rule | Get-NetFirewallInterfaceFilter
        if ($port.LocalPort -eq '8081' -and $port.Protocol -eq 'TCP' -and
            $scope.LocalAddress -contains $address.IPAddress -and
            ($scope.RemoteAddress -contains $subnet -or $scope.RemoteAddress -contains $windowsSubnet) -and
            $program.Program -eq $JavaPath -and $interface.InterfaceAlias -contains $adapter.Name) {
            Write-Host 'The same-Wi-Fi firewall rule is ready.'; return
        }
    }
}

$administrator = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole(
    [Security.Principal.WindowsBuiltInRole]::Administrator)
if (-not $administrator) {
    Write-Host 'Windows will ask permission to configure only this website port on the current Wi-Fi subnet.'
    $arguments = @('-NoProfile','-ExecutionPolicy','Bypass','-File',('"'+$PSCommandPath+'"'))
    if ($Remove) { $arguments += '-Remove' }
    else { $arguments += @('-InterfaceIndex',$InterfaceIndex,'-JavaPath',('"'+$JavaPath+'"')) }
    $child = Start-Process powershell.exe -Verb RunAs -ArgumentList $arguments -WindowStyle Hidden -Wait -PassThru
    if ($child.ExitCode -ne 0) { throw 'Windows did not complete the Wi-Fi rule. Run START-WIFI.cmd again and approve the prompt.' }
    return
}

if ($rule) { $rule | Remove-NetFirewallRule }
if ($Remove) { Write-Host 'Removed only the evaluation Wi-Fi rule.'; return }
New-NetFirewallRule -Name $ruleName -DisplayName 'Real Estate Portal - same Wi-Fi only (8081)' `
    -Description $description -Direction Inbound -Action Allow -Enabled True -Protocol TCP -LocalPort 8081 `
    -LocalAddress $address.IPAddress -RemoteAddress $subnet -InterfaceAlias $adapter.Name -Program $JavaPath `
    -Profile Any -EdgeTraversalPolicy Block | Out-Null
Write-Host "Allowed website TCP 8081 only from $subnet on $($adapter.Name). MySQL is not shared."
