#!/bin/bash
VERSION_SUFFIX=$(buildkite-agent meta-data get version_suffix || echo "")
export VERSION_SUFFIX
echo "VERSION_SUFFIX=${VERSION_SUFFIX}"
export ORG_GRADLE_PROJECT_signingInMemoryKey=${SIGNING_KEY}
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=${SIGNING_PASSWORD}
export ORG_GRADLE_PROJECT_mavenCentralUsername=${SONATYPE_USERNAME}
export ORG_GRADLE_PROJECT_mavenCentralPassword=${SONATYPE_PASSWORD}
