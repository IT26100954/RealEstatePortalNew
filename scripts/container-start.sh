#!/bin/sh
set -eu
# Accept the public CA as a mounted file or a multiline hosting variable.
ca_file=/etc/secrets/aiven-ca.pem
truststore_dir=
if [ -n "${AIVEN_CA_PEM:-}" ]; then
  truststore_dir=$(mktemp -d /tmp/portal-truststore.XXXXXX)
  ca_file="${truststore_dir}/aiven-ca.pem"
  printf '%s\n' "$AIVEN_CA_PEM" > "$ca_file"
  unset AIVEN_CA_PEM
fi
if [ -f "$ca_file" ]; then
  if [ -z "$truststore_dir" ]; then
    truststore_dir=$(mktemp -d /tmp/portal-truststore.XXXXXX)
  fi
  keytool -importcert -noprompt -alias aiven-ca -file "$ca_file" \
    -keystore ${truststore_dir}/aiven-truststore.p12 -storetype PKCS12 -storepass changeit >/dev/null 2>&1
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djavax.net.ssl.trustStore=${truststore_dir}/aiven-truststore.p12 -Djavax.net.ssl.trustStoreType=PKCS12 -Djavax.net.ssl.trustStorePassword=changeit"
  export JAVA_TOOL_OPTIONS
fi
: "${DB_URL:?Set DB_URL to the hosted MySQL JDBC URL with sslMode=VERIFY_IDENTITY}"
: "${DB_USER:?Set the hosted runtime database user}"
: "${DB_PASSWORD:?Set the hosted runtime database password}"
exec java -jar /app/application.jar
