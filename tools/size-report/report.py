#!/usr/bin/env python3
"""Render the PR size report from two measure_size.sh JSON results."""

import argparse
import json
from pathlib import Path

COMMENT_IDENTIFIER = "<!-- ux-helper-size-report -->"
# Changes within this many KB either way are reported as minimal.
NEUTRAL_THRESHOLD_KB = 5


def load(path):
    try:
        return json.loads(Path(path).read_text())
    except (OSError, ValueError):
        return {}


def kb(data, key):
    return data[key] // 1024 if key in data else None


def format_size(value_kb):
    if value_kb is None:
        return "N/A"
    if abs(value_kb) >= 1024:
        return f"{value_kb / 1024:.2f} MB"
    return f"{value_kb} KB"


def format_delta(value_kb):
    if value_kb is None:
        return "N/A"
    return ("+" if value_kb >= 0 else "") + format_size(value_kb)


def delta(base, pr):
    return None if base is None or pr is None else pr - base


def render(base, pr, base_ref):
    rows = []
    for label, key in (
        ("APK Impact", "helper_apk_impact_bytes"),
        ("DEX Impact", "helper_dex_impact_bytes"),
    ):
        base_kb, pr_kb = kb(base, key), kb(pr, key)
        rows.append(
            f"| {label} | {format_size(base_kb)} | {format_size(pr_kb)} "
            f"| {format_delta(delta(base_kb, pr_kb))} |"
        )

    apk_delta = delta(
        kb(base, "helper_apk_impact_bytes"), kb(pr, "helper_apk_impact_bytes")
    )
    if apk_delta is None:
        status = "ℹ️ The size could not be measured on one or both branches."
    elif apk_delta > NEUTRAL_THRESHOLD_KB:
        status = "⚠️ This change increases roktux size impact."
    elif apk_delta < -NEUTRAL_THRESHOLD_KB:
        status = "✅ This change decreases roktux size impact."
    else:
        status = "➡️ roktux size impact change is minimal."

    fence = "```"
    return "\n".join(
        [
            COMMENT_IDENTIFIER,
            "## 📦 roktux Size Impact Report",
            "",
            "How much roktux adds to an app's size, measured as the difference",
            "between a bare Compose app and the same app with the dependency added.",
            "Both are release builds shrunk by R8.",
            "",
            "| Metric | Target Branch | This PR | Change |",
            "|--------|---------------|---------|--------|",
            *rows,
            "",
            status,
            "",
            "<details>",
            "<summary>Raw measurements</summary>",
            "",
            f"**Target branch ({base_ref}):**",
            f"{fence}json",
            json.dumps(base, separators=(",", ":")),
            fence,
            "",
            "**This PR:**",
            f"{fence}json",
            json.dumps(pr, separators=(",", ":")),
            fence,
            "</details>",
            "",
        ]
    )


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("base", help="JSON result for the target branch")
    parser.add_argument("pr", help="JSON result for the PR branch")
    parser.add_argument("--base-ref", default="main", help="Target branch name")
    args = parser.parse_args()
    print(render(load(args.base), load(args.pr), args.base_ref), end="")


if __name__ == "__main__":
    main()
