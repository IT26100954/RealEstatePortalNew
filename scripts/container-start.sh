#!/bin/sh
set -eu
# Aiven CA is a Render secret file; no private key is required.
if [ -f /etc/secrets/aiven-ca.pem ]; then
  truststore_dir=$(mktemp -d /tmp/portal-truststore.XXXXXX)
  keytool -importcert -noprompt -alias aiven-ca -file /etc/secrets/aiven-ca.pem \
    -keystore ${truststore_dir}/aiven-truststore.p12 -storetype PKCS12 -storepass changeit >/dev/null 2>&1
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djavax.net.ssl.trustStore=${truststore_dir}/aiven-truststore.p12 -Djavax.net.ssl.trustStoreType=PKCS12 -Djavax.net.ssl.trustStorePassword=changeit"
  export JAVA_TOOL_OPTIONS
fi
: "${DB_URL:?Set DB_URL to the hosted MySQL JDBC URL with sslMode=VERIFY_IDENTITY}"
: "${DB_USER:?Set the hosted runtime database user}"
: "${DB_PASSWORD:?Set the hosted runtime database password}"
exec java -jar /app/application.jar
