#!/usr/bin/env bash
#
# Builds two otherwise-identical release apps and measures the size difference:
#   - baseline     — a bare Compose app (Activity + Material 3)
#   - with-helper  — the same app plus roktux, calling RoktLayout
#
# Both are minified and resource-shrunk by R8, so the delta is what roktux and the
# dependencies it adds on top of Compose cost a partner app after shrinking.
#
# Usage: ./measure_size.sh [--json] [--root <checkout>]
#
#   --json           Output results as a single line of JSON (for CI)
#   --root <path>    Checkout whose roktux is measured (default: this repository)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
OUTPUT_JSON=false

while [[ $# -gt 0 ]]; do
	case $1 in
	--json) OUTPUT_JSON=true ;;
	--root)
		ROOT="$(cd "$2" && pwd)"
		shift
		;;
	*)
		echo "Unknown argument: $1" >&2
		exit 1
		;;
	esac
	shift
done

rm -rf "${SCRIPT_DIR}"/{baseline,with-helper}/build/outputs/apk

# Gradle output goes to stderr so stdout stays clean for JSON.
"${SCRIPT_DIR}/../../gradlew" -p "${SCRIPT_DIR}" -PuxHelperRoot="${ROOT}" \
	:baseline:assembleRelease :with-helper:assembleRelease --quiet >&2

apk_path() {
	echo "${SCRIPT_DIR}/$1/build/outputs/apk/release/$1-release.apk"
}

file_size_bytes() {
	stat -f%z "$1" 2>/dev/null || stat -c%s "$1"
}

# Uncompressed size of every classes*.dex in the APK.
dex_size_bytes() {
	unzip -l "$1" | awk '$4 ~ /^classes[0-9]*\.dex$/ { total += $1 } END { print total + 0 }'
}

BASELINE_APK="$(apk_path baseline)"
WITH_HELPER_APK="$(apk_path with-helper)"

BASELINE_APK_BYTES=$(file_size_bytes "${BASELINE_APK}")
BASELINE_DEX_BYTES=$(dex_size_bytes "${BASELINE_APK}")
WITH_HELPER_APK_BYTES=$(file_size_bytes "${WITH_HELPER_APK}")
WITH_HELPER_DEX_BYTES=$(dex_size_bytes "${WITH_HELPER_APK}")
HELPER_APK_IMPACT=$((WITH_HELPER_APK_BYTES - BASELINE_APK_BYTES))
HELPER_DEX_IMPACT=$((WITH_HELPER_DEX_BYTES - BASELINE_DEX_BYTES))

if [[ ${OUTPUT_JSON} == "true" ]]; then
	printf '{"baseline_apk_size_bytes":%d,"baseline_dex_size_bytes":%d,"with_helper_apk_size_bytes":%d,"with_helper_dex_size_bytes":%d,"helper_apk_impact_bytes":%d,"helper_dex_impact_bytes":%d}\n' \
		"${BASELINE_APK_BYTES}" "${BASELINE_DEX_BYTES}" "${WITH_HELPER_APK_BYTES}" "${WITH_HELPER_DEX_BYTES}" "${HELPER_APK_IMPACT}" "${HELPER_DEX_IMPACT}"
else
	cat <<REPORT

=== roktux Size Measurement Results ===

Baseline app (Compose, no roktux):
  APK size: ${BASELINE_APK_BYTES} bytes
  DEX size: ${BASELINE_DEX_BYTES} bytes

With roktux:
  APK size: ${WITH_HELPER_APK_BYTES} bytes
  DEX size: ${WITH_HELPER_DEX_BYTES} bytes

roktux impact:
  APK delta: ${HELPER_APK_IMPACT} bytes
  DEX delta: ${HELPER_DEX_IMPACT} bytes
REPORT
fi
