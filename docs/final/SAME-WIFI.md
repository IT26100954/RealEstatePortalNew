# Open the project from another device on the same Wi-Fi

1. On the server laptop, keep MySQL running and connect to the same normal Wi-Fi as the second device.
2. The launcher switches automatically from its own laptop-only instance. Stop an unrelated IntelliJ/backend instance yourself if it occupies 8081.
3. Double-click START-WIFI.cmd. Approve Windows' administrator prompt if shown: it creates one narrow inbound rule for this Java program, TCP 8081, this Wi-Fi address and this Wi-Fi subnet only. It does not disable the firewall or expose MySQL.
4. The launcher prints and opens the current Wi-Fi URL, for example http://192.168.1.61:8081. The address is also saved privately under .local/wifi-website-address.txt.
5. Type that exact address in the second device's browser. Do not use localhost or 127.0.0.1 there: those names refer to that second device itself.
6. Use your existing LOCAL application login, or separate local demo accounts. Each browser has its own login session, but all devices share the same local project data.

The laptop, MySQL and Java process must stay running, and the laptop must not sleep. The second device can work anywhere within that same reachable local network, not anywhere on the internet. Mobile data, a different network, guest/client isolation, a VPN or school Wi-Fi restrictions can prevent access. Connecting to similarly named Wi-Fi networks alone does not prove devices can talk to each other.

Use STOP-LOCAL.cmd to stop either local mode. START-LOCAL.cmd remains laptop-only. The START launchers automatically switch their own running app between modes; this restarts Java and signs out existing sessions. REMOVE-WIFI-ACCESS.cmd removes only the rule created for this project (Windows may ask administrator permission).

Wi-Fi addresses can change after reconnecting or switching routers. Restart START-WIFI.cmd to detect the new address and update its narrow rule. No router port forwarding, public tunnel, internet hosting or GitHub merge is configured. Your Windows network classification and all other firewall rules are left alone.

This mode uses ordinary HTTP on the local network, so use it on a trusted network for demonstration; it is not an internet deployment. MySQL continues to be accessed by Java through 127.0.0.1. Do not enter real ADMIN credentials on an untrusted shared network.

A successful request to the Wi-Fi address from the laptop verifies the binding, assets and API, but only opening the link on the second physical device verifies its Wi-Fi/router path. If it fails, check the exact current address, Windows permission prompt, same network, VPN/mobile data and guest/client isolation before changing account or database settings.
