# Same-Wi-Fi verification - 8 November 2026

## Configured

- START-WIFI.cmd selects the current physical Wi-Fi IPv4 address and binds Java to that address only. START-LOCAL.cmd remains laptop-only; STOP-LOCAL.cmd stops either managed mode.
- Windows administrator approval was used for one project-owned rule. It permits only this Java executable, inbound TCP 8081, the laptop's current Wi-Fi address, and the current Wi-Fi IPv4 subnet. Edge traversal is blocked. Other firewall rules and the Windows network category were not changed.
- REMOVE-WIFI-ACCESS.cmd removes only that owned rule. The setup helper recognises both CIDR and Windows netmask notation, so a matching rule is reused without another administrator prompt.
- Java still connects to the existing local MySQL database on loopback. The database port was not opened by this change. No table creation, account/password reset or bootstrap was performed.

## Verified from the server laptop

- The application listens on its Wi-Fi IPv4 address on port 8081, not all interfaces.
- 56 real-MySQL HTTP checks passed through the Wi-Fi URL across all six modules.
- Disposable account/record cleanup restored original table counts; existing ADMIN fingerprints were identical before/after those checks.
- A mobile-size browser passed registration, login, profile update, all module screen rendering and logout with no JavaScript errors. Its temporary account was removed.
- The narrow firewall rule was inspected and its matching-rule reuse check passed.

## Confirmed on a second physical device

On 8 November 2026 the owner confirmed that http://192.168.1.61:8081 loaded on a second device connected to the same Wi-Fi. This supplements the server-side automated checks. Guest/client isolation, VPNs, another network or a changed laptop IP can still affect future access.

This is same-network HTTP demonstration access, not public internet hosting. The laptop must stay awake with MySQL and Java running. The tested Wi-Fi setup is included in the setup/local-evaluation review branch. Merging remains a separate owner decision.
